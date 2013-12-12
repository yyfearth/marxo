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
  #return alert 'Local test data only support sign-in with email!' # test
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
        when 'not_authorized' # the user is logged in but has not authed
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

require [
  'lib/common'
  'lib/html5-dataset'
  'lib/bootstrap-fileupload'
  'lib/bootstrap-wysiwyg'
  #'lib/backbone.localstorage' # test
], ->
  console.log 'ver', 'site', 1

  # Models

  class User extends Backbone.Model
    #idAttribute: 'email' # test
    urlRoot: ROOT + '/users'
    defaults:
      type: 'PARTICIPANT'

  class Page extends Backbone.Model
    urlRoot: ROOT + '/pages'

  class Pages extends Backbone.Collection
    @pages: new Pages
    model: Page
    url: Page::urlRoot

  class Submission extends Backbone.Model
    url: -> "#{ROOT}/pages/#{@get 'content_id'}/submissions"

  # Views

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
        @signin auth, true # sign in without alert when fail
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
          auth = 'Basic ' + btoa "facebook:#{response.accessToken}"
          @signin auth, =>
            if confirm 'Your Facebook account is not signed up!\n\nDo you want to sign up with this account?'
              @dialog.popup()._signUpFB silence: true # sign up when failed
        return
      return
    signin: (auth, error_callback) ->
      $inputs = @$el.find('input,button').prop 'disabled', true
      # test
      #email = atob(auth.slice(6)).split(':')[0]
      #throw new Error 'local test data only support sign-in with email' if email.indexOf('@') < 1
      new User({id: 'me'}).fetch
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
        error: (ignored, xhr) =>
          console.error 'sign-in failed', xhr
          if typeof error_callback is 'function'
            error_callback null, xhr
          else unless error_callback
            alert 'Sign in failed'
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
      $.ajaxSetup headers:
        Authorization: user.auth
      $el.trigger 'signedin', [user]
      $go_console = $el.find('#go_console')
      if user.has 'tenant_id'
        $go_console.prop 'href', './' # !!! ROOT + '/../console/'
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
      @$el.on
        show: (e) =>
          @$parent.append @$el if e.target is @el
          return
        hidden: (e) =>
          @$el.detach() if e.target is @el
          return
      @$parent = $el.parent()
      @$el.detach()
      return
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
      #console.log @, @textContent
      return
    _getOauth: ->
      oauth = {}
      @$openAccounts.each ->
        oauth[@_name.toLowerCase()] = @value if @value
      oauth
    fill: (attrs) ->
      form = @form
      form.email.value = attrs.email or ''
      form.name.value = attrs.name or ''
      form.desc.textContent = attrs.desc or ''
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
    _signUpFB: (options = {}) -> require ['fb'], (FB) =>
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
              alert 'Facebook account is bound, please click "Save".' unless options.silence
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
              alert 'Facebook account is bound, please setup a password then click "Save".' unless options.silence
              @$passwords[0].focus()
            , 100
            console.log 'facebook connected', response
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

  class ContentView extends Backbone.View
    $container: $('#content')
    show: ->
      @hide()
      @render() unless @rendered
      @$container.append @$el
      @$thumb?.addClass 'active'
      @
    hide: ->
      @$el.detach()
      @$container.empty()
      @$thumb?.removeClass 'active'
      @
    render: ->
      @rendered = true
      @

  class PageView extends ContentView
    _fonts: [
      'Serif', 'Sans', 'Arial', 'Arial Black'
      'Courier', 'Courier New', 'Comic Sans MS'
      'Helvetica', 'Impact', 'Lucida Grande', 'Lucida Sans'
      'Tahoma', 'Times', 'Times New Roman', 'Verdana'
    ]
    _tpl: {}
    tpl: (name, attrs) ->
      _tpl = @_tpl
      # lazy load
      unless _tpl.section
        $('#content_tpl').remove().children('.tpl[name]').each ->
          $el = $ @
          _tpl[$el.attr 'name'] = $el.html().trim().replace(/\s+/g, ' ').replace(/> </g, '>\n<')
          return
      unless name
        _tpl
      else unless attrs
        _tpl[name] ? ''
      else unless _tpl[name]
        ''
      else
        _tpl[name].replace /{{\s*\w+\s*}}/g, (name) ->
          name = name.match(/^{{\s*(\w+)\s*}}$/)[1]
          attrs[name] or ''
    events:
      'click .form-mask': (e) ->
        e.preventDefault()
        $('#user_profile a.dropdown-toggle').click()
        false
      'click .dropdown-menu input': (e) ->
        e.preventDefault()
        false
      'change .dropdown-menu input': (e) ->
        $(e.currentTarget).parent('.dropdown-menu')
        .siblings('.dropdown-toggle').dropdown 'toggle'
        return
      'keydown .dropdown-menu input': (e) -> if e.which is 27 # esc
        $(e.currentTarget).val('').change()
        .parents('.dropdown-menu').siblings('.dropdown-toggle').dropdown 'toggle'
        return
      'click .btn.hyperlink': (e) ->
        setTimeout ->
          $(e.currentTarget).siblings('.dropdown-menu').find('input').focus()
        , 200
        return
      'blur .rich-editor[contenteditable]:visible': (e) ->
        $el = $(e.currentTarget)
        $el.siblings('textarea.rich-editor-html').val $el.cleanHtml()
        return
      'click .btn-switch': (e) ->
        $btn = $(e.currentTarget)
        $parent = $btn.parents('[data-role="editor-toolbar"]')
        $code = $parent.find('textarea.rich-editor-html')
        $editor = $parent.find('.rich-editor')
        $edits = $parent.find('[data-edit],.btn.dropdown-toggle,.btn-edit')
        if $code.is ':visible' # to editor
          $code.hide()
          $editor.html($code.val()).show()[0].focus()
          $edits.prop 'disabled', false
        else # to code
          $editor.hide()
          $code.show()[0].focus()
          $edits.prop 'disabled', true
        return
      'change form input[type=file]': (e) ->
        delete e.currentTarget.info
        $.removeData e.currentTarget, 'info'
        return
      'submit form': (e) ->
        e.preventDefault()
        @submit()
        false
    initialize: (options) ->
      throw new Error 'page id must be given' unless options?.id
      super options
      @id = options.id
      @model = Pages.pages.get(@id) or new Page id: @id
      @tpl = @tpl.bind @
      $el = @$el
      $('#user_profile').on 'signedin', ->
        $el.find('.form-mask').hide()
        $el.find('form').show()
        return
      @_renderInput = @_renderInput.bind @
      @
    renderNotFound: ->
      @$el.html '<h1>404: Page not found!</h1>'
      @
    _renderFonts: ->
      unless html = @_fonts_html
        el = document.createElement 'ul'
        for fontName in @_fonts
          li = document.createElement 'li'
          a = document.createElement 'a'
          a.dataset.edit = "fontName #{fontName}"
          a.style.fontFamily = fontName
          a.textContent = fontName
          li.appendChild a
          el.appendChild li
        html = @_fonts_html = el.innerHTML
      html
    _render: (model = @model) ->
      unless html = @html
        console.log 'render', model.attributes
        tpl = @tpl
        _renderInput = @_renderInput
        hasInput = false
        sections = model.get('sections')?.map (section, i) ->
          hasInput = true if section.type
          tpl 'section',
            title: _.escape(section.name)
            desc: section.desc + if section.options?.required then '' else ' <em>(Optional)</em>'
            body: _renderInput(section, i)
        html = tpl 'page',
          title: model.escape('name')
          desc: model.get('desc')
          sections: unless sections?.length then '' else sections.join '\n'
        html = @html = unless hasInput then html.replace('form-actions', 'form-actions hide') else html
      $el = @$el.html(html)
      setTimeout -> # defer
        $el.find('.rich-editor').attr('contenteditable', 'true').each ->
          $("##{@id}.rich-editor").wysiwyg()
        $el.find('.btn-toolbar').find('[data-edit],.btn.dropdown-toggle,.btn-edit').tooltip container: $el
        # check login user
        if User.current?.id
          $el.find('.form-mask').hide()
          $el.find('form').show()
      , 10
      html
    _renderInput: (data, i) ->
      type = (data.type or '').toLowerCase()
      options = data.options or {}
      tpl = @tpl()
      switch type
        when '', 'none'
          body = ''
        when 'text'
          body = if options.text_multiline then tpl.textarea else tpl.text
        when 'html'
          body = tpl.html.replace '{{fonts}}', @_renderFonts()
          body = body.replace '<textarea ', '<textarea data-required="required" ' if options.required
        when 'radio'
          list = unless options.gen_from_list then options.manual_options else [
            'List item 1 (Auto Genearted)'
            'List item 2 (Auto Genearted)'
            '... (Auto Genearted)'
          ]
          body = list?.map((item, i) -> tpl.radio.replace('{{i}}', i).replace '{{text}}', item).join '\n'
        when 'file'
          accept = options.file_accept
          if accept is 'image/*'
            body = tpl.image
          else
            accept = unless accept then '' else "accept='#{accept}' "
            body = tpl.file.replace /accept(?:=['"]{2})?/, accept
        else
          throw new Error 'unknown section type ' + type
      body = body.replace /\s*required(?:="\w*")?/ig, '' unless options.required
      body.replace /{{name}}/g, 'section_' + i
    render: ->
      if @rendered or @model.has 'name'
        @_render()
        @model.fetch reset: true # for visit count inc
      else
        @update()
      super
    update: ->
      @model.fetch
        reset: true
        success: (model) =>
          if 'PAGE' is model.get('type').toUpperCase()
            @_render model
          else
            @renderNotFound()
          return
        error: =>
          @renderNotFound()
      @
    upload: (file, auth) ->
      unless file.files?.length
        null
      else if info = file.info or $.data file, 'info'
        info
      else
        data = new FormData
        data.append 'file', file.files[0]
        $.ajax
          url: ROOT + '/files'
          type: 'POST'
          data: data
          processData: false
          contentType: false
          headers:
            Authorization: auth
          success: (info) ->
            file.info = info
            $(file).data id: info.id, info: info
            console.log 'upload success', info
          error: (info) ->
            console.error 'upload failed', info
    validate: ->
      unless User.current?.auth
        alert 'Only sign-in user can submit!'
        return false
      $els = @$el.find('form').find ':input[required]:visible:enabled, textarea.rich-editor-html[data-required]:enabled'
      for input in [].slice.call $els
        unless input.value.trim()
          $input = $(input)
          if $input.is ':visible'
            input.focus()
          else
            $input.siblings('.rich-editor').focus()
          alert 'This field is required!'
          return false
      try
        for input in [].slice.call @$el.find 'form :invalid'
          input.focus()
          alert 'This field is invalid!'
          return false
      true
    submit: ->
      return @ unless @validate()
      @_disableSubmit true
      # upload files
      files = [].slice.call @$el.find 'form input[type=file][name^=section_]'
      requests = for file in files then @upload file, User.current.auth
      $.when.apply($, requests).then (results...) =>
        data = []
        console.log 'infos', results
        for info, i in results
          if id = info?[0]?.id
            data.push name: files[i].name, value: "files/#{id}"
        console.log 'files', data
        @_submit data
      , =>
        alert 'File upload failed!'
        @_disableSubmit false
      @
    _disableSubmit: (disabled) ->
      (@$submit ?= @$el.find 'form :submit').prop 'disabled', disabled
      return
    _submit: (data) ->
      # prepare data
      sections = []
      for {name, value} in @$el.find('form').serializeArray().concat data or []
        if value and /^section_\d+$/i.test name
          name = Number name[8..]
          sections[name] = value
      user = User.current
      name = user.get 'name'
      email = user.get 'email'
      # submit
      new Submission().save {
        content_id: @model.id
        name
        key: email
        desc: "Submitted by #{name}&lt;#{email}&gt;"
        sections
      }, success: (submission) =>
        console.log 'submit success', submission
        alert "Submit successful."
        # TODO: show result page
        @_disableSubmit false
      , error: (err) =>
        console.error 'submit failed', err
        alert "Failed to submit!"
        @_disableSubmit false
      return

  class PageListView extends ContentView
    collection: Pages.pages
    $thumb: $('.navbar li:has(>a.icon-project)')
    _render: ->
      projects = @collection.groupBy 'workflow_id'
      $list = $('<ul>', class: 'nav nav-pills nav-stacked')
      for own id, project of projects
        # TODO: add project name
        for page in project #if /^PAGE$/i.test page.get('type')
          name = page.get 'name'
          name += ' (Ended)' if /^FINISHED$/i.test page.get 'status'
          $list.append $('<li>').append $ '<a>',
            href: "##{page.id}"
            text: page.get 'name'
      @$el.html('<h3>Projects</h3>').append $list
      return
    update: ->
      @collection.fetch
        reset: true
        success: => @_render()
        error: => @$el.html('<h3>Projects</h3><p>Failed to get the page list</p>')
      @
    show: ->
      @update() unless @collection.length
      super

  class HomeView extends ContentView
    $thumb: $('.navbar li:has(>a.icon-home)')
    el: $('#home_page').removeClass('tpl').detach()

  # Router

  class Router extends Backbone.Router
    routes:
      '': -> @navigate 'home', trigger: true, replace: true
      'home': 'showHome'
      'list': 'showPageList'
      'page/:id': 'showPage'
      ':id': 'showPage'
    cache:
      page: {}
    $container: $('#content')
    _show: (view) ->
      if view isnt @_cur
        @_cur?.hide()
        @_cur = view.show()
      @
    showHome: ->
      @_show @cache.home ?= new HomeView
    showPage: (id) ->
      console.log 'show page:', id
      @_show @cache[id] ?= new PageView {id}
    showPageList: ->
      @_show @cache.list ?= new PageListView

  # EP
  window.user_profile = new UserProfileView
  Backbone.View::router = new Router
  Backbone.history.start()

  return
