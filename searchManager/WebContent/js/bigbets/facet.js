(function($){

	var FacetSort = {
			moduleName: "Facet Sort",
			selectedRule:  null,
			tabSelectedId: 1,
			
			rulePage: 1,
			rulePageSize: 5,
			
			facetFields : ["Category", "Manufacturer"],
			facetValueList: null,
			
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
						
						//TODO
						$("#ruleTypeIcon").attr("src", "../images/icon_keyword.png");
						
						self.selectedRuleStatus = ruleStatus;
						$("#facetsorting").show();
						$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

						self.addTabListener();

						//TODO
						//self.addSaveRuleListener();
						//self.addDeleteRuleListener();
						//self.addDownloadListener();

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
				self.addTabListener();
				
				
			},
			
			setFacetSort : function(rule){
				var self = this;
				self.selectedRule = rule;
				//initialize facet value list
				self.getFacetValueDropdownList();
				self.showFacetSort();
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
			
			setActiveTab: function(facetName){
				var self = this;
				var facetNameLower = facetName.toLowerCase();
				
				var $facet = $('div#'+facetNameLower);
				var $facetTab = $('div#facetTabPattern').clone();
				
				$facet.html("");
				
				$facetTab.show();
				$facetTab.prop({id : facetNameLower});
				
				$facetTab.find("span#addFacetSortTitleHeader").text("Elevated " + facetName + " Values");
				
				var $selectedValuesUL = $('ul#selectedFacetValueList');
				
				//TODO: populate elevated values
				//self.populateSelectedValues($selectedValuesUL, facetNameLower, '');

				//TODO: add new link listener
				self.addNewFacetValueListener($facetTab);
				
				$facetTab.find("span#addNewLink").text("[add new " + facetNameLower + " value]");
				$facetTab.find("div#facetvaluelist").prop({id : facetNameLower +'list'});
				
				$facet.append($facetTab);
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
			
			populateSelectedValues : function(content, facet, ruleId){
				var self = this;
				
				//TODO
				for(var i=0; i < 3; i++){
					var $li = $('li#addFacetValuePattern').clone();
					$li.show();
					$li.prop({id : 'facetValue' + i});
					content.append($li);
					
					$li.find("select#facetValuesPattern").prop({id: 'facetValues' + i});
				}
			},
			
			populateSortOrderList : function(contentHolder){
				FacetSortServiceJS.getSortOrderList({
					callback: function(data){
						var list = data;

						$.each(list, function(sortName, sortDisplayText) { 
							contentHolder.append($("<option>", {value: sortDisplayText}).text(sortDisplayText));
						});
					},
					preHook: function(){
						contentHolder.find("option").remove();
					}
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
					page: self.rulePage,
					pageSize: self.rulePageSize,
					headerText : "Facet Sorting Rule",
					searchText : "Enter Keyword",
					showAddButton: allowModify,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, keyword, page){
						self.rulePage = page;
						self.ruleFilterText = keyword;
						//TODO
						RedirectServiceJS.getAllRule(keyword, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(keyword, page, data.totalSize);
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
											//var popType = $.trim($contentHolder.find('select[id="popType"]').val());
											//var sortType = $.trim($contentHolder.find('select[id="popSortOrder"]').val());

											var ruleType = $.trim($contentHolder.find("input#popType").val());
											var sortType = $.trim($contentHolder.find("input#popSortOrder").val());
											
											if($contentHolder.find('div#keywordinput').is(":visible")){
												popName = $.trim($contentHolder.find('input[id="popKeywordName"]').val());
											}
											else if($contentHolder.find('div#templatelist').is(":visible")){
												popName = $.trim($contentHolder.find("select#popName").val());
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
												//TODO
												//FacetSortServiceJS.checkForRuleNameDuplicate('', popType, popName, {
													//callback: function(data){
														//if (data==true){
															//jAlert("Another facet sorting rule is already using the name provided.",self.moduleName);
														//}else{
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
														//}
													//}
												//});
											}
										}
									});

									$contentHolder.find('a#clearButton').off().on({
										click: function(e){
											//TODO
											//$contentHolder.find('input[type="text"], textarea').val("");
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

						//TODO
						//FacetSortServiceJS.getTotalKeywordInRule(id, {
						RedirectServiceJS.getTotalKeywordInRule(id,{
							callback: function(count){
								var totalText = "&#133;"; 
								base.$el.find(selector + ' div.itemLink a').html(totalText);

								base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').off().on({
									click: function(e){
										self.setFacetSort(model);
									}
								});
							},
							preHook: function(){ 
								base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
							}
						});

						//TODO
						DeploymentServiceJS.getRuleStatus(self.moduleName, id, {
							callback:function(data){
								base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
							}
						});
					}
				});
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
						
						self.populateFacetListDropdown(facetTab);
						self.setActiveTab(facetTab); 
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
							FacetSortServiceJS.updateRule(self.selectedRule["ruleId"], ruleName, description, {
								callback: function(data){
									response = data;
									showActionResponse(response, "update", ruleName);
								},
								preHook: function(){
									self.prepareFacetSort();
								},
								postHook: function(){
									if(response==1){
										RedirectServiceJS.getRule(self.selectedRule["ruleId"],{
											callback: function(data){
												self.setRedirect(data);
											},
											preHook: function(){
												self.prepareRedirect();
											}
										});
									}
									else{
										self.setRedirect(self.selectedRule);
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