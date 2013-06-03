(function ($) {

	BannerPage = {
			moduleName: "Banner",
			rulePage: 1,
			rulePageSize: 10,
			ruleItemPageSize: 2,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",

			selectedRule: null,
			selectedRuleItemPage: 1,
			selectedRuleItemTotal: 1,
			selectedRuleStatus: null,
			ruleFilterText: "",
			bannerInfo: null,

			lookupMessages: {
				successAddNewKeyword: "Successfully added keyword {0}",
				failedAddNewKeyword: "Failed to add keyword {0}",
				successAddBannerToKeyword: "Successfully added banner {0} to {1} with priority {2}",
				failedAddBannerToKeyword: "Failed to add banner {0} to {1} with priority {2}",
				jumpToPageOfAddedBanner: "The banner is in page {0}. Do you want to refresh page to view the banner?"
			},

			init: function(){
				var self = this;
				$("#ruleItemHolder, #addBannerBtn").hide();
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
					showAddButton: true, 
					filterText: self.ruleFilterText,

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

						BannerServiceJS.getTotalRuleItems(item.model["ruleId"], {
							callback: function(sr){
								var count = sr["data"];
								if (count > 0) 
									item.ui.find("#itemLinkValue").html("(" + count + ")");

								item.ui.find("#itemLinkValue").off().on({
									click: function(e){
										self.selectedRuleItemTotal = count;
										self.setRule(e.data.item.model);
									}
								}, {item: item});
							},
							preHook: function(){ 
								item.ui.find("#itemLinkValue").hide();
								item.ui.find("#itemLinkPreloader").show();
							},
							postHook: function(){ 
								item.ui.find("#itemLinkValue").show();
								item.ui.find("#itemLinkPreloader").hide();
							}
						});
					},

					itemNameCallback: function(base, item){
						self.setRule(item.model);
					},

					itemAddCallback: function(base, ruleName){
						BannerServiceJS.addRule(ruleName, {
							callback: function(sr){
								switch(sr["status"]){
								case 0: 
									jAlert($.formatText(self.lookupMessages.successAddNewKeyword, ruleName), "Banner Rule", function(){
										BannerServiceJS.getRuleByNameExact(ruleName, {
											callback: function(sr){
												self.setRule(sr["data"]);
											}
										});
									}); 
									break;
								default:  jAlert($.formatText(self.lookupMessages.failedAddNewKeyword, ruleName), "Banner Rule"); 
								}

								base.getList(ruleName, 1);

							},
							preHook: function(e){
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
							callback: function(response){
								if (response.status == 0) {
									self.setRule(response.data);
								} else {
									jAlert(response.errorMessage.message, "Error");
								}
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
						self.setRuleItemFilter();
						self.getRuleItemList(1);
					}
				});
			},

			addRuleItemToggleHandler: function(ui, item){
				var self = this;
				var toggle = $.cookie('banner.toggle' + $.formatAsId(item["memberId"]));
				ui.find("#bannerInfo").hide();
				self.setToggleStatus(ui, item, ($.isBlank(toggle) || "hide".toLowerCase() === toggle) ? false: true);

				ui.find("#toggleText").off().on({
					click: function(e){
						var status = $.cookie('banner.toggle' + $.formatAsId(e.data.item["memberId"]));
						self.setToggleStatus(e.data.ui, e.data.item, ($.isBlank(status) || "hide".toLowerCase() === status) ? true : false);
					}
				}, {ui:ui, item:item});
			},

			setToggleStatus: function(ui, item, show){
				var self = this;

				if (show){
					ui.find("#toggleText").text("Show Less").end()
					.find("#bannerInfo").slideDown("slow", function(){
						$.cookie('banner.toggle' + $.formatAsId(item["memberId"]), "show" ,{path:GLOBAL_contextPath});

						self.addInputFieldListener(ui, item, ui.find("input#imagePath"), self.previewImage);
						self.addInputFieldListener(ui, item, ui.find("input#linkPath"), self.validateLinkPath);
						self.addSetAliasHandler(ui, item);
						self.addUpdateRuleItemHandler(ui, item);
						self.addDeleteItemHandler(ui, item);

						var uii = $(this).parents(".ruleItem");
						
						uii.find("input, textarea").prop({
							readonly: false,
							disabled: false
						}).end()
						.find(".startDate, .endDate").datepicker("enable");

						self.addScheduleRestriction(uii, item);
						self.addItemExpiredRestriction(uii, item);

					});

				}else{
					ui.find("#toggleText").text("Show More");

					ui.find("#bannerInfo").slideUp("slow", function(){
						$.cookie('banner.toggle' + $.formatAsId(item["memberId"]), "hide" ,{path:GLOBAL_contextPath});
						// all element readonly and disabled regardless of schedule, rule status, and expiration
						var uii = $(this).parents(".ruleItem");
						
						uii.find("input, textarea").prop({
							readonly: true,
							disabled: true
						}).end().find(".startDate, .endDate").datepicker("disable");	
					});
				}
			},

			setRuleItemFilter: function(value){
				var self = this;
				var filter = $.isNotBlank(value)? value : $.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]));

				if ($.isNotBlank(filter)){
					$("#itemFilter").val(filter);
				}else{
					$.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]), "all" ,{path:GLOBAL_contextPath});
					$("#itemFilter").val("all");
				}

				$("#itemFilter").off().on({
					change: function(e){
						$.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]), $(this).val(), {path:GLOBAL_contextPath});
						self.getRuleItemList(1);
					}
				});
			},

			getRuleItemFilter: function(){
				var self = this;
				return $.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]));
			},

			getRuleItemList: function(page){
				var self = this;
				var rule = self.selectedRule;
				var $iHolder = $("#ruleItemHolder");
				self.selectedRuleItemPage = page;
				$(".ruleItem:not(#ruleItemPattern)").remove();
				$("#ruleItemHolder").hide();

				BannerServiceJS.getRuleItems(self.getRuleItemFilter(), rule["ruleId"], page, self.ruleItemPageSize, {
					callback: function(sr){
						var recordSet = sr["data"];

						if (recordSet && recordSet["totalSize"]>0){
							$("#ruleItemHolder").show();

							$("#ruleItemPagingTop").paginate({
								type: 'short',
								currentPage: page, 
								pageSize: self.ruleItemPageSize,
								pageStyle: "style2",
								totalItem: recordSet["totalSize"],
								callbackText: function(itemStart, itemEnd, itemTotal){
									var selectedText = $.trim($("#itemFilter").val()) !== "all" ? " " + $("#itemFilter option:selected").text(): "";
									if ($("#itemFilter").val() === "all") 
										self.selectedRuleItemTotal = itemTotal;
									
									return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + selectedText + " Items";
								},
								pageLinkCallback: function(e){ self.getRuleItemList(e.data.page); },
								nextLinkCallback: function(e){ self.getRuleItemList(e.data.page + 1); },
								prevLinkCallback: function(e){ self.getRuleItemList(e.data.page - 1); },
								firstLinkCallback: function(e){ self.getRuleItemList(1); },
								lastLinkCallback: function(e){ self.getRuleItemList(e.data.totalPages); }
							});

							self.populateRuleItem(recordSet);
						}
					},
					preHook: function(e){
						$(".ruleItem:not(#ruleItemPattern)").remove();
						$("#ruleItemPagingTop").empty();
					}
				});
			},

			addRuleStatusRestriction: function(){
				var self = this;
				
				if(self.selectedRuleStatus["locked"]){
					$("#addBannerBtn, .setAliasBtn").hide();
					
					$(".ruleItem").find("input, textarea").prop({
						readonly: true,
						disabled: true
					}).end
					.find(".startDate, .endDate").datepicker('disable');
				}
				
			},

			populateRuleItem: function(rs){
				var self = this;
				var $iHolder = $("#ruleItemHolder");
				var $iPattern = $iHolder.find("#ruleItemPattern").hide();

				for(var i=0; i < rs["totalSize"]; i++){
					var ui = $iPattern.clone();
					var item = rs["list"][i];
					ui.prop({
						id: "ruleItem_" + item["memberId"]
					}).addClass(i + 1 == rs["totalSize"] ? "last": "").appendTo($iHolder).show();
					self.populateRuleItemFields(ui, item);
				}
			},

			populateRuleItemFields: function(ui, item){
				var self = this;

				self.previewImage(ui, item, item["imagePath"]["path"]);

				ui
				.find("#imageTitle").text(item["imagePath"]["alias"]).end()
				.find("#priority").val(item["priority"]).end()
				.find("#startDate").val(item["formattedStartDate"]).end()
				.find("#endDate").val(item["formattedEndDate"]).end()

				.find("#imagePath").val(item["imagePath"]["path"]).end()
				.find("#imageAlias").val(item["imagePath"]["alias"]).prop({
					id: item["imagePath"]["id"],
					readonly: true,
					disabled: true,
				}).end()
				.find("#imageAlt").val(item["imageAlt"]).end()
				.find("#linkPath").val(item["linkPath"]).end()
				.find("#description").val(item["description"]).end()
				.find("#description").val(item["description"]).end()
				.find("#temporaryDisable").prop({
					checked: item["disable"]
				}).end()
				.find("#openNewWindow").prop({
					checked: item["openNewWindow"]
				})

				// Select a date range, datepicker issue on multiple id even with scoping
				.find("#startDate").prop({id: "startDate_" + item["memberId"]}).datepicker({
					minDate: currentDate,
					defaultDate: currentDate,
					changeMonth: true,
					changeYear: true,
					showOn: "both",
					buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
					buttonImageOnly: true,
					onClose: function(selectedDate) {
						ui.find("#endDate_" + item["memberId"]).datepicker("option", "minDate", selectedDate);
					}
				}).end()

				.find("#endDate").prop({id: "endDate_" + item["memberId"]}).datepicker({
					minDate: ui.find("#startDate_" + item["memberId"]).datepicker("getDate"),
					defaultDate: currentDate,
					changeMonth: true,
					changeYear: true,
					showOn: "both",
					buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
					buttonImageOnly: true,
					onClose: function(selectedDate) {
						if(!ui.find("#startDate_" + item["memberId"]).datepicker("isDisabled")){
							ui.find("#startDate_" + item["memberId"]).datepicker("option", "maxDate", selectedDate);
						}
					}
				});

				self.registerEventListener(ui, item);
			},

			registerEventListener: function(ui, item){
				var self = this;

				self.addDurationHandler(ui, item);
				self.addCopyToHandler(ui, item);
				self.getLinkedKeyword(ui, item);
				self.addShowKeywordHandler(ui, item);
				self.addItemAuditHandler(ui, item);
				self.addLastUpdateHandler(ui, item);
				self.addItemCommentHandler(ui, item);
				self.addRuleItemToggleHandler(ui, item);
				self.addRuleItemHandler();
				self.addDeleteAllItemHandler();
				self.addDownloadRuleHandler(ui, item);
				self.addRuleStatusRestriction();
			},

			addItemExpiredRestriction: function(ui, item){
				var self = this;
				
				if(item["expired"]){
					ui.find("input, textarea").prop({
						readonly: true,
						disabled: true
					});
				}
			},

			addDurationHandler: function(ui, item){
				var self = this;
				var color = "orange";
				var durationText = "Not Yet Started";

				if(!item["expired"] && item["started"]){
					color = "green";
					durationText = item["daysLeft"];
				}else if(item["expired"]){
					color = "red";
					durationText = "Has Expired";
				}

				ui.find("#daysLeft").text(durationText).css({
					color: color
				});
			},

			addDeleteAllItemHandler: function(){
				var self = this;

				$('#deleteAllItemIcon').off().on({
					click: function(e){
						if (e.data.locked) return;
						jConfirm("Delete all banner item in " + self.selectedRule["ruleName"] + "?", self.moduleName, function(result){
							if(result){
								BannerServiceJS.deleteAllRuleItem(self.selectedRule["ruleId"], {
									callback: function(e){
										self.getRuleItemList(1);
									}
								});
							}
						});
					},
					mouseenter: showHoverInfo
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify});
			},

			addLastUpdateHandler: function(ui, item){
				ui.find('#lastModifiedIcon').off().on({
					mouseenter: showLastModified 
				},{user: item["lastModifiedBy"], date: item["formattedLastModifiedDateTime"]});
			},

			addScheduleRestriction: function(ui, item){
				var self = this;
				// Disable when rule item has started
				ui.find(".startDate").datepicker(item["started"]? 'disable' : 'enable');
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
							if(callback) callback(e.data.ui, e.data.item, $(e.currentTarget).val());
						}
					},

					focusout: function(e) {
						if (e.data.locked) return;

						if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()) {
							if(callback) callback(e.data.ui, e.data.item, $(e.currentTarget).val());
						}
					}
				}, {ui: ui , item: item, locked: self.selectedRuleStatus["locked"] || !allowModify, input: ""});

			},

			getImagePath: function(ui, item, imagePath){
				var self = this;

				BannerServiceJS.getImagePath(imagePath, {
					callback: function(sr){
						var iPath = sr["data"];
						if (iPath!=null){
							ui.find(".imageAlias").prop({
								id: iPath["id"],
								readonly: true,
								disabled: true
							}).val(iPath["alias"]).end()

							.find("#setAliasBtn").show();

							self.addSetAliasHandler(ui, item);
						}else{
							ui.find(".imageAlias").val("").prop({
								readonly: false,
								disabled: false
							}).removeAttr("id").end()
							.find("#setAliasBtn").hide();
						}
					}
				});
			},

			validateLinkPath: function(ui, linkPath){

			},

			previewImage: function(ui, item, imagePath){
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

				self.getImagePath(ui, item, imagePath);
			},

			addSetAliasHandler: function(ui, item){
				var self = this;

				ui.find("#setAliasBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						var btnSetText = "Set Alias";
						var btnCancelText = "Cancel";
						var setAlias = $(e.currentTarget).find("#setAliasText").text() === btnSetText;

						e.data.ui
						.find("#imagePath").prop({
							readonly: setAlias,
							disabled: setAlias
						}).end()

						.find(".imageAlias").prop({
							readonly: !setAlias,
							disabled: !setAlias
						}).val(
								!setAlias? e.data.item["imagePath"]["alias"] : ""
						);

						$(e.currentTarget).find("#setAliasText").text(
								setAlias? btnCancelText: btnSetText
						);
					},
					mouseenter: showHoverInfo
				}, {ui: ui, item: item, locked: self.selectedRuleStatus['locked'] || !allowModify || item["expired"]});
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

			getLinkedKeyword: function(ui, item){
				var self = this;
				var count = 1;


				BannerServiceJS.getTotalRuleWithImage(item["imagePath"]["id"], item["imagePath"]["alias"],{
					callback: function(sr){
						var total = sr["data"];
						if ($.isNumeric(total) && total > 1){
							count = total;
						} 
					},
					preHook: function(e){
						ui.find("#keywordCount").text(count);
					},
					postHook: function(e){
						ui.find("#keywordCount").text(count);
					}
				});
			},

			addShowKeywordHandler: function(ui, item){
				var self = this;

				ui.find("#keywordBtn").listbox({
					title: "Linked Keywords",
					emptyText: "No linked keywords",
					locked: self.selectedRuleStatus["locked"] || !allowModify,
					page: 1,
					rule: self.selectedRule,
					ruleItem: item, 
					pageSize: 5,
					parentNameText: item["imagePath"]["alias"],
					itemDataCallback:function(base, page){
						BannerServiceJS.getAllRuleWithImage(item["imagePath"]["id"], item["imagePath"]["alias"], page, base.options.pageSize, {
							callback:function(sr){
								var recordSet = sr["data"];
								var total = recordSet["totalSize"];
								base.populateList(recordSet);
								base.addPaging(page, total);
							},
							preHook: function(e){
								base.prepareList();
							},
							postHook: function(e){
								base.reposition();
							}
						});
					},
					itemDeleteCallback:function(base, rule, rItem){
						BannerServiceJS.deleteRuleItemWithImage(rule["ruleId"], rItem["imagePath"]["id"], rItem["imagePath"]["alias"], {
							callback:function(e){
								base.getList(1);
							},
							preHook: function(e){
								base.prepareList();
							},
							postHook: function(e){
								self.getLinkedKeyword(ui, item);
							}
						});
					}
				});
			},

			addRuleItemHandler: function(){
				var self = this;
				
				$("#addBannerBtn").addbanner({
					id: 'addbanner',
					rule: self.selectedRule,
					ruleItem: null,
					mode: 'add',
					isPopup: true,
					priority: self.selectedRuleItemTotal + 1 ,
					addBannerCallback: function(e){
						var params = e.data;

						var mapParams = {
								"ruleId": params["ruleId"],
								"ruleName": params["ruleName"],
								"priority": params["priority"], 
								"startDate": params["startDate"], 
								"endDate": params["endDate"], 
								"imageAlt": params["imageAlt"], 
								"linkPath": params["linkPath"], 
								"description": params["description"], 
								"imagePathId": params["imagePathId"], 
								"imagePath": params["imagePath"], 
								"imageAlias": params["imageAlias"]
						};

						BannerServiceJS.addRuleItem(mapParams, {
							callback: function(sr){
								switch(sr["status"]){
								case 0: 
									jAlert($.formatText(self.lookupMessages.successAddBannerToKeyword, params["imageAlias"], params["ruleName"], params["priority"]), "Banner Rule", function(){
										self.getRuleItemList(1);
									}); 
									break;
								default:  jAlert($.formatText(self.lookupMessages.failedAddBannerToKeyword, params["imageAlias"], params["ruleName"], params["priority"]), "Banner Rule"); 
								}
							},
							preHook: function(e){
								
							}
						});
					}
				}).show();
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

			addDeleteItemHandler: function(ui, item){
				var self = this;

				ui.find("#deleteBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm("Delete banner " + item["imagePath"]["alias"] + " from " + self.selectedRule["ruleName"] + "?", self.moduleName, function(result){
							if(result){
								BannerServiceJS.deleteRuleItem(self.selectedRule["ruleId"], item["memberId"], item["imagePath"]["alias"],{
									callback: function(sr){
										self.getRuleItemList(1);
									}
								});
							}
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify || item["expired"]});
			},

			getUpdatedFields: function(ui, item){
				var self = this;
				
				//Updatable fields
				var imagePathId = $.trim(ui.find(".imageAlias").prop("id"));
				var imagePath = $.trim(ui.find("#imagePath").val());
				var imageAlias = $.trim(ui.find(".imageAlias").val());
				var priority = $.trim(ui.find("#priority").val());
				var startDate = $.trim(ui.find(".startDate").val());
				var endDate = $.trim(ui.find(".endDate").val());
				var imageAlt = $.trim(ui.find("#imageAlt").val());
				var linkPath = $.trim(ui.find("#linkPath").val());
				var description = $.trim(ui.find("#description").val());
				var openNewWindow = ui.find("#openNewWindow").is(':checked');
				var disable = ui.find("#temporaryDisable").is(':checked');
				
				var mapParams = {
						"ruleId": self.selectedRule["ruleId"] ,
						"memberId": item["memberId"],
						
						"imagePathId": imagePathId !== item["imagePath"]["id"] ?  imagePathId: null,
						"imagePath": $.isNotBlank(imagePath) && !$.iequals(imagePath, item["imagePath"]["path"]) ? imagePath : null,
						"imageAlias": $.isNotBlank(imageAlias) && !$.iequals(imageAlias, item["imagePath"]["alias"]) ? imageAlias: null,
						
						"priority": $.isNotBlank(priority) && $.isNumeric(priority) && !$.iequals(priority, $.trim(item["priority"]))?  priority : null,
						"startDate": $.isNotBlank(startDate) && $.isDate(startDate) && !$.iequals(startDate, item["formattedStartDate"]) ?  startDate: null,
						"endDate": $.isNotBlank(endDate) && $.isDate(endDate) && !$.iequals(endDate, item["formattedEndDate"])?  endDate: null,
						"imageAlt": $.isNotBlank(imageAlt) &&  !$.iequals(imageAlt, item["imageAlt"]) ? imageAlt: null,
						"linkPath": $.isNotBlank(linkPath) && !$.iequals(linkPath, item["linkPath"])  ? linkPath: null,
						"description": $.isNotBlank(description) && !$.iequals(description, item["description"]) ? description: null,
						"disable": disable != item["disabled"] ? disable : null,
						"openNewWindow": openNewWindow != item["openNewWindow"] ? openNewWindow: null
				};
				
				return mapParams;
			},
			
			addUpdateRuleItemHandler: function(ui, item){
				var self = this;

				ui.find("#updateBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						//get all fields value
						var ruleId = self.selectedRule["ruleId"];
						var memberId = e.data.item["memberId"];
						var imagePathId = e.data.ui.find(".imageAlias").prop("id");
						var imagePath = e.data.ui.find("#imagePath").val();
						var imageAlias = e.data.ui.find(".imageAlias").val();

						var priority = e.data.ui.find("#priority").val();
						var startDate = e.data.ui.find(".startDate").val();
						var endDate = e.data.ui.find(".endDate").val();
						var imageAlt = e.data.ui.find("#imageAlt").val();
						var linkPath = e.data.ui.find("#linkPath").val();
						var description = e.data.ui.find("#description").val();
						var disable = e.data.ui.find("#temporaryDisable").is(':checked');

						if($.isBlank(imagePath)) {
							jAlert("Image path is required.", "Banner");
						} else if($.isBlank(imageAlias)) {
							jAlert("Image alias is required.", "Banner");
						} else if($.isBlank(imageAlt)) {
							jAlert("Image alt is required.", "Banner");
						}else if($.isBlank(linkPath)) {
							jAlert("Link path is required.", "Banner");
						} else{
							jConfirm("Update " + e.data.item["imagePath"]["alias"] + "?", self.moduleName, function(result){
								if(result){
									BannerServiceJS.updateRuleItem(self.getUpdatedFields(e.data.ui, e.data.item), {
										callback: function(data){

										},
										preHook: function(){

										},
										postHook: function(){
											self.getRuleItemList(self.selectedRuleItemPage);
										}
									});
								}
							});
						}	
					},
					mouseenter: showHoverInfo
				},{ui:ui, item:item, locked:self.selectedRuleStatus["locked"] || !allowModify || item["expired"]});
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
				}, { item: item, locked: self.selectedRuleStatus["locked"] || !allowModify || item["expired"]});
			},

			addItemAuditHandler: function(ui, item){
				var self = this;
				ui.find('#auditIcon').off().on({
					click: function(e){
						$(e.currentTarget).viewaudit({
							itemDataCallback: function(base, page){
								AuditServiceJS.getBannerItemTrail(self.selectedRule["ruleId"], e.data.item["memberId"], base.options.page, base.options.pageSize, {
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
				}, {ui:ui, item: item});
			},

			addDownloadRuleHandler: function(){
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
