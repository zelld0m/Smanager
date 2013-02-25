(function($){
//Pending optimization and class style
	$(document).ready(function(){
		var entityName = "";
		var tabSelected = "";
		var tabSelectedText = "";
		var refresh = false;
		var memberIdToItem = new Array();
		var memberIds = new Array();
		
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

					if (selected>0){
						$(tabSelected).find("#actionBtn").show();
					}
					else{
						//$(tabSelected).find("#actionBtn").hide();
						$(tabSelected).find("th#selectAll > input[type='checkbox']").attr("checked", false); 
					}
				}
			});
		};

		var postMsg = function(data,pub){
			var msg_ = pub ? 'approved:' : 'rejected:';

			var okmsg = 'Following rules were successfully ' + msg_;	

			for(var i=0; i<data.length; i++){	
				okmsg += '\n-'+ $("tr#ruleItem" + $.formatAsId(data[i]) + " > td#ruleRefId > p#ruleName").text();	
			}

			jAlert(okmsg,"Approval");
		};

		function checkIfDeleted() {
			var refIds = getSelectedRefId();
			for(var i=0; i<refIds.length; i++){	
				status = $("tr#ruleItem" + $.formatAsId(refIds[i]) + " > td#type").html();
				if ('DELETE' == status) {
					return true;
				}
			}
			return false;
		};

		var approvalHandler = function(){
			$(tabSelected).find("a#approveBtn, a#rejectBtn").on({
				click: function(evt){
					var comment = $.trim($(tabSelected).find("#approvalComment").val());

					if (getSelectedRefId().length==0){
						jAlert("Please select rule","Approval");
					}else if ($.isBlank(comment)){
						jAlert("Please add comment.","Approval");
					}else if(!isXSSSafe(comment)){
						jAlert("Invalid comment. HTML/XSS is not allowed.","Approval");
					}else{
						var a = [];
						var arrSelectedKeys = Object.keys(getSelectedItems());
						
						$.each(arrSelectedKeys, function(k){ 
							a.push($("#ruleItem" + $.formatAsId(arrSelectedKeys[k])).find("#ruleName").text());
						});
					
						switch($(evt.currentTarget).attr("id")){
						case "approveBtn":
							var confirmMsg = "Continue approval of the following rules:\n" + a.join('\n');
							jConfirm(confirmMsg, "Confirm Approval", function(status){
								if(status){
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
									});
								}
							});
							break;
						case "rejectBtn": 
							if (checkIfDeleted()) {
								jAlert("Deleted rules cannot be rejected!","Approval");
								return;
							}
							
							var confirmMsg = "Continue reject of the following rules:\n" + a.join('\n');
							jConfirm(confirmMsg, "Confirm Reject", function(status){
								if(status){
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
									});
								}
							});
							
							break;
						}	
					}

				}
			});
		};

		var getApprovalList = function(){
			DeploymentServiceJS.getApprovalList(entityName, false, {
				callback:function(data){
					var list = data.list;

					var HTML = $("div#tabContentTemplate").html();
					$(tabSelected).html(HTML);

					if (data.totalSize>0){

						// Populate table row
						for(var i=0; i<data.totalSize ; i++){
							$table = $(tabSelected).find("table#rule");
							$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"])).show();
							var requestedDate = $.isNotBlank(list[i]["lastModifiedDate"])? list[i]["lastModifiedDate"].toUTCString(): "";
							var showId = list[i]["ruleRefId"].toLowerCase() !== list[i]["description"].toLowerCase();

							$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
							$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);

							//TODO: Get delete details from file
							if (list[i]["updateStatus"]!=="DELETE"){
								$tr.find("td#ruleOption > img.previewIcon").attr("id", list[i]["ruleRefId"]).on({click:previewRow},{ruleStatus:list[i]});
							}else{
								$tr.find("td#ruleOption > img.previewIcon").hide();
							}

							//if(showId) 
							//	$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);

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

						if (data.totalSize==1) $(tabSelected).find('th#selectAll > input[type="checkbox"]').remove();

					}else{
						$(tabSelected).find("table#rule").append('<tr><td class="txtAC" colspan="5">No pending rules found</td></tr>');
						$(tabSelected).find('th#selectAll > input[type="checkbox"]').remove();
						$(tabSelected).find('div#actionBtn').hide();
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
 
		

		var getItemType = function(item){
			var $condition = item.condition;
			var type = "unknown";

			if($.isBlank($condition)){
				return type;
			}

			if (!$condition["CNetFilter"] && !$condition["IMSFilter"]){
				type="facet";
			}else if($condition["CNetFilter"]){
				type="cnet";
			}else if($condition["IMSFilter"]){
				type="ims";
			}

			return type;
		};
		
		var setImage = function(tr, item){
			var imagePath = item["imagePath"];
			switch(getItemType(item)){
				case "ims" : imagePath = GLOBAL_contextPath + '/images/ims_img.jpg'; break;
				case "cnet" : imagePath = GLOBAL_contextPath + '/images/productSiteTaxonomy_img.jpg'; break;
				case "facet" : imagePath = GLOBAL_contextPath + '/images/facet_img.jpg'; break;
				default: if ($.isBlank(imagePath)) imagePath = GLOBAL_contextPath + "/images/no-image60x60.jpg"; break;
			}

			setTimeout(function(){	
				tr.find("td#itemImage > img").attr("src",imagePath).off().on({
					error:function(){ 
						$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image60x60.jpg"); 
					}
				});
			},10);
		};

		var prepareForceAddStatus = function(){
			$('div#forceAdd').show();
		};

		var updateForceAddStatus = function(data){
			for(var mapKey in data){
				var $tr = $('tr#item' + $.formatAsId(mapKey));
				var $item = memberIdToItem[mapKey];

				// Force Add Color Coding
				if(data[mapKey] && !$item["forceAdd"]){

				}else if(data[mapKey] && $item["forceAdd"]){
					$tr.addClass("forceAddBorderErrorClass");
				}else if(!data[mapKey] && $item["forceAdd"]){
					$tr.addClass("forceAddClass");
				}else if(!data[mapKey] && !$item["forceAdd"]){
					$tr.addClass("forceAddErrorClass");
				}
			}

			$('div#forceAdd').hide();
		};

		var populateItemTable = function(ruleType, content, ruleStatus, data){
			var $content = content;
			var list = data.list;

			var $table = $content.find("table#item");

			$table.find("tr:not(#itemPattern)").remove();

			if (data.totalSize==0){
				$tr = $content.find("tr#itemPattern").clone().attr("id","item0").show();
				$tr.find("td:not(#itemPosition)").remove();
				$tr.find("td#itemPosition").attr("colspan", "6").html("No item specified for this rule");
				$tr.appendTo($table);
			}else{
				for (var i = 0; i < data.totalSize; i++) {
					memberIdToItem[list[i]["memberId"]] = list[i];
					memberIds.push(list[i]["memberId"]);
					
					var $tr = $content.find("tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["memberId"])).show();	
					$tr.find("td#itemPosition").html(ruleType.toLowerCase()==="elevate"?  list[i]["location"] : parseInt(i) + 1);

					var PART_NUMBER = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "PART_NUMBER";
					var FACET = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "FACET";

					if(FACET){
						setImage($tr,list[i]);
						$tr.find("td#itemMan").text(list[i].condition["readableString"])
						.prop("colspan",3)
						.removeClass("txtAC")
						.addClass("txtAL")
						.attr("width", "363px");
						$tr.find("#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 

						if ($.isBlank(list[i]["isExpired"])){
							$tr.find("#itemValidityDaysExpired").remove();
						}

						$tr.find("td#itemDPNo,td#itemName").remove();
					}
					else if(PART_NUMBER){
						if($.isNotBlank(list[i]["dpNo"])){
							setImage($tr,list[i]);
							$tr.find("td#itemDPNo").html(list[i]["dpNo"]);
							$tr.find("td#itemMan").html(list[i]["manufacturer"]);
							$tr.find("td#itemName").html(list[i]["name"]);
						}else{
							$tr.find("td#itemImage").html("Product EDP:" + list[i]["edp"] + " is no longer available in the search server you are connected")
							.prop("colspan",4)
							.removeClass("txtAC")
							.addClass("txtAL")
							.attr("width", "369px");
							$tr.find("td#itemDPNo,td#itemMan,td#itemName").remove();
						}

						$tr.find("#itemValidity").html(list[i]["formattedExpiryDate"] + "<br/>" +  list[i]["validityText"]); 
						if ($.isBlank(list[i]["isExpired"])){
							$tr.find("#itemValidityDaysExpired").remove();
						}
					}

					$tr.appendTo($table);
				};
				
				if (tabSelectedText === "Elevate" && memberIds.length>0){
					ElevateServiceJS.isRequireForceAdd(ruleStatus["ruleRefId"], memberIds, {
						callback:function(data){
							updateForceAddStatus(data);
						},
						preHook: function(){
							prepareForceAddStatus();
						}
					});
				} 

			}

			// Alternate row style
			$content.find("tr#itemPattern").hide();
			$content.find("tr:not(#itemPattern):even").addClass("alt");

		};

		var populatePreview = function(api, $content, ruleStatus){

			var populateKeywordInRule = function(data){
				var list = data.list;
				var $table = $content.find("div.ruleKeyword table#item");
				$table.find("tr:not(#itemPattern)").remove();

				if (data.totalSize==0){
					$tr = $content.find("div.ruleKeyword tr#itemPattern").clone().attr("id","item0").show();
					$tr.find("td#fieldName").html("No keywords associated to this rule").attr("colspan","2");
					$tr.find("td#fieldValue").remove();
					$tr.appendTo($table);
				}else{
					for(var i=0; i< data.totalSize; i++){
						$tr = $content.find("div.ruleKeyword tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["keyword"])).show();
						$tr.find("td#fieldName").html(parseInt(i)+1);
						$tr.find("td#fieldValue").html(list[i]["keyword"]);
						$tr.appendTo($table);
					}	
				}

				$table.find("tr:even").addClass("alt");
			};

			switch(tabSelectedText){
			case "Elevate": 
				$content.html($("#previewTemplate1").html());
				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				ElevateServiceJS.getProducts(null, ruleStatus["ruleRefId"], 0, 0,{
					callback: function(data){
						populateItemTable("Elevate", $content, ruleStatus, data);
					}
				});
				break;
			case "Exclude": 
				$content.html($("#previewTemplate1").html());
				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				ExcludeServiceJS.getProducts(null, ruleStatus["ruleRefId"] , 0, 0,{
					callback: function(data){
						populateItemTable("Exclude", $content, ruleStatus, data);
					}
				});
				break;
			case "Demote": 
				$content.html($("#previewTemplate1").html());
				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				DemoteServiceJS.getProducts(null, ruleStatus["ruleRefId"], 0, 0,{
					callback: function(data){
						populateItemTable("Demote", $content, ruleStatus, data);
					}
				});
				break;
			case "Facet Sort":
				$content.html($("#facetSortTemplate").html());
				var $table = $content.find("table#item");
				var $ruleInfo = $content.find("div#ruleInfo");
				
				FacetSortServiceJS.getRuleById(ruleStatus["ruleRefId"], {
					callback: function(data){
						$ruleInfo.find("#ruleName").text(data.name);
						$ruleInfo.find("#ruleType").text(data.ruleType.toLowerCase());
						
						for(var facetGroup in data.items){
							var facetName = facetGroup;
							var facetValue = data.items[facetGroup];
							var highlightedItems = "";
							var $tr = $table.find("tr#itemPattern").clone();
							$tr.prop({id: $.formatAsId(facetName)});
							$tr.find("#itemName").text(facetName);
							
							if($.isArray(facetValue)){
								for(var i=0; i < facetValue.length; i++){
									highlightedItems += (i+1) + ' - ' + facetValue[i] + '<br/>';
								}
							}
							$tr.find("#itemHighlightedItem").html(highlightedItems);

							var sortTypeDisplay = "";
							var sortType = data.groupSortType[facetGroup] == null ? data.sortType : data.groupSortType[facetGroup];
							
							switch(sortType){
								case "ASC_ALPHABETICALLY": sortTypeDisplay = "A-Z"; break;
								case "DESC_ALPHABETICALLY": sortTypeDisplay = "Z-A"; break;
								case "ASC_COUNT": sortTypeDisplay = "Count Asc"; break;
								case "DESC_COUNT": sortTypeDisplay = "Count Desc"; break;
							}
							
							$tr.find("#itemSortType").text(sortTypeDisplay);
							$tr.show();
							$table.append($tr);
						};						
					},
					postHook:function(){
						$table.find("tr#preloader").hide();
					}
				});
				
				break;
			case "Query Cleaning": 
				$content.html($("#queryCleaningTemplate").html());
				$content.find(".infoTabs").tabs({});
				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				$content.find("div.ruleFilter table#itemHeader th#fieldNameHeader").html("#");
				$content.find("div.ruleFilter table#itemHeader th#fieldValueHeader").html("Rule Filter");
				$content.find("div.ruleChange > #noChangeKeyword, div.ruleChange > #hasChangeKeyword").hide();

				RedirectServiceJS.getRule(ruleStatus["ruleRefId"], {
					callback: function(data){

						var $table = $content.find("div.ruleFilter table#item");
						$table.find("tr:not(#itemPattern)").remove();

						if(data.conditions.length==0){
							$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html("No filters specified for this rule").attr("colspan","2");
							$tr.find("td#fieldValue").remove();
							$tr.appendTo($table);

						}else{
							for(var field in data.conditions){
								$tr = $content.find("div.ruleFilter tr#itemPattern").clone().attr("id","item" + $.formatAsId(field)).show();
								$tr.find("td#fieldName").html(parseInt(field)+1);
								$tr.find("td#fieldValue").html(data.conditions[field]);
								$tr.appendTo($table);
							}	
						}

						$table.find("tr:even").addClass("alt");
						$content.find("#description").html(data["description"]);
						switch (data["redirectTypeId"]) {
						case "1":
							$content.find("#redirectType").html("Filter");
							break;
						case "2":
							$content.find("#redirectType").html("Replace Keyword");
							break;
						case "3":
							$content.find("#redirectType").html("Direct Hit");
							break;
						default:
							$content.find("#redirectType").html("");
						break;									
						}

						if ($.isNotBlank(data["changeKeyword"])){
							$content.find("div#ruleChange > div#hasChangeKeyword").show();
							$content.find("div#ruleChange > div#hasChangeKeyword > div > span#changeKeyword").html(data["changeKeyword"]);
						}else{
							$content.find("div#ruleChange > #noChangeKeyword").show();
						}

						var includeKeywordText = "Include keyword in search: <b>NO</b>";
						if($.isNotBlank(data["includeKeyword"])){
							includeKeywordText = "Include keyword in search: ";
							if(data["includeKeyword"]){
								includeKeywordText += "<b>YES</b>";
							}
							else{
								includeKeywordText += "<b>NO</b>";
							}
						}
						$content.find("div.ruleFilter div#includeKeywordInSearchText").show();
						$content.find("div.ruleFilter div#includeKeywordInSearchText").html(includeKeywordText);
					}
				});

				RedirectServiceJS.getAllKeywordInRule(ruleStatus["ruleRefId"], "", 0, 0, {
					callback: function(data){
						populateKeywordInRule(data);
					}
				});


				break;
			case "Ranking Rule": 
				$content.html($("#previewTemplate2").html());
				$content.find(".infoTabs").tabs({

				});

				$content.find("#ruleInfo").text($.trim(ruleStatus["description"]));
				$content.find("#requestType").text(ruleStatus["updateStatus"]);

				RelevancyServiceJS.getRule(ruleStatus["ruleRefId"], {
					callback: function(data){
						$content.find("#startDate").html(data["formattedStartDate"]);
						$content.find("#endDate").html(data["formattedEndDate"]);
						$content.find("#description").html(data["description"]);

						var $table = $content.find("div.ruleField table#item");
						$table.find("tr:not(#itemPattern)").remove();

						for(var field in data.parameters){
							$tr = $content.find("div.ruleField tr#itemPattern").clone().attr("id","item0").show();
							$tr.find("td#fieldName").html(field);
							$tr.find("td#fieldValue").html(data.parameters[field]);
							$tr.appendTo($table);
						}	

						$table.find("tr:even").addClass("alt");
					}
				});

				RelevancyServiceJS.getAllKeywordInRule(ruleStatus["ruleRefId"], "", 0, 0, {
					callback: function(data){
						populateKeywordInRule(data);
					}
				});

				break;
			};

			$content.find("a#approveBtn, a#rejectBtn").off().on({
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
							if (checkIfDeleted()) {
								jAlert("Deleted rules cannot be rejected!","Approval");
								return;
							}
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
						jAlert("Please add comment.","Approval");
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
						text: (tabSelectedText==="Ranking Rule"? "Ranking Rule Preview" :  tabSelectedText + " Rule Preview"), button:true
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
