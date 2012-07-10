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

			newFilterGroupText: "New Filter Group Condition",
			defaultIMS: "CatCode",

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

			populateCategories: function(ui, condition){
				var self = this;
				var $select = ui.find("select#categoryList");
				var $input = ui.find("input#categoryList");

				CategoryServiceJS.getIMSCategories({
					callback: function(data){
						var list = data;
						
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						
						if ($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Category"])){
							$select.prop("selectedText",condition.IMSFilters["Category"]);
							$input.val(condition.IMSFilters["Category"]);
						}
						
						if($.isNotBlank($input.val())) self.populateSubcategories(ui, condition);
					},
					preHook:function(){
						ui.find("img#preloaderCategoryList").show();
						$select.find("option").remove();
					},
					postHook:function(){
						ui.find("img#preloaderCategoryList").hide();
					}
				});
			},

			populateSubcategories: function(ui, condition){
				var self = this;
				console.log(ui.find("input#categoryList").val());
				var inCategory = $.trim(ui.find("input#categoryList").val());
				var $select = ui.find("select#subCategoryList");
				var $input = ui.find("input#subCategoryList");

				CategoryServiceJS.getIMSSubcategories(inCategory, {
					callback: function(data){
						var list = data;
						
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						
						if ($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["SubCategory"])){
							$select.prop("selectedText",condition.IMSFilters["SubCategory"]);
							$input.val(condition.IMSFilters["SubCategory"]);
						}
						
						if($.isNotBlank($input.val())) self.populateClass(ui, condition);
					},
					preHook:function(){
						ui.find("img#preloaderSubCategoryList").show();
						self.clearIMSComboBox(ui, "subcategory");
					},
					postHook:function(){
						ui.find("img#preloaderSubCategoryList").hide();
					}
				});
			},

			populateClass: function(ui, condition){
				var self = this;
				var inCategory = $.trim(ui.find("input#categoryList").val());
				var inSubCategory = $.trim(ui.find("input#subCategoryList").val());
				var $select = ui.find("select#classList");
				var $input = ui.find("input#classList");

				CategoryServiceJS.getIMSClasses(inCategory,inSubCategory, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						if ($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Class"])){
							$select.prop("selectedText",condition.IMSFilters["Class"]);
							$input.val(condition.IMSFilters["Class"]);
						}
						if($.isNotBlank($input.val())) self.populateMinor(ui, condition);
					},
					preHook:function(){
						ui.find("img#preloaderClassList").show();
						self.clearIMSComboBox(ui, "class");
					},
					postHook:function(){
						ui.find("img#preloaderClassList").hide();
					}
				});
			},

			populateMinor: function(ui, condition){
				var self = this;
				var inCategory = $.trim(ui.find("input#categoryList").val());
				var inSubCategory = $.trim(ui.find("input#subCategoryList").val());
				var inClass = $.trim(ui.find("input#classList").val());
				var $select = ui.find("select#minorList");
				var $input = ui.find("input#minorList");

				CategoryServiceJS.getIMSMinors(inCategory,inSubCategory, inClass, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						if ($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Minor"])){
							$select.prop("selectedText",condition.IMSFilters["Minor"]);
							$input.val(condition.IMSFilters["Minor"]);
						}
					},
					preHook:function(){
						ui.find("img#preloaderMinorList").show();
						self.clearIMSComboBox(ui, "minor");
					},
					postHook:function(){
						ui.find("img#preloaderMinorList").hide();
						self.populateManufacturers(ui, condition);
					}
				});
			},

			populateManufacturers: function(ui, condition){
				var self = this;
				var $select = ui.find("select#manufacturerList");
				var $input = ui.find("input#manufacturerList");

				var inCatCode = "";
				var inCategory = "";
				var inSubCategory = "";
				var inClass = "";
				var inMinor = "";

				var catCodeVal = $.trim(ui.find("input#catcode").val());

				if ($.isNotBlank(catCodeVal) && catCodeVal.length < 4 && ui.find("a.switchToCatName").is(":visible")){
					inCatCode = catCodeVal;
				}else if(ui.find("a.switchToCatCode").is(":visible")){
					inCategory = $.trim(ui.find("input#categoryList").val());
					inSubCategory = $.trim(ui.find("input#subCategoryList").val());
					inClass = $.trim(ui.find("input#classList").val());
					inMinor = $.trim(ui.find("input#minorList").val());
				}

				CategoryServiceJS.getIMSManufacturers(inCatCode, inCategory, inSubCategory, inClass, inMinor, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderManufacturerList").show();
						self.clearIMSComboBox(ui, "manufacturer");
					},
					postHook:function(){
						ui.find("img#preloaderManufacturerList").hide();
						if ($.isNotBlank(condition)  && $.isNotBlank(condition.IMSFilters["Manufacturer"])){
							$select.prop("selectedText",condition.IMSFilters["Manufacturer"]);
							$input.val(condition.IMSFilters["Manufacturer"]);
						}
					}
				});
			},  

			addFacetFieldListener: function(ui, condition){
				var self = this;
				var $facet = ui.find("div.facet");

				$facet.find("select.selectCombo").combobox({
					selected: function(e, u){

					}
				});

				if ($.isNotBlank(condition)){
					$facet.find("input#platformList").val(condition.facets["Platform"]);
					$facet.find("select#platformList").prop("selectedText", condition.facets["Platform"]);

					$facet.find("input#conditionList").val(condition.facets["Condition"]);
					$facet.find("select#conditionList").prop("selectedText", condition.facets["Condition"]);

					$facet.find("input#availabilityList").val(condition.facets["Availability"]);
					$facet.find("select#availabilityList").prop("selectedText", condition.facets["Availability"]);

					$facet.find("input#licenseList").val(condition.facets["License"]);
					$facet.find("select#licenseList").prop("selectedText", condition.facets["License"]);
				}
			},

			addIMSFieldListener: function(ui, condition){
				var self = this;
				var $ims = ui.find("div.ims");

				$ims.find("select.selectCombo").combobox({
					selected: function(e, u){
						var $item = $(this).parents(".conditionItem");
						switch($(this).attr("id").toLowerCase()){
						case "categorylist" :
							$item.find("input#categoryList").val(u.item.text);
							$item.find("input#categoryList").prop("selectedText", u.item.text);
							self.populateSubcategories(ui, condition); break;
						case "subcategorylist" : 
							$item.find("input#subCategoryList").val(u.item.text);
							$item.find("input#subCategoryList").prop("selectedText", u.item.text);
							self.populateClass(ui, condition); break;
						case "classlist" : 
							$item.find("input#classList").val(u.item.text);
							$item.find("input#classList").prop("selectedText", u.item.text);
							self.populateMinor(ui, condition); break;
						}
					}
				});

				var usingCategory = $.isNotBlank(condition) && condition["imsUsingCategory"];

				if (usingCategory || ui.find("a.switchToCatCode").is(":visible")){
					self.populateCategories(ui, condition);
				}

				if($ims.find("a.switchToCatName").is(":visible")){
					var $input = $ims.find("input#catcode");

					self.populateManufacturers(ui, condition);

					if ($.isNotBlank(condition)){
						$input.val(condition.IMSFilters["CatCode"]);
						$ims.find("input#manufacturerList").val(condition.IMSFilters["Manufacturer"]);
					}

					$input.off().on({
						focusout: function(e){
							self.populateManufacturers(ui, condition);
						}
					},{condition: condition});
				}		

			},

			clearIMSComboBox: function(ui, trigger){
				var self = this;
				var $ims = ui.find("div.ims");

				if ($.isBlank(trigger)){
					$ims.find("input").val("");
					$ims.find("select.selectCombo option").remove();
				}else{
					switch (trigger.toLowerCase()){
					case "category": 
						$ims.find("input#categoryList").val("");
						$ims.find("select#categoryList option").remove();
					case "subcategory": 
						$ims.find("input#subCategoryList").val("");
						$ims.find("select#subCategoryList option").remove();
					case "class": 
						$ims.find("input#classList").val("");
						$ims.find("select#classList option").remove();
					case "minor": 
						$ims.find("input#minorList").val("");
						$ims.find("select#minorList option").remove();
					case "manufacturer": 
						$ims.find("input#manufacturerList").val("");
						$ims.find("select#manufacturerList option").remove();	
					}
				}
			},

			buildConditionAsMap: function(ui){
				var self = this;
				var condMap = new Object();
				var catCode = new Array();
				var category = new Array();
				var subCategory = new Array();
				var clazz = new Array();
				var minor = new Array();
				var manufacturer = new Array();

				if (ui.find("div.ims").is(":visible")){
					catCode[0] = $.trim(ui.find("input#catcode").val());
					category[0] = $.trim(ui.find("select#categoryList option:selected").val());
					subCategory[0] = $.trim(ui.find("select#subCategoryList option:selected").val());
					clazz[0] = $.trim(ui.find("select#classList option:selected").val());
					minor[0] = $.trim(ui.find("select#minorList option:selected").val());
					manufacturer[0] = $.trim(ui.find("select#manufacturerList option:selected").val());

					if ($.isNotBlank(catCode[0])) condMap["CatCode"] = catCode; 	
					if ($.isNotBlank(category[0])) condMap["Category"] = category; 	
					if ($.isNotBlank(subCategory[0])) condMap["SubCategory"] = subCategory; 	
					if ($.isNotBlank(clazz[0])) condMap["Class"] = clazz; 	
					if ($.isNotBlank(minor[0])) condMap["Minor"] = minor; 	
					if ($.isNotBlank(manufacturer[0])) condMap["Manufacturer"] = manufacturer; 	
				}

				if (ui.find("div.facet").is(":visible")){
					var platform = $.trim(ui.find("select#platformList option:selected").val());
					var condition = $.trim(ui.find("select#conditionList option:selected").val());
					var availability = $.trim(ui.find("select#availabilityList option:selected").val());
					var license = $.trim(ui.find("select#licenseList option:selected").val());

					switch(platform){
					case "universal": condMap["Platform"] = ["Universal"]; break;
					case "pc": condMap["Platform"] = ["PC"]; break;
					case "linux": condMap["Platform"] = ["Linux"]; break;
					case "mac": condMap["Platform"] = ["Macintosh"]; break;
					}

					switch(condition){
					case "refurbished": condMap["Condition"] = ["Refurbished"]; break;
					case "open": condMap["Condition"] = ["Open Box"]; break;
					case "clearance": condMap["Condition"] = ["Clearance"]; break;
					}

					switch(availability){
					case "instock": condMap["Availability"] = ["In Stock"]; break;
					case "call": condMap["Availability"] = ["Call"]; break;
					}

					switch(license){
					case "license": condMap["License"] = ["Show License Products Only"]; break;
					case "nonlicense": condMap["License"] = ["Show Non-License Products Only"]; break;
					}

				}

				return condMap;
			},

			addSaveButtonListener: function(ui, condition){
				var self = this;

				var $saveBtn = ui.find(".saveBtn");

				$saveBtn.find("div.buttons").html(ui.hasClass("tempConditionItem")? "Save": "Update");

				$saveBtn.off().on({
					click:function(evt){
						var $item = $(this).parents(".conditionItem");
						var condMap = self.buildConditionAsMap(ui);

						if ($item.hasClass("tempConditionItem")){
							RedirectServiceJS.addCondition(self.selectedRule["ruleId"], condMap, {
								callback:function(data){
									if (data!=null){
										var list = data.list;
										var newItem = list[data.totalSize-1];
										$item.removeClass("tempConditionItem");
										$item.find("a.conditionFormattedText").html(newItem["readableString"]);
										$item.attr("id",newItem["sequenceNumber"]);
										$item.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");
									}
								},
								preHook:function(){

								},
								postHook:function(){

								}
							});
						}else{
							RedirectServiceJS.updateCondition(self.selectedRule["ruleId"], $item.attr("id"), condMap, {
								callback:function(data){
									if (data!=null){
										var list = data.list;
										var updatedItem = null;
										
										for (item in list){
											if (parseInt(list[item]["sequenceNumber"])==parseInt($item.attr("id"))){
												updatedItem = list[item];
											}
										}	
										$item.find("a.conditionFormattedText").html(updatedItem["readableString"]);
										$item.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");
									}
								},
								preHook:function(){

								},
								postHook:function(){

								}
							});
						}
					},
					mouseenter: showHoverInfo
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify , condition: condition});
			},

			addToggleListener: function(ui, condition){
				var self = this;

				ui.find("img.toggleIcon, a.conditionFormattedText").off().on({
					click: function(e){
						var $item = $(this).parents(".conditionItem");
						if ($item.find("div.conditionFields").is(":visible")){
							$item.find("img.toggleIcon").attr("src", GLOBAL_contextPath + "/images/icon_expand.png");
							$item.find(".conditionFields").slideUp("slow", function(){

							});
						}else{
							$item.find("img.toggleIcon").attr("src", GLOBAL_contextPath + "/images/icon_collapse.png");
							$item.find(".conditionFields").slideDown("slow", function(){
								self.addIMSFieldListener($item, e.data.condition);
								self.addFacetFieldListener($item, e.data.condition);
								self.addSaveButtonListener($item, e.data.condition);
							});
						}
					}
				},{condition: condition});
			},

			addNewFilterGroupListener: function(){
				var self = this;

				$("#addFilterGroupBtn").off().on({
					click: function(e){
						if(!e.data.locked){
							var $divItemList = $("div#conditionList");

							if ($divItemList.find("div.tempConditionItem").length > 0){
								alert("You have an empty filter group");
								return;
							}

							switch($("select#filterGroup option:selected").val()){
							case "ims": 
								$divItemList.find("div.ims").show();
								$divItemList.find("div.facet").show();
								break;
							case "facet": 
								$divItemList.find("div.ims").hide();
								$divItemList.find("div.facet").show();
								break;
							}

							$divItemList.find("div#emptyConditionItem").hide();
							var $divItem = $divItemList.find('div#conditionItemPattern').clone();

							var currCondCount = parseInt($divItemList.find("div.conditionItem:not(#conditionItemPattern):last").attr("id"));
							if (!$.isNumeric(currCondCount)){
								currCondCount = 0; 
							}

							$divItem.prop("id", 1 + parseInt(currCondCount));
							$divItem.addClass("tempConditionItem");
							$divItem.find(".conditionFormattedText").html(self.newFilterGroupText);
							$divItem.show();
							$divItemList.append($divItem);
							self.addToggleListener($divItem);
							self.switchIMSFields($divItem);
							self.addDeleteFilterGroupListener();
							$divItem.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");

							switch(self.defaultIMS){
							case "CatCode": 
								$divItem.find("a.switchToCatCode").triggerHandler("click"); 
								break;
							case "CatName": 
								$divItem.find("a.switchToCatName").triggerHandler("click"); 
								break;
							}
							self.addIMSFieldListener($divItem);
						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			switchIMSFields:function(ui, condition){
				var self = this;

				ui.find("a.switchToCatCode,a.switchToCatName").off().on({
					click: function(e){
						var $item = $(this).parents(".conditionItem");
						var $table = $item.find("table.imsFields");
						switch($(e.currentTarget).attr("class")){
						case "switchToCatName" : 
							$table.find("tr.catCode").hide();
							$table.find("tr.catName").show();
							break;
						case "switchToCatCode" : 
							$table.find("tr.catCode").show();
							$table.find("tr.catName").hide();
							break;
						}
						self.addIMSFieldListener(ui, e.data.condition);
					}
				},{condition: condition});	
			},

			showEmptyFilterGroup: function(){
				var $divItemList = $("div#conditionList");

				if ($divItemList.find("div.conditionItem:not(#conditionItemPattern)").length==0){
					$divItemList.find("div#emptyConditionItem").show();
				}else{
					$divItemList.find("div#emptyConditionItem").hide();
				}
			},

			addDeleteFilterGroupListener: function(){
				var self = this;
				var $divItemList = $("div#conditionList");

				$divItemList.find("img.deleteIcon,a.deleteBtn").off().on({
					click: function(e){
						var $item = $(this).parents(".conditionItem");
						var readableString = $item.find(".conditionFormattedText").html();
						if (!e.data.locked && confirm("Delete rule condition: \n" + readableString)){
							if ($item.hasClass("tempConditionItem")){
								$item.remove();
								self.showEmptyFilterGroup();
							}else{
								RedirectServiceJS.deleteConditionInRule(self.selectedRule["ruleId"], $item.attr("id"),{
									callback:function(code){
										showActionResponse(code, "delete", readableString);
										if(code==1){
											$item.remove();
											self.showEmptyFilterGroup();
										}
									}
								});
							}
						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});	
			},

			showRuleCondition: function(){
				var self = this;

				self.addNewFilterGroupListener();

				var $divItemList = $("div#conditionList");
				$divItemList.find("div.conditionItem:not(#conditionItemPattern)").remove();

				RedirectServiceJS.getConditionInRule(self.selectedRule["ruleId"], 0, 0, {
					callback: function(data){
						if(data!=null && data.totalSize > 0){
							var list = data.list;

							for(var i=0; i < list.length; i++){
								var item = list[i];
								var $divItem = $divItemList.find('div#conditionItemPattern').clone();
								$divItem.prop("id", item["sequenceNumber"]);
								$divItem.find(".conditionFormattedText").html(item["readableString"]);

								var $table = $divItem.find("table.imsFields");

								if($.isNotBlank(item) && $.isEmptyObject(item.IMSFilters)){
									$divItem.find("div.ims").hide();
								}

								if(item["imsUsingCategory"]){
									$table.find("tr.catName").show();
									$table.find("tr.catCode").hide();
								}else{
									$table.find("tr.catName").hide();
									$table.find("tr.catCode").show();
								}

								$divItem.show();
								$divItemList.append($divItem);
								self.addToggleListener($divItem, item);
								self.switchIMSFields($divItem, item);
							}

							self.addDeleteFilterGroupListener();

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