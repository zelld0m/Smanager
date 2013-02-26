(function($){

	var RedirectRule = {
			moduleName: "Query Cleaning",
			selectedRule:  null,
			selectedRuleStatus: null,

			ruleFilterText: "",
			ruleKeywordFilterText: "",
			tabSelectedTypeId: 1,

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
						if(e.data.locked) return;
						jConfirm("Delete " + self.selectedRule["ruleName"] + "'s rule?", "Delete Rule Condition", function(result){
							if(result){
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
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			addSaveRuleListener: function(){
				var self = this;

				$("#saveBtn").off().on({
					click: function(e){
						if (e.data.locked) return;
						setTimeout(function() {
							var ruleName = $.trim($('div#redirect input[id="name"]').val());  
							var description = $.trim($('div#redirect textarea[id="description"]').val());  

							if (self.checkIfUpdateAllowed()){
								if ($.isBlank(ruleName)){
									jAlert("Rule name is required.","Query Cleaning");
								}
								else if (!isAllowedName(ruleName)){
									jAlert("Rule name contains invalid value.","Query Cleaning");
								}
								else if (!isAscii(description)) {
									jAlert("Description contains non-ASCII characters.","Query Cleaning");										
								}
								else if (!isXSSSafe(description)){
									jAlert("Description contains XSS.","Query Cleaning");
								}
								else {
									RedirectServiceJS.checkForRuleNameDuplicate(self.selectedRule["ruleId"], ruleName, {
										callback: function(data){
											if (data==true){
												jAlert("Another query cleaning rule is already using the name provided.","Query Cleaning");
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
						}, 500 );
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			prepareRedirect : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #redirect, #noSelected").hide();
				$("#titleHeader").empty();
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
					ruleType: "Query Cleaning",
					rule: self.selectedRule,
					enableVersion: true,
					authorizeRuleBackup: allowModify,
					authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
					postRestoreCallback: function(base, rule){
						base.api.destroy();
						self.showRedirect();
					},
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
						$("#titleHeader").text(self.selectedRule["ruleName"]);
						self.selectedRuleStatus = ruleStatus;
						$("#redirect").show();
						$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

						$("#name").val(self.selectedRule["ruleName"]);
						$("#description").val(self.selectedRule["description"]);

						self.getKeywordInRuleList(1);
						self.addTabListener();
						self.addSaveRuleListener();
						self.addDeleteRuleListener();
						self.addDownloadListener();

						$('#auditIcon').off().on({
							click: function(e){
								$(e.currentTarget).viewaudit({
									itemDataCallback: function(base, page){
										AuditServiceJS.getRedirectTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
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
					fieldName: "keyword",
					itemTitle: "New Keyword",
					page: page,
					region: "content",
					pageStyle: "style2",
					pageSize: self.keywordInRulePageSize,
					headerText : "Using This Rule",
					headerTextAlt : "Keyword",
					itemTextClass: "cursorText",
					showAddButton: !self.selectedRuleStatus["locked"] && allowModify,
					showStatus: false,

					itemDataCallback: function(base, keyword, page){
						RedirectServiceJS.getAllKeywordInRule(self.selectedRule["ruleId"], keyword, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data, keyword);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemOptionCallback: function(base, item){
						var icon = '<a id="deleteKw" href="javascript:void(0);"><img src="' + GLOBAL_contextPath + '/images/icon_delete2.png"></a>';

						item.ui.find(".itemLink").html($(icon));

						item.ui.find(".itemLink > a#deleteKw").off().on({
							click: function(e){
								if (e.data.locked) return;

								jConfirm('Delete "' + item.name + '" in ' + self.selectedRule["ruleName"]  + '?', "Delete Keyword", function(result){
									if(result){
										RedirectServiceJS.deleteKeywordInRule(self.selectedRule["ruleId"], item.name,{
											callback:function(code){
												showActionResponse(code, "delete", item.name);
												self.getKeywordInRuleList(1);
												self.getRedirectRuleList(1);
											},
											preHook: function(){ 
												base.prepareList(); 
											}
										});
									}
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
					fieldName: "keyword",
					page: self.ruleKeywordPage,
					pageSize: self.ruleKeywordPageSize,
					headerText : "Query Cleaning Keyword",
					showAddButton: false,
					showStatus: false,
					filterText: self.ruleKeywordFilterText,

					itemDataCallback: function(base, keyword, page){
						self.ruleKeywordPage = page;
						self.ruleKeywordFilterText = keyword;
						StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
							callback: function(data){
								base.populateList(data, keyword);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemOptionCallback: function(base, item){
						RedirectServiceJS.getTotalRuleUsedByKeyword(item.name, {
							callback: function(count){
								if (count == 0) return;

								item.ui.find("#itemLinkValue").html("(" + count + ")").on({
									click: function(e){
										$(e.currentTarget).qtip({
											content: {
												text: $('<div/>'),
												title: { text: 'Query Cleaning for ' + item.name, button: true }
											},
											show: { 
												ready: true,
												modal: true 
											},
											events: { 
												show: function(e, api){
													var $content = $("div", api.elements.content).html($("#sortRulePriorityTemplate").html());

													RedirectServiceJS.getAllRuleUsedByKeyword(item.name, {
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
												},
												hide: function(e, api){
													api.destroy();
												}
											}
										});
									}
								});
							},
							preHook: function(){ 
								item.ui.find("#itemLinkValue").hide();
								item.ui.find("#itemLinkPreloader").show();
							},
							postHook: function(){ 
								item.ui.find("#itemLinkValue").show();
								item.ui.find("#itemLinkPreloader").hide();
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
					moduleName: "Query Cleaning",
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
								base.populateList(data, keyword);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemAddCallback: function(base, name){
						RedirectServiceJS.checkForRuleNameDuplicate("", name, {
							callback: function(data){
								if (data==true){
									jAlert("Another query cleaning rule is already using the name provided.","Query Cleaning");
								}else{
									RedirectServiceJS.addRuleAndGetModel(name, {
										callback: function(data){
											if (data!=null){
												base.getList(name, 1);
												self.selectedRule = data;
												self.setRedirect(data);
											}else{
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

					itemNameCallback: function(base, item){
						self.setRedirect(item.model);
					},

					itemOptionCallback: function(base, item){
						RedirectServiceJS.getTotalKeywordInRule(item.model["ruleId"],{
							callback: function(count){
								if (count > 0) item.ui.find("#itemLinkValue").html("(" + count + ")");
								item.ui.find("#itemLinkValue").on({
									click: function(e){
										self.setRedirect(item.model);
									}
								});
							},
							preHook: function(){ 
								item.ui.find("#itemLinkValue").hide();
								item.ui.find("#itemLinkPreloader").show();
							},
							postHook: function(){ 
								item.ui.find("#itemLinkValue").show();
								item.ui.find("#itemLinkPreloader").hide();
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
				
//				$("div#searchHeaderText").rkMessageType({
//					id: 1,
//					rule: self.selectedRule,
//					successTypeUpdateCallback: function(value){
//						self.selectedRule["replaceKeywordMessageType"] = value;
//					},
//					successCustomTextUpdateCallback: function(customText){
//						self.selectedRule["replaceKeywordMessageCustomText"] = customText;
//					}
//				});
				
				$input.val(self.selectedRule["changeKeyword"]).prop({disabled: self.selectedRuleStatus["locked"] || !allowModify});

				var inputVal = encodeURIComponent($.trim($input.val()));

				if ($.isNotBlank($input.val()))
					$('div#keyword').find('#activerules > .alert > #rules').activerule({
						keyword: inputVal, 
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
						self.updateActiveRedirectType();
					},
					mouseenter: showHoverInfo
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify});

				switch(parseInt(self.selectedRule["redirectTypeId"])){
				case 1: $("div#filter").find('input#activate').prop({checked:true, disabled: true }); break;
				case 2: $("div#keyword").find('input#activate').prop({checked:true, disabled: true }); break;
				case 3: $("div#page").find('input#activate').prop({checked:true, disabled: true }); break;
				};
			},

			updateActiveRedirectType : function(){
				var self = this;
				RedirectServiceJS.setRedirectType(self.selectedRule["ruleId"], parseInt(self.tabSelectedTypeId), {
					callback: function(data){
						self.selectedRule["redirectTypeId"] = self.tabSelectedTypeId;
					},
					postHook: function(){
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

			populateLevel1Categories: function(ui, condition){
				var self = this;
				var $select = ui.find("select#level1CategoryList");
				var $table = ui.find("table.cnetFields");

				CategoryServiceJS.getCNETLevel1Categories({
					callback: function(data){
						var list = data;

						$select.append($("<option>", {value: ""}).text("-Select Level 1-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderLevel1CategoryList").show();
						self.clearCNETComboBox(ui, "level1Cat");
						$table.find("tr#level2Cat, tr#level3Cat").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderLevel1CategoryList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Level1Category"])){
							$select.val(condition.CNetFilters["Level1Category"]);
							$select.change();
						}else{
							self.populateCNETManufacturers(ui, condition);
						}
					}
				});
			},

			populateLevel2Categories: function(ui, condition, selectedLevel1Category){
				var self = this;
				var $select = ui.find("select#level2CategoryList");
				var $table = ui.find("table.cnetFields");

				CategoryServiceJS.getCNETLevel2Categories(selectedLevel1Category, {
					callback: function(data){
						var list = data;

						$select.append($("<option>", {value: ""}).text("-Select Level 2-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#level2Cat").show();
						}else{
							$table.find("tr#level2Cat").hide();
						}  
					},
					preHook:function(){
						ui.find("img#preloaderLevel2CategoryList").show();
						self.clearCNETComboBox(ui, "level2Cat");
						$table.find("tr#level3Cat").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderLevel2CategoryList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Level2Category"])){
							$select.val(condition.CNetFilters["Level2Category"]);
							$select.change();
						}else{
							self.populateCNETManufacturers(ui, condition);
						}
					}
				});
			},

			populateLevel3Categories: function(ui, condition, selectedLevel1Category, selectedLevel2Category){
				var self = this;
				var $select = ui.find("select#level3CategoryList");
				var $table = ui.find("table.cnetFields");

				CategoryServiceJS.getCNETLevel3Categories(selectedLevel1Category, selectedLevel2Category, {
					callback: function(data){
						var list = data;

						$select.append($("<option>", {value: ""}).text("-Select Level 3-"));
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
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderLevel3CategoryList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Level3Category"])){
							$select.val(condition.CNetFilters["Level3Category"]);
							$select.change();
						}else{
							self.populateCNETManufacturers(ui, condition);
						}
					}
				});
			},

			populateCNETManufacturers: function(ui, condition){
				var self = this;
				var $select = ui.find("select#cnetmanufacturerList");
				var $table = ui.find("table.cnetFields");

				var inLevel1Category = $.trim(ui.find("select#level1CategoryList").val());
				var inLevel2Category = $.trim(ui.find("select#level2CategoryList").val());
				var inLevel3Category = $.trim(ui.find("select#level3CategoryList").val());

				CategoryServiceJS.getCNETManufacturers(inLevel1Category, inLevel2Category, inLevel3Category, {
					callback: function(data){
						var list = data;
						$select.append($("<option>", {value: ""}).text("-Select Manufacturer-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderCNETManufacturerList").show();
						self.clearCNETComboBox(ui, "cnetmanufacturer");
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderCNETManufacturerList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if ($.isNotBlank(condition) && $.isNotBlank(condition.CNetFilters["Manufacturer"])){
							$select.val(condition.CNetFilters["Manufacturer"]);
							$select.change();
						}
					}
				});
			}, 

			makeSelectSearchable: function(ui, condition, select){
				var self = this;
				select.searchable({
					change: function(u, e){
						var selectedCategory = $.trim(ui.find("select#categoryList > option:gt(0):selected:eq(0)").text());
						var selectedSubcategory = $.trim(ui.find("select#subCategoryList > option:gt(0):selected:eq(0)").text());
						var selectedClass = $.trim(ui.find("select#classList > option:gt(0):selected:eq(0)").text());

						var selectedLevel1Category = $.trim(ui.find("select#level1CategoryList > option:gt(0):selected:eq(0)").text());
						var selectedLevel2Category = $.trim(ui.find("select#level2CategoryList > option:gt(0):selected:eq(0)").text());

						if($.isBlank(u.value)){
							switch($(e.currentTarget).prop("id").toLowerCase()){
							case "categorylist": 
								ui.find("tr#subcategory").hide();
							case "subcategorylist":
								ui.find("tr#class").hide();
							case "classlist": 
								ui.find("tr#minor").hide();
							case "minorlist": 
								break;
							case "level1categorylist": 
								ui.find("tr#level2Cat").hide();
							case "level2categorylist": 
								ui.find("tr#level3Cat").hide();
							case "level3categorylist": 
								break;
							case "templatenamelist": 
								if (ui.find("div.ims").is(":visible"))
									self.populateIMSTemplateNames(ui, condition);
								else if (ui.find("div.cnet").is(":visible"))
									self.populateCNETTemplateNames(ui, condition);
								break;
							}
						}

						switch($(e.currentTarget).prop("id").toLowerCase()){
						case "categorylist": 
							if($.isNotBlank(selectedCategory)){
								self.populateSubcategories(ui, condition, selectedCategory);
							}
							break;
						case "subcategorylist": 
							if($.isNotBlank(selectedCategory) && $.isNotBlank(selectedSubcategory)){
								self.populateClass(ui, condition, selectedCategory, selectedSubcategory);
							}
							break;
						case "classlist": 
							if($.isNotBlank(selectedCategory) && $.isNotBlank(selectedSubcategory)  && $.isNotBlank(selectedClass)){
								self.populateMinor(ui, condition, selectedCategory, selectedSubcategory, selectedClass);
							}
							break;
						case "minorlist":
							self.populateIMSManufacturers(ui, condition);
							break;
						case "level1categorylist": 
							if($.isNotBlank(selectedLevel1Category))
								self.populateLevel2Categories(ui, condition, selectedLevel1Category);
							break;
						case "level2categorylist": 
							if($.isNotBlank(selectedLevel1Category) && $.isNotBlank(selectedLevel2Category))
								self.populateLevel3Categories(ui, condition, selectedLevel1Category, selectedLevel2Category);
							break;
						case "level3categorylist": 
							self.populateCNETManufacturers(ui, condition);
							break;
						case "templatenamelist": 
							if (ui.find("div.ims").is(":visible"))
								self.populateIMSDynamicAttributes(ui, condition, u.value);
							else if (ui.find("div.cnet").is(":visible"))
								self.populateCNETDynamicAttributes(ui, condition, u.value);
							break;
						case "dynamicattributelist": 
							self.addDynamicAttributeButtonListener(ui, condition, u.value);
							break;
						};
					}
				});
			},

			populateCategories: function(ui, condition){
				var self = this;
				var $select = ui.find("select#categoryList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSCategories({
					callback: function(data){
						var list = data;

						$select.append($("<option>", {value: ""}).text("-Select Category-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderCategoryList").show();
						self.clearIMSComboBox(ui, "category");
						$table.find("tr#subcategory,tr#class,tr#minor").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderCategoryList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Category"])){
							$select.val(condition.IMSFilters["Category"]);
							$select.change();
						}else{
							self.populateIMSManufacturers(ui, condition);
						}
					}
				});
			},

			populateSubcategories: function(ui, condition, selectedCategory){
				var self = this;
				var $select = ui.find("select#subCategoryList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSSubcategories(selectedCategory, {
					callback: function(data){
						var list = data;

						$select.append($("<option>", {value: ""}).text("-Select SubCategory-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#subcategory").show();
						}else{
							$table.find("tr#subcategory").hide();
						}  
					},
					preHook:function(){
						ui.find("img#preloaderSubCategoryList").show();
						self.clearIMSComboBox(ui, "subcategory");
						$table.find("tr#class,tr#minor").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderSubCategoryList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["SubCategory"])){
							$select.val(condition.IMSFilters["SubCategory"]);
							$select.change();
						}else{
							self.populateIMSManufacturers(ui, condition);
						}
					}
				});
			},

			populateClass: function(ui, condition, selectedCategory, selectedSubCategory){
				var self = this;
				var $select = ui.find("select#classList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSClasses(selectedCategory,selectedSubCategory, {
					callback: function(data){
						var list = data;

						$select.append($("<option>", {value: ""}).text("-Select Class-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}

						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#class").show();
						}else{
							$table.find("tr#class").hide();
						}  
					},
					preHook:function(){
						ui.find("img#preloaderClassList").show();
						self.clearIMSComboBox(ui, "class");
						$table.find("tr#minor").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderClassList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Class"])){
							$select.val(condition.IMSFilters["Class"]);
							$select.change();
						}else{
							self.populateIMSManufacturers(ui, condition);
						}
					}
				});
			},

			populateMinor: function(ui, condition, selectedCategory, selectedSubCategory, selectedClass){
				var self = this;
				var $select = ui.find("select#minorList");
				var $table = ui.find("table.imsFields");

				CategoryServiceJS.getIMSMinors(selectedCategory, selectedSubCategory, selectedClass, {
					callback: function(data){
						var list = data;
						$select.append($("<option>", {value: ""}).text("-Select Minor-"));
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
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderMinorList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["SubClass"])){
							$select.val(condition.IMSFilters["SubClass"]);
							$select.change();
						}else{
							self.populateIMSManufacturers(ui, condition);
						}
					}
				});
			},

			populateIMSManufacturers: function(ui, condition){
				var self = this;
				var $select = ui.find("select#manufacturerList");
				var $table = ui.find("table.imsFields");

				var inCatCode = "";
				var inCategory = "";
				var inSubCategory = "";
				var inClass = "";
				var inMinor = "";

				var catCodeVal = $.trim(ui.find("input#catcode").val());

				if ($.isNotBlank(catCodeVal) && ui.find("a.switchToCatName").is(":visible")){
					inCatCode = catCodeVal;
				}else if(ui.find("a.switchToCatCode").is(":visible")){
					inCategory = ui.find("select#categoryList >option:gt(0):selected:eq(0)").text();
					inSubCategory = ui.find("select#subCategoryList >option:gt(0):selected:eq(0)").text();
					inClass = ui.find("select#classList >option:gt(0):selected:eq(0)").text();
					inMinor = ui.find("select#minorList >option:gt(0):selected:eq(0)").text();
				}

				CategoryServiceJS.getIMSManufacturers(inCatCode, inCategory, inSubCategory, inClass, inMinor, {
					callback: function(data){
						var list = data;
						$select.append($("<option>", {value: ""}).text("-Select Manufacturer-"));
						for(var i=0; i<list.length; i++){
							if($.isNotBlank(list[i]))
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
						if ($.isNotBlank(list) && list.length>0){
							$table.find("tr#manufacturer").show();
						}else{
							$table.find("tr#manufacturer").hide();
						}  
					},
					preHook:function(){
						ui.find("img#preloaderManufacturerList").show();
						self.clearIMSComboBox(ui, "manufacturer");
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderManufacturerList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if ($.isNotBlank(condition) && $.isNotBlank(condition.IMSFilters["Manufacturer"])){
							$select.val(condition.IMSFilters["Manufacturer"]);
						}
					}
				});
			},  

			addFacetFieldListener: function(ui, condition){
				var $facet = ui.find("div.facet");

				if ($.isBlank(condition)) return; 

				if($.isNotBlank(condition.facets["Platform"]))
					$facet.find("select#platformList > option:contains('" + condition.facets["Platform"][0] + "')").prop("selected", true);

				if($.isNotBlank(condition.facets["Condition"]))
					$facet.find("select#conditionList > option:contains('" + condition.facets["Condition"][0] + "')").prop("selected", true);

				if($.isNotBlank(condition.facets["Availability"]))
					$facet.find("select#availabilityList > option:contains('" + condition.facets["Availability"][0] + "')").prop("selected", true);

				if($.isNotBlank(condition.facets["License"]))
					$facet.find("select#licenseList > option:contains('" + condition.facets["License"][0] + "')").prop("selected", true);

				if($.isNotBlank(condition.facets["ImageExists"]))
					$facet.find("select#imageExistsList > option:contains('" + condition.facets["ImageExists"][0] + "')").prop("selected", true);

				$facet.find("input#nameContains").val(condition.facets["Name"]);
				$facet.find("input#descriptionContains").val(condition.facets["Description"]);

			},

			populateDynamicAttributeValues: function(ui, condition, attributeMap){
				var self = this;

				if(condition.dynamicAttributes){
					var $divItemList = ui.find('div#dynamicAttributeItemList');
					$divItemList.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)").remove();

					$.each(condition.dynamicAttributes, function(attrName, attrData) { 
						if(attrName != "TemplateName" || attrName != GLOBAL_storeFacetTemplateName){
							var $divDynamicAttributeItem = $divItemList.find('div#dynamicAttributeItemPattern').clone();
							var $ulAttributeValues = $divDynamicAttributeItem.find("div#dynamicAttributeValues");

							$ulAttributeValues.prop({id:$.formatAsId(attrName), title:attrName});
							var currCondCount = parseInt($divItemList.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern):last").attr("id"));
							if (!$.isNumeric(currCondCount)){
								currCondCount = 0; 
							}

							var countId = 1 + parseInt(currCondCount);
							$divDynamicAttributeItem.prop("id", "dynamicAttributeItem");

							if(attributeMap && attributeMap[attrName]){
								var attributeValues = attributeMap[attrName].attributeValues;
								$divDynamicAttributeItem.find('span#dynamicAttributeLabel').html(attributeMap[attrName].attributeDisplayName + ":");

								if(attributeValues){
									for(var i=0; i<attributeValues.length; i++){
										var $liAttributeValue = $ulAttributeValues.find("div#dynamicAttributeValuesPattern").clone();
										$liAttributeValue.show();
										$liAttributeValue.prop("id", "dynamicAttributeValues" + countId);
										$liAttributeValue.find("input.checkboxFilter").prop({name:attrName, value:attributeValues[i], checked: ($.inArray(attributeValues[i], attrData) > -1)});
										$liAttributeValue.find("span#attributeValueName").text(attributeValues[i].split("|")[1]);
										$ulAttributeValues.append($liAttributeValue);
									}
								}

								$divDynamicAttributeItem.show();
								$divDynamicAttributeItem.prop("id", countId);
								$divDynamicAttributeItem.addClass("tempDynamicAttributeItem");
								$divItemList.append($divDynamicAttributeItem);

								self.addDeleteDynamicAttributeButtonListener($divDynamicAttributeItem, condition);
							}
						}
					});
				}
			},

			populateIMSTemplateNames: function(ui, condition){
				var self = this;
				var $select = ui.find("select#templateNameList");
				var $table = ui.find("table.dynamicAttributeFields");

				CategoryServiceJS.getIMSTemplateNames({
					callback: function(data){
						var list = data;
						$select.append($("<option>", {value: ""}).text("-Select Template-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderTemplateNameList").show();
						self.clearDynamicAttributeComboBox(ui, "templateNameList");
						$table.find("tr#dynamicAttributeName").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderTemplateNameList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if ($.isNotBlank(condition) && !$.isEmptyObject(condition.dynamicAttributes)){
							$select.val(condition.dynamicAttributes[GLOBAL_storeFacetTemplateName][0]);
							self.populateIMSDynamicAttributes(ui, condition, condition.dynamicAttributes[GLOBAL_storeFacetTemplateName][0]);
						}
					}
				});
			},

			populateCNETTemplateNames: function(ui, condition){
				var self = this;
				var $select = ui.find("select#templateNameList");
				var $table = ui.find("table.dynamicAttributeFields");

				CategoryServiceJS.getCNETTemplateNames({
					callback: function(data){
						var list = data;
						$select.append($("<option>", {value: ""}).text("-Select Template-"));
						for(var i=0; i<list.length; i++){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					},
					preHook:function(){
						ui.find("img#preloaderTemplateNameList").show();
						self.clearDynamicAttributeComboBox(ui, "templateNameList");
						$table.find("tr#dynamicAttributeName").hide();
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderTemplateNameList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if ($.isNotBlank(condition) && !$.isEmptyObject(condition.dynamicAttributes)){
							$select.val(condition.dynamicAttributes[GLOBAL_storeFacetTemplateName][0]);
							self.populateCNETDynamicAttributes(ui, condition, condition.dynamicAttributes[GLOBAL_storeFacetTemplateName][0]);
						}
					}
				});
			},

			populateIMSDynamicAttributes: function(ui, condition, selectedTemplateName){
				var self = this;
				var $select = ui.find("select#dynamicAttributeList");
				var $table = ui.find("table.dynamicAttributeFields");

				CategoryServiceJS.getIMSTemplateAttributes(selectedTemplateName, {
					callback: function(data){
						self.templateAttributes = data;
						var isEmpty = true;

						$.each(self.templateAttributes, function(attrName, attrData) { 
							$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
							isEmpty = false;
						});

						if (!isEmpty){
							$select.prepend($("<option>", {value: ""}).text("-Select Attribute-"));
							$table.find("tr#dynamicAttributeName").show();
						}else{
							$table.find("tr#dynamicAttributeName").hide();
						}
					},
					preHook:function(){
						ui.find("img#preloaderDynamicAttributeList").show();
						self.clearDynamicAttributeComboBox(ui, "attributevaluelist");
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderDynamicAttributeList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if ($.isNotBlank(condition) && !$.isEmptyObject(condition["dynamicAttributes"])){
							self.populateDynamicAttributeValues(ui, condition, self.templateAttributes);
						}
					}
				});
			},

			populateCNETDynamicAttributes: function(ui, condition, selectedTemplateName){
				var self = this;
				var $select = ui.find("select#dynamicAttributeList");
				var $table = ui.find("table.dynamicAttributeFields");

				CategoryServiceJS.getCNETTemplateAttributes(selectedTemplateName, {
					callback: function(data){
						self.templateAttributes = data;
						var isEmpty = true;

						$.each(self.templateAttributes, function(attrName, attrData) { 
							$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
							isEmpty = false;
						});

						if (!isEmpty){
							$select.prepend($("<option>", {value: ""}).text("-Select Attribute-"));
							$table.find("tr#dynamicAttributeName").show();
						}else{
							$table.find("tr#dynamicAttributeName").hide();
						}
					},
					preHook:function(){
						ui.find("img#preloaderDynamicAttributeList").show();
						self.clearDynamicAttributeComboBox(ui, "attributevaluelist");
						$table.find("select.selectCombo").prop("disabled", true);
					},
					postHook:function(){
						ui.find("img#preloaderDynamicAttributeList").hide();
						$table.find("select.selectCombo").prop("disabled", false);
						self.makeSelectSearchable(ui, condition, $select);
						if ($.isNotBlank(condition) && !$.isEmptyObject(condition.dynamicAttributes)){
							self.populateDynamicAttributeValues(ui, condition, self.templateAttributes);
						}
					}
				});
			},

			addCNETFieldListener: function(ui, condition){
				var $cnet = ui.find("div.cnet");

				if($.isBlank($cnet) && $cnet.is(":not(:visible)")){
					return;
				}				
			},

			addIMSFieldListener: function(ui, condition){
				var self = this;
				var $ims = ui.find("div.ims");

				if($.isBlank($ims) && $cnet.is(":not(:visible)")){
					return;
				}

				$ims.find("a.switchToCatCode,a.switchToCatName").off().on({
					click: function(e){
						var $item = $(this).parents(".conditionItem");
						var $table = $item.find("table.imsFields");

						switch($(e.currentTarget).attr("class")){
						case "switchToCatName" : 
							$table.find("tr.catCode").hide();
							$table.find("tr.catName").show();
							self.populateCategories(ui, e.data.condition);
							break;
						case "switchToCatCode" : 
							$table.find("tr.catCode").show();
							$table.find("tr.catName").hide();
							self.populateIMSManufacturers(ui, e.data.condition);
							break;
						}
					}
				},{condition: condition});

				var $input = $ims.find("input#catcode");

				$input.off().on({
					mouseenter: function(e){
						e.data.input = $.trim($(e.currentTarget).val());
					},
					mouseleave: function(e){
						if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()){
							self.populateIMSManufacturers(e.data.ui, e.data.condition);
						}
					},
					focusin: function(e){
						e.data.input = $.trim($(e.currentTarget).val());
					},
					focusout: function(e){
						if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()){
							self.populateIMSManufacturers(e.data.ui, e.data.condition);
						}
					}
				},{ui:ui, condition: condition, input: ""});
			},

			checkDisplay: function(ui, condition){
				var self = this;
				var selectedFilter = $("select#filterGroup option:selected").val();
				var $cnet = ui.find("div.cnet");
				var $ims = ui.find("div.ims");

				ui.find("div.cnet, div.ims, div.dynamicAttribute").hide();

				if(($.isBlank(condition) && selectedFilter === "cnet") || ($.isNotBlank(condition) && condition.CNetFilter)){
					ui.find("div.cnet, div.dynamicAttribute").show();
					self.addCNETFieldListener(ui, condition);

					var $table = $cnet.find("table.cnetFields");
					$table.find("tr.catName").show();
					self.populateLevel1Categories(ui, condition);
					self.populateCNETTemplateNames(ui, condition);
				}
				else if(($.isBlank(condition) && selectedFilter === "ims") ||  ($.isNotBlank(condition) && condition.IMSFilter)){
					ui.find("div.ims, div.dynamicAttribute").show();

					if(GLOBAL_store === 'pcmall' || GLOBAL_store === 'pcmallcap' || GLOBAL_store === 'pcmgbd'){
						ui.find("div.dynamicAttribute").hide();
					}

					self.addIMSFieldListener(ui, condition);

					var usingCategory = $.isNotBlank(condition) && condition["imsUsingCategory"];
					var usingCatCode = $.isNotBlank(condition) && condition["imsUsingCatCode"];
					var $table = $ims.find("table.imsFields");

					if ((usingCategory && !usingCatCode) || ui.find("a.switchToCatCode").is(":visible")){
						$table.find("tr.catName").show();
						$table.find("tr.catCode").hide();
						self.populateCategories(ui, condition);
					}else{
						$table.find("tr.catName").hide();
						$table.find("tr.catCode").show();
						self.populateIMSManufacturers(ui, condition);
					}
					self.populateIMSTemplateNames(ui, condition);
				}
			},

			clearDynamicAttributeComboBox: function(ui, trigger){
				var $dynamicAttribute = ui.find("div.dynamicAttribute");

				if ($.isBlank(trigger)){
					$dynamicAttribute.find("select.selectCombo option").remove();
				}else{
					switch (trigger.toLowerCase()){
					case "templatenamelist": 
						$dynamicAttribute.find("select#templateNameList option").remove();
					case "attributevaluelist":
						$dynamicAttribute.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)").remove();
					case "dynamicattributelist": 
						$dynamicAttribute.find("select#dynamicAttributeList option").remove();
					}
				}
			},

			clearCNETComboBox: function(ui, trigger){
				var $cnet = ui.find("div.cnet");

				if ($.isBlank(trigger)){
					$cnet.find("select.selectCombo option").remove();
				}else{
					switch (trigger.toLowerCase()){
					case "level1cat": 
						$cnet.find("select#level1CategoryList option").remove();
					case "level2cat": 
						$cnet.find("select#level2CategoryList option").remove();
					case "level3cat": 
						$cnet.find("select#level3CategoryList option").remove();
					case "cnetmanufacturer": 
						$cnet.find("select#cnetmanufacturerList option").remove();	
					}
				}
			},

			clearIMSComboBox: function(ui, trigger){
				var self = this;
				var $ims = ui.find("div.ims");

				if ($.isBlank(trigger)){
					$ims.find("select.selectCombo option").remove();
				}else{
					switch (trigger.toLowerCase()){
					case "category": 
						$ims.find("select#categoryList option").remove();
					case "subcategory": 
						$ims.find("select#subCategoryList option").remove();
					case "class": 
						$ims.find("select#classList option").remove();
					case "minor": 
						$ims.find("select#minorList option").remove();
					case "manufacturer": 
						$ims.find("select#manufacturerList option").remove();	
					}
				}
			},

			buildConditionAsMap: function(ui){
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
					catCode[0] = $.trim(ui.find("input#catcode").val().toUpperCase());
					category[0] = ui.find("select#categoryList > option:gt(0):selected:eq(0)").text();
					subCategory[0] = ui.find("select#subCategoryList > option:gt(0):selected:eq(0)").text();
					clazz[0] = ui.find("select#classList > option:gt(0):selected:eq(0)").text();
					minor[0] = ui.find("select#minorList > option:gt(0):selected:eq(0)").text();
					manufacturer[0] = ui.find("select#manufacturerList > option:gt(0):selected:eq(0)").text();

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
					level1Cat[0] = ui.find("select#level1CategoryList > option:gt(0):selected:eq(0)").text();
					level2Cat[0] = ui.find("select#level2CategoryList > option:gt(0):selected:eq(0)").text();
					level3Cat[0] = ui.find("select#level3CategoryList > option:gt(0):selected:eq(0)").text();
					cnetManufacturer[0] = ui.find("select#cnetmanufacturerList > option:gt(0):selected:eq(0)").text();

					if ($.isNotBlank(level1Cat[0])) condMap["Level1Category"] = level1Cat; 	
					if ($.isNotBlank(level2Cat[0])) condMap["Level2Category"] = level2Cat; 	
					if ($.isNotBlank(level3Cat[0])) condMap["Level3Category"] = level3Cat; 	
					if ($.isNotBlank(cnetManufacturer[0])) condMap["Manufacturer"] = cnetManufacturer; 	
				}

				if(ui.find("div.dynamicAttribute").is(":visible")){
					var inTemplateName = ui.find("select#templateNameList > option:gt(0):selected:eq(0)").text();
					var $divDynamicAttrItems = ui.find("div.dynamicAttributeItem");

					if($.isNotBlank(inTemplateName)){
						condMap[GLOBAL_storeFacetTemplateName] = $.makeArray(inTemplateName);

						$divDynamicAttrItems.find("div").each(function(){ 
							var attributeItem = this.title;
							var attributeValues = new Array();

							$divDynamicAttrItems.find("input:checkbox[name="+attributeItem+"]:checked").each(function(){
								attributeValues.push($(this).val()); 
							});

							if(attributeValues.length > 0)
								condMap[attributeItem] = attributeValues;
						});
					}
				}

				if (ui.find("div.facet").is(":visible")){
					var platform = ui.find("select#platformList  > option:gt(0):selected:eq(0)").text();
					var condition = ui.find("select#conditionList  > option:gt(0):selected:eq(0)").text();
					var availability = ui.find("select#availabilityList  > option:gt(0):selected:eq(0)").text();
					var license = ui.find("select#licenseList > option:gt(0):selected:eq(0)").text();
					var nameContains = $.trim(ui.find("input#nameContains").val());
					var descriptionContains = $.trim(ui.find("input#descriptionContains").val());
					var imageExists = ui.find("select#imageExistsList > option:gt(0):selected:eq(0)").text();

					if($.isNotBlank(platform)){
						switch(platform.toLowerCase()){
						case "universal": condMap["Platform"] = ["Universal"]; break;
						case "pc": condMap["Platform"] = ["PC"]; break;
						case "linux": condMap["Platform"] = ["Linux"]; break;
						case "macintosh": condMap["Platform"] = ["Macintosh"]; break;
						}
					}

					if($.isNotBlank(condition)){
						switch(condition.toLowerCase()){
						case "refurbished": condMap["Condition"] = ["Refurbished"]; break;
						case "open box": condMap["Condition"] = ["Open Box"]; break;
						case "clearance": condMap["Condition"] = ["Clearance"]; break;
						}
					}

					if($.isNotBlank(availability)){
						switch(availability.toLowerCase()){
						case "in stock": condMap["Availability"] = ["In Stock"]; break;
						case "call": condMap["Availability"] = ["Call"]; break;
						}
					}

					if($.isNotBlank(license)){
						switch(license.toLowerCase()){
						case "license products only": condMap["License"] = ["License Products Only"]; break;
						case "non-license products only": condMap["License"] = ["Non-License Products Only"]; break;
						}
					}

					if($.isNotBlank(imageExists)){
						switch(imageExists.toLowerCase()){
						case "products with image only": condMap["ImageExists"] = ["Products With Image Only"]; break;
						case "products without image only": condMap["ImageExists"] = ["Products Without Image Only"]; break;
						}
					}

					if($.isNotBlank(nameContains))
						condMap["Name"] = $.makeArray(nameContains);

					if($.isNotBlank(descriptionContains))
						condMap["Description"] = $.makeArray(descriptionContains);
				}

				return condMap;
			},

			addDeleteDynamicAttributeButtonListener: function(ui,condition){
				var self = this;

				ui.find("img.deleteAttrIcon").off().on({
					click: function(e){
						if (e.data.locked) return;
						var $item = $(this).parents(".dynamicAttributeItem");
						var attributeName = $item.find("#dynamicAttributeLabel").text();
						jConfirm("Delete " + attributeName.substr(0,attributeName.length-1) + " attribute?", "Delete Item Attribute", function(result){
							if (result) $item.remove();
						});
					},
					mouseenter: showHoverInfo
				},{condition: condition, locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			addDynamicAttributeButtonListener: function(ui,condition, attrName){
				var self = this;

				ui.find("a.addDynamicAttrBtn").off().on({
					click: function(e){
						if (!e.data.locked){
							var $divItemList = ui.find('div#dynamicAttributeItemList');
							var $divDynamicAttributeItem = $divItemList.find('div#dynamicAttributeItemPattern').clone();
							var inDynamicAttribute = ui.find("select#dynamicAttributeList >option:gt(0):selected:eq(0)").text();
							var $ulAttributeValues = $divDynamicAttributeItem.find("div#dynamicAttributeValues");

							if($.isNotBlank(inDynamicAttribute)){
								if($divItemList.find("div#"+$.formatAsId(attrName)).length > 0){
									jAlert("Attribute already added. Please select a different attribute name.","Query Cleaning");
								}
								else{
									$ulAttributeValues.prop({id: $.formatAsId(attrName), title: attrName});
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
												var $liAttributeValue = $ulAttributeValues.find("div#dynamicAttributeValuesPattern").clone();
												$liAttributeValue.show();
												$liAttributeValue.prop("id", "dynamicAttributeValues" + countId);
												$liAttributeValue.find("input.checkboxFilter").prop({name:attrName, value:attributeValues[i]});
												$liAttributeValue.find("span#attributeValueName").text(attributeValues[i].split("|")[1]);
												$ulAttributeValues.append($liAttributeValue);
											}
										}
									}

									$divDynamicAttributeItem.prop("id", countId);
									$divDynamicAttributeItem.addClass("tempDynamicAttributeItem");
									$divDynamicAttributeItem.show();
									$divItemList.append($divDynamicAttributeItem);

									self.addDeleteDynamicAttributeButtonListener($divDynamicAttributeItem, e.data.condition);
								}
							}
							else{
								jAlert("Please select a dynamic attribute.","Query Cleaning");
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
						var valid = true;

						if (!$.isBlank(condMap["CatCode"]) && !validateCatCode("Category Code", condMap["CatCode"])){
							return;
						}

						if ($.isEmptyObject(condMap)){
							jAlert('Please specify at least one filter condition',"Query Cleaning");
							return;
						}else{
							$.each(condMap, function(idx, el){
								$.each(el, function(i,elem){
									if(!validateGeneric("Input", elem)) {
										valid = false;
										return;
									}
								});
							});
						}
						
						if(!valid){
							return;
						}

						if ($item.hasClass("tempConditionItem")){
							RedirectServiceJS.addCondition(self.selectedRule["ruleId"], condMap, {
								callback:function(data){
									if (data!=null){
										var list = data.list;
										var newItem = list[data.totalSize-1];
										$item.removeClass("tempConditionItem");
										$item.find("a.conditionFormattedText").text(newItem["readableString"]);
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
										$item.find("a.conditionFormattedText").text(updatedItem["readableString"]);
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
								jAlert("You have an unsaved filter group","Query Cleaning");
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
								jAlert("You have an unsaved filter group","Query Cleaning");
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
								$divItem.find("div.cnet, div.facet, div.dynamicAttribute").show();
								break;
							case "ims": 
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
						if(e.data.locked) return;

						var $item = $(this).parents(".conditionItem");
						var readableString = $.isNotBlank(e.data.condition)? e.data.condition["readableString"] : $item.find(".conditionFormattedText").html();

						jConfirm("Delete " + readableString, "Delete Rule Condition", function(result){
							if(result){
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
						});
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
								
								$divItem.find(".conditionFormattedText").text(item["readableString"]);
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

			addTabListener: function(){
				var self = this;

				$("#redirect-type").tabs("destroy").tabs({
					show: function(event, ui){
						var tabNumber = ui.index;
						self.tabSelectedTypeId = tabNumber + 1;
						self.setActiveRedirectType();
						switch(self.tabSelectedTypeId){
						case 1: self.showRuleCondition(); self.setIncludeKeyword(); break;
						case 2: self.getChangeKeywordActiveRules(); break;
						}
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
