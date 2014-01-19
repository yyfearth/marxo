"use strict"

define 'home', ['base', 'models', 'notification', 'diagram'],
({
find
#findAll
tpl
fill
View
FrameView
NavListView
STATUS_CLS
}, {
Project
Projects
}, {
NotificationListView
}, WorkflowDiagramView) ->

  class HomeFrameView extends FrameView
    collection: Projects.projects
    initialize: (options) ->
      super options
      list = @notificationList = new NotificationListView el: find('.sidebar-list', @el), parent: @
      @viewEl = find '#home_view', @el
      @_render = _.throttle @_render.bind(@), 100, trailing: false
      @listenTo @collection, 'reset', @_render
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
      @on 'deactivate', ->
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
      @collection.load @_render
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
      super
    _find: (name) ->
      find "[name='#{name}']", @el
    _render: ->
      project = @model
      obj = project.toJSON()
      obj.counts = "(#{project.nodes?.length or 0} Nodes, #{project.links?.length or 0} Links)"
      for key, value of obj
        @_find(key)?.textContent = value
      find('a.btn-view', @el)?.href = '#project/' + project.id
      cls = STATUS_CLS[project.status lowercase: true]
      find('.label[name=status]', @el)?.classList.add cls if cls
      # cur nodes
      el = @_find 'cur-nodes'
      curNodeIds = _.uniq project.get('current_node_ids') or []
      unless len = curNodeIds.length
        el.innerHTML = '<li class="nav-header">No Current Node</li>'
      else
        el.innerHTML = "<li class=\"nav-header\">Current Nodes (#{len})</li>"
        frag = document.createDocumentFragment()
        for nodeId in curNodeIds
          node = project.nodes.get nodeId
          name = node.get('name') or '(No Name)'
          status = node.status lowercase: true
          li = document.createElement 'li'
          a = document.createElement 'a'
          a.href = "#project/#{project.id}/node/#{node.id}"
          a.className = 'status-' + status
          a.textContent = name
          span = document.createElement 'span'
          span.className = 'label pull-right ' + STATUS_CLS[status] or ''
          span.textContent = status.toUpperCase()
          a.appendChild span
          li.appendChild a
          frag.appendChild li
        el.appendChild frag
      @diagram.draw project
      return
    remove: ->
      @diagram.remove()
      @el.innerHTML = ''
      super

  HomeFrameView
