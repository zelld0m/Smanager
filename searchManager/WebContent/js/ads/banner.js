(function ($) {

	BannerPage = {
			moduleName: "Banner",
			rulePage: 1,
			rulePageSize: 10,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",

			selectedRule: null,
			selectedRuleStatus: null,
			ruleFilterText: "",
			bannerInfo: null,

			messages: {
				
			},
			
			init: function(){
				var self = this;
				$("#addBannerBtn").show();
				$("#titleText").text(self.moduleName);
				self.showRuleList(1);
			},

			setRule: function(rule){
				var self = this;
				self.selectedRule = rule;
				$("#addBannerBtn").show();
				self.showRuleStatus();
			},

			showRuleList: function(page){
				var self = this;

				$("#rulePanel").sidepanel({
					moduleName: self.moduleName,
					headerText : "Keyword",
					fieldId: "ruleId",
					fieldName: "ruleName",
					page: page,
					pageSize: self.rulePageSize,
					showAddButton: true, // TODO:

					itemDataCallback: function(base, ruleName, page){
						self.rulePage = page;
						self.ruleFilterText = ruleName;

						BannerServiceJS.getAllRules(ruleName, page, base.options.pageSize, {
							callback: function(sr){
								var data = sr["data"]; 
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
								self.setRule(item.model);
							}
						});
					},
					
					itemNameCallback: function(base, item){
						self.setRule(item.model);
					},
					
					itemAddCallback: function(base, ruleName){
						BannerServiceJS.addRule(ruleName, {
							callback: function(sr){
								showActionResponse(sr["status"], "add", ruleName);
								self.showRuleList();
							},
							postHook: function(e){
								base.prepareList();
							}
						});
					}
				});
			},

			showRuleStatus: function(){
				var self = this;
				
				$("#ruleStatus").rulestatus({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					ruleType: "Banner",
					enableVersion: true,
					authorizeRuleBackup: allowModify,
					authorizeSubmitForApproval: allowModify,
					
					postRestoreCallback: function(base, rule){
						base.api.destroy();
						BannerServiceJS.getRuleById(self.selectedRule["ruleId"],{
							callback: function(data){
								self.setRule(data);
							},
							preHook: function(){
								self.beforeShowRuleStatus();	
							}
						});
					},
					
					afterSubmitForApprovalRequest:function(ruleStatus){
						self.showRuleStatus();
					},
					
					beforeRuleStatusRequest: function(){
						self.showRuleList();
						self.beforeShowRuleStatus();	
					},
					
					afterRuleStatusRequest: function(ruleStatus){
						self.afterShowRuleStatus();
						self.selectedRuleStatus = ruleStatus;
						self.getRuleItemList();
					}
				});
			},
			
			getRuleItemList: function(){
				var self = this;
				var rule = self.selectedRule;
				BannerServiceJS.getRuleItem(rule["ruleId"], 0, 0, {
					callback: function(sr){
						var recordSet = sr["data"];
						self.populateRuleItem(recordSet);
					},
					preHook: function(e){
						
					},
					preHook: function(e){
						
					}
				});
			},
			
			populateRuleItem: function(rs){
				var self = this;
				var $iHolder = $("#ruleItemHolder");
				var $iPattern = $iHolder.find("#ruleItemPattern").hide();
				$iHolder.children().remove(":not(#ruleItemPattern)");
				
				for(var i=0; i < rs["totalSize"]; i++){
					var $ui = $iPattern.clone();
					var $ruleItem = rs["list"][i]; 
					self.populateRuleItemFields($ui, $ruleItem);
					$ui.show();
					$iHolder.append($ui);
				}
			},
			
			populateRuleItemFields: function(ui, item){
				var self = this;
				ui.prop({
					id: item["id"]
				}).find("#imagePath").val(item["imagePath"]["path"]).end()
				  .find("#alias").val(item["imagePath"]["alias"]).end()
				  .find("#imageAlt").val(item["imageAlt"]).end()
				  .find("#linkPath").val(item["linkPath"]).end()
				  .find("#description").val(item["description"]);
				//TODO: Event Listener
			},
			
			showBanner: function(){
				var self = this;
				var rule = self.selectedRule;
				
				self.showImagePreview();
				
				$("#editImageLink").uploadimage({
					isPopup: true,
					isLocked: self.selectedRule["isLocked"] || !allowModify,
					rule: self.selectedRule,
					imageChangeCallback: function(e){
						self.selectedRule["imagePath"] = e.data["imagePath"];
						self.selectedRule["imageAlt"] = e.data["imageAlt"];
						self.selectedRule["linkPath"] = e.data["linkPath"];
						self.showImagePreview();
					}
				});
			},
			
			showImagePreview: function(){
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
			
			beforeShowRuleStatus: function(){
				var self = this;
				$("#preloader").show();
				$("#infographic, #ruleStatus, #ruleContent").hide();
				$("#titleText").text(self.moduleName);
				$("#titleHeader").empty();
			},
			
			afterShowRuleStatus: function(){
				var self = this;
				$("#preloader, #infographic").hide();
				$("#ruleStatus, #ruleContent").show();
				$("#titleText").text(self.moduleName + " for ");
				$("#titleHeader").text(self.selectedRule["ruleName"]);
			},

			deleteRule: function(){
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

			updateRule: function(){
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
			
			downloadRule: function(){
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
		BannerPage.init();
	});	
	
})(jQuery);