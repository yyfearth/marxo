"use strict"

define 'project', ['base', 'manager', 'models'],
({
find
#findAll
#View
FrameView
InnerFrameView
NavListView
#ModalDialogView
}, {
ManagerView
WorkflowFilterView
}, {
Workflows
Projects
}) ->
  class ProjectFrameView extends FrameView
    initialize: (options) ->
      super options
      @creator = new ProjectCreatorView el: '#project_creator', parent: @
      @viewer = new ProjectViewerView el: '#project_viewer', parent: @
      @manager = new ProjectManagemerView el: '#project_manager', parent: @
      @
    open: (name, sub) ->
      switch name
        when 'new'
          @switchTo @creator
        when 'mgr'
          @switchTo @manager
        else
          throw new Error 'open project with a name or id is needed' unless name
          @switchTo @viewer
          @viewer.load name
          @viewer.popup sub if sub
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
        title = _.escape wf.get 'title'
        @$el.addClass('workflow-link-cell').append $('<a>',
          tabIndex: -1
          href: '#workflow/' + id
        ).attr({title}).text title
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

  class ProjectManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'title:project'
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
        # TODO: create project from workflow
      @
    render: ->
      @list.fetch()
      super

  class ProjectCreatorView extends InnerFrameView
    #    initialize: (options) ->
    #      super options

  class ProjectViewerView extends InnerFrameView
    #    initialize: (options) ->
    #      super options
    load: (name) ->
      console.log 'load project', name
      @
    popup: ({link, node, action} = {}) ->
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      console.log 'popup node/link viewer', {link, node, action}
      @

  ProjectFrameView
