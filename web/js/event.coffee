"use strict"

define 'event', ['base', 'manager', 'models', 'lib/jquery-ui', 'lib/fullcalendar'], ({
find
View
FrameView
InnerFrameView
}, {
ManagerView
ProjectFilterView
}, {
Events
}) ->

  class EventFrameView extends FrameView
    collection: new Events
    initialize: (options) ->
      super options
      @calendar = new EventCalendarView el: '#event_calendar', parent: @
      @manager = new EventManagemerView el: '#event_manager', parent: @
      # TODO: init @editor
      @
    open: (name, sub) ->
      switch name
        when 'calendar'
          @switchTo @calendar
        when 'mgr'
          @switchTo @manager
        else
          console.log 'open', name, sub
      # TODO: open @editor
      # throw 'open project with a name or id is needed' unless name
      # @switchTo @editor
      # @viewer.load name
      # @viewer.popup sub if sub
      @

  # Event Calendar

  class EventCalendarView extends InnerFrameView
    collection: EventFrameView::collection
    initialize: (options) ->
      super options
      #@sidebarListEl = find '.sidebar-list', @el
      @calView = new CalendarView parent: @, el: find '#calendar_view', @el
      @
    render: ->
      @calView.render()

      @$el.find('.external-event').each ->

        # create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
        # it doesn't need to have a start or end
        # use the element's text as the event title

        # store the Event Object in the DOM element so we can get to it later
        $(@).data 'event', title: $.trim($(@).text())

        # make the event draggable using jQuery UI
        $(@).draggable
          helper: 'clone'
          zIndex: 999
          revert: true # will cause the event to go back to its
          revertDuration: 0

      @

  class CalendarView extends View
    cfg:
      header:
        left: 'prev,next today'
        center: 'title'
        right: 'month,agendaWeek,agendaDay'
      handleWindowResize: false # handle manually
      editable: true
      droppable: true # this allows things to be dropped onto the calendar !!!
    collection: EventFrameView::collection
    #initialize: (options) ->
    #  super options
    #  @
    remove: ->
      $(window).off 'resize', @_resize
      @
    render: ->
      fullCalendar = @fullCalendar ?= @$el.fullCalendar.bind @$el

      @$el.empty()

      @cfg.drop ?= (date, allDay, e) =>
        # this function is called when something is dropped
        $thumb = $(e.target)
        @addEvent $.extend({}, $thumb.data('event'), {date, allDay})
        $thumb.remove()
      fullCalendar @cfg

      unless @rendered
        # auto delayed resize
        @_resize = => @delayedTrigger 'resize', 150
        @on 'resize', =>
          console.log 'resized cal'
          h = @$el.parents('.inner-frame').innerHeight()
          fullCalendar 'option', 'height', h
        $(window).resize(@_resize).resize()

        # mouse scroll to nav monthes
        @$el.on 'mousewheel DOMMouseScroll', (e) =>
          e.preventDefault()
          e = e.originalEvent
          fullCalendar if e.wheelDelta > 0 or e.detail < 0 then 'prev' else 'next'
          false
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
      'checkbox'
      'id'
      'title:project'
      'desc'
      'created_at'
      'updated_at'
      'status'
    ]
    collection: EventFrameView::collection
    initialize: (options) ->
      super options
      collection = @collection.fullCollection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        field: 'project.id'
        collection: collection
      _skip = @skip.bind @
      @on
        skip: _skip
        remove_selected: _skip
      @
    skip: (models) ->
      console.log 'skip', models
      @
    reload: ->
      super
      @projectFilter.clear()
    render: ->
      super
      @projectFilter.render()
      @

  EventFrameView