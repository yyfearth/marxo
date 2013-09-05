"use strict"

define 'config', ['base', 'manager', 'models'],
({
find
#findAll
#View
FrameView
InnerFrameView
FormViewMixin
#ModalDialogView
}, {
ManagerView
ProjectFilterView
}, {
Tenant
Publishers
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
    @acts_as FormViewMixin
    model: new Tenant(id: 0) # fake
    events:
      'click .btn-reset': 'reset'
    initialize: (options) ->
      super options
      @initForm()
      @form.onreset = =>
        setTimeout =>
          @fill @model.attributes
        , 1
      @on 'submit', @save.bind @
      @
    save: ->
      $btn = $(@_submit_btn)
      $btn.button 'loading'
      data = @read()
      console.log 'save', data
      @model.save data, success: ->
        $btn.button 'reset'
      @
    render: ->
      super
      @model.fetch success: (data) =>
        console.log 'fetch tenant', data.attributes
        @model = data
        @fill data.attributes
      @

  class UsernameCell extends Backgrid.StringCell
    render: ->
      @$el.text "#{@model.get 'first_name'} #{@model.get 'last_name'}"
      @

  class UserManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
    ,
      name: 'email'
      label: 'Email'
      cell: 'string'
      editable: false
    ,
      name: 'first_name'
      label: 'Username'
      cell: UsernameCell
      editable: false
    ,
      # TODO: support display and filter by multiple projects
      'status'
      'actions:user'
    ]
    collection: new Publishers
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
