// Libs: bootstrap-fileupload + bootstrap-wysiwyg
"use strict";

/**
 * Bootstrap.js by @mdo and @fat, extended by @ArnoldDaniels.
 * plugins: bootstrap-fileupload.js
 * Copyright 2012 Twitter, Inc.
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
!function(e){var t=function(t,n){this.$element=e(t),this.type=this.$element.data("uploadtype")||(this.$element.find(".thumbnail").length>0?"image":"file"),this.$input=this.$element.find(":file");if(this.$input.length===0)return;this.name=this.$input.attr("name")||n.name,this.$hidden=this.$element.find('input[type=hidden][name="'+this.name+'"]'),this.$hidden.length===0&&(this.$hidden=e('<input type="hidden" />'),this.$element.prepend(this.$hidden)),this.$preview=this.$element.find(".fileupload-preview");var r=this.$preview.css("height");this.$preview.css("display")!="inline"&&r!="0px"&&r!="none"&&this.$preview.css("line-height",r),this.original={exists:this.$element.hasClass("fileupload-exists"),preview:this.$preview.html(),hiddenVal:this.$hidden.val()},this.$remove=this.$element.find('[data-dismiss="fileupload"]'),this.$element.find('[data-trigger="fileupload"]').on("click.fileupload",e.proxy(this.trigger,this)),this.listen()};t.prototype={listen:function(){this.$input.on("change.fileupload",e.proxy(this.change,this)),e(this.$input[0].form).on("reset.fileupload",e.proxy(this.reset,this)),this.$remove&&this.$remove.on("click.fileupload",e.proxy(this.clear,this))},change:function(e,t){if(t==="clear")return;var n=e.target.files!==undefined?e.target.files[0]:e.target.value?{name:e.target.value.replace(/^.+\\/,"")}:null;if(!n){this.clear();return}this.$hidden.val(""),this.$hidden.attr("name",""),this.$input.attr("name",this.name);if(this.type==="image"&&this.$preview.length>0&&(typeof n.type!="undefined"?n.type.match("image.*"):n.name.match(/\.(gif|png|jpe?g)$/i))&&typeof FileReader!="undefined"){var r=new FileReader,i=this.$preview,s=this.$element;r.onload=function(e){i.html('<img src="'+e.target.result+'" '+(i.css("max-height")!="none"?'style="max-height: '+i.css("max-height")+';"':"")+" />"),s.addClass("fileupload-exists").removeClass("fileupload-new")},r.readAsDataURL(n)}else this.$preview.text(n.name),this.$element.addClass("fileupload-exists").removeClass("fileupload-new")},clear:function(e){this.$hidden.val(""),this.$hidden.attr("name",this.name),this.$input.attr("name","");if(navigator.userAgent.match(/msie/i)){var t=this.$input.clone(!0);this.$input.after(t),this.$input.remove(),this.$input=t}else this.$input.val("");this.$preview.html(""),this.$element.addClass("fileupload-new").removeClass("fileupload-exists"),e&&(this.$input.trigger("change",["clear"]),e.preventDefault())},reset:function(e){this.clear(),this.$hidden.val(this.original.hiddenVal),this.$preview.html(this.original.preview),this.original.exists?this.$element.addClass("fileupload-exists").removeClass("fileupload-new"):this.$element.addClass("fileupload-new").removeClass("fileupload-exists")},trigger:function(e){this.$input.trigger("click"),e.preventDefault()}},e.fn.fileupload=function(n){return this.each(function(){var r=e(this),i=r.data("fileupload");i||r.data("fileupload",i=new t(this,n)),typeof n=="string"&&i[n]()})},e.fn.fileupload.Constructor=t,e(document).on("click.fileupload.data-api",'[data-provides="fileupload"]',function(t){var n=e(this);if(n.data("fileupload"))return;n.fileupload(n.data());var r=e(t.target).closest('[data-dismiss="fileupload"],[data-trigger="fileupload"]');r.length>0&&(r.trigger("click.fileupload"),t.preventDefault())})}(window.jQuery)

/**
 * http://github.com/mindmup/bootstrap-wysiwyg
 * Modified by yyfearth@gmail.com for hotkeys with jQuery 2.0
 */
!function(e){var t=function(t){var n=e.Deferred(),o=new FileReader;return o.onload=function(e){n.resolve(e.target.result)},o.onerror=n.reject,o.onprogress=n.notify,o.readAsDataURL(t),n.promise()};e.fn.cleanHtml=function(){var t=e(this).html();return t&&t.replace(/(<br>|\s|<div><br><\/div>|&nbsp;)*$/,"")},e.fn.wysiwyg=function(n){var o,a,i,r=this,c=function(){a.activeToolbarClass&&e(a.toolbarSelector).find(i).each(function(){var t=e(this).data(a.commandRole);document.queryCommandState(t)?e(this).addClass(a.activeToolbarClass):e(this).removeClass(a.activeToolbarClass)})},l=function(e,t){var n=e.split(" "),o=n.shift(),a=n.join(" ")+(t||"");document.execCommand(o,0,a),c()},s=function(){r.keydown(function(e){var t=null;if(e.ctrlKey||e.metaKey)switch(e.which){case 66:t="bold";break;case 73:t="italic";break;case 85:t="underline";break;case 90:t=e.shiftKey?"redo":"undo";break;case 98:t="redo"}else 9==e.which&&(t=e.shiftKey?"outdent":"indent");return t?(e.preventDefault(),e.stopPropagation(),l(t),!1):!0})},d=function(){var e=window.getSelection();return e.getRangeAt&&e.rangeCount?e.getRangeAt(0):void 0},u=function(){o=d()},f=function(){var e=window.getSelection();if(o){try{e.removeAllRanges()}catch(t){document.body.createTextRange().select(),document.selection.empty()}e.addRange(o)}},m=function(n){r.focus(),e.each(n,function(n,o){/^image\//.test(o.type)?e.when(t(o)).done(function(e){l("insertimage",e)}).fail(function(e){a.fileUploadError("file-reader",e)}):a.fileUploadError("unsupported-file-type",o.type)})},h=function(e,t){f(),document.queryCommandSupported("hiliteColor")&&document.execCommand("hiliteColor",0,t||"transparent"),u(),e.data(a.selectionMarker,t)},g=function(t,n){t.find(i).click(function(){f(),r.focus(),l(e(this).data(n.commandRole)),u()}),t.find("[data-toggle=dropdown]").click(f),t.find("input[type=text][data-"+n.commandRole+"]").on("webkitspeechchange change",function(){var t=this.value;this.value="",f(),t&&(r.focus(),l(e(this).data(n.commandRole),t)),u()}).on("focus",function(){var t=e(this);t.data(n.selectionMarker)||(h(t,n.selectionColor),t.focus())}).on("blur",function(){var t=e(this);t.data(n.selectionMarker)&&h(t,!1)}),t.find("input[type=file][data-"+n.commandRole+"]").change(function(){f(),"file"===this.type&&this.files&&this.files.length>0&&m(this.files),u(),this.value=""})},p=function(){r.on("dragenter dragover",!1).on("drop",function(e){var t=e.originalEvent.dataTransfer;e.stopPropagation(),e.preventDefault(),t&&t.files&&t.files.length>0&&m(t.files)})};return a=e.extend({},e.fn.wysiwyg.defaults,n),i="a[data-"+a.commandRole+"],button[data-"+a.commandRole+"],input[type=button][data-"+a.commandRole+"]",s(),a.dragAndDropImages&&p(),g(e(a.toolbarSelector),a),r.attr("contenteditable",!0).on("mouseup keyup mouseout",function(){u(),c()}),e(window).bind("touchend",function(e){var t=r.is(e.target)||r.has(e.target).length>0,n=d(),o=n&&n.startContainer===n.endContainer&&n.startOffset===n.endOffset;(!o||t)&&(u(),c())}),this},e.fn.wysiwyg.defaults={toolbarSelector:"[data-role=editor-toolbar]",commandRole:"edit",activeToolbarClass:"btn-info",selectionMarker:"edit-focus-marker",selectionColor:"darkgrey",dragAndDropImages:!0,fileUploadError:function(e,t){console.log("File upload error",e,t)}}}(window.jQuery);

// AMD
define('lib/content');
