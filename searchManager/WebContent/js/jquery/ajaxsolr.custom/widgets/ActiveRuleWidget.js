(function ($) {
	
	AjaxSolr.ActiveRuleWidget = AjaxSolr.AbstractWidget.extend({
		
		getRuleStatus:function($li, rule){
			DeploymentServiceJS.getRuleStatus(rule["type"], rule["id"], {
				callback:function(data){
					if(!$.isEmptyObject(data)){
						$li.find('.ruleStatus > .status').text(getRuleNameSubTextStatus(data));
						$li.find('.ruleStatus > .statusMode').text($.isNotBlank(data["locked"]) && data["locked"]? " [ Read-Only ]" : "");
						$li.find('.lastPublished').text($.isNotBlank(data["lastPublishedDate"])? 'Last Published: ' + data["lastPublishedDate"].toUTCString(): '');
					}
				}
			});
		},
		
		beforeRequest:function(){
			var self = this;
			
			$(self.target).find('ul#itemListing > li.items:not(#itemPattern)').each(function(idx, el){
				$(el).find('.select > input[type="checkbox"]').prop("disabled", true);
				$(el).find('.preloader').show();
			});
		},
		
		getTemplate: function(){
			var output  = '';

			output  +='<div style="display:block;" class="fsize11 marT10 fDGray border">';
			output  +='	<div id="activeRuleNote" class="w655 marL20 info notification border fsize11 marB20 marT10">';
			output  +=' 	Below are rules applied to your current search. You can toggle ON/OFF of each active rules to examine its effect on search results';
			output  +=' </div>';
			output  +='	<ul id="itemListing" class="mar16 marB20 marL20" >';
			output  +='		<li id="itemPattern" class="items borderB padTB5 clearfix" style="display:none; width:690px">';
			output  +='			<div class="floatL"><label class="ruleType fbold w160"></label></div>';
			output	+='			<div class="floatL">';
			output  +='				<div class="w230"><label class="imageIcon w20 floatL posRel topn2"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/icon_reviewContent2.png")  + '" class="top2 posRel"></label>';
			output  +='				<label class="name floatL w210"><span class="fbold"></span></label></div><div class="clearB"></div>';
			output  +='				<div class="w230"><label class="ruleStatus w230 marT6">';
			output  +='					<span class="status fgray"></span>';
			output  +='					<span class="statusMode fsize11 forange padL5"></span>';
			output  +='				</label></div>';
			output	+='			</div>';
			output	+='			<div class="floatR w300">';
			output  +='				<div class="w300">';
			output  +='					<label class="select floatR w20 posRel topn3"><input type="checkbox" class="firerift-style-checkbox on-off ruleControl"></label>';
			output  +='					<label class="w30 preloader floatR" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/ajax-loader-rect.gif")  + '"></label>';
			output  +='				</div>';
			output  +='				<div class="w300 floatR"><label class="lastPublished fgray"></label></div>';
			output	+='			</div>';
			output  +='		</li>';
			output  +='	</ul>';
			output  +='<div class="clearB"></div>';
			output  +='</div>';
			output  +='<a href="javascript:void(0);">';
			output  +='<div class="minW100 floatR borderB borderR borderL height23 posRel topn1 fbold fsize11 padT8 marL5" style="display:block; background: #fff; z-index:500; color:#329eea;">';
			output  +='	<img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/icon_arrowDownBlue.png")  + '" class="top2 posRel marL5 marR3">';
			output  +='	<span>Active Rules</span>';
			output  +='</div>';
			output  +='</a>';

			return $(output);
		},
		
		afterRequest: function () {
			var self = this;
			$(self.target).empty();
			var keyword = $.isArray(self.manager.store.values('q'))? self.manager.store.values('q')[0]: self.manager.store.values('q');
			
			if($.isNotBlank(keyword)){
				$(self.target).html(self.getTemplate());
				
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
						case "demote": checkboxId="disableDemote"; break;
						case "exclude": checkboxId="disableExclude"; break;
						case "facet sort": checkboxId="disableFacetSort"; break;
					}
					
					$li.find('.select > input[type="checkbox"]').prop({
						"id": checkboxId
						}).val(rule["id"]).slidecheckbox({
						id: checkboxId,
						initOn: rule["active"]==="true",
						locked: false, //TODO:
						changeStatusCallback: function(base, dt){
							var cid = dt.id;
							var cval = dt.value; 
							if(dt.status){
								self.manager.store.remove(cid);
							}else{
								self.manager.store.addByValue(cid, AjaxSolr.Parameter.escapeValue(cval));
							}

							self.manager.doRequest();
						}
					});
					
					$li.find(".ruleType").text(rule["type"]);
					$li.find(".name").text(rule["name"]);
					
					$li.find(".imageIcon > img").preview({
						ruleType: rule["type"],
						ruleId: rule["id"],
						itemForceAddStatusCallback: function(base, memberIds){
							if (rule["type"].toLowerCase() === "elevate")
							ElevateServiceJS.isRequireForceAdd(keyword, memberIds, {
								callback:function(data){
									base.updateForceAddStatus(data);
								},
								preHook: function(){
									base.prepareForceAddStatus();
								}
							});
						}
					});
					
					$li.show();
					self.getRuleStatus($li, rule);
					$ul.append($li);
				}
				
				if (!$.isEmptyObject(self.manager.response.responseHeader["replacement_keyword"]) && $.isNotBlank(self.manager.response.responseHeader["replacement_keyword"]["replacement_keyword"])){
					var $li = $ul.find("li#itemPattern").clone().prop("id", "rrNote");
					$li.find(".ruleType").removeClass("fbold")
											  .removeClass("w310")
											  .addClass("w95p")								
											  .html('<div class="alert padL10">Search results displayed are for <span class="fbold fred">' + self.manager.response.responseHeader["replacement_keyword"]["replacement_keyword"] + "</span>");
					$li.find(".select,.imageIcon,.name").remove();
					$li.show();
					$ul.append($li);
				}
				
				$ul.find("li").removeClass("alt");
				$ul.find("li:even").addClass("alt");
				
			}
		}
	});

})(jQuery);