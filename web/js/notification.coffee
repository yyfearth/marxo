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
      super

  class NotificationActionCell extends Backgrid.ActionsCell
    render: ->
      super
      # model = @model
      # btn = @_find 'process', 'a'
      # if model.has 'target_url'
      #   btn?.href = model.get 'target_url'
      # else
      # @_hide btn
      @_hide 'process'
      @

  class NotificationCenterView extends ManagerView
    columns: [
      'id'
    ,
      name: 'name'
      label: 'Name'
      cell: 'tooltip'
      placement: 'right'
      tooltip: 'desc'
      editable: false
    ,
      name: 'workflow_id'
      label: 'Project'
      cell: 'workflow'
      editable: false
    ,
      name: 'level'
      label: 'Level'
      cell: 'label'
      cls:
        #minor: ''
        #trivial: ''
        normal: 'label-info'
        major: 'label-warning'
        critical: 'label-important'
        fatal: 'label-important'
        error: 'label-important'
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
    _levelCls:
      minor: ''
      trivial: ''
      normal: 'label-info'
      major: 'label-warning'
      critical: 'label-important'
      fatal: 'label-important'
      error: 'label-important'
    collection: new Notifications
    initialize: (options) ->
      super options
      collection = @collection.fullCollection
      @levelFilter = new NavFilterView
        el: find('.level-filter', @el)
        field: 'level'
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
      @autoUpdate = @autoUpdate.bind @
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
      @_auto_update = clearTimeout @_auto_update if @_auto_update
      if val then @fetch
        success: =>
          console.log 'auto update notifications successful'
          @_auto_update = setTimeout @autoUpdate, @_reload_timeout + 1
        error: -> console.log 'auto update notifications failed, stop auto update'
      @
    _renderPopover: (model) ->
      console.log 'render popover', model
      # html details
      div = document.createElement 'div'
      div.className = 'notification-content'
      p = document.createElement 'p'
      p.innerHTML = model.escape 'desc'
      div.appendChild p
      unless /FINISHED|STOPPED|EXPIRED/.test model.status()
        if model.has 'expires_at'
          small = document.createElement 'small'
          expires_at = new Date(model.get 'expires_at').toLocaleString()
          small.innerHTML = "Expected expires at #{expires_at}"
          div.appendChild small
        #if model.has 'target_url' # TODO: gen url by ids
        #  type = model.get 'type'
        #  btn = document.createElement 'a'
        #  btn.href = model.get 'target_url'
        #  btn.className = 'btn btn-small'
        #  if type is 'ROUTINE'
        #    btn.innerHTML = 'View &raquo;'
        #  else
        #    btn.className += ' btn-primary icon-right-open'
        #    btn.innerHTML = 'Process'
        #  div.appendChild btn
      div
    _render: (models = @collection) ->
      models = models.fullCollection if models.fullCollection
      #console.log 'render models', models
      fragments = document.createDocumentFragment()
      col = models.filter (model) ->
        model._date = new Date(model.get('updated_at') or model.get('created_at')).getTime()
        model._before = Date.now() - model._date
        switch model.status()
          when 'EXPIRED'
            false
          when 'FINISHED'
            model._before < 86400000 # 1d
          else
            model._before < 2592000000 # 30d
      col.forEach (model) => fragments.appendChild @_renderItem model
      @el.appendChild fragments
    _levelCls:
      minor: 'muted'
      trivial: ''
      normal: 'text-info'
      major: 'text-warning'
      critical: 'text-error'
      fatal: 'text-error'
      error: 'text-error'
    _renderItem: (model = @defaultItem) ->
      #console.log 'render item', model
      li = document.createElement 'li'
      li.className = @itemClassName if @itemClassName
      a = document.createElement 'a'
      if model.id?
        a.className = "pointer #{cls}"
        a.dataset.id = model.id
        $.data a, 'model', model
        cls = @_levelCls[model.get('level')?.toLowerCase()] or ''
        span = document.createElement 'span'
        span.textContent = model.get 'name'
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
      @_super_render()
      @

  # exporet
  NotificationFrameView.NotificationListView = NotificationListView

  NotificationFrameView
