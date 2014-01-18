"use strict"

define 'models', ['module', 'lib/common'], (module) ->

  ## Common

  ROOT = module.config().BASE_URL or '/api'
  ROOT = ROOT[...-1] if ROOT[-1..] is '/'

  class Entity extends Backbone.Model
    _expires: 1000
    @oid: do ->
      S4 = -> (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
      ->  S4() + S4() + S4() + S4() + S4() + S4()
    syncValidation: -> return # local
    sync: (method, model, options = {}) ->
      @syncValidation method, model, options
      super method, model, options
    load: (callback, {expires} = {}) ->
      expires ?= @_expires
      if not @length or not @_last_load or expires < 1 or (Date.now() - @_last_load) > expires
        queue = @_loading_cb_queue
        if queue?
          queue.push callback if typeof callback is 'function'
        else
          queue = @_loading_cb_queue = if typeof callback is 'function' then [callback] else []
          @fetch
            reset: true
            success: (collection, response, options) =>
              @_loading_cb_queue = null
              @_last_load = Date.now()
              @trigger 'loaded', collection
              cb collection, 'loaded', response, options for cb in queue
              return
            error: (collection, response, options) =>
              @_loading_cb_queue = null
              cb @, 'error', response, options for cb in queue
              return
      else
        callback? @, 'skipped'
      @
    status: (options) ->
      val = @get('status') or ''
      if options?.lowercase then val.toLowerCase() else val.toUpperCase()
    type: (options) ->
      val = @get('type') or ''
      if options?.lowercase then val.toLowerCase() else val.toUpperCase()

  # just a alias, otherwise PageableCollection will not extends Collection
  Collection = Backbone.Collection
  Collection::_expires = Entity::_expires
  Collection::load = Entity::load

  class SimpleCollection extends Collection
    syncValidation: Entity::syncValidation
    constructor: (models, options) ->
      @url = options.url if options?.url
      @on 'add remove', -> @_last_load = Date.now() - @_expires + 100
      super models, options
    sync: (method, model, options = {}) ->
      @syncValidation method, model, options
      super method, model, options

  class ManagerCollection extends Backbone.PageableCollection
    mode: 'client'
    defaultState:
      pageSize: 255
    constructor: (options...) ->
      @state ?= {}
      for key, value of @defaultState
        @state[key] = value
      @on 'add remove', -> @_last_load = Date.now() - @_expires + 100
      super options...
    syncValidation: Entity::syncValidation
    sync: (method, model, options = {}) ->
      @syncValidation method, model, options
      super method, model, options

  class StatusEntity extends Entity
    defaults:
      status: 'IDLE'
    status: (val, options = {}) ->
      if val?.lowercase?
        options = val
        val = null
      callback = if typeof options is 'function' then options else options.callback
      remotely = callback? or (options.remote ? val?) # use remote mode by default when set
      # get url for remote mode
      if remotely
        url = @url?() or @url
        throw new Error 'cannot get status of a entity remotely without url' unless url
        url += '/' if url[-1..] isnt '/'
        url += 'status'

      unless val? # get
        val = (@get('status') or '').toUpperCase()
        if remotely
          @fetch reset: true, success: =>
            callback? @status options
            return
        if options.lowercase then val.toLowerCase() else val
      else # set
        val = val.toUpperCase()
        if remotely
          unless /^(?:FINISHED|STARTED|STOPPED|PAUSED)$/.test val
            throw new Error 'this status cannot be set remotely', val
          console.log 'update status', val, url
          $.ajax
            url: url
            type: 'PUT'
            data: JSON.stringify(val)
            processData: false
            dataType: 'json'
            contentType: 'application/json'
            headers:
              Authorization: User.current?.get('credential') or ''
            success: (val) =>
              @set 'status', val
              callback? val
            error: (xhr) ->
              console.error 'update status failed', url, val, xhr.responseText
              callback? null, xhr.responseText
        else
          @set 'status', val
        @

  ## Tenant / User

  class Tenant extends Entity
    urlRoot: ROOT + '/tenants'

  class User extends Entity
    idAttribute: 'email' # local
    urlRoot: ROOT + '/users'
    name: ->
      if @has 'name'
        @get 'name'
      else if @has('first_name') and @has('last_name')
        "#{@get 'first_name'} #{@get 'last_name'}"
      else
        @get('first_name') or @get('last_name') or null

#  class Participant extends User

  class Publisher extends User
    defaults:
      type: 'PUBLISHER'

#  class Evalutator extends User

#  class Participants extends SimpleCollection
#    model: Participant
#    url: '/users'

  class Publishers extends ManagerCollection
    @publishers: new Publishers
    model: Publisher
    url: Publisher::urlRoot

  ## Workflow

  class Workflow extends StatusEntity
    _name: 'workflow'
    urlRoot: ROOT + '/workflows'
    constructor: (model, options) ->
      super model, options
      @_createNodeRef = @_createNodeRef.bind @
      @_createLinkRef = @_createLinkRef.bind @
      @_wire()
    _wire: ->
      attr = @attributes
      url = @url?() or @url or ''
      nodes = if Array.isArray(attr.nodes) then attr.nodes else []
      nodes = @nodes = new Nodes nodes, url: url + '/nodes'
      nodes._loaded = nodes.length > 0

      links = if Array.isArray(attr.links) then attr.links else []
      links = @links = new Links links, url: url + '/links'
      links._loaded = links.length > 0

      _deleted = @_deleted = []

      # auto wire nodes
      nodes.forEach @_createNodeRef
      @listenTo nodes, add: @_createNodeRef, remove: (node) =>
        @_removeNodeRef node
        _deleted.push node unless node.isNew()
        return
      # auto sync node ids
      @listenTo nodes, 'add remove', =>
        ids = []
        nodes.forEach (m) -> ids.push m.id unless m?.isNew()
        @set 'node_ids', ids

      # auto wire links
      links.forEach @_createLinkRef
      @listenTo links, add: @_createLinkRef, remove: (link) =>
        @_removeLinkRef link
        _deleted.push link unless link.isNew()
        return
      # auto sync link ids
      @listenTo links, 'add remove', =>
        ids = []
        links.forEach (m) -> ids.push m.id unless m?.isNew()
        @set 'link_ids', ids

      # start node
      if nodes.length
        start_node_id = attr.start_node?.id or attr.start_node_id
        unless start_node_id?
          @startNode = null
        else unless @startNode = nodes[if typeof start_node_id is 'number' then 'at' else 'get'] start_node_id
          console.error 'cannot find node specified by start_node_id', start_node_id, @toJSON()
        if not @startNode and nodes.length
          starts = nodes.filter (n) -> not n.inLinks.length
          if starts.length is 1
            console.warn 'auto detecting start node', attr
            @startNode = starts[0]
          else
            console.error 'cannot find or more than one start node detected', attr
            @startNode = null

      @_sorted = null
      @set {}

      return
    fetch: (options = {}) -> # override for warp
      _success = options.success?.bind @
      options.success = (model, response, options) ->
        model._wire()
        model.nodes._loaded = model.links._loaded = true
        model.trigger 'loaded', model
        _success? model, response, options
      super options
    loaded: ->
      Boolean @nodes?._loaded and @links?._loaded
    sort: (options = {}) ->
      unless @startNode?
        console.error 'cannot sort without start node'
      else if options.force or not @_sorted
        cindex = {}
        nodes_count = 0
        links_count = 0
        level = [@startNode]

        while node = level.shift()
          unless cindex.hasOwnProperty node.cid
            cindex[node.cid] = node
            node.idx = nodes_count++
            for link in node.outLinks
              link.idx = links_count++
              level.push link.nextNode

        @nodes.comparator = @links.comparator = (o) -> o.idx
        @nodes.sort()
        @links.sort()
        # auto reset sorted flag and remove comparator when added
        @nodes.comparator = @links.comparator = (o) =>
          unless o.idx?
            @_sorted = null
            @nodes.comparator = @links.comparator = null
          o.idx

        @_sorted = nodes_count is @nodes.length and links_count is @links.length
      @
    validate: (ignored, options) -> if options?.traverse # by default skip
      try
        throw new Error 'cannot validate a not loaded workflow' unless @loaded()
        if @nodes.length
          startNode = @startNode
          nodes = @nodes
          throw new Error 'no start node' unless nodes.length and startNode?
          link = @links
          link_idx = {}
          nodes.forEach (n, i) -> if not n.inLinks.length and n isnt startNode
            throw new Error "find a node without in links and it is not the start node #{n.id ? i}"
          link.forEach (l, i) ->
            key = "#{l.prevNode.cid}-#{l.nextNode.cid}"
            throw new Error "duplicated link #{key} #{l.id or i}" if link_idx.hasOwnProperty key
          unless @sort(force: true)._sorted
            throw new Error 'after traversed workflow and find nodes or links not visited'
      catch e
        console.error 'validate workflow failed:', e.message, @
        return e
      return
    copy: (workflow, options = {}) -> # create form workflow as template
      throw new Error 'must be copy from a workflow' unless workflow instanceof Workflow
      throw new Error 'cannot copy a invalid workflow' unless workflow.isValid traverse: options.traverse isnt false
      nodes = []
      links = []
      node_index = {}
      silent = silent: true
      _clean = (data) -> if data?
        delete data.id
        delete data.status
        delete data.workflow_id
        delete data.node_id
        delete data.action_id
        return
      workflow.nodes.forEach (node) ->
        cloned_node = node.clone().unset('id', silent)
        .unset('workflow_id', silent).set template_id: node.id, status: 'IDLE'
        if node.has 'actions' # remove id in action
          cloned_node.set 'actions', node.get('actions').map (action) ->
            _clean action
            _clean action.content
            _clean action.event
            _clean action.tracking
            action
        nodes[node.idx] = cloned_node
        cloned_node.idx = node.idx
        node_index[node.id] = cloned_node
        return
      workflow.links.forEach (link) ->
        prevNode = node_index[link.get 'prev_node_id']
        nextNode = node_index[link.get 'next_node_id']
        # remove linked nodes id then ref nodes
        cloned_link = link.clone().unset('id', silent).unset('workflow_id', silent).set
          template_id: link.id, prev_node_id: prevNode.idx, next_node_id: nextNode.idx, status: 'IDLE'
        cloned_link.prevNode = prevNode
        cloned_link.nextNode = nextNode
        links[link.idx] = cloned_link
        cloned_link.idx = link.idx
        return
      attr = workflow.attributes
      start_node_id = if attr.start_node_id? then node_index[attr.start_node_id]?.idx else null
      @clear silent
      @set
        name: attr.name
        desc: attr.desc
        key: attr.key
        start_node_id: start_node_id
        template_id: workflow.id
        nodes: nodes
        links: links
        status: 'IDLE'
      @_wire()
      @
    _save: Entity::save
    save: (attributes = {}, options = {}) -> # local
      contents = []
      _getAttr = (r, i) ->
        attr = r.attributes
        attr.id ?= i.toString()
        if attr.actions?.length then for action, i in attr.actions
          action_id = action.id ?= i.toString()
          action.tracking?.name or= action.name + ' (Tracking)'
          for n in ['content', 'event', 'tracking']
            o = action[n]
            if o?
              o.id ?= Entity.oid()
              o.name or= action.name
          c = action.content
          if c?
            c.node_id = attr.id
            c.action_id = action_id
            contents.push c
            action.content_id = c.id
            delete action.content
        attr
      if @nodes? then attributes.nodes = @nodes.map _getAttr
      if @links? then attributes.links = @links.map _getAttr
      console.log 'save local', @_name, attributes
      ret = super attributes, options
      setTimeout =>
        wf_id = @id
        new Contents(contents).forEach (c) -> c.save workflow_id: wf_id
      , 10
      ret
    destroy: (options) ->
      @nodes.forEach (node) ->
        actions = node.attributes.actions
        if actions?.length then for action in actions
          content_id = action.content_id ? action.content?.id
          new Content(id: content_id).destroy() if content_id?
        return
      super options
    find: ({nodeId, linkId, actionId, callback}) ->
      _cb = (wf) ->
        if linkId?
          link = wf.links.get linkId
        else if nodeId?
          node = wf.nodes.get nodeId
          action = node.actions().get actionId if node and actionId
        else if actionId? # only action id
          console.error 'only action id without node id is not allowed', nodeId, actionId
        callback? {node, link, action}
      if @loaded()
        _cb @
      else
        @fetch reset: true, success: _cb
      @
    createNode: (data, callback) ->
      data.workflow_id ?= @id
      @nodes.create data, wait: true, success: callback
      @
    _createNodeRef: (node) ->
      throw new Error 'it must be a Node object' unless node instanceof Node
      node.set 'workflow_id', @id if @id and not node.has 'workflow_id' # for test data only
      node.workflow = @
      node.inLinks = []
      node.outLinks = []
      node.actions().forEach (action) ->
        action.workflow = node.workflow
        action.node = node
        return
      return
    _removeNodeRef: (node) ->
      # remove all connected links
      node.inLinks.concat(node.outLinks).forEach (link) -> link.destroy()
      return
    createLink: (data, callback) ->
      data.workflow_id ?= @id
      @links.create data, wait: true, success: callback
      @
    _createLinkRef: (link) ->
      throw new Error 'it must be a Link object' unless link instanceof Link
      link.set 'workflow_id', @id if @id and not link.has 'workflow_id' # for test data only
      link.workflow = @
      prevNodeId = link.get 'prev_node_id'
      nextNodeId = link.get 'next_node_id'
      if prevNodeId? and nextNodeId?
        link.prevNode = @nodes[if typeof prevNodeId is 'number' then 'at' else 'get'] prevNodeId
        throw new Error "cannot find prev node with id #{prevNodeId} for link #{link.id}" unless link.prevNode
        link.nextNode = @nodes[if typeof nextNodeId is 'number' then 'at' else 'get'] nextNodeId
        throw new Error "cannot find next node with id #{prevNodeId} for link #{link.id}" unless link.nextNode
      else unless link.prevNode? and link.nextNode?
        throw new Error 'link ' + (link.key or link.id) + ' is broken, prev/next node missing'
      link.prevNode.outLinks.push link
      link.nextNode.inLinks.push link
      return
    _removeLinkRef: (link) ->
      outLinks = link.prevNode.outLinks
      idx = outLinks.indexOf link
      outLinks.splice idx, 1
      inLinks = link.nextNode.inLinks
      idx = inLinks.indexOf link
      inLinks.splice idx, 1
      return
    hasLink: (from, to) ->
      from = @nodes.get from if typeof from is 'string'
      to = @nodes.get to if typeof to is 'string'
      for link in from.outLinks
        return true if link.nextNode is to
      false

  class Workflows extends ManagerCollection
    @workflows: new Workflows
    @find: (options) ->
      wfs = @workflows
      unless wfs.length
        wfs.load (wfs) -> wfs.find options
      else
        wfs.find options
      wfs
    model: Workflow
    url: Workflow::urlRoot
    _expires: 600000 # 10 min
    find: ({workflowId, nodeId, linkId, actionId, callback, fetch}) ->
      throw new Error 'workflowId is required' unless workflowId?
      throw new Error 'async callback is required' unless typeof callback is 'function'

      _find = (workflow) ->
        if nodeId? or linkId? or actionId?
          workflow.find {
            nodeId, linkId, actionId
            callback: (results) ->
              results.workflow = workflow
              callback results
          }
        else if fetch and not workflow.loaded()
          workflow.fetch
            success: (workflow) -> callback {workflow}
            error: -> callback {}
        else
          callback {workflow}

      if workflow = @fullCollection.get workflowId
        _find workflow
      else if fetch is true
        new @model(id: workflowId).fetch
          success: _find
          error: -> callback {}
      else
        callback {}
      @

  ## Project

  class Project extends Workflow
    _name: 'project'
    urlRoot: ROOT + '/projects'
    defaults:
      is_project: true

  class Projects extends Workflows
    @projects: new Projects
    @find: (options) ->
      wfs = @projects
      unless wfs.length
        wfs.load (wfs) -> wfs.find options
      else
        wfs.find options
      wfs
    model: Project
    url: Project::urlRoot
    _expires: 60000 # 1 min

  findProjectOrWorkflow = (options) ->
    unless options.workflowId and typeof options.callback is 'function'
      throw new Error 'workflowId and callback must be given'
    _callback = options.callback
    _tried = false
    options.callback = (ret) -> if _callback
      if ret.workflow or Object.keys(ret).length
        # console.log 'find project or workflow got', ret, _tried
        _callback ret
        _callback = null
      else if _tried
        _callback ret
      _tried = true
      return
    Projects.find options
    Workflows.find options
    return

  # Workflow/Project Sub-entities

  class ChangeObserableEntity extends StatusEntity
    constructor: (model, options) ->
      super model, options
      @setChangeFlag = @setChangeFlag.bind @
      @resetChangeFlag()
    resetChangeFlag: ->
      @setChangeFlag false
    setChangeFlag: (val) ->
      if val?
        @_changed = Boolean val
        @off 'change', @setChangeFlag
        unless val
          @set {}
          @on 'change', @setChangeFlag
      else if not @_changed and @hasChanged() and not @isNew()
        @_changed = true
        @off 'change', @setChangeFlag
      @
    destroy: (options) ->
      super options
      @destroy = -> @ # call once
      @

  class Node extends ChangeObserableEntity
    _name: 'node'
    urlRoot: ROOT + '/nodes'
    name: -> @get 'name'
    actions: -> @_actions ?= new Actions @get 'actions'

  class Nodes extends SimpleCollection
    model: Node
    url: Node::urlRoot

  class Link extends ChangeObserableEntity
    _name: 'link'
    urlRoot: ROOT + '/links'
    name: -> @get('name') or @get('desc') or @get('key')

  class Links extends SimpleCollection
    model: Link
    url: Link::urlRoot

  class Action extends StatusEntity
    constructor: (model, options) ->
      super model, options
      @_wire()
      @on 'reset sync', @_wire.bind @
    _wire: ->
      if @has 'content'
        @content = new Content @get 'content'
        @content.action = @
      else
        @content = null
      for name in ['event', 'tracking']
        if @has name
          evt = @[name] = new Event @get name
          evt.action = @
        else
          @[name] = null
      @
    name: -> @get('name') or @get('type')?.replace(/_/, ' ').capitalize() or '(No Name)'
    urlRoot: ROOT + '/actions'

  class Actions extends SimpleCollection
    @actions: do ->
      actions = new Actions
      _load = _.debounce (wfs) ->
        _actions = []
        _index = {}
        wfs.fullCollection.forEach (wf) -> if wf.loaded()
          wf.nodes.forEach (node) ->
            node.actions().forEach (action) ->
              id = node.id + '-' + action.id ? action.cid
              unless _index[id]?
                _actions.push _index[id] = action
              else if _index[id].cid isnt action.cid
                console.warn 'action id duplicate (ignore)', _index[id], action
              return
            return
          return
        actions.reset _actions
        actions.trigger 'loaded', actions
      , 10
      actions.listenTo Projects.projects, 'loaded', (wfs) ->
        _load wfs if wfs.fullCollection? # otherwise it will got workflow/project loaded event
        return
      actions.load = (callback, options) ->
        Projects.projects.load (wfs, ret) ->
          if actions.length is 0
            actions.once 'loaded', callback
            _load wfs
          else
            callback actions, ret
          return
        , options
        actions
      actions
    model: Action
    url: Action::urlRoot

  ## Home

  class Notification extends Entity
    urlRoot: ROOT + '/notifications'
    get: (attribute) ->
      value = super attribute
      if attribute is 'desc' and @has 'date'
        value.replace '{{date}}', new Date(@get 'date').toLocaleString()
      else
        value

  class Notifications extends ManagerCollection
    @notifications: new Notifications
    model: Notification
    url: Notification::urlRoot

  ## Events

  class Event extends Entity
    urlRoot: ROOT + '/events'
    constructor: (model, options) ->
      super @_proc(model), options
    pause: (attr) -> @_proc attr
    _proc: (attr = @attributes) ->
      if attr.duration?
        duration = Number attr.duration
        attr.duration = unless duration then 0 else duration
      for n in ['starts', 'ends'] then if attr[n]?
        date = new Date attr[n]
        if isNaN date.getTime()
          console.error 'invalid date', attr[n]
          delete attr[n]
        else
          attr[n] = date
      if attr.ends? and Date.now() > attr.ends.getTime()
        attr.status = 'FINISHED'
      else if attr.starts? and Date.now() > attr.starts.getTime()
        attr.status = 'STARTED'
      else
        attr.status = 'IDLE'
      attr
    isEmpty: ->
      attr = @attributes
      Boolean attr.duration or attr.starts or attr.ends

  class Events extends ManagerCollection
    model: Event
    #url: Event::urlRoot # no longer allow fetch directly

  ## Content

  class Content extends Entity
    urlRoot: ROOT + '/contents'
    constructor: (model, options) ->
      super @_proc(model), options
    pause: (attr) -> @_proc attr
    _proc: (attr = @attributes) ->
      posted = attr.posted_at or attr.records?.length or attr.submissions?.length
      attr.status = if posted then 'POSTED' else 'IDLE'
      attr
    posted: -> @attributes.posted_at
    hasReport: -> Boolean(@get('records')?.length or @get('submissions')?.length)

  class Contents extends ManagerCollection
    model: Content
    url: Content::urlRoot

  ## Service

  class Service extends Entity
    idAttribute: 'service'
    urlRoot: ROOT + '/services'
    connected: -> /^CONNECTED$/i.test @get 'status'

  { # exports
  ROOT
  Entity
  Collection
  ManagerCollection
  Tenant
  User
  Publishers
  Publisher
#  Participants
#  Participant
#  Evalutator
  Workflows
  Workflow
  Nodes
  Node
  Links
  Link
  Actions
  Action
  Projects
  Project
  Notifications
  Notification
  Event
  Events
  Content
  Contents
  Service
  findProjectOrWorkflow
  }
