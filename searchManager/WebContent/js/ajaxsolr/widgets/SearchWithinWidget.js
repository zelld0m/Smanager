(function ($) {
	AjaxSolr.SearchWithinWidget = AjaxSolr.AbstractFacetWidget.extend({
		afterRequest: function () {
			var self = this;
			$(this.target).empty();

			if (self.manager.response.response.docs.length > 0 && $.isNotBlank(self.manager.store.values('q'))){
				$(this.target).html(AjaxSolr.theme('searchWithin'));
				$(this.target).find('input').focus();
				$(self.searchWithinInput).val($.cookie('searchWithin'));

				$(self.searchWithinInput).bind('keydown', function(e) {
					var code = (e.keyCode ? e.keyCode : e.which);
					if (code == 13) {
						self.reloadSearch();
					}
				}); 

				$(this.target).find('#searchbutton').click(function() {
					self.reloadSearch();
				});  	
			}
		},

		reloadSearch: function () {
			var self = this;
			var value = $(self.searchWithinInput).val();
			var oldSearchString = $.cookie('searchWithin');
			self.manager.store.removeByValue('fq', oldSearchString);
			$.cookie('searchWithin', value, {expires: 1});
			if (value.trim()){
				self.manager.store.addByValue('fq', $.trim(value));
			}
			self.manager.doRequest(0);
		}

	});
})(jQuery);