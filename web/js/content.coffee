"use strict"

define 'content', [
  'module', 'base', 'models', 'manager', 'report'
  'lib/jquery-ui'
  'lib/bootstrap-fileupload'
  'lib/bootstrap-wysiwyg'
], (module, {
find
findAll
tpl
tplAll
fill
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
}, ReportView) ->

  class ContentFrameView extends FrameView
    initialize: (options) ->
      super options
      @manager = new ContentManagerView el: @el, parent: @
      @editor = new MessageEditor el: '#msg_editor', parent: @
      @composer = new EmailComposer el: '#email_composer', parent: @
      @designer = new PageDesigner el: '#page_designer', parent: @
      @reporter = new ReportView el: '#report_viewer', parent: @
      @
    open: (name, arg) ->
      if name
        @popup name, arg, (action, data) ->
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
        @reporter.cancel()
      @
    popup: (data, action, callback) ->
      if typeof data is 'string'
        new Content(id: data).fetch success: (data) => @popup data, action, callback
        return @
      else unless data instanceof Content
        data = new Content data
      type = data.type()
      if action is 'report'
        @reporter.popup data, callback
      else
        editor = switch type
          when 'FACEBOOK', 'TWITTER'
            @editor
          when 'PAGE'
            @designer
          when 'EMAIL'
            @composer
          else
            throw new Error 'unsupported media type ' + type
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
      fragment = document.createDocumentFragment()
      for fontName in @_fonts
        li = document.createElement 'li'
        a = document.createElement 'a'
        a.dataset.edit = "fontName #{fontName}"
        a.style.fontFamily = fontName
        a.textContent = fontName
        li.appendChild a
        fragment.appendChild li
      fontTarget.appendChild fragment
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
      @$el.find('input[type=file]').each ->
        overlay = $(@)
        target = overlay.parents('.btn-edit')
        overlay.width(target.outerWidth()).height target.outerHeight()
      @$editor = @$el.find('.rich-editor').wysiwyg()
      @$code = @$editor.siblings('.rich-editor-html').removeAttr 'name'
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

  class MessageEditor extends FormDialogView
    @acts_as ContentEditorMixin
    popup: (data, ignored, callback) ->
      super data, callback
      @field = @form.message
      @fill data
      posted = 'IDLE' isnt data.status()
      @field.readOnly = posted
      @btnSave.disabled = posted
      @
    fill: (data) ->
      type = data.type()
      field = @field
      @$el.find('small.media').text "(#{type})"
      @form.name.value = data.get 'name'
      field.value = data.get 'message'
      switch type
        when 'FACEBOOK'
          field.maxLength = 65535
          field.rows = 10
        when 'TWITTER'
          field.maxLength = 140
          field.rows = 5
        else
          console.warn 'text editor is only for socal media, not for page or email!', type
      @
    read: -> @field.value
    save: ->
      @data.set 'message', @read()
      @callback 'save'
      @hide true
      @

  class EmailComposer extends FormDialogView
    @acts_as ContentEditorMixin, RichEditorMixin
    popup: (data, ignored, callback) ->
      super data, callback
      @fill data
      posted = 'IDLE' isnt data.get('status').toUpperCase()
      @readOnlyHtml posted
      @$el.find('form :input').prop 'readOnly', posted
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
      @renderRichEditor()
      super

  class PageDesigner extends ModalDialogView
    @acts_as ContentEditorMixin
    _preview_tpl: tplAll('#preview_tpl')
    events:
      'click #new_section': -> @addSection()
      'click .btn-save': 'save'
      'click .btn-preview': 'togglePreview'
    initialize: (options) ->
      super options
      @previewEl = find '.container.preview', @el
      @btnSave = find '.btn-save', @el
      @btnPreview = find '.btn-preview', @el
      @pageDesc = new PageDescView el: find '#page_desc', @el
      @submitOptions = null # new SubmitOptionsEditor el: find '#submit_options', @el
      @sections = []
      @sectionsEl = find '#sections', @el
      $(@sectionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content,.readonly'
      #@on 'sections_update', =>
      #  count = (findAll '.section', @sectionsEl).length
      #  @submitOptions.$el[if count then 'show' else 'hide']()
      @
    popup: (model, action, callback) ->
      super model, callback
      @model = model
      #console.log 'content form', data.attributes
      data =
        name: model.get('name')
        desc: model.get('desc')
        sections: model.get('sections') or []
        options: model.get('options') or {}
      @url = "content/#{model.id}"
      @pageDesc.fill data
      @submitOptions?.fill data.options
      posted = @readonly = 'IDLE' isnt model.status()
      @sectionsEl.classList.add 'readonly' if posted
      if model.has 'sections' # need @readonly
        @addSection section for section in data.sections
      else unless @readonly # add an empty section if sections have never been defined except readonly mode
        @addSection() # need @readonly
      @pageDesc.readOnlyHtml posted
      @$el.find('form :input').prop 'readOnly', posted
      @$el.find('#new_section, form select, form input[type=checkbox]').prop 'disabled', posted
      @btnSave.disabled = posted
      if action is 'preview'
        @showPreview data
        @btnSave.disabled = true
      @
    reset: -> # called after close
      super
      for view in @sections
        try view?.remove()
      @sections = []
      @sectionsEl.innerHTML = ''
      @pageDesc.reset()
      @submitOptions?.reset()
      @previewEl.classList.remove 'active'
      @btnPreview.classList.remove 'active'
      @sectionsEl.classList.remove 'readonly'
      @btnSave.disabled = false
      @url = ''
      @readonly = false
      @
    read: (callback) ->
      throw new Error 'content editor read is async, callback is needed' unless typeof callback is 'function'

      _read = (formView) ->
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

      defered = [_read @pageDesc]
      for el in findAll '.box.section', @el
        _idx = el.dataset.idx
        data = _read @sections[_idx]
        defered.push data
      #defered.push read @submitOptions

      $.when.apply(@, defered).fail(-> callback null).done (page_desc, sections...) -> # , submit_options
        #console.log 'save content editor', page_desc, sections, submit_options
        callback
          name: page_desc.name
          desc: page_desc.desc
          sections: sections
          #options: submit_options
      @
    save: ->
      @togglePreview() if @previewEl.classList.contains 'active'
      @read (data) =>
        if data
          console.log 'save content', data
          @data.set data
          # TODO: deal with invalid settings
          @callback 'save'
          @hide true
    addSection: (data) ->
      view = new SectionEditor idx: @sections.length, readonly: @readonly, parent: @
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
      @previewEl.innerHTML = @_genPreview data
      @previewEl.classList.add 'active'
      @btnPreview.classList.add 'active'
      @
    togglePreview: ->
      cls = @previewEl.classList
      btnCls = @btnPreview.classList
      if cls.contains 'active'
        # hide
        cls.remove 'active'
        btnCls.remove 'active'
        @btnSave.disabled = false or @readonly
        @router.navigate @url
      else
        # gen preview and show
        @router.navigate @url + '/preview'
        if @readonly
          data = @model.toJSON()
          data.page_desc = data
          @showPreview data
        else
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

    _genPreview: (data) ->
      console.log 'gen preview page', data
      sections = data.sections?.map (section, i) ->
        view = new SectionEditor idx: i
        view.genPreview section
      content = fill @_preview_tpl.page,
        title: data.name
        desc: data.desc
        sections: sections.join('\n')
      content = content.replace 'form-actions', 'form-actions hide' unless sections?.length
      content

    render: ->
      @pageDesc.render()
      #@submitOptions.render()
      _body = find '.modal-body', @el
      $(_body).find('.btn[title]').tooltip container: _body
      super

  class BoxFormView extends BoxView
    @acts_as FormViewMixin
    render: ->
      @initForm()
      super
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
    _preview_tpl: PageDesigner::_preview_tpl
    events:
      'change select[name=type]': (e) -> unless @readonly
        $el = $ e.currentTarget
        @_changeType $el.val()
        return
      'change form input[name=name]': (e) ->
        value = e.currentTarget.value
        title = find '.box-title', @el
        title.textContent = unless value then 'New Section' else 'Section: ' + value
        return
      'change input, textarea, select': (e) ->
        @trigger 'change', e.target, @data if @rendered and not @readonly
        return
    initialize: (options) ->
      super options
      @idx = options.idx
      @id ?= options.id or @idx
      @id = 'section_' + @id if typeof @id is 'number'
      throw new Error 'id must be given for a section' unless @id
      @readonly = options.readonly
      @updatePreview = _.debounce @updatePreview.bind(@), 100
      @
    _changeType: (type) ->
      @changeType type
      @$typeEl.siblings('label.checkbox').css 'visibility',
        if not type or type is 'none' then 'hidden' else 'visible'
      return
    _bind: ->
      @$typeEl = $ @_find 'type'
      # bind radio type change
      auto_gen = @_find 'auto_gen'
      auto_gen_key = @_find 'gen_from_submission'
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
      @autoIncOptionList = new AutoIncOptionList
        el: manual_options, readonly: @readonly
      # bind change event
      @listenTo @autoIncOptionList, 'change', (el) =>
        @trigger 'change', el, @data
      # bind update preview on any changes
      @previewEl = find '.preview', @el
      unless @readonly
        @on 'change fill reset', => @updatePreview @data
      @
    _find: (part_id) ->
      find "##{@id}_#{part_id}", @el
    fill: (data) ->
      @reset()
      data = unless data? then {} else $.extend {}, data, data.options
      data.type = if data.type then data.type.toLowerCase() else 'none'
      super data
      if /^radio$/i.test(data.type) and not data.gen_from_submission and data.manual_options
        @_find('auto_gen').checked = data.gen_from_submission
        @autoIncOptionList.fill data.manual_options # manual options
      @_changeType data.type if @readonly
      @updatePreview data
      @
    read: ->
      data = super()
      return {} unless data
      # manual options
      if /^radio$/i.test(data?.type)
        if @_find('auto_gen').checked
          delete data.manual_options
          #console.log 'gen_from_submission', data.gen_from_submission
        else
          delete data.gen_from_submission
          data.manual_options = @autoIncOptionList.read()

      # convert to data and data.options
      type = (data.type or 'none').toUpperCase()
      options = data
      data =
        name: options.name
        desc: options.desc
        type: type
        options: if type is 'NONE' then {} else options
      delete options.name
      delete options.desc
      delete options.type
      data
    render: ->
      unless @rendered
        @el.id = @id
        @el.dataset.idx = @idx
        @el.innerHTML = @tpl.replace /section_#/g, @id
        @_bind()
        super # ready fill
        $(@btn_close).remove() if @readonly # must after super
      @
    reset: ->
      super
      @_changeType 'none'
      @
    genPreview: (data) ->
      #console.log 'gen preview', @id, data
      tpl = @_preview_tpl
      type = data.type or ''
      options = data.options or data
      switch type.toLowerCase()
        when '', 'none'
          body = ''
        when 'text'
          body = if options.text_multiline then tpl.textarea else tpl.text
        when 'html'
          body = tpl.html
        when 'radio'
          el = tpl.radio.replace '{{name}}', "#{@id}_preview_radio"
          list = unless options.gen_from_submission
            options.manual_options or []
          else [
            'Submission 1 (Auto Genearted)'
            'Submission 2 (Auto Genearted)'
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
    updatePreview: (data = @read()) ->
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
        unless @readonly
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
      @readonly = options.readonly
      tpl = find '.manual_option', @el
      throw new Error 'cannot find manual option tpl' unless tpl
      @_tpl = tpl.cloneNode true
      dataset = find('input.manual_option_text[data-option-required]', @_tpl).dataset
      delete dataset.optionRequired
      @_container = find '.controls', @el
      # make options sortable
      unless @readonly then $(@_container).sortable
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
      else
        console.error 'values should be an string array', values
      @validate()
      @
    read: ->
      @validate()
      @values
    validate: (silence) -> # it will generate value
      silence = Boolean silence
      $els = @$el.find 'input.manual_option_text:not(.new)'
      find('input.manual_option_text', @el).required = true
      unless $els.length
        @values = null
        return false
      values = {}
      valid = true
      $els.removeClass('error').each ->
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
      type = model.type()
      status = model.status()

      # view
      view_btn = @_find 'view', 'a'
      if type is 'PAGE' and status isnt 'IDLE'
        view_btn.href = (module.config().SITE_BASE_URL or './#') + model.id
        view_btn.title = 'View page in new window'
      else if model.has('post_id') and type is 'FACEBOOK'
        view_btn.href = 'https://www.facebook.com/' + model.get 'post_id'
      else
        @_hide view_btn

      # report
      report_btn = @_find 'report', 'a'
      if model.get('records')?.length or model.get('submissions')?.length
        report_btn.href = "#content/#{model.id}/report"
      else
        @_hide report_btn

      # status
      if 'IDLE' is status
        @_hide report_btn
        @_hide 'preview' if 'PAGE' isnt model.get 'type'
        # block / unblock
        #@_hide if 'PAUSED' is status then 'block' else 'unblock'
      else
        # buttons pre post
        @_hide 'edit'

      # currently not supported
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
      'status'
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
      @typeFilter = new NavFilterView
        el: find('.type-filter', @el)
        field: 'type'
        collection: collection
      @projectFilter = new ProjectFilterView
        el: find('ul.project-list', @el)
        collection: collection
      @refresh = @refresh.bind @
      #@on
      #  block: @block.bind @
      #  unblock: @unblock.bind @
      @
    #block: (model) ->
    #  if confirm 'Are you sure to block this content to post to its media?'
    #    model.save {status: 'BLOCKED'}, wait: true, success: @refresh
    #  @
    #unblock: (model) ->
    #  if confirm 'Are you sure to unblock this content and post to its media?'
    #    model.save {status: 'WAITING'}, wait: true, success: @refresh
    #  @
    reload: ->
      super
      @typeFilter.clear()
      @projectFilter.clear()
    render: ->
      super
      @typeFilter.render()
      @projectFilter.render()
      @

  ContentFrameView
