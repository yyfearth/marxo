"use strict"

define 'workflow', [
  'base', 'manager', 'models'
  'lib/jquery-jsplumb', 'lib/jquery-ui'
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
}, jsPlumb) ->

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
          throw 'open workflow with a name or id is needed' unless name
          console.log 'show workflow editor for', name
          @switchTo @editor
          @editor.load name, sub
      @

  ## Workflow Manager

  class WorkflowManagerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'title:workflow'
      'desc'
      'type'
      'status'
      'created_at'
      'updated_at'
      'actions:workflow'
    ]
    collection: new Workflows
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
    create: (template) ->
      template = '' if not template or /^(?:new|empty)$/i.test template
      @creator.popup template: template, (action, data) =>
        if action is 'save'
          console.log 'create new wf:', data
          @collection.create data, wait: true
          # location.hash = '#workflow/' + data.id
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
    initialize: (options) ->
      super options
    popup: (data, callback) ->
      super data, callback
      @fill data
      @
    save: ->
      @data = @read()
      @callback 'save'
      @hide true
      @

  ## Workflow Editor (Workflow/Node/Link/Action Editor)

  class WorkflowEditorView extends InnerFrameView
    events:
      'click .wf-save': 'save'
      'click .wf-reset': 'reset'
      'click #workflow_header': ->
        @renamer.popup @model, (action, wf) =>
          if action is 'save'
            console.log 'save title', wf
            @titleEl.textContent = wf.get 'title'
            @descEl.textContent = wf.get 'desc'
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
        el: find('#workflow_title_editor', @el)

      @btnSave = find '.wf-save', @el
      @titleEl = find '.editable-title', @el
      @descEl = find '.editable-desc', @el

      @listenTo @nodeList, 'select', (id, node) =>
        console.log 'select from node list', id, node
        if id is 'new'
          node = null
        else if id
          node ?= @model.nodes.get id
        @view.createNode node
      @
    reset: ->
      @reload() if confirm 'All changes will be descarded since last save, are you sure to do that?'
      @
    save: ->
      #if @model?.hasChanged()
      console.log 'save', @model.attributes
      @model.nodes.forEach (node) ->
        node.view.el.style.zIndex = ''
        style = node.view.el.getAttribute 'style'
        if style isnt node.get 'style'
          node.set 'style', style
          #node.save()
          console.log 'node style', node.id, style
      @model.save {},
        success: (wf) ->
          console.log 'saved', wf
        error: ->
          console.error 'save failed'
      @
    load: (wf, sub) ->
      _load = (wf) =>
        @view.load wf, sub
        @nodeList.setNodes wf.nodes
        return
      if not wf
        @id = null
        @model = null
        @titleEl.textContent = ''
        @descEl.textContent = ''
        @view.clear()
      else if typeof wf is 'string'
        if @id is wf and not sub?.reload
          @load @model, sub
        else
          @fetch wf, (err, wf) => @load wf, sub
      else if wf.id
        if @id is wf.id
          _load wf
        else
          @id = wf.id
          @titleEl.textContent = wf.get 'title'
          @descEl.textContent = wf.get 'desc'
          @model = wf
          if wf.loaded()
            _load wf
          else
            wf.fetch success: _load
      else
        throw 'load neigher workflow id string nor workflow object'
      @
    reload: ->
      @load @id, reload: true
    render: ->
      @nodeList.render()
      @
    fetch: (id, callback) ->
      wf = @workflow = new Workflow id: id
      wf.fetch success: -> callback null, wf
      @

  class NodeListView extends NavListView
    urlRoot: 'node'
    headerTitle: 'Common Nodes'
    defaultItem: new Node id: 'new', title: 'Empty Node'
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
      throw 'data must be an model entity' unless data instanceof Entity
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
    el: '#node_editor'
    events:
      'click a.action-thumb': '_addAction'
      'shown': '_fixStyle'
    _too_many_actions_limit: 7
    initialize: (options) ->
      super options
      @actionsEl = find '#actions', @el
      @_fixStyle = @_fixStyle.bind @
      $(window).on 'resize', @_fixStyle
      $(@actionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content'
      @_too_many_alert = find '#too_many_actions_alert', @el
      @on 'actions_update', @_checkActionLimit.bind @
      @
    remove: ->
      $(window).off 'resize', @_fixStyle
      super
    _fixStyle: -> # make sure the top of action box will below the title, name and desc
      @actionsEl.style.top = 20 + $(@form).height() + 'px'
      return
    _addAction: (e) ->
      e.preventDefault()
      target = e.target
      matched = target.href.match /action:(\w+)/i
      @addAction type: matched[1] if matched
      false
    fill: (attributes) ->
      # fill info form
      super attributes
      @clearActions()
      @actions = new Actions attributes.actions or []
      @actions.forEach @addAction.bind @
      @
    save: ->
      console.log 'save'
      # save actions
      actions = findAll('.action', @actionsEl).map (el) ->
        action = $(el).data 'model'
        # TODO: validate each action
        throw 'cannot get action from action.$el' unless action
        action.attributes
      @data.set 'actions', actions
      console.log 'save actions', actions, @data
      # save the node
      super
      @
    reset: ->
      super
      @clearActions()
    clearActions: ->
      @actions?.forEach (model) -> model.view?.remove()
      @actions = null
      $(findAll('.action', @actionsEl)).remove()
      @
    viewAction: (id) ->
      console.log 'view action id:', id
      el = @actionsEl.querySelector '#action_' + id
      if el?
        console.log 'dataset5', @el.dataset['aria-hidden']
        hidden = @el.getAttribute 'aria-hidden'
        if hidden is 'true'
          @$el.one 'shown', -> el.scrollIntoView()
        else if hidden is 'false'
          el.scrollIntoView()
        else
          setTimeout ->
            el.scrollIntoView()
          , 600
      el
    addAction: (model) ->
      model = new Action model unless model instanceof Action
      actionView = new ActionView model: model, parent: @, container: @actionsEl
      @listenTo actionView, 'remove', @removeAction.bind @
      actionView.render()
      actionView.el.scrollIntoView()
      @delayedTrigger 'actions_update', 100
      #@actions.add actionView
      @
    removeAction: (view) ->
      #@actions.remove view
      @delayedTrigger 'actions_update', 100
      @
    _checkActionLimit: ->
      cls = @_too_many_alert.classList
      count = findAll('.action', @actionsEl).length # @actions.length
      if count > @_too_many_actions_limit
        cls.add 'active'
        @_too_many_alert.scrollIntoView()
      else
        cls.remove 'active'

  class ActionView extends BoxView
    className: 'box action'
    _tpl: tplAll '#actions_tpl'
    initialize: (options) ->
      super options
      @containerEl = options.container
      @model = options.model
      @model.view = @
      @type = @model.get?('type') or options.model.type or options.type
      throw 'need action model and type' unless @model and @type
      @
    remove: ->
      model = @model
      model.type = null
      model.name = null
      model.data = null
      super
    render: ->
      @el.innerHTML = @_tpl[@type]
      @el.id = 'action_' + @model.id or 'no_id'
      #@containerEl.appendChild @el
      @containerEl.insertBefore @el, find '.alert', @containerEl
      # get els in super
      super
      if /webkit/i.test navigator.userAgent
        $(@el).disableSelection()
      else
        $('.box-header, .btn', @el).disableSelection()
      @form = find 'form', @el
      @fill @model?.data
      @$el.data model: @model, view: @
      @listenTo @model, 'destroy', @remove.bind @
      @
    fill: (data) -> # filling the form with data
      return unless data and @form
      form = @form
      for name, value of data
        el = form[name]
        #form[key].value = value
        $(el).val value if el?.getAttribute('name') is name
      # TODO: support customized controls
      @
    read: (data) -> # read form the form to get a json data
      throw 'cannot find the form, may not rendered yet' unless @form
      data ?= {}
      els = [].slice.call @form.elements
      els.forEach (el) ->
        $el = $ el
        name = $el.attr 'name'
        data[name] = $el.val() if name
      # TODO: support customized controls
      data

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
            # set style after createNode since it will remove style when clone
            @createNode node, (node) =>
              $el_offset = @$el.offset()
              x = ui.offset.left - $el_offset.left
              y = ui.offset.top - $el_offset.top
              node.set 'style', "left:#{if x < 0 then 0 else x}px;top:#{if y < 0 then 0 else y}px"
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
      throw 'cannot open a action without given a node' if action and not node
      throw 'node and link cannot be open together' if link and node

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
    _renderModel: (wf) ->
      console.log 'render wf', wf
      wf = @model
      throw 'workflow not loaded' unless wf?
      #console.log wf.nodes
      wf.nodes.forEach @_addNode.bind @
      wf.links.forEach @_addLink.bind @
      return
    addNode: (node = 'emtpy') ->
      if node is 'new' or node is 'empty'
        console.log 'add a empty node'
        return @createNode()
      else unless node instanceof Node
        console.error 'add a invalid node', node
        throw 'add a invalid node'
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
      if not node or node.id is 'new'
        node = new Node
      else if node instanceof Node and node.id
        node = node.clone()
        title = node.get 'title'
        desc = node.get 'desc'
        node.set
          template_id: node.id
          name: node.get('name') + '_clone'
          title: title + ' (Clone)'
          desc: if desc then desc + ' (Clone)' else null
        node.unset 'style'
        node.unset 'id'
      else if node.name
        node = new Node node
      else
        console.error 'invalid node to create', node
        return
      @nodeEditor.popup node, (action, node) =>
        if action is 'save'
          # prevent create if callback return false
          @model.createNode node if false isnt callback? node
        else # canceled
          console.log 'canceled or ignored create node', action
      @
    editNode: (node) ->
      return @ unless node?.id
      @nodeEditor.popup node, (action, node) =>
        if action is 'save'
          node.view.update node
          console.log 'saved node', node
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
      if confirm "Delete the node: #{node.get 'title'}?"
        console.log 'remove node', node
        #@model.nodes.remove node
        node.destroy()
      @
    createLink: (from, to, callback) ->
      from = @model.nodes.get from unless from.id and from.has 'name'
      to = @model.nodes.get to unless to.id and to.has 'name'
      name = "#{from.get 'name'}_to_#{to.get 'name'}"
      data = new Link
        name: name[0..32].toLowerCase()
        prev_node_id: from.id
        next_node_id: to.id
      @linkEditor.popup data, (action, link) =>
        if action is 'save'
          #@model.links.add link
          @model.createLink link if false isnt callback? link
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
          console.log 'saved link', link
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
      if confirm "Delete the link: #{link.get('title') or '(No Name)'}?"
        console.log 'remove node', link
        link.destroy()
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
      jsPlumb.draggable @$el, stack: '.node'
      @parentEl.appendChild @el
      # build endpoints must after append el to dom
      @srcEndpoint ?= jsPlumb.addEndpoint @el, @sourceEndpointStyle, parameters:
        model: @model
        view: @
      jsPlumb.makeTarget @$el, @targetEndpointStyle, parameters:
        node: node
        view: @
      @
    update: (node = @model) ->
      @$el.popover 'destroy'
      @_renderModel node
      jsPlumb.repaint @el.id
      @
    _renderModel: (node = @model) ->
      @el.innerHTML = node.escape 'title'
      title = node.get 'title'
      if node.has 'style'
        @el.setAttribute 'style', node.get 'style'
      else
        @el.style.left = node.x + 'px'
        @el.style.top = node.y + 'px'
      @$el.addClass('target').data node: node, view: @
      @$el.popover
        container: @parentEl
        trigger: 'manual'
        placement: 'bottom'
        title: title
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
      # _desc = "#{link.prevNode.get 'title'} to #{link.nextNode.get 'title'}"
      title = link.get 'title'
      @label.setLabel title or ''
      label$el.css 'visibility', if title then 'visible' else 'hidden'
      label$el.popover
        container: @parentEl
        title: title or 'Link'
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
