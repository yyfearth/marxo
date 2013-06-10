"use strict"

define 'workflow_models', ['console'], ({Entity, ManagerCollection}) ->
  ROOT = 'https://marxo.cloudfoundry.com/api'
  # ROOT = '/api'

  class Workflow extends Entity
    urlRoot: ROOT + '/workflows'
    initialize: (options...) ->
      super options...
      url = @url?()
      if url
        @nodes = new Nodes url: @url() + '/nodes', @get 'nodes'
        @links = new Links url: @url() + '/links', @get 'links'
      @

  class Workflows extends ManagerCollection
    model: Workflow
    url: ROOT + '/workflows'
  # url: -> @tenant.url() + '/workflows'

  class Node extends Entity

  class Nodes extends Backbone.Collection
    model: Node
    url: ROOT + '/nodes'
  # url: -> @workflow.url() + '/nodes'

  class Link extends Entity

  class Links extends Backbone.Collection
    model: Link
    url: ROOT + '/links'
  # url: -> @workflow.url() + '/links'

  # class Action extends Entity

  # class Actions extends Backbone.Collection
  #    model: Action
  #    url: -> @node.url() + '/actions'

  { # exports
  Workflows
  Workflow
  Nodes
  Node
  Links
  Link
  # Action
  }
