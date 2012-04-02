(function($){
	var catCode = "";
	var ruleId = "";
	var ruleName = "";
	var searchTerm = "";
	var rules;
	var ruleCondition = "";
	var startRow = 0;
	var endRow = 100;
	var storeId = "macmall";
	var active = 1;
	var priority = 1;
	var ruleCnt = 0;
	var redirectFlag= false;
	
	var addRule = function() { RedirectServiceJS.addRedirectRule(ruleName,searchTerm, ruleCondition, storeId, active, priority, {
		callback: function(data){
			if (data > 0) {
				clearValues();
				ruleId = "";
				initPage();
			}
		},
		errorHandler: function(message){ alert(message); }
	});
	};

	var updateRule = function() { RedirectServiceJS.updateRedirectRule(ruleId, ruleName, searchTerm, ruleCondition, storeId, active, priority, {
		callback: function(data){
			if (data > 0) {
				clearValues();
				ruleId = "";
				initPage();
			}
		},
		errorHandler: function(message){ alert(message); }
	});
	};
	
	var deleteRule = function() { RedirectServiceJS.removeRedirectRule(ruleId, ruleName, {
		callback: function(data){
//			if (data > 0) {
//				alert($('#kDispPattern' + ruleId).length > 0);
//				$('#kDispPattern' + ruleId).remove();
//				ruleId = "";
//				clearValues();
//				initPage();
//			}
		},
		postHook: function(){
//			alert($('#kDispPattern' + ruleId).length > 0);
			$('#kDispPattern' + ruleId).remove();
			ruleId = "";
			clearValues();
			initPage();
		},
		errorHandler: function(message){ alert(message); }
	});
	};

	function setRuleForEdit(rule) {
		clearValues();
		ruleId = rule.ruleId;
		$("#ruleId").val(rule.ruleId);
		$("#ruleName").val(rule.ruleName);
		$("#headerRuleName").text(rule.ruleName);
		$("#priority").text(rule.priority);
		$("#activeFlag").text(rule.activeFlag);

		var searchTerms = rule.searchTerm.split("||");
		
		for (var i = 0; i < searchTerms.length; i++) {
			$("#searchTermList").append($("<option>", { value : searchTerms[i] }).text(searchTerms[i])); 
		}				
		if (rule.condition.indexOf("http://") > -1) {
			redirectFlag = true;
			$("#url").val(rule.condition);
		} else {
			var conditions = rule.condition.split("||");
			for (var i = 0; i < conditions.length; i++) {
				$("#ruleList").append($("<option>", { value : conditions[i] }).text(conditions[i])); 
			}				
		}
	}
	
	function clearValues() {
		$("#ruleId").val("");
		$("#ruleName").val("");
		$("#addRuleName").val("");
		$("#headerRuleName").text("");
		$("#ruleList option").remove();
		$("#searchTermList option").remove();
		$("#searchTerm").val("Add Search Term");
		$("#url").val("http://");
		redirectFlag = false;
		catCode = "";
		searchTerm = "";
	}
	
	var getCategories = function() { CategoryServiceJS.getCategories(catCode,{
		callback: function(data){
			var selectList;
			categories = data.categories;
			manufacturers = data.manufacturers;
			if (catCode.length == 0) {
				
				$("#categoryList option").remove();
				$("#subCategoryList option").remove();
				$("#classList option").remove();
				$("#minorList option").remove();
				selectList = $("#categoryList");
				selectList.append($("<option>", { value : "" }).text("All Categories")); 
			} else if (catCode.length == 1) {
				$("#subCategoryList option").remove();
				$("#classList option").remove();
				$("#minorList option").remove();
				selectList = $("#subCategoryList");
				selectList.append($("<option>", { value : "" }).text("All Sub-categories")); 
			} else if (catCode.length == 2) {
				
				$("#classList option").remove();
				$("#minorList option").remove();
				selectList = $("#classList");
				selectList.append($("<option>", { value : "" }).text("All Class")); 
			} else if (catCode.length == 3) {
				
				$("#minorList option").remove();
				selectList = $("#minorList");
				selectList.append($("<option>", { value : "" }).text("All Minor")); 
			}
			for (var i = 0; i < categories.length; i++) {
				category = categories[i];
				selectList.append($("<option>", { value : category.catCode }).text(category.catName)); 
			}				
			
//			selectList.combobox();
			$("#manufacturerList option").remove();
			$("#manufacturerList").append($("<option>", { value : "" }).text("All Manufacturers")); 
			$.each(manufacturers, function(key, element) {
				if (element.length > 0) {
					$("#manufacturerList").append($("<option>", { value : element }).text(element)); 
				}
			});
		},
		errorHandler: function(message){ alert(message); }
	});
	};

	var getRedirectRuleList = function() { RedirectServiceJS.getRedirectRule(searchTerm, ruleId, storeId, startRow, endRow, {
		callback: function(data){
			rules = data.list;
			ruleCnt = data.totalSize;
			for (var i = 0; i < rules.length; i++) {
				rule = rules[i];
				dwr.util.cloneNode("kDispPattern", { idSuffix:rule.ruleId});
				$("#kDispPattern" + rule.ruleId + " div.keywordText a").html(rule.ruleName);
				$("#kDispPattern" + rule.ruleId).attr("style", "display:block"); 	
				$("#kDispPattern" + rule.ruleId + " div.keywordText a").bind('click', function(e) {
					ruleId = this.parentNode.parentNode.parentNode.parentNode.id.replace("kDispPattern","");
					if ($("#ruleId").val() == 0) {
						
					} else if (ruleId != $("#ruleId").val()) {
						clearValues();
						$("#ruleId").val(ruleId);
						getRedirectRuleList(searchTerm, ruleId, storeId, startRow, endRow);
					}
				});
				if ($("#ruleId").val() == rule.ruleId) {
					$("#searchTerm").val("Add Search Term");
					setRuleForEdit(rule);
				}
			}				
		},
		preHook: function(){ 
			dwr.util.removeAllRows("#keywordBody", { filter:function(tr) {
				return (tr.id != "kDispPattern");
			}});
		},
		postHook: function(){
			if (ruleId == "") {
				setRuleForEdit(rules[0]);
			}
		},
		errorHandler: function(message){ alert(message); }
	});
	};

	$(document).ready(function() { 
		
		UtilityServiceJS.getStoreName({
			callback: function(data){ storeId = data; },
			errorHandler: function(message){ alert(message); }
		});		
		
	    $("#ruleList").keydown(function(e) {
	    	if(e.keyCode == 46) {
	    		$("#ruleList option:selected").remove();
	    	}
	    });

	    $("#searchTermList").keydown(function(e) {
	    	if(e.keyCode == 46) {
	    		$("#searchTermList option:selected").remove();
	    	}
	    });

	    $("#categoryList").change(function() {
	    	catCode = $(this).val();
			getCategories(catCode);
	    });

	    $("#subCategoryList").change(function() {
	    	catCode = $(this).val();
	    	if (catCode.length==0) {
	    		catCode=$("#categoryList option:selected").val();
	    	}
			getCategories(catCode);
	    });

	    $("#classList").change(function() {
	    	catCode = $(this).val();
	    	if (catCode.length==0) {
	    		catCode=$("#subCategoryList option:selected").val();
	    	}
			getCategories(catCode);
	    });

//	    $(".tabbernav").click(function() {
//	    	alert("click tab");
//	    });
	    
	    $("#addRule").click(function() {
	    	if ($("#url").val() != "http://") {
	    		$("#url").val("http://");
	    		redirectFlag = false;
	    	}
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
	    	$("#ruleList").append($("<option>", { value : rule }).text(rule)); 
	    });

	    $("#editRule").click(function() {
	    	rule = $("#ruleList option:selected");
	    	if (rule.length>0) {
	    		setCatManValues(rule.val());
	    		rule.remove();
	    	} 
	    });

	    $("#searchTerm").focus(function(){
	    	var defaultText = $(this).val();
	    	if(defaultText === "Add Search Term") {
	    		$(this).val('');
	    	}
	    });
	    
	    $("#url").focus(function(){
	    	$("#ruleList option").remove();
	    });
	    
	    $("#url").keydown(function(e) {
	    	redirectFlag = true;
	    });

	    
	    $("#addSearchTerm").click(function() {
	    	searchTerm = $("#searchTerm").val();
	    	if (searchTerm.length>0 && searchTerm != "Add Search Term") {
//	    		if ($('#searchTermList option[value=' + searchTerm + ']').length < 1) {
			    	$("#searchTermList")
			          .append($("<option>", { value : searchTerm })
			          .text(searchTerm)); 
			    	$("#searchTerm").val("Add Search Term");
//	    		} else {
//	    			alert("Search term already exists!");
//	    		}
	    	}
	    });

	    $("#editSearchTerm").click(function() {
	    	searchTerm = $("#searchTermList option:selected");
	    	if (searchTerm.length>0) {
		    	$("#searchTerm").val(searchTerm.val());
		    	searchTerm.remove();
	    	}
	    });
	    
	    $("#add").click(function() {
	    	ruleName = $("#addRuleName").val().trim();
	    	if (ruleName == "") {
	    		alert("Rule Name cannot be blank!");
	    	} else if ($("#ruleId").val() == "0") {
	    		alert("You are currently in add mode!");
	    	} else {
	    		clearValues();
	    		$("#ruleId").val("0");
	    		$("#headerRuleName").text(ruleName);
	    		$("#ruleName").val(ruleName);
	    		$("#delete").text("Cancel");
	    	}
	    });

	    $("#delete").click(function() {
	    	if ($("#delete").text() == "Cancel") {
	    		var res=confirm("Cancel changes?");
	    		if (res==true) {
		    		 $("#delete").text("Delete");
		    		 clearValues();
		    		 ruleId = "";
		    		 initPage();
	    		}
	    	} else {
	    		var res=confirm("Delete rule " + $("#ruleName").val() + "?");
	    		if (res==true) {
	    			ruleId = $("#ruleId").val();
	    			ruleName = $("#ruleName").val();
	    			deleteRule(ruleId, ruleName);
	    		}
	    	}
	    });

	    $("#save").click(function() {
	    	ruleId = $("#ruleId").val();
	    	ruleName = $("#ruleName").val();
    		searchTerm = "";
    		ruleCondition = "";
    		$("#searchTermList option").each(function() {
    	    	searchTerm += $(this).val() + "||";
    	    });
    		if (redirectFlag) {
    			ruleCondition = $("#url").val();
    		} else {
        		$("#ruleList option").each(function() {
        			ruleCondition += $(this).val() + "||";
        	    });
    		}
    		if (searchTerm.length == 0) {
    			alert("Please add at least one Search Term.");
    		} else if (ruleCondition.length == 0) {
    			alert("Please add at least one Category/Manufacturer or Redirect to Page rule.");
    		} else if (redirectFlag && ruleCondition.indexOf("http://", 0) == -1) {
    			alert("URL should start with http://");
    		} else {
    			if (!redirectFlag) {
            		ruleCondition = ruleCondition.substring(0, ruleCondition.lastIndexOf("||"));
    			}
        		searchTerm = searchTerm.substring(0, searchTerm.lastIndexOf("||"));
	    		
    	    	if (ruleId == 0) {
    	    		addRule(ruleName, searchTerm, ruleCondition, storeId, active, priority);
    	    		$("#delete").text("Delete");
    	    	} else {
    	    		updateRule(ruleId, ruleName, searchTerm, ruleCondition, storeId, active, priority);
    	    	}
    		}
	    });

		initPage = function() {
			getCategories(catCode);
			getRedirectRuleList(searchTerm,ruleId, storeId, startRow, endRow);
		};
		
		initPage();
	});	
})(jQuery);	