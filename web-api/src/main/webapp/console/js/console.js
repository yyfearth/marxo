// Generated by CoffeeScript 1.6.3
(function() {
  "use strict";
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define('console', ['base'], function(_arg) {
    var ConsoleView, FrameView, Router, SignInView, Tenant, User, View, find, findAll, _ref, _ref1;
    find = _arg.find, findAll = _arg.findAll, View = _arg.View, FrameView = _arg.FrameView, Tenant = _arg.Tenant, User = _arg.User;
    ConsoleView = (function(_super) {
      __extends(ConsoleView, _super);

      function ConsoleView() {
        _ref = ConsoleView.__super__.constructor.apply(this, arguments);
        return _ref;
      }

      ConsoleView.prototype.el = '#main';

      ConsoleView.prototype.user = sessionStorage.user;

      ConsoleView.prototype.events = {
        'touchstart #navbar .dropdown > a': function(e) {
          var $el;
          $el = $(e.currentTarget).parent();
          if ($el.hasClass('hover')) {
            setTimeout(function() {
              return $el.removeClass('hover');
            }, 500);
            return true;
          } else {
            e.preventDefault();
            e.stopPropagation();
            this.$dropdowns.not($el).removeClass('hover');
            $el.addClass('hover');
            this.$el.one('touchstart', function(e) {
              if (!$el.has(e.target).length) {
                return $el.removeClass('hover');
              }
            });
            return false;
          }
        },
        'mouseenter #navbar ul.nav > li': function(e) {
          return e.currentTarget.classList.add('hover');
        },
        'mouseleave #navbar ul.nav > li': function(e) {
          return e.currentTarget.classList.remove('hover');
        },
        'click #navbar .dropdown-menu li': function(e) {
          return $(e.currentTarget).parents('.dropdown').removeClass('hover');
        },
        'click #navbar .dropdown > a': function(e) {
          var $el, _t;
          $el = $(e.currentTarget).parent();
          if ($el.hasClass('hover')) {
            _t = setTimeout(function() {
              _t = null;
              return $el.removeClass('hover');
            }, 500);
            $(e.currentTarget).one('mouseleave', function() {
              if (_t) {
                return _t = clearTimeout(_t);
              }
            });
          }
          return true;
        }
      };

      ConsoleView.get = function() {
        if (this.instance == null) {
          this.instance = new this;
        }
        return this.instance;
      };

      ConsoleView.prototype.initialize = function() {
        var tenant, user, _base,
          _this = this;
        user = tenant = null;
        try {
          if (this.user) {
            user = new User(JSON.parse(this.user));
            if (!user.has('email')) {
              user = null;
            }
            tenant = new Tenant(user.get('tenant'));
            if (!tenant.has('name')) {
              tenant = null;
            }
          }
        } catch (_error) {}
        console.log('load from session', user, tenant);
        this.user = user;
        this.tenant = tenant;
        this.avatarEl = find('img#avatar');
        this.usernameEl = find('#username');
        if ((_base = this.avatarEl.dataset).src == null) {
          _base.src = this.avatarEl.src;
        }
        this.avatarEl.onerror = function() {
          return this.src = this.dataset.src;
        };
        this.frames = {};
        findAll('.frame', this.el).forEach(function(frame) {
          return _this.frames[frame.id] = {
            id: frame.id,
            el: frame,
            parent: _this
          };
        });
        this.navContainer = find('#navbar', this.el);
        this.framesContainer = find('#frames', this.el);
        this._fixStyle = this._fixStyle.bind(this);
        $(window).on('resize', this._fixStyle);
        this.$frames = $('#navbar [data-frame]');
        this.$dropdowns = $(this.navContainer).find('.dropdown');
        this.$el.tooltip({
          selector: '[title]'
        });
        return this;
      };

      ConsoleView.prototype._fixStyle = function() {
        var h;
        h = this.navContainer.clientHeight || 41;
        return this.framesContainer.style.top = h + 'px';
      };

      ConsoleView.prototype.remove = function() {
        $(window).off('resize', this._fixStyle);
        return ConsoleView.__super__.remove.apply(this, arguments);
      };

      ConsoleView.prototype.showFrame = function(frame, name, sub) {
        var $frame, $target, oldFrame, view, _ref1,
          _this = this;
        frame = this.frames[frame];
        if (frame == null) {
          return;
        }
        if (!frame.el.classList.contains('active')) {
          oldFrame = find('.frame.active', this.el);
          if (oldFrame) {
            oldFrame.classList.remove('active');
            view = $.data(oldFrame, 'view');
            if (view) {
              setTimeout(function() {
                return view.trigger('deactivate');
              }, 10);
            }
          }
          if ((_ref1 = find('#navbar li.active')) != null) {
            _ref1.classList.remove('active');
          }
          frame.el.classList.add('active');
          $(window).resize();
          console.log('frame', frame);
        }
        if (frame instanceof FrameView) {
          frame.trigger('activate');
          if (typeof frame.open === "function") {
            frame.open(name, sub);
          }
        } else {
          console.log('load module:', frame.id);
          require([frame.id], function(TheFrameView) {
            frame = _this.frames[frame.id] = new TheFrameView(frame);
            frame.render();
            frame.trigger('activate');
            return typeof frame.open === "function" ? frame.open(name, sub) : void 0;
          });
        }
        $frame = this.$frames.filter("[data-frame='" + frame.id + "']").addClass('active');
        $target = $frame.find("[data-inner-frame='" + name + "']").addClass('active');
        this.$frames.not($frame).removeClass('active');
        this.$frames.find(".active[data-inner-frame]").not($target).removeClass('active');
        return this;
      };

      ConsoleView.prototype.signout = function() {
        sessionStorage.clear();
        this.user = this.tenant = User.current = Tenant.current = null;
        $.ajaxSetup({
          headers: {
            Accept: 'application/json',
            Authorization: ''
          }
        });
        this.router.clear();
        this.hide();
        this.trigger('signout');
        location.hash = 'signin';
        location.reload();
        return this;
      };

      ConsoleView.prototype.signin = function(user, tenant, remember) {
        var u;
        this.user = User.current = user;
        this.tenant = Tenant.current = tenant;
        if (remember) {
          u = user.toJSON();
          delete u.password;
          u.tenant = tenant.toJSON();
          sessionStorage.user = JSON.stringify(u);
          console.log('logged in', u);
        } else {
          delete sessionStorage.user;
        }
        $.ajaxSetup({
          headers: {
            Accept: 'application/json',
            Authorization: 'Basic ' + user.get('credential')
          }
        });
        this.avatarEl.src = "https://secure.gravatar.com/avatar/" + (user.get('email_md5')) + "?s=20&d=mm";
        $(this.usernameEl).text(user.fullname());
        return this.show();
      };

      ConsoleView.prototype.show = function() {
        this.el.style.visibility = 'visible';
        this.el.classList.add('active');
        this.el.style.opacity = 1;
        return this;
      };

      ConsoleView.prototype.hide = function() {
        var _this = this;
        this.el.classList.remove('active');
        setTimeout(function() {
          return _this.el.style.visibility = 'hidden';
        }, SignInView.prototype.delay);
        return this;
      };

      return ConsoleView;

    })(View);
    SignInView = (function(_super) {
      __extends(SignInView, _super);

      function SignInView() {
        _ref1 = SignInView.__super__.constructor.apply(this, arguments);
        return _ref1;
      }

      SignInView.prototype.el = '#signin';

      SignInView.get = function() {
        if (this.instance == null) {
          this.instance = new this;
        }
        return this.instance;
      };

      SignInView.prototype.events = {
        'submit form': 'submit'
      };

      SignInView.prototype.delay = 500;

      SignInView.prototype.initialize = function(options) {
        var remember, tenant, user, _ref2;
        SignInView.__super__.initialize.call(this, options);
        this.form = find('form', this.el);
        this.form.remember.checked = remember = localStorage.marxo_sign_in_remember === 'true';
        if (remember) {
          this.form.email.value = localStorage.marxo_sign_in_email;
        }
        _ref2 = ConsoleView.get(), user = _ref2.user, tenant = _ref2.tenant;
        if ((user instanceof User) && (tenant instanceof Tenant)) {
          this.signedIn(user, tenant);
        } else {
          this.show();
        }
        return this;
      };

      SignInView.prototype.submit = function(e) {
        var email, password;
        e.preventDefault();
        email = this.form.email.value.trim();
        password = this.form.password.value.trim();
        if (!email) {
          this.form.email.focus();
          alert('Please fill out the Email!');
        } else if (!/.+@.+\..+/.test(email)) {
          this.form.email.select();
          alert('The Email is invalid!');
        } else if (!password) {
          this.form.password.focus();
          alert('Password is required!');
        } else {
          this._signIn(email, password);
        }
        return false;
      };

      SignInView.prototype._disable = function(val) {
        return $(this.form.elements).prop('disabled', val);
      };

      SignInView.prototype._signIn = function(email, password) {
        var _this = this;
        this._disable(true);
        require(['crypto'], function(_arg1) {
          var auth, hash, hashPassword, md5Email;
          hashPassword = _arg1.hashPassword, md5Email = _arg1.md5Email;
          email = email.toLowerCase();
          hash = hashPassword(email, password);
          auth = 'Basic ' + btoa("" + email + ":" + hash);
          return new User({
            email: email
          }).fetch({
            headers: {
              Authorization: auth
            },
            success: function(user) {
              var tenantId;
              console.log('login with', email, hash);
              if (user.has('password') && hash !== user.get('password')) {
                _this._disable(false);
                alert('(TEST ONLY) Password not correct');
              } else if (user.has('tenant_id') && email === user.get('email')) {
                user.set('email_md5', md5Email(email));
                user.set('credential', btoa("" + email + ":" + hash));
                tenantId = user.get('tenant_id');
                new Tenant({
                  id: tenantId
                }).fetch({
                  headers: {
                    Authorization: auth
                  },
                  success: function(tenant) {
                    if (tenant.id === tenantId) {
                      return _this.signedIn(user, tenant);
                    } else {
                      _this._disable(false);
                      return alert('Failed to get tenant profile of this user');
                    }
                  },
                  error: function() {
                    _this._disable(false);
                    return alert('Failed to get tenant profile');
                  }
                });
              } else {
                _this.form.password.select();
                _this._disable(false);
                alert('User not exist or email and password are not matched.');
              }
            },
            error: function(ignored, response) {
              console.error('sign-in failed', response);
              _this._disable(false);
              alert('Sign in failed.\nUser not exist or email and password are not matched.');
            }
          });
        });
      };

      SignInView.prototype.signedIn = function(user, tenant) {
        var remember;
        this.trigger('success', user, tenant);
        this.hide();
        localStorage.marxo_sign_in_remember = remember = this.form.remember.checked;
        localStorage.marxo_sign_in_email = remember ? this.form.email.value : '';
        ConsoleView.get().signin(user, tenant, remember);
        if (/signin/i.test(location.hash)) {
          this.router.back({
            fallback: 'home'
          });
        }
        return this;
      };

      SignInView.prototype.show = function() {
        var _this = this;
        this.el.style.opacity = 0;
        this.el.style.display = 'block';
        this._disable(false);
        setTimeout(function() {
          _this.el.classList.add('active');
          return _this.el.style.opacity = 1;
        }, 1);
        return this;
      };

      SignInView.prototype.hide = function() {
        var _this = this;
        this.el.classList.remove('active');
        this.el.style.opacity = 0;
        setTimeout(function() {
          _this.form.password.value = '';
          _this._disable(false);
          return _this.el.style.display = 'none';
        }, this.delay);
        return this;
      };

      return SignInView;

    })(View);
    Router = (function(_super) {
      __extends(Router, _super);

      Router.get = function() {
        if (this.instance == null) {
          this.instance = new this;
        }
        return this.instance;
      };

      Router.prototype.routes = {
        'workflow/:id(/link/:link)(/node/:node)(/action/:action)': function(id, link, node, action) {
          return this.show('workflow', id, {
            link: link,
            node: node,
            action: action
          });
        },
        'project/:id(/link/:link)(/node/:node)(/action/:action)': function(id, link, node, action) {
          return this.show('project', id, {
            link: link,
            node: node,
            action: action
          });
        },
        'content/:id(/:action)': function(id, action) {
          return this.show('content', id, action);
        },
        'config/:name(/:sub)': function(name, sub) {
          return this.show('config', name, sub);
        },
        'event/calendar(/:id)': function(id) {
          return this.show('event', 'calendar', id);
        },
        'signout': 'signout'
      };

      function Router(options) {
        var frame, frameMenu, innerFrame, innerMenu, _fn, _frame, _i, _inner, _j, _len, _len1, _ref2, _ref3,
          _this = this;
        Router.__super__.constructor.call(this, options);
        this.route('', 'home', function() {
          _this.navigate('home', {
            replace: true
          });
          return _this.show('home');
        });
        this.route('signin', 'signin', function() {});
        this.frames = {};
        _ref2 = findAll('[data-frame]', find('#navbar'));
        _fn = function(frame) {
          return _this.route(frame + '(/:name)(/)', frame, function(name) {
            return _this.show(frame, name);
          });
        };
        for (_i = 0, _len = _ref2.length; _i < _len; _i++) {
          frameMenu = _ref2[_i];
          frame = frameMenu.dataset.frame;
          _frame = this.frames[frame] = {
            _name: frame
          };
          _ref3 = findAll('[data-inner-frame]', frameMenu);
          for (_j = 0, _len1 = _ref3.length; _j < _len1; _j++) {
            innerMenu = _ref3[_j];
            innerFrame = innerMenu.dataset.innerFrame;
            _inner = _frame[innerFrame] = {
              _name: innerFrame
            };
            if (innerMenu.dataset["default"]) {
              _frame._cur = _inner;
            }
          }
          _fn(frame);
        }
        this.on('route', function() {
          _this._last = _this._cur;
          return _this._cur = Backbone.history.fragment;
        });
      }

      Router.prototype.back = function(opt) {
        var hash;
        if (opt == null) {
          opt = {};
        }
        if (opt.trigger == null) {
          opt.trigger = true;
        }
        if (opt.replace == null) {
          opt.replace = false;
        }
        if (opt.real) {
          hash = location.hash;
          history.go(-1);
          if (typeof opt.fallback === 'string') {
            setTimeout(function() {
              if (location.hash === hash) {
                return this.navigate(opt.fallback, opt);
              }
            }, 100);
          }
        } else if (this._last) {
          this.navigate(this._last, opt);
        } else if (typeof opt.fallback === 'string') {
          this.navigate(opt.fallback, opt);
        } else {
          console.log('failed to go back for no last record');
        }
        return this;
      };

      Router.prototype.clear = function() {
        this._last = this._cur = null;
        return this;
      };

      Router.prototype.show = function(frame, name, sub) {
        var handler, _frame, _ref2, _ref3;
        if (ConsoleView.get().user == null) {
          this.navigate('signin', {
            replace: true
          });
        } else {
          console.log('route', frame, name || '', sub || '');
          _frame = this.frames[frame];
          if ((_frame._cur != null) && !name) {
            name = (_ref2 = _frame._cur) != null ? _ref2._name : void 0;
            if (name) {
              this.navigate("#" + frame + "/" + name, {
                replace: true
              });
            }
          }
          if (name) {
            _frame._cur = _frame[name];
          }
          this.frames._cur = _frame;
          if ((_ref3 = ConsoleView.get()) != null) {
            _ref3.showFrame(frame, name, sub);
          }
          handler = this[frame];
          if (handler != null) {
            handler.call(this, name);
          }
        }
        return this;
      };

      Router.prototype.signout = function() {
        console.log('sign out');
        ConsoleView.get().signout();
        this.navigate('signin', {
          replace: true
        });
        return this;
      };

      return Router;

    })(Backbone.Router);
    View.prototype.router = Router.get();
    return {
      ConsoleView: ConsoleView,
      SignInView: SignInView,
      Router: Router
    };
  });

}).call(this);
