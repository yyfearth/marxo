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
#do procData = ->
#  nodeIndex = data.nodes.index = {}

#  data.links.forEach (link) ->
#    # link from node
#    link.fromNode = nodeIndex[link.from]
#    link.fromNode.toLinks.push link
#    # link to node
#    link.toNode = nodeIndex[link.to]
#    link.toNode.fromLinks.push link
#    # creat uuid
#    link.uuid = link.fromNode.uuid + '-' + link.toNode.uuid
#    # add to index
#    linkIndex[link.id] = linkIndex[link.uuid] = link
#    return

#  grid = window.grid = [startNodes.concat(lonelyNodes)]
#  # vertical
#  grid.spanX = 350
#  grid.spanY = 150
#  grid.vertical = false
#
#  do traval = (level = 0) ->
#    nextLevel = []
#    grid[level]?.forEach (node, i) ->
#      node.gridX = i
#      node.gridY = level
#      if grid.vertical
#        node.x = i * grid.spanX
#        node.y = level * grid.spanY
#      else
#        node.x = level * grid.spanX
#        node.y = i * grid.spanY
#      node.toLinks?.forEach (link) ->
#        nextLevel.push link.toNode unless link.toNode.x?
#      return
#    if nextLevel.length
#      grid[level + 1] = nextLevel
#      traval level + 1
#    return
#  return
## end of proc data

#  jsPlumb.bind 'jsPlumbConnectionDetached', (info) ->
#    deleteLink info.connection.getParameter 'link'
#    node = data.nodes.index[info.sourceId]
#    jsPlumb.deleteEndpoint node.srcEndpoint
#    node.srcEndpoint = jsPlumb.addEndpoint node.el, sourceEndpoint, parameters:
#      node: node
#    return
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
      Endpoint: ['Dot', radius: 3]
      ConnectionsDetachable: true
      ReattachConnections: true
      HoverPaintStyle:
        strokeStyle: '#42a62c'
        lineWidth: 2
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
    initialize: ->
      jsPlumb.importDefaults @jsPlumbDefaults
      @_loadModel()
      @_bind()
      return
    _bind: ->
      # link label
      jsPlumb.bind 'jsPlumbConnection', (info) ->
        conn = info.connection
        link = conn.getParameter 'model'
        label = conn.getOverlay 'label'
        if not link?
          # conn.setParameter 'link', createLink info.sourceId, info.targetId
          label.hide()
        else if link.has 'title'
          label.setLabel link.get 'title'
        else
          label.hide()
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
          # TODO: workflow validation
          callback.call @, wf
        return
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
            model: link
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
        radius: 7
      connector: [
        'Flowchart'
        stub: [40, 60]
        gap: 10
      ]
      connectorStyle:
        strokeStyle: '#346789'
        lineWidth: 2
      maxConnections: -1
    targetEndpointStyle:
      anchor: ['LeftMiddle', 'BottomCenter']
      dropOptions:
        hoverClass: 'hover'
    initialize: (options) ->
      @parent = options.parent
      @parentEl = @parent.el
      return
    render: ->
      console.log 'render node'
      # el.css top: node.y, left: node.x
      name = @el.id = @model.get 'name'
      @el.innerHTML = @model.escape 'title'
      jsPlumb.draggable @$el
      @parentEl.appendChild @el
      # build endpoints must after append el to dom
      @srcEndpoint = jsPlumb.addEndpoint @el, @sourceEndpointStyle, parameters:
        model: @model
        view: @
      jsPlumb.makeTarget @$el, @targetEndpointStyle, parameters:
        node: @model
        view: @
      @

  #  class LinkView extends Backbone.View

  #  workflows = new TenantWorkflows
  #  workflows.fetch success: ->
  #  workflow = workflows.get '51447afb4728cb2036cf9ca1'
  #  console.log 'workflow', workflow
  #  async.parallel [
  #    (callback) -> workflow.nodes.fetch success: callback, error: callback
  #    (callback) -> workflow.links.fetch success: callback, error: callback
  #  ], (err) ->
  #    console.log 'workflow', workflow

  WorkflowFrameView

