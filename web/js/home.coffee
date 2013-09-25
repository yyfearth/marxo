"use strict"

define 'home', ['base', 'models'],
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
}) ->
  class HomeFrameView extends FrameView
    initialize: (options) ->
      super options
      @notificationList = new NotificationListView el: find('.sidebar-list', @el), parent: @
    _tpl: tpl('#project_overview_tpl')
    render: ->
      _tpl = @_tpl
      html = []
      Projects.projects.fetch success: (projects) =>
        projects.forEach (project) ->
          obj = project.toJSON()
          obj.counts = "(#{project.get('node_ids')?.length} Nodes, #{project.get('link_ids')?.length} Links)"
          html.push fill _tpl, obj
        find('#home_view', @el).innerHTML = html.join '\n'
      @notificationList.fetch()
      @

  class NotificationListView extends NavListView
    auto: false
    urlRoot: 'notification'
    headerTitle: 'Notification'
    itemClassName: 'notification-list-item'
    collection: Notifications.notifications
    defaultItem: null
    events:
      'click': (e) ->
        el = e.target
        if el.tagName is 'A' and el.dataset.id
          e.preventDefault()
          @trigger 'select', el.dataset.id, $(el).data 'model'
          false
    render: ->
      @_clear()
      @_render()
      @el.appendChild @_renderItem
        title: 'View All >'
        href: '#notification'
      @

  HomeFrameView
