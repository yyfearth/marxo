"use strict"

define 'models', ['module', 'lib/common'], (module) ->

  ## Common

  ROOT = module.config().BASE_URL or '/api'
  ROOT = ROOT[...-1] if ROOT[-1..] is '/'

  class Entity extends Backbone.Model
    _expires: 1000
    syncValidation: (method, model, options = {}) ->
      options.dataType ?= 'json'
      options.headers ?= {}
      options.headers.Authorization ?= User.current?.get('credential') or ''
      throw new Error 'no auth, need user login' unless options.headers.Authorization
      if method isnt 'read'
        cur_tenant_id = User.current?.get 'tenant_id'
        throw new Error 'cannot create or update or delete without login user' unless cur_tenant_id?
        switch method
          when 'create', 'update'
            unless model.has 'tenant_id'
              model.set 'tenant_id', cur_tenant_id
            else if cur_tenant_id isnt model.get 'tenant_id'
              console.log cur_tenant_id, model.get 'tenant_id'
              throw new Error 'cannot replace existing tenant_id'
          when 'delete'
            if cur_tenant_id isnt model.get 'tenant_id'
              throw new Error 'cannot delete item not belong to your tenant'
          when 'read'
            break
          else
            throw new Error 'unsupported method ' + method
      return
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
    status: (val, options = {}) ->
      callback = if typeof options is 'function' then options else options.callback
      remotely = callback? or (options.remote ? val?) # use remote mode by default when set
      # get url for remote mode
      if remotely
        url = @url?() or @url
        throw new Error 'cannot get status of a entity remotely without url' unless url
        url += '/' unless url[-1..] isnt '/'
        url += 'status'

      unless val? # get
        val = (@get('status') or '').toUpperCase()
        if remotely
          $.get url, (status) =>
            @set 'status', status if status isnt val
            callback? status
          .fail -> callback? val
        val
      else # set
        val = val.toUpperCase()
        if remotely
          unless /^(?:FINISHED|STARTED|STOPPED|PAUSED)$/.test val
            throw new Error 'this status cannot be set remotely', val
          console.log 'update status', val, url
          $.ajax
            url: url
            type: 'PUT'
            data: val
            dataType: 'text'
            mimeType: 'text/plain'
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
    urlRoot: ROOT + '/users'
    name: ->
      if @has 'name'
        @get 'name'
      else if @has('first_name') and @has('last_name')
        "#{@get 'first_name'} #{@get 'last_name'}"
      else
        @get('first_name') or @get('last_name') or null

# TODO: service: by default, publisher can get the list of members
# and cannot get list of participant, but can get participant by id

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

      _createNodeRef = @_createNodeRef.bind @
      nodes.forEach _createNodeRef
      @listenTo nodes, add: _createNodeRef, remove: (node) =>
        @_removeNodeRef node
        _deleted.push node unless node.isNew()
        return

      _createLinkRef = @_createLinkRef.bind @
      links.forEach _createLinkRef
      @listenTo links, add: _createLinkRef, remove: (link) =>
        @_removeLinkRef link
        _deleted.push link unless link.isNew()
        return

      # start node
      if nodes.length
        start_node_id = attr.start_node_id
        if start_node_id?
          unless @startNode = nodes[if typeof start_node_id is 'number' then 'at' else 'get'] start_node_id
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
      @
    loaded: ->
      Boolean @nodes?._loaded and @links?._loaded
    sort: (options = {}) ->
      if options.force or not @_sorted
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
      workflow.nodes.forEach (node) ->
        cloned_node = node.clone().unset('id', silent)
        .unset('workflow_id', silent).set template_id: node.id
        if node.has 'actions' # remove id in action
          cloned_node.set 'actions', node.get('actions').map (action) ->
            new Action(action).unset('id', silent).toJSON()
        nodes[node.idx] = cloned_node
        cloned_node.idx = node.idx
        node_index[node.id] = cloned_node
        return
      workflow.links.forEach (link) ->
        prevNode = node_index[link.get 'prev_node_id']
        nextNode = node_index[link.get 'next_node_id']
        # remove linked nodes id then ref nodes
        cloned_link = link.clone().unset('id', silent).unset('workflow_id', silent).set
          template_id: link.id, prev_node_id: prevNode.idx, next_node_id: nextNode.idx
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
      @_wire()
      @
    save: (attributes = {}, options = {}) ->
      throw new Error 'cannot save workflow by given nodes or links directly' if attributes.nodes or attributes.links
      console.log 'saving', @_name, attributes, @
      # replace original callbacks
      _success = options.success
      _err = options.error
      options.success = (wf, resp, opt) =>
        # console.log 'saved wf', wf
        # save nodes
        workflow_id = wf.id
        console.log 'workflow_id', workflow_id
        save_opt = wait: true, reset: true
        requests = []
        @nodes?.forEach (node) =>
          if node.isNew()
            # console.log 'create node', workflow_id
            requests.push node.save {workflow_id}, save_opt
          else if node._changed
            node.resetChangeFlag().save()
          return
        $.when.apply($, requests).then =>
          # console.log 'new nodes created', @nodes
          # then save links
          requests = []
          @links?.forEach (link) =>
            if link.isNew()
              if not link.prevNode? and 'number' is typeof idx = link.get 'prev_node_id'
                link.prevNode = @nodes.at idx
              if not link.nextNode? and 'number' is typeof idx = link.get 'next_node_id'
                link.nextNode = @nodes.at idx
              attr =
                workflow_id: workflow_id
                prev_node_id: link.prevNode.id
                next_node_id: link.nextNode.id
              # console.log 'create link', attr
              requests.push link.save attr, save_opt
            else if link._changed
              link.resetChangeFlag().save()
            return
          # update workflow for start node
          if @startNode and @get('start_node_id') isnt @startNode.id
            requests.push @save start_node_id: @startNode.id
          if (typeof _success is 'function') or (typeof _err is 'function')
            $.when.apply($, requests).then =>
              # console.log 'new links created', @links
              # finally done
              console.log 'saved', @_name, attributes, @
              _success wf, resp, opt
            , =>
              console.error 'fail to save links for wf', @
              _err wf, null, options
              return
          console.log 'saving wf', @
          return
        , =>
          console.error 'fail to save nodes for wf', @
          _err? wf, null, options
          return
        # delete unused nodes/links (no wait)
        @_deleted?.forEach (model) -> model.destroy()
        return
      # save workflow
      @unset 'start_node_id', silent: true if typeof @get 'start_node_id' is 'number'
      @unset 'nodes', silent: true
      @unset 'links', silent: true
      super attributes, options
    find: ({nodeId, linkId, actionId, callback}) ->
      _cb = (wf) =>
        if linkId
          link = wf.links.get linkId
        else if nodeId
          node = wf.nodes.get nodeId
          action = node.actions().get actionId if node and actionId
        else if actionId # only action id
          unless idx = @_action_index
            idx = @_action_index = {}
            wf.nodes.forEach (node) ->
              node.actions().forEach (action) -> if action.id?
                action.node = node
                console.warn 'duplicated action id', action.id, action, node if idx[action.id]
                idx[action.id] = action
                return
              return
          action = idx[actionId]
          node = action?.node
        callback? {node, link, action}
      if @loaded()
        _cb @
      else
        @fetch reset: true, success: _cb
      @
    createNode: (data) ->
      data.workflow_id ?= @id
      @nodes.create data, wait: true
      @
    _createNodeRef: (node) ->
      throw new Error 'it must be a Node object' unless node instanceof Node
      node.set 'workflow_id', @id if @id and not node.has 'workflow_id' # for test data only
      node.workflow = @
      node.inLinks = []
      node.outLinks = []
      return
    _removeNodeRef: (node) ->
      # remove all connected links
      node.inLinks.concat(node.outLinks).forEach (link) -> link.destroy()
      return
    createLink: (data) ->
      data.workflow_id ?= @id
      @links.create data, wait: true
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
      throw new Error 'workflowId is required' unless workflowId
      throw new Error 'async callback is required' unless typeof callback is 'function'

      _find = (workflow) ->
        if nodeId or linkId or actionId
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
    name: -> @get('name') or @get('type')?.replace(/_/, ' ').capitalize() or '(No Name)'
  # idAttribute: 'index'

  class Actions extends SimpleCollection
    @actions: new Actions
    @find: (id, callback) -> @actions.find id, callback
    model: Action
    find: (id, callback) ->
      unless @length
        @load (actions) -> actions.find id, callback
      else
        callback? @get id
      @
  # url: -> @node.url() + '/actions'

  ## Project

  class Project extends Workflow
    _name: 'project'
    urlRoot: ROOT + '/projects'

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

  class Events extends ManagerCollection
    model: Event
    url: Event::urlRoot

  ## Content

  class Content extends Entity
    urlRoot: ROOT + '/contents'

  class Contents extends ManagerCollection
    model: Content
    url: Content::urlRoot

  ## Report

  class Report extends Entity

  class Reports extends ManagerCollection
    model: Report
    url: ROOT + '/reports'

  ## Service

  class Service extends Entity
    idAttribute: 'service'
    urlRoot: ROOT + '/services'
    connected: ->
      /CONNECTED/i.test @get 'status'

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
  Report
  Reports
  Service
  findProjectOrWorkflow
  }
