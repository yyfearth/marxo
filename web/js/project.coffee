"use strict"

define 'project', ['base', 'manager', 'models', 'actions'],
({
find
#findAll
#View
FrameView
InnerFrameView
NavListView
FormView
FormDialogView
}, {
ManagerView
WorkflowFilterView
}, {
Workflow
Workflows
Project
Projects
}, ActionsMixin) ->

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
          Projects.find projectId: name, callback: ({project}) =>
            throw new Error "project with id #{name} cannot found" unless project
            @editor.edit project, sub
      # @switchTo @viewer
      # @viewer.load project
      # @viewer.focus sub
      @

  # Editor

  class ProjectEditorView extends FormDialogView
    goBackOnHidden: 'project/mgr'
    workflows: Workflows.workflows
    events:
      'change select[name=workflow_id]': (e) ->
        wf = e.currentTarget.value
        cur = @model.get 'workflow_id'
        @$wfbtns.hide()
        @sidebar.classList.remove 'active'
        @btnSave.disabled = true
        if wf and cur
          if wf is cur
            @sidebar.classList.add 'active'
            @btnSave.disabled = false
          else
            @$wfbtns.show()
        else if cur and not wf
          @$wfbtns.not(@$btnSelect).show()
        else if wf and not cur
          @$btnSelect.show()
        return
      'click .btn-select': '_selectWorkflow'
      'click .btn-revert': ->
        @form.workflow_id.value = @model.get('workflow_id') or ''
        $(@form.workflow_id).change()
        return
      'click li.sidebar-item > a, a.linked-item': (e) ->
        e.preventDefault()
        @navTo $(e.currentTarget).data 'model'
        false
    initialize: (options) ->
      super options
      @sidebar = find '.sidebar', @el
      @$btnSelect = $ find '.btn-select', @form
      @$wfbtns = @$btnSelect.add find '.btn-revert', @form
      @$wfPreview = $ find '#wf_preview', @el
      @$nodeLinkSection = $ find 'section.node-link', @el
      @$projectForm = $ @form
      @$actions = $ find '.node-actions', @el
      @dataEditor = new NodeLinkDataEditor el: @$nodeLinkSection[0], actionEl: @$actions[0]
      @
    create: (wf) ->
      wf = wf?.id or wf
      wf = null unless typeof wf is 'string'
      @popup new Project(workflow_id: wf), (action, data) =>
        console.log 'wf created', action, data
      @
    edit: (project, opt = {}) ->
      {link, node, action} = opt
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      console.log 'popup node/link editor', {link, node, action}
      @popup project, (action, data) =>
        console.log 'project saved', action, data
      @
    popup: (model, callback) ->
      data = model.toJSON()
      @model = model
      @render() unless @rendered
      super data, callback
      select = @form.workflow_id
      select.disabled = true
      @workflows.load (ignored, ret) =>
        @_renderSelect() if 'loaded' is ret
        @fill data
        select.disabled = not model.isNew() or model.has('node_ids') or model.nodes?.length
        if select.disabled
          @_renderProject model
        else
          @_selectWorkflow()
        # auto foucs
        setTimeout =>
          if select.value
            @form.name.focus()
          else
            select.focus()
        , 550
      @
    navTo: (model) ->
      type = model?._name or 'project'
      if @_cur_type isnt type
        @$projectForm.hide()
        @$actions.hide()
        $section = @$nodeLinkSection.hide()
        $nodeOptions = $section.find('.node-options').hide()
        $linkOptions = $section.find('.link-options').hide()
        if type is 'project'
          @$projectForm.show()
        else
          $section.show()
          if type is 'node'
            @$wfPreview.show()
            @$actions.show()
            $nodeOptions.show()
          else if type is 'link'
            $linkOptions.show()
          else
            throw new Error "unknown type to nav #{type}"
        @_cur_type = type
      else if @_cur_model is model
        return @
      @_cur_model = model
      console.log 'nav to', type, model
      @dataEditor.fill model if model
      # TODO: select nothing in flow
      # TODO: select node with id in flow
      # TODO: select link with id in flow
      @
    _selectWorkflow: ->
      wf = @form.workflow_id.value
      return unless wf
      wf = @workflows.get wf unless wf instanceof Workflow
      project = @model
      if project.nodes?.length or project.has 'node_ids'
        # return @ unless confirm 'Change workflow will discard existing settings!\n\nAre you sure to change?'
        # clear nodes and links
        project.set node_ids: null, nodes: null, link_ids: null, links: null
        project._warp()
      console.log 'selected wf for project', wf.name
      project.copy wf, @_renderProject.bind @
      return
    _renderProject: (project) ->
      @sidebar.classList.add 'active'
      @$wfbtns.hide()
      @btnSave.disabled = false
      # update sidebar
      $sidebar = $ @sidebar
      $sidebar.find('li.node-item, li.link-item').remove()
      nodes = document.createDocumentFragment()
      _renderSidebarItem = @_renderSidebarItem.bind @
      project.nodes.forEach (node) ->
        nodes.appendChild _renderSidebarItem node
      links = document.createDocumentFragment()
      project.links.forEach (link) ->
        links.appendChild _renderSidebarItem link
      $sidebar.find('.node-header').after nodes
      $sidebar.find('.link-header').after links
      return
    _renderSidebarItem: (model) ->
      el = document.createElement 'li'
      el.className = "sidebar-item #{model._name}-item"
      a = document.createElement 'a'
      name = a.textContent = model.name()
      a.dataset.id = model.id
      $a = $(a).data 'model', model
      $a.tooltip title: name, placement: 'right', container: @el if name.length > 15
      el.appendChild a
      el
    render: ->
      @workflows.load => @_renderSelect()
      super
    reset: ->
      @$wfbtns.hide()
      $(@sidebar).find('li.node-item, li.link-item').remove()
      @navTo null
      super
    _renderSelect: ->
      select = @form.workflow_id
      wfs = @workflows.fullCollection
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
    save: ->
      @callback 'save'
      @hide true
      @

  class NodeLinkDataEditor extends FormView
    @acts_as ActionsMixin
    initialize: (options) ->
      super options
      options.projectMode = true
      @initActions options
      @nameEl = find '.node-link-name', @el
      @keyEl = find '.node-link-key', @el
      @$inLinks = $ find '[name=in_links]', @form
      @$outLinks = $ find '[name=out_links]', @form
      @$prevNode = $ find '[name=prev_node]', @form
      @$nextNode = $ find '[name=next_node]', @form
      @$linkedNodeLinks = @$inLinks.add @$outLinks.add @$prevNode.add @$nextNode
      @
    fill: (model) ->
      @reset()
      super model.attributes
      _renderLinked = @_renderLinked.bind @
      if model.actions? # is node
        name = model.name()
        if model.inLinks?.length
          @$inLinks.append model.inLinks.map _renderLinked
        else
          @$inLinks.append _renderLinked null
        if model.outLinks?.length
          @$outLinks.append model.outLinks.map _renderLinked
        else
          @$outLinks.append _renderLinked null
        @fillActions model.actions()
      else # is link
        name = "Link: #{model.name()}"
        @$prevNode.append _renderLinked model.prevNode
        @$nextNode.append _renderLinked model.nextNode
      @nameEl.textContent = name
      @keyEl.textContent = model.get 'key'
      @
    _renderLinked: (model) ->
      if model
        btn = document.createElement 'a'
        btn.className = 'btn btn-link linked-item'
        btn.textContent = model.name()
        $.data btn, 'model', model
      else
        btn = document.createElement 'button'
        btn.className = 'btn btn-link'
        btn.disabled = true
        btn.textContent = '(None)'
      btn
    read: ->
      data = super
      data.actions = @readActions()
      data
    reset: ->
      @clearActions()
      @$linkedNodeLinks.empty()
      super

  # Viewer

  class ProjectViewerView extends InnerFrameView
    initialize: (options) ->
      super options
    load: (name) ->
      console.log 'load project', name
      @
    focus: (opt = {}) ->
      {link, node, action} = opt
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      console.log 'focus node/link', {link, node, action}

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
