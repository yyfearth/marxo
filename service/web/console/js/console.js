(function(){"use strict";var t={}.hasOwnProperty,n=function(n,e){function i(){this.constructor=n}for(var r in e)t.call(e,r)&&(n[r]=e[r]);return i.prototype=e.prototype,n.prototype=new i,n.__super__=e.prototype,n};define("console",["lib/common"],function(t){var e,i,r,o,s,a,u,c,l,p,h,f,_,y,d,m,v,g,w,b,z,k,E,L,V,S,T,F,P,x,O,I,B;return v=function(t,n){return null==n&&(n=document),n.querySelector(t)},g=function(t,n){return null==n&&(n=document),[].slice.call(n.querySelectorAll(t))},m=function(t){function e(){return w=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.initialize=function(t){this.el.view=this,(null!=t?t.parent:void 0)&&(this.parent=t.parent,this.parentEl=this.parent.el)},e}(Backbone.View),e=function(t){function e(){return b=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.el="#main",e.get=function(){return this.instance==null&&(this.instance=new this),this.instance},e.prototype.initialize=function(){var t=this;this.frames={},g(".frame",this.el).forEach(function(n){var e;e=v('#navbar a[href="#'+n.id+'"]'),t.frames[n.id]={id:n.id,el:n,parent:t,navEl:null!=e?e.parentElement:void 0}}),["home","content","report","profile"].forEach(function(n){return t.frames[n]=new o(t.frames[n])}),this.fixStyles()},e.prototype.fixStyles=function(){var t,n;n=v("#navbar",this.el),t=v("#frames",this.el),(window.onresize=function(){var e;e=n.clientHeight||41,t.style.top=e+"px"})()},e.prototype.showFrame=function(t,n){var e,i,r=this;t=this.frames[t],null!=t&&(console.log("frame",t),t instanceof o?typeof t.open=="function"&&t.open(n):require([t.id],function(e){t=r.frames[t.id]=new e(t),t.render(),typeof t.open=="function"&&t.open(n)}),t.el.classList.contains("active")||((e=v("#main .frame.active"))!=null&&e.classList.remove("active"),(i=v("#navbar li.active"))!=null&&i.classList.remove("active"),t.el.classList.add("active"),t.navEl.classList.add("active"),$(window).resize()))},e.prototype.signout=function(){delete sessionStorage.user,f.get().show(),this.hide(),this.trigger("signout")},e.prototype.show=function(){this.el.style.visibility="visible",this.el.classList.add("active"),this.el.style.opacity=1},e.prototype.hide=function(){var t=this;this.el.classList.remove("active"),setTimeout(function(){t.el.style.visibility="hidden"},f.prototype.delay)},e}(m),s=function(t){function e(){return S=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.initialize=function(t){e.__super__.initialize.call(this,t)},e}(m),o=function(t){function e(){return T=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.initialize=function(t){var n;e.__super__.initialize.call(this,t),this.navEl=t.navEl||((n=v('#navbar a[href="#'+this.id+'"]'))!=null?n.parentElement:void 0)},e.prototype.switchTo=function(t){var n,e;"string"==typeof t&&(t=this[t]),t&&t instanceof s?(t.el.classList.contains("active")||(console.log("switch inner-frame",(n=t.el)!=null?n.id:void 0),(e=v(".inner-frame.active[name]",this.el))!=null&&e.classList.remove("active"),t.el.classList.add("active")),t.rendered||(t.render(),t.rendered=!0)):console.warn("inner frame cannot find",frameName)},e}(m),a=function(t){function e(){return F=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.initialize=function(t){var n=this;e.__super__.initialize.call(this,t),this.$el.modal({show:!1,backdrop:"static"}),this.$el.on("hidden",this.callback.bind(this)),g("button[data-action]",this.el).forEach(function(t){var e;return e=n[t.dataset.action],"function"==typeof e?t.onclick=e.bind(n):console.warn("unknow action",t.dataset.action,t)})},e.prototype.popup=function(t,n){return this.data=t,this._callback=n,this.show(!0)},e.prototype.callback=function(t){null==t&&(t="cancel"),this._callback!=null&&(typeof this._callback=="function"&&this._callback(t,this.data),this.reset())},e.prototype.reset=function(){return this.data=null,this._callback=null,this},e.prototype.cencel=function(){return this.hide(!0)},e.prototype.show=function(t){return this.shown=null!=t?t:!0,this.$el.modal(this.shown?"show":"hide"),this},e.prototype.hide=function(t){return null==t&&(t=!0),this.show(!t)},e}(m),f=function(t){function i(){return P=i.__super__.constructor.apply(this,arguments)}return n(i,t),i.prototype.el="#signin",i.get=function(){return this.instance==null&&(this.instance=new this),this.instance},i.prototype.events={"submit form":"submit"},i.prototype.initialize=function(t){i.__super__.initialize.call(this,t),sessionStorage.user?this.signedIn():this.show()},i.prototype.submit=function(){return console.log("sign in"),this.signedIn(),!1},i.prototype.signedIn=function(){var t;t={id:"test",name:"test"},sessionStorage.user=JSON.stringify(t),this.trigger("success",t),this.hide(),e.get().show(),/signin/i.test(location.hash)&&(location.hash="")},i.prototype.delay=500,i.prototype.show=function(){var t=this;this.el.style.opacity=0,this.el.style.display="block",setTimeout(function(){t.el.classList.add("active"),t.el.style.opacity=1},1)},i.prototype.hide=function(){var t=this;this.el.classList.remove("active"),this.el.style.opacity=0,setTimeout(function(){t.el.style.display="none"},this.delay)},i}(m),i=function(t){function e(){return x=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.set=function(t){return e.__super__.set.call(this,t)},e.prototype.validate=function(t){return t.name&&t.id?/\w{,10}/.test(t.name)?void 0:"name max len is 10 and must be consist of alphabetic char or _":"id and name are required"},e}(Backbone.Model),y=function(t){function e(){return O=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.model=_,e.prototype.url="/",e}(Backbone.Collection),_=function(t){function e(){return I=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.url=function(){return ROOT+"/"+this.name+"/profile"},e}(i),d=function(t){function e(){return B=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(i),c=function(t){function e(){return z=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.model=u,e.prototype.url="/users",e}(Backbone.Collection),p=function(t){function e(){return k=e.__super__.constructor.apply(this,arguments)}return n(e,t),e.prototype.model=l,e.prototype.url=function(){return this.tenant.url()+"/users"},e}(Backbone.Collection),u=function(t){function e(){return E=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(d),l=function(t){function e(){return L=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(d),r=function(t){function e(){return V=e.__super__.constructor.apply(this,arguments)}return n(e,t),e}(d),h=function(t){function i(t){var n=this;i.__super__.constructor.call(this,t),this.route("","home",function(){return n.navigate("home",{replace:!0}),n.show("home")}),this.frames.forEach(function(t){n.route(t+"(/:name)",t,function(e){return n.show(t,e)})}),this.route("signin","signin",function(){}),this.route("signout","signout")}return n(i,t),i.get=function(){return this.instance==null&&(this.instance=new this),this.instance},i.prototype.frames=["home","project","workflow","calendar","content","report","config","profile"],i.prototype.show=function(t,n){var i,r;return sessionStorage.user?(console.log("route",t,n||""),(r=e.get())!=null&&r.showFrame(t,n),i=this[t],null!=i&&i.call(this,n),void 0):(this.navigate("signin",{replace:!0}),void 0)},i.prototype.signout=function(){console.log("sign out"),e.get().signout(),this.navigate("signin",{replace:!0})},i}(Backbone.Router),{async:t,find:v,findAll:g,View:m,ConsoleView:e,FrameView:o,InnerFrameView:s,ModalDialogView:a,SignInView:f,Entity:i,Tenants:y,Tenant:_,User:d,Participants:c,Publichers:p,Participant:u,Publicher:l,Evalutator:r,Router:h}})}).call(this);
