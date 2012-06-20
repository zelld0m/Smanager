(function ($) {
	
	AjaxSolr.ActiveRuleWidget = AjaxSolr.AbstractWidget.extend({
		
		beforeRequest: function(){
			var self = this;
			$(self.target).empty();
		},
		
		afterRequest: function () {
			var self = this;
			var keyword = self.manager.store.values('q');
			
			if ($.isNotBlank(keyword)){
				$(self.target).html(AjaxSolr.theme('activeRule'));
				
				var rules = self.manager.response.responseHeader["search_rules"];
				var $ul = $(self.target).find("ul#itemListing");
				$ul.find("li.items:not(#itemPattern)").remove();
				
				for(var i=0; i<rules.length; i++){
					var rule = rules[i]["rule"];
					$li = $ul.find("li#itemPattern").clone().prop("id", $.formatAsId(rule["id"]));
					
					var checkboxId = "";
					
					switch(rule["type"].toLowerCase()){
						case "ranking rule": checkboxId="disableRelevancy"; break;
						case "query cleaning": checkboxId="disableRedirect"; break;
						case "elevate": checkboxId="disableElevate"; break;
						case "exclude": checkboxId="disableExclude"; break;
					}
					
					$li.find('label.select > input[type="checkbox"]')
					   .prop("checked", rule["active"]!=="false")
					   .prop("id", checkboxId)
					   .prop("value", rule["id"])
					   .off()
					   .on({
						   click:function(evt){
							   var cid = $(this).prop("id"); 
							   var cval = $(this).prop("value"); 
							
							   if($(this).is(":checked")){
								   self.manager.store.removeByValue(cid, AjaxSolr.Parameter.escapeValue(cval));
							   }else{
								   self.manager.store.addByValue(cid, AjaxSolr.Parameter.escapeValue(cval));
							   }
							   
							   self.manager.doRequest();
						   }
					   });
					
					$li.find("label.ruleType").html(rule["type"]);
					$li.find("label.name").html(rule["name"]);
					$li.show();
					$ul.append($li);
				}
			}
		}
	});

})(jQuery);