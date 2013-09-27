'use strict'

define 'report', ['base', 'models', 'manager', 'lib/chart'],
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
}) ->

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
    initialize: (options) ->
      super options

      data = [
        value: 30,
        color: '#D41400'
      ,
        value: 50,
        color: '#00C0F7'
      ,
        value: 100,
        color: '#3B5998'
      ]

      options =
        segmentShowStroke: true
        segmentStrokeColor: '#fff'
        segmentStrokeWidth: 2
        animation: true
        animationSteps: 100
        animationEasing: 'easeOutElastic'
        animateRotate: true
        animateScale: true

      context = document.getElementById('chart1').getContext('2d')
      chart = new Chart(context).Pie(data, options)

      data =
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July']
        datasets: [
          fillColor: 'rgba(220,220,220,0.5)'
          strokeColor: '#D41400'
          pointColor: '#D41400'
          pointStrokeColor: '#fff'
          data: [19, 83, 48, 45, 32, 72, 30]
        ,
          fillColor: 'rgba(220,220,220,0.5)'
          strokeColor: '#00C0F7'
          pointColor: '#00C0F7'
          pointStrokeColor: '#fff'
          data: [64, 73, 28, 64, 15, 12, 50]
        ,
          fillColor: 'rgba(220,220,220,0.5)'
          strokeColor: '#3B5998'
          pointColor: '#3B5998'
          pointStrokeColor: '#fff'
          data: [80, 83, 51, 88, 102, 99, 100]
        ]

      options =
        scaleOverlay: off
        scaleShowLabels: on
      #  scaleOverride: on
        scalesSteps: 10
        onAnimationComplete: ->
          console.log 'hi'

      element = document.getElementById('chart2')

      if element
        context = element.getContext('2d')
        chart = new Chart(context).Line(data, options)

      @

  ReportFrameView
