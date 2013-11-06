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

console.log 'ver', 3

requirejs.config
  shim:
    'lib/jquery-ui':
      deps: ['lib/common']
    'lib/facebook':
      exports: 'FB'
  paths:
    'lib/facebook': '//connect.facebook.net/en_US/all'
  config:
    models:
      BASE_URL: 'http://masonwan.com/marxo/api' # '../api'
    config:
      FB_APP_ID: '213527892138380'
      FB_SCOPES: 'publish_actions, email, read_stream, '

define 'main', ['console'], ({ConsoleView, SignInView, Router}) ->
  # EP
  window.app = app =
    console: ConsoleView.get()
    signin: SignInView.get()
    router: Router.get()

  Backbone.history.start()

  document.body.style.opacity = 1
  document.title = document.title.replace /^.*?(?=MARXO)/i, ''
  return
