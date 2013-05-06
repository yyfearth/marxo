"use strict"
#createLink = (sourceId, targetId) ->
#  uuid = sourceId + '-' + targetId
#  console.log 'create link', uuid
#  fromNode = data.nodes.index[sourceId]
#  toNode = data.nodes.index[targetId]
#  # create link
#  link = {uuid, fromNode, toNode}
#  # id:
#  # from:
#  # to:
#  data.links.push link
#  fromNode.toLinks.push link
#  toNode.fromLinks.push link
#  data.links.index[uuid] = link
#  link
#
#deleteLink = (link) ->
#  console.log 'delete link', link.uuid
#  delete data.links.index[link.id]
#  delete data.links.index[link.uuid]
#  removeFromArray data.links, link
#  removeFromArray link.fromNode.toLinks, link
#  removeFromArray link.toNode.fromLinks, link
#  return
#
#removeFromArray = (array, item) ->
#  idx = array.indexOf item
#  array.splice idx, 1 if idx isnt -1
#
#
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

#  jsPlumb.bind 'dblclick', (conn)->
#    if confirm('Delete connection from ' + conn.sourceId + ' to ' + conn.targetId + '?')
#      jsPlumb.detach conn
#    return

define 'workflow', ['console', 'workflow_models', 'lib/jquery-ui', 'lib/jquery-jsplumb'],
({
async
find
View
FrameView
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
TenantLinks
TenantLink
}) ->
  class WorkflowFrameView extends FrameView
    initialize: (options) ->
      super options
      @view = new WorkflowView
        parent: @
        el: find('#workflow_view', @el)
        nodeEditor: new NodeEditor
          parent: @, el: find('#node_editor', @el)
        linkEditor: new LinkEditor
          parent: @, el: find('#link_editor', @el)
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
        @view.load name
      # '51447afb4728cb2036cf9ca1'
      # else show the activeInnerFrame
      return

  # TODO: move to console
  class ModalDialogView extends View
    initialize: (options) ->
      super options
      @$el.modal
        show: false
        backdrop: 'static'
      return
    show: (show = true) ->
      @$el.modal if show then 'show' else 'hide'
      @
    hide: (hide = true) ->
      @show not hide

  class NodeEditor extends ModalDialogView
    initialize: (options) ->
      super options
      return

  class LinkEditor extends ModalDialogView
    initialize: (options) ->
      super options
      return


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
      # link label
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
      wf = @
      _hidePopover = -> if wf._popped
        if wf._popped._delay
          wf._popped._delay = clearTimeout wf._popped._delay
        else
          $(wf._popped).popover 'hide'
        return
      _togglePopover = ->
        if wf._popped isnt @ and not @_hidding
          @._delay = setTimeout =>
            $(@).popover 'show' if wf._popped = @
            @._delay = null
          , 100
          wf._popped = @
        else
          _hidePopover()
          wf._popped = null
        return
      # link click
      jsPlumb.bind 'click', (e) ->
        label = e.getOverlay 'label'
        _togglePopover.call label.canvas
        return
      # link dblclick
      jsPlumb.bind 'dblclick', (e) ->
        console.log 'show link'
        wf.linkEditor.show()
        return
      # node click
      @$el.on 'click', '.node', _togglePopover
      @$el.on 'dblclick', '.node', (e) ->
        console.log 'show node'
        wf.nodeEditor.show()
        return
      @$el.on 'mousedown', (e) ->
        if wf._popped and not wf.$el.find('.popover').has(e.target).length
          _hidePopover()
          org_popped = wf._popped
          wf._popped = null
          org_popped._hidding = true
          setTimeout ->
            delete org_popped._hidding
          , 300
        return
      return
    load: (id, callback) ->
      # clear
      @el.innerHTML = ''
      @_loadModel id, (err, wf) =>
        if err or not wf
          # TODO: show error
          return
        @_renderModel wf
        callback? wf
        return
      @
    _loadModel: (id, callback) ->
      wf = @model = new TenantWorkflow id: id
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
            callback err
            return
          console.log 'workflow', wf
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
          callback null, wf
        return
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
        view = node.view = new NodeView model: node, parent: @
        view.render()
        @el.appendChild view.el
        return
      # build links
      wf.links.forEach (link) =>
        view = link.view = new LinkView model: link, parent: @
        view.render()
      return
    addNode: (node) ->
      @
    removeNode: (node) ->
      @
    addLink: (link) ->
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
      node = @model
      name = @el.id = node.get 'name'
      @el.innerHTML = node.escape 'title'
      @el.title = node.get 'title'
      @el.style.left = node.x + 'px'
      @el.style.top = node.y + 'px'
      jsPlumb.draggable @$el
      @parentEl.appendChild @el
      # build endpoints must after append el to dom
      @buildSrcEndpoint()
      jsPlumb.makeTarget @$el, @targetEndpointStyle, parameters:
        node: node
        view: @
      @$el.popover
        container: @parentEl
        trigger: 'manual'
        placement: 'bottom'
      @
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
