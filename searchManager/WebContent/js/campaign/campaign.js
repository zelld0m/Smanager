(function ($) {
	var Campaign = {
		moduleName: "Campaign",
		selectedRule:  null,
		rulePageSize: 15,
		selectedRuleStatus: null,
		excFields: new Array(),
		
		noPreviewImagePath: GLOBAL_contextPath + "/images/nopreview.png",
	
		showMicroGallery: function(){
			var self = this;
			var $gallery = $("div#microGallery");
			
			$gallery.prop({title: self.selectedRule["readableString"]});

			CampaignServiceJS.getBannersInCampaign(self.selectedRule["ruleId"],{
				callback : function(data){
					var imgCount = 0;
					if(data){
						var list = data.list;
						imgCount = list.length;
						
						//populate images
						for(var i = 0; i < list.imgCount; i++){
							$newImg = $('<img />');
							$.newImg.prop({
								alt: $.isNotBlank(list[i]["imageAlt"]) ? list[i]["imageAlt"] : "img"+i,
								src: $.isNotBlank(list[i]["imagePath"]) ? list[i]["imagePath"] : self.noPreviewImagePath
							});
							
							$gallery.append($newImg);
						}
					}
					
					$("#microGallery").microgallery({
						size		: 'large',	/*small,medium,large*/
						menu		: true,
				        mode    	: 'thumbs',
						cycle		: true,
						autoplay	: true,
						autoplayTime: 3000,
						imgCount	: imgCount,
						headerText	: "Banners",
						ruleId		: ruleId,
						
						//TODO
						//addBanner event listener
						addImageCallback : function(base, contentHolder){
							contentHolder.qtip({
								content: { text: $('<div>'), title: { text: base.headerText, button: true }},
								show: {modal:true},
								events: { 
									render: function(e, api){
										var $contentHolder = $("div", api.elements.content).html($("#setupFieldValueS1").html());

										$contentHolder.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
										$contentHolder.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();

										CampaignServiceJS.searchBannersInThisCampaign(self.selectedRule["ruleId"],"", 1, self.rulePageSize, {
											callback:function(data){
												var list = data.list;

												for (var i=0; i< data.totalSize; i++){
													var bannerName = list[i].bannerName;
													var bannerId = list[i].bannerId;

													if ($.isNotBlank(bannerName)){
														//$.pushIfNotExist(self.excFields, bannerName, function(el){ return el === name; });
														//self.populateSelectedField($contentHolder, bannerName, bannerId);
													}
												}	

												self.populateFieldList($contentHolder,1);
											},
											preHook: function(){
												$contentHolder.find("tbody#fieldSelectedPattern > tr").not("#fieldSelectedPattern").remove();
											}
										});
										
										$contentHolder.find('a#closeBtn').on({
											click: function(e){
												api.hide();
											}
										});
									
										/*var searchActivated = false;
										var newSearch = "";
										var oldSearch = "";
										
										var sendRequest = function(event){
											setTimeout(function(){
												self.sfSearchKeyword = newSearch = $.trim($(event.target).val());

												if (newSearch === self.campaignNameSearchText) {
													newSearch = "";
												};

												if (oldSearch !== newSearch) {
													self.populateCampaignList($contentHolder,1);
													oldSearch = newSearch;
													sendRequest(event);
													newSearch = "";
												}
												else {
													searchActivated = false;
												}
											}, self.reloadRate);  
										};

										var timeout = function(event){
											if (!searchActivated) {
												searchActivated = true;
												sendRequest(event);
											}
										};*/
										
										/*$contentHolder.find('input[id="searchBoxField"]').val(self.searchText).on({
											blur: function(e){
												if ($.trim($(e.target).val()).length == 0) 
													$(e.target).val(self.searchText);
												timeout(e);
											},
											focus: function(e){
												if ($.trim($(e.target).val()) == self.searchText) 
													$(e.target).val("");
												timeout(e);
											},
											keyup: timeout 
										});*/

									},
									hide: function (e, api){
										self.excFields = new Array();
										self.showMicroGallery();
										api.destroy();
									}
								}
							});
						}
					});
				},
				preHook: function(){
					//show preloader
					$gallery.find("img:visible").remove();
				},
				postHook: function(){
					//hide preloader
				}
			});
		},
		
		populateFieldList : function(content, page){
			var self = this;
			var currVal = "";
			BannerServiceJS.getRules(currVal, page, self.rulePageSize, {
				callback:function(data){
					var list = data.list;
					var listSize = data.totalSize;

					content.find("ul#fieldListing > li").not("#fieldListingPattern").remove();

					for (var i=0; i< listSize; i++){
						if(list[i]!=null){
							var idx = self.excFields.indexOf(list[i].ruleName);
							if(idx == -1){
								content.find("#fieldListingPattern").clone().appendTo("ul#fieldListing").attr("id","fieldListing_"+i).attr("style","display:float");
								content.find('#fieldListing_' + i + ' span').html(list[i].ruleName);
	
								content.find('#fieldListing_' + i + ' a').on({click: function(e){
									var element = e.data.bannerName;
	
									CampaignServiceJS.addCampaignBanner(e.data.campaignId, e.data.bannerId, {
										callback: function(data){
											if(data){
												if($.pushIfNotExist(self.excFields, element, function(el){ return el === element; })){
													//self.populateSelectedField(content, element, 0);
													self.populateFieldList(content, 1);
												}
											}else{
												showActionResponse(code, "add", element);
											}
										}
									});
								}},{bannerName:list[i].ruleName, bannerId:list[i].ruleId, campaignId:self.selectedRule["ruleId"]});
							}
						}	
					}

					//TODO
					//content.find('span#sfCount').html(schemaFieldsTotal + " Record" + (schemaFieldsTotal > 1 ? "s":""));
					//var selectedCount = content.find('tr.fieldSelectedItem:not(#fieldSelectedPattern)').length;
					//content.find('span#sfSelectedCount').html(selectedCount + " Record" + (selectedCount > 1 ? "s":""));
					//addSchemaFieldsPaging(content, page);
				},
				preHook:function(){
					content.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
					content.find("div#fieldListing div#preloader").show();					
					content.find("div#fieldListing div#content").hide();					
				},
				postHook:function(){
					content.find("div#fieldListing div#preloader").hide();	
					content.find("div#fieldListing div#content").show();						
				}
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
				
				itemNameCallback: function(base, item){
					self.setRule(item.model);
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
								
								var popDates = $contentHolder.find("#popStartDate, #popEndDate").prop({readonly: true}).datepicker({			
									minDate: 0,
									maxDate: '+1Y',			
									showOn: "both",
									buttonImage: "../images/icon_calendar.png",
									buttonImageOnly: true,
									changeMonth: true,
								    changeYear: true,
									onSelect: function(selectedDate) {
										var option = this.id == "popStartDate" ? "minDate" : "maxDate",
												instance = $(this).data("datepicker"),
												date = $.datepicker.parseDate( instance.settings.dateFormat ||
														$.datepicker._defaults.dateFormat, selectedDate, instance.settings);
										popDates.not(this).datepicker("option", option, date);
									}
								});
								
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
																showActionResponse(1, "add", ruleName);
																self.getRuleList(1);														
																self.setRule(data);
															},
															preHook: function(){ base.prepareList(); }
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
			template += 	'<div class="w282 padT10 newCampaign">';
			template +=			'<label class="w72 txtLabel">Name</label> <label><input id="popName" type="text" class="w185" maxlength="100"></label><div class="clearB"></div>';
			template += 		'<label class="w72 txtLabel">Schedule </label> <label><input id="popStartDate" type="text" class="w65 fsize11"></label> <label class="txtLabel"> - </label> <label><input id="popEndDate" type="text" class="w65 fsize11"></label><div class="clearB"></div>';
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
					$("div#ruleContent input#name").val(self.selectedRule["ruleName"]);
					$("div#ruleContent textarea#description").val(self.selectedRule["comment"]);
					$("div#ruleContent input#startDate").val(self.selectedRule["formattedStartDateTime"]);
					$("div#ruleContent input#endDate").val(self.selectedRule["formattedEndDateTime"]);
					
					$("div#ruleContent input#startDate, div#ruleContent input#endDate").datepicker("destroy");

					var dates = $("div#ruleContent input#startDate, div#ruleContent input#endDate").prop({readonly: true}).datepicker({
						minDate: 0,
						maxDate: '+1Y',
						showOn: "both",
						buttonImage: "../images/icon_calendar.png",
						buttonImageOnly: true,
						changeMonth: true,
					    changeYear: true,
						disabled: self.selectedRuleStatus.locked || $.endsWith(self.selectedRule["ruleId"], "_default") || !allowModify,
						onSelect: function(selectedDate) {
							var option = this.id == "startDate" ? "minDate" : "maxDate",
									instance = $(this).data("datepicker"),
									date = $.datepicker.parseDate(
											instance.settings.dateFormat ||
											$.datepicker._defaults.dateFormat,
											selectedDate, instance.settings);
							dates.not(this).datepicker("option", option, date);
						}
					});
					
					self.addSaveRuleListener();
					self.addDeleteRuleListener();
					self.addDownloadListener();
					
					self.showMicroGallery();

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

					
					var ruleName = $.trim($('div#ruleContent input[id="name"]').val()); 
					var description = $.trim($('div#ruleContent textarea[id="description"]').val()); 
					var startDate = $.trim($('div#ruleContent input[name="startDate"]').val());
					var endDate = $.trim($('div#ruleContent input[name="endDate"]').val());
					
					var response = 0;
					if ($.isBlank(ruleName)){
						jAlert("Rule name is required.", self.moduleName);
					}
					else if (!isAllowedName(ruleName)){
						jAlert("Rule name contains invalid value.", self.moduleName);
					}
					else if (ruleName.length>100){
						jAlert("Name should not exceed 100 characters.", self.moduleName);
					}
					else if (!isAscii(description)) {
						jAlert("Description contains non-ASCII characters.", self.moduleName);
					}
					else if (!isXSSSafe(description)){
						jAlert("Description contains XSS.", self.moduleName);
					}
					else if (description.length>255){
						jAlert("Description should not exceed 255 characters.", self.moduleName);
					}
					else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
						jAlert("Please provide a valid date range!",self.moduleName);
					} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
						jAlert("End date cannot be earlier than start date!",self.moduleName);
					}
					else {
						CampaignServiceJS.checkForRuleNameDuplicate(self.selectedRule["ruleId"], ruleName, {
							callback: function(data){
								if (data==true){
									jAlert("Another campaign is already using the name provided.", self.moduleName);
								}else{
									CampaignServiceJS.updateRule(self.selectedRule["ruleId"], ruleName, startDate, endDate, description, {
										callback: function(data){
											response = data;
											showActionResponse(response, "update", ruleName);
										},
										preHook: function(){
											self.prepareRule();
										},
										postHook: function(){
											if(response==1){
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
								}
							}
						});
					}
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