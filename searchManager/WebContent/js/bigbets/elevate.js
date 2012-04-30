(function($){
	var moduleName = "Elevate";
	var selectedRule = null;
	var selectedRuleStatus = null;

	var selectedRuleItemPage = 1;
	var selectedRuleItemTotal = 0;
	var selectedRulePage = 1;

	var rulePageSize = 10;
	var ruleItemPageSize = 6;

	var addItemFieldDefaultText = "Enter SKU #";
	var dateMinDate = -2;
	var dateMaxDate = "+1Y";
	var defaultItemDisplay = "sortableTile";

	var deleteItemInRuleConfirmText = "This will remove item associated to this rule. Continue?";
	var clearRuleConfirmText = "This will remove all items associated to this rule. Continue?";
	var lockedItemDisplayText = "This item is locked";


	var showAddItem = function(e){
		if (e.data.locked) return;

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

					contentHolder.find("#addItemToRuleBtn").on({
						click: function(){
							var commaDelimitedNumberPattern = /^\d+(,\d+)*$/;

							var skus = $.trim(contentHolder.find("#addItemDPNo").val());
							var sequence = $.trim(contentHolder.find("#addItemPosition").val());
							var expDate = $.trim(contentHolder.find("#addItemDate_1").val());
							var comment = $.trim(contentHolder.find("#addItemComment").val().replace(/\n\r?/g, '<br />'));

							if ($.isNotBlank(skus) && commaDelimitedNumberPattern.test(skus)){
								ElevateServiceJS.addItemToRuleUsingPartNumber(selectedRule.ruleId, sequence, expDate, comment, skus.split(','), {
									callback : function(code){
										showActionResponse(code, "add", skus);
										if (code==1) showElevate();
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
			}
		});
	};

	var deleteItemInRule = function(e){
		var data = e.data;
		if (!data.locked && confirm(deleteItemInRuleConfirmText)){
			ElevateServiceJS.deleteItemInRule(selectedRule.ruleName, data["edp"], {
				callback: function(code){
					showActionResponse(code, "delete", data["edp"]);
					if(code==1) showElevate();
				},
				preHook: function(){
					prepareElevate();
				}
			});
		}
	};

	var updateRuleItemPosition = function(edp, destinationIndex) {
		ElevateServiceJS.updateElevate(selectedRule.ruleName,edp,destinationIndex, {
			callback : function(data){
				showElevate();
			},
			preHook: function(){
				prepareElevate();
			}
		});
	};

	var setItemValues = function(item){
		var id = $.formatAsId(item["edp"]); 
		var disableCalendar = false;

		$("#sItemImg" + id).attr("src",item["imagePath"]);
		$("#sItemMan" + id).html(item["manufacturer"]);
		$("#sItemName" + id).html(item["name"]);
		$("#sItemDPNo" + id).html(item["dpNo"]);
		$("#sItemMfrPN" + id).html(item["mfrPN"]);

		$("#sItemModBy" + id).html(item["lastModifiedBy"]);
		$("#sItemModDate" + id).html(item["formattedLastModifiedDate"]);
		$("#sItemValidityText" + id).html(item["validityText"]);
		$("#sItemPosition" + id).val(item["location"]);

		if (item["isExpired"]) $("#sItemValidityText" + id).html('<img src="../images/expired_stamp50x16.png">');

		$("#sItemImg" + id).on({
			error:function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); 
			}
		});

		if (selectedRuleStatus.locked)
			$('#sItemPosition' + id).attr("readonly", "readonly");

		$('#sItemPosition' + id).on({
			keypress:function(e){
				if (e.data.locked) return;

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
					return true;
				}else{
					if(code == 8) return true;
					if(code < 32 || (code > 48 && code < 58)) return true;
					alert("Should be numeric value");
					return false;
				}
			},
			focus:function(e){
				if (e.data.locked) return; 
				if ($(this).val()==item["location"]) $(this).val("");
			},
			blur:function(e){
				if (e.data.locked) return; 
				$(this).val(item["location"]);   
			},	
			mouseenter: showHoverInfo
		},{locked: selectedRuleStatus.locked});

		// Product is no longer visible in the setting
		if ($.isBlank(item["dpNo"])){
			disableCalendar = true;			
			$("#sItemImg" + id).attr("src","../images/padlock_img.jpg"); 
			$("#sItemMan" + id).html(lockedItemDisplayText);
			$("#sItemDelete" + id).html("");
			$("#sItemDPNo" + id).html("Unavailable");
			$("#sItemMfrPN" + id).html("Unavailable");
			$("#sItemName" + id).html('<p><font color="red">Product Id:</font> ' + item["edp"] + '<br/>This is no longer available in the search server you are connected</p>');
			$('#sItemPosition' + id).attr("readonly", "readonly");
		}

		$('#commentIcon' + id).on({
			click: showCommentList
		}, {locked: selectedRuleStatus.locked, type:moduleName, item: item, name: selectedRule.ruleName});

		$('#auditIcon' + id).on({
			click: showAuditList
		}, {locked: selectedRuleStatus.locked, type:moduleName, item: item, name: selectedRule.ruleName});

		$('#sItemDelete' + id).off().on({
			click: deleteItemInRule,
			mouseenter: showHoverInfo
		},{locked: selectedRuleStatus.locked, edp:item["edp"]});

		$("#sItemExpDate" + id).val(item["formattedExpiryDate"]);

		$("input#sItemExpDate" + id).datepicker({
			showOn: "both",
			minDate: dateMinDate,
			maxDate: dateMaxDate,
			buttonText: "Expiration Date",
			buttonImage: "../images/icon_calendar.png",
			buttonImageOnly: true,
			disabled: disableCalendar || selectedRuleStatus.locked,
			onSelect: function(dateText, inst) {	
				if (item["formattedExpiryDate"] != dateText){
					ElevateServiceJS.updateExpiryDate(selectedRule.ruleName,item["edp"], dateText, {
						callback: function(code){
							showActionResponse(code, "update", dateText);
							if(code==1) showElevate();
						}
					});
				}
			}
		});

	}; 

	var showDisplayOption= function(){
		(selectedRuleItemTotal == 0 && getItemFilter==="all") ? $('#sortableDisplayOptions').hide(): $('#sortableDisplayOptions').show();
	};

	var showPaging = function(page){
		selectedRuleItemTotal > 0 ? $("#sortablePagingTop, #sortablePagingBottom").show() : "";
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
		getElevateRuleList(1);
		prepareElevate();
		$("#preloader").hide();

		if(selectedRule==null){
			$("#noSelected").show();
			$("#titleText").html(moduleName);
			return;
		}


		$("#submitForApproval").show();
		$("#elevate").show();

		$("#titleText").html(moduleName + " for ");
		$("#titleHeader").html(selectedRule.ruleName);
		$("#addItemHolder").show();

		populateItem(1);

		$("#addItem, #addItemDPNo").val(addItemFieldDefaultText).off().on({
			blur: setFieldDefaultTextHandler,
			focus: setFieldEmptyHandler
		},{text:addItemFieldDefaultText}
		);

		$("#addItemBtn").off().on({
			click: showAddItem,
			mouseenter: showHoverInfo
		},{locked: selectedRuleStatus.locked});

		$("a#downloadIcon").off().on({click:showDownloadOption}, {
			selector: "#downloadIcon",
			template: "#downloadTemplate",
			title: "Download Page",
			keyword: selectedRule.ruleName,
			filter: getItemFilter,
			itemPage: selectedRulePage,
			itemPageSize:ruleItemPageSize
		});

		$("a#clearRuleBtn").off().on({
			mouseenter: showHoverInfo,
			click: function(e){
				if(!e.data.locked && confirm(clearRuleConfirmText))
					ElevateServiceJS.clearRule(selectedRule.ruleName, {
						callback: function(data){
							showElevate();
						}
					});
			}
		},{locked: selectedRuleStatus.locked});

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
	};

	var setElevate = function(rule){
		selectedRule = rule;

		if (rule!=null){
			DeploymentServiceJS.getRuleStatus(moduleName, selectedRule.ruleId, {
				callback:function(data){
					selectedRuleStatus = data;
					$('#itemPattern' + $.escapeQuotes($.formatAsId(selectedRule.ruleId)) + ' div.itemSubText').html(getRuleNameSubTextStatus(selectedRuleStatus));
					showDeploymentStatusBar(selectedRuleStatus);
					showElevate();
				},
				preHook: function(){
					prepareElevate();
				}
			});		
		}else{
			showElevate();
		}
	};

	var getElevateRuleList = function(page){
		$("#rulePanel").sidepanel({
			fieldId: "keywordId",
			fieldName: "keyword",
			headerText : "Keyword",
			searchText : "Enter Search",
			page: page,
			pageSize: rulePageSize,

			itemDataCallback: function(base, keyword, page){
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

				ElevateServiceJS.getTotalProductInRule(id,{
					callback: function(count){

						var totalText = (count == 0) ? "&#133;": "(" + count + ")"; 
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
			},

			itemAddCallback: function(base, keyword){
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