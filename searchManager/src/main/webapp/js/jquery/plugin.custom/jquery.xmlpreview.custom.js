(function($){

	$.xmlpreview = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		base.XML_SOURCE = "xml";
		base.DATABASE_SOURCE = "database";

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("xmlpreview", base);

		base.init = function(){
			base.options = $.extend({},$.xmlpreview.defaultOptions, options);

			base.$el.on({
				click: function(e){
					base.showQtipPreview();
				}
			});

			base.typeaheadManager = new AjaxSolr.Manager({
				solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
				store: (new AjaxSolr.ParameterStore())
			});

			base.typeaheadManager.addWidget(new AjaxSolr.TypeaheadSearchResultWidget({
				id: 'suggestion',
				target: '.suggestionFirst',
				brandId: 'brand',
				brandTarget: '.brandFirst',
				categoryId: 'category',
				categoryTarget: '.categoryFirst'
			}));
			
			base.typeaheadSourceManager = new AjaxSolr.Manager({
				solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
				store: (new AjaxSolr.ParameterStore())
			});
			
			base.typeaheadSourceManager.addWidget(new AjaxSolr.TypeaheadSearchResultWidget({
				id: 'suggestionSource',
				target: '.suggestionSourceFirst',
				brandId: 'brandSource',
				brandTarget: '.brandSourceFirst',
				categoryId: 'categorySource',
				categoryTarget: '.categorySourceFirst'
			}));

		};

		base.prepareForceAddStatus = function(contentHolder){
			contentHolder.find('div#forceAdd').show();
		};

		base.updateForceAddStatus = function(contentHolder, data, memberIdToItemMap){
			for(var mapKey in data){
				var $tr = contentHolder.find('tr#item' + $.formatAsId(mapKey));
				var $item = memberIdToItemMap[mapKey];

				// Force Add Color Coding
				if(!$item){

				}
				else if(data[mapKey] && !$item[0]["forceAdd"]){

				}else if(data[mapKey] && $item[0]["forceAdd"]){
					$tr.addClass("forceAddBorderErrorClass");
				}else if(!data[mapKey] && $item[0]["forceAdd"]){
					$tr.addClass("forceAddClass");
				}else if(!data[mapKey] && !$item[0]["forceAdd"]){
					$tr.addClass("forceAddErrorClass");
				}
			}

			contentHolder.find('div#forceAdd').hide();
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

		base.populateImportAsList = function(data, contentHolder, sourceData){
			var opt = $("#ruleItem"+$.formatAsId(base.options.ruleId)+" #importAs select").val();
			var strNewName = $("#ruleItem"+$.formatAsId(base.options.ruleId)+" #importAs #replacement #newName").val();

			contentHolder.find("#importAs").importas({
				inPreview: true,
				rule: base.options.ruleXml,
				selectedOpt:opt,
				newName:strNewName,
				ruleStatusList: base.options.ruleStatusMap==null? null : base.options.ruleStatusMap[base.options.ruleType],
						ruleTransferMap: base.options.ruleTransferMap,
						targetRuleStatusCallback: function(item, r, rs){
							var locked = !$.isEmptyObject(rs) && (rs["approvalStatus"]==="PENDING" || rs["approvalStatus"]==="APPROVED" || rs["updateStatus"] === "DELETE");

							var $importBtn = contentHolder.find("div#setImportBtn").removeClass('import_locked').removeClass('approve_active').addClass('approve_gray');
							var $rejectBtn = contentHolder.find("div#setRejectBtn").removeClass('import_locked').removeClass('reject_active').addClass('reject_gray');

							base.buttonHandler($importBtn);
							base.buttonHandler($rejectBtn);

							if(r["rejected"]){
								$rejectBtn
								.addClass('import_locked')
								.off("click mouseenter")
								.on({
									click: function(e){

									},
									mouseenter: showHoverInfo
								}, {locked: true, message: "You are not allowed to perform this action because you do not have the required permission or rule has been previously rejected."});
							}

							if (locked){
								$importBtn
								.addClass('import_locked')
								.off("click mouseenter")
								.on({
									click: function(e){

									},
									mouseenter: showHoverInfo
								}, {locked: true, message: "You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked."});
							}

							if(options.viewOnly != true)
								contentHolder.find("div#leftPreview").find("div#btnHolder").show();

							if(!$.isEmptyObject(rs)) base.getDatabaseData(contentHolder.find("div#rightPreview"), rs["ruleId"], rs["ruleName"]);
						}
			});
		};

		base.isLocked = function() {
			var rule = base.options.rule;
			var ruleEntity = rule["ruleEntity"];

			switch (ruleEntity) {
			case "QUERY_CLEANING":
			case "RANKING_RULE":
				if(!$.isEmptyObject(base.options.ruleTransferMap) && !$.isEmptyObject(base.options.ruleTransferMap[rule["ruleId"]])
						&& $.isNotBlank(base.options.ruleTransferMap[rule["ruleId"]]["ruleIdTarget"])) {
					return true;
				}
				break;
			}

			return false;
		};

		base.populateImportTypeList = function(data, contentHolder){
			var $importType= contentHolder.find("div.rulePreview > label#importType");
			var $select = $('<select></select>');
			$select.attr("id", "importType");
			var opt = $("#ruleItem"+$.formatAsId(base.options.ruleId)+" #type select").val();
			for (var index in data){
				$select.append($("<option>", {value: index}).text(data[index]));
			}
			$select.val(opt);
			$importType.html($select);
		};

		base.populateItemTable = function($content, ruleType, data, ruleName, sourceData){
			var list = data;
			var memberIds = new Array();
			var memberConditions = new Array();
			var $table = $content.find("table#item");
			base.memberIdToItem = new Object();

			$table.find("tr:not(#itemPattern)").remove();
			$table.find("tr").removeClass("alt");

			$content.find("#ruleInfo").text($.trim(ruleName));

			if(list == null || (list.length==0 && base.options.ruleXml == null && base.XML_SOURCE === sourceData)){
				$tr = $content.find("tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td:not(#itemPosition)").remove();
				$tr.find("td#itemPosition").attr("colspan", "6").html("Unable to find data for this rule. Please contact Search Manager Team.");
				$tr.appendTo($table);
			}else if (list.length==0){
				$tr = $content.find("tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td:not(#itemPosition)").remove();
				$tr.find("td#itemPosition").attr("colspan", "6").html("No item specified for this rule");
				$tr.appendTo($table);
			}else{
				for (var i = 0; i < list.length; i++) {
					memberIds.push(list[i]["memberId"]);
					base.memberIdToItem[list[i]["memberId"]] = $.makeArray(list[i]);

					var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["memberId"])).show();	
					$tr.find("td#itemPosition").html(ruleType.toLowerCase()!=="exclude"?  list[i]["location"] : parseInt(i) + 1);

					var PART_NUMBER = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "PART_NUMBER";
					var FACET = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "FACET";

					var formattedExpiryDate = $.isNotBlank(list[i]["expiryDate"])? $.toStoreFormat(list[i]["expiryDate"],GLOBAL_storeDateFormat) + "<br/>" +  list[i]["validityText"]: "";

					if(FACET){
						memberConditions.push(list[i].condition["conditionForSolr"]);
						base.setImage($tr,list[i]);
						$tr.find("td#itemMan").text(list[i].condition["readableString"])
						.prop("colspan",3)
						.removeClass("txtAC")
						.addClass("txtAL")
						.attr("width", "363px");

						$tr.find("#itemValidity").html(formattedExpiryDate); 

						if ($.isBlank(list[i]["isExpired"])){
							$tr.find("#itemValidityDaysExpired").remove();
						}

						$tr.find("td#itemDPNo,td#itemName").remove();
					}
					else if(PART_NUMBER){
						memberConditions.push("EDP:"+list[i]["edp"]);
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

						$tr.find("#itemValidity").html(formattedExpiryDate);
						if ($.isBlank(list[i]["isExpired"])){
							$tr.find("#itemValidityDaysExpired").remove();
						}
					}

					$tr.appendTo($table);
				};

				if (ruleType.toLowerCase() === "elevate" && memberIds.length>0){ 
					if(base.XML_SOURCE === sourceData){
						base.options.itemXmlForceAddStatusCallback(base, $content, ruleName, memberIds, memberConditions, base.memberIdToItem);
					}
					else if(base.DATABASE_SOURCE === sourceData){
						base.options.itemForceAddStatusCallback(base, $content, ruleName, memberIds, base.memberIdToItem);
					}
				}
			}

			// Alternate row style
			$content.find("tr#itemPattern").hide();
			$content.find("tr:not(#itemPattern):even").addClass("alt");
		};
		
		base.sectionTemplate = function() {
			var html = '';
			
			html +=	'					<table id="sectionTemplate" class="sectionTable pad5">';
			html +=	'						<tr>';
			html +=	'							<td class="pad1 accordionHeader" valign="bottom">';
			html +=	'								<div class="floatL marT5 sectionName">Dynamic Section</div>';
			html +=	'								<div class="floatR preloader padT9 padB10" style="display:none;">'+base.options.rectLoader+'</div>';
			html +=	'							</td>';
			html +=	'						</tr>';
			html +=	'						<tr>';
			html +=	'							<td nowrap>';
			html +=	'								<div class="productList w580" style="overflow-x:auto; overflow-y:hidden; white-space:nowrap; vertical-align:top"></div>';
			html +=	'							</td>';
			html +=	'						</tr>';
			html +=	'					</table>';
			
			return html;
		};

		base.getDatabaseData = function($content, ruleId, ruleName){
			var sourceData = base.DATABASE_SOURCE;
			var ruleType = base.options.ruleType;
			//var ruleName = base.options.ruleName;

			switch(ruleType.toLowerCase()){
			case "elevate": 
				ElevateServiceJS.getAllElevatedProductsIgnoreKeyword(ruleId, 0, 0,{
					callback: function(data){
						var list = (data.list) ? data.list : new Array();
						base.populateItemTable($content, "Elevate", list, ruleId, sourceData);
					}
				});
				break;
			case "exclude": 
				ExcludeServiceJS.getAllExcludedProductsIgnoreKeyword(ruleId , 0, 0,{
					callback: function(data){
						var list = (data.list) ? data.list : new Array();
						base.populateItemTable($content, "Exclude", list, ruleId, sourceData);
					}
				});
				break;
			case "demote": 
				DemoteServiceJS.getAllProductsIgnoreKeyword(ruleId , 0, 0,{
					callback: function(data){
						var list = (data.list) ? data.list : new Array();
						base.populateItemTable($content, "Demote", list, ruleId, sourceData);
					}
				});
				break;
			case "type-ahead": 
			case "typeahead": 
				var $table = $content.find("table#item");
				var $rulePreview = $content;

				$rulePreview.find("#ruleInfo").text(ruleName);
				$rulePreview.find("#requestType").text(base.options.requestType);
				
				TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, ruleName, 0, 1, 1, GLOBAL_storeMaxTypeahead, {
					callback:function(response) {
						var data = response['data'];
						var list = data.list;

						var $trClone = $table.find("tr#itemPattern")

						for(var i = 0; i < list.length; i++) {
							
							if(list[i].ruleName == ruleName) {
								$rulePreview.find("#rulePriority").text($.trim(list[i].priority));
								$rulePreview.find("#ruleDisabled").text(list[i].disabled);
							}
							
							var $tr = $trClone.clone();

							if(i < GLOBAL_storeKeywordMaxCategory)
								$tr.find("#category").text(list[i].ruleName);

							if(i == 0) {
								$tr.find("#category").append('<span id="count2"></span>');
								
								$tr.find("#suggestion").append('<div class="suggestionFirst"></div>');
								$tr.find("#brand").append('<div class="brandFirst"></div>')
								$tr.find("#category").append('<div class="categoryFirst"></div>')
								
								$tr.show();
								$table.append($tr);

								var $sectionContainer = $content.find('div#sectionTableContainer');
								
								$sectionContainer.typeaheadaddsection({moduleName:"Typeahead", editable:false, sectionTableTemplate : base.sectionTemplate(), accordion: true});

								base.typeaheadManager.store.addByValue('q', $.trim(list[i].ruleName)); //AjaxSolr.Parameter.escapeValue(value.trim())
								base.typeaheadManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
								base.typeaheadManager.store.addByValue('fl', 'Name,ImagePath_2,EDP');
								base.typeaheadManager.store.addByValue('storeAlias', GLOBAL_storeId);
								base.typeaheadManager.store.addByValue('fl', 'Manufacturer,Name,ImagePath_2');
								base.typeaheadManager.store.addByValue('facet', 'true');
								base.typeaheadManager.store.addByValue('facet.field', 'Manufacturer');
								base.typeaheadManager.store.addByValue('facet.mincount', 1);
								base.typeaheadManager.store.addByValue('facet.field', 'Category');
								base.typeaheadManager.store.addByValue('facet.field', GLOBAL_storeFacetTemplateName); 
								base.typeaheadManager.store.addByValue('divCount', 'countDiv2');
								base.typeaheadManager['countDiv2'] = $tr.find("#category").find('span#count2');
								
								for(name in GLOBAL_typeaheadSolrParams) {
									base.typeaheadManager.store.addByValue(name, GLOBAL_typeaheadSolrParams[name]);
								}
								base.typeaheadManager.doRequest(0);
								
							} else {

								$tr.show();
								$table.append($tr);
							}
						}
					},
					preHook : function () {
						$table.find("#preloader").parent().parent().show();
					},
					postHook: function() {
						$table.find("#preloader").parent().parent().hide();
					}
				});
				break;
			case "facetsort": 
				var $table = $content.find("table#item");
				var $ruleInfo = $content.find("#ruleInfo");
				$table.find("tr:not(#itemPattern)").remove();

				FacetSortServiceJS.getRuleByName(GLOBAL_storeId, ruleName, {
					callback: function(data){
						$table.find("tr:not(#itemPattern)").remove();
						$table.find("tr").removeClass("alt");
						if(data == null){
							$ruleInfo.text(ruleName);

							var $tr = $table.find("tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("#itemName").html("No items specified for this rule.").attr("colspan","3");
							$tr.find("td#itemHighlightedItem, td#itemSortType").remove();
							$table.append($tr);
						}
						else{
							$ruleInfo.text(data.name);
							$content.find("#ruleType").text(data.ruleType.toLowerCase());

							if(data.items && data.items.length == 0){
								var $tr = $table.find("tr#itemPattern").clone();
								$tr.find("#itemName").html("No items specified for this rule.").attr("colspan","3");
								$table.append($tr);
							}
							else{
								var $categoryTR = null;
								var $manufacturerTR = null;

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

									facetName == 'Category' && ($categoryTR = $tr);
									facetName == 'Manufacturer' && ($manufacturerTR = $tr);
									$tr.show();
								}

								$categoryTR && $table.append($categoryTR);
								$manufacturerTR && $table.append($manufacturerTR);
							}
						}						
					}
				});
				break;
			case "querycleaning": 
				var $table = $content.find("div.ruleFilter table#item");
				$table.find("tr:not(#itemPattern)").remove();
				$content.find(".infoTabs").tabs({});
				$content.find("div.ruleFilter table#itemHeader th#fieldNameHeader").html("#");
				$content.find("div.ruleFilter table#itemHeader th#fieldValueHeader").html("Rule Filter");
				$content.find("div.ruleChange > #noChangeKeyword, div.ruleChange > #hasChangeKeyword").hide();

				RedirectServiceJS.getRule(ruleId, {
					callback: function(data){
						var searchTerms = null;
						$table.find("tr:not(#itemPattern)").remove();
						$table.find("tr").removeClass("alt");
						$content.find("#ruleInfo").html(ruleName);
						if(data == null){
							$content.find("#description").html("");

							$content.find("div.ruleFilter div#includeKeywordInSearchText").hide();
							$content.find("div#ruleChange > #noChangeKeyword").show();

							$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html("No filters specified for this rule").attr("colspan","2");
							$tr.find("td#fieldValue").remove();
							$tr.appendTo($table);
						}
						else{ 
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

							searchTerms = data["searchTerms"];
						}
						base.populateKeywordInRule($content, searchTerms);
					}
				});

				break;
			case "rankingrule": 
				var $table = $content.find("div.ruleField table#item");
				$content.find(".infoTabs").tabs({});
				$table.find("tr:not(#itemPattern)").remove();
				RelevancyServiceJS.getRule(ruleId, {
					callback: function(data){
						var relKeyword = null;

						$table.find("tr:not(#itemPattern)").remove();
						$table.find("tr").removeClass("alt");
						$content.find("#ruleInfo").html(ruleName);

						if(data == null){
							$content.find("#startDate").html("");
							$content.find("#endDate").html("");
							$content.find("#description").html("");
						}
						else{
							$content.find("#ruleInfo").html(data["ruleName"]);
							$content.find("#startDate").html($.toStoreFormat(data["startDate"], GLOBAL_storeDateFormat));
							$content.find("#endDate").html($.toStoreFormat(data["endDate"], GLOBAL_storeDateFormat));
							$content.find("#description").html(data["description"]);

							relKeyword = base.toStringArray(data["relKeyword"]);
						}

						if(data == null){
							$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html("No parameters found for this rule").attr("colspan","2");
							$tr.find("td#fieldValue").remove();
							$tr.appendTo($table);
						}
						else if(data.parameters.length==0){
							$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html("No parameters specified for this rule").attr("colspan","2");
							$tr.find("td#fieldValue").remove();
							$tr.appendTo($table);
						}
						else{
							for(var field in data.parameters){
								$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
								$tr.find("td#fieldName").html(field);
								$tr.find("td#fieldValue").html(data.parameters[field]);
								$tr.appendTo($table);
							}
						}

						$table.find("tr:even").addClass("alt");
						base.populateKeywordInRule($content, relKeyword);
					}
				});
				break;
			}
		};

		base.getRuleData = function($content){
			var sourceData = base.XML_SOURCE;
			var products = (base.options.ruleXml) ? base.options.ruleXml["products"] : new Array();
			var ruleType = base.options.ruleType;
			var ruleId = base.options.ruleId;

			switch(ruleType.toLowerCase()){
			case "elevate":
				base.populateItemTable($content, "Elevate", products, ruleId, sourceData);
				break;
			case "exclude": 
				base.populateItemTable($content, "Exclude", products, ruleId, sourceData);
				break;
			case "demote": 
				base.populateItemTable($content, "Demote", products, ruleId, sourceData);
				break;
			case "type-ahead":
			case "typeahead":

				var $table = $content.find("table#item");
				var $ruleInfo = $content.find("#ruleInfo");
				var $ruleType = $content.find("#ruleType");
				var xml = base.options.ruleXml;

				$ruleInfo.text($.trim(xml.ruleName));
				$ruleType.text($.trim(xml.ruleEntity));
				$content.find("#requestType").text(base.options.requestType);
				
				$content.find("#rulePriority").text($.trim(xml.priority));
				$content.find("#ruleDisabled").text(xml.disabled);

				TypeaheadRuleServiceJS.getAllRules(xml.store, xml.ruleName, 0, 1, 1, GLOBAL_storeMaxTypeahead, {
					callback:function(response) {
						var data = response['data'];
						var list = data.list;

						var $trClone = $table.find("tr#itemPattern");

						for(var i = 0; i < list.length; i++) {
							var $tr = $trClone.clone();

							$tr.find("#suggestion").attr("id", "suggestionSource");
							$tr.find("#brand").attr("id", "brandSource");
							$tr.find("#category").attr("id", "categorySource");
							
							if(i < GLOBAL_storeKeywordMaxCategory) {
								$tr.find("#categorySource").text(list[i].ruleName);
							}

							if(i == 0) {
								$tr.find("#categorySource").append('<span id="count"></span>');
								
								$tr.find("#suggestionSource").append('<div class="suggestionSourceFirst"></div>');
								$tr.find("#brandSource").append('<div class="brandSourceFirst"></div>');
								$tr.find("#categorySource").append('<div class="categorySourceFirst"></div>');

								$tr.show();
								$table.append($tr);
								
								var $sectionContainer = $content.find('div#sectionTableContainer');
								
								$sectionContainer.typeaheadaddsection({moduleName:"Typeahead", editable:false, sectionTableTemplate : base.sectionTemplate(), accordion: true});

								base.typeaheadSourceManager.store.addByValue('q', $.trim(list[i].ruleName)); //AjaxSolr.Parameter.escapeValue(value.trim())
								base.typeaheadSourceManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
								base.typeaheadSourceManager.store.addByValue('fl', 'Name,ImagePath_2,EDP');
								base.typeaheadSourceManager.store.addByValue('storeAlias', xml.store);
								base.typeaheadSourceManager.store.addByValue('fl', 'Manufacturer,Name,ImagePath_2');
								base.typeaheadSourceManager.store.addByValue('facet', 'true');
								base.typeaheadSourceManager.store.addByValue('facet.field', 'Manufacturer');
								base.typeaheadSourceManager.store.addByValue('facet.mincount', 1);
								base.typeaheadSourceManager.store.addByValue('facet.field', 'Category');
								base.typeaheadSourceManager.store.addByValue('facet.field', GLOBAL_storeFacetTemplateName);
								base.typeaheadSourceManager.countDiv = $tr.find("#categorySource").find('span#count');
								base.typeaheadSourceManager.store.addByValue('divCount', 'countDiv');
								
								for(name in GLOBAL_typeaheadSolrParams) {
									base.typeaheadSourceManager.store.addByValue(name, GLOBAL_typeaheadSolrParams[name]);
								}
								base.typeaheadSourceManager.doRequest(0);
							} else {

								$tr.show();
								$table.append($tr);
							}
						}
					},
					preHook : function () {
						$table.find("#preloader").parent().parent().show();
					},
					postHook: function() {
						$table.find("#preloader").parent().parent().hide();
					}
				});

				break;
			case "facetsort": 
				var $table = $content.find("table#item");
				var $ruleInfo = $content.find("#ruleInfo");
				var xml = base.options.ruleXml;

				$ruleInfo.text(ruleId);
				if(xml == null){
					var $tr = $table.find("tr#itemPattern").clone().attr("id","item0").show();
					$tr.find("#itemName").html("No items specified for this rule.").attr("colspan","3");
					$tr.find("td#itemHighlightedItem, td#itemSortType").remove();
					$table.append($tr);
				}
				else{
					$ruleInfo.text(xml.ruleName);
					$content.find("#ruleType").text(xml.ruleType.toLowerCase());

					if(xml.groups && xml.groups.length == 0){
						var $tr = $table.find("tr#itemPattern").clone().attr("id","item0").show();
						$tr.find("#itemName").html("No items specified for this rule.").attr("colspan","3");
						$tr.find("td#itemHighlightedItem, td#itemSortType").remove();
						$table.append($tr);
					}
					else{
						for(var index in xml.groups){
							var facetGroup = xml.groups[index];
							var facetName = facetGroup["groupName"];
							var highlightedItems = "";
							var $tr = $table.find("tr#itemPattern").clone();
							$tr.prop({id: $.formatAsId(facetName)});
							$tr.find("#itemName").text(facetName);

							var facetGroupItems = facetGroup["groupItem"];

							if($.isArray(facetGroupItems)){
								for(var i=0; i < facetGroupItems.length; i++){
									highlightedItems += (i+1) + ' - ' + facetGroupItems[i] + '<br/>';
								}
							}
							$tr.find("#itemHighlightedItem").html(highlightedItems);

							var sortTypeDisplay = "";
							var sortType = facetGroup["sortType"] == null ? xml.sortType : facetGroup["sortType"];

							switch(sortType){
							case "ASC_ALPHABETICALLY": sortTypeDisplay = "A-Z"; break;
							case "DESC_ALPHABETICALLY": sortTypeDisplay = "Z-A"; break;
							case "ASC_COUNT": sortTypeDisplay = "Count Asc"; break;
							case "DESC_COUNT": sortTypeDisplay = "Count Desc"; break;
							}

							$tr.find("#itemSortType").text(sortTypeDisplay);
							$tr.show();
							$table.append($tr);
						}
					}
				}			
				break;
			case "querycleaning": 
				$content.find(".infoTabs").tabs({});

				$content.find("div.ruleFilter table#itemHeader th#fieldNameHeader").html("#");
				$content.find("div.ruleFilter table#itemHeader th#fieldValueHeader").html("Rule Filter");
				$content.find("div.ruleChange > #noChangeKeyword, div.ruleChange > #hasChangeKeyword").hide();

				var xml = base.options.ruleXml;

				var $table = $content.find("div.ruleFilter table#item");
				$table.find("tr:not(#itemPattern)").remove();
				$table.find("tr").removeClass("alt");

				if($.isBlank(xml["ruleCondition"]["ruleCondition"])){
					$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item0").show();
					$tr.find("td#fieldName").html("No filters specified for this rule").attr("colspan","2");
					$tr.find("td#fieldValue").remove();
					$tr.appendTo($table);

				}else{
					for(var field in xml["ruleCondition"]["ruleCondition"]){
						$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item" + $.formatAsId(field)).show();
						$tr.find("td#fieldName").html(parseInt(field)+1);
						$tr.find("td#fieldValue").text(xml["ruleCondition"]["ruleCondition"][field].readableString);
						$tr.appendTo($table);
					}	
				}

				$table.find("tr:even").addClass("alt");
				$content.find("#ruleInfo").html(xml["ruleName"]);
				$content.find("#description").html(xml["description"]);

				switch (xml["redirectType"]) {
				case "FILTER":
					$content.find("#redirectType").html("Filter");
					break;
				case "CHANGE_KEYWORD":
					$content.find("#redirectType").html("Replace Keyword");
					break;
				case "DIRECT_HIT":
					$content.find("#redirectType").html("Direct Hit");
					break;
				default:
					$content.find("#redirectType").html("");
				break;									
				}

				if ($.isNotBlank(xml["replacementKeyword"])){
					$content.find("div#ruleChange").find("#replaceKeywordVal").html(xml["replacementKeyword"]);
				}

				$content.find("div#ruleChange").find("#searchHeaderTextOpt").text(
						xml["replaceKeywordMessageType"]["intValue"] == 3 ? 
								xml["replaceKeywordMessageCustomText"] :
									xml["replaceKeywordMessageType"]["description"]
				);

				var includeKeywordText = "Include keyword in search: <b>NO</b>";
				if($.isNotBlank(xml["includeKeyword"])){
					includeKeywordText = "Include keyword in search: ";
					if(xml["includeKeyword"]){
						includeKeywordText += "<b>YES</b>";
					}
					else{
						includeKeywordText += "<b>NO</b>";
					}
				}
				$content.find("div.ruleFilter div#includeKeywordInSearchText").show();
				$content.find("div.ruleFilter div#includeKeywordInSearchText").html(includeKeywordText);

				base.populateKeywordInRule($content, xml["ruleKeyword"]["keyword"]);

				break;
			case "rankingrule": 
				$content.find(".infoTabs").tabs({});

				var xml = base.options.ruleXml;

				$content.find("#ruleInfo").html(xml["ruleName"]);
				$content.find("#startDate").html($.toStoreFormat(xml["startDate"]));
				$content.find("#endDate").html($.toStoreFormat(xml["endDate"]));
				$content.find("#description").html(xml["description"]);

				var $table = $content.find("div.ruleField table#item");
				$table.find("tr:not(#itemPattern)").remove();
				$table.find("tr").removeClass("alt");

				for(var field in xml.parameters){
					$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
					$tr.find("td#fieldName").html(field);
					$tr.find("td#fieldValue").html(xml.parameters[field]);
					$tr.appendTo($table);
				}	

				$table.find("tr:even").addClass("alt");

				base.populateKeywordInRule($content, xml["ruleKeyword"]["keyword"]);

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
			$table.find("tr").removeClass("alt");

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
			var template = '';

			if (base.options.enablePreTemplate && $.isBlank(base.options.preTemplate)){
				switch(base.options.ruleType.toLowerCase()){
				case "elevate": 
				case "exclude":
				case "demote":
					template  = '<div class="rulePreview w600">';
					//template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
					template += '	<label class="w110 floatL fbold">Rule Name:</label>';
					template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
					template += '	<div class="clearB"></div>';

					template += '</div>';
					template += '<div class="clearB"></div>';
					break;
				case "facetsort":
					template  = '<div class="rulePreview w600">';
					template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
					template += '	<label class="w110 floatL fbold">Rule Name:</label>';
					template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL marL20 fbold">Rule Type:</label>';
					template += '	<label class="wAuto floatL" id="ruleType"></label>';					
					template += '	<div class="clearB"></div>';
					template += '</div>';
					template += '<div class="clearB"></div>';
					break;
				case "querycleaning":
					template  = '<div class="rulePreview w600 marB20">';
					template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
					template += '	<label class="w110 floatL fbold">Rule Name:</label>';
					template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL marL20 fbold">Description:</label>';
					template += '	<label class="wAuto floatL" id="description">';
					template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '	</label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL marL20 fbold">Active Type:</label>';
					template += '	<label class="wAuto floatL" id="redirectType">';
					template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '	</label>';
					template += '	<div class="clearB"></div>';							
					template += '</div>';
					break;
				case "rankingrule":
					template  = '<div class="rulePreview w600 marB20">';
					template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
					template += '	<label class="w110 floatL fbold">Rule Name:</label>';
					template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL marL20 fbold">Start Date:</label>';
					template += '	<label class="wAuto floatL" id="startDate">';
					template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '	</label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL marL20 fbold">End Date:</label>';
					template += '	<label class="wAuto floatL" id="endDate">';
					template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '	</label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL marL20 fbold">Description:</label>';
					template += '	<label class="wAuto floatL" id="description">';
					template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
					template += '	</label>';
					template += '	<div class="clearB"></div>';					
					template += '</div>';
					break;
				case 'typeahead':
					template  = '<div class="rulePreview w600">';
					//template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
					template += '	<label class="w110 floatL fbold">Rule Name:</label>';
					template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
					template += '	<div class="clearB"></div>';

					template += '	<label class="w110 floatL fbold">Priority:</label>';
					template += '	<label class="wAuto floatL" id="rulePriority"></label>';
					template += '	<div class="clearB"></div>';
					
					template += '	<label class="w110 floatL fbold">Disabled:</label>';
					template += '	<label class="wAuto floatL" id="ruleDisabled"></label>';
					template += '	<div class="clearB"></div>';
					
					template += '</div>';
					template += '<div class="clearB"></div>';
					break;
				default: template = '';
				}
			}
			else if(base.options.enablePreTemplate && $.isNotBlank(base.options.preTemplate)){
				template = base.options.preTemplate;
			}

			return template;
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
				template += '	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;border-bottom: 1px solid #ccc;">';
				template += '		<table id="item" class="tblItems w100p" style="border-bottom:none;">';
				template += '			<tbody>';
				template += '				<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '					<td width="20px" class="txtAC" id="itemPosition" style="border-bottom:none;"></td>';
				template += '					<td width="60px" class="txtAC" id="itemImage" style="border-bottom:none;"><img src="" width="50"/></td>';
				template += '					<td width="94px" class="txtAC" id="itemMan" style="border-bottom:none;"></td>';
				template += '					<td width="70px" class="txtAC" id="itemDPNo" style="border-bottom:none;"></td>';
				template += '					<td width="160px" class="txtAC" id="itemName" style="border-bottom:none;"></td>';
				template += '					<td width="auto" class="txtAC">';
				template += '						<div id="itemValidity" class="w74 wordwrap"></div>';
				template += '						<div id="itemValidityDaysExpired"><img src="' + GLOBAL_contextPath + '/images/expired_stamp50x16.png"></div>';
				template +='					</td>';
				template += '				</tr>';
				template += '				<tr>';
				template += '					<td colspan="6" class="txtAC" style="border-bottom:none;">';
				template += '						<img id="preloader" alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';	
				template += '					</td>';
				template += '				</tr>';
				template += '			</tbody>';
				template += '		</table>';
				template += '</div>';
				break;
			case "facetsort":
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
				template += '	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;border-bottom: 1px solid #ccc;">';
				template += '		<table id="item" class="tblItems w100p" style="border-bottom:none;">';
				template += '			<tbody>';
				template += '				<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '					<td width="60px" class="txtAC" id="itemName" style="border-bottom:none;"></td>';
				template += '					<td width="84px" class="txtAL" id="itemHighlightedItem" style="border-bottom:none;"></td>';
				template += '					<td width="50px" class="txtAC" id="itemSortType" style="border-bottom:none;"></td>';
				template += '				</tr>';
				template += '			</tbody>';
				template += '		</table>';
				template += '	</div>';
				break;
			case "rankingrule": 


				template += '	<div id="rankingSummary" class="infoTabs marB20 tabs">';
				template += '		<ul class="posRel top5" style="z-index:100">';

				if (base.options.ruleId.toLowerCase()!== (GLOBAL_storeId.toLowerCase()+ "_default")){
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
				template += '				<table id="item" style="border-collapse:collapse; border-bottom:none;" class="tblItems w100p marB10">';
				template += '					<tbody>';
				template += '						<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '							<td class="txtAC w70" id="fieldName" style="border-bottom:none;"></td>';
				template += '							<td id="fieldValue" class="wAuto" style="border-bottom:none;"></td>';
				template += '						</tr>';
				template += '						<tr>';
				template += '							<td colspan="2" class="itemRow txtAC" style="border-bottom:none;">';
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
					template += '				<table id="item" style="border-collapse:collapse; border-bottom:none;" class="tblItems w100p marB10">';
					template += '					<tbody>';
					template += '						<tr id="itemPattern" class="itemRow" style="display: none">';
					template += '							<td class="txtAC w70" id="fieldName" style="border-bottom:none;"></td>';
					template += '							<td id="fieldValue" class="wAuto" style="border-bottom:none;"></td>';
					template += '						</tr>';
					template += '						<tr>';
					template += '							<td colspan="2" class="itemRow txtAC" style="border-bottom:none;">';
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
				template += '<div id="rankingSummary" class="infoTabs marB20 tabs">';

				template += '	<ul class="posRel top5" style="z-index:100">';
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
				template += '			<table id="item" style="border-collapse:collapse; border-bottom:none;" class="tblItems w100p marB10">';
				template += '				<tbody>';
				template += '					<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '						<td class="txtAC w70" id="fieldName" style="border-bottom:none;"></td>';
				template += '						<td id="fieldValue" class="wAuto" style="border-bottom:none;"></td>';
				template += '					</tr>';
				template += '					<tr>';
				template += '						<td colspan="2" class="itemRow txtAC" style="border-bottom:none;">';
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
				template += '			<table id="item" style="border-collapse:collapse;border-bottom:none;" class="tblItems w100p marB10">';
				template += '				<tbody>';
				template += '					<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '						<td class="txtAC w70" id="fieldName" style="border-bottom:none;"></td>';
				template += '						<td id="fieldValue" class="wAuto" style="border-bottom:none;"></td>';
				template += '					</tr>';
				template += '					<tr>';
				template += '						<td colspan="2" class="itemRow txtAC" style="border-bottom:none;">';
				template += '							<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">	';
				template += '						</td>';
				template += '					</tr>';
				template += '				</tbody>';
				template += '			</table>';
				template += '		</div>	';
				template += '	</div>';
				break;
			case 'type-ahead' :
			case 'typeahead' :
				template += '<div id="forceAdd" class="loadingWrapper" style="display:none"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-circ16x16.gif"><span class="fsize12 posRel topn3 padL5">Retrieving Force Add Status</span></div>';
				template += '	<div class="w600 mar0 pad0">';
				template += '		<table class="tblItems w100p marT5">';
				template += '			<tbody>';
				template += '				<tr>';
				template += '					<th width="33%">Categories</th>';
				template += '					<th width="33%" id="selectAll">Brands</th>';
				template += '					<th width="34%">Suggestions</th>';
				template += '				</tr>';
				template += '			<tbody>';
				template += '		</table>';
				template += '	</div>';
				template += '	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;border-bottom: 1px solid #ccc;">';
				template += '		<table id="item" class="tblItems w100p" style="border-bottom:none;">';
				template += '			<tbody>';
				template += '				<tr id="itemPattern" class="itemRow" style="display: none">';
				template += '					<td width="33%" class="txtAL valignTop" id="category" style="border-bottom:none;"></td>';
				template += '					<td width="33%" class="txtAL valignTop" id="brand" style="border-bottom:none;"></td>';
				template += '					<td width="34%" class="txtAL valignTop" id="suggestion" style="border-bottom:none;"></td>';
				template += '				</tr>';
				template += '				<tr style="display:none;">';
				template += '					<td colspan="6" class="txtAC" style="border-bottom:none;">';
				template += '						<img id="preloader" alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';	
				template += '					</td>';
				template += '				</tr>';
				template += '			</tbody>';
				template += '		</table>';
				template += '</div>';
				template += '		<div id="sectionTableContainer">';
				template += '			<table id="section" class="marT15 marB10">';
				template += '				<tr>';
				template += '					<td>';
				template += '						<div id="sectionBox">';
				template += '						</div>';
				template += '					</td>';
				template += '				</tr>';
				template += '			</table>';
				template += '		</div>';
				break;
			}

			return template;
		};

		base.getPostTemplate = function(){
			var template = '';

			if (base.options.enablePostTemplate && $.isBlank(base.options.postTemplate)){
				template  = '<div id="actionBtn" class="floatR fsize12 border pad5 w580 marB20" style="background: #f3f3f3;">';
				template += '	<h3 class="padL15" style="border:none">Approval Guidelines</h3>';
				template += '	<div class="fgray padL15 padR12 padB15 fsize11">';
				template += '		<p align="justify">';
				template += '			Before approving any rule, it is advisable to review rule details.<br/><br/>';
				template += '			If the rule is ready to be pushed to production, click on <strong>Approve</strong>.';
				template += '			If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.';
				template += '		<p>';
				template += '	</div>';
				template += '	<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>';
				template += '	<label class="floatL w480"><textarea id="comment" rows="5" class="w460" style="height:32px"></textarea></label>';
				template += '	<div class="clearB"></div>';
				template += '	<div id="btnHolder" align="right" class="padR15 marT10" style="display:none">';
				template += '		<a id="okBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template += '			<div class="buttons fontBold">Approve</div>';
				template += '		</a>';
				template += '		<a id="rejectBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template += '			<div class="buttons fontBold">Reject</div>';
				template += '		</a>';
				template += '	</div>';
				template += '</div>';
			}
			else if(base.options.enablePostTemplate && $.isNotBlank(base.options.postTemplate)){
				template = base.options.postTemplate;
			}

			return template;
		};

		base.getRightPreTemplate = function(){
			var template = '';

			if (base.options.enableRightPanel && $.isBlank(base.options.rightPanelTemplate)){
				//TODO
				template  = '	<div class="w280 floatR border" style="height:500px">';
				template += '		<div> lorem ipsum dolor sit amet </div>';
				template += '	</div>';
			}
			else if(base.options.enableRightPanel && $.isNotBlank(base.options.rightPanelTemplate)){
				template = base.options.rightPanelTemplate;
			}

			return template;
		};

		base.showLeftPane = function(ruleId, ruleType){
			var $div = $('<div id="leftPreview" class="floatL pad5"></div>');
			$div.append(base.getPreTemplate());
			$div.append(base.getTemplate());
			$div.append(base.getPostTemplate());

			return $div;
		};

		base.showRightPane = function(ruleId, ruleType){
			if(base.options.enableRightPanel){
				var $div = $('<div id="rightPreview" class="floatR pad5"></div>');

				$div.append(base.getRightPreTemplate());
				$div.append(base.getTemplate());

				return $div;
			}

			return '';
		};

		base.buttonHandler = function(elem) {
			elem.off("click mouseenter").on({
				click: function(evt){
					switch($(evt.currentTarget).attr("id")){
					case 'setImportBtn':
						var importAsLabel = base.contentHolder.find("#rightPreview > div.rulePreview > div#importAs");
						var importAs = importAsLabel.find("select#importAsSelect > option:selected").val();
						var newName = importAsLabel.find("div#replacement input#newName").val();
						var opt = base.contentHolder.find("#leftPreview > div.rulePreview > label#importType > select#importType > option:selected").val();

						base.options.changeImportAsCallback(base, base.options.ruleId, importAs, base.options.ruleName, newName);
						base.options.changeImportTypeCallback(base, base.options.ruleId, opt);
						base.options.checkUncheckCheckboxCallback(base, base.options.ruleId, 'import');
						base.api.hide();
						break;
					case 'setRejectBtn':
						base.options.checkUncheckCheckboxCallback(base, base.options.ruleId, 'reject');
						base.api.hide();
						break;
					}
				}
			});
		};

		base.showQtipPreview = function(){
			base.$el.qtip({
				content: {
					text: $('<div/>'),
					title: { 
						text: base.options.headerText, 
						button: true
					}
				},
				position:{
					at: 'center',
					my: 'center',
					target: $(window)
				},
				show:{
					ready: true,
					modal: true
				},
				style: {
					width: 'auto',
					position: 'fixed'
				},
				events: { 
					render: function(event, api) {
						$(this).css('position', 'fixed');
					},
					show: function(event, api){
						base.contentHolder = $("div", api.elements.content);
						base.api = api;

						//left pane is shown by default
						base.contentHolder.append(base.showLeftPane());

						switch(base.options.leftPanelSourceData){
						case base.XML_SOURCE: 
							if(base.options.ruleXml != null){
								base.getRuleData(base.contentHolder.find("#leftPreview"));
							}else{
								base.options.itemGetRuleXmlCallback(base, base.contentHolder.find("#leftPreview"), base.options.ruleType, base.options.ruleId, base.options.leftPanelSourceData);
							}
							break;
						case base.DATABASE_SOURCE:
							base.getDatabaseData(base.contentHolder.find("#leftPreview"), base.options.dbRuleId, base.options.ruleName);
							break;
						default: break;
						}				
						base.options.itemImportTypeListCallback(base, base.contentHolder.find("#leftPreview"));

						if(base.options.enableRightPanel){
							base.contentHolder.append(base.showRightPane());

							switch(base.options.rightPanelSourceData){
							case base.XML_SOURCE:
								if(base.options.ruleXml != null){
									base.getRuleData(base.contentHolder.find("#rightPreview"));
								}
								else{ //if ruleXml is null, try to retrieve it
									base.options.itemGetRuleXmlCallback(base, base.contentHolder.find("#rightPreview"), base.options.ruleType, base.options.ruleId, base.options.rightPanelSourceData);
								}
								break;
							case base.DATABASE_SOURCE:
								if($.isBlank(base.options.dbRuleId)){ //if dbRuleId is blank, selected option is "Import As New Rule", display preview of ruleXml

									switch(base.options.ruleType.toLowerCase()){ //do only if ruleType is either Query Cleaning or Ranking Rule
									case "querycleaning":
									case "rankingrule":
										base.getDatabaseData(base.contentHolder.find("#rightPreview"));
										break;
//									case "typeahead":
//									case "type-ahead":
//										base.getDatabaseData(base.contentHolder.find("#rightPreview"), base.options.rule.id, base.options.rule.ruleName);
//										break;
									default: break;
									}
								}
								else{ //if dbRuleId is not blank, display preview of rule from database
									base.getDatabaseData(base.contentHolder.find("#rightPreview"), base.options.dbRuleId, base.options.ruleName);
								}
								break;
							default: break;
							}
							base.options.itemImportAsListCallback(base, base.contentHolder, base.options.rightPanelSourceData);
						}

						base.contentHolder.find("a#okBtn, a#rejectBtn").off().on({
							click: function(evt){
								var comment= $.defaultIfBlank($.trim(base.contentHolder.find("#comment").val()), "");
								if(!validateComment(base.options.transferType, comment, 1, 250)){
									//error message in validateComment
								}else{
									comment = comment.replace(/\n\r?/g, '<br/>');
									switch($(evt.currentTarget).attr("id")){
									case "okBtn": 
										switch(base.options.transferType.toLowerCase()){
										case "export": 
											RuleTransferServiceJS.exportRule(GLOBAL_storeId, base.options.ruleType, $.makeArray(base.options.ruleId), comment, {
												callback: function(data){									
													base.api.hide();
													showActionResponseFromMap(data, "export", base.options.transferType,
													"Unable to find published data for this rule. Please contact Search Manager Team.");
												},
												postHook: function(){
													base.options.postButtonClick(base);
												}
											});
											break;
										case "import":
											setTimeout(function() {
												var importAsLabel = base.contentHolder.find("#rightPreview > div.rulePreview > div#importAs");
												var importAs = importAsLabel.find("select#importAsSelect").children("option:selected").val();
												var ruleName = importAsLabel.find("input#newName").val();

												var importType = base.contentHolder.find("#leftPreview > div.rulePreview > label#importType > select#importType").children("option:selected").text();

												if($.isBlank(ruleName)){
													jAlert("Please add Import As rule name.", base.options.transferType);	
												}
												else{
													RuleTransferServiceJS.importRules(base.options.ruleType, $.makeArray(base.options.ruleId), comment, $.makeArray(importType), $.makeArray(importAs), $.makeArray(ruleName), {
														callback: function(data){									
															base.api.hide();
															showActionResponseFromMap(data, "import", base.options.transferType,
															"Unable to find published data for this rule. Please contact Search Manager Team.");
														},
														postHook: function(){
															base.options.postButtonClick(base);
														}	
													});
												}
											}, 500);
											break;
										}
										break;

									case "rejectBtn": 
										switch(base.options.transferType.toLowerCase()){
										case "export": 
											break;
										case "import": 
											var ruleName = base.options.ruleName;

											RuleTransferServiceJS.unimportRules(base.options.ruleType, $.makeArray(base.options.ruleId), comment, $.makeArray(ruleName),{
												callback: function(data){
													base.api.hide();
													showActionResponseFromMap(data, "reject", base.options.transferType,
													"Unable to find published data for this rule. Please contact Search Manager Team.");
												},
												postHook: function(){
													base.options.postButtonClick(base);
												}
											});
											break;
										}
										break;
									}	
								}

							}
						});
					},

					hide:function(event, api){
						base.api.destroy();
					}
				}
			});
		};

		// Run initializer
		base.init();
	};

	$.xmlpreview.defaultOptions = {
			headerText:"Rule Preview",
			transferType: "",
			ruleType: "",
			ruleId: "",	//xml id
			ruleName: "",
			ruleInfo: "",
			ruleXml: null,
			rule: null,
			dbRuleId: "", //database rule Id
			requestType: "",
			version: "",
			enablePreTemplate: false,
			enablePostTemplate: false,
			enableRightPanel: false,
			leftPanelSourceData: "database",	//"database" or "xml"
			rightPanelSourceData: "database", //"database" or "xml"
			preTemplate: "",
			postTemplate: "",
			rightPanelTemplate: "",
			itemGetRuleXmlCallback: function(base, contentHolder, ruleType, ruleId, sourceData){},
			itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItem){},
			itemXmlForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberConditions, memberIdToItem){},
			itemImportTypeListCallback: function(base, contentHolder){},
			itemImportAsListCallback: function(base, contentHolder){},

			checkUncheckCheckboxCallback: function(base, ruleId, pub){},
			changeImportTypeCallback: function(base, ruleId, importType){},
			changeImportAsCallback: function(base, ruleId, importAs, ruleName, newName){},

			setSelectedOverwriteRulePreview: function(base, rulename){},
			postButtonClick: function(base){},
			ruleStatusList: null,
			ruleTransferMap: null
	};

	$.fn.xmlpreview = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.xmlpreview(this, options));
			});
		};
	};
})(jQuery);
