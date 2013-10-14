// Generated by CoffeeScript 1.6.3
(function() {
  "use strict";
  var browser_test;

  (browser_test = function() {
    var EXCUSE, UA, fail;
    EXCUSE = 'Marxo Console is powered by HTML5 technologies which need a modern browser to support.\nPlease upgrade or switch your browser.';
    UA = navigator.userAgent;
    fail = function(msg) {
      alert(msg ? "" + msg + "\n\n" + EXCUSE : EXCUSE);
      return location.href = 'http://browsehappy.com/?locale=en';
    };
    if (/MSIE[1-8]\b/i.test(UA)) {
      fail('Microsoft Internet Explorer versions below 9 are not supported!');
    } else if (/^Opera\//i.test(UA)) {
      fail('Old versions of Opera (before 15) are not tested for compatibility!\nA recent version of Opera (from 15) is recommended.');
    } else if (/^Mozilla\/4/i.test(UA)) {
      fail('Your browser is out-of-date!');
    }
  })();

  require.config({
    shim: {
      'lib/jquery-ui': {
        deps: ['lib/common']
      },
      'lib/facebook': {
        exports: 'FB'
      }
    },
    paths: {
      'lib/facebook': '//connect.facebook.net/en_US/all'
    }
  });

  define('main', ['console'], function(_arg) {
    var ConsoleView, Router, SignInView, app;
    ConsoleView = _arg.ConsoleView, SignInView = _arg.SignInView, Router = _arg.Router;
    window.app = app = {
      console: ConsoleView.get(),
      signin: SignInView.get(),
      router: Router.get()
    };
    Backbone.history.start();
    document.body.style.opacity = 1;
    document.title = document.title.replace(/^.*?(?=MARXO)/i, '');
  });

}).call(this);
