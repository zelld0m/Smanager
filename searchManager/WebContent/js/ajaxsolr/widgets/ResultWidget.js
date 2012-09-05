(function ($) {

	AjaxSolr.ResultWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest: function () {
			$(this.target).html(AjaxSolr.theme('showAjaxLoader',"Please wait..."));
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty(); 

			var keyword = $.trim(self.manager.store.values('q'));
			var hasKeyword = $.isNotBlank(keyword);

			if (hasKeyword){
				if (this.manager.response.response.docs.length == 0){
					$(this.target).append(AjaxSolr.theme('noSearchResult', keyword));
				}else{

					for (var i = 0, l = self.manager.response.response.docs.length; i < l; i++) {
						var doc = self.manager.response.response.docs[i];
						var debug = self.manager.response.debug.explain[doc.EDP]; 
						$(self.target).append(AjaxSolr.theme('result', i, hasKeyword,doc, AjaxSolr.theme('snippet', doc), self.auditHandler(doc), self.docHandler(doc), self.debugHandler(doc), self.featureHandler(keyword,doc), self.elevateHandler(keyword,doc), self.excludeHandler(keyword,doc)));

						if (doc.Expired != undefined)
							$(this.target).find("li#resultItem_" + doc.EDP + " div#expiredHolder").attr("style","display:float");

						$(self.target).find("li#resultItem_" + doc.EDP + " div.itemImg img").on({
							error:function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); 
							}
						});						

						if (i+1 == l){
							$(self.target).wrapInner("<ul class='searchList'>");
						}
					}

					if ($.isNotBlank(self.manager.response.responseHeader["Redirect"])){
						$(this.target).find("div.ruleOptionHolder").hide();
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

			return function () {
				var selector  = "#resultItem_" + doc.EDP + " div#auditHolder";

				$(selector).off().on({
					click: function(e){
						$(e.currentTarget).viewaudit({
							itemDataCallback: function(base, page){
								AuditServiceJS.getItemTrail(e.data.doc["EDP"], base.options.page, base.options.pageSize, {
									callback: function(data){
										var total = data.totalSize;
										base.populateList(data);
										base.addPaging(base.options.page, total);
									},
									preHook: function(){
										base.prepareList();
									},
									postHook:function(){
										base.api.reposition();
									}
								});
							}
						});
					}
				},{doc: doc});
			}
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
			if (!allowModify) return;
			var self = this;
			var selector  = "#resultItem_" + doc.EDP + " div#elevateHolder";
			var title = "Elevate Product";
			var content = AjaxSolr.theme('createConfirmDialog', doc, title, "<h2 class='confirmTitle'>Review Elevate Info</h2>"); 
			var needRefresh = false;
			var elevated = doc["ElevateType"] === "PART_NUMBER" || doc["Expired"] != undefined;
			var maxPosition = 0;
			var currentExpiryDate = "";
			var currentPosition = 0;
			var expiredDateSelected = false;
			var idSuffix = "_" + doc.EDP;
			var noExpiryDateText = "Indefinite";
			var expDateMinDate = 0;
			var expDateMaxDate = "+1Y";

			return function () {

				setProductImage = function(contentHolder, item){
					setTimeout(function(){		
						// Product is no longer visible in the setting
						var id = "_" + item["memberId"];

						if ($.isBlank(item["dpNo"])){
							contentHolder.find("#listItemsPattern" + id + " > div > img#productImage" + id).prop("src", AjaxSolr.theme('getAbsoluteLoc', 'images/padlock_img60x60.jpg'));
							var $selector = contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo");
							$selector.find("li#partNo" + id + ", li#mfrNo" + id + ", li#expiryDate" + id).html("Unavailable");
						}
						else{
							contentHolder.find("#listItemsPattern" + id + " > div > img#productImage" + id).prop("src", item["imagePath"]).off().on({
								error:function(){ 
									$(this).unbind("error").prop("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image60x60.jpg')); 
								}
							});
						}
					},10);
				};

				populateSelectedProduct = function(contentHolder){
					ElevateServiceJS.getProductByEdp(keyword, doc["EDP"], {
						callback : function(item){
							if(item!=null){
								doc["ElevateId"] = item["memberId"];
								setTimeout(function(){	
									contentHolder.find("input#aExpiryDate_" + doc["EDP"]).val(item["formattedExpiryDate"]);
									contentHolder.find("input#aElevatePosition_" + doc["EDP"]).val(item["location"]);
								},1);
							}else{
								elevated = false;
							}
						},
						errorHandler: handleAddElevateError 
					});
				};
				
				prepareElevateResult = function (contentHolder){
					contentHolder.find("#toggleItems > ul.listItems > :not(#listItemsPattern)").remove();
					contentHolder.find("#toggleItems > ul.listItems").append(AjaxSolr.theme('showAjaxLoader',"Please wait..."));
				};

				updateElevateResult = function(contentHolder, doc, keyword){
					ElevateServiceJS.getAllElevatedProductsIgnoreKeyword(keyword, 0, 0,{
						callback: function(data){
							var list = data.list;

							var setImage = function(contentHolder, id, imagePath){
								setTimeout(function(){	
									contentHolder.find("#listItemsPattern" + id + " > div > img#productImage" + id).prop("src", imagePath).off().on({
										error:function(){ 
											$(this).unbind("error").prop("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image60x60.jpg')); 
										}
									});
								},10);
							};

							var getFacetItemType = function(item){
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
							};

							contentHolder.find("#toggleItems > ul#listItems_" + doc.EDP + " > :not(#listItemsPattern)").remove();
							
							for (var i=0; i<data.totalSize; i++){
								var PART_NUMBER = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "PART_NUMBER";
								var FACET = $.isNotBlank(list[i]["memberTypeEntity"]) && list[i]["memberTypeEntity"] === "FACET";

								var id = "_" + list[i]["memberId"];

								dwr.util.cloneNode("listItemsPattern", {idSuffix: id});
								contentHolder.find("#listItemsPattern" + id).attr("style", "display:block");

								if (i%2==0) contentHolder.find("#listItemsPattern" + id).addClass("alt");
								if (list[i].dpNo == contentHolder.find("#aPartNo_" + doc.EDP).html()) contentHolder.find("#listItemsPattern" + id).addClass("selected");

								contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#elevatePosition" + id).html(list[i].location);

								var expiryDate = list[i].formattedExpiryDate;
								if (list[i].isExpired){
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#validityText" + id).html('<img id="stampExpired' + id + '" src="../images/expired_stamp50x16.png">');
								}else{
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#validityText" + id).html("Validity:");
								}
								contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#expiryDate" + id).html($.isBlank(expiryDate)? noExpiryDateText : expiryDate);


								if(FACET){
									var readableStr = list[i].condition['readableString'];

									if(readableStr.length > 100){
										readableStr = readableStr.substring(0,100);
										readableStr += "...";
									}

									contentHolder.find("#listItemsPattern" + id + " > div > div#readableStr" + id).html(readableStr);
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#partNo" + id).remove();
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li.partNoLabel").remove();
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#mfrNo" + id).remove();
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li.mfrNoLabel").remove();

									var imagePath = list[i]["imagePath"];

									if($.isBlank(imagePath)){
										imagePath = GLOBAL_contextPath + '/images/';
										switch(getFacetItemType(list[i])){
										case "ims" : imagePath += "ims_img.jpg"; break;
										case "cnet" : imagePath += "cnet_img.jpg"; break;
										case "facet" : imagePath += "facet_img.jpg"; break;
										}
									}

									setImage(contentHolder, id, imagePath);
								}
								else if(PART_NUMBER){
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#partNo" + id).html(list[i].dpNo);
									contentHolder.find("#listItemsPattern" + id + " > div > div > ul.listItemInfo > li#mfrNo" + id).html(list[i].mfrPN);
									contentHolder.find("#listItemsPattern" + id + " > div > div#readableStr" + id).remove();

									setProductImage(contentHolder, list[i]);
								}

								// delete icon is clicked
								contentHolder.find("#listItemsPattern" + id + " > div > div > a.deleteIcon").click({item: list[i]},function(e){
									if (confirm("Continue?")){
										ElevateServiceJS.deleteItemInRule(keyword, e.data.item["memberId"], {
											callback : function(data){
												if (e.data.item["memberTypeEntity"]==="PART_NUMBER"){
													contentHolder.find("a#removeBtn").attr("style","display:none");
													contentHolder.find("#aElevatePosition_" + e.data.item["EDP"]).val("");
													contentHolder.find("#aExpiryDate_" + e.data.item["EDP"]).val("");
												}

												maxPosition--;
												needRefresh = true;
											},
											preHook: function() { prepareElevateResult(contentHolder); },
											postHook: function() { 
												updateElevateResult(contentHolder, doc, keyword);
												populateSelectedProduct(contentHolder);
											}
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

							if(updatedExpiryDate !== "Indefinite")
								contentHolder.find("#aExpiryDate_" + doc.EDP).val($.isDate(updatedExpiryDate) ? updatedExpiryDate : "");

							if (stampVisible){
								contentHolder.find("#aStampExpired_" + doc.EDP).show();
							}else{
								contentHolder.find("#aStampExpired_" + doc.EDP).hide();
							}

							//Disable
							DeploymentServiceJS.getRuleStatus("Elevate", keyword, {
								callback:function(ruleStatus){
									if(ruleStatus!=null && $.inArray(ruleStatus["approvalStatus"],["PENDING","APPROVED"])>=0){
										contentHolder.find("#removeBtn,#saveBtn, .deleteIcon").hide();
										contentHolder.find("#listItems_" + doc.EDP).sortable("option", "disabled", true);
										contentHolder.find("#aExpiryDate_"+ doc.EDP).datepicker("option", "disabled", true);
									}
								}
							});

						}
					});
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
						show: function(event, api) {
							var contentHolder = $('div', api.elements.content);

							contentHolder.html(content);

							// Set maximum elevate position
							ElevateServiceJS.getTotalProductInRule(keyword, {
								callback: function(count){
									switch(count){
									case 0:
									case 1:
									default:
										maxPosition = count > 0 ? (count+1): 1;	
									}	
								}
							});
							
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
											var memberId = ui.item.attr("id").split('_')[1];

											ElevateServiceJS.updateElevate(keyword,memberId,destinationIndex,{
												callback : function(event){
													needRefresh = true;
													populateSelectedProduct(contentHolder);
												},
												preHook: function() { prepareElevateResult(contentHolder); },
												postHook: function() { updateElevateResult(contentHolder, doc, keyword); }
											});
										}	 
									}
								});
							}

							// button display control
							contentHolder.find("a#removeBtn").attr("style", elevated? "display:float" : "display:none"); 
							contentHolder.find("a#cancelBtn").click(function(event){api.hide();}); 

							contentHolder.find("#aExpiryDate_" + doc["EDP"]).datepicker({
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

							if (elevated){
								populateSelectedProduct(contentHolder);
							}

							contentHolder.find("#saveBtn").click(function(){
								var position = parseInt($.trim(contentHolder.find("#aElevatePosition_"+doc.EDP).val()));
								var comment = $.trim(contentHolder.find("#aComment_" + doc.EDP).val());
								var expiryDate = $.trim(contentHolder.find("#aExpiryDate_" + doc.EDP).val());
								var today = new Date();
								//ignore time of current date 
								today.setHours(0,0,0,0);
								if (position>0 && position <= maxPosition){

									if(!isXSSSafe(comment)){
										alert("Invalid comment. HTML/XSS is not allowed.");
									}
									else if(today.getTime() > new Date(expiryDate).getTime()){
										alert("Expiry date cannot be earlier than today");
									}else if (elevated){
										//TODO: why not one sql call? -> should sp append to existing comment instead of replacing existing comments.
										//TODO: add more restriction
										if (position != currentPosition || comment.length > 0 || expiryDate !== currentExpiryDate) 
											ElevateServiceJS.updateElevateItem(keyword, doc["ElevateId"], position, comment, expiryDate, {
												callback : function(data){
													if(data>0){
														needRefresh = true;
													}
												},
												preHook: function() { prepareElevateResult(contentHolder); },
												postHook: function() { updateElevateResult(contentHolder, doc, keyword); }
											});
									}else{
										//add elevation
										ElevateServiceJS.addElevate(keyword, 'PART_NUMBER', doc.EDP, position, expiryDate, comment, {
											callback : function(event){
												maxPosition++;
												needRefresh = true;
												elevated = true;
												content.find("a#removeBtn").attr("style","display:float"); 
											},
											preHook: function() { prepareElevateResult(contentHolder); },
											postHook: function() {
												updateElevateResult(contentHolder, doc, keyword); 
												populateSelectedProduct(contentHolder);
											},
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
								ElevateServiceJS.deleteItemInRule(keyword, doc["ElevateId"],{
									callback : function(event){
										contentHolder.find("a#removeBtn").attr("style","display:none");
										contentHolder.find("#aElevatePosition_"+doc["EDP"]).val("");
										contentHolder.find("#aExpiryDate_"+doc["EDP"]).val("");
										needRefresh = true;
										elevated = false;
										maxPosition--;
									},
									preHook: function() { prepareElevateResult(contentHolder); },
									postHook: function() { 
										updateElevateResult(contentHolder, doc, keyword); 
										populateSelectedProduct(contentHolder);
										}
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
			if (!allowModify) return;
			var self = this;
			var needRefresh = false;
			var idSuffix = "_" + doc.EDP;
			var expDateMinDate = 0;
			var expDateMaxDate = "+1Y";
			var excluded = false;

			return function () {
				var selector  = "#resultItem_" + doc.EDP + " div#excludeHolder";
				var title = "Exclude Product";
				var content = AjaxSolr.theme('createConfirmDialog', doc, title, "<h2 class='confirmTitle'>Review Exclude Info</h2>"); 

				populateSelectedProduct = function(contentHolder, api){
					ExcludeServiceJS.getProductByEdp(keyword, doc["EDP"], {
						callback : function(item){
							if(item!=null){
								excluded = true;
								
								setTimeout(function(){	
									contentHolder.find("input#aExpiryDate_" + doc["EDP"]).val(item["formattedExpiryDate"]);
								},1);
							}else{
								excluded = false;
							}
							
							contentHolder.find("#removeBtn").click(function(){
								var expiryDate = $.trim(contentHolder.find("#aExpiryDate_" + doc["EDP"]).val());
								var comment = $.trim(contentHolder.find("#aComment_" + doc["EDP"]).val());

								var today = new Date();
								//ignore time of current date 
								today.setHours(0,0,0,0);

								if(!isXSSSafe(comment)){
									alert("Invalid comment. HTML/XSS is not allowed.");
								}
								else if(today.getTime() > new Date(expiryDate).getTime()){
									alert("Expiry date cannot be earlier than today");
								}
								else if(excluded){
									ExcludeServiceJS.updateExcludeFacet(keyword, item["memberId"], comment, expiryDate, item["condition"], {
										callback : function(data) {
											needRefresh = true;
											api.hide();
										},
										preHook: function() {},
										postHook: function() {}
									});
								}
								else{
									ExcludeServiceJS.addExclude(keyword, 'PART_NUMBER', parseInt(doc["EDP"]), expiryDate, comment, {
										callback : function(data) {
											needRefresh = true;
											api.hide();
										},
										preHook: function() {},
										postHook: function() {}
									});	
								}
							});
						}/*,
						errorHandler: handleAddElevateError */
					});
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
							$('div', api.elements.content).html(content);

							contentHolder = $('div', api.elements.content);

							// Remove other elements
							contentHolder.find("#saveBtn").remove();
							contentHolder.find("a#toggleCurrent").remove();
							contentHolder.find("div#current").remove();
							contentHolder.find("#aElevatePosition" + idSuffix).parent("li").remove();
							//contentHolder.find("#aExpiryDate" + idSuffix).parent("li").remove();
							contentHolder.find("#removeBtn > div").html("Exclude");
							contentHolder.find("a#cancelBtn").click(function(event){api.hide();}); 

							contentHolder.find("img#productImage").error(function(){
								$(this).unbind("error").attr("src", AjaxSolr.theme('getAbsoluteLoc', '../images/no-image.jpg'));
							});

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

							populateSelectedProduct(contentHolder, api);
														
							//Disable
							DeploymentServiceJS.getRuleStatus("Exclude", keyword, {
								callback:function(ruleStatus){
									if(ruleStatus!=null && $.inArray(ruleStatus["approvalStatus"],["PENDING","APPROVED"])>=0){
										contentHolder.find("#removeBtn,#saveBtn").hide();
										contentHolder.find("#aExpiryDate_"+doc.EDP).datepicker("option", "disabled", true);
									}
								}
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
