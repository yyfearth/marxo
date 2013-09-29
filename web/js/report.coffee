'use strict'

define 'report', ['base', 'models', 'manager', 'lib/d3v3', 'lib/nvd3'],
({
find
#findAll
#View
FrameView
#InnerFrameView
ModalDialogView
}, {
# Tenant
Reports
Report
}, {
ManagerView
# NavFilterView
ProjectFilterView
}, d3, nv) ->

  class ReportFrameView extends FrameView
    initialize: (options) ->
      super options
      @viewer = new ReportView parent: @
      @manager = new ReportManagerView el: @el, parent: @
      @
    open: (name) ->
      if name
        @viewer.popup {}, (action, data) ->
          console.log 'report dialog', action, data
      else unless @manager.rendered
        # TODO: show real data
        @manager.render()
      @

  class ReportManagerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'name:report'
    ,
#      name: 'media'
#      label: 'Media'
#      cell: 'string'
#      editable: false
#    ,
      'project'
      'node_action'
      'status'
      'created_at'
      'updated_at'
    ,
      name: 'ended_at'
      label: 'Date Ended'
      cell: 'readonly-datetime'
      editable: false
    ,
      'actions:report'
    ]
    collection: new Reports
    initialize: (options) ->
      super options
      collection = @collection.fullCollection
      #      @mediaFilter = new NavFilterView
      #        el: '#media-filter'
      #        field: 'media'
      #        collection: collection
      console.log 'prj', find('ul.project-list', @el)
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: collection
      _remove = @remove.bind @
      @on
      #edit: @edit.bind @
        remove: _remove
        remove_selected: _remove
      @
#    edit: (model) ->
#      console.log 'edit', model
#      @
    remove: (models) ->
      models = [models] unless Array.isArray models
      console.log 'remove', models
      #  if confirm 'Make sure these selected workflows is not in use!\nDo you realy want to remove selected workflows?'
      #    # TODO: check usage, if used cannot remove directly
      #    model?.destroy() for model in models
      #    @reload() if models.length >= @pageSize / 2
      #  #console.log 'delete', model, @
      @
    reload: ->
      super
      #@mediaFilter.clear()
      @projectFilter.clear()
    render: ->
      super
      #@mediaFilter.render()
      @projectFilter.render()
      @
  #view: (models) ->
  #  models = [models] unless Array.isArray models
  #  console.log 'view', models

  class ReportView extends ModalDialogView
    el: '#report_viewer'
    goBackOnHidden: 'report'
    popup: (model, callback) ->
      @render() unless @rendered
      super model, callback
    render: ->
      setTimeout => # after shown (test only)

        vote = [
          name: 'Submission 1'
          value: 30
        ,
          name: 'Submission 2'
          value: 50
        ,
          name: 'Submission 3'
          value: 100
        ]

        @pieChart '#pie', vote, 'percent'

        @barChart '#bar', vote

      , 550
      @
    pieChart: (el, data, labelType) ->
      nv.addGraph ->
        width = 500
        height = 500
        chart = nv.models.pieChart()
        .x((d) -> d.name).y((d) -> d.value)
        .color(d3.scale.category10().range())
        .width(width).height(height).labelType(labelType)
        d3.select("#{el} svg").datum(data).transition().duration(1200)
        .attr("width", width).attr("height", height).call chart
        nv.utils.windowResize chart.update
        chart
      @
    barChart: (el, data) ->
      nv.addGraph ->
        chart = nv.models.discreteBarChart()
        .x((d) -> d.name).y((d) -> d.value)
        .staggerLabels(true).showValues(true).transitionDuration(250)
        d3.select("#{el} svg").datum([
          values: data
        ]).call chart
        nv.utils.windowResize chart.update
        chart
      @

  #
  #      options =
  #        segmentShowStroke: true
  #        segmentStrokeColor: '#fff'
  #        segmentStrokeWidth: 2
  #        animation: true
  #        animationSteps: 100
  #        animationEasing: 'easeOutElastic'
  #        animateRotate: true
  #        animateScale: true
  #
  #      data =
  #        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July']
  #        datasets: [
  #          fillColor: 'rgba(220,220,220,0.5)'
  #          strokeColor: '#D41400'
  #          pointColor: '#D41400'
  #          pointStrokeColor: '#fff'
  #          data: [19, 83, 48, 45, 32, 72, 30]
  #        ,
  #          fillColor: 'rgba(220,220,220,0.5)'
  #          strokeColor: '#00C0F7'
  #          pointColor: '#00C0F7'
  #          pointStrokeColor: '#fff'
  #          data: [64, 73, 28, 64, 15, 12, 50]
  #        ,
  #          fillColor: 'rgba(220,220,220,0.5)'
  #          strokeColor: '#3B5998'
  #          pointColor: '#3B5998'
  #          pointStrokeColor: '#fff'
  #          data: [80, 83, 51, 88, 102, 99, 100]
  #        ]
  #
  #      options =
  #        scaleOverlay: off
  #        scaleShowLabels: on
  #      #  scaleOverride: on
  #        scalesSteps: 10
  #        onAnimationComplete: ->
  #          console.log 'hi'
  #
  #      element = document.getElementById('chart2')
  #
  #      if element
  #        context = element.getContext('2d')
  #        chart = new Chart(context).Line(data, options)

  #      @

  ReportFrameView
