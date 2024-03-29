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

MODULES = $(shell find js/*.coffee | grep -v -e /main\. -e /base\. -e /models\. -e /manager\. -e /console\. -e /home\. -e /notification\. -e /profile\.)
DIST_MODULES = $(addprefix $(dist)/, $(MODULES:.coffee=.js))

print = @echo "$(CWHT)- $(CYEL)$< $(CWHT)> $(CBLU)$@$(CRST)"

default: clean js css html res

upload_dist:
	scp -r $(dist)/* wilson@masonwan.com:web/marxo

upload:
	rm -rf test/
	mkdir test
	cp favicon.ico test/
	cp robots.txt test/
	mkdir test/font
	cp font/* test/font/
	mkdir test/img
	cp img/* test/img/
	mkdir -p test/js/lib
	cp js/lib/*.js test/js/lib
	rm test/js/lib/*.min.js
	coffee -co test/js js
	mkdir test/css
	lessc --no-ie-compat --clean-css css/console.less test/css/console.css
	lessc --no-ie-compat --clean-css css/site.less test/css/site.css
	jade --DP --out test/ *.jade
	scp -r test/* wilson@masonwan.com:web/marxo

coffeelint:
	coffeelint -f coffeelint.json js/*.coffee

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
	rm -f "$(dist)/favicon.ico" "$(dist)/robots.txt"
clean_html:
	rm -f $(dist)/*.html

lib: $(dist)/js/lib/require.js $(dist)/js/lib/jquery-ui.js $(dist)/js/lib/preview.js
modules: $(DIST_MODULES)
js: lib modules
css: $(dist)/css/console.css $(dist)/css/site.css
html: $(dist)/index.html $(dist)/console.html $(dist)/console-local.html
res: $(dist)/font $(dist)/favicon.ico $(dist)/robots.txt

$(dist)/favicon.ico: favicon.ico
	$(print)
	cp $^ $@

$(dist)/robots.txt: robots.txt
	$(print)
	cp $^ $@

$(dist)/font: font
	$(print)
	cp -r $^/ $@/

js/lib/common.min.js: js/lib/common.js
	$(print)
	uglifyjs $^ -mco $@ --screw-ie8

js/lib/backgrid.min.js: js/lib/backgrid.js
	$(print)
	uglifyjs $^ -mco $@ --screw-ie8

$(dist)/js/lib/require.js: js/lib/require.js js/lib/common.min.js js/lib/backgrid.min.js js/lib/html5-dataset.js $(dist)/js/main.js
	$(print)
	@mkdir -p $(@D)
	cat $^ | uglifyjs - --screw-ie8 -o $@
	rm -f $(dist)/js/main.js

$(dist)/js/lib/%.js: js/lib/%.js
	$(print)
	@mkdir -p $(@D)
	cp $^ $@

$(dist)/js/main.js: js/models.coffee js/base.coffee js/console.coffee js/manager.coffee js/main.coffee js/notification.coffee js/home.coffee js/profile.coffee
	@echo "$(CWHT)- $(CYEL)$^ $(CWHT)> $(CBLU)$@$(CRST)"
	@mkdir -p $(@D)
	coffee -pjc $^ | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/test.js: js/test.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/backbone.localstorage.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/workflow.js: js/workflow.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | cat js/lib/jquery-jsplumb.js - | uglifyjs - -mco $@ --screw-ie8

$(dist)/js/event.js: js/event.coffee
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
	coffee -pc $< | uglifyjs - -mc --screw-ie8 | cat js/lib/d3v3.js js/lib/nvd3.js - > $@

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
	lessc --no-ie-compat --clean-css $^ $@

$(dist)/index.html: index.jade
	$(print)
	@mkdir -p $(@D)
	jade -Dp . < $< | tr '\n' ' ' | sed -e "s/> </></g" > $@

$(dist)/test.html: test.jade
	$(print)
	@mkdir -p $(@D)
	jade -Dp . < $< | tr '\n' ' ' | sed -e "s/> </></g" > $@
