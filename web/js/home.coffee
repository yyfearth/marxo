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
      @listenTo @collection, 'reset', _.delay @_render, 100
      @listenTo @collection, 'add', (project) =>
        view = @views.index['_idx_' + project.cid] = new ProjectOverview model: project, parent: @
        $(@viewEl).prepend view.render().$el
        @views.unshift view
        return
      @listenTo @collection, 'remove', (project) =>
        @views['_idx_' + project.cid]?.remove()
        return
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
      index = {}
      @views = @collection.map (project) =>
        view = index['_idx_' + project.cid] = new ProjectOverview model: project, parent: @
        list.appendChild view.render().el
        view
      @views.index = index
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
      @el.innerHTML = @_tpl
      @diagram = new WorkflowDiagramView maxTimeout: 1000, el: find '.wf-diagram', @el
      @model.fetch reset: true
      @
    _find: (name) ->
      find "[name='#{name}']", @el
    _render: ->
      project = @model
      obj = project.toJSON()
      obj.counts = "(#{project.nodes?.length or 0} Nodes, #{project.links?.length or 0} Links)"
      for key, value of obj
        @_find(key)?.textContent = value
      @diagram.draw project
      return
    remove: ->
      @diagram.remove()
      @el.innerHTML = ''
      super

  HomeFrameView
