(function($){

	$(document).ready(function(){
		var tabSelected = "";
		var entityName = "";
		
		var getSelectedItems = function(){
			var selectedItems = [];
			$(tabSelected).find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").each(function(index, value){
				selectedItems[$(this).attr("id")]=$(this).attr("name");
			});
			return selectedItems;
		};
		
		var getSelectedRefId = function(){
			var selectedRefIds = [];
			var selectedItems = getSelectedItems();
			for (var i in selectedItems){
				selectedRefIds.push(i); 
			}
			return selectedRefIds; 
		};

		var getSelectedStatusId = function(){
			var selectedStatusId = [];
			var selectedItems = getSelectedItems();
			for (var i in selectedItems){
				selectedStatusId.push(selectedItems[i]); 
			}
			return selectedStatusId; 
		};
		
		var checkSelectAllHandler = function(){
			$(tabSelected).find("th#selectAll > input[type='checkbox']").on({
				click: function(evt){
					var selectAll = $(this).is(":checked");
					$(tabSelected).find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").attr("checked", selectAll);
					selectAll? $(tabSelected).find("#actionBtn").show() : $(tabSelected).find("#actionBtn").hide();
				}
			});
		};

		var checkSelectHandler = function(){
			$(tabSelected).find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").on({
				click: function(evt){
					var selected = $(tabSelected).find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").length;

					if (selected>0){
						$(tabSelected).find("#actionBtn").show();
					}
					else{
						$(tabSelected).find("#actionBtn").hide();
						$(tabSelected).find("th#selectAll > input[type='checkbox']").attr("checked", false); 
					}
				}
			});
		};

		var publishHandler = function(){
			$(tabSelected).find("a#publishBtn, a#publishBtn").on({
				click: function(evt){
					var comment = $.trim($(tabSelected).find("#approvalComment").val());
					
					if ($.isNotBlank(comment)){

						switch($(evt.currentTarget).attr("id")){
						case "publishBtn": 
							DeploymentServiceJS.publishRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
								callback: function(data){
									getForProductionList();
								},
								preHook:function(){ 
									prepareTabContent(); 
								},
								postHook:function(){ 
									cleanUpTabContent(); 
								}	
							});break;

						case "unpublishBtn": 
							DeploymentServiceJS.unpublishRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
								callback: function(data){
									getForProductionList();
								},
								preHook:function(){ 
									prepareTabContent(); 
								},
								postHook:function(){ 
									cleanUpTabContent(); 
								}	
							});break;
						}	
					}else{
						alert("Please add comment.");
					}
					
				}
			});
		};
		
		var getForProductionList = function(){
			DeploymentServiceJS.getDeployedRules(entityName, {
				callback:function(data){
					var list = data.list;
					
					if (data.totalSize>0){
						var HTML = $("div#tabContentTemplate").html();
						$(tabSelected).html(HTML);
					
						// Populate table row
						for(var i=0; i<data.totalSize ; i++){
							$table = $(tabSelected).find("table#rule");
							$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"])).show();
							
							$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
							$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);
							$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);
							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
							$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
							$tr.find("td#productionStatus").html(list[i]["publishedStatus"]);
							$tr.find("td#productionDate").html('<joda:parseDateTime var="parsed" pattern="yy/M/d" value="' + list[i]["lastPublishedDate"] + '" /><joda:format value="${parsed}" style="L-" />');
							$tr.appendTo($table);
						}
						
						// Alternate row style
						$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");
						
						checkSelectHandler();
						checkSelectAllHandler();
						publishHandler();
						
					}
				},
				preHook:function(){ 
					prepareTabContent(); 
					},
				postHook:function(){ 
					cleanUpTabContent(); 
				}			
			});
		};
		
		var prepareTabContent = function(){
			$(tabSelected).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>');
		};
		
		var cleanUpTabContent = function(){
			$(tabSelected).find('div.circlePreloader').remove();
		};
		
		var switchTab = $("ul.ui-tabs-nav > li > a").on({
			click: function(evt){
				tabSelected = $(this).attr("href");
				tabSelectedText = $(this).find("span").html();
				entityName = tabSelected.substring(1, tabSelected.length-3);
				getForProductionList();
			}
		});
		
		var init = function(){
			tabSelected = $("li.ui-tabs-selected > a").attr("href");
			entityName = tabSelected.substring(1, tabSelected.length-3);
			getForProductionList();
		};

		$("ul.ui-tabs-nav > li > a").on({
			click: function(evt){
				tabSelected = $(this).attr("href");
				entityName = tabSelected.substring(1, tabSelected.length-3);
				getForProductionList();
			}
		});
		
		init();
	});
})(jQuery);	