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
      @center = new NotificationCenterView el: @el, parent: @
    render: ->
      @center.render()
      @

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
      @

  class NotificationCenterView extends ManagerView
    columns: [
      'id'
    ,
      name: 'title'
      label: 'Title'
      cell: 'tooltip'
      placement: 'right'
      tooltip: 'desc'
      editable: false
    ,
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
      @statusFilter = new NavFilterView
        el: find('.status-filter', @el)
        field: 'status'
        collection: collection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: collection
        allowEmpty: true
      @
    reload: ->
      super
      @projectFilter.clear()
    render: ->
      super
      @projectFilter.render()
      @

  # TODO: auto reload

  class NotificationListView extends NavListView
    auto: false
    urlRoot: 'notification'
    headerTitle: 'Notification'
    itemClassName: 'notification-list-item'
    collection: Notifications.notifications
    defaultItem: null
    initialize: (options) ->
      super options
      # popover details
      _renderPopover = @_renderPopover.bind @
      @$el.popover
        html: true
        selector: 'a.pointer'
        container: 'body'
        placement: 'right'
        trigger: 'click'
        title: -> @innerHTML
        content: ->
          $el = $(@).addClass 'active'
          popover = $el.data 'popover'
          setTimeout ->
            # hide after click anywhere after shown
            $el.removeClass 'active'
            $(document.body).one 'click', -> popover.hide()
          , 100
          _renderPopover $el.data 'model'
      @
    autoUpdate: (val) ->
      #console.log 'set auto update notifications', val
      @_auto_update = clearInterval @_auto_update if @_auto_update
      if val then @_auto_update = setInterval =>
        console.log 'auto update notifications'
        @fetch()
      , @_reload_timeout + 1
      @
    _renderPopover: (model) ->
      console.log 'render popover', model
      # html details
      div = document.createElement 'div'
      div.className = 'notification-content'
      p = document.createElement 'p'
      p.innerHTML = model.escape 'desc'
      div.appendChild p
      if 'ACTIVE' is model.get 'status'
        if model.has 'expires_at'
          small = document.createElement 'small'
          expires_at = new Date(model.get 'expires_at').toLocaleString()
          small.innerHTML = "Expected expires at #{expires_at}"
          div.appendChild small
        if model.has 'target_url'
          type = model.get 'type'
          btn = document.createElement 'a'
          btn.href = model.get 'target_url'
          btn.className = 'btn btn-small'
          if type is 'ROUTINE'
            btn.innerHTML = 'View &raquo;'
          else
            btn.className += ' btn-primary icon-right-open'
            btn.innerHTML = 'Process'
          div.appendChild btn
      div
    _render: (models = @collection) ->
      models = models.fullCollection if models.fullCollection
      #console.log 'render models', models
      fragments = document.createDocumentFragment()
      col = models.filter (model) ->
        model._date = new Date(model.get('updated_at') or model.get('created_at')).getTime()
        model._before = Date.now() - model._date
        switch model.get 'status'
          when 'EXPIRED'
            false
          when 'PROCESSED'
            model._before < 86400000 # 1d
          else
            model._before < 2592000000 # 30d
      _.sortBy(col, (model) ->
        t = model._before
        switch model.get 'type'
          when 'EMERGENT'
            t
          when 'REQUISITE'
            10000000000 + t
          else
            20000000000 + t
      ).forEach (model) => fragments.appendChild @_renderItem model
      @el.appendChild fragments
    _statusCls:
      'ROUTINE': 'text-info'
      'REQUISITE': 'text-warning'
      'EMERGENT': 'text-error'
    _renderItem: (model = @defaultItem) ->
      #console.log 'render item', model
      li = document.createElement 'li'
      li.className = @itemClassName if @itemClassName
      a = document.createElement 'a'
      if model.id
        a.className = "pointer #{cls}"
        a.dataset.id = model.id
        $.data a, 'model', model
        cls = @_statusCls[model.get('type')] or ''
        span = document.createElement 'span'
        span.textContent = model.get 'title'
        span.className = cls
        # TODO: only new notification add icon
        i = document.createElement 'i'
        i.className = "icon-notify #{cls}"
        a.appendChild i
        a.appendChild span
      else if model.href
        a.href = model.href
        a.textContent = model.title
      li.appendChild a
      li
    render: ->
      @_clear()
      @_render()
      @el.appendChild @_renderItem
        title: 'View All >'
        href: '#notification'
      @

  # exporet
  NotificationFrameView.NotificationListView = NotificationListView

  NotificationFrameView
