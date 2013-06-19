"use strict"

require ['lib/common', 'main'], ->
  require ['lib/test/backbone.localstorage'], ->
    window.load_test_data = -> require ['test_data']
  return
