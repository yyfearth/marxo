// Generated by eXtraCoffeeScript 1.6.1
(function() {
  var createLink, data, deleteLink, procData, removeFromArray,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  data = {
    name: 'demo_wf',
    desc: 'Demo Workflow',
    tenantId: "507f81413d070321728fdeff",
    nodes: [
      {
        id: "507f81413d070321728fde10",
        name: 'Post Idea',
        desc: 'Post software project ideas'
      }, {
        id: "507f81413d070321728fde11",
        name: 'Post Cancel',
        desc: 'Post cancel notification'
      }, {
        id: "507f81413d070321728fde12",
        name: 'Post Requrement',
        desc: 'Post project requirement'
      }, {
        id: "507f81413d070321728fde13",
        name: 'Submit Design',
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
      }, {
        id: "507f81413d070321728fde14",
        name: 'Notification',
        desc: 'Notification'
      }, {
        id: "507f81413d070321728fde15",
        name: 'Post Result',
        desc: 'Post & e-mail result everyone'
      }
    ],
    links: [
      {
        id: "507f81413d070321728fde21",
        name: 'Like count >= 300',
        desc: 'Continue to post requirement if like count >= 300',
        from: "507f81413d070321728fde10",
        to: "507f81413d070321728fde12"
      }, {
        id: "507f81413d070321728fde21",
        name: 'Like count &lt; 300',
        desc: 'Cancel if like count &lt; 300',
        from: "507f81413d070321728fde10",
        to: "507f81413d070321728fde11"
      }, {
        id: "507f81413d070321728fde22",
        from: "507f81413d070321728fde12",
        to: "507f81413d070321728fde13"
      }, {
        id: "507f81413d070321728fde22",
        name: 'Pass rate &lt;= 50%',
        desc: 'Notification if pass rate &lt;= 50%',
        from: "507f81413d070321728fde13",
        to: "507f81413d070321728fde14"
      }, {
        id: "507f81413d070321728fde23",
        name: 'Pass rate &gt; 50%',
        desc: 'Post & e-mail to everyone if pass rate &gt; 50%',
        from: "507f81413d070321728fde13",
        to: "507f81413d070321728fde15"
      }
    ]
  };

  createLink = function(sourceId, targetId) {
    var fromNode, link, toNode, uuid;
    uuid = sourceId + '-' + targetId;
    console.log('create link', uuid);
    fromNode = data.nodes.index[sourceId];
    toNode = data.nodes.index[targetId];
    link = {
      uuid: uuid,
      fromNode: fromNode,
      toNode: toNode
    };
    data.links.push(link);
    fromNode.toLinks.push(link);
    toNode.fromLinks.push(link);
    data.links.index[uuid] = link;
    return link;
  };

  deleteLink = function(link) {
    console.log('delete link', link.uuid);
    delete data.links.index[link.id];
    delete data.links.index[link.uuid];
    removeFromArray(data.links, link);
    removeFromArray(link.fromNode.toLinks, link);
    removeFromArray(link.toNode.fromLinks, link);
  };

  removeFromArray = function(array, item) {
    var idx;
    idx = array.indexOf(item);
    if (idx !== -1) {
      return array.splice(idx, 1);
    }
  };

  (procData = function() {
    var endNodes, grid, linkIndex, lonelyNodes, nodeIndex, startNodes, traval;
    nodeIndex = data.nodes.index = {};
    linkIndex = data.links.index = {};
    startNodes = data.nodes.starts = [];
    endNodes = data.nodes.ends = [];
    lonelyNodes = data.nodes.alones = [];
    data.nodes.forEach(function(node) {
      var uuid;
      uuid = node.uuid = node.name.toLowerCase().replace(/\W/g, '_');
      nodeIndex[uuid] = nodeIndex[node.id] = node;
      node.toLinks = [];
      node.fromLinks = [];
    });
    data.links.forEach(function(link) {
      link.fromNode = nodeIndex[link.from];
      link.fromNode.toLinks.push(link);
      link.toNode = nodeIndex[link.to];
      link.toNode.fromLinks.push(link);
      link.uuid = link.fromNode.uuid + '-' + link.toNode.uuid;
      linkIndex[link.id] = linkIndex[link.uuid] = link;
    });
    data.nodes.forEach(function(node) {
      var _ref;
      if ((node.fromLinks.length === (_ref = node.toLinks.length) && _ref === 0)) {
        lonelyNodes.push(node);
      } else if (node.fromLinks.length === 0) {
        startNodes.push(node);
      } else if (node.toLinks.length === 0) {
        endNodes.push(node);
      }
    });
    grid = window.grid = [startNodes.concat(lonelyNodes)];
    grid.spanX = 350;
    grid.spanY = 150;
    grid.vertical = false;
    (traval = function(level) {
      var nextLevel, _ref;
      nextLevel = [];
      if ((_ref = grid[level]) != null) {
        _ref.forEach(function(node, i) {
          var _ref1;
          node.gridX = i;
          node.gridY = level;
          if (grid.vertical) {
            node.x = i * grid.spanX;
            node.y = level * grid.spanY;
          } else {
            node.x = level * grid.spanX;
            node.y = i * grid.spanY;
          }
          if ((_ref1 = node.toLinks) != null) {
            _ref1.forEach(function(link) {
              return nextLevel.push(link.toNode);
            });
          }
        });
      }
      if (nextLevel.length) {
        grid[level + 1] = nextLevel;
        traval(level + 1);
      }
    })(0);
  })();

  define('workflow', ['console', 'workflow_models', 'lib/jquery-ui.custom.min', 'lib/jquery.jsPlumb.min'], function(_arg, _arg1) {
    var Action, FrameView, Link, LinkView, Node, NodeView, SharedActions, SharedLink, SharedLinks, SharedNode, SharedNodes, SharedWorkflow, SharedWorkflows, Tenant, TenantActions, TenantLink, TenantLinks, TenantNode, TenantNodes, TenantWorkflow, TenantWorkflows, Workflow, WorkflowFrameView, WorkflowView, find;
    find = _arg.find, FrameView = _arg.FrameView;
    Tenant = _arg1.Tenant, SharedWorkflows = _arg1.SharedWorkflows, TenantWorkflows = _arg1.TenantWorkflows, Workflow = _arg1.Workflow, SharedWorkflow = _arg1.SharedWorkflow, TenantWorkflow = _arg1.TenantWorkflow, SharedNodes = _arg1.SharedNodes, TenantNodes = _arg1.TenantNodes, Node = _arg1.Node, SharedNode = _arg1.SharedNode, TenantNode = _arg1.TenantNode, SharedLinks = _arg1.SharedLinks, TenantLinks = _arg1.TenantLinks, Link = _arg1.Link, SharedLink = _arg1.SharedLink, TenantLink = _arg1.TenantLink, SharedActions = _arg1.SharedActions, TenantActions = _arg1.TenantActions, Action = _arg1.Action;
    WorkflowFrameView = (function(_super) {

      __extends(WorkflowFrameView, _super);

      function WorkflowFrameView() {
        return WorkflowFrameView.__super__.constructor.apply(this, arguments);
      }

      WorkflowFrameView.prototype.initialize = function(options) {
        WorkflowFrameView.__super__.initialize.call(this, options);
        this.view = new WorkflowView({
          el: find('#workflow_view', this.el)
        });
      };

      WorkflowFrameView.prototype.render = function() {
        this.view.render();
      };

      return WorkflowFrameView;

    })(FrameView);
    WorkflowView = (function(_super) {

      __extends(WorkflowView, _super);

      function WorkflowView() {
        return WorkflowView.__super__.constructor.apply(this, arguments);
      }

      WorkflowView.prototype.jsPlumbDefaults = {
        Endpoint: [
          'Dot', {
            radius: 3
          }
        ],
        ConnectionsDetachable: true,
        ReattachConnections: true,
        HoverPaintStyle: {
          strokeStyle: '#42a62c',
          lineWidth: 2
        },
        ConnectionOverlays: [
          [
            'Arrow', {
              location: 1,
              id: 'arrow'
            }
          ], [
            'Label', {
              location: 0.5,
              label: 'new link',
              id: 'label',
              cssClass: 'aLabel'
            }
          ]
        ]
      };

      WorkflowView.prototype.initialize = function() {
        jsPlumb.importDefaults(this.jsPlumbDefaults);
      };

      WorkflowView.prototype.render = function() {
        this.el.onselectstart = function() {
          return false;
        };
        return this;
      };

      return WorkflowView;

    })(Backbone.View);
    NodeView = (function(_super) {

      __extends(NodeView, _super);

      function NodeView() {
        return NodeView.__super__.constructor.apply(this, arguments);
      }

      NodeView.prototype.tagName = 'div';

      NodeView.prototype.className = 'node';

      NodeView.prototype.sourceEndpointStyle = {
        isSource: true,
        uniqueEndpoint: true,
        anchor: 'RightMiddle',
        paintStyle: {
          fillStyle: '#225588',
          radius: 7
        },
        connector: [
          'Flowchart', {
            stub: [40, 60],
            gap: 10
          }
        ],
        connectorStyle: {
          strokeStyle: '#346789',
          lineWidth: 2
        },
        maxConnections: -1
      };

      NodeView.prototype.targetEndpointStyle = {
        dropOptions: {
          hoverClass: 'hover'
        },
        anchor: ['LeftMiddle', 'BottomCenter']
      };

      NodeView.prototype.render = function() {
        this.el.innerHTML = this.model.escape('name');
        return this;
      };

      return NodeView;

    })(Backbone.View);
    LinkView = (function(_super) {

      __extends(LinkView, _super);

      function LinkView() {
        return LinkView.__super__.constructor.apply(this, arguments);
      }

      return LinkView;

    })(Backbone.View);
    return WorkflowFrameView;
  });

}).call(this);

/*
//@ sourceMappingURL=workflow.map
*/
