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
		
		var getApprovalList = function(){
			DeploymentServiceJS.getApprovalList(entityName, false, {
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
							$tr.find("td#ruleRefId").html(list[i]["ruleRefId"]);
							$tr.find("td#updateStatus").html(list[i]["updateStatus"]);
							$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
							$tr.appendTo($table);
						}
						
						// Alternate row style
						$(tabSelected).find("tr#ruleItemPattern").hide();
						$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");
						
						// Multiple select listener
						$(tabSelected).find("th#selectAll > input[type='checkbox']").on({
							click: function(evt){
								var selectAll = $(this).is(":checked");
								$(tabSelected).find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").attr("checked", selectAll);
								selectAll? $(tabSelected).find("#actionBtn").show() : $(tabSelected).find("#actionBtn").hide();
							}
						});
						
						// Single select listener
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
						
						
						$(tabSelected).find("#approveBtn").on({
							click: function(evt){
								DeploymentServiceJS.approveRule(entityName, getSelectedItems(), {
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
						
						$(tabSelected).find("#rejectBtn").on({
							click: function(evt){
								DeploymentServiceJS.unapproveRule(entityName, getSelectedItems(), {
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
			getApprovalList();
		};

		$("ul.ui-tabs-nav > li > a").on({
			click: function(evt){
				tabSelected = $(this).attr("href");
				entityName = tabSelected.substring(1, tabSelected.length-3);
				getApprovalList();
			}
		});
		
		init();
	});
})(jQuery);	