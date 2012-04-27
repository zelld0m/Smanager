/**
 * TODO: 
 * 1. Confirmation text should be informative
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

	var showRedirect = function(){
		$("#preloader").hide();

		if(selectedRule==null){
			$("#submitForApproval").hide();
			$("#noSelected").show();
			$("#redirect").hide();
			$("#titleText").html(moduleName);
			return;
		}

		$("#submitForApproval").show();
		$("#noSelected").hide();
		$("#redirect").show();

		$("#titleText").html(moduleName + " for ");
		$("#titleHeader").html(selectedRule.ruleName);
		$("#name").val(selectedRule.ruleName);
		$("#description").val(selectedRule.description);

		refreshKeywordInRuleList(1);
		refreshRuleConditionList(1);

		$("#saveBtn").on({
			click: updateRule,
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked});

		$("#deleteBtn").on({
			click: deleteRule,
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked});

		$("#submitForApprovalBtn").on({
			click: function(e){
				var ruleStatus = null;
				var data = e.data;

				if(confirm(e.data.module + " rule will be locked for approval. Continue?")){
					DeploymentServiceJS.processRuleStatus(e.data.module, e.data.ruleRefId, e.data.ruleRefName, e.data.isDelete,{
						callback: function(data){
							ruleStatus = data;
						},
						preHook:function(){
							$("#preloader").show();
							$("#redirect").hide();
						},
						postHook: function(){
							setRedirect(selectedRule, ruleStatus);
						}
					});
				}
			}
		}, { module: moduleName, ruleRefId: selectedRule.ruleId , ruleRefName: selectedRule.ruleName, isDelete: false});

		$("#addRuleCondition").on({
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
					RedirectServiceJS.addRedirectRuleCondition(selectedRule.ruleId, rule,{
						callback:function(code){
							showActionResponse(code, "add", rule);
							refreshRuleConditionList(1);
						}
					});
				}
			}
		},{locked: selectedRuleStatus.locked});	
	};

	var refreshKeywordInRuleList = function(page){
		$("#keywordInRulePanel").sidepanel({
			fieldId: "keywordId",
			fieldName: "keyword",
			page: page,
			region: "content",
			pageStyle: "style2",
			pageSize: keywordInRulePageSize,
			headerText : "Using This Rule",
			searchText : "Enter Keyword",
			itemDataCallback: function(base, keyword, page){
				RedirectServiceJS.getKeywordInRule(selectedRule.ruleId, keyword, page, keywordInRulePageSize, {
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
						if (!e.data.locked && confirm('Remove "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RedirectServiceJS.deleteKeywordInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									refreshKeywordInRuleList(1);
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
							refreshKeywordInRuleList(1);
						},
						preHook: function(){ base.prepareList(); }
					});
				}else{
					//TODO: Trigger showHoverInfo
				}
			}
		});
	};

	var refreshRuleConditionList = function(page){
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
						if (confirm('Remove "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RedirectServiceJS.deleteConditionInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									refreshRuleConditionList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
					}
				});
			}
		});
	};

	var setRedirect = function(rule, ruleStatus){
		selectedRule = rule;
		selectedRuleStatus = ruleStatus;
		showDeploymentStatusBar(selectedRuleStatus);
		showRedirect();
	};

	var getRedirectRuleList = function(ruleId, page) { 

		$("#redirectRulePanel").sidepanel({
			fieldId: "ruleId",
			fieldName: "ruleName",
			page: page,
			pageSize: 5,
			headerText : "Query Cleaning Rule",
			searchText : "Enter Name",

			itemDataCallback: function(base, keyword, page){
				RedirectServiceJS.getRedirectRule(keyword, ruleId, page, rulePageSize, {
					callback: function(data){
						base.populateList(data);
						base.addPaging(keyword, page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},

			itemAddCallback: function(base, name){
				RedirectServiceJS.addRedirectRuleAndGetModel(name, {
					callback: function(model){
						base.getList(name, 1);
						setRedirect(model, null);
					},
					preHook: function(){ base.prepareList(); }
				});
			},

			itemOptionCallback: function(base, id, name, model){
				var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));
				var ruleStatus = null;

				DeploymentServiceJS.getRuleStatus(moduleName, id, {
					callback:function(data){
						ruleStatus = data;
						var status = (ruleStatus==null) ? "" : ruleStatus["approvalStatus"];

						switch (status){
						case "REJECTED": base.$el.find(selector + ' div.itemSubText').html("Action Required"); break;
						case "PENDING": base.$el.find(selector + ' div.itemSubText').html("Awaiting Approval"); break;
						case "APPROVED": base.$el.find(selector + ' div.itemSubText').html("Ready For Production"); break;
						default: base.$el.find(selector + ' div.itemSubText').html("Setup a Rule"); break;
						}	
					},
					postHook: function(){
						RedirectServiceJS.getRedirectKeywordCount(id,{
							callback: function(count){

								var totalText = (count == 0) ? "&#133;": "(" + count + ")"; 
								base.$el.find(selector + ' div.itemLink a').html(totalText);

								base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').on({
									click: function(e){
										setRedirect(model, ruleStatus);
									}
								});
							},
							preHook: function(){ 
								base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
							}
						});
					}
				});


			}
		});
	};

	var checkIfUpdateAllowed = function(){
		var ruleName = $.trim($("#name").val());  
		var ruleDescription = $.trim($("#description").val());  
		isDirty = false;

		isDirty = isDirty || (ruleName.toLowerCase()!==selectedRule.ruleName.toLowerCase());
		isDirty = isDirty || (ruleDescription.toLowerCase()!==selectedRule.description.toLowerCase());

		// Required field
		isDirty = isDirty && $.isNotBlank(ruleName);

		return isDirty;
	};

	var updateRule = function(e) { 
		if (e.data.locked) return
		var ruleName = $.trim($("#name").val());  
		var ruleDescription = $.trim($("#description").val());  

		if (checkIfUpdateAllowed()){
			RedirectServiceJS.updateRedirectRule(selectedRule, ruleName, ruleDescription, {
				callback: function(data){
					alert("Rule info updated successfully");
					setRedirect(selectedRule);
				}
			});
		}
		else{
			if ($.isBlank(ruleName)){
				showMessage("#name", "Rule name is required");
				$("#name").val(selectedRule.ruleName);
			}
		}
	};

	var deleteRule = function(e) { 
		if (!e.data.locked && confirm(deleteRuleConfirmText)){
			RedirectServiceJS.deleteRedirectRule(selectedRule,{
				callback: function(data){

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