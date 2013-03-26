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
				fieldName: "bannerName",
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
									self.setBanner(item.model);
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
						id: "add-facetsort",
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
										var bannerName = $contentHolder.find("#popKeywordName").val();
										var description = $contentHolder.find("#description").val();
										var imagePath = $contentHolder.find("#imagePath").val();
										var linkPath = "";
										var imageAlt = "";
										
										if($.isBlank(bannerName)){
											jAlert("Name is required.", self.moduleName);
										}else if(!isAllowedName(bannerName)){
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
											BannerServiceJS.getRuleByName(bannerName, {
												callback: function(data){
													if (data != null){
														jAlert("Another banner is already using the name provided.",self.moduleName);
													}else{
														BannerServiceJS.addRule(bannerName, linkPath, imagePath, imageAlt, description, {
															callback: function(data){
																if(data != null){
																	showActionResponse(1, "add", bannerName);
																	self.getBannerList(1);
																	self.setBanner(data);
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
		
		prepareBanner : function(){
			clearAllQtip();
			$("#preloader").show();
			$("#submitForApproval").hide();
			$("#titleHeader").empty();
		},
		
		setBanner : function(rule){
			var self = this;
			self.selectedRule = rule;

			self.showBannerContent();
		},
		
		showBannerContent : function(){
			var self = this;
			
			self.prepareBanner();
			self.getBannerList(1);

			if(self.selectedRule==null){
				$("#preloader").hide();
				$("#titleText").html(self.moduleName);
				return;
			}
			
			$("#submitForApproval").rulestatus({
				moduleName: self.moduleName,
				rule: self.selectedRule,
				ruleType: "Facet Sort",
				enableVersion: true,
				authorizeRuleBackup: allowModify,
				authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
				postRestoreCallback: function(base, rule){
					base.api.destroy();
					FacetSortServiceJS.getRuleById(self.selectedRule["ruleId"],{
						callback: function(data){
							self.setBanner(data);
						},
						preHook: function(){
							self.prepareBanner();
						}
					});
				},
				afterSubmitForApprovalRequest:function(ruleStatus){
					self.showBannerContent();
				},
				beforeRuleStatusRequest: function(){
					self.prepareBanner();	
				},
				afterRuleStatusRequest: function(ruleStatus){
					$("#preloader").hide();
					$("#submitForApproval").show();
					$("#titleText").html(self.moduleName + " for ");
					$("#titleHeader").text(self.selectedRule["ruleName"]);
					$("#readableString").html(self.selectedRule["readableString"]);

					self.selectedRuleStatus = ruleStatus;

					$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

					self.addSaveRuleListener();
					self.addDeleteRuleListener();
					self.addDownloadListener();

					$('#auditIcon').off().on({
						click: function(e){
							$(e.currentTarget).viewaudit({
								itemDataCallback: function(base, page){
									//TODO
									AuditServiceJS.getFacetSortTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
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
		
		init : function() {
			var self = this;
			self.showBannerContent();
		}
	};
	
	$(document).ready(function() {
		Banner.init();
	});	
})(jQuery);