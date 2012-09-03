(function ($) {
	AjaxSolr.SearchWithinWidget = AjaxSolr.AbstractFacetWidget.extend({
		beforeRequest: function () {
			var self = this;
			$(self.target).find('input[type="text"]').prop("disabled",true);
		},
		
		afterRequest: function () {
			var self = this;
			$(self.target).empty();

			if (self.manager.response.response.docs.length > 0 && $.isNotBlank(self.manager.store.values('q'))){
				$(self.target).html(AjaxSolr.theme('searchWithin'));
				$(self.target).find('input').focus();
				$(self.searchWithinInput).val($.cookie('searchWithin'));

				$(self.searchWithinInput).bind('keydown', function(e) {
					var code = (e.keyCode ? e.keyCode : e.which);
					if (code == 13) {
						self.reloadSearch();
					}
				}); 

				$(self.target).find('#searchbutton').click(function() {
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
			if ($.trim(value)){
				if(!isXSSSafe($.trim(value))){
					alert("Invalid keyword. HTML/XSS is not allowed.");
					return false;
				}
				else
					self.manager.store.addByValue('fq', $.trim(value));
			}
			self.manager.doRequest(0);
		}

	});
})(jQuery);