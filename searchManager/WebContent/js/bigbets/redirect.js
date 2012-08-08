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
			templateAttributes: null,

			addDownloadListener: function(){
				var self = this;
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

			addDeleteRuleListener: function(){
				var self = this;

				$("#deleteBtn").off().on({
					click: function(e){
						if (!e.data.locked && confirm("Delete " + self.selectedRule["ruleName"] + "'s rule?")){
							RedirectServiceJS.deleteRule(self.selectedRule,{
								callback: function(code){
									showActionResponse(code, "delete", self.selectedRule["ruleName"]);
									if(code==1) {
										self.selectedRule = null;
										self.setRedirect(null);
									}
								}
							});
						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			addSaveRuleListener: function(){
				var self = this;

				$("#saveBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						var ruleName = $.trim($('div#redirect input[id="name"]').val());  
						var description = $.trim($('div#redirect textarea[id="description"]').val());  

						if (self.checkIfUpdateAllowed()){
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
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			prepareRedirect : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #redirect, #noSelected").hide();
				$("#titleHeader").html("");
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

				$("#submitForApproval").rulestatus({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					authorizeRuleBackup: true,
					authorizeSubmitForApproval: true, // TODO: verify if need to be controlled user access
					afterSubmitForApprovalRequest:function(ruleStatus){
						self.selectedRuleStatus = ruleStatus;
						self.showRedirect();
					},
					beforeRuleStatusRequest: function(){
						self.prepareRedirect();	
					},
					afterRuleStatusRequest: function(ruleStatus){
						$("#submitForApproval").show();
						$("#preloader").hide();
						$("#titleText").html(self.moduleName + " for ");
						$("#titleHeader").html(self.selectedRule["ruleName"]);
						self.selectedRuleStatus = ruleStatus;
						$("#redirect").show();
						$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));


						$("#name").val(self.selectedRule["ruleName"]);
						$("#description").val(self.selectedRule["description"]);

						self.getKeywordInRuleList(1);
						self.refreshTabContent();
						self.addTabListener();
						self.addSaveRuleListener();
						self.addDeleteRuleListener();
						self.addDownloadListener();

						$('#auditIcon').on({
							click: showAuditList
						}, {locked: self.selectedRuleStatus["locked"] || !allowModify, type:self.moduleName, ruleRefId: self.selectedRule["ruleId"], name:  self.selectedRule["ruleName"]});

						$("div#keyword").find('input[type="text"]#changeKeyword').val($.trim(self.selectedRule["changeKeyword"]));

						$("div#keyword").find("#changeKeywordBtn").off().on({
							mouseenter: showHoverInfo,
							click: function(evt){
								if (!evt.data.locked){
									$('div#keyword').find('#activerules').hide();
									$('div#keyword').find('#activerules > .alert > #rules').empty();
									self.updateChangeKeyword();
								}
							}
						},{locked: self.selectedRuleStatus["locked"] || !allowModify});
					}
				});
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

					showAddButton: !self.selectedRuleStatus["locked"] && allowModify,
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
								if (!e.data.locked && confirm('Delete "' + name + '" in ' + self.selectedRule["ruleName"]  + '?'))
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
						if (!self.selectedRuleStatus["locked"] && allowModify){
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
				self.showRedirect();
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
												self.selectedRule = data;
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
				var self = this;
				var ruleName = $.trim($('div#redirect input[id="name"]').val());  
				var description = $.trim($('div#redirect textarea[id="description"]').val());  
				isDirty = false;

				isDirty = isDirty || (ruleName.toLowerCase()!==$.trim(self.selectedRule["ruleName"]).toLowerCase());
				isDirty = isDirty || (description.toLowerCase()!==$.trim(self.selectedRule["description"]).toLowerCase());

				return isDirty;
			},

			getChangeKeywordActiveRules : function(){
				var self = this;
				var $input = $("div#keyword").find('input[type="text"]#changeKeyword');
				var $preloader = $("div#keyword").find('#preloader');

				$input.val(self.selectedRule["changeKeyword"]).prop({disabled: self.selectedRuleStatus["locked"] || !allowModify});

				if ($.isNotBlank($input.val()))
					$('div#keyword').find('#activerules > .alert > #rules').activerule({
						keyword: $input.val(), 
						beforeRequest: function(){
							$preloader.show();
							$input.prop({disabled:true});
							$('div#keyword').find('#activerules > .alert > #rules').empty();
							$('div#keyword').find('#activerules').hide();
						},
						afterRequest: function(){
							$preloader.hide();
							$input.prop({disabled: self.selectedRuleStatus["locked"] || !allowModify});
							$('div#keyword').find('#activerules').show();
						}
					});
			},

			updateChangeKeyword : function(){
				var self = this;
				var $input = $("div#keyword").find('input[type="text"]#changeKeyword');
				var $preloader = $("div#keyword").find('#preloader');

				RedirectServiceJS.setChangeKeyword(self.selectedRule["ruleId"], $.trim($input.val()), {
					callback: function(data){
						if (data>0){
							self.selectedRule["changeKeyword"] = $.trim($input.val());
						}else{
							$input.val(self.selectedRule["changeKeyword"]);
						}
					},
					preHook: function(){
						$preloader.show();
						$input.prop({disabled:true});
					},
					postHook: function(){
						$preloader.hide();
						$input.prop({disabled: self.selectedRuleStatus["locked"] || !allowModify});
						self.getChangeKeywordActiveRules();
					}
				});
			},

			setActiveRedirectType : function(){
				var self = this;

				$('input[type="checkbox"].activate').prop({checked:false, disabled: false });

				$('input[type="checkbox"].activate').prop({disabled: self.selectedRuleStatus["locked"] || !allowModify }).off().on({
					click:function(e){
						if (e.data.locked) return;

						var typeId = 1;
						switch(self.tabSelectedTypeId){
						case "#filter": typeId = 1; break;
						case "#keyword": typeId = 2; break; 
						case "#page": typeId = 3; break; 
						}

						self.updateActiveRedirectType(typeId);
					},
					mouseenter: showHoverInfo
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify});

				switch(parseInt(self.selectedRule["redirectTypeId"])){
				case 1: $("div#filter").find('input#activate').prop({checked:true, disabled: true }); break;
				case 2: $("div#keyword").find('input#activate').prop({checked:true, disabled: true }); break;
				case 3: $("div#page").find('input#activate').prop({checked:true, disabled: true }); break;
				};

			},

			updateActiveRedirectType : function(typeId){
				var self = this;
				RedirectServiceJS.setRedirectType(self.selectedRule["ruleId"], typeId, {
					callback: function(data){
						self.selectedRule["redirectTypeId"] = parseInt(typeId);
						self.setActiveRedirectType();
					}
				});
			},

			setIncludeKeyword : function(){
				var self = this;

				$('input[type="checkbox"].includeKeyword').prop({checked:false});

				RedirectServiceJS.getRule(self.selectedRule["ruleId"], {
					callback: function(data){
						if($.isNotBlank(data["includeKeyword"])){
							$("div#filter").find('input#includeKeyword').prop({checked:data["includeKeyword"] });
						}
					}
				});

				$('input[type="checkbox"].includeKeyword').prop({disabled: self.selectedRuleStatus["locked"] || !allowModify }).off().on({
					click:function(e){
						if (e.data.locked) return;

						var isIncludeKeyword = $('input[type="checkbox"].includeKeyword')[0].checked;

						self.updateIncludeKeyword(isIncludeKeyword);
					},
					mouseenter: showHoverInfo
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify});
			},

			updateIncludeKeyword : function(isIncludeKeyword){
				var self = this;
				RedirectServiceJS.setIncludeKeyword(self.selectedRule["ruleId"], isIncludeKeyword, {
					callback: function(data){
						self.setIncludeKeyword();
					}
				});
			},

			populateLevel1Categories: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#level1CategoryList");
				var $input = ui.find("input#level1CategoryList");
				var $table = ui.find("table.cnetFields");

				CategoryServiceJS.getCNETLevel1Categories({
					callback: function(data){
						var list = data;

						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if($.isNotBlank($input.val())) self.populateLevel2Categories(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderLevel1CategoryList").show();
						self.clearCNETComboBox(ui, "level1Cat");
						$table.find("tr#level2Cat, tr#level3Cat").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Level1Category"])){
							$select.prop("selectedText",condition.CNetFilters["Level1Category"]);
							$input.val(condition.CNetFilters["Level1Category"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderLevel1CategoryList").hide();
						if($.isBlank($input.val()))
							self.populateCNETManufacturers(ui, condition, e);
					}
				});
			},

			populateLevel2Categories: function(ui, condition, e){
				var self = this;
				var inLevel1Category = $.trim(ui.find("input#level1CategoryList").val());
				var $select = ui.find("select#level2CategoryList");
				var $input = ui.find("input#level2CategoryList");
				var $table = ui.find("table.cnetFields");

				CategoryServiceJS.getCNETLevel2Categories(inLevel1Category, {
					callback: function(data){
						var list = data;

						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#level2Cat").show();
						}else{
							$table.find("tr#level2Cat").hide();
						}  

						if($.isNotBlank($input.val())) self.populateLevel3Categories(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderLevel2CategoryList").show();
						self.clearCNETComboBox(ui, "level2Cat");
						$table.find("tr#level3Cat").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Level2Category"])){
							$select.prop("selectedText",condition.CNetFilters["Level2Category"]);
							$input.val(condition.CNetFilters["Level2Category"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderLevel2CategoryList").hide();
						if($.isNotBlank(inLevel1Category) && $.isBlank($input.val()))
							self.populateCNETManufacturers(ui, condition, e);
					}
				});
			},

			populateLevel3Categories: function(ui, condition, e){
				var self = this;
				var inLevel1Category = $.trim(ui.find("input#level1CategoryList").val());
				var inLevel2Category = $.trim(ui.find("input#level2CategoryList").val());
				var $select = ui.find("select#level3CategoryList");
				var $input = ui.find("input#level3CategoryList");
				var $table = ui.find("table.cnetFields");

				CategoryServiceJS.getCNETLevel3Categories(inLevel1Category, inLevel2Category, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#level3Cat").show();
						}else{
							$table.find("tr#level3Cat").hide();
						}  

					},
					preHook:function(){
						ui.find("img#preloaderLevel3CategoryList").show();
						self.clearCNETComboBox(ui, "level3Cat");
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Level3Category"])){
							$select.prop("selectedText",condition.CNetFilters["Level3Category"]);
							$input.val(condition.CNetFilters["Level3Category"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderLevel3CategoryList").hide();
						self.populateCNETManufacturers(ui, condition, e);
					}
				});
			},

			populateCNETManufacturers: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#cnetmanufacturerList");
				var $input = ui.find("input#cnetmanufacturerList");

				var inLevel1Category = "";
				var inLevel2Category = "";
				var inLevel3Category = "";

				inLevel1Category = $.trim(ui.find("input#level1CategoryList").val());
				inLevel2Category = $.trim(ui.find("input#level2CategoryList").val());
				inLevel3Category = $.trim(ui.find("input#level3CategoryList").val());

				CategoryServiceJS.getCNETManufacturers(inLevel1Category, inLevel2Category, inLevel3Category, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderCNETManufacturerList").show();
						self.clearCNETComboBox(ui, "cnetmanufacturer");
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Manufacturer"])){
							$select.prop("selectedText",condition.CNetFilters["Manufacturer"]);
							$input.val(condition.CNetFilters["Manufacturer"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderCNETManufacturerList").hide();
					}
				});
			}, 

			populateCategories: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#categoryList");
				var $input = ui.find("input#categoryList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSCategories({
					callback: function(data){
						var list = data;

						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if($.isNotBlank($input.val())) self.populateSubcategories(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderCategoryList").show();
						self.clearIMSComboBox(ui, "category");
						$table.find("tr#subcategory,tr#class,tr#minor").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Category"])){
							$select.prop("selectedText",condition.IMSFilters["Category"]);
							$input.val(condition.IMSFilters["Category"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderCategoryList").hide();
						if($.isBlank($input.val()))
							self.populateManufacturers(ui, condition, e);
					}
				});
			},

			populateSubcategories: function(ui, condition, e){
				var self = this;
				var inCategory = $.trim(ui.find("input#categoryList").val());
				var $select = ui.find("select#subCategoryList");
				var $input = ui.find("input#subCategoryList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSSubcategories(inCategory, {
					callback: function(data){
						var list = data;

						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#subcategory").show();
						}else{
							$table.find("tr#subcategory").hide();
						}  

						if($.isNotBlank($input.val())) self.populateClass(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderSubCategoryList").show();
						self.clearIMSComboBox(ui, "subcategory");
						$table.find("tr#class,tr#minor").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["SubCategory"])){
							$select.prop("selectedText",condition.IMSFilters["SubCategory"]);
							$input.val(condition.IMSFilters["SubCategory"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderSubCategoryList").hide();
						if($.isNotBlank(inCategory) && $.isBlank($input.val()))
							self.populateManufacturers(ui, condition, e);
					}
				});
			},

			populateClass: function(ui, condition, e){
				var self = this;
				var inCategory = $.trim(ui.find("input#categoryList").val());
				var inSubCategory = $.trim(ui.find("input#subCategoryList").val());
				var $select = ui.find("select#classList");
				var $input = ui.find("input#classList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSClasses(inCategory,inSubCategory, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#class").show();
						}else{
							$table.find("tr#class").hide();
						}  

						if($.isNotBlank($input.val())) self.populateMinor(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderClassList").show();
						self.clearIMSComboBox(ui, "class");
						$table.find("tr#minor").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Class"])){
							$select.prop("selectedText",condition.IMSFilters["Class"]);
							$input.val(condition.IMSFilters["Class"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderClassList").hide();
						if($.isNotBlank(inSubCategory) && $.isBlank($input.val()))
							self.populateManufacturers(ui, condition, e);
					}
				});
			},

			populateMinor: function(ui, condition, e){
				var self = this;
				var inCategory = $.trim(ui.find("input#categoryList").val());
				var inSubCategory = $.trim(ui.find("input#subCategoryList").val());
				var inClass = $.trim(ui.find("input#classList").val());
				var $select = ui.find("select#minorList");
				var $input = ui.find("input#minorList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSMinors(inCategory,inSubCategory, inClass, {
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#minor").show();
						}else{
							$table.find("tr#minor").hide();
						}  
					},
					preHook:function(){
						ui.find("img#preloaderMinorList").show();
						self.clearIMSComboBox(ui, "minor");
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["SubClass"])){
							$select.prop("selectedText",condition.IMSFilters["SubClass"]);
							$input.val(condition.IMSFilters["SubClass"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderMinorList").hide();
						self.populateManufacturers(ui, condition, e);
					}
				});
			},

			populateManufacturers: function(ui, condition, e){
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
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Manufacturer"])){
							$select.prop("selectedText",condition.IMSFilters["Manufacturer"]);
							$input.val(condition.IMSFilters["Manufacturer"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderManufacturerList").hide();
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

					$facet.find("input#nameContains").val(condition.facets["Name"]);
					$facet.find("input#descriptionContains").val(condition.facets["Description"]);

				}
			},

			addFacetTemplateFieldListener: function(ui, condition){
				var self = this;
				var $dynamicAttribute = ui.find("div.dynamicAttribute");

				$dynamicAttribute.find("select.selectCombo").combobox({
					selected: function(e, u){
						var $item = $(this).parents(".conditionItem");
						switch($(this).attr("id").toLowerCase()){
						case "templatenamelist" :
							if(u.item){
								$item.find("input#templateNameList").val(u.item.text);
								$item.find("input#templateNameList").prop("selectedText", u.item.text);
								
								if (ui.find("div.ims").is(":visible"))
									self.populateIMSDynamicAttributes(ui, condition, e);
								else if (ui.find("div.cnet").is(":visible"))
									self.populateCNETDynamicAttributes(ui, condition, e);
							}

							$item.find("input#dynamicAttributeList").val("");
							break;
						
						case "dynamicattributelist" :
							if(u.item){
								$item.find("input#dynamicAttributeList").val(u.item.text);
								$item.find("input#dynamicAttributeList").prop("selectedText", u.item.text);
								self.addDynamicAttributeButtonListener(ui, condition, u.item.value);
							}
							break;
						}
					}
				});
				
				if (ui.find("div.ims").is(":visible"))
					self.populateIMSTemplateNames(ui, condition);
				else if (ui.find("div.cnet").is(":visible"))
					self.populateCNETTemplateNames(ui, condition);
			},

			populateIMSTemplateNames: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#templateNameList");
				var $input = ui.find("input#templateNameList");
				var $table = ui.find("table.dynamicAttributeFields");

				var inTemplateName = $.trim($input.val());

				CategoryServiceJS.getIMSTemplateNames({
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						
						if($.isNotBlank(inTemplateName)) self.populateIMSDynamicAttributes(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderTemplateNameList").show();
						self.clearDynamicAttributeComboBox(ui, "dynamicAttributeName");
						$table.find("tr#dynamicAttributeName").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.dynamicAttributes)){
							$select.prop("selectedText",condition.dynamicAttributes["TemplateName"]);
							$input.val(condition.dynamicAttributes["TemplateName"]);
						}
					},
					postHook:function(){
						ui.find("img#preloaderTemplateNameList").hide();
					}
				});
			},

			populateCNETTemplateNames: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#templateNameList");
				var $input = ui.find("input#templateNameList");
				var $table = ui.find("table.dynamicAttributeFields");

				var inTemplateName = $.trim($input.val());

				CategoryServiceJS.getCNETTemplateNames({
					callback: function(data){
						var list = data;
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						
						if($.isNotBlank(inTemplateName)) self.populateCNETDynamicAttributes(ui, condition, e);
					},
					preHook:function(){
						ui.find("img#preloaderTemplateNameList").show();
						self.clearDynamicAttributeComboBox(ui, "dynamicAttributeName");
						$table.find("tr#dynamicAttributeName").hide();
						if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.dynamicAttributes)){
							$select.prop("selectedText",condition.dynamicAttributes["TemplateName"]);
							$input.val(condition.dynamicAttributes["TemplateName"]);
						}

					},
					postHook:function(){
						ui.find("img#preloaderTemplateNameList").hide();
					}
				});
			},

			populateIMSDynamicAttributes: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#dynamicAttributeList");
				var $input = ui.find("input#dynamicAttributeList");
				var $templateName = ui.find("input#templateNameList");
				var $table = ui.find("table.dynamicAttributeFields");

				var inTemplateName = $.trim($templateName.val());

				CategoryServiceJS.getIMSTemplateAttributes(inTemplateName, {
					callback: function(data){
						self.templateAttributes = data;
						var isEmpty = true;
						
						$.each(self.templateAttributes, function(attrName, attrData) { 
							$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
							isEmpty = false;
						});
						
						if (!isEmpty){
							$table.find("tr#dynamicAttributeName").show();
						}else{
							$table.find("tr#dynamicAttributeName").hide();
						}
						
						
					},
					preHook:function(){
						ui.find("img#preloaderDynamicAttributeList").show();
						self.clearDynamicAttributeComboBox(ui, "dynamicattributelist");
						//$table.find("tr#dynamicAttributeValue").hide();
						/*if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.dynamicAttributes)){
						$select.prop("selectedText",condition.dynamicAttributes["TemplateName"]);
						$input.val(condition.dynamicAttributes["TemplateName"]);
						}*/
					},
					postHook:function(){
						ui.find("img#preloaderDynamicAttributeList").hide();
					}
				});
			},
			
			populateCNETDynamicAttributes: function(ui, condition, e){
				var self = this;
				var $select = ui.find("select#dynamicAttributeList");
				var $input = ui.find("input#dynamicAttributeList");
				var $templateName = ui.find("input#templateNameList");
				var $table = ui.find("table.dynamicAttributeFields");

				var inTemplateName = $.trim($templateName.val());

				CategoryServiceJS.getCNETTemplateAttributes(inTemplateName, {
					callback: function(data){
						self.templateAttributes = data;
						var isEmpty = true;
						
						$.each(self.templateAttributes, function(attrName, attrData) { 
							$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
							isEmpty = false;
						});
						
						if (!isEmpty){
							$table.find("tr#dynamicAttributeName").show();
						}else{
							$table.find("tr#dynamicAttributeName").hide();
						}
					},
					preHook:function(){
						ui.find("img#preloaderDynamicAttributeList").show();
						self.clearDynamicAttributeComboBox(ui, "dynamicattributelist");
						//$table.find("tr#dynamicAttributeValue").hide();
						//if (!e && $.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Manufacturer"])){
						//$select.prop("selectedText",condition.IMSFilters["Manufacturer"]);
						//$input.val(condition.IMSFilters["Manufacturer"]);
						//}
					},
					postHook:function(){
						ui.find("img#preloaderDynamicAttributeList").hide();
					}
				});
			},

			initializeIMSFilters: function(comboboxId, ui, condition){
				var self = this;
				var $ims = ui.find("div.ims");
				var $input = $ims.find("input#"+comboboxId);

				switch(comboboxId.toLowerCase()){
				case "catcode":
					$input.val(condition.IMSFilters["CatCode"]);
					break;
				case "categorylist" :
					$input.val(condition.IMSFilters["Category"]);
					break;
				case "subcategorylist" : 
					$input.val(condition.IMSFilters["SubCategory"]);
					break;
				case "classlist" : 
					$input.val(condition.IMSFilters["Class"]);
					break;
				case "minorlist" : 
					$input.val(condition.IMSFilters["SubClass"]);
					break;
				case "manufacturerlist" : 
					$input.val(condition.IMSFilters["Manufacturer"]);
					break;
				}
			},

			initializeCNETFilters: function(comboboxId, ui, condition){
				var self = this;
				var $ims = ui.find("div.cnet");
				var $input = $ims.find("input#"+comboboxId);

				switch(comboboxId.toLowerCase()){
				case "level1categorylist":
					$input.val(condition.CNetFilters["Level1Category"]);
					break;
				case "level2categorylist" :
					$input.val(condition.CNetFilters["Level1Category"]);
					break;
				case "level3categorylist" : 
					$input.val(condition.CNetFilters["Level1Category"]);
					break;
				case "cnetmanufacturerlist" : 
					$input.val(condition.CNetFilters["Manufacturer"]);
					break;
				}
			},

			addCNETFieldListener: function(ui, condition){
				var self = this;
				var $cnet = ui.find("div.cnet");

				if($.isBlank($cnet) && $cnet.is(":not(:visible)")){
					return;
				}

				var updateCNETCombobox = function (target, e, u){
					var $item = $(target).parents(".conditionItem");
					switch($(target).attr("id").toLowerCase()){
					case "level1categorylist" :
						if(u.item){
							$item.find("input#level1CategoryList").val(u.item.text);
							$item.find("input#level1CategoryList").prop("selectedText", u.item.text);
							self.populateLevel2Categories(ui, condition, e);
						}
						else self.populateLevel1Categories(ui, condition, e);

						$item.find("input#level2CategoryList").val("");
						$item.find("input#level3CategoryList").val("");
						$item.find("input#cnetmanufacturerList").val("");
						break;
					case "level2categorylist" : 
						if(u.item){
							$item.find("input#level2CategoryList").val(u.item.text);
							$item.find("input#level2CategoryList").prop("selectedText", u.item.text);
							self.populateLevel3Categories(ui, condition, e);
							$item.find("input#level3CategoryList").val("");
						}
						else self.populateLevel2Categories(ui, condition, e);

						$item.find("input#cnetmanufacturerList").val("");
						break;
					case "level3categorylist" : 
						if(u.item){
							$item.find("input#level3CategoryList").val(u.item.text);
							$item.find("input#level3CategoryList").prop("selectedText", u.item.text);
							self.populateCNETManufacturers(ui, condition, e);
						}
						else self.populateLevel3Categories(ui, condition, e);

						$item.find("input#cnetmanufacturerList").val("");
						break;
					case "cnetmanufacturerlist" : 
						if(u.item){
							$item.find("input#cnetmanufacturerList").val(u.item.text);
							$item.find("input#cnetmanufacturerList").prop("selectedText",u.item.text);
						} 
						break;
					}
				};


				$cnet.find("select.selectCombo").combobox({
					change: function(e, u){
						updateCNETCombobox(this, e, u);
					},
					selected: function(e, u){
						updateCNETCombobox(this, e, u);
					}
				});
			},

			addIMSFieldListener: function(ui, condition){
				var self = this;
				var $ims = ui.find("div.ims");

				if($.isBlank($ims) && $cnet.is(":not(:visible)")){
					return;
				}

				var updateIMSCombobox = function(target, e, u){
					var $item = $(target).parents(".conditionItem");
					switch($(target).attr("id").toLowerCase()){
					case "categorylist" :
						if(u.item){
							$item.find("input#categoryList").val(u.item.text);
							$item.find("input#categoryList").prop("selectedText", u.item.text);
							self.populateSubcategories(ui, condition, e);
						}
						else self.populateCategories(ui, condition, e);

						$item.find("input#subCategoryList").val("");
						$item.find("input#classList").val("");
						$item.find("input#minorList").val("");
						$item.find("input#manufacturerList").val("");
						break;
					case "subcategorylist" :
						if(u.item){
							$item.find("input#subCategoryList").val(u.item.text);
							$item.find("input#subCategoryList").prop("selectedText", u.item.text);
							self.populateClass(ui, condition, e);
						}
						else self.populateSubcategories(ui, condition, e);

						$item.find("input#classList").val("");
						$item.find("input#minorList").val("");
						$item.find("input#manufacturerList").val("");
						break;
					case "classlist" : 
						if(u.item){
							$item.find("input#classList").val(u.item.text);
							$item.find("input#classList").prop("selectedText", u.item.text);
							self.populateMinor(ui, condition, e);
						}
						else self.populateClass(ui, condition, e);

						$item.find("input#minorList").val("");
						$item.find("input#manufacturerList").val("");
						break;
					case "minorlist" : 
						if(u.item){
							$item.find("input#minorList").val(u.item.text);
							$item.find("input#minorList").prop("selectedText", u.item.text); 
							self.populateManufacturers(ui, condition, e);
						}
						else self.populateMinor(ui, condition, e);

						$item.find("input#manufacturerList").val("");
						break;
					case "manufacturerlist" : 
						if(ui.item){
							$item.find("input#manufacturerList").val(u.item.text);
							$item.find("input#manufacturerList").prop("selectedText", u.item.text); 
						}
						break;
					}
				};

				$ims.find("select.selectCombo").combobox({
					change: function(e, u){
						updateIMSCombobox(this, e, u);
					},
					selected: function(e, u){
						updateIMSCombobox(this, e, u);
					}
				});

				$ims.find("a.switchToCatCode,a.switchToCatName").off().on({
					click: function(e){
						var $item = $(this).parents(".conditionItem");
						var $table = $item.find("table.imsFields");

						switch($(e.currentTarget).attr("class")){
						case "switchToCatName" : 
							$table.find("tr.catCode").hide();
							$table.find("tr.catName").show();
							self.populateCategories(ui, e.data.condition, e);
							break;
						case "switchToCatCode" : 
							$table.find("tr.catCode").show();
							$table.find("tr.catName").hide();
							self.populateManufacturers(ui, e.data.condition, e);
							break;
						}
					}
				},{condition: condition});

				var $input = $ims.find("input#catcode");


				$input.off().on({
					focusout: function(e){
						self.populateManufacturers(ui, e.data.condition, e);
					}
				},{condition: condition});
			},

			checkDisplay: function(ui, condition){
				var self = this;
				var selectedFilter = $("select#filterGroup option:selected").val();
				var $cnet = ui.find("div.cnet");
				var $ims = ui.find("div.ims");

				ui.find("div.cnet, div.ims, div.dynamicAttribute").hide();

				if(($.isBlank(condition) && selectedFilter === "cnet") || ($.isNotBlank(condition) && condition.CNetFilter)){
					//ui.find("div.cnet").show();
					ui.find("div.cnet, div.dynamicAttribute").show();
					self.addCNETFieldListener(ui, condition);
					self.addFacetTemplateFieldListener(ui, condition);

					var $table = $cnet.find("table.cnetFields");
					$table.find("tr.catName").show();

					if ($.isNotBlank(condition)){
						self.initializeCNETFilters("level1CategoryList", ui, condition);
						self.initializeCNETFilters("level2CategoryList", ui, condition);
						self.initializeCNETFilters("level3CategoryList", ui, condition);
						self.initializeCNETFilters("cnetmanufacturerList", ui, condition);
					}

					self.populateLevel1Categories(ui, condition);
					self.populateCNETTemplateNames(ui, condition);
				}
				else if(($.isBlank(condition) && selectedFilter === "ims") ||  ($.isNotBlank(condition) && condition.IMSFilter)){
					//ui.find("div.ims").show();
					
					ui.find("div.ims, div.dynamicAttribute").show();
					self.addIMSFieldListener(ui, condition);
					self.addFacetTemplateFieldListener(ui, condition);

					var usingCategory = $.isNotBlank(condition) && condition["imsUsingCategory"];
					var usingCatCode = $.isNotBlank(condition) && condition["imsUsingCatCode"];
					var $table = $ims.find("table.imsFields");

					if ((usingCategory && !usingCatCode) || ui.find("a.switchToCatCode").is(":visible")){
						$table.find("tr.catName").show();
						$table.find("tr.catCode").hide();

						if ($.isNotBlank(condition)){
							self.initializeIMSFilters("categoryList", ui, condition);
							self.initializeIMSFilters("subCategoryList", ui, condition);
							self.initializeIMSFilters("classList", ui, condition);
							self.initializeIMSFilters("minorList", ui, condition);
							self.initializeIMSFilters("manufacturerList", ui, condition);
						}

						self.populateCategories(ui, condition);
					}else{
						$table.find("tr.catName").hide();
						$table.find("tr.catCode").show();

						if ($.isNotBlank(condition)){
							self.initializeIMSFilters("catcode", ui, condition);
							self.initializeIMSFilters("manufacturerList", ui, condition);
						}

						self.populateManufacturers(ui, condition);
					}
					self.populateIMSTemplateNames(ui, condition);
				}
			},

			clearDynamicAttributeComboBox: function(ui, trigger){
				var self = this;
				var $dynamicAttribute = ui.find("div.dynamicAttribute");

				if ($.isBlank(trigger)){
					$dynamicAttribute.find("input").val("");
					$dynamicAttribute.find("select.selectCombo option").remove();
				}else{
					switch (trigger.toLowerCase()){
					case "templatename": 
						$dynamicAttribute.find("input#templateNameList").val("");
						$dynamicAttribute.find("select#templateNameList option").remove();
					case "dynamicattributelist": 
						$dynamicAttribute.find("input#dynamicAttributeList").val("");
						$dynamicAttribute.find("select#dynamicAttributeList option").remove();
					}
				}
			},

			clearCNETComboBox: function(ui, trigger){
				var self = this;
				var $cnet = ui.find("div.cnet");

				if ($.isBlank(trigger)){
					$cnet.find("input").val("");
					$cnet.find("select.selectCombo option").remove();
				}else{
					switch (trigger.toLowerCase()){
					case "level1cat": 
						$cnet.find("input#level1CategoryList").val("");
						$cnet.find("select#level1CategoryList option").remove();
					case "level2cat": 
						$cnet.find("input#level2CategoryList").val("");
						$cnet.find("select#level2CategoryList option").remove();
					case "level3cat": 
						$cnet.find("input#level3CategoryList").val("");
						$cnet.find("select#level3CategoryList option").remove();
					case "cnetmanufacturer": 
						$cnet.find("input#cnetmanufacturerList").val("");
						$cnet.find("select#cnetmanufacturerList option").remove();	
					}
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
				var level1Cat = new Array();
				var level2Cat = new Array();
				var level3Cat = new Array();
				var cnetManufacturer = new Array();


				if (ui.find("div.ims").is(":visible")){
					catCode[0] = $.trim(ui.find("input#catcode").val());
					category[0] = $.trim(ui.find("input#categoryList").val());
					subCategory[0] = $.trim(ui.find("input#subCategoryList").val());
					clazz[0] = $.trim(ui.find("input#classList").val());
					minor[0] = $.trim(ui.find("input#minorList").val());
					manufacturer[0] = $.trim(ui.find("input#manufacturerList").val());

					if (ui.find("a.switchToCatName").is(":visible")){
						if ($.isNotBlank(catCode[0])) condMap["CatCode"] = catCode;
					}else{
						if ($.isNotBlank(category[0])) condMap["Category"] = category; 	
						if ($.isNotBlank(subCategory[0])) condMap["SubCategory"] = subCategory; 	
						if ($.isNotBlank(clazz[0])) condMap["Class"] = clazz; 	
						if ($.isNotBlank(minor[0])) condMap["SubClass"] = minor; 	
					}
					if ($.isNotBlank(manufacturer[0])) condMap["Manufacturer"] = manufacturer; 	
				}

				if (ui.find("div.cnet").is(":visible")){
					level1Cat[0] = $.trim(ui.find("input#level1CategoryList").val());
					level2Cat[0] = $.trim(ui.find("input#level2CategoryList").val());
					level3Cat[0] = $.trim(ui.find("input#level3CategoryList").val());
					cnetManufacturer[0] = $.trim(ui.find("input#cnetmanufacturerList").val());

					if ($.isNotBlank(level1Cat[0])) condMap["Level1Category"] = level1Cat; 	
					if ($.isNotBlank(level2Cat[0])) condMap["Level2Category"] = level2Cat; 	
					if ($.isNotBlank(level3Cat[0])) condMap["Level3Category"] = level3Cat; 	

					if ($.isNotBlank(cnetManufacturer[0])) condMap["Manufacturer"] = cnetManufacturer; 	
				}
				
				if(ui.find("div.dynamicAttribute").is(":visible")){
					var inTemplateName = ui.find("input#templateNameList").val();
					var $divDynamicAttrItems = ui.find("div.dynamicAttributeItem");
					
					condMap["TemplateName"] = $.makeArray(inTemplateName.trim());
					
					$divDynamicAttrItems.find("ul").each(function(){ 
						var attributeItem = this.id;
						var attributeValues = new Array();
						
						$("input:checkbox[name="+attributeItem+"]:checked").each(function(){
							attributeValues.push($(this).val()); 
						});

						if(attributeValues.length > 0)
							condMap[attributeItem] = attributeValues;
					});
				}

				if (ui.find("div.facet").is(":visible")){
					var platform = $.trim(ui.find("input#platformList").val());
					var condition = $.trim(ui.find("input#conditionList").val());
					var availability = $.trim(ui.find("input#availabilityList").val());
					var license = $.trim(ui.find("input#licenseList").val());
					var nameContains = $.trim(ui.find("input#nameContains").val());
					var descriptionContains = $.trim(ui.find("input#descriptionContains").val());

					switch(platform.toLowerCase()){
					case "universal": condMap["Platform"] = ["Universal"]; break;
					case "pc": condMap["Platform"] = ["PC"]; break;
					case "linux": condMap["Platform"] = ["Linux"]; break;
					case "macintosh": condMap["Platform"] = ["Macintosh"]; break;
					}

					switch(condition.toLowerCase()){
					case "refurbished": condMap["Condition"] = ["Refurbished"]; break;
					case "open box": condMap["Condition"] = ["Open Box"]; break;
					case "clearance": condMap["Condition"] = ["Clearance"]; break;
					}

					switch(availability.toLowerCase()){
					case "in stock": condMap["Availability"] = ["In Stock"]; break;
					case "call": condMap["Availability"] = ["Call"]; break;
					}

					switch(license.toLowerCase()){
					case "show license products only": condMap["License"] = ["Show License Products Only"]; break;
					case "show non-license products only": condMap["License"] = ["Show Non-License Products Only"]; break;
					}

					if($.isNotBlank(nameContains))
						condMap["Name"] = $.makeArray(nameContains);

					if($.isNotBlank(descriptionContains))
						condMap["Description"] = $.makeArray(descriptionContains);
				}

				return condMap;
			},

			addDynamicAttributeButtonListener: function(ui,condition, attrName){
				var self = this;

				ui.find("a.addDynamicAttrBtn").off().on({
					click: function(e){
						if (!e.data.locked){
							var $divItemList = ui.find('div#dynamicAttributeItemList');
							var $divDynamicAttributeItem = $divItemList.find('div#dynamicAttributeItemPattern').clone();
							var $input = ui.find("input#dynamicAttributeList");
							var inDynamicAttribute = $.trim($input.val());
							var inTemplateName = ui.find("input#templateNameList").val();
							var $ulAttributeValues = $divDynamicAttributeItem.find("ul#dynamicAttributeValues");
							
							if($.isNotBlank(inDynamicAttribute)){
								$ulAttributeValues.prop("id", attrName);
								var currCondCount = parseInt($divItemList.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern):last").attr("id"));
								if (!$.isNumeric(currCondCount)){
									currCondCount = 0; 
								}
								
								var countId = 1 + parseInt(currCondCount);
								$divDynamicAttributeItem.prop("id", "dynamicAttributeItem");
								
								var $dynamicAttributeLabel = $divDynamicAttributeItem.find('span#dynamicAttributeLabel');
								$dynamicAttributeLabel.html(inDynamicAttribute + ":");
								
								var attributeMap = self.templateAttributes;
								if(attributeMap && attributeMap[attrName]){
									var attributeValues = attributeMap[attrName].attributeValues;
									if(attributeValues){
										for(var i=0; i<attributeValues.length; i++){
											var $liAttributeValue = $ulAttributeValues.find("li#dynamicAttributeValuesPattern").clone();
											$liAttributeValue.prop("id", "dynamicAttributeValues" + countId);
											$liAttributeValue.show();
											
											var $attributeValueItem = $liAttributeValue.find("input.checkboxFilter");
											$attributeValueItem.prop({name:attrName, value:attributeValues[i]});
											
											$liAttributeValue.find("span#attributeValueName").text(attributeValues[i].split("|")[1]);
											
											//$liAttributeValue.append($attributeValueItem);
											$ulAttributeValues.append($liAttributeValue);
										}
									}
								}
	
								$divDynamicAttributeItem.prop("id", countId);
								$divDynamicAttributeItem.addClass("tempDynamicAttributeItem");
								$divDynamicAttributeItem.show();
								$divItemList.append($divDynamicAttributeItem);
							}
							else{
								//alert("");
							}
						}
					},
					mouseenter: showHoverInfo
				},{condition: condition, locked:self.selectedRuleStatus["locked"] || !allowModify});	
			},
			
			addSaveButtonListener: function(ui, condition){
				var self = this;

				var $saveBtn = ui.find(".saveBtn");

				$saveBtn.find("div.buttons").html(ui.hasClass("tempConditionItem")? "Save": "Update");

				$saveBtn.off().on({
					click:function(e){

						if (e.data.locked) return;

						var $item = $(this).parents(".conditionItem");
						var condMap = self.buildConditionAsMap($item);

						if ($.isEmptyObject(condMap)){
							alert('Please specify at least one filter condition');
							return;
						}

						if ($item.hasClass("tempConditionItem")){
							RedirectServiceJS.addCondition(self.selectedRule["ruleId"], condMap, {
								callback:function(data){
									if (data!=null){
										var list = data.list;
										var newItem = list[data.totalSize-1];
										$item.removeClass("tempConditionItem");
										$item.find("a.conditionFormattedText").html(newItem["readableString"]);
										$item.attr("id",newItem["sequenceNumber"]);
										self.addToggleListener($item, newItem);
										self.addCloneFilterGroupListener($item, newItem);
										self.addDeleteFilterGroupListener($item, newItem);
										$item.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");
									};
								},
								preHook:function(){
									$item.find("img#preloaderUpdating").show();
								},
								postHook:function(){
									$item.find("img#preloaderUpdating").hide();
								}
							});
						}else{
							RedirectServiceJS.updateCondition(self.selectedRule["ruleId"], $item.attr("id"), condMap, {
								callback:function(data){
									if (data!=null){
										var list = data.list;
										var updatedItem = null;

										for (var item in list){
											if (parseInt(list[item]["sequenceNumber"])==parseInt($item.attr("id"))){
												updatedItem = list[item];
											};
										}	
										$item.find("a.conditionFormattedText").html(updatedItem["readableString"]);
										self.addToggleListener($item, updatedItem);
										self.addCloneFilterGroupListener($item, updatedItem);
										self.addDeleteFilterGroupListener($item, updatedItem);
										$item.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");
									};
								},
								preHook:function(){
									$item.find("img#preloaderUpdating").show();
								},
								postHook:function(){
									$item.find("img#preloaderUpdating").hide();
								}
							});
						};
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
								self.checkDisplay($item, e.data.condition);
								self.addFacetFieldListener($item, e.data.condition);
								self.addSaveButtonListener($item, e.data.condition);
							});
						}
					}
				},{condition: condition});
			},

			addCloneFilterGroupListener: function(ui,condition){
				var self = this;

				ui.find("img.cloneIcon,a.cloneBtn").off().on({
					click: function(e){
						if (!e.data.locked){
							var $divItemList = $("div#conditionList");

							if ($.isBlank(e.data.condition) || $divItemList.find("div.tempConditionItem").length > 0){
								alert("You have an unsaved filter group");
								return;
							}

							var readableString = e.data.condition["readableString"];

							var $divItem = $divItemList.find('div#conditionItemPattern').clone();

							var currCondCount = parseInt($divItemList.find("div.conditionItem:not(#conditionItemPattern):last").attr("id"));
							if (!$.isNumeric(currCondCount)){
								currCondCount = 0; 
							}

							$divItem.prop("id", 1 + parseInt(currCondCount));
							$divItem.addClass("tempConditionItem");
							$divItem.find(".conditionFormattedText").html('<span class="fred fbold">Cloned </span>' + readableString);
							$divItem.show();
							$divItemList.append($divItem);

							self.checkDisplay($divItem, e.data.condition);

							self.addToggleListener($divItem, e.data.condition);
							self.addCloneFilterGroupListener($divItem, e.data.condition);
							self.addDeleteFilterGroupListener($divItem, e.data.condition);

							$divItem.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");
						}
					},
					mouseenter: showHoverInfo
				},{condition: condition, locked:self.selectedRuleStatus["locked"] || !allowModify});	
			},

			addNewFilterGroupListener: function(){
				var self = this;

				$("#addFilterGroupBtn").off().on({
					click: function(e){
						if(!e.data.locked){
							var $divItemList = $("div#conditionList");

							if ($divItemList.find("div.tempConditionItem").length > 0){
								alert("You have an unsaved filter group");
								return;
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

							switch($("select#filterGroup option:selected").val()){
							case "cnet": 
								$divItem.find("div.ims").remove();
								$divItem.find("div.cnet, div.facet, div.dynamicAttribute").show();
								break;
							case "ims": 
								$divItem.find("div.cnet").remove();
								$divItem.find("div.ims, div.facet, div.dynamicAttribute").show();

								var $table = $divItem.find("table.imsFields");

								switch(self.defaultIMS){
								case "CatCode": 
									$table.find("tr.catName").hide();
									$divItem.find("a.switchToCatCode").triggerHandler("click"); break;
								case "CatName": 
									$table.find("tr.catCode").hide();
									$divItem.find("a.switchToCatName").triggerHandler("click"); break;
								}

								break;
							case "facet": 
								$divItem.find("div.ims, div.cnet").remove();
								$divItem.find("div.facet, div.dynamicAttribute").show();
								break;
							}

							self.addToggleListener($divItem);
							self.addCloneFilterGroupListener($divItem);
							self.addDeleteFilterGroupListener($divItem);

							$divItem.find("img.toggleIcon, a.conditionFormattedText").triggerHandler("click");


						}
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			showEmptyFilterGroup: function(){
				var $divItemList = $("div#conditionList");

				if ($divItemList.find("div.conditionItem:not(#conditionItemPattern)").length==0){
					$divItemList.find("div#emptyConditionItem").show();
				}else{
					$divItemList.find("div#emptyConditionItem").hide();
				}
			},

			addDeleteFilterGroupListener: function(ui, condition){
				var self = this;

				ui.find("img.deleteIcon,a.deleteBtn").off().on({
					click: function(e){
						var $item = $(this).parents(".conditionItem");
						var readableString = $.isNotBlank(e.data.condition)? e.data.condition["readableString"] : $item.find(".conditionFormattedText").html();
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
									},
									preHook:function(){
										$item.find("img#preloaderUpdating").show();
									},
									postHook:function(){
										$item.find("img#preloaderUpdating").hide();
									}
								});
							}
						}
					},
					mouseenter: showHoverInfo
				},{condition: condition, locked:self.selectedRuleStatus["locked"] || !allowModify});	
			},

			showRuleCondition: function(){
				var self = this;

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
								$divItem.find("tr.catCode,tr.catName").hide();
								$divItem.show();
								$divItemList.append($divItem);
								self.addToggleListener($divItem, item);
								self.addCloneFilterGroupListener($divItem, item);
								self.addDeleteFilterGroupListener($divItem, item);
							}

							return;
						}

						$divItemList.find("div#emptyConditionItem").show();

					},
					preHook:function(){
						$divItemList.find("div#emptyConditionItem").hide();
						$divItemList.find("div.conditionItem:not(#conditionItemPattern)").remove();
						$divItemList.find("#preloader").show();
					},
					postHook:function(){
						$divItemList.find("#preloader").hide();
						self.addNewFilterGroupListener();
					}
				});
			},

			refreshTabContent: function(){
				var self = this;
				self.tabSelectedTypeId = $("li.ui-tabs-selected > a").attr("href");
				self.setActiveRedirectType();
				self.setIncludeKeyword();

				switch(self.tabSelectedTypeId){
				case "#filter" : self.showRuleCondition(); break;
				case "#keyword" : self.getChangeKeywordActiveRules(); break;
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
			}
	};

	$(document).ready(function() {
		RedirectRule.init();
	});	

})(jQuery);	
