"use strict"

define 'project', ['base', 'manager', 'models'],
({
find
#findAll
#View
FrameView
InnerFrameView
NavListView
FormDialogView
}, {
ManagerView
WorkflowFilterView
}, {
Workflow
Workflows
Project
Projects
}) ->

  class ProjectFrameView extends FrameView
    initialize: (options) ->
      super options
      @editor = new ProjectEditorView el: '#project_editor', parent: @
      @viewer = new ProjectViewerView el: '#project_viewer', parent: @
      @manager = new ProjectManagemerView el: '#project_manager', parent: @
      @listenTo @manager, 'create', (id) => @editor.create id
      @
    open: (name, sub) ->
      switch name
        when 'new'
          @editor.create sub
        when 'mgr'
          @switchTo @manager
        else
          throw new Error 'open project with a name or id is needed' unless name
          @switchTo @viewer
          @viewer.load name
          @viewer.popup sub if sub
      @

  # Editor

  class ProjectEditorView extends FormDialogView
    goBackOnHidden: 'project/mgr'
    collection: Workflows.workflows
    initialize: (options) ->
      super options
      @
    create: (wf) ->
      wf = wf?.id or wf
      wf = null unless typeof wf is 'string'
      @popup new Project(workflow_id: wf), (action, data) =>
        console.log 'wf created', action, data
      @
    popup: (model, callback) ->
      data = model.toJSON()
      @model = model
      @render() unless @rendered
      super data, callback
      select = @form.workflow_id
      select.disabled = true
      @collection.load (ignored, ret) =>
        select.disabled = false
        @_renderSelect() if 'loaded' is ret
        @fill data
      @
    render: ->
      select = @form.workflow_id
      select.disabled = true
      @collection.load =>
        select.disabled = false
        @_renderSelect()
      @
    _renderSelect: ->
      select = @form.workflow_id
      wfs = @collection.fullCollection
      if wfs.length
        owned = document.createElement 'optgroup'
        owned.label = 'Owned Workflows'
        shared = document.createElement 'optgroup'
        shared.label = 'Shared Workflows'
        wfs.forEach (wf) ->
          # TODO: the id should be current logined
          op = document.createElement 'option'
          op.value = wf.id
          op.textContent = wf.get 'name'
          unless wf.has 'tanent_id'
            shared.appendChild op
          else
            owned.appendChild op
        select.innerHTML = ''
        op = document.createElement 'option'
        op.value = ''
        op.textContent = '(Please Select)'
        select.appendChild op
        select.appendChild owned if owned.childElementCount
        select.appendChild shared if shared.childElementCount
      return
  #save: ->
  #  @callback 'save'
  #  @hide true
  #  @

  # Viewer

  class ProjectViewerView extends InnerFrameView
    initialize: (options) ->
      super options
    load: (name) ->
      console.log 'load project', name
      @
    popup: (opt = {}) ->
      {link, node, action} = opt
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      console.log 'popup node/link viewer', {link, node, action}
      @

  # Manager

  class WorkflowListView extends NavListView
    auto: false
    urlRoot: 'worklfow'
    headerTitle: 'Workflows'
    itemClassName: 'workflow-list-item'
    collection: Workflows.workflows
    defaultItem: null
    events:
      'click': (e) ->
        el = e.target
        if el.tagName is 'A' and el.dataset.id
          e.preventDefault()
          @trigger 'select', el.dataset.id, $(el).data 'model'
          false
    render: ->
      @_clear()
      @_render()
      @

  class WorkflowCell extends Backgrid.UriCell
    collection: Workflows.workflows
    initialize: (options) ->
      super options
      @urlRoot = @column.get('urlRoot') or @urlRoot
      @urlRoot += '/' if @urlRoot and @urlRoot[-1..] isnt '/'
    render: ->
      @$el.empty()
      id = @model.get('workflow_id')
      _render = (wf) =>
        name = _.escape wf.get 'name'
        @$el.addClass('workflow-link-cell').append $('<a>',
          tabIndex: -1
          href: '#workflow/' + id
        ).attr('title', name).text name
        @delegateEvents()
      _callback = (wfs) ->
        wf = wfs.get id
        if wf
          _render wf
        else
          wf = new Workflow id: id
          wf.fetch success: _render
      unless @collection.length
        @collection.fetch success: (wfs) ->
          wfs._last_load = Date.now()
          _callback wfs
      else
        _callback @collection
      @

  class ProjectActionCell extends Backgrid.ActionsCell
    render: ->
      # TODO: show buttons depend on status
      super

  class ProjectManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'name:project'
      'desc'
    ,
      name: 'workflow_id'
      label: 'Workflow'
      editable: false
      cell: WorkflowCell
    ,
      'created_at'
      'updated_at'
      'status'
    ,
      name: 'project'
      label: ''
      editable: false
      sortable: false
      cell: ProjectActionCell
    ]
    collection: new Projects
    initialize: (options) ->
      super options
      @list = new WorkflowListView el: find 'ul.workflow-list', @el
      @listenTo @list, 'select', (id, model) ->
        console.log 'create project from workflow', id, model
        @trigger 'create', id, model
      # TODO: create project from workflow
      @
    render: ->
      @list.fetch()
      super

  ProjectFrameView
