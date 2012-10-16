(function($){

	var FacetSort = {
			moduleName: "Facet Sort",
			selectedRule:  null,
			selectedRuleStatus: null,
			maxHighlightedFacet: 5,

			tabSelectedId: 1,
			tabSelectedName: "",
			keyword: "",
			fq: "",

			rulePage: 1,
			rulePageSize: 10,
			
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
				$("#submitForApproval").hide();
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
					$("#titleText").html(self.moduleName);
					return;
				}

				$("#submitForApproval").rulestatus({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					ruleType: "Facet Sort",
					enableVersion:true,
					authorizeRuleBackup: true,
					authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
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
						$("#titleHeader").html(self.selectedRule["ruleName"]);
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
				
				if(self.selectedRule["ruleType"]){
					if("KEYWORD" === self.selectedRule["ruleType"]){
						self.keyword = self.selectedRule["ruleName"];
					}
					else if("TEMPLATE" === self.selectedRule["ruleType"]){
						self.fq = GLOBAL_storeFacetTemplateName + ":\"" + self.selectedRule["ruleName"] + "\"";
					}
				}
				
				self.showFacetSort();
			},

			populateFacetListDropdown: function(){
				var self = this;
				var $facetDiv = $("div#facetsort div#"+self.tabSelectedId);

				var $select = $facetDiv.find("select#facetValuesPattern");

				$select.find("option.not:('valuePattern')").remove();
				$select.prop({id : "_items"+self.tabSelectedId});

				if(self.facetValueList){
					var facetValues = self.facetValueList[self.tabSelectedName];

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
						
						if(GLOBAL_store === "pcmall" || GLOBAL_store === "pcmallcap" || GLOBAL_store === "sbn"){
							self.facetValueList["Category"] = json.FacetTemplate.Level1;
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
				contentHolder.find('ul#selectedFacetValueList').sortable("destroy").sortable({
					//handle : '.handle',
					cursor : 'move',
					axis: 'y',
					tolerance: 'intersect',
					placeholder: 'placeHolder_small',
					forceHelperSize: true,
					forcePlaceholderSize: true
				});
			},
			
			checkDuplicateFacet : function (target, e, u, facetGroupId){
				var self = this;
				var selectedFacetName = "";
				var itemId = $(target).attr("id");
				
				if(u.item){
					selectedFacetName = u.item.text;
					var selectedFacetMap = self.buildFacetGroupItemsMap();
					var selectedFacets = selectedFacetMap[facetGroupId];
					
					//check duplicate
					if($.isNotBlank(u.item.text)){
						if($.inArray(u.item.text, selectedFacets) >= 0){
							jAlert(selectedFacetName + " is already selected.", self.moduleName);
							// TODO set input text to empty string
						}
					}
				}
			},

			populateSelectedValues : function(facetDiv, facetGroupId){
				var self = this;
				var $ul = facetDiv.find("ul#selectedFacetValueList");

				FacetSortServiceJS.getAllFacetGroupItem(self.selectedRule["ruleId"], facetGroupId, {
					callback: function(data){
						var facetGroupItems = data.list;
						for(var index in facetGroupItems){
							var item = facetGroupItems[index];
							var itemName = item["name"];

							var $li = facetDiv.find('li#addFacetValuePattern').clone();
							$li.show();
							$li.removeClass("addFacetValuePattern");
							$li.prop({id : ""});

							var $select = $li.find("select.selectCombo");
							$select.prop({id: "_items_"+facetGroupId});

							$select.combobox({
								change: function(e, u){
									self.checkDuplicateFacet(this, e, u, facetGroupId);
								},
								selected: function(e, u){
									self.checkDuplicateFacet(this, e, u, facetGroupId);
								}
							});
							$li.find("input#_items_"+facetGroupId).val(itemName);
							$li.find("select#_items_"+facetGroupId).prop("selectedText", itemName);

							$ul.append($li);
							self.addDeleteFacetValueListener($li);
						}
					},
					preHook:function(){
						
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
				$.each(self.sortOrderList, function(sortName, sortDisplayText) { 
					contentHolder.append($("<option>", {value: sortDisplayText, selected: sortName===selectedOrder}).text(sortDisplayText));
				});

			},

			populateTemplateNameList: function(contentHolder){
				$select = contentHolder.find('select[id="popName"]');

				CategoryServiceJS.getTemplateNamesByStore(GLOBAL_store, {
					callback: function(data){
						var list = data;

						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook: function(){
						$select.find("option").remove();
					}
				});
			},

			getFacetSortRuleList : function(page) { 
				var self = this;

				$("#keywordSidePanel").sidepanel({
					fieldId: "ruleId",
					fieldName: "ruleName",
					page: page,
					pageSize: self.rulePageSize,
					headerText : "Facet Sorting Rule",
					searchText : "Enter Keyword",
					allowBlankText: true,
					showAddButton: allowModify,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, ruleName, page){
						self.rulePage = page;
						self.ruleFilterText = ruleName;
						FacetSortServiceJS.getAllRule(ruleName, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(ruleName, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemAddCallback: function(base, name){
						$("a#addButton").qtip({
							id: "add-facetsort",
							content: {
								text: $('<div/>'),
								title: { text: 'New Facet Sort', button: true }
							},
							position: {
								target: $("a#addButton")
							},
							show: {
								ready: true
							},
							style: {width: 'auto'},
							events: { 
								show: function(e, api){
									var $contentHolder = $("div", api.elements.content).html($("#addFacetSortTemplate").html());
									var $select = $("select#popSortOrder");
									//populate sort order dropdown list
									self.populateSortOrderList($select);

									if ($.isNotBlank(name)) $contentHolder.find('input[id="popKeywordName"]').val(name);

									$contentHolder.find("select.selectCombo").combobox({
										selected: function(e, u){
											switch($(this).attr("id").toLowerCase()){
											case "poptype":
												var selectedType = u.item.text;
												var $divTemplate = $contentHolder.find('div#templatelist');
												$contentHolder.find('div#keywordinput, div#templatelist').show();

												switch(selectedType.toLowerCase()){
												case "keyword": 
													$contentHolder.find('div#templatelist').hide();
													$contentHolder.find('div#keywordinput').show();
													break;
												case "template":
													$contentHolder.find('div#keywordinput').hide();
													$contentHolder.find('div#templatelist').show();
													self.populateTemplateNameList($divTemplate);
													break;
												}
												break;
											}
										}
									});

									$contentHolder.find('select[id="popType"]').off().on({
										change: function(e){
											var selectedType = e.target.value;
											$divKeyword = $contentHolder.find('div#keywordinput');
											$divTemplate = $contentHolder.find('div#templatelist');

											switch(selectedType.toLowerCase()){
											case "keyword": 
												$divTemplate.remove();
												$divKeyword.show();
												break;
											case "template":
												$divTemplate.show();
												$divKeyword.remove();
												self.populateTemplateNameList($divTemplate);
												break;
											default:
												break;
											}
										}
									});

									$contentHolder.find('a#addButton').off().on({
										click: function(e){
											var popName = "";

											var ruleType = $.trim($contentHolder.find("input#popType").val());
											var sortType = $.trim($contentHolder.find("input#popSortOrder").val());

											if($contentHolder.find('div#keywordinput').is(":visible")){
												popName = $.trim($contentHolder.find('input[id="popKeywordName"]').val());
											}
											else if($contentHolder.find('div#templatelist').is(":visible")){
												popName = $.trim($contentHolder.find("input#popName").val());
											}

											if ($.isBlank(popName)){
												jAlert("Facet Sort rule name is required.",self.moduleName);
											}
											else if ($.isBlank(ruleType)){
												jAlert("Facet Sort rule type is required.",self.moduleName);
											}
											else if ($.isBlank(sortType)){
												jAlert("Facet Sort order is required.",self.moduleName);
											}
											else {
												FacetSortServiceJS.getRuleByNameAndType(popName, ruleType, {
													callback: function(data){
														if (data != null){
															jAlert("Another facet sorting rule is already using the name and type provided.",self.moduleName);
														}else{
															FacetSortServiceJS.addRule(popName, ruleType, sortType, {
																callback: function(data){
																	if (data!=null){
																		showActionResponse(1, "add", popName);
																		self.getFacetSortRuleList(1);
																		self.setFacetSort(data);
																	}
																},
																preHook: function(){ 
																	base.prepareList(); 
																}
															});
														}
													}
												});
											}
										}
									});

									$contentHolder.find('a#clearButton').off().on({
										click: function(e){
											$contentHolder.find('input#popKeywordName').val("");
											$contentHolder.find("input#popSortOrder").val("");
											$contentHolder.find("input#popName").val("");
										}
									});
								},
								hide: function (e, api){
									sfExcFields = new Array();
									api.destroy();
								}
							}
						});

					},

					itemOptionCallback: function(base, id, name, model){
						var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));
						var totalText = "&#133;";
						var ruleType = model["ruleType"];

						switch(ruleType.toLowerCase()){
						case "keyword": base.$el.find(selector + ' div.itemIcon').append(self.keywordIconPath); break;
						case "template": base.$el.find(selector + ' div.itemIcon').append(self.templateIconPath); break;
						}

						base.$el.find(selector + ' div.itemLink a').html(totalText);
						base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').off().on({
							click: function(e){
								self.setFacetSort(e.data.model);
							}
						},{model: model});

						DeploymentServiceJS.getRuleStatus(self.moduleName, id, {
							callback:function(data){
								base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
							}
						});
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

			addNewFacetValueListener : function(content, facetGroupId){
				var self = this;
				var ul = content.find('ul#selectedFacetValueList');

				content.find("a#addNewFacetValue").off().on({
					click: function(e){
						if (!e.data.locked){
							var items = self.buildFacetGroupItemsMap();
							var conditionCount = (items && items[facetGroupId]) ? items[facetGroupId].length : 0;
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
	
							$li.find("select.selectCombo").combobox({
								change: function(e, u){
									self.checkDuplicateFacet(this, e, u, facetGroupId);
								},
								selected: function(e, u){
									self.checkDuplicateFacet(this, e, u, facetGroupId);
								}
							});
	
							self.addDeleteFacetValueListener($li);
						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});

				content.find("span#addNewLink").text("[add new " + self.tabSelectedName.toLowerCase() + " value]");
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
						var items = $("input#_items_"+self.facetGroupIdList[index]);
	
						for(var i = 0; i < items.length; i++){
							facetItems[i] = $(items[i]).val();
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

						var sortType = $("select#facetSortOrder option:selected").val();
						var facetGroupItems = self.buildFacetGroupItemsMap();
						var sortOrders = self.buildFacetGroupSortTypeMap();

						//if (self.checkIfUpdateAllowed()){
						var response = 0;
						FacetSortServiceJS.updateRule(self.selectedRule["ruleId"], self.selectedRule["ruleName"], sortType, facetGroupItems, sortOrders,  {
							callback: function(data){
								response = data;
								showActionResponse(response, "update", self.selectedRule["ruleName"]);
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
						//}
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
							urlParams += (key + '=' + params[key]);
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
			}
	};

	$(document).ready(function() {
		FacetSort.init();
	});
})(jQuery);	
