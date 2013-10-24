// Generated by CoffeeScript 1.6.3
(function() {
  "use strict";
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define('manager', ['base', 'models', 'lib/backgrid'], function(_arg, _arg1) {
    var InnerFrameView, ManagerCollection, ManagerPaginator, ManagerView, MergeableFilter, NavFilterView, NavListView, ProjectFilterView, Projects, SeqCell, find, findAll, tpl, _ref, _ref1, _ref10, _ref11, _ref12, _ref2, _ref3, _ref4, _ref5, _ref6, _ref7, _ref8, _ref9;
    find = _arg.find, findAll = _arg.findAll, tpl = _arg.tpl, InnerFrameView = _arg.InnerFrameView, NavListView = _arg.NavListView;
    ManagerCollection = _arg1.ManagerCollection, Projects = _arg1.Projects;
    SeqCell = (function(_super) {
      __extends(SeqCell, _super);

      function SeqCell() {
        _ref = SeqCell.__super__.constructor.apply(this, arguments);
        return _ref;
      }

      SeqCell.prototype.formatter = null;

      SeqCell.prototype.initialize = function(options) {
        var _this = this;
        if (this.formatter == null) {
          this.formatter = {
            fromRaw: function() {
              var seq;
              seq = _this.model._seq;
              if (seq != null) {
                return seq + 1;
              } else {
                return '';
              }
            },
            toRaw: function() {
              return _this.model.id;
            }
          };
        }
        return SeqCell.__super__.initialize.call(this, options);
      };

      return SeqCell;

    })(Backgrid.StringCell);
    Backgrid.LinkCell = (function(_super) {
      __extends(LinkCell, _super);

      function LinkCell() {
        _ref1 = LinkCell.__super__.constructor.apply(this, arguments);
        return _ref1;
      }

      LinkCell.prototype.initialize = function(options) {
        LinkCell.__super__.initialize.call(this, options);
        this.urlRoot = this.column.get('urlRoot') || this.urlRoot;
        if (this.urlRoot && this.urlRoot.slice(-1) !== '/') {
          return this.urlRoot += '/';
        }
      };

      LinkCell.prototype.render = function() {
        var field, id, placement, title, tooltip, tooltip_field, url;
        this.$el.empty();
        field = this.column.get('name');
        placement = this.column.get('placement');
        title = this.model.get(field);
        id = this.model.id;
        tooltip_field = this.column.get('tooltip') || this.column.get('name') || 'title';
        tooltip = this.model.get(tooltip_field) || this.model.get('name');
        url = !this.urlRoot ? null : '#' + this.urlRoot + id;
        this.$el.addClass(field + '-link-cell').append($('<a>', {
          tabIndex: -1,
          href: url
        }).text(title));
        this.$el.attr({
          title: tooltip,
          'data-placement': placement,
          'data-container': 'body'
        });
        this.delegateEvents();
        return this;
      };

      return LinkCell;

    })(Backgrid.UriCell);
    Backgrid.TooltipCell = (function(_super) {
      __extends(TooltipCell, _super);

      function TooltipCell() {
        _ref2 = TooltipCell.__super__.constructor.apply(this, arguments);
        return _ref2;
      }

      TooltipCell.prototype.className = 'tooltip-cell';

      TooltipCell.prototype.render = function() {
        var key, placement, tooltip;
        TooltipCell.__super__.render.apply(this, arguments);
        placement = this.column.get('placement');
        key = this.column.get('tooltip') || this.column.get('name') || 'title';
        tooltip = this.model.get(key);
        this.$el.attr({
          title: tooltip,
          'data-placement': placement,
          'data-container': 'body'
        });
        return this;
      };

      return TooltipCell;

    })(Backgrid.StringCell);
    Backgrid.ReadonlyDatetimeCell = (function(_super) {
      __extends(ReadonlyDatetimeCell, _super);

      function ReadonlyDatetimeCell() {
        _ref3 = ReadonlyDatetimeCell.__super__.constructor.apply(this, arguments);
        return _ref3;
      }

      ReadonlyDatetimeCell.prototype.className = 'datetime-cell';

      ReadonlyDatetimeCell.prototype.formatter = null;

      ReadonlyDatetimeCell.prototype.initialize = function(options) {
        if (this.formatter == null) {
          this.formatter = {
            fromRaw: function(datetime) {
              if (!datetime) {
                return '';
              } else if (datetime instanceof Date) {
                return datetime.toLocaleString();
              } else if (typeof datetime === 'number' || /^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(?:\.\d{3})?Z$/.test(datetime)) {
                return new Date(datetime).toLocaleString();
              } else {
                console.error('unsupported datetime', datetime);
                return '';
              }
            },
            toRaw: function() {}
          };
        }
        return ReadonlyDatetimeCell.__super__.initialize.call(this, options);
      };

      return ReadonlyDatetimeCell;

    })(Backgrid.StringCell);
    Backgrid.ActionsCell = (function(_super) {
      __extends(ActionsCell, _super);

      function ActionsCell() {
        _ref4 = ActionsCell.__super__.constructor.apply(this, arguments);
        return _ref4;
      }

      ActionsCell.prototype.className = 'action-cell';

      ActionsCell.prototype._tpl = {};

      ActionsCell.prototype.tpl = function(type) {
        var _base;
        if (!type) {
          return '';
        } else {
          return (_base = this._tpl)[type] != null ? (_base = this._tpl)[type] : _base[type] = tpl("#t_" + type + "_action_cell");
        }
      };

      ActionsCell.prototype.render = function() {
        var html;
        html = this.tpl(this.column.get('name') || this.name);
        this.el.innerHTML = html.replace(/\{\{id\}\}/g, this.model.id);
        this.el.dataset.model = this.model.id;
        this.$el.data('model', this.model);
        this.$el.find('.btn[title]').attr({
          'data-container': 'body'
        });
        this.delegateEvents();
        return this;
      };

      ActionsCell.prototype._find = function(name, tag) {
        return find("" + (tag || '') + "[name='" + name + "']", this.el);
      };

      ActionsCell.prototype._hide = function(name, tag) {
        var _btn;
        _btn = typeof name !== 'string' ? name : this._find(name, tag);
        if (_btn != null) {
          _btn.style.display = 'none';
        }
        return _btn;
      };

      return ActionsCell;

    })(Backgrid.Cell);
    Backgrid.ProjectCell = (function(_super) {
      __extends(ProjectCell, _super);

      function ProjectCell() {
        _ref5 = ProjectCell.__super__.constructor.apply(this, arguments);
        return _ref5;
      }

      ProjectCell.prototype.render = function() {
        var id,
          _this = this;
        this.$el.empty();
        id = this.model.get('project_id');
        if (id) {
          Projects.find({
            projectId: id,
            callback: function(_arg2) {
              var name, project;
              project = _arg2.project;
              if (project) {
                name = _.escape(project.get('name'));
              } else {
                console.warn('project not found', id);
                name = '(Unknown Project)';
              }
              _this.$el.addClass('project-link-cell').append($('<a>', {
                tabIndex: -1,
                href: '#project/' + id
              }).attr('title', name).text(name));
              return _this.delegateEvents();
            }
          });
        }
        return this;
      };

      return ProjectCell;

    })(Backgrid.UriCell);
    Backgrid.NodeActionCell = (function(_super) {
      __extends(NodeActionCell, _super);

      function NodeActionCell() {
        _ref6 = NodeActionCell.__super__.constructor.apply(this, arguments);
        return _ref6;
      }

      NodeActionCell.prototype.render = function() {
        var action_id, node_id, project_id, url,
          _this = this;
        this.$el.empty();
        project_id = this.model.get('project_id');
        node_id = this.model.get('node_id');
        action_id = this.model.get('action_id');
        url = "#project/" + project_id + "/node/" + node_id + "/action/" + action_id;
        Projects.find({
          projectId: project_id,
          nodeId: node_id,
          actionId: action_id,
          callback: function(_arg2) {
            var action, action_name, html, node, node_name, tooltip;
            node = _arg2.node, action = _arg2.action;
            if (node && action) {
              node_name = _.escape(node.get('name'));
              action_name = _.escape(action.get('name') || action.get('type'));
              tooltip = "" + node_name + ": " + action_name;
              html = "<span class='node-title'>" + node_name + "</span>: " + action_name;
              _this.$el.addClass('action-link-cell').append($('<a>', {
                tabIndex: -1,
                href: url
              }).attr({
                title: tooltip
              }).html(html));
              return _this.delegateEvents();
            } else {
              return console.warn('failed to get node action for url', url);
            }
          }
        });
        return this;
      };

      return NodeActionCell;

    })(Backgrid.UriCell);
    Backgrid.LabelCell = (function(_super) {
      __extends(LabelCell, _super);

      function LabelCell() {
        _ref7 = LabelCell.__super__.constructor.apply(this, arguments);
        return _ref7;
      }

      LabelCell.prototype.className = 'label-cell';

      LabelCell.prototype.formatter = {
        fromRaw: function(raw) {
          return raw.toLowerCase();
        },
        toRaw: function(formatted) {
          return formatted.toUpperCase();
        }
      };

      LabelCell.prototype.render = function() {
        var cls, formattedValue, labelCls, rawValue, val;
        this.$el.empty();
        rawValue = this.model.get(this.column.get('name'));
        if (rawValue) {
          val = rawValue.toLowerCase();
          labelCls = 'label capitalized ';
          if (this.column.has('cls')) {
            cls = this.column.get('cls');
            if (typeof cls !== 'string') {
              cls = cls[val] || '';
            }
            labelCls += cls;
          } else {
            labelCls += "label-" + val;
          }
          formattedValue = this.formatter.fromRaw(rawValue);
          this.$el.append($('<span>', {
            "class": labelCls
          }).text(formattedValue));
        }
        this.delegateEvents();
        return this;
      };

      return LabelCell;

    })(Backgrid.StringCell);
    ManagerPaginator = (function(_super) {
      __extends(ManagerPaginator, _super);

      function ManagerPaginator() {
        _ref8 = ManagerPaginator.__super__.constructor.apply(this, arguments);
        return _ref8;
      }

      ManagerPaginator.prototype.className = 'pagination';

      ManagerPaginator.prototype.initialize = function(options) {
        ManagerPaginator.__super__.initialize.call(this, options);
        return this.listenTo(this.collection, 'reset', this.render.bind(this));
      };

      ManagerPaginator.prototype.render = function() {
        ManagerPaginator.__super__.render.apply(this, arguments);
        if (this.collection.state.totalPages < 2) {
          this.$el.hide();
        } else {
          this.$el.show();
        }
        return this;
      };

      return ManagerPaginator;

    })(Backgrid.Extension.Paginator);
    MergeableFilter = (function(_super) {
      __extends(MergeableFilter, _super);

      function MergeableFilter() {
        _ref9 = MergeableFilter.__super__.constructor.apply(this, arguments);
        return _ref9;
      }

      MergeableFilter.prototype.events = {
        'click .close': function(e) {
          e.preventDefault();
          this.clear();
          return false;
        },
        'keydown input[type=text]': function(e) {
          if (e.which === 27) {
            e.preventDefault();
            this.clear();
            return false;
          }
        },
        'input input[type=text]': 'search',
        'submit': function(e) {
          e.preventDefault();
          this.search();
          return false;
        }
      };

      MergeableFilter.prototype.initialize = function(options) {
        var _base;
        MergeableFilter.__super__.initialize.call(this, options);
        return this._shared_matchers = (_base = this.collection)._matchers != null ? (_base = this.collection)._matchers : _base._matchers = [];
      };

      MergeableFilter.prototype.getMatcher = function(query) {
        return this.makeMatcher(query).bind(this);
      };

      MergeableFilter.prototype.mergeMatcher = function(query) {
        var i, matcher, _i, _len, _matchers;
        _matchers = this._shared_matchers;
        for (i = _i = 0, _len = _matchers.length; _i < _len; i = ++_i) {
          matcher = _matchers[i];
          if (matcher._filter === this) {
            _matchers.splice(i, 1);
            break;
          }
        }
        if (query != null) {
          matcher = this.getMatcher(query);
          matcher._filter = this;
          _matchers.push(matcher);
        }
        if (_matchers.length > 1) {
          return function(query) {
            var _j, _len1;
            for (_j = 0, _len1 = _matchers.length; _j < _len1; _j++) {
              matcher = _matchers[_j];
              if (false === (typeof matcher === "function" ? matcher(query) : void 0)) {
                return false;
              }
            }
            return true;
          };
        } else {
          return _matchers[0];
        }
      };

      MergeableFilter.prototype.search = function() {
        return this._search(this.$el.find('input[type=text]').val());
      };

      MergeableFilter.prototype._search = function(query) {
        var col, matcher, shadow, _ref10;
        console.log('search', query);
        col = this.collection;
        if ((_ref10 = col.pageableCollection) != null) {
          _ref10.getFirstPage({
            silent: true
          });
        }
        matcher = this.mergeMatcher(query);
        if (matcher) {
          shadow = this._gen_seq(this.shadowCollection.filter(matcher));
          col.reset(shadow, {
            reindex: false
          });
          col._filtered = true;
        } else if (col._filtered) {
          col.reset(this._gen_seq(this.shadowCollection.models), {
            reindex: false
          });
          col._filtered = false;
        }
        this.lastQuery = query;
        return this;
      };

      MergeableFilter.prototype.clear = function() {
        var $el;
        $el = this.$el.find('input[type=text]');
        if ($el.val()) {
          $el.val('');
          this.search(null);
        }
        return this;
      };

      MergeableFilter.prototype._gen_seq = function(col) {
        col.forEach(function(model, i) {
          return model._seq = i;
        });
        return col;
      };

      return MergeableFilter;

    })(Backgrid.Extension.ClientSideFilter);
    NavFilterView = (function(_super) {
      __extends(NavFilterView, _super);

      function NavFilterView() {
        _ref10 = NavFilterView.__super__.constructor.apply(this, arguments);
        return _ref10;
      }

      NavFilterView.prototype.events = {
        'click a[href]': function(e) {
          e.preventDefault();
          this.select(e.target);
          return false;
        }
      };

      NavFilterView.prototype.initialize = function(options) {
        var field, root;
        field = (options != null ? options.field : void 0) || this.field;
        if (typeof field !== 'string') {
          throw new Error('nav filter only accept one options.field');
        }
        this.fields = [field];
        this.keys = field.split('.');
        root = options.urlRoot || this.urlRoot || field;
        this._regex = new RegExp("#" + root + ":(\\w+)");
        this._matchers = {};
        return NavFilterView.__super__.initialize.call(this, options);
      };

      NavFilterView.prototype.getMatcher = function(query) {
        var _base;
        return (_base = this._matchers)[query] != null ? (_base = this._matchers)[query] : _base[query] = this.makeMatcher(query);
      };

      NavFilterView.prototype.makeMatcher = function(query) {
        var keys, regexp;
        console.log('q', query);
        keys = this.keys;
        if (keys.length === 1) {
          if (query === '') {
            return function(model) {
              var value;
              value = model.get(keys[0]);
              return value === '' || (value == null);
            };
          } else {
            regexp = new RegExp(query.trim(), 'i');
            return function(model) {
              return regexp.test(model.get(keys[0]));
            };
          }
        } else {
          if (query === '') {
            return function(model) {
              var key, value;
              value = model.get(keys[0]);
              while (value && (key = keys[++i])) {
                value = value[key];
              }
              return value === '' || (value == null);
            };
          } else {
            regexp = new RegExp(query.trim(), 'i');
            return function(model) {
              var i, key, value;
              i = 0;
              value = model.get(keys[0]);
              while (value && (key = keys[++i])) {
                value = value[key];
              }
              return regexp.test(value);
            };
          }
        }
      };

      NavFilterView.prototype.search = function(query) {
        return this._search(query);
      };

      NavFilterView.prototype.render = function() {
        this.delegateEvents();
        return this;
      };

      NavFilterView.prototype.clear = function() {
        return this.select(null);
      };

      NavFilterView.prototype.select = function(a) {
        var last, matched, query;
        if (a == null) {
          a = find('a[href]', this.el);
        }
        last = find('.active', this.el);
        if (last !== (a != null ? a.parentElement : void 0)) {
          matched = a.href.match(this._regex);
          query = matched != null ? matched[1] : void 0;
          console.log('filter', this.fields[0], query);
          this.search((function() {
            switch (query) {
              case 'all':
                return null;
              case 'empty':
                return '';
              default:
                return query;
            }
          })());
          if (last != null) {
            last.classList.remove('active');
          }
          a.parentElement.classList.add('active');
        }
        return this;
      };

      return NavFilterView;

    })(MergeableFilter);
    ProjectFilterView = (function(_super) {
      __extends(ProjectFilterView, _super);

      function ProjectFilterView() {
        _ref11 = ProjectFilterView.__super__.constructor.apply(this, arguments);
        return _ref11;
      }

      ProjectFilterView.prototype.field = 'project_id';

      ProjectFilterView.prototype.urlRoot = 'project';

      ProjectFilterView.prototype.headerTitle = 'Projects';

      ProjectFilterView.prototype.initialize = function(options) {
        ProjectFilterView.__super__.initialize.call(this, options);
        this.allowEmpty = options.allowEmpty;
        this.headerTitle = options.headerTitle || this.headerTitle;
        return this.list = new NavListView({
          el: this.el,
          auto: false,
          collection: Projects.projects,
          urlRoot: this.urlRoot,
          headerTitle: this.headerTitle,
          defaultItem: 'all',
          emptyItem: 'empty',
          allowEmpty: this.allowEmpty,
          itemClassName: 'project-list-item'
        });
      };

      ProjectFilterView.prototype.render = function() {
        ProjectFilterView.__super__.render.apply(this, arguments);
        if (!this.list.fetch()) {
          this.list.render();
        }
        return this;
      };

      return ProjectFilterView;

    })(NavFilterView);
    ManagerView = (function(_super) {
      __extends(ManagerView, _super);

      function ManagerView() {
        _ref12 = ManagerView.__super__.constructor.apply(this, arguments);
        return _ref12;
      }

      ManagerView.prototype._predefinedColumns = {
        checkbox: {
          name: '',
          cell: 'select-row',
          headerCell: 'select-all'
        },
        id: {
          name: 'id',
          label: '#',
          cell: SeqCell,
          editable: false
        },
        name: {
          name: 'name',
          label: 'Name',
          cell: 'tooltip',
          editable: false
        },
        title: {
          name: 'title',
          label: 'Title',
          cell: 'tooltip',
          editable: false
        },
        desc: {
          name: 'desc',
          label: 'Description',
          cell: 'string',
          editable: false
        },
        project: {
          name: 'project_id',
          label: 'Project',
          cell: 'project',
          editable: false
        },
        node_action: {
          name: 'action',
          label: 'Node: Action',
          cell: 'node-action',
          editable: false
        },
        type: {
          name: 'type',
          label: 'Type',
          cell: 'label',
          cls: 'label-info',
          editable: false
        },
        status: {
          name: 'status',
          label: 'Status',
          cell: 'label',
          cls: 'label-info',
          editable: false
        },
        created_at: {
          name: 'created_at',
          label: 'Date Created',
          cell: 'readonly-datetime',
          editable: false
        },
        updated_at: {
          name: 'updated_at',
          label: 'Date Updated',
          cell: 'readonly-datetime',
          editable: false
        }
      };

      ManagerView.prototype._defaultEvents = {
        'click .action-cell button[name]': '_action_cell',
        'click .action-buttons .btn': '_action_buttons',
        'change .select-row-cell input[type="checkbox"]': '_selection_changed'
      };

      ManagerView.prototype.initialize = function(options) {
        var action, collection, event, _base, _gen_seq, _ref13;
        if (this.events == null) {
          this.events = {};
        }
        _ref13 = this._defaultEvents;
        for (event in _ref13) {
          if (!__hasProp.call(_ref13, event)) continue;
          action = _ref13[event];
          if ((_base = this.events)[event] == null) {
            _base[event] = action;
          }
        }
        ManagerView.__super__.initialize.call(this, options);
        if (options.collection instanceof ManagerCollection) {
          this.collection = options.collection;
        }
        collection = this.collection;
        if (!(collection instanceof ManagerCollection)) {
          throw new Error('collection must be a instance of ManagerCollection');
        }
        _gen_seq = function() {
          return collection.fullCollection.each(function(model, i) {
            return model._seq = i;
          });
        };
        this.listenTo(collection.fullCollection, 'reset add remove', _gen_seq);
        this.listenTo(collection, 'add remove', _gen_seq);
        this.listenTo(collection, 'remove', this._selection_changed.bind(this));
        this.pageSize = collection.state.pageSize || 15;
        this.grid = new Backgrid.Grid({
          columns: this._configColumns(),
          collection: collection
        });
        this.paginator = new ManagerPaginator({
          collection: collection
        });
        this.filter = new MergeableFilter({
          collection: collection.fullCollection,
          fields: ['title'],
          wait: 300
        });
        $('.action-buttons .btn[title]').attr({
          'data-placement': 'bottom',
          'data-container': 'body'
        });
        return this;
      };

      ManagerView.prototype._configColumns = function() {
        var cfg, cfgs, columns, _i, _len, _ref13;
        columns = [];
        _ref13 = this.columns;
        for (_i = 0, _len = _ref13.length; _i < _len; _i++) {
          cfg = _ref13[_i];
          if (typeof cfg === 'string') {
            cfgs = cfg.split(':');
            if (cfgs[1]) {
              cfg = (function() {
                switch (cfgs[0]) {
                  case 'actions':
                    return {
                      name: cfgs[1],
                      label: '',
                      editable: false,
                      sortable: false,
                      cell: 'actions'
                    };
                  case 'title':
                    return {
                      name: 'title',
                      label: 'Title',
                      cell: 'link',
                      urlRoot: cfgs[1],
                      editable: false
                    };
                  case 'name':
                    return {
                      name: 'name',
                      label: 'Name',
                      cell: 'link',
                      urlRoot: cfgs[1],
                      editable: false
                    };
                  default:
                    return null;
                }
              })();
              if (cfg) {
                columns.push(cfg);
              } else {
                console.error('cannot understand predefined column', cfg);
              }
            } else if (this._predefinedColumns[cfg] != null) {
              cfg = this._predefinedColumns[cfg];
              columns.push(cfg);
            } else {
              console.error('cannot found predefined column', cfg);
            }
          } else {
            columns.push(cfg);
          }
        }
        return columns;
      };

      ManagerView.prototype.render = function() {
        ManagerView.__super__.render.apply(this, arguments);
        this.$el.find('table.grid-table').replaceWith(this.grid.render().$el.addClass('grid-table'));
        this.$el.find('.grid-paginator').replaceWith(this.paginator.render().$el.addClass('grid-paginator'));
        this.$el.find('.grid-filter').empty().append(this.filter.render().$el);
        this.reload();
        this.$enable_if_selected = this.$el.find('.enable_if_selected');
        return this;
      };

      ManagerView.prototype.refresh = function() {
        this.grid.body.refresh();
        return this;
      };

      ManagerView.prototype.reload = function() {
        this.collection.fetch({
          reset: true
        });
        this.collection.getPage(1);
        this.filter.clear();
        return this;
      };

      ManagerView.prototype.getSelected = function() {
        return this.grid.getSelectedModels().filter(function(r) {
          return r != null;
        });
      };

      ManagerView.prototype._action_cell = function(e) {
        var action, btn, cell, model;
        btn = e.target;
        action = btn.dataset.action || btn.getAttribute('name');
        cell = btn.parentNode;
        model = this.collection.get(cell.dataset.model);
        console.log('action', action, model);
        $(btn).tooltip('hide');
        if (action && model) {
          this.trigger(action, model);
        }
      };

      ManagerView.prototype._action_buttons = function(e) {
        var action, btn, selected;
        btn = e.target;
        action = btn.dataset.action || btn.getAttribute('name');
        if (!action) {
          return;
        }
        if (btn.classList.contains('enable_if_selected')) {
          selected = this.getSelected();
          console.log('action', action, selected);
          this.trigger(action, selected, this);
        } else {
          console.log('action', action);
          this.trigger(action, this);
          if (typeof this[action] === "function") {
            this[action]();
          }
        }
      };

      ManagerView.prototype._selection_changed = function() {
        var checkAll, checkboxes, checked, indeterminate, selected, _ref13;
        selected = this.getSelected();
        this.$enable_if_selected.prop('disabled', !(selected != null ? selected.length : void 0));
        this.delayedTrigger('selection_changed', 100, selected, this.grid, this);
        checkboxes = findAll('.select-row-cell input[type=checkbox]', this.el);
        checked = (_ref13 = checkboxes[0]) != null ? _ref13.checked : void 0;
        indeterminate = checkboxes.some(function(box) {
          return box.checked !== checked;
        });
        checkAll = find('.select-all-header-cell input[type=checkbox]', this.el);
        checkAll.indeterminate = indeterminate;
        if (!indeterminate) {
          checkAll.checked = checked;
        }
      };

      return ManagerView;

    })(InnerFrameView);
    return {
      ManagerView: ManagerView,
      NavFilterView: NavFilterView,
      ProjectFilterView: ProjectFilterView
    };
  });

}).call(this);