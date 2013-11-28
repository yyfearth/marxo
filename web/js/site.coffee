"use strict"

requirejs.config
  shim:
    'lib/jquery-ui':
      deps: ['lib/common']
    'lib/facebook':
      exports: 'FB'
  paths:
    'lib/facebook': '//connect.facebook.net/en_US/all'
  config:
    models: # for testsing
      BASE_URL: localStorage.ROOT ? 'http://masonwan.com/marxo/api' # '../api'
    config:
      FB_APP_ID: '213527892138380'
      FB_SCOPES: 'publish_actions, email, read_stream'

define 'site', ['lib/common'], ->

  return
