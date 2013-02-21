find = (selector, parent) ->
  parent ?= document
  parent.querySelector selector

findAll = (selector, parent) ->
  parent ?= document
  [].slice.call parent.querySelectorAll selector

frames =
  names: ['home', 'project', 'workflow', 'calendar', 'content', 'report', 'config', 'profile']
  _el: find '#main'
  _els: findAll '#main .frame'

frames.names.forEach (n) ->
  frame = frames[n] = find '#' + n
  frame.navEl = (find "#navbar a[href=\"##{n}\"]")?.parentElement
  return

do fixStyles = ->
  padTop = find('#navbar').clientHeight
  document.body.style.paddingTop = padTop + 'px'
  do window.onresize = ->
    # console.log 'resize', window.innerHeight, frames._els
    frames._els.forEach (el) -> if el.tagName.toLowerCase() is 'iframe'
      el.style.height = window.innerHeight - padTop + 'px'
      el.style.top = padTop + 'px'
    #    console.log el.tagName
    return
  return

do fakeSignin = ->
  signin = $ '#signin'
  main = $ '#main'
  delay = 500;
  $('#signin :submit').click(->
    signin.css 'opacity', 0
    main.show()
    setTimeout ->
      signin.hide();
      return
    , delay
    false
  ).click()

# backbone

class Router extends Backbone.Router
  routes:
    '': 'home'
    'home': 'home'

  initialize: ->
    frames.names.forEach (frame) =>
      @route frame + '(/:name)', frame, (name) => @show frame, name
      return
    return

  show: (frame, name) ->
    console.log 'frame', frame, name
    frame = frames[frame]
    return unless frame?
    unless frame.classList.contains 'active'
      find('#main .frame.active')?.classList.remove 'active'
      find('#navbar li.active')?.classList.remove 'active'
      frame.classList.add 'active'
      frame.navEl.classList.add 'active'
    @[frame]? name
    return

  home: ->
    @navigate 'home', replace: true
    @show 'home'
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

window.app = app = {}
app.router = new Router

Backbone.history.start()
