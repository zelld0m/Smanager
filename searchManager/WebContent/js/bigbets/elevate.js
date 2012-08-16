(function($){

	var Elevate = {
			moduleName: "Elevate",
			selectedRule:  null,
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

			removeExpiryDateConfirmText: "This will remove expiry date associated to this rule. Continue?",
			removeRuleItemConfirmText: "This will remove item associated to this rule. Continue?",
			clearRuleItemConfirmText: "This will remove all items associated to this rule. Continue?",

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
									alert("The keyword provided already exists.");
								}
							}
						});
					},

					pageChangeCallback: function(page){
						self.rulePage = page;
					}
				});
			},

			setRuleItemValues: function(li, item){
				var $li = li;
				var $item = item;
				var self = this;

				var id = $.formatAsId($item["edp"]);
				$li.attr("id", id);

				$li.find(".sortOrderTextBox").val($item["location"]);
				$li.find(".name").html($item["name"]);
				$li.find(".manufacturer").html($item["manufacturer"]);
				$li.find(".sku").html($item["dpNo"]);
				$li.find(".mfrpn").html($item["mfrPN"]);

				if (item["isExpired"]){
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
							ElevateServiceJS.updateExpiryDate(self.selectedRule["ruleName"], $item["edp"], dateText, {
								callback: function(code){
									showActionResponse(code, "update", "expiry date of SKU#: " + $item["dpNo"]);
									if(code==1) self.showRuleContent();
								}
							});
						}
					}
				});

				$li.find('.clearDate').off().on({
					click: function(e){
						if (e.data.locked) return;

						if (confirm(self.removeExpiryDateConfirmText)){
							ElevateServiceJS.updateExpiryDate(self.selectedRule["ruleName"], e.data.item["memberId"], "", {
								callback: function(code){
									showActionResponse(code, "update", "expiry date of SKU#: " + e.data.item["dpNo"]);
									if(code==1) self.showRuleContent();
								}
							});
						}
					}
				}, {locked: self.selectedRuleStatus["locked"] || !allowModify, item: $item});

				$li.find('.deleteRuleItemIcon').off().on({
					click: function(e){
						if (e.data.locked) return;

						if(confirm(self.removeRuleItemConfirmText)){
							ElevateServiceJS.deleteItemInRule(self.selectedRule["ruleName"], e.data.item["memberId"], {
								callback: function(code){
									showActionResponse(code, "delete", $.isBlank(e.data.item["dpNo"])? "Product Id#: " + e.data.item["edp"] : "SKU#: " + e.data.item["dpNo"]);
									self.showRuleContent();
								},
								preHook: function(){
									self.preShowRuleContent();
								}
							});
						}

					},
					mouseenter: showHoverInfo
				},{locked: self.selectedRuleStatus["locked"] || !allowModify, item:$item});

				setTimeout(function(){		
					if ($.isBlank($item["dpNo"])){
						$li.find(".itemImg").prop("src","../images/padlock_img.jpg"); 
						$li.find(".name").html('<p><font color="red">Product Id:</font> ' + item["edp"] + '<br/>This is no longer available in the search server you are connected</p>');
						$li.find(".manufacturer").html(self.lockedItemDisplayText);
						$li.find(".sku, .mfrpn").html("Unavailable");
						return;
					}

					$li.find("img.itemImg").prop("src",item['imagePath']).off().on({
						error:function(){ 
							$(this).unbind("error").prop("src", "../images/no-image.jpg"); 
						}
					});
				}, 10);
			},

			preShowRuleContent: function(){
				var self = this;
				$("#preloader").show();
				$("#noSelected,#ruleSelected,#ruleItemDisplayOptions").hide();
				$("#ruleItemPagingTop, #ruleItemPagingBottom").empty();
			},

			postShowRuleContent: function(){
				var self = this;
				$("#preloader,#noSelected").hide();
				$("#ruleSelected, #addRuleItemContainer").show();
				$("#titleText").html(self.moduleName + " for ");
				$("#titleHeader").html(self.selectedRule["ruleName"]);
			},

			populateRuleItem: function(page){
				var self = this;
				self.selectedRulePage = $.isNotBlank(page) && $.isNumeric(page) ? page : 1;
				self.selectedRuleItemTotal = 0;
				var $ul = $("ul#ruleItemHolder");
				$ul.find('li.ruleItem:not(#ruleItemPattern)').remove();

				ElevateServiceJS.getProducts(self.getRuleItemFilter(), self.selectedRule["ruleName"], self.selectedRulePage, self.ruleItemPageSize, {
					callback: function(data){
						self.selectedRuleItemTotal = data.totalSize;
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
					preHook: function(){
						self.preShowRuleContent();
					},
					postHook: function(){
						self.postShowRuleContent();
					}
				});
			},

			addRuleItemOptionListener: function(){
				var self = this;

				$("#filterDisplay").on({
					change: function(e){
						self.setRuleItemFilter($(this).val());
					}
				});
				
				$("#tileViewIcon").off().on({click:function(e) {
					$.cookie('elevate.display', 'tileView', {expires: 1, path:GLOBAL_contextPath});
					$("#listViewIcon").removeClass("active");
					self.setRuleItemDisplay();
				}});

				$("#listViewIcon").off().on({click:function(e) {
					$.cookie('elevate.display', 'listView', {expires: 1, path:GLOBAL_contextPath});
					$("#tileViewIcon").removeClass("active");
					self.setRuleItemDisplay();
				}});

//				$("#addItem, #addItemDPNo").val(addItemFieldDefaultText).off().on({
//				blur: setFieldDefaultTextHandler,
//				focus: setFieldEmptyHandler
//				}, {text:addItemFieldDefaultText});

//				$("#addItemBtn").off().on({
//				click: showAddItem,
//				mouseenter: showHoverInfo
//				},{locked: self.selectedRuleStatus["locked"] || !allowModify});

				$("#clearRuleItemIcon").off().on({
					click: function(e){
						console.log("triggered");
						if(e.data.locked) return;
						if (confirm(self.clearRuleItemConfirmText)){
							ElevateServiceJS.clearRule(self.selectedRule["ruleName"], {
								callback: function(code){
									showActionResponse(code, "clear", self.selectedRule["ruleName"]);
									self.showRuleContent();
								}
							});
						}
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

				$("#submitForApproval").rulestatus({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					authorizeRuleBackup: true,
					authorizeSubmitForApproval: allowModify,
					afterSubmitForApprovalRequest:function(ruleStatus){
						self.selectedRuleStatus = ruleStatus;
						self.showRuleContent();
					},
					beforeRuleStatusRequest: function(){
						self.preShowRuleContent();	
					},
					afterRuleStatusRequest: function(ruleStatus){
						self.selectedRuleStatus = ruleStatus;
						self.getRuleList();
						self.setRuleItemDisplay();
						self.populateRuleItem();
					}
				});	
			},

			setRule: function(rule){
				var self = this;
				self.selectedRule = rule;
				self.showRuleContent();
			},

			setRuleItemDisplay: function(){
				var self = this;

				$("#ruleItemContainer").removeClass("tileView").removeClass("listView");

				if ($.cookie('elevate.display')==="listView" || $.cookie('elevate.display')==="tileView"){
					$("#ruleItemContainer").addClass($.cookie('elevate.display'));
					$("#" + $.cookie('elevate.display') + "Icon").addClass("active");
				}else{
					$.cookie('elevate.display', self.defaultRuleItemDisplay, {expires: 1, path:GLOBAL_contextPath});
					$("#ruleItemContainer").addClass(self.defaultRuleItemDisplay);
					$("#" + self.defaultRuleItemDisplay + "Icon").addClass("active");				
				}
			},

			setRuleItemFilter: function(value){
				var self = this;
				$.cookie('elevate.filter', value ,{expires: 1, path:GLOBAL_contextPath});
				$("#filterDisplay").val(value);
				self.populateRuleItem(1);
			},
			
			getRuleItemFilter: function(){
				var cookieFilter = $.trim($.cookie('elevate.filter'));
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