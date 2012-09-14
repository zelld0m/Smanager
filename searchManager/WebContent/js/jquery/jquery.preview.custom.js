(function($){

	$.preview = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("preview", base);

		base.init = function(){
			base.options = $.extend({},$.preview.defaultOptions, options);

			base.$el.off().on({
				click: base.showPreview()
			});
		};
		
		base.populateItemTable = function(ruleType, content, data){
			var $content = content;
			var list = data.list;
			
			var $table = $content.find("table#item");
			
			$table.find("tr:not(#itemPattern)").remove();

			if (data.totalSize==0){
				$tr = $content.find("tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td:not(#itemPosition)").remove();
				$tr.find("td#itemPosition").attr("colspan", "6").html("No item specified for this rule");
				$tr.appendTo($table);
			}else{
				
				var setImage = function(tr, imagePath){
					setTimeout(function(){	
						tr.find("td#itemImage > img").attr("src",imagePath).off().on({
							error:function(){ 
								$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image60x60.jpg"); 
							}
						});
					},10);
				};
				
				var getFacetItemType = function(item){
					var $condition = item.condition;
					var type = "";

					if (!$condition["CNetFilter"] && !$condition["IMSFilter"]){
						type="facet";
					}else if($condition["CNetFilter"]){
						type="cnet";
					}else if($condition["IMSFilter"]){
						type="ims";
					}
					return type;
				};
				
				for (var i = 0; i < data.totalSize; i++) {
					var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["edp"])).show();	
					$tr.find("td#itemPosition").html(ruleType.toLowerCase()==="elevate"?  list[i]["location"] : parseInt(i) + 1);

					if (list[i]["forceAdd"]){
						$tr.addClass("forceAddClass");
					}
					
					var PART_NUMBER = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "PART_NUMBER";
					var FACET = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "FACET";
					
					if(FACET){
						var imagePath = list[i]["imagePath"];
						
						if($.isBlank(imagePath)){
							imagePath = GLOBAL_contextPath + '/images/';
							switch(getFacetItemType(list[i])){
							case "ims" : imagePath += "ims_img.jpg"; break;
							case "cnet" : imagePath += "cnet_img.jpg"; break;
							case "facet" : imagePath += "facet_img.jpg"; break;
							}
						}
						
						setImage($tr,imagePath);
						$tr.find("td#itemMan").html(list[i].condition["readableString"])
							.prop("colspan",3)
							.removeClass("txtAC")
							.addClass("txtAL");
						$tr.find("#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 
						
						if (!list[i]["isExpired"]){
							$tr.find("#itemValidityDaysExpired").remove();
						}
						
						$tr.find("td#itemDPNo,td#itemName").remove();
					}
					else if(PART_NUMBER){
						if($.isNotBlank(list[i]["dpNo"])){
							setImage($tr,list[i]["imagePath"]);
							$tr.find("td#itemDPNo").html(list[i]["dpNo"]);
							$tr.find("td#itemMan").html(list[i]["manufacturer"]);
							$tr.find("td#itemName").html(list[i]["name"]);
							$tr.find("#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]);
							
							if (!list[i]["isExpired"]){
								$tr.find("#itemValidityDaysExpired").remove();
							}
						}
						else{
							$tr.find("td#itemImage").html("Product EDP:" + list[i]["edp"] + " is no longer available in the search server you are connected")
													.prop("colspan",4)
													.removeClass("txtAC")
													.addClass("txtAL");
							$tr.find("td#itemDPNo,td#itemMan,td#itemName,td#itemValidity").remove();
						}
					}
					
					
					$tr.appendTo($table);
				};
			}

			// Alternate row style
			$content.find("tr#itemPattern").hide();
			$content.find("tr:not(#itemPattern):even").addClass("alt");
		};
		
		base.getDatabaseData = function(content){
			var $content = content;
			
			switch(base.options.ruleType.toLowerCase()){
				case "elevate": 
					ElevateServiceJS.getAllElevatedProductsIgnoreKeyword(base.options.ruleId, 0, 0,{
						callback: function(data){
							base.populateItemTable("Elevate", $content, data);
						}
					});
					break;
				case "exclude": 
					ExcludeServiceJS.getAllExcludedProductsIgnoreKeyword(base.options.ruleId , 0, 0,{
						callback: function(data){
							base.populateItemTable("Exclude", $content, data);
						}
					});
					break; 
				case "query cleaning": 
					$content.find(".infoTabs").tabs({});
				
					$content.find("div.ruleFilter table#itemHeader th#fieldNameHeader").html("#");
					$content.find("div.ruleFilter table#itemHeader th#fieldValueHeader").html("Rule Filter");
					$content.find("div.ruleChange > #noChangeKeyword, div.ruleChange > #hasChangeKeyword").hide();
					
					RedirectServiceJS.getRule(base.options.ruleId, {
						callback: function(data){

							var $table = $content.find("div.ruleFilter table#item");
							$table.find("tr:not(#itemPattern)").remove();

							if(data.readableConditions.length==0){
								$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item0").show();
								$tr.find("td#fieldName").html("No filters specified for this rule").attr("colspan","2");
								$tr.find("td#fieldValue").remove();
								$tr.appendTo($table);

							}else{
								for(var field in data.readableConditions){
									$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item" + $.formatAsId(field)).show();
									$tr.find("td#fieldName").html(parseInt(field)+1);
									$tr.find("td#fieldValue").html(data.readableConditions[field]);
									$tr.appendTo($table);
								}	
							}

							$table.find("tr:even").addClass("alt");
							$content.find("#ruleInfo").html(data["ruleName"] + " [ " + data["ruleId"] + " ]");
							$content.find("#description").html(data["description"]);
							switch (data["redirectTypeId"]) {
								case "1":
									$content.find("#redirectType").html("Filter");
									break;
								case "2":
									$content.find("#redirectType").html("Replace Keyword");
									break;
								case "3":
									$content.find("#redirectType").html("Direct Hit");
									break;
								default:
									$content.find("#redirectType").html("");
									break;									
							}
							
							if ($.isNotBlank(data["changeKeyword"])){
								$content.find("div#ruleChange > div#hasChangeKeyword").show();
								$content.find("div#ruleChange > div#hasChangeKeyword > div > span#changeKeyword").html(data["changeKeyword"]);
							}else{
								$content.find("div#ruleChange > #noChangeKeyword").show();
							}
							
							var includeKeywordText = "Include keyword in search: <b>NO</b>";
							if($.isNotBlank(data["includeKeyword"])){
								includeKeywordText = "Include keyword in search: ";
								if(data["includeKeyword"]){
									includeKeywordText += "<b>YES</b>";
								}
								else{
									includeKeywordText += "<b>NO</b>";
								}
							}
							$content.find("div.ruleFilter div#includeKeywordInSearchText").show();
							$content.find("div.ruleFilter div#includeKeywordInSearchText").html(includeKeywordText);
							
							base.populateKeywordInRule($content, data["searchTerms"]);
						}
					});
					
					break;
				case "ranking rule": 
					$content.find(".infoTabs").tabs({});
					
					RelevancyServiceJS.getRule(base.options.ruleId, {
						callback: function(data){
							$content.find("#ruleInfo").html(data["ruleName"] + " [ " + data["ruleId"] + " ]");
							$content.find("#startDate").html(data["formattedStartDate"]);
							$content.find("#endDate").html(data["formattedEndDate"]);
							$content.find("#description").html(data["description"]);

							var $table = $content.find("div.ruleField table#item");
							$table.find("tr:not(#itemPattern)").remove();
							
							for(var field in data.parameters){
								$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
								$tr.find("td#fieldName").html(field);
								$tr.find("td#fieldValue").html(data.parameters[field]);
								$tr.appendTo($table);
							}	
							
							$table.find("tr:even").addClass("alt");
							
							base.populateKeywordInRule($content, base.toStringArray(data["relKeyword"]));
						}
					});
					
					break;
			}
		};
		
		base.getFileData = function(content){
			var $content = content;
			
			switch(base.options.ruleType.toLowerCase()){
				case "ranking rule": 
					$content.find(".infoTabs").tabs({});
					
					RuleVersioningServiceJS.getRankingRuleVersion(base.options.ruleId, base.options.version, {
						callback: function(data){
							$content.find("#ruleInfo").html("<strong>Version " + base.options.version  + "</strong> of " + data["ruleName"]);
							$content.find("#startDate").html(data["formattedStartDate"]);
							$content.find("#endDate").html(data["formattedEndDate"]);
							$content.find("#description").html(data["description"]);

							var $table = $content.find("div.ruleField table#item");
							$table.find("tr:not(#itemPattern)").remove();
							
							for(var field in data.parameters){
								$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
								$tr.find("td#fieldName").html(field);
								$tr.find("td#fieldValue").html(data.parameters[field]);
								$tr.appendTo($table);
							}	
							
							$table.find("tr:even").addClass("alt");
							
							base.populateKeywordInRule($content, base.toStringArray(data["relKeyword"]));
						}
					});
					
					break;
			}
		};
		
		base.toStringArray = function(relKeyObj){
			var keyList = new Array();
			var i = 0;
			for (var relKey in relKeyObj){
				keyList[i++] = relKeyObj[relKey]["keyword"]["keyword"];
			}
			return keyList;
		};
		
		base.populateKeywordInRule = function(content, list){
			var $content = content;
			var $table = $content.find("div.ruleKeyword table#item");
			$table.find("tr:not(#itemPattern)").remove();

			if (list==null || list.length==0){
				$tr = $content.find("div.ruleKeyword tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td#fieldName").html("No keywords associated to this rule").attr("colspan","2");
				$tr.find("td#fieldValue").remove();
				$tr.appendTo($table);
			}else{
				for(var i=0; i< list.length; i++){
					$tr = $content.find("div.ruleKeyword tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i])).show();
					$tr.find("td#fieldName").html(parseInt(i)+1);
					$tr.find("td#fieldValue").html(list[i]);
					$tr.appendTo($table);
				}	
			}
			
			$table.find("tr:even").addClass("alt");
		};
		
		base.getTemplate = function(){
			var template = '<div>';

			switch(base.options.ruleType.toLowerCase()){
				case "elevate": 
				case "exclude": 
					template += '<div id="previewTemplate1">';
					template += '	<div class="w600 mar0 pad0">';
					template += '		<table class="tblItems w100p marT5">';
					template += '			<tbody>';
					template += '				<tr>';
					template += '					<th width="20px">#</th>';
					template += '					<th width="60px" id="selectAll">Image</th>';
					template += '					<th width="94px">Manufacturer</th>';
					template += '					<th width="70px">SKU #</th>';
					template += '					<th width="160px">Name</th>';
					template += '					<th width="90px">Validity</th>';
					template += '				</tr>';
					template += '			<tbody>';
					template += '		</table>';
					template += '	</div>';
					template += '	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;">';
					template += '		<table id="item" class="tblItems w100p">';
					template += '			<tbody>';
					template += '				<tr id="itemPattern" class="itemRow" style="display: none">';
					template += '					<td width="20px" class="txtAC" id="itemPosition"></td>';
					template += '					<td width="60px" class="txtAC" id="itemImage"><img src="" width="50"/></td>';
					template += '					<td width="94px" class="txtAC" id="itemMan"></td>';
					template += '					<td width="70px" class="txtAC" id="itemDPNo"></td>';
					template += '					<td width="162px" class="txtAC" id="itemName"></td>';
					template += '					<td class="txtAC">';
					template += '						<div id="itemValidity"></div>';
					template += '						<div id="itemValidityDaysExpired"><img src="' + GLOBAL_contextPath + '/images/expired_stamp50x16.png"></div>';
					template +='					</td>';
					template += '				</tr>';
					template += '				<tr>';
					template += '					<td colspan="6" class="txtAC">';
					template += '						<img id="preloader" alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';	
					template += '					</td>';
					template += '				</tr>';
					template += '			</tbody>';
					template += '		</table>';
					template += '	</div>';
					template += '</div>';
					break;
				case "ranking rule": 
					template += '<div id="previewTemplate2">';
					template += '	<div class="rulePreview w590 marB20">';
					template += '		<label class="w110 floatL marL20 fbold">Rule Info:</label>';
					template += '		<label class="wAuto floatL" id="ruleInfo">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '		<div class="clearB"></div>';
					template += '		<label class="w110 floatL marL20 fbold">Start Date:</label>';
					template += '		<label class="wAuto floatL" id="startDate">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '		<div class="clearB"></div>';
					template += '		<label class="w110 floatL marL20 fbold">End Date:</label>';
					template += '		<label class="wAuto floatL" id="endDate">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '		<div class="clearB"></div>';
					template += '		<label class="w110 floatL marL20 fbold">Description:</label>';
					template += '		<label class="wAuto floatL" id="description">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '		<div class="clearB"></div>';
					template += '	</div>';

					template += '	<div id="rankingSummary" class="infoTabs marB20 tabs">';
					template += '		<ul class="posRel top5" style="z-index:100">';
					
					if (base.options.ruleId.toLowerCase()!== (GLOBAL_store.toLowerCase()+ "_default")){
						template += '			<li><a href="#ruleKeyword"><span>Keyword</span></a></li>';
					}

					template += '			<li><a href="#ruleField"><span>Rule Field</span></a></li>';
					template += '		</ul>';

					template += '		<div id="ruleField" class="ruleField">';
					template += '			<div class="w580 mar0 padLR5">';
					template += '				<table class="tblItems w100p marT10" id="itemHeader">';
					template += '					<tbody>';
					template += '						<tr>';
					template += '							<th id="fieldNameHeader" class="w70 txtAC">Field Name</th>';
					template += '							<th id="fieldValueHeader" class="wAuto txtAC">Field Value</th>';
					template += '						</tr>';
					template += '					<tbody>';
					template += '				</table>';
					template += '			</div>';
					template += '			<div style="max-height:180px; overflow-y:auto;" class="w580 mar0">';
					template += '				<table id="item" style="border-collapse:collapse" class="tblItems w100p marB10">';
					template += '					<tbody>';
					template += '						<tr id="itemPattern" class="itemRow" style="display: none">';
					template += '							<td class="txtAC w70" id="fieldName"></td>';
					template += '							<td id="fieldValue" class="wAuto"></td>';
					template += '						</tr>';
					template += '						<tr>';
					template += '							<td colspan="2" class="itemRow txtAC">';
					template += '								<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '							</td>';
					template += '						</tr>';
					template += '					</tbody>';
					template += '				</table>';
					template += '			</div>';
					template += '		</div>';
					template += '		<div class="clearB"></div>	';
					
					if (base.options.ruleId.toLowerCase()!== (GLOBAL_store.toLowerCase()+ "_default")){
						template += '		<div id="ruleKeyword" class="ruleKeyword marB10">';
						template += '			<div class="w580 mar0 padLR5">';
						template += '				<table class="tblItems w100p marT10" id="itemHeader">';
						template += '					<tbody>';
						template += '						<tr>';
						template += '							<th id="fieldNameHeader" class="w70 txtAC">#</th>';
						template += '							<th id="fieldValueHeader" class="wAuto txtAC">Keyword</th>';
						template += '						</tr>';
						template += '					<tbody>';
						template += '				</table>';
						template += '			</div>';
						template += '			<div style="max-height:180px; overflow-y:auto;" class="w580 mar0">';
						template += '				<table id="item" style="border-collapse:collapse" class="tblItems w100p marB10">';
						template += '					<tbody>';
						template += '						<tr id="itemPattern" class="itemRow" style="display: none">';
						template += '							<td class="txtAC w70" id="fieldName"></td>';
						template += '							<td id="fieldValue" class="wAuto"></td>';
						template += '						</tr>';
						template += '						<tr>';
						template += '							<td colspan="2" class="itemRow txtAC">';
						template += '								<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
						template += '							</td>';
						template += '						</tr>';
						template += '					</tbody>';
						template += '				</table>';
						template += '			</div>';
						template += '		</div>';
					}
					
					template += '	</div>';
					template += '</div>';
					break;
				case "query cleaning": 
					template += '<div id="queryCleaningTemplate">';
					template += '	<div class="rulePreview w590 marB20">';
					template += '		<label class="w110 floatL marL20 fbold">Rule Info:</label>';
					template += '		<label class="wAuto floatL" id="ruleInfo">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '		<div class="clearB"></div>';
					template += '		<label class="w110 floatL marL20 fbold">Description:</label>';
					template += '		<label class="wAuto floatL" id="description">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '		<div class="clearB"></div>';
					template += '		<label class="w110 floatL marL20 fbold">Active Type:</label>';
					template += '		<label class="wAuto floatL" id="redirectType">';
					template += '			<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '		</label>';
					template += '	<div class="clearB"></div>			';				
					template += '</div>';

					template += '<div id="rankingSummary" class="infoTabs marB20 tabs">';

					template += '	<ul class="posRel top5" style="z-index:100">';
					template += '		<li><a href="#ruleKeyword"><span>Keyword</span></a></li>';
					template += '		<li><a href="#ruleFilter"><span>Filter</span></a></li>';
					template += '		<li><a href="#ruleChange"><span>Replace KW</span></a></li>';
					template += '	</ul>';
					template += '	<div class="clearB"></div>';
					
					template += '	<div id="ruleChange" class="ruleChange marB10">';
					template += '		<div id="noChangeKeyword" class="txtAC mar20" style="display:none">';
					template += '			<span class="fsize11">No replacement keyword associated to this rule</span>';
					template += '		</div>';
					template += '		<div id="hasChangeKeyword" style="display:none">';
					template += '			<div class="fsize12 txtAL mar20">';
					template += '				Replace Keyword: <span id="changeKeyword" class="fbold"></span>';
					template += '			</div>';
					template += '		</div>';
					template += '		<div class="clearB"></div>';
					template += '	</div>';

					template += '	<div class="clearB"></div>';
					template += '	<div id="ruleFilter" class="ruleFilter marB10">';
					template += '		<div id="includeKeywordInSearchText" class="includeKeywordInSearchText border bgf6f6f6 w570 pad5 mar10"></div>';
					template += '		<div class="w580 mar0 padLR5">';
					template += '			<table class="tblItems w100p marT10" id="itemHeader">';
					template += '				<tbody>';
					template += '					<tr>';
					template += '						<th id="fieldNameHeader" class="w70 txtAC">Field Name</th>';
					template += '						<th id="fieldValueHeader" class="wAuto txtAC">Field Value</th>';
					template += '					</tr>';
					template += '				<tbody>';
					template += '			</table>';
					template += '		</div>';
					template += '		<div style="max-height:180px; overflow-y:auto;" class="w580 mar0">';
					template += '			<table id="item" style="border-collapse:collapse" class="tblItems w100p marB10">';
					template += '				<tbody>';
					template += '					<tr id="itemPattern" class="itemRow" style="display: none">';
					template += '						<td class="txtAC w70" id="fieldName"></td>';
					template += '						<td id="fieldValue" class="wAuto"></td>';
					template += '					</tr>';
					template += '					<tr>';
					template += '						<td colspan="2" class="itemRow txtAC">';
					template += '							<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">	';
					template += '						</td>';
					template += '					</tr>';
					template += '				</tbody>';
					template += '			</table>';
					template += '		</div>';
					template += '	</div>';

					template += '	<div class="clearB"></div>';
					template += '	<div id="ruleKeyword" class="ruleKeyword marB10">';
					template += '		<div class="w580 mar0 padLR5">';
					template += '			<table class="tblItems w100p marT10" id="itemHeader">';
					template += '				<tbody>';
					template += '					<tr>';
					template += '						<th id="fieldNameHeader" class="w70 txtAC">#</th>';
					template += '						<th id="fieldValueHeader" class="wAuto txtAC">Keyword</th>';
					template += '					</tr>';
					template += '				<tbody>';
					template += '			</table>';
					template += '		</div>';
					template += '		<div style="max-height:180px; overflow-y:auto;" class="w580 mar0">';
					template += '			<table id="item" style="border-collapse:collapse" class="tblItems w100p marB10">';
					template += '				<tbody>';
					template += '					<tr id="itemPattern" class="itemRow" style="display: none">';
					template += '						<td class="txtAC w70" id="fieldName"></td>';
					template += '						<td id="fieldValue" class="wAuto"></td>';
					template += '					</tr>';
					template += '					<tr>';
					template += '						<td colspan="2" class="itemRow  txtAC">';
					template += '							<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">	';
					template += '						</td>';
					template += '					</tr>';
					template += '				</tbody>';
					template += '			</table>';
					template += '		</div>	';
					template += '	</div>';
					template += '</div>';
					break;
			}
			template += '</div>';
			
			return template;
		};

		base.showPreview = function(){
			base.$el.qtip({
				content: {
					text: $('<div/>'),
					title: { 
						text: base.options.headerText, 
						button: true
					}
				},
				position:{
					at: 'top center',
					my: 'bottom center',
					target: base.$el
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						var $content = $("div", api.elements.content);
						$content.html(base.getTemplate());
						if ($.isNotBlank(base.options.version)){
							base.getFileData($content);
						}else{
							base.getDatabaseData($content);
						}
					},
					hide:function(event, api){
						$("div", api.elements.content).empty();
					}
				}
			});
		};
		
		// Run initializer
		base.init();
	};
	
	$.preview.defaultOptions = {
			headerText:"Rule Preview",
			ruleType: "",
			ruleId: "",
			version: ""
	};

	$.fn.preview = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.preview(this, options));
			});
		};
	};
})(jQuery);
