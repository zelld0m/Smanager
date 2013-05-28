(function ($) {

	BannerPage = {
			moduleName: "Banner",
			rulePage: 1,
			rulePageSize: 10,
			ruleItemPageSize: 2,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",

			selectedRule: null,
			selectedRuleStatus: null,
			ruleFilterText: "",
			bannerInfo: null,

			messages: {

			},

			init: function(){
				var self = this;
				$("#addBannerBtn").hide();
				$("#titleText").text(self.moduleName);
				self.getRuleList(1);
			},

			setRule: function(rule){
				var self = this;
				self.selectedRule = rule;
				self.showRuleStatus();
			},

			getRuleList: function(page){
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
								self.getRuleList();
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

				$("#ruleStatus").rulestatusbar({
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
						self.getRuleList();
						self.beforeShowRuleStatus();	
					},

					afterRuleStatusRequest: function(ruleStatus){
						self.afterShowRuleStatus();
						self.selectedRuleStatus = ruleStatus;
						self.getRuleItemList(1);
					}
				});
			},

			getRuleItemList: function(page){
				var self = this;
				var rule = self.selectedRule;
				var $iHolder = $("#ruleItemHolder");
				$iHolder.find(".ruleItem:not(#ruleItemPattern)").remove();

				BannerServiceJS.getRuleItems(rule["ruleId"], page, self.ruleItemPageSize, {
					callback: function(sr){
						var recordSet = sr["data"];

						$("#ruleItemPagingTop").paginate({
							type: 'short',
							currentPage: page, 
							pageSize: self.ruleItemPageSize,
							pageStyle: "style2",
							totalItem: recordSet["totalSize"],
							callbackText: function(itemStart, itemEnd, itemTotal){
								var selectedText = $.trim($("#filterDisplay").val()) != "all" ? " " + $("#filterDisplay option:selected").text(): "";
								return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + selectedText + " Items";
							},
							pageLinkCallback: function(e){ self.getRuleItemList(e.data.page); },
							nextLinkCallback: function(e){ self.getRuleItemList(e.data.page + 1); },
							prevLinkCallback: function(e){ self.getRuleItemList(e.data.page - 1); }
						});

						self.populateRuleItem(recordSet);
					},
					preHook: function(e){

					},
					postHook: function(e){
						self.adjustPageToRuleStatus(self.selectedRuleStatus["locked"] || !allowModify);
					}
				});
			},

			adjustPageToRuleStatus: function(locked){
				var self = this;

				if(locked){
					$("#addBannerBtn").hide();
					$(".ruleItem").find("input, textarea").prop({
						readonly: true,
						disabled: true
					});
				}else{
					$("#addBannerBtn").show();
					$(".ruleItem").find("input, textarea").prop({
						readonly: false,
						disabled: false
					});
					self.addRuleItemHandler();
				}
			},

			populateRuleItem: function(rs){
				var self = this;
				var $iHolder = $("#ruleItemHolder");
				var $iPattern = $iHolder.find("#ruleItemPattern").hide();

				for(var i=0; i < rs["totalSize"]; i++){
					var $ui = $iPattern.clone();
					var $ruleItem = rs["list"][i];
					$ui.prop({
						id: "ruleItem_" + $ruleItem["memberId"]
					});
					self.populateRuleItemFields($ui, $ruleItem);
					$ui.show();
					$iHolder.append($ui);
				}
			},

			populateRuleItemFields: function(ui, item){
				var self = this;

				self.previewImage(ui, item["imagePath"]["path"]);

				ui
				.find("#imageTitle").text(item["imagePath"]["alias"]).end()
				.find("#priority").val(item["priority"]).end()
				.find("#startDate").val(item["formattedStartDate"]).end()
				.find("#endDate").val(item["formattedEndDate"]).end()

				.find("#imagePath").val(item["imagePath"]["path"]).end()
				.find("#imageAlias").val(item["imagePath"]["alias"]).prop({id: item["imagePath"]["id"]}).end()
				.find("#imageAlt").val(item["imageAlt"]).end()
				.find("#linkPath").val(item["linkPath"]).end()
				.find("#description").val(item["description"]).end()

				// Select a date range, datepicker issue on multiple id even with scoping
				.find("#startDate").prop({id: "startDate_" + item["memberId"]}).datepicker({
					defaultDate: "+1w",
					changeMonth: true,
					changeYear: true,
					showOn: "both",
					buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
					onClose: function(selectedDate) {
						ui.find("#endDate_" + item["memberId"]).datepicker("option", "minDate", selectedDate);
					}
				}).end()

				.find("#endDate").prop({id: "endDate_" + item["memberId"]}).datepicker({
					defaultDate: "+1w",
					changeMonth: true,
					changeYear: true,
					showOn: "both",
					buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
					onClose: function(selectedDate) {
						ui.find("#startDate_" + item["memberId"]).datepicker("option", "maxDate", selectedDate);
					}
				});

				// Days left
				if($.isNotBlank(item["daysLeft"])){
					ui.find("daysLeft").text(item["daysLeft"]);
				}

				self.registerEventListener(ui, item);
			},

			registerEventListener: function(ui, item){
				var self = this;

				self.addScheduleRestriction(ui, item);
				self.addInputFieldListener(ui, item, ui.find("input#imagePath"), self.previewImage);
				self.addInputFieldListener(ui, item, ui.find("input#linkPath"), self.validateLinkPath);
				self.addCopyToHandler(ui, item);
				self.addShowKeywordHandler(ui, item);
				self.addItemCommentHandler(ui, item);
				self.addSetAliasHandler(ui, item);
				self.addDeleteRuleHandler(ui, item);
			},

			addScheduleRestriction: function(ui, item){
				// Disable when rule item has started
				ui.find(".startDate").datepicker(item["started"]? 'disable' : 'enable').end()
				
				// Disable when rule is locked, rule item has expired, and user has no permission
				.find(".startDate, .endDate").datepicker(item["expired"] || self.selectedRuleStatus["locked"] || !allowModify ? 'disable' : 'enable');
			},
			
			addInputFieldListener: function(ui, item, input, callback){
				var self = this;

				input.off().on({
					mouseenter: function(e) {
						if(e.data.locked) {
							showHoverInfo;
						} else {
							e.data.input = $.trim($(e.currentTarget).val());
						}
					},

					focusin: function(e) {
						if(e.data.locked) {
							showHoverInfo;
						} else {
							e.data.input = $.trim($(e.currentTarget).val());
						}
					},

					mouseleave: function(e) {
						if (e.data.locked) return;

						if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()) {
							if(callback) callback(e.data.ui, $(e.currentTarget).val());
						}
					},

					focusout: function(e) {
						if (e.data.locked) return;

						if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()) {
							if(callback) callback(e.data.ui, $(e.currentTarget).val());
						}
					}
				}, {ui: ui , item: item, locked: self.selectedRuleStatus["locked"] || !allowModify, input: ""});

			},

			getImagePath: function(ui, imagePath){
				var self = this;
				
				BannerServiceJS.getImagePath(imagePath, {
					callback: function(sr){
						if (sr!=null && recordSet["totalSize"]==1){
							var recordSet = sr["data"]; 
							var iPath = recordSet["list"][0];

							ui.find(".alias").prop({id: iPath["id"]}).val(iPath["path"]).prop({
								readonly: true,
								disabled: true
							}).end()
							.find(".setAliasLink > div").text("Update Alias");

						}else{
							ui.find(".alias").prop({id: "alias"}).val("").prop({
								readonly: false,
								disabled: false
							}).end()
							.find(".setAliasLink > div").text("Save Alias");
						}
					},
					preHook: function(e){

					},
					preHook: function(e){
					
					}
				});
			},
			
			validateLinkPath: function(ui, linkPath){
				
			},
			
			previewImage: function(ui, imagePath){
				var self = this;
				var $previewHolder = ui.find("#preview");
				
				if($.isBlank(imagePath)){
					imagePath = self.noPreviewImage;
				}
				
				$previewHolder.find("img#imagePreview").attr("src",imagePath).off().on({
					error:function(){ 
						$(this).unbind("error").attr("src", self.noPreviewImage); 
					}
				});
			},

			addSetAliasHandler: function(ui, item){
				var self = this;

				ui.find("#setAliasBtn").off().on({
					click: function(e){

						e.data.ui
						.find("#imagePath").prop({
							readonly: true,
							disabled: true
						}).end()

						.find(".alias").prop({
							readonly: false,
							disabled: false
						}).end()

						.find("#setAliasLink > div").text(
								e.data.ui.find(".alias").prop("id") === "alias" ?
										"Save Alias" :
											"Update Alias"	
						);

						e.data.ui.find("#cancelAliasBtn").show();
					}
				}, {ui: ui, item: item }).end()

				.find("#cancelAliasBtn").off().on({
					click: function(e){

						e.data.ui
						.find("#imagePath").prop({
							readonly: false,
							disabled: false
						}).end()

						.find(".alias").prop({
							readonly: true,
							disabled: true
						}).val(item["imagePath"]["alias"]).end()

						.find("#setAliasLink > div").text("Set Alias");

						$(e.currentTarget).hide();
					}
				}, {ui: ui, item: item});
			},

			addCopyToHandler: function(ui, item){
				var self = this;

				ui.find("#copyToBtn").addbanner({
					id: 'copybanner',
					rule: self.selectedRule,
					ruleItem: item,
					mode: 'copy',
					isPopup: true
				});
			},

			addShowKeywordHandler: function(ui, item){
				var self = this;
			},

			addRuleItemHandler: function(){
				var self = this;

				$("#addBannerBtn").addbanner({
					id: 'addbanner',
					rule: self.selectedRule,
					ruleItem: null,
					mode: 'add',
					isPopup: true,
					addBannerCallback: function(e){
						var params = e.data;
						BannerServiceJS.addRuleItem(
								params["ruleId"], 1, params["startDate"], params["endDate"], 
								params["imageAlt"], params["linkPath"], params["description"], 
								params["imagePathId"], params["imagePath"], params["imageAlias"], {
									callback: function(e){

									},
									preHook: function(e){},
									postHook: function(e){
										self.getRuleItemList(1);
									}
								});
					}
				});
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

			addDeleteRuleHandler: function(ui, item){
				var self = this;

				ui.find("#deleteBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm("Delete banner " + item["imagePath"]["alias"] + " from " + self.selectedRule["ruleName"] + "?", self.moduleName, function(result){
							if(result){
								BannerServiceJS.deleteRuleItem(self.selectedRule["ruleId"], item["memberId"], item["imagePath"]["alias"],{
									callback: function(sr){
										if (sr & sr["status"]==0){
											self.setRule(self.selectedRule);
										}else if(sr & sr["status"]!=0){
											jAlert(sr["errorMessage"]);
										}
									}
								});
							}
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
			},

			addUpdateRuleHandler: function(ui, item){
				var self = this;

				ui.find("#updateBtn").off().on({
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
			
			addItemCommentHandler: function(ui, item){
				var self = this;
				ui.find("#commentIcon").off().on({
					click: function(e){
						$(e.currentTarget).comment({
							showAddComment: true,
							locked: e.data.locked,
							itemDataCallback: function(base, page){
								if(e.data){
									CommentServiceJS.getComment(self.moduleName, e.data.item["memberId"], base.options.page, base.options.pageSize, {
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
							},
							itemAddComment: function(base, comment){
								CommentServiceJS.addRuleItemComment(self.moduleName, e.data.item["memberId"], comment, {
									callback: function(data){
										base.getList(base.options.page);
									},
									preHook: function(){
										base.prepareList();
									}
								});
							}
						});
					}
				}, { item: item, locked: self.selectedRuleStatus["locked"] || !allowModify});
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