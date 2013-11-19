"use strict"

define 'home', ['base', 'models', 'notification'],
({
find
#findAll
tpl
fill
View
FrameView
NavListView
WorkflowDiagramView
}, {
Project
Projects
}, {
NotificationListView
}) ->

  class HomeFrameView extends FrameView
    collection: Projects.projects
    initialize: (options) ->
      super options
      list = @notificationList = new NotificationListView el: find('.sidebar-list', @el), parent: @
      @_render = @_render.bind @
      @viewEl = find '#home_view', @el
      @listenTo @collection, 'reset add remove', _.delay @_render, 100
      _auto_update = list.autoUpdate.bind list
      _auto_update true
      @on 'activate', =>
        @render()
        _auto_update true
      @on 'deactivate', =>
        _auto_update false
      return
    _render: ->
      @views?.forEach (view) -> view.remove()
      list = document.createDocumentFragment()
      @views = @collection.map (project) =>
        view = new ProjectOverview model: project, parent: @
        view.render()
        list.appendChild view.el
      @viewEl.innerHTML = ''
      @viewEl.appendChild list
      return
    render: ->
      @collection.load (ignored, resp) =>
        @_render() if @rendered and resp is 'loaded'
      @notificationList.fetch()
      super

  class ProjectOverview extends View
    _tpl: tpl('#project_overview_tpl')
    initialize: (options) ->
      @model = options.model
      @listenTo @model, 'loaded', @_render.bind @
      @listenTo @model, 'destroy', @remove.bind @
      super options
    render: ->
      throw new Error 'nothing to load' unless @model
      @el.id = 'overview_' + @model.cid
      @model.fetch reset: true
      @
    _render: ->
      _tpl = @_tpl
      project = @model
      obj = project.toJSON()
      obj.counts = "(#{project.nodes?.length or 0} Nodes, #{project.links?.length or 0} Links)"
      @el.innerHTML = fill _tpl, obj
      @diagram = new WorkflowDiagramView maxTimeout: 1000, el: find '.wf-diagram', @el
      @diagram.draw project
      return
    remove: ->
      @stopListening @model
      @diagram.remove()
      @el.innerHTML = ''
      super

  HomeFrameView
