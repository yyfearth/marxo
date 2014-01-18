"use strict"

define 'project', ['base', 'manager', 'models', 'diagram', 'actions'],
({
find
#findAll
View
FrameView
InnerFrameView
NavListView
FormView
FormDialogView
STATUS_CLS
}, {
ManagerView
WorkflowFilterView
}, {
Workflow
Workflows
Project
Projects
}, WorkflowDiagramView, ActionsMixin) ->

  class ProjectFrameView extends FrameView
    initialize: (options) ->
      super options
      @editor = new ProjectEditorView el: '#project_editor', parent: @
      @viewer = new ProjectViewerView el: '#project_viewer', parent: @
      @manager = new ProjectManagemerView el: '#project_manager', parent: @
      @listenTo @manager, 'create', (id) => @editor.create id
      @
    _url_pattern: /(#project\/\w+(?:\/(?:node|link|action)\/\w+)*).*/
    open: (name, sub) ->
      switch name
        when 'new'
          @editor.create sub
        when 'mgr'
          @switchTo @manager
        else
          throw new Error 'open project with a name or id is needed' unless name
          Projects.find workflowId: name, fetch: true, callback: ({workflow}) =>
            unless workflow
              console.error "project with id #{name} cannot found", name, sub
              alert 'Project not found!'
              @router.navigate 'project/mgr'
              @switchTo @manager
            else if sub?.edit
              @editor.edit workflow, sub
            else
              @switchTo @viewer
              @viewer.load workflow
              @viewer.select sub
              @viewer.btnEdit.href = location.hash.replace @_url_pattern, '$1/edit'
            return
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
        unless wf
          @wfDiagram.clear()
        else if wf is cur and @model.loaded()
          @wfDiagram.draw @model
        else
          @wfDiagram.draw $(e.currentTarget).find(':selected').data 'model'
        if wf and cur?
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
      @listenTo @wfDiagram, 'select', (model, workflow) =>
        @navTo model if @model and @model.id is workflow.id
        return
      @$nodeLinkSection = $ find 'section.node-link', @el
      @$projectForm = $ @form
      @$actions = $ find '.node-actions', @el
      @dataEditor = new NodeLinkDataEditor el: @$nodeLinkSection[0], actionEl: @$actions[0]
      @_renderSelect = _.throttle @_renderSelect.bind(@), 100, trailing: false
      @on 'shown', => # auto foucs
        select = @form.template_id
        if select.value
          @form.name.select()
        else
          select.focus()
        return
      @
    create: (wf) ->
      wf = wf?.id or wf
      wf = null unless typeof wf is 'string'
      @popup new Project(template_id: wf), null, (action) => if action is 'save'
        console.log 'wf created', action, @model
        @projects.create @model, wait: true
        @trigger 'create', @model, @
      @
    edit: (project, opt = {}) ->
      {link, node, action} = opt
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      console.log 'popup node/link editor', opt
      @popup project, opt, (action, data) => if action is 'save'
        console.log 'project saved', action, data
        @model.save()
        @trigger 'edit', @model, @
      @
    popup: (model, {link, node, action} = {}, callback) ->
      data = model.toJSON()
      @model = model
      super data, callback
      select = @form.template_id
      select.disabled = true
      @workflows.load (ignored, ret) =>
        @_renderSelect() if 'loaded' is ret or not @sidebar.rendered
        @fill data
        unless model.isNew()
          if model.has 'created_by'
            @$el.find('#project_created_by').val(model.get 'created_by')
            .parents('.control-group').show()
          @$el.find('#project_created_at').val(new Date(model.get 'created_at').toLocaleString())
          .parents('.control-group').show()
        isUpdate = select.disabled = not model.isNew() or model.has('node_ids') or model.nodes?.length
        if isUpdate
          @_renderProject model
          if node
            @navTo model.nodes.get node
            @dataEditor.viewAction action if action
          else if link
            @navTo model.links.get link
        else
          @_selectWorkflow()
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
          $section.show().find('input, textarea').prop 'readOnly', 'IDLE' isnt model.status()
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
      item = $sidebar.find(if model then ".sidebar-item:has(a[data-cid='#{model.cid}'])" else ".project-item").addClass 'active'
      item[0].scrollIntoViewIfNeeded()
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
      #if project.nodes?.length or project.has 'nodes'
      # return @ unless confirm 'Change workflow will discard existing settings!\n\nAre you sure to change?'
      console.log 'selected wf for project', wf.name
      _copy = (wf) =>
        unless wf.isValid(traverse: true) and wf.nodes.length
          @model.set 'template_id', ''
          @sidebar.classList.remove 'active'
          @btnSave.disabled = true
          setTimeout ->
            alert "Cannot create project from workflow #{wf.get 'name'}, because it is broken or not finished yet."
          , 500
          return
        project.copy wf, traverse: false # already traversed
        project.set 'status', 'IDLE'
        unless @form.name.value
          @form.name.value = wf.get 'name'
          $(@form.name).trigger 'input'
        @form.desc.value = "Created from workflow #{wf.get 'name'}" unless @form.desc.value
        @_renderProject project
        return
      console.log 'cpy', wf.loaded(), wf
      if wf.loaded() and wf.nodes?.length
        _copy wf
      else
        wf.fetch reset: true, success: _copy, error: ->
          alert 'failed to load worklfow ' + wf.get 'name'
      return
    _renderProject: (project) ->
      @sidebar.classList.add 'active'
      @$wfbtns.hide()
      readonly = 'IDLE' isnt project.status()
      @btnSave.disabled = readonly
      @$projectForm.find('input, textarea').prop 'readOnly', readonly
      # update sidebar
      project.sort()
      nodes = project.nodes
      links = project.links
      $sidebar = $ @sidebar
      $sidebar.find('li.node-item, li.link-item').remove()
      frag_nodes = document.createDocumentFragment()
      _renderSidebarItem = @_renderSidebarItem.bind @
      nodes.forEach (node, i) ->
        frag_nodes.appendChild _renderSidebarItem node, i
      frag_links = document.createDocumentFragment()
      links.forEach (link, i) ->
        frag_links.appendChild _renderSidebarItem link, i
      $sidebar.find('.node-header').after frag_nodes
      $sidebar.find('.link-header').after frag_links
      @sidebar.rendered = true
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
    reset: ->
      @$wfbtns.hide()
      @$projectForm.find('input, textarea').prop 'readOnly', false
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
          $(op).data 'model', wf
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
    events:
      'click .status-btns > .btn': (e) ->
        switch $(e.currentTarget).attr 'name'
          when 'start'
            @setStatus 'started'
          when 'stop'
            @setStatus 'stopped'
          when 'pause'
            @setStatus 'paused'
          when 'delete'
            @destroy()
          else
            throw new Error 'unknown status action'
        return
      'click .btn-reload': -> if @model?
        @model.fetch reset: true
        @router.navigate "project/#{@model.id}", trigger: true
        return
    collection: Projects.projects
    initialize: (options) ->
      diagramEl = find '.wf-diagram', @el
      @wfDiagram = new WorkflowDiagramView el: diagramEl
      @statusView = new ProjectStatusView el: $(diagramEl).next()
      @$title = $ find '.project-name', @el
      @$desc = $ find '.project-desc', @el
      @$status = $ find '.label-status > span', @el
      @btnEdit = find '.btn-edit', @el
      @list = new NavListView
        el: find('.project-list', @el)
        auto: false
        collection: @collection
        urlRoot: 'project'
        seperator: '/'
        headerTitle: 'Projects'
        defaultItem: false
        emptyItem: false
        allowEmpty: false
        itemClassName: 'project-list-item'
      @listenTo @list, 'updated', => if @model?
        @list.$el.find("li:has(a[data-id='#{@model.id}'])").addClass 'active'
      @listenTo @wfDiagram, 'select', (model) =>
        @router.navigate "project/#{@model.id}/#{model._name}/#{model.id}", trigger: true
      @_updateStatus = @_updateStatus.bind @
      super options
    load: (project, force) ->
      if force or @model isnt project
        console.log 'load project', project
        @stopListening @model if @model
        project = @collection.fullCollection.get(project) or new Project {project} if typeof project is 'string'
        @model = project
        @listenTo project, 'loaded', @_render.bind @
        @render() unless @rendered
        if project.loaded()
          @_render project
        else
          project.fetch reset: true
      @
    _render: ->
      console.log 'view project', @model
      project = @model
      @$title.text project.get 'name'
      @$desc.text "(#{project.nodes?.length or 0} Nodes, #{project.links?.length or 0} Links) #{project.get 'desc'}"
      @btnEdit.href = "#project/#{project.id}/edit"
      @_updateStatus()
      $list = @list.$el
      $selected = $list.find("li:has(a[data-id='#{project.id}'])").addClass 'active'
      $list.find('li.active').not($selected).removeClass 'active'
      @wfDiagram.draw project
      @statusView.load project, true
      return
    _updateStatus: ->
      status = @model.status()
      show_btns = switch status
        when 'IDLE'
          'start, delete'
        when 'STARTED', 'TRACKED'
          'pause'
        when 'PAUSED'
          'start, stop'
        when 'STOPPED'
          'start, delete'
        when 'FINISHED'
          ''
        when 'ERROR'
          'start, delete'
        else
          console.error 'unknow status', status
          'delete'
      $btns = @$el.find '.status-btns'
      $btns.find('.btn').hide()
      $btns.find('.btn[name=start]').text if status is 'PAUSED' then 'Resume' else 'Start'
      $btns.find(show_btns.replace(/(\w+)/g, '.btn[name="$1"]')).show()
      @$status.text(status).parent()
      .removeClass('label-success label-warning label-inverse label-info')
      .addClass STATUS_CLS[status.toLowerCase()] or ''
      return
    destroy: ->
      if confirm 'Are you sure to delete this project?\n\nThis step cannot be undone!'
        @model.destroy()
        model = @collection.at(0)
        if model
          @load model
        else
          @router.back()
      @
    setStatus: (status) ->
      unless status
        @_updateStatus()
      else
        status = status.toUpperCase()
        if status isnt @model.status()
          @model.status status, remote: true, callback: (status) =>
            @_updateStatus()
            alert 'Failed to chanage status!' unless status
            return
        else console.log 'status not changed', status
      @
    select: (opt = {}) ->
      {link, node, action} = opt
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node
      #console.log 'focus node/link', opt
      @statusView.select opt
      highlight = @wfDiagram.highlight
      if node
        highlight node, 'node'
      else if link
        highlight link, 'link'
      else
        highlight null
      @
    render: ->
      @statusView.render()
      @list.fetch()
      super

  class ProjectStatusView extends View
    _prefix: 'prj_status_lst'
    _cls: STATUS_CLS
    events:
      'dblclick li > a[href]': (e) ->
        @router.navigate e.currentTarget.href.replace(/^.*?#(project.*)/, '$1/edit'), trigger: true
      'click button[name=finish]': (e) ->
        return unless confirm 'Are you sure to force finish this action?\n\nIt will cause close submission and stop tracking.'
        action = $.data e.currentTarget, 'model'
        action?.status? 'FINISHED', (status) =>
          if 'FINISHED' isnt status
            alert 'Failed to force finish this action!'
          else @model.load =>
            setTimeout =>
              @select @_cur_selected or {}
            , 100
          return
        return
    initialize: (options) ->
      @$list = $ find '.nodes-links-list', @el
      @$detail = $ find '.node-link-detail', @el
      super options
    load: (wf, force) ->
      if force or @model isnt wf
        @model = wf
        @_renderList()
      @
    reset: ->
      @_cur_selected = null
      @load null, true
    select: ({link, node, action} = {}) ->
      unless @model?
        console.error 'model not given yet'
        return @
      @_cur_selected = {link, node, action}
      _prefix = @_prefix
      if node
        id = "##{_prefix}_node_#{node}"
        #console.log 'select node', node, @model, @model.nodes.get node
        @_renderNode @model.nodes.get node
        @$detail.find("##{_prefix}_action_#{action}").addClass('active')[0]?.scrollIntoViewIfNeeded() if action
      else if link
        id = "##{@_prefix}_link_#{link}"
        @_renderLink @model.links.get link
      else
        @$detail.empty()
        id = null
      #console.log 'select id', id
      @$list.find("li.active").removeClass 'active'
      @$list.find(id).addClass('active')[0]?.scrollIntoViewIfNeeded() if id
      @
    _renderLabel: (text, cls) ->
      span = document.createElement 'span'
      span.className = "label #{cls or ''}"
      span.textContent = text
      span
    _renderListItem: (model) ->
      wf = model.workflow
      li = document.createElement 'li'
      _model = model._name or ''
      li.id = "#{@_prefix}_#{_model}_#{model.id}"
      li.className = "capitalized #{_model}"
      a = document.createElement 'a'
      a.href = model._href = "#project/#{wf.id}/#{_model}/#{model.id}"
      a.textContent = "#{_model} #{model.idx + 1}: "
      i = document.createElement 'i'
      i.className = 'icon-right-open pull-right'
      a.appendChild i
      name = document.createElement 'strong'
      name.textContent = model.get('name') or model.name?().replace /_/g, ' '
      _label = @_renderLabel
      a.appendChild name
      a.appendChild _label '(Start Node)', 'label-info start-node' if model is wf.startNode
      if status = model.status(lowercase: true)
        a.className = "status-#{status}"
        a.appendChild _label status.toUpperCase(), 'pull-right ' + @_cls[status] or ''
      li.appendChild a
      li
    _renderHeaderItem: (text) ->
      li = document.createElement 'li'
      li.className = 'list-header capitalized disabled text-center'
      a = document.createElement 'a'
      a.textContent = text
      li.appendChild a
      li
    _renderList: (wf = @model) ->
      @$list.empty()
      return unless wf
      wf.sort()
      _header = @_renderHeaderItem
      _item = @_renderListItem.bind @
      frag = document.createDocumentFragment()
      if wf.nodes.length
        frag.appendChild _header 'Nodes'
        wf.nodes.forEach (node, i) ->
          node.idx ?= i
          frag.appendChild _item node
      if wf.links.length
        frag.appendChild _header 'Links'
        wf.links.forEach (link, i) ->
          link.idx ?= i
          frag.appendChild _item link
      @$list.append frag
      return
    _renderNode: (model) ->
      @$detail.empty()
      return unless model
      _prefix = @_prefix
      _label = @_renderLabel
      _href = model._href ?= "#project/#{model.workflow.id}/node/#{model.id}"
      _cls = @_cls
      _renderRef = @_renderRefLink
      frag = document.createDocumentFragment()
      frag.appendChild @_renderHeaderItem "#{model._name} #{model.idx + 1}: #{model.get 'name'}"
      model.actions().forEach (action, i) ->
        li = document.createElement 'li'
        li.id = "#{_prefix}_action_#{action.id}"
        li.className = 'action'
        a = document.createElement 'a'
        a.href = "#{_href}/action/#{action.id}"
        a.textContent = "Action #{i + 1}: "
        name = document.createElement 'strong'
        name.textContent = action.name()
        a.appendChild name
        if status = action.status(lowercase: true)
          a.className = "status-#{status}"
          if /^(?:started|tracked|paused)$/i.test status
            btn = document.createElement 'button'
            btn.className = 'btn btn-round btn-warning icon-ok pull-right'
            btn.name = 'finish'
            btn.title = 'Force Finish'
            btn.dataset.id = action.id
            btn.dataset.placement = 'left'
            $.data btn, 'model', action
            a.appendChild btn
          a.appendChild _label status.toUpperCase(), 'pull-right ' + _cls[status] or ''
        if action.content?.hasReport()
          _renderRef a, action, 'content', 'Report', 'icon-report', '#content/{{id}}/report'
        _renderRef a, action, 'content', 'Content', 'icon-page', '#content/{{id}}'
        _renderRef a, action, 'event', 'Event', 'icon-calendar', '#event/{{id}}'
        _renderRef a, action, 'tracking', 'Tracking', 'icon-calendar', '#event/{{id}}'
        span = document.createElement 'span'
        span.className = 'clearfix'
        a.appendChild span
        li.appendChild a
        frag.appendChild li
        return
      @$detail.removeClass('link-condition').addClass('node-actions').append frag
      return
    _renderRefLink: (el, action, name, title, cls, url) ->
      name = name.toLowerCase()
      id = action.get(name)?.id ? action.get(name + '_id')?.toString()
      if id?
        btn = document.createElement 'a'
        btn.className = 'ref-link pull-right ' + cls or ''
        btn.textContent = title
        btn.href = url.replace '{{id}}', id
        el.appendChild btn
      return
    _renderLink: (model) ->
      @$detail.empty()
      return unless model
      frag = document.createDocumentFragment()
      frag.appendChild @_renderHeaderItem "#{model._name} #{model.idx + 1}: #{model.name()}"
      li = document.createElement 'li'
      a = document.createElement 'a'
      a.textContent = 'Condition'
      #console.log 'render link', model.attributes
      if status = model.status(lowercase: true)
        a.className = "status-#{status}"
        a.appendChild @_renderLabel status.toUpperCase(), 'pull-right ' + @_cls[status] or ''
      li.appendChild a
      frag.appendChild li
      @$detail.removeClass('node-actions').addClass('link-condition').append frag
      return

  # Manager

  class WorkflowListView extends NavListView
    auto: false
    urlRoot: 'worklfow'
    headerTitle: 'Create from Workflows'
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
      @_super_render()
      @

  class WorkflowCell extends Backgrid.UriCell
    initialize: (options) ->
      super options
      @urlRoot = @column.get('urlRoot') or @urlRoot
      @urlRoot += '/' if @urlRoot and @urlRoot[-1..] isnt '/'
    render: -> # replace super
      @$el.empty()
      id = @model.get('template_id')
      unless id
        console.warn 'workflow cell cannot find template_id for project', @model
        @$el.text '(None)'
      else Workflows.find workflowId: id, callback: ({workflow}) =>
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
      super
      @_hide 'remove' if /^STARTED$|^PAUSED$/.test @model.status()
      @

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
      'status'
      'updated_at'
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
      @listenTo @collection, 'remove', (model) ->
        projects.remove model
        return
      @
    remove: (models) ->
      models = [models] unless Array.isArray models
      models = models.filter (model) -> not /^STARTED$|^PAUSED$/.test model.status()
      unless models.length
        alert '''None of selected project can be removed.
        Projects already STARTED or PAUSED cannot be removed.
        If you really want to remove them, STOP them first.'''
        return @
      names = models.map (model) -> model.get 'name'
      if confirm "Are you sure to remove these projects? \n#{names.join '\n'}\n\nThis action cannot be undone!"
        models.forEach (model) -> model.destroy()
      @
    render: ->
      @list.fetch()
      super

  ProjectFrameView
