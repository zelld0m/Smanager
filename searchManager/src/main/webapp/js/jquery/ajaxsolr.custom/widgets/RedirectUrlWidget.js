(function($) {
    AjaxSolr.RedirectUrlWidget = AjaxSolr.AbstractWidget.extend({
        checkRelativeRedirectUrl: function(urlVal) {
            for (var i = 0; i < GLOBAL_storeRedirectRelativePath.length; i++) {
                var prefix = GLOBAL_storeRedirectRelativePath[i];
                if ($.startsWith(urlVal, prefix)) {
                    return true;
                }
            }

            return false;
        },
        afterRequest: function() {
            var self = this;
            var direct_hit = self.manager.response.responseHeader.direct_hit;

            if (direct_hit !== undefined) {
                var redirectUrlDivIFrame = $("#redirectUrlDivIFrame");
                var redirectUrlParam = direct_hit["redirect_url"];
                var urlVal = redirectUrlParam;

                if ($.isBlank(urlVal)) {
                    jAlert("No redirect URL was specified", "Redirect URL");
                    return;
                }

                if ($.isNotBlank(urlVal) && self.checkRelativeRedirectUrl(urlVal)) {
                    urlVal = GLOBAL_storeDefaultBannerLinkPathProtocol + "://"
                            + GLOBAL_storeParameters.storeDomains[0] + urlVal;
                }

                redirectUrlDivIFrame.attr("src", urlVal);

                $("#redirectUrlTextDiv").html(urlVal);

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