(function($){
	var moduleName = "Elevate";
	var selectedRule = null;
	var selectedRuleStatus = null;

	var selectedRuleItemPage = 1;
	var selectedRuleItemTotal = 0;
	var selectedRulePage = 1;

	var rulePage = 1;
	var rulePageSize = 10;
	var ruleFilterText = "";

	var ruleItemPageSize = 6;

	var addItemFieldDefaultText = "Enter SKU #";
	var zeroCountHTMLCode = "&#133;";
	var dateMinDate = 0;
	var dateMaxDate = "+1Y";
	var defaultItemDisplay = "sortableTile";

	var deleteItemInRuleConfirmText = "This will remove item associated to this rule. Continue?";
	var clearRuleConfirmText = "This will remove all items associated to this rule. Continue?";
	var lockedItemDisplayText = "This item is locked";

	var showAddItem = function(e){
		if (e.data.locked || !allowModify) return;

		$(this).qtip({
			content: {
				text: $('<div/>'),
				title: { text: 'Elevate Item', button: true
				}
			},
			position:{
				at: 'top center',
				my: 'bottom center'
			},
			show:{
				solo: true,
				ready: true
			},
			style: {
				width: 'auto'
			},
			events: { 
				show: function(event, api){
					var contentHolder = $("div", api.elements.content);
					contentHolder.html($("#addItemTemplate").html());
					contentHolder.find("#addOption").tabs({

					});

					contentHolder.find("#addItemDate").attr('id', 'addItemDate_1');

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
								alert("There are no SKUs specified in the list.");
							}
							else if (!commaDelimitedNumberPattern.test(skus)) {
								alert("List contains an invalid SKU.");
							}							
							else if (!$.isBlank(expDate) && !$.isDate(expDate)){
								alert("Invalid date specified.");
							}
							else if(today.getTime() > new Date(expDate).getTime())
								alert("Start date cannot be earlier than today");
							else if (!isXSSSafe(comment)){
								alert("Invalid comment. HTML/XSS is not allowed.");
							}
							else {								
								ElevateServiceJS.addItemToRuleUsingPartNumber(selectedRule.ruleId, sequence, expDate, comment, skus.split(/[\s,]+/), {
									callback : function(code){
										showActionResponseFromMap(code, "add", skus, "Please check for the following:\n a) SKU(s) are already present in the list\n b) SKU(s) are actually searchable using the specified keyword.");
										showElevate();
									},
									preHook: function(){ 
										prepareElevate();
									}
								});								
							}
						}
					});
				},
				hide: function(event, api){
					api.destroy();
				}
			}
		});
	};

	var prepareElevate = function(){
		clearAllQtip();
		$("#preloader").show();
		$("#submitForApproval").hide();
		$("#noSelected").hide();
		$("#elevate").hide();
		$("#addItemHolder").hide();
		$("#titleText").html(moduleName);
		$("#titleHeader").html("");
		$('#sortable-bigbets li:not(#sItemPattern)').remove();
		$('#sortablePagingTop,#sortablePagingBottom,#sortableDisplayOptions').hide();
	};

	var populateItem = function(page){
		selectedRulePage = page;
		selectedRuleItemTotal = 0;
		ElevateServiceJS.getProducts(getItemFilter(), selectedRule.ruleName, page, ruleItemPageSize, {
			callback: function(data){
				selectedRuleItemTotal = data.totalSize;
				var list = data.list;
				var item, id;

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

	var deleteItemInRule = function(e){
		var data = e.data;
		if (!data.locked && allowModify && confirm(deleteItemInRuleConfirmText)){
			ElevateServiceJS.deleteItemInRule(selectedRule.ruleName, data["edp"], {
				callback: function(code){
					showActionResponse(code, "delete", data["edp"]);
					showElevate();
				},
				preHook: function(){
					prepareElevate();
				}
			});
		}
	};

	var updateRuleItemPosition = function(edp, destinationIndex) {
		ElevateServiceJS.updateElevate(selectedRule.ruleName,edp,destinationIndex, {
			callback : function(code){
				showActionResponse(code, "update position", edp);
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
							alert("Maximum allowed value is " + (selectedRuleItemTotal));
						}else{
							updateRuleItemPosition(item["edp"], destinationIndex);
						}
					}
				}else{
					if (((code==48 || code==96) && $.isBlank($(e.target).val())) || (code > 31 && (code < 48 || code > 57))){
						alert("Should be a positive number not greater than " + selectedRuleItemTotal);
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
	
		$('#commentIcon' + id).on({
			click: showCommentList
		}, {locked: selectedRuleStatus.locked || !allowModify, type:moduleName, item: item, name: selectedRule.ruleName});

		$('#auditIcon' + id).on({
			click: showAuditList
		}, {locked: selectedRuleStatus.locked || !allowModify, type:moduleName, item: item, name: selectedRule.ruleName});

		$('#sItemDelete' + id).off().on({
			click: deleteItemInRule,
			mouseenter: showHoverInfo
		},{locked: selectedRuleStatus.locked || !allowModify, edp:item["edp"]});

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
					}
				});
			}
		},10);
	}; 

	var showDisplayOption= function(){
		(selectedRuleItemTotal == 0 && getItemFilter()==="all") ? $('#sortableDisplayOptions').hide(): $('#sortableDisplayOptions').show();
	};

	var showPaging = function(page){
		selectedRuleItemPage = page;
		$("#sortablePagingTop, #sortablePagingBottom").paginate({
			currentPage:page, 
			pageSize:ruleItemPageSize,
			totalItem:selectedRuleItemTotal,
			callbackText: function(itemStart, itemEnd, itemTotal){
				var selectedText = $.trim($("#filterDisplay").val()) != "all" ? " " + $("#filterDisplay option:selected").text(): "";
				return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + selectedText + " Items";
			},
			pageLinkCallback: function(e){ populateItem(e.data.page); },
			nextLinkCallback: function(e){ populateItem(e.data.page + 1); },
			prevLinkCallback: function(e){ populateItem(e.data.page - 1); }
		});
	};

	var showElevate = function(){
		if(selectedRule==null){
			$("#preloader").hide();
			$("#noSelected").show();
			$("#titleText").html(moduleName);
			return;
		}

		DeploymentServiceJS.getRuleStatus(moduleName, selectedRule.ruleId, {
			preHook: function(){ 
				prepareElevate(); 
			},
			callback:function(data){
				selectedRuleStatus = data;
				showDeploymentStatusBar(moduleName, selectedRuleStatus);
				getElevateRuleList();
				populateItem(1);

				$("#addItem, #addItemDPNo").val(addItemFieldDefaultText).off().on({
					blur: setFieldDefaultTextHandler,
					focus: setFieldEmptyHandler
				},{text:addItemFieldDefaultText}
				);

				$("#addItemBtn").off().on({
					click: showAddItem,
					mouseenter: showHoverInfo
				},{locked: selectedRuleStatus.locked || !allowModify});

				$("a#clearRuleBtn").off().on({
					mouseenter: showHoverInfo,
					click: function(e){
						if(!e.data.locked && allowModify && confirm(clearRuleConfirmText))
							ElevateServiceJS.clearRule(selectedRule.ruleName, {
								callback: function(code){
									showActionResponse(code, "clear", selectedRule.ruleName);
									showElevate();
								}
							});
					}
				},{locked: selectedRuleStatus.locked || !allowModify});

				$("#submitForApprovalBtn").off().on({
					click: function(e){
						var ruleStatus = null;
						var data = e.data;

						if(confirm(e.data.module + " " + e.data.ruleRefName + " will be locked for approval. Continue?")){
							DeploymentServiceJS.processRuleStatus(e.data.module, e.data.ruleRefId, e.data.ruleRefName, e.data.isDelete,{
								callback: function(data){
									ruleStatus = data;
								},
								preHook:function(){
									prepareElevate();
								},
								postHook: function(){
									setElevate(selectedRule);
								}
							});
						}
					}
				}, { module: moduleName, ruleRefId: selectedRule.ruleId , ruleRefName: selectedRule.ruleName, isDelete: false});
				
				$("a#downloadIcon").download({
					headerText:"Download Elevate",
					hasPageOption: true,
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;
						
						params["filename"] = e.data.filename;
						params["type"] = e.data.type;
						params["keyword"] = selectedRule.ruleName;
						params["page"] = (e.data.page==="current") ? selectedRuleItemPage : e.data.page;
						params["filter"] = getItemFilter();
						params["itemperpage"] = ruleItemPageSize;
						params["clientTimezone"] = +new Date();

						for(var key in params){
							if (count>0) urlParams +='&';
							urlParams += (key + '=' + params[key]);
							count++;
						};

						document.location.href = url + '?' + urlParams;
					}
				});
			}
		});	
	};

	var setElevate = function(rule){
		selectedRule = rule;
		showElevate();
	};

	var getElevateRuleList = function(){

		$("#rulePanel").sidepanel({
			fieldId: "keywordId",
			fieldName: "keyword",
			headerText : "Keyword",
			searchText : "Enter Keyword",
			showAddButton: allowModify,
			page: rulePage,
			pageSize: rulePageSize,
			filterText: ruleFilterText,

			itemDataCallback: function(base, keyword, page){
				ruleFilterText = keyword;
				rulePage = page;
				StoreKeywordServiceJS.getAllKeyword(keyword, page, rulePageSize,{
					callback: function(data){
						base.populateList(data);
						base.addPaging(keyword, page, data.totalSize);
					},
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
							alert("The keyword provided already exists.");
						}
					}
				});
			},

			pageChangeCallback: function(page){
				rulePage = page;
			}
		});
	};

	var getItemFilter = function(){
		var cookieFilter = $.trim($.cookie('elevate.filter'));
		var filter = $.isBlank(cookieFilter)? $("#filterDisplay").val() : cookieFilter;
		return $.isBlank(filter) ? "all" : filter;
	};

	var setItemFilter = function(value){
		$.cookie('elevate.filter', value ,{expires: 1});
		$("#filterDisplay").val(value);
		showElevate();
	};

	var setItemDisplay = function(){

		$("#sortable-bigbets").removeClass("sortableTile");
		$("#sortable-bigbets").removeClass("sortableList");

		if ($.cookie('elevate.display')=="sortableList" || $.cookie('elevate.display')=="sortableTile"){
			$("#sortable-bigbets").addClass($.cookie('elevate.display'));
			$("#" + $.cookie('elevate.display')).addClass("active");
		}else{
			$("#sortable-bigbets").addClass(defaultItemDisplay);
			$("#" + defaultItemDisplay).addClass("active");				
		}
	};

	var init = function() {
		setItemDisplay();
		setItemFilter();
		getElevateRuleList();
		showElevate();
	};

	$(document).ready(function() { 
		$("#filterDisplay").on({
			change: function(e){
				setItemFilter($(this).val());
			}
		});

		$("#sortableTile").on({click:function(e) {
			$.cookie('elevate.display', 'sortableTile',{expires: 1});
			$("#sortableList").removeClass("active");
			setItemDisplay();
		}
		});

		$("#sortableList").on({click:function(e) {
			$.cookie('elevate.display', 'sortableList',{expires: 1});
			$("#sortableTile").removeClass("active");
			setItemDisplay();
		}
		});

		init();	
	});	
})(jQuery);	
