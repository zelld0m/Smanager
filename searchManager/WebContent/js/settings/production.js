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
				}
			});
		};

		var checkSelectHandler = function(){
			$(tabSelected).find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").on({
				click: function(evt){
					var selected = $(tabSelected).find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").length;
					if (selected==0){
						$(tabSelected).find("th#selectAll > input[type='checkbox']").attr("checked", false); 
					}
				}
			});
		};
		
		var postMsg = function(data,pub){
			var list = data.list;
			var msg_ = pub?'published:':'unpublished:';

			if (data.totalSize>0){			
				var okmsg = 'Following rules were successfully ' + msg_;	
				var flmsg = 'Following rules were unsuccessfully ' + msg_;
				var okcnt = 0;
				var flcnt = 0;
				
				for(var i=0; i<data.totalSize; i++){	
					var rName = $("tr#ruleItem" + $.formatAsId(list[i].ruleId) + " > td#ruleRefId > p#ruleName").html();
					if(list[i].published == '1'){
						okcnt++;
						okmsg += '\n-' + rName;	
					}
					else{
						flcnt++;
						flmsg += '\n-' + rName;
					}
				}
			
				var cmpltmsg = "";
				if (okcnt>0){
					cmpltmsg += okmsg;
				}
				if (flcnt>0){
					cmpltmsg += (okcnt>0? "\n\n":"");
					cmpltmsg += flmsg;
				}
				
				alert(cmpltmsg);	
			}else{		
				alert("No rules were " + msg_);
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
									postMsg(data,true);	
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
									postMsg(data,false);	
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
		
		var getForProductionList = function(filterBy){
			
			DeploymentServiceJS.getDeployedRules(entityName, filterBy, {
				callback:function(data){
					var list = data.list;
					
						var HTML = $("div#tabContentTemplate").html();
						$(tabSelected).html(HTML);
						
						if (data.totalSize>0){
							
							$(tabSelected).find("div#ruleCount").html(data.totalSize == 1 ? "1 Rule" : data.totalSize + " Rules");
							// Populate table row
							for(var i=0; i<data.totalSize ; i++){
								$table = $(tabSelected).find("table#rule");
								$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"])).show();

								var lastPublishedDate = $.isNotBlank(list[i]["lastPublishedDate"])? list[i]["lastPublishedDate"].toUTCString(): "";
								var showId = list[i]["ruleRefId"] !== list[i]["description"];

								$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
								$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);
								
								if($.isBlank(filterBy))
									$tr.find("td#select").html(i+1);
								
								if(showId)
									$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);
								$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);

								$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
								if($.isNotBlank(list[i]["approvalStatus"])) 
									$tr.find("td#requestType").html(list[i]["updateStatus"]);

								$tr.find("td#production > p#productionStatus").html(list[i]["publishedStatus"]);
								$tr.find("td#production > p#productionDate").html(lastPublishedDate);
								$tr.appendTo($table);
							}

							// Alternate row style
							$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");

							checkSelectHandler();
							checkSelectAllHandler();
							publishHandler();
							$(tabSelected).find('div#actionBtn').show();
							
							if (data.totalSize==1) $(tabSelected).find('th#selectAll > input[type="checkbox"]').remove();
							
						}else{
							$(tabSelected).find("table#rule").append('<tr><td class="txtAC" colspan="5">No matching records found</td></tr>');
							$(tabSelected).find('div#actionBtn').hide();
							$(tabSelected).find('th#selectAll > input[type="checkbox"]').hide();
						}
						
						// What button to display
						switch(filterBy){
							case "approved" : 
								$(tabSelected).find('a#unpublishBtn').hide();
								break;
							case "published" : 
							case "delete" : 
								$(tabSelected).find('a#publishBtn').hide(); break;
							case undefined:
							default:
								$(tabSelected).find('div#actionBtn').hide();
								$(tabSelected).find('th#selectAll > input[type="checkbox"]').remove();
						}
				},
				preHook:function(){ prepareTabContent(); },
				postHook:function(){ 
					
					cleanUpTabContent();
					
					$(tabSelected).find("select#ruleFilter").val(filterBy).on({
						change: function(evt){
							getForProductionList($(this).val());
						}
					});
					
				}			
			});
		};
		
		var prepareTabContent = function(){
			if (!$("div.circlePreloader").is(":visible")) $('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>').prependTo($(tabSelected));
			$(tabSelected).find('table.tblItems').hide();
			$(tabSelected).find('div.filter').hide();
			$(tabSelected).find('div#actionBtn').hide();
		};
		
		var cleanUpTabContent = function(){
			$(tabSelected).find('div.circlePreloader').remove();
		};
		
		$("ul.ui-tabs-nav > li > a").on({
			click: function(evt){
				tabSelected = $(this).attr("href");
				entityName = tabSelected.substring(1, tabSelected.length-3);
				getForProductionList();
			}
		});

		var init = function(){
			tabSelected = $("li.ui-tabs-selected > a").attr("href");
			entityName = tabSelected.substring(1, tabSelected.length-3);
			getForProductionList();
		};
		
		init();
	});
})(jQuery);	
