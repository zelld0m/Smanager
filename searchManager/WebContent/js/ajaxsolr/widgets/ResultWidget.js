(function ($) {

	AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest: function () {
			$(this.target).html(AjaxSolr.theme('showAjaxLoader',"Please wait..."));
			$('#canvasContainer').hide();
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty(); 

			var keyword = $.trim(self.manager.store.values('q'));
			var hasKeyword = $.isNotBlank(keyword);

			animatedTagCloud = function(){
				if (!$('#tagCanvas').tagcanvas({
					textFont: null,
					textColour: null,
					weight: true,
					outlineThickness : 1,
					maxSpeed : 0.05,
					depth : 0.8,
					reverse: true,
					freezeActive: true,
					shape: "sphere"
				}, 'tagContainer')) {
					$('#canvasContainer').hide();
				}
			};
			
			if (!hasKeyword){
				$(self.target).empty(); 
				$('#canvasContainer').show();
				$ul = $('ul#tagList');
				$ul.empty();

				StoreKeywordServiceJS.getAll({
					callback: function(data){
						var list = data.list;
						var total = data.totalSize;
						var colorCSSList= ["fColorOne","fColorTwo","fColorThree","fColorFour"];
						var sizeCSSList = ["fontxSmall","fontSmall","fontMedium","fontLarge"];

						for (var i=0; i < total; i++){
							var randomColor = colorCSSList[Math.floor(Math.random()*colorCSSList.length)];
							var randomSize = sizeCSSList[Math.floor(Math.random()*sizeCSSList.length)];
							$ul.append('<li><a class="' + randomColor + ' ' + randomSize + '" href="javascript:void(0)">' + list[i].keyword.keyword + '</a></li>');	
						}
						
						$('ul#tagList > li > a').click(function(e){
							$('#canvasContainer').hide();
							self.manager.store.addByValue('q',$.trim($(e.target).html()));
							self.manager.doRequest(0);
						});
						
						animatedTagCloud();
					},
					errorHandler: function(message){ alert(message); }
				});

				
			}
			else{
				if (this.manager.response.response.docs.length == 0){
					$(this.target).append(AjaxSolr.theme('noSearchResult', keyword));
				}else{

					for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++) {
						var doc = this.manager.response.response.docs[i];
						var debug = this.manager.response.debug.explain[doc.EDP]; 
						$(this.target).append(AjaxSolr.theme('result', i, hasKeyword,doc, AjaxSolr.theme('snippet', doc), this.auditHandler(doc), this.docHandler(doc), this.debugHandler(doc), this.featureHandler(keyword,doc), this.elevateHandler(keyword,doc), this.excludeHandler(keyword,doc)));

						if (doc.Expired != undefined)
							$(this.target).find("li#resultItem_" + doc.EDP + " div#expiredHolder").attr("style","display:float");

						$(this.target).find("li#resultItem_" + doc.EDP + " div.itemImg img").error(function(){
							$(this).unbind("error").attr("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image.jpg'));
						});

						if (i+1 == l){
							$(this.target).wrapInner("<ul class='searchList'>");
						}
					}

				}
			}
		},

		facetLinks: function (facet_field, facet_values) {
			var links = [];
			if (facet_values) {
				for (var i = 0, l = facet_values.length; i < l; i++) {
					links.push(AjaxSolr.theme('facet_link', facet_values[i], this.facetHandler(facet_field, facet_values[i])));
				}
			}
			return links;
		},

		facetHandler: function (facet_field, facet_value) {
			var self = this;
			return function () {
				self.manager.store.remove('fq');
				self.manager.store.addByValue('fq', facet_field + ':' + AjaxSolr.Parameter.escapeValue(facet_value));
				self.manager.doRequest(0);
				return false;
			};
		},

		auditHandler: function (doc) {
			var self = this;

			return function () {
				prepareAuditList = function(contentHolder){
					contentHolder.find("#auditPagingTop").html("");
					contentHolder.find("#auditPagingBottom").html("");
					contentHolder.find("#auditHolder").html('<div class="circlePreloader"><img src="../images/ajax-loader-circ25x25.gif"></div>');
				};

				updateAuditList = function(contentHolder, edp, auditPage, auditPageSize){
					var idSuffix = '_' + edp;

					AuditServiceJS.getItemTrail(edp, auditPage, auditPageSize, {
						callback: function(data){
							var totalItems = data.totalSize;
							var auditItems = "";

							for(var i = 0 ; i <  data.list.length ; i++){
								var auditTemplate = $("#auditTemplate").html();

								var item = data.list[i];

								auditTemplate = auditTemplate.replace("%%timestamp%%", item.formatDateTimeUsingConfig);
								auditTemplate = auditTemplate.replace("%%commentor%%", item.username);
								auditTemplate = auditTemplate.replace("%%comment%%", item.details);
								auditItems += auditTemplate;
							}

							if (totalItems > 0){
								contentHolder.find("#auditPagingTop, #auditPagingBottom").paginate({
									type: 'short',
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
							}else{
								auditItems ="<div><center>No available audit trail</center></div>";
							}

							contentHolder.find("#auditHolder").html(auditItems);
							contentHolder.find("#auditHolder > div:nth-child(even)").addClass("alt");
						},
						preHook: function(){ prepareAuditList(contentHolder); },
						errorHandler: function(message){ alert(message); }					
					});
				};

				var selector  = "#resultItem_" + doc.EDP + " div#auditHolder";
				var title = "Audit Trail for " + doc.DPNo;

				$(selector).qtip({
					content: {
						text: $('<div/>'),
						title: {
							text: title,
							button: true
						}
					},
					position: {
						my: 'top center',
						at: 'bottom center'
					},
					events: {
						render: function(event, api) {
							var auditPage=1;
							var auditPageSize=5;
							var contentHolder = $('div', api.elements.content);
							contentHolder.html($("#viewAuditTemplate").html());

							updateAuditList(contentHolder, doc.EDP, auditPage, auditPageSize);
						}
					}
				}).click(function(event) { event.preventDefault(); });	 
			};
		},

		debugHandler: function (doc) {
			var self = this;

			return function () {

				var selector  = "#resultItem_" + doc.EDP + " div#debugHolder";
				var title = "Scoring Details for Item " + doc.DPNo;
				var content = self.manager.response.debug.explain[doc.EDP]; 

				$(selector).qtip({
					content: {
						text: $('<div/>'),
						title: {
							text: title,
							button: true
						}
					},
					position: {
						my: 'bottom center',
						at: 'top center'
					},
					events: {
						render: function(event, api) {
							$('div', api.elements.content).html(AjaxSolr.theme('formatDebug', content));
						}
					}
				}).click(function(event) { event.preventDefault(); });	 
			};
		},

		docHandler: function (doc) {
			var self = this;

			return function () {

				var selector  = "#resultItem_" + doc.EDP + " div#docHolder";
				var title = "Schema Details for Item " + doc.DPNo;

				$(selector).qtip({
					content: {
						text: $('<div/>'),
						title: {
							text: title,
							button: true
						}
					},
					position: {
						my: 'bottom center',
						at: 'top center'
					},
					events: {
						render: function(event, api) {
							content = $('div', api.elements.content);
							content.html(AjaxSolr.theme('displayDoc', doc));

							SearchableList(content);

							content.find('a.attributes').click(function(event) {
								var field = $(this).parent().find(".attribField").val();
								var value = AjaxSolr.Parameter.escapeValue($(this).parent().find(".attribValue").val());
								self.manager.store.addByValue('fq', field + ':' + value);
								self.manager.doRequest(0);
							});
						}
					}
				}).click(function(event) { event.preventDefault(); });
			};
		},

		featureHandler: function (keyword, doc) {
			var self = this;

			return function () {

			};
		},

		elevateHandler: function (keyword,doc) {

			var self = this;
			var selector  = "#resultItem_" + doc.EDP + " div#elevateHolder";
			var title = "Elevate Product";
			var content = AjaxSolr.theme('createConfirmDialog', doc, title, "<h2 class='confirmTitle'>Review Elevate Info</h2>"); 
			var needRefresh = false;
			var elevated = doc.Elevate == undefined && doc.Expired == undefined ? false : true;
			var maxPosition = 0;
			var currentExpiryDate = "";
			var currentPosition = 0;
			var expiredDateSelected = false;
			var idSuffix = "_" + doc.EDP;
			var noExpiryDateText = "Indefinite";
			
			return function () {
				prepareElevateResult = function (contentHolder){
					contentHolder.find("#toggleItems > ul.listItems > :not(#listItemsPattern)").remove();
					contentHolder.find("#toggleItems > ul.listItems").append(AjaxSolr.theme('showAjaxLoader',"Please wait..."));
				};

				updateElevateResult = function(contentHolder, doc, keyword){
					//if(contentHolder.find("div#current").is(':visible')){
						ElevateServiceJS.getAllElevatedProducts(keyword, 0, 0,{
							callback: function(data){
								var list = data.list;

								contentHolder.find("#toggleItems > ul#listItems_" + doc.EDP + " > :not(#listItemsPattern)").remove();

								for (var i=0; i<data.totalSize; i++){
									var id = "_" + list[i].edp;
									dwr.util.cloneNode("listItemsPattern", {idSuffix: id});
									contentHolder.find("#listItemsPattern" + id).attr("style", "display:block");
									contentHolder.find("#listItemsPattern" + id + " > div > img").attr("src", list[i].imagePath);

									if (i%2==0) contentHolder.find("#listItemsPattern" + id).addClass("alt");
									if (list[i].dpNo == contentHolder.find("#aPartNo_" + doc.EDP).html()) contentHolder.find("#listItemsPattern" + id).addClass("selected");

									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#elevatePosition" + id).html(list[i].location);
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#partNo" + id).html(list[i].dpNo);
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#mfrNo" + id).html(list[i].mfrPN);
									var expiryDate = list[i].formattedExpiryDate;
									
									if (list[i].isExpired){
										contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#validityText" + id).html('<img id="stampExpired' + id + '" src="../images/expired_stamp50x16.png">');
										//contentHolder.find("#listItemsPattern" + id + " > div > div#stampExpired" + id).show();
									}else{
										contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#validityText" + id).html("Validity:");
										//contentHolder.find("#listItemsPattern" + id + " > div > div#stampExpired" + id).hide();
									}
										
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#expiryDate" + id).html($.isBlank(expiryDate)? noExpiryDateText : expiryDate);
								
									

									contentHolder.find("#listItemsPattern" + id + " > div > img#productImage" + id).error(function(){
										$(this).unbind("error").attr("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image60x60.jpg'));
									});

									// delete icon is clicked
									contentHolder.find("#listItemsPattern" + id + " > div > div > a#deleteIcon" + id).click({edp: list[i].edp},function(e){
										var edp = e.data.edp;
										if (confirm("Continue?")){
											ElevateServiceJS.removeElevate(keyword, edp, {
												callback : function(data){
													contentHolder.find("a#removeBtn").attr("style","display:none");
													contentHolder.find("#aElevatePosition_"+edp).val("");
													contentHolder.find("#aExpiryDate_"+edp).val("");
													maxPosition--;
													needRefresh = true;
												},
												preHook: function() { prepareElevateResult(contentHolder); },
												postHook: function() { updateElevateResult(contentHolder, doc, keyword); },
												errorHandler: function(message){ alert(message); }
											});
										}
									});
								}

								contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(even)').addClass("alt");
								contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(odd)').removeClass("alt");
							},
							preHook: function() {
								prepareElevateResult(contentHolder);
							},
							postHook: function() {
								var updatedPosition = parseInt($.trim(contentHolder.find("li#elevatePosition_" + doc.EDP).html()));
								var updatedExpiryDate = $.trim(contentHolder.find("li#expiryDate_" + doc.EDP).html());
								var stampVisible = contentHolder.find("img#stampExpired_" + doc.EDP).is(":visible");

								if(updatedPosition != "" && updatedPosition > 0){
									contentHolder.find("#aElevatePosition_"+doc.EDP).val(updatedPosition);
									contentHolder.find("a#removeBtn").attr("style","display:float");
								}
								
								if(updatedExpiryDate != "")
									contentHolder.find("#aExpiryDate_" + doc.EDP).val($.isDate("mm/dd/yy", updatedExpiryDate) ? updatedExpiryDate : "");

								if (stampVisible){
									contentHolder.find("#aStampExpired_" + doc.EDP).show();
								}else{
									contentHolder.find("#aStampExpired_" + doc.EDP).hide();
								}
							}
						});
				//	}
				};

				$(selector).qtip({
					content: {
						text: $('<div/>'),
						title: { text: title, button: true }
					},
					position: {
						my: 'left center',
						at: 'top center'
					},
					style: {
						width: "auto"
					},
					show: {
						modal:  true
					},
					events: {
						render: function(event, api) {
							var contentHolder = $('div', api.elements.content);

							contentHolder.html(content);

							var expDateMinDate = -2;
							var expDateMaxDate = "+1Y";

							contentHolder.find("#aExpiryDate_"+doc.EDP).datepicker({
								showOn: "both",
								minDate: expDateMinDate,
								maxDate: expDateMaxDate,
								buttonText: "Expiration Date",
								buttonImage: "../images/icon_calendar.png",
								buttonImageOnly: true,
								onSelect: function(dateText, inst) {
									var today = new Date();
									var selDate = Date.parse(dateText);
									today = Date.parse(today.getMonth()+1+'/'+today.getDate()+'/'+today.getFullYear());
									expiredDateSelected = (selDate < today)? true : false;
								}
							});

							// Set maximum elevate position
							ElevateServiceJS.getElevatedProductCount(keyword, {
								callback: function(count){
									switch(count){
									case 0:
									case 1:
									default:
										maxPosition = count > 0 ? (count+1): 1;	
									}	
								}
							});
							
							// Toggle current elevation list
//							contentHolder.find("a#toggleCurrent").click(function(event){
//								contentHolder.find("div#current").toggle("slow", function() {
									if(contentHolder.find("div#current").is('not(:visible)')){
										contentHolder.find("a#toggleCurrent>img").attr("src", "../images/btnTonggleShow.png");
									}else{
										contentHolder.find("a#toggleCurrent>img").attr("src", "../images/btnTonggleHide.png");

										needRefresh = updateElevateResult(contentHolder, doc, keyword) || needRefresh;

										// add draggable feature
										contentHolder.find("#listItems_" + doc.EDP).sortable({ 
											handle : '.handle',
											cursor : 'move',
											start: function(event, ui) {
												ui.item.data('start_pos', ui.item.index());
											},     
											change: function(event, ui) {
												var index = ui.placeholder.index();
												if (ui.item.data('start_pos') < index){
													contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(' + index + ')').addClass('sortableHighlights');
												}else{
													contentHolder.find('#listItems_' + doc.EDP + ' > li:eq(' + (index + 1) + ')').addClass('sortableHighlights');
												}
											},
											update: function(event, ui) {
												var ref = ui.item.attr("id").split('_')[1];
												contentHolder.find('#listItems_' + doc.EDP + ' > li').removeClass('sortableHighlights');
												contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(even)').addClass("alt");
												contentHolder.find('#listItems_' + doc.EDP + ' > li:nth-child(odd)').removeClass("alt");
											},
											stop: function(event, ui) {
												var sourceIndex = (ui.item.data('start_pos')+1) ;
												var destinationIndex = (ui.item.index()+1);

												// Update elevate position
												if(sourceIndex != destinationIndex){
													var edp = ui.item.attr("id").split('_')[1];
													var dpNo = ui.item.find("#partNo_" + edp).html();

													ElevateServiceJS.updateElevate(keyword,edp,destinationIndex,{
														callback : function(event){
															needRefresh = true;
														},
														preHook: function() { prepareElevateResult(contentHolder); },
														postHook: function() { updateElevateResult(contentHolder, doc, keyword); },
														errorHandler: function(message){ alert(message); }
													});
												}	 
											}
										});
									}
//								});
//							});

							// button display control
							contentHolder.find("a#removeBtn").attr("style", elevated? "display:float" : "display:none"); 
							contentHolder.find("a#cancelBtn").click(function(event){api.hide();}); 

							if (elevated){
								//TODO: should not pass server
								ElevateServiceJS.getElevatedProduct(keyword, doc.EDP,{
									callback : function(item){
										currentExpiryDate = item.formattedExpiryDate;
										currentPosition = item.location;
										contentHolder.find("#aElevatePosition_" + doc.EDP).val(currentPosition);
										contentHolder.find("#aExpiryDate_" + doc.EDP).val(currentExpiryDate);
									},
									errorHandler: handleAddElevateError 
								});
							}

							contentHolder.find("#saveBtn").click(function(){
								var position = parseInt($.trim(contentHolder.find("#aElevatePosition_"+doc.EDP).val()));
								var comment = $.trim(contentHolder.find("#aComment_" + doc.EDP).val());
								var expiryDate = $.trim(contentHolder.find("#aExpiryDate_" + doc.EDP).val());

								if (position>0 && position <= maxPosition){

									if (elevated){
										//TODO: why not one sql call? -> should sp append to existing comment instead of replacing existing comments.
										//TODO: add more restriction
										dwr.engine.beginBatch();
										if (position != currentPosition) ElevateServiceJS.updateElevate(keyword,doc.EDP,position,{callback : function(e){}});
										if (comment.length > 0) ElevateServiceJS.addComment(keyword,doc.EDP,comment,{callback : function(e){}});
										if (expiryDate != currentExpiryDate) ElevateServiceJS.updateExpiryDate(keyword,doc.EDP,expiryDate,{callback : function(e){}});
										dwr.engine.endBatch();
										needRefresh = true;
										prepareElevateResult(contentHolder);
										updateElevateResult(contentHolder, doc, keyword);

									}else{
										//add elevation
										ElevateServiceJS.addElevate(keyword, doc.EDP, position, expiryDate, comment, {
											callback : function(event){
												maxPosition++;
												needRefresh = true;
												elevated = true;
												content.find("a#removeBtn").attr("style","display:float"); 
											},
											preHook: function() { prepareElevateResult(contentHolder); },
											postHook: function() { updateElevateResult(contentHolder, doc, keyword); },
											errorHandler: function(message){ alert(message); }
										});
									}

									contentHolder.find("#aStampExpired_"+doc.EDP).attr("style", expiredDateSelected? "display:float" : "display:none");

								}else{
									alert("Please specify elevate position. Max allowed elevation is " + maxPosition);
									contentHolder.find("#aElevatePosition_" + doc.EDP ).focus();
								}

							});

							contentHolder.find("#removeBtn").click(function(){
								ElevateServiceJS.removeElevate(keyword, doc.EDP,{
									callback : function(event){
										contentHolder.find("a#removeBtn").attr("style","display:none");
										contentHolder.find("#aElevatePosition_"+doc.EDP).val("");
										contentHolder.find("#aExpiryDate_"+doc.EDP).val("");
										needRefresh = true;
										elevated = false;
										maxPosition--;
									},
									preHook: function() { prepareElevateResult(contentHolder); },
									postHook: function() { updateElevateResult(contentHolder, doc, keyword); },
									errorHandler: function(message){ alert(message); }
								});
							});
						},
						hide: function(event, api) {
							$("#aExpiryDate_"+doc.EDP).datepicker('destroy');
							api.destroy();
							if (needRefresh) self.manager.doRequest();
						}
					}
				}).click(function(event) { event.preventDefault(); });	  
			};
		},

		excludeHandler: function (keyword,doc) {

			var self = this;
			var needRefresh = false;
			var idSuffix = "_" + doc.EDP;

			return function () {
				var selector  = "#resultItem_" + doc.EDP + " div#excludeHolder";
				var title = "Exclude Product";
				var content = AjaxSolr.theme('createConfirmDialog', doc, title, "<h2 class='confirmTitle'>Review Exclude Info</h2>"); 

				$(selector).qtip({
					content: {
						text: $('<div/>'),
						title: { text: title, button: true }
					},
					position: {
						my: 'left center',
						at: 'top center'
					},
					style: {
						width: "auto"
					},
					show: {
						modal:  true
					},
					events: {
						render: function(event, api) {
							$('div', api.elements.content).html(content);

							contentHolder = $('div', api.elements.content);

							// Remove other elements
							contentHolder.find("#saveBtn").remove();
							contentHolder.find("a#toggleCurrent").remove();
							contentHolder.find("div#current").remove();
							contentHolder.find("#aElevatePosition" + idSuffix).parent("li").remove();
							contentHolder.find("#aExpiryDate" + idSuffix).parent("li").remove();
							contentHolder.find("#removeBtn > div").html("Exclude");
							contentHolder.find("a#cancelBtn").click(function(event){api.hide();}); 

							contentHolder.find("img#productImage").error(function(){
								$(this).unbind("error").attr("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image.jpg'));
							});

							contentHolder.find("#removeBtn").click(function(){
								ExcludeServiceJS.addExclude(keyword, parseInt(doc.EDP), {
									callback : function(data) {
										needRefresh = true;
										api.hide();
									},
									preHook: function() {},
									postHook: function() {},
									errorHandler: function(message){ alert(message); }
								});						  
							});

						},
						hide: function(event, api) {
							api.destroy();
							if (needRefresh) self.manager.doRequest();
						}
					}
				}).click(function(event) { event.preventDefault(); });	  
			};
		}
	});
})(jQuery);