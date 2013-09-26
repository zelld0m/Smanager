(function($) {

    var WIDGET_NAME = "multiSearchWithin",
        WIDGET_LABEL = "Search Within",

        // components
        TEXT_INPUT_ID = "#searchWithin",
        SELECT_INPUT_ID = "#searchWithinType",
        BUTTON_ID = "#searchBtn",

        // data id
        SEARCH_WITHIN_PARAM = GLOBAL_searchWithinParamName,
        SEARCH_KEYWORD = "q",

        // filter types
        FILTER_TYPES = GLOBAL_searchWithinTypes,
        
        TEMPLATE = '<div class="h27">'
                 + '  <div class="floatL">'
                 + '    <div class="marT4" style="display:inline-block">'
                 + '      <span><select id="searchWithinType" class="farial fsize12"></select></span>'
                 + '    </div>'
                 + '    <div class="w155 marT4 marR5" style="display:inline-block">'
                 + '      <span><input type="text" id="searchWithin" name="searchWithin" class="w120 farial fsize12 fgray pad3"></span>'
                 + '      <a href="javascript:void(0)" id="searchBtn"><div class="btnFilter"></div></a>'
                 + '    </div>'
                 + '  </div>'
                 + '</div>';

    AjaxSolr.MultiSearchWithinWidget = AjaxSolr.AbstractWidget.extend({
        params : {},
        type   : "",

        init: function() {
            var self = this;
            $.each(FILTER_TYPES, function() {
                self.params[this.toString()] = [];
            });
        },

        paramsAsString: function() {
            return JSON.stringify(this.params);
        },

        beforeRequest: function() {
            var $container = $(this.target);

            $container.find(TEXT_INPUT_ID).prop("disabled",true);
            $container.find(SELECT_INPUT_ID).prop("disabled", true);
            this.type = $container.find(SELECT_INPUT_ID).val();
        },

        afterRequest: function() {
            var self = this;
            var $container = $(self.target);
            var keyword = self.manager.store.values(SEARCH_KEYWORD);

            $container.empty();

            if ((self.manager.response.response.docs.length > 0 && $.isNotBlank(keyword))
                    || ($.isNotBlank(keyword) && !self.isEmpty())) {
                $container.html(TEMPLATE);

                if (!self.isEmpty()) $('input#keepRefinement').prop("checked", true);

                var $text = $container.find(TEXT_INPUT_ID);
                var $select = $container.find(SELECT_INPUT_ID);
                var $button = $container.find(BUTTON_ID);

                $.each(self.params, function(idx) {
                    $select.append($("<option value=\"" + idx + "\">" + self.getLabel(idx) + "</option>"));
                });

                self.type && $select.val(self.type);

                $text.off().on({
                    focusin: function(e) {
                        $.iequals($.trim($text.val()), $.trim(self.defaultText)) && $text.val("");
                    },
                    focusout: function(e) {
                        $.isBlank($text.val()) && $text.val(self.defaultText);
                    },
                    keydown: function(e) {
                        var code = (e.keyCode ? e.keyCode : e.which);
                        var searchWithin = $.trim($text.val());

                        if (code == 13 && !$.iequals(searchWithin, $.trim(self.defaultText))) {
                            self.makeRequest($select.val(), searchWithin);
                        }
                    }
                }).val(self.defaultText);

                $button.off().on({
                    click:function(e) {
                        var searchWithin = $.trim($text.val());
                        !$.iequals(searchWithin, $.trim(self.defaultText)) && self.makeRequest($select.val(), searchWithin);
                    }
                });
            }
        },

        makeRequest: function(type, text) {
            if(validateGeneric(WIDGET_LABEL, text, this.minCharRequired) && $.inArray(text, this.params[type]) < 0) {
                this.manager.store.remove(SEARCH_WITHIN_PARAM);
                this.params[type].push(text);

                this.manager.store.addByValue(SEARCH_WITHIN_PARAM, this.paramsAsString());
                this.manager.doRequest(0);
            }
        },

        clear: function(type, text) {
            // type was specified
            if (type && this.params[type]) {
                // when text is specified and type contains text, clear text on type
                if (text && $.inArray(text, this.params[type]) >= 0) {
                    this.params[type].splice($.inArray(text, this.params[type]), 1);
                }
                // when text was not specified, clear type
                else if (!text) {
                    this.params[type].splice(0);
                }
                // when text was specified not on type, do nothing
            }
            // when type was not specified, clear all
            else {
                $.each(this.params, function() { this.splice(0); });
            }
        },

        isEmpty: function() {
            for (type in this.params) if (this.params[type].length) return false;
            return true;
        },

        getLabel: function(type) {
            return type && (type.charAt(0).toUpperCase() + type.slice(1)).replace(/([a-z])([A-Z])/g, '$1 $2');
        },

        paramName: SEARCH_WITHIN_PARAM

    });

})(jQuery);