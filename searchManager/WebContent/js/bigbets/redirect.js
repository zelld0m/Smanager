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

	var catCode = "";
	var selectedRule = null;
	var selectedRuleStatus = null;

	var rulePageSize = 10;
	var ruleConditionPageSize = 5;
	var keywordInRulePageSize = 5;

	var deleteRuleConfirmText = "Continue deleting this rule?";
	var deleteKeywordInRuleConfirmText = "Continue deleting this keyword?";

	var prepareRedirect = function(){
		$("#preloader").show();
		$("#submitForApproval").hide();
		$("#noSelected").hide();
		$("#relevancy").hide();
	};
	
	var showRedirect = function(){
		prepareRedirect();
		$("#preloader").hide();
		resetInputFields("#redirect");

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
		},{locked:selectedRuleStatus.locked});

		$("#deleteBtn").off().on({
			click: deleteRule,
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked});

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

		$("#addRuleCondition").off().on({
			mouseenter: showHoverInfo,
			click:function() {
				category = $("select#categoryList option[value!='all']:selected").val();
				subCategory = $("select#subCategoryList option[value!='all']:selected").val();
				clazz = $("select#classList option[value!='all']:selected").val();
				minor = $("select#minorList option[value!='all']:selected").val();
				manufacturer = $("select#manufacturerList option[value!='all']:selected").val();

				var rule = "";

				if ($.isBlank(category) && $.isBlank(manufacturer) && $.isBlank($("#catcodetext").val())) {
					alert("At least one category code, category or manufacturer is expected.");
					return;
				} else if ($.isNotBlank($("#catcodetext").val())){
					rule = $("#catcodetext").val();
					$("#catcodetext").val("");
				} else if ($.isNotBlank(minor)){
					rule=minor;
				} else if ($.isNotBlank(clazz)){
					rule=clazz;
				} else if ($.isNotBlank(subCategory)){
					rule=subCategory;
				} else if ($.isNotBlank(category)){
					rule=category;
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
							getRuleConditionInRuleList(1);
						}
					});
				}
			}
		},{locked: selectedRuleStatus.locked});	
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
			showAddButton: !selectedRuleStatus.locked,
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
						if (!e.data.locked && confirm('Delete "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RedirectServiceJS.deleteKeywordInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									getKeywordInRuleList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
					},
					mouseenter: showHoverInfo
				},{locked: selectedRuleStatus.locked});
			},
			itemAddCallback: function(base, keyword){
				if (!selectedRuleStatus.locked){
					RedirectServiceJS.addKeywordToRule(selectedRule.ruleId, keyword, {
						callback: function(code){
							showActionResponse(code, "add", keyword);
							getKeywordInRuleList(1);
						},
						preHook: function(){ base.prepareList(); }
					});
				}
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
						if (!e.data.locked && confirm('Delete "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RedirectServiceJS.deleteConditionInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									getRuleConditionInRuleList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
					},
					mouseenter: showHoverInfo
				},{locked: selectedRuleStatus.locked});
			}
		});
	};

	var setRedirect = function(rule){
		selectedRule = rule;

		DeploymentServiceJS.getRuleStatus(moduleName, selectedRule.ruleId, {
			callback:function(data){
				selectedRuleStatus = data;
				$('#itemPattern' + $.escapeQuotes($.formatAsId(selectedRule.ruleId)) + ' div.itemSubText').html(getRuleNameSubTextStatus(selectedRuleStatus));
				showDeploymentStatusBar(selectedRuleStatus);
				showRedirect();
			}
		});		
	};

	var getRedirectRuleList = function(page) { 

		$("#rulePanel").sidepanel({
			fieldId: "ruleId",
			fieldName: "ruleName",
			page: page,
			pageSize: rulePageSize,
			headerText : "Query Cleaning Rule",
			searchText : "Enter Name",

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
		var ruleName = $.trim($("#name").val());  
		var description = $.trim($("#description").val());  
		isDirty = false;
		console.log(selectedRule);
		isDirty = isDirty || (ruleName.toLowerCase()!==$.trim(selectedRule.ruleName).toLowerCase());
		isDirty = isDirty || (description.toLowerCase()!==$.trim(selectedRule.description).toLowerCase());

		// Required field
		isDirty = isDirty && $.isNotBlank(ruleName);

		return isDirty;
	};

	var updateRule = function(e) { 
		if (e.data.locked) return;
		var ruleName = $.trim($("#name").val());  
		var description = $.trim($("#description").val());  

		if (checkIfUpdateAllowed()){
			var response = 0;
			RedirectServiceJS.updateRule(selectedRule.ruleId, ruleName, description, {
				callback: function(data){
					response = data;
				},
				postHook: function(){
					RedirectServiceJS.getRule(selectedRule.ruleId,{
						callback: function(data){
							showActionResponse(response, "update", ruleName);
							setRedirect(selectedRule);
						}
					});
				}
			});
		}else{

			if ($.isBlank(ruleName)){
				showMessage("#name", "Rule name is required");
				$("#name").val(selectedRule.ruleName);
			}
		}

	};

	var deleteRule = function(e) { 
		if (!e.data.locked && confirm(deleteRuleConfirmText)){
			RedirectServiceJS.deleteRule(selectedRule,{
				callback: function(code){
					showActionResponse(code, "delete", selectedRule.ruleName);
				}
			});
		}
	};

	var getCategories = function() { 
		CategoryServiceJS.getCategories(catCode,{
			callback: function(data){
				var selectList;
				categories = data.categories;
				manufacturers = data.manufacturers;

				switch(catCode.length){
				case 0: 
					$("#categoryList, #subCategoryList, #classList, #minorList").filter(":not option[value='all']").remove();
					selectList = $("#categoryList");
					break;
				case 1: 
					$("#subCategoryList, #classList, #minorList").filter(":not option[value='all']").remove();
					selectList = $("#subCategoryList");
					break;
				case 2: 
					$("#classList, #minorList").filter(":not option[value='all']").remove();
					selectList = $("#classList");
					break;
				case 3: 
					$("#minorList").filter(":not option[value='all']").remove();
					selectList = $("#minorList");
					break;
				}

				for (var i = 0; i < categories.length; i++) {
					category = categories[i];
					selectList.append($("<option>", { value : category.catCode }).text(category.catName)); 
				}

				$("#manufacturerList :not option[value='all']").remove();
				$.each(manufacturers, function(key, element) {
					if (element.length > 0) {
						$("#manufacturerList").append($("<option>", { value : element }).text(element)); 
					}
				});
			}
		});
	};

	init = function() {
		showRedirect();
		getRedirectRuleList();
		getCategories();
	};

	$(document).ready(function() {
		$("#categoryList").append($("<option>", { value : "all", selected: "selected"}).text("All Category")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				$("input#subCategoryList").val($("#subCategoryList option:[value='all']").text());
				$("input#classList").val($("#classList option:[value='all']").text());
				$("input#minorList").val($("#minorList option:[value='all']").text());
				$("input#manufacturerList").val($("#manufacturerList option:[value='all']").text());
				getCategories(catCode);
			}
		});

		$("select#subCategoryList").append($("<option>", { value : "all", selected: "selected"}).text("All Subcategory")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				if (catCode.length==0) {
					catCode=$("#categoryList option:selected").val();
				}
				$("input#classList").val($("#classList option:[value='all']").text());
				$("input#minorList").val($("#minorList option:[value='all']").text());
				$("input#manufacturerList").val($("#manufacturerList option:[value='all']").text());
				getCategories(catCode);
			}
		});

		$("select#classList").append($("<option>", { value : "all", selected: "selected"}).text("All Class")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				if (catCode.length==0) {
					catCode=$("#subCategoryList option:selected").val();
				}
				$("input#minorList").val($("#minorList option:[value='all']").text());
				$("input#manufacturerList").val($("#manufacturerList option:[value='all']").text());
				getCategories(catCode);
			}
		});

		$("select#minorList").append($("<option>", { value : "all", selected: "selected"}).text("All Minor")).combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				if (catCode.length==0) {
					catCode=$("#classList option:selected").val();
				}
				$("input#manufacturerList").val($("#manufacturerList option:[value='all']").text());
				getCategories(catCode);
			}
		});

		$("select#manufacturerList").append($("<option>", { value : "all", selected: "selected"}).text("All Manufacturers")).combobox(); 

		init();
	});	
})(jQuery);	