define 'console', ['lib/common'], ->
  find = (selector, parent) ->
    parent ?= document
    parent.querySelector selector

  findAll = (selector, parent) ->
    parent ?= document
    [].slice.call parent.querySelectorAll selector

  class ConsoleView extends Backbone.View
    el: '#main'
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    initialize: ->
      @frames = {}
      findAll('.frame', @el).forEach (frame) =>
        navEl = find "#navbar a[href=\"##{frame.id}\"]"
        @frames[frame.id] = id: frame.id, el: frame, navEl: navEl?.parentElement
        return
      [ # for debug only
        'home'
        'project'
        'content'
        'report'
        'config'
        'profile'
      ].forEach (n) => @frames[n] = new FrameView @frames[n]
      @fixStyles()
      return
    fixStyles: ->
      navContainer = find '#navbar', @el
      framesContainer = find '#frames', @el
      do window.onresize = =>
        h = navContainer.clientHeight or 41
        framesContainer.style.top = h + 'px'
        return
      return
    showFrame: (frame) ->
      frame = @frames[frame]
      return unless frame?
      unless frame instanceof FrameView
        require [frame.id], (TheFrameView) =>
          frame = @frames[frame.id] = new TheFrameView frame
          frame.render()
          return
      unless frame.el.classList.contains 'active'
        find('#main .frame.active')?.classList.remove 'active'
        find('#navbar li.active')?.classList.remove 'active'
        frame.el.classList.add 'active'
        frame.navEl.classList.add 'active'
      return
    signout: ->
      # TODO: sign out
      delete sessionStorage.user
      SignInView.get().show()
      @hide()
      @trigger 'signout'
      return

    show: ->
      @el.style.visibility = 'visible'
      @el.classList.add 'active'
      @el.style.opacity = 1
      return
    hide: ->
      @el.classList.remove 'active'
      setTimeout =>
        @el.style.visibility = 'hidden'
        return
      , SignInView::delay
      return

  class FrameView extends Backbone.View
    initialize: (options) ->
      @navEl = options.navEl or (find "#navbar a[href=\"##{@id}\"]")?.parentElement
      return

  class SignInView extends Backbone.View
    el: '#signin'
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    events:
      'submit form': 'submit'
    submit: -> # fake
      console.log 'sign in'
      @signedIn()
      false
    signedIn: -> # debug only
      user = id: 'test', name: 'test'
      sessionStorage.user = JSON.stringify user
      @trigger 'success', user
      @hide()
      ConsoleView.get().show()
      # Router.get().navigate 'home'
      location.hash = ''
      return
    delay: 500
    show: ->
      @el.style.opacity = 0
      @el.style.display = 'block'
      setTimeout =>
        @el.classList.add 'active'
        @el.style.opacity = 1
        return
      , 1
      return
    hide: ->
      @el.classList.remove 'active'
      @el.style.opacity = 0
      setTimeout =>
        @el.style.display = 'none'
        return
      , @delay
      return

  class Entity extends Backbone.Model
    idAttribute: '_id'
    set: (attrs) ->
      @_name = attrs.name.tolowerCase().replace /\W+/g, '_' if attrs.name
      super attrs
    validate: (attrs) ->
      unless attrs.name and attrs.id
        'id and name are required'
      else if attrs.name.length > 10
        'name max len is 10'
      else
        return

  # TODO: include workflow models when need

  class Tenants extends Backbone.Collection
    model: Tenant
    url: '/'

  class Tenant extends Entity
    idAttribute: '_name'

  class User extends Entity

  class Participants extends Backbone.Collection
    model: Participant
    url: '/users'

  class Publichers extends Backbone.Collection
    model: Publicher
    url: -> @tenant.url() + '/users'

  class Participant extends User

  class Publicher extends User

  class Evalutator extends User # TODO: howto save them

  class Router extends Backbone.Router
    @get: -> # singleton
      unless @instance?
        @instance = new @
      @instance
    frames: [
      'home'
      'project'
      'workflow'
      'calendar'
      'content'
      'report'
      'config'
      'profile'
    ]
    constructor: (options) ->
      @route '', 'home', =>
        @navigate 'home', replace: true
        @show 'home'
      @frames.forEach (frame) =>
        @route frame + '(/:name)', frame, (name) => @show frame, name
        return
      @route 'signin', 'signin', => return
      @route 'signout', 'signout'
      return

    show: (frame, name) ->
      unless sessionStorage.user
        @navigate 'signin', replace: true
        return
      console.log 'route', frame, name or ''
      if frame isnt @current
        @current = frame
        ConsoleView.get()?.showFrame frame
      handler = @[frame]
      handler.call @, name if handler?
      return

    home: ->
      return
    project: (name) ->
      return
    workflow: (name) ->
      return
    calendar: (name) ->
      return
    content: (name) ->
      return
    report: (name) ->
      return

    signout: ->
      console.log 'sign out'
      ConsoleView.get().signout()
      @navigate 'signin', replace: true
      return

  { # exports
  find
  findAll
  ConsoleView
  FrameView
  SignInView
  Entity
  Tenants
  Tenant
  User
  Participants
  Publichers
  Participant
  Publicher
  Evalutator
  Router
  }
