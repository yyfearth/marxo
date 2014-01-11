"use strict"

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
User
Publisher
Publishers
Service
}) ->

  class ConfigFrameView extends FrameView
    initialize: (options) ->
      super options
      @profile = new TenantProfileView el: '#tenant_profile', parent: @
      @manager = new UserManagemerView el: '#user_manager', parent: @, list: @list
      @connector = new InnerFrameView el: '#service_connector', parent: @
      @
    open: (name) ->
      switch name
        when 'users'
          @switchTo @manager
          @manager.open sub
        when 'tenant'
          @switchTo @profile
        when 'service'
          @switchTo @connector
        else
          throw new Error 'empty or unknown sub action for config frame ' + name
      @

  # Tenant Profile
  class TenantProfileView extends InnerFrameView
    @acts_as FormViewMixin
    events:
      'reset form': ->
        if @model then setTimeout =>
          @fill @model.attributes
        , 1
        return
      'click .btn-reload': -> @reload true
    initialize: (options) ->
      super options
      @initForm()
      @$btnReload = $ find '.btn-reload', @el
      @$btns = $ findAll '.btn', @form
      @on 'submit', =>
        @$btns.prop 'disabled', true
        @save() # it will call reload, while will enable btns
        return
      @reload = _.debounce @reload.bind(@), 100
      @
    save: ->
      $btn = $(@_submit_btn)
      $btn.button 'loading'
      data = @read()
      console.log 'save', data
      @model.save data, wait: true, success: =>
        $btn.button 'reset'
        @reload true
      @
    render: ->
      @reload false
      super
    reload: (force) =>
      $btnReload = @$btnReload.button 'loading'
      $btns = @$btns.prop 'disabled', true
      @_load force, ->
        $btnReload.button 'reset'
        $btns.prop 'disabled', false
        return
      @
    _throttle: 60000 # 1min
    _last_load: 0
    _load: (force, callback) ->
      throw new Error 'invalid user logined' unless User.current?.has 'tenant_id'
      if force or Date.now() - @_last_load > @_throttle
        @model = null
        @form.reset()
        @model ?= new Tenant id: User.current.get 'tenant_id'
        @model.fetch reset: true, success: (data) =>
          console.log 'fetch tenant', data.attributes
          @model = Tenant.current = data
          @fill data.attributes
          @_last_load = new Date().getTime()
          callback? data
      else
        callback? null
      return

  # User Editor
  class UserEditor extends FormDialogView
    goBackOnHidden: 'config/users'
    initialize: (options) ->
      super options
      @$title = $ find '.modal-title', @el
      @$sex = $ findAll '[name=sex]', @form
      @$editOnly = @$el.find '.edit-only'
      @passwords = findAll '[type=password]', @form
      # remove elements not used in console
      @$el.find('#user_avatar, #link_fb').parents('.control-group').remove()
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
    fill: (data) ->
      super data
      @_setSex data.sex
      @
    read: ->
      data = super
      password = @passwords[0].value
      data.password = password if password
      data.sex = @_getSex()
      data
    reset: ->
      @$title.text 'Create User'
      @$editOnly.hide()
      @form.email.disabled = false
      $(@passwords).removeAttr 'required'
      @_setSex()
      super
    popup: (data, callback) ->
      super data, callback
      if data.email # edit mode
        @form.email.disabled = true
        @$editOnly.show()
        if data.first_name or data.last_name
          @$title.text "User: #{data.first_name} #{data.last_name}"
        else
          @$title.text "User: #{data.email}"
      else
        $(@passwords).attr 'required', 'required'
      @fill data
      @
    validate: ->
      psw = @passwords[0].value
      psw2 = @passwords[1].value
      if (psw or psw2) and psw isnt psw2
        @passwords[1].select()
        alert 'Passwords are not matched!'
        return false
      super
    save: ->
      data = @data = @read()
      email = @form.email.value.trim()
      unless data.password
        @callback 'save'
        @hide true
      else require ['crypto'], ({hashPassword}) =>
        console.log 'crypto', email, data.password
        data.password = hashPassword email, data.password
        @callback 'save'
        @hide true
      @

  # User Manager

  class UserManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
    ,
      name: 'first_name'
      label: 'Username'
      cell: Backgrid.StringCell.extend render: ->
        @$el.text @model.name()
        @
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
      @edit = @edit.bind @
      @remove = @remove.bind @
      @on 'remove remove_selected', @remove
      @
    reload: ->
      @projectFilter.clear()
      super
    open: (id) ->
      unless id
        @editor.cancel()
      else if id is 'new'
        @edit null
      else
        user = @collection.get id
        if user?.has 'email'
          @edit user
        else
          new Publisher({id}).fetch success: @edit
      @
    edit: (user) ->
      user = new Publisher unless user instanceof Publisher
      @editor.popup user.toJSON(), (action, data) =>
        console.log 'user', action, data
        if action is 'save'
          # enforce tenant id
          data.tenant_id = @signin_user.tenant_id
          if user.isNew()
            @collection.create data, wait: true
          else
            user.save data, wait: true
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
      @signin_user = User.current.toJSON() # logined user
      @projectFilter.render()
      @

  ConfigFrameView
