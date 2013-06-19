"use strict"

define 'content', ['console', 'models', 'manager'],
({
find
#findAll
#View
FrameView
#ModalDialogView
}, {
Contents
}, {
ManagerView
NavFilterView
ProjectFilterView
}) ->

  class ContentFrameView extends FrameView
    initialize: (options) ->
      super options
      @manager = new ContentManagerView el: @el, parent: @
    render: ->
      super()
      @manager.render()
      @

  class NodeActionCell extends Backgrid.LinkCell
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

  class ContentActionCell extends Backgrid.ActionsCell
    render: ->
      super()
      # TODO: show buttons depend on status
      view_btn = @el.querySelector('a[name="view"]')
      url = @model.get 'url'
      if url
        view_btn.href = @model.get 'url'
      else
        view_btn.style.display = 'none'
      @

  class ContentManagerView extends ManagerView
    columns: [
      'checkbox'
      'id'
    ,
      name: 'title'
      tooltip: 'desc'
      cell: 'tooltip'
      label: 'Title'
      editable: false
    ,
      name: 'media'
      label: 'Media'
      cell: 'string'
      editable: false
    ,
      'project'
    ,
      name: 'action'
      label: 'Node: Action'
      cell: NodeActionCell
      editable: false
    ,
      'status'
    ,
      name: 'posted_at'
      label: 'Date Posted'
      cell: 'readonly-datetime'
      editable: false
    ,
      'created_at'
    ,
      name: 'content'
      label: ''
      editable: false
      sortable: false
      cell: ContentActionCell
    ]
    collection: new Contents
    initialize: (options) ->
      super options
      collection = @collection.fullCollection
      @mediaFilter = new NavFilterView
        el: '#media-filter'
        field: 'media'
        collection: collection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        field: 'project.id'
        collection: collection
      #_view = @view.bind @
      _remove = @remove.bind @
      @on
        edit: @edit.bind @
      #view: _view
      #view_selected: _view
        remove: _remove
        remove_selected: _remove
      @
    edit: (model) ->
      console.log 'edit', model
      @
    remove: (models) ->
      models = [models] unless Array.isArray models
      console.log 'remove', models
      #  if confirm 'Make sure these selected workflows is not in use!\nDo you realy want to remove selected workflows?'
      #    # TODO: check usage, if used cannot remove directly
      #    model?.destroy() for model in models
      #    @reload() if models.length >= @pageSize / 2
      #  #console.log 'delete', model, @
      @
    reload: ->
      super()
      @mediaFilter.clear()
      @projectFilter.clear()
    render: ->
      super()
      @mediaFilter.render()
      @projectFilter.render()
      @
  #view: (models) ->
  #  models = [models] unless Array.isArray models
  #  console.log 'view', models

  ContentFrameView
