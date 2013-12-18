"use strict"

define 'utils', ['lib/html5-dataset'], ->

  ## Utils

  find = (selector, parent) ->
    parent ?= document
    parent.querySelector selector

  findAll = (selector, parent) ->
    parent ?= document
    [].slice.call parent.querySelectorAll selector

  _html = (el) ->
    el.innerHTML.trim().replace(/\s+/g, ' ').replace(/> </g, '>\n<')

  tpl = (selector, returnDom) ->
    tpl_el = find selector
    throw new Error 'cannot load template from ' + selector unless tpl_el
    tpl_el.parentNode.removeChild tpl_el
    if returnDom then tpl_el else _html tpl_el

  tplAll = (selector, multi) ->
    hash = {}
    unless multi # default
      tpl_els = findAll '.tpl[name]', tpl selector, true
    else
      tpl_els = findAll selector
    throw new Error 'unable to find tpl elements or empty in ' + selector unless tpl_els.length
    for tpl_el in tpl_els
      name = tpl_el.getAttribute 'name'
      throw new Error 'to get a tpl dict, tpl element must have a "name" attribute' unless name
      hash[name] = _html tpl_el
    hash

  fill = (html, model) ->
    html.replace /{{\s*\w+\s*}}/g, (name) ->
      name = name.match(/^{{\s*(\w+)\s*}}$/)[1]
      model[name]?.toString() or model.escape?(name) or ''

  # Polyfill
  Date::now ?= -> +new Date
  String::capitalize ?= ->
    @charAt(0).toUpperCase() + @slice(1).toLowerCase()

  # Fallback
  Element::scrollIntoViewIfNeeded ?= Element::scrollIntoView

  # Enable CoffeeScript class for Javascript Mixin
  # https://github.com/yi/coffee-acts-as
  # e.g.: class A ...   class B ...
  #       class C
  #         @acts_as A, B
  Function::acts_as = (argv...) ->
    #console.log "[Function::acts_as]: argv #{argv}"
    for cl in argv
      @::["__is#{cl}"] = true
      for key, value of cl::
        @::[key] = value
    @

  # Calc duration format in '1 days 2 hours'
  DurationConvertor = do ->
    AUTO_SHORT_MAX = 30
    _regex = /(?:(\d+)w(?:eek)?s?)?(?:(\d+)d(?:ay)?s?)?(?:(\d+)h(?:our)?s?)?(?:(\d+)m(?:in(?:use?)?s?)?)?(?:(\d+)s(?:ec(?:ond)?)?s?)?(?:(\d+)ms)?/i
    _delays = [604800000, 86400000, 3600000, 60000, 1000, 1]
    _day_delay = _delays[1]
    _units = [
      # set week to null if only use days
      ['week', 's'],
      ['day', 's'],
      ['hour', 's'],
      ['minus', 'es'],
      ['second', 's'],
      'ms'
    ]
    _stringify = (delay, short) ->
      str = []
      for ms, i in _delays
        s = _units[i]
        continue unless s
        next = delay % ms
        d = (delay - next) / ms
        if i is 0 and d < 5 and (next - next % _day_delay) % 7 isnt 0 # is week
          d = 0
        else
          delay = next
        if d
          unless Array.isArray s # is ms
            str.push if short then "#{d}#{s}" else "#{d} #{s}"
          else if short
            str.push "#{d}#{s[0].charAt 0}"
          else
            s = if d is 1 then s[0] else s[0] + s[1]
            str.push "#{d} #{s}"
      str.join ' '

    parse: (str) ->
      str = str.trim().replace /\s+|\band\b/ig, ''
      unless str
        0
      else if /^\d+$/.test str
        # pure number in ms
        parseInt str
      else
        delay = 0
        match = str.match(_regex).slice(1)
        for n, i in match
          delay += n * _delays[i] if n
        delay
    stringify: (delay, short) ->
      if delay >= 0
        str = _stringify delay, short
        if not short? and str.length > AUTO_SHORT_MAX
          _stringify delay, true
        else
          str
      else
        console.error "delay should be number >= 0 but it is #{delay}", delay if delay
        ''

  { # exports
  find
  findAll
  tpl
  tplAll
  fill
  DurationConvertor
  }
