/**
 * TODO: 
 * 1. Confirmation text should be informative
 * 2. Persist current page of rule list
 * 	  a. page refresh
 *    b. new rule
 *    c. delete rule
 * 3. Persist selected rule when page refresh (cookie)    
 */
(function($){
	var moduleName="Query Cleaning";

	var selectedRule = null;
	var selectedRuleStatus = null;

	var rulePageSize = 5;
	var ruleConditionPageSize = 5;
	var keywordInRulePageSize = 5;
	var ruleKeywordPageSize = 5;

	var deleteRuleConfirmText = "Continue deleting this rule?";
	var deleteKeywordInRuleConfirmText = "Continue deleting this keyword?";

	var prepareRedirect = function(){
		clearAllQtip();
		$("#preloader").show();
		$("#submitForApproval").hide();
		$("#noSelected").hide();
		$("#redirect").hide();
		$("#titleHeader").html("");
	};

	var getSelectedCategory = function() { 
		rule = "";
		
		category = $("select#categoryList option[value!='all']:selected").val();
		subCategory = $("select#subCategoryList option[value!='all']:selected").val();
		clazz = $("select#classList option[value!='all']:selected").val();
		minor = $("select#minorList option[value!='all']:selected").val();

		if ($.isNotBlank(category)){
			rule=category;
			if ($.isNotBlank(subCategory)){
				rule=subCategory;
				if ($.isNotBlank(clazz)){
					rule=clazz;
					if ($.isNotBlank(minor)){
						rule=minor;
					}
				}				
			}
		}
		return rule;
	};
	
	var showRedirect = function(){
		prepareRedirect();
		getCategories(0);
		$("#preloader").hide();
		resetInputFields("#redirect");

		getRedirectRuleList(1);
		getRedirectRuleKeywordList(1);

		if(selectedRule==null){
			$("#noSelected").show();
			$("#titleText").html(moduleName);
			return;
		}

		$("#submitForApproval").show();
		$("#redirect").show();

		$("#titleText").html(moduleName + " for ");
		$("#titleHeader").html(selectedRule.ruleName);

		$("#name").val(selectedRule.ruleName);
		$("#description").val(selectedRule.description);

		getKeywordInRuleList(1);
		getRuleConditionInRuleList(1);

		$("#saveBtn").off().on({
			click: updateRule,
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked || !allowModify});

		$("#deleteBtn").off().on({
			click: deleteRule,
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked || !allowModify});

		$("a#downloadIcon").download({
			headerText:"Download Query Cleaning",
			requestCallback:function(e){
				var params = new Array();
				var url = document.location.pathname + "/xls";
				var urlParams = "";
				var count = 0;
				params["id"] = selectedRule["ruleId"];
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
		
		$("#submitForApprovalBtn").off().on({
			click: function(e){
				var ruleStatus = null;
				var data = e.data;

				if(confirm(e.data.module + " " + e.data.ruleRefName + " will be locked for approval. Continue?")){
					DeploymentServiceJS.processRuleStatus(e.data.module, e.data.ruleRefId, e.data.ruleRefName, e.data.isDelete,{
						callback: function(data){
							ruleStatus = data;
						},
						preHook:function(){
							prepareRedirect();
						},
						postHook: function(){
							setRedirect(selectedRule);
						}
					});
				}
			}
		}, { module: moduleName, ruleRefId: selectedRule.ruleId , ruleRefName: selectedRule.ruleName, isDelete: false});

		$('#auditIcon').on({
			click: showAuditList
		}, {locked: selectedRuleStatus.locked || !allowModify, type:moduleName, ruleRefId: selectedRule.ruleId, name: selectedRule.ruleName});

		var addRuleConditionRunning = false;
		$("#addRuleCondition").off().on({
			mouseenter: showHoverInfo,
			click:function() {
				
				if (!addRuleConditionRunning) {
					addRuleConditionRunning = true;
					
					var rule = getSelectedCategory();
					manufacturer = $("select#manufacturerList option[value!='all']:selected").val();

					if ($.isBlank(rule) && $.isBlank(manufacturer) && $.isBlank($("#catcodetext").val())) {
						alert("At least one category code, category or manufacturer is expected.");
						addRuleConditionRunning = false;
						return;
					}
					
					if ($.isNotBlank($("#catcodetext").val())){
						rule = $("#catcodetext").val();
						$("#catcodetext").val("");
					}

					if (rule.length > 0) {
						if (rule.length < 4 && rule.indexOf("*", 0) == -1) {
							rule = rule + "*";
						}
						rule = "CatCode:" + rule;
					}
					if (manufacturer!=null && manufacturer.length>0){
						if (rule.length > 0) {
							rule = rule + " AND ";
						}
						rule = rule + "Manufacturer:\"" + manufacturer + "\"";
					}

					if($.isNotBlank(rule)){
						RedirectServiceJS.addRuleCondition(selectedRule.ruleId, rule,{
							callback:function(code){
								showActionResponse(code, "add", rule);
								addRuleConditionRunning = false;
								getRuleConditionInRuleList(1);
							}
						});
					}
					else {
						addRuleConditionRunning = false;
					}
				}
			}
		},{locked: selectedRuleStatus.locked || !allowModify});	
	};

	var getKeywordInRuleList = function(page){
		$("#keywordInRulePanel").sidepanel({
			fieldId: "keywordId",
			fieldName: "keyword",
			page: page,
			region: "content",
			pageStyle: "style2",
			pageSize: keywordInRulePageSize,
			headerText : "Using This Rule",
			searchText : "Enter Keyword",
			showAddButton: !selectedRuleStatus.locked || allowModify,
			itemDataCallback: function(base, keyword, page){
				RedirectServiceJS.getAllKeywordInRule(selectedRule.ruleId, keyword, page, keywordInRulePageSize, {
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
						if (!e.data.locked && allowModify && confirm('Delete "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RedirectServiceJS.deleteKeywordInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									getKeywordInRuleList(1);
									getRedirectRuleList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
					},
					mouseenter: showHoverInfo
				},{locked: selectedRuleStatus.locked || !allowModify});
			},
			itemAddCallback: function(base, keyword){
				if (!selectedRuleStatus.locked || allowModify){
					RedirectServiceJS.addKeywordToRule(selectedRule.ruleId, keyword, {
						callback: function(code){
							showActionResponse(code, "add", keyword);
							getKeywordInRuleList(1);
							getRedirectRuleList(1);
						},
						preHook: function(){ base.prepareList(); }
					});
				}
			}
		});
	};

	var getRedirectRuleKeywordList = function(page){
		$("#ruleKeywordPanel").sidepanel({
			fieldId: "keywordId",
			fieldName: "keyword",
			page: page,
			pageSize: ruleKeywordPageSize,
			headerText : "Query Cleaning Keyword",
			searchText : "Enter Keyword",
			showAddButton: false,
			itemDataCallback: function(base, keyword, page){
				StoreKeywordServiceJS.getAllKeyword(keyword, page, ruleKeywordPageSize,{
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
	};

	var getRuleConditionInRuleList = function(page){
		$("#ruleConditionPanel").sidepanel({
			fieldId: "",
			fieldName: "condition",
			page: page,
			region: "content",
			pageStyle: "style2",
			pageSize: ruleConditionPageSize,
			headerText : "Rule Condition",
			searchText : "Enter Keyword",
			showAddButton: false,
			itemDataCallback: function(base, keyword, page){
				RedirectServiceJS.getConditionInRule(selectedRule.ruleId, page, ruleConditionPageSize, {
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
						if (!e.data.locked && allowModify && confirm('Delete "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RedirectServiceJS.deleteConditionInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									getRuleConditionInRuleList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
					},
					mouseenter: showHoverInfo
				},{locked: selectedRuleStatus.locked || !allowModify});
			}
		});
	};

	var setRedirect = function(rule){
		selectedRule = rule;

		if (rule!=null){
			DeploymentServiceJS.getRuleStatus(moduleName, selectedRule.ruleId, {
				callback:function(data){
					selectedRuleStatus = data;
					$('#itemPattern' + $.escapeQuotes($.formatAsId(selectedRule.ruleId)) + ' div.itemSubText').html(getRuleNameSubTextStatus(selectedRuleStatus));
					showDeploymentStatusBar(moduleName, selectedRuleStatus);
					showRedirect();
				},
				preHook: function(){
					prepareRedirect();
				}
			});		
		}else{
			showRedirect();
		}
	};

	var getRedirectRuleList = function(page) { 

		$("#rulePanel").sidepanel({
			fieldId: "ruleId",
			fieldName: "ruleName",
			page: page,
			pageSize: rulePageSize,
			headerText : "Query Cleaning Rule",
			searchText : "Enter Name",
			showAddButton: allowModify,
			itemDataCallback: function(base, keyword, page){
				RedirectServiceJS.getAllRule(keyword, page, rulePageSize, {
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
										setRedirect(data);
									}else{
										setRedirect(selectedRule);
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
								setRedirect(model);
							}
						});
					},
					preHook: function(){ 
						base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
					}
				});

				DeploymentServiceJS.getRuleStatus(moduleName, id, {
					callback:function(data){
						base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
					}
				});
			}
		});
	};

	var checkIfUpdateAllowed = function(){
		var ruleName = $.trim($('div#redirect input[id="name"]').val());  
		var description = $.trim($('div#redirect textarea[id="description"]').val());  
		isDirty = false;

		isDirty = isDirty || (ruleName.toLowerCase()!==$.trim(selectedRule.ruleName).toLowerCase());
		isDirty = isDirty || (description.toLowerCase()!==$.trim(selectedRule.description).toLowerCase());

		return isDirty;
	};

	var updateRule = function(e) { 
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
				RedirectServiceJS.checkForRuleNameDuplicate(selectedRule.ruleId, ruleName, {
					callback: function(data){
						if (data==true){
							alert("Another query cleaning rule is already using the name provided.");
						}else{
							var response = 0;
							RedirectServiceJS.updateRule(selectedRule.ruleId, ruleName, description, {
								callback: function(data){
									response = data;
									showActionResponse(response, "update", ruleName);
								},
								preHook: function(){
									prepareRedirect();
								},
								postHook: function(){
									if(response==1){
										RedirectServiceJS.getRule(selectedRule.ruleId,{
											callback: function(data){
												setRedirect(data);
											},
											preHook: function(){
												prepareRedirect();
											}
										});
									}
									else{
										setRedirect(selectedRule);
									}

								}
							});
						}
					}
				});
			}
		}
	};

	var deleteRule = function(e) { 
		if (!e.data.locked  && allowModify && confirm(deleteRuleConfirmText)){
			RedirectServiceJS.deleteRule(selectedRule,{
				callback: function(code){
					showActionResponse(code, "delete", selectedRule.ruleName);
					if(code==1) setRedirect(null);
				}
			});
		}
	};

	var getCategories = function(level) { 
	
		var selectList;
		
		if (level <= 3) {
			$("select#minorList option").remove();
			$("select#minorList").append($("<option>", { value : "all", selected: "selected" }).text("All Minor Classes"));
			$("input#minorList").val($("#minorList option:[value='all']").text());
			selectList = $("select#minorList");					
		}
		if (level <= 2) {
			$("select#classList option").remove();
			$("select#classList").append($("<option>", { value : "all", selected: "selected" }).text("All Classes")); 
			$("input#classList").val($("#classList option:[value='all']").text());
			selectList = $("select#classList");
		}		
		if (level <= 1) {
			$("select#subCategoryList option").remove();
			$("select#subCategoryList").append($("<option>", { value : "all", selected: "selected" }).text("All Subcategories")); 
			$("input#subCategoryList").val($("#subCategoryList option:[value='all']").text());
			selectList = $("select#subCategoryList");
		}		
		if (level == 0) {
			$("select#categoryList option").remove();
			$("select#categoryList").append($("<option>", { value : "all", selected: "selected" }).text("All Categories")); 
			$("input#categoryList").val($("#categoryList option:[value='all']").text());
			selectList = $("select#categoryList");					
		}		
		$("select#manufacturerList option").remove();
		$("select#manufacturerList").append($("<option>", { value : "all", selected: "selected" }).text("All Manufacturers"));
		$("input#manufacturerList").val($("#manufacturerList option:[value='all']").text());

		catCode = getSelectedCategory();
		$("input#minorList").attr("disabled","disabled");
		$("input#manufacturerList").attr("disabled","disabled");
		$("input#classList").attr("disabled","disabled");
		$("input#subCategoryList").attr("disabled","disabled");
		$("input#categoryList").attr("disabled","disabled");
		$("span.ui-combobox a.ui-button").hide();
		$("img.loadIcon").show();

		CategoryServiceJS.getCategories(catCode,{
			callback: function(data){				
				categories = data.categories;
				manufacturers = data.manufacturers;
				
				for (var i = 0; i < categories.length; i++) {
					category = categories[i];
					selectList.append($("<option>", { value : category.catCode }).text(category.catCode + " - " + category.catName)); 
				}

				$.each(manufacturers, function(key, element) {
					if (element.length > 0) {
						$("select#manufacturerList").append($("<option>", { value : element }).text(element)); 
					}
				});
				
				$("input#categoryList").removeAttr("disabled");
				$("input#subCategoryList").removeAttr("disabled");
				$("input#classList").removeAttr("disabled");
				$("input#minorList").removeAttr("disabled");
				$("input#manufacturerList").removeAttr("disabled");
				$("img.loadIcon").hide();
				$("span.ui-combobox a.ui-button").show();

			}
		});
	};

	init = function() {
		showRedirect();
	};

	$(document).ready(function() {
		$("select#categoryList").append($("<option>", { value : "all", selected: "selected"}).text("All Categories")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				getCategories(1);
			}
		});

		$("select#subCategoryList").append($("<option>", { value : "all", selected: "selected"}).text("All Subcategories")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				if (catCode.length==0) {
					catCode=$("#categoryList option:selected").val();
				}
				getCategories(2);
			}
		});

		$("select#classList").append($("<option>", { value : "all", selected: "selected"}).text("All Classes")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				if (catCode.length==0) {
					catCode=$("#subCategoryList option:selected").val();
				}
				getCategories(3);
			}
		});

		$("select#minorList").append($("<option>", { value : "all", selected: "selected"}).text("All Minor Classes")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				if (catCode.length==0) {
					catCode=$("#classList option:selected").val();
				}
				getCategories(4);
			}
		});

		$("select#manufacturerList").append($("<option>", { value : "all", selected: "selected"}).text("All Manufacturers")).combobox(); 

		init();
	});	
})(jQuery);	
