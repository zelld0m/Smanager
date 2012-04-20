(function($){

	var keywordPageSize = 15;
	var keywordDefaultSearchText = "Enter Keyword";

	var sortableCache = { };
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
	var selectedKeyword= "";
	var defaultItemDisplay = "sortableList";
	var defaultFilterDisplay = "all";
	var errmsg_skunumeric ="SKU # should be numeric";

	var searchActivated = false;
	var oldSearch = "";
	var newSearch = "";

	$(document).ready(function() { 

		UtilityServiceJS.getStoreLabel({
			callback: function(data){ storeLabel = data; },
			errorHandler: function(message){ alert(message); }
		});		

		/* Common functions */
		getHTMLTemplate = function(selector){ return $(selector).html().replace("%%store%%",storeLabel);};
		handlerSetFieldDefaultText = function(e){if ($.trim($(this).val()).length == 0) $(this).val(e.data.defaultText);};
		handlerSetFieldEmpty = function(e){if ($.trim($(this).val()) == e.data.defaultText) $(this).val("");};

		navigateKeywords = function(page){
			showKeywordList("#keywordList", "Keyword", "Search Keyword", "Exclude", page, keywordPageSize);
		};

		removeSortableItem = function(item){
			var deleteSelector = '#sItemDelete' + formatToId(item.edp);
			if (confirm(sortableConfirmDeleteText)){  
				var edp = item.edp;

				// adjust active paging, 1 sibling because of hidden pattern used for cloning
				if (sortablePage==sortableTotalPages && sortableTotalPages > 1 && $(deleteSelector).parents("li").siblings().length==1){
					--sortablePage;
				}

				ExcludeServiceJS.removeExclude(getSelectedKeyword(),edp, {
					callback : function(data){
						$(deleteSelector).parents("li").remove();
						isDelete = 1;
						updateSortableList(getSelectedKeyword(),sortablePage==0? 1 : sortablePage);
						navigateKeywords(0);
					},
					errorHandler: function(message){ alert(message); }
				});
			}
			isDelete = 0;
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
			blur: handlerSetFieldDefaultText,
			focus: handlerSetFieldEmpty,
			keydown: handlerSearchSKU
		},{defaultText:sortableAddDefaultSearchText}
		);

		initPage = function() {
			dwr.util.setValue("searchFilter",keywordDefaultSearchText);
			dwr.util.setValue("addSortable",sortableAddDefaultSearchText);
			setItemFilter("all");
			$("#titleText").html("Exclude List");
			showPageAuditList("#notificationList", "Exclude Activities", "Exclude", 1);
			navigateKeywords(1);
			setItemDisplay();
		},

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
				showAudit(sortableItem.edp);
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
						ExcludeServiceJS.updateExpiryDate(getSelectedKeyword(),sortableItem.edp, dateText, {
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

			ExcludeServiceJS.getProducts(getItemFilter(), keyword, sortablePage, sortablePageSize,{
				callback: function(data){
					sortableTotalItems = data.totalSize;
					sortableTotalPages = Math.ceil(sortableTotalItems/sortablePageSize);

					var sortableList = data.list;

					// Delete all the rows except for the "pattern" row
					$("#sortable-bigbets-container img#no-items-img").remove();
					$('#sortable-bigbets li:not(#sItemPattern)').remove();

					// Create a new set cloned from the pattern row
					var sortableItem, id;

					//TODO: Should be configurable
					if (sortableTotalItems == 0){
						if ($.isBlank(getSelectedKeyword())){
							$("#sortable-bigbets-container").prepend('<img id="no-items-img"/>');
							$("#sortable-bigbets-container img#no-items-img").attr("src", "../images/ElevatePageisBlank.jpg");							
						}
						else{
							//TODO: no results
						}
					}

					for (var i = 0; i < sortableList.length; i++) {
						sortableItem = sortableList[i];
						id = formatToId(sortableItem.edp);
						dwr.util.cloneNode("sItemPattern", { idSuffix:id });
						refreshItemForUpdate(sortableItem);
						$("#sItemPattern" + id).attr("style", "display:block"); 		
						sortableCache[id] = sortableItem;
					};

					var pagingText = sortableTotalItems == 0   ? "" : showPaging(sortablePage,sortablePageSize,sortableTotalItems,keyword); 				    
					$('#sortableDisplayOptions').attr("style",(sortableTotalItems == 0 && $.isBlank(getSelectedKeyword())) ? "display:none" : "display:block");
				},
				preHook: function(){ prepareSortableList(); },
				postHook: function(){ $("#sortable-bigbets-container > .circlePreloader").remove(); },
				errorHandler: function(message){ alert(message); }
			});

		},

		addSortableItem = function(partNumber, index, expiry, comments) {
			if ($.trim(partNumber).length > 0 && $.isNumeric(partNumber)){
				ExcludeServiceJS.addExcludeByPartNumber(getSelectedKeyword(),partNumber,index, expiry, comments, {
					callback : function(data){
						updateSortableList(getSelectedKeyword(),sortablePage==0? 1 : sortablePage);	
						navigateKeywords(1);
					},
					preHook: function(){ prepareSortableList(); },
					postHook: function(){ $("#sortable-bigbets-container > .circlePreloader").remove(); },
					errorHandler: function(message){ alert(message);}
				});
			}else{
				alert("Part Number is required and must be numeric");
			}
		},

		getSelectedKeyword = function(){
			return $("#keywordHeader").html();
		},

		getSortableDefaultAddPosition = function(){
			return ((sortablePage-1)*sortablePageSize)+1;
		},

		/* START
		 * Case: Adding of new item
		 */
		$('#addSortableImg').qtip({
			content: {
				text: $('<div/>'),
				title: { text: 'Exclude Item', button: true
				}
			},
			events: { 
				render: function(e, api){
					var contentHolder = $("div", api.elements.content);
					contentHolder.find("#tabs").tabs();
					
					contentHolder.html(getHTMLTemplate("#addItemTemplate"));
					contentHolder.find("#addItemDate").attr('id', 'addItemDate_1');
					contentHolder.find("#addItemDPNo").val($.isNumeric($.trim($("#addSortable").val()))? $.trim($("#addSortable").val()) :sortablePopupAddDefaultText);
					contentHolder.find("#addItemDPNo").bind('blur', function(e) {if ($.trim($(this).val()).length == 0) $(this).val(sortablePopupAddDefaultText);});
					contentHolder.find("#addItemDPNo").bind('focus',function(e) {$(this).val("");});

					contentHolder.find("#addItemDate_1").datepicker({
						showOn: "both",
						minDate: expDateMinDate,
						maxDate: expDateMaxDate,
						buttonText: "Expiration Date",
						buttonImage: "../images/icon_calendar.png",
						buttonImageOnly: true
					});

					contentHolder.find("#addItemBtn").click(function(e){
						var dpNo = $.trim(contentHolder.find("#addItemDPNo").val());
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

		/*-- START comment and audit list display --*/
		prepareAuditList = function(contentHolder, idSuffix){
			contentHolder.find("#auditPagingTop" + idSuffix).html("");
			contentHolder.find("#auditPagingBottom" + idSuffix).html("");
			contentHolder.find("#auditHolder" + idSuffix).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
		};

		updateAuditList = function(contentHolder, edp, auditPage, auditPageSize){
			var idSuffix = '_' + edp;

			AuditServiceJS.getExcludeItemTrail(getSelectedKeyword(), edp, auditPage, auditPageSize, {
				callback: function(data){
					var totalItems = data.totalSize;
					var auditItems = "";

					for(var i = 0 ; i <  data.list.length ; i++){
						var auditTemplate = getHTMLTemplate("#auditTemplate" + idSuffix); 
						var item = data.list[i];

						auditTemplate = auditTemplate.replace("%%timestamp%%", item.formatDateTimeUsingConfig);
						auditTemplate = auditTemplate.replace("%%commentor%%", item.username);
						auditTemplate = auditTemplate.replace("%%comment%%", item.details);
						auditItems += auditTemplate;
					}

					contentHolder.find("#auditPagingTop" + idSuffix + ", #auditPagingBottom" + idSuffix).paginate({
						type: "short",
						pageStyle: "style2",
						currentPage: auditPage, 
						pageSize: auditPageSize,
						totalItem: totalItems,
						callbackText: function(itemStart, itemEnd, itemTotal){
							return itemStart + ' - ' + itemEnd + ' of ' + itemTotal;
						},
						pageLinkCallback: function(e){ updateAuditList(contentHolder, edp, e.data.page, auditPageSize);},
						nextLinkCallback: function(e){ updateAuditList(contentHolder, edp, e.data.page+1, auditPageSize); },
						prevLinkCallback: function(e){ updateAuditList(contentHolder, edp, e.data.page-1, auditPageSize); },
						firstLinkCallback: function(e){ updateAuditList(contentHolder, edp, 1, auditPageSize); },
						lastLinkCallback: function(e){ updateAuditList(contentHolder, edp, e.data.totalPages, auditPageSize); }
					});

					contentHolder.find("#auditHolder" + idSuffix).html(auditItems);
					contentHolder.find("#auditHolder" + idSuffix + "> div:nth-child(even)").addClass("alt");
				},
				preHook: function(){ prepareAuditList(contentHolder, idSuffix); },
				errorHandler: function(message){ alert(message); }					
			});
		};

		showAudit = function(edp){
			var id = '_' + edp;

			$('#auditIcon' + id).qtip({
				content: {
					text: $('<div/>'),
					title: { text: 'Audit Log', button: true }
				},
				position: {
					at: 'bottom right', 
					my: 'top left'
				},
				events: {
					render: function(e, api) {
						var auditPage=1;
						var auditPageSize=5;
						var contentHolder = $('div', api.elements.content);
						contentHolder.html(getHTMLTemplate("#viewAuditTemplate" + id));

						updateAuditList(contentHolder, edp, auditPage, auditPageSize);
					}
				}
			}).click(function(e) { e.preventDefault(); });	   
		};

		prepareCommentList = function(contentHolder, selector){
			contentHolder.find(selector).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
		};

		updateCommentList = function(contentHolder, edp){
			var id = '_' + edp;
			ExcludeServiceJS.getComment(getSelectedKeyword(), edp, {
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
						},
						errorHandler: function(message){ alert(message); }
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
								ExcludeServiceJS.addComment(getSelectedKeyword(), edp, comment,{
									callback: function(data){
										needRefresh = true;
									},
									preHook: function(){ prepareCommentList(contentHolder, "#commentHolder" + id); },
									postHook: function(){ 
										contentHolder.find("#newComment" + id).val(""); 
										updateCommentList(contentHolder, edp);
									},
									errorHandler: function(message){ alert(message); }
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
		};
		/*-- END comment and audit list display --*/

		/** START EVENT LISTER */

		/** Case: Download icon is clicked */
		$("#downloadIcon").on({click: showDownloadOption}, {
			selector: "#downloadIcon",
			title: "Download Page",
			filter: getItemFilter,
			pageSize:sortablePageSize
		});


		/** END EVENT LISTER */

		initPage();	
	});	
})(jQuery);	