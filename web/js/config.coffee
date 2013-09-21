"use strict"

FB_APP_ID = '503617123064248'

define 'config', ['base', 'manager', 'models'],
({
find
findAll
View
FrameView
InnerFrameView
ModalDialogView
FormViewMixin
FormDialogView
}, {
ManagerView
ProjectFilterView
}, {
Tenant
Publisher
Publishers
Service
}) ->

  class ConfigFrameView extends FrameView
    initialize: (options) ->
      super options
      @profile = new TenantProfileView el: '#tenant_profile', parent: @
      @manager = new UserManagemerView el: '#user_manager', parent: @, list: @list
      @connector = new ServiceConnectorView el: '#service_connector', parent: @
      @
    open: (name, sub) ->
      switch name
        when 'users'
          @switchTo @manager
        when 'tenant'
          @switchTo @profile
        when 'service'
          @switchTo @connector
          @connector.open sub if sub
        else
          throw 'empty or unknown sub action for config frame ' + name
      @

  # Services

  class ServiceConnectorView extends InnerFrameView
    initialize: (options) ->
      super options
      @facebookView = new FacebookStatusView el: find '.btn-facebook', @el
      @twitterView = new ServiceStatusView el: find '.btn-twitter', @el
      @emailView = new ServiceStatusView el: find '.btn-email', @el
    open: (service) ->
      switch service
        when 'facebook'
          @facebookView.click false
        when 'twitter', 'email'
        # TODO: click twitter and show email
          console.log 'connect service details', service
        else
          throw 'unknown service'
    render: ->
      @facebookView.render()
      @twitterView.render()
      @emailView.render()
      @

  class ServiceStatusView extends View
    initialize: (options) ->
      @events ?= {}
      @events.click ?= 'click'
      @render = @render.bind @
      @_render = @_render.bind @
      @click = @click.bind @
      @changed = @changed.bind @
      @_default_text = @$el.text()
      @popup ?= new ModalDialogView el: find "##{@service}_status"
      super options
    click: (auto_connect) ->
      if @model.connected?()
        @showStatus()
      else if auto_connect is false
        @el.focus()
      else
        @connect()
      @
    changed: (auth) ->
      auth ?=
        service: @service
        status: 'disconnected'
      @model.clear().save auth, success: @render
      @
    render: (model = @model) ->
      model?.fetch success: @_render, error: @_render
      super
    showStatus: ->
      # fill form
      data = @model.toJSON()
      data.title = @$el.text()
      data.expires_at = new Date data.expires_at
      dlg = @popup.el
      for name, value of data
        el = find "[name='#{name}']", dlg
        $(el).text value
        el.href = value if el?.href

      @popup.popup data, (action, data) =>
        switch action
          when 'disconnect'
            @disconnect()
          when 'save'
            @changed data
          else
            console.log @service, 'status popup', action
      @
    _render: (@model) ->
      text = @service.charAt(0).toUpperCase() + @service[1..]
      field = @text_field and @model?.get @text_field
      @$el.removeClass 'connected disconnected'
      switch @model?.get 'status'
        when 'disconnected'
          cls = 'disconnected'
          text += ' Disconnected'
          text += ' from ' + field if field
        when 'connected'
          cls = 'connected'
          text += ' Connected'
          text += ' as ' + field if field
        else
          cls = ''
          text = @_default_text
      @$el.addClass cls if cls
      @$el.text text
      @

  class FacebookStatusPopup extends ModalDialogView
    el: '#facebook_status'
    goBackOnHidden: 'config/service'
    events:
      'click .btn-disconnect': 'disconnect'
    disconnect: ->
      if confirm 'Are you sure to disconnect your Facebook account?\n\nIt will cause Marxo Facebook Service unable to send and track messages!'
        @callback 'disconnect'
        @hide()
      @

  class FacebookStatusView extends ServiceStatusView
    service: 'facebook'
    copy_fields: ['username', 'link', 'locale', 'timezone'] # + fullname(name)
    text_field: 'fullname'
    cfg:
      appId: FB_APP_ID # App ID
      status: false # check login status
      cookie: false # enable cookies to allow the server to access the session
      xfbml: true
    model: new Service(service: 'facebook')
    popup: new FacebookStatusPopup
    FB: (callback) ->  # lazy init
      if @_FB?
        callback.call @, @_FB
      else require ['lib/facebook'], (@_FB) =>
        @_FB.init @cfg
        callback.call @, @_FB
      @
    connect: (callback = @changed) ->
      fields = @copy_fields
      @FB (FB) -> FB.login (response) ->
        response = response.authResponse
        if response?.accessToken and response.expiresIn > 0
          auth =
            user_id: response.userID
            access_token: response.accessToken
            expires_at: new Date Date.now() + 1000 * response.expiresIn
            service: 'facebook'
            status: 'connected'
          FB.api '/me', (response) ->
            auth[key] = response[key] for key in fields
            auth.fullname = response.name
            console.log 'facebook connected', auth
            callback auth
        else
          console.warn 'User cancelled login or did not fully authorize.', response
          callback null
          alert 'You cancelled login or did not fully authorize.'
      @
    #disable: ->
    #  if confirm 'Are you sure to stop using Facebook service?\n\nIt will cause Marxo Service stop to send messages and track the responses!'
    #    @model.destroy()
    #    @render()
    #  @
    disconnect: (callback = @changed) ->
      @FB (FB) -> FB.getLoginStatus (response) ->
        if response.status is 'connected'
          FB.logout (response) ->
            # user is now logged out
            console.log 'logout', response
            callback null
        else
          callback null
      @

  # Tenant Profile
  class TenantProfileView extends InnerFrameView
    @acts_as FormViewMixin
    model: new Tenant(id: 0) # fake
    events:
      'click .btn-reset': 'reset'
    # TODO: reload button
    initialize: (options) ->
      super options
      @initForm()
      @form.onreset = =>
        setTimeout =>
          @fill @model.attributes
        , 1
      @on 'submit', @save.bind @
      @
    save: ->
      $btn = $(@_submit_btn)
      $btn.button 'loading'
      data = @read()
      console.log 'save', data
      @model.save data, success: ->
        $btn.button 'reset'
      @
    render: ->
      super
      @model.fetch success: (data) =>
        console.log 'fetch tenant', data.attributes
        @model = data
        @fill data.attributes
      @

  # User Editor
  class UserEditor extends FormDialogView
    el: '#user_editor'
    initialize: (options) ->
      super options
      @$title = $ find '.modal-title', @el
      @$sex = $ findAll '[name=sex]', @form
      @
    _setSex: (sex = '') ->
      $sex = @$sex.filter "[value='#{sex}']"
      if $sex.length and not $sex.hasClass 'active'
        @$sex.not($sex).removeClass 'active'
        $sex.addClass 'active'
      $sex
    _getSex: ->
      @$sex.filter('.active').attr 'value'
    fill: (data) ->
      super data
      @_setSex data.sex
      @
    reset: ->
      @$title.text 'Create User'
      @form.email.disabled = false
      @_setSex()
      super
    popup: (data, callback) ->
      super data, callback
      @$title.text "User: #{data.first_name} #{data.last_name}" if data.first_name or data.last_name
      @fill data
      @form.email.disabled = Boolean data.email
      @
    save: ->
      @data = @read()
      @data.sex = @_getSex()
      @callback 'save'
      @hide true
      @

  # User Manager

  class UsernameCell extends Backgrid.StringCell
    render: ->
      @$el.text "#{@model.get 'first_name'} #{@model.get 'last_name'}"
      @

  class UserManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
    ,
      name: 'first_name'
      label: 'Username'
      cell: UsernameCell
      editable: false
    ,
      name: 'email'
      label: 'Email'
      cell: 'email'
      editable: false
    ,
      # TODO: support display and filter by multiple projects
      'status'
      'created_at'
      'actions:user'
    ]
    collection: new Publishers
    initialize: (options) ->
      super options
      @editor = new UserEditor el: '#user_editor', parent: @
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: @collection.fullCollection
      @on 'create edit', @edit.bind @
      @on 'remove remove_selected', @remove.bind @
      @
    reload: ->
      super
      @projectFilter.clear()
    edit: (user) ->
      user = new Publisher unless user instanceof Publisher
      # TODO: set tenant
      @editor.popup user.attributes, (action, data) =>
        console.log 'user', action, data
        if action is 'save'
          if user.isNew()
            @collection.create data
          else
            user.save data
          @refresh()
      @
    remove: (users) ->
      users = [users] unless Array.isArray users
      @_remove user for user in users
      @refresh()
      @
    _remove: (user) ->
      email = user.get 'email'
      if @signin_user.email is email
        alert 'Cannot remove currently signed in user ' + email
      else if confirm 'Are you sure to remoe user ' + email + '?\n\nIt cannot be restored after removal!'
        user.destroy()
    render: ->
      super
      @projectFilter.render()
      # logined user
      @signin_user = JSON.parse sessionStorage.user
      @

  ConfigFrameView
