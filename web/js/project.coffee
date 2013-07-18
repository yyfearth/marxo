"use strict"

define 'project', ['console', 'manager', 'models'],
({
#find
#findAll
#View
FrameView
InnerFrameView
#ModalDialogView
}, {
ManagerView
}, {
Projects
}) ->
  class ProjectFrameView extends FrameView
    initialize: (options) ->
      super options
      @creator = new ProjectCreatorView el: '#project_creator', parent: @
      @viewer = new ProjectViewerView el: '#project_viewer', parent: @
      @manager = new ProjectManagemerView el: '#project_manager', parent: @
      return
    open: (name, sub) ->
      switch name
        when 'new'
          @switchTo @creator
        when 'mgr'
          @switchTo @manager
        else
          if name
            @switchTo @viewer
            @viewer.load name
            @viewer.popup sub if sub
          else unless @manager.rendered
            # 1st time default frame
            @switchTo @manager
      return
    render: ->
      super
      @manager.render()
      @

  class ProjectActionCell extends Backgrid.ActionsCell
    render: ->
      super
      # TODO: show buttons depend on status
      view_btn = @el.querySelector('a[name="view"]')
      view_btn.href = '#project/' + @model.id
      @

  class ProjectManagemerView extends ManagerView
    columns: [
      'checkbox'
      'id'
      'title:project'
      'desc'
      'created_at'
      'updated_at'
      'status'
    ,
      name: 'project'
      label: ''
      editable: false
      sortable: false
      cell: ProjectActionCell
    ]
    collection: new Projects

  class ProjectCreatorView extends InnerFrameView
    #    initialize: (options) ->
    #      super options
  class ProjectViewerView extends InnerFrameView
    #    initialize: (options) ->
    #      super options
    load: (name) ->
      console.log 'load project', name
      @
    popup: ({link, node, action} = {}) ->
      throw 'cannot open a action without given a node' if action and not node
      throw 'node and link cannot be open together' if link and node
      console.log 'popup node/link viewer', {link, node, action}
      @

  ProjectFrameView
