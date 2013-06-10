"use strict"

define 'project', ['console'],
({
#find
#findAll
#View
FrameView
InnerFrameView
#ModalDialogView
# Project
}) ->
  class ProjectFrameView extends FrameView
    initialize: (options) ->
      super options
      @creator = new ProjectCreatorView el: '#project_creator', parent: @
      @viewer = new ProjectViewerView el: '#project_viewer', parent: @
      @manager = new ProjectManagemerView el: '#project_manager', parent: @
      return
    open: (name) ->
      switch name
        when 'new'
          @switchTo @creator
        when 'mgr'
          @switchTo @manager
        else
          if name
            @switchTo @viewer
            @viewer.load name
      return

  class ProjectCreatorView extends InnerFrameView
#    initialize: (options) ->
#      super options
  class ProjectViewerView extends InnerFrameView
#    initialize: (options) ->
#      super options
  class ProjectManagemerView extends InnerFrameView
#    initialize: (options) ->
#      super options
    load: (name) ->
      console.log 'load project', name
      @

  ProjectFrameView
