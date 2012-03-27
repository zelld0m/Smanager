/**
 * Common functions for bigbets tab
 */
(function($){
	var gStoreLabel = "";
	
	$(document).ready(function() { 
		
		UtilityServiceJS.getStoreLabel({
			callback: function(data){ gStoreLabel = data; },
			errorHandler: function(message){ alert(message); }
		});
		
		getHTMLTemplate = function(selector){ 
			return $(selector).html().replace("%%store%%", gStoreLabel);
		};

		setFieldDefaultTextHandler = function(e){
			if ($.trim($(this).val()).length == 0) 
				$(this).val(e.data.text);
		};

		setFieldEmptyHandler = function(e){
			if ($.trim($(this).val()) == e.data.text) 
				$(this).val("");
		};
		
		/** Enumerate all keywords */
		showKeywordList = function(selector, headerText, searchText, type, moduleType, itemPage, itemPageSize){
			
			showItem = function(keyword){
				$("#titleText").html("" + moduleType + " List for ");
				$("#keywordHeader").html(keyword);
				$("div#addSortableHolder").show();
				updateSortableList(keyword, 1);
			};
			
			$(selector).sidepanel({
				type: type,
				module: moduleType,
				headerText : headerText,
				searchText : searchText,
				page: itemPage,
				pageSize: itemPageSize,

				itemNameCallback: function(e){ showItem(e.data.name);},
				itemCountCallback: function(e){ showItem(e.data.name);},

				itemDataCallback: function(base, keyword, page){
					StoreKeywordServiceJS.getAllByKeyword(keyword, page, base.options.pageSize,{
						callback: function(data){
							base.populateList(data);
							base.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
					});
				},
				
				itemAddCallback: function(base, keyword){
					StoreKeywordServiceJS.addKeyword(keyword,{
						callback : function(data){
							base.getList(keyword, 1);
							alert(headerText + ' "' + keyword + '" added successfully.');
						},
						errorHandler: function(message){ alert(message); }
					});
				}
			});
		};
		
		/** Show download option as qtip2 */
		showDownloadOption = function(e){
			$(e.data.selector).qtip({
				content: {
					text: $('<div/>'),
					title: { text: e.data.title, button: true
					}
				},
				events: {  
					render: function(rdEvent, api){
						var content = $("div", api.elements.content);
						content.html(getHTMLTemplate(e.data.template));
					
						content.find("#downloadBtn").on("click", {}, function(dlEvent){
							var params = new Array();
							var page = content.find('select[name="page"] option:selected').val();
							var url = document.location.href + "/xls";
							var urlParams = "";
							var count = 0;
							
							params["filename"] = content.find('input[name="filename"]').val().replace(/ /g,"_");
							params["type"] = content.find('select[name="type"] option:selected').val();
							
							params["keyword"] = e.data.itemKeyword.call();
							params["page"] = (page=="current") ? e.data.itemPage : page;
							params["filter"] = e.data.filter.call();
							params["itemperpage"] = e.data.itemPageSize;

							for(var key in params){
								if (count>0) urlParams +='&';
								urlParams += (key + '=' + params[key]);
								count++;
							};

							document.location.href = url + '?' + urlParams; 				
						});

					}
				}

			});
		};
		
		/** Show audit trail inside qtip2 */
		showAuditList = function(edp){
			var id = '_' + edp;

			$('#auditIcon' + id).qtip({
				content: {
					text: $('<div/>'),
					title: { text: 'Audit Log', button: true }
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
		
		prepareAuditList = function(contentHolder, idSuffix){
			contentHolder.find("#auditPagingTop" + idSuffix).html("");
			contentHolder.find("#auditPagingBottom" + idSuffix).html("");
			contentHolder.find("#auditHolder" + idSuffix).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
		};

		updateAuditList = function(contentHolder, edp, auditPage, auditPageSize){
			var idSuffix = '_' + edp;
			
			AuditServiceJS.getElevateItemTrail(getSelectedKeyword(), edp, auditPage, auditPageSize, {
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
						currentPage: auditPage, 
						pageSize: auditPageSize,
						totalItem: totalItems,
						type: "short",
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
		
	});
	
})(jQuery);	