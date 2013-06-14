"use strict"

define 'console', ['models', 'lib/common'], ({ManagerCollection}) ->
  find = (selector, parent) ->
    parent ?= document
    parent.querySelector selector

  findAll = (selector, parent) ->
    parent ?= document
    [].slice.call parent.querySelectorAll selector

  ## Views

  class View extends Backbone.View
    initialize: (options) ->
      @el.view = @
      if options?.parent
        @parent = options.parent
        @parentEl = @parent.el
      return
    delayedTrigger: (eventName, delay = 10, args...) ->
      timeout_key = "_#{eventName}_timtout"
      clearTimeout @[timeout_key] if @[timeout_key]
      @[timeout_key] = setTimeout =>
        @[timeout_key] = null
        @trigger eventName, args...
        return
      , delay

  class ConsoleView extends View
    el: '#main'
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    initialize: ->
      @frames = ({})
      findAll('.frame', @el).forEach (frame) =>
        navEl = find "#navbar a[href=\"##{frame.id}\"]"
        @frames[frame.id] =
          id: frame.id
          el: frame
          parent: @
          navEl: navEl?.parentElement
        return
      @fixStyles()
      return
    fixStyles: ->
      # auto resize
      navContainer = find '#navbar', @el
      framesContainer = find '#frames', @el
      do window.onresize = =>
        h = navContainer.clientHeight or 41
        framesContainer.style.top = h + 'px'
        return
      # hide menu after click
      $('.dropdown-menu').click ->
        navContainer.classList.add 'hide-dropdown'
        $(document.body).one 'mousemove', ->
          navContainer.classList.remove 'hide-dropdown'
        return
      return
    showFrame: (frame, name) ->
      frame = @frames[frame]
      return unless frame?
      # console.log 'frame', frame
      if frame instanceof FrameView
        frame.open? name
      else
        console.log 'load module:', frame.id
        require [frame.id], (TheFrameView) =>
          frame = @frames[frame.id] = new TheFrameView frame
          frame.render()
          frame.open? name
          return
      unless frame.el.classList.contains 'active'
        find('#main .frame.active')?.classList.remove 'active'
        find('#navbar li.active')?.classList.remove 'active'
        frame.el.classList.add 'active'
        frame.navEl.classList.add 'active'
        $(window).resize()
      return
    signout: ->
      # TODO: sign out
      delete sessionStorage.user
      SignInView.get().show()
      @hide()
      @trigger 'signout'
      return

    show: ->
      @el.style.visibility = 'visible'
      @el.classList.add 'active'
      @el.style.opacity = 1
      return
    hide: ->
      @el.classList.remove 'active'
      setTimeout =>
        @el.style.visibility = 'hidden'
        return
      , SignInView::delay
      return

  class InnerFrameView extends View
    initialize: (options) ->
      super options
      return

  class FrameView extends View
    initialize: (options) ->
      super options
      @navEl = options.navEl or (find "#navbar a[href=\"##{@id}\"]")?.parentElement
      return
    switchTo: (innerframe) ->
      innerframe = @[innerframe] if typeof innerframe is 'string'
      if innerframe and innerframe instanceof InnerFrameView
        unless innerframe.el.classList.contains 'active'
          console.log 'switch inner-frame', innerframe.el?.id
          find('.inner-frame.active[name]', @el)?.classList.remove 'active'
          innerframe.el.classList.add 'active'
        unless innerframe.rendered
          innerframe.render()
          innerframe.rendered = true
      else
        console.warn 'inner frame cannot find', frameName
      return
  #open: (name) -> # should be override

  class BoxView extends View
    events:
      'click .btn-close': 'close'
      'click .btn-minimize': 'minimize'
    initialize: (options) ->
      super options
    render: ->
      @btn_min = find '.btn-minimize', @el
      @btn_close = find '.btn-close', @el
      @contentEl = find '.box-content', @el
      return
    close: -> # should be override
      console.log 'box close button clicked'
      return
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
      return

  class ModalDialogView extends View
    initialize: (options) ->
      super options
      @$el.modal
        show: false
        backdrop: 'static'
      @$el.on 'hidden', @callback.bind @
      return
    popup: (@data, callback) ->
      @_callback = callback
      @show true
    callback: (action = 'cancel') ->
      return unless @_callback?
      @trigger action, @data, @
      @_callback? action, @data, @
      @reset()
      return
    reset: ->
      @data = null
      @_callback = null
      @
    #action: -> # should be customized, e.g. ok, save, export
    #  @callback 'action_name'
    #  @hide true
    cencel: ->
      @hide true
    show: (@shown = true) ->
      @$el.modal if @shown then 'show' else 'hide'
      @
    hide: (hide = true) ->
      @show not hide


  class FormDialogView extends ModalDialogView
    initialize: (options) ->
      super options
      @form = find 'form', @el
      @form.onsubmit = (e) =>
        e.preventDefault()
        @save()
        false
      submit_btn = find '[type="submit"]', @form
      unless submit_btn?
        submit_btn = document.createElement 'input'
        submit_btn.type = 'submit'
        submit_btn.style.display = 'none'
        @form.appendChild submit_btn
      @_submit_btn = submit_btn
      find('button.btn-save', @el)?.onclick = @submit.bind @
      @
    submit: ->
      @_submit_btn.click()
      @
    #popup: (data, callback) ->
    #  # already set @data = data
    #  super data, callback
    #  @fill data
    #  @
    fill: (attributes) ->
      @_attributes = ({})
      for name, value of attributes
        input = @form[name]
        if input?.name is name and input.value?
          input.value = value
          @_attributes[name] = value
      @
    read: ->
      attributes = @_attributes
      if attributes? then for input in @form.elements
        name = input.getAttribute 'name'
        attributes[name] = input.value if name and (input.value or attributes[name]?)
      attributes
    #save: ->
    #  @callback 'save'
    #  @hide true
    #  @
    reset: -> # called after close
      super()
      @form.reset()
      @

  class SignInView extends View
    el: '#signin'
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    events:
      'submit form': 'submit'
    initialize: (options) ->
      super options
      # auto sign in
      if (sessionStorage.user)
        @signedIn()
      else
        @show()
      return
    submit: -> # fake
      console.log 'sign in'
      @signedIn()
      false
    signedIn: -> # debug only
      user = id: 'test', name: 'test'
      sessionStorage.user = JSON.stringify user
      @trigger 'success', user
      @hide()
      ConsoleView.get().show()
      # Router.get().navigate 'home'
      location.hash = '' if /signin/i.test location.hash
      return
    delay: 500
    show: ->
      @el.style.opacity = 0
      @el.style.display = 'block'
      setTimeout =>
        @el.classList.add 'active'
        @el.style.opacity = 1
        return
      , 1
      return
    hide: ->
      @el.classList.remove 'active'
      @el.style.opacity = 0
      setTimeout =>
        @el.style.display = 'none'
        return
      , @delay
      return

  class SeqCell extends Backgrid.StringCell
    formatter: null
    initialize: (options) ->
      @formatter ?=
        fromRaw: =>
          seq = @model._seq
          if seq? then seq + 1 else ''
        toRaw: =>
          @model.id
      super options

  class LinkCell extends Backgrid.UriCell
    urlRoot: ''
    initialize: (options) ->
      super options
      @urlRoot += '/' if @urlRoot[-1..] isnt '/'
    render: ->
      @$el.empty()
      formattedValue = @formatter.fromRaw @model.get @column.get 'name'
      @$el.append $('<a>',
        tabIndex: -1
        href: @urlRoot + @model.id
        title: formattedValue
      ).text formattedValue
      @delegateEvents()
      @

  class DateTimeCell extends Backgrid.StringCell
    className: 'datetime-cell'
    formatter: null
    initialize: (options) ->
      @formatter ?=
        fromRaw: (datetime) ->
          if datetime instanceof Date
            datetime.toLocaleString()
          else
            try
              new Date(datetime).toLocaleString()
            catch e
              console.error 'convert datetime error', datetime, e
              ''
        toRaw: (datetime) ->
          # or to timestamp?
          try
            new Date(datetime).toISOString()
          catch e
            console.error 'convert datetime error', datetime, e
            ''
      super options

  class ActionCell extends Backgrid.Cell
    @tpl: (type) -> # load form html template
      if not type
        ''
      else if @_tpl? and @_tpl[type]?
        @_tpl[type] # cached
      else
        el = find "#t_#{type}_action_cell"
        if el?
          @_tpl ?= ({})
          # load template
          tpl = @_tpl[type] = el.innerHTML
          # remove template from dom
          el.parentNode.removeChild el
        else
          throw 'cannot find template for type: ' + type
        tpl
    className: 'action-cell'
    render: ->
      @el.innerHTML = @constructor.tpl @type
      @el.dataset.model = @model.id
      @$el.data 'model', @model
      @delegateEvents()
      @

  class ManagerPaginator extends Backgrid.Extension.Paginator
    className: 'pagination'
    initialize: (options) ->
      super options
      # fix for backgrid.paginator re-render
      @collection.on 'reset', => @render()
    render: ->
      super()
      if @collection.state.totalPages < 2
        @$el.hide()
      else
        @$el.show()
      @

  class ManagerView extends InnerFrameView
    _predefinedColumns: {
      checkbox:
        # name is a required parameter, but you don't really want one on a select all column
        name: ''
        # Backgrid.Extension.SelectRowCell lets you select individual rows
        cell: 'select-row'
        # Backgrid.Extension.SelectAllHeaderCell lets you select all the row on a page
        headerCell: 'select-all'
      id:
        name: 'id' # The key of the model attribute
        label: '#' # The name to display in the header
        cell: SeqCell
        editable: false
      name:
        name: 'name'
        label: 'Name'
        cell: 'string'
        editable: false
      title:
        name: 'title'
        label: 'Title'
        cell: 'string'
        editable: false
      desc:
        name: 'desc'
        label: 'Description'
        cell: 'string'
        editable: false
      status: # TODO: change to list cell and editable
        name: 'status'
        label: 'Status'
        cell: 'string'
        editable: false
      created_at:
        name: 'created_at'
        label: 'Date Created'
        cell: DateTimeCell
        editable: false
      updated_at:
        name: 'updated_at'
        label: 'Date Updated'
        cell: DateTimeCell
        editable: false
    }
    _defaultEvents:
      'click .action-cell .btn': '_action_cell'
      'click .action-buttons .btn': '_action_buttons'
      'change input[type="checkbox"]': '_selection_changed'
    initialize: (options) ->
      @events ?= {}
      for own event, action of @_defaultEvents
        @events[event] ?= action

      super options

      @collection = options.collection if options.collection instanceof ManagerCollection
      collection = @collection
      throw 'collection must be a instance of ManagerCollection' unless collection instanceof ManagerCollection
      # add a sequence to models
      _gen_seq = -> collection.fullCollection.each (model, i) -> model._seq = i
      collection.fullCollection.on reset: _gen_seq
      collection.on add: _gen_seq, remove: _gen_seq
      # selection may change after remove
      collection.on 'remove', => @_selection_changed()
      # page size alias
      @pageSize = collection.state.pageSize or 15
      #window.collection = collection # debug
      #window.mgr = @ # debug

      @grid = new Backgrid.Grid
        columns: @_configColumns()
        collection: collection
      @paginator = new ManagerPaginator
        collection: collection
      @filter = new Backgrid.Extension.ClientSideFilter
        collection: collection.fullCollection,
        fields: ['title']
        wait: 300

      # tooltip on bottom
      $('.action-buttons .btn[title]').attr 'data-placement': 'bottom'
      @
    _configColumns: ->
      columns = []
      for cfg in @columns
        if typeof cfg is 'string'
          cfgs = cfg.split ':'
          if cfgs[1]
            cfg = switch cfgs[0]
              when 'actions'
                name: ''
                label: ''
                cell: ActionCell.extend(type: cfgs[1])
                editable: false
                sortable: false
              when 'title'
                name: 'title'
                label: 'Title'
                cell: LinkCell.extend(urlRoot: cfgs[1])
                editable: false
              else
                null
            if cfg
              columns.push cfg
            else
              console.error 'cannot understand predefined column', cfg
          else if @_predefinedColumns[cfg]?
            cfg = @_predefinedColumns[cfg]
            columns.push cfg
          else
            console.error 'cannot found predefined column', cfg
        else
          columns.push cfg
      columns
    render: ->
      @$el.find('table.grid-table').replaceWith @grid.render().$el.addClass 'grid-table'
      @$el.find('.grid-paginator').replaceWith @paginator.render().$el.addClass 'grid-paginator'
      @$el.find('.grid-filter').empty().append @filter.render().$el
      @reload()
      @$enable_if_selected = @$el.find '.enable_if_selected'
      @
    reload: ->
      @collection.fetch reset: true
      @collection.getPage 1
      @
    getSelected: ->
      @grid.getSelectedModels().filter (r) -> r?
    _action_cell: (e) ->
      btn = e.target
      action = btn.dataset.action or btn.getAttribute 'name'
      cell = btn.parentNode
      # model = $(cell).data('model')
      model = @collection.get cell.dataset.model
      console.log 'action', action, model
      @trigger action, model if action and model
      return
    _action_buttons: (e) ->
      btn = e.target
      action = btn.dataset.action or btn.getAttribute 'name'
      return unless action
      if btn.classList.contains 'enable_if_selected'
        selected = @getSelected()
        console.log 'action', action, selected
        @trigger action, selected, @
      else
        console.log 'action', action
        @trigger action, @
        @[action]?() # reload
      return
    _selection_changed: ->
      selected = @getSelected()
      @$enable_if_selected.prop 'disabled', not selected?.length
      @delayedTrigger 'selection_changed', 100, selected, @grid, @
      return

  ## Router

  class Router extends Backbone.Router
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    frames: [
      'home'
      'project'
      'workflow'
      'calendar'
      'content'
      'report'
      'config'
      'profile'
    ]
    constructor: (options) ->
      super options
      @route '', 'home', =>
        @navigate 'home', replace: true
        @show 'home'
      @frames.forEach (frame) =>
        @route frame + '(/:name)', frame, (name) =>
          @show frame, name
        return
      @route 'signin', 'signin', => return
      @route 'signout', 'signout'
      return

    show: (frame, name) ->
      unless sessionStorage.user
        @navigate 'signin', replace: true
        return
      console.log 'route', frame, name or ''
      ConsoleView.get()?.showFrame frame, name
      handler = @[frame]
      handler.call @, name if handler?
      return

    #home: -> return
#    project: (name) ->
#      if name is 'new'
#        console.log 'show project create'
#      else if name is 'mgr'
#        console.log 'show project mgr'
#      else if name
#        console.log 'show project viewr?/editor? for', name
#      return
    #calendar: (name) -> return
    #content: (name) -> return
    #report: (name) -> return

    signout: ->
      console.log 'sign out'
      ConsoleView.get().signout()
      @navigate 'signin', replace: true
      return

  $ -> $(document.body).tooltip selector: '[title]:not([data-original-title])'

  { # exports
  find
  findAll
  View
  ConsoleView
  BoxView
  FrameView
  InnerFrameView
  ManagerView
  ModalDialogView
  FormDialogView
  SignInView
  Router
  }
