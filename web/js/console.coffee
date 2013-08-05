"use strict"

define 'console', ['models', 'lib/common'], ({Collection}) ->

  ## Utils

  find = (selector, parent) ->
    parent ?= document
    parent.querySelector selector

  findAll = (selector, parent) ->
    parent ?= document
    [].slice.call parent.querySelectorAll selector

  # enable coffeescript class for javascript mixin
  # https://github.com/yi/coffee-acts-as
  # ex: class C
  #       @acts_as A, B
  Function::acts_as = (argv...) ->
    #console.log "[Function::acts_as]: argv #{argv}"
    for cl in argv
      @::["__is#{cl}"] = true
      for key, value of cl::
        @::[key] = value
    @

  ## Views

  class View extends Backbone.View
    initialize: (options) ->
      @el.view = @
      if options?.parent
        @parent = options.parent
        @parentEl = @parent.el
      return
    delayedTrigger: (eventName, delay = 10, args...) ->
      timeout_key = "_#{eventName}_timtout"
      clearTimeout @[timeout_key] if @[timeout_key]
      @[timeout_key] = setTimeout =>
        @[timeout_key] = null
        @trigger eventName, args...
        return
      , delay
    render: ->
      @rendered = true
      @

  class ConsoleView extends View
    el: '#main'
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    initialize: ->
      @frames = {}
      findAll('.frame', @el).forEach (frame) =>
        navEl = find "#navbar a[href=\"##{frame.id}\"]"
        @frames[frame.id] =
          id: frame.id
          el: frame
          parent: @
          navEl: navEl?.parentElement
        return
      @fixStyles()
      # Init tooltips
      @$el.tooltip selector: '[title]'
      return
    fixStyles: ->
      # auto resize
      navContainer = find '#navbar', @el
      framesContainer = find '#frames', @el
      do window.onresize = =>
        h = navContainer.clientHeight or 41
        framesContainer.style.top = h + 'px'
        return
      # hide menu after click
      $('.dropdown-menu').click ->
        navContainer.classList.add 'hide-dropdown'
        $(document.body).one 'mousemove', ->
          navContainer.classList.remove 'hide-dropdown'
        return
      return
    showFrame: (frame, name, sub) ->
      frame = @frames[frame]
      return unless frame?
      # console.log 'frame', frame
      if frame instanceof FrameView
        frame.open? name, sub
      else
        console.log 'load module:', frame.id
        require [frame.id], (TheFrameView) =>
          frame = @frames[frame.id] = new TheFrameView frame
          frame.render()
          frame.open? name, sub
          return
      unless frame.el.classList.contains 'active'
        find('#main .frame.active')?.classList.remove 'active'
        find('#navbar li.active')?.classList.remove 'active'
        frame.el.classList.add 'active'
        frame.navEl.classList.add 'active'
        $(window).resize()
      return
    signout: ->
      # TODO: sign out
      delete sessionStorage.user
      SignInView.get().show()
      @hide()
      @trigger 'signout'
      return

    show: ->
      @el.style.visibility = 'visible'
      @el.classList.add 'active'
      @el.style.opacity = 1
      return
    hide: ->
      @el.classList.remove 'active'
      setTimeout =>
        @el.style.visibility = 'hidden'
        return
      , SignInView::delay
      return

  class InnerFrameView extends View
    initialize: (options) ->
      super options
      return

  class FrameView extends View
    initialize: (options) ->
      super options
      @navEl = options.navEl or (find "#navbar a[href=\"##{@id}\"]")?.parentElement
      return
    switchTo: (innerframe) ->
      innerframe = @[innerframe] if typeof innerframe is 'string'
      if innerframe and innerframe instanceof InnerFrameView
        unless innerframe.el.classList.contains 'active'
          console.log 'switch inner-frame', innerframe.el?.id
          find('.inner-frame.active[name]', @el)?.classList.remove 'active'
          innerframe.el.classList.add 'active'
        unless innerframe.rendered
          innerframe.render()
          innerframe.rendered = true
      else
        console.warn 'inner frame cannot find', frameName
      return
  #open: (name) -> # should be override

  class BoxView extends View
    className: 'box'
    events:
      'click .btn-close': 'close'
      'click .btn-minimize': 'minimize'
    initialize: (options) ->
      super options
    render: ->
      @btn_min = find '.btn-minimize', @el
      @btn_close = find '.btn-close', @el
      @contentEl = find '.box-content', @el
      return
    close: -> # should be override
      console.log 'box close button clicked'
      return
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
      return

  class ModalDialogView extends View
    initialize: (options) ->
      super options
      @$el.modal
        show: false
        backdrop: 'static'
      @$el.on 'hidden', (e) =>
        @callback() if e.target is @el and false isnt @trigger 'hidden', @
      return
    popup: (data, callback) ->
      if data is @data
        callback? 'ignored'
      else
        @reset()
        @data = data
        @_callback = callback
        @show true
      @
    callback: (action = 'cancel') ->
      return unless @_callback?
      @trigger action, @data, @
      @_callback? action, @data, @
      @_action = action
      @reset()
      return
    reset: ->
      @data = null
      @_action = null
      @_callback = null
      @
    #action: -> # should be customized, e.g. ok, save, export
    #  @callback 'action_name'
    #  @hide true
    cancel: ->
      @callback() if @_callback
      @hide true
    show: (@shown = true) ->
      @$el.modal if @shown then 'show' else 'hide'
      @
    hide: (hide = true) ->
      @show not hide

  class FormViewMixin
    initForm: ->
      @form = find 'form', @el
      throw 'FormViewMixin require a form element in ' + (@el.id or @el.outerHTML) unless @form
      @form.onsubmit = (e) =>
        e.preventDefault()
        @form._callback? @form
        @form._callback = null
        @trigger 'submit', @form
        false
      submit_btn = find '[type="submit"]', @form
      unless submit_btn?
        submit_btn = document.createElement 'input'
        submit_btn.type = 'submit'
        submit_btn.style.display = 'none'
        @form.appendChild submit_btn
      @_submit_btn = submit_btn
      if @form.title and @form.name
        matched = false
        cached = ''
        $(@form.title).on 'input', =>
          cached = @form.title.value.trim().replace(/\W+/g, '_')[0..32].toLowerCase()
          matched or= not @form.name.value
          @form.name.value = cached if matched
        return
        $(@form.name).on
          input: => matched = @form.name.value is cached
          change: => @form.name.value = @form.name.value.toLowerCase()
      @
    submit: (callback) ->
      @form._callback = callback if typeof callback is 'function'
      @_submit_btn.click()
      @
    fill: (attributes) ->
      @_attributes = {}
      if attributes? then for name, value of attributes
        input = @form[name]
        console.log input.name, input.length, input, value, input.value? if input?
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
          else
            $(input).val(value).change() # set value and fire event
        @_attributes[name] = value
      #console.log 'fill form', @_attributes
      @
    read: ->
      if @_attributes? # must use after fill
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
                attributes[name] = input.value if input.value or @_attributes[name]?
        attributes
      else
        null
    #reset: ->
    #  @form.reset()
    #  @

  class FormDialogView extends ModalDialogView
    @acts_as FormViewMixin
    initialize: (options) ->
      super options
      @initForm()
      find('button.btn-save', @el)?.onclick = => @submit @save.bind @
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

  class SignInView extends View
    el: '#signin'
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    events:
      'submit form': 'submit'
    initialize: (options) ->
      super options
      # auto sign in
      if (sessionStorage.user)
        @signedIn()
      else
        @show()
      return
    submit: -> # fake
      console.log 'sign in'
      @signedIn()
      false
    signedIn: -> # debug only
      user = id: 'test', name: 'test'
      sessionStorage.user = JSON.stringify user
      @trigger 'success', user
      @hide()
      ConsoleView.get().show()
      # Router.get().navigate 'home'
      location.hash = '' if /signin/i.test location.hash
      return
    delay: 500
    show: ->
      @el.style.opacity = 0
      @el.style.display = 'block'
      setTimeout =>
        @el.classList.add 'active'
        @el.style.opacity = 1
        return
      , 1
      return
    hide: ->
      @el.classList.remove 'active'
      @el.style.opacity = 0
      setTimeout =>
        @el.style.display = 'none'
        return
      , @delay
      return

  class NavListView extends View
    urlRoot: ''
    headerTitle: ''
    defaultItem: 'all'
    itemClassName: ''
    targetClassName: ''
    _reload_timeout: 60000 # 1min
    initialize: (options) ->
      super options
      @collection = options.collection or @collection
      throw 'collection must be given' unless @collection instanceof Collection
      @urlRoot = options.urlRoot or @urlRoot
      @headerTitle = options.headerTitle or @headerTitle
      @defaultItem = options.defaultItem or @defaultItem
      @itemClassName = options.itemClassName or @itemClassName
      @targetClassName = options.targetClassName or @targetClassName
      @listenTo @collection, 'reset', @render.bind @
      @fetch false if options.auto
    fetch: (force) ->
      col = @collection
      ts = new Date().getTime()
      if force or not col._last_load or ts - col._last_load > @_reload_timeout
        # TODO: add a refresh button
        console.log 'fetch for list', @headerTitle
        col.fetch reset: true
        col._last_load = new Date().getTime()
        true
      else
        false
    render: ->
      @_clear()
      @_render()
      @
    _clear: ->
      @el.innerHTML = ''
      @el.appendChild @_renderHeader null
      @el.appendChild @_renderItem null
      return
    _render: (models = @collection) ->
      #console.log 'render models', models
      fragments = document.createDocumentFragment()
      models.forEach (model) =>
        fragments.appendChild @_renderItem model
        return
      @el.appendChild fragments
      return
    _renderHeader: (title = @headerTitle) ->
      header = document.createElement 'li'
      header.className = 'nav-header'
      header.textContent = title
      header
    _renderItem: (model = @defaultItem) ->
      #console.log 'render item', model
      li = document.createElement 'li'
      li.className = @itemClassName if @itemClassName
      a = document.createElement 'a'
      a.className = @targetClassName if @targetClassName
      if model.id
        a.href = "##{@urlRoot}:#{model.id}"
        a.textContent = model.get 'title'
        a.dataset.id = model.id
        $(a).data 'model', model
      else if model.href
        a.href = model.href
        a.textContent = model.title
      else if model is 'all'
        a.href = "##{@urlRoot}:all"
        a.textContent = 'All'
      else if model is 'new' or model is 'empty'
        a.href = "##{@urlRoot}:new"
        a.textContent = 'Empty'
      else
        console.error 'unsupported item for list', model
        return
      li.className += ' active' if model is @defaultItem
      li.appendChild a
      li

  ## Router

  class Router extends Backbone.Router
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    frames: [
      'home'
      'project'
      'workflow'
      'calendar'
      'content'
      'report'
      'config'
      'profile'
    ]
    routes:
      'workflow/:id(/link/:link)(/node/:node)(/action/:action)': (id, link, node, action) ->
        @show 'workflow', id, {link, node, action}
      'project/:id(/link/:link)(/node/:node)(/action/:action)': (id, link, node, action) ->
        @show 'project', id, {link, node, action}
    constructor: (options) ->
      super options
      @route '', 'home', =>
        @navigate 'home', replace: true
        @show 'home'
      @frames.forEach (frame) =>
        @route frame + '(/:name)(/)', frame, (name) =>
          @show frame, name
        return
      @route 'signin', 'signin', => return
      @route 'signout', 'signout'
      return

    show: (frame, name, sub) ->
      unless sessionStorage.user
        @navigate 'signin', replace: true
        return
      console.log 'route', frame, (name or ''), sub
      ConsoleView.get()?.showFrame frame, name, sub
      handler = @[frame]
      handler.call @, name if handler?
      return

    #home: -> return
#    project: (name) ->
#      if name is 'new'
#        console.log 'show project create'
#      else if name is 'mgr'
#        console.log 'show project mgr'
#      else if name
#        console.log 'show project viewr?/editor? for', name
#      return
    #calendar: (name) -> return
    #content: (name) -> return
    #report: (name) -> return

    signout: ->
      console.log 'sign out'
      ConsoleView.get().signout()
      @navigate 'signin', replace: true
      return

  { # exports
  find
  findAll
  View
  ConsoleView
  BoxView
  FrameView
  InnerFrameView
  NavListView
  ModalDialogView
  FormDialogView
  FormViewMixin
  SignInView
  Router
  }
