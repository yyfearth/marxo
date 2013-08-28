"use strict"

define 'console', ['base'], ({find, findAll, View, FrameView}) ->

  class ConsoleView extends View
    el: '#main'
    events:
      'touchstart #navbar .dropdown > a': (e) ->
        $el = $(e.currentTarget).parent()
        if $el.hasClass 'hover' # if already touched
          setTimeout ->
            $el.removeClass 'hover'
          , 500
          true
        else # show menu after 1st touch
          e.preventDefault()
          e.stopPropagation()
          @$dropdowns.not($el).removeClass 'hover'
          $el.addClass 'hover'
          @$el.one 'touchstart', (e) ->
            $el.removeClass 'hover' unless $el.has(e.target).length
          false
      'mouseenter #navbar .dropdown': (e) -> # show menu when mouse enter
        e.currentTarget.classList.add 'hover'
      'mouseleave #navbar .dropdown': (e) -> # hide menu when mouse out
        e.currentTarget.classList.remove 'hover'
      'click #navbar .dropdown-menu li': (e) -> # hide menu when click
        $(e.currentTarget).parents('.dropdown').removeClass 'hover'
      'click #navbar .dropdown > a': (e) ->
        $el = $(e.currentTarget).parent()
        if $el.hasClass 'hover'
          _t = setTimeout ->
            _t = null
            $el.removeClass 'hover'
          , 500
          $(e.currentTarget).one 'mouseleave', ->
            _t = clearTimeout(_t) if _t
        true
    @get: -> # singleton
      @instance = new @ unless @instance?
      @instance
    initialize: ->
      @frames = {}
      findAll('.frame', @el).forEach (frame) =>
        @frames[frame.id] =
          id: frame.id
          el: frame
          parent: @
      # fix style
      @navContainer = find '#navbar', @el
      @framesContainer = find '#frames', @el
      @_fixStyle = @_fixStyle.bind @
      $(window).on 'resize', @_fixStyle
      @$frames = $('#navbar [data-frame]')
      @$dropdowns = $(@navContainer).find('.dropdown')
      # init tooltips
      @$el.tooltip selector: '[title]'
      @
    _fixStyle: ->
      h = @navContainer.clientHeight or 41
      @framesContainer.style.top = h + 'px'
    remove: ->
      $(window).off 'resize', @_fixStyle
      super
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
      unless frame.el.classList.contains 'active'
        find('#main .frame.active')?.classList.remove 'active'
        find('#navbar li.active')?.classList.remove 'active'
        frame.el.classList.add 'active'
        $(window).resize()

      $frame = @$frames.filter("[data-frame='#{frame.id}']").addClass 'active'
      $target = $frame.find("[data-inner-frame='#{name}']").addClass 'active'
      @$frames.not($frame).removeClass 'active'
      @$frames.find(".active[data-inner-frame]").not($target).removeClass 'active'
      @
    signout: ->
      # TODO: sign out
      delete sessionStorage.user
      SignInView.get().show()
      @hide()
      @trigger 'signout'
      @

    show: ->
      @el.style.visibility = 'visible'
      @el.classList.add 'active'
      @el.style.opacity = 1
      @
    hide: ->
      @el.classList.remove 'active'
      setTimeout =>
        @el.style.visibility = 'hidden'
      , SignInView::delay
      @

  class SignInView extends View
    el: '#signin'
    @get: -> # singleton
      @instance = new @ unless @instance?
      @instance
    events:
      'submit form': 'submit'
    delay: 500
    initialize: (options) ->
      super options
      @form = find 'form', @el
      @form.remember.checked = remember = localStorage.marxo_sign_in_remember is 'true'
      @form.email.value = localStorage.marxo_sign_in_email if remember
      # auto sign in
      if (sessionStorage.user)
        @signedIn()
      else
        @show()
      @
    submit: -> # fake
      console.log 'sign in'
      @signedIn()
      false
    signedIn: -> # debug only
      user =
        id: 'test', name: 'test'
      sessionStorage.user = JSON.stringify user
      @trigger 'success', user
      @hide()
      ConsoleView.get().show()
      # Router.get().navigate 'home'
      location.hash = '' if /signin/i.test location.hash
      @_remember()
      @
    _remember: ->
      localStorage.marxo_sign_in_remember = remember = @form.remember.checked
      localStorage.marxo_sign_in_email = if remember then @form.email.value else ''
      unless remember # clean session after leave
        $(window).unload -> delete sessionStorage.user
    show: ->
      @el.style.opacity = 0
      @el.style.display = 'block'
      setTimeout =>
        @el.classList.add 'active'
        @el.style.opacity = 1
      , 1
      @
    hide: ->
      @el.classList.remove 'active'
      @el.style.opacity = 0
      setTimeout =>
        @el.style.display = 'none'
      , @delay
      @

  ## Router

  class Router extends Backbone.Router
    @get: -> # singleton
      @instance = new @ unless @instance?
      @instance
    routes:
      'workflow/:id(/link/:link)(/node/:node)(/action/:action)': (id, link, node, action) ->
        @show 'workflow', id, {link, node, action}
      'project/:id(/link/:link)(/node/:node)(/action/:action)': (id, link, node, action) ->
        @show 'project', id, {link, node, action}
      'content/:id(/:action)': (id, action) ->
        @show 'content', id, action
      'config/service(/:name)': (name) ->
        @show 'config', 'service', name
      'signout': 'signout'
    constructor: (options) ->
      super options
      @route '', 'home', =>
        @navigate 'home', replace: true
        @show 'home'
      @route 'signin', 'signin', => return

      @frames = {}
      for frameMenu in findAll '[data-frame]', find '#navbar'
        frame = frameMenu.dataset.frame
        _frame = @frames[frame] =
          _name: frame
        for innerMenu in findAll '[data-inner-frame]', frameMenu
          innerFrame = innerMenu.dataset.innerFrame
          _inner = _frame[innerFrame] =
            _name: innerFrame
          _frame._cur = _inner if innerMenu.dataset.default

        do (frame) => @route frame + '(/:name)(/)', frame, (name) =>
          @show frame, name
      @

    show: (frame, name, sub) ->
      unless sessionStorage.user
        @navigate 'signin', replace: true
      else
        console.log 'route', frame, (name or ''), sub or ''
        _frame = @frames[frame]
        if _frame._cur? and not name
          name = _frame._cur?._name
          if name then @navigate "##{frame}/#{name}", replace: true
        _frame._cur = _frame[name] if name
        @frames._cur = _frame
        #console.log 'frames data', frame, name, @frames

        ConsoleView.get()?.showFrame frame, name, sub
        handler = @[frame]
        handler.call @, name if handler?
      @

    signout: ->
      console.log 'sign out'
      ConsoleView.get().signout()
      @navigate 'signin', replace: true
      @

  { # exports
  ConsoleView
  SignInView
  Router
  }
