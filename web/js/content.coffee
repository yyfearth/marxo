"use strict"

define 'content', [
  'base', 'models', 'manager'
  'lib/jquery-ui'
  'lib/bootstrap-fileupload'
  'lib/bootstrap-wysiwyg'
], ({
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
FormDialogView
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
      @editor = new TextEditor el: '#text_editor', parent: @
      @composer = new EmailComposer el: '#email_composer', parent: @
      @designer = new PageDesigner el: '#page_designer', parent: @
      @
    open: (name, arg) ->
      if name
        @load name, arg, (action, data) ->
          if action is 'save'
            data.save {},
              success: (content) ->
                console.log 'saved', content
              error: ->
                console.error 'save failed'
          else
            console.log action, data
      else
        @manager.render() unless @manager.rendered
        @designer.cancel()
        @editor.cancel()
        @composer.cancel()
      @
    load: (id, action, callback) ->
      if id instanceof Content
        @popup id, callback
      else if typeof id is 'string'
        new Content({id}).fetch success: (data) => @popup data, action, callback
      else
        throw new Error 'content editor can only load a content model or an id string'
    popup: (data, action, callback) ->
      media = data.get 'type'
      editor = switch media
        when 'FACEBOOK', 'TWITTER'
          @editor
        when 'PAGE'
          @designer
        when 'EMAIL'
          @composer
        else
          throw new Error 'unsupported media type ' + media
      editor.render() unless editor.rendered
      editor.popup data, action, callback
      @

  class ContentEditorMixin
    goBackOnHidden: 'content'
    load: (id, action, callback) ->
      if id instanceof Content
        @popup id, callback
      else if typeof id is 'string'
        new Content({id}).fetch success: (data) => @popup data, action, callback
      else
        throw new Error 'content editor can only load a content model or an id string'

  class RichEditorMixin
    _fonts: [
      'Serif', 'Sans', 'Arial', 'Arial Black'
      'Courier', 'Courier New', 'Comic Sans MS'
      'Helvetica', 'Impact', 'Lucida Grande', 'Lucida Sans'
      'Tahoma', 'Times', 'Times New Roman', 'Verdana'
    ]
    events:
      'click .btn.hyperlink': (e) ->
        setTimeout ->
          $(e.currentTarget).siblings('.dropdown-menu').find('input').focus()
        , 200
      'click .btn-switch': '_switch'
    readOnlyHtml: (val) ->
      @readOnly = val
      @$editor.attr 'contenteditable', not val
      @$code.attr 'readonly', val
      @$edits.prop 'disabled', val
      @
    fillHtml: (html) -> # can only be called after rendered
      @$editor.html html or ''
      @
    readHtml: ->
      if @$code.is(':visible') then @$code.val() else @$editor.cleanHtml()
    resetHtml: ->
      @$code.val('')
      @_switch false
      @$el.find('.btn-switch').removeClass 'active'
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
      return
    renderRichEditor: ->
      @_renderFonts()
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
      @$editor = @$el.find('.rich-editor').wysiwyg()
      @$code = @$editor.siblings('.rich-editor-html')
      @$edits = @$el.find('.btn-toolbar').find('[data-edit],.btn.dropdown-toggle,.btn-edit')
      @$edits.tooltip container: @el
      @
    _switch: (toCode) ->
      $editor = @$editor
      $code = @$code
      toCode = not $code.is ':visible' unless typeof toCode is 'boolean'
      if toCode
        $editor.hide()
        $code.show().val $editor.cleanHtml()
      else
        $code.hide()
        $editor.show().html $code.val()
      @$edits.prop 'disabled', toCode unless @readOnly
      return

  class TextEditor extends FormDialogView
    @acts_as ContentEditorMixin
    popup: (data, ignored, callback) ->
      super data, callback
      @fill data
      posted = 'POSTED' is data.get 'status'
      @form.desc.readOnly = posted
      @btnSave.disabled = posted
      @
    fill: (data) ->
      media = data.get 'type'
      @$el.find('small.media').text "(#{media.toLowerCase()})"
      @form.name.value = data.get 'name'
      @form.desc.value = data.get 'desc'
      textarea = @form.desc
      switch media
        when 'FACEBOOK'
          textarea.maxLength = 65535
          textarea.rows = 10
        when 'TWITTER'
          textarea.maxLength = 140
          textarea.rows = 5
        else
          console.warn 'text editor is only for socal media, not for page or email!', media
      @
    read: -> @form.desc.value
    save: ->
      @data.set 'desc', @read()
      @callback 'save'
      @hide true
      @

  class EmailComposer extends FormDialogView
    @acts_as ContentEditorMixin, RichEditorMixin
    popup: (data, ignored, callback) ->
      super data, callback
      @fill data
      posted = 'POSTED' is data.get 'status'
      @readOnlyHtml posted
      @btnSave.disabled = posted
      @
    fill: (data) -> # can only be called after rendered
      super data.attributes
      @fillHtml data.get 'desc'
      @
    read: ->
      data = super
      data.desc = @readHtml()
      data
    save: ->
      @data.set @read()
      @callback 'save'
      @hide true
      @
    reset: ->
      super
      @resetHtml()
      @
    render: ->
      super
      @renderRichEditor()
      @

  class PageDesigner extends ModalDialogView
    @acts_as ContentEditorMixin
    _preview_html_tpl: tpl('#preview_html_tpl').replace(/_tpl_?(?=[^<]*>)/g, '')
    _preview_submit_tpl: tpl('#preview_submit_tpl')
    events:
      'click #new_section': -> @addSection()
      'click .btn-save': 'save'
      'click .btn-preview': 'togglePreview'
    initialize: (options) ->
      super options
      @iframe = find 'iframe', @el
      @btnSave = find '.btn-save', @el
      @btnPreview = find '.btn-preview', @el
      @pageDesc = new PageDescView el: find '#page_desc', @el
      #@submitOptions = new SubmitOptionsEditor el: find '#submit_options', @el
      @sections = []
      @sectionsEl = find '#sections', @el
      $(@sectionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content'
      #@on 'sections_update', =>
      #  count = (findAll '.section', @sectionsEl).length
      #  @submitOptions.$el[if count then 'show' else 'hide']()
      @
    popup: (data, action, callback) ->
      super data, callback
      #console.log 'content form', data.attributes
      page_desc =
        name: data.get 'name'
        desc: data.get 'desc'
      @pageDesc.fill page_desc
      #@submitOptions.fill data.get 'options' if data.has 'options'
      sections = data.get('sections') or []
      if data.has 'sections'
        @addSection section for section in sections
      else # add an empty section if sections have never been defined
        @addSection()
      if action is 'preview'
        @showPreview {page_desc, sections}
        @btnSave.disabled = true
      @
    read: (callback) ->
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

      throw new Error 'content editor read is async, callback is needed' unless typeof callback is 'function'

      defered = [read @pageDesc]
      for el in findAll '.box.section', @el
        _idx = el.dataset.idx
        data = read @sections[_idx]
        defered.push data
      #defered.push read @submitOptions

      $.when.apply(@, defered).fail(-> callback null).done (page_desc, sections..., submit_options) ->
        #console.log 'save content editor', page_desc, sections, submit_options
        callback {page_desc, sections, submit_options}
      @
    save: ->
      @togglePreview() if @iframe.classList.contains 'active'
      @read (data) =>
        if data
          console.log 'save content', data
          @data.set 'name', data.page_desc.name
          @data.set 'desc', data.page_desc.desc
          # TODO: deal with invalid settings
          @data.set 'sections', data.sections
          @data.set 'options', data.submit_options
          @callback 'save'
          @hide true
    reset: -> # called after close
      super
      @sectionsEl.innerHTML = ''
      @pageDesc.reset()
      #@submitOptions.reset()
      @iframe.classList.remove 'active'
      @btnPreview.classList.remove 'active'
      @btnSave.disabled = false
      @
    addSection: (data) ->
      view = new SectionEditor idx: @sections.length, parent: @
      view.render()
      view.fill data
      #console.log data
      @sectionsEl.appendChild view.el
      @sections.push view
      @listenTo view, 'remove', =>
        @sections[view.id] = null
        #@delayedTrigger 'sections_update', 100
      #@delayedTrigger 'sections_update', 1
      @
    removeSection: (view) ->
      view.remove()
      @
    showPreview: (data) ->
      #console.log 'read', data
      throw new Error 'data is empty for gen preview' unless data
      console.log 'show preview', data
      cls = @iframe.classList
      btnCls = @btnPreview.classList
      iframe = @iframe
      html = @_genPreview data
      if html isnt iframe.getAttribute 'srcdoc'
        iframe.setAttribute 'srcdoc', html
        unless 'srcdoc' of iframe
          url = 'javascript: window.frameElement.getAttribute("srcdoc");'
          iframe.src = url
          iframe.contentWindow?.location = url
      cls.add 'active'
      btnCls.add 'active'
      @
    togglePreview: ->
      cls = @iframe.classList
      btnCls = @btnPreview.classList
      if cls.contains 'active'
        # hide
        cls.remove 'active'
        btnCls.remove 'active'
        @btnSave.disabled = false
      else
        # gen preview and show
        @btnPreview.disabled = true
        @read (data) =>
          #console.log 'read', data
          if data
            @showPreview data
          else
            cls.remove 'active'
            btnCls.remove 'active'
          @btnPreview.disabled = false
      @

    _genPreview: ({page_desc, sections}) ->
      #console.log 'gen preview page', page_desc, sections
      content = ["<h1>#{page_desc.name}</h1>\n<p>#{page_desc.desc or ''}</p>"]
      for data, i in sections
        view = new SectionEditor idx: i
        content.push view.genPreview data
      content = content.join '\n'
      content += @_preview_submit_tpl if sections?.length
      @_preview_html_tpl.replace '{{content}}', content

    render: ->
      @pageDesc.render()
      #@submitOptions.render()
      _body = find '.modal-body', @el
      $(_body).find('.btn[title]').tooltip container: _body
      super
      @

  class BoxFormView extends BoxView
    @acts_as FormViewMixin
    render: ->
      @initForm()
    reset: ->
      @form.reset()
      @

  class PageDescView extends BoxFormView
    @acts_as RichEditorMixin
    fill: (data) -> # can only be called after rendered
      super data
      @fillHtml data.desc
      @
    read: ->
      data = super
      data.desc = @readHtml()
      data
    reset: ->
      super
      @resetHtml()
      @
    render: ->
      super
      @renderRichEditor()
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

  #class SubmitOptionsEditor extends BoxFormView
  #  @acts_as ChangeTypeMixin
  #  events:
  #    'change input[type=radio]': ->
  #      @changeType @value if @checked
  #  # TODO: support options
  #  reset: ->
  #    super
  #    @$el.find('input[type=radio]').change()
  #    @

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
      throw new Error 'id must be given for a section' unless @id
      @
    _bind: ->
      # bind title change
      titleEl = @_find 'name'
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
      @updatePreview = _.debounce @updatePreview.bind(@), 500
      @on 'change fill reset', => @updatePreview @data
      @
    _find: (part_id) ->
      find "##{@id}_#{part_id}", @el
    fill: (data) ->
      @reset()
      data = unless data? then {} else $.extend {}, data, data.options
      super data
      if data.type is 'radio' and not data.gen_from_list and data.manual_options
        # manual options
        @autoIncOptionList.fill data.manual_options
      @
    read: ->
      data = super()
      # manual options
      if data?.type is 'radio' and not data.gen_from_list
        data.manual_options = @autoIncOptionList.read()
      # TODO: stop if invalid

      # convert to data and data.options
      options = data
      data =
        name: options.name
        desc: options.desc
        type: options.type
        options: options
      delete options.name
      delete options.desc
      delete options.type
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
      throw new Error 'cannot find preview tpl with name section' unless tpls.section
      tpls
    genPreview: (data) ->
      #console.log 'gen preview', @id, data
      tpl = @_preview_tpl
      type = data.type or ''
      options = data.options or {}
      switch type
        when ''
          body = ''
        when 'text'
          body = if options.text_multiline then tpl.textarea else tpl.text
        when 'html'
          body = tpl.html
        when 'radio'
          el = tpl.radio.replace '{{name}}', "#{@id}_preview_radio"
          list = unless options.gen_from_list then options.manual_options else [
            'List item 1 (Auto Genearted)'
            'List item 2 (Auto Genearted)'
            '... (Auto Genearted)'
          ]
          body = list.map((item) -> el.replace '{{text}}', item).join '\n'
        when 'file'
          accept = options.file_accept
          if accept is 'image/*'
            body = tpl.image
          else
            accept = unless accept then '' else "accept='#{accept}' "
            body = tpl.file.replace /accept(?:=['"]{2})?/, accept
        else
          throw new Error 'unknown section type ' + type
      tpl.section
        .replace('{{title}}', data.name or '(Need a Title)')
        .replace('{{desc}}', data.desc?.replace(/\n/g, '<br/>') or '')
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
      throw new Error 'cannot find manual option tpl' unless tpl
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
      model = @model
      # view
      view_btn = @_find 'view', 'a'
      if model.has 'url'
        view_btn.href = model.get 'url'
      else
        @_hide view_btn

      # report
      report_btn =  @_find 'report', 'a'
      if model.has('records') or model.has('submissions')
        report_btn.href = "#content/#{model.id}/report"
      else
        @_hide report_btn

      # status
      status = model.get('status').toUpperCase()
      if 'IDLE' is status
        @_hide report_btn
        @_hide 'preview' if 'PAGE' isnt model.get 'type'
        # block / unblock
        #@_hide if 'PAUSED' is status then 'block' else 'unblock'
      else
        # buttons pre post
        @_hide 'edit'

      @_hide 'block'
      @_hide 'unblock'
      @

  class ContentManagerView extends ManagerView
    columns: [
      # 'checkbox'
      'id'
      'name:content'
    ,
      name: 'type'
      label: 'Media'
      cell: 'label'
      cls:
        page: 'label-success icon-page'
        twitter: 'label-twitter icon-twitter'
        facebook: 'label-facebook icon-facebook'
        email: 'label-info icon-mail'
      editable: false
    ,
      'workflow'
      'node_action'
    ,
      name: 'status'
      label: 'Status'
      cell: 'label'
      cls:
        posted: 'label-success'
        waiting: 'label-info'
        blocked: 'label-inverse'
      editable: false
    ,
      name: 'posted_at'
      label: 'Date Posted'
      cell: 'readonly-datetime'
      editable: false
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
        el: find('.media-filter', @el)
        field: 'type'
        collection: collection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: collection
      @refresh = @refresh.bind @
      @on
        block: @block.bind @
        unblock: @unblock.bind @
      @
    block: (model) ->
      if confirm 'Are you sure to block this content to post to its media?'
        model.save {status: 'BLOCKED'}, wait: true, success: @refresh
      @
    unblock: (model) ->
      # TODO: check if it can be unlocked (it may expired)
      if confirm 'Are you sure to unblock this content and post to its media?'
        model.save {status: 'WAITING'}, wait: true, success: @refresh
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
