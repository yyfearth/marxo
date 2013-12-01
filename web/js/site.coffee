"use strict"

ROOT = localStorage.ROOT ? 'http://masonwan.com/marxo/api'
ROOT = ROOT[...-1] if ROOT[-1..] is '/'

requirejs.config
  shim:
    'lib/jquery-ui':
      deps: ['lib/common']
    'lib/facebook':
      exports: 'FB'
  paths:
    'lib/facebook': '//connect.facebook.net/en_US/all'

define 'fb', ['lib/facebook'], (FB) =>
  FB.init
    appId: '213527892138380'
    scopes: 'email'
    status: true # check login status
    cookie: true # enable cookies to allow the server to access the session
    xfbml: false
  autoLogin = FB.autoLogin = (callback, tried) ->
    FB.getLoginStatus (response) ->
      switch response.status
        when 'connected'
          console.log 'connected', response
          callback response, FB
        when 'not_authorized'
          # the user is logged in but has not authed
          console.warn 'User cancelled login or did not fully authorize.', response
          alert 'You cancelled login or did not fully authorize.'
        else
          if tried
            alert 'Failed to login your Facebook account.'
          else FB.login (response) -> # the user isn't logged in
            console.log 'login', response
            autoLogin callback, true
      return
    FB
  FB

require ['lib/common'], ->
  console.log 'ver', 'site', 1

  $.ajaxSetup dataType: 'json'

  class User extends Backbone.Model
    urlRoot: ROOT + '/users'
    defaults:
      type: 'PARTICIPANT'

  class UserProfileView extends Backbone.View
    el: '#user_profile'
    events:
      'submit form': (e) ->
        e.preventDefault()
        $invalid = @$form.find(':invalid')
        if $invalid.length
          $invalid[0].select()
        else
          form = @$form[0]
          email = form.email.value
          password = form.password.value
          if email and password
            @_signInEmail email, password
          else
            form[if email then 'password' else 'email'].select()
        false
      'click .sign-in .btn-facebook': '_signInFB'
      'click a.dropdown-toggle': ->
        if form = @$form[0]
          setTimeout ->
            form.email.focus() if $(form).is ':visible'
          , 100
        return
      'click #sign_out': (e) ->
        e.preventDefault()
        @signout()
        false
      'click .btn-sign-up': ->
        @dialog.popup()
      'click #view_profile': ->
        @dialog.popup User.current
    initialize: (options) ->
      @$form = @$el.find('form')
      @dialog = new UserDialogView
      @$avatar = @$el.find('img#avatar')
      img = @$avatar.attr 'src'
      @$avatar.on 'error', -> @src = img
      @listenTo @dialog, 'signedup update', (user) -> @signin user.auth
      # auto login
      if auth = sessionStorage[' ']
        delete sessionStorage[' ']
        @signin auth, true
      super options
    _signInEmail: (email, password) ->
      require ['crypto'], ({hashPassword}) =>
        hash = hashPassword email, password
        @signin 'Basic ' + btoa "#{email}:#{hash}"
      return
    _signInFB: -> require ['fb'], (FB) =>
      FB.autoLogin (response) =>
        response = response.authResponse
        if response?.accessToken and response.expiresIn > 0
          @signin 'Basic ' + btoa "facebook:#{response.accessToken}"
        else
          console.warn 'User cancelled login or did not fully authorize.', response
          alert 'You cancelled login or did not fully authorize.'
        return
      return
    signin: (auth, silence) ->
      $inputs = @$el.find('input,button').prop 'disabled', true
      new User(id: 'me').fetch
        headers:
          Authorization: auth
        reset: true
        success: (user) =>
          console.log 'logined', user
          user.auth = auth
          if user.has 'email_md5'
            @_signedIn user
          else require ['crypto'], ({md5Email}) =>
            user.set 'email_md5', md5Email user.get 'email'
            @_signedIn user
          $inputs.prop 'disabled', false
          return
        error: (ignored, response) =>
          console.error 'sign-in failed', response
          alert 'Sign in failed' unless silence
          $inputs.prop 'disabled', false
      @
    signout: ->
      User.current = null
      sessionStorage.clear()
      location.reload()
      @
    _signedIn: (user) ->
      $el = @$el
      User.current = user
      @$avatar.attr 'src', "https://secure.gravatar.com/avatar/#{user.get 'email_md5'}?s=20&d=mm"
      $el.click().find('#username').text user.get 'name'
      $el.find('#sign_in_menu').remove()
      $el.find('#user_menu').removeClass 'tpl'
      $go_console = $el.find('#go_console')
      if user.has 'tenant_id'
        $go_console.prop 'href', ROOT + '/../console/'
      else
        $go_console.parent('li').remove()
      window.onunload = ->
        if User.current?.auth
          sessionStorage[' '] = User.current.auth
        else
          delete sessionStorage[' ']
        return
      return

  class UserDialogView extends Backbone.View
    el: '#user_editor'
    events:
      'click .btn-save': ->
        @$form.submit()
      'submit form': (e) ->
        e.preventDefault()
        @save() if @validate()
        false
    initialize: (options) ->
      super options
      $el = @$el
      $form = @$form = $el.find 'form'
      @form = $form[0]
      @btnSave = $el.find('.btn-save')[0]
      @$title = $el.find '.modal-title'
      @$sex = $form.find '[name=sex]'
      @$editOnly = $el.find '.edit-only'
      @$passwords = $form.find '[type=password]'
      @$avatar = $form.find '#user_avatar img'
      @$openAccounts = $el.find('#link_fb').parents('.control-group').find('.btn')
      @$openAccounts.each -> @_name = $(@).data 'name'
      @$openAccounts.click (e) =>
        $btn = $ e.target
        if $btn.val() # disconnect
          $btn.val ''
          @_setOauth true
          alert 'To disconnect please press "Save".\n\nAfter you save the change, you have to sign in with email and password next time.'
          @btnSave.focus()
        else
          name = $btn.data('name').toLowerCase()
          if name is 'facebook'
            @_signUpFB()
          else
            throw new Error "connect #{name} not supported yet"
        return
      @$el.find('[title]').tooltip placement: 'bottom', container: @$form, delay: 300
      @
    _setSex: (sex = '') ->
      $sex = @$sex.filter "[value='#{sex.toLowerCase()}']"
      $sex = @$sex.filter "[value='']" unless $sex.length
      if not $sex.hasClass 'active'
        @$sex.not($sex).removeClass 'active'
        $sex.addClass 'active'
      $sex
    _getSex: ->
      @$sex.filter('.active').attr('value').toUpperCase()
    _setOauth: (oauth = {}) -> @$openAccounts.each ->
      @value = oauth[@_name.toLowerCase()] or '' unless oauth is true
      if @value
        txt = 'Disconnect from'
      else if @form.email.disabled # edit
        txt = 'Connect to'
      else
        txt = 'Sign up with'
      @textContent = "#{txt} #{@_name} Account"
      console.log @, @textContent
      return
    _getOauth: ->
      oauth = {}
      @$openAccounts.each ->
        oauth[@_name.toLowerCase()] = @value if @value
      oauth
    fill: (attrs) ->
      form = @form
      form.email.value = attrs.email
      form.name.value = attrs.name
      form.desc.textContent = attrs.desc
      @_setSex attrs.sex
      @_setOauth attrs.oauth
      @
    read: ->
      data =
        sex: @_getSex()
        email: @form.email.value.trim()
        oauth: @_getOauth()
      for kv in @$form.serializeArray()
        data[kv.name] = kv.value.trim()
      password = @$passwords.val()
      data.password = password if password
      data.email = data.email.toLowerCase()
      data
    popup: (data) ->
      emailEl = @form.email
      @form.reset()
      @_auth = null
      if data # edit
        data = data.toJSON() if data instanceof User
        @$editOnly.show()
        @$title.text "User: #{data.name or data.email}"
        @fill data
        @$passwords.removeAttr 'required'
        disabled = true
        @$avatar.attr 'src', "https://secure.gravatar.com/avatar/#{data.email_md5}?s=200&d=mm"
        setTimeout ->
          emailEl.focus()
        , 200
      else
        @$title.text 'Sign Up'
        @$editOnly.hide()
        @$passwords.attr 'required', 'required'
        @_setOauth()
        disabled = false
      emailEl.disabled = disabled
      @$el.modal 'show'
      @
    validate: ->
      $invalid = @$form.find(':invalid')
      if $invalid.length
        $invalid[0].select()
        return false
      psw = @$passwords[0].value
      psw2 = @$passwords[1].value
      if (psw or psw2) and psw isnt psw2
        @$passwords[1].select()
        alert 'Passwords are not matched!'
        return false
      true
    _signUpFB: -> require ['fb'], (FB) =>
      FB.autoLogin (response) =>
        response = response.authResponse
        if response?.accessToken and response.expiresIn > 0
          @_auth = 'Basic ' + btoa "facebook:#{response.accessToken}"
          if @form.email.disabled # edit
            data = @read()
            data.oauth =
              facebook: response.userID
            @fill data
            console.log 'facebook connected', response
            setTimeout =>
              alert 'Facebook account is bound, please click "Save".'
              @btnSave.focus()
            , 100
          else FB.api '/me', (response) => # sign up
            @fill
              email: response.email
              name: response.name
              sex: response.gender
              oauth:
                facebook: response.id
            setTimeout =>
              alert 'Facebook account is bound, please setup a password then click "Save".'
              @$passwords[0].focus()
            , 100
            console.log 'facebook connected', response
        else
          console.warn 'User cancelled login or did not fully authorize.', response
          alert 'You cancelled login or did not fully authorize.'
        return
    _save: (data) ->
      user = User.current
      if user instanceof User
        # update
        throw new Error 'Emails not matched! Somebody hacked that?' if data.email isnt user.get('email')
        console.log 'save user', data
        password_changed = Boolean user.get 'password'
        user.save data,
          headers:
            Authorization: user.auth
          success: (user) =>
            if password_changed
              User.current = null
              alert 'You password is changed, please login again.'
              sessionStorage.clear()
              location.reload()
            else
              @trigger 'updated', user
            @$el.modal 'hide'
            return
          error: (ignored, xhr) ->
            console.error 'sign up failed', xhr.responseJSON
            alert 'Save user profile failed! '
            return
      else # sign up
        console.log 'sign up user', data
        new User(email: data.email).save data,
          success: (user) =>
            user.auth = @_auth or 'Basic ' + btoa "#{user.get 'email'}:#{data.password}"
            @trigger 'signedup', user
            @$el.modal 'hide'
            return
          error: (ignored, xhr) ->
            console.error 'sign up failed', xhr.responseJSON
            alert 'Sign up failed! '
            return
      return
    save: ->
      data = @data = @read()
      unless data.password
        @_save data
      else require ['crypto'], (crypto) =>
        #console.log 'crypto', data.email, data.password
        data.password = crypto.hashPassword data.email, data.password
        @_save data
      @

  new UserProfileView

  return
