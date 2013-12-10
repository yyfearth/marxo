"use strict"

require ['lib/backbone.localstorage', 'models'], (ignored, {Entity, User, Workflow}) ->
  test_data_ver = 20

  # auto load test data
  window.load_test_data = -> require ['test_data'], -> localStorage._test_data_loaded = test_data_ver
  cur_ver = Number localStorage._test_data_loaded or 0
  load_test_data() if cur_ver < test_data_ver

  # override for local test data
  Entity::syncValidation = -> return

  User::idAttribute = 'email'

  Workflow::save = (attributes = {}, options = {}) ->
    _getAttr = (r, i) ->
      attr = r.attributes
      attr.id ?= i
      if Array.isArray attr.actions
        action.id ?= i for action, i in attr.actions
      attr
    if @nodes? then attributes.nodes = @nodes.map _getAttr
    if @links? then attributes.links = @links.map _getAttr
    console.log 'save local', @_name, attributes
    super attributes, options

  require ['main'] # EP
  return
