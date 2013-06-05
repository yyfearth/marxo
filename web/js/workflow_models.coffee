"use strict"
define 'workflow_models', ['console'], ({Entity, Tenant, ManagerCollection}) ->
  ROOT = 'https://marxo.cloudfoundry.com/api'
  # ROOT = '/api'

  class Workflow extends Entity

  #  class SharedWorkflow extends Workflow

  class TenantWorkflow extends Workflow
    urlRoot: ROOT + '/workflows'
    initialize: (options...) ->
      super options...
      url = @url?()
      if url
        @nodes = new TenantNodes url: @url() + '/nodes', @get 'nodes'
        @links = new TenantLinks url: @url() + '/links', @get 'links'
      return

  #  class SharedWorkflows extends Backbone.Collection
  #    model: SharedWorkflow
  #    url: ROOT + '/shared/workflows'
  class TenantWorkflows extends ManagerCollection
    model: TenantWorkflow
    url: ROOT + '/workflows'
    # url: -> @tenant.url() + '/workflows'

  #  class SharedNodes extends Backbone.Collection
  #    model: SharedNode
  #    url: ROOT + '/shared/nodes'

  class Node extends Entity

  #  class SharedNode extends Node

  class TenantNode extends Node

  #  class SharedLinks extends Backbone.Collection
  #    model: SharedLink
  #    url: ROOT + '/shared/links'

  class TenantNodes extends Backbone.Collection
    model: TenantNode
    url: ROOT + '/nodes'
  #    url: -> @workflow.url() + '/nodes'

  class Link extends Entity

  #  class SharedLink extends Link

  class TenantLink extends Link

  class TenantLinks extends Backbone.Collection
    model: TenantLink
    url: ROOT + '/links'
  #    url: -> @workflow.url() + '/links'

  #  class SharedActions extends Backbone.Collection
  #    model: Action
  #    url: ROOT + '/shared/actions'

  #  class TenantActions extends Backbone.Collection
  #    model: Action
  #    url: -> @node.url() + '/actions'
  #
  #  class Action extends Entity

  { # exports
  #  Tenant
  #  SharedWorkflows
  #  TenantWorkflows
  #  Workflow
  #  SharedWorkflow
  #  TenantWorkflow
  #  SharedNodes
  #  TenantNodes
  #  Node
  #  SharedNode
  #  TenantNode
  #  SharedLinks
  #  TenantLinks
  #  Link
  #  SharedLink
  #  TenantLink
  #  SharedActions
  #  TenantActions
  #  Action

  TenantWorkflows
  Workflow
  TenantWorkflow
  TenantNodes
  Node
  TenantNode
  TenantLinks
  Link
  TenantLink
  }
