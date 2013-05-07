"use strict"

define 'workflow', ['console', 'workflow_models', 'lib/jquery-ui', 'lib/jquery-jsplumb'],
({
async
find
findAll
View
FrameView
InnerFrameView
ModalDialogView
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
  class WorkflowFrameView extends FrameView
  #initialize: (options) ->
  #  super options
  #  return
    _initInnerFrames: -> # override default
      @editor = new WorkflowEditorView el: '#workflow_editor', parent: @el
      @manager = new WorkflowManagerView el: '#workflow_manager', parent: @el
      @innerFrames =
        editor: @editor
        manager: @manager
      return
    open: (name) ->
      if name is 'new'
        console.log 'show workflow editor with create mode'
        @show 'manager'
        # DOTO: show create new model
      else if name is 'mgr'
        console.log 'show workflow mgr'
        @show 'manager'
      else if name
        console.log 'show workflow editor for', name
        @show 'editor'
        @editor.load name
      return

  class WorkflowEditorView extends InnerFrameView
    initialize: (options) ->
      super options
      @view = new WorkflowView
        parent: @
        el: find('#workflow_view', @el)
        nodeEditor: new NodeEditorView
          parent: @
        linkEditor: new LinkEditorView
          parent: @
      @nodeList = new NodeListView
        parent: @, el: find('#node_list', @el)
        workflowView: @view
      return
    load: (id) ->
      @fetch id, (err, wf) =>
        if wf
          @view.load wf
        else
          @view.clear()
      #TODO: load node list
      return
    render: ->
      @nodeList.render()
      return
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
      return

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

  class WorkflowManagerView extends InnerFrameView

  class EditorView extends ModalDialogView
    initialize: (options) ->
      super options
      @form = find 'form', @el
      return
    popup: (data, callback) ->
      super data, callback
      @fill data
      @
    fill: (data) ->
      @_attributes = ({})
      for name, value of data.attributes
        input = @form[name]
        if input?.name is name and input.value?
          input.value = value
          @_attributes[name] = value
      @
    save: -> # should be customized, e.g. ok, save, export
      if @_attributes?
        for name, value of @_attributes
          input = @form[name]
          @_attributes[name] = input.value if input.value isnt value
          @data.set @_attributes
      @callback 'save'
      @hide true

  class NodeEditorView extends EditorView
    el: '#node_editor'
    initialize: (options) ->
      super options
      @actionsEl = find '#actions', @el
      _fixStyle = @_fixStyle.bind @
      $(window).resize _fixStyle
      $(@el).on 'shown', _fixStyle
      return
    _fixStyle: ->
      @actionsEl.style.top = 20 + $(@form).height() + 'px'
      return

  class LinkEditorView extends EditorView
    el: '#link_editor'
    initialize: (options) ->
      super options
      return
    popup: (data, callback) ->
      super data, callback

      @


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
    initialize: (options) ->
      super options
      @nodeEditor = options.nodeEditor
      @linkEditor = options.linkEditor
      jsPlumb.importDefaults @jsPlumbDefaults
      @render()
      return
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
          # conn.setParameter 'link', createLink info.sourceId, info.targetId
        else if link.has 'title'
          label.setLabel link.get 'title'
        return
      jsPlumb.bind 'jsPlumbConnectionDetached', (info) ->
        conn = info.connection
        link = conn.getParameter 'link'
        # deleteLink info.connection.getParameter 'link'
        link.prevNode.view.buildSrcEndpoint()
        return
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
      # link dblclick
      jsPlumb.bind 'dblclick', (conn) ->
        view.editLink conn.getParameter 'link'
        return
      # node click
      @$el.on 'click', '.node', _togglePopover
      # node dblclick
      @$el.on 'dblclick', '.node', ->
        view.editNode @node
        return
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
      @
    load: (wf) ->
      # clear
      @clear()
      @_processModel wf
      @_renderModel wf
      @
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
      @
    addLink: (link) ->
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
      @$el.popover
        container: @parentEl
        trigger: 'manual'
        placement: 'bottom'
        title: title
      return
    buildSrcEndpoint: ->
      jsPlumb.deleteEndpoint @srcEndpoint if @srcEndpoint?
      @srcEndpoint = jsPlumb.addEndpoint @el, @sourceEndpointStyle, parameters:
        model: @model
        view: @
      @

  class LinkView extends View
    render: ->
      link = @model
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
      @

  WorkflowFrameView
