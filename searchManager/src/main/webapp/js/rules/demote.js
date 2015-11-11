(function($){

	var Demote = {
			moduleName: "Demote",
			selectedRule:  null,
			selectedRuleItemPage: 1,
			selectedRuleItemTotal: 0,
			selectedRuleStatus: null,

			rulePage: 1,
			rulePageSize: 15,
			ruleItemPageSize: 6,
			ruleFilterText: "",
			dateMinDate: 0,
			dateMaxDate: "+1Y",
			zeroCountHTMLCode: "&#133;",
			defaultRuleItemDisplay: "tileView",
			lockedItemDisplayText: "Item is locked",

			removeExpiryDateConfirmText: "Expiry date for this item will be removed. Continue?",
			removeRuleItemConfirmText: "Item will be removed from this rule. Continue?",
			clearRuleItemConfirmText: "All items associated to this rule will be removed. Continue?",
			addForceAddItem: " is not part of natural search results. Continue?",

			getRuleList: function(){
				var self = this;

				$("#rulePanel").sidepanel({
					moduleName: self.moduleName,
					fieldName: "keyword",
					showAddButton: allowModify,
					page: self.rulePage,
					pageSize: self.rulePageSize,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, keyword, page){
						self.ruleFilterText = keyword;
						self.rulePage = page;
						StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
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
						DemoteServiceJS.getTotalProductInRule(item.model["ruleId"],{
							callback: function(count){
								if (count > 0) item.ui.find("#itemLinkValue").html("(" + count + ")");
								item.ui.find("#itemLinkValue").on({
									click: function(e){
										self.setRule(item.model);
									}
								});
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

					itemAddCallback: function(base, keyword){
						StoreKeywordServiceJS.getKeyword(keyword,{
							callback : function(data){
								if(data==null){
									StoreKeywordServiceJS.addKeyword(keyword,{
										callback : function(data){
											showActionResponse(data==null?0:1, "add", keyword);
											if(data!=null){
												base.getList(keyword, 1);
												self.setRule(data);
											}
										}
									});
								}
								else {
									jAlert("Keyword <strong>" + keyword + "</strong> already exists.", "Duplicate Record");
								}
							}
						});
					},

					pageChangeCallback: function(page){
						self.rulePage = page;
					}
				});
			},

			getFacetItemType: function(item){
				var $condition = item.condition;
				var type = "";

				if (!$condition["CNetFilter"] && !$condition["IMSFilter"]){
					type="facet";
				}else if($condition["CNetFilter"]){
					type="cnet";
				}else if($condition["IMSFilter"]){
					type="ims";
				}
				return type;
			},

			setRuleItemValues: function(li, item){
				var $li = li;
				var $item = item;
				var self = this;

				var PART_NUMBER = $item["memberTypeEntity"] === "PART_NUMBER";
				var FACET = $item["memberTypeEntity"] === "FACET";
				var id = $.formatAsId($item["memberId"]);

				$li.attr("id", id);
				$li.find(".sortOrderTextBox").val($item["location"]);

				if(PART_NUMBER){
					//$li.find(".manufacturer").html($item["manufacturer"]);
					$li.find(".name").html($item["name"]);
					$li.find("#sku,#mfrpn").show();
					$li.find(".sku").html($item["edp"]);
					$li.find(".mfrpn").html($item["mfrPN"]);
				}

				if(FACET){
					var maxLength = 120;
					var condition = $item.condition["readableString"];
					if(condition != null && condition.length > maxLength) {
						condition = condition.substring(0, maxLength) + "...";
					}
					$li.find(".name").html($("<a>").text(condition)).attr('title', $item.condition["readableString"]);
					
					// $li.find(".name").html($("<a>").text($item.condition["readableString"]));
					$li.find(".name > a").off().on({
						click:function(e){
							$(this).addproduct({
								moduleName: "Demote",
								type: self.getFacetItemType(e.data.item),
								locked: e.data.locked,
								newRecord: false,
								item: $.extend(true, {}, e.data.item),
								showPosition: true,
								maxPosition: self.selectedRuleItemTotal,
								updateFacetItemCallback: function(memberId, position, expiryDate, comment, selectedFacetFieldValues, api){
									api.hide();
									var updateFacetItem = function() {
										DemoteServiceJS.updateFacet(self.selectedRule["ruleId"], memberId, position, comment, expiryDate,  selectedFacetFieldValues, {
											callback: function(data){
												api.destroy();
												showActionResponse(data, "update", (e.data.item["memberTypeEntity"] === "FACET" ? 
														"Rule " + self.getFacetRuleTypeLabel(item) + " Item: " + e.data.item.condition["readableString"] : 
														"Product ID#: " + e.data.item["edp"]));
												self.populateRuleItem(self.selectedRuleItemPage);
											},
										});
									};
									RedirectServiceJS.convertMapToRedirectRuleCondition(selectedFacetFieldValues, {
										preHook: function(){ 
											self.preShowRuleContent();
										},
										callback: function(data) {
											var readableString = data.readableString;
											ElevateServiceJS.isItemInNaturalResult(self.selectedRule["ruleId"], $.makeArray("0"), $.makeArray(data.conditionForSolr), {
												callback: function(data) {
													if (data["0"] === false) {
														jConfirm("The " + self.getFacetRuleTypeLabel(item) + " " + readableString + self.addForceAddItem, "Update " + self.getFacetRuleTypeLabel(item), function(result){
															if (result) {
																updateFacetItem();
															} else {
																self.postShowRuleContent();
																api.show();
															}
														});
													}
													else {
														updateFacetItem();
													}
												}
											});
										}
									});
								}
							});
						}
					},{locked: self.selectedRuleStatus["locked"] || !allowModify, item: $item});
				}

				if ($item["isExpired"]){
					$li.find(".validityDaysExpired").show();
					$li.find(".validityDays").empty();
				}else{
					$li.find(".validityDaysExpired").hide();
					$li.find(".validityDays").html($item["validityText"]);
				} 

				var formattedExpiryDate = $.toStoreFormat($item["expiryDate"], GLOBAL_dateFormat);
				
				if($.isBlank(formattedExpiryDate)){
					$li.find(".clearDate").hide();
				}else{
					$li.find(".validityDateTextBox").val(formattedExpiryDate);
					$li.find(".clearDate").show();
				};

				$li.find(".validityDateTextBox").prop({readonly: true}).datepicker({
					showOn: "both",
					minDate: self.dateMinDate,
					maxDate: self.dateMaxDate,
					changeMonth: true,
				    changeYear: true,
					buttonText: "Expiration Date",
					buttonImage: "../images/icon_calendar.png",
					buttonImageOnly: true,
					disabled: self.selectedRuleStatus["locked"] || !allowModify,
					onSelect: function(dateText, inst) {	
						if ($.toStoreFormat($item["expiryDate"], GLOBAL_dateFormat) !== dateText){
							self.updateValidityDate($item, "update", dateText);
						}
					}
				});

				$li.find('.clearDate').off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm(self.removeExpiryDateConfirmText, "Remove Field Value", function(result){
							if(result) self.updateValidityDate(e.data.item, "delete", "");
						});
					}
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify, item: $item});

				$li.find('.commentRuleItemIcon').off().on({
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
								DemoteServiceJS.addRuleComment(self.selectedRule["ruleId"], e.data.item["memberId"], comment, {
									callback: function(data){
										showActionResponse(data, "add comment", (e.data.item["memberTypeEntity"] === "FACET" ? "Rule " + self.getFacetRuleTypeLabel(e.data.item) + " Item: " + e.data.item.condition["readableString"] : "Product ID#: " + e.data.item["edp"]));
										base.getList(base.options.page);
									},
									preHook: function(){
										base.prepareList();
									}
								});
							}
						});
					}
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify, item: $item});
				
				
				$li.find('.auditRuleItemIcon').off().on({
					click: function(e){
						var itemId = e.data.item["memberId"];
						$(e.currentTarget).viewaudit({
							itemDataCallback: function(base, page){
								AuditServiceJS.getDemoteItemTrail(self.selectedRule["ruleId"], itemId, base.options.page, base.options.pageSize, {
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
				}, {item: $item});

				$li.find('.lastModifiedIcon').off().on({
					mouseenter: showLastModified 
				},{user: $item["lastModifiedBy"], date:$.toStoreFormat($item["lastModifiedDateTime"])});

				$li.find('.deleteRuleItemIcon').off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm(self.removeRuleItemConfirmText, "Delete Item", function(result){
							if(result){
								DemoteServiceJS.deleteItemInRule(self.selectedRule["ruleName"], e.data.item["memberId"], {	
									callback: function(code){
										showActionResponse(code, "delete", e.data.item["memberTypeEntity"] === "FACET" ? "Rule " + self.getFacetRuleTypeLabel(e.data.item) + " Item: " + e.data.item.condition["readableString"] : "Product ID#: " + e.data.item["edp"]);
										self.showRuleContent();
									},
									preHook: function(){
										self.preShowRuleContent();
									}
								});
							}
						});
					},
					mouseenter: showHoverInfo
				},{locked: self.selectedRuleStatus["locked"] || !allowModify, item:$item});

				if (self.selectedRuleStatus["locked"]){
					$li.find('.clearDate').hide();
					$li.find('.sortOrderTextBox').prop("readonly", true);
				}

				$li.find('.sortOrderTextBox').off().on({
					keypress:function(e){
						if (e.data.locked) return;

						var currentIndex = $.trim(($(this).parents("li.ruleItem").index()) + ((self.selectedRuleItemPage-1)*self.ruleItemPageSize));

						var code = (e.keyCode ? e.keyCode : e.which);

						if(code == 13) { 
							var destinationIndex = $.trim($(this).val());
							if($.isNumeric(destinationIndex) && currentIndex!=destinationIndex){
								if(destinationIndex > self.selectedRuleItemTotal){
									jAlert("Position value should be from 1 - " + (self.selectedRuleItemTotal) + ".", "Max Value Exceeded");
								}else{
									self.setRuleItemPosition(e.data.item, destinationIndex);
								}
							}
						}else{
							if (((code==48 || code==96) && $.isBlank($(e.target).val())) || (code > 31 && (code < 48 || code > 57))){
								jAlert("Position value should be a number from 1 - " + self.selectedRuleItemTotal + ".", "Invalid Input Type");
								return false;
							}
						}
						return true;
					},
					focus:function(e){
						if (e.data.locked) return; 
						showMessage(this, "Press <strong>ENTER</strong> to update<br/><strong>Position Range:</strong> 1 - " + self.selectedRuleItemTotal);
						if (parseInt($(this).val()) == parseInt(e.data.item["location"])) $(this).val("");
					},
					blur:function(e){
						if (e.data.locked) return; 
						$(this).val(e.data.item["location"]);   
					},	
					mouseenter: showHoverInfo
				},{locked: self.selectedRuleStatus["locked"] || !allowModify, item: $item});

				setTimeout(function(){	
					if (PART_NUMBER){
						if ($.isBlank($item["edp"])){
							$li.find(".itemImg").prop("src",GLOBAL_contextPath + '/images/padlock_img.jpg'); 
							$li.find(".name").html('<p><font color="red">Product Id:</font> ' + item["edp"] + '<br/>Not available in the search server</p>');
							$li.find(".manufacturer").html(self.lockedItemDisplayText);
							$li.find(".sku, .mfrpn").html("Unavailable");
							return;
						}

						$li.find("img.itemImg").prop("src",item['imagePath']).off().on({
							error:function(){ 
								$(this).unbind("error").prop("src", GLOBAL_contextPath + '/images/no-image.jpg'); 
							}
						});
					}

					if (FACET){
						var imagePath = "";

						switch(self.getFacetItemType($item)){
						case "ims" : imagePath = "ims_img.jpg"; break;
						case "cnet" : imagePath = "productSiteTaxonomy_img.jpg"; break;
						case "facet" : imagePath = "facet_img.jpg"; break;
						}

						if($.isNotBlank(imagePath))
							$li.find(".itemImg").prop("src",GLOBAL_contextPath + '/images/' + imagePath); 
					}

				}, 10);
			},

			updateValidityDate: function(item, action, dateText){
				var self = this;
				var $item = item;
				DemoteServiceJS.updateExpiryDate(self.selectedRule["ruleName"], $item["memberId"], dateText, {
					callback: function(code){
						showActionCallBackJobResponse(code, action, "expiry date of " + ($item["memberTypeEntity"] === "FACET" ? "Rule " + self.getFacetRuleTypeLabel($item) + " Item: " + $item.condition["readableString"] : "Product ID#: " + $item["edp"]),
								function(){if(code==1) self.populateRuleItem(self.selectedRuleItemPage);});						
					}
				});
			},

			preShowRuleContent: function(){
				var self = this;
				$("#preloader").show();
				$("#ruleItemPagingTop, #ruleItemPagingBottom").empty();
				$("#ruleItemDisplayOptions").hide();
				$("#noSelected, #ruleSelected, #addRuleItemContainer").fadeOut("slow", function(){
					$("#titleText").html(self.moduleName);
					$("#titleHeader").empty();
				});
			},

			postShowRuleContent: function(){
				var self = this;
				$("#preloader, #noSelected").hide();
				var $selector = $("#ruleSelected, #addRuleItemContainer");

				if (self.selectedRuleStatus["locked"] || !allowModify){
					$selector = $("#ruleSelected");
				}

				$selector.fadeIn("slow", function(){
					$("#titleText").html(self.moduleName + " for ");
					$("#titleHeader").text(self.selectedRule["ruleName"]);
				});
			},

			populateRuleItem: function(page){
				var self = this;
				self.selectedRuleItemPage = page;
				self.preShowRuleContent();

				$("#submitForApproval").rulestatusbar({
					moduleName: self.moduleName,
					ruleType: "Demote",
					rule: self.selectedRule,
					enableVersion: true,
					authorizeRuleBackup: allowModify,
					authorizeSubmitForApproval: allowModify,
					postRestoreCallback: function(base, rule){
						base.api.destroy();
						self.showRuleContent();
					},
					beforeSubmitForApprovalRequest: function(ruleStatus){
						self.preShowRuleContent();
					},
					afterSubmitForApprovalRequest: function(ruleStatus){
						self.selectedRuleStatus = ruleStatus;
						self.showRuleContent();
					},
					afterRuleStatusRequest: function(ruleStatus){
						self.selectedRuleStatus = ruleStatus;
						self.selectedRuleItemPage = $.isNotBlank(page) && $.isNumeric(page) ? page : 1;
						self.selectedRuleItemTotal = 0;
						var $ul = $("ul#ruleItemHolder");

						DemoteServiceJS.getProducts(self.getRuleItemFilter(), self.selectedRule["ruleName"], self.selectedRuleItemPage, self.ruleItemPageSize, {
							callback: function(data){
								self.selectedRuleItemTotal = data.totalSize;
								$ul.find('li.ruleItem:not(#ruleItemPattern)').remove();

								var list = data.list;

								if(self.getRuleItemFilter()==="all"){
									var totalText = self.selectedRuleItemTotal==0? self.zeroCountHTMLCode:  "(" + self.selectedRuleItemTotal + ")";
									// $('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemLink a').html(totalText);
									$("#itemListing").find("tr div.itemText a").filter(function() {return $(this).text() == self.selectedRule['ruleId']}).closest('.itemHolder').find('.itemLink a').html(totalText);
								}
								
								if(self.selectedRuleItemTotal == 0 && self.getRuleItemFilter()==="all"){
									$('#ruleItemDisplayOptions').hide(); 
								}else{
									$('#ruleItemDisplayOptions').show();
									self.addRuleItemOptionListener();
								}

								if(self.selectedRuleItemTotal == 0){
									$("#optionSplitter").hide();
								}else{
									$("#optionSplitter").show();
								}

								$("#ruleItemPagingTop, #ruleItemPagingBottom").paginate({
									currentPage:page, 
									pageSize:self.ruleItemPageSize,
									totalItem:self.selectedRuleItemTotal,
									callbackText: function(itemStart, itemEnd, itemTotal){
										var selectedText = $.trim($("#filterDisplay").val()) != "all" ? " " + $("#filterDisplay option:selected").text(): "";
										return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + selectedText + " Items";
									},
									pageLinkCallback: function(e){ self.populateRuleItem(e.data.page); },
									nextLinkCallback: function(e){ self.populateRuleItem(e.data.page + 1); },
									prevLinkCallback: function(e){ self.populateRuleItem(e.data.page - 1); }
								});

								for (var i = 0; i < self.selectedRuleItemTotal; i++) {
									var $item = list[i];
									if($item != null){
										var $li = $ul.find('li#ruleItemPattern').clone();
										self.setRuleItemValues($li, $item);
										$li.show();
										$ul.append($li);
									}
								};

							},
							preHook:function(){
								self.preShowRuleContent();
							},
							postHook: function(){
								self.postShowRuleContent();
								$("a#addRuleItemIcon").off().on({
									click:function(e){
										$(this).addproduct({
											moduleName: "Demote",
											type: $('select#selectRuleItemType').val(),
											locked: self.selectedRuleStatus["locked"] || !allowModify,
											showPosition: true,
											maxPosition: self.selectedRuleItemTotal + 1,
											addProductItemCallback:function(position, expiryDate, comment, skus, api){
												api.hide();
												var addEdps = function() {
													DemoteServiceJS.addItemToRuleUsingPartNumber(self.selectedRule["ruleId"], position, expiryDate, comment, skus, {
														callback : function(code){
															api.destroy();
															showActionResponseFromMap(code, "add", "Multiple Rule Item Add", 
																"Please check for the following:<ol type='a' class='mar0 padL30'><li>Product(s) are already present in the list</li><li>Product(s) are actually searchable using the specified keyword.</li></ol>");
															self.populateRuleItem(self.selectedRuleItemPage);
														}
													});
												};
												
												var conditionForSolr = new Array();
												for (var i = 0; i < skus.length; i++) {
													conditionForSolr[i] = "DistSku:" + skus[i];
												}
												
												ElevateServiceJS.isItemInNaturalResult(self.selectedRule["ruleId"], skus, conditionForSolr, {
													preHook: function(){
														self.preShowRuleContent();
													},
													callback: function(data) {
														var forceAddEdps = new Array();
														for (var edp in data){
															if (data[edp] === false) {
																forceAddEdps.push(edp);
															}
														}
														if (forceAddEdps.length > 0) {
															var confirmMessage = "The following Products are not part of the natural search results: " + forceAddEdps.join() + ". Continue?";
															jConfirm(confirmMessage, "Multiple Rule Item Add", function(result){
																if (result) {
																	addEdps();
																} else {
																	api.show();
																	self.postShowRuleContent();
																}
															});
														}
														else {
															addEdps();
														}
													}
												});
												
												
											},
											addFacetItemCallback: function(position, expiryDate, comment, selectedFacetFieldValues, ruleType, api){
												api.hide();
												var addFacetItem = function() {
													DemoteServiceJS.addFacetRule(self.selectedRule["ruleId"], position, expiryDate, comment, selectedFacetFieldValues, {
														callback: function(data){
															api.destroy();
															showActionResponse(data, "add", "New Rule "+ ruleType +" Item");
															self.populateRuleItem(self.selectedRuleItemPage);
														}
													});
												};
												RedirectServiceJS.convertMapToRedirectRuleCondition(selectedFacetFieldValues, {
													preHook: function(){ 
														self.preShowRuleContent();
													},
													callback: function(data) {
														var readableString = data.readableString;
														ElevateServiceJS.isItemInNaturalResult(self.selectedRule["ruleId"], $.makeArray("0"), $.makeArray(data.conditionForSolr), {
															callback: function(data) {
																if (data["0"] === false) {
																	jConfirm("The " + ruleType + " " + readableString + self.addForceAddItem, "Add " + ruleType, function(result){
																		if (result) {
																			addFacetItem();
																		} else {
																			self.postShowRuleContent();
																			api.show();
																		}
																	});
																}
																else {
																	addFacetItem();
																}
															}
														});
													}
												});
											}
										});
									}
								});
							}
						});

					}
				});	
			},

			addRuleItemOptionListener: function(){
				var self = this;

				$("#filterDisplay").off().on({
					change: function(e){
						$.cookie('demote.filter' + $.formatAsId(self.selectedRule["ruleId"]),$(this).val(),{path:GLOBAL_contextPath});
						self.setRuleItemFilter();
					}
				});

				$("#tileViewIcon").off().on({click:function(e) {
					$.cookie('demote.display' + $.formatAsId(self.selectedRule["ruleId"]), 'tileView', {path:GLOBAL_contextPath});
					$("#listViewIcon").removeClass("active");
					self.setRuleItemDisplay();
					$(".qtip:visible").qtip('reposition');
				}});

				$("#listViewIcon").off().on({click:function(e) {
					$.cookie('demote.display' + $.formatAsId(self.selectedRule["ruleId"]), 'listView', {path:GLOBAL_contextPath});
					$("#tileViewIcon").removeClass("active");
					self.setRuleItemDisplay();
					$(".qtip:visible").qtip('reposition');
				}});

				$("#addRuleItemIcon").off().on({
					click: function(e){
						self.showAddProductItem(e);
					}, 
					mouseenter: showHoverInfo
				},{locked: self.selectedRuleStatus["locked"] || !allowModify});

				$("#clearRuleItemIcon").off().on({
					click: function(e){
						if(e.data.locked) return;

						jConfirm(self.clearRuleItemConfirmText, "Delete Item", function(result){
							if(result) 
								DemoteServiceJS.clearRule(self.selectedRule["ruleName"], {
									callback: function(code){
										showActionResponse(code, "clear", self.selectedRule["ruleName"]);
										self.showRuleContent();
									}
								});
						});

					},
					mouseenter: showHoverInfo,
				},{locked: self.selectedRuleStatus["locked"] || !allowModify});

				$("#downloadRuleItemIcon").download({
					headerText:"Download Demote",
					hasPageOption: true,
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;

						params["filename"] = e.data.filename;
						params["type"] = e.data.type;
						params["keyword"] = self.selectedRule["ruleName"];
						params["page"] = (e.data.page==="current") ? self.selectedRuleItemPage : e.data.page;
						params["filter"] = self.getRuleItemFilter();
						params["itemperpage"] = self.ruleItemPageSize;
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

			showRuleContent: function(){
				var self = this;
				self.getRuleList();

				if(self.selectedRule==null){
					$("#preloader").hide();
					$("#titleText").html(self.moduleName);
					return;
				}

				self.getRuleList();
				self.setRuleItemDisplay();
				self.setRuleItemFilter();
			},

			setRule: function(rule){
				var self = this;
				self.selectedRule = rule;
				$("#uploadFromExcel").remove();
				self.showRuleContent();
			},

			setRuleItemDisplay: function(){
				var self = this;

				$("#ruleItemContainer").removeClass("tileView").removeClass("listView");

				if ($.cookie('demote.display' + $.formatAsId(self.selectedRule["ruleId"]))==="listView" || $.cookie('demote.display'+ $.formatAsId(self.selectedRule["ruleId"]))==="tileView"){
					$("#ruleItemContainer").addClass($.cookie('demote.display' + $.formatAsId(self.selectedRule["ruleId"])));
					$("#" + $.cookie('demote.display' + $.formatAsId(self.selectedRule["ruleId"])) + "Icon").addClass("active");
				}else{
					$.cookie('demote.display' + $.formatAsId(self.selectedRule["ruleId"]), self.defaultRuleItemDisplay, {path:GLOBAL_contextPath});
					$("#ruleItemContainer").addClass(self.defaultRuleItemDisplay);
					$("#" + self.defaultRuleItemDisplay + "Icon").addClass("active");				
				}
			},

			setRuleItemFilter: function(value){
				var self = this;
				var selectedFilter = $.isNotBlank(value)? value : $.cookie('demote.filter' + $.formatAsId(self.selectedRule["ruleId"]));

				if ($.isNotBlank(selectedFilter)){
					$("#filterDisplay").val(selectedFilter);
				}else{
					$.cookie('demote.filter' + $.formatAsId(self.selectedRule["ruleId"]), "all" ,{path:GLOBAL_contextPath});
					$("#filterDisplay").val("all");
				}

				self.populateRuleItem();
			},

			setRuleItemPosition: function(item, position) {
				var self = this;
				var $item = item;

				DemoteServiceJS.update(self.selectedRule["ruleName"], $item["memberId"], position, null, {
					callback : function(code){
						var updateMessage = ($item["memberTypeEntity"] === "FACET" ? "Rule " + self.getFacetRuleTypeLabel(item) + " Item: " + $item.condition["readableString"] : "Product ID#: " + $item["edp"]);
						self.populateRuleItem();
						showActionResponse(code, "update position", updateMessage);
					},
					preHook: function(){
						self.preShowRuleContent();
					}
				});
			},

			getFacetRuleTypeLabel: function (item) {
				if (item.condition.IMSFilter) {
					return "IMS Categories";
				} else if (item.condition.CNetFilter) {
					return 'Product Site Taxonomy';
				} else {
					return "Facet";
				}
			},

			getRuleItemFilter: function(){
				var self = this;
				var cookieFilter = $.trim($.cookie('demote.filter' + $.formatAsId(self.selectedRule["ruleId"])));
				var activefilter = $.isBlank(cookieFilter)? $("#filterDisplay").val() : cookieFilter;
				return $.isBlank(activefilter) ? "all" : activefilter;
			},

			init : function() {
				var self = this;
				self.showRuleContent();
			}
	};

	$(document).ready(function() {
		Demote.init();
	});	

})(jQuery);	
