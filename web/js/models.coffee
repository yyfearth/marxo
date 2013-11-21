"use strict"

define 'models', ['module', 'lib/common'], (module) ->

  ## Common

  ROOT = module.config().BASE_URL
  ROOT = ROOT[...-1] if ROOT[-1..] is '/'

  syncValidation = (method, model, options = {}) ->
    options.headers ?= {}
    options.headers.Accept ?= 'application/json'
    options.headers.Authorization ?= User.current?.get('credential') or ''
    throw new Error 'no auth, need user login' unless options.headers.Authorization
    if method isnt 'read'
      cur_tenant_id = User.current?.get 'tenant_id'
      throw new Error 'cannot create or update or delete without login user' unless cur_tenant_id
      switch method
        when 'create', 'update'
          unless model.has 'tenant_id'
            model.set 'tenant_id', cur_tenant_id
          else if cur_tenant_id isnt model.get 'tenant_id'
            throw new Error 'cannot replace existing tenant_id'
        when 'delete'
          if cur_tenant_id isnt model.get 'tenant_id'
            throw new Error 'cannot delete item not belong to your tenant'
        when 'read'
          break
        else
          throw new Error 'unsupported method ' + method
    return

  class Entity extends Backbone.Model
    sync: (method, model, options = {}) ->
      syncValidation method, model, options
      super method, model, options

  # just a alias, otherwise PageableCollection will not extends Collection
  Collection = Backbone.Collection

  class SimpleCollection extends Collection
    sync: (method, model, options = {}) ->
      syncValidation method, model, options
      super method, model, options

  class ManagerCollection extends Backbone.PageableCollection
    mode: 'client'
    defaultState:
      pageSize: 15
    _delay: 1000 # 1s
    constructor: (options...) ->
      @state ?= {}
      for key, value of @defaultState
        @state[key] = value
      super options...
    load: (callback, options = {}) ->
      options = delay: options if typeof options is 'number'
      options.delay ?= @_delay
      options.reset ?= true
      if not @_last_load or options.delay < 1 or (Date.now() - @_last_load) > options.delay
        @fetch
          reset: options.reset
          success: (collection, response, options) =>
            @_last_load = Date.now()
            @trigger 'loaded', collection
            callback? collection, 'loaded', response, options
          error: (collection, response, options) =>
            callback? @, 'error', response, options
      else
        callback? @, 'skipped'
      @
    sync: (method, model, options = {}) ->
      syncValidation method, model, options
      super method, model, options

  ## Tenant / User

  class Tenant extends Entity
    urlRoot: ROOT + '/tenants'

  class User extends Entity
    urlRoot: ROOT + '/users'
    idAttribute: 'email'
    fullname: ->
      if @has 'full_name'
        @get 'full_name'
      else if @has('first_name') and @has('last_name')
        "#{@get 'first_name'} #{@get 'last_name'}"
      else if @has 'name'
        @get 'name'
      else
        @get('first_name') or @get('last_name') or null

# TODO: service: by default, publisher can get the list of members
# and cannot get list of participant, but can get participant by id

#  class Participant extends User

  class Publisher extends User

#  class Evalutator extends User

#  class Participants extends SimpleCollection
#    model: Participant
#    url: '/users'

  class Publishers extends ManagerCollection
    @publishers: new Publishers
    model: Publisher
    url: Publisher::urlRoot

  ## Workflow

  class Workflow extends Entity
    _name: 'workflow'
    urlRoot: ROOT + '/workflows'
    constructor: (model, options) ->
      super model, options
      @_warp model
    _warp: (model = @) ->
      model = model.attributes if model instanceof @constructor
      url = @url?() or @url or ''
      _nodes_loaded = Array.isArray model.nodes
      nodes = if _nodes_loaded then model.nodes else []
      nodes = @nodes = new Nodes nodes, url: url + '/nodes'
      nodes._loaded = _nodes_loaded

      _links_loaded = Array.isArray model.links
      links = if _links_loaded then model.links else []
      links = @links = new Links links, url: url + '/links'
      links._loaded = _links_loaded

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

      @set {}

      return
    fetch: (options = {}) -> # override for warp
      _success = options.success?.bind @
      options.success = (model, response, options) =>
        @_warp model
        @trigger 'loaded', model
        _success? model, response, options
      super options
      @
    loaded: ->
      Boolean @nodes?._loaded and @links?._loaded
    copy: (workflow) -> # create form workflow as template
      throw new Error 'must be copy from a workflow' unless workflow instanceof Workflow
      nodes = []
      links = []
      node_index = {}
      silent = silent: true
      workflow.nodes.forEach (node, i) ->
        cloned_node = node.clone().unset('id', silent)
        .unset('workflow_id', silent).set template_id: node.id
        if node.has 'actions' # remove id in action
          cloned_node.set 'actions', node.get('actions').map (action) ->
            new Action(action).unset('id', silent).toJSON()
        nodes.push cloned_node
        cloned_node.idx = i
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
        links.push cloned_link
      attr = workflow.attributes
      @set
        name: attr.name
        desc: attr.desc
        key: attr.key
        template_id: workflow.id
        nodes: nodes
        links: links
      @_warp @
      @
    save: (attributes = {}, options = {}) -> # override for sync ids
      if Backbone.LocalStorage? # for local sync
        _getAttr = (r, i) ->
          attr = r.attributes
          attr.id ?= i
          if Array.isArray attr.actions
            action.id ?= i for action, i in attr.actions
          attr
        if @nodes? then attributes.nodes = @nodes.map _getAttr
        if @links? then attributes.links = @links.map _getAttr
        console.log 'save local', @_name, attributes
        super attributes, options
      else # for ajax sync
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
        @unset 'nodes', silent: true
        @unset 'links', silent: true
        super attributes, options
    find: ({nodeId, linkId, actionId, callback}) ->
      n = @_name
      if @loaded()
        if linkId
          link = @links.get linkId
        else if nodeId
          node = @nodes.get nodeId
          action = node.actions().get actionId if actionId
        callback? {node, link, action}
      else if linkId
        id = @id
        new Link(id: linkId).fetch
          error: ->
            callback? {}
          success: (link) ->
            link = null if id isnt link.get n + '_id'
            callback? {link}
      else if nodeId
        projectId = @id
        new Node(id: nodeId).fetch
          error: ->
            callback? {}
          success: (node) ->
            node = null if projectId isnt node.get n + '_id'
            action = node.actions().get actionId if node and actionId
            callback? {node, action}
      else
        callback? {}
      @
    createNode: (data) ->
      data.workflow_id ?= @id
      @nodes.create data, wait: true
      @
    _createNodeRef: (node) ->
      throw new Error 'it must be a Node object' unless node instanceof Node
      node.set 'workflow_id', @id if @id and not node.has 'workflow_id' # for test data only
      node.workflow = @ if @_name is 'workflow'
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
      link.workflow = @ if @_name is 'workflow'
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
      unless @workflows.length
        @workflows.load (wfs) ->
          wfs.find options
      else
        @workflows.find options
      @workflows
    model: Workflow
    url: Workflow::urlRoot
    _delay: 600000 # 10 min
    find: ({workflowId, nodeId, linkId, actionId, callback, nofetch}) ->
      throw new Error 'workflowId is required' unless workflowId
      workflow = @get workflowId
      _find = (workflow) ->
        if nodeId or linkId or actionId
          workflow.find {
            nodeId, linkId, actionId
            callback: (results) ->
              results.workflow = workflow
              callback? results
          }
        else
          callback? {workflow}
      if workflow
        _find workflow
      else if nofetch is true
        new @model(id: workflowId).fetch
          success: _find
          error: -> callback? {}
      else
        callback? {}
      @

  class ChangeObserableEntity extends Entity
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
  # url: -> @workflow.url() + '/nodes'

  class Link extends ChangeObserableEntity
    _name: 'link'
    urlRoot: ROOT + '/links'
    name: -> @get('name') or @get('desc') or @get('key')

  class Links extends SimpleCollection
    model: Link
    url: Link::urlRoot
  # url: -> @workflow.url() + '/links'

  class Action extends Entity
  # idAttribute: 'index'

  class Actions extends SimpleCollection
    model: Action
  # url: -> @node.url() + '/actions'

  ## Project

  class Project extends Workflow
    _name: 'project'
    urlRoot: ROOT + '/projects'

  class Projects extends Workflows
    @projects: new Projects
    @find: (options) ->
      unless @projects.length
        @projects.load (projects) ->
          projects.find options
      else
        @projects.find options
      @projects
    model: Project
    url: Project::urlRoot
    _delay: 60000 # 1 min

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
  }
