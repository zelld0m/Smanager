(function($){
	AjaxSolr.SearchRuleModeWidget = AjaxSolr.AbstractWidget.extend({
		init: function() {
			var self = this;
			self.toggleSearchRuleMode();
			$('#searchRuleMode > #searchRuleModeIcon').off().on({
				click: function() {
					self.toggleSearchRuleMode();
				}
			});
		},
		beforeRequest: function() {
			var self = this;
			$('#searchRuleMode > #searchRuleModeIcon').off('click');
		},
		afterRequest: function() {
			var self = this;
			$('#searchRuleMode > #searchRuleModeIcon').off().on({
				click: function() {
					var prevKeyword = $.trim(self.manager.store.values('q'));
					var keyword = $.trim($('input#keyword').val());
					self.toggleSearchRuleMode();
					if (keyword.toLowerCase() !== $.trim(WIDGET_TEXTDEFAULT_searchKeyword).toLowerCase()) {
						self.makeRequest(keyword);
					}
				}
			});
		},
		toggleSearchRuleMode: function() {
			var self = this;
			var isEnableProductionMode = $('#searchRuleMode > input#enableProductionMode').prop('checked');
			if (isEnableProductionMode) {
				self.manager.store.addByValue('gui', 'false');
				$('#searchRuleMode > #searchRuleModeIcon').attr("class", "enabled");
				$('#searchRuleMode > input#enableProductionMode').prop('checked', false);
			} else {
				self.manager.store.addByValue('gui', 'true');
				$('#searchRuleMode > #searchRuleModeIcon').attr("class", "disabled");
				$('#searchRuleMode > input#enableProductionMode').prop('checked', true);
			}
		},
		makeRequest: function(keyword) {
			var self = this;
            if (validateGeneric("Keyword", keyword, self.minCharRequired)) {
                var prevKeyword = $.trim(self.manager.store.values('q'));
                if (prevKeyword === null || prevKeyword === undefined ||
                		prevKeyword.toLowerCase() !== keyword.toLowerCase()) {
                	self.manager.store.addByValue('q', $.trim(keyword));
                }
                self.manager.doRequest(0);
            }
		}
	});
})(jQuery);