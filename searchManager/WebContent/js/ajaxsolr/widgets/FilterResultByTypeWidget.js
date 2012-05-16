(function ($) {

	AjaxSolr.FilterResultByTypeWidget = AjaxSolr.AbstractWidget.extend({
		afterRequest: function () {
			var self = this;
			$(this.target).empty();

			if (self.manager.response.response.docs.length > 0 && $.isNotBlank(self.manager.store.values('q'))){
				$(this.target).html(AjaxSolr.theme('filterByType', this.headerText));
			}
		}
	});

})(jQuery);