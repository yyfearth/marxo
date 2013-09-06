#
# Makefile for build marxo web console
#
# Runtime: Node.js 0.8+
# Compilers: coffee, lessc, jade
# Minifier: uglifyjs
#

dist = dist

CRST = \033[0m
CYEL = \033[33m
CBLU = \033[34m
CWHT = \033[37m

MODULES = $(shell find js/*.coffee | grep -v -e /main\. -e /models\. -e /manager\. -e /console\.)
DIST_MODULES = $(addprefix $(dist)/, $(MODULES:.coffee=.js))

print = @echo "$(CWHT)- $(CYEL)$< $(CWHT)> $(CBLU)$@$(CRST)"

default: clean js css html res

.PHONY: js css html

clean: clean_js clean_css clean_res clean_html

clean_js:
	rm -rf "$(dist)/js"
clean_lib:
	rm -rf "$(dist)/js/lib"
clean_css:
	rm -rf "$(dist)/css"
clean_res:
	rm -rf "$(dist)/font"
clean_html:
	rm -f "$(dist)/index.html"

lib: $(dist)/js/lib/require.js $(dist)/js/lib/jquery-ui.js $(dist)/js/lib/preview.js
main: $(dist)/js/main.js
modules: $(DIST_MODULES)
js: lib main modules
css: $(dist)/css/console.css $(dist)/css/preview.css
html: $(dist)/index.html
res: $(dist)/font $(dist)/favicon.ico $(dist)/robots.txt

$(dist)/favicon.ico: favicon.ico
	$(print)
	cp -r $^ $@

$(dist)/robots.txt: robots.txt
	$(print)
	cp -r $^ $@

$(dist)/font: font
	$(print)
	cp -r $^/ $@/

$(dist)/js/lib/require.js: js/lib/require.js js/lib/common.js js/lib/crypto-js.js
	$(print)
	@mkdir -p $(@D)
	cat $^ | uglifyjs - --screw-ie8 -o $@

$(dist)/js/lib/%.js: js/lib/%.js
	$(print)
	@mkdir -p $(@D)
	cp $^ $@

$(dist)/js/main.js: js/models.coffee js/base.coffee js/console.coffee js/manager.coffee js/main.coffee
	@echo "$(CWHT)- $(CYEL)$^ $(CWHT)> $(CBLU)$@$(CRST)"
	@mkdir -p $(@D)
	coffee -pjc $^ | cat js/lib/backgrid.js js/lib/html5-dataset.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/test.js: js/test.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/backbone.localstorage.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/workflow.js: js/workflow.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/jquery-jsplumb.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/calendar.js: js/calendar.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/fullcalendar.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/content.js: js/content.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/bootstrap-wysiwyg.js js/lib/bootstrap-fileupload.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/report.js: js/report.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/chart.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/crypto.js: js/crypto.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/crypto-js.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/%.js: js/%.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | uglifyjs - -mco $@ --screw-ie8

$(dist)/css/%.css: css/%.less
	$(print)
	@mkdir -p $(@D)
	lessc --no-ie-compat --yui-compress -O2 $^ $@

$(dist)/index.html: index.jade
	$(print)
	@mkdir -p $(@D)
	jade -D -p . $< -o $(@D)
