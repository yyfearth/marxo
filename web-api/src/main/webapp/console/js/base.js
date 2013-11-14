// Generated by CoffeeScript 1.6.3
(function() {
  "use strict";
  var __slice = [].slice,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define('base', ['models', 'lib/common', 'lib/html5-dataset'], function(_arg) {
    var BoxView, Collection, FormDialogView, FormView, FormViewMixin, FrameView, InnerFrameView, ModalDialogView, NavListView, Tenant, User, View, fill, find, findAll, tpl, tplAll, _base, _base1, _html, _ref, _ref1, _ref2, _ref3, _ref4, _ref5, _ref6, _ref7;
    Collection = _arg.Collection, Tenant = _arg.Tenant, User = _arg.User;
    find = function(selector, parent) {
      if (parent == null) {
        parent = document;
      }
      return parent.querySelector(selector);
    };
    findAll = function(selector, parent) {
      if (parent == null) {
        parent = document;
      }
      return [].slice.call(parent.querySelectorAll(selector));
    };
    _html = function(el) {
      return el.innerHTML.trim().replace(/\s+/g, ' ').replace(/> </g, '>\n<');
    };
    tpl = function(selector, returnDom) {
      var tpl_el;
      tpl_el = find(selector);
      if (!tpl_el) {
        throw new Error('cannot load template from ' + selector);
      }
      tpl_el.parentNode.removeChild(tpl_el);
      if (returnDom) {
        return tpl_el;
      } else {
        return _html(tpl_el);
      }
    };
    tplAll = function(selector, multi) {
      var hash, name, tpl_el, tpl_els, _i, _len;
      hash = {};
      if (!multi) {
        tpl_els = findAll('.tpl[name]', tpl(selector, true));
      } else {
        tpl_els = findAll(selector);
      }
      if (!tpl_els.length) {
        throw new Error('unable to find tpl elements or empty in ' + selector);
      }
      for (_i = 0, _len = tpl_els.length; _i < _len; _i++) {
        tpl_el = tpl_els[_i];
        name = tpl_el.getAttribute('name');
        if (!name) {
          throw new Error('to get a tpl dict, tpl element must have a "name" attribute');
        }
        hash[name] = _html(tpl_el);
      }
      return hash;
    };
    fill = function(html, model) {
      return html.replace(/{{\s*\w+\s*}}/g, function(name) {
        name = name.match(/^{{\s*(\w+)\s*}}$/)[1];
        return model[name] || (typeof model.escape === "function" ? model.escape(name) : void 0) || '';
      });
    };
    if ((_base = Date.prototype).now == null) {
      _base.now = function() {
        return +(new Date);
      };
    }
    if ((_base1 = String.prototype).capitalize == null) {
      _base1.capitalize = function() {
        return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
      };
    }
    Function.prototype.acts_as = function() {
      var argv, cl, key, value, _i, _len, _ref;
      argv = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      for (_i = 0, _len = argv.length; _i < _len; _i++) {
        cl = argv[_i];
        this.prototype["__is" + cl] = true;
        _ref = cl.prototype;
        for (key in _ref) {
          value = _ref[key];
          this.prototype[key] = value;
        }
      }
      return this;
    };
    View = (function(_super) {
      __extends(View, _super);

      function View() {
        _ref = View.__super__.constructor.apply(this, arguments);
        return _ref;
      }

      View.prototype.initialize = function(options) {
        var _ref1;
        if ((_ref1 = this.el) != null ? _ref1.tagName : void 0) {
          this.el.view = this;
          this.$el.data('view', this);
        }
        if (options != null ? options.parent : void 0) {
          this.parent = options.parent;
          this.parentEl = this.parent.el;
        }
        return this;
      };

      View.prototype.delayedTrigger = function() {
        var args, delay, eventName, timeout_key,
          _this = this;
        eventName = arguments[0], delay = arguments[1], args = 3 <= arguments.length ? __slice.call(arguments, 2) : [];
        if (delay == null) {
          delay = 10;
        }
        timeout_key = "_" + eventName + "_timtout";
        if (this[timeout_key]) {
          clearTimeout(this[timeout_key]);
        }
        this[timeout_key] = setTimeout(function() {
          _this[timeout_key] = null;
          return _this.trigger.apply(_this, [eventName].concat(__slice.call(args)));
        }, delay);
        return this;
      };

      View.prototype.render = function() {
        this.rendered = true;
        this.el.view = this;
        this.$el.data('view', this);
        return this;
      };

      View.prototype.remove = function() {
        this.trigger('remove', this);
        return View.__super__.remove.apply(this, arguments);
      };

      return View;

    })(Backbone.View);
    InnerFrameView = (function(_super) {
      __extends(InnerFrameView, _super);

      function InnerFrameView() {
        _ref1 = InnerFrameView.__super__.constructor.apply(this, arguments);
        return _ref1;
      }

      return InnerFrameView;

    })(View);
    FrameView = (function(_super) {
      __extends(FrameView, _super);

      function FrameView() {
        _ref2 = FrameView.__super__.constructor.apply(this, arguments);
        return _ref2;
      }

      FrameView.prototype.initialize = function(options) {
        var _ref3;
        FrameView.__super__.initialize.call(this, options);
        this.navEl = options.navEl || ((_ref3 = find("#navbar a[href=\"#" + this.id + "\"]")) != null ? _ref3.parentElement : void 0);
        return this;
      };

      FrameView.prototype.switchTo = function(innerframe) {
        var oldFrame, view, _ref3;
        if (typeof innerframe === 'string') {
          innerframe = this[innerframe];
        }
        if (innerframe && innerframe instanceof InnerFrameView) {
          if (!innerframe.rendered) {
            innerframe.render();
            innerframe.rendered = true;
          }
          if (!innerframe.el.classList.contains('active')) {
            console.log('switch inner-frame', (_ref3 = innerframe.el) != null ? _ref3.id : void 0);
            oldFrame = find('.inner-frame.active[name]', this.el);
            if (oldFrame) {
              oldFrame.classList.remove('active');
              view = $.data(oldFrame, 'view');
              if (view != null) {
                view.trigger('deactivate');
              }
            }
            innerframe.el.classList.add('active');
            innerframe.trigger('activate');
          }
        } else {
          console.warn('inner frame cannot find');
        }
        return this;
      };

      return FrameView;

    })(View);
    BoxView = (function(_super) {
      __extends(BoxView, _super);

      function BoxView() {
        _ref3 = BoxView.__super__.constructor.apply(this, arguments);
        return _ref3;
      }

      BoxView.prototype.className = 'box';

      BoxView.prototype.events = {
        'click .btn-close': 'remove',
        'click .btn-minimize': 'minimize'
      };

      BoxView.prototype.initialize = function(options) {
        return BoxView.__super__.initialize.call(this, options);
      };

      BoxView.prototype.render = function() {
        this.btn_min = find('.btn-minimize', this.el);
        this.btn_close = find('.btn-close', this.el);
        this.contentEl = find('.box-content', this.el);
        return this;
      };

      BoxView.prototype.minimize = function() {
        var btn_min, content;
        btn_min = this.btn_min || find('.btn-minimize', this.el);
        content = this.contentEl || find('.box-content', this.el);
        if (btn_min.classList.contains('icon-up-open')) {
          content.classList.add('minimized');
          btn_min.classList.remove('icon-up-open');
          btn_min.classList.add('icon-down-open');
        } else {
          content.classList.remove('minimized');
          btn_min.classList.remove('icon-down-open');
          btn_min.classList.add('icon-up-open');
        }
        return this;
      };

      return BoxView;

    })(View);
    ModalDialogView = (function(_super) {
      __extends(ModalDialogView, _super);

      function ModalDialogView() {
        _ref4 = ModalDialogView.__super__.constructor.apply(this, arguments);
        return _ref4;
      }

      ModalDialogView.prototype.initialize = function(options) {
        var _this = this;
        ModalDialogView.__super__.initialize.call(this, options);
        this.$el.modal({
          show: false,
          backdrop: 'static'
        });
        this.$el.on('hidden', function(e) {
          if (e.target === _this.el && false !== _this.trigger('hidden', _this)) {
            _this.callback();
            if (_this.goBackOnHidden && location.hash.slice(1) !== _this.goBackOnHidden) {
              _this.goBack();
            }
            return _this.reset();
          }
        });
        this.listenTo(this.router, 'route', function() {
          if (_this._hash && _this._hash !== location.hash.slice(0, _this._hash.length)) {
            return _this.cancel();
          }
        });
        return this;
      };

      ModalDialogView.prototype.goBack = function() {
        if (this._hash === location.hash) {
          this.router.back({
            fallback: this.goBackOnHidden
          });
        }
        return this;
      };

      ModalDialogView.prototype.popup = function(data, callback) {
        if (data === this.data) {
          if (typeof callback === "function") {
            callback('ignored');
          }
        } else {
          this.reset();
          this.data = data;
          this._callback = callback;
          this._hash = location.hash;
          this.show(true);
        }
        return this;
      };

      ModalDialogView.prototype.callback = function(action) {
        if (action == null) {
          action = 'cancel';
        }
        if (this._callback == null) {
          return;
        }
        this.trigger(action, this.data, this);
        if (typeof this._callback === "function") {
          this._callback(action, this.data, this);
        }
        this._action = action;
        this._callback = null;
        return this;
      };

      ModalDialogView.prototype.reset = function() {
        this.data = null;
        this._action = null;
        this._callback = null;
        this._hash = null;
        this.trigger('reset', this);
        return this;
      };

      ModalDialogView.prototype.cancel = function() {
        if (this._callback) {
          this.callback();
        }
        return this.hide(true);
      };

      ModalDialogView.prototype.show = function(shown) {
        if (shown == null) {
          shown = true;
        }
        this.$el.modal(shown ? 'show' : 'hide');
        this.shown = shown;
        return this;
      };

      ModalDialogView.prototype.hide = function(hide) {
        if (hide == null) {
          hide = true;
        }
        return this.show(!hide);
      };

      return ModalDialogView;

    })(View);
    FormViewMixin = (function() {
      function FormViewMixin() {}

      FormViewMixin.prototype.initForm = function() {
        var cached, matched, submit_btn, title,
          _this = this;
        this.form = find('form', this.el);
        if (!this.form) {
          throw new Error('FormViewMixin require a form element in ' + (this.el.id || this.el.outerHTML));
        }
        this.form.onsubmit = function(e) {
          var _base2;
          e.preventDefault();
          if (_this.validate(_this.form)) {
            if (typeof (_base2 = _this.form)._callback === "function") {
              _base2._callback(_this.form);
            }
            _this.form._callback = null;
            _this.trigger('submit', _this.form, _this.data);
          }
          return false;
        };
        submit_btn = find('[type="submit"]', this.form);
        if (submit_btn == null) {
          submit_btn = document.createElement('input');
          submit_btn.type = 'submit';
          submit_btn.style.display = 'none';
          this.form.appendChild(submit_btn);
        }
        this._submit_btn = submit_btn;
        if (this.form.key && (title = this.form.name || this.form.title)) {
          matched = false;
          cached = '';
          $(title).on('input', function() {
            cached = title.value.trim().replace(/\W+/g, '_').slice(0, 33).toLowerCase();
            matched || (matched = !_this.form.key.value);
            if (matched) {
              _this.form.key.value = cached;
            }
            return true;
          });
          $(this.form.key).on({
            input: function() {
              return matched = _this.form.key.value === cached;
            },
            change: function() {
              return _this.form.key.value = _this.form.key.value.toLowerCase();
            }
          });
        }
        return this;
      };

      FormViewMixin.prototype.validate = function(form) {
        var input, _i, _j, _len, _len1, _ref5, _ref6;
        _ref5 = $(form).find('[required]:visible:enabled');
        for (_i = 0, _len = _ref5.length; _i < _len; _i++) {
          input = _ref5[_i];
          if (!input.value.trim()) {
            input.focus();
            alert('This field is required!');
            return false;
          }
        }
        try {
          _ref6 = findAll(':invalid', form);
          for (_j = 0, _len1 = _ref6.length; _j < _len1; _j++) {
            input = _ref6[_j];
            input.focus();
            alert('This field is invalid!');
            return false;
          }
        } catch (_error) {}
        return true;
      };

      FormViewMixin.prototype.submit = function(callback) {
        if (typeof callback === 'function') {
          this.form._callback = callback;
        }
        this._submit_btn.click();
        return this;
      };

      FormViewMixin.prototype.fill = function(attributes) {
        var checked, input, name, radio, value, _i, _len, _ref5;
        this._attributes = {};
        if (attributes != null) {
          for (name in attributes) {
            value = attributes[name];
            input = this.form[name];
            if ((input != null ? typeof input.item === "function" ? (_ref5 = input.item(0)) != null ? _ref5.type : void 0 : void 0 : void 0) === 'radio') {
              input = [].slice.call(input);
              for (_i = 0, _len = input.length; _i < _len; _i++) {
                radio = input[_i];
                checked = radio.type === 'radio' && radio.value === value;
                if (radio.checked !== checked) {
                  radio.checked = checked;
                  if (checked) {
                    $(radio).change();
                  }
                }
              }
            } else if ((input != null ? input.name : void 0) === name && (input.value != null)) {
              if (input.type === 'checkbox') {
                input.checked = value;
                $(input).change();
              } else {
                $(input).val(value).change();
              }
            }
            this._attributes[name] = value;
          }
        }
        this.trigger('fill', this._attributes, attributes);
        return this;
      };

      FormViewMixin.prototype.read = function() {
        var attributes, input, name, val, _i, _len, _ref5;
        if (this._attributes == null) {
          return null;
        } else {
          attributes = {};
          _ref5 = this.form.elements;
          for (_i = 0, _len = _ref5.length; _i < _len; _i++) {
            input = _ref5[_i];
            name = input.name;
            if (name && !input.disabled && !$(input).is(':hidden')) {
              switch (input.type) {
                case 'radio':
                  if (input.checked) {
                    attributes[name] = input.value;
                  }
                  break;
                case 'checkbox':
                  attributes[name] = input.checked;
                  break;
                default:
                  if (input.value || (this._attributes[name] != null)) {
                    val = input.value;
                    attributes[name] = typeof val === 'string' ? val.trim() : val;
                  }
              }
            }
          }
          this.trigger('read', attributes, this._attributes);
          return attributes;
        }
      };

      return FormViewMixin;

    })();
    FormView = (function(_super) {
      __extends(FormView, _super);

      function FormView() {
        _ref5 = FormView.__super__.constructor.apply(this, arguments);
        return _ref5;
      }

      FormView.acts_as(FormViewMixin);

      FormView.prototype.initialize = function(options) {
        FormView.__super__.initialize.call(this, options);
        return this.initForm();
      };

      FormView.prototype.reset = function() {
        this.form.reset();
        return this;
      };

      return FormView;

    })(View);
    FormDialogView = (function(_super) {
      __extends(FormDialogView, _super);

      function FormDialogView() {
        _ref6 = FormDialogView.__super__.constructor.apply(this, arguments);
        return _ref6;
      }

      FormDialogView.acts_as(FormViewMixin);

      FormDialogView.prototype.initialize = function(options) {
        var _ref7,
          _this = this;
        FormDialogView.__super__.initialize.call(this, options);
        this.initForm();
        this.btnSave = find('button.btn-save', this.el);
        if ((_ref7 = this.btnSave) != null) {
          _ref7.onclick = function() {
            return _this.submit(_this.save.bind(_this));
          };
        }
        return this;
      };

      FormDialogView.prototype.reset = function() {
        FormDialogView.__super__.reset.apply(this, arguments);
        this.form.reset();
        return this;
      };

      return FormDialogView;

    })(ModalDialogView);
    NavListView = (function(_super) {
      __extends(NavListView, _super);

      function NavListView() {
        _ref7 = NavListView.__super__.constructor.apply(this, arguments);
        return _ref7;
      }

      NavListView.prototype.urlRoot = '';

      NavListView.prototype.headerTitle = '';

      NavListView.prototype.defaultItem = 'all';

      NavListView.prototype.itemClassName = '';

      NavListView.prototype.targetClassName = '';

      NavListView.prototype.emptyItem = 'new';

      NavListView.prototype.allowEmpty = false;

      NavListView.prototype._reload_timeout = 60000;

      NavListView.prototype.initialize = function(options) {
        var _this = this;
        NavListView.__super__.initialize.call(this, options);
        this.collection = options.collection || this.collection;
        if (!(this.collection instanceof Collection)) {
          throw new Error('collection must be given');
        }
        this.urlRoot = options.urlRoot || this.urlRoot;
        this.headerTitle = options.headerTitle || this.headerTitle;
        this.defaultItem = options.defaultItem || this.defaultItem;
        this.itemClassName = options.itemClassName || this.itemClassName;
        this.targetClassName = options.targetClassName || this.targetClassName;
        this.emptyItem = options.emptyItem || this.emptyItem;
        this.allowEmpty = options.allowEmpty || this.allowEmpty;
        this.listenTo(this.collection, 'reset add remove', this.render.bind(this));
        if (this.events == null) {
          this.events = {};
        }
        this.events['click .btn-refresh'] = function() {
          return _this.fetch(true);
        };
        if (options.auto) {
          return this.fetch(false);
        }
      };

      NavListView.prototype.fetch = function(force) {
        var col, ts;
        col = this.collection;
        ts = Date.now();
        if (force || !col._last_load || ts - col._last_load > this._reload_timeout) {
          console.log('fetch for list', this.headerTitle);
          col.fetch({
            reset: true
          });
          col._last_load = Date.now();
          return true;
        } else {
          return false;
        }
      };

      NavListView.prototype.render = function() {
        this._clear();
        this._render();
        return this;
      };

      NavListView.prototype._clear = function() {
        this.el.innerHTML = '';
        this.el.appendChild(this._renderHeader(null));
        if (this.defaultItem) {
          this.el.appendChild(this._renderItem(this.defaultItem));
        }
        if (this.allowEmpty && this.defaultItem !== this.emptyItem) {
          return this.el.appendChild(this._renderItem(this.emptyItem));
        }
      };

      NavListView.prototype._render = function(models) {
        var fragments,
          _this = this;
        if (models == null) {
          models = this.collection;
        }
        if (models.fullCollection) {
          models = models.fullCollection;
        }
        fragments = document.createDocumentFragment();
        models.forEach(function(model) {
          return fragments.appendChild(_this._renderItem(model));
        });
        return this.el.appendChild(fragments);
      };

      NavListView.prototype._renderHeader = function(title) {
        var btn, header;
        if (title == null) {
          title = this.headerTitle;
        }
        header = document.createElement('li');
        header.className = 'nav-header';
        header.textContent = title;
        if (title === this.headerTitle) {
          btn = document.createElement('button');
          btn.type = 'button';
          btn.className = 'btn-refresh icon-refresh';
          header.insertBefore(btn, header.firstChild);
        }
        return header;
      };

      NavListView.prototype._renderItem = function(model) {
        var a, li;
        if (model == null) {
          model = this.defaultItem;
        }
        li = document.createElement('li');
        if (this.itemClassName) {
          li.className = this.itemClassName;
        }
        a = document.createElement('a');
        if (this.targetClassName) {
          a.className = this.targetClassName;
        }
        if (model.id) {
          a.href = "#" + this.urlRoot + ":" + model.id;
          a.textContent = model.get('title') || model.get('name');
          a.dataset.id = model.id;
          $(a).data('model', model);
        } else if (model.href) {
          a.href = model.href;
          a.textContent = model.title;
        } else if (model === 'all') {
          a.href = "#" + this.urlRoot + ":all";
          a.textContent = 'All';
        } else if (model === 'new' || model === 'empty') {
          a.href = "#" + this.urlRoot + ":" + this.emptyItem;
          a.textContent = 'Empty';
        } else {
          console.dir(model);
          throw new Error('unsupported item for list');
        }
        if (model === this.defaultItem) {
          li.className += ' active';
        }
        li.appendChild(a);
        return li;
      };

      return NavListView;

    })(View);
    return {
      find: find,
      findAll: findAll,
      tpl: tpl,
      tplAll: tplAll,
      fill: fill,
      View: View,
      BoxView: BoxView,
      FrameView: FrameView,
      InnerFrameView: InnerFrameView,
      NavListView: NavListView,
      ModalDialogView: ModalDialogView,
      FormDialogView: FormDialogView,
      FormViewMixin: FormViewMixin,
      FormView: FormView,
      Tenant: Tenant,
      User: User
    };
  });

}).call(this);