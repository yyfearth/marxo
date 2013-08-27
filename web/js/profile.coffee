"use strict"

define 'profile', ['base'],
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
