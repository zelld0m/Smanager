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
					var rName = $("tr#ruleItem" + $.formatAsId(list[i].ruleId) + " > td#ruleRefId > p#ruleName").text();
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
				
				jAlert(cmpltmsg,"Push to Prod");	
			}else{		
				jAlert("No rules were " + msg_,"Push to Prod");
			}
		};
		
		var publishHandler = function(){
			$(tabSelected).find("a#publishBtn, a#unpublishBtn").on({
				click: function(evt){
					var comment = $.defaultIfBlank($.trim($(tabSelected).find("#approvalComment").val()),"");
					
					if(getSelectedRefId().length==0){
						jAlert("Please select rule","Push to Prod");
					}else if (!validateComment("Push to Prod", comment, 1)){
						//error alert in validateComment
					}else{
						var selRuleFltr = $(tabSelected).find("#ruleFilter").val();
						var a = [];
						var arrSelectedKeys = Object.keys(getSelectedItems());
						
						$.each(arrSelectedKeys, function(k){ 
							a.push($(tabSelected).find("#ruleItem" + $.formatAsId(arrSelectedKeys[k])).find("#ruleName").text());
						});

						comment = comment.replace(/\n\r?/g, '<br/>');
						switch($(evt.currentTarget).attr("id")){
						case "publishBtn": 
							var confirmMsg = "Continue publishing of the following rules:\n" + a.join('\n');

							jConfirm(confirmMsg, "Confirm Publish", function(status){
								if(status){
									var exception = false;
									DeploymentServiceJS.publishRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
										callback: function(data){									
											postMsg(data,true);	
											getForProductionList(selRuleFltr);	
										},
										preHook:function(){ 
											prepareTabContent(); 
										},
										postHook:function(){ 
											if (!exception) {
												cleanUpTabContent()
											}
											else {
												$("div.circlePreloader").hide();
												$(tabSelected).find('table.tblItems').show();
												$(tabSelected).find('div.filter').show();
												$(tabSelected).find('div#actionBtn').show();
											}; 
										},
										exceptionHandler: function(message, exc){ 
											exception = true; 
											jAlert(message, "Publish Rule"); 
										}
									});
								}
							});
							break;
							
						case "unpublishBtn": 
							var confirmMsg = "Continue unpublishing of the following rules:\n" + a.join('\n');
							jConfirm(confirmMsg, "Confirm Unpublish", function(status){
								if(status){
									var exception = false;
									DeploymentServiceJS.unpublishRule(entityName, getSelectedRefId(), comment, getSelectedStatusId(),{
										callback: function(data){
											postMsg(data,false);	
											getForProductionList(selRuleFltr);
										},
										preHook:function(){ 
											prepareTabContent(); 
										},
										postHook:function(){ 
											if (!exception) {
												cleanUpTabContent()
											}
											else {
												$("div.circlePreloader").hide();
												$(tabSelected).find('table.tblItems').show();
												$(tabSelected).find('div.filter').show();
												$(tabSelected).find('div#actionBtn').show();
											}; 
										},
										exceptionHandler: function(message, exc){ 
											exception = true; 
											jAlert(message, "Unpublish Rule"); 
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
		
		var publishHandlerLinguistics = function(){
			$(tabSelected).find("a#publishBtn").on({
				click: function(evt){
					var comment = $.defaultIfBlank($.trim($(tabSelected).find("#approvalComment").val()),"");
					
					if (!validateComment("Push to Prod", comment, 1)){
						//error alert in validateComment
					}else{
						var selRuleFltr = $(tabSelected).find("#ruleFilter").val();
						comment = comment.replace(/\n\r?/g, '<br/>');
						switch($(evt.currentTarget).attr("id")){
						case "publishBtn": 
							jConfirm("Continue publishing of Did You Mean List?", "Confirm Publish", function(status){
								if(status){
									var exception = false;
									DeploymentServiceJS.publishRule(entityName, ["spell_rule"], comment, ["spell_rule"],{
										callback: function(data){	
											jAlert("Did You Mean List was successfully published.", "Publish Rule");
											getForProductionList("approved");	
										},
										preHook:function(){ 
											prepareTabContent(); 
										},
										postHook:function(){ 
											if (!exception) {
												cleanUpTabContent()
											}
											else {
												$(tabSelected).find("div#requestDetails").hide();
												$("div.circlePreloader").hide();
												$(tabSelected).find('table.tblItems').show();
												$(tabSelected).find('div.filter').show();
												$(tabSelected).find('div#actionBtn').show();
											}; 
										},
										exceptionHandler: function(message, exc){ 
											exception = true; 
											jAlert(message, "Publish Rule"); 
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
		
		var getForProductionList = function(filterBy){
			
			DeploymentServiceJS.getDeployedRules(entityName, filterBy, {
				callback:function(data){
					var list = data.list;
						
						var HTML;
						if (entityName === "didYouMean") {
							HTML = $("div#tabContentTemplateLinguistics").html();
						}
						else {
							HTML = $("div#tabContentTemplate").html();
						}
						$(tabSelected).html(HTML);
						
						if (data.totalSize>0){
							
							if (entityName === 'didYouMean'){
								// Populate table row
								// set to number to be displayed + 1, to detect if there is an overflow
								var displaySize = 50;
								SpellRuleServiceJS.getModifiedSpellRules(null, null, null, 1, displaySize + 1, {
									callback: function(response) {
										// Populate table row
										var responseData = response.data;
										var responseList = responseData.spellRule;
										$(tabSelected).find("label#numSuggestions").append(responseData.maxSuggest);
										$(tabSelected).find("label#productionStatus").html(list[0]["publishedStatus"]);
										$(tabSelected).find("label#productionDate").html($.isNotBlank(list[0]["lastPublishedDate"])? list[0]["lastPublishedDate"].toUTCString(): "");
										$table = $(tabSelected).find("table#rule");
										if (responseList.length == 0) {
											$table.append('<tr><td class="txtAC" colspan="3">No new/modified entries found. Click <a href="javascript:void(0);" id="downloadIcon">here</a> to download full list.</td></tr>');
										}
										else {
											if (displaySize > responseList.length) {
												displaySize = responseList.length;
											}
											for(var i=0; i<displaySize ; i++){
												var termHTML = "";
												var suggestionHTML = "";
												$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(responseList[i]["ruleId"])).show();
												responseList[i].ruleKeyword["keyword"].forEach(function (item) {
													termHTML += "<span class=\"term\">" + item + "</span>";
												});
												$tr.find("td#searchTerms").html(termHTML);
												responseList[i].suggestKeyword["suggest"].forEach(function (item) {
													suggestionHTML += "<span class=\"term\">" + item + "</span>";
												});
												$tr.find("td#suggestions").html(suggestionHTML);
												$tr.find("td#type").html(responseList[i]["status"]);
												$tr.appendTo($table);
											}
											$table.append('<tr><td class="txtAC" colspan="3"><div>Click <a href="javascript:void(0);" id="downloadIcon">here</a> to download full list.</div></td></tr>');
										}
										
										$(tabSelected).find("table#rule tr td div a#downloadIcon").download({
											headerText:"Download Did You Mean Rules",
											moduleName: entityName,
											ruleType: entityName,
											solo: $(".internal-tooltip"),
											classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped internal-tooltip',
											requestCallback:function(e2) {
												var params = new Array();
												var url = GLOBAL_contextPath + "/spell/" + GLOBAL_storeId + "/xls";
												var urlParams = "";
												var count = 0;

												params["filename"] = e2.data.filename;
												params["type"] = e2.data.type;
												params["id"] = "spell_rule";
												params["clientTimezone"] = +new Date();

												for(var key in params){
													if (count>0) urlParams +='&';
													urlParams += (key + '=' + encodeURIComponent(params[key]));
													count++;
												};
												document.location.href = url + '?' + urlParams;
											}
										});
										
										publishHandlerLinguistics();
									}
								});
							}
							else {
								$(tabSelected).find("div#ruleCount").html(data.totalSize == 1 ? "1 Rule" : data.totalSize + " Rules");
								// Populate table row
								for(var i=0; i<data.totalSize ; i++){
									$table = $(tabSelected).find("table#rule");
									$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"])).show();
	
									var lastPublishedDate = $.isNotBlank(list[i]["lastPublishedDate"])? list[i]["lastPublishedDate"].toUTCString(): "";
									var showId = list[i]["ruleRefId"].toLowerCase() !== list[i]["description"].toLowerCase();
	
									$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
									$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);
									
									if($.isBlank(filterBy))
										$tr.find("td#select").html(i+1);
									
									//if(showId)
									//	$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);
									$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
	
									$tr.find("td#approvalStatus").html(list[i]["approvalStatus"]);
									if($.isNotBlank(list[i]["approvalStatus"])) 
										$tr.find("td#requestType").html(list[i]["updateStatus"]);
	
									$tr.find("td#production > p#productionStatus").html(list[i]["publishedStatus"]);
									$tr.find("td#production > p#productionDate").html(lastPublishedDate);
									$tr.appendTo($table);
								}
								
								checkSelectHandler();
								checkSelectAllHandler();
								publishHandler();
								
								if (data.totalSize==1) $(tabSelected).find('th#selectAll > input[type="checkbox"]').remove();

							}

							// Alternate row style
							$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");

							$(tabSelected).find('div#actionBtn').show();							
							
						}else{
							$(tabSelected).find("div#requestDetails").hide();
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
			$(tabSelected).find('div#requestDetails').hide();
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
				getForProductionList("approved");
			}
		});

		var init = function(){
			tabSelected = $("li.ui-tabs-selected > a").attr("href");
			entityName = tabSelected.substring(1, tabSelected.length-3);
			getForProductionList("approved");
			
			RuleTransferServiceJS.getAutoExport({
				callback:function(isAutoExport){
					$('#autoExportValue').text(isAutoExport? 'ON':'OFF').parent('#autoExportStatus').show();
				}
			});
			
		};
		
		init();
	});
})(jQuery);	
