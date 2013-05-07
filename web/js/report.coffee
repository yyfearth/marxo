"use strict"

define 'report', ['console', 'lib/chart'],
({
#find
#findAll
#View
FrameView
#InnerFrameView
ModalDialogView
# Tenant
}) ->
  class ReportFrameView extends FrameView
    initialize: (options) ->
      super options
      @viewer = new ReportView parent: @
      return
    open: (name) ->
      if name
        @viewer.show true
      @

  class ReportView extends ModalDialogView
    el: '#report_viewer'
    initialize: (options) ->
      super options
      data = [
        value: 30,
        color: '#337'
      ,
        value: 50,
        color: "#337"
      ,
        value: 100,
        color: "#a11"
      ]

      options =
        segmentShowStroke : true
        segmentStrokeColor : "#fff"
        segmentStrokeWidth : 2
        animation : true
        animationSteps : 150
        animationEasing : "easeOutElastic"
        animateRotate : true
        animateScale : true

      context = document.getElementById('chart1').getContext('2d')
      chart = new Chart(context).Pie(data, options)

      data =
        labels: ["January", "February", "March", "April", "May", "June", "July"]
        datasets: [
          fillColor: "rgba(220,220,220,0.5)"
          strokeColor: "rgba(220,220,220,1)"
          pointColor: "rgba(220,220,220,1)"
          pointStrokeColor: "#fff"
          data: [19, 83, 48, 45, 32, 72, 89, ]
        ,
          fillColor: "rgba(151,187,205,0.5)"
          strokeColor: "rgba(151,187,205,1)"
          pointColor: "rgba(151,187,205,1)"
          pointStrokeColor: "#fff"
          data: [64, 73, 28, 64, 15, 12, 59, ]
        ]

      options =
        scaleOverlay: off
        scaleShowLabels: on
      #  scaleOverride: on
        scalesSteps: 10
        onAnimationComplete: ->
          console.log 'hi'

      context = document.getElementById('chart2').getContext('2d')
      chart = new Chart(context).Line(data, options)
      return

  ReportFrameView
