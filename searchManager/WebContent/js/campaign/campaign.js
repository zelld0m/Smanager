(function ($) {
	var Campaign = {
		moduleName: "Campaign",
		selectedRule:  null,
		rulePageSize: 15,
		selectedRuleStatus: null,
	
		showMicroGallery: function(){
			$("#mG1").microgallery({
				size		: 'large',	/*small,medium,large*/
				menu		: true,
		        mode    	: 'thumbs',
				cycle		: true,
				autoplay	: true,
				autoplayTime: 3000});
			
			
			$("#mG2").microgallery({
				size		: 'large',	/*small,medium,large*/
				menu		: true,
				mode    	: 'thumbs',
				cycle		: true,
				autoplay	: true,
				autoplayTime: 3000});
			
			$("#mG3").microgallery({
				size		: 'large',	/*small,medium,large*/
				menu		: true,
				mode    	: 'thumbs',
				cycle		: true,
				autoplay	: true,
				autoplayTime: 3000});
			
			$("#mG4").microgallery({
				size		: 'large',	/*small,medium,large*/
				menu		: true,
				mode    	: 'thumbs',
				cycle		: true,
				autoplay	: true,
				autoplayTime: 3000});
			
			
		    $("#startdatepicker").datepicker({
			    showOn: "both",
			    buttonImage: "../../images/icon_calendarwithBG.png",
			    buttonImageOnly: true
		    });
		    
		    $("#enddatepicker").datepicker({
			    showOn: "both",
			    buttonImage: "../../images/icon_calendarwithBG.png",
			    buttonImageOnly: true
		    });
		    
		    $("#startdatepicker2").datepicker({
			    showOn: "both",
			    buttonImage: "../images/icon_calendarwithBG.png",
			    buttonImageOnly: true
		    });
		    
		    $("#enddatepicker2").datepicker({
			    showOn: "both",
			    buttonImage: "../images/icon_calendarwithBG.png",
			    buttonImageOnly: true
		    });
		},
		
		getBannerInCampaignList: function(page){},
		
		getKeywordInCampaignList: function(){},
		
		getRuleList: function(page){
			var self = this;
			$("#keywordSidePanel").sidepanel({
				moduleName: self.moduleName,
				headerText : "Campaign",
				fieldName: "ruleName",
				page: page,
				pageSize: self.rulePageSize,
				showAddButton: allowModify,
				filterText: self.ruleFilterText,

				itemDataCallback: function(base, ruleName, page){
					self.rulePage = page;
					self.ruleFilterText = ruleName;
					CampaignServiceJS.getRules(ruleName, page, base.options.pageSize, {
						callback: function(data){
							base.populateList(data, ruleName);
							base.addPaging(ruleName, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
						
					});
				},
				
				itemOptionCallback: function(base, item){
					//TODO
					//CampaignServiceJS.getTotalKeywordInRule(item.model["ruleId"],{
					//	callback: function(count){
					//		if (count > 0) item.ui.find("#itemLinkValue").html("(" + count + ")");
							
							item.ui.find("#itemLinkValue").on({
								click: function(e){
									self.setRule(item.model);
								}
							});
					//	},
					//	preHook: function(){ 
					//		item.ui.find("#itemLinkValue").hide();
					//		item.ui.find("#itemLinkPreloader").show();
					//	},
					//	postHook: function(){ 
					//		item.ui.find("#itemLinkValue").show();
					//		item.ui.find("#itemLinkPreloader").hide();
					//	}
					//});
				},
				
				itemAddCallback: function(base, name){
					$("a#addButton").qtip({
						id: "add-campaign",
						content: {
							text: $('<div/>'),
							title: { text: 'New ' + self.moduleName, button: true }
						},
						position: {
							target: $("a#addButton")
						},
						show: {
							ready: true
						},
						style: {width: 'auto'},
						events: { 
							show: function(e, api){
								var $contentHolder = $("div", api.elements.content).html(self.getAddRuleTemplate());
								
								if ($.isNotBlank(name)) $contentHolder.find('input[id="popName"]').val(name);
								
								$contentHolder.find('a#addButton').off().on({
									click: function(e){
										var ruleName = $contentHolder.find("#popName").val();
										var startDate = $contentHolder.find("#popStartDate").val();
										var endDate = $contentHolder.find("#popEndDate").val();
										var description = $contentHolder.find("#popDescription").val();
										
										if ($.isBlank(ruleName)){
											jAlert("Rule name is required.",self.moduleName);
										}else if (!isAllowedName(ruleName)){
											jAlert("Rule name contains invalid value.",self.moduleName);
										}else if (!isAscii(description)) {
											jAlert("Description contains non-ASCII characters.",self.moduleName);										
										}else if (!isXSSSafe(description)){
											jAlert("Description contains XSS.","Ranking Rule");
										}else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
											jAlert("Please provide a valid date range.",self.moduleName);
										} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
											jAlert("End date cannot be earlier than start date!",self.moduleName);
										}else{
											CampaignServiceJS.getRuleByName(ruleName, {
												callback: function(data){
													if (data != null){
														jAlert("Another campaign is already using the name provided.",self.moduleName);
													}else{
														CampaignServiceJS.addRule(ruleName, startDate, endDate, description, {
															callback: function(data){
																if(data != null){
																	showActionResponse(1, "add", ruleName);
																	self.getRuleList(1);
																	self.setRule(data);
																}
															},
															preHook: function(){ base.prepareList(); },
														});
													}
												}
											});
										}
									}
								});

								$contentHolder.find('a#clearButton').off().on({
									click: function(e){
										$contentHolder.find("#popName").html("");
										$contentHolder.find("#popStartDate").html("");
										$contentHolder.find("#popEndDate").html("");
										$contentHolder.find("#popDescription").html("");
									}
								});
							},
							hide: function (e, api){
								api.destroy();
							}
						}
					});
				}
			});
		},
		
		getAddRuleTemplate: function(){
			var template = "";
			template += '<div id="addRuleTemplate">';
			template += 	'<div class="w282 padT10 newRule">';
			template +=			'<label class="w72 txtLabel">Name</label> <label><input id="popName" type="text" class="w185" maxlength="100"></label><div class="clearB"></div>';
			template += 		'<label class="w72 txtLabel">Schedule </label> <label><input name="popStartDate" type="text" class="w65 fsize11"></label> <label class="txtLabel"> - </label> <label><input name="popEndDate" type="text" class="w65 fsize11"></label><div class="clearB"></div>';
			template += 		'<label class="w72 txtLabel">Description</label> <label><textarea id="popDescription" rows="1" class="w185" maxlength="255"></textarea> </label><div class="clearB"></div>';
			template += 		'<div class="txtAR pad3"><a id="addButton" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> <a id="clearButton" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>';
			template += 	'</div>';
			template += '</div>';
			
			return template;
		},
		
		showRuleContent: function(){
			var self = this;
			
			self.prepareRule();
			$("#preloader").hide();
			self.getRuleList(1);

			if(self.selectedRule==null){
				$("#noSelected").show();
				$("#titleText").html(self.moduleName);
				return;
			}
			
			$("#submitForApproval").rulestatus({
				moduleName: self.moduleName,
				rule: self.selectedRule,
				ruleType: "Campaign",
				enableVersion: true,
				authorizeRuleBackup: allowModify,
				authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
				postRestoreCallback: function(base, rule){
					base.api.destroy();
					CampaignServiceJS.getRuleById(self.selectedRule["ruleId"],{
						callback: function(data){
							self.setRule(data);
						},
						preHook: function(){
							self.prepareRule();
						}
					});
				},
				afterSubmitForApprovalRequest:function(ruleStatus){
					self.showRuleContent();
				},
				beforeRuleStatusRequest: function(){
					self.prepareRule();	
				},
				afterRuleStatusRequest: function(ruleStatus){
					$("#preloader").hide();
					$("#submitForApproval").show();
					$("#titleText").html(self.moduleName + " for ");
					$("#titleHeader").text(self.selectedRule["ruleName"]);
					$("#ruleContent").show();
					
					self.selectedRuleStatus = ruleStatus;

					$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

					//set name, description, etc
					$("#name").val(self.selectedRule["ruleName"]);
					$("#description").val(self.selectedRule["description"]);
					
					self.getBannerInCampaignList(1);
					self.addSaveRuleListener();
					self.addDeleteRuleListener();
					self.addDownloadListener();

					$('#auditIcon').off().on({
						click: function(e){
							$(e.currentTarget).viewaudit({
								itemDataCallback: function(base, page){
									AuditServiceJS.getCampaignTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
										callback: function(data){
											var total = data.totalSize;
											base.populateList(data);
											base.addPaging(base.options.page, total);
										},
										preHook: function(){
											base.prepareList();
										}
									});
								}
							});
						}
					});
				}
			});
		},
		
		prepareRule : function(){
			clearAllQtip();
			$("#preloader").show();
			$("#submitForApproval, #ruleContent, #noSelected").hide();
			$("#titleHeader").empty();
		},
		
		setRule : function(rule){
			var self = this;
			self.selectedRule = rule;

			self.showRuleContent();
		},
		
		addSaveRuleListener: function(){
			var self = this;
			$("#saveBtn").off().on({
				click: function(e){
					if (e.data.locked) return;

					setTimeout(function() {
						var description = "";

						var response = 0;
						CampaignServiceJS.updateRule(self.selectedRule["ruleId"], self.selectedRule["ruleName"], description, {
							callback: function(data){
								response = data;
								showActionResponse(data > 0 ? 1 : data, "update", self.selectedRule["ruleName"]);
							},
							preHook: function(){
								self.prepareRule();
							},
							postHook: function(){
								if(response>0){
									CampaignServiceJS.getRuleById(self.selectedRule["ruleId"],{
										callback: function(data){
											self.setRule(data);
										},
										preHook: function(){
											self.prepareRule();
										}
									});
								}
								else{
									self.setRule(self.selectedRule);
								}

							}
						});
					}, 500 );
				},
				mouseenter: showHoverInfo
			},{locked:self.selectedRuleStatus["locked"] || !allowModify});
		},
		
		addDownloadListener: function(){
			var self = this;
			$("a#downloadIcon").download({
				headerText:"Download " + self.moduleName,
				requestCallback:function(e){
					var params = new Array();
					var url = document.location.pathname + "/xls";
					var urlParams = "";
					var count = 0;
					params["id"] = self.selectedRule["ruleId"];
					params["filename"] = e.data.filename;
					params["type"] = e.data.type;
					params["clientTimezone"] = +new Date();

					for(var key in params){
						if (count>0) urlParams +='&';
						urlParams += (key + '=' + encodeURIComponent(params[key]));
						count++;
					};

					document.location.href = url + '?' + urlParams;
				}
			});
		},
		
		addDeleteRuleListener: function(){
			var self = this;
			$("#deleteBtn").off().on({
				click: function(e){
					if (e.data.locked) return;

					jConfirm("Delete " + self.selectedRule["ruleName"] + "'s rule?", self.moduleName, function(result){
						if(result){
							CampaignServiceJS.deleteRule(self.selectedRule["ruleId"],{
								callback: function(code){
									showActionResponse(code, "delete", self.selectedRule["ruleName"]);
									if(code==1) {
										self.setRule(null);
									}
								}
							});
						}
					});
				},
				mouseenter: showHoverInfo
			},{locked:self.selectedRuleStatus["locked"] || !allowModify});
		},
		
		init : function() {
			var self = this;
			self.showRuleContent();
		}
	};
	
	$(document).ready(function() {
		Campaign.init();
	});
	
})(jQuery);