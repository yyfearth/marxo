find = (selector, parent) ->
  parent ?= document
  parent.querySelector selector

findAll = (selector, parent) ->
  parent ?= document
  [].slice.call parent.querySelectorAll selector

FRAMES = ['home', 'project', 'workflow', 'calendar', 'content', 'report', 'config', 'profile']

class ConsoleView extends Backbone.View
  el: '#main'
  initialize: ->
    @frames = {}
    FRAMES.forEach (n) =>
      @frames[n] = @createFrame n
      return
    @fixStyles()
    return
  createFrame: (name) ->
    switch name
      when 'workflow'
        new WorkflowIFrameView
      when 'project'
        new ProjectIFrameView
      when 'calendar'
        new CalendarIFrameView
      else
        new FrameView name: name
  fixStyles: ->
    padTop = find('#navbar').clientHeight
    document.body.style.paddingTop = padTop + 'px'
    frames = findAll '.frame', @el
    do window.onresize = =>
      # console.log 'resize', window.innerHeight, frames._els
      frames.forEach (el) -> if el.tagName.toLowerCase() is 'iframe'
        el.style.height = window.innerHeight - padTop + 'px'
        el.style.top = padTop + 'px'
      return
    return
  showFrame: (frame) ->
    frame = @frames[frame]
    return unless frame?
    unless frame.el.classList.contains 'active'
      find('#main .frame.active')?.classList.remove 'active'
      find('#navbar li.active')?.classList.remove 'active'
      frame.el.classList.add 'active'
      frame.navEl.classList.add 'active'
      frame.render()
    return

class FrameView extends Backbone.View
  initialize: (options) ->
    @name = options.name
    @setElement find '#' + @name
    @navEl = (find "#navbar a[href=\"##{@name}\"]")?.parentElement
    @

class IFrameView extends FrameView
  constructor: (options) ->
    @styles = ['bootstrap.min']
    @scripts = ['lib/common', 'lib/jquery-ui.custom.min']
    super options
  initialize: (options) ->
    super options
    return
  render: -> unless @doc?
    console.log 'render', @name
    styles = (@styles.map (css) -> "<link rel='stylesheet' type='text/css' href='css/#{css}.css'/>").join '\n'
    scripts = (@scripts.map (js) -> "<script src='js/#{js}.js'></s" + "cript>").join '\n'
    html = "<!DOCTYPE html><html><base href=\"#{location.href}\"/><head lang=\"en\"><meta charset=\"utf-8\"/>#{styles}</head><body>#{scripts}</body></html>"
    if 'srcdoc' of @el
      @el.srcdoc = html
    else
      @el.src = "data:text/html;charset=utf-8," + encodeURI html
    @doc = @el.contentDocument
    @window = @el.contentWindow
    @

class WorkflowIFrameView extends IFrameView
  initialize: (options = {}) ->
    name = options.name = 'workflow'
    @styles.push name
    @scripts.push 'lib/jquery.jsPlumb.min'
    @scripts.push name
    super options

class ProjectIFrameView extends IFrameView
  initialize: (options = {}) ->
    options.name = 'project'
    super options
  render: -> unless @doc?
    super()
    @

class CalendarIFrameView extends IFrameView
  initialize: (options = {}) ->
    name = options.name = 'calendar'
    @styles.push 'fullcalendar'
    @styles.push name
    @scripts.push 'lib/fullcalendar.min'
    @scripts.push name
    super options

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
    @

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
  frames: FRAMES
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

window.app = app =
  console: new ConsoleView
  signin: new SignInView
  router: new Router

app.signin.on 'success', ->
  @hide()
  app.console.$el.show()
  return
# fake
app.signin.trigger 'success'

app.router.console = app.console

Backbone.history.start()
