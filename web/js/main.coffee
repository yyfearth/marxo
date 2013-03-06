require ['console'], ({ConsoleView, SignInView, Router}) ->
  # EP
  window.app = app =
    console: new ConsoleView
    signin: new SignInView
    router: new Router

  app.signin.on 'success', ->
    @hide()
    app.console.$el.show()
    return

  # fake
  app.signin.trigger 'success'

  app.router.console = app.console

  Backbone.history.start()

  return
