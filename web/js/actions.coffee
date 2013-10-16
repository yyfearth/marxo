"use strict"

define 'actions', ['base', 'models', 'lib/jquery-ui'],
({find, findAll, tplAll, BoxView}, {Action, Actions}) ->

  class ActionsMixin
    initActions: (options) ->
      @actionsEl = options?.actionEl or find '.node-actions', @el
      @projectMode = Boolean options?.projectMode
      $(@actionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content'
      @
    fillActions: (actions) ->
      # @clearActions()
      unless actions instanceof Actions
        actions = actions.actions?() or new Actions actions.actions or actions
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
          @$el.one 'shown', -> el.scrollIntoView()
        else if hidden is 'false'
          el.scrollIntoView()
        else
          setTimeout ->
            el.scrollIntoView()
          , 600
      el
    addAction: (model, options) ->
      model = new Action model unless model instanceof Action
      actionView = new ActionView model: model, parent: @, container: @actionsEl, projectMode: @projectMode
      @listenTo actionView, 'remove', @removeAction.bind @
      actionView.render()
      actionView.el.scrollIntoView() if options?.scrollIntoView
      @delayedTrigger 'actions_update', 100
      #@actions.add actionView
      @
    removeAction: (view) ->
      console.log 'remove action view', view
      view.remove?()
      @delayedTrigger 'actions_update', 100
      @

  class ActionView extends BoxView
    className: 'box action'
    _tpl: tplAll('#actions_tpl')
    initialize: (options) ->
      super options
      @projectMode = options.projectMode
      @containerEl = options.container
      @model = options.model
      @model.view = @
      @type = @model.get?('type') or options.model.type or options.type
      throw new Error 'need action model and type' unless @model and @type
      @
    remove: ->
      model = @model
      model.type = null
      model.name = null
      model.data = null
      # remove only once
      @remove = -> @
      super
    render: ->
      _tpl = @_tpl[@type]
      unless _tpl
        console.error 'unable to find tpl for action type', @type
        @remove()
      else
        @el.innerHTML = @_tpl[@type]
        @el.id = 'action_' + @model.id or 'no_id'
        @_name = @$el.find('.box-header h4').text()
        #@containerEl.appendChild @el
        @containerEl.insertBefore @el, find '.alert', @containerEl
        # get els in super
        super
        if /webkit/i.test navigator.userAgent
          $(@el).disableSelection()
        else
          $('.box-header, .btn', @el).disableSelection()
        @form = find 'form', @el
        @form.key.readOnly = @projectMode
        $(@btn_close).remove() if @projectMode
        @fill @model?.toJSON()
        @$el.data model: @model, view: @
        @listenTo @model, 'destroy', @remove.bind @
      @
    fill: (data) -> # filling the form with data
      return unless data and @form
      data.name = @_name unless data.name
      data.key = data.type unless data.key
      form = @form
      for name, value of data
        el = form[name]
        #form[key].value = value
        $(el).val value if el?.getAttribute?('name') is name
      # TODO: support customized controls
      @
    read: (data) -> # read form the form to get a json data
      throw new Error 'cannot find the form, may not rendered yet' unless @form
      data ?= {}
      els = [].slice.call @form.elements
      els.forEach (el) ->
        $el = $ el
        name = $el.attr 'name'
        data[name] = $el.val() if name
      # TODO: support customized controls
      data

  ActionsMixin
