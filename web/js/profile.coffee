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
      @load = _.throttle @load.bind(@), 100
      @on 'activate', @load
    render: ->
      @load()
      super
    load: ->
      (@model = User.current).fetch success: (data) =>
        unless User.current?
          console.warn 'user logged out'
          return
        console.log 'fetch tenant', data.attributes
        @model = User.current = data
        attrs = data.toJSON()
        sex = attrs.sex
        attrs.sex = if /^male$|^female$/i.test(sex) then sex.capitalize() else 'Unspecified'
        # console.log attrs
        @fill attrs
        @avatar.src = "https://secure.gravatar.com/avatar/#{attrs.email_md5}?s=200&d=mm"
        @btn.href = '#config/users/' + data.id
        $(@btn).removeAttr 'disabled'
        return
      @

  ProfileFrameView
