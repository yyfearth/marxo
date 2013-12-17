"use strict"

define 'console', ['base'], ({find, findAll, View, FrameView, Tenant, User}) ->

  class ConsoleView extends View
    el: '#main'
    user: sessionStorage.user
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
      'mouseenter #navbar ul.nav > li': (e) -> # show menu when mouse enter
        e.currentTarget.classList.add 'hover'
      'mouseleave #navbar ul.nav > li': (e) -> # hide menu when mouse out
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
    initialize: (options) ->
      # init user
      user = null
      try
        if @user
          user = new User JSON.parse @user
          user = null unless user.has 'email'
      console.log 'load from session', user
      @user = user
      @avatarEl = find 'img#avatar'
      @usernameEl = find '#username'
      @avatarEl.dataset.src ?= @avatarEl.src
      @avatarEl.onerror = -> @src = @dataset.src
      # init frames
      @frames = el: find '#frames', @el
      findAll('#frames > .frame', @el).forEach (frame) =>
        $(frame).detach()
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
      super options
    _fixStyle: ->
      h = @navContainer.clientHeight or 41
      @framesContainer.style.top = h + 'px'
    remove: ->
      $(window).off 'resize', @_fixStyle
      super
    showFrame: (frame, name, sub) ->
      frame = @frames[frame]
      $frames = $ @frames.el
      return unless frame?
      # console.log 'frame', frame
      unless frame.el.classList.contains 'active'
        oldFrame = find '.frame.active', @el
        if oldFrame
          oldFrame.classList.remove 'active'
          view = $.data oldFrame, 'view'
          if view then setTimeout ->
            view.trigger 'deactivate'
            view.$el.detach()
          , 10
        find('#navbar li.active')?.classList.remove 'active'
        $frames.append frame.el
        frame.el.classList.add 'active'
        $(window).resize()
        # console.log 'frame', frame
      if frame instanceof FrameView
        frame.trigger 'activate'
        frame.open? name, sub
      else
        console.log 'load module:', frame.id
        require [frame.id], (TheFrameView) =>
          frame = @frames[frame.id] = new TheFrameView frame
          frame.render()
          frame.trigger 'activate'
          frame.open? name, sub

      $frame = @$frames.filter("[data-frame='#{frame.id}']").addClass 'active'
      $target = $frame.find("[data-inner-frame='#{name}']").addClass 'active'
      @$frames.not($frame).removeClass 'active'
      @$frames.find(".active[data-inner-frame]").not($target).removeClass 'active'
      @
    signout: ->
      sessionStorage.clear()
      @user = User.current = null
      @router.clear()
      @hide()
      @trigger 'signout'
      location.hash = 'signin'
      location.reload()
      @
    signin: (user, remember) ->
      @user = User.current = user
      if remember
        u = user.toJSON()
        delete u.password
        sessionStorage.user = JSON.stringify u
        console.log 'logged in', u
      else
        delete sessionStorage.user
      # update avatar
      @avatarEl.src = "https://secure.gravatar.com/avatar/#{user.get 'email_md5'}?s=20&d=mm"
      $(@usernameEl).text user.name()
      @show()
    show: ->
      @_hide_ts = clearTimeout @_hide_ts if @_hide_ts
      @$parent.append @$el
      @el.style.visibility = 'visible'
      @el.classList.add 'active'
      @el.style.opacity = 1
      @
    hide: ->
      @el.classList.remove 'active'
      @_hide_ts = setTimeout =>
        @_hide_ts = null
        @el.style.visibility = 'hidden'
        @$el.detach()
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
      remember = @form.remember.checked = localStorage.marxo_sign_in_remember is 'true'
      @form.email.value = localStorage.marxo_sign_in_email if remember
      # auto sign in
      console = ConsoleView.get()
      if console.user instanceof User
        user = console.user
        console.user = {}
        @hide()
        @_validateUser user
      else
        @_fail()
      @
    submit: (e) ->
      e.preventDefault()
      email = @form.email.value.trim()
      password = @form.password.value.trim()
      unless email
        @form.email.focus()
        alert 'Please fill out the Email!'
      else unless /.+@.+\..+/.test email
        @form.email.select()
        alert 'The Email is invalid!'
      else if not password
        @form.password.focus()
        alert 'Password is required!'
      else
        @_signIn email, password
      false
    _fail: (msg) ->
      ConsoleView.get().user = User.current = null
      sessionStorage.clear()
      @router.navigate 'signin', replace: true unless location.hash is '#signin'
      @show()
      @_disable false
      alert msg if msg
      return
    _disable: (val) -> $(@form.elements).prop 'disabled', val
    _signIn: (email, password) ->
      @_disable true
      require ['crypto'], ({hashPassword, md5Email}) =>
        email = email.toLowerCase()
        hash = hashPassword email, password
        user = new User
          email: email
          credential: 'Basic ' + btoa "#{email}:#{hash}"
          email_md5: md5Email email
        console.log 'login with', email, hash
        @_validateUser user
        return
      return
    _validateUser: (user) ->
      email = user.get 'email'
      auth = user.get 'credential'
      email_md5 = user.get 'email_md5'
      unless email and auth and email_md5
        console.warn 'saved user is not valid', email, auth, email_md5
        @_fail()
      else new User({id: 'me', email}).fetch
        headers:
          Authorization: auth
        reset: true
        success: (user) =>
          if user.has('password') and user.get('password') isnt atob(auth[6..]).slice(email.length + 1) # for test only
            @_fail '(TEST ONLY) Password not correct'
          else if user.has('tenant_id') and email is user.get 'email'
            user.set 'email_md5', email_md5
            user.set 'credential', auth
            @signedIn user
          else
            @_fail 'User not exist or email and password are not matched.'
            @form.password.select()
          return
        error: (ignored, response) =>
          console.error 'sign-in failed', response
          @_fail 'Sign in failed.\nUser not exist or email and password are not matched.'
          return
      return
    signedIn: (user) -> # test data only
      @trigger 'success', user
      @hide()
      remember = localStorage.marxo_sign_in_remember = @form.remember.checked
      localStorage.marxo_sign_in_email = if remember then @form.email.value else ''
      ConsoleView.get().signin user, remember
      @router.back fallback: 'home' if /signin/i.test location.hash
      @
    show: ->
      @_hide_ts = clearTimeout @_hide_ts if @_hide_ts
      unless @el.classList.contains 'active'
        @el.style.opacity = 0
        @el.style.display = 'block'
        @_disable false
        @$parent.append @$el
        setTimeout =>
          @el.classList.add 'active'
          @el.style.opacity = 1
        , 1
      @
    hide: ->
      @el.classList.remove 'active'
      @el.style.opacity = 0
      @_hide_ts = setTimeout =>
        @form.password.value = ''
        @_disable false
        @el.style.display = 'none'
        @$el.detach()
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
      'project/:id(/link/:link)(/node/:node)(/action/:action)(/:edit)': (id, link, node, action, edit) ->
        @show 'project', id, {link, node, action, edit: edit is 'edit'}
      'content/:id(/:action)': (id, action) ->
        @show 'content', id, action
      'config/:name(/:sub)': (name, sub) ->
        @show 'config', name, sub
      'event/calendar(/:id)': (id) ->
        @show 'event', 'calendar', id
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

      @on 'route', =>
        @_last = @_cur
        @_cur = Backbone.history.fragment

    back: (opt = {}) ->
      opt.trigger ?= true
      opt.replace ?= false
      if opt.real
        hash = location.hash
        history.go -1
        if typeof opt.fallback is 'string' then setTimeout ->
          @navigate opt.fallback, opt if location.hash is hash
        , 100
      else if @_last
        @navigate @_last, opt
      else if typeof opt.fallback is 'string'
        @navigate opt.fallback, opt
      else
        console.log 'failed to go back for no last record'
      @
    clear: ->
      @_last = @_cur = null
      @
    show: (frame, name, sub) ->
      unless ConsoleView.get().user?
        @navigate 'signin', replace: true
      else
        console.log 'route', frame, (name or ''), sub or ''
        _frame = @frames[frame]
        if _frame._cur? and not name
          name = _frame._cur?._name
          @navigate "##{frame}/#{name}", replace: true if name
        unless /^new$|^signout$/.test name
          _cur = _frame[name]
          _cur = _name: name unless _cur? or not name or /^content$/.test(frame)
          _frame._cur = _cur
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

  # reg router for all views
  View::router = Router.get()

  { # exports
  findAll
  ConsoleView
  SignInView
  Router
  }
