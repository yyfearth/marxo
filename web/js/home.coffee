"use strict"

define 'home', ['base'],
({
#find
#findAll
#View
FrameView
#ModalDialogView
# Project
}) ->
  class HomeFrameView extends FrameView
    initialize: (options) ->
      super options

  HomeFrameView
