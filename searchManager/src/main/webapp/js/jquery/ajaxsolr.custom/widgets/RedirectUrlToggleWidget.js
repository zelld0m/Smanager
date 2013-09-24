(function($) {
    AjaxSolr.RedirectUrlToggleWidget = AjaxSolr.AbstractWidget.extend({
        init: function() {
            var self = this;

            $("#enableRedirectToPage").change(function(e) {
                var isEnableRedirectToPage = $("#enableRedirectToPage").is(":checked");

                if (isEnableRedirectToPage) {
                    var keyword = $.trim($('input#keyword').val());

                    if (keyword.toLowerCase() !==
                            $.trim(WIDGET_TEXTDEFAULT_searchKeyword).toLowerCase()) {
                        self.makeRequest(keyword);
                    }
                }
            });
        },
        beforeRequest: function() {
            var self = this;

            var isEnableRedirectToPage = $("#enableRedirectToPage").is(":checked");

            if (isEnableRedirectToPage) {
                self.manager.store.addByValue('enableRedirectToPage', 'true');
            }
        },
        makeRequest: function(keyword) {
            var self = this;

            if (validateGeneric("Keyword", keyword, self.minCharRequired)) {
                var isKeepChecked = $('input[name="keepRefinement"]').is(':checked');

                if (!isKeepChecked) {
                    self.manager.store.remove('fq');
                    self.manager.store.remove('disableElevate');
                    self.manager.store.remove('disableExclude');
                    self.manager.store.remove('disableDemote');
                    self.manager.store.remove('disableFacetSort');
                    self.manager.store.remove('disableRedirect');
                    self.manager.store.remove('disableRelevancy');
                    self.manager.store.remove('disableDidYouMean');
                    self.manager.store.remove('disableBanner');
                    self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"] = "";
                }

                self.searchKeyword = keyword;
                self.manager.store.addByValue('q', $.trim(keyword));
                self.manager.doRequest(0);
            } else {
                $("#enableRedirectToPage").prop("checked", false);
            }
        }
    });
})(jQuery);