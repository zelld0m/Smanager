(function($) {
    AjaxSolr.RedirectUrlWidget = AjaxSolr.AbstractWidget.extend({
        afterRequest: function() {
            var self = this;
            var redirectUrlParam = self.manager.response.responseHeader.params["redirectUrl"];
            
            if (redirectUrlParam !== undefined) {
                var redirectUrlDivIFrame = $("#redirectUrlDivIFrame");
                
                redirectUrlDivIFrame.attr("src", redirectUrlParam);
                
                $("#redirectUrlDiv").dialog({
                    title: "Redirected to " + redirectUrlParam,
                    width: 800,
                    height: 600,
                    modal: true,
                    close: function() {
                        redirectUrlDivIFrame.attr("src", "about:blank");
                    }
                });
            }
        }
    });
})(jQuery);