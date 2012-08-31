(function($){

	var Elevate = {
			moduleName: "Elevate",
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

						ElevateServiceJS.getTotalProductInRule(id,{
							callback: function(count){

<<<<<<< HEAD
								var totalText = (count == 0) ? self.zeroCountHTMLCode: "(" + count + ")"; 
								base.$el.find(selector + ' div.itemLink a').html(totalText);
=======
					contentHolder.find("#addItemDate_1").datepicker({
						showOn: "both",
						minDate: dateMinDate,
						maxDate: dateMaxDate,
						buttonText: "Expiration Date",
						buttonImage: "../images/icon_calendar.png",
						buttonImageOnly: true
					});
					
					contentHolder.find("#clearBtn").on({
						click: function(evt){
							contentHolder.find("input,textarea").val("");
						}
					});
					
					contentHolder.find("#addItemPosition").on({
						keypress:function(e){
							var charCode = (e.which) ? e.which : e.keyCode;
							if (charCode > 31 && (charCode < 48 || charCode > 57))
								return false;
						},
						keydown:function(e){
							var charCode = (e.which) ? e.which : e.keyCode;
							var ctrlDown = e.ctrlKey||e.metaKey ;
							if (ctrlDown) {
								return false;
							}
						},
						contextmenu:function(e){
							return false;
						}
					});
>>>>>>> refs/remotes/origin/sprint_6_revamp_alert

<<<<<<< HEAD
								base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').on({
									click: function(e){
										self.setRule(model);
=======
					contentHolder.find("#addItemToRuleBtn").on({
						click: function(evt){

							var commaDelimitedNumberPattern = /^\s*\d+\s*(,?\s*\d+\s*)*$/;

							var skus = $.trim(contentHolder.find("#addItemDPNo").val());
							var sequence = $.trim(contentHolder.find("#addItemPosition").val());
							var expDate = $.trim(contentHolder.find("#addItemDate_1").val());
							var comment = $.trim(contentHolder.find("#addItemComment").val().replace(/\n\r?/g, '<br />'));
							var today = new Date();
							//ignore time of current date 
							today.setHours(0,0,0,0);
							
							if ($.isBlank(skus)) {
								jAlert("There are no SKUs specified in the list.","Elevate");
							}
							else if (!commaDelimitedNumberPattern.test(skus)) {
								jAlert("List contains an invalid SKU.","Elevate");
							}							
							else if (!$.isBlank(expDate) && !$.isDate(expDate)){
								jAlert("Invalid date specified.","Elevate");
							}
							else if(today.getTime() > new Date(expDate).getTime())
								jAlert("Start date cannot be earlier than today.","Elevate");
							else if (!isXSSSafe(comment)){
								jAlert("Invalid comment. HTML/XSS is not allowed.","Elevate");
							}
							else {								
								ElevateServiceJS.addItemToRuleUsingPartNumber(selectedRule.ruleId, sequence, expDate, comment, skus.split(/[\s,]+/), {
									callback : function(code){
										showActionResponseFromMap(code, "add", skus, "Please check for the following:\n a) SKU(s) are already present in the list\n b) SKU(s) are actually searchable using the specified keyword.");
										showElevate();
									},
									preHook: function(){ 
										prepareElevate();
>>>>>>> refs/remotes/origin/sprint_6_revamp_alert
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

<<<<<<< HEAD
					pageChangeCallback: function(page){
						self.rulePage = page;
=======
				if(getItemFilter()==="all"){
					var totalText = selectedRuleItemTotal==0? zeroCountHTMLCode:  "(" + selectedRuleItemTotal + ")";
					$('#itemPattern' + $.escapeQuotes($.formatAsId(selectedRule.ruleId)) + ' div.itemLink a').html(totalText);
				}

				$('#sortable-bigbets li:not(#sItemPattern)').remove();
				for (var i = 0; i < selectedRuleItemTotal; i++) {
					item = list[i];
					if(item!=null){
						id = $.formatAsId(item["edp"]);
						dwr.util.cloneNode("sItemPattern", { idSuffix:id });
						$("#sItemPattern" + id).show(); 		
						setItemValues(item);
					}
				};

				showPaging(page);
				showDisplayOption();
			},
			postHook: function(){
				$("#preloader").hide();
				$("#submitForApproval").show();
				$("#elevate").show();

				$("#titleText").html(moduleName + " for ");
				$("#titleHeader").html(selectedRule.ruleName);
				$("#addItemHolder").show();
			}
		});
	};

	var removeExpiryDate = function(e){
		var data = e.data;
		if (!data.locked && allowModify && confirm(removeExpiryDateConfirmText)){
			var dateText = "";
			ElevateServiceJS.updateExpiryDate(selectedRule.ruleName, data.item["edp"], dateText, {
				callback: function(code){
					showActionResponse(code, "update", "expiry date of SKU#: " + data.item["dpNo"]);
					if(code==1) showElevate();
				}
			});
		}
	};
	
	var deleteItemInRule = function(e){
		var data = e.data;
		if (!data.locked && allowModify && confirm(deleteItemInRuleConfirmText)){
			ElevateServiceJS.deleteItemInRule(selectedRule.ruleName, data["edp"], {
				callback: function(code){
					showActionResponse(code, "delete", $.isBlank(data.item["dpNo"])? "Product Id#: " + data.item["edp"] : "SKU#: " + data.item["dpNo"]);
					showElevate();
				},
				preHook: function(){
					prepareElevate();
				}
			});
		}
	};

	var updateRuleItemPosition = function(edp, destinationIndex, dpNo) {
		ElevateServiceJS.updateElevate(selectedRule.ruleName,edp,destinationIndex, {
			callback : function(code){
				showActionResponse(code, "update position", $.isBlank(dpNo)? "Product Id#: " + edp : "SKU#: " + dpNo);
				showElevate();
			},
			preHook: function(){
				prepareElevate();
			}
		});
	};

	var setItemValues = function(item){
		var id = $.formatAsId(item["edp"]); 
		
		$("#sItemMan" + id).html(item["manufacturer"]);
		$("#sItemName" + id).html(item["name"]);
		$("#sItemDPNo" + id).html(item["dpNo"]);
		$("#sItemMfrPN" + id).html(item["mfrPN"]);

		$("#sItemModBy" + id).html(item["lastModifiedBy"]);
		$("#sItemModDate" + id).html(item["formattedLastModifiedDate"]);
		$("#sItemValidityText" + id).html(item["validityText"]);
		$("#sItemPosition" + id).val(item["location"]);

		if (item["isExpired"]) $("#sItemValidityText" + id).html('<img src="../images/expired_stamp50x16.png">');
		
		if ($.isBlank(item["validityText"])) $('#removeExpiryDateIcon' + id).hide();

		if (selectedRuleStatus.locked)
			$('#sItemPosition' + id).attr("readonly", "readonly");

		$('#sItemPosition' + id).on({
			keypress:function(e){
				if (e.data.locked || !allowModify) return;

				var currentIndex = $.trim(($(this).parent("li").index()+1) + ((selectedRuleItemPage-1)*ruleItemPageSize));

				var code = (e.keyCode ? e.keyCode : e.which);

				if(code == 13) { 
					var destinationIndex = $.trim($(this).val());
					if($.isNumeric(destinationIndex) && currentIndex!=destinationIndex){
						if(destinationIndex > selectedRuleItemTotal){
							jAlert("Maximum allowed value is " + (selectedRuleItemTotal),"Elevate");
						}else{
							updateRuleItemPosition(item["edp"], destinationIndex, item["dpNo"]);
						}
					}
				}else{
					if (((code==48 || code==96) && $.isBlank($(e.target).val())) || (code > 31 && (code < 48 || code > 57))){
						jAlert("Should be a positive number not greater than " + selectedRuleItemTotal,"Elevate");
						return false;
					}
				}
				return true;
			},
			focus:function(e){
				if (e.data.locked || !allowModify) return; 
				if ($(this).val()==item["location"]) $(this).val("");
			},
			blur:function(e){
				if (e.data.locked || !allowModify) return; 
				$(this).val(item["location"]);   
			},	
			mouseenter: showHoverInfo
		},{locked: selectedRuleStatus.locked || !allowModify});
	
		$('#removeExpiryDateIcon' + id).on({
			click: removeExpiryDate
		}, {locked: selectedRuleStatus.locked || !allowModify, type:moduleName, item: item, name: selectedRule.ruleName});
		
		$('#commentIcon' + id).on({
			click: showCommentList
		}, {locked: selectedRuleStatus.locked || !allowModify, type:moduleName, item: item, name: selectedRule.ruleName});

		$('#auditIcon' + id).on({
			click: showAuditList
		}, {locked: selectedRuleStatus.locked || !allowModify, type:moduleName, item: item, name: selectedRule.ruleName});

		$('#sItemDelete' + id).off().on({
			click: deleteItemInRule,
			mouseenter: showHoverInfo
		},{locked: selectedRuleStatus.locked || !allowModify, edp:item["edp"], item:item});

		$("#sItemExpDate" + id).val(item["formattedExpiryDate"]);
		
		$("input#sItemExpDate" + id).datepicker({
			showOn: "both",
			minDate: dateMinDate,
			maxDate: dateMaxDate,
			buttonText: "Expiration Date",
			buttonImage: "../images/icon_calendar.png",
			buttonImageOnly: true,
			disabled: selectedRuleStatus.locked || !allowModify,
			onSelect: function(dateText, inst) {	
				if (item["formattedExpiryDate"] != dateText){
					ElevateServiceJS.updateExpiryDate(selectedRule.ruleName,item["edp"], dateText, {
						callback: function(code){
							showActionResponse(code, "update", "expiry date of SKU#: " + item["dpNo"]);
							if(code==1) showElevate();
						}
					});
				}
			}
		});
		
		setTimeout(function(){		
			// Product is no longer visible in the setting
			if ($.isBlank(item["dpNo"])){
				$("#sItemImg" + id).attr("src","../images/padlock_img.jpg"); 
				$("#sItemMan" + id).html(lockedItemDisplayText);
				$("#sItemDPNo" + id).html("Unavailable");
				$("#sItemMfrPN" + id).html("Unavailable");
				$("#sItemName" + id).html('<p><font color="red">Product Id:</font> ' + item["edp"] + '<br/>This is no longer available in the search server you are connected</p>');
			}
			else{
				$("#sItemImg" + id).prop("src",item['imagePath']).off().on({
					error:function(){ 
						$(this).unbind("error").attr("src", "../images/no-image.jpg"); 
>>>>>>> refs/remotes/origin/sprint_6_revamp_alert
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
								showPosition: true,
								updateFacetItemCallback: function(memberId, position, expiryDate, comment, selectedFacetFieldValues){
									ElevateServiceJS.updateElevateFacet(self.selectedRule["ruleId"], memberId, position, comment, expiryDate,  selectedFacetFieldValues, {
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
								CommentServiceJS.addRuleItemComment(self.moduleName, e.data.item["memberId"], comment, {
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
						var itemId = e.data.item["memberTypeEntity"] === "PART_NUMBER"? e.data.item["edp"] : e.data.item["memberId"];
						$(e.currentTarget).viewaudit({
							itemDataCallback: function(base, page){
								AuditServiceJS.getElevateItemTrail(self.selectedRule["ruleId"], itemId, base.options.page, base.options.pageSize, {
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
								ElevateServiceJS.deleteItemInRule(self.selectedRule["ruleName"], e.data.item["memberId"], {	
									callback: function(code){
										showActionResponse(code, "delete", e.data.item["memberTypeEntity"] === "FACET" ? "Rule Facet Item: " + e.data.item.condition["readableString"] : $.isBlank(e.data.item["dpNo"])? "Product Id#: " + e.data.item["edp"] : "SKU#: " + e.data.item["dpNo"]);
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

						var currentIndex = $.trim(($(this).parent("li").index()+1) + ((self.selectedRuleItemPage-1)*self.ruleItemPageSize));

						var code = (e.keyCode ? e.keyCode : e.which);

						if(code == 13) { 
							var destinationIndex = $.trim($(this).val());
							if($.isNumeric(destinationIndex) && currentIndex!=destinationIndex){
								if(destinationIndex > self.selectedRuleItemTotal){
									jAlert("Elevation value should be from 1 - " + (self.selectedRuleItemTotal) + ".", "Max Value Exceeded");
								}else{
									self.setRuleItemPosition(e.data.item, destinationIndex);
								}
							}
						}else{
							if (((code==48 || code==96) && $.isBlank($(e.target).val())) || (code > 31 && (code < 48 || code > 57))){
								jAlert("Elevation value should be a number from 1 - " + self.selectedRuleItemTotal + ".", "Invalid Input Type");
								return false;
							}
						}
						return true;
					},
					focus:function(e){
						if (e.data.locked) return; 
						showMessage(this, "Press <strong>ENTER</strong> to update<br/><strong>Elevation Range:</strong> 1 - " + self.selectedRuleItemTotal);
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
						case "cnet" : imagePath = "cnet_img.jpg"; break;
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
				ElevateServiceJS.updateExpiryDate(self.selectedRule["ruleName"], $item["memberId"], dateText, {
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
				$("#noSelected, #ruleSelected, #addRuleItemContainer, #ruleItemDisplayOptions").fadeOut("slow", function(){
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
					rule: self.selectedRule,
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

						ElevateServiceJS.getProducts(self.getRuleItemFilter(), self.selectedRule["ruleName"], self.selectedRulePage, self.ruleItemPageSize, {
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
											showPosition: true,
											addProductItemCallback:function(position, expiryDate, comment, skus){
												ElevateServiceJS.addItemToRuleUsingPartNumber(self.selectedRule["ruleId"], position, expiryDate, comment, skus, {
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
												ElevateServiceJS.addFacetRule(self.selectedRule["ruleId"], position, expiryDate, comment, selectedFacetFieldValues, {
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
						$.cookie('elevate.filter' + $.formatAsId(self.selectedRule["ruleId"]),$(this).val(),{path:GLOBAL_contextPath});
						self.setRuleItemFilter();
					}
				});

				$("#tileViewIcon").off().on({click:function(e) {
					$.cookie('elevate.display' + $.formatAsId(self.selectedRule["ruleId"]), 'tileView', {path:GLOBAL_contextPath});
					$("#listViewIcon").removeClass("active");
					self.setRuleItemDisplay();
				}});

				$("#listViewIcon").off().on({click:function(e) {
					$.cookie('elevate.display' + $.formatAsId(self.selectedRule["ruleId"]), 'listView', {path:GLOBAL_contextPath});
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
								ElevateServiceJS.clearRule(self.selectedRule["ruleName"], {
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
					headerText:"Download Elevate",
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

				if ($.cookie('elevate.display' + $.formatAsId(self.selectedRule["ruleId"]))==="listView" || $.cookie('elevate.display'+ $.formatAsId(self.selectedRule["ruleId"]))==="tileView"){
					$("#ruleItemContainer").addClass($.cookie('elevate.display' + $.formatAsId(self.selectedRule["ruleId"])));
					$("#" + $.cookie('elevate.display' + $.formatAsId(self.selectedRule["ruleId"])) + "Icon").addClass("active");
				}else{
					$.cookie('elevate.display' + $.formatAsId(self.selectedRule["ruleId"]), self.defaultRuleItemDisplay, {path:GLOBAL_contextPath});
					$("#ruleItemContainer").addClass(self.defaultRuleItemDisplay);
					$("#" + self.defaultRuleItemDisplay + "Icon").addClass("active");				
				}
			},

			setRuleItemFilter: function(value){
				var self = this;
				var selectedFilter = $.isNotBlank(value)? value : $.cookie('elevate.filter' + $.formatAsId(self.selectedRule["ruleId"]));

				if ($.isNotBlank(selectedFilter)){
					$("#filterDisplay").val(selectedFilter);
				}else{
					$.cookie('elevate.filter' + $.formatAsId(self.selectedRule["ruleId"]), "all" ,{path:GLOBAL_contextPath});
					$("#filterDisplay").val("all");
				}

				self.populateRuleItem();
			},

			setRuleItemPosition: function(item, position) {
				var self = this;
				var $item = item;

				ElevateServiceJS.updateElevate(self.selectedRule["ruleName"], $item["memberId"], position, null, {
					callback : function(code){
						showActionResponse(code, "update position", $.isBlank($item["dpNo"])? "Product Id#: " + $item["edp"] : "SKU#: " + $item["dpNo"]);
						self.populateRuleItem();
					},
<<<<<<< HEAD
					preHook: function(){
						self.preShowRuleContent();
=======
					preHook: function(){ base.prepareList(); }
				});
			},

			itemOptionCallback: function(base, id, name, model){

				var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));
				dwr.engine.beginBatch();
				ElevateServiceJS.getTotalProductInRule(id,{
					callback: function(count){

						var totalText = (count == 0) ? zeroCountHTMLCode: "(" + count + ")"; 
						base.$el.find(selector + ' div.itemLink a').html(totalText);

						base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').on({
							click: function(e){
								setElevate(model);
							}
						});
					},
					preHook: function(){ 
						base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
					}
				});

				DeploymentServiceJS.getRuleStatus(moduleName, id, {
					callback:function(data){
						base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
					}
				});
				dwr.engine.endBatch();
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
										setElevate(data);
									}
								}
							});
						}
						else {
							jAlert("The keyword provided already exists.","Elevate");
						}
>>>>>>> refs/remotes/origin/sprint_6_revamp_alert
					}
				});
			},

			getRuleItemFilter: function(){
				var self = this;
				var cookieFilter = $.trim($.cookie('elevate.filter' + $.formatAsId(self.selectedRule["ruleId"])));
				var activefilter = $.isBlank(cookieFilter)? $("#filterDisplay").val() : cookieFilter;
				return $.isBlank(activefilter) ? "all" : activefilter;
			},

			init : function() {
				var self = this;
				self.showRuleContent();
			}
	};

	$(document).ready(function() {
		Elevate.init();
	});	

})(jQuery);	
