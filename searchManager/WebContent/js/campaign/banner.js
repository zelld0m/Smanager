(function ($) {

	var Banner = {
		moduleName: "Banner",
		selectedRule:  null,
		rulePageSize: 15,
		selectedRuleStatus: null,
		
		getBannerList : function(page) {
			var self = this;
			$("#keywordSidePanel").sidepanel({
				moduleName: self.moduleName,
				headerText : "Banner",
				fieldName: "ruleName",
				page: page,
				pageSize: self.rulePageSize,
				showAddButton: allowModify,
				filterText: self.ruleFilterText,

				itemDataCallback: function(base, ruleName, page){
					self.rulePage = page;
					self.ruleFilterText = ruleName;
					BannerServiceJS.getRules(ruleName, page, base.options.pageSize, {
						callback: function(data){
							base.populateList(data, ruleName);
							base.addPaging(ruleName, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
						
					});
				},
				
				itemOptionCallback: function(base, item){
					//TODO
					//BannerServiceJS.getTotalCampaignUsingBanner(item.model["ruleId"],{
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
				
				itemNameCallback: function(base, item){
					self.setRule(item.model);
				},
				
				itemAddCallback: function(base, name){
					$("a#addButton").qtip({
						id: "add-banner",
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
								var $contentHolder = $("div", api.elements.content).html(self.getAddBannerTemplate());
								
								if ($.isNotBlank(name)) $contentHolder.find('input[id="popKeywordName"]').val(name);
								
								$contentHolder.find('a#addButton').off().on({
									click: function(e){
										var ruleName = $contentHolder.find("#popKeywordName").val();
										var description = $contentHolder.find("#description").val();
										var imagePath = $contentHolder.find("#imagePath").val();
										var linkPath = "";
										var imageAlt = "";
										
										if($.isBlank(ruleName)){
											jAlert("Name is required.", self.moduleName);
										}else if(!isAllowedName(ruleName)){
											jAlert("Name contains invalid value.",self.moduleName);
										}else if($.isBlank(imagePath)){
											jAlert("Image path is required.", self.moduleName);
										}else if (!isXSSSafe(imagePath)){
											jAlert("Image path contains XSS.",self.moduleName);
										}else if($.isNotBlank(description) && !isXssSafe(description)){
											jAlert("Description contains XSS.",self.moduleName);
										}else if($.isNotBlank(linkPath) && !isXssSafe(linkPath)){
											jAlert("Link path contains XSS.",self.moduleName);
										}else if($.isNotBlank(imageAlt) && !isXssSafe(imageAlt)){
											jAlert("Image alt contains XSS.",self.moduleName);
										}else{
											BannerServiceJS.getRuleByName(ruleName, {
												callback: function(data){
													if (data != null){
														jAlert("Another banner is already using the name provided.",self.moduleName);
													}else{
														BannerServiceJS.addRule(ruleName, linkPath, imagePath, imageAlt, description, {
															callback: function(data){
																//if(data != null){
																	showActionResponse(1, "add", ruleName);
																	self.getBannerList(1);
																	self.setRule(data);
																//}
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
										$contentHolder.find("#popKeywordName").html("");
										$contentHolder.find("#imagePath").html("");
										$contentHolder.find("#description").html("");
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
		
		getAddBannerTemplate : function(){
			var template = "";
			template += '<div id="addBannerTemplate">';
			template += '<div class="w282 padT10 newBanner">';
			
			template += '	<div id="keywordinput">';
			template += '		<label class="floatL w80 txtLabel">Name: </label>'; 
			template += '		<label class="floatL"><input id="popKeywordName" type="text" class="w188" maxlength="100"></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div id="bannerImageMode">';
			template += '		<label class="floatL w80 txtLabel"></label>'; 
			template += '		<label class="floatL"><label class="floatL padTB2">Paste image URL | Upload an image</label></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="bannerImage">';
			template += '		<label class="floatL w80 txtLabel">Image: </label>'; 
			template += '		<label class="floatL"><label class="floatL padTB2"><textarea id="imagePath" class="w240"></textarea></label></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div id="description">';
			template += '		<label class="floatL w80 txtLabel">Description: </label>'; 
			template += '		<label class="floatL"><input id="description" type="text" class="w188" maxlength="200"></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div class="txtAR pad3 marT10">';
			template += '		<a id="addButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Save</div> </a>'; 
			template += '		<a id="clearButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Clear</div> </a>';
			template += '	</div>'; 
			template += '</div>';
			template += '</div>';
			return template;
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
		
		showRuleContent : function(){
			var self = this;
			
			self.prepareRule();
			$("#preloader").hide();
			self.getBannerList(1);

			if(self.selectedRule==null){
				$("#noSelected").show();
				$("#titleText").html(self.moduleName);
				return;
			}
			
			$("#submitForApproval").rulestatus({
				moduleName: self.moduleName,
				rule: self.selectedRule,
				ruleType: "Banner",
				enableVersion: true,
				authorizeRuleBackup: allowModify,
				authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
				postRestoreCallback: function(base, rule){
					base.api.destroy();
					BannerServiceJS.getRuleById(self.selectedRule["ruleId"],{
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
									AuditServiceJS.getBannerTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
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
		
		getBannerInCampaignList : function(page){
			var self = this;
			$("#campaignWithBannerPanel").selectbox({
				fieldName: "campaignName",
				itemTitle: "New Campaign",
				page: page,
				region: "content",
				pageStyle: "style2",
				pageSize: self.keywordInRulePageSize,
				headerText : "Campaigns Using This Banner",
				headerTextAlt : "Campaign",
				itemTextClass: "cursorText",
				showAddButton: !self.selectedRuleStatus["locked"] && allowModify,
				showStatus: false,

				itemDataCallback: function(base, keyword, page){
					BannerServiceJS.getAllCampaignUsingThisBanner(self.selectedRule["ruleId"], campaignNameFilter, page, base.options.pageSize, {
						callback: function(data){
							base.populateList(data, keyword);
							base.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
					});
				},
				
				itemNameCallback: function(base, item){
					self.setRule(item.model);
				},

				itemOptionCallback: function(base, item){
					var icon = '<a id="deleteKw" href="javascript:void(0);"><img src="' + GLOBAL_contextPath + '/images/icon_delete2.png"></a>';

					item.ui.find(".itemLink").html($(icon));

					item.ui.find(".itemLink > a#deleteKw").off().on({
						click: function(e){
							if (e.data.locked) return;

							jConfirm('Delete "' + item.name + '" in ' + self.selectedRule["ruleName"]  + '?', "Delete Keyword", function(result){
								if(result){
									RedirectServiceJS.deleteBannerInCampaign(self.selectedRule["ruleId"], item.name,{
										callback:function(code){
											showActionResponse(code, "delete", item.name);
											self.getKeywordInRuleList(1);
											self.getBannerList(1);
										},
										preHook: function(){ 
											base.prepareList(); 
										}
									});
								}
							});					
						},
						mouseenter: showHoverInfo
					},{locked: self.selectedRuleStatus["locked"] || !allowModify});
				},

				itemAddCallback: function(base, keyword){
					if (!self.selectedRuleStatus["locked"] && allowModify){
						RedirectServiceJS.addKeywordToRule(self.selectedRule["ruleId"], keyword, {
							callback: function(code){
								showActionResponse(code, "add", keyword);
								self.getKeywordInRuleList(1);
								self.getRedirectRuleList(1);
							},
							preHook: function(){ base.prepareList(); }
						});
					}
				}
			});
		},
		
		addSaveRuleListener: function(){
			var self = this;
			$("#saveBtn").off().on({
				click: function(e){
					if (e.data.locked) return;

					setTimeout(function() {
						var ruleName = $.trim($('div#ruleContent input[id="name"]').val()); 
						var description = $.trim($('div#ruleContent textarea[id="description"]').val());

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
						}else{
							BannerServiceJS.checkForRuleNameDuplicate(self.selectedRule["ruleId"], ruleName, {
								callback: function(data){
									if (data==true){
										jAlert("Another banner is already using the name provided.", self.moduleName);
									}else{
										BannerServiceJS.updateRule(self.selectedRule["ruleId"], ruleName, description,  {
											callback: function(data){
												response = data;
												showActionResponse(data > 0 ? 1 : data, "update", ruleName);
											},
											preHook: function(){
												self.prepareRule();
											},
											postHook: function(){
												if(response>0){
													BannerServiceJS.getRuleById(self.selectedRule["ruleId"],{
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
							BannerServiceJS.deleteRule(self.selectedRule["ruleId"],{
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
		Banner.init();
	});	
})(jQuery);