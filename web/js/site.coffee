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
  FB

require ['lib/common'], ->
  console.log 'ver', 'site', 1

  class User extends Backbone.Model
    urlRoot: ROOT + '/users'
    fullname: ->
      if @has 'full_name'
        @get 'full_name'
      else if @has('first_name') and @has('last_name')
        "#{@get 'first_name'} #{@get 'last_name'}"
      else if @has 'name'
        @get 'name'
      else
        @get('first_name') or @get('last_name') or null
    sync: (method, model, options = {}) ->
      options.dataType = 'json'
      options.headers ?= {}
      options.headers.Authorization ?= @get('credential') or ''
      super method, model, options

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
      # auto login
      if sessionStorage.user
        try
          user = new User JSON.parse sessionStorage.user
          auth = user.get 'credential'
        catch e
          console.error 'pause user failed', e
          delete sessionStorage.user
        @signin auth if auth
      super options
    _signInEmail: (email, password) ->
      require ['crypto'], ({hashPassword}) =>
        hash = hashPassword email, password
        auth = 'Basic ' + btoa "#{email}:#{hash}"
        @signin auth
      return
    _signInFB: -> require ['fb'], (FB) =>
      FB.login (response) ->
        response = response.authResponse
        if response?.accessToken and response.expiresIn > 0
          @signin 'Basic ' + btoa "facebook:#{response.accessToken}"
        else
          console.warn 'User cancelled login or did not fully authorize.', response
          alert 'You cancelled login or did not fully authorize.'
        return
      return
    signin: (auth) ->
      new User(id: 'me').fetch
        headers:
          Authorization: auth
        reset: true
        success: (user) =>
          console.log 'logined', user
          user.set 'credential', auth
          if user.has 'email_md5'
            @_signedIn user
          else require ['crypto'], ({md5Email}) =>
            user.set 'email_md5', md5Email user.get 'email'
            @_signedIn user
          return
        error: (ignored, response) =>
          console.error 'sign-in failed', response
          alert 'Sign in failed'
      @
    signout: ->
      sessionStorage.clear()
      location.reload()
      @
    _signedIn: (user) ->
      $el = @$el
      User.current = user
      sessionStorage.user = JSON.stringify user.toJSON()
      @$avatar.attr 'src', "https://secure.gravatar.com/avatar/#{user.get 'email_md5'}?s=20&d=mm"
      $el.click().find('#username').text user.fullname()
      $el.find('#sign_in_menu').remove()
      $el.find('#user_menu').removeClass 'tpl'
      $go_console = $el.find('#go_console')
      if user.has 'tenant_id'
        $go_console.prop 'href', ROOT + '/../console/'
      else
        $go_console.parent('li').remove()
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
      @$title = $el.find '.modal-title'
      @$sex = $form.find '[name=sex]'
      @$editOnly = $el.find '.edit-only'
      @$passwords = $form.find '[type=password]'
      @$avatar = $form.find '#user_avatar img'
      @$openAccounts = $el.find('#link_fb').parents('.control-group').find('.btn')
      @$openAccounts.each -> @_name = $(@).data 'name'
      @
    _setSex: (sex = '') ->
      $sex = @$sex.filter "[value='#{sex.toLowerCase()}']"
      if $sex.length and not $sex.hasClass 'active'
        @$sex.not($sex).removeClass 'active'
        $sex.addClass 'active'
      $sex
    _getSex: ->
      @$sex.filter('.active').attr('value').toUpperCase()
    fill: (attrs) ->
      form = @form
      for name, value of attrs
        $input = $ form[name]
        $input.val value if $input.is 'input'
      @_setSex attrs.sex
      @
    read: ->
      data =
        sex: @_getSex(), email: @form.email.value.trim()
      for kv in @$form.serializeArray()
        data[kv.name] = kv.value.trim()
      password = @$passwords.val()
      data.password = password if password
      data
    popup: (data) ->
      emailEl = @form.email
      if data # edit mode
        data = data.toJSON() if data instanceof User
        @$editOnly.show()
        if data.first_name or data.last_name
          @$title.text "User: #{data.first_name} #{data.last_name}"
        else
          @$title.text "User: #{data.email}"
        @fill data
        @$passwords.removeAttr 'required'
        disabled = true
        btnTxt = 'Link'
        @$avatar.attr 'src', "https://secure.gravatar.com/avatar/#{data.email_md5}?s=200&d=mm"
        setTimeout ->
          emailEl.focus()
        , 200
      else
        @$title.text 'Sign Up'
        @$editOnly.hide()
        @$passwords.attr 'required', 'required'
        disabled = false
        btnTxt = 'Sign up with'
      emailEl.disabled = disabled
      @$openAccounts.each -> $(@).text "#{btnTxt} #{@_name} Account"
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
    _save: (data) ->
      user = User.current
      if user instanceof User
        # update
        throw new Error 'Emails not matched! Somebody hacked that?' if data.email isnt user.get('email')
        console.log 'save user', data
        user.save data,
          success: (user) =>
            @trigger 'updated', user
            @$el.modal 'hide'
            return
          error: (ignored, xhr) ->
            console.error 'sign up failed', xhr.responseJSON
            alert  'Save user profile failed! '
            return
      else # sign up
        console.log 'sign up user', data
        new User(email: data.email).save data,
          success: (user) =>
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
