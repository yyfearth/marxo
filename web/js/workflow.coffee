data =
  name: 'demo_wf'
  desc: 'Demo Workflow'
  tenantId: "507f81413d070321728fdeff"
  nodes: [
    id: "507f81413d070321728fde10"
    name: 'Post Idea'
    desc: 'Post software project ideas'
  ,
    id: "507f81413d070321728fde11"
    name: 'Post Cancel'
    desc: 'Post cancel notification'
  ,
    id: "507f81413d070321728fde12"
    name: 'Post Requrement'
    desc: 'Post project requirement'
  ,
    id: "507f81413d070321728fde13"
    name: 'Submit Design'
    desc: 'Retrieve theme design submissions & e-mail to stackholders'
  ,
    id: "507f81413d070321728fde14"
    name: 'Notification'
    desc: 'Notification'
  ,
    id: "507f81413d070321728fde15"
    name: 'Post Result'
    desc: 'Post & e-mail result everyone'
  ]
  links: [
    id: "507f81413d070321728fde21"
    name: 'Like count >= 300'
    desc: 'Continue to post requirement if like count >= 300'
    from: "507f81413d070321728fde10"
    to: "507f81413d070321728fde12"
  ,
    id: "507f81413d070321728fde21"
    name: 'Like count &lt; 300'
    desc: 'Cancel if like count &lt; 300'
    from: "507f81413d070321728fde10"
    to: "507f81413d070321728fde11"
  ,
    id: "507f81413d070321728fde22"
    from: "507f81413d070321728fde12"
    to: "507f81413d070321728fde13"
  ,
    id: "507f81413d070321728fde22"
    name: 'Pass rate &lt;= 50%'
    desc: 'Notification if pass rate &lt;= 50%'
    from: "507f81413d070321728fde13"
    to: "507f81413d070321728fde14"
  ,
    id: "507f81413d070321728fde23"
    name: 'Pass rate &gt; 50%'
    desc: 'Post & e-mail to everyone if pass rate &gt; 50%'
    from: "507f81413d070321728fde13"
    to: "507f81413d070321728fde15"
  ]

createLink = (sourceId, targetId) ->
  uuid = sourceId + '-' + targetId
  console.log 'create link', uuid
  fromNode = data.nodes.index[sourceId]
  toNode = data.nodes.index[targetId]
  # create link
  link = {uuid, fromNode, toNode}
    # id: 
    # from: 
    # to: 
  data.links.push link
  fromNode.toLinks.push link
  toNode.fromLinks.push link
  data.links.index[uuid] = link
  link

deleteLink = (link) ->
  console.log 'delete link', link.uuid
  delete data.links.index[link.id]
  delete data.links.index[link.uuid]
  removeFromArray data.links, link
  removeFromArray link.fromNode.toLinks, link
  removeFromArray link.toNode.fromLinks, link
  return

removeFromArray = (array, item) ->
  idx = array.indexOf item
  array.splice idx, 1 if idx isnt -1

do procData = ->
  nodeIndex = data.nodes.index = {}
  linkIndex = data.links.index = {}
  startNodes = data.nodes.starts = []
  endNodes = data.nodes.ends = []
  lonelyNodes = data.nodes.alones = []

  data.nodes.forEach (node) ->
    # gen uuid
    uuid = node.uuid = node.name.toLowerCase().replace /\W/g, '_'
    # add to index by uuid and id
    nodeIndex[uuid] = nodeIndex[node.id] = node
    # add links
    node.toLinks = []
    node.fromLinks = []
    return

  data.links.forEach (link) ->
    # link from node
    link.fromNode = nodeIndex[link.from]
    link.fromNode.toLinks.push link
    # link to node
    link.toNode = nodeIndex[link.to]
    link.toNode.fromLinks.push link
    # creat uuid
    link.uuid = link.fromNode.uuid + '-' + link.toNode.uuid
    # add to index
    linkIndex[link.id] = linkIndex[link.uuid] = link
    return

  data.nodes.forEach (node) -> # get start, end, alone nodes
    if node.fromLinks.length is node.toLinks.length is 0
      lonelyNodes.push node
    else if node.fromLinks.length is 0
      startNodes.push node
    else if node.toLinks.length is 0
      endNodes.push node
    return

  grid = window.grid = [startNodes.concat(lonelyNodes)] # vertical
  grid.spanX = 350
  grid.spanY = 150
  grid.vertical = false

  do traval = (level = 0) ->
    nextLevel = []
    grid[level]?.forEach (node, i) ->
      node.gridX = i
      node.gridY = level
      if grid.vertical
        node.x = i * grid.spanX
        node.y = level * grid.spanY
      else
        node.x = level * grid.spanX
        node.y = i * grid.spanY
      node.toLinks?.forEach (link) ->
        nextLevel.push link.toNode
      return
    if nextLevel.length
      grid[level + 1] = nextLevel
      traval level + 1
    return
  return # end of proc data

jsPlumb.ready ->
  jsPlumb.importDefaults
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

  sourceEndpoint =
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
  targetEndpoint =
    dropOptions: hoverClass: 'hover'
    anchor: ['LeftMiddle', 'BottomCenter']

  root = $ '<div id="demo"></div>'
  $(document.body).append root

  data.nodes.forEach (node) ->
    # add to dom
    el = node.el = $ "<div class=\"node\" id=\"#{node.uuid}\"><strong>#{node.name}</strong></div>"
    # el.append '<div class="ep"></div>'
    el.css top: node.y, left: node.x
    root.append el
    jsPlumb.draggable el
    # add endpoints
    node.srcEndpoint = jsPlumb.addEndpoint el, sourceEndpoint, parameters: node: node
    jsPlumb.makeTarget el, targetEndpoint, parameters: node: node
    return

  jsPlumb.bind 'jsPlumbConnection', (info) ->
    conn = info.connection
    link = conn.getParameter 'link'
    label = conn.getOverlay 'label'
    if not link?
      conn.setParameter 'link', createLink info.sourceId, info.targetId
      label.hide()
    else if link.name?
      label.setLabel link.name
    else
      label.hide()
    return

  jsPlumb.bind 'jsPlumbConnectionDetached', (info) ->
    deleteLink info.connection.getParameter 'link'
    node = data.nodes.index[info.sourceId]
    jsPlumb.deleteEndpoint node.srcEndpoint
    node.srcEndpoint = jsPlumb.addEndpoint node.el, sourceEndpoint, parameters: node: node
    return

  jsPlumb.bind 'beforeDrop', (info) ->
    uuid = info.sourceId + '-' + info.targetId
    newlink = data.links.index[uuid]
    oldLink = info.connection.getParameter 'link'
    if newlink?
      console.log 'link exists', uuid
      # alert 'link exists' if oldLink?
      false # cancel if link exists
    else
      deleteLink oldLink if oldLink?
      true

  # connect nodes by links
  data.links.forEach (link) ->
    jsPlumb.connect
      source: link.fromNode.srcEndpoint
      target: link.toNode.el
      parameters: link: link
    return

  jsPlumb.bind 'dblclick', (conn)->
    if confirm('Delete connection from ' + conn.sourceId + ' to ' + conn.targetId + '?')
      jsPlumb.detach conn
    return

# chrome fix
document.body.onselectstart = -> false
