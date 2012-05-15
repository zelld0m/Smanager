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

		var postMsg = function(data,pub){
			var msg_ = pub ? 'approved:' : 'rejected:';

			var okmsg = 'Following rules were successfully ' + msg_;	

			for(var i=0; i<data.length; i++){	
				okmsg += '\n-'+ $("tr#ruleItem" + $.formatAsId(data[i]) + " > td#ruleRefId > p#ruleName").html();	
			}

			alert(okmsg);
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
									postMsg(data,true);	
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
									postMsg(data,false);	
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

							if(showId) 
								$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);

							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
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
			if (!$("div.circlePreloader").is(":visible")) $('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>').prependTo($(tabSelected));
			$(tabSelected).find('table.tblItems').hide();
			$(tabSelected).find('div#actionBtn').hide();
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

		var populateItemTable = function(ruleType, content, ruleStatus, data){
			var $content = content;
			var list = data.list;
			$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
			$content.find("#requestType").text(ruleStatus["updateStatus"]);

			var $table = $content.find("table#item");

			if (data.totalSize==0){
				$tr = $content.find("tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td:not(#itemPosition)").remove();
				$tr.find("td#itemPosition").attr("colspan", "6").html("No item specified for this rule");
				$tr.appendTo($table);
			}else{

				for (var i = 0; i < data.totalSize; i++) {
					var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["edp"])).show();	
					$tr.find("td#itemPosition").html(list[i]["location"]);
					$tr.find("td#itemImage > img").attr("src",list[i]["imagePath"]);
					$tr.find("td#itemDPNo").html(list[i]["dpNo"]);
					$tr.find("td#itemMan").html(list[i]["manufacturer"]);
					$tr.find("td#itemName").html(list[i]["name"]);
					$tr.find("td#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 
					$tr.appendTo($table);
				};
			}

			// Alternate row style
			$content.find("tr#itemPattern").hide();
			$content.find("tr:not(#itemPattern):even").addClass("alt");

			$content.find("a#approveBtn, a#rejectBtn").on({
				click:function(evt){
					var ruleStatusId = ruleStatus["ruleStatusId"];
					var comment = $.trim($content.find("#approvalComment").val());

					if ($.isNotBlank(comment)){

						switch($(evt.currentTarget).attr("id")){
						case "approveBtn": 
							DeploymentServiceJS.approveRule(ruleType, $.makeArray(ruleStatus["ruleRefId"]), comment, $.makeArray(ruleStatusId),{
								callback: function(data){
									refresh = true;
								}
							});break;

						case "rejectBtn": 
							DeploymentServiceJS.unapproveRule(ruleType, $.makeArray(ruleStatus["ruleRefId"]), comment, $.makeArray(ruleStatusId), {
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
		};

		var populatePreview = function(api, $content, ruleStatus){
			switch(tabSelectedText){
			case "Elevate": 
				$content.html($("#previewTemplate1").html());

				ElevateServiceJS.getProducts(null, ruleStatus["ruleRefId"], 0, 0,{
					callback: function(data){
						populateItemTable("Elevate", $content, ruleStatus, data);
					},
					preHook: function(){

					}
				});
				break;
			case "Exclude": 
				$content.html($("#previewTemplate1").html());
				ExcludeServiceJS.getProducts(null, ruleStatus["ruleRefId"] , 0, 0,{
					callback: function(data){
						populateItemTable("Exclude",$content, ruleStatus, data);
					},
					preHook: function(){

					},
					postHook: function(){

					}
				});
				break;
			case "Query Cleaning": 
				$content.html($("#previewTemplate2").html());
				$content.find(".infoTabs").tabs({});
				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]) + " [ " + $.trim(ruleStatus["ruleRefId"] + " ]"));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				$content.find('a[href="#ruleField"] >span').html("Rule Condition");

				$content.find("div.ruleField table#itemHeader th#fieldNameHeader").html("#");
				$content.find("div.ruleField table#itemHeader th#fieldValueHeader").html("Rule Condition");

				var $ul = $content.find("div.ruleRanking ul#relevancyInfo");
				$ul.find("li span#startDate").parent("li").remove();
				$ul.find("li span#endDate").parent("li").remove();

				RedirectServiceJS.getRule(ruleStatus["ruleRefId"], {
					callback: function(data){

						var $table = $content.find("div.ruleField table#item");

						if(data.conditions.length==0){
							$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html("No condition specified for this rule").attr("colspan","2");
							$tr.find("td#fieldValue").remove();
							$tr.appendTo($table);

						}else{
							for(var field in data.conditions){
								$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item" + $.formatAsId(field)).show();
								$tr.find("td#fieldName").html(parseInt(field)+1);
								$tr.find("td#fieldValue").html(data.conditions[field]);
								$tr.appendTo($table);
							}	
						}

						$ul.find("li span#description").html(data["description"]);
					},
					preHook: function(){

					}
				});

				RedirectServiceJS.getAllKeywordInRule(ruleStatus["ruleRefId"], "", 0, 0, {
					callback: function(data){
						var list = data.list;

						var $ul = $content.find("div.ruleKeyword ul#keywordInRule");

						if (data.totalSize==0){
							$content.find("#noKeyword").html("No keyword is using this rule");
						}else{
							$content.find("#noKeyword").html("");
							for (var i=0; i< data.totalSize; i++){
								$("<li>").text(list[i]["keyword"]).appendTo($ul);
							}
						}
					},
					preHook: function(){

					}
				});


				break;
			case "Ranking Rule": 
				$content.html($("#previewTemplate2").html());
				$content.find(".infoTabs").tabs({});
				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]) + " [ " + $.trim(ruleStatus["ruleRefId"] + " ]"));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				RelevancyServiceJS.getRule(ruleStatus["ruleRefId"], {
					callback: function(data){

						var $table = $content.find("div.ruleField table#item");

						for(var field in data.parameters){
							$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html(field);
							$tr.find("td#fieldValue").html(data.parameters[field]);
							$tr.appendTo($table);
						}	
						var $ul = $content.find("div.ruleRanking ul#relevancyInfo");

						$ul.find("li span#startDate").html(data["formattedStartDate"]);
						$ul.find("li span#endDate").html(data["formattedEndDate"]);
						$ul.find("li span#description").html(data["description"]);
					},
					preHook: function(){

					}
				});

				RelevancyServiceJS.getAllKeywordInRule(ruleStatus["ruleRefId"], "", 0, 0, {
					callback: function(data){
						var list = data.list;

						var $ul = $content.find("div.ruleKeyword ul#keywordInRule");

						if (data.totalSize==0){
							$content.find("#noKeyword").html("No keyword is using this rule");
						}else{
							$content.find("#noKeyword").html("");
							for (var i=0; i< data.totalSize; i++){
								$("<li>").text(list[i]["keyword"]).appendTo($ul);
							}
						}	
					},
					preHook: function(){

					}
				});

				break;
			};

			$content.find("a#approveBtn, a#rejectBtn").on({
				click: function(evt){
					var comment = $content.find("#approvalComment").val();

					if ($.isNotBlank(comment)){
						switch($(evt.currentTarget).attr("id")){
						case "approveBtn": 
							DeploymentServiceJS.approveRule(tabSelectedText, $.makeArray(ruleStatus["ruleRefId"]) , comment, $.makeArray(ruleStatus["ruleStatusId"]), {
								callback: function(data){
									postMsg(data,true);	
									getApprovalList();
								},
								preHook: function(){
									api.destroy();
								}
							});break;

						case "rejectBtn": 
							DeploymentServiceJS.unapproveRule(tabSelectedText, $.makeArray(ruleStatus["ruleRefId"]) , comment, $.makeArray(ruleStatus["ruleStatusId"]), {
								callback: function(data){
									postMsg(data,false);	
									getApprovalList();
								},
								preHook: function(){
									api.destroy();
								}
							});break;
						}	
					}else{
						alert("Please add comment.");
					}
				}
			});

			return $content;
		};

		var previewRow = function(evt){

			$(this).qtip({
				id: "rule-preview",
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
						populatePreview(api, $content, evt.data.ruleStatus);
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