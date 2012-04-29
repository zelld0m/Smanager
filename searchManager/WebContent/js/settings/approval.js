(function($){

	$(document).ready(function(){
		var entityName = "";
		var tabSelected = "";
		var tabSelectedText = "";
		var refresh = false;

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
		
		var approvalHandler = function(){
			$(tabSelected).find("a#approveBtn, a#rejectBtn").on({
				click: function(evt){
					var comment = $.trim($(tabSelected).find("#approvalComment").val());
					
					if ($.isNotBlank(comment)){

						switch($(evt.currentTarget).attr("id")){
						case "approveBtn": 
							DeploymentServiceJS.approveRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
								callback: function(data){
									getApprovalList();
								},
								preHook:function(){ 
									prepareTabContent(); 
								},
								postHook:function(){ 
									cleanUpTabContent(); 
								}	
							});break;

						case "rejectBtn": 
							DeploymentServiceJS.unapproveRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
								callback: function(data){
									getApprovalList();
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
							var requestedDate = $.isNotBlank(list[i]["lastModifiedDate"])? list[i]["lastModifiedDate"].toUTCString(): "";
							var showId = list[i]["ruleRefId"] !== list[i]["description"];
							
							$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
							$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);
							$tr.find("td#ruleOption > img.previewIcon").attr("id", list[i]["ruleRefId"]).on({click:previewRow},{ruleStatus:list[i]});
							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["ruleRefId"]);
							if(showId) $tr.find("td#ruleRefId > p#ruleId").html(list[i]["description"]);
							$tr.find("td#type").html(list[i]["updateStatus"]);
							$tr.find("td#requested > p#requestedBy").html(list[i]["lastModifiedBy"]);
							$tr.find("td#requested > p#requestedDate").html(requestedDate);
							$tr.appendTo($table);
						}

						// Alternate row style
						$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");

						checkSelectHandler();
						checkSelectAllHandler();
						approvalHandler();
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
		
		var populatePreview = function($content, ruleStatus){
			switch(tabSelectedText){
			case "Elevate": 
				$content.html($("#previewTemplate").html());

				ElevateServiceJS.getProducts(null, ruleStatus["ruleRefId"], 0, 0,{
					callback: function(data){
						var list = data.list;
								
						$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
						$content.find("#requestType").text(ruleStatus["updateStatus"]);
						
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

				$content.find("a#approveBtn, a#rejectBtn").on({
					click:function(evt){
						var ruleStatusId = ruleStatus["ruleStatusId"];
						var comment = $.trim($content.find("#approvalComment").val());

						if ($.isNotBlank(comment)){

							switch($(evt.currentTarget).attr("id")){
							case "approveBtn": 
								DeploymentServiceJS.approveRule("Elevate", $.makeArray(ruleStatus["ruleRefId"]), comment, $.makeArray(ruleStatusId),{
									callback: function(data){
										refresh = true;
									}
								});break;

							case "rejectBtn": 
								DeploymentServiceJS.unapproveRule("Elevate", $.makeArray(ruleStatus["ruleRefId"]), comment, $.makeArray(ruleStatusId), {
									callback: function(data){
										refresh = true;
									}
								});break;
							}	
						}else{
							alert("Please add comment.");
						}
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
				$content.html($("#previewRankingRuleTemplate").html());
				RelevancyServiceJS.getById(ruleRefId, {
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
			};

			return $content;
		};

		var previewRow = function(evt){
			
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
						populatePreview($content, evt.data.ruleStatus);
					},
					hide: function(event, api) {
						if (refresh) getApprovalList();
						api.destroy();
					}
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