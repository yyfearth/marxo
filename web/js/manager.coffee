"use strict"

define 'manager', ['base', 'models', 'lib/backgrid'],
({
find
findAll
tpl
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
      field = @column.get 'name'
      placement = @column.get 'placement'
      title = @model.get field
      id = @model.id
      tooltip_field = @column.get('tooltip') or @column.get('name') or 'title'
      tooltip = @model.get(tooltip_field) or @model.get('name') # do not need escape here
      url = unless @urlRoot then null else '#' + @urlRoot + id
      @$el.addClass(field + '-link-cell').append $('<a>',
        tabIndex: -1
        href: url
      ).text title
      @$el.attr title: tooltip, 'data-placement': placement, 'data-container': 'body'
      @delegateEvents()
      @

  class Backgrid.TooltipCell extends Backgrid.StringCell
    className: 'tooltip-cell'
    render: ->
      super
      placement = @column.get 'placement'
      key = @column.get('tooltip') or @column.get('name') or 'title'
      tooltip = @model.get key # do not need escape here
      @$el.attr title: tooltip, 'data-placement': placement, 'data-container': 'body'
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
    className: 'action-cell'
    _tpl: {}
    tpl: (type) -> # load form html template
      unless type then '' else @_tpl[type] ?= tpl "#t_#{type}_action_cell"
    render: ->
      html = @tpl @column.get('name') or @name
      @el.innerHTML = html.replace /\{\{id\}\}/g, @model.id
      @el.dataset.model = @model.id
      @$el.data 'model', @model
      @$el.find('.btn[title]').attr 'data-container': 'body'
      @delegateEvents()
      @
    _find: (name, tag) ->
      find "#{tag or ''}[name='#{name}']", @el
    _hide: (name, tag) ->
      _btn = if typeof name isnt 'string' then name else @_find name, tag
      _btn?.style.display = 'none'
      _btn

  class Backgrid.ProjectCell extends Backgrid.UriCell
    render: ->
      @$el.empty()
      id = @model.get('project_id')
      if id then Projects.find projectId: id, callback: ({project}) =>
        if project
          name = _.escape project.get 'name'
        else
          console.warn 'project not found', id
          name = '(Unknown Project)'
        @$el.addClass('project-link-cell').append $('<a>',
          tabIndex: -1
          href: '#project/' + id
        ).attr('title', name).text name
        @delegateEvents()
      @

  class Backgrid.NodeActionCell extends Backgrid.UriCell
    render: ->
      @$el.empty()
      project_id = @model.get 'project_id'
      node_id = @model.get 'node_id'
      action_id = @model.get 'action_id'
      url = "#project/#{project_id}/node/#{node_id}/action/#{action_id}"
      Projects.find
        projectId: project_id
        nodeId: node_id
        actionId: action_id
        callback: ({node, action}) =>
          if node and action
            node_name = _.escape node.get 'name'
            action_name = _.escape action.get('name') or action.get('type')
            tooltip = "#{node_name}: #{action_name}"
            html = "<span class='node-title'>#{node_name}</span>: #{action_name}"
            @$el.addClass('action-link-cell').append $('<a>',
              tabIndex: -1
              href: url
            ).attr(title: tooltip).html html
            @delegateEvents()
          else
            console.warn 'failed to get node action for url', url
      @

  # single label tag
  class Backgrid.LabelCell extends Backgrid.StringCell
    className: 'label-cell'
    formatter:
      fromRaw: (raw) -> raw.toLowerCase()
      toRaw: (formatted) -> formatted.toUpperCase()
    render: ->
      @$el.empty()
      rawValue = @model.get @column.get 'name'
      if rawValue
        val = rawValue.toLowerCase()
        labelCls = 'label capitalized '
        if val isnt 'none' and @column.has 'cls'
          cls = @column.get 'cls'
          cls = cls[val] or '' unless typeof cls is 'string'
          labelCls += cls
        else
          labelCls += "label-#{val}"
        formattedValue = @formatter.fromRaw rawValue
        @$el.append $('<span>', class: labelCls).text formattedValue
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
      'keydown input[type=text]': (e) ->
        if e.which is 27 # ESC
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
      if query?
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
      $el = @$el.find('input[type=text]')
      if $el.val()
        $el.val('')
        @search null
      @
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
      throw new Error 'nav filter only accept one options.field' unless typeof field is 'string'
      @fields = [field]
      @keys = field.split '.'
      root = options.urlRoot or @urlRoot or field
      @_regex = new RegExp "##{root}:(\\w+)"
      @_matchers = {}
      super options
    getMatcher: (query) ->
      @_matchers[query] ?= @makeMatcher query
    makeMatcher: (query) ->
      console.log 'q', query
      keys = @keys
      if keys.length is 1
        if query is ''
          (model) ->
            value = model.get keys[0]
            value is '' or not value?
        else
          regexp = new RegExp query.trim(), 'i'
          (model) ->
            regexp.test model.get keys[0]
      else
        if query is ''
          (model) ->
            value = model.get keys[0]
            while value and key = keys[++i]
              value = value[key]
            value is '' or not value?
        else
          regexp = new RegExp query.trim(), 'i'
          (model) ->
            i = 0
            value = model.get keys[0]
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
        @search switch query
          when 'all' then null
          when 'empty' then ''
          else query
        last?.classList.remove 'active'
        a.parentElement.classList.add 'active'
      @

  class ProjectFilterView extends NavFilterView
    field: 'project_id'
    urlRoot: 'project'
    headerTitle: 'Projects'
    initialize: (options) ->
      super options
      @allowEmpty = options.allowEmpty
      @headerTitle = options.headerTitle or @headerTitle
      @list = new NavListView
        el: @el
        auto: false
        collection: Projects.projects
        urlRoot: @urlRoot
        headerTitle: @headerTitle
        defaultItem: 'all'
        emptyItem: 'empty'
        allowEmpty: @allowEmpty
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
        cell: 'tooltip'
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
        name: 'project_id'
        label: 'Project'
        cell: 'project'
        editable: false
      node_action:
        name: 'action'
        label: 'Node: Action'
        cell: 'node-action'
        editable: false
      type:
        name: 'type'
        label: 'Type'
        cell: 'label'
        cls: 'label-info'
        editable: false
      status:
        name: 'status'
        label: 'Status'
        cell: 'label'
        cls: 'label-info'
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
      'click .action-cell button[name]': '_action_cell'
      'click .action-buttons .btn': '_action_buttons'
      'change .select-row-cell input[type="checkbox"]': '_selection_changed'
    initialize: (options) ->
      @events ?= {}
      for own event, action of @_defaultEvents
        @events[event] ?= action

      super options

      @collection = options.collection if options.collection instanceof ManagerCollection
      collection = @collection
      throw new Error 'collection must be a instance of ManagerCollection' unless collection instanceof ManagerCollection
      # add a sequence to models
      _gen_seq = -> collection.fullCollection.each (model, i) -> model._seq = i
      @listenTo collection.fullCollection, 'reset add remove', _gen_seq
      @listenTo collection, 'add remove', _gen_seq
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
              when 'name'
                name: 'name'
                label: 'Name'
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
    refresh: ->
      @grid.body.refresh()
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
      # reflect to select all
      checkboxes = findAll '.select-row-cell input[type=checkbox]', @el
      checked = checkboxes[0]?.checked
      indeterminate = checkboxes.some (box) -> box.checked isnt checked
      checkAll = find '.select-all-header-cell input[type=checkbox]', @el
      checkAll.indeterminate = indeterminate
      checkAll.checked = checked unless indeterminate
      return

  { # exports
  ManagerView
  NavFilterView
  ProjectFilterView
  }
