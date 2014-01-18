"use strict"

do -> # browser test
  EXCUSE = 'Marxo Console is powered by HTML5 technologies which need a modern browser to support.\nPlease upgrade or switch your browser.'
  UA = navigator.userAgent
  fail = (msg) ->
    alert if msg then "#{msg}\n\n#{EXCUSE}" else EXCUSE
    location.href = 'http://browsehappy.com/?locale=en'
  if /MSIE[1-8]\b/i.test UA
    fail 'Microsoft Internet Explorer versions below 9 are not supported!'
  else if /^Opera\//i.test UA
    fail 'Old versions of Opera (before 15) are not tested for compatibility!\nA recent version of Opera (from 15) is recommended.'
  else if /^Mozilla\/4/i.test UA
    fail 'Your browser is out-of-date!'
  return

console.log 'ver', 'console', 5

requirejs.config
  shim:
    'lib/jquery-ui':
      deps: ['lib/common']
  config:
    models:
      BASE_URL: '/api'

define 'main', ['lib/backbone.localstorage', 'console'],
(ignored, {findAll, ConsoleView, SignInView, Router}) -> # EP

  EP = ->
    if localStorage.no_transition
      cls = document.body.classList
      cls.remove 'fade'
      cls.add 'no-transition'
      el.classList.remove 'fade' for el in findAll '.fade'

    window.app =
      console: ConsoleView.get()
      signin: SignInView.get()
      router: Router.get()

    Backbone.history.start()

    document.title = document.title.replace /^.*?(?=MARXO)/i, ''
    document.body.style.opacity = 1
    return

  # DEMO DATA VERSION
  demo_data_ver = 1

  # auto load test data
  load_demo_data = window.load_demo_data = (callback) ->
    localStorage.clear()
    require ['data'], ->
      localStorage._demo_data_loaded = demo_data_ver
      callback?()
    return
  cur_ver = Number localStorage._demo_data_loaded or 0
  if demo_data_ver <= cur_ver then EP() else load_demo_data EP

  return
