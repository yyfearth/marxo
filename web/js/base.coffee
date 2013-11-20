"use strict"

define 'base', ['models', 'lib/common', 'lib/html5-dataset'], ({Collection, Tenant, User, Workflow}) ->

  ## Utils

  find = (selector, parent) ->
    parent ?= document
    parent.querySelector selector

  findAll = (selector, parent) ->
    parent ?= document
    [].slice.call parent.querySelectorAll selector

  _html = (el) ->
    el.innerHTML.trim().replace(/\s+/g, ' ').replace(/> </g, '>\n<')

  tpl = (selector, returnDom) ->
    tpl_el = find selector
    throw new Error 'cannot load template from ' + selector unless tpl_el
    tpl_el.parentNode.removeChild tpl_el
    if returnDom then tpl_el else _html tpl_el

  tplAll = (selector, multi) ->
    hash = {}
    unless multi # default
      tpl_els = findAll '.tpl[name]', tpl selector, true
    else
      tpl_els = findAll selector
    throw new Error 'unable to find tpl elements or empty in ' + selector unless tpl_els.length
    for tpl_el in tpl_els
      name = tpl_el.getAttribute 'name'
      throw new Error 'to get a tpl dict, tpl element must have a "name" attribute' unless name
      hash[name] = _html tpl_el
    hash

  fill = (html, model) ->
    html.replace /{{\s*\w+\s*}}/g, (name) ->
      name = name.match(/^{{\s*(\w+)\s*}}$/)[1]
      model[name] or model.escape?(name) or ''

  # Polyfill
  Date::now ?= -> +new Date
  String::capitalize ?= ->
    @charAt(0).toUpperCase() + @slice(1).toLowerCase()

  # Enable CoffeeScript class for Javascript Mixin
  # https://github.com/yi/coffee-acts-as
  # e.g.: class A ...   class B ...
  #       class C
  #         @acts_as A, B
  Function::acts_as = (argv...) ->
    #console.log "[Function::acts_as]: argv #{argv}"
    for cl in argv
      @::["__is#{cl}"] = true
      for key, value of cl::
        @::[key] = value
    @

  ## Common Views

  class View extends Backbone.View
    initialize: (options) ->
      if @el?.tagName
        @el.view = @
        @$el.data 'view', @
      if options?.parent
        @parent = options.parent
        @parentEl = @parent.el
      @
    delayedTrigger: (eventName, delay = 10, args...) ->
      timeout_key = "_#{eventName}_timtout"
      clearTimeout @[timeout_key] if @[timeout_key]
      @[timeout_key] = setTimeout =>
        @[timeout_key] = null
        @trigger eventName, args...
      , delay
      @
    render: ->
      @rendered = true
      @el.view = @
      @$el.data 'view', @
      @
    remove: ->
      @trigger 'remove', @
      super

  class InnerFrameView extends View
    # initialize: (options) ->
    #   super options
    #   return

  class FrameView extends View
    initialize: (options) ->
      super options
      @navEl = options.navEl or (find "#navbar a[href=\"##{@id}\"]")?.parentElement
      @
    switchTo: (innerframe) ->
      innerframe = @[innerframe] if typeof innerframe is 'string'
      if innerframe and innerframe instanceof InnerFrameView
        unless innerframe.rendered
          innerframe.render()
          innerframe.rendered = true
        unless innerframe.el.classList.contains 'active'
          console.log 'switch inner-frame', innerframe.el?.id
          oldFrame = find '.inner-frame.active[name]', @el
          if oldFrame
            oldFrame.classList.remove 'active'
            view = $.data oldFrame, 'view'
            view?.trigger 'deactivate'
          innerframe.el.classList.add 'active'
          innerframe.trigger 'activate'
      else
        console.warn 'inner frame cannot find'
      @
  #open: (name) -> # should be override

  class BoxView extends View
    className: 'box'
    events:
      'click .btn-close': 'remove'
      'click .btn-minimize': 'minimize'
    initialize: (options) ->
      super options
    render: ->
      @btn_min = find '.btn-minimize', @el
      @btn_close = find '.btn-close', @el
      @contentEl = find '.box-content', @el
      @
    minimize: ->
      btn_min = @btn_min or find '.btn-minimize', @el
      content = @contentEl or find '.box-content', @el
      # console.log btn_min_icon, content
      if btn_min.classList.contains 'icon-up-open'
        # minimize
        content.classList.add 'minimized'
        btn_min.classList.remove 'icon-up-open'
        btn_min.classList.add 'icon-down-open'
      else
        # restore
        content.classList.remove 'minimized'
        btn_min.classList.remove 'icon-down-open'
        btn_min.classList.add 'icon-up-open'
      @

  class ModalDialogView extends View
    initialize: (options) ->
      super options
      @$el.modal
        show: false
        backdrop: 'static'
      @$el.on 'hidden', (e) =>
        if e.target is @el and false isnt @trigger 'hidden', @
          @callback()
          @goBack() if @goBackOnHidden and location.hash[1..] isnt @goBackOnHidden
          @reset()
      # cancel dialog if hash changed
      @listenTo @router, 'route', =>
        # cancel if current hash isnt start with saved hash while popup
        @cancel() if @_hash and @_hash isnt location.hash.slice 0, @_hash.length
      @
    goBack: ->
      if @_hash is location.hash
        @router.back fallback: @goBackOnHidden
      @
    popup: (data, callback) ->
      @render() unless @rendered
      if data is @data
        callback? 'ignored'
      else
        @reset()
        @data = data
        @_callback = callback
        @_hash = location.hash
        @show true
      @
    callback: (action = 'cancel') ->
      return unless @_callback?
      @trigger action, @data, @
      @_callback? action, @data, @
      @_action = action
      @_callback = null
      #@reset() # move to hidden
      @
    reset: ->
      @data = null
      @_action = null
      @_callback = null
      @_hash = null
      @trigger 'reset', @
      @
    #action: -> # should be customized, e.g. ok, save, export
    #  @callback 'action_name'
    #  @hide true
    cancel: ->
      @callback() if @_callback
      @hide true
    show: (shown = true) ->
      @$el.modal if shown then 'show' else 'hide'
      @shown = shown
      @
    hide: (hide = true) ->
      @show not hide

  class FormViewMixin
    initForm: ->
      @form = find 'form', @el # 1st
      throw new Error 'FormViewMixin require a form element in ' + (@el.id or @el.outerHTML) unless @form
      @form.onsubmit = (e) =>
        e.preventDefault()
        if @validate @form
          @form._callback? @form
          @form._callback = null
          @trigger 'submit', @form, @data
        false
      submit_btn = find '[type="submit"]', @form
      unless submit_btn?
        submit_btn = document.createElement 'input'
        submit_btn.type = 'submit'
        submit_btn.style.display = 'none'
        @form.appendChild submit_btn
      @_submit_btn = submit_btn
      if @form.key and title = (@form.name or @form.title)
        matched = false
        cached = ''
        $(title).on 'input', =>
          cached = title.value.trim().replace(/\W+/g, '_')[0..32].toLowerCase()
          matched or= not @form.key.value
          @form.key.value = cached if matched
          true
        $(@form.key).on
          input: => matched = @form.key.value is cached
          change: => @form.key.value = @form.key.value.toLowerCase()
      @
    validate: (form) ->
      for input in $(form).find '[required]:visible:enabled'
        unless input.value.trim()
          input.focus()
          alert 'This field is required!'
          return false
      try
        for input in findAll ':invalid', form
          input.focus()
          alert 'This field is invalid!'
          return false
      true
    submit: (callback) ->
      @form._callback = callback if typeof callback is 'function'
      @_submit_btn.click()
      @
    fill: (attributes) ->
      @_attributes = {}
      if attributes? then for name, value of attributes
        input = @form[name]
        #console.log input.name, input.length, input, value, input.value? if input?
        if input?.item?(0)?.type is 'radio'
          input = [].slice.call input
          for radio in input
            checked = radio.type is 'radio' and radio.value is value
            if radio.checked isnt checked
              radio.checked = checked
              $(radio).change() if checked # fire event
        else if input?.name is name and input.value?
          if input.type is 'checkbox'
            input.checked = value
            $(input).change()
          else # set value and fire event
            $(input).val(value).change()
        @_attributes[name] = value
      @trigger 'fill', @_attributes, attributes
      #console.log 'fill form', @_attributes
      @
    #reset: ->
    #  @form.reset()
    #  @
    read: ->
      unless @_attributes?
        null
      else # must use after fill
        attributes = {}
        for input in @form.elements
          name = input.name
          if name and not input.disabled and not $(input).is(':hidden')
            #input.style.visibility isnt 'hidden'
            switch input.type
              when 'radio'
                attributes[name] = input.value if input.checked
              when 'checkbox'
                attributes[name] = input.checked
              else
                if input.value or @_attributes[name]?
                  val = input.value
                  attributes[name] = if typeof val is 'string' then val.trim() else val
        @trigger 'read', attributes, @_attributes
        attributes

  class FormView extends View
    @acts_as FormViewMixin
    initialize: (options) ->
      super options
      @initForm()
    reset: ->
      @form.reset()
      @

  class FormDialogView extends ModalDialogView
    @acts_as FormViewMixin
    initialize: (options) ->
      super options
      @initForm()
      @btnSave = find 'button.btn-save', @el
      @btnSave?.onclick = => @submit @save.bind @
      @
    #popup: (data, callback) ->
    #  # already set @data = data
    #  super data, callback
    #  @fill data
    #  @
    #save: ->
    #  @callback 'save'
    #  @hide true
    #  @
    reset: -> # called after close
      super
      @form.reset()
      @

  class NavListView extends View
    urlRoot: ''
    headerTitle: ''
    defaultItem: 'all'
    itemClassName: ''
    targetClassName: ''
    emptyItem: 'new'
    allowEmpty: false
    _reload_timeout: 60000 # 1min
    initialize: (options) ->
      super options
      @collection = options.collection or @collection
      throw new Error 'collection must be given' unless @collection instanceof Collection
      @urlRoot = options.urlRoot or @urlRoot
      @headerTitle = options.headerTitle or @headerTitle
      @defaultItem = options.defaultItem or @defaultItem
      @itemClassName = options.itemClassName or @itemClassName
      @targetClassName = options.targetClassName or @targetClassName
      @emptyItem = options.emptyItem or @emptyItem
      @allowEmpty = options.allowEmpty or @allowEmpty
      @listenTo @collection, 'reset add remove', @render.bind @
      @events ?= {}
      @events['click .btn-refresh'] = => @fetch true
      @fetch false if options.auto
    fetch: (options = {}) ->
      col = @collection
      ts = Date.now()
      options = {force: options} if typeof options is 'boolean'
      if options.force or not col._last_load or ts - col._last_load > @_reload_timeout
        console.log 'fetch for list', @headerTitle
        options.reset ?= true
        col.fetch options
        col._last_load = Date.now()
        true
      else
        @render()
        false
    render: ->
      @_clear()
      @_render()
      @
    _clear: ->
      @el.innerHTML = ''
      @el.appendChild @_renderHeader null
      @el.appendChild @_renderItem @defaultItem if @defaultItem
      @el.appendChild @_renderItem @emptyItem if @allowEmpty and @defaultItem isnt @emptyItem
    _render: (models = @collection) ->
      models = models.fullCollection if models.fullCollection
      #console.log 'render models', models
      fragments = document.createDocumentFragment()
      models.forEach (model) =>
        fragments.appendChild @_renderItem model
      @el.appendChild fragments
    _renderHeader: (title = @headerTitle) ->
      header = document.createElement 'li'
      header.className = 'nav-header'
      header.textContent = title
      if title is @headerTitle
        btn = document.createElement 'button'
        btn.type = 'button'
        btn.className = 'btn-refresh icon-refresh'
        header.insertBefore btn, header.firstChild
      header
    _renderItem: (model = @defaultItem) ->
      #console.log 'render item', model
      li = document.createElement 'li'
      li.className = @itemClassName if @itemClassName
      a = document.createElement 'a'
      a.className = @targetClassName if @targetClassName
      if model.id?
        a.href = "##{@urlRoot}:#{model.id}"
        a.textContent = model.get('title') or model.get('name')
        a.dataset.id = model.id
        $(a).data 'model', model
      else if model.href
        a.href = model.href
        a.textContent = model.title
      else if model is 'all'
        a.href = "##{@urlRoot}:all"
        a.textContent = 'All'
      else if model is 'new' or model is 'empty'
        a.href = "##{@urlRoot}:#{@emptyItem}"
        a.textContent = 'Empty'
      else
        console.dir model
        throw new Error 'unsupported item for list'
      li.className += ' active' if model is @defaultItem
      li.appendChild a
      li

  class WorkflowDiagramView extends View
    initialize: (options) ->
      @model = options.model
      @r = options.radius or 20
      @w = options.width or 0
      @h = options.height or 0
      @t = options.timeout ? 100
      @max_t = options.maxTimeout
      @_tick = @_tick.bind @
      @_draw = @_draw.bind @
      @_click = @_click.bind @
      @_mouseover = @_mouseover.bind @
      @_mouseleave = @_mouseleave.bind @
      @draw = @draw.bind @
      super options
    _click: (d) -> @trigger 'select', d.model
    _data: (wf) ->
      @model = wf
      r = @r + 1
      w = @w or @$el.innerWidth()
      h = @h or @$el.innerHeight()
      fixed = true
      @data =
        nodes: wf.nodes.map (node, i) ->
          node._idx = i
          _fixed = node.has 'offset'
          offset = if _fixed
            node.get 'offset'
          else
            fixed = false
            x: r * i * 0.56
            y: h / 2
          x = r + Math.round((offset.x or 0) / r / 2) * r
          y = r + Math.round((offset.y or 0) / r / 2) * r
          w = x if x > w
          h = y if y > h
          x: x
          y: y
          title: "Node #{i + 1}: #{node.get 'name'}"
          fixed: _fixed or i is 0
          index: i + 1
          model: node
        links: wf.links.map (link, i) ->
          src = link.prevNode._idx
          tar = link.nextNode._idx
          source: src
          target: tar
          title: "Link #{i + 1}: #{link.name()}"
          straight: tar > src
          model: link
      @w = w + r + r
      @h = h + r + r
      @fixed = fixed
      @force.size([@w, @h])
      @svg.attr('viewBox', '0 0 ' + @w + ' ' + @h)
      #console.log 'nodes'
      #console.table @data.nodes
      #console.log 'links'
      #console.table @data.links
      return
    _init: (callback) -> require ['lib/d3v3'], (@d3) =>
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
      @link = svg.append('svg:g').selectAll('.link')
      @node = svg.append('svg:g').selectAll('g')
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
      pos = @d3.mouse(@el)
      # console.log 'over', d, pos, @tooltip
      @tooltip.transition().duration(200).style('opacity', .9)
      .style('left', pos[0] + 'px')
      .style('top', pos[1] + 'px')
      .text(d.title)
    _mouseleave: ->
      @tooltip.transition().duration(500).style('opacity', 0)
    _draw: ->
      r = @r
      t = @t
      data = @data
      force = @force
      force.nodes(data.nodes).links(data.links)
      # load nodes
      link = @link = @link.data(data.links)
      link.enter().append('path').attr('class', 'link').attr('id', (d) -> 'link_' + d.model.cid)
      .style('marker-end', 'url(#end-arrow)').on('click', @_click)
      .on('mouseover', @_mouseover).on('mouseleave', @_mouseleave)
      link.exit().remove()
      # load links
      @node = @node.data(data.nodes)
      node = @node.enter().append('svg:g').call @force.drag()
      node.attr('id',(d) -> 'node_' + d.model.cid).on('click', @_click)
      .on('mouseover', @_mouseover).on('mouseleave', @_mouseleave)
      node.append('circle').attr('class', 'node').attr('r', r)
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
      return
    clear: ->
      if @d3
        @model = {}
        @data = links: [], nodes: []
        @w = @h = 0
        @_draw()
      @
    draw: (wf = @model) ->
      throw new Error 'unable to draw workflow' unless wf instanceof Workflow
      @render() unless @rendered
      @w = @h = 0
      unless wf.loaded
        wf.fetch reset: true, success: @draw
      else unless @d3
        @_init =>
          @_data wf
          @_draw()
          return
      else
        @_data wf if wf isnt @model
        @_draw()
      @
    highlight: (model) -> # must be node or link
      if d3 = @d3
        d3.selectAll('svg .active').classed 'active', false
        d3.select("##{model._name}_#{model.cid}").classed 'active', true if model
      @

  { # exports
  find
  findAll
  tpl
  tplAll
  fill
  View
  BoxView
  FrameView
  InnerFrameView
  NavListView
  ModalDialogView
  FormDialogView
  FormViewMixin
  FormView
  WorkflowDiagramView
  Tenant
  User
  }
