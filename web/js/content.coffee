"use strict"

define 'content', ['console'],
({
#find
#findAll
#View
FrameView
#ModalDialogView
# Project
}) ->
  class ContentFrameView extends FrameView
    initialize: (options) ->
      super options

  ContentFrameView
