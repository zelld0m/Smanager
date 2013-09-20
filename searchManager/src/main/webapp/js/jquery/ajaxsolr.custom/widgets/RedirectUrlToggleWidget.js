(function($) {
    AjaxSolr.RedirectUrlToggleWidget = AjaxSolr.AbstractWidget.extend({
        beforeRequest: function() {
            var self = this;
            
            var isEnableRedirectToPage = $("#enableRedirectToPage").is(":checked");
            
            if (isEnableRedirectToPage) {
                self.manager.store.addByValue('enableRedirectToPage', 'true');
            }
        }
    });
})(jQuery);