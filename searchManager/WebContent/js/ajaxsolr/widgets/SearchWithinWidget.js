(function ($) {
	AjaxSolr.SearchWithinWidget = AjaxSolr.AbstractFacetWidget.extend({
		searchWithin: "",

		beforeRequest: function () {
			var self = this;
			$(self.target).find('input[type="text"]').prop("disabled",true);
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty();
			var keyword = self.manager.store.values('q');
			
			if ((self.manager.response.response.docs.length > 0 && $.isNotBlank(keyword)) || ($.isNotBlank(keyword) && $.isNotBlank(self.searchWithin))){
				$(self.target).html(AjaxSolr.theme('searchWithin'));

				$(self.target).find('input#searchWithin').off().on({
					focusin: function(e){
						if ($.trim($(e.currentTarget).val()).toLowerCase() === $.trim(self.defaultText).toLowerCase())
							$(e.currentTarget).val("");
					},
					focusout: function(e){
						if ($.isBlank($(e.currentTarget).val())) 
							$(e.currentTarget).val(self.defaultText);
					},
					keydown: function(e){
						var code = (e.keyCode ? e.keyCode : e.which);
						var searchWithin = $.trim($(self.target).find('input#searchWithin').val());
						
						if (code == 13 && searchWithin.toLowerCase() !== $.trim(self.defaultText).toLowerCase()) {
							self.makeRequest(searchWithin);
						}
					}
				}).val($.isNotBlank(self.searchWithin) ? self.searchWithin : self.defaultText);

				$(self.target).find('#searchBtn').off().on({
					click:function(e){
						var searchWithin = $.trim($(self.target).find('input#searchWithin').val());
						if (searchWithin.toLowerCase() !== $.trim(self.defaultText).toLowerCase())
							self.makeRequest(searchWithin);
					}
				});
			}
		},

		makeRequest: function (searchWithin) {
			var self = this;
			if(validateGeneric("Search Within", searchWithin, self.minCharRequired)){
				self.manager.store.removeByValue('fq', self.searchWithin);
				self.searchWithin = searchWithin;
				self.manager.store.addByValue('fq', searchWithin);
				self.manager.doRequest(0);
			}
		}
	});
})(jQuery);