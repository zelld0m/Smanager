(function($){

	$(document).ready(function(){
		var tabSelected = "";
		var entityName = "";
		
		var getSelectedItems = function(){
			var selectedIds = new Array();
			$(tabSelected).find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").each(function(index, value){
				selectedIds.push($(this).attr("id"));
			});
			return selectedIds;
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
			$(tabSelected).find("#approveBtn").on({
				click: function(evt){
					DeploymentServiceJS.publishRule(entityName, getSelectedItems(), {
						callback:function(data){
							getApprovalList();
						},
						preHook:function(){ 
							prepareTabContent(); 
						},
						postHook:function(){ 
							cleanUpTabContent(); 
						}	
					});
				}
			});
		};

		var unpublishHandler = function(){
			$(tabSelected).find("#rejectBtn").on({
				click: function(evt){
					DeploymentServiceJS.unpublishRule(entityName, getSelectedItems(), {
						callback:function(data){
							getApprovalList();
						},
						preHook:function(){ 
							prepareTabContent(); 
						},
						postHook:function(){ 
							cleanUpTabContent(); 
						}	
					});
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
							$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"]));
							
							$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
							$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);
							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
							$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
							$tr.find("td#productionStatus").html(list[i]["publishedStatus"]);
							$tr.find("td#productionDate").html('<joda:parseDateTime var="parsed" pattern="yy/M/d" value="' + list[i]["lastPublishedDate"] + '" /><joda:format value="${parsed}" style="L-" />');
							$tr.appendTo($table);
						}
						
						// Alternate row style
						$(tabSelected).find("tr#ruleItemPattern").hide();
						$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");
						
						checkSelectHandler();
						checkSelectAllHandler();
						publishHandler();
						unpublishHandler();
						
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