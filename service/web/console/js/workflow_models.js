(function(){"use strict";var t={}.hasOwnProperty,n=function(n,e){function o(){this.constructor=n}for(var r in e)t.call(e,r)&&(n[r]=e[r]);return o.prototype=e.prototype,n.prototype=new o,n.__super__=e.prototype,n};define("workflow_models",["console"],function(t){var e,o,r,i,s,l,u,a,c,p,h,f,d,y,_,m,g,v,w,b,k;return e=t.Entity,s=t.Tenant,i="http://marxo.cloudfoundry.com/api",f=function(t){function e(){return d=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(e),p=function(t){function e(){return y=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.urlRoot=i+"/workflows",e.prototype.initialize=function(){this.nodes=new c({url:this.url()+"/nodes"},this.get("nodes")),this.links=new u({url:this.url()+"/links"},this.get("links"))},e}(f),h=function(t){function e(){return _=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.model=p,e.prototype.url=i+"/workflows",e}(Backbone.Collection),r=function(t){function e(){return m=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(e),a=function(t){function e(){return g=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(r),c=function(t){function e(){return v=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.model=a,e.prototype.url=i+"/nodes",e}(Backbone.Collection),o=function(t){function e(){return w=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(e),l=function(t){function e(){return b=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(o),u=function(t){function e(){return k=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.model=l,e.prototype.url=i+"/links",e}(Backbone.Collection),{TenantWorkflows:h,Workflow:f,TenantWorkflow:p,TenantNodes:c,Node:r,TenantNode:a,TenantLinks:u,Link:o,TenantLink:l}})}).call(this);
