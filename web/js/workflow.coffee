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

  class WorkflowManagerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'name:workflow'
      'desc'
      'type'
      'sharing'
      'created_at'
      'updated_at'
      'actions:workflow'
    ]
    collection: new Workflows
    defaultFilterField: 'name'
    events:
      'click #wf_template_list .wf_tempate a': (e) ->
        e.preventDefault()
        wf = e.target.href.match /#workflow:(\w+)/
        console.log 'wf template clicked', wf
        @create wf[1] if wf.length is 2
        false
    initialize: (options) ->
      super options
      @creator = new WorkflowCreatorView el: '#workflow_creator', parent: @
      _remove = @remove.bind @
      @on remove: _remove, remove_selected: _remove
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
            @collection.create data, wait: true
      @
    remove: (models) ->
      models = [models] unless Array.isArray models
      if confirm 'Make sure these selected workflows is not in use!\nDo you realy want to remove selected workflows?'
        # TODO: check usage, if used cannot remove directly
        model?.destroy() for model in models
        @reload() if models.length >= @pageSize / 2
      #console.log 'delete', model, @
      @

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
      'click .wf-reset': 'reset'
      'click #workflow_header': ->
        @renamer.popup @model, (action, wf) =>
          if action is 'save'
            console.log 'save name', wf
            @nameEl.textContent = wf.get 'name'
            @descEl.textContent = wf.get 'desc'
            @model.trigger 'changed', 'rename_workflow', @model
          return
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

      @nodeList.on 'select', (id, node) =>
        console.log 'select from node list', id, node
        if id is 'new'
          node = null
        else if id
          node ?= @model.nodes.get id
        @view.createNode node

      @_changed = @_changed.bind @
      @
    reset: ->
      if confirm 'All changes will be descarded since last save, are you sure to do that?'
        @reload()
        @btnSave.disabled = @btnReset.disabled = true
      @
    save: ->
      console.log 'save', @model.attributes
      @btnSave.disabled = @btnReset.disabled = true
      @model.save {},
        wait: true
        success: (wf) ->
          console.log 'saved', wf
        error: ->
          @_changed()
          console.error 'save failed', @model
      @
    _changed: ->
      @btnSave.disabled = @btnReset.disabled = false
      return
    _loaded: (wf, sub) ->
      @id = wf.id
      @nameEl.textContent = wf.get 'name'
      @descEl.textContent = wf.get('desc') or ''
      @model = wf
      @view.load wf, sub
      @nodeList.setNodes wf.nodes
      @listenTo wf, 'changed', (action, entity) ->
        @_changed()
        console.log 'workflow changed', action, entity
      return
    _clear: ->
      @stopListening @model
      @id = null
      @model = null
      @nameEl.textContent = ''
      @descEl.textContent = ''
      @btnSave.disabled = true
      @btnReset.disabled = true
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
          @load new Workflow(id: wf), sub
      else if wf.id
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
      @

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
          render = @render.bind @
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
      @

  class EditorView extends FormDialogView
    popup: (data, callback) ->
      throw new Error 'data must be an model entity' unless data instanceof Entity
      same = data is @data
      super data, callback
      @fill data.attributes unless same
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
      @addAction {context_type: matched[1]}, {scrollIntoView: true} if matched
      false
    fill: (attributes) ->
      # fill info form
      super attributes
      @fillActions attributes?.actions
      @
    save: ->
      @data.set 'actions', @readActions()
      console.log 'save node', @data
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
        @_too_many_alert.scrollIntoView()
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
        [ 'Label',
          location: 0.5
          label: 'new link'
          id: 'label'
          cssClass: 'link-label'
        ]
      ]
    gridDefaults:
      padding: 30
      spanX: 300
      spanY: 150
      vertical: false

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
        unless view.model.hasLink sourceId, targetId
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
        @_togglePopover target: label.canvas
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
      else
        console.error 'no node or link in data', $target, e
        return
      func?.call @, model
      return
    clear: ->
      # TODO: destroy all views of nodes and links
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
      {vertical, padding, spanX, spanY} = @gridDefaults

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
      #console.log wf.nodes
      unless wf.nodes.length and wf.nodes.at(0).has 'offset'
        @_sortNodeViews wf.nodes
      jsPlumb.ready =>
        wf.nodes.forEach @_addNode.bind @
        wf.links.forEach @_addLink.bind @
        return
      return
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
      else if node instanceof Node and node.id
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
          @model.createNode node if false isnt callback? node
          @model.trigger 'changed', 'create_node', node
        else # canceled
          console.log 'canceled or ignored create node', action
      @
    editNode: (node) ->
      return @ unless node?.id
      @nodeEditor.popup node, (action, node) =>
        if action is 'save'
          node.view.update node
          @model.trigger 'changed', 'edit_node', node
        else
          console.log 'canceled or ignored edit node', action
        # restore url to workflow only
        location.hash = @hash if action isnt 'ignored'
      # add node to url
      hash = "#{@hash}/node/#{node.id}"
      if location.hash.indexOf(hash) is -1
        @nodeEditor.$el.one 'shown', -> location.hash = hash
      @
    removeNode: (node) ->
      return @ unless node?.id
      # use confirm since no support for undo
      if confirm "Delete the node: #{node.get 'name'}?"
        #@model.nodes.remove node
        node.destroy()
        @model.trigger 'changed', 'remove_node', node
      @
    createLink: (from, to, callback) ->
      from = @model.nodes.get from unless from.id and from.has 'name'
      to = @model.nodes.get to unless to.id and to.has 'name'
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
          @model.trigger 'changed', 'edit_link', link
        else # canceled
          console.log 'canceled or ignored edit link', action
        # restore url to workflow only
        location.hash = @hash if action isnt 'ignored'
      # add link to url
      hash = "#{@hash}/link/#{link.id}"
      if location.hash.indexOf(hash) is -1
        @linkEditor.$el.one 'shown', -> location.hash = hash
      @
    removeLink: (link) ->
      return @ unless link?.id
      # use confirm since no support for undo
      if confirm "Delete the link: #{link.get('name') or '(No Name)'}?"
        link.destroy()
        @model.trigger 'changed', 'remove_link', link
      @
    render: ->
      @_bind()
      # TODO: make el drag scrollable
      # @$el.draggable axis: 'x'
      # chrome fix
      @el.onselectstart = -> false
      @

  class NodeView extends View
    tagName: 'div'
    className: 'node'
    sourceEndpointStyle:
      isSource: true
      uniqueEndpoint: true
      anchor: 'RightMiddle'
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
      maxConnections: -1
    targetEndpointStyle:
      isTarget: true
      anchor: ['LeftMiddle', 'BottomCenter']
      paintStyle:
        fillStyle: '#225588'
        radius: 5
      dropOptions:
        hoverClass: 'hover'
        activeClass: 'active'
    _popover_tpl: tpl('#t_popover')
    render: ->
      node = @el.node = @model
      @el.id = 'node_' + node.id
      @listenTo node, 'destroy', @remove.bind @
      @_renderModel node
      jsPlumb.draggable @$el, stack: '.node', stop: =>
        # set offset after drag
        $el_pos = $(node.view.el).position()
        offset =
          x: if $el_pos.left < 0 then 0 else $el_pos.left
          y: if $el_pos.top < 0 then 0 else $el_pos.top
        node.set 'offset', offset
        node.workflow?.trigger 'changed', 'move_node', node
      @parentEl.appendChild @el
      # build endpoints must after append el to dom
      param =
        parameters:
          node: node
          view: @
      @srcEndpoint = jsPlumb.addEndpoint @$el, @sourceEndpointStyle, param
      jsPlumb.makeTarget @$el, @targetEndpointStyle, param
      @
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
      @
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
      console.log 'remove', @conn, @model, @
      jsPlumb.detach @conn
      @_destroyPopover()
      super

  WorkflowFrameView
