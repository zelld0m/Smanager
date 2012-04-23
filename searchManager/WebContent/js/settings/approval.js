/**
 * Author: MPanaligan
 * Reviewer:
 * 
 */
(function($){

	$(document).ready(function(){
		var entityName = "";
		var tabSelected = "";
		var tabSelectedText = "";
		
		var getSelectedItems = function(){
			var selectedIds = new Array();
			$(tabSelected).find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").each(function(index, value){
				selectedIds.push($(this).attr("id"));
			});
			return selectedIds;
		};
		
		var checkSelectAllHandler = $(tabSelected).find("th#selectAll > input[type='checkbox']").on({
			click: function(evt){
				var selectAll = $(this).is(":checked");
				$(tabSelected).find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").attr("checked", selectAll);
				selectAll? $(tabSelected).find("#actionBtn").show() : $(tabSelected).find("#actionBtn").hide();
			}
		});
		
		var checkSelectHandler = $(tabSelected).find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").on({
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
		
		var approveHandler = $(tabSelected).find("#approveBtn").on({
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
		
		var rejectHandler = $(tabSelected).find("#rejectBtn").on({
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
							$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"])).show();
							
							$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
							$tr.find("td#ruleRefId > a").html(list[i]["ruleRefId"]);
							$tr.find("td#updateStatus").html(list[i]["updateStatus"]);
							$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
							$tr.appendTo($table);
						}
						
						// Alternate row style
						$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");
						
						checkSelectHandler;
						checkSelectAllHandler;
						approveHandler;
						rejectHandler;
						previewRow();
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
				getApprovalList();
			}
		});
		
		var populatePreview = function($content, ruleRefId){
			switch(tabSelectedText){
				case "Elevate": 
					$content.html($("#previewTemplate").html());
					ElevateServiceJS.getProducts(null, ruleRefId , 0, 0,{
						callback: function(data){
							var list = data.list;
	
							for (var i = 0; i < data.totalSize; i++) {
								var $table = $content.find("table#item");
								var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["edp"])).show();	
								$tr.find("td#itemPosition").html(list[i]["location"]);
								$tr.find("td#itemImage > img").attr("src",list[i]["imagePath"]);
								$tr.find("td#itemDPNo").html(list[i]["dpNo"]);
								$tr.find("td#itemMan").html(list[i]["manufacturer"]);
								$tr.find("td#itemName").html(list[i]["name"]);
								$tr.find("td#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 
								$tr.appendTo($table);
							};
	
							// Alternate row style
							$content.find("tr#itemPattern").hide();
							$content.find("tr:not(#itemPattern):even").addClass("alt");
	
						},
						preHook: function(){
							
						},
						postHook: function(){
							
						}
					});
					break;
				case "Exclude": 
					$content.html($("#previewTemplate").html());
					ExcludeServiceJS.getProducts(null, ruleRefId , 0, 0,{
						callback: function(data){
							var list = data.list;
	
							for (var i = 0; i < data.totalSize; i++) {
								var $table = $content.find("table#item");
								var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["edp"])).show();	
								$tr.find("td#itemPosition").html(list[i]["location"]);
								$tr.find("td#itemImage > img").attr("src",list[i]["imagePath"]);
								$tr.find("td#itemDPNo").html(list[i]["dpNo"]);
								$tr.find("td#itemMan").html(list[i]["manufacturer"]);
								$tr.find("td#itemName").html(list[i]["name"]);
								$tr.find("td#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 
								$tr.appendTo($table);
							};
	
							// Alternate row style
							$content.find("tr#itemPattern").hide();
							$content.find("tr:not(#itemPattern):even").addClass("alt");
	
						},
						preHook: function(){
							
						},
						postHook: function(){
							
						}
					});
					break;
				case "Query Cleaning": 
	
					break;
				case "Ranking Rule": 
	
					break;
			};
			
			return $content;
		};
		
		var previewRow = function(){
			$(tabSelected).find(".ruleRefId > a").on({
				click: function(evt){
					$(this).qtip({
						content: {
							text: $('<div/>'),
							title: { 
								text: tabSelectedText + " Rule Preview", button:true
							}
						},
						position: {
							  my: 'center',
					          at: 'center',
					          target: $(window)
					        },
						show: {
							modal: true,
							solo: true,
							ready: true
						},
						style: {
							width: 'auto'
						},
						events: {
							show: function(event, api) {
								var $content = $("div", api.elements.content);
								populatePreview($content, $(evt.target).html());
							},
							hide: function(event, api) {
								api.destroy();
							}
						}
					});
				}
			});
		};
		
		var init = function(){
			tabSelected = $("li.ui-tabs-selected > a").attr("href");
			tabSelectedText = $("li.ui-tabs-selected > a").find("span").html();
			entityName = tabSelected.substring(1, tabSelected.length-3);
			getApprovalList();
			switchTab;
		};
		
		init();
	});
})(jQuery);	