"use strict"

define 'workflow', [
  'base', 'manager', 'models', 'actions', 'lib/jquery-jsplumb'
], ({
find
findAll
tpl
tplAll
View
BoxView
FrameView
InnerFrameView
ModalDialogView
FormDialogView
NavListView
}, {
ManagerView
}, {
Entity
Workflows
Workflow
Nodes
Node
Link
Actions
Action
}, ActionsMixin, jsPlumb) ->

  ## Workflow Main Frame
  class WorkflowFrameView extends FrameView
    initialize: (options) ->
      super options
      @editor = new WorkflowEditorView el: '#workflow_editor', parent: @
      @manager = new WorkflowManagerView el: '#workflow_manager', parent: @
      @
    open: (name, sub) ->
      switch name
        when 'new'
          console.log 'show workflow editor with create mode'
          @switchTo @manager
          @manager.create name
        when 'mgr'
          console.log 'show workflow mgr'
          @switchTo @manager
        else
          throw new Error 'open workflow with a name or id is needed' unless name
          console.log 'show workflow editor for', name
          @switchTo @editor
          @editor.load name, sub
      @

  ## Workflow Manager

  class TemplateWorkflowListView extends NavListView
    auto: false
    urlRoot: 'worklfow'
    headerTitle: 'Create from Template'
    itemClassName: 'workflow-list-item'
    collection: Workflows.workflows
    defaultItem: null
    allowEmpty: true
    emptyItem: new Workflow id: 'new', name: 'Empty Workflow'
    events:
      'click': (e) ->
        el = e.target
        if el.tagName is 'A' and el.dataset.id
          e.preventDefault()
          @trigger 'select', el.dataset.id
          false
    _render: (models = @collection) ->
      models = models.fullCollection if models.fullCollection
      #console.log 'render models', models
      fragments1 = document.createDocumentFragment()
      fragments2 = document.createDocumentFragment()
      models.forEach (model) =>
        fragments = if model.has 'tenant_id' then fragments2 else fragments1
        fragments.appendChild @_renderItem model
      @el.appendChild fragments1
      @el.appendChild @_renderHeader 'Create from Workflows'
      @el.appendChild fragments2
      return
    render: ->
      @_clear()
      @_render()
      @_super_render()
      @

  class WorkflowActionCell extends Backgrid.ActionsCell
    render: ->
      super
      @_hide 'remove' unless @model.get 'tenant_id'
      @

  class WorkflowManagerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'name:workflow'
      'desc'
      #'type'
      'sharing'
      'created_at'
      'updated_at'
    ,
      name: 'workflow'
      label: ''
      editable: false
      sortable: false
      cell: WorkflowActionCell
    ]
    collection: new Workflows
    defaultFilterField: 'name'
    initialize: (options) ->
      super options
      @list = new TemplateWorkflowListView el: find 'ul.template-list', @el
      @creator = new WorkflowCreatorView el: '#workflow_creator', parent: @
      _remove = @remove.bind @
      @on remove: _remove, remove_selected: _remove
      @list.on 'select', @create.bind @
      # sync collection with shared collection
      @listenTo @collection, 'add', (model) =>
        Workflows.workflows.add model
        @refresh()
      @listenTo @collection, 'remove', (model) ->
        Workflows.workflows.remove model
      @
    create: (template_id) ->
      template_id = '' if not template_id or /^(?:new|empty)$/i.test template_id
      @creator.popup {template_id}, (action, data) =>
        if action is 'save'
          console.log 'create new wf:', data
          if template_id = data.template_id
            wf = Workflows.workflows.get template_id
            throw new Error 'cannot find workflow with id', template_id unless wf
            _do_create = (wf) =>
              data.desc = "Cloned from workflow #{wf.get 'name'}" unless data.desc
              new Workflow().copy(wf).save data, success: (wf) =>
                @collection.add wf
                @refresh()
              return
            if wf.loaded()
              _do_create wf
            else
              wf.fetch reset: true, success: _do_create
          else
            delete data.template_id # api not accept empty template_id
            @collection.create data, wait: true
      @
    remove: (models) ->
      models = [models] unless Array.isArray models
      if confirm 'Make sure these selected workflows is not in use!\nDo you realy want to remove selected workflows?'
        model?.destroy() for model in models
        @reload() if models.length >= @pageSize / 2
      #console.log 'delete', model, @
      @
    render: ->
      @list.fetch()
      super

  class WorkflowCreatorView extends FormDialogView
    el: '#workflow_creator'
    goBackOnHidden: 'workflow/mgr'
    collection: Workflows.workflows
    #initialize: (options) ->
    #  super options
    popup: (data, callback) ->
      super data, callback
      @fill data
      @
    save: ->
      @data = @read()
      @callback 'save'
      @hide true
      @
    render: ->
      @collection.load => @_renderSelect()
      super
    _renderSelect: ->
      select = @form.template_id
      wfs = @collection.fullCollection
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
        op.textContent = '(Empty Workflow)'
        select.appendChild op
        select.appendChild owned if owned.childElementCount
        select.appendChild shared if shared.childElementCount
      return

  ## Workflow Editor (Workflow/Node/Link/Action Editor)

  class WorkflowEditorView extends InnerFrameView
    events:
      'click .wf-save': 'save'
      'click .wf-reset': (e) ->
        @reset e.modifiers or not e.currentTarget.classList.contains 'btn-danger'
      'click #workflow_header': ->
        @renamer.popup @model, (action, wf) =>
          if action is 'save'
            console.log 'save name', wf
            @nameEl.textContent = wf.get 'name'
            @descEl.textContent = wf.get 'desc'
            @model.trigger 'changed', 'rename_workflow', @model
          return
      'change #wf_orientation': (e) ->
        @view.setOrientation e.currentTarget.value, true
    initialize: (options) ->
      super options
      @view = new WorkflowView
        el: find('#workflow_view', @el)
        nodeEditor: new NodeEditorView
        linkEditor: new LinkEditorView
      @nodeList = new NodeListView
        el: find('#node_list', @el)
        parent: @
      @renamer = new EditorView
        el: find('#workflow_name_editor', @el)

      @btnSave = find '.wf-save', @el
      @btnReset = find '.wf-reset', @el
      @nameEl = find '.editable-name', @el
      @descEl = find '.editable-desc', @el
      @$orientation = $ find '#wf_orientation', @el

      @nodeList.on 'select', (id, node) =>
        console.log 'select from node list', id, node
        if id is 'new'
          node = null
        else if id
          node ?= @model.nodes.get id
        @view.createNode node
      @
    _enableBtns: (enable) ->
      @btnSave.disabled = not enable or @readonly
      $btn = $ @btnReset
      if enable
        $btn.addClass('btn-danger').text 'Reset'
      else
        $btn.removeClass('btn-danger').text 'Reload'
      return
    reset: (force) ->
      if force is true or confirm 'All changes will be descarded since last save, are you sure to do that?'
        @reload()
        @_enableBtns false
      @
    save: ->
      wf = @model
      if @readonly
        console.error 'not allow to change readonly workflow!'
        @reset true
        return @
      else unless wf.nodes.length
        alert 'Cannot save a workflow without any node!'
        return @
      else unless wf.has('start_node_id') and wf.nodes.get wf.get 'start_node_id'
        startNodes = wf.nodes.filter (node) -> not node.inLinks.length
        if startNodes.length is 1
          @view.setStartNode startNodes[0].id
        else
          alert 'Please set the start node!'
          return @
      console.log 'save', wf.attributes
      @_enableBtns false
      wf.save {},
        wait: true
        success: (wf) ->
          console.log 'saved', wf
        error: ->
          @_enableBtns true
          console.error 'save failed', @model
      @
    _loaded: (wf, sub) ->
      @id = wf.id
      @nameEl.textContent = wf.get 'name'
      @descEl.textContent = wf.get('desc') or ''
      @model = wf
      # calc orientation
      w = 0
      h = 0
      @model.nodes.forEach (node) -> if node.has 'offset'
        {x, y} = node.get 'offset'
        w = x if x > w
        h = y if y > h
        return
      orientation = if w and h and w < h then 'v' else 'h'
      @$orientation.val orientation
      @view.setOrientation orientation
      @view.load wf, sub
      @nodeList.setNodes wf.nodes
      # TODO: show warning in workflow editor view
      @readonly = not @model.isNew() and not @model.has 'tenant_id'
      @listenTo wf, 'changed', (action, entity) ->
        @_enableBtns true
        console.log 'workflow changed', action, entity
      return
    _clear: ->
      @stopListening @model
      @id = null
      @model = null
      @nameEl.textContent = ''
      @descEl.textContent = ''
      @_enableBtns false
      @readonly = false
      @view.clear()
      return
    load: (wf, sub) ->
      if not wf
        @_clear()
      else if typeof wf is 'string'
        if @id is wf and not sub?.reload
          @load @model, sub
        else
          @_clear()
          wf = new Workflow id: wf
          wf.fetch silent: true, success: (wf) => @_loaded wf, sub
      else if wf.id?
        if @id is wf.id
          @_loaded wf, sub
        else
          if wf.loaded()
            @_loaded wf, sub
          else
            @_clear()
            wf.fetch silent: true, success: (wf) => @_loaded wf, sub
      else
        throw new Error 'load neigher workflow id string nor workflow object'
      @
    reload: ->
      @load @id, reload: true
    render: ->
      @nodeList.render()
      super

  class NodeListView extends NavListView
    urlRoot: 'node'
    headerTitle: 'Common Nodes'
    defaultItem: new Node(id: 'new', name: 'Empty Node')
    itemClassName: ''
    targetClassName: 'node thumb'
    collection: new Nodes
    events:
      'click': (e) ->
        el = e.target
        if el.tagName is 'A' and el.dataset.id
          e.preventDefault()
          @trigger 'select', el.dataset.id, $(el).data 'model'
          false
      'mouseenter .node': (e) ->
        unless $.data e.target, 'is-draggable'
          $(e.target).draggable(
            containment: @parent.el
            helper: 'clone'
            zIndex: 999
          ).data 'is-draggable', true
        return
    setNodes: (nodes) ->
      if @nodes isnt nodes
        @stopListening @nodes if @nodes
        @nodes = nodes
        if nodes
          render = @render
          @listenTo nodes, 'reset', render
          @listenTo nodes, 'add', render
          @listenTo nodes, 'remove', render
        @render()
      @
    render: ->
      @_clear()
      @el.appendChild @_renderHeader 'Shared Nodes'
      @_render()
      if @nodes
        @el.appendChild @_renderHeader 'Used Nodes'
        @_render @nodes
      @_super_render()
      @

  class EditorView extends FormDialogView
    popup: (data, callback) ->
      throw new Error 'data must be an model entity' unless data instanceof Entity
      super data, callback
      @readonly = not data.isNew() and not data.has 'tenant_id'
      @fill data.attributes
      @btnSave.textContent = if data.isNew() then 'OK' else 'Save'
      @on 'shown', =>
        $(@form).find(':input').prop 'readOnly', @readonly
        @btnSave.disabled = @readonly
        @form.name.select()
      @
    save: ->
      @data.set @read()
      @callback 'save'
      @hide true
      @

  class NodeEditorView extends EditorView
    @acts_as ActionsMixin
    el: '#node_editor'
    events:
      'click a.action-thumb': '_addAction'
      'shown': '_fixStyle'
    _too_many_actions_limit: 7
    initialize: (options) ->
      super options
      @initActions options
      @_fixStyle = @_fixStyle.bind @
      $(window).on 'resize', @_fixStyle
      @_too_many_alert = find '#too_many_actions_alert', @el
      @on 'actions_update', @_checkActionLimit.bind @
      @
    remove: ->
      $(window).off 'resize', @_fixStyle
      super
    _fixStyle: -> # make sure the top of action box will below the name, key and desc
      @actionsEl.style.top = 20 + $(@form).height() + 'px'
      return
    _addAction: (e) ->
      e.preventDefault()
      target = e.target
      matched = target.href.match /action:(\w+)/i
      @addAction {type: matched[1]}, {scrollIntoView: true} if matched
      false
    fill: (attributes) ->
      # fill info form
      super attributes
      @fillActions attributes?.actions
      @
    save: ->
      @data.set 'actions', @readActions()
      # save the node
      super
      @
    reset: ->
      @clearActions()
      super
    _checkActionLimit: ->
      cls = @_too_many_alert.classList
      count = findAll('.action', @actionsEl).length # @actions.length
      if count > @_too_many_actions_limit
        cls.add 'active'
        @_too_many_alert.scrollIntoViewIfNeeded()
      else
        cls.remove 'active'

  class LinkEditorView extends EditorView
    el: '#link_editor'
    #initialize: (options) ->
    #  super options
    #popup: (data, callback) ->
    #  super data, callback
    #  @

  ## Workflow Views (Workflow/Node/Link View)

  class WorkflowView extends View
    jsPlumbDefaults:
      DragOptions:
        zIndex: 2000
      Endpoint: ['Dot', radius: 3, cssClass: 'endpoint']
      ConnectionsDetachable: true
      ReattachConnections: true
      HoverPaintStyle:
        strokeStyle: '#42a62c'
        lineWidth: 2
        zIndex: 2000
      Connector: [
        'Flowchart'
        stub: [40, 60]
        gap: 10
        cssClass: 'link'
      ]
      ConnectionOverlays: [
        [ 'Arrow',
          location: -5
          id: 'arrow'
        ]
      ]
    gridDefaults:
      padding: 30
      spanX: 300
      spanY: 150
    events:
      'click .node': '_togglePopover'
      'mousedown': '_cancelPopover'
      'click .popover .btn-delete': (e) -> @_action 'remove', e
      'click .popover .btn-edit': (e) -> @_action 'edit', e
      'dblclick .node': (e) -> @_action 'edit', e

    initialize: (options) ->
      super options
      @nodeEditor = options.nodeEditor
      @linkEditor = options.linkEditor
      jsPlumb.importDefaults @jsPlumbDefaults
      @render()
      @
    _bind: ->
      view = @
      jsPlumb.bind 'beforeDrop', (info) ->
        sourceId = info.sourceId[5..]
        targetId = info.targetId[5..]
        if sourceId is 'start'
          view.setStartNode targetId
        else unless view.model.hasLink sourceId, targetId
          # remove node_ in id
          view.createLink sourceId, targetId
        false
      # link dblclick to edit
      jsPlumb.bind 'dblclick', (conn) ->
        view.editLink conn.getParameter 'link'
        return

      # link click
      jsPlumb.bind 'click', (conn) =>
        label = conn.getOverlay 'label'
        @_togglePopover target: label.canvas if label?
        return

      # droppable for .node
      @$el.droppable
        accept: '.node.thumb'
        drop: (e, ui) =>
          node = ui.draggable.data 'model'
          if node instanceof Node
            # set offset after createNode since it will remove offset when clone
            @createNode node, (node) =>
              $el_offset = @$el.offset()
              offset =
                x: ui.offset.left - $el_offset.left
                y: ui.offset.top - $el_offset.top
              offset.x = 0 if offset.x < 0
              offset.y = 0 if offset.y < 0
              node.set 'offset', offset
              true
            true
          else
            false

      return

    _hidePopover: ->
      _popped = @_popped
      if _popped
        _popped._delay = clearTimeout(_popped._delay) if _popped._delay
        $(_popped).popover 'hide'
      return
    _cancelPopover: (e) ->
      if @_popped and not @$el.find('.popover').has(e.target).length
        @_hidePopover()
        org_popped = @_popped
        @_popped = null
        org_popped._hidding = true
        setTimeout ->
          delete org_popped._hidding
        , 300
      return
    _togglePopover: (e) ->
      el = e.target
      if @_popped isnt el and not el._hidding
        el._delay = setTimeout =>
          $(el).popover 'show' if @_popped = el
          el._delay = null
        , 100
        @_popped = el
      else
        @_hidePopover()
        @_popped = null
      return

    _action: (action, e) ->
      $target = $ e.target
      $target = $target.parents '.target' unless $target.hasClass 'target'
      if model = $target.data 'node'
        func = @[action + 'Node']
      else if model = $target.data 'link'
        func = @[action + 'Link']
      else unless $target.hasClass 'node-start'
        console.error 'no node or link in data', $target, e
        return
      func?.call @, model
      return
    clear: ->
      # TODO: destroy all views of nodes and links
      @startNode?.remove()
      @startNode = null
      @el.innerHTML = ''
      # unbind model events
      @stopListening @model
      @
    load: (wf, {link, node, action, reload} = {}) ->
      throw new Error 'cannot open a action without given a node' if action and not node
      throw new Error 'node and link cannot be open together' if link and node

      if wf isnt @model or reload
        @clear()
        @model = wf
        # bind add node/link event
        @listenTo @model.nodes, 'add', @_addNode.bind @
        @listenTo @model.links, 'add', @_addLink.bind @
        # node/link remove already binded on destroy
        @_renderModel wf
        @hash = "#workflow/#{wf.id}"

      #console.log 'load wf', location.hash
      if node
        @editNode wf.nodes.get node
        @nodeEditor.viewAction action if action
      else if link
        @editLink wf.links.get link
      else
        @nodeEditor.cancel()
        @linkEditor.cancel()
      @
    setOrientation: (orientation = 'h', reload) ->
      if @orientation isnt orientation
        @orientation = orientation
        @load @model, reload: true if reload
      @
    _sortNodeViews: (nodes) ->
      nodes.lonely = []
      nodes.start = []
      nodes.end = []
      nodes.forEach (node) ->
        if node.inLinks.length is node.outLinks.length is 0
          nodes.lonely.push node
        else if node.inLinks.length is 0
          nodes.start.push node
        else if node.outLinks.length is 0
          nodes.end.push node

      grid = @grid = [nodes.start.concat nodes.lonely]
      {padding, spanX, spanY} = @gridDefaults
      vertical = @orientation is 'v'

      do traval = (level = 0) ->
        nextLevel = []
        grid[level]?.forEach (node, i) ->
          node.gridX = i
          node.gridY = level
          if vertical
            node.x = i * spanX
            node.y = level * spanY
          else
            node.x = level * spanX
            node.y = i * spanY
          node.x += padding
          node.y += padding
          node.outLinks?.forEach (link) ->
            nextLevel.push link.nextNode unless link.nextNode.gridX?
            return
          return
        if nextLevel.length
          grid[level + 1] = nextLevel
          traval level + 1
        return

      console.log 'grid', grid
      return
    _renderModel: (wf) ->
      console.log 'render wf', wf
      wf = @model
      throw new Error 'workflow not loaded' unless wf?
      # start node
      @startNode?.remove()
      view = @startNode = new StartNodeView parent: @
      view.render()
      @el.appendChild view.el
      #console.log wf.nodes
      unless wf.nodes.length and wf.nodes.at(0).has 'offset'
        @_sortNodeViews wf.nodes
      jsPlumb.ready =>
        wf.nodes.forEach @_addNode.bind @
        wf.links.forEach @_addLink.bind @
        @setStartNode wf.get 'start_node_id'
        return
      return
    setStartNode: (node) ->
      unless node?
        console.log 'auto detect start node'
        startNodes = @model.nodes.filter (node) -> not node.inLinks.length
        node = startNodes[0] or @model.nodes.at 0
        node = node?.id
      else if node.id?
        node = node.id
      console.log 'set start node', node
      if node?
        if node isnt @model.get 'start_node_id'
          @model.set 'start_node_id', node
          @model.trigger 'changed', 'set_start_node', @model
        startNode = @startNode
        throw new Error 'startNodeView not exist' unless startNode
        if startNode.conn then try
          jsPlumb.detach startNode.conn
        startNode.conn = jsPlumb.connect
          source: startNode.srcEndpoint
          target: "node_#{node}"
          cssClass: 'link'
          hoverClass: 'hover'
      @
    addNode: (node = 'emtpy') ->
      if node is 'new' or node is 'empty'
        console.log 'add a empty node'
        return @createNode()
      else unless node instanceof Node
        console.error 'add a invalid node', node
        throw new Error 'add a invalid node'
      console.log 'add node', node
      #@model.nodes.add node
      @model.createNode node
      #@_addNode node
      @
    _addNode: (node) ->
      view = node.view = new NodeView model: node, parent: @
      view.render()
      @el.appendChild view.el
      return
    createNode: (node, callback) ->
      wf_id = @model.id
      if not node or node.id is 'new'
        node = new Node workflow_id: wf_id
      else if node instanceof Node and node.id?
        node = node.clone()
        name = node.get 'name'
        desc = node.get 'desc'
        node.set
          workflow_id: wf_id
          template_id: node.id
          key: node.get('key') + '_clone'
          name: name + ' (Clone)'
          desc: if desc then desc + ' (Clone)' else null
        node.unset 'offset'
        node.unset 'id'
      else if node.name
        node.workflow_id = wf_id
        node = new Node node
      else
        console.error 'invalid node to create', node
        return
      @nodeEditor.popup node, (action, node) =>
        if action is 'save'
          # prevent create if callback return false
          if false isnt callback? node
            #@model.trigger 'changed', 'create_node', node
            @model.createNode node, =>
              @setStartNode node if @model.nodes.length is 1
              @model.save()
        else # canceled
          console.log 'canceled or ignored create node', action
      @
    editNode: (node) ->
      return @ unless node?.id?
      @nodeEditor.popup node, (action, node) =>
        if action is 'save'
          node.view.update node
          unless node.isNew() # auto save
            node.save()
          else
            @model.trigger 'changed', 'edit_node', node
        else
          console.log 'canceled or ignored edit node', action
        # restore url to workflow only
        @router.navigate @hash if action isnt 'ignored'
      # add node to url
      hash = "#{@hash}/node/#{node.id}"
      if location.hash.indexOf(hash) is -1
        @nodeEditor.once 'shown', => @router.navigate hash
      @
    removeNode: (node) ->
      return @ if node?.isNew()
      entities = [].concat node.inLinks, node.outLinks
      # use confirm since no support for undo
      if confirm "Delete the node: #{node.get 'name'} with all #{entities.length} links connected?"
        {nodes, links} = @model
        nodes.remove node
        @setStartNode() if node.id is @model.get 'start_node_id'
        links.remove entities
        nodes.remove node
        @model.save()
        entities.push node
        entity.destroy() for entity in entities
        #@model.trigger 'changed', 'remove_node', node
      @
    createLink: (from, to, callback) ->
      from = @model.nodes.get from unless from.id? and from.has 'name'
      to = @model.nodes.get to unless to.id? and to.has 'name'
      key = "#{from.get 'key'}_to_#{to.get 'key'}"
      data = new Link
        workflow_id: @model.id
        key: key[0..32].toLowerCase()
        prev_node_id: from.id
        next_node_id: to.id
      @linkEditor.popup data, (action, link) =>
        if action is 'save'
          #@model.links.add link
          @model.createLink link if false isnt callback? link
          @model.trigger 'changed', 'create_link', link
        else # canceled
          console.log 'canceled or ignored create link', action
      @
    _addLink: (link) ->
      view = link.view = new LinkView model: link, parent: @
      view.render()
      return
    editLink: (link) ->
      return @ unless link?.id
      @linkEditor.popup link, (action, link) =>
        if action is 'save'
          link.view.update link
          unless link.isNew() # auto save
            link.save()
          else
            @model.trigger 'changed', 'edit_link', link
        else # canceled
          console.log 'canceled or ignored edit link', action
        # restore url to workflow only
        @router.navigate @hash if action isnt 'ignored'
      # add link to url
      hash = "#{@hash}/link/#{link.id}"
      if location.hash.indexOf(hash) is -1
        @linkEditor.once 'shown', => @router.navigate hash
      @
    removeLink: (link) ->
      return @ if link?.isNew()
      # use confirm since no support for undo
      if confirm "Delete the link: #{link.get('name') or '(No Name)'}?"
        @model.links.remove link
        @model.save()
        link.destroy()
        #@model.trigger 'changed', 'remove_link', link
      @
    render: ->
      @_bind()
      # TODO: make el drag scrollable
      # @$el.draggable axis: 'x'
      # chrome fix
      @el.onselectstart = -> false
      super

  class StartNodeView extends View
    id: 'node_start'
    className: 'node node-start target'
    sourceEndpointStyle:
      isSource: true
      uniqueEndpoint: true
      maxConnections: -1
      paintStyle:
        fillStyle: '#225588'
        radius: 9
      connectorStyle:
        strokeStyle: '#346789'
        dashstyle: '2 2'
        lineWidth: 2
        outlineColor: '#fff'
        outlineWidth: 1
      connectorHoverStyle:
        strokeStyle: '#42a62c'
        outlineWidth: 2
    render: ->
      vertical = @parent.orientation is 'v'
      @$el.text('(S)').tooltip
        title: 'Start Point'
        container: @el
        placement: if vertical then 'right' else 'bottom'
      jsPlumb.draggable @$el, stack: '.node'
      @parentEl.appendChild @el
      @sourceEndpointStyle.anchor = if vertical then 'BottomCenter' else 'RightMiddle'
      @srcEndpoint = jsPlumb.addEndpoint @$el, @sourceEndpointStyle, parameters:
        start: true
        view: @
      super
    remove: ->
      if @srcEndpoint and @conn then try
        jsPlumb.deleteEndpoint @srcEndpoint
      @srcEndpoint = @conn = null
      super

  class NodeView extends View
    tagName: 'div'
    className: 'node'
    sourceEndpointStyle:
      isSource: true
      uniqueEndpoint: true
      maxConnections: -1
      paintStyle:
        fillStyle: '#225588'
        radius: 9
      connectorStyle:
        strokeStyle: '#346789'
        lineWidth: 2
        outlineColor: '#fff'
        outlineWidth: 1
      connectorHoverStyle:
        strokeStyle: '#42a62c'
        outlineWidth: 2
      connectorOverlays: [
        [ 'Label',
          location: 0.5
          label: 'new link'
          id: 'label'
          cssClass: 'link-label'
        ]
      ]
    targetEndpointStyle:
      isTarget: true
      paintStyle:
        fillStyle: '#225588'
        radius: 5
      dropOptions:
        hoverClass: 'hover'
        activeClass: 'active'
    _popover_tpl: tpl('#t_popover')
    render: ->
      el = @el
      node = el.node = @model
      @el.id = 'node_' + node.id
      @listenTo node, 'destroy', @remove.bind @
      if @parent.orientation is 'v'
        @sourceEndpointStyle.anchor = 'BottomCenter'
        @targetEndpointStyle.anchor = ['LeftMiddle', 'TopCenter', 'RightMiddle']
      else
        @sourceEndpointStyle.anchor = 'RightMiddle'
        @targetEndpointStyle.anchor = ['LeftMiddle', 'TopCenter', 'BottomCenter']
      @_renderModel node
      jsPlumb.draggable @$el, stack: '.node', stop: ->
        # set offset after drag
        x = parseInt el.style.left
        y = parseInt el.style.top
        console.log '$el_pos1',x,y
        x = 0 unless x > 0
        y = 0 unless y > 0
        console.log '$el_pos2',x,y
        node.set 'offset', {x, y}
        node.workflow?.trigger 'changed', 'move_node', node
        return
      @parentEl.appendChild el
      # build endpoints must after append el to dom
      param =
        parameters:
          node: node
          view: @
      @srcEndpoint = jsPlumb.addEndpoint @$el, @sourceEndpointStyle, param
      jsPlumb.makeTarget @$el, @targetEndpointStyle, param
      super
    update: (node = @model) ->
      @$el.popover 'destroy'
      @_renderModel node
      jsPlumb.repaint @el.id
      @
    _renderModel: (node = @model) ->
      @el.innerHTML = node.escape 'name'
      name = node.get 'name'
      if node.has 'offset'
        offset = node.get 'offset'
        style = @el.style
        style.left = offset.x + 'px'
        style.top = offset.y + 'px'
      else
        @el.style.left = node.x + 'px'
        @el.style.top = node.y + 'px'
      @$el.addClass('target').data node: node, view: @
      @$el.popover
        container: @parentEl
        trigger: 'manual'
        placement: 'bottom'
        title: name
        html: true
        content: @_popover_tpl.replace '{desc}', node.get('desc') or ''
      @$popover = @$el.data('popover').tip()
      @$popover?.addClass('target').data node: node, view: @
      return
    remove: ->
      @$el.popover 'destroy'
      jsPlumb.deleteEndpoint @srcEndpoint
      jsPlumb.deleteEndpoint @
      super

  class LinkView extends View
    _popover_tpl: NodeView::_popover_tpl
    render: ->
      link = @model
      link.view = @
      conn = @conn = jsPlumb.connect
        source: link.prevNode.view.srcEndpoint
        target: link.nextNode.view.el
        cssClass: 'link'
        hoverClass: 'hover'
        parameters:
          link: link
          view: @
      @setElement conn.canvas
      @listenTo link, 'destroy', @remove.bind @
      @label = conn.getOverlay 'label'
      @labelEl = @label.canvas
      @_renderLabel link
      super
    update: (link = @model) ->
      @_destroyPopover()
      @_renderLabel link
      @
    _renderLabel: (link) ->
      label$el = $(@labelEl)
      # _desc = "#{link.prevNode.get 'name'} to #{link.nextNode.get 'name'}"
      name = link.get 'name'
      @label.setLabel name or ''
      label$el.css 'visibility', if name then 'visible' else 'hidden'
      label$el.popover
        container: @parentEl
        title: name or 'Link'
        trigger: 'manual'
        placement: 'bottom'
        html: true
        content: @_popover_tpl.replace '{desc}', link.get('desc') or ''
      @$popover = label$el.data('popover').tip()
      @$popover?.addClass('target').data link: link, view: @
      @
    _destroyPopover: ->
      $(@labelEl).popover 'destroy'
    remove: ->
      @model?.view = null
      console.log 'remove', @conn, @model, @
      jsPlumb.detach @conn
      @_destroyPopover()
      super

  WorkflowFrameView
