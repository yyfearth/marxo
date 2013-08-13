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
clean_css:
	rm -rf "$(dist)/css"
clean_res:
	rm -rf "$(dist)/font" "$(dist)/img"
clean_html:
	rm -f "$(dist)/index.html"

lib: $(dist)/js/lib
main: $(dist)/js/main.js
modules: $(DIST_MODULES)
js: lib main modules
css: $(dist)/css/console.css
html: $(dist)/index.html
res: $(dist)/img $(dist)/font

$(dist)/img: img
	$(print)
	cp -r $^/ $@/

$(dist)/font: font
	$(print)
	cp -r $^/ $@/

$(dist)/js/lib: js/lib
	$(print)
	@mkdir -p $@/test/
	cp $^/*.js $@/
	cp $^/test/*.js $@/test/

$(dist)/js/main.js: js/models.coffee js/console.coffee js/manager.coffee js/main.coffee
	@echo "$(CWHT)- $(CYEL)$^ $(CWHT)> $(CBLU)$@$(CRST)"
	@mkdir -p $(@D)
	coffee -pjc $^ | uglifyjs - -o $@ -mc --screw-ie8

$(dist)/js/%.js: js/%.coffee
	$(print)
	@mkdir -p $(@D)
	coffee -pc $< | uglifyjs - -o $@ -mc --screw-ie8

$(dist)/css/console.css: css/console.less
	$(print)
	@mkdir -p $(@D)
	lessc --no-ie-compat --yui-compress -O2 "css/console.less" "$(dist)/css/console.css"

$(dist)/index.html: index.jade
	$(print)
	@mkdir -p $(@D)
	jade -D -p . $< -o $(@D)
