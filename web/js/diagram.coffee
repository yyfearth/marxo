"use strict"

define 'diagram', ['base', 'lib/d3v3'], ({View}, d3) ->

  class WorkflowDiagramView extends View
    initialize: (options) ->
      @model = options.model
      @r = options.radius or 20
      @t = options.timeout ? 100
      @max_t = options.maxTimeout
      @_tick = @_tick.bind @
      @_draw = @_draw.bind @
      @_click = @_click.bind @
      @_mouseover = @_mouseover.bind @
      @_mouseleave = @_mouseleave.bind @
      @draw = @draw.bind @
      super options
    _click: (d) -> @trigger 'select', d.model, @model
    _sort: (wf, nodes, links) ->
      nodes_cindex = {}
      links_cindex = {}
      level = [wf.startNode]

      while node = level.shift()
        unless nodes_cindex.hasOwnProperty node.cid
          nodes.push nodes_cindex[node.cid] = node
          for link in node.outLinks
            links.push links_cindex[link.cid] = link
            level.push link.nextNode
      return
    _data: (wf) ->
      @model = wf
      r = @r + 1
      w = 0
      h = 0
      fixed = true

      if wf.startNode?
        nodes = []
        links = []
        @_sort wf, nodes, links
        @_invalid = nodes.length isnt wf.nodes.length or links.length isnt wf.links.length
        # console.log 'sort nodes', @_valid, nodes, links
      else
        @_invalid = true
        nodes = wf.nodes
        links = wf.links

      @data =
        nodes: nodes.map (node, i) ->
          node._idx = i
          _fixed = node.has 'offset'
          {x, y} = if _fixed
            node.get 'offset'
          else
            fixed = false
            x: r * i * 0.56
            y: h / 2
          x = r + Math.round((x or 0) / r / 2) * r
          y = r + Math.round((y or 0) / r / 2) * r
          w = x if x > w
          h = y if y > h
          cls = []
          txt = "Node #{i + 1}: #{node.get 'name'}"
          if node is wf.startNode
            txt = '[Start] ' + txt
            cls.push 'start-node'
          if status = node.get 'status'
            txt += " (#{status.toUpperCase()})" unless /^IDLE$|^NONE$/i.test status
            cls.push 'status-' + status.toLowerCase()
          x: x
          y: y
          id: "node_#{node.id ? node.cid}"
          cls: cls.join(' ')
          tooltip: txt
          fixed: _fixed or node is wf.startNode
          index: i + 1
          model: node
        links: links.map (link, i) ->
          src = link.prevNode._idx
          tar = link.nextNode._idx
          if status = link.get 'status'
            cls = 'status-' + status.toLowerCase()
            status = if /^IDLE$|^NONE$/i.test(status) then '' else " (#{status.toUpperCase()})"

          id: "link_#{link.id ? link.cid}"
          cls: cls
          source: src
          target: tar
          tooltip: "Link #{i + 1}: #{link.name()}#{status or ''}"
          straight: tar > src
          model: link

      if fixed and h > w # swap to ensure w >= h
        [w, h] = [h, w]
        @data.nodes.forEach (node) ->
          x = node.x
          node.x = node.y
          node.y = x
          return
      w = @w = Math.max @$el.innerWidth(), w + r + r
      h = @h = Math.max @$el.innerHeight(), h + r + r
      @fixed = fixed

      @force.size([w, h])
      @svg.attr('viewBox', "0 0 #{w} #{h}")
      #console.log 'nodes'
      #console.table @data.nodes
      #console.log 'links'
      #console.table @data.links
      return
    _init: (callback) ->
      # init force layout
      @force = d3.layout.force().friction(0.5).charge(-800).linkDistance(100).on 'tick', @_tick
      # init svg
      svg = @svg = d3.select(@el).html('').append('svg')
      .attr('preserveAspectRatio', 'xMidYMid meet')
      svg.append('svg:defs').append('svg:marker')
      .attr('id', 'end-arrow')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', 6)
      .attr('markerWidth', 6)
      .attr('markerHeight', 6)
      .attr('orient', 'auto')
      .append('svg:path')
      .attr('d', 'M0,-5L10,0L0,5')
      .attr('fill', '#000')
      # init nodes and links
      @link = svg.append('svg:g').attr('class', 'links').selectAll('.link')
      @node = svg.append('svg:g').attr('class', 'nodes').selectAll('g')
      # init tooltip
      @tooltip = d3.select(@el).append('div')
      .attr('class', 'tooltip')
      .style('opacity', 0)
      callback? d3
      @_init = -> false # once
      return
    _tick: ->
      r = @r
      padding = r + 7
      @node.attr 'transform', (d) -> "translate(#{d.x},#{d.y})"
      @link.attr 'd', (d) ->
        sourceX = d.source.x
        sourceY = d.source.y
        targetX = d.target.x
        targetY = d.target.y
        deltaX = targetX - sourceX
        deltaY = targetY - sourceY
        if deltaX or deltaY
          arc = 0
          dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
          normX = deltaX / dist
          normY = deltaY / dist
          sourceX += r * normX
          sourceY += r * normY
          targetX -= padding * normX
          targetY -= padding * normY
          dist *= 0.75
          return "M#{sourceX},#{sourceY}L#{targetX},#{targetY}" if d.straight
        else
          arc = 1
          dist = r
          ++targetX
          ++targetY
        "M#{sourceX},#{sourceY}A#{dist},#{dist} 0,#{arc},1 #{targetX},#{targetY}"
      return
    _mouseover: (d) ->
      pos = d3.mouse(@el)
      # console.log 'over', d, pos, @tooltip
      @tooltip.transition().duration(200).style('opacity', .9)
      .style('left', pos[0] + 'px')
      .style('top', pos[1] + 'px')
      .text(d.tooltip)
    _mouseleave: ->
      @tooltip.transition().duration(500).style('opacity', 0)
    _draw: ->
      console.log 'draw', @data
      r = @r
      t = @t
      data = @data
      force = @force
      force.nodes(data.nodes).links(data.links)
      # load nodes
      link = @link = @link.data(data.links)
      link.enter().append('path').attr('class', (d) -> "link #{d.cls}").attr('id', (d) -> d.id)
      .style('marker-end', 'url(#end-arrow)').on('click', @_click)
      .on('mouseover', @_mouseover).on('mouseleave', @_mouseleave)
      link.exit().remove()
      # load links
      @node = @node.data(data.nodes)
      node = @node.enter().append('svg:g').call @force.drag()
      node.attr('id',(d) -> d.id).on('click', @_click)
      .on('mouseover', @_mouseover).on('mouseleave', @_mouseleave)
      node.append('circle').attr('class', (d) -> "node #{d.cls}").attr('r', r)
      node.append('svg:text').attr('x', 0).attr('y', 10).attr('class', 'index').text (d) -> d.index
      @node.exit().remove()
      # start
      force.start()
      #i = 0
      #force.tick() while force.alpha() > 0.001 and ++i < 10000
      #force.stop()
      # auto stop
      fixed = @fixed
      if @max_t and not fixed
        t = @max_t
        fixed = true
      setTimeout (-> force.stop()), t if t and fixed

      @svg.classed 'invalid', @_invalid
      @$el[if @_invalid then 'addClass' else 'removeClass'] 'invalid'
      return
    clear: ->
      if @svg
        @model = {}
        @data = {}
        @w = @h = 0
        emtpy = []
        @force.nodes(emtpy).links(emtpy)
        @link = @link.data(emtpy)
        @link.exit().remove()
        @node = @node.data(emtpy)
        @node.exit().remove()
      @
    draw: (wf = @model) ->
      throw new Error 'unable to draw workflow' unless wf.nodes
      @render() unless @rendered
      @w = @h = 0
      unless wf.loaded()
        console.log 'wf diagram load wf', wf._name, wf.id
        wf.fetch success: @draw
      else unless @svg
        @_init =>
          @_data wf
          @_draw()
          return
      else
        if not @model or wf isnt @model
          @clear()
          @_data wf
        @_draw()
      @
    highlight: (model) -> # must be node or link
      if svg = @svg
        svg.selectAll('.active').classed 'active', false
        svg.select("##{model._name}_#{model.id ? model.cid}").classed 'active', true if model
      @

  WorkflowDiagramView
