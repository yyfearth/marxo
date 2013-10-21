"use strict"

define 'home', ['base', 'models', 'notification'],
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
Project
Projects
Notifications
}, {
NotificationListView
}) ->

  class HomeFrameView extends FrameView
    collection: Projects.projects
    initialize: (options) ->
      super options
      @notificationList = new NotificationListView el: find('.sidebar-list', @el), parent: @
      @_render = @_render.bind @
      @listenTo @collection, 'reset add remove', @_render
      return
    _tpl: tpl('#project_overview_tpl')
    _render: ->
      _tpl = @_tpl
      html = []
      @collection.forEach (project) ->
        obj = project.toJSON()
        obj.counts = "(#{project.get('node_ids')?.length} Nodes, #{project.get('link_ids')?.length} Links)"
        html.push fill _tpl, obj
      find('#home_view', @el).innerHTML = html.join '\n'
      return
    render: ->
      @collection.load @_render
      @notificationList.fetch()
      @

  HomeFrameView
