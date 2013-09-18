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
      @designer = new PageDesigner el: '#page_designer', parent: @
      @
    open: (name, arg) ->
      if name
        @designer.render() unless @designer.rendered
        @designer.load name, arg, (action, data) ->
          if action is 'save'
            data.save {},
              success: (content) ->
                console.log 'saved', content
              error: ->
                console.error 'save failed'
          console.log action, data
      else
        @manager.render() unless @manager.rendered
        @designer.cancel()
      @

  class PageDesigner extends ModalDialogView
    _preview_html_tpl: tpl('#preview_html_tpl').replace(/_tpl_?(?=[^<]*>)/g, '')
    _preview_submit_tpl: tpl('#preview_submit_tpl')
    goBackOnHidden: 'content'
    events:
      'click #new_section': -> @addSection()
      'click .btn-save': 'save'
      'click .btn-preview': 'togglePreview'
    initialize: (options) ->
      super options
      @iframe = find 'iframe', @el
      @btnPreview = find '.btn-preview', @el
      @btnSave = find '.btn-save', @el
      @pageDesc = new PageDescView el: find '#page_desc', @el
      @submitOptions = new SubmitOptionsEditor el: find '#submit_options', @el
      @sections = []
      @sectionsEl = find '#sections', @el
      $(@sectionsEl).sortable
        axis: 'y'
        delay: 150
        distance: 15
        cancel: '.box-content'
      @on 'sections_update', =>
        count = (findAll '.section', @sectionsEl).length
        @submitOptions.$el[if count then 'show' else 'hide']()
      @
    load: (id, action, callback) ->
      if id instanceof Content
        @popup id, callback
      else if typeof id is 'string'
        new Content({id}).fetch success: (data) => @popup data, action, callback
      else
        throw 'content editor can only load a content model or an id string'
    popup: (data, action, callback) ->
      super data, callback
      #console.log 'content form', data.attributes
      page_desc =
        title: data.get 'title'
        desc: data.get 'desc'
      @pageDesc.fill page_desc
      @submitOptions.fill data.get 'options' if data.has 'options'
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

      throw 'content editor read is async, callback is needed' unless typeof callback is 'function'

      defered = [read @pageDesc]
      for el in findAll '.box.section', @el
        _idx = el.dataset.idx
        data = read @sections[_idx]
        defered.push data
      defered.push read @submitOptions

      $.when.apply(@, defered).fail(-> callback null).done (page_desc, sections..., submit_options) ->
        #console.log 'save content editor', page_desc, sections, submit_options
        callback {page_desc, sections, submit_options}
      @
    save: ->
      @togglePreview() if @iframe.classList.contains 'active'
      @read (data) =>
        if data
          console.log 'save content', data
          @data.set 'title', data.page_desc.title
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
      @submitOptions.reset()
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
        @delayedTrigger 'sections_update', 100
      @delayedTrigger 'sections_update', 1
      @
    removeSection: (view) ->
      view.remove()
      @
    showPreview: (data) ->
      #console.log 'read', data
      throw 'data is empty for gen preview' unless data
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
      content = ["<h1>#{page_desc.title}</h1>\n<p>#{page_desc.desc or ''}</p>"]
      for data, i in sections
        view = new SectionEditor idx: i
        content.push view.genPreview data
      content = content.join '\n'
      content += @_preview_submit_tpl if sections?.length
      @_preview_html_tpl.replace '{{content}}', content

    render: ->
      super
      @pageDesc.render()
      @submitOptions.render()
      _body = find '.modal-body', @el
      $(_body).find('.btn[title]').tooltip container: _body
      @

  class BoxFormView extends BoxView
    @acts_as FormViewMixin
    render: ->
      @initForm()
    reset: ->
      @form.reset()
      @

  class PageDescView extends BoxFormView
    _fonts: [
      'Serif', 'Sans', 'Arial', 'Arial Black'
      'Courier', 'Courier New', 'Comic Sans MS'
      'Helvetica', 'Impact', 'Lucida Grande', 'Lucida Sans'
      'Tahoma', 'Times', 'Times New Roman', 'Verdana'
    ]
    events:
      'click .btn.hyperlink': (e) ->
        setTimeout =>
          $(e.currentTarget).siblings('.dropdown-menu').find('input').focus()
        , 200
      'click .btn-switch': '_switch'
    fill: (data) -> # can only be called after rendered
      super data
      @$editor.html data.desc or ''
      @
    read: ->
      data = super
      data.desc = if @$code.is(':visible') then @$code.val() else @$editor.cleanHtml()
      data
    reset: ->
      super
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
    render: ->
      super
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
      @$edits.prop 'disabled', toCode

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
      # view
      view_btn = find 'a[name="view"]', @el
      url = @model.get 'url'
      if url
        view_btn.href = @model.get 'url'
      else
        view_btn.style.display = 'none'
      # report & block
      report_btn = find 'a[name="report"]', @el
      if 'POSTED' is @model.get 'status'
        # TODO: report id
        report_btn.href = '#report/test'
        @$el.find('a[name="edit"],button[name="block"]').hide()
      else
        report_btn.style.display = 'none'
        if 'PAGE' isnt @model.get 'media'
          find('a[name="preview"]', @el)?.style.display = 'none'
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
      cell: 'label'
      cls:
        page: 'label-success icon-page'
        twitter: 'label-twitter icon-twitter'
        facebook: 'label-facebook icon-facebook'
        email: 'icon-mail'
      editable: false
    ,
      'project'
      'node_action'
    ,
      name: 'status'
      label: 'Status'
      cell: 'label'
      cls:
        posted: 'label-success'
        waiting: 'label-info'
      editable: false
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
      @on block: @block.bind @
      @
    block: (model) ->
      console.log 'block', model
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
