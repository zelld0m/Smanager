(function($){

	var FacetSort = {
			moduleName: "Typeahead",
			selectedRule:  null,
			selectedRuleStatus: null,
			maxHighlightedFacet: 5,

			initialNoOfItems: 100,
			itemsPerScroll: 100,
			startIndex: 0,
			reportType: {basic: 1, withStats: 2, custom: 3},
			latestFile: null,

			tabSelectedId: 1,
			tabSelectedName: "",
			keyword: "",
			fq: "",


			rulePage: 1,
			rulePageSize: 15,

			removeFacetGroupItemConfirmText: "Delete facet value?",

			facetFields : ["Category", "Manufacturer"],	//TODO This might be retrieved from a lookup table
			facetValueList: null,
			sortOrderList: null,
			facetGroupIdList: null,

			keywordIconPath: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_keyword.png'/>",
			templateIconPath:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_template.png'/>",

			prepareFacetSort : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #noSelected").hide();
				$("#facetsorting").hide();
				$("#titleHeader").empty();
				$("#ruleTypeIcon").html("");
			},

			showFacetSort : function(){
				var self = this;

				self.prepareFacetSort();
				self.getFacetSortRuleList(1);

				if(self.selectedRule==null){
					$("#preloader").hide();
					$("#noSelected").show();
					$("#titleText").html(self.moduleName);
					return;
				}

				$("#submitForApproval").rulestatusbar({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					ruleType: "Facet Sort",
					enableVersion: true,
					authorizeRuleBackup: allowModify,
					authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
					postRestoreCallback: function(base, rule){
						base.api.destroy();
						FacetSortServiceJS.getRuleById(self.selectedRule["ruleId"],{
							callback: function(data){
								self.setFacetSort(data);
							},
							preHook: function(){
								self.prepareFacetSort();
							}
						});
					},
					afterSubmitForApprovalRequest:function(ruleStatus){
						self.showFacetSort();
					},
					beforeRuleStatusRequest: function(){
						self.prepareFacetSort();	
					},
					afterRuleStatusRequest: function(ruleStatus){
						$("#preloader").hide();
						$("#submitForApproval").show();
						$("#titleText").html(self.moduleName + " for ");
						$("#titleHeader").text(self.selectedRule["ruleName"]);
						$("#readableString").html(self.selectedRule["readableString"]);

						switch(self.selectedRule["ruleType"].toLowerCase()){
						case "keyword":	$("#ruleTypeIcon").append(self.keywordIconPath); break;
						case "template": $("#ruleTypeIcon").append(self.templateIconPath); break;
						}

						var $facetSortOrder = $('#facetSortOrder');
						self.populateSortOrderList($facetSortOrder, self.selectedRule["sortType"]);
						self.selectedRuleStatus = ruleStatus;

						$facetSortOrder.prop({disabled: self.selectedRuleStatus["locked"] || !allowModify});

						$("#facetsorting").show();
						$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

						self.createFacetGroupTabs();
						self.addSaveRuleListener();
						self.addDeleteRuleListener();
						self.addDownloadListener();

						$('#auditIcon').off().on({
							click: function(e){
								$(e.currentTarget).viewaudit({
									itemDataCallback: function(base, page){
										AuditServiceJS.getFacetSortTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
											callback: function(data){
												var total = data.totalSize;
												base.populateList(data);
												base.addPaging(base.options.page, total);
											},
											preHook: function(){
												base.prepareList();
											}
										});
									}
								});
							}
						});
					}
				});
			},

			setFacetSort : function(rule){
				var self = this;
				self.selectedRule = rule;
				self.keyword = "";
				self.fq = "";

				if(self.selectedRule!=null && self.selectedRule["ruleType"]){
					if("KEYWORD" === self.selectedRule["ruleType"]){
						self.keyword = encodeURIComponent(self.selectedRule["ruleName"]);
					}
					else if("TEMPLATE" === self.selectedRule["ruleType"]){
						self.fq = GLOBAL_storeFacetTemplateName + ":\"" + encodeURIComponent(self.selectedRule["ruleName"]) + "\"";
					}
				}

				self.showFacetSort();
			},

			populateFacetListDropdown: function(){
				var self = this;
				var $facetDiv = $("div#facetsort div#"+self.tabSelectedId);

				var $select = $facetDiv.find("select#facetValuesPattern");
				$select.find("option").remove();
				//$select.prop({id : "_items"+self.tabSelectedId});

				if(self.facetValueList){
					var facetValues = self.facetValueList[self.tabSelectedName];

					$select.append($("<option>", {value: ""}).text("-Select " + self.tabSelectedName + "-"));
					for (var facetValue in facetValues){
						$select.append($("<option>", {value: facetValue}).text(facetValue));
					}
				}

			},

			populateTabContent : function(){
				var self = this;
				var facetTabId = self.tabSelectedId;
				var tabContainer = $("#"+facetTabId);

				//if there exists tempItems, tab is already populated, do not refresh
				if(tabContainer.find("li.tempItem").length > 0) return;

				tabContainer.show();
				if(!tabContainer.hasClass("isShown")){
					tabContainer.addClass("isShown");
				}
				tabContainer.find("div#facetvaluelist").prop({id : facetTabId +'_list'});
				tabContainer.find("span#addFacetSortTitleHeader").text("");
				tabContainer.find("span#addNewLink").text("");
				tabContainer.find("ul#selectedFacetValueList li:not(#addFacetValuePattern)").remove();

				$("#"+facetTabId+"_list").viewfacetvalues({
					headerText: "Facet Preview of " + self.tabSelectedName,
					keyword: self.keyword,
					facetField: self.tabSelectedName,
					fq: self.fq,
					afterSolrRequestCallback: function(json){
						self.facetValueList = json.facet_counts.facet_fields;

						if(GLOBAL_PCMGroup){
							self.facetValueList["Category"] = [];

							if(json.FacetTemplate){
								self.facetValueList["Category"] = json.FacetTemplate.Level1;
							}
						}

						self.populateFacetListDropdown();
						self.populateSelectedValues(tabContainer, facetTabId.split("_")[1]);
						self.addNewFacetValueListener(tabContainer, facetTabId.split("_")[1]);
						self.addSortableOption(tabContainer);

						tabContainer.find("span#addFacetSortTitleHeader").text("Highlighted " + self.tabSelectedName + " Values");
					}
				});
			},

			addSortableOption : function(contentHolder){
				var self = this;
				contentHolder.find('ul#selectedFacetValueList').sortable("destroy").sortable({
					//handle : '.handle',
					cursor : 'move',
					axis: 'y',
					tolerance: 'intersect',
					placeholder: 'placeHolder_small',
					forceHelperSize: true,
					forcePlaceholderSize: true,
					disabled: self.selectedRuleStatus["locked"] || !allowModify
				});

			},

			checkDuplicateFacet : function (e, u, facetGroupId){
				if($("div#_" + facetGroupId).hasClass("isShown")){
					var value = u.value;
					$("select#_items_"+facetGroupId).not($(e.currentTarget)).each(function() {
						if ($(this).val() === value && $.isNotBlank(value)) {
							jAlert(value + " is already selected.", self.moduleName);
							$(e.currentTarget).prop("selectedIndex", 0);
							return;
						}
					});
				}
			},
			loadSplunkData: function() {
				var self = this;
				TopKeywordServiceJS.getFileList({
					callback: function(files){
						self.latestFile = files[0];
						TopKeywordServiceJS.getFileContents(self.latestFile, {
							callback: function(data){
								var list = data.list;
								var $divList = $("div#itemList");
								$divList.find("div.items:not(#itemPattern1, #itemPattern2)").remove();
								if (list.length > 0){
									self.resetHeader();
									self.loadItems($divList, list, self.startIndex, self.initialNoOfItems);
									self.startIndex = self.initialNoOfItems;
									$divList.off().on({
										scroll: function(e){
											if(list.length > self.startIndex){
												if ($divList[0].scrollTop == $divList[0].scrollHeight - $divList[0].clientHeight) {
													self.loadItems($divList, list, self.startIndex, self.itemsPerScroll);
													self.startIndex = self.startIndex + self.itemsPerScroll;
												}
											}
										}
									},{list: list});

									$("#keywordCount").html(data.totalSize == 1 ? "1 Keyword" : data.totalSize + " Keywords");
									$("div#countSec").show();
								}else{
									$empty = '<div id="empty" class="items txtAC borderB">File selected has no records to display</div>';
									$divList.append($empty);
									$("div#countSec").hide();
								}
							},
							preHook:function(){
								$('div#preloader').show();
							},
							postHook:function(){
								$('div#preloader').hide();
							}
						});
					}}
				);
			},
			loadItems: function($divList, list, start, noOfItems, type){
				var listLen = list.length;
				var patternId;
				var isType2 = false;
				var self = this;
				if (type == this.reportType.custom) {
					patternId = "div#itemPattern";
				} else {
					isType2 = self.latestFile.indexOf("-splunk") > 0;
					patternId = isType2 ? "div#itemPattern2" : "div#itemPattern1";
				}

				for (var i=start; i < start + noOfItems ; i++){
					if(i == listLen)
						break;

					var $divItem = $divList.find(patternId).clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));
					$divItem.find("label.iter").html(parseInt(i)+1);
					$divItem.find("label.keyword").html(list[i]["keyword"]);
					$divItem.find("label.count").html(list[i]["count"]);

					if (isType2) {
						$divItem.find("label.results").html(list[i]["resultCount"]);
						$divItem.find("label.sku").html($.isNotBlank(list[i]["sku"]) ? list[i]["sku"]: "&nbsp;");
					}

					$divItem.find("a.toggle").text("Show Active Rule").on({
						click:function(data){
							var toggle = this;
							var $itm = $(toggle).parents("div.items");
							var  key = $itm.find(".keyword").html();

							if($itm.find("div.rules").is(":visible")){
								$(toggle).html("Show Active Rule");
								$itm.find("div.rules").empty().hide();
							}else{
								var $loader = $('<img id="preloader" alt="Retrieving..." src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">');
								$itm.find("div.rules").show().activerule({
									keyword: key,
									beforeRequest: function(){
										$(toggle).hide();
										$loader.insertAfter(toggle);
									},
									afterRequest: function(){
										$(toggle).show().html("Hide Active Rule");
										$(toggle).nextAll().remove();
									}
								});
							}
						}
					});

					$divItem.show();
					$divList.append($divItem);
				}

				$divList.find("div.items").removeClass("alt");
				$divList.find("div.items:even").addClass("alt");
			},

			resetHeader: function() {
				var $divHeader1 = $("div#itemHeader1");
				var $divHeader2 = $("div#itemHeader2");
				var self = this;
				if (self.latestFile.indexOf("-splunk") > 0) {
					$divHeader1.hide();
					$divHeader2.show();
				} else {
					$divHeader2.hide();
					$divHeader1.show();
				}
			},
			populateSelectedValues : function(facetDiv, facetGroupId){
				var self = this;
				var $ul = facetDiv.find("ul#selectedFacetValueList");
				$ul.find('li:not(#addFacetValuePattern)').remove();

				FacetSortServiceJS.getAllFacetGroupItem(self.selectedRule["ruleId"], facetGroupId, {
					callback: function(data){
						var facetGroupItems = data.list;

						$ul.find('li:not(#addFacetValuePattern)').remove();
						for(var index in facetGroupItems){
							var item = facetGroupItems[index];
							var itemName = item["name"];

							var $li = facetDiv.find('li#addFacetValuePattern').clone();
							$li.show();
							$li.removeClass("addFacetValuePattern");
							$li.prop({id : ""});

							var $select = $li.find("select.selectCombo");
							$select.prop({id: "_items_"+facetGroupId});

							$select.searchable({
								change: function(u, e){
									self.checkDuplicateFacet(e, u, facetGroupId);
								}
							});

							$li.find("select#_items_" + facetGroupId + " option:contains('" + itemName + "')")
							.filter(function() { return $(this).text() === itemName; })
							.prop("selected", true);

							$ul.append($li);
							self.addDeleteFacetValueListener($li);
						}
					}
				});
			},

			getSortOrderList : function(){
				var self = this;
				FacetSortServiceJS.getSortOrderList({
					callback: function(data){
						self.sortOrderList = data;
					}
				});
			},

			populateSortOrderList : function(contentHolder, selectedOrder){
				var self = this;
				contentHolder.find("option").remove();

				if($.isNotBlank(selectedOrder)){
					$.each(self.sortOrderList, function(sortName, sortDisplayText) { 
						contentHolder.append($("<option>", {value: sortDisplayText, selected: sortName===selectedOrder}).text(sortDisplayText));
					});
				}
				else{
					$.each(self.sortOrderList, function(sortName, sortDisplayText) { 
						contentHolder.append($("<option>", {value: sortDisplayText}).text(sortDisplayText));
					});
				}
			},

			populateTemplateNameList: function(contentHolder){
				var $select = contentHolder.find('select[id="popName"]');
				var count = 0;

				CategoryServiceJS.getTemplateNamesByStore(GLOBAL_storeId, {
					callback: function(data){
						var list = data;
						count = list.length;

						if(count>0)
							$select.append($("<option>", {value: ""}).text("-Select Template-"));

						for(var i=0; i<count; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook: function(){
						$select.find("option").remove();
						$select.prop("disabled", true);
					},
					postHook: function(){
						if(count>0){
							$select.prop("disabled", false).searchable({});
						}
					}
				});
			},

			getFacetSortRuleList : function(page) { 
				var self = this;

				$("#keywordSidePanel").sidepanel({
					moduleName: self.moduleName,
					fieldName: "ruleName",
					page: page,
					pageSize: self.rulePageSize,
					headerText : "Type-ahead Rule",
					customAddRule: true,
					showAddButton: allowModify,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, ruleName, page){
						self.rulePage = page;
						self.ruleFilterText = ruleName;
						TypeaheadRuleServiceJS.getAllRules(ruleName, page, base.options.pageSize, {
							callback: function(response){
								var data = response["data"];
								base.populateList(data, ruleName);
								base.addPaging(ruleName, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemAddCallback: function(base, name){

						var popName = name;
						popName = $.trim(popName.replace(/\s+(?=\s)/g,''));

						if ($.isBlank(popName)){
							jAlert("Search Keyword is required.",self.moduleName);
						}
						else if (!isAllowedName(popName)){
							jAlert("Search Keyword contains invalid value.",self.moduleName);
						}
						else {
							TypeaheadRuleServiceJS.addRule(GLOBAL_storeId, popName, {
								callback: function(response){
									if(response.status < 0) {
										jAlert(response.errorMessage.message, self.moduleName);
										self.getFacetSortRuleList(1);
									} else {
										var data = response['data'];
										if (data != null){
											showActionResponse(1, "add", popName);
											self.setFacetSort(data);
											self.getFacetSortRuleList(1);
										}

									}
								},
								preHook: function(){ 
									base.prepareList(); 
								}
							});
						}


					},

					itemNameCallback: function(base, item){
						self.setFacetSort(item.model);
					},

					itemOptionCallback: function(base, item){
						var iconPath = "";

						item.ui.find("#itemLinkValue").empty();
						switch(item.model["ruleType"].toLowerCase()){
						case "keyword": iconPath = self.keywordIconPath; break;
						case "template": iconPath = self.templateIconPath; break;
						}
						if ($.isNotBlank(iconPath)) item.ui.find(".itemIcon").html(iconPath);
					}
				});
			},

			createFacetGroupTabs : function(){
				var self = this;
				var $facetSortDiv = $("div#facetsort");
				var $ul = $facetSortDiv.find("ul#facetGroupTab"); 
				$ul.find("li:not('.facetGroupTabPattern')").remove();
				$facetSortDiv.find("div.facetTab").remove();

				self.facetGroupIdList = new Array();

				FacetSortServiceJS.getAllFacetGroup(self.selectedRule["ruleId"], {
					callback: function(data){
						var facetGroups = data.list;
						for(var index in facetGroups){
							var facetGroup = facetGroups[index];
							var facetGroupId = $.formatAsId(facetGroup["id"]);
							var $li = $ul.find("li.facetGroupTabPattern").clone();

							self.facetGroupIdList[index] = facetGroup["id"];

							$li.show();
							$li.removeClass("facetGroupTabPattern");
							$li.find("span.facetGroupName").html(facetGroup["name"]);
							$li.find("a").prop({href: "#"+facetGroupId});
							$ul.find("li.facetGroupTabPattern").before($li);

							if($facetSortDiv.find("div#"+facetGroupId)){
								$facetSortDiv.find("div#"+facetGroupId).remove();
							}

							var $facetDiv = $facetSortDiv.find("div.facetTabPattern").clone();
							var $facetSort = $facetDiv.find("select.facetGroupSortOrder");

							$facetDiv.show();
							$facetDiv.removeClass("facetTabPattern");
							$facetDiv.prop({id : facetGroupId});
							$facetDiv.addClass("facetTab");

							$facetDiv.find("input#facetGroupCheckbox").prop({checked: (facetGroup["sortType"] != null), disabled: self.selectedRuleStatus["locked"] || !allowModify }).off().on({
								click:function(e){
									if (e.data.locked) return;

									var $this = e.data.ui;
									if ($this.attr('disabled')) $this.removeAttr('disabled');
									else $this.attr('disabled', 'disabled');
								},
								mouseenter: showHoverInfo
							}, {locked: self.selectedRuleStatus["locked"] || !allowModify, ui : $facetSort});

							self.populateSortOrderList($facetSort, facetGroup["sortType"]);
							$facetSort.prop({disabled: (facetGroup["sortType"] == null || (self.selectedRuleStatus["locked"] || !allowModify))});

							$("div.facetTabPattern").before($facetDiv);
						}
					},
					postHook: function() { self.addTabListener(); }
				});
			},

			addTabListener: function(){
				var self = this;

				$("#facetsort").tabs("destroy").tabs({
					cookie: {
						expires: 0
					},
					show: function(event, ui){
						if(ui.panel){
							self.tabSelectedId = ui.panel.id;
							self.tabSelectedName = $(ui.tab).find("span.facetGroupName").text();
							self.populateTabContent();
						}
					}
				});
			},

			checkNumberOfHighlightedItems : function(content, facetGroupId){
				var self = this;
				if(content.hasClass("isShown")){
					var items = content.find("select#_items_"+facetGroupId);
					return items ? items.length : -1; //return -1 if input element not found
				}
				return -1; //div is not shown
			},

			addNewFacetValueListener : function(content, facetGroupId){
				var self = this;
				var ul = content.find('ul#selectedFacetValueList');
				var facetValues = self.facetValueList[self.tabSelectedName];

				if($.isEmptyObject(facetValues)){
					content.find("span#addNewLink").hide();
					return;
				}

				content.find("span#addNewLink").text("[add new " + self.tabSelectedName.toLowerCase() + " value]");
				content.find("a#addNewFacetValue").off().on({
					click: function(e){
						if (!e.data.locked){
							var conditionCount = self.checkNumberOfHighlightedItems(content, facetGroupId);
							if(conditionCount < 0){
								return;
							}
							if (conditionCount >= self.maxHighlightedFacet) {
								jAlert("Maximum allowed number of highlighted facet values is "+self.maxHighlightedFacet+"!",self.moduleName);
								return;
							}

							var $li = content.find('li#addFacetValuePattern').clone();

							$li.show();
							$li.removeClass("addFacetValuePattern");
							$li.addClass("tempItem");
							$li.prop({id: ""});

							ul.append($li);

							$li.find("select.selectCombo").prop({id: "_items_"+facetGroupId});
							$li.find("select.selectCombo").searchable({
								change: function(u, e){
									self.checkDuplicateFacet(e, u, facetGroupId);
								}
							});

							self.addDeleteFacetValueListener($li);
						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			addDeleteFacetValueListener : function(contentHolder){
				var self = this;

				contentHolder.find("img.delFacetValueIcon").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm(self.removeFacetGroupItemConfirmText, self.moduleName, function(result){
							if(result) contentHolder.remove();
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			buildFacetGroupItemsMap: function(){
				var self = this;

				var itemMap = new Object();

				for(var index in self.facetGroupIdList){
					var facetItems = [];

					if($("div#_" + self.facetGroupIdList[index]).hasClass("isShown")){
						var items = $("select#_items_"+self.facetGroupIdList[index]);

						for(var i = 0; i < items.length; i++){
							var itemVal = $(items[i]).find("option:gt(0):selected:eq(0)").text();
							if($.isNotBlank(itemVal) && $.inArray(itemVal, facetItems) ==-1 && isXSSSafeAllowNonAscii(itemVal)){
								facetItems.push(itemVal);
							}
						}
						itemMap[self.facetGroupIdList[index]] = facetItems;
					}
				}

				return itemMap;
			},

			buildFacetGroupSortTypeMap: function(){
				var self = this;

				var itemMap = new Object();

				for(var index in self.facetGroupIdList){
					if($("div#_" + self.facetGroupIdList[index]).hasClass("isShown")){
						var sortType = null;
						var isChecked = $("div#_"+self.facetGroupIdList[index] +" input#facetGroupCheckbox").is(":checked");

						if(isChecked){
							sortType = $("div#_"+self.facetGroupIdList[index] +" select.facetGroupSortOrder option:selected").val();
						}
						itemMap[self.facetGroupIdList[index]] = sortType;
					}
				}

				return itemMap;
			},

			addSaveRuleListener: function(){
				var self = this;
				$("#saveBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						setTimeout(function() {
							var sortType = $("select#facetSortOrder option:selected").val();
							var facetGroupItems = self.buildFacetGroupItemsMap();
							var sortOrders = self.buildFacetGroupSortTypeMap();

							var response = 0;
							FacetSortServiceJS.updateRule(self.selectedRule["ruleId"], self.selectedRule["ruleName"], sortType, facetGroupItems, sortOrders,  {
								callback: function(data){
									response = data;
									showActionResponse(data > 0 ? 1 : data, "update", self.selectedRule["ruleName"]);
								},
								preHook: function(){
									self.prepareFacetSort();
								},
								postHook: function(){
									if(response>0){
										FacetSortServiceJS.getRuleById(self.selectedRule["ruleId"],{
											callback: function(data){
												self.setFacetSort(data);
											},
											preHook: function(){
												self.prepareFacetSort();
											}
										});
									}
									else{
										self.setFacetSort(self.selectedRule);
									}

								}
							});
						}, 500 );
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			addDownloadListener: function(){
				var self = this;
				$("a#downloadIcon").download({
					headerText:"Download Facet Sort",
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;
						params["id"] = self.selectedRule["ruleId"];
						params["filename"] = e.data.filename;
						params["type"] = e.data.type;
						params["clientTimezone"] = +new Date();

						for(var key in params){
							if (count>0) urlParams +='&';
							urlParams += (key + '=' + encodeURIComponent(params[key]));
							count++;
						};

						document.location.href = url + '?' + urlParams;
					}
				});
			},

			addDeleteRuleListener: function(){
				var self = this;
				$("#deleteBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm("Delete " + self.selectedRule["ruleName"] + "'s rule?", self.moduleName, function(result){
							if(result){
								FacetSortServiceJS.deleteRule(self.selectedRule["ruleId"],{
									callback: function(code){
										showActionResponse(code, "delete", self.selectedRule["ruleName"]);
										if(code==1) {
											self.setFacetSort(null);
										}
									}
								});
							}
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			init : function() {
				var self = this;
				self.getSortOrderList();
				self.showFacetSort();
				self.loadSplunkData();
			}
	};

	$(document).ready(function() {
		FacetSort.init();
	});
})(jQuery);	
