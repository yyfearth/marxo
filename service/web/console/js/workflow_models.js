(function(){"use strict";var t={}.hasOwnProperty,e=function(e,n){function o(){this.constructor=e}for(var i in n)t.call(n,i)&&(e[i]=n[i]);return o.prototype=n.prototype,e.prototype=new o,e.__super__=n.prototype,e};define("workflow_models",["console"],function(t){var n,o,i,r,s,l,a,u,c,p,h,d,f,_,y,m,w,g,v,k,b;return n=t.Entity,s=t.Tenant,r="http://marxo.cloudfoundry.com/api",d=function(t){function n(){return f=n.__super__.constructor.apply(this,arguments)}return e(n,t),n}(n),p=function(t){function n(){return _=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.urlRoot=r+"/workflows",n.prototype.initialize=function(){this.nodes=new c({url:this.url()+"/nodes"},this.get("nodes")),this.links=new a({url:this.url()+"/links"},this.get("links"))},n}(d),h=function(t){function n(){return y=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.model=p,n.prototype.url=r+"/workflows",n}(Backbone.Collection),i=function(t){function n(){return m=n.__super__.constructor.apply(this,arguments)}return e(n,t),n}(n),u=function(t){function n(){return w=n.__super__.constructor.apply(this,arguments)}return e(n,t),n}(i),c=function(t){function n(){return g=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.model=u,n.prototype.url=r+"/nodes",n}(Backbone.Collection),o=function(t){function n(){return v=n.__super__.constructor.apply(this,arguments)}return e(n,t),n}(n),l=function(t){function n(){return k=n.__super__.constructor.apply(this,arguments)}return e(n,t),n}(o),a=function(t){function n(){return b=n.__super__.constructor.apply(this,arguments)}return e(n,t),n.prototype.model=l,n.prototype.url=r+"/links",n}(Backbone.Collection),{TenantWorkflows:h,Workflow:d,TenantWorkflow:p,TenantNodes:c,Node:i,TenantNode:u,TenantLinks:a,Link:o,TenantLink:l}})}).call(this);
