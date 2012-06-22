(function ($) {

	AjaxSolr.RuleSelectorWidget = AjaxSolr.AbstractWidget.extend({
		
		init: function () {
			var self = this;
			
			var $rankingRuleSelect = $(self.target).find('select#rankingRule');
			
			RelevancyServiceJS.getAllRule("", 0, 0, {
				callback:function(data){
					var list = data.list;
					var total = data.totalSize;

					$rankingRuleSelect.find('option').remove();

					$rankingRuleSelect.append($("<option>", { value : "keyword_default", selected: true}).text("[ Auto-Match ]"));
					
					for(var i=0; i<total; i++){
						$rankingRuleSelect.append($("<option>", { value : list[i].relevancyId}).text(list[i].relevancyName));
					}

					var relevancyId = $.trim(self.manager.store.values('relevancyId'));

					if ($.isNotBlank(relevancyId)) {
						$rankingRuleSelect.find('option[value="' + relevancyId + '"]').prop('selected', true);
					}

					$rankingRuleSelect.combobox({
						selected: function(event, ui){
							var key = self.manager.store.values('q');
							var selectedVal = $(this).val();
							self.manager.store.addByValue('relevancyId', selectedVal==="keyword_default"? "":selectedVal);
							if($.isNotBlank(key)) 
								self.manager.doRequest();
						}
					});
				}
			});
		},

		beforeRequest: function(){
			var self = this;
			$(self.target).find('input[type="text"], input[type="checkbox"]').prop("disabled", true);
		}
	});

})(jQuery);