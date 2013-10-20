// Generated by CoffeeScript 1.6.3
(function() {
  "use strict";
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define('actions', ['base', 'models', 'lib/jquery-ui'], function(_arg, _arg1) {
    var Action, ActionView, Actions, ActionsMixin, BoxView, find, findAll, tplAll, _ref;
    find = _arg.find, findAll = _arg.findAll, tplAll = _arg.tplAll, BoxView = _arg.BoxView;
    Action = _arg1.Action, Actions = _arg1.Actions;
    ActionsMixin = (function() {
      function ActionsMixin() {}

      ActionsMixin.prototype.initActions = function(options) {
        this.actionsEl = (options != null ? options.actionEl : void 0) || find('.node-actions', this.el);
        this.projectMode = Boolean(options != null ? options.projectMode : void 0);
        $(this.actionsEl).sortable({
          axis: 'y',
          delay: 150,
          distance: 15,
          cancel: '.box-content'
        });
        return this;
      };

      ActionsMixin.prototype.fillActions = function(actions) {
        if (!(actions instanceof Actions)) {
          actions = (typeof actions.actions === "function" ? actions.actions() : void 0) || new Actions(actions.actions || actions);
        }
        console.log('fill actions', actions);
        this.actions = actions;
        this.actions.forEach(this.addAction.bind(this));
        return this;
      };

      ActionsMixin.prototype.readActions = function() {
        return findAll('.action', this.actionsEl).map(function(el) {
          var view;
          view = $(el).data('view');
          if (!view) {
            throw new Error('cannot get action from action.$el');
          }
          return view.read();
        });
      };

      ActionsMixin.prototype.clearActions = function() {
        var _ref;
        if ((_ref = this.actions) != null) {
          _ref.forEach(function(model) {
            var _ref1;
            return (_ref1 = model.view) != null ? _ref1.remove() : void 0;
          });
        }
        this.actions = null;
        $(findAll('.action', this.actionsEl)).remove();
        return this;
      };

      ActionsMixin.prototype.viewAction = function(id) {
        var el, hidden;
        console.log('view action id:', id);
        el = this.actionsEl.querySelector('#action_' + id);
        if (el != null) {
          hidden = this.el.getAttribute('aria-hidden');
          if (hidden === 'true') {
            this.$el.one('shown', function() {
              return el.scrollIntoView();
            });
          } else if (hidden === 'false') {
            el.scrollIntoView();
          } else {
            setTimeout(function() {
              return el.scrollIntoView();
            }, 600);
          }
        }
        return el;
      };

      ActionsMixin.prototype.addAction = function(model, options) {
        var actionView;
        if (!(model instanceof Action)) {
          model = new Action(model);
        }
        actionView = new ActionView({
          model: model,
          parent: this,
          container: this.actionsEl,
          projectMode: this.projectMode
        });
        this.listenTo(actionView, 'remove', this.removeAction.bind(this));
        actionView.render();
        if (options != null ? options.scrollIntoView : void 0) {
          actionView.el.scrollIntoView();
        }
        this.delayedTrigger('actions_update', 100);
        return this;
      };

      ActionsMixin.prototype.removeAction = function(view) {
        console.log('remove action view', view);
        if (typeof view.remove === "function") {
          view.remove();
        }
        this.delayedTrigger('actions_update', 100);
        return this;
      };

      return ActionsMixin;

    })();
    ActionView = (function(_super) {
      __extends(ActionView, _super);

      function ActionView() {
        _ref = ActionView.__super__.constructor.apply(this, arguments);
        return _ref;
      }

      ActionView.prototype.className = 'box action';

      ActionView.prototype._tpl = tplAll('#actions_tpl');

      ActionView.prototype.initialize = function(options) {
        var _base;
        ActionView.__super__.initialize.call(this, options);
        this.projectMode = options.projectMode;
        this.containerEl = options.container;
        this.model = options.model;
        this.model.view = this;
        this.type = (typeof (_base = this.model).get === "function" ? _base.get('type') : void 0) || options.model.type || options.type;
        if (!(this.model && this.type)) {
          throw new Error('need action model and type');
        }
        return this;
      };

      ActionView.prototype.remove = function() {
        var model;
        model = this.model;
        model.type = null;
        model.name = null;
        model.data = null;
        this.remove = function() {
          return this;
        };
        return ActionView.__super__.remove.apply(this, arguments);
      };

      ActionView.prototype.render = function() {
        var _ref1, _tpl;
        _tpl = this._tpl[this.type];
        if (!_tpl) {
          console.error('unable to find tpl for action type', this.type);
          this.remove();
        } else {
          this.el.innerHTML = this._tpl[this.type];
          this.el.id = 'action_' + this.model.id || 'no_id';
          this._name = this.$el.find('.box-header h4').text();
          this.containerEl.insertBefore(this.el, find('.alert', this.containerEl));
          ActionView.__super__.render.apply(this, arguments);
          if (/webkit/i.test(navigator.userAgent)) {
            $(this.el).disableSelection();
          } else {
            $('.box-header, .btn', this.el).disableSelection();
          }
          this.form = find('form', this.el);
          this.form.key.readOnly = this.projectMode;
          if (this.projectMode) {
            $(this.btn_close).remove();
          }
          this.fill((_ref1 = this.model) != null ? _ref1.toJSON() : void 0);
          this.$el.data({
            model: this.model,
            view: this
          });
          this.listenTo(this.model, 'destroy', this.remove.bind(this));
        }
        return this;
      };

      ActionView.prototype.fill = function(data) {
        var el, form, name, value;
        if (!(data && this.form)) {
          return;
        }
        if (!data.name) {
          data.name = this._name;
        }
        if (!data.key) {
          data.key = data.type;
        }
        form = this.form;
        for (name in data) {
          value = data[name];
          el = form[name];
          if ((el != null ? typeof el.getAttribute === "function" ? el.getAttribute('name') : void 0 : void 0) === name) {
            $(el).val(value);
          }
        }
        return this;
      };

      ActionView.prototype.read = function(data) {
        var els;
        if (!this.form) {
          throw new Error('cannot find the form, may not rendered yet');
        }
        if (data == null) {
          data = {};
        }
        els = [].slice.call(this.form.elements);
        els.forEach(function(el) {
          var $el, name;
          $el = $(el);
          name = $el.attr('name');
          if (name) {
            return data[name] = $el.val();
          }
        });
        return data;
      };

      return ActionView;

    })(BoxView);
    return ActionsMixin;
  });

}).call(this);
