"use strict"

define 'content', ['console', 'models', 'manager', 'lib/jquery-ui', 'lib/content'], ({
find
findAll
tpl
tplAll
View
BoxView
FrameView
InnerFrameView
ModalDialogView
FormViewMixin
}, {
Contents
Content
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
        @editor.load name, (action, data) ->
          if action is 'save'
            data.save {},
              success: (content) ->
                console.log 'saved', content
              error: ->
                console.error 'save failed'
          console.log action, data
      else
        @manager.render() unless @manager.rendered
        @editor.cancel()
      @

  class ContentEditor extends ModalDialogView
    _fonts: [
      'Serif', 'Sans', 'Arial', 'Arial Black'
      'Courier', 'Courier New', 'Comic Sans MS'
      'Helvetica', 'Impact', 'Lucida Grande', 'Lucida Sans'
      'Tahoma', 'Times', 'Times New Roman', 'Verdana'
    ]
    _html_tpl: tpl('#html_tpl')
    events:
      'click #new_section': -> @addSection()
      'click .btn-save': 'save'
    # TODO: preview btn
    initialize: (options) ->
      super options
      _hash_regex = /content\/.+/
      @on 'hidden', ->
        history.go(-1) if _hash_regex.test location.hash
        setTimeout -> # fallback when go back to the same hash
          location.hash = '#content' if _hash_regex.test location.hash
        , 100
      @editor = find '.rich-editor', @el
      @pageDesc = new BoxFormView el: find '#page_desc', @el
      # TODO: desc rich editor support with code which
      @submitOptions = new SubmitOptionsEditor el: find '#submit_options', @el
      @sections = []
      @sectionsEl = find '#sections', @el
      $(@sectionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content'
      @
    load: (id, callback) ->
      if id instanceof Content
        @popup id, callback
      else if typeof id is 'string'
        new Content({id}).fetch success: (data) => @popup data, callback
      else
        throw 'content editor can only load a content model or an id string'
    popup: (data, callback) ->
      super data, callback
      #console.log 'content form', data.attributes
      @pageDesc.fill data.attributes
      @cfg = data.get 'data'
      #console.log 'cfg data', @cfg
      @submitOptions.fill @cfg?.submit_options
      @cfg?.sections?.forEach (section) =>
        #console.log 'add section', section
        @addSection section
      @
    save: ->
      read = (formView) ->
        deferred = $.Deferred()
        if formView instanceof BoxFormView
          _t = setTimeout ->
            console.warn 'read box form timeout', formView.id
            deferred.reject formView
          , 100
          formView.submit ->
            clearTimeout _t
            # console.log 'passed', formView.el.id, formView.read()
            deferred.resolve formView.read()
        else
          console.error 'read invalid box form', formView
          deferred.reject formView
        deferred.promise()

      defered = [read @pageDesc]
      for el in findAll '.box.section', @el
        _idx = el.dataset.idx
        data = read @sections[_idx]
        data._idx = _idx
        defered.push data
      defered.push read @submitOptions

      $.when.apply(@, defered).done (page_desc, sections..., submit_options) =>
        #console.log 'save content editor', page_desc, sections, submit_options
        @data.set 'title', page_desc.title
        sections = sections.sort (a, b) -> a._idx - b._idx
        delete sec._idx for sec in sections
        # TODO: deal with desc
        # TODO: deal with invalid settings
        @data.set 'data', {page_desc, sections, submit_options}
        @callback 'save'
        @hide true
      @
    reset: -> # called after close
      super
      @sectionsEl.innerHTML = ''
      @pageDesc.reset()
      @submitOptions.reset()
      @
    addSection: (data) ->
      view = new SectionEditor idx: @sections.length, parent: @
      view.render()
      view.fill data
      #console.log data
      @sectionsEl.appendChild view.el
      @sections.push view
      @
    removeSection: (view) ->
      @sections[view.id] = null
      view.remove()
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
      @pageDesc.render()
      @submitOptions.render()
      _body = find '.modal-body', @el
      $(_body).find('.btn[title]').tooltip container: _body
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

  class BoxFormView extends BoxView
    @acts_as FormViewMixin
    render: ->
      @initForm()
    reset: ->
      @form.reset()
      @

  class ChangeTypeMixin
    changeType: (type) ->
      unless type is @_type
        @trigger 'type_change', type, @_type
        #console.log 'change type', type
        @optionFields ?= findAll '.option-field', @el
        for field in @optionFields
          cls = field.classList
          show = type and cls.contains type + '-option'
          required = find '[data-option-required]', field
          required?.required = show
          #for input in findAll 'input, select, file', field
          # input.disabled = not show
          # input.style.visibility = if show then 'visible' else 'hidden'
          if show
            cls.remove 'hide'
          else
            cls.add 'hide'
        @_type = type
      @

  class SubmitOptionsEditor extends BoxFormView
    @acts_as ChangeTypeMixin
    events:
      'change input[type=radio]': ->
        @changeType @value if @checked
    reset: ->
      super
      @$el.find('input[type=radio]').change()
      @

  class SectionEditor extends BoxFormView
    @acts_as ChangeTypeMixin
    tagName: 'section'
    className: 'box section'
    tpl: tpl('#section_tpl')
    initialize: (options) ->
      super options
      @idx = options.idx
      @id ?= options.id or @idx
      @id = 'section_' + @id if typeof @id is 'number'
      throw 'id must be given for a section' unless @id
      @
    _bind: ->
      # bind title change
      titleEl = @_find 'title'
      title = find '.box-title', @el
      titleEl.onchange = ->
        title.textContent = unless @value then 'New Section' else 'Section: ' + @value
        true
      # bind type change
      typeEl = @_find 'type'
      @$typeEl = $ typeEl
      do typeEl.onchange = => @changeType typeEl.value
      # bind radio type change
      auto_gen = @_find 'gen_from_list'
      auto_gen_key = @_find 'gen_list_key'
      manual_options = @_find 'manual_options'
      manual_option_label = find 'input[type=text]', manual_options
      auto_gen.onchange = ->
        auto_gen_key.disabled = not @checked
        cls = manual_options.classList
        if @checked
          cls.add 'hide'
          cls.remove 'radio-option'
          auto_gen_key.select()
        else
          cls.remove 'hide'
          cls.add 'radio-option'
          manual_option_label.select()
        true
      @autoIncOptionList = new AutoIncOptionList el: manual_options
      # bind change event
      @listenTo @autoIncOptionList, 'change', (el) =>
        @trigger 'change', el, @data
      $(@form).on 'change', 'input, textarea, select', (e) =>
        @trigger 'change', e.target, @data
      # bind update preview on any changes
      @previewEl = find '.preview', @el
      @on 'change fill reset', => @delayedTrigger 'update_preview', 500, @data
      @on 'update_preview', @updatePreview.bind @
      @
    _find: (part_id) ->
      find "##{@id}_#{part_id}", @el
    fill: (data) ->
      @reset()
      super data
      if data?.section_type is 'radio' and not data.gen_from_list and data.manual_options
        # manual options
        @autoIncOptionList.fill data.manual_options
      @
    read: ->
      data = super()
      # manual options
      if data?.section_type is 'radio' and not data.gen_from_list
        data.manual_options = @autoIncOptionList.read()
      # TODO: stop if invalid
      data
    render: ->
      @el.id = @id
      @el.dataset.idx = @idx
      @el.innerHTML = @tpl.replace /section_#/g, @id
      super
      @_bind()
      # init read
      @fill()
      @
    reset: ->
      super
      @$typeEl.change()
      @
    _preview_tpl: do ->
      tpls = tplAll '#preview_tpl'
      throw 'cannot find preview tpl with name section' unless tpls.section
      tpls
    genPreview: (data) ->
      #console.log 'gen preview', @id, data
      tpl = @_preview_tpl
      type = data.section_type or ''
      switch type
        when ''
          body = ''
        when 'text'
          body = if data.text_multiline then tpl.textarea else tpl.text
        when 'html'
          body = tpl.html
        when 'radio'
          el = tpl.radio.replace '{{name}}', "#{@id}_preview_radio"
          list = unless data.gen_from_list then data.manual_options else [
            'List item 1 (Auto Genearted)'
            'List item 2 (Auto Genearted)'
            '... (Auto Genearted)'
          ]
          body = list.map((item) -> el.replace '{{text}}', item).join '\n'
        when 'file'
          accept = data.file_accept
          if accept is 'image/*'
            body = tpl.image
          else
            accept = unless accept then '' else "accept='#{accept}' "
            body = tpl.file.replace /accept(?:=['"]{2})?/, accept
        else
          throw 'unknown section type ' + type
      tpl.section
        .replace('{{title}}', data.section_title or '(Need a Title)')
        .replace('{{desc}}', data.section_desc or '')
        .replace('{{body}}', body)
    updatePreview: ->
      data = @read()
      console.log 'update preview', @id, data
      if Object.keys(data).length
        @previewEl.innerHTML = @genPreview data
      else
        @previewEl.innerHTML = ''
      @

  class AutoIncOptionList extends View
    events:
      'input input.manual_option_text.new': (e) ->
        input = e.target
        if input.value.trim()
          input.classList.remove 'new'
          input.required = true
          input.dataset.optionRequired = true
          @_container.appendChild @_tpl.cloneNode true
          @trigger 'change change:add', input, @
        true
      'click .close': (e) ->
        e.preventDefault()
        $el = $(e.target).parents('.manual_option')
        val = $el.find('input.manual_option_text').val()
        $el.remove()
        @trigger 'change change:remove', $el[0], val
        @validate()
        false
      'blur input.manual_option_text': ->
        @validate false
    initialize: (options) ->
      super options
      tpl = find '.manual_option', @el
      throw 'cannot find manual option tpl' unless tpl
      @_tpl = tpl.cloneNode true
      dataset = find('input.manual_option_text[data-option-required]', @_tpl).dataset
      delete dataset.optionRequired
      @_container = find '.controls', @el
      # make options sortable
      $(@_container).sortable
        axis: 'y'
        delay: 150
        distance: 5
        cursor: 'move'
        items: '>.manual_option:not(:has(.new))'
        cancel: 'input.manual_option_text'
        change: (e, ui) => @trigger 'change change:move', ui.item[0], @
      @
    fill: (values) ->
      if values?.length
        frag = document.createDocumentFragment()
        for val in values
          el = @_tpl.cloneNode true
          input = find 'input.manual_option_text', el
          input.value = val
          input.classList.remove 'new'
          frag.appendChild el
        $(@_container).prepend frag
        @validate()
      else
        console.error 'values should be an string array', values
      @
    read: ->
      @validate()
      @values
    validate: (silence) -> # it will generate value
      silence = Boolean silence
      values = {}
      valid = true
      @$el.find('input.manual_option_text:not(.new)').removeClass('error').each ->
        val = @value.trim()
        if not val
          valid = false
          true
        else if values.hasOwnProperty val
          @classList.add 'error'
          $(@).one 'input', -> @classList.remove 'error'
          @select() unless silence
          valid = false
          silence
        else
          @value = val
          values[val] = @
          true
      @values = if valid then Object.keys(values) else null
      valid

  ## manager

  class ContentActionCell extends Backgrid.ActionsCell
    render: ->
      super
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
