"use strict"

require ['lib/backbone.localstorage'], ->
  test_data_ver = 15
  window.load_test_data = -> require ['test_data'], -> localStorage._test_data_loaded = test_data_ver
  cur_ver = Number localStorage._test_data_loaded or 0
  load_test_data() if cur_ver < test_data_ver
  require ['main'] # EP
  return
