(function($){
	var keywordDefaultSearchText = "Enter Keyword";

	var sortableTotalItems = 0;
	var sortableTotalPages = 0;
	var sortablePage = 1;
	var sortablePageSize = 6;
	var sortableAddDefaultSearchText = "Enter SKU #";
	var sortablePopupAddDefaultText = "SKU #";
	var sortableConfirmDeleteText = "Proceed with delete?";
	var isDelete = 0;
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

									if($.inArray(status,["PENDING","APPROVED"])>=0){
										base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').qtip({
											content: {
												text: $('<div/>'),
												title: { text: 'Why is this item locked?', button: true
												}
											},
											position:{
												at: 'right center',
												my: 'left center'
											},
											show:{
												modal: true
											},
											style:{width:'auto'},
											events: {
												show: function(evt, api){
													var $content = $("div", api.elements.content);
													$content.html($("#whyIsLocked").html());
												}
											}
										});
									}else{
										base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').on({
											click: function(e){
												$("#titleText").html("Elevate List for ");
												$("#keywordHeader").html(e.data.name);
												$("div#addSortableHolder").show();
												updateSortableList(e.data.name, 1);
												ruleStatus = e.data.status;
											}},{name:name, status: data});
									}
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

		removeSortableItem = function(item){
			var deleteSelector = '#sItemDelete' + formatToId(item.edp);
			if (confirm(sortableConfirmDeleteText)){  
				var edp = item.edp;

				// adjust active paging, 1 sibling because of hidden pattern used for cloning
				if (sortablePage==sortableTotalPages && sortableTotalPages > 1 && $(deleteSelector).parents("li").siblings().length==1){
					--sortablePage;
				}

				ElevateServiceJS.removeElevate(getSelectedKeyword(),edp, {
					callback : function(data){
						$(deleteSelector).parents("li").remove();
						isDelete = 1;
						updateSortableList(getSelectedKeyword(),sortablePage==0? 1 : sortablePage);
						populateKeywordList(1);
					}
				});
			}
			isDelete = 0;
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

			$('#sItemDelete' + id).click(function(e){removeSortableItem(sortableItem);});
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



			/* Case: Put back elevate position when focus leave the field*/
			$('#sItemPosition' + id).blur(function(e){
				//$(this).val($(this).parents("li").index()+1) + ((sortablePage-1)*sortablePageSize);
			});

			if (sortableItem.isExpired)
				$("#sItemValidityText" + id).html('<img src="../images/expired_stamp50x16.png">');
			//$("#sItemStampExp" + id).html('<img src="../images/expired_stamp.png">');

			$("#sItemImg" + id).error(function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); });

			if ($.trim(sortableItem.dpNo).length > 0){
				showComment(sortableItem.edp);				
				showAuditList(sortableItem.edp);
			}

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
				disabled: disableCalendar,
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
			$("#sortable-bigbets-container > .circlePreloader").remove();
			$("#sortable-bigbets-container img#no-items-img").remove();
			$('#sortable-bigbets li:not(#sItemPattern)').remove();
			$('#sortablePagingTop').html("");
			$('#sortablePagingBottom').html("");
			$("#sortable-bigbets-container").prepend('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>');
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
				postHook: function(){ $("#sortable-bigbets-container > .circlePreloader").remove(); }
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

		/**
		 * Case: Adding of new item
		 */
		$('#addSortableImg').qtip({
			content: {
				text: $('<div/>'),
				title: { text: 'Elevate Item', button: true
				}
			},
			events: { 
				render: function(e, api){
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
				}
			}
		}).click(function(e) { e.preventDefault(); });
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

		prepareCommentList = function(contentHolder, selector){
			contentHolder.find(selector).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
		};

		updateCommentList = function(contentHolder, edp){
			var id = '_' + edp;
			ElevateServiceJS.getComment(getSelectedKeyword(), edp, {
				callback: function(comment){
					var commentItems = "";
					CommentServiceJS.parseComment(comment, {
						callback: function(data){

							for(var i = 0 ; i < data.list.length; i++){
								var commentTemplate = getHTMLTemplate("#commentTemplate" + id); 
								var item = data.list[i];
								if (i%0==0) $(commentTemplate).find("div#commentTemplate" + id + " div").addClass("altBg"); 
								commentTemplate = commentTemplate.replace("%%timestamp%%",item.date);
								commentTemplate = commentTemplate.replace("%%commentor%%",item.username);
								commentTemplate = commentTemplate.replace("%%comment%%",item.comment);
								commentItems += commentTemplate;
							}

						},
						preHook: function(){ prepareCommentList(contentHolder, "#commentHolder" + id); },
						postHook: function(){ 
							contentHolder.find("#newComment" + id).val("");
							contentHolder.find("#commentHolder" + id).html(commentItems);
							contentHolder.find("#commentHolder" + id + "> div:nth-child(even)").addClass("alt");
						}
					});
				}
			});
		};

		showComment = function(edp){
			var id = '_' + edp;
			var needRefresh = false;

			$('#commentIcon' + id).qtip({
				content: {
					text: $('<div/>'),
					title: { text: 'Comments', button: true }
				},
				events: {
					render: function(e, api) {
						var contentHolder = $('div', api.elements.content);

						contentHolder.html(getHTMLTemplate("#addCommentTemplate" + id));
						updateCommentList(contentHolder, edp);

						contentHolder.find("#addCommentBtn" + id).click(function(e){

							var comment = $.trim(contentHolder.find("#newComment" + id).val().replace(/\n\r?/g, '<br/>'));
							if ($.trim(comment).length>0)
								ElevateServiceJS.addComment(getSelectedKeyword(), edp, comment,{
									callback: function(data){
										needRefresh = true;
									},
									preHook: function(){ prepareCommentList(contentHolder, "#commentHolder" + id); },
									postHook: function(){ 
										contentHolder.find("#newComment" + id).val(""); 
										updateCommentList(contentHolder, edp);
									}
								});			 						

						});
					},
					hide: function (e, api){
						if(needRefresh)
							updateSortableList(getSelectedKeyword(), sortablePage);
					}
				}
			}).click(function(e) { e.preventDefault(); });
		};
		/*-- END comment and audit list display --*/

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

		//Case: Download icon is clicked
		$("#downloadIcon").on("click",{
			selector: "#downloadIcon",
			template: "#downloadTemplate",
			title: "Download Page",
			itemKeyword: getSelectedKeyword,
			filter: getItemFilter,
			itemPage: sortablePage,
			itemPageSize:sortablePageSize
		}, showDownloadOption);

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
		
		var clearRuleHandler = function(){
			$("div#clearRule > #clearRuleBtn").on({
				click: function(){
					if(confirm("This will remove all items associated to this rule. Continue?"))
						ElevateServiceJS.clearRule(getSelectedKeyword(), {
							callback: function(data){
								updateSortableList();
							}
						});
				}
			});
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
			clearRuleHandler();
		};

		init();	
	});	
})(jQuery);	
