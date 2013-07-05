"use strict"

define 'calendar', ['console', 'lib/jquery-ui', 'lib/fullcalendar'], ({find, FrameView}) ->
  class CalendarFrameView extends FrameView
    initialize: (options) ->
      super options
      @sidebarListEl = find '.sidebar-list', @el
      return
    render: ->

      @$el.find('.external-event').each ->

        # create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
        # it doesn't need to have a start or end
        eventObject = title: $.trim($(@).text())
        # use the element's text as the event title

        # store the Event Object in the DOM element so we can get to it later
        $(@).data 'eventObject', eventObject

        # make the event draggable using jQuery UI
        $(@).draggable
          helper: 'clone'
          zIndex: 999
          revert: true # will cause the event to go back to its
          revertDuration: 0

      viewEl = find '#calendar_view', @el

      $(viewEl).fullCalendar
        header:
          left: 'prev,next today'
          center: 'title'
          right: 'month,agendaWeek,agendaDay'
        editable: true
        droppable: true # this allows things to be dropped onto the calendar !!!
        drop: (date, allDay) -> # this function is called when something is dropped
      
          # retrieve the dropped element's stored Event Object
          originalEventObject = $(this).data('eventObject')
      
          # we need to copy it, so that multiple events don't have a reference to the same object
          copiedEventObject = $.extend({}, originalEventObject)
      
          # assign it the date that was reported
          copiedEventObject.start = date
          copiedEventObject.allDay = allDay
      
          # render the event on the calendar
          # the last `true` argument determines if the event 'sticks' (http://arshaw.com/fullcalendar/docs/event_rendering/renderEvent/)
          $(viewEl).fullCalendar 'renderEvent', copiedEventObject, true
      
          # is the 'remove after drop' checkbox checked?
      
          # if so, remove the element from the 'Draggable Events' list
          $(this).remove()  if $('#drop-remove').is(':checked')

      # for auto size
      container = viewEl.parentNode
      r = 800 / 645
      autoResize = =>
        w = container.clientWidth
        h = container.clientHeight
        if w / h < r
          viewEl.style.width = '100%'
        else
          viewEl.style.width = Math.floor(h * r) - 20 + 'px'
        return

      $(window).resize(autoResize).resize()

      return

  CalendarFrameView
