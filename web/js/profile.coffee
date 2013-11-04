"use strict"

define 'profile', ['base'],
({
find
#findAll
#View
FrameView
FormViewMixin
# Project
User
}) ->
  class ProfileFrameView extends FrameView
    @acts_as FormViewMixin
    initialize: (options) ->
      super options
      throw new Error 'not signed in yet' unless User.current
      @model = User.current
      @initForm()
      @btn = find '#update_user', @el
      @avatar = find '#user_avatar img', @el
      @on 'load', @load.bind @
      @on 'activate', => @delayedTrigger 'load', 100
    render: ->
      super
      @delayedTrigger 'load', 100
    load: ->
      (@model = User.current).fetch success: (data) =>
        unless User.current?
          console.warn 'user logged out'
          return
        console.log 'fetch tenant', data.attributes
        @model = User.current = data
        attrs = data.toJSON()
        sex = attrs.sex
        attrs.sex = unless sex then 'Unspecified' else sex.charAt(0).toUpperCase() + sex[1..]
        console.log attrs
        @fill attrs
        @avatar.src = "https://secure.gravatar.com/avatar/#{attrs.email_md5}?s=200&d=mm"
        @btn.href = '#config/users/' + data.id
        $(@btn).removeAttr 'disabled'
        return
      @

  ProfileFrameView
