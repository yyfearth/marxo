// Generated by CoffeeScript 1.6.3
(function() {
  "use strict";
  require(['lib/backbone.localstorage'], function() {
    var cur_ver, test_data_ver;
    test_data_ver = 17;
    window.load_test_data = function() {
      return require(['test_data'], function() {
        return localStorage._test_data_loaded = test_data_ver;
      });
    };
    cur_ver = Number(localStorage._test_data_loaded || 0);
    if (cur_ver < test_data_ver) {
      load_test_data();
    }
    require(['main']);
  });

}).call(this);
