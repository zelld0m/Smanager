(function($) {
    AjaxSolr.RedirectUrlWidget = AjaxSolr.AbstractWidget.extend({
        afterRequest: function() {
            var self = this;
            var direct_hit = self.manager.response.responseHeader.direct_hit;
            
            if (direct_hit !== undefined) {
                var redirectUrlDivIFrame = $("#redirectUrlDivIFrame");
                var redirectUrlParam = direct_hit["redirect_url"];
                
                redirectUrlDivIFrame.attr("src", redirectUrlParam);
                
                $("#redirectUrlTextDiv").append(redirectUrlParam);
                
                $("#redirectUrlDiv").dialog({
                    title: "Redirected to Page",
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