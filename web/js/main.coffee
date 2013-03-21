"use strict"
require ['console'], ({ConsoleView, SignInView, Router}) ->
  # EP
  window.app = app =
    console: ConsoleView.get()
    signin: SignInView.get()
    router: Router.get()

  Backbone.history.start()

  return
