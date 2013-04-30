(function($){
//	Pending optimization and class style
	$(document).ready(function(){
		var entityName = "";
		var tabSelected = "";

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
					var comment = $.defaultIfBlank($.trim($(tabSelected).find("#approvalComment").val()),"");

					if (getSelectedRefId().length==0){
						jAlert("Please select rule","Approval");
					}else if (!validateComment("Approval", comment, 1, 300)){
						// error alert in function validateComment
					}else{
						var a = [];
						var arrSelectedKeys = Object.keys(getSelectedItems());

						
						$.each(arrSelectedKeys, function(k){ 
							a.push($(tabSelected).find("#ruleItem" + $.formatAsId(arrSelectedKeys[k])).find("#ruleName").text());
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

		var approvalHandlerLinguistics = function(){
			$(tabSelected).find("a#approveBtn, a#rejectBtn").on({
				click: function(evt){
					var comment = $.defaultIfBlank($.trim($(tabSelected).find("#approvalComment").val()),"");
//					if ($.isBlank(comment)){
//						jAlert("Please add comment.","Approval");
//					}else if(!isXSSSafe(comment)){
//						jAlert("Invalid comment. HTML/XSS is not allowed.","Approval");
					
					if (!validateComment("Approval", comment, 1, 300)){
						// error alert in function validateComment
					}else{
						// TODO: only spell rule supported at the moment
						switch($(evt.currentTarget).attr("id")){
						case "approveBtn":
							jConfirm("Continue approval for Did You Mean List?", "Confirm Approval", function(status){
								if(status){
									DeploymentServiceJS.approveRule(entityName, ["spell_rule"], comment, ["spell_rule"],{
										callback: function(data){
											jAlert("Did You Mean List was successfully approved.", "Approval");
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

							jConfirm("Continue reject for Did You Mean List?", "Confirm Reject", function(status){
								if(status){
									DeploymentServiceJS.unapproveRule(entityName, ["spell_rule"], comment, ["spell_rule"],{
										callback: function(data){
											jAlert("Did You Mean List was successfully rejected.", "Approval");
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
		
		var postTemplate = function(ruleType){
			var template = '';

			template  = '<div id="actionBtn" class="floatR fsize12 border w600 marT5 marB8" style="background: #f3f3f3;">';
			template += '	<h3 class="padL15" style="border:none">Approval Guidelines</h3>';
			template += '	<div class="fgray padL15 padR10 padB15 fsize11">';
			template += '		<p align="justify">';
			template += '			Before approving any rule, it is advisable to review rule details.<br/><br/>';
			template += '			If the rule is ready to be pushed to production, click on <strong>Approve</strong>.';
			template += '			If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.';
			template += '		<p>';
			template += '	</div>';
			template += '	<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>';
			template += '	<label class="floatL w480"><textarea id="approvalComment" rows="5" class="w460" style="height:32px"></textarea></label>';
			template += '	<div class="clearB"></div>';
			template += '	<div align="right" class="padR15 marT10">';
			template += '		<a id="approveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Approve</div>';
			template += '		</a>';
			template += '		<a id="rejectBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Reject</div>';
			template += '		</a>';
			template += '	</div>';
			template += '</div>';

			return template;
		};

		var preTemplate = function(ruleType){
			switch(ruleType.toLowerCase()){
			case "elevate": 
			case "exclude":
			case "demote":
				template  = '<div class="rulePreview w600">';
				template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
				template += '	<label class="w110 floatL fbold">Rule Name:</label>';
				template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Request Type:</label>';
				template += '	<label class="wAuto floatL" id="requestType"></label>';					
				template += '	<div class="clearB"></div>';
				template += '</div>';
				template += '<div class="clearB"></div>';
				break;
			case "facetsort":
			case "facet sort":
				template  = '<div class="rulePreview w600">';
				template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
				template += '	<label class="w110 floatL fbold">Rule Name:</label>';
				template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Rule Type:</label>';
				template += '	<label class="wAuto floatL" id="ruleType"></label>';					
				template += '	<div class="clearB"></div>';
				template += '</div>';
				template += '<div class="clearB"></div>';
				break;
			case "querycleaning":
			case "query cleaning":
				template  = '<div class="rulePreview w590 marB20">';
				template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
				template += '	<label class="w110 floatL fbold">Rule Name:</label>';
				template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Description:</label>';
				template += '	<label class="wAuto floatL" id="description">';
				template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
				template += '	</label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Active Type:</label>';
				template += '	<label class="wAuto floatL" id="redirectType">';
				template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
				template += '	</label>';
				template += '	<div class="clearB"></div>';							
				template += '</div>';
				break;
			case "rankingrule":
			case "ranking rule":
				template  = '<div class="rulePreview w590 marB20">';
				template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
				template += '	<label class="w110 floatL fbold">Rule Name:</label>';
				template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Start Date:</label>';
				template += '	<label class="wAuto floatL" id="startDate">';
				template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
				template += '	</label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">End Date:</label>';
				template += '	<label class="wAuto floatL" id="endDate">';
				template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
				template += '	</label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Description:</label>';
				template += '	<label class="wAuto floatL" id="description">';
				template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
				template += '	</label>';
				template += '	<div class="clearB"></div>';					
				template += '</div>';
				break;
			default: template = '';
			}

			return template;
		};

		var getApprovalList = function(){
			DeploymentServiceJS.getApprovalList(entityName, false, {
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
							$(tabSelected).find("label#requestedBy").html(list[0]["requestBy"]);
							$(tabSelected).find("label#requestedDate").html($.isNotBlank(list[0]["lastRequestDate"])? list[0]["lastRequestDate"].toUTCString(): "");
							// set to number to be displayed + 1, to detect if there is an overflow
							var displaySize = 50;
							SpellRuleServiceJS.getModifiedSpellRules(null, null, null, 1, displaySize + 1, {
								callback: function(response) {
									// Populate table row
									var responseData = response.data;
									var responseList = responseData.spellRule;
									$table = $(tabSelected).find("table#rule");
									$(tabSelected).find("label#numSuggestions").append(responseData.maxSuggest);
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
									
									// Alternate row style
									$(tabSelected).find("tr:not(#ruleItemPattern):even").addClass("alt");
									
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
									
									approvalHandlerLinguistics();
								}
							});
						}
						else {
							// Populate table row
							for(var i=0; i<data.totalSize ; i++){
								$table = $(tabSelected).find("table#rule");
								$tr = $(tabSelected).find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(list[i]["ruleRefId"])).show();
								var ruleStatus = list[i];
								var requestedDate = $.isNotBlank(list[i]["lastModifiedDate"])? list[i]["lastModifiedDate"].toUTCString(): "";
								var showId = list[i]["ruleRefId"].toLowerCase() !== list[i]["description"].toLowerCase();
	
								$tr.find("td#select > input[type='checkbox']").attr("id", list[i]["ruleRefId"]);
								$tr.find("td#select > input[type='checkbox']").attr("name", list[i]["ruleStatusId"]);
	
								//TODO: Get delete details from file
								if (list[i]["updateStatus"]!=="DELETE"){
									$tr.find("td#ruleOption > img.previewIcon").attr("id", list[i]["ruleRefId"]).preview({
										ruleType: entityName,
										ruleId: ruleStatus["ruleId"],
										ruleRefId: ruleStatus["ruleRefId"],
										ruleStatusId: ruleStatus["ruleStatusId"],
										requestType: ruleStatus["updateStatus"],
										ruleInfo: ruleStatus["description"],
										center: true,
										enablePreTemplate: true,
										enablePostTemplate: true,
										preTemplate: function(base){
											return preTemplate(base.options.ruleType);
										},
										postTemplate: function(base){
											return postTemplate(base.options.ruleType);
										},
										templateEvent: function(base){
											var $content = base.contentHolder; 
											$content.find("a#approveBtn, a#rejectBtn").off().on({
												click: function(evt){
													var comment = $.defaultIfBlank($content.find("#approvalComment").val(),"");
	
													if (validateComment("Approval", comment, 1, 300)){
														comment = comment.replace(/\n\r?/g, '<br/>');
														
														switch($(evt.currentTarget).attr("id")){
														case "approveBtn": 
															DeploymentServiceJS.approveRule(tabSelectedText, $.makeArray(base.options.ruleRefId) , comment, $.makeArray(base.options.ruleStatusId), {
																callback: function(data){
																	postMsg(data,true);	
																	getApprovalList();
																},
																preHook: function(){
																	base.api.destroy();
																}
															});break;
	
														case "rejectBtn": 
															if (checkIfDeleted()) {
																jAlert("Deleted rules cannot be rejected!","Approval");
																return;
															}
															DeploymentServiceJS.unapproveRule(tabSelectedText, $.makeArray(base.options.ruleRefId) , comment, $.makeArray(base.options.ruleStatusId), {
																callback: function(data){
																	postMsg(data,false);	
																	getApprovalList();
																},
																preHook: function(){
																	base.api.destroy();
																}
															});break;
														}	
													}
												}
											});
										},
										itemForceAddStatusCallback: function(base, memberIds){
											if (base.options.ruleType.toLowerCase() === "elevate"){
												ElevateServiceJS.isRequireForceAdd(base.options.ruleId, memberIds, {
													callback:function(data){
														base.updateForceAddStatus(data);
													},
													preHook: function(){
														base.prepareForceAddStatus();
													}
												});
											}
										}
									});
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
							
						}
					}else{
						$(tabSelected).find("div#requestDetails").hide();
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
			$(tabSelected).find('div#requestDetails').hide();
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
