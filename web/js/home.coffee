"use strict"

define 'home', ['console'],
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
