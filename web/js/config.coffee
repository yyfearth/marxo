"use strict"

define 'config', ['console'],
({
#find
#findAll
#View
FrameView
InnerFrameView
#ModalDialogView
# Tenant
}) ->

  class ConfigFrameView extends FrameView
    initialize: (options) ->
      super options
      @profile = new TenantProfileView el: '#tenant_profile', parent: @
      @manager = new UserManagemerView el: '#user_manager', parent: @
      return
    open: (name) ->
      if /^users/.test name
        @switchTo @manager
      else if name
        @switchTo @profile
      return

  class TenantProfileView extends InnerFrameView
    initialize: (options) ->
      super options
  class UserManagemerView extends InnerFrameView
    initialize: (options) ->
      super options

  ConfigFrameView
