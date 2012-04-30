/**
 * Common functions for bigbets tab
 */
(function($){
	var itemAuditPageSize= 5;
	var pageAuditPageSize = 5;
	var gStoreLabel = "";

	$(document).ready(function() { 

		UtilityServiceJS.getStoreLabel({
			callback: function(data){ gStoreLabel = data; }
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

		/** Enumerate all audit for current page*/
		showPageAuditList = function(selector, headerText, moduleType, itemPage){
			$(selector).auditpanel({
				headerText : headerText,
				page: itemPage,
				pageSize: pageAuditPageSize,
				itemDataCallback: function(base, page){
					if ($.trim(moduleType).toLowerCase()==="elevate")
						AuditServiceJS.getElevateActivity(page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});

					if ($.trim(moduleType).toLowerCase()==="exclude")
						AuditServiceJS.getExcludeActivity(page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
				},
				itemOptionCallback: function(base, id, name, model){
					var selector = '#item' + $.escapeQuotes($.formatAsId(id)); 

					base.$el.find(selector + ' .user').html(model["username"]);
					base.$el.find(selector + ' .changedesc').html(model["keyword"]+ " " + model["details"]);
					base.$el.find(selector + ' .page').html(model["elapsedTime"]);
				}
			});
		};

		/** Enumerate all keywords */
		showKeywordList = function(selector, headerText, searchText, moduleType, itemPage, itemPageSize){

			showItem = function(e){
				$("#titleText").html("" + moduleType + " List for ");
				$("#keywordHeader").html(e.data.name);
				$("div#addSortableHolder").show();
				updateSortableList(e.data.name, 1);
				ruleStatus = e.data.status;
			};

			$(selector).sidepanel({
				fieldId: "keywordId",
				fieldName: "keyword",
				headerText : headerText,
				searchText : searchText,
				page: itemPage,
				pageSize: itemPageSize,

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

					if ($.trim(moduleType).toLowerCase()==="elevate"){
						DeploymentServiceJS.getRuleStatus("elevate", name, {
							callback:function(data){
								var status = (data==null) ? "" : data["approvalStatus"];
								ElevateServiceJS.getElevatedProductCount(name,{
									callback: function(count){
										var totalText = (count == 0) ? "-" : count; 
										base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html(totalText);

										switch (status){
										case "REJECTED": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').append(" - For Immediate Action"); break;
										case "PENDING": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').append(" - Awaiting Approval"); break;
										case "APPROVED": base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').append(" - Ready For Production"); break;
										}

										if($.inArray(status,["PENDING","APPROVED"])>=0){
											base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').qtip({
												content: "<div/>",
												show:{
													modal: true
												},
												events: {
													show: function(evt, api){
														var $content = $("div", api.elements.content);
														$content.html($("#whyIsLocked").html());
													}
												}
											});
										}else{
											base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').on({click:showItem},{name:name, status: data});
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
					}

					if ($.trim(moduleType).toLowerCase()==="exclude")
						ExcludeServiceJS.getExcludedProductCount(name,{
							callback: function(count){
								var totalText = (count == 0) ? "-" :(count == 1) ? "1 Item" : count + " Items"; 
								base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html(totalText);
								base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').on({click:showItem},{name:name});
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
							case -1: 
								alert("Duplicate entry for '" + keyword + "'.");
								break;
							case 0: 
								alert("Error encountered while adding '" + keyword + "'.");
								break;
							default: 
								base.getList(keyword, 1);
							alert(headerText + ' "' + keyword + '" added successfully.');
							};
						},
						errorHandler: function(message){ alert(message); }
					});
				}
			});
		};

		/** Show download option as qtip2 */
		showDownloadOption = function(e){
			var data = e.data;

			$(this).qtip({
				content: {
					text: $('<div/>'),
					title: { text: data.title, button: true
					}
				},
				style:{
					width: 'auto'
				},
				show:{
					solo: true,
					modal: true,
					ready: true
				},
				events: {  
					show: function(event, api){
						var content = $("div", api.elements.content);
						content.html(getHTMLTemplate(data.template));

						content.find("#downloadBtn").on("click", {}, function(dlEvent){
							var params = new Array();
							var page = content.find('select[name="page"] option:selected').val();
							var url = document.location.href + "/xls";
							var urlParams = "";
							var count = 0;

							params["filename"] = content.find('input[name="filename"]').val().replace(/ /g,"_");
							params["type"] = content.find('select[name="type"] option:selected').val();

							params["keyword"] = data.keyword;
							params["page"] = (page=="current") ? data.itemPage : page;
							params["filter"] = data.filter.call();
							params["itemperpage"] = data.itemPageSize;

							for(var key in params){
								if (count>0) urlParams +='&';
								urlParams += (key + '=' + params[key]);
								count++;
							};

							document.location.href = url + '?' + urlParams; 				
						});
					},
					hide: function(event, api){
						api.destroy();
					}
				}
			});
		};

		/** Show audit trail inside qtip2 */
		showAuditList = function(e){
			$(this).qtip({
				id: "show-audit",
				content: {
					text: $('<div/>'),
					title: { text: 'Item Audit', button: true }
				},
				style:{
					width: 'auto'
				},
				show:{
					solo: true,
					ready: true
				},
				events: {
					show: function(event, api) {
						var contentHolder = $('div', api.elements.content);
						if(e.data.type==="Elevate" || e.data.type==="Exclude") {
							contentHolder.html(getHTMLTemplate("#viewAuditTemplate" + $.formatAsId(e.data.item["edp"])));
						}
						else {
							contentHolder.html(getHTMLTemplate("#viewAuditTemplate"));							
						}
						updateAuditList(e, contentHolder, 1, itemAuditPageSize);
					},
					hide: function(event,api){
						api.destroy();
					}
				}
			});	   
		};

		var prepareAuditList = function(contentHolder, idSuffix){
			contentHolder.find("#auditPagingTop" + idSuffix).html("");
			contentHolder.find("#auditPagingBottom" + idSuffix).html("");
			contentHolder.find("#auditHolder" + idSuffix).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
		};

		var updateAuditList = function(e, contentHolder, auditPage, auditPageSize){
			var edp = "";
			var idSuffix = "";
			var ruleId = "";
			
			if(e.data.type==="Elevate" || e.data.type==="Exclude") {
				edp = e.data.item["edp"];
				idSuffix = $.formatAsId(edp);
			}
			else {
				ruleId = e.data.ruleRefId;
			}
			

			if(e.data.type==="Elevate"){
				AuditServiceJS.getElevateItemTrail(e.data.name, edp, auditPage, auditPageSize, {
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
							pageLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page, auditPageSize);},
							nextLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page+1, auditPageSize); },
							prevLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page-1, auditPageSize); },
							firstLinkCallback: function(evt){ updateAuditList(e,contentHolder, 1, auditPageSize); },
							lastLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.totalPages, auditPageSize); }
						});

						contentHolder.find("#auditHolder" + idSuffix).html(auditItems);
						contentHolder.find("#auditHolder" + idSuffix + "> div:nth-child(even)").addClass("alt");
					},
					preHook: function(){ prepareAuditList(contentHolder, idSuffix); }		
				});
			}

			if(e.data.type==="Exclude"){
				AuditServiceJS.getExcludeItemTrail(e.data.name, edp, auditPage, auditPageSize, {
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
							pageLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page, auditPageSize);},
							nextLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page+1, auditPageSize); },
							prevLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page-1, auditPageSize); },
							firstLinkCallback: function(evt){ updateAuditList(e,contentHolder, 1, auditPageSize); },
							lastLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.totalPages, auditPageSize); }
						});

						contentHolder.find("#auditHolder" + idSuffix).html(auditItems);
						contentHolder.find("#auditHolder" + idSuffix + "> div:nth-child(even)").addClass("alt");
					},
					preHook: function(){ prepareAuditList(contentHolder, idSuffix); }		
				});
			}
			
			if(e.data.type==="Query Cleaning"){
				AuditServiceJS.getRedirectTrail(e.data.ruleRefId, auditPage, auditPageSize, {
					callback: function(data){
						var totalItems = data.totalSize;
						var auditItems = "";

						for(var i = 0 ; i <  data.list.length ; i++){
							var auditTemplate = getHTMLTemplate("#auditTemplate"); 
							var item = data.list[i];

							auditTemplate = auditTemplate.replace("%%timestamp%%", item.formatDateTimeUsingConfig);
							auditTemplate = auditTemplate.replace("%%commentor%%", item.username);
							auditTemplate = auditTemplate.replace("%%comment%%", item.details);
							auditItems += auditTemplate;
						}

						contentHolder.find("#auditPagingTop, #auditPagingBottom").paginate({
							type: "short",
							pageStyle: "style2",
							currentPage: auditPage, 
							pageSize: auditPageSize,
							totalItem: totalItems,
							callbackText: function(itemStart, itemEnd, itemTotal){
								return itemStart + ' - ' + itemEnd + ' of ' + itemTotal;
							},
							pageLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page, auditPageSize);},
							nextLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page+1, auditPageSize); },
							prevLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page-1, auditPageSize); },
							firstLinkCallback: function(evt){ updateAuditList(e,contentHolder, 1, auditPageSize); },
							lastLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.totalPages, auditPageSize); }
						});

						contentHolder.find("#auditHolder").html(auditItems);
						contentHolder.find("#auditHolder> div:nth-child(even)").addClass("alt");
					},
					preHook: function(){ prepareAuditList(contentHolder); }
				});
			}

			if(e.data.type==="Ranking Rule"){
				AuditServiceJS.getRelevancyTrail(e.data.ruleRefId, auditPage, auditPageSize, {
					callback: function(data){
						var totalItems = data.totalSize;
						var auditItems = "";

						for(var i = 0 ; i <  data.list.length ; i++){
							var auditTemplate = getHTMLTemplate("#auditTemplate"); 
							var item = data.list[i];

							auditTemplate = auditTemplate.replace("%%timestamp%%", item.formatDateTimeUsingConfig);
							auditTemplate = auditTemplate.replace("%%commentor%%", item.username);
							auditTemplate = auditTemplate.replace("%%comment%%", item.details);
							auditItems += auditTemplate;
						}

						contentHolder.find("#auditPagingTop, #auditPagingBottom").paginate({
							type: "short",
							pageStyle: "style2",
							currentPage: auditPage, 
							pageSize: auditPageSize,
							totalItem: totalItems,
							callbackText: function(itemStart, itemEnd, itemTotal){
								return itemStart + ' - ' + itemEnd + ' of ' + itemTotal;
							},
							pageLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page, auditPageSize);},
							nextLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page+1, auditPageSize); },
							prevLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.page-1, auditPageSize); },
							firstLinkCallback: function(evt){ updateAuditList(e,contentHolder, 1, auditPageSize); },
							lastLinkCallback: function(evt){ updateAuditList(e,contentHolder, evt.data.totalPages, auditPageSize); }
						});

						contentHolder.find("#auditHolder").html(auditItems);
						contentHolder.find("#auditHolder> div:nth-child(even)").addClass("alt");
					},
					preHook: function(){ prepareAuditList(contentHolder); }
				});
			};
		};

		var prepareCommentList = function(contentHolder, selector){
			contentHolder.find(selector).html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
		};

		var updateCommentList = function(contentHolder, e){
			var edp = e.data.item["edp"];
			var id = $.formatAsId(edp);

			if (e.data.type==="Elevate"){
				ElevateServiceJS.getComment(e.data.name, edp, {
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
			}

			if (e.data.type==="Exclude"){
				ExcludeServiceJS.getComment(e.data.name, edp, {
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
			}
		};

		showCommentList = function(e){
			var data = e.data;
			var edp = data.item.edp;
			var id = $.formatAsId(edp);

			$(this).qtip({
				id: "show-comment",
				content: {
					text: $('<div/>'),
					title: { text: 'Item Comments', button: true }
				},
				style:{
					width: 'auto'
				},
				show:{
					solo: true,
					ready: true
				},
				events: {
					show: function(event, api) {
						var contentHolder = $('div', api.elements.content);
						contentHolder.html($("#addCommentTemplate" + id).html());
						updateCommentList(contentHolder, e);

						if(data.locked) contentHolder.find("#newComment" + id).attr("readonly","readonly");

						contentHolder.find("#addCommentBtn" + id).on({click:function(event){
							if(data.locked) return;
							var comment = $.trim(contentHolder.find("#newComment" + id).val().replace(/\n\r?/g, '<br/>'));
							if ($.isNotBlank(comment)){
								if(data.type==="Elevate"){
									ElevateServiceJS.addComment(getSelectedKeyword(), edp, comment,{
										callback: function(data){

										},
										preHook: function(){ 
											prepareCommentList(contentHolder, "#commentHolder" + id); 
										},
										postHook: function(){ 
											contentHolder.find("#newComment" + id).val(""); 
											updateCommentList(contentHolder, e);
										}
									});
								}
								if(data.type==="Exclude"){
									ExcludeServiceJS.addComment(data.name, edp, comment,{
										callback: function(data){

										},
										preHook: function(){ 
											prepareCommentList(contentHolder, "#commentHolder" + id); 
										},
										postHook: function(){ 
											contentHolder.find("#newComment" + id).val(""); 
											updateCommentList(contentHolder, e);
										}
									});
								}
							}
						},
						mouseenter: showHoverInfo
						},{locked: data.locked});

						contentHolder.find("#clearCommentBtn" + id).on({click:function(e){
							if(data.locked) return;
							contentHolder.find("#newComment" + id).val("");
						},
						mouseenter: showHoverInfo
						},{locked: data.locked});
					},
					hide: function (event, api){
						api.destroy();
					}
				}
			});
		};
	});

})(jQuery);	