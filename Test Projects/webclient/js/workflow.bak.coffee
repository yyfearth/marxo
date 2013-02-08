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
    name: 'Submit Design and Email'
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
    # add to index
    linkIndex[link.id] = link
    # link from node
    link.fromNode = nodeIndex[link.from]
    link.fromNode.toLinks.push link
    # link to node
    link.toNode = nodeIndex[link.to]
    link.toNode.fromLinks.push link
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

    EndpointStyles: ['Dot', radius:2]
    HoverPaintStyle: strokeStyle:"#42a62c", lineWidth:2
    # the overlays to decorate each connection with.  note that the label overlay uses a function to generate the label text;
    # in this case it returns the 'labelText' member that we set on each connection in the 'init' method below.
    ConnectionOverlays: [
      [ 'Arrow', location: 1 ]
      [ 'Label',
        location: 0.5
        id: 'label'
        cssClass: 'aLabel'
      ]
    ]

  # this is the paint style for the connecting lines..
#  connectorPaintStyle =
#    lineWidth: 5
#    strokeStyle: '#deea18'
#    joinstyle: 'round'


  # .. and this is the hover style.
#  connectorHoverStyle =
#    lineWidth: 7
#    strokeStyle: '#2e2aF8'


  # the definition of source endpoints (the small blue ones)
  sourceEndpoint =
    endpoint: 'Dot'
    paintStyle:
      fillStyle: '#225588'
      radius: 7
    isSource: true
    connector: [ 'Flowchart', stub: [40, 60], gap: 10 ]
#    connectorStyle: connectorPaintStyle
    maxConnections: -1
#    hoverPaintStyle: connectorHoverStyle
#    connectorHoverStyle: connectorHoverStyle
    dragOptions:
      {}
    overlays: [
      [ 'Label',
        location: [0.5, 1.5]
        # label: 'Drag'
        cssClass: 'endpointSourceLabel'
      ]
    ]

  # a source endpoint that sits at BottomCenter
  # bottomSource = jsPlumb.extend( { anchor:'BottomCenter' }, sourceEndpoint),
  # the definition of target endpoints (will appear when the user drags a connection)
  targetEndpoint =
    endpoint: 'Dot'
    paintStyle:
      fillStyle: '#558822'
      radius: 11
    hoverPaintStyle: connectorHoverStyle
    maxConnections: -1
    dropOptions:
      hoverClass: 'hover'
      activeClass: 'active'
    isTarget: true
    overlays: [
      [ 'Label', 
        location: [0.5, -0.5]
        # label: 'Drop'
        cssClass: 'endpointTargetLabel'
      ]
    ]

  root = $ '#demo'

  data.nodes.forEach (node) ->
    # add to dom
    root.append "<div class=\"window\" id=\"#{node.uuid}\" style=\"top:#{node.y}px;left:#{node.x}px\"><strong>#{node.name}</strong></div>"
    # add endpoints
    node.sourceEndpoint = jsPlumb.addEndpoint node.uuid, sourceEndpoint,
      anchor: 'RightMiddle'
      parameters: node: node
    node.targetEndpoint = jsPlumb.addEndpoint node.uuid, targetEndpoint,
      anchor: 'LeftMiddle'
      parameters: node: node
    return

  # listen for new connections; initialise them the same way we initialise the connections at startup.
  jsPlumb.bind 'jsPlumbConnection', (conn, originalEvent) ->
    conn = conn.connection
    # link created
    link = conn.getParameter 'link'
    label = conn.getOverlay 'label'
    if link.name?
      label.setLabel link.name
    else
      label.hide()
    return

  # make all the window divs draggable
  #jsPlumb.draggable(jsPlumb.getSelector('.window'), { grid: [20, 20] });
  # THIS DEMO ONLY USES getSelector FOR CONVENIENCE. Use your library's appropriate selector method!
  jsPlumb.draggable(jsPlumb.getSelector('.window'))

  # connect a few up
  #  jsPlumb.connect({uuids: ['window2BottomCenter', 'window3TopCenter']})
  #  jsPlumb.connect({uuids: ['window2LeftMiddle', 'window4LeftMiddle']})
  #  jsPlumb.connect({uuids: ['window4TopCenter', 'window4RightMiddle']})
  #  jsPlumb.connect({uuids: ['window3RightMiddle', 'window2RightMiddle']})
  #  jsPlumb.connect({uuids: ['window4BottomCenter', 'window1TopCenter']})
  #  jsPlumb.connect({uuids: ['window3BottomCenter', 'window1BottomCenter']})
  data.links.forEach (link) ->
    jsPlumb.connect
      source: link.fromNode.sourceEndpoint
      target: link.toNode.targetEndpoint
      parameters: link: link
    return

  #
  # listen for clicks on connections, and offer to delete connections on click.
  #
  jsPlumb.bind 'click', (conn, originalEvent)->
    jsPlumb.detach(conn) if confirm('Delete connection from ' + conn.sourceId + ' to ' + conn.targetId + '?')
    return

  jsPlumb.bind 'connectionDrag', (connection) ->
    console.log('connection ' + connection.id + ' is being dragged')
    return

  jsPlumb.bind 'connectionDragStop', (connection) ->
    console.log('connection ' + connection.id + ' was dragged')
    return

# chrome fix
document.querySelector('#demo').onselectstart = -> false
