"use strict"

define 'actions', ['base', 'models', 'lib/jquery-ui'],
({find, findAll, tplAll, BoxView, DurationConvertor}, {Action, Actions}) ->

  class ActionsMixin
    initActions: (options) ->
      @actionsEl = options?.actionEl or find '.node-actions', @el
      unless @projectMode = Boolean options?.projectMode
        $(@actionsEl).sortable
          axis: 'y'
          delay: 150
          distance: 15
          cancel: '.box-content'
      @
    fillActions: (actions) ->
      if actions
        # @clearActions()
        actions = new Actions actions if Array.isArray actions
        throw new Error 'fill action only accept Actions or array' unless actions instanceof Actions
        console.log 'fill actions', actions
        @actions = actions
        @actions.forEach @addAction.bind @
      @
    readActions: ->
      findAll('.action', @actionsEl).map (el) ->
        view = $(el).data 'view'
        # TODO: validate each action
        throw new Error 'cannot get action from action.$el' unless view
        view.read()
    clearActions: ->
      @actions?.forEach (model) -> model.view?.remove()
      @actions = null
      $(findAll('.action', @actionsEl)).remove()
      @
    viewAction: (id) ->
      console.log 'view action id:', id
      el = @actionsEl.querySelector '#action_' + id
      if el?
        hidden = @el.getAttribute 'aria-hidden'
        if hidden is 'true'
          @$el.one 'shown', -> el.scrollIntoViewIfNeeded()
        else if hidden is 'false'
          el.scrollIntoViewIfNeeded()
        else setTimeout ->
          el.scrollIntoViewIfNeeded()
        , 600
      el
    addAction: (model, options) ->
      try
        model = new Action model unless model instanceof Action
        actionView = new ActionView
          model: model
          parent: @, container: @actionsEl
          projectMode: @projectMode
          readonly: @readonly and model.status() isnt 'IDLE'
        @listenTo actionView, 'remove', @removeAction.bind @
        actionView.render()
        actionView.el.scrollIntoViewIfNeeded() if options?.scrollIntoView
        @delayedTrigger 'actions_update', 100
        #@actions.add actionView
      catch e
        console.error 'faild to load action:', e, model
      @
    removeAction: (view) ->
      console.log 'remove action view', view
      view.remove?()
      @delayedTrigger 'actions_update', 100
      @

  class ActionView extends BoxView
    className: 'box action'
    _tpl: tplAll('#actions_tpl')
    events: # for duration inputs
      'change .event-inputs input[type=checkbox]': (e) ->
        checkbox = e.currentTarget
        $input = $(checkbox).parents('.event-inputs').find('input[name$=duration]')
        $input.prop 'disabled', not checkbox.checked
        $input[0].focus() if checkbox.checked
        return
      'change .event-inputs input[name$=duration]': (e) ->
        val = DurationConvertor.parse e.currentTarget.value.trim()
        e.currentTarget.value = unless val then '' else DurationConvertor.stringify val
        return
      'blur .event-inputs input[name$=duration]': _.debounce (e) ->
        $el = $ e.target
        unless $el.val().trim()
          $el.parents('.event-inputs').find('input[type=checkbox]')
          .prop('checked', false).change()
        return
      , 150
    initialize: (options) ->
      super options
      unless options.model
        throw new Error 'need action model'
        console.dir options
      @projectMode = options.projectMode
      @readonly = options.readonly
      @containerEl = options.container
      @model = options.model
      @model.view = @
      @alertEl = find '.alert', @containerEl
      type = @type = (@model.get?('type') or options.model.type or options.type or '').toLowerCase()
      unless @_tpl.hasOwnProperty type
        @type = 'unknown'
        console.warn 'unknown action type', type, options
      @
    remove: ->
      @remove = -> @ # remove only once
      super
    render: ->
      _tpl = @_tpl[@type]
      unless _tpl
        console.error 'unable to find tpl for action type', @type
        @remove()
      else unless @model?
        console.error 'no action model to render', @
        @remove()
      else
        model = @model
        data = model.toJSON() # IT IS NOT DEEP COPY
        data.event = $.extend {}, data.event if data.event?
        data.tracking = $.extend {}, data.tracking if data.tracking?
        @el.innerHTML = _tpl
        @el.id = 'action_' + model.id or model.cid
        @_name = @$el.find('.box-header h4').text()
        # add to container
        if @alertEl?
          @containerEl.insertBefore @el, @alertEl
        else
          @containerEl.appendChild @el
        # get els in super
        super
        if /webkit/i.test navigator.userAgent
          $(@el).disableSelection()
        else
          $('.box-header, .btn', @el).disableSelection()
        # deal with form
        @form = find 'form', @el
        $form = $ @form
        $(@btn_close).remove() if readonly = @projectMode or @readonly
        @form.key.readOnly = readonly
        if @readonly
          $form.find('input, textarea').prop 'readOnly', true
          $form.find('select, input[type=checkbox], input[type=radio]').prop 'disabled', true
        # for event button
        @$el.find('.btn-event').each ->
          $btn = $(@)
          $parent = $btn.parents('.event-inputs')
          name = null
          if model.isNew() or
          (name = $btn.siblings('input[name$=duration]').attr 'name') and not (id = data[name = name[0...-9]]?.id)?
            $btn.parent().removeClass 'input-append'
            $btn.remove()
          else
            $btn.prop 'href', "#event/#{id}"
          # for event input enable
          unless model.isNew()
            event = data[name]
            checked = event? and (Number(event.duration) or event.ends)
            $parent.find('input[type=checkbox]').prop 'checked', checked
            $parent.find('input[name$=duration]').prop 'disabled', not checked
        # for page buttons
        if @type is 'page' and contentId = model.get('content')?.id
          url = "#content/#{contentId}"
          #console.log @type, url
          btnDesgin = find 'a.btn-design', @el
          btnDesgin.removeAttribute 'disabled'
          btnDesgin.href = url
          btnPreview = find 'a.btn-preview', @el
          btnPreview.removeAttribute 'disabled'
          btnPreview.href = url + '/preview'
        # auto fill data and build ref
        @fill data
        @$el.data model: model, view: @
        @listenTo model, 'destroy', @remove.bind @
      super
    fill: (data) -> # filling the form with data
      return unless data and @form
      data.name = @_name unless data.name
      data.key = data.type unless data.key
      data.event?.duration = DurationConvertor.stringify data.event.duration
      data.tracking?.duration = DurationConvertor.stringify data.tracking.duration
      form = @form
      namespace = ''
      do _travel = (namespace, data) ->
        for name, value of data
          continue if '_' is name.charAt(0)
          name = namespace + name
          if $.isPlainObject value
            _travel name + '.', value
          else if form[name]?.name is name
            #console.log name, value
            el = form[name]
            if /^checkbox$/i.test(el.type) and typeof value is 'boolean'
              el.checked = value
            else
              $(el).val value
        return
      @
    read: -> # read form the form to get a json data
      throw new Error 'cannot find the form, may not rendered yet' unless @form
      data = @model.toJSON()
      data.type = @type.toUpperCase()
      for el in [].slice.call @form.elements
        if el.name and not el.disabled
          $el = $ el
          val = if /^checkbox$/i.test(el.type) then el.checked else $el.val()
          names = el.name.split '.'
          if names?.length
            _data = data
            name = names.pop()
            _data = _data[key] ?= {} for key in names
            _data[name] = val
      # data.event.duration must exist, 0 for no wait
      data.event ?= {}
      if data.event.duration
        data.event.duration = DurationConvertor.parse data.event.duration if typeof data.event.duration is 'string'
      else if data.event.ends?
        delete data.event.duration
      else
        data.event.duration = 0
      # data.tracking must not exist if not tracking (duration is 0)
      if data.tracking?
        duration = data.tracking.duration
        duration = data.tracking.duration = DurationConvertor.parse duration if duration and typeof duration is 'string'
        if duration or data.tracking.ends?
          data.tracking.name or= data.name + ' (Tracking)'
        else
          delete data.tracking
      # auto fill ids to these sub entities
      for n in ['content', 'event', 'tracking']
        if data[n]?
          _data = data[n]
          _data.action_id = data.id if data.id?
          _data.node_id = data.node_id if data.node_id?
          _data.workflow_id = data.workflow_id if data.workflow_id?
          _data.name or= data.name
      data

  ActionsMixin
