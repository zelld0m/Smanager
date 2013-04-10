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
				click: base.showQtipPreview()
			});
		};

		base.prepareForceAddStatus = function(){
			base.contentHolder.find('div#forceAdd').show();
		};

		base.updateForceAddStatus = function(data){
			for(var mapKey in data){
				var $tr = base.contentHolder.find('tr#item' + $.formatAsId(mapKey));
				var $item = base.memberIdToItem[mapKey];

				// Force Add Color Coding
				if(data[mapKey] && !$item["forceAdd"]){

				}else if(data[mapKey] && $item["forceAdd"]){
					$tr.addClass("forceAddBorderErrorClass");
				}else if(!data[mapKey] && $item["forceAdd"]){
					$tr.addClass("forceAddClass");
				}else if(!data[mapKey] && !$item["forceAdd"]){
					$tr.addClass("forceAddErrorClass");
				}
			}

			base.contentHolder.find('div#forceAdd').hide();
		};

		base.setImage = function(tr, item){

			var imagePath = item["imagePath"];
			switch(base.getItemType(item)){
			case "ims" : imagePath = GLOBAL_contextPath + '/images/ims_img.jpg'; break;
			case "cnet" : imagePath = GLOBAL_contextPath + '/images/productSiteTaxonomy_img.jpg'; break;
			case "facet" : imagePath = GLOBAL_contextPath + '/images/facet_img.jpg'; break;
			default: if ($.isBlank(imagePath)) imagePath = GLOBAL_contextPath + "/images/no-image60x60.jpg"; break;
			}

			setTimeout(function(){	
				tr.find("td#itemImage > img").attr("src", imagePath).off().on({
					error:function(){ 
						$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image60x60.jpg"); 
					}
				});
			},10);
		};

		base.getItemType = function(item){
			var $condition = item.condition;
			var type = "unknown";

			if($.isBlank($condition)){
				return type;
			}

			if (!$condition["CNetFilter"] && !$condition["IMSFilter"]){
				type="facet";
			}else if($condition["CNetFilter"]){
				type="cnet";
			}else if($condition["IMSFilter"]){
				type="ims";
			}

			return type;
		};

		base.populateItemTable = function(ruleType, data){
			var $content = base.contentHolder;
			var list = data.list;

			var memberIds = new Array();
			var $table = $content.find("table#item");
			base.memberIdToItem = new Array();

			$table.find("tr:not(#itemPattern)").remove();

			$content.find("#ruleInfo").text($.trim(base.options.ruleInfo));
			$content.find("#requestType").text(base.options.requestType);

			if (data.totalSize==0){
				$tr = $content.find("tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td:not(#itemPosition)").remove();
				$tr.find("td#itemPosition").attr("colspan", "6").html("No item specified for this rule");
				$tr.appendTo($table);
			}else{

				for (var i = 0; i < data.totalSize; i++) {
					memberIds.push(list[i]["memberId"]);
					base.memberIdToItem[list[i]["memberId"]] = list[i];

					var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["memberId"])).show();	
					$tr.find("td#itemPosition").html(ruleType.toLowerCase()==="elevate"?  list[i]["location"] : parseInt(i) + 1);

					var PART_NUMBER = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "PART_NUMBER";
					var FACET = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "FACET";

					if(FACET){
						base.setImage($tr,list[i]);
						$tr.find("td#itemMan").text(list[i].condition["readableString"])
						.prop("colspan",3)
						.removeClass("txtAC")
						.addClass("txtAL")
						.attr("width", "363px");
						$tr.find("#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 

						if ($.isBlank(list[i]["isExpired"])){
							$tr.find("#itemValidityDaysExpired").remove();
						}

						$tr.find("td#itemDPNo,td#itemName").remove();
					}
					else if(PART_NUMBER){
						if($.isNotBlank(list[i]["dpNo"])){
							base.setImage($tr,list[i]);
							$tr.find("td#itemDPNo").html(list[i]["dpNo"]);
							$tr.find("td#itemMan").html(list[i]["manufacturer"]);
							$tr.find("td#itemName").html(list[i]["name"]);
						}
						else{
							$tr.find("td#itemImage").html("Product EDP:" + list[i]["edp"] + " is no longer available in the search server you are connected")
							.prop("colspan",4)
							.removeClass("txtAC")
							.addClass("txtAL")
							.attr("width", "369px");
							$tr.find("td#itemDPNo,td#itemMan,td#itemName").remove();
						}

						$tr.find("#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]);
						if ($.isBlank(list[i]["isExpired"])){
							$tr.find("#itemValidityDaysExpired").remove();
						}
					}

					$tr.appendTo($table);
				};

				if (base.options.ruleType.toLowerCase() === "elevate" && memberIds.length>0) 
					base.options.itemForceAddStatusCallback(base, memberIds);
			}

			// Alternate row style
			$content.find("tr#itemPattern").hide();
			$content.find("tr:not(#itemPattern):even").addClass("alt");
		};

		base.getDatabaseData = function(){
			var $content = base.contentHolder;

			switch(base.options.ruleType.toLowerCase()){
			case "elevate": 
				ElevateServiceJS.getAllElevatedProductsIgnoreKeyword(base.options.ruleId, 0, 0,{
					callback: function(data){
						base.populateItemTable("Elevate", data);
					},
					postHook:function(){
						base.options.templateEvent(base);
					}
				});
				break;
			case "exclude": 
				ExcludeServiceJS.getAllExcludedProductsIgnoreKeyword(base.options.ruleId , 0, 0,{
					callback: function(data){
						base.populateItemTable("Exclude", data);
					},
					postHook:function(){
						base.options.templateEvent(base);
					}
				});
				break;
			case "demote": 
				DemoteServiceJS.getAllProductsIgnoreKeyword(base.options.ruleId , 0, 0,{
					callback: function(data){
						base.populateItemTable("Demote", data);
					},
					postHook:function(){
						base.options.templateEvent(base);
					}
				});
				break;
			case "facetsort": 
			case "facet sort": 
				var $table = $content.find("table#item");
				var $rulePreview = $content.find("div#leftPreview");

				FacetSortServiceJS.getRuleById(base.options.ruleId, {
					callback: function(data){
						$rulePreview.find("#ruleInfo").text(data.name);
						$rulePreview.find("#ruleType").text(data.ruleType.toLowerCase());

						for(var facetGroup in data.items){
							var facetName = facetGroup;
							var facetValue = data.items[facetGroup];
							var highlightedItems = "";
							var $tr = $table.find("tr#itemPattern").clone();
							$tr.prop({id: $.formatAsId(facetName)});
							$tr.find("#itemName").text(facetName);

							if($.isArray(facetValue)){
								for(var i=0; i < facetValue.length; i++){
									highlightedItems += (i+1) + ' - ' + facetValue[i] + '<br/>';
								}
							}
							$tr.find("#itemHighlightedItem").html(highlightedItems);

							var sortTypeDisplay = "";
							var sortType = data.groupSortType[facetGroup] == null ? data.sortType : data.groupSortType[facetGroup];

							switch(sortType){
							case "ASC_ALPHABETICALLY": sortTypeDisplay = "A-Z"; break;
							case "DESC_ALPHABETICALLY": sortTypeDisplay = "Z-A"; break;
							case "ASC_COUNT": sortTypeDisplay = "Count Asc"; break;
							case "DESC_COUNT": sortTypeDisplay = "Count Desc"; break;
							}

							$tr.find("#itemSortType").text(sortTypeDisplay);
							$tr.show();
							$table.append($tr);
						};						
					},
					postHook:function(){
						$table.find("tr#preloader").hide();
						base.options.templateEvent(base);
					}
				});
				break;
			case "querycleaning": 
			case "query cleaning": 
				$content.find(".infoTabs").tabs({});

				$content.find("div.ruleFilter table#itemHeader th#fieldNameHeader").html("#");
				$content.find("div.ruleFilter table#itemHeader th#fieldValueHeader").html("Rule Filter");

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
						$content.find("#ruleInfo").html(data["ruleName"]);
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
							$content.find("div#ruleChange").find("#replaceKeywordVal").html(data["changeKeyword"]);
						}

						$content.find("div#ruleChange").find("#searchHeaderTextOpt").text(
								data["replaceKeywordMessageType"]["intValue"] == 3 ? 
										data["replaceKeywordMessageCustomText"] :
											data["replaceKeywordMessageType"]["description"]
						);

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
					},
					postHook: function(){
						base.options.templateEvent(base);
					}
				});

				break;
			case "rankingrule": 
			case "ranking rule": 
				$content.find(".infoTabs").tabs({});

				RelevancyServiceJS.getRule(base.options.ruleId, {
					callback: function(data){
						$content.find("#ruleInfo").html(data["ruleName"]);
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
					},
					postHook: function(){
						base.options.templateEvent(base);
					}
				});

				break;
			case "didyoumean":
			case "did you mean": 
				// TODO
				var $table = $content.find("table#item");
				var $rulePreview = $content.find("div#leftPreview");

				SpellRuleServiceJS.getRuleById(base.options.ruleId, {
					callback: function(data){
						var $tr = $content.find("tr#itemPattern").clone();
						
						var list1 = data["searchTerms"];
						for(var i=0; i<list1.length; i++) {
							$tr.find("td#itemSearchTerms").append("<span class='term' >" + list1[i] + "</span>");
						}
						
						var list2 = data["suggestions"];
						for(var i=0; i<list2.length; i++) {
							$tr.find("td#itemSuggestions").append("<span class='term' >" + list2[i] + "</span>");
						}
						$tr.show();
						$table.append($tr);
					},
					postHook:function() {
						$table.find("tr#preloader").hide();
						base.options.templateEvent(base);
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

		base.getPreTemplate = function(){
			if (base.options.enablePreTemplate){
				return base.options.preTemplate(base);
			}
		};

		base.getPostTemplate = function(){
			if (base.options.enablePostTemplate){
				return base.options.postTemplate(base);
			}
		};

		base.getTemplate = function(){
			var template = '';
			//var template = '<div>';

			switch(base.options.ruleType.toLowerCase()){
			case "elevate": 
			case "exclude":
			case "demote":
				template += '<div id="forceAdd" class="loadingWrapper" style="display:none"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-circ16x16.gif"><span class="fsize12 posRel topn3 padL5">Retrieving Force Add Status</span></div>';
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
				template += '					<td width="160px" class="txtAC" id="itemName"></td>';
				template += '					<td width="auto" class="txtAC">';
				template += '						<div id="itemValidity" class="w74 wordwrap"></div>';
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
				template += '</div>';
				break;
			case "facetsort":
			case "facet sort":
				template += '	<div class="w600 mar0 pad0">';
				template += '		<table class="tblItems w100p marT5">';
				template += '			<tbody>';
				template += '				<tr>';
				template += '					<th width="60px">Facet Name</th>';
				template += '					<th width="84px">Highlighted Items</th>';
				template += '					<th width="50px">Sorting of Other Items</th>';
				template += '				</tr>';
				template += '			<tbody>';
				template += '		</table>';
				template += '	</div>';
				template += '	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;">';
				template += '		<table id="item" class="tblItems w100p">';
				template += '			<tbody>';
				template += '				<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '					<td width="60px" class="txtAC" id="itemName"></td>';
				template += '					<td width="84px" class="txtAL" id="itemHighlightedItem"></td>';
				template += '					<td width="50px" class="txtAC" id="itemSortType"></td>';
				template += '				</tr>';
				template += '				<tr id="preloader">';
				template += '					<td colspan="6" class="txtAC">';
				template += '						<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';	
				template += '					</td>';
				template += '				</tr>';
				template += '			</tbody>';
				template += '		</table>';
				template += '	</div>';
				break;
			case "rankingrule": 
			case "ranking rule": 


				template += '	<div id="rankingSummary" class="infoTabs marB20 tabs">';
				template += '		<ul class="posRel top5 padB0" style="z-index:100">';

				if (base.options.ruleId.toLowerCase()!== (GLOBAL_storeId.toLowerCase()+ "_default")){
					template += '			<li><a href="#ruleKeyword"><span>Keyword</span></a></li>';
				}

				template += '			<li><a href="#ruleField"><span>Rule Field</span></a></li>';
				template += '		</ul>';

				template += '		<div id="ruleField" class="ruleField">';
				template += '			<div class="w582 mar0 padLR10">';
				template += '				<table class="tblItems w100p marT10" id="itemHeader">';
				template += '					<tbody>';
				template += '						<tr>';
				template += '							<th id="fieldNameHeader" class="w70 txtAC">Field Name</th>';
				template += '							<th id="fieldValueHeader" class="wAuto txtAC">Field Value</th>';
				template += '						</tr>';
				template += '					<tbody>';
				template += '				</table>';
				template += '			</div>';
				template += '			<div style="max-height:180px; overflow-y:auto;" class="w582 marLRB10">';
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

				if (base.options.ruleId.toLowerCase()!== (GLOBAL_storeId.toLowerCase()+ "_default")){
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

			case "querycleaning": 
			case "query cleaning": 
				template += '<div id="rankingSummary" class="infoTabs marB20 tabs">';

				template += '	<ul class="posRel top5 padB0" style="z-index:100">';
				template += '		<li><a href="#ruleKeyword"><span>Keyword</span></a></li>';
				template += '		<li><a href="#ruleFilter"><span>Filter</span></a></li>';
				template += '		<li><a href="#ruleChange"><span>Replace KW</span></a></li>';
				template += '	</ul>';
				template += '	<div class="clearB"></div>';

				template += '	<div id="ruleChange" class="ruleChange marB10 w602">';
				template += '		<div id="replaceKeyword" class="txtAL border bgf6f6f6 pad5 mar10">';
				template += '			<span>Replacement Keyword:</span>';
				template += '			<span id="replaceKeywordVal" class="fbold">None</span>';
				template += '		</div>';
				template += '		<div id="searchHeaderText" class="txtAL border bgf6f6f6 pad5 mar10">';
				template += '			<span>Search Header Text:</span>';
				template += '			<span id="searchHeaderTextOpt" class="fbold">None</span>';
				template += '		</div>';
				template += '		<div class="clearB"></div>';
				template += '	</div>';

				template += '	<div class="clearB"></div>';
				template += '	<div id="ruleFilter" class="ruleFilter marB10 w602">';
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
				template += '	<div id="ruleKeyword" class="ruleKeyword marB10 w602">';
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
				template += '						<td colspan="2" class="itemRow txtAC">';
				template += '							<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">	';
				template += '						</td>';
				template += '					</tr>';
				template += '				</tbody>';
				template += '			</table>';
				template += '		</div>	';
				template += '	</div>';
				break;
			// TODO 
			case "did you mean":
				template += '	<div class="w600 mar0 pad0">';
				template += '		<table class="tblItems w100p marT5">';
				template += '			<tbody>';
				template += '				<tr>';
				template += '					<th width="72px">Search Terms</th>';
				template += '					<th width="72px">Suggestions</th>';
				template += '				</tr>';
				template += '			<tbody>';
				template += '		</table>';
				template += '	</div>';
				template += '	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;">';
				template += '		<table id="item" class="tblItems w100p">';
				template += '			<tbody>';
				template += '				<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '					<td width="72px" class="txtAC term-list" id="itemSearchTerms"></td>';
				template += '					<td width="72px" class="txtAL term-list" id="itemSuggestions"></td>';
				template += '				</tr>';
				template += '				<tr id="preloader">';
				template += '					<td colspan="2" class="txtAC">';
				template += '						<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';
				template += '					</td>';
				template += '				</tr>';
				template += '			</tbody>';
				template += '		</table>';
				template += '	</div>';
				break;
			}

			return template;
		};

		base.showQtipPreview = function(e){
			var showContent = function(){
				var $div = $('<div id="leftPreview" class="floatL"></div>');
				$div.append(base.getPreTemplate());
				$div.append(base.getTemplate());
				$div.append(base.getPostTemplate());
				base.contentHolder.html($div);
				base.getDatabaseData();
			};
			
			if(base.options.center){
				base.$el.qtip({
					id: "rule-preview",
					content: {
						text: $('<div/>'),
						title: { 
							text: base.options.headerText, 
							button: true
						}
					},
					position: {
						my: 'center',
						at: 'center',
						target: $(window)
					},
					show: {
						modal: true,
						solo: true
					},
					style: {
						width: 'auto'
					},
					events: {
						show: function(event, api) {
							base.contentHolder = $("div", api.elements.content);
							base.api = api;
							showContent();
						},
						hide: function(event, api) {
							$("div", api.elements.content).empty();
						}
					}
				});
			}else{
				base.$el.qtip({
					id: "rule-preview",
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
							base.contentHolder = $("div", api.elements.content);
							base.api = api;
							showContent();
						},
						hide:function(event, api){
							$("div", api.elements.content).empty();
						}
					}
				});
			}
		};

		// Run initializer
		base.init();
	};

	$.preview.defaultOptions = {
			headerText:"Rule Preview",
			ruleType: "",
			ruleId: "",
			ruleRefId: "",
			ruleStatusId: "",
			ruleInfo: "",
			requestType: "",
			center: false,
			enablePreTemplate: false,
			enablePostTemplate: false,
			preTemplate: function(base){},
			postTemplate: function(base){},
			templateEvent: function(base){},
			itemForceAddStatusCallback: function(base, memberIds){},
			setSelectedOverwriteRulePreview: function(base, rulename){}
	};

	$.fn.preview = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.preview(this, options));
			});
		};
	};
})(jQuery);
