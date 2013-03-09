define 'console', ['lib/common'], ->
  find = (selector, parent) ->
    parent ?= document
    parent.querySelector selector

  findAll = (selector, parent) ->
    parent ?= document
    [].slice.call parent.querySelectorAll selector

  class ConsoleView extends Backbone.View
    el: '#main'
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
        framesContainer.style.top = navContainer.clientHeight + 'px'
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

  class FrameView extends Backbone.View
    initialize: (options) ->
      @navEl = options.navEl or (find "#navbar a[href=\"##{@id}\"]")?.parentElement
      return

  class SignInView extends Backbone.View
    el: '#signin'
    delay: 500
    events:
      'submit form': 'submit'
    submit: -> # fake
      @trigger 'success', id: 'test', name: 'test'
      false
    hide: ->
      @$el.css 'opacity', 0
      setTimeout =>
        @$el.hide();
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
      return

    show: (frame, name) ->
      throw 'console is not binded' unless @console?
      console.log 'route', frame, name or ''
      if frame isnt @current
        @current = frame
        @console?.showFrame frame
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
