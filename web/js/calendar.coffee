define 'calendar', ['console', 'lib/jquery-ui.custom.min', 'lib/fullcalendar.min'], ({find, FrameView}) ->
  class CalendarFrameView extends FrameView
    initialize: (options) ->
      super options
      @sidebarListEl = find '.sidebar-list', @el

      return
    render: ->
      $('#calendar_view', @el).fullCalendar
        header:
          left: "prev,next today"
          center: "title"
          right: "month,agendaWeek,agendaDay"
        editable: true

      viewEl = find '#calendar_view', @el
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
