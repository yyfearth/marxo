"use strict"

define 'base', ['utils', 'models', 'lib/common'],
({find,  findAll,  tpl,  tplAll,  fill, DurationConvertor}, {Collection, Tenant, User, ROOT}) ->

  ## Shared Constant

  STATUS_CLS =
    started: 'label-success'
    monitoring: 'label-success'
    paused: 'label-warning'
    stopped: 'label-inverse'
    finished: 'label-info'
    error: 'label-important'

  ## Common Views

  class View extends Backbone.View
    initialize: (options) ->
      if @el?.tagName
        @el.view = @
        @$parent = @$el.parent()
        @$el.data 'view', @
      if options?.parent
        @parent = options.parent
        @parentEl = @parent.el
      @render = @render.bind @
      @_super_render = @_super_render.bind @
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
      @_super_render()
    _super_render: ->
      @rendered = true
      @el.view = @
      @$el.data 'view', @
      @
    remove: ->
      @trigger 'remove', @
      super

  class InnerFrameView extends View

  class FrameView extends View
    initialize: (options) ->
      super options
      @navEl = options.navEl or (find "#navbar a[href=\"##{@id}\"]")?.parentElement
      @
    switchTo: (innerframe) ->
      innerframe = @[innerframe] if typeof innerframe is 'string'
      if innerframe and innerframe instanceof InnerFrameView
        innerframe.$parent.append innerframe.$el
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
            view.$el.detach()
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
      super
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
      autoFocusEl = find '[autofocus]', @el
      @$el.on
        hide: (e) => if e.target is @el
          @trigger 'hide', @
          return
        hidden: (e) => if e.target is @el and false isnt @trigger 'hidden', @
          @callback()
          @goBack() if @goBackOnHidden and location.hash[1..] isnt @goBackOnHidden
          @reset()
          @$el.detach() if e.target is @el
          return
        show: (e) => if e.target is @el
          @$parent.append @$el if e.target is @el
          @trigger 'show', @
          return
        shown: (e) => if e.target is @el
          autoFocusEl?.focus()
          @trigger 'shown', @
          return
      # cancel dialog if hash changed
      @listenTo @router, 'route', =>
        # cancel if current hash isnt start with saved hash while popup
        @cancel() if @_hash and @_hash isnt location.hash.slice 0, @_hash.length
      @$el.detach()
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
      _save = @save?.bind? @
      _submit = (e) =>
        e.stopImmediatePropagation()
        if _save
          @submit _save
        else
          submit_btn.click()
        false
      $form = $(@form).on 'keypress', 'input', (e) -> _submit e if e.which is 13
      $form.on 'keydown', 'textarea', (e) -> _submit e if e.which is 13 and (e.ctrlKey or e.metaKey)
      # auto name key
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
    seperator: ':'
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
      @seperator = options.seperator ? @seperator
      @urlRoot = options.urlRoot ? @urlRoot
      @headerTitle = options.headerTitle or @headerTitle
      @defaultItem = options.defaultItem ? @defaultItem
      @itemClassName = options.itemClassName or @itemClassName
      @targetClassName = options.targetClassName or @targetClassName
      @allowEmpty = options.allowEmpty or @allowEmpty
      @emptyItem = options.emptyItem ? @emptyItem
      @listenTo @collection, 'reset add remove', @render
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
      @trigger 'updated', @
      super
    _clear: ->
      @el.innerHTML = ''
      @el.appendChild @_renderHeader null
      @el.appendChild @_renderItem @defaultItem if @defaultItem
      @el.appendChild @_renderItem @emptyItem if @allowEmpty and @emptyItem and @defaultItem isnt @emptyItem
      return
    _render: (models = @collection) ->
      models = models.fullCollection if models.fullCollection
      #console.log 'render models', models
      fragments = document.createDocumentFragment()
      _renderItem = @_renderItem.bind @
      models.forEach (model) ->
        fragments.appendChild _renderItem model
      @el.appendChild fragments
      @trigger 'updated', @
      return
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
      sep = @seperator
      root = @urlRoot
      if model.id?
        a.href = "##{root}#{sep}#{model.id}"
        a.textContent = model.get('title') or model.get('name')
        a.dataset.id = model.id
        $(a).data 'model', model
      else if model.href
        a.href = model.href
        a.textContent = model.title
      else if model is 'all'
        a.href = "##{root}#{sep}all"
        a.textContent = 'All'
      else if model is 'new' or model is 'empty'
        a.href = "##{root}#{sep}#{@emptyItem}"
        a.textContent = 'Empty'
      else
        console.dir model
        throw new Error 'unsupported item for list'
      li.className += ' active' if model is @defaultItem
      li.appendChild a
      li

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
  Tenant
  User
  DurationConvertor
  ROOT
  STATUS_CLS
  }
