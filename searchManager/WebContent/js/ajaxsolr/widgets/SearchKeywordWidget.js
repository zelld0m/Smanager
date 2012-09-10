(function ($) {

	AjaxSolr.SearchKeywordWidget = AjaxSolr.AbstractWidget.extend({
		searchKeyword: "",

		init: function () {
			var self = this;
			self.searchKeyword = "";
			$(self.target).html(AjaxSolr.theme('searchKeyword'));
			$(self.target).find('input#keyword').off().on({
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
					var keyword = $.trim($(self.target).find('input#keyword').val());

					if (code == 13 && keyword.toLowerCase() !== $.trim(self.defaultText).toLowerCase()) 
						self.makeRequest(keyword); 
				}
			}).val(self.defaultText);

			$(self.target).find('#searchBtn').off().on({
				click:function(e){
					var keyword = $.trim($(self.target).find('input#keyword').val());
					if (keyword.toLowerCase() !== $.trim(self.defaultText).toLowerCase())
						self.makeRequest(keyword);
				}
			});
		},

		beforeRequest: function(){
			var self = this;
			$(self.target).find('input').prop("disabled", true);
		},

		afterRequest: function () {
			var self = this;
			var keyword = $.trim(self.manager.store.values('q'));

			$(self.target).find('input').prop("disabled", false);

			if ($.isBlank(keyword)) $(self.target).find('div#refinementHolder').hide();

			$(self.target).find('input').val(keyword);
			$(self.target).find('input').focus();
		},

		makeRequest: function(keyword){
			var self = this;
			if(validateGeneric("Keyword", keyword, self.minCharRequired)){
				var isKeepChecked= $('input[name="keepRefinement"]').is(':checked');

				if (!isKeepChecked){
					self.manager.store.remove('fq');
					self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"] = "";
				}

				self.searchKeyword = keyword;
				self.manager.store.addByValue('q', $.trim(keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
				self.manager.doRequest(0);
			}
		}
	});
})(jQuery);