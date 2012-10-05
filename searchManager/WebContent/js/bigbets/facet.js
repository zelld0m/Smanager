(function($){

	var FacetSort = {
			moduleName: "Facet Sort",
			selectedRule:  null,
			tabSelectedId: 1,
			
			rulePage: 1,
			rulePageSize: 5,
			
			facetFields : ["Category", "Manufacturer"],
			facetValueList: null,
			sortOrderList: null,
			
			keywordIconPath: "../images/icon_keyword.png",
			templateIconPath:"../images/icon_template.png",
			
			prepareFacetSort : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #facetsorting, #noSelected").hide();
				$("#titleHeader").html("");
				$("#ruleTypeIcon").attr("src", "");
			},
			
			showFacetSort : function(){
				var self = this;
				
				self.prepareFacetSort();
				$("#preloader").hide();
				self.getFacetSortRuleList(1);
				
				if(self.selectedRule==null){
					$("#noSelected").show();
					$("#titleText").html(self.moduleName);
					return;
				}
				
				$("#submitForApproval").rulestatus({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					authorizeRuleBackup: true,
					authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
					afterSubmitForApprovalRequest:function(ruleStatus){
						self.showFacetSort();
					},
					beforeRuleStatusRequest: function(){
						self.prepareFacetSort();	
					},
					afterRuleStatusRequest: function(ruleStatus){
						$("#submitForApproval").show();
						$("#preloader").hide();
						$("#titleText").html(self.moduleName + " for ");
						$("#titleHeader").html(self.selectedRule["ruleName"]);
						$("#readableString").html(self.selectedRule["readableString"]);
						
						switch(self.selectedRule["ruleType"].toLowerCase()){
						case "keyword":	$("#ruleTypeIcon").attr("src", self.keywordIconPath); break;
						case "template": $("#ruleTypeIcon").attr("src", self.templateIconPath); break;
						}
						
						var $facetSortOrder = $('#facetSortOrder');
						self.populateSortOrderList($facetSortOrder, self.selectedRule["sortType"]);
						
						self.selectedRuleStatus = ruleStatus;
						$("#facetsorting").show();
						$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

						//create tabs for facet groups
						self.createFacetGroupTabs();
						
						self.addSaveRuleListener();
						self.addDeleteRuleListener();
						self.addDownloadListener();

						$('#auditIcon').off().on({
							click: function(e){
								$(e.currentTarget).viewaudit({
									itemDataCallback: function(base, page){
										//TODO
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
				
				$("#facetsorting").show();
			},
			
			setFacetSort : function(rule){
				var self = this;
				self.selectedRule = rule;
				//initialize facet value list
				self.getFacetValueDropdownList();
				self.showFacetSort();
				self.getSortOrderList();
			},
			
			getFacetValueDropdownList : function () {
				var self = this;

				var	getFacetParams = function (keyword){
					var paramString = "";

					var params = {
							'facet': true,
							'q': keyword,
							'facet.field': self.facetFields,
							'rows': 0,
							'facet.mincount': 1,
							'facet.limit': -1,
							'facet.sort':'HEX',
							'gui': true,
							'json.nl':'map'
					};

					for (var name in params) {
						if ($.isArray(params[name])){
							for (var param in params[name]){
								paramString += "&" + name + "=" + params[name][param];
							}
						}else{
							if(name.toLowerCase() !== "sort".toLowerCase())
								paramString += "&" + name + "=" + params[name];
						}
					}

					return paramString;
				};

				$.getJSON(
						GLOBAL_solrUrl + GLOBAL_store + '/select' + '?' + getFacetParams("") + '&wt=json&json.wrf=?', 
						function (json, textStatus) { 
							if (textStatus!=="success"){
								api.destroy();
							}

							self.facetValueList = json.facet_counts.facet_fields;; 
						}
				);
			},
			
			populateFacetListDropdown: function(facetField){
				var self = this;

				var $facetDiv = $('div#facetTabPattern');
				var $select = $facetDiv.find('select#facetValuesPattern');
				
				if(self.facetValueList){
					var facetValues = self.facetValueList[facetField];

					for (var facetValue in facetValues){
						$select.append($("<option>", {value: facetValue}).text(facetValue));
					}
				}
			},
			
			viewFacetValues : function(facet){
				var self = this;
				
				$("#"+facet.toLowerCase()+"list").viewfacetvalues({
					keyword:"",
					facetField: facet
				});
			},
			
			//TODO
			populateSelectedValues : function(content, facet, ruleId){
				var self = this;
				
				content.find("li:not(#addFacetValuePattern)").remove();
				for(var i=0; i < 3; i++){
					var $li = $('li#addFacetValuePattern').clone();
					$li.show();
					$li.prop({id : ''});
					$li.find("select#facetValuesPattern").prop({id: 'facetValues' + i});
					
					content.append($li);
				}
				
				content.find("select.selectCombo").combobox({});
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
												var selectedType = e.target.text;
												$divTemplate = $contentHolder.find('div#templatelist');
												$contentHolder.find('div#keywordinput, div#templatelist').hide();
												
												switch(selectedType.toLowerCase()){
													case "keyword": 
														$contentHolder.find('div#keywordinput').show();
														break;
													case "template":
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
											else if (!isAllowedName(popName)) {
												jAlert(ruleNameErrorText,self.moduleName);
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
						case "keyword":
							base.$el.find(selector + ' div.itemIcon img').attr("src", self.keywordIconPath);
							break;
						case "template":
							base.$el.find(selector + ' div.itemIcon img').attr("src", self.templateIconPath);
							break;
						}
						
						base.$el.find(selector + ' div.itemLink a').html(totalText);
						base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').off().on({
							click: function(e){
								self.setFacetSort(model);
							}
						});

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
				var $ul = $("ul#facetGroupTab"); 
				$ul.find("li:not('.facetGroupTabPattern')").remove();
				
				FacetSortServiceJS.getAllFacetGroup(self.selectedRule["ruleId"], {
					callback: function(data){
						var facetGroups = data.list;
						for(var index in facetGroups){
							var facetGroup = facetGroups[index];
							var facetGroupName = (facetGroup["name"]).toLowerCase(); 
							var $li = $ul.find("li.facetGroupTabPattern").clone();
							$li.show();
							$li.removeClass("facetGroupTabPattern");
							$li.find("span.facetGroupName").html(facetGroup["name"]);
							$li.find("a").prop({href: "#"+facetGroupName});
							$ul.find("li.facetGroupTabPattern").before($li);
							
							var $facetDiv = $("div.facetTabPattern").clone();
							var $facetSort = $facetDiv.find("select.facetGroupSortOrder");
							$facetDiv.show();
							$facetDiv.removeClass("facetTabPattern");
							$facetDiv.prop({id : facetGroupName});
							self.populateSortOrderList($facetSort, facetGroup["sortType"]);
							$("div.facetTabPattern").before($facetDiv);
						}
					},
					postHook: function() { self.addTabListener(); }
				});
			},
			
			setActiveTab: function(facetName){
				var self = this;
				var facetNameLower = facetName.toLowerCase();
				var $facet = $('div#'+facetNameLower);
				var $facetTab = $('div#'+facetNameLower).clone();
				var $selectedValuesUL = $('ul#selectedFacetValueList');
				
				$facet.html("");
				$facetTab.show();
				$facetTab.prop({id : facetNameLower});
				$facetTab.find("span#addFacetSortTitleHeader").text("Elevated " + facetName + " Values");
				
				
				self.populateSelectedValues($selectedValuesUL, facetNameLower, '');
				self.addNewFacetValueListener($facetTab);
				
				$facetTab.find("span#addNewLink").text("[add new " + facetNameLower + " value]");
				$facetTab.find("div#facetvaluelist").prop({id : facetNameLower +'list'});
				
				$('ul#selectedFacetValueList').sortable();
				
				// TODO add draggable feature
				/*$selectedValuesUL.sortable({ 
					handle : 'li.handle',
					cursor : 'move',
					start: function(event, ui) {
						ui.item.data('start_pos', ui.item.index());
					},     
					change: function(event, ui) {
						var index = ui.placeholder.index();
						if (ui.item.data('start_pos') < index){
							contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(' + index + ')').addClass('sortableHighlights');
						}else{
							contentHolder.find('#listItems_' + doc.EDP + ' > li:eq(' + (index + 1) + ')').addClass('sortableHighlights');
						}
					},
					update: function(event, ui) {
						var ref = ui.item.attr("id").split('_')[1];
						contentHolder.find('#listItems_' + doc.EDP + ' > li').removeClass('sortableHighlights');
						contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(even)').addClass("alt");
						contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(odd)').removeClass("alt");
					},
					stop: function(event, ui) {
						var sourceIndex = (ui.item.data('start_pos')+1) ;
						var destinationIndex = (ui.item.index()+1);

						// Update demote position
						if(sourceIndex != destinationIndex){
							var memberId = ui.item.attr("id").split('_')[1];

							DemoteServiceJS.update(keyword,memberId,destinationIndex,{
								callback : function(event){
									needRefresh = true;
									populateSelectedProduct(contentHolder);
								},
								preHook: function() { prepareDemoteResult(contentHolder); },
								postHook: function() { updateDemoteResult(contentHolder, doc, keyword); }
							});
						}	 
					}
				});*/
				$facet.append($facetTab);
			},
			
			addTabListener: function(){
				var self = this;
				
				$("#facetsort").tabs("destroy").tabs({
					show: function(event, ui){
						var tabNumber = ui.index;
						self.tabSelectedId = tabNumber + 1;
						var facetTab = "Category";
						switch(self.tabSelectedId){
							case 1: facetTab = "Category"; break;
							case 2: facetTab = "Manufacturer"; break;
							default: break;
						}
						
						//self.setActiveTab(facetTab);
						self.populateFacetListDropdown(facetTab);
						self.viewFacetValues(facetTab);
					}
				});
			},
			
			//TODO
			addNewFacetValueListener : function(content){
				var self = this;
				
				$("a#addNewFacetValue").off().on({
					click: function(e){
						var $li = $('li#addFacetValuePattern').clone();
						$li.prop({id : facet+'Value'});
						content.append($li);
						
						$li.find("select#facetValuesPattern").prop({id: facet+'Values' + i});
					}
				});
			},
			
			//TODO
			addSaveRuleListener: function(){
				var self = this;

				$("#saveBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						if (self.checkIfUpdateAllowed()){
							FacetSortServiceJS.updateRule(self.selectedRule["ruleId"], {
								callback: function(data){
									response = data;
									showActionResponse(response, "update", ruleName);
								},
								preHook: function(){
									self.prepareFacetSort();
								},
								postHook: function(){
									if(response==1){
										FacetSortServiceJS.getRule(self.selectedRule["ruleId"],{
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
						}
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
						if (!e.data.locked && confirm("Delete " + self.selectedRule["ruleName"] + "'s rule?")){
							FacetSortServiceJS.deleteRule(self.selectedRule["ruleId"],{
								callback: function(code){
									showActionResponse(code, "delete", self.selectedRule["ruleName"]);
									if(code==1) {
										self.setFacetSort(null);
									}
								}
							});
						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},
			
			init : function() {
				var self = this;
				self.showFacetSort();
			}
	};
	

	$(document).ready(function() {
		FacetSort.init();
	});
})(jQuery);	