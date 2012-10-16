(function($){

	var Exclude = {
			moduleName: "Exclude",
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

			getRuleList: function(){
				var self = this;

				$("#rulePanel").sidepanel({
					fieldId: "keywordId",
					fieldName: "keyword",
					headerText : "Keyword",
					searchText : "Enter Keyword",
					showAddButton: allowModify,
					page: self.rulePage,
					pageSize: self.rulePageSize,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, keyword, page){
						self.ruleFilterText = keyword;
						self.rulePage = page;
						StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
							callback: function(data){
								base.populateList(data);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemOptionCallback: function(base, id, name, model){

						var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));

						ExcludeServiceJS.getTotalProductInRule(id,{
							callback: function(count){

								var totalText = (count == 0) ? self.zeroCountHTMLCode: "(" + count + ")"; 
								base.$el.find(selector + ' div.itemLink a').html(totalText);

								base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').on({
									click: function(e){
										self.setRule(model);
									}
								});
							},
							preHook: function(){ 
								base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
							}
						});

						DeploymentServiceJS.getRuleStatus(self.moduleName, id, {
							callback:function(data){
								base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
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
					$li.find(".manufacturer").html($item["manufacturer"]);
					$li.find(".name").html($item["name"]);
					$li.find("#sku,#mfrpn").show();
					$li.find(".sku").html($item["dpNo"]);
					$li.find(".mfrpn").html($item["mfrPN"]);
				}

				if(FACET){

					$li.find(".name").html($("<a>").html($item.condition["readableString"]));
					$li.find(".name > a").off().on({
						click:function(e){
							$(this).addproduct({
								type: self.getFacetItemType(e.data.item),
								locked: e.data.locked,
								newRecord: false,
								item: e.data.item,
								updateFacetItemCallback: function(memberId, position, expiryDate, comment, selectedFacetFieldValues){
									ExcludeServiceJS.updateExcludeFacet(self.selectedRule["ruleId"], memberId, comment, expiryDate,  selectedFacetFieldValues, {
										callback: function(data){
											showActionResponse(data, "update", (e.data.item["memberTypeEntity"] === "FACET" ? "Rule Facet Item: " + e.data.item.condition["readableString"] : $.isBlank(e.data.item["dpNo"])? "Product Id#: " + e.data.item["edp"] : "SKU#: " + e.data.item["dpNo"]));
											self.populateRuleItem(self.selectedRulePage);
										},
										preHook: function(){ 
											self.preShowRuleContent();
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

				var formattedExpiryDate = $item["formattedExpiryDate"];
				if($.isBlank(formattedExpiryDate)){
					$li.find(".clearDate").hide();
				}else{
					$li.find(".validityDateTextBox").val(formattedExpiryDate);
					$li.find(".clearDate").show();
				};

				$li.find(".validityDateTextBox").datepicker({
					showOn: "both",
					minDate: self.dateMinDate,
					maxDate: self.dateMaxDate,
					buttonText: "Expiration Date",
					buttonImage: "../images/icon_calendar.png",
					buttonImageOnly: true,
					disabled: self.selectedRuleStatus["locked"] || !allowModify,
					onSelect: function(dateText, inst) {	
						if ($item["formattedExpiryDate"] !== dateText){
							self.updateValidityDate($item, dateText);
						}
					}
				});

				$li.find('.clearDate').off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm(self.removeExpiryDateConfirmText, "Remove Field Value", function(result){
							if(result) self.updateValidityDate(e.data.item, "");
						});
					}
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify, item: $item});

				$li.find('.commentRuleItemIcon').off().on({
					click: function(e){
						$(e.currentTarget).comment({
							showAddComment: true,
							locked: e.data.locked,
							itemDataCallback: function(base, page){
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
							},
							itemAddComment: function(base, comment){
								ExcludeServiceJS.addRuleComment(self.selectedRule["ruleId"], e.data.item["memberId"], comment, {
									callback: function(data){
										showActionResponse(data, "add comment", (e.data.item["memberTypeEntity"] === "FACET" ? "Rule Facet Item: " + e.data.item.condition["readableString"] : $.isBlank(e.data.item["dpNo"])? "Product Id#: " + e.data.item["edp"] : "SKU#: " + e.data.item["dpNo"]));
										if(data==1){
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
								AuditServiceJS.getExcludeItemTrail(self.selectedRule["ruleId"], itemId, base.options.page, base.options.pageSize, {
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
				},{user: $item["lastModifiedBy"], date:$item["formattedLastModifiedDate"]});

				$li.find('.deleteRuleItemIcon').off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm(self.removeRuleItemConfirmText, "Delete Item", function(result){
							if(result){
								ExcludeServiceJS.deleteItemInRule(self.selectedRule["ruleName"], e.data.item["memberId"], {
									callback: function(code){
										showActionResponse(code, "delete", e.data.item["memberTypeEntity"] === "FACET" ? "Rule Facet Item: " + e.data.item.condition["readableString"] : 
											$.isBlank(e.data.item["dpNo"])? "Product Id#: " + e.data.item["edp"] : "SKU#: " + e.data.item["dpNo"]);
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

				setTimeout(function(){	
					if (PART_NUMBER){
						if ($.isBlank($item["dpNo"])){
							$li.find(".itemImg").prop("src",GLOBAL_contextPath + '/images/padlock_img.jpg'); 
							$li.find(".name").html('<p><font color="red">Product Id:</font> ' + item["edp"] + '<br/>This is no longer available in the search server you are connected</p>');
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

			updateValidityDate: function(item, dateText){
				var self = this;
				var $item = item;
				ExcludeServiceJS.updateExpiryDate(self.selectedRule["ruleName"], $item["memberId"], dateText, {
					callback: function(code){
						showActionResponse(code, "update", "expiry date of " + ($item["memberTypeEntity"] === "FACET" ? "Rule Facet Item: " + $item.condition["readableString"] : $.isBlank($item["dpNo"])? "Product Id#: " + $item["edp"] : "SKU#: " + $item["dpNo"]));
						if(code==1) self.populateRuleItem(self.selectedRulePage);
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
					$("#titleHeader").html(self.selectedRule["ruleName"]);
				});
			},

			populateRuleItem: function(page){
				var self = this;
				self.selectedRuleItemPage = page;
				self.preShowRuleContent();

				$("#submitForApproval").rulestatus({
					moduleName: self.moduleName,
					ruleType: "Exclude",
					rule: self.selectedRule,
					enableVersion:true,
					authorizeRuleBackup: true,
					viewAuditCallback: function(target){
						$(target).viewaudit({
							getDataCallback: function(base, page){
								CommentServiceJS.getComment(self.moduleName, self.selectedRule["ruleId"], page, 5, {
									callback: function(data){}
								});
							}
						});
					},
					
					authorizeSubmitForApproval: allowModify,
					afterSubmitForApprovalRequest: function(ruleStatus){
						self.populateRuleItem(page);
					},
					afterRuleStatusRequest: function(ruleStatus){
						self.selectedRuleStatus = ruleStatus;
						self.selectedRulePage = $.isNotBlank(page) && $.isNumeric(page) ? page : 1;
						self.selectedRuleItemTotal = 0;
						var $ul = $("ul#ruleItemHolder");

						ExcludeServiceJS.getProducts(self.getRuleItemFilter(), self.selectedRule["ruleName"], self.selectedRulePage, self.ruleItemPageSize, {
							callback: function(data){
								self.selectedRuleItemTotal = data.totalSize;
								$ul.find('li.ruleItem:not(#ruleItemPattern)').remove();

								var list = data.list;

								if(self.getRuleItemFilter()==="all"){
									var totalText = self.selectedRuleItemTotal==0? self.zeroCountHTMLCode:  "(" + self.selectedRuleItemTotal + ")";
									$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemLink a').html(totalText);
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
											type: $('select#selectRuleItemType').val(),
											locked: self.selectedRuleStatus["locked"] || !allowModify,
											addProductItemCallback:function(position, expiryDate, comment, skus){
												ExcludeServiceJS.addItemToRuleUsingPartNumber(self.selectedRule["ruleId"], expiryDate, comment, skus, {
													callback : function(code){
														showActionResponseFromMap(code, "add", skus, "Please check for the following:\n a) SKU(s) are already present in the list\n b) SKU(s) are actually searchable using the specified keyword.");
														self.populateRuleItem(self.selectedRulePage);
													},
													preHook: function(){ 
														self.preShowRuleContent();
													}
												});		
											},
											addFacetItemCallback: function(position, expiryDate, comment, selectedFacetFieldValues){
												ExcludeServiceJS.addFacetRule(self.selectedRule["ruleId"], expiryDate, comment, selectedFacetFieldValues, {
													callback: function(data){
														showActionResponse(data, "add", "New Rule Facet Item");
														self.populateRuleItem();
													},
													preHook: function(){ 
														self.preShowRuleContent();
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
						$.cookie('exclude.filter' + $.formatAsId(self.selectedRule["ruleId"]),$(this).val(),{path:GLOBAL_contextPath});
						self.setRuleItemFilter();
					}
				});

				$("#tileViewIcon").off().on({click:function(e) {
					$.cookie('exclude.display' + $.formatAsId(self.selectedRule["ruleId"]), 'tileView', {path:GLOBAL_contextPath});
					$("#listViewIcon").removeClass("active");
					self.setRuleItemDisplay();
				}});

				$("#listViewIcon").off().on({click:function(e) {
					$.cookie('exclude.display' + $.formatAsId(self.selectedRule["ruleId"]), 'listView', {path:GLOBAL_contextPath});
					$("#tileViewIcon").removeClass("active");
					self.setRuleItemDisplay();
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
								ExcludeServiceJS.clearRule(self.selectedRule["ruleName"], {
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
					headerText:"Download Exclude",
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
							urlParams += (key + '=' + params[key]);
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
				self.showRuleContent();
			},

			setRuleItemDisplay: function(){
				var self = this;

				$("#ruleItemContainer").removeClass("tileView").removeClass("listView");

				if ($.cookie('exclude.display' + $.formatAsId(self.selectedRule["ruleId"]))==="listView" || $.cookie('exclude.display'+ $.formatAsId(self.selectedRule["ruleId"]))==="tileView"){
					$("#ruleItemContainer").addClass($.cookie('exclude.display' + $.formatAsId(self.selectedRule["ruleId"])));
					$("#" + $.cookie('exclude.display' + $.formatAsId(self.selectedRule["ruleId"])) + "Icon").addClass("active");
				}else{
					$.cookie('exclude.display' + $.formatAsId(self.selectedRule["ruleId"]), self.defaultRuleItemDisplay, {path:GLOBAL_contextPath});
					$("#ruleItemContainer").addClass(self.defaultRuleItemDisplay);
					$("#" + self.defaultRuleItemDisplay + "Icon").addClass("active");				
				}
			},

			setRuleItemFilter: function(value){
				var self = this;
				var selectedFilter = $.isNotBlank(value)? value : $.cookie('exclude.filter' + $.formatAsId(self.selectedRule["ruleId"]));

				if ($.isNotBlank(selectedFilter)){
					$("#filterDisplay").val(selectedFilter);
				}else{
					$.cookie('exclude.filter' + $.formatAsId(self.selectedRule["ruleId"]), "all" ,{path:GLOBAL_contextPath});
					$("#filterDisplay").val("all");
				}

				self.populateRuleItem();
			},

			getRuleItemFilter: function(){
				var self = this;
				var cookieFilter = $.trim($.cookie('exclude.filter' + $.formatAsId(self.selectedRule["ruleId"])));
				var activefilter = $.isBlank(cookieFilter)? $("#filterDisplay").val() : cookieFilter;
				return $.isBlank(activefilter) ? "all" : activefilter;
			},

			init : function() {
				var self = this;
				self.showRuleContent();
			}
	};

	$(document).ready(function() {
		Exclude.init();
	});	

})(jQuery);	
