(function($){
	var selectedRule = null;
	var keywordInRulePageSize = 5;
	var rulePageSize = 5;
	var ruleItemPageSize = 5
	var catCode = "";

	$(document).ready(function() { 
		
		var showRedirect = function(){
			selectedRule!=null ? $("#submitForApproval").show() : $("#submitForApproval").hide();
			selectedRule!=null ? $("#noSelected").hide():$("#noSelected").show();
			selectedRule==null ? $("#redirect").hide(): $("#redirect").show();
			$("#preloader").hide();
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
							if (confirm('Remove "' + name + '" in ' + selectedRule.ruleName  + '?'))
								RedirectServiceJS.deleteKeywordInRule(selectedRule.ruleId, name,{
									callback:function(data){
										refreshKeywordInRuleList(1);
									},
									preHook: function(){ base.prepareList(); }
								});
						}
					});
				},
				itemAddCallback: function(base, keyword){
					RedirectServiceJS.addKeywordToRule(selectedRule.ruleId, keyword, {
						callback: function(data){
							alert(keyword + " added successfully");
							refreshKeywordInRuleList(1);
						},
						preHook: function(){ base.prepareList(); }
					});
				}
			});
		};
		
		var refreshRuleItemList = function(page){
			$("#ruleItemPanel").sidepanel({
				fieldId: "keywordId",
				fieldName: "keyword",
				page: page,
				region: "content",
				pageStyle: "style2",
				pageSize: rulePageSize,
				headerText : "Rule Item Condition",
				searchText : "Enter Keyword",
				showAddButton: false,
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
							if (confirm('Remove "' + name + '" in ' + selectedRule.ruleName  + '?'))
								RedirectServiceJS.deleteKeywordInRule(selectedRule.ruleId, name,{
									callback:function(data){
										refreshKeywordInRuleList(1);
									},
									preHook: function(){ base.prepareList(); }
								});
						}
					});
				},
				itemAddCallback: function(base, keyword){
					RedirectServiceJS.addKeywordToRule(selectedRule.ruleId, keyword, {
						callback: function(data){
							alert(keyword + " added successfully");
							refreshKeywordInRuleList(1);
						},
						preHook: function(){ base.prepareList(); }
					});
				}
			});
		};

		var getRedirectRuleList = function(ruleId, page) { 

			$("#redirectSidePanel").sidepanel({
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
					RedirectServiceJS.addRedirectRule(name, {
						callback: function(data){
							base.getList(name, 1);
						},
						preHook: function(){ base.prepareList(); }
					});
				},

				itemOptionCallback: function(base, id, name){
					DeploymentServiceJS.getRuleStatus("Query Cleaning", id, {
						callback:function(data){
							var status = (data==null) ? "" : data["approvalStatus"];
							
							switch (status){
								case "REJECTED": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Action Required"); break;
								case "PENDING": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Awaiting Approval"); break;
								case "APPROVED": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Ready For Production"); break;
								default: base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Setup a Rule"); break;
							}	
						}
					});
				},
				
				itemNameCallback: function(e){
					selectedRule = e.data.model;
					showRedirect();
					refreshKeywordInRuleList(1);
					refreshRuleItemList(1);
				}
			});
			
		};
		
		var updateRule = function() { 
			RedirectServiceJS.updateRedirectRule(ruleId, ruleName, {
				callback: function(data){

				}
			});
		};

		var deleteRule = function() { 
			RedirectServiceJS.removeRedirectRule(ruleId, ruleName, searchTerm, {
				callback: function(data){

				}
			});
		};

		var submitForApprovalHandler = function(){
			$("a#submitForApprovalBtn").on({
				click: function(){
					if(confirm("This query cleaning rule will be locked for approval. Continue?"))
						DeploymentServiceJS.processRuleStatus("Query Cleaning", ruleId, ruleName, false,{
							callback: function(data){

							}
						});
				}
			});
		};	
		
		var getCategories = function() { 
			CategoryServiceJS.getCategories(catCode,{
				callback: function(data){
					var selectList;
					categories = data.categories;
					manufacturers = data.manufacturers;
					
					switch(catCode.length){
						case 0: 
							$("#categoryList option").remove();
							$("#subCategoryList option").remove();
							$("#classList option").remove();
							$("#minorList option").remove();
							selectList = $("#categoryList");
							selectList.append($("<option>", { value : "", selected: "selected" }).text("All Categories")); 
							break;
						case 1: 
							$("#subCategoryList option").remove();
							$("#classList option").remove();
							$("#minorList option").remove();
							selectList = $("#subCategoryList");
							selectList.append($("<option>", { value : "" }).text("All Sub-categories"));
							break;
						case 2: 
							$("#classList option").remove();
							$("#minorList option").remove();
							selectList = $("#classList");
							selectList.append($("<option>", { value : "" }).text("All Class")); 
							break;
						case 3: 
							$("#minorList option").remove();
							selectList = $("#minorList");
							selectList.append($("<option>", { value : "" }).text("All Minor")); 
							break;
					}
				
					for (var i = 0; i < categories.length; i++) {
						category = categories[i];
						selectList.append($("<option>", { value : category.catCode }).text(category.catName)); 
					}

					$("#manufacturerList option").remove();
					$("#manufacturerList").append($("<option>", { value : "" }).text("All Manufacturers")); 
					$.each(manufacturers, function(key, element) {
						if (element.length > 0) {
							$("#manufacturerList").append($("<option>", { value : element }).text(element)); 
						}
					});
				}
			});
		};
		
		$("#categoryList").combobox({
			selected: function(event, ui) {
				catCode = $(this).val();
				getCategories(catCode);
			}
		});


		$("#subCategoryList").combobox({
			selected: function(event, ui) {
			catCode = $(this).val();
			if (catCode.length==0) {
				catCode=$("#categoryList option:selected").val();
			}
			getCategories(catCode);
			}
		});

		$("#classList").combobox({
			selected: function(event, ui) {
			catCode = $(this).val();
			if (catCode.length==0) {
				catCode=$("#subCategoryList option:selected").val();
			}
			getCategories(catCode);
			}
		});
		
		$("#minorList").combobox({
			selected: function(event, ui) {
			catCode = $(this).val();
			if (catCode.length==0) {
				catCode=$("#classList option:selected").val();
			}
			getCategories(catCode);
			}
		});
		
		$("#manufacturerList").combobox();
		
		
		$("#addRule").on({
			click:function() {
				category = $("#categoryList option:selected").val();
		    	subCategory = $("#subCategoryList option:selected").val();
		    	clazz = $("#classList option:selected").val();
		    	minor = $("#minorList option:selected").val();
		    	manufacturer = $("#manufacturerList option:selected").val();
				
		    	var rule = "";
		    	
		    	if (category.length==0 && manufacturer.length==0 && $("#catcodetext").val().length==0) {
		    		alert("At least one category code, category or manufacturer is expected.");
		    		return;
		    	} else if ($("#catcodetext").val().length>0){
		    		rule = $("#catcodetext").val();
		    		$("#catcodetext").val("");
		    	} else if (minor!=null && minor.length>0){
		    		rule=minor;
		    	} else if (clazz!=null && clazz.length>0){
		    		rule=clazz;
		    	} else if (subCategory!=null && subCategory.length>0){
		    		rule=subCategory;
		    	} else if (category!=null && category.length>0){
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
		    		RedirectServiceJS.addRedirectCondition(selectedRule.ruleId, rule,{
		    			callback:function(data){
		    				
		    			}
		    		});
		    	}
			}
		});
		
		init = function() {
			showRedirect();
			getRedirectRuleList();
			getCategories();
		};

		init();
	});	
})(jQuery);	