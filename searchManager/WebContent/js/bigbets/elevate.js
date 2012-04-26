(function($){
	var keywordDefaultSearchText = "Enter Keyword";

	var sortableTotalItems = 0;
	var sortableTotalPages = 0;
	var sortablePage = 1;
	var sortablePageSize = 6;
	var sortableAddDefaultSearchText = "Enter SKU #";
	var sortablePopupAddDefaultText = "SKU #";
	
	var expDateMinDate = -2;
	var expDateMaxDate = "+1Y";
	var lockedItemText = "This item is locked";

	var storeLabel = "";

	var defaultItemDisplay = "sortableList";
	var defaultFilterDisplay = "all";
	var errmsg_skunumeric ="SKU # should be numeric";

	var keywordPageSize = 10;
	var keywordHeaderText = "Keyword";
	var keywordSearchText = "Search Keyword";
	var itemDeleteConfirmText = "This will remove item associated to this rule. Continue?";
	var ruleClearConfirmText = "This will remove all items associated to this rule. Continue?";
	var isRuleLocked = false;
	var ruleStatus = null;

	$(document).ready(function() { 
		
		updateSortableItemPosition = function(edp, destinationIndex) {
			if ($.isNotBlank(getSelectedKeyword())){
				ElevateServiceJS.updateElevate(getSelectedKeyword(),edp,destinationIndex, {
					callback : function(data){
						updatePositionBox();
						updateSortableList(getSelectedKeyword(), sortablePage);
					},
					preHook: function(){ prepareSortableList(); },
					postHook: function(){ $("#sortable-bigbets-container > .circlePreloader").remove(); },
					errorHandler: function(message){ alert(message);}
				});
			}
		},

		populateKeywordList = function(page){
			$("#keywordList").sidepanel({
				fieldId: "keywordId",
				fieldName: "keyword",
				headerText : keywordHeaderText,
				searchText : keywordSearchText,
				page: page,
				pageSize: keywordPageSize,

				itemDataCallback: function(base, keyword, page){
					StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
						callback: function(data){
							base.populateList(data);
							base.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
					});
				},

				itemOptionCallback: function(base, id, name){
					DeploymentServiceJS.getRuleStatus("elevate", name, {
						callback:function(data){
							var status = (data==null) ? "" : data["approvalStatus"];
							ElevateServiceJS.getElevatedProductCount(name,{
								callback: function(count){
									var totalText = (count == 0) ? "&#133;": "(" + count + ")"; 
									base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html(totalText);

									switch (status){
									case "REJECTED": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Action Required"); break;
									case "PENDING": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Awaiting Approval"); break;
									case "APPROVED": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Ready For Production"); break;
									default: base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemSubText').html("Setup a Rule"); break;
									}

									base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').on({
										click: function(e){
											$("#titleText").html("Elevate List for ");
											$("#keywordHeader").html(e.data.name);
											$("div#addSortableHolder").show();
											ruleStatus = e.data.status;
											isRuleLocked = ruleStatus!=null && ruleStatus["locked"];
											updateSortableList(e.data.name, 1);
											addEventListener();
										}},{name:name, status: data});
								},
								preHook: function(){ 
									base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
								}
							});

						},
						preHook: function(){ 
							base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
						}
					});
				},

				itemAddCallback: function(base, keyword){
					StoreKeywordServiceJS.addKeyword(keyword,{
						callback : function(data){
							switch (data){
							case -1: alert("Duplicate entry for '" + keyword + "'."); break;
							case 0: alert("Error encountered while adding '" + keyword + "'."); break;
							default: 
								base.getList(keyword, 1);
							alert(headerText + ' "' + keyword + '" added successfully.');
							};
						}
					});
				}
			});
		};

		var removeItem = function(e){
			if (!isRuleLocked && confirm(itemDeleteConfirmText)){
				var item = e.data.item;
				var deleteSelector = '#sItemDelete' + formatToId(item.edp);
				var edp = item.edp;

				// adjust active paging, 1 sibling because of hidden pattern used for cloning
				if (sortablePage==sortableTotalPages && sortableTotalPages > 1 && $(deleteSelector).parents("li").siblings().length==1){
					--sortablePage;
				}

				ElevateServiceJS.removeElevate(getSelectedKeyword(),edp, {
					callback : function(data){
						$(deleteSelector).parents("li").remove();
						updateSortableList(getSelectedKeyword(),sortablePage==0? 1 : sortablePage);
						populateKeywordList(1);
					}
				});
			}
		};

		//update the position in textbox
		updatePositionBox = function(){
			var index = ((sortablePage-1)*sortablePageSize) + 1;
			//$("#sortable-bigbets li input.sItemPosition").each(function(){ $(this).val(index++); });
		};

		// Case: Search SKU
		handlerSearchSKU = function(e){
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				if ($.isNumeric($.trim($(this).val()))){
					return;
				}
				alert(errmsg_skunumeric);
			}	
		};

		$("#addSortable").on({
			blur: setFieldDefaultTextHandler,
			focus: setFieldEmptyHandler,
			keydown: handlerSearchSKU
		},{text:sortableAddDefaultSearchText}
		);


		formatToId = function(keyword){
			return "_".concat(keyword.replace(/ /g,"_").toLowerCase());
		},

		escapeQuotes = function(id) {
			return id.replace(/\"/g, "\\\"").replace(/\'/g, "\\\'");
		},

		formatToEDP = function(id){
			return id.replace(/sItemPattern_/g,"");
		},

		getKeyword = function(){
			var keyword = $.trim(dwr.util.getValue("searchFilter"));
			if (keyword.toLowerCase() == keywordDefaultSearchText.toLowerCase())
				keyword = "";
			return keyword;
		},

		// Case: Create pagination for sortable items
		showPaging = function(sortablePage, sortablePageSize, totalItem,  keyword){
			$("#sortablePagingTop, #sortablePagingBottom").paginate({
				currentPage:sortablePage, 
				pageSize:sortablePageSize,
				totalItem:totalItem,
				callbackText: function(itemStart, itemEnd, itemTotal){
					var selectedText = $.trim($("#filterDisplay").val()) != "all" ? " " + $("#filterDisplay option:selected").text(): "";
					return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + selectedText + " Items";
				},
				pageLinkCallback: function(e){ updateSortableList(keyword, e.data.page); },
				nextLinkCallback: function(e){ updateSortableList(keyword, e.data.page + 1); },
				prevLinkCallback: function(e){ updateSortableList(keyword, e.data.page - 1); }
			});
		},

		refreshItemForUpdate = function(sortableItem){
			id = formatToId(sortableItem.edp); 
			var disableCalendar = false;

			$("#sItemImg" + id).attr("src",sortableItem.imagePath);
			$("#sItemMan" + id).html(sortableItem.manufacturer);
			$("#sItemName" + id).html(sortableItem.name);
			$("#sItemDPNo" + id).html(sortableItem.dpNo);
			$("#sItemMfrPN" + id).html(sortableItem.mfrPN);

			$("#sItemModBy" + id).html($.trim(sortableItem.lastModifiedBy));
			$("#sItemModDate" + id).html(sortableItem.formattedLastModifiedDate);
			$("#sItemValidityText" + id).html(sortableItem.validityText);
			$("#sortableBox" + id).html($("#sortableBox" + id).html().replace("%%store%%",storeLabel));

			$('#commentIcon' + id).on({
				click: showComment
			}, {locked: isRuleLocked, item: sortableItem});
			
			$('#sItemDelete' + id).on({
				click: removeItem,
				mouseenter:showHoverInfo
			}, {locked: isRuleLocked, item: sortableItem});
			
			
			$("#sItemPosition" + id).val(sortableItem.location);

			$('#sItemPosition' + id).keydown(function(e){
				var currentIndex = $.trim(($(this).parent("li").index()+1) + ((sortablePage-1)*sortablePageSize));

				var code = (e.keyCode ? e.keyCode : e.which);
				if(code == 13) { 
					var destinationIndex = $.trim($(this).val());
					if($.isNumeric(destinationIndex) && currentIndex!=destinationIndex){
						if(destinationIndex > sortableTotalItems){
							alert("Maximum allowed value is " + (sortableTotalItems));
						}else{
							updateSortableItemPosition(sortableItem.edp, destinationIndex);
							updateSortableList(getSelectedKeyword(), sortablePage);
						}
					}
				}
			});

			if (sortableItem.isExpired)
				$("#sItemValidityText" + id).html('<img src="../images/expired_stamp50x16.png">');

			$("#sItemImg" + id).on({
				error:function(e){ 
					$(this).unbind("error").attr("src", "../images/no-image.jpg"); 
				}
			});

			// Product is no longer visible in the setting
			if ($.trim(sortableItem.dpNo).length == 0){
				disableCalendar = true;			
				$("#sItemImg" + id).attr("src","../images/padlock_img.jpg"); 
				$("#sItemMan" + id).html(lockedItemText);
				$("#sItemDelete" + id).html("");
				$("#sItemDPNo" + id).html("Unavailable");
				$("#sItemMfrPN" + id).html("Unavailable");
				$("#sItemName" + id).html('<p><font color="red">Product Id:</font> ' + sortableItem.edp + '<br/>This is no longer available in the search server you are connected</p>');
			}

			$("#sItemExpDate" + id).val(sortableItem.formattedExpiryDate);

			//datepicker for expiry date
			$("input#sItemExpDate" + id).datepicker({
				showOn: "both",
				minDate: expDateMinDate,
				maxDate: expDateMaxDate,
				buttonText: "Expiration Date",
				buttonImage: "../images/icon_calendar.png",
				buttonImageOnly: true,
				disabled: disableCalendar || isRuleLocked,
				onSelect: function(dateText, inst) {	
					if (sortableItem.formattedExpiryDate != dateText){
						ElevateServiceJS.updateExpiryDate(getSelectedKeyword(),sortableItem.edp, dateText, {
							callback: function(data){
								updateSortableList(getSelectedKeyword(), sortablePage);
							},
							errorHandler: function(message){ alert(message); }
						});
					}
				}
			});
		},

		prepareSortableList = function() {
			$("#clearRule").hide();
			$("#sortable-bigbets-container > .circlePreloader").remove();
			$("#sortable-bigbets-container img#no-items-img").remove();
			$('#sortable-bigbets li:not(#sItemPattern)').remove();
			$('#sortablePagingTop').html("");
			$('#sortablePagingBottom').html("");
			$("#sortable-bigbets-container").prepend('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>');
		};

		cleanUpSortableList = function() {
			$("#sortable-bigbets-container > .circlePreloader").remove();

			if (isRuleLocked){
				$("input#addSortable").attr("readonly", "readonly");
				$("input.sItemPosition").attr("readonly", "readonly");
			}
		};

		updateSortableList = function(keyword, page) {
			sortablePage = page;

			ElevateServiceJS.getProducts(getItemFilter(), keyword, sortablePage, sortablePageSize,{
				callback: function(data){
					sortableTotalItems = data.totalSize;
					sortableTotalPages = Math.ceil(sortableTotalItems/sortablePageSize);

					var sortableList = data.list;

					// Delete all the rows except for the "pattern" row
					$("#sortable-bigbets-container img#no-items-img").remove();
					$('#sortable-bigbets li:not(#sItemPattern)').remove();

					// Create a new set cloned from the pattern row
					var sortableItem, id;

					$.isNotBlank(getSelectedKeyword())?  $("#submitForApproval").show() : $("#submitForApproval").hide();

					if(ruleStatus!=null){
						$("span#status").html(ruleStatus["approvalStatus"]);
						$("span#statusDate").html(ruleStatus["lastModifiedDate"]);
					}

					//TODO: Should be configurable
					if (sortableTotalItems == 0){
						$("#clearRule").hide();
						if ($.isBlank(getSelectedKeyword())){
							$("#sortable-bigbets-container").prepend('<img id="no-items-img"/>');
							$("#sortable-bigbets-container img#no-items-img").attr("src", "../images/ElevatePageisBlank.jpg");							
						}
						else{
							//TODO: no results
						}
					}else{
						$("#clearRule").show();
					}

					for (var i = 0; i < sortableList.length; i++) {
						sortableItem = sortableList[i];
						id = formatToId(sortableItem.edp);
						dwr.util.cloneNode("sItemPattern", { idSuffix:id });
						refreshItemForUpdate(sortableItem);
						$("#sItemPattern" + id).attr("style", "display:block"); 		
					};

					//updatePositionBox();

					var pagingText = sortableTotalItems == 0   ? "" : showPaging(sortablePage,sortablePageSize,sortableTotalItems,keyword); 				    
					$('#sortableDisplayOptions').attr("style",(sortableTotalItems == 0 && $.isBlank(getSelectedKeyword())) ? "display:none" : "display:block");
				},
				preHook: function(){ prepareSortableList(); },
				postHook: function(){ cleanUpSortableList(); }
			});

		},

		addSortableItem = function(partNumber, index, expiry, comments) {
			var commaDelimitedNumberPattern = /^\d+(,\d+)*$/;
			if ($.trim(partNumber).length > 0 && commaDelimitedNumberPattern.test($.trim(partNumber))){
				ElevateServiceJS.addElevateByPartNumber(getSelectedKeyword(),index, expiry, comments, partNumber.split(','), {
					callback : function(data){
						updateSortableList(getSelectedKeyword(),sortablePage==0? 1 : sortablePage);	
						populateKeywordList(1);
					},
					preHook: function(){ prepareSortableList(); },
					postHook: function(){ $("#sortable-bigbets-container > .circlePreloader").remove(); }
				});
			}else{
				alert("SKU # is required and must be numeric");
			}
		};

		getSelectedKeyword = function(){
			return $("#keywordHeader").html();
		};

		getSortableDefaultAddPosition = function(){
			return ((sortablePage-1)*sortablePageSize)+1;
		};

		showAddItemOption = function (e){
			if (!isRuleLocked){
				$(e.currentTarget).qtip({
					content: {
						text: $('<div/>'),
						title: { text: 'Add Elevate Item', button: true
						}
					},
					show: {
						solo: true,
						ready: true	
					},
					style: {width: 'auto'},
					events: { 
						show: function(e, api){
							var contentHolder = $("div", api.elements.content);
							var template = $.processTemplate($("#addItemTemplate").html());

							contentHolder.html(template);

							contentHolder.find("#addOption").tabs({
								cookie: { 
									expires: 30,
									name: "ui-tab-elevate-addoption"
								}
							});

							contentHolder.find("#addItemDate").attr('id', 'addItemDate_1');
							contentHolder.find("#addItemDPNo").bind('blur', function(e) {if ($.trim($(this).val()).length == 0) $(this).val(sortablePopupAddDefaultText);});
							contentHolder.find("#addItemDPNo").bind('focus',function(e) {if ($.trim($(this).val()) == sortablePopupAddDefaultText) $(this).val("")});

							contentHolder.find("#addItemDate_1").datepicker({
								showOn: "both",
								minDate: expDateMinDate,
								maxDate: expDateMaxDate,
								buttonText: "Expiration Date",
								buttonImage: "../images/icon_calendar.png",
								buttonImageOnly: true
							});

							contentHolder.find("#addItemBtn").click(function(e){
								var dpNo = $.trim(contentHolder.find("#addItemDPNo").val().replace(/\n\r?/g, '')).replace(/ /g,'');
								var index = $.trim(contentHolder.find("#addItemPosition").val());
								var expiry = $.trim(contentHolder.find("#addItemDate_1").val());
								var comment = $.trim(contentHolder.find("#addItemComment").val().replace(/\n\r?/g, '<br />'));

								//TODO: add field validation
								addSortableItem(dpNo,index,expiry,comment);

								contentHolder.find("#addItemDPNo").val("");
								contentHolder.find("#addItemPosition").val("");
								contentHolder.find("#addItemDate_1").val("");
								$('div', api.elements.content).find("#addItemComment").val("");
							});
						},
						hide: function(event, api){
							api.destroy();
						}
					}
				}).click(function(e) { e.preventDefault(); });
			}
		};
		/*-- END --*/

		/* START
		 * Case: Switching of item display
		 * This will use cookie to persist selected display 
		 */
		setItemDisplay = function(){

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

		$("#sortableTile").click(function(e) {
			$.cookie('elevate.display', 'sortableTile',{expires: 1});
			$("#sortableList").removeClass("active");
			setItemDisplay();
		});

		$("#sortableList").click(function(e) {
			$.cookie('elevate.display', 'sortableList',{expires: 1});
			$("#sortableTile").removeClass("active");
			setItemDisplay();
		});
		/*-- END grid/list switching --*/

		/*-- START filter display --*/
		$("#filterDisplay").change(function(e){
			setItemFilter($(this).val());
		});

		getItemFilter = function(){
			var cookieFilter = $.trim($.cookie('elevate.filter'));
			var filter = $.isBlank(cookieFilter)? $("#filterDisplay").val() : cookieFilter;
			return $.isBlank(filter) ? "all" : filter;
		};

		setItemFilter = function(value){
			$.cookie('elevate.filter', value ,{expires: 1});
			$("#filterDisplay").val(value);
			updateSortableList(getSelectedKeyword(), 1);
			$("#sortable-bigbets").sortable("option", "disabled", value=="all" ? true: true );
		};
		/*-- END filter display --*/
		
		var submitForApprovalHandler = function(){
			$("a#submitForApprovalBtn").on({
				click: function(){
					if(confirm("This elevate rule will be locked for approval. Continue?"))
						DeploymentServiceJS.processRuleStatus("Elevate", getSelectedKeyword(), getSelectedKeyword(), false,{
							callback: function(data){
								populateKeywordList();
								updateSortableList();
							}
						});
				}
			});
		};
		
		$("a#downloadIcon").on({click:showDownloadOption}, {
			selector: "#downloadIcon",
			template: "#downloadTemplate",
			title: "Download Page",
			itemKeyword: getSelectedKeyword,
			filter: getItemFilter,
			itemPage: sortablePage,
			itemPageSize:sortablePageSize
		});

		/* START: Event Listener */
		
		var addEventListener = function(){
			$("a#addSortableBtn").on({
				click: showAddItemOption, 
				mouseenter: showHoverInfo
				},{locked: isRuleLocked});
			
			$("a#clearRuleBtn").on({
				mouseenter: showHoverInfo,
				click: function(e){
					if(confirm(ruleClearConfirmText))
						ElevateServiceJS.clearRule(getSelectedKeyword(), {
							callback: function(data){
								updateSortableList();
							}
						});
				}
			},{locked: isRuleLocked});
		};
		
		var init = function() {
			dwr.util.setValue("searchFilter",keywordDefaultSearchText);
			dwr.util.setValue("addSortable",sortableAddDefaultSearchText);
			setItemFilter("all");
			$("#titleText").html("Elevate List");
			showPageAuditList("#notificationList", "Elevate Activities", "Elevate", 1);
			populateKeywordList(1);
			setItemDisplay();
			submitForApprovalHandler();
		};

		init();	
	});	
})(jQuery);	
