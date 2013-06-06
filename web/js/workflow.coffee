"use strict"

define 'workflow', ['console', 'workflow_models', 'lib/jquery-ui', 'lib/jquery-jsplumb'],
({
async
find
findAll
View
BoxView
FrameView
InnerFrameView
ManagerView
ModalDialogView
FormDialogView
}, {
# Tenant
# SharedWorkflows
# TenantWorkflows
# Workflow
# SharedWorkflow
# TenantWorkflow
# SharedNodes
# TenantNodes
# Node
# SharedNode
# TenantNode
# SharedLinks
# TenantLinks
# Link
# SharedLink
# TenantLink
# SharedActions
# TenantActions
# Action
TenantWorkflows
TenantWorkflow
TenantNodes
TenantNode
Node
TenantLinks
TenantLink
}) ->

  ## Workflow Main Frame
  class WorkflowFrameView extends FrameView
    initialize: (options) ->
      super options
      @editor = new WorkflowEditorView el: '#workflow_editor', parent: @
      @manager = new WorkflowManagerView el: '#workflow_manager', parent: @
      @
    open: (name) ->
      switch name
        when 'new'
          console.log 'show workflow editor with create mode'
          @switchTo @manager
          @manager.create name
        when 'mgr'
          console.log 'show workflow mgr'
          @switchTo @manager
        else
          if name
            console.log 'show workflow editor for', name
            @switchTo @editor
            @editor.load name
          else unless @manager.rendered
            # 1st time default frame
            @switchTo @manager
      @

  ## Workflow Manager

  class WorkflowManagerView extends ManagerView
    collection: new TenantWorkflows
    initialize: (options) ->
      super options
      @creator = new WorkflowCreatorView el: '#workflow_creator', parent: @
      # temp
      @$el.find('#wf_template_list').on 'click', '.wf_tempate a', (e) =>
        e.preventDefault()
        wf = e.target.href.match /#workflow:(\w+)/
        console.log 'wf template clicked', wf
        @create wf[1] if wf.length is 2
        false
      @
    create: (template) ->
      template = '' if not template or /^(?:new|empty)$/i.test template
      @creator.popup template: template, (action, data) =>
        if action is 'save'
          console.log 'create new wf:', data
          @collection.create data
      @

  class WorkflowCreatorView extends FormDialogView
    el: '#workflow_creator'
    initialize: (options) ->
      super options
      @$el.on 'hidden', -> location.hash = '#workflow/mgr'
      @
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
    initialize: (options) ->
      super options
      @view = new WorkflowView
        el: find('#workflow_view', @el)
        nodeEditor: new NodeEditorView
        linkEditor: new LinkEditorView
      @nodeList = new NodeListView
        el: find('#node_list', @el)
        workflowView: @view
      @
    load: (id) ->
      @fetch id, (err, wf) =>
        if wf
          @view.load wf
        else
          @view.clear()
      #TODO: load node list
      @
    render: ->
      @nodeList.render()
      @
    fetch: (id, callback) ->
      wf = @workflow = new TenantWorkflow id: id
      wf.fetch success: =>
        async.parallel [
          (callback) ->
            wf.nodes.fetch success: ((c) ->
              callback null, c), error: ->
              callback 'fetch nodes failed'
          (callback) ->
            wf.links.fetch success: ((c) ->
              callback null, c), error: ->
              callback 'fetch links failed'
        ], (err) =>
          if err
            console.error err
            callback? err
          else
            console.log 'workflow', wf
            callback? null, wf
        return
      return
      @

  class NodeListView extends View
    initialize: (options) ->
      super options
      @workflowView = options.workflowView
      @el.onclick = (e) =>
        el = e.target
        if el.tagName is 'A' and el.dataset.node
          e.preventDefault()
          @workflowView.addNode el.dataset.node
          false
      return
    render: ->
      @el.innerHTML = ''
      items = document.createDocumentFragment()
      items.appendChild @renderHeader 'Common Nodes'
      items.appendChild @renderItem 'common', new Node id: 'new', name: 'Empty Node'
      #items.appendChild @renderHeader 'Used Nodes'
      items.appendChild @renderHeader 'Shared Nodes'
      @el.appendChild items
      @
    renderHeader: (text) ->
      li = document.createElement 'li'
      li.className = 'nav-header'
      li.innerHTML = text
      li
    renderItem: (listName, node) ->
      li = document.createElement 'li'
      a = document.createElement 'a'
      a.className = 'node'
      a.href = '#node:' + node.id
      a.dataset.list = listName
      a.dataset.node = node.id
      a.innerHTML = node.get 'name'
      li.appendChild a
      li

  class EditorView extends FormDialogView
    popup: (data, callback) ->
      throw 'data must be an model entity' unless data instanceof Entity
      # already set @data = data
      super data, callback
      @fill data.attributes
      @
    save: ->
      @data.set @read()
      @callback 'save'
      @hide true
      @
    reset: -> # called after close
      super()
      @form.reset()
      @

  class NodeEditorView extends EditorView
    el: '#node_editor'
    events:
      'click a.action-thumb': '_addAction'
    initialize: (options) ->
      super options
      @actions = []
      @actionViews = []
      @actionsEl = find '#actions', @el
      _fixStyle = @_fixStyle.bind @
      $(window).resize _fixStyle
      $(@el).on 'shown', _fixStyle
      $(@actionsEl).sortable
        delay: 150
        distance: 15
      @
    _fixStyle: -> # make sure the top of action box will below the title, name and desc
      @actionsEl.style.top = 20 + $(@form).height() + 'px'
      return
    _addAction: (e) ->
      e.preventDefault()
      target = e.target
      matched = target.href.match /action:(\w+)/i
      @addAction type: matched[1] if matched
      false
    fill: (data) ->
      # fill info form
      super data
      # temp TODO: load models from node
      [
        type: 'post_to_multi_social_media'
      ,
        type: 'send_email'
      ].forEach @addAction.bind @
      @
    save: ->
      console.log 'save'
      # save actions
      # TODO: read all actions and check
      # save the node
      super()
      @
    reset: ->
      super()
      @actions = []
      @actionViews = []
      @actionsEl.innerHTML = ''
      @
    addAction: (model) ->
      actionView = new ActionView model: model, parent: @, container: @actionsEl
      actionView.on 'close', @removeAction.bind @
      actionView.render()
      @actions.push model
      @actionViews.push actionView
      actionView
    removeAction: (view, model) ->
      idx = @actions.indexOf model
      return if idx < 0
      @actions.splice idx, 1
      @actionViews.splice idx, 1
      @

  class ActionView extends BoxView
    @tpl: (type) -> # load form html template
      if @_tpl? and @_tpl[type]?
        @_tpl[type] # cached
      else
        el = find "#t_#{type}"
        if el?
          @_tpl ?= ({})
          # load template
          tpl = @_tpl[type] = el.innerHTML
          # remove template from dom
          el.parentNode.removeChild el
        else
          throw 'cannot find template for type: ' + type
        tpl
    className: 'box action'
    initialize: (options) ->
      super options
      @containerEl = options.container
      @model = options.model
      @type = options.model.type or options.type
      throw 'need action model and type' unless @model and @type
      @
    close: ->
      @el.parentNode.removeChild @el
      model = @model
      @trigger 'close', @, model
      model.type = null
      model.name = null
      model.data = null
      @
    render: ->
      tpl = @constructor.tpl @type
      @containerEl.appendChild @el
      @el.innerHTML = tpl
      # get els in super
      super()
      if /webkit/i.test navigator.userAgent
        $(@el).disableSelection()
      else
        $('.box-header, .btn', @el).disableSelection()
      @form = find 'form', @el
      @fill @model?.data
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
      data ?= ({})
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
          location: 1
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
      'click .popover .btn-delete': '_delete'
      'click .popover .btn-edit': '_edit'
      'dblclick .node': '_edit'
    initialize: (options) ->
      super options
      @nodeEditor = options.nodeEditor
      @linkEditor = options.linkEditor
      jsPlumb.importDefaults @jsPlumbDefaults
      @render()
      @
    _bind: ->
      view = @
      # link label
      #  jsPlumb.bind 'beforeDrop', (info) ->
      #    uuid = info.sourceId + '-' + info.targetId
      #    newlink = data.links.index[uuid]
      #    oldLink = info.connection.getParameter 'link'
      #    if newlink?
      #      console.log 'link exists', uuid
      #      # alert 'link exists' if oldLink?
      #      false # cancel if link exists
      #    else
      #      deleteLink oldLink if oldLink?
      #      true
      jsPlumb.bind 'jsPlumbConnection', (info) ->
        conn = info.connection
        link = conn.getParameter 'link'
        label = conn.getOverlay 'label'
        if not link?
          # TODO: create link
          # conn.setParameter 'link', createLink info.sourceId, info.targetId
        else if link.has 'title'
          label.setLabel link.get 'title'
        return
      jsPlumb.bind 'jsPlumbConnectionDetached', (info) ->
        # this will be called after link view destory
        conn = info.connection
        link = conn.getParameter 'link'
        # rebuild src end point for auto delele issue
        link.prevNode.view.buildSrcEndpoint()
        return

      # link dblclick to edit
      jsPlumb.bind 'dblclick', (conn) ->
        view.editLink conn.getParameter 'link'
        return

      # for popover
      _hidePopover = -> if view._popped
        if view._popped._delay
          view._popped._delay = clearTimeout view._popped._delay
        else
          $(view._popped).popover 'hide'
        return
      _togglePopover = ->
        if view._popped isnt @ and not @_hidding
          @._delay = setTimeout =>
            $(@).popover 'show' if view._popped = @
            @._delay = null
          , 100
          view._popped = @
        else
          _hidePopover()
          view._popped = null
        return
      # link click
      jsPlumb.bind 'click', (conn) ->
        label = conn.getOverlay 'label'
        _togglePopover.call label.canvas
        return
      # node click
      @$el.on 'click', '.node', _togglePopover
      @$el.on 'mousedown', (e) ->
        if view._popped and not view.$el.find('.popover').has(e.target).length
          _hidePopover()
          org_popped = view._popped
          view._popped = null
          org_popped._hidding = true
          setTimeout ->
            delete org_popped._hidding
          , 300
        return
      return
    clear: ->
      @el.innerHTML = ''
      # TODO: destory all nodes and links
      @
    load: (wf) ->
      # clear
      @clear()
      @_processModel wf
      @_renderModel wf
      @
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
    _delete: (e) ->
      @_action 'remove', e
      return
    _edit: (e) ->
      @_action 'edit', e
      return
    _processModel: (wf) ->
      @model = wf
      # pre-process nodes and links
      wf.nodes.forEach (node) ->
        node.workflow = wf
        node.inLinks = []
        node.outLinks = []
        return
      wf.links.forEach (link) ->
        link.workflow = wf
        link.prevNode = wf.nodes.get link.get 'prevNodeId'
        link.nextNode = wf.nodes.get link.get 'nextNodeId'
        unless link.prevNode and link.nextNode
          console.error 'link', link.name or link.id, 'is broken, prev/next node missing'
        link.prevNode.outLinks.push link
        link.nextNode.inLinks.push link
        return
      nodes = wf.nodes
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
        return
      @_sortNodeViews nodes
      # TODO: workflow validation
      return
    _sortNodeViews: (nodes) ->
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
      throw 'workflow not loaded' unless wf?
      # must bind before render nodes/links
      @_bind()
      # build nodes
      wf.nodes.forEach (node) =>
        @_addNode node
        return
      # build links
      wf.links.forEach (link) =>
        @_addLink link
        return
      return
    addNode: (node = 'emtpy') ->
      if node is 'new' or node is 'empty'
        console.log 'add a empty node'
        return @createNode()
      if typeof node is 'string'
        console.log 'add node by id', node
        # TODO: add node by id
      else unless node instanceof Node
        console.error 'add a invalid node', node
        throw 'add a invalid node'
      console.log 'add node', node
      @_addNode node
      @
    _addNode: (node) ->
      view = node.view = new NodeView model: node, parent: @
      view.render()
      @el.appendChild view.el
      return
    createNode: ->
      @nodeEditor.popup (new TenantNode), (action, node) =>
        if action is 'save'
          @_addNode node
        else # canceled
          console.log 'canceled create node'
      @
    editNode: (node) ->
      @nodeEditor.popup node, (action, node) =>
        if action is 'save'
          node.view.update node
          console.log 'saved node', node
        else # canceled
          console.log 'canceled edit node'
      @
    removeNode: (node) ->
      # use confirm since no support for undo
      if confirm "Delete the node: #{node.get 'title'}?"
        console.log 'remove node', node
        node.view?.distory()
      # TODO: remove view and model
      @
    addLink: (link) ->
      # TODO: add view and model
      @
    _addLink: (link) ->
      view = link.view = new LinkView model: link, parent: @
      view.render()
      return
    editLink: (link) ->
      @linkEditor.popup link, (action, link) =>
        if action is 'save'
          console.log 'saved link', link
        else # canceled
          console.log 'canceled edit link'
      @
    removeLink: (link) ->
      # use confirm since no support for undo
      if confirm "Delete the link: #{link.get('title') or '(No Name)'}?"
        console.log 'remove node', link
        link.view?.distory()
      # TODO: remove view and model
      @
    render: ->
      @el.onselectstart = ->
        false
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
    _popover_tpl: do ->
      el = find '#t_popover'
      throw 'cannot find template for popover #t_popover' unless el?
      html = el.innerHTML
      el.parentNode.removeChild el
      html
    render: ->
      node = @el.node = @model
      name = @el.id = node.get 'name'
      @_renderModel node
      jsPlumb.draggable @$el
      @parentEl.appendChild @el
      # build endpoints must after append el to dom
      @buildSrcEndpoint()
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
      title = @el.title = node.get 'title'
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
    buildSrcEndpoint: ->
      jsPlumb.deleteEndpoint @srcEndpoint if @srcEndpoint?
      @srcEndpoint = jsPlumb.addEndpoint @el, @sourceEndpointStyle, parameters:
        model: @model
        view: @
      @
    distory: ->
      # TODO: destory view and model
      @$el.popover 'destroy'
      @trigger 'distory', @, @model
      return

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
      @label = conn.getOverlay 'label'
      @labelEl = @label.canvas
      title = link.get 'title'
      label$el = $(@labelEl)
      # _desc = "#{link.prevNode.get 'title'} to #{link.nextNode.get 'title'}"
      if title
        @labelEl.title = 'Link: ' + title
      else
        label$el.css 'visibility', 'hidden'
        @labelEl.title = 'Link'
      label$el.popover
        container: @parentEl
        trigger: 'manual'
        placement: 'bottom'
        html: true
        content: @_popover_tpl.replace '{desc}', link.get('desc') or ''
      @$popover = label$el.data('popover').tip()
      @$popover?.addClass('target').data link: link, view: @
      @
    distory: ->
      jsPlumb.detach @conn
      # TODO: destory model
      $(@labelEl).popover 'destroy'
      @trigger 'distory', @, @model
      return

  WorkflowFrameView
