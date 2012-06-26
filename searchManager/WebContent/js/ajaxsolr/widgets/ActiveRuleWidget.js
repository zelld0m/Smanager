(function ($) {
	
	AjaxSolr.ActiveRuleWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest:function(){
			var self = this;
			
			$(self.target).find('ul#itemListing > li.items:not(#itemPattern)').each(function(idx, el){
				$(el).find('label.select > input[type="checkbox"]').prop("disabled", true);
				$(el).find('.preloader').show();
			});
		},
		
		afterRequest: function () {
			var self = this;
			$(self.target).empty();
			var keyword = self.manager.store.values('q');
			
			if($.isNotBlank(keyword)){
				$(self.target).html(AjaxSolr.theme('activeRule'));
				
				var rules = self.manager.response.responseHeader["search_rules"];
				var $ul = $(self.target).find("ul#itemListing");
				$ul.find("li.items:not(#itemPattern)").remove();
				
				for(var i=0; i<rules.length; i++){
					var rule = rules[i]["rule"];
					$li = $ul.find("li#itemPattern").clone().prop("id", $.formatAsId(rule["id"]));
					
					$li.removeClass("fgray");
					if(rule["active"]!=="true") {
						$li.addClass("fgray"); 
					}
					
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
								   self.manager.store.remove(cid);
							   }else{
								   self.manager.store.addByValue(cid, AjaxSolr.Parameter.escapeValue(cval));
							   }
							   
							   self.manager.doRequest();
						   }
					   });
					
					$li.find("label.ruleType").html(rule["type"]);
					$li.find("label.name").html(rule["name"]);
					
					$li.find("label.imageIcon > img").preview({
						ruleType: rule["type"],
						ruleId: rule["id"]
					});
					$li.show();
					$ul.append($li);
				}
				
				if ($.isNotBlank(self.manager.response.responseHeader["Redirect"])){
					var $li = $ul.find("li#itemPattern").clone().prop("id", "rrNote");
					$li.find("label.ruleType").removeClass("fbold")
											  .removeClass("w310")
											  .html('<div class="alert">Search results using <span class="fbold">' + self.manager.response.responseHeader["Redirect"] + "</span>");
					$li.find("label.select,label.imageIcon,label.name").remove();
					$li.show();
					$ul.append($li);
				}
				
				$ul.find("li").removeClass("alt");
				$ul.find("li:even").addClass("alt");
				
			}
		}
	});

})(jQuery);