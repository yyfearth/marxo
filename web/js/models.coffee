"use strict"

define 'models', ['module', 'lib/common'], (module) ->

  ## Common

  ROOT = module.config().BASE_URL
  ROOT = ROOT[...-1] if ROOT[-1..] is '/'

  _setAuth = (options) ->
    options.headers ?= {}
    options.headers.Accept ?= 'application/json'
    options.headers.Authorization ?= User.current?.get('credential') or ''
    options

  class Entity extends Backbone.Model
    sync: (method, model, options = {}) ->
      super method, model, _setAuth options

  # just a alias, otherwise PageableCollection will not extends Collection
  Collection = Backbone.Collection

  class SimpleCollection extends Collection
    sync: (method, model, options = {}) ->
      super method, model, _setAuth options

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
    load: (callback, delay = @_delay) ->
      if not @_last_load or delay < 1 or (Date.now() - @_last_load) > delay
        @fetch
          reset: true
          success: (collection, response, options) =>
            @_last_load = Date.now()
            #@trigger 'loaded', collection
            callback? collection, 'loaded', response, options
          error: (collection, response, options) ->
            callback? @, 'error', response, options
      else
        callback? @, 'skipped'
      @
    sync: (method, model, options = {}) ->
      super method, model, _setAuth options

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
      options.success = (collection, response, options) =>
        @_warp collection
        _success? collection, response, options
      super options
      @
    loaded: ->
      Boolean @nodes?._loaded and @links?._loaded
    save: (attributes = {}, options) -> # override for sync ids
      node_ids = @nodes?.map (r) -> r.id
      link_ids = @links?.map (r) -> r.id
      attributes.node_ids = node_ids if node_ids?.join(',') isnt @get('node_ids')?.join(',')
      attributes.link_ids = link_ids if link_ids?.join(',') isnt @get('link_ids')?.join(',')
      if isLocalTest = Backbone.LocalStorage? and @sync isnt Backbone.ajaxSync # for test only
        if @nodes? then attributes.nodes = @nodes?.map (r) -> r.attributes
        if @links? then attributes.links = @links?.map (r) -> r.attributes
      #else # save nodes and links
      @nodes?.forEach (node) =>
        if node.isNew()
          @nodes.create node, wait: true
        else if node._changed
          node.resetChangeFlag()
          node.save()
      @links?.forEach (link) =>
        if link.isNew()
          @links.create link, wait: true
        else if link._changed
          link.resetChangeFlag()
          link.save()

      console.log 'save', @_name, attributes, @
      super attributes, options

      unless isLocalTest # or never delete, until any bkg task to achieve them
        @_deleted.forEach (model) -> model.destroy()

      @
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
      @nodes.create data, wait: true
      @
    _createNodeRef: (node) ->
      throw new Error 'it must be a Node object' unless node instanceof Node
      node.workflow = @ if @_name is 'workflow'
      node.inLinks = []
      node.outLinks = []
      return
    _removeNodeRef: (node) ->
      # remove all connected links
      node.inLinks.concat(node.outLinks).forEach (link) -> link.destroy()
      return
    createLink: (data) ->
      @links.create data, wait: true
      @
    _createLinkRef: (link) ->
      throw new Error 'it must be a Link object' unless link instanceof Link
      unless link.has('prev_node_id') and link.has('next_node_id')
        throw new Error 'link ' + (link.key or link.id) + 'is broken, prev/next node missing'
      link.workflow = @ if @_name is 'workflow'
      prevNodeId = link.get 'prev_node_id'
      nextNodeId = link.get 'next_node_id'
      link.prevNode = @nodes.get prevNodeId
      throw new Error "cannot find prev node with id #{prevNodeId} for link #{link.id}" unless link.prevNode
      link.nextNode = @nodes.get nextNodeId
      throw new Error "cannot find next node with id #{prevNodeId} for link #{link.id}" unless link.nextNode
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
    model: Workflow
    url: Workflow::urlRoot
    _delay: 600000 # 10 min

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
    copy: (workflow, callback) -> # copy form workflow as template
      workflow ?= @get 'workflow_id'
      workflow = new Workflow id: workflow if typeof workflow is 'string'
      throw new Error 'must be create from a workflow' unless workflow instanceof Workflow
      unless workflow.loaded()
        workflow.fetch success: (wf) => @copy wf, callback
      else
        id = @id
        nodes = []
        links = []
        workflow.nodes.forEach (node) ->
          cloned_node = node.clone()
          # TODO: for test only, should be null
          node_id = node.id + 1000000
          cloned_node.set id: node_id, template_id: node.id, project_id: id
          # TODO: for test only, should give action id
          if node.has 'actions'
            cloned_node.set 'actions', node.get('actions').map (action, i) ->
              action.id = i
              action
          nodes.push cloned_node
        workflow.links.forEach (link) ->
          cloned_link = link.clone()
          # TODO: for test only, should be null
          link_id = link.id + 1000000
          cloned_link.set
            id: link_id
            template_id: link.id
            project_id: id
            # TODO: for test only, should be auto updated
            prev_node_id: link.get('prev_node_id') + 1000000
            next_node_id: link.get('next_node_id') + 1000000
          links.push cloned_link
        @set
          workflow_id: workflow.id
          template_id: null
          node_ids: nodes.map((n) -> n.id)
          link_ids: links.map((l) -> l.id)
          nodes: nodes
          links: links
        @_warp @
        console.log @
        callback? @, workflow
      @
    _createNodeRef: (node) ->
      super node
      node.project = @
      @
    _createLinkRef: (link) ->
      super link
      link.project = @
      @

  class Projects extends ManagerCollection
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
    find: ({projectId, nodeId, linkId, actionId, callback}) ->
      throw new Error 'projectId is required' unless projectId
      project = @get projectId
      _find = (project) ->
        if nodeId or linkId or actionId
          project.find {
            nodeId, linkId, actionId
            callback: (results) ->
              results.project = project
              callback? results
          }
        else
          callback? {project}
      if project
        _find project
      else
        new Project(id: projectId).fetch
          success: _find
          error: -> callback? {}
      @

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
