"use strict"

ROOT = localStorage.ROOT ? 'http://masonwan.com/marxo/api'

requirejs.config
  shim:
    'lib/jquery-ui':
      deps: ['lib/common']
    'lib/facebook':
      exports: 'FB'
  paths:
    'lib/facebook': '//connect.facebook.net/en_US/all'
  config:
    config:
      FB_APP_ID: '213527892138380'
      FB_SCOPES: 'publish_actions, email, read_stream'

require ['lib/common'], ->
  console.log 'ver', 'site', 0

  class User extends Backbone.Model
    urlRoot: ROOT + '/users'
    idAttribute: 'email'
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
      'click #sign_out': (e) ->
        e.preventDefault()
        @signout()
        false
    initialize: (options) ->
      @$form = @$el.find('form')
      @$avatar = @$el.find('img#avatar')
      img = @$avatar.attr 'src'
      @$avatar.on 'error', -> @src = img
      # auto login
      if sessionStorage.user
        try
          user = new User JSON.parse sessionStorage.user
          email = user.get 'email'
          auth = user.get 'credential'
        catch e
          console.error 'pause user failed', e
          delete sessionStorage.user
        @signin email, auth if email and auth
      super options
    _signInEmail: (email, password) ->
      require ['crypto'], ({hashPassword}) =>
        hash = hashPassword email, password
        auth = 'Basic ' + btoa "#{email}:#{hash}"
        @signin email, auth
      return
    _signInFB: ->
      # TODO: sign in using fb
      return
    signin: (email, auth) ->
      new User({email}).fetch
        headers:
          Authorization: auth
        success: (user) =>
          if email is user.get 'email'
            console.log 'logined', user
            user.set 'credential', auth
            if user.has 'email_md5'
              @_signedIn user
            else require ['crypto'], ({md5Email}) =>
              user.set 'email_md5', md5Email email
              @_signedIn user
          else
            console.error 'sign-in failed', 'email not matched'
            alert 'Sign in failed'
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


  new UserProfileView

  return
