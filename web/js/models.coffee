"use strict"

define 'models', ['lib/common'], ->
  # ROOT = 'https://marxo.cloudfoundry.com/api'
  ROOT = '/api'

  ## Common

  Entity = Backbone.Model
  #class Entity extends Backbone.Model
  #  set: (attrs, options) ->
  #    @_name = attrs.name.tolowerCase().replace /\W+/g, '_' if attrs.name
  #    super attrs, options
  #  validate: (attrs) ->
  #    unless attrs.name and attrs.id
  #      'id and name are required'
  #    else unless /\w{,10}/.test attrs.name
  #      'name max len is 10 and must be consist of alphabetic char or _'
  #    else
  #      return

  # just a alias, otherwise PageableCollection will not extends Collection
  Collection = Backbone.Collection

  class ManagerCollection extends Backbone.PageableCollection
    mode: 'client'
    defaultState:
      pageSize: 15
      sortKey: 'updated_at'
      order: -1
    constructor: (options...) ->
      @state ?= {}
      for key, value of @defaultState
        @state[key] = value
      super options...

  ## Tenant / User

  class Tenant extends Entity
    urlRoot: '/tenants'

  class User extends Entity

  class Participant extends User

  class Publicher extends User

  class Evalutator extends User

  class Participants extends Collection
    model: Participant
    url: '/users'

  class Publichers extends Collection
    model: Publicher
    url: -> (@tenant?.url?() or '') + '/users'

  ## Workflow

  class Workflow extends Entity
    urlRoot: ROOT + '/workflows'
    initialize: (model, options) ->
      super model, options
      @warp model
      @on
        sync: => @warp @, true
        reset: => @_warped = false
      @
    warp: (model = @, rewarp) ->
      if model is true
        rewarp = true
        model = @attributes
      return @ if @_warped and not rewarp
      model = model.attributes if model instanceof @constructor
      #url = @url?() or @url or ''
      if Array.isArray model.nodes
        nodes = model.nodes
      else if Array.isArray model.nodeIds
        nodes = model.nodeIds.map (id) -> new Node id: id
      else
        nodes = []
      @nodes = new Nodes nodes
      # url: url + '/nodes'

      if Array.isArray model.links
        links = model.links
      else if Array.isArray model.linkIds
        links = model.linkIds.map (id) -> new link id: id
      else
        links = []
      @links = new Links links
      # url: url + '/links'

      @set {}
      @_warped = true
      @
    load: (reload, callback) ->
      if typeof reload is 'function'
        callback = reload
        reload = false
      @warp()
      if @_load and not reload
        callback? null, @
      else async.parallel [
        (callback) =>
          @nodes.fetch success: ((c) ->
            callback null, c), error: ->
            callback 'fetch nodes failed'
        (callback) =>
          @links.fetch success: ((c) ->
            callback null, c), error: ->
            callback 'fetch links failed'
      ], (err) =>
        if err
          console.error err
          callback? err
        else
          console.log 'workflow', @
          @_loaded = true
          callback? null, @
      @

  class Workflows extends ManagerCollection
    model: Workflow
    url: ROOT + '/workflows'
  # url: -> @tenant.url() + '/workflows'

  class Node extends Entity

  class Nodes extends Collection
    model: Node
    url: ROOT + '/nodes'
  # url: -> @workflow.url() + '/nodes'

  class Link extends Entity

  class Links extends Collection
    model: Link
    url: ROOT + '/links'
  # url: -> @workflow.url() + '/links'

  class Action extends Entity

  class Actions extends Collection
    model: Action
  # url: -> @node.url() + '/actions'

  ## Project

  class Project extends Entity

  class Projects extends ManagerCollection
    @projects = new Projects
    model: Project
    url: ROOT + '/projects'

  ## Home

  class Notification extends Entity

  class Notifications extends ManagerCollection
    model: Notification
    url: ROOT + '/notifications'

  ## Content

  class Content extends Entity

  class Contents extends ManagerCollection
    model: Content
    url: ROOT + '/contents'


  { # exports
  Entity
  Collection
  ManagerCollection
  Tenant
  User
  Participants
  Publichers
  Participant
  Publicher
  Evalutator
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
  Notification
  Notifications
  Content
  Contents
  }
