(function(){var t={}.hasOwnProperty,e=function(e,n){function o(){this.constructor=e}for(var i in n)t.call(n,i)&&(e[i]=n[i]);return o.prototype=n.prototype,e.prototype=new o,e.__super__=n.prototype,e};define("workflow",["console","workflow_models","lib/jquery-ui","lib/jquery-jsplumb"],function(t,n){var o,i,r,s,l,a,u,c,p,h,f,d,y,m,_,g,v,w;return m=t.async,_=t.find,o=t.FrameView,h=n.TenantWorkflows,f=n.Workflow,p=n.TenantWorkflow,c=n.TenantNodes,r=n.Node,u=n.TenantNode,a=n.TenantLinks,i=n.Link,l=n.TenantLink,d=function(t){function n(){return g=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.initialize=function(t){n.__super__.initialize.call(this,t),this.view=new y({el:_("#workflow_view",this.el)})},n}(o),y=function(t){function n(){return v=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.jsPlumbDefaults={Endpoint:["Dot",{radius:3}],ConnectionsDetachable:!0,ReattachConnections:!0,HoverPaintStyle:{strokeStyle:"#42a62c",lineWidth:2},ConnectionOverlays:[["Arrow",{location:1,id:"arrow"}],["Label",{location:.5,label:"new link",id:"label",cssClass:"aLabel"}]]},n.prototype.initialize=function(){jsPlumb.importDefaults(this.jsPlumbDefaults),this._loadModel(),this._bind()},n.prototype._bind=function(){jsPlumb.bind("jsPlumbConnection",function(t){var e,n,o;e=t.connection,o=e.getParameter("model"),n=e.getOverlay("label"),null==o?n.hide():o.has("title")?n.setLabel(o.get("title")):n.hide()})},n.prototype._loadModel=function(t){var e,n=this;null==t&&(t=this.render),e=this.model=new p({id:"51447afb4728cb2036cf9ca1"}),e.fetch({success:function(){m.parallel([function(t){return e.nodes.fetch({success:function(e){return t(null,e)},error:function(){return t("fetch nodes failed")}})},function(t){return e.links.fetch({success:function(e){return t(null,e)},error:function(){return t("fetch links failed")}})}],function(o){return o?(console.error(o),void 0):(console.log("workflow",e),e.nodes.forEach(function(t){t.workflow=e}),e.links.forEach(function(t){return t.workflow=e,t.prevNode=e.nodes.get(t.get("prevNodeId")),t.nextNode=e.nodes.get(t.get("nextNodeId")),t.prevNode&&t.nextNode?void 0:console.error("link",t.name||t.id,"is broken, prev/next node missing")}),t.call(n,e))})}})},n.prototype.render=function(){var t,e=this;if(console.log("render wf"),this.el.onselectstart=function(){return!1},t=this.model,null==t)throw"workflow not loaded";return t.nodes.forEach(function(t){var n;n=t.view=new s({model:t,parent:e}),n.render(),e.el.appendChild(n.el)}),t.links.forEach(function(t){jsPlumb.connect({source:t.prevNode.view.srcEndpoint,target:t.nextNode.view.el})}),this},n}(Backbone.View),s=function(t){function n(){return w=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.tagName="div",n.prototype.className="node",n.prototype.sourceEndpointStyle={isSource:!0,uniqueEndpoint:!0,anchor:"RightMiddle",paintStyle:{fillStyle:"#225588",radius:7},connector:["Flowchart",{stub:[40,60],gap:10}],connectorStyle:{strokeStyle:"#346789",lineWidth:2},maxConnections:-1},n.prototype.targetEndpointStyle={anchor:["LeftMiddle","BottomCenter"],dropOptions:{hoverClass:"hover"}},n.prototype.initialize=function(t){this.parent=t.parent,this.parentEl=this.parent.el},n.prototype.render=function(){var t;return console.log("render node"),t=this.el.id=this.model.get("name"),this.el.innerHTML=this.model.escape("title"),jsPlumb.draggable(this.$el),this.parentEl.appendChild(this.el),this.srcEndpoint=jsPlumb.addEndpoint(this.el,this.sourceEndpointStyle,{parameters:{model:this.model,view:this}}),jsPlumb.makeTarget(this.$el,this.targetEndpointStyle,{parameters:{node:this.model,view:this}}),this},n}(Backbone.View),d})}).call(this);