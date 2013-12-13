'use strict'

define 'report', ['base', 'models'],
({
find
#findAll
#View
#InnerFrameView
ModalDialogView
}, {
# Tenant
ROOT
Report
}) ->

  class ReportView extends ModalDialogView
    el: '#report_viewer'
    goBackOnHidden: 'content'
    events:
      'click .nav-tabs li.disabled': (e) ->
        e.stopImmediatePropagation()
        false
      'change #accumulative': '_renderRecords'
    initialize: (options) ->
      super options
      @accumulative = find '#accumulative', @el
      @$el.find('a[data-toggle=tab]').on 'shown', (e) =>
        @trigger 'tab:' + e.target.target
      @
    popup: (model, callback) ->
      super model, callback
      @model = model
      console.log model

      unless records?.length # gen test data
        records = @_genRandReports()
        @model.set 'records', records

      @once
        shown: ->
          @_renderOverview()
          @_renderRecords()
          @_renderSubmissions()
          return
        hide: -> # clear charts
          @renderChart '#stacked', 'area', [], chart: @_records_chart if @_records_chart
          @renderChart '#overview_chart', 'bar', [], chart: @_overview_chart if @_overview_chart
          return
      @
    _disableTab: (el) ->
      @$el.find("[target=#{el}]").removeAttr('href').parent('li').addClass 'disabled'
      return
    _record_map:
      likes_count: 'Facebook Likes'
      comments_count: 'Facebook Comments'
      shares_count: 'Facebook Shares'
      visit_count: 'Page Visit'
      submissions_count: 'Submission'
    _renderOverview: ->
      records = @model.get 'records'
      $title = @$el.find('#overview_chart .title')
      if records?.length
        last = records.slice(-1)[0]
        console.log 'last', last
        to = new Date last.created_at
        from = new Date records[0].created_at
        @$el.find('#overview_chart .title').text "#{from.toLocaleString()} - #{to.toLocaleString()}"
        values = []
        for own field, name of @_record_map
          values.push name: name, value: last[field] if last[field]
        @renderChart '#overview_chart', 'bar', [values: values],
          chart: @_overview_chart
          callback: (chart) => @_overview_chart = chart
      else
        $title.text 'No Records Yet'
      return
    _renderTab: (el, field, renderer) ->
      values = @model.get field
      unless values?.length
        @_disableTab el
      else if $(el).is ':visible'
        renderer values, el
      else @once 'tab:' + el, -> renderer values, el
      return
    _renderRecords: -> @_renderTab '#report_feedback', 'records', (records) =>
      accumulative = @accumulative.checked
      index = {}
      datum = []
      for own field, key of @_record_map
        datum.push key: key, values: index[field] = []
      for record in records
        ts = new Date(record.created_at).getTime()
        if isNaN ts
          console.error 'invalide date in record', record
        else for own field, count of record
          idx = index[field]
          if idx?
            unless accumulative
              _count = unless idx.length then 0 else idx[idx.length - 1]._count
              idx.push ts: ts, count: count - _count, _count: count
            else
              idx.push {ts, count}
      # console.log 'parsed records', datum
      @renderChart '#stacked', 'area', datum,
        chart: @_records_chart
        callback: (chart) => @_records_chart = chart
      return
    _renderAnalysis: -> @_renderTab '#report_analysis', 'sections', (sections) =>
      accumulative = @accumulative.checked
      index = {}
      datum = []
      for own field, key of @_record_map
        datum.push key: key, values: index[field] = []
      for record in sections
        ts = new Date(record.created_at).getTime()
        if isNaN ts
          console.error 'invalide date in record', record
        else for own field, count of record
          idx = index[field]
          if idx?
            unless accumulative
              _count = unless idx.length then 0 else idx[idx.length - 1]._count
              idx.push ts: ts, count: count - _count, _count: count
            else
              idx.push {ts, count}
      # console.log 'parsed records', datum
      @renderChart '#stacked', 'area', datum,
        chart: @_records_chart
        callback: (chart) => @_records_chart = chart
      return
    _renderSubmissions: -> @_renderTab '#report_submissions', 'sections', (sections, el) =>
      submissions = @model.get 'submissions'
      unless submissions?.length
        $table = '<div class="text-center"><em class="muted">No submission yet</em></div>'
      else
        col = []
        $thead = $('<tr>').append '<th>#</th>'
        for section, i in sections
          if section.type and 'none' isnt section.type.toLowerCase()
            $thead.append $('<th>', text: section.name)
            section.index = i
            col.push section
        $thead.append '<th>Submitted By</th><th>Submitted At</th>'
        $table = $('<table>', class: 'table table-hover').append $thead
        for submission, i in submissions then if submission.sections?.length
          $row = $('<tr>').append $('<td>', text: i + 1)
          for {index, type, options} in col
            $row.append $cell = $('<td>')
            val = submission.sections[index]
            unless val?
              $cell.html '<td class="muted">-</td>'
              continue
            #console.log 'type', type
            switch type.toLowerCase()
              when 'file'
                $cell.append $ '<a>', class: 'icon-download', href: "#{ROOT}/#{val}/download", text: 'Download'
                $cell.append ' '
                $cell.append $ '<a>',
                  class: 'icon-link-ext'
                  href: "#{ROOT}/#{val}", target: '_blank'
              when 'radio'
                if options.manual_options?.length
                  $cell.text options.manual_options[val]
                else
                  # TODO: auto gen list
                  console.log 'TODO: auto gen list'
              when 'html'
                $cell.text $('<div>').html(val).text()
              else
                $cell.text val
          $row.append $('<td>', text: "#{submission.name} <#{submission.key}>")
          $row.append $('<td>', text: new Date(submission.created_at).toLocaleString())
          $table.append $row
      @$el.find(el).empty().append($table)
      return
    reset: ->
      #@accumulative.checked = true
      @$el.find('.modal-header .nav-tabs a[data-toggle=tab]').each ->
        ($thumb = $ @).attr('href', $thumb.attr 'target').parent('li').removeClass 'disabled'
        return
      #@$el.find('.tab-content > .tab-pane').empty()
      # test only
      @$el.find('.nav-tabs a[data-toggle=tab]:eq(0)').tab 'show' # test
      @
    render: ->
      @reset()
      super
    renderChart: (el, type, data, options) ->
      if _initChart = @_initChart
        console.log 'render chart', el, type
        {nv, d3} = _initChart
        func = _initChart[type]
        unless _initChart.hasOwnProperty(type) and typeof func is 'function'
          throw new Error 'unsupported chart type ' + type
        if chart = options?.chart
          svg = d3.select(el + '>svg')
          if data?.length
            svg.datum(data).transition().duration(300).call chart
          else
            svg.selectAll('*').remove()
          options?.callback? chart
        else nv.addGraph ->
          chart = func options
          d3.select(el + '>svg').datum(data).transition().duration(300).call chart
          nv.utils.windowResize chart.update
          options?.callback? chart
          chart
      else require ['lib/d3v3', 'lib/nvd3'], (d3, nv) =>
        @_initChart =
          d3: d3
          nv: nv
          pie: (options) ->
            chart = nv.models.pieChart()
            .x((d) -> d.name).y((d) -> d.value)
            .color(d3.scale.category10().range()).labelType(options?.labelType or 'percent')
            chart
          bar: (options) ->
            chart = nv.models.discreteBarChart()
            .x((d) -> d.name).y((d) -> d.value)
            .staggerLabels(true).showValues(true)
            chart
          area: (options) ->
            chart = nv.models.stackedAreaChart().useInteractiveGuideline(true)
            .x((d) -> d.ts).y((d) -> d.count)
            formater = d3.time.format if options?.daily then '%x' else '%x %-I:00%p'
            chart.xAxis.tickFormat (d) -> formater new Date d
            chart.yAxis.tickFormat d3.format(',d')
            chart
        @renderChart el, type, data, options
      @

    _genRandReports: -> # for test
      records = []
      fields = Object.keys @_record_map
      ts = Date.now()
      ts = ts - ts % 36000000 # trim to hour
      _c = {}
      _inc =
        likes_count: 10
        comments_count: 5
        shares_count: 5
        visit_count: 3
        submissions_count: 1
      for field in fields
        _c[field] = 0
      c = 100 + (Math.random() * 1000) | 0
      while --c
        record =
          created_at: ts
        ts += 3600000 # 1h
        for field in fields
          inc = _inc[field]
          r = if Math.random() > Math.min(inc / 10, 0.9) then 0 else 1 + (Math.random() * inc) | 0
          record[field] = _c[field] += r
        records.push record
      records

  ReportView
