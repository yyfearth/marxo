"use strict"

define 'console', ['base'], ({find, findAll, View, FrameView, Tenant, User}) ->

  class ConsoleView extends View
    el: '#main'
    user: sessionStorage.user
    tenant: sessionStorage.tenant
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
    initialize: ->
      # init user
      user = tenant = null
      try
        if @user
          user = new User JSON.parse @user
          user = null unless user.has 'email'
        if @tenant
          tenant = new Tenant JSON.parse @tenant
          tenant = null unless tenant.has 'name'
      @user = user
      @tenant = tenant
      @avatarEl = find 'img#avatar'
      @usernameEl = find '#username'
      @avatarEl.dataset.src ?= @avatarEl.src
      @avatarEl.onerror = -> @src = @dataset.src
      # init frames
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
      unless frame.el.classList.contains 'active'
        oldFrame = find '.frame.active', @el
        if oldFrame
          oldFrame.classList.remove 'active'
          view = $.data oldFrame, 'view'
          if view then setTimeout ->
            view.trigger 'deactivate'
          , 10
        find('#navbar li.active')?.classList.remove 'active'
        frame.el.classList.add 'active'
        $(window).resize()
        console.log 'frame', frame
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
      # TODO: real sign out
      delete sessionStorage.user
      delete sessionStorage.tenant
      @user = @tenant = User.current = Tenant.current = null
      $.ajaxSetup
        headers:
          Accept: 'application/json'
          Authorization: null
      SignInView.get().show()
      @hide()
      @trigger 'signout'
      @
    signin: (user, tenant, remember) ->
      @user = User.current = user
      @tenant = Tenant.current = tenant
      if remember
        u = user.toJSON()
        delete u.password
        sessionStorage.user = JSON.stringify u
        sessionStorage.tenant = JSON.stringify tenant.toJSON()
        console.log 'logged in', u
      else
        delete sessionStorage.user
        delete sessionStorage.tenant
      # set ajax basic auth
      $.ajaxSetup
        headers:
          Accept: 'application/json'
          Authorization: 'Basic ' + user.get 'credential'
      # update avatar
      @avatarEl.src = "https://secure.gravatar.com/avatar/#{user.get 'email_md5'}?s=20&d=mm"
      $(@usernameEl).text user.fullname()
      @show()
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
      {user, tenant} = ConsoleView.get()
      if (user instanceof User) and (tenant instanceof Tenant)
        @signedIn user, tenant
      else
        @show()
      @
    submit: (e) -> # fake
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
    _disable: (val) -> $(@form.elements).prop 'disabled', val
    _signIn: (email, password) ->
      @_disable true
      require ['crypto'], ({hashPassword, md5Email}) =>
        # fake validation
        email = email.toLowerCase()
        hash = hashPassword email, password
        auth = 'Basic ' + btoa "#{email}:#{hash}"
        user = new User {email}
        user.fetch
          headers:
            Authorization: auth
          success: (user) =>
            console.log 'login with', email, hash
            if user.has('tenant_id') and email is user.get 'email'
              user.set 'email_md5', md5Email email
              user.set 'credential', btoa "#{email}:#{hash}"
              tenantId = user.get 'tenant_id'
              new Tenant(id: tenantId).fetch
                headers:
                  Authorization: auth
                success: (tenant) =>
                  if tenant.id is tenantId
                    @signedIn user, tenant
                  else
                    @_disable false
                    alert 'Failed to get tenant profile of this user'
                error: =>
                  @_disable false
                  alert 'Failed to get tenant profile'
            else
              @form.password.select()
              @_disable false
              alert 'User not exist or email and password are not matched.'
            return
          error: (ignored, response) =>
            @_disable false
            alert 'Sign in failed: ' + response
            return
      return
    signedIn: (user, tenant) -> # test data only
      @trigger 'success', user, tenant
      @hide()
      localStorage.marxo_sign_in_remember = remember = @form.remember.checked
      localStorage.marxo_sign_in_email = if remember then @form.email.value else ''
      ConsoleView.get().signin user, tenant, remember
      @router.back fallback: 'home' if /signin/i.test location.hash
      @
    show: ->
      @el.style.opacity = 0
      @el.style.display = 'block'
      @_disable false
      setTimeout =>
        @el.classList.add 'active'
        @el.style.opacity = 1
      , 1
      @
    hide: ->
      @el.classList.remove 'active'
      @el.style.opacity = 0
      setTimeout =>
        @form.password.value = ''
        @_disable false
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
    show: (frame, name, sub) ->
      unless ConsoleView.get().user?
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

  # reg router for all views
  View::router = Router.get()

  { # exports
  ConsoleView
  SignInView
  Router
  }
