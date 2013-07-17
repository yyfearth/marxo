"use strict"

define 'content', ['console', 'models', 'manager', 'lib/bootstrap-wysiwyg'],
({
find
#findAll
#View
FrameView
InnerFrameView
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
      @editor = new ContentEditor el: '#content_editor', parent: @
      @
    render: ->
      super
      @manager.render()
      @editor.render()
      # test
      @

  class ContentEditor extends InnerFrameView
    _fonts: [
      'Serif'
      'Sans'
      'Arial'
      'Arial Black'
      'Courier'
      'Courier New'
      'Comic Sans MS'
      'Helvetica'
      'Impact'
      'Lucida Grande'
      'Lucida Sans'
      'Tahoma'
      'Times'
      'Times New Roman'
      'Verdana'
    ]
    initialize: (options) ->
      super options
      @editor = find '.rich-editor', @el
      @
    _renderFonts: ->
      fontTarget = find '.fonts-select', @el
      fontTarget.innerHTML = ''
      flagment = document.createDocumentFragment()
      for fontName in @_fonts
        li = document.createElement 'li'
        a = document.createElement 'a'
        a.dataset.edit = 'fontName #{fontName}'
        a.style.fontFamily = fontName
        a.textContent = fontName
        li.appendChild a
        flagment.appendChild li
      fontTarget.appendChild flagment
      return
    render: ->
      super
      @$el.find('a[title]').tooltip container: @el
      @_renderFonts()
      @$el.find('.btn.hyperlink').click ->
        setTimeout =>
          $(@).siblings('.dropdown-menu').find('input').focus()
        , 200
      @$el.find('.dropdown-menu input').click( -> false).change(->
        $(@).parent('.dropdown-menu').siblings('.dropdown-toggle').dropdown 'toggle'
      ).keydown (e) ->
        if e.which is 27 # esc
          @value = ''
          $(@).change().parents('.dropdown-menu').siblings('.dropdown-toggle').dropdown 'toggle'
        true
      @$el.find('[type=file]').each ->
        overlay = $(@)
        target = $(overlay.data('target'))
        overlay.css(opacity: 0, position: 'absolute', cursor: 'pointer').offset(target.offset())
          .width(target.outerWidth()).height target.outerHeight()
      @$el.find('.rich-editor').wysiwyg()
      @

  class ContentActionCell extends Backgrid.ActionsCell
    render: ->
      super
      # TODO: show buttons depend on status
      view_btn = find 'a[name="view"]', @el
      url = @model.get 'url'
      if url
        view_btn.href = @model.get 'url'
      else
        view_btn.style.display = 'none'
      find('button[name="edit"]', @el).style.display = 'none' if 'POSTED' is @model.get 'status'
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
      'node_action'
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
      super
      @mediaFilter.clear()
      @projectFilter.clear()
    render: ->
      super
      @mediaFilter.render()
      @projectFilter.render()
      @
  #view: (models) ->
  #  models = [models] unless Array.isArray models
  #  console.log 'view', models

  ContentFrameView
