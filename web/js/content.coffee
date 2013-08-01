"use strict"

define 'content', [
  'console', 'models', 'manager'
  'lib/jquery-ui'
  'lib/bootstrap-wysiwyg'
], ({
find
findAll
#View
BoxView
FrameView
InnerFrameView
FormDialogView
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
    open: (name) ->
      if name
        @editor.render() unless @editor.rendered
        @editor.popup {}, (action, data) => # test only
          console.log action, data
      else
        @manager.render() unless @manager.rendered
        @editor.cancel()
      @

  class ContentEditor extends FormDialogView
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
    events:
      'click #new_section': -> @addSection null # test only
    initialize: (options) ->
      super options
      @on 'hidden', -> history.go(-1) if /content\/.+/.test location.hash
      @editor = find '.rich-editor', @el
      @contentDesc = new BoxView el: find '#content_desc', @el
      @submitOptions = new SubmitOptionsEditor el: find '#submit_options', @el
      @sectionsEl = find '#sections', @el
      $(@sectionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content'
      @
    popup: (data, callback) ->
      super data, callback
      @fill data
      @
    save: ->
      @data = @read()
      @callback 'save'
      @hide true
      @
    fill: (data) ->
      super data
      @addSection null # test only
      @
    reset: ->
      super
      @sectionsEl.innerHTML = ''
      @
    addSection: (section) ->
      section = new SectionEditor id: 0, parent: @ # test only
      section.render()
      console.log section
      @sectionsEl.appendChild section.el
      @
    removeSection: (section) ->
      section.close()
      @
    _renderFonts: ->
      fontTarget = find '.fonts-select', @el
      fontTarget.innerHTML = ''
      flagment = document.createDocumentFragment()
      for fontName in @_fonts
        li = document.createElement 'li'
        a = document.createElement 'a'
        a.dataset.edit = "fontName #{fontName}"
        a.style.fontFamily = fontName
        a.textContent = fontName
        li.appendChild a
        flagment.appendChild li
      fontTarget.appendChild flagment
    render: ->
      super
      @$el.find('a[title]').tooltip container: @el
      @_renderFonts()
      @$el.find('.btn.hyperlink').click ->
        setTimeout =>
          $(@).siblings('.dropdown-menu').find('input').focus()
        , 200
      @$el.find('.dropdown-menu input').click(-> false).change(->
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

  class ChangeTypeMixin
    changeType: (type) ->
      console.log 'change type', type
      @optionFields ?= findAll '.option-field', @el
      for field in @optionFields
        cls = field.classList
        show = type and cls.contains type + '-option'
        required = find '[data-option-required]', field
        required?.required = show
        if show
          cls.remove 'hide'
        else
          cls.add 'hide'
      @

  class SectionEditor extends BoxView
    @acts_as ChangeTypeMixin
    tagName: 'section'
    className: 'box section'
    tpl: do ->
      tpl_el = document.querySelector('#section_tpl')
      throw 'cannot load template from #section_tpl' unless tpl_el
      tpl_el.parentNode.removeChild tpl_el
      tpl_el.innerHTML
    initialize: (options) ->
      super options
      @id ?= options.id
      @id = 'section_' + @id if typeof @id is 'number'
      console.log @id
      throw 'id must be given for a section' unless @id
      @
    _bind: ->
      typeEl = @_find 'type'
      typeEl.onchange = => @changeType typeEl.value
      titleEl = @_find 'title'
      title = find '.box-title', @el
      titleEl.onchange = ->
        title.textContent = unless @value then 'New Section' else 'Section: ' + @value
        true
      auto_gen = @_find 'gen_from_list'
      auto_gen_key = @_find 'gen_list_key'
      manual_options = @_find 'manual_options'
      auto_gen.onchange = ->
        auto_gen_key.disabled = not @checked
        cls = manual_options.classList
        if @checked
          cls.add 'hide'
          cls.remove 'radio-option'
        else
          cls.remove 'hide'
          cls.add 'radio-option'
        true
      @
    _find: (part_id) ->
      find "##{@id}_#{part_id}", @el
    render: ->
      super
      @el.id = @id
      @el.innerHTML = @tpl.replace /section_#/g, @id
      @_bind()
      @
    close: ->
      @destroy()
    destroy: ->
      @el.parentNode.removeChild @el
      @

  class SubmitOptionsEditor extends BoxView
    @acts_as ChangeTypeMixin
    initialize: (options) ->
      super options
      changeType = @changeType.bind @
      @$el.on 'change', 'input[type=radio]', ->
        changeType @value if @checked
      @

  ## manager

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
      edit_btn = find 'a[name="edit"]', @el
      if 'POSTED' isnt @model.get 'status'
        edit_btn.href = '#content/' + @model.id
      else
        edit_btn.style.display = 'none'
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
      _remove = @remove.bind @
      @on
        remove: _remove
        remove_selected: _remove
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

  ContentFrameView
