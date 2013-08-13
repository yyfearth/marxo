"use strict"

define 'manager', ['console', 'models'],
({
find
InnerFrameView
NavListView
}, {
ManagerCollection
Projects
}) ->

  ## Cells

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

  class Backgrid.LinkCell extends Backgrid.UriCell
    initialize: (options) ->
      super options
      @urlRoot = @column.get('urlRoot') or @urlRoot
      @urlRoot += '/' if @urlRoot and @urlRoot[-1..] isnt '/'
    render: ->
      @$el.empty()
      key = @column.get 'name'
      title = @model.get key
      if title.id and title.title
        id = title.id
        title = title.title
        tooltip = _.escape title
      else
        id = @model.id
        tooltip = @model.escape(@column.get('tooltip') or @column.get('name') or 'title')
      url = unless @urlRoot then null else '#' + @urlRoot + id
      @$el.addClass(key + '-link-cell').append $('<a>',
        tabIndex: -1
        href: url
      ).text title
      @$el.attr title: tooltip, 'data-container': 'body'
      @delegateEvents()
      @

  class Backgrid.TooltipCell extends Backgrid.StringCell
    className: 'tooltip-cell'
    render: ->
      super
      key = @column.get('tooltip') or @column.get('name') or 'title'
      tooltip = @model.escape key
      @$el.attr title: tooltip, 'data-container': 'body'
      @

  class Backgrid.ReadonlyDatetimeCell extends Backgrid.StringCell
    className: 'datetime-cell'
    formatter: null
    initialize: (options) ->
      @formatter ?=
        fromRaw: (datetime) ->
          if not datetime
            ''
          else if datetime instanceof Date
            datetime.toLocaleString()
          else if typeof datetime is 'number' or /^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(?:\.\d{3})?Z$/.test datetime
            new Date(datetime).toLocaleString()
          else
            console.error 'unsupported datetime', datetime
            ''
        toRaw: -> return
      super options

  class Backgrid.ActionsCell extends Backgrid.Cell
    @_tpl: {}
    @tpl: (type) -> # load form html template
      if not type
        ''
      else if @_tpl? and @_tpl[type]?
        @_tpl[type] # cached
      else
        el = find "#t_#{type}_action_cell"
        if el?
          # load template
          tpl = @_tpl[type] = el.innerHTML
          # remove template from dom
          el.parentNode.removeChild el
        else
          throw 'cannot find template for type: ' + type
        tpl
    className: 'action-cell'
    render: ->
      @el.innerHTML = @constructor.tpl @column.get('name') or @name
      @el.dataset.model = @model.id
      @$el.data 'model', @model
      @$el.find('.btn[title]').attr 'data-container': 'body'
      @delegateEvents()
      @


  class Backgrid.NodeActionCell extends Backgrid.LinkCell
    render: ->
      @$el.empty()
      project = @model.get 'project'
      node = @model.get 'node'
      action = @model.get 'action'
      url = "#project/#{project.id}/node/#{node.id}/action/#{action.id}"
      tooltip = "#{node.title}: #{action.title}"
      html = "<span class='project-title'>#{_.escape node.title}</span>: #{_.escape action.title}"
      @$el.addClass('action-link-cell').append $('<a>',
        tabIndex: -1
        href: url
      ).html html
      @$el.attr title: tooltip, 'data-container': 'body'
      @delegateEvents()
      @

  ## Paginator
  class ManagerPaginator extends Backgrid.Extension.Paginator
    className: 'pagination'
    initialize: (options) ->
      super options
      # fix for backgrid.paginator re-render
      @listenTo @collection, 'reset', @render.bind @
    render: ->
      super
      if @collection.state.totalPages < 2
        @$el.hide()
      else
        @$el.show()
      @

  ## Filters

  class MergeableFilter extends Backgrid.Extension.ClientSideFilter
    events:
      'click .close': (e) ->
        e.preventDefault()
        @clear()
        false
      'input input[type=text]': 'search'
      'submit': (e) ->
        e.preventDefault()
        @search()
        false
    initialize: (options) ->
      super options
      @_shared_matchers = @collection._matchers ?= []
    getMatcher: (query) ->
      @makeMatcher(query).bind @
    mergeMatcher: (query) ->
      _matchers = @_shared_matchers
      #console.log 'old _matchers', _matchers
      for matcher, i in _matchers
        if matcher._filter is @
          _matchers.splice i, 1
          break
      #console.log 'existing other _matchers', _matchers
      if query
        matcher = @getMatcher query
        matcher._filter = @
        _matchers.push matcher
      #console.log 'add matcher', _matchers
      if _matchers.length > 1
        # merge matchers for more than one
        #console.log 'combine _matchers', _matchers
        (query) ->
          for matcher in _matchers
            return false if false is matcher? query
          true
      else
        # only or no matcher
        _matchers[0]
    search: ->
      @_search @$el.find('input[type=text]').val()
    _search: (query) ->
      query = null unless query
      console.log 'search', query
      col = @collection
      col.pageableCollection?.getFirstPage silent: true
      matcher = @mergeMatcher query
      if matcher
        shadow = @_gen_seq @shadowCollection.filter matcher
        col.reset shadow, reindex: false
        col._filtered = true
      else if col._filtered # query is null and no other matchers
        col.reset @_gen_seq(@shadowCollection.models), reindex: false
        col._filtered = false
      @lastQuery = query
      @
    clear: ->
      @$el.find('input[type=text]').val('')
      @search null
    _gen_seq: (col) ->
      col.forEach (model, i) -> model._seq = i
      col

  class NavFilterView extends MergeableFilter
    events:
      'click a[href]': (e) ->
        e.preventDefault()
        @select e.target
        false
    initialize: (options) ->
      field = options?.field or @field
      throw 'nav filter only accept one options.field' unless typeof field is 'string'
      @fields = [field]
      @keys = field.split '.'
      root = options.urlRoot or @urlRoot or field
      @_regex = new RegExp "##{root}:(\\w+)"
      @_matchers = {}
      super options
    getMatcher: (query) ->
      @_matchers[query] ?= @makeMatcher query
    makeMatcher: (query) ->
      regexp = new RegExp query.trim(), 'i'
      keys = @keys
      (model) ->
        i = 0
        value = model.get keys[i]
        while value and key = keys[++i]
          value = value[key]
        regexp.test value
    search: (query) ->
      @_search query
    render: ->
      @delegateEvents()
      @
    clear: ->
      @select null
    select: (a) ->
      # 1st a is the default
      a ?= find 'a[href]', @el
      last = find '.active', @el
      if last isnt a?.parentElement
        matched = a.href.match @_regex
        query = matched?[1]
        console.log 'filter', @fields[0], query
        query = null if query is 'all'
        @search query
        last?.classList.remove 'active'
        a.parentElement.classList.add 'active'
      @

  class ProjectFilterView extends NavFilterView
    field: 'project_id'
    urlRoot: 'project'
    headerTitle: 'Projects'
    initialize: (options) ->
      super options
      @headerTitle = options.headerTitle or @headerTitle
      @list = new NavListView
        el: @el
        auto: false
        collection: Projects.projects
        urlRoot: @urlRoot
        headerTitle: @headerTitle
        defaultItem: 'all'
        itemClassName: 'project-list-item'
    render: ->
      super
      @list.render() unless @list.fetch()
      @

  ## Manager View

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
        cell: 'tooltip'
        editable: false
      desc:
        name: 'desc'
        label: 'Description'
        cell: 'string'
        editable: false
      project:
        name: 'project'
        label: 'Project'
        urlRoot: 'project'
        cell: 'link'
        editable: false
      node_action:
        name: 'action'
        label: 'Node: Action'
        cell: 'node-action'
        editable: false
      status: # TODO: change to list cell and editable
        name: 'status'
        label: 'Status'
        cell: 'string'
        editable: false
      created_at:
        name: 'created_at'
        label: 'Date Created'
        cell: 'readonly-datetime'
        editable: false
      updated_at:
        name: 'updated_at'
        label: 'Date Updated'
        cell: 'readonly-datetime'
        editable: false
    }
    _defaultEvents:
      'click .action-cell .btn': '_action_cell'
      'click .action-buttons .btn': '_action_buttons'
      'change .select-row-cell input[type="checkbox"]': '_selection_changed'
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
      @listenTo collection.fullCollection, 'reset', _gen_seq
      @listenTo collection, add: _gen_seq, remove: _gen_seq
      # selection may change after remove
      @listenTo collection, 'remove', @_selection_changed.bind @
      # page size alias
      @pageSize = collection.state.pageSize or 15
      #window.collection = collection # debug
      #window.mgr = @ # debug

      @grid = new Backgrid.Grid
        columns: @_configColumns()
        collection: collection
      @paginator = new ManagerPaginator
        collection: collection
      @filter = new MergeableFilter
        collection: collection.fullCollection,
        fields: ['title']
        wait: 300

      # tooltip on bottom
      $('.action-buttons .btn[title]').attr 'data-placement': 'bottom', 'data-container': 'body'
      @
    _configColumns: ->
      columns = []
      for cfg in @columns
        if typeof cfg is 'string'
          cfgs = cfg.split ':'
          if cfgs[1]
            cfg = switch cfgs[0]
              when 'actions'
                name: cfgs[1]
                label: ''
                editable: false
                sortable: false
                cell: 'actions'
              when 'title'
                name: 'title'
                label: 'Title'
                cell: 'link'
                urlRoot: cfgs[1]
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
      super
      @$el.find('table.grid-table').replaceWith @grid.render().$el.addClass 'grid-table'
      @$el.find('.grid-paginator').replaceWith @paginator.render().$el.addClass 'grid-paginator'
      @$el.find('.grid-filter').empty().append @filter.render().$el
      @reload()
      @$enable_if_selected = @$el.find '.enable_if_selected'
      @
    reload: ->
      @collection.fetch reset: true
      @collection.getPage 1
      @filter.clear()
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
      $(btn).tooltip 'hide'
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

  { # exports
  ManagerView
  NavFilterView
  ProjectFilterView
  }
