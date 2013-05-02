(function ($) {

	$.BannerPage = {
			moduleName: "Banner",
			rulePage: 1,
			rulePageSize: 10,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",

			selectedRule: null,
			selectedRuleStatus: null,
			ruleFilterText: "",
			bannerInfo: null,

			_init: function(){
				var self = this;
				$("#titleText").text(self.moduleName);
				self._showRuleList(1);
			},

			_setRule: function(rule){
				var self = this;
				self.selectedRule = rule;
				self._showRuleStatus();
			},

			_showRuleList: function(page){
				var self = this;

				$("#rulePanel").sidepanel({
					moduleName: self.moduleName,
					headerText : self.moduleName,
					fieldName: "ruleName",
					page: page,
					pageSize: self.rulePageSize,
					showAddButton: true, // TODO:

					itemDataCallback: function(base, ruleName, page){
						self.rulePage = page;
						self.ruleFilterText = ruleName;

						BannerServiceJS.getRules(ruleName, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data, ruleName);
								base.addPaging(ruleName, page, data.totalSize);
							},
							preHook: function(){ 
								base.prepareList(); 
							}
						});
					},

					itemOptionCallback: function(base, item){
						item.ui.find("#itemLinkValue").on({
							click: function(e){
								self._setRule(item.model);
							}
						});
					},
					
					itemNameCallback: function(base, item){
						self._setRule(item.model);
					},

				});
			},

			_showRuleStatus: function(){
				var self = this;
				
				$("#ruleStatus").rulestatus({
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
								self._setRule(data);
							},
							preHook: function(){
								self._beforeShowRuleStatus();	
							}
						});
					},
					
					afterSubmitForApprovalRequest:function(ruleStatus){
						self._showRuleStatus();
					},
					
					beforeRuleStatusRequest: function(){
						self._showRuleList();
						self._beforeShowRuleStatus();	
					},
					
					afterRuleStatusRequest: function(ruleStatus){
						self._afterShowRuleStatus();
						self.selectedRuleStatus = ruleStatus;
						self._showBanner();
						self._showRuleToCampaign();
						self._deleteRule();
						self._updateRule();
					}
				});
			},

			_showBanner: function(){
				var self = this;
				var rule = self.selectedRule;
				
				self._showImagePreview();
				
				$("#editImageLink").uploadimage({
					isPopup: true,
					isLocked: self.selectedRule["isLocked"] || !allowModify,
					rule: self.selectedRule,
					imageChangeCallback: function(e){
						self.selectedRule["imagePath"] = e.data["imagePath"];
						self.selectedRule["imageAlt"] = e.data["imageAlt"];
						self.selectedRule["linkPath"] = e.data["linkPath"];
						self._showImagePreview();
					}
				});
			},
			
			_showImagePreview: function(){
				var self = this;
				var imagePath = self.selectedRule["imagePath"];
				var $previewHolder = $("div#bannerImage");
				
				if($.isBlank(imagePath)){
					imagePath = noPreviewImage;
				}
				
				$previewHolder.find("span.preloader").show();
				
				setTimeout(function(){
					$previewHolder.find("img#imagePreview").prop("src",imagePath).off().on({
						error:function(){ 
							$(this).unbind("error").prop("src", noPreviewImage); 
						}
					});
				},10);
				
				$previewHolder.find("span.preloader").hide();
			},
			
			_beforeShowRuleStatus: function(){
				var self = this;
				$("#preloader").show();
				$("#infographic, #ruleStatus, #ruleContent").hide();
				$("#titleText").text(self.moduleName);
				$("#titleHeader").empty();
			},
			
			_showRule: function(){
				var self = this;
				self._showRuleStatus();
			},
			
			_showRuleToCampaign: function(){
				var self = this;
				var $bannerRelations = $("#bannerRelations");
				var $preloader = $bannerRelations.find("#preloader");
				var $bannerToCampaign = $bannerRelations.find("#bannerToCampaign");
				
				BannerServiceJS.searchCampaignUsingThisBanner(self.selectedRule["ruleId"], "", 0, 0, {
					callback: function(data){
						$preloader.hide();
						
						$bannerToCampaign.selectbox({
							maxSelectionList: 4,
							selectedItems: data.list
						});

					},
					preHook: function(){ 
						$preloader.show();
					}
				});
				
			},
			
			_afterShowRuleStatus: function(){
				var self = this;
				$("#preloader, #infographic").hide();
				$("#ruleStatus, #ruleContent").show();
				$("#titleText").text(self.moduleName + " for ");
				$("#titleHeader").text(self.selectedRule["ruleName"]);
			},

			_deleteRule: function(){
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

			_updateRule: function(){
				var self = this;
				$("#updateBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm("Update " + self.selectedRule["ruleName"] + "'s rule?", self.moduleName, function(result){
							if(result){
								BannerServiceJS.updateRule(self.selectedRule["ruleId"],self.selectedRule["linkPath"],self.selectedRule["imagePath"],self.selectedRule["imageAlt"], ruleName, description, {
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
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},
			
			_downloadRule: function(){
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
			}
	};

	$(document).ready(function() {
		$.BannerPage._init();
	});	
})(jQuery);