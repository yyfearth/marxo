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

# backbone & bootstrap

define 'workflow', ['console', 'workflow_models', 'lib/jquery-ui', 'lib/jquery-jsplumb'],
({
 async
 find
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
      @view = new WorkflowView el: find '#workflow_view', @el
      return

  class WorkflowView extends Backbone.View
    jsPlumbDefaults:
      DragOptions:
        zIndex: 2000
      Endpoint: ['Dot', radius: 3]
      ConnectionsDetachable: true
      ReattachConnections: true
      HoverPaintStyle:
        strokeStyle: '#42a62c'
        lineWidth: 2
        zIndex: 2000
      ConnectionOverlays: [
        [ 'Arrow',
          location: 1
          id: 'arrow'
        ]
        [ 'Label',
          location: 0.5
          label: 'new link'
          id: 'label'
          cssClass: 'aLabel'
        ]
      ]
    gridDefaults:
      padding: 15
      spanX: 300
      spanY: 150
      vertical: false
    initialize: ->
      jsPlumb.importDefaults @jsPlumbDefaults
      @_loadModel()
      @_bind()
      return
    _bind: ->
      # link label
      jsPlumb.bind 'jsPlumbConnection', (info) ->
        conn = info.connection
        link = conn.getParameter 'link'
        label = conn.getOverlay 'label'
        unless link?
          # conn.setParameter 'link', createLink info.sourceId, info.targetId
          label.hide()
        else if link.has 'title'
          label.setLabel link.get 'title'
        else
          label.hide()
        return
      jsPlumb.bind 'jsPlumbConnectionDetached', (info) ->
        conn = info.connection
        link = conn.getParameter 'link'
        # deleteLink info.connection.getParameter 'link'
        link.prevNode.view.buildSrcEndpoint()
        return
      return
    _loadModel: (callback = @render) ->
      wf = @model = new TenantWorkflow id: '51447afb4728cb2036cf9ca1'
      wf.fetch success: =>
        async.parallel [
          (callback) -> wf.nodes.fetch success: ((c) -> callback null, c), error: -> callback 'fetch nodes failed'
          (callback) -> wf.links.fetch success: ((c) -> callback null, c), error: -> callback 'fetch links failed'
        ], (err) =>
          if err
            console.error err
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
          callback.call @, wf
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
    render: ->
      console.log 'render wf'
      @el.onselectstart = -> false
      wf = @model
      throw 'workflow not loaded' unless wf?
      # build nodes
      wf.nodes.forEach (node) =>
        view = node.view = new NodeView model: node, parent: @
        view.render()
        @el.appendChild view.el
        return
      # build links
      wf.links.forEach (link) =>
        # view = link.view = new LinkView model: link
        # link.view = view
        jsPlumb.connect
          source: link.prevNode.view.srcEndpoint
          target: link.nextNode.view.el
          parameters:
            link: link
        return
      @

  class NodeView extends Backbone.View
    tagName: 'div'
    className: 'node'
    sourceEndpointStyle:
      isSource: true
      uniqueEndpoint: true
      anchor: 'RightMiddle'
      paintStyle:
        fillStyle: '#225588'
        radius: 9
      connector: [
        'Flowchart'
        stub: [40, 60]
        gap: 10
      ]
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
    initialize: (options) ->
      @parent = options.parent
      @parentEl = @parent.el
      return
    render: ->
      console.log 'render node'
      name = @el.id = @model.get 'name'
      @el.innerHTML = @model.escape 'title'
      @el.style.left = @model.x + 'px'
      @el.style.top = @model.y + 'px'
      jsPlumb.draggable @$el
      @parentEl.appendChild @el
      # build endpoints must after append el to dom
      @buildSrcEndpoint()
      jsPlumb.makeTarget @$el, @targetEndpointStyle, parameters:
        node: @model
        view: @
      @
    buildSrcEndpoint: ->
      jsPlumb.deleteEndpoint @srcEndpoint if @srcEndpoint?
      @srcEndpoint = jsPlumb.addEndpoint @el, @sourceEndpointStyle, parameters:
        model: @model
        view: @


  #  class LinkView extends Backbone.View

  WorkflowFrameView

