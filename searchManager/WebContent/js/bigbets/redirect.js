(function($){

	var RedirectRule = {
			moduleName: "Query Cleaning",
			selectedRule:  null,
			selectedRuleStatus: null,

			ruleFilterText: "",
			ruleKeywordFilterText: "",
			tabSelectedTypeId: "",

			rulePage: 1,
			ruleKeywordPage: 1,
			keywordInRulePage: 1,

			rulePageSize: 5,
			ruleKeywordPageSize: 5,
			keywordInRulePageSize: 5,

			prepareRedirect : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #redirect, #noSelected").hide();
				$("#titleHeader").html("");
			},

			addDownloadListener: function(){
				$("a#downloadIcon").download({
					headerText:"Download Query Cleaning",
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;
						params["id"] = self.selectedRule["ruleId"];
						params["filename"] = e.data.filename;
						params["type"] = e.data.type;

						for(var key in params){
							if (count>0) urlParams +='&';
							urlParams += (key + '=' + params[key]);
							count++;
						};

						document.location.href = url + '?' + urlParams;
					}
				});
			},

			showRedirect : function(){
				var self = this;

				self.prepareRedirect();
				$("#preloader").hide();
				self.getRedirectRuleList(1);
				self.getRedirectRuleKeywordList(1);

				if(self.selectedRule==null){
					$("#noSelected").show();
					$("#titleText").html(self.moduleName);
					return;
				}

				$("#submitForApproval").show();
				$("#redirect").show();

				$("#titleText").html(self.moduleName + " for ");
				$("#titleHeader").html(self.selectedRule["ruleName"]);

				$("#name").val(self.selectedRule["ruleName"]);
				$("#description").val(self.selectedRule["description"]);

				self.getKeywordInRuleList(1);
				self.refreshTabContent();
				self.addTabListener();

				$("#saveBtn").off().on({
					click: self.updateRule,
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});

				$("#deleteBtn").off().on({
					click: self.deleteRule,
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});

				self.addDownloadListener();

				$("#submitForApprovalBtn").off().on({
					click: function(e){
						if(confirm(e.data.module + " " + e.data.ruleRefName + " will be locked for approval. Continue?")){
							DeploymentServiceJS.processRuleStatus(e.data.module, e.data.ruleRefId, e.data.ruleRefName, e.data.isDelete,{
								callback: function(data){
									self.ruleStatus = data;
								},
								preHook:function(){
									self.prepareRedirect();
								},
								postHook: function(){
									self.setRedirect(self.selectedRule);
								}
							});
						}
					}
				}, { module: self.moduleName, ruleRefId: self.selectedRule["ruleId"] , ruleRefName: self.selectedRule["ruleName"], isDelete: false});

				$('#auditIcon').on({
					click: showAuditList
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify, type:self.moduleName, ruleRefId: self.selectedRule["ruleId"], name:  self.selectedRule["ruleName"]});

				$("div#keyword").find('input[type="text"]#changeKeyword').val($.trim(self.selectedRule["changeKeyword"]));

				$("div#keyword").find("#changeKeywordBtn").off().on({
					mouseenter: showHoverInfo,
					click: function(evt){
						if (!evt.data.locked){
							var inputChangedKeyword = $.trim($("div#keyword").find('input[type="text"]#changeKeyword').val());

							$('div#keyword').find('#activerules > .alert > #rules').html("");
							$('div#keyword').find('#activerules').hide();
							self.updateChangeKeyword(inputChangedKeyword);
						}
					}
				},{locked: self.selectedRuleStatus["locked"] || !allowModify});
			},

			getKeywordInRuleList : function(page){
				var self = this;
				$("#keywordInRulePanel").sidepanel({
					fieldId: "keywordId",
					fieldName: "keyword",
					page: page,
					region: "content",
					pageStyle: "style2",
					pageSize: self.keywordInRulePageSize,
					headerText : "Using This Rule",
					searchText : "Enter Keyword",

					showAddButton: !self.selectedRuleStatus["locked"] || allowModify,
					itemDataCallback: function(base, keyword, page){
						RedirectServiceJS.getAllKeywordInRule(self.selectedRule["ruleId"], keyword, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},
					itemOptionCallback: function(base, id, name){
						var icon = "";
						var suffixId = $.escapeQuotes($.formatAsId(id));

						icon = '<a id="delete' + suffixId + '" href="javascript:void(0);"><img src="../images/icon_delete2.png"></a>';
						base.$el.find('#itemPattern' + suffixId + ' div.itemLink').html($(icon));

						base.$el.find('#itemPattern' + suffixId + ' div.itemLink a#delete' + suffixId).on({
							click: function(e){
								if (!e.data.locked && allowModify && confirm('Delete "' + name + '" in ' + self.selectedRule["ruleName"]  + '?'))
									RedirectServiceJS.deleteKeywordInRule(self.selectedRule["ruleId"], name,{
										callback:function(code){
											showActionResponse(code, "delete", name);
											self.getKeywordInRuleList(1);
											self.getRedirectRuleList(1);
										},
										preHook: function(){ base.prepareList(); }
									});
							},
							mouseenter: showHoverInfo
						},{locked: self.selectedRuleStatus["locked"] || !allowModify});
					},
					itemAddCallback: function(base, keyword){
						if (!self.selectedRuleStatus["locked"] || allowModify){
							RedirectServiceJS.addKeywordToRule(self.selectedRule["ruleId"], keyword, {
								callback: function(code){
									showActionResponse(code, "add", keyword);
									self.getKeywordInRuleList(1);
									self.getRedirectRuleList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
						}
					}
				});
			},

			getRedirectRuleKeywordList : function(page){
				var self = this;
				$("#ruleKeywordPanel").sidepanel({
					fieldId: "keywordId",
					fieldName: "keyword",
					page: self.ruleKeywordPage,
					pageSize: self.ruleKeywordPageSize,
					headerText : "Query Cleaning Keyword",
					searchText : "Enter Keyword",
					showAddButton: false,
					filterText: self.ruleKeywordFilterText,

					itemDataCallback: function(base, keyword, page){
						self.ruleKeywordPage = page;
						self.ruleKeywordFilterText = keyword;
						StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
							callback: function(data){
								base.populateList(data);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemOptionCallback: function(base, id, name){
						var suffixId = $.escapeQuotes($.formatAsId(id));

						RedirectServiceJS.getTotalRuleUsedByKeyword(name, {
							callback: function(data){
								base.$el.find('#itemPattern' + suffixId + ' div.itemLink a').html((data == 0) ? "-" :(data == 1) ? "1 Item" : data + " Items");

								if (data > 0)
									base.$el.find('#itemPattern' + suffixId + ' div.itemLink a').qtip({
										content: {
											text: $('<div/>'),
											title: { text: 'Query Cleaning for ' + name, button: true }
										},
										show: { modal: true },
										events: { 
											render: function(rEvt, api){
												var $content = $("div", api.elements.content).html($("#sortRulePriorityTemplate").html());

												RedirectServiceJS.getAllRuleUsedByKeyword(name, {
													callback: function(data){
														var list = data.list;

														$content.find("ul#ruleListing > li:not(#rulePattern)").remove();

														for(var i=0; i<data.totalSize; i++){
															var rule = list[i];
															var suffixId = $.escapeQuotes($.formatAsId(rule["ruleId"]));
															$content.find("li#rulePattern").clone().appendTo("ul#ruleListing").attr("id", "rule" + suffixId).show();
															$content.find("li#rule" + suffixId + " span.ruleName").attr("id", rule["ruleId"]).html(rule["ruleName"]);
														}

														$content.find("ul#ruleListing > li:nth-child(even)").addClass("alt");
													}
												});
											}
										}
									});
							},
							preHook: function(){ 
								base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
							}
						});

					}
				});
			},

			setRedirect : function(rule){
				var self = this;
				self.selectedRule = rule;

				if (rule==null){
					self.showRedirect();
					return;
				}

				DeploymentServiceJS.getRuleStatus(self.moduleName, self.selectedRule["ruleId"], {
					callback:function(data){
						self.selectedRuleStatus = data;
						$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));
						showDeploymentStatusBar(self.moduleName, self.selectedRuleStatus);
						self.showRedirect();
					},
					preHook: function(){
						self.prepareRedirect();
					}
				});		

			},

			getRedirectRuleList : function(page) { 
				var self = this;

				$("#rulePanel").sidepanel({
					fieldId: "ruleId",
					fieldName: "ruleName",
					page: self.rulePage,
					pageSize: self.rulePageSize,
					headerText : "Query Cleaning Rule",
					searchText : "Enter Name",
					showAddButton: allowModify,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, keyword, page){
						self.rulePage = page;
						self.ruleFilterText = keyword;
						RedirectServiceJS.getAllRule(keyword, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemAddCallback: function(base, name){
						RedirectServiceJS.checkForRuleNameDuplicate("", name, {
							callback: function(data){
								if (data==true){
									alert("Another query cleaning rule is already using the name provided.");
								}else{
									RedirectServiceJS.addRuleAndGetModel(name, {
										callback: function(data){
											if (data!=null){
												base.getList(name, 1);
												self.setRedirect(data);
											}else{
												self.setRedirect(self.selectedRule);
											}
										},
										preHook: function(){ 
											base.prepareList(); 
										}
									});
								}
							}
						});
					},

					itemOptionCallback: function(base, id, name, model){
						var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));

						RedirectServiceJS.getTotalKeywordInRule(id,{
							callback: function(count){

								var totalText = (count == 0) ? "&#133;": "(" + count + ")"; 
								base.$el.find(selector + ' div.itemLink a').html(totalText);

								base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').on({
									click: function(e){
										self.setRedirect(model);
									}
								});
							},
							preHook: function(){ 
								base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
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

			checkIfUpdateAllowed : function(){
				var ruleName = $.trim($('div#redirect input[id="name"]').val());  
				var description = $.trim($('div#redirect textarea[id="description"]').val());  
				isDirty = false;

				isDirty = isDirty || (ruleName.toLowerCase()!==$.trim(selectedRule.ruleName).toLowerCase());
				isDirty = isDirty || (description.toLowerCase()!==$.trim(selectedRule.description).toLowerCase());

				return isDirty;
			},

			updateRule : function(e) { 
				if (e.data.locked || !allowModify) return;

				var ruleName = $.trim($('div#redirect input[id="name"]').val());  
				var description = $.trim($('div#redirect textarea[id="description"]').val());  

				if (checkIfUpdateAllowed()){
					if ($.isBlank(ruleName)){
						alert("Rule name is required.");
					}
					else if (!isAllowedName(ruleName)){
						alert("Rule name contains invalid value.");
					}
					else if (!isAscii(description)) {
						alert("Description contains non-ASCII characters.");										
					}
					else if (!isXSSSafe(description)){
						alert("Description contains XSS.");
					}
					else {
						RedirectServiceJS.checkForRuleNameDuplicate(self.selectedRule["ruleId"], ruleName, {
							callback: function(data){
								if (data==true){
									alert("Another query cleaning rule is already using the name provided.");
								}else{
									var response = 0;
									RedirectServiceJS.updateRule(self.selectedRule["ruleId"], ruleName, description, {
										callback: function(data){
											response = data;
											showActionResponse(response, "update", ruleName);
										},
										preHook: function(){
											self.prepareRedirect();
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
							}
						});
					}
				}
			},

			deleteRule : function(e) { 
				if (!e.data.locked  && allowModify && confirm("Delete " + self.selectedRule["ruleName"] + "'s rule?")){
					RedirectServiceJS.deleteRule(self.selectedRule,{
						callback: function(code){
							showActionResponse(code, "delete", self.selectedRule["ruleName"]);
							if(code==1) self.setRedirect(null);
						}
					});
				}
			},

			getChangeKeywordActiveRules : function(inputChangedKeyword){

				$('div#keyword').find('#activerules > .alert > #rules').html("");
				$('div#keyword').find('#activerules').hide();

				if ($.isBlank(inputChangedKeyword)){ 
					return;
				}

				var activeRulesCtr = 4;
				var activeRules = "";

				if($("div#keyword").find('#preloader').is(":not(:visible)"))
					$("div#keyword").find('#preloader').show();

				var hideLoader = function(){
					$("div#keyword").find('#preloader').hide();
					if ($.isNotBlank(activeRules)){
						$('div#keyword').find('#activerules > .alert > #rules').html(activeRules);
						$('div#keyword').find('#activerules').show();
					};
				};

				ElevateServiceJS.getTotalProductInRule(inputChangedKeyword, {
					callback:function(data){
						if (data>0){
							activeRules += ($.isNotBlank(activeRules)? ', ':'') + data + ' Elevate Item';
						}; 
					},
					postHook: function(){
						activeRulesCtr--;
						if (activeRulesCtr==0) hideLoader();
					}
				});

				ExcludeServiceJS.getTotalProductInRule(inputChangedKeyword, {
					callback:function(data){
						if (data>0){
							activeRules += ($.isNotBlank(activeRules)? ', ':'') + data + ' Exclude Item';
						}; 
					},
					postHook: function(){
						activeRulesCtr--;
						if (activeRulesCtr==0) hideLoader();
					}
				});

				RedirectServiceJS.getAllRuleUsedByKeyword(inputChangedKeyword, {
					callback:function(data){
						if (data.totalSize>0){
							activeRules += ($.isNotBlank(activeRules)? ", ":'') + data.totalSize + ' Redirect Rule';
							activeRules += "(";

							for(var i=0; i<data.totalSize; i++){
								activeRules += data.list[i]["ruleName"];
								if ((i+1)<data.totalSize) activeRules += ", ";
							}

							activeRules += ")";
						}; 
					},
					postHook: function(){
						activeRulesCtr--;
						if (activeRulesCtr==0) hideLoader();
					}
				});

				RelevancyServiceJS.getAllRuleUsedByKeyword(inputChangedKeyword, {
					callback:function(data){
						if (data.totalSize>0){
							activeRules += ($.isNotBlank(activeRules)? ", ":'') + data.totalSize + ' Relevancy Rule';
							activeRules += "(";

							for(var i=0; i<data.totalSize; i++){
								activeRules += data.list[i]["relevancy"]["ruleName"];
								if ((i+1)<data.totalSize) activeRules += ", ";
							}

							activeRules += ")";
						} 
					},
					postHook: function(){
						activeRulesCtr--;
						if (activeRulesCtr==0) hideLoader();
					}
				});
			},

			updateChangeKeyword : function(inputChangedKeyword){
				var self = this;
				RedirectServiceJS.setChangeKeyword(self.selectedRule["ruleId"], inputChangedKeyword, {
					callback: function(data){

					},
					preHook: function(){
						$("div#keyword").find('#preloader').show();
					},
					postHook: function(){
						self.getChangeKeywordActiveRules(inputChangedKeyword);
					}
				});
			},

			setActiveRedirectType : function(){
				var self = this;

				switch(parseInt(self.selectedRule["redirectTypeId"])){
				case 1: $("div#filter").find('input[type="checkbox"]#activate').prop("checked", true).prop("disabled", true); break;
				case 2: $("div#keyword").find('input[type="checkbox"]#activate').prop("checked", true).prop("disabled", true); break;
				case 3: $("div#page").find('input[type="checkbox"]#activate').prop("checked", true).prop("disabled", true); break;
				};

				$('input[type="checkbox"]#activate').prop("checked", false).prop("disabled", false).off().on({
					click:function(evt){
						var typeId = 1;
						switch(self.tabSelectedTypeId){
						case "#filter": typeId = 1; break;
						case "#keyword": typeId = 2; break; 
						case "#page": typeId = 3; break; 
						}

						self.updateActiveRedirectType(typeId);
					}
				});
			},

			updateActiveRedirectType : function(typeId){
				var self = this;
				RedirectServiceJS.setRedirectType(self.selectedRule["ruleId"], typeId, {
					callback: function(data){
						self.selectedRule["redirectTypeId"] = typeId;
						self.setActiveRedirectType();
					}
				});
			},


			showRuleCondition: function(){
				var self = this;

				var $divItemList = $("div#conditionList");

				RedirectServiceJS.getConditionInRule(self.selectedRule["ruleId"], 0, 0, {
					callback: function(data){
						if(data!=null && data.totalSize > 0){
							var list = data.list;
							for(var i=0; i < list.length; i++){
								var item = list[i];
								var $divItem = $divItemList.find('div#conditionItemPattern').clone();
								$divItem.prop("id", $.formatAsId(item["sequenceNumber"]));
								$divItem.find(".conditionFormattedText").html(item["readableString"]);

								$divItem.show();
								$divItemList.append($divItem);
							}

							$divItemList.find("img.toggleIcon").off().on({
								click: function(evt){
									var $item = $(this).parents(".conditionItem");
									if ($item.find("div.conditionFields").is(":visible")){
										$(this).attr("src", GLOBAL_contextPath + "/images/icon_expand.png");
										$item.find(".conditionFields").hide();
									}else{
										$(this).attr("src", GLOBAL_contextPath + "/images/icon_collapse.png");
										$item.find("div.conditionFields").show();
									}
								}
							});
							return;
						}

						$divItemList.find("div#emptyConditionItem").show();

					},
					preHook:function(){
						$divItemList.find("div#emptyConditionItem").hide();
						$divItemList.find("#preloader").show();
					},
					postHook:function(){
						$divItemList.find("#preloader").hide();
					}
				});
			},			

			refreshTabContent: function(){
				var self = this;
				self.tabSelectedTypeId = $("li.ui-tabs-selected > a").attr("href");
				self.setActiveRedirectType();

				switch(self.tabSelectedTypeId){
				case "#filter" : self.showRuleCondition(); break;
				case "#keyword" : self.getChangeKeywordActiveRules(self.selectedRule["changeKeyword"]); break;
				}

			},

			addTabListener: function(){
				var self = this;
				$("div#redirect-type > ul.ui-tabs-nav > li > a").on({
					click: function(evt){
						self.refreshTabContent();
					}
				});
			},

			init : function() {
				var self = this;
				self.showRedirect();
			},
	};

	$(document).ready(function() {
		RedirectRule.init();
	});	

})(jQuery);	