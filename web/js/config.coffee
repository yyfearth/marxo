"use strict"

define 'config', ['base', 'manager', 'models'],
({
find
#findAll
#View
FrameView
InnerFrameView
#ModalDialogView
}, {
ManagerView
ProjectFilterView
}, {
Tenant
Publichers
}) ->

  class ConfigFrameView extends FrameView
    initialize: (options) ->
      super options
      @profile = new TenantProfileView el: '#tenant_profile', parent: @
      @manager = new UserManagemerView el: '#user_manager', parent: @, list: @list
      @connector = new ServiceConnectorView el: '#service_connector', parent: @
      @
    open: (name, sub) ->
      switch name
        when 'users'
          @switchTo @manager
        when 'tenant'
          @switchTo @profile
        when 'service'
          @switchTo @connector
          @connector.open sub if sub
        else
          throw 'empty or unknown sub action for config frame ' + name
      @

  class ServiceConnectorView extends InnerFrameView
    initialize: (options) ->
      super options
    open: (service) ->
      console.log 'connect service details', service

  class TenantProfileView extends InnerFrameView
    initialize: (options) ->
      super options

  #  class UserActionCell extends Backgrid.ActionsCell
  #    render: ->
  #      super
  #      # TODO: show buttons depend on status
  #      view_btn = @el.querySelector('a[name="view"]')
  #      view_btn.href = '#config/users/' + @model.id
  #      @

  class UserManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'title:users' # TODO: user name, title, email, etc.
      'project' # TODO: multiple projects?
      'status'
#    ,
#      name: 'user'
#      label: ''
#      editable: false
#      sortable: false
#      cell: UserActionCell
    ]
    collection: new Publichers
    initialize: (options) ->
      super options
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        field: 'project.id'
        collection: @collection.fullCollection
      @
    reload: ->
      super
      @projectFilter.clear()
    render: ->
      super
      @projectFilter.render()
      @

  ConfigFrameView
