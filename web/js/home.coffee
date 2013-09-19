"use strict"

define 'home', ['base', 'models'],
({
find
#findAll
tpl
fill
#View
FrameView
#ModalDialogView
}, {
Project
Projects
}) ->
  class HomeFrameView extends FrameView
    initialize: (options) ->
      super options
    _tpl: tpl('#project_overview_tpl')
    render: ->
      _tpl = @_tpl
      html = []
      Projects.projects.fetch success: (projects) =>
        projects.forEach (project) ->
          obj = project.toJSON()
          obj.counts = "(#{project.get('node_ids')?.length} Nodes, #{project.get('link_ids')?.length} Links)"
          html.push fill _tpl, obj
        find('#home_view', @el).innerHTML = html.join '\n'
      @

  HomeFrameView
