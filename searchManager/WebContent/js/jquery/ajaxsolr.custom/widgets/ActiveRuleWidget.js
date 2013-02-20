(function ($) {
	
	AjaxSolr.ActiveRuleWidget = AjaxSolr.AbstractWidget.extend({
		init: function(){
			var settings = $.cookie('ar.' + GLOBAL_username);
			if($.isBlank(settings)){
				$.cookie('ar.' + GLOBAL_username, "collapse" ,{path:GLOBAL_contextPath});
			}
		},
		
		getRuleStatus:function($li, rule){
			DeploymentServiceJS.getRuleStatus(rule["type"], rule["id"], {
				callback:function(data){
					if(!$.isEmptyObject(data)){
						$li.find('.ruleStatus > .status').text(getRuleNameSubTextStatus(data));
						$li.find('.ruleStatus > .statusMode').text($.isNotBlank(data["locked"]) && data["locked"]? " [ Read-Only ]" : "");
						$li.find('.lastPublished').text($.isNotBlank(data["lastPublishedDate"])? 'Published on ' + data["lastPublishedDate"].toUTCString(): '');
					}
				}
			});
		},
		
		beforeRequest:function(){
			var self = this;
			$(self.target).find('#switcher').off();
			$(self.target).find('#switcherText').text("Active Rule");
			
			//TODO: Remove/Update
			$(self.target).find('ul#itemListing > li.items:not(#itemPattern)').each(function(idx, el){
				$(el).find('.select > input[type="checkbox"]').prop("disabled", true);
				$(el).find('.preloader').show();
			});
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
				$(self.target).find('#switcherText').text(rules.length + ' Active ' + (rules.length > 1 ? 'Rules': 'Rule'))
				
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

				if($.cookie('ar.' + GLOBAL_username) === "expand"){
					self.showRules();
				}else{
					self.hideRules();
				}
				
				$(self.target).find('#switcher').off().on({
					click: function(e){
						if ($(self.target).find("#collapse").is(':visible')){
							$.cookie('ar.' + GLOBAL_username, "expand" ,{path:GLOBAL_contextPath});
							self.showRules();
						}else{
							$.cookie('ar.' + GLOBAL_username, "collapse" ,{path:GLOBAL_contextPath});
							self.hideRules();
						}
					}
				});
			}
		},
		
		showRules: function(){
			var self = this;
			$(self.target).find("#collapse").fadeOut("slow", function(foe){
				$(self.target).find("#expand").slideDown("slow", function(sde){
					$(self.target).find("#switcherIcon").prop({
						src: GLOBAL_contextPath + "/images/icon_expand.png"
					});
				});
			});
		},
		
		hideRules: function(){
			var self = this;
			
			$(self.target).find("#expand").fadeOut("slow", function(foe){
				$(self.target).find("#collapse").slideDown("slow", function(sde){
					$(self.target).find("#switcherIcon").prop({
						src: GLOBAL_contextPath + "/images/icon_collapse.png"
					});
				});
			});
		},
		
		getTemplate: function(){
			var output  = '';

			output  +='<div style="display:block;" class="fsize11 marT10 fDGray border">';
			output  +='	<div id="expand" style="display:none">';
			output  +='		<div id="activeRuleNoteHide" class="w655 marL20 info notification border fsize11 marB20 marT10">';
			output  +=' 		Below are rules applied to your current search. You can toggle ON/OFF of each active rules to examine its effect on search results';
			output  +=' 	</div>';
			output  +='		<ul id="itemListing" class="mar16 marB10 marL20" >';
			output  +='			<li id="itemPattern" class="items borderB padTB5 clearfix" style="display:none; width:690px">';
			output  +='				<div class="floatL marT6">';
			output  +='					<label class="select floatL w80 posRel topn3"><input type="checkbox" class="firerift-style-checkbox on-off ruleControl"></label>';
			output  +='				</div>';
			output	+='				<div class="floatR w300">';
			output  +='					<div class="w300">';
			output  +='						<label class="w30 preloader posRel floatR" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/ajax-loader-rect.gif")  + '"></label>';
			output  +='						<label class="ruleStatus w240 marT6 posRel floatL">';
			output  +='							<span class="status fgray marL60"></span>';
			output  +='							<span class="statusMode fsize11 forange padL5"></span>';
			output  +='						</label>';
			output  +='						<label class="lastPublished w240 fgray"></label>';
			output  +='					</div>';
			output	+='				</div>';
			output	+='				<div class="floatR">';
			output  +='					<div class="w230">';
			output  +='						<label class="ruleType fbold w230 marT6"></label>';
			output  +='					</div>';
			output  +='					<div class="w230">';
			output  +='						<label class="imageIcon w20 floatL posRel topn2"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/icon_reviewContent2.png")  + '" class="top2 posRel"></label>';
			output  +='						<label class="name floatL w210"><span class="fbold"></span></label>';
			output  +='					</div><div class="clearB"></div>';
			output	+='				</div>';
			output  +='			</li>';
			output  +='		</ul>';
			output  +='	</div>';
			output  +='	<div id="collapse" style="display:none">';
			output  +='		<div id="activeRuleNoteShow" class="w655 marL20 info notification border fsize11 marB10 marT10">';
			output  +=' 		Toggle this section to view all rules applied to current search';
			output  +=' 	</div>';
			output  +='	</div>';
			output  +='</div>';
			
			output  +='<a id="switcher" href="javascript:void(0);">';
			output  +='	<div class="minW140 floatR borderB borderR borderL height23 posRel topn1 fbold fsize11 padT8 marL5" style="display:block; background: #fff; z-index:500; color:#329eea;">';
			output  +='		<img id="switcherIcon" src="' + GLOBAL_contextPath + '/images/icon_expand.png" class="posRel marL20 marR3 marTn2 floatL">';
			output  +='		<span id="switcherText" class="posRel marB6 floatL"></span>';
			output  +='	</div>';
			output  +='</a>';

			return $(output);
		}
		
	});

})(jQuery);