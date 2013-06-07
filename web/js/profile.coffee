"use strict"

define 'profile', ['console'],
({
#find
#findAll
#View
FrameView
#ModalDialogView
# Project
}) ->
  class ProfileFrameView extends FrameView
    initialize: (options) ->
      super options

  ProfileFrameView
