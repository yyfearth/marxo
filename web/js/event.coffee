"use strict"

define 'event', ['base', 'manager', 'models', 'lib/jquery-ui', 'lib/fullcalendar'], ({
find
View
FrameView
InnerFrameView
FormDialogView
}, {
ManagerView
ProjectFilterView
}, {
Events
Event
}) ->

  class EventFrameView extends FrameView
    collection: new Events
    initialize: (options) ->
      super options
      @calendar = new EventCalendarView
        el: '#event_calendar'
        parent: @
        collection: @collection.fullCollection
      @manager = new EventManagemerView
        el: '#event_manager'
        parent: @
        collection: @collection
      @editor = new EventEditorView el: '#event_editor', parent: @
      @collection.fetch reset: true
      @
    open: (name, sub) ->
      switch name
        when 'calendar'
          @switchTo @calendar
          @calendar.goto sub
        when 'mgr'
          @switchTo @manager
        else
          throw new Error 'open project with a name or id is needed' unless name
          @load name
      @
    load: (id) ->
      _load = (event) =>
        @editor.popup event.toJSON(), (action, data) =>
          event.save data if action is 'save'

      if id instanceof Event
        _load id
      else if event = @collection.get id
        _load event
      else
        new Event(id: id).fetch
          success: _load
          error: ->
            err = "Cannot find event with id #{id} or net work problem"
            console.error err
            alert err

  # Util

  DurationConvertor = do ->
    AUTO_SHORT_MAX = 30
    _regex = /(?:(\d+)w(?:eek)?s?)?(?:(\d+)d(?:ay)?s?)?(?:(\d+)h(?:our)?s?)?(?:(\d+)m(?:in(?:use?)?s?)?)?(?:(\d+)s(?:ec(?:ond)?)?s?)?(?:(\d+)ms)?/i
    _delays = [604800000, 86400000, 3600000, 60000, 1000, 1]
    _units = [
      # set week to null if only use days
      ['week', 's'],
      ['day', 's'],
      ['hour', 's'],
      ['minus', 'es'],
      ['second', 's'],
      'ms'
    ]
    _stringify = (delay, short) ->
      str = []
      for ms, i in _delays
        s = _units[i]
        continue unless s
        next = delay % ms
        d = (delay - next) / ms
        delay = next
        if d
          unless Array.isArray s # is ms
            str.push if short then "#{d}#{s}" else "#{d} #{s}"
          else if short
            str.push "#{d}#{s[0].charAt 0}"
          else
            s = if d is 1 then s[0] else s[0] + s[1]
            str.push "#{d} #{s}"
      str.join ' '

    parse: (str) ->
      str = str.trim().replace /\s+|\band\b/ig, ''
      unless str
        0
      else if /^\d+$/.test str
        # pure number in ms
        parseInt str
      else
        delay = 0
        match = str.match(_regex).slice(1)
        for n, i in match
          delay += n * _delays[i] if n
        delay
    stringify: (delay, short) ->
      if delay >= 0
        str = _stringify delay, short
        if not short? and str.length > AUTO_SHORT_MAX
          _stringify delay, true
        else
          str
      else
        console.error "delay should be number >= 0 but it is #{delay}", delay if delay
        ''

  # Event Editor

  class EventEditorView extends FormDialogView
    goBackOnHidden: 'event/mgr'
    initialize: (options) ->
      super options
      @$info = $ find '.info', @form
      @$form = $ @form
      @btnView = find '.btn-view', @el
      @_dateToLocale = (date) -> if date then new Date(date).toLocaleString() else ''
      if @form.starts.type is 'text'
        # not support datetime type
        @_dateToString = @_dateToLocale
      else
        @_dateToString = (date) -> if date then new Date(date).toISOString() else ''
      @_changed = @_changed.bind @
    _changed: (e) ->
      form = @form
      _toTS = (date) -> unless date then null else new Date(date).getTime()
      starts = _toTS form.starts.value.trim()
      ends = _toTS form.ends.value.trim()
      duration = DurationConvertor.parse form.duration.value.trim()
      if starts and ends and duration and duration isnt ends - starts
        # current changed
        switch e?.currentTarget?.name
          when 'starts', 'duration' # if starts or duration changed, keep duration
            ends = null
          when 'ends' # if ends changed, change duration
            duration = null
          else
            console.warn 'starts, ends and duration are not matched', starts, ends, duration
            starts = ends = duration = null
            invalid = 'Start Date, End Date and Duration are not matched'
      if starts
        form.starts = @_dateToString starts
        if ends
          unless ends > starts
            invalid = 'Start Date must before End Date'
            console.warn 'starts <= ends', starts, ends
          else unless duration # starts and ends but duration
            duration = ends - starts
            form.duration.value = DurationConvertor.stringify duration
        else if duration # starts and duration but ends
          ends = new Date starts + duration
          form.ends.value = @_dateToString ends
      else if ends and duration # ends and duration but starts
        starts = new Date ends - duration
        form.starts.value = @_dateToString starts

      if invalid
        msg = invalid
        cls = 'error'
      else
        console.log 'starts, ends, duration:', starts, ends, duration
        cls = ''
        msg = []
        if starts
          msg.push "It will be started at #{@_dateToLocale starts}."
          msg.push '<small>A notication will be sent if associated action has not been executed yet.</small>'
        else
          msg.push 'It will be started automatically when associated action been executed.'
        if ends
          msg.push "It will be ended at #{@_dateToLocale ends}."
        else
          cls = 'warning'
          msg.push 'It will be ended only after trigger "skip" manually.'
        msg.push "Duration between starts and ends is #{form.duration.value}." if duration
        msg.push '<small>4 Notifications will be sent before and after event starts and ends.</small>'
        msg = msg.join '<br/>'

      @$info.html(msg).parents('.control-group')
      .removeClass('success error').addClass cls
      @

    fill: (data) ->
      if data.starts
        data.starts = new Date data.starts
        starts = data.starts.getTime()
        if data.duration and not data.ends
          data.ends = new Date starts + data.duration if data.duration
        else if data.ends
          data.ends = new Date data.ends
          data.duration ?= data.ends.getTime() - starts
      data.duration = unless data.duration then '' else DurationConvertor.stringify data.duration
      console.log JSON.stringify data
      data.starts = @_dateToString data.starts
      data.ends = @_dateToString data.ends
      console.log 'fill event data', data
      super data
      @_changed()
      @
    read: ->
      data = super
      data.starts = unless data.starts?.length then null else new Date data.starts
      data.ends = if data.ends?.length then null else new Date data.ends
      data.duration = DurationConvertor.parse data.duration
      data.duration = null unless data.duration
      data
    reset: ->
      @$info.empty()
      @btnView.href = ''
      super
    popup: (data, callback) ->
      super data, callback
      q = ['change', '[name=starts],[name=ends],[name=duration]', @_changed]
      @btnView.href = "#event/calendar/#{data.id}"
      @$form.off q...
      @fill data
      @$form.on q...
      @
    save: ->
      @data = @read()
      @callback 'save'
      @hide true
      @

  # Event Calendar

  class EventCalendarView extends InnerFrameView
    collection: EventFrameView::collection
    initialize: (options) ->
      super options
      #@sidebarListEl = find '.sidebar-list', @el
      @$sidebar = $ find '.sidebar-list', @el
      @calView = new FullCalendarView parent: @, el: find '#calendar_view', @el
      @listenTo @collection, 'reset add remove change', =>
        @update() if @$el.is ':visible'
      @listenTo @calView, 'modify', @_modify.bind @
      @update = _.throttle @update.bind(@), 500
      #@on 'activate', update # use show instead
      @
    _modify: (event) -> # (event, revertFunc)
      console.log 'modify event', event.start, event.end, event
      if event.model and event.start
        unless event.end
          end = new Date event.start
          # set to next midnight
          end.setHours 24, 0, 0, 0
          event.end = end
          console.log 'fix end', end
        # TODO: deal with allDay
        # TODO: validation and revert
        # TODO: start date limits
        event.model.save
          starts: event.start
          ends: event.end
          duration: event.end.getTime() - event.start.getTime()
      return
    update: (event = @_curEvent) ->
      id = event?.id or event
      console.log 'cur', id
      cal = @calView
      col = @collection
      cal.render() unless cal.rendered
      events = []
      unsched = []
      col.forEach (evt) ->
        _evt =
          id: evt.id
          url: "#event/#{evt.id}"
          title: evt.get('name') or '(No Name)'
          model: evt
        _evt.start = new Date evt.get 'starts' if evt.has 'starts'
        _evt.end = new Date evt.get 'ends' if evt.has 'ends'
        _evt.allDay = _evt.start? and not _evt.end? # if no ends, by default it is allday
        switch status = evt.get('status').toUpperCase()
          when 'FINISHED'
            _evt.color = '#ccc'
            _evt.editable = false
          when 'IDLE'
            # default color
            _evt.editable = true
            _evt.color = '#006dcc' if _evt.allDay
          when 'STARTED'
            _evt.color = if _evt.end? then '#468847' else '#faa732'
            _evt.startEditable = false
            _evt.durationEditable = true
          when 'ERROR'
            _evt.color = '#da4f49'
            _evt.editable = false
          else
            # default color
            _evt.editable = false
            console.warn 'unsupported event status', status
        if id and id is evt.id # current active event
          event = evt
          _evt.className = 'active'
        if _evt.start?
          events.push _evt
        else
          unsched.push _evt
        return

      cal.setEvents events: events

      # TODO: project filter

      #console.log 'unsched events', unsched
      $frag = $ document.createDocumentFragment()
      $frag.append '<li class="nav-header">Unscheduled Events</li>'
      for _evt in unsched
        $frag.append $('<li>').append $('<a>', class: 'external-event', text: _evt.title, href: "#event/#{_evt.id}")
        .data('event', _evt).draggable
          helper: 'clone'
          zIndex: 999
          revert: true # will cause the event to go back to its
          revertDuration: 0
      @$sidebar.empty().append $frag

      if event instanceof Event and event.has 'starts'
        cal.goto new Date event.get 'starts'
      else
        @_curEvent = null
      @
    goto: (event) ->
      # TODO: not efficent re-render all events for just select
      @update @_curEvent = event
      @
    render: ->
      @calView.render()
      super

  class FullCalendarView extends View
    cfg:
      header:
        left: 'prev,next today'
        center: 'title'
        right: 'month,agendaWeek,agendaDay'
      handleWindowResize: false # handle manually
      editable: true
      droppable: true # this allows things to be dropped onto the calendar !!!
    initialize: (options) ->
      super options
      @collection = options.collection
      $el = @$el
      fullCalendar = @fullCalendar ?= $el.fullCalendar.bind $el

      @cfg.drop = (date, allDay, e) =>
        # this function is called when something is dropped
        $thumb = $(e.target)
        event = $thumb.data 'event'
        console.log 'drop', date, allDay, event
        event.start = date
        unless event.end?
          if event.model.has 'duration'
            duration = event.model.get 'duration'
            event.end = new Date date.getTime() + duration if duration > 0
          else
            event.allDay = allDay
        @addEvent event
        $thumb.remove()
        @trigger 'modify', event
        return

      @cfg.eventResize = (event, dayDelta, minuteDelta, revertFunc) =>
        @trigger 'modify', event, revertFunc
      @cfg.eventDrop = (event, dayDelta, minuteDelta, allDay, revertFunc) =>
        @trigger 'modify', event, revertFunc

      # auto delayed resize
      $(window).resize @_resize = _.debounce ->
        fullCalendar 'option', 'height', $el.parents('.inner-frame').innerHeight()
      , 150

      # mouse scroll to nav monthes
      parent = @el.parentNode
      $el.on 'mousewheel DOMMouseScroll', _.throttle (e) ->
        # always next/prev when shift or ctrl pressed
        # never next/prev when alt or meta pressed
        force = e.shiftKey or e.ctrlKey
        if force or not (e.altKey or e.metaKey) and fullCalendar('getView').name is 'month'
          prev = e.originalEvent.wheelDelta > 0 or e.originalEvent.detail < 0
          if force or prev and parent.scrollTop is 0 or
          not prev and parent.scrollTop >= parent.scrollHeight - parent.offsetHeight
            e.preventDefault()
            fullCalendar if prev then 'prev' else 'next'
            parent.scrollTop = 0
            false
        return
      , 350
      @
    _view_map:
      month: 'month', week: 'agendaWeek', day: 'agendaDay'
      agendaWeek: 'agendaWeek', agendaDay: 'agendaDay'
    goto: (date, view) ->
      if view and @_view_map.hasOwnProperty view
        @fullCalendar 'changeView', @_view_map[view]
      @fullCalendar 'gotoDate', date.getFullYear(), date.getMonth(), date.getDate()
      @
    remove: ->
      $(window).off 'resize', @_resize
      @fullCalendar 'destory'
      @
    render: ->
      @$el.empty()
      @fullCalendar @cfg
      @_resize()
      @

    setEvents: (events) ->
      # clear
      @fullCalendar 'removeEvents'
      console.log 'set events', events
      setTimeout =>
        @fullCalendar 'addEventSource', events
      , 100
      @

    addEvent: (event) -> # this function is called when something is dropped
      # render the event on the calendar
      # the last `true` argument determines if the event 'sticks'
      # (http://arshaw.com/fullcalendar/docs/event_rendering/renderEvent/)
      @fullCalendar 'renderEvent', event, true
      @

  # Event Manager

  class EventManagemerView extends ManagerView
    columns: [
      # 'checkbox'
      'id'
      'name:event'
      'workflow'
      'node_action'
      'type'
      'status'
      'actions:event'
    ]
    initialize: (options) ->
      super options
      @collection = options.collection
      collection = @collection.fullCollection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: collection
      @on 'skip remove_selected', @skip.bind @
      @
    skip: (models) ->
      console.log 'skip', models
      # TODO: support skip event
      @
    reload: ->
      super
      @projectFilter.clear()
    render: ->
      super
      @projectFilter.render()
      @

  EventFrameView
