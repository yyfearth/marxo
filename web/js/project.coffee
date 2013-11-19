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
WorkflowDiagramView
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
          Projects.find workflowId: name, callback: ({workflow}) =>
            throw new Error "project with id #{name} cannot found" unless workflow
            @editor.edit workflow, sub
      # @switchTo @viewer
      # @viewer.load project
      # @viewer.focus sub
      @

  # Editor

  class ProjectEditorView extends FormDialogView
    goBackOnHidden: 'project/mgr'
    workflows: Workflows.workflows
    projects: Projects.projects
    events:
      'change select[name=template_id]': (e) ->
        wf = e.currentTarget.value
        cur = @model.get 'template_id'
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
        @form.template_id.value = @model.get('template_id') or ''
        $(@form.template_id).change()
        return
      'click li.sidebar-item > a, a.linked-item': (e) ->
        e.preventDefault()
        @navTo $(e.currentTarget).data('model') or e.currentTarget.dataset.item
        false
    initialize: (options) ->
      super options
      @sidebar = find '.sidebar', @el
      @$btnSelect = $ find '.btn-select', @form
      @$wfbtns = @$btnSelect.add find '.btn-revert', @form
      wfPreview = find '#wf_preview', @el
      @$wfPreview = $ wfPreview
      @wfDiagram = new WorkflowDiagramView el: wfPreview
      @$nodeLinkSection = $ find 'section.node-link', @el
      @$projectForm = $ @form
      @$actions = $ find '.node-actions', @el
      @dataEditor = new NodeLinkDataEditor el: @$nodeLinkSection[0], actionEl: @$actions[0]
      @
    create: (wf) ->
      wf = wf?.id or wf
      wf = null unless typeof wf is 'string'
      @popup new Project(template_id: wf), (action) => if action is 'save'
        console.log 'wf created', action, @model
        @projects.create @model, wait: true
        # TODO: save all nodes and links?
        @trigger 'create', @model, @
      @
    edit: (project, opt = {}) ->
      {link, node, action} = opt
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      console.log 'popup node/link editor', {link, node, action}
      @popup project, (action, data) => if action is 'save'
        console.log 'project saved', action, data
        @model.save @model
        @trigger 'edit', @model, @
      @
    popup: (model, callback) ->
      data = model.toJSON()
      @model = model
      super data, callback
      select = @form.template_id
      select.disabled = true
      @workflows.load (ignored, ret) =>
        @_renderSelect() if 'loaded' is ret
        @fill data
        unless model.isNew()
          if model.has 'created_by'
            @$el.find('#project_created_by').val(model.get 'created_by')
            .parents('.control-group').show()
          @$el.find('#project_created_at').val(new Date(model.get 'created_at').toLocaleString())
          .parents('.control-group').show()
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
      type = (if typeof model is 'string' then model else model?._name) or 'project'
      if @_cur_type isnt type
        @$projectForm.hide()
        @$actions.hide()
        $section = @$nodeLinkSection.hide()
        $nodeOptions = $section.find('.node-options').hide()
        $linkOptions = $section.find('.link-options').hide()
        if type is 'project'
          @$projectForm.show()
          model = null
        else if type is 'users'
          # TODO: user list
          @$projectForm.show()
          model = null
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
      # read prev model
      @_readData()
      # fill new model
      @_cur_model = model
      console.log 'nav to', type, model
      @dataEditor.fill model if model
      # show active status
      $sidebar = $ @sidebar
      $sidebar.find('.sidebar-item.active').removeClass 'active'
      $sidebar.find(if model then ".sidebar-item:has(a[data-cid='#{model.cid}'])" else ".project-item").addClass 'active'
      @wfDiagram.highlight model
      @
    _readData: ->
      if model = @_cur_model
        data = @dataEditor.read()
        console.log 'data', data, 'for', model
        model.set data
        model._changed = true
      return
    _selectWorkflow: ->
      wf = @form.template_id.value
      return unless wf
      wf = @workflows.get wf unless wf instanceof Workflow
      project = @model
      if project.nodes?.length or project.has 'node_ids'
        # return @ unless confirm 'Change workflow will discard existing settings!\n\nAre you sure to change?'
        # clear nodes and links
        project.set node_ids: null, nodes: null, link_ids: null, links: null
        project._warp()
      console.log 'selected wf for project', wf.name
      project.copy wf
      unless @form.name.value
        @form.name.value = wf.get 'name'
        $(@form.name).trigger 'input'
      @_renderProject project
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
      project.nodes.forEach (node, i) ->
        nodes.appendChild _renderSidebarItem node, i
      links = document.createDocumentFragment()
      project.links.forEach (link, i) ->
        links.appendChild _renderSidebarItem link, i
      $sidebar.find('.node-header').after nodes
      $sidebar.find('.link-header').after links
      @wfDiagram.draw project
      return
    _renderSidebarItem: (model, i) ->
      el = document.createElement 'li'
      el.className = "sidebar-item #{model._name}-item"
      a = document.createElement 'a'
      name = a.textContent = "#{1 + i}. #{model.name()}"
      a.dataset.cid = model.cid
      $a = $(a).data 'model', model
      $a.tooltip title: name, placement: 'right', container: @el if name.length > 15
      el.appendChild a
      el
    render: ->
      @workflows.load => @_renderSelect()
      super
    reset: ->
      @$wfbtns.hide()
      @$el.find('.control-group:has(#project_created_at)').hide()
      $(@sidebar).find('li.node-item, li.link-item').remove()
      @navTo null
      super
    _renderSelect: ->
      select = @form.template_id
      wfs = @workflows.fullCollection
      if wfs.length
        owned = document.createElement 'optgroup'
        owned.label = 'Owned Workflows'
        shared = document.createElement 'optgroup'
        shared.label = 'Shared Workflows'
        wfs.forEach (wf) ->
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
      @_readData() # read last modified
      @data = @read()
      @model.set @read()
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
      id = @model.get('template_id')
      unless id
        console.warn 'workflow cell cannot find template_id for project',  @model
        @$el.text '(None)'
      else @collection.find workflowId: id, callback: ({workflow}) =>
        if workflow
          name = _.escape workflow.get 'name'
          @$el.addClass('workflow-link-cell').append $('<a>',
            tabIndex: -1
            href: '#workflow/' + id
          ).attr('title', name).text name
          @delegateEvents()
        else
          console.warn 'failed to find workflow with id', id, @model
          @$el.text '(Unknown)'
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
      name: 'template_id'
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
    defaultFilterField: 'name'
    initialize: (options) ->
      super options
      @list = new WorkflowListView el: find 'ul.workflow-list', @el
      @listenTo @list, 'select', (id, model) ->
        console.log 'create project from workflow', id, model
        @trigger 'create', id, model
      @on 'remove', @remove.bind @
      # sync collections
      projects = Projects.projects.fullCollection
      @listenTo projects, 'add', (model) =>
        @collection.add model
        @refresh()
        return
      @listenTo @collection, 'remove', (model) =>
        projects.remove model
        return
      @
    remove: (models) ->
      models = [models] unless Array.isArray models
      names = models.map (model) -> model.get 'name'
      # TODO: project life cycle (engine)
      # TODO: started projects cannot be deleted
      if confirm "Are you sure to remove these projects: #{names.join ', '}?\n\nThis action cannot be undone!"
        models.forEach (model) -> model.destroy()
        # TODO: remove related data?
      @
    render: ->
      @list.fetch()
      super

  ProjectFrameView
