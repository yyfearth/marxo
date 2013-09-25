"use strict"

define 'notification', ['base', 'manager', 'models'],
({
find
#findAll
tpl
fill
#View
FrameView
NavListView
#ModalDialogView
}, {
ManagerView
NavFilterView
ProjectFilterView
}, {
Projects
Notifications
}) ->
  class NotificationFrameView extends FrameView
    initialize: (options) ->
      super options
      @manager = new NotificationManagemerView el: @el, parent: @
    render: ->
      @manager.render()
      @

  # TODO: auto mute expired

  class NotificationActionCell extends Backgrid.ActionsCell
    render: ->
      super
      model = @model
      btn = @_find 'process', 'a'
      status = model.get 'status'
      if status is 'ACTIVE' and model.has 'target_url'
        btn?.href = model.get 'target_url'
      else
        @_hide btn
      @_hide 'mute' if status is 'EXPIRED'
      @

  class NotificationManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'title'
      'project'
    ,
      name: 'type'
      label: 'Type'
      cell: 'label'
      cls:
        routine: 'label-info'
        requisite: 'label-warning'
        emergent: 'label-important'
      editable: false
    ,
      name: 'status'
      label: 'Status'
      cell: 'label'
      cls:
        active: 'label-warning'
        precessed: 'label-success'
        expired: 'label-default'
      editable: false
    ,
      'created_at'
      'updated_at'
    ,
      name: 'notification'
      label: ''
      editable: false
      sortable: false
      cell: NotificationActionCell
    ]
    collection: new Notifications
    initialize: (options) ->
      super options
      collection = @collection.fullCollection
      #@editor = new NotificationEditor el: '#user_editor', parent: @
      @statusFilter = new NavFilterView
        el: find('.status-filter', @el)
        field: 'status'
        collection: collection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: collection
        allowEmpty: true
      #@on 'create edit', @edit.bind @
      #@on 'remove remove_selected', @remove.bind @
      @
    reload: ->
      super
      @projectFilter.clear()
    #remove: (users) ->
    #  users = [users] unless Array.isArray users
    #  @_remove user for user in users
    #  @refresh()
    #  @
    #_remove: (user) ->
    #  email = user.get 'email'
    #  if @signin_user.email is email
    #    alert 'Cannot remove currently signed in user ' + email
    #  else if confirm 'Are you sure to remoe user ' + email + '?\n\nIt cannot be restored after removal!'
    #    user.destroy()
    render: ->
      super
      @projectFilter.render()
      # logined user
      @signin_user = JSON.parse sessionStorage.user
      @

  NotificationFrameView
