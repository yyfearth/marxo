define 'workflow_models', ['console'], ({Entity}) ->
  class Tenant extends Entity
    idAttribute: '_name'

  class SharedWorkflows extends Backbone.Collection
    model: SharedWorkflow
    url: '/shared/workflows'

  class TenantWorkflows extends Backbone.Collection
    model: TenantWorkflow
    url: -> @tenant.url() + '/workflows'

  class Workflow extends Entity

  class SharedWorkflow extends Workflow

  class TenantWorkflow extends Workflow

  class SharedNodes extends Backbone.Collection
    model: SharedNode
    url: '/shared/nodes'

  class TenantNodes extends Backbone.Collection
    model: TenantNode
    url: -> @workflow.url() + '/nodes'

  class Node extends Entity

  class SharedNode extends Node

  class TenantNode extends Node

  class SharedLinks extends Backbone.Collection
    model: SharedLink
    url: '/shared/links'

  class TenantLinks extends Backbone.Collection
    model: TenantLink
    url: -> @workflow.url() + '/links'

  class Link extends Entity

  class SharedLink extends Link

  class TenantLink extends Link

  class SharedActions extends Backbone.Collection
    model: Action
    url: '/shared/actions'

  class TenantActions extends Backbone.Collection
    model: Action
    url: -> @node.url() + '/actions'

  class Action extends Entity

  { # exports
  Tenant
  SharedWorkflows
  TenantWorkflows
  Workflow
  SharedWorkflow
  TenantWorkflow
  SharedNodes
  TenantNodes
  Node
  SharedNode
  TenantNode
  SharedLinks
  TenantLinks
  Link
  SharedLink
  TenantLink
  SharedActions
  TenantActions
  Action
  }
