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
                var keywordFromStore = $.trim(self.manager.store.values('q'));
                
                // add keyword as a store value if it doesn't exist yet or it is not
                // the same as the current store value
                if (keywordFromStore === null || keywordFromStore === undefined || 
                    keywordFromStore.toLowerCase() !== keyword.toLowerCase()) {
                    self.manager.store.addByValue('q', $.trim(keyword));
                }
                
                self.manager.doRequest(0);
            } else {
                $("#enableRedirectToPage").prop("checked", false);
            }
        }
    });
})(jQuery);