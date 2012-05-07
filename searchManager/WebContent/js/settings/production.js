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
		
		var postMsg = function(data,pub){
			var list = data.list;
			var msg_ = pub?'published':'unpublished'+'.';

			if (data.totalSize>1){			
				var okmsg = 'Successfully '+msg_;	
				var flmsg = '\n\nFailed.';
				var okcnt = 0;
				var flcnt = 0;
				for(var i=0; i<data.totalSize; i++){					
					if(list[i].published == '1'){
						okcnt++;
						okmsg += '\n-'+list[i].ruleId;	
					}
					else{
						flcnt++;
						flmsg += '\n-'+list[i].ruleId;
					}
				}
	
				if(okcnt < 1){
					okmsg = '';
					flmsg = flmsg.replace('\n\n', '');
				}if(flcnt < 1)
					flmsg = '';
					
				alert(okmsg+flmsg);	
			}else{			
				if(list != null && list[0].published == '1')
					alert(list[0].ruleId+' successfully '+msg_);
				else
					alert(list[0].ruleId+' was not '+msg_);
			}
		};
		
		
		var publishHandler = function(){
			$(tabSelected).find("a#publishBtn, a#unpublishBtn").on({
				click: function(evt){
					var comment = $.trim($(tabSelected).find("#approvalComment").val());
					
					if ($.isNotBlank(comment)){

						switch($(evt.currentTarget).attr("id")){
						case "publishBtn": 
							DeploymentServiceJS.publishRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
								callback: function(data){									
									getForProductionList();	
									postMsg(data,true);	
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
									postMsg(data,false);	
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
							
							var lastPublishedDate = $.isNotBlank(list[i]["lastPublishedDate"])? list[i]["lastPublishedDate"].toUTCString(): "";
							var showId = list[i]["ruleRefId"] !== list[i]["description"];
							
							$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
							$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);
							if(showId)
								$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);
							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
							$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
							$tr.find("td#production > p#productionStatus").html(list[i]["publishedStatus"]);
							$tr.find("td#production > p#productionDate").html(lastPublishedDate);
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
