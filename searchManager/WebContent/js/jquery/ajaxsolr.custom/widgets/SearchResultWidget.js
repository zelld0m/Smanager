(function ($) {

	AjaxSolr.SearchResultWidget = AjaxSolr.AbstractWidget.extend({
		expDateMinDate: 0,
		expDateMaxDate: "+1Y",

		beforeRequest: function () {
			$(this.target).html(AjaxSolr.theme('showAjaxLoader',"Please wait..."));
		},

		errorRequest: function () {
			$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
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
						$(self.target).append(AjaxSolr.theme('result', i, hasKeyword,doc, AjaxSolr.theme('snippet', doc), self.auditHandler(doc), self.docHandler(doc), self.debugHandler(doc), self.featureHandler(keyword,doc), self.elevateHandler(keyword,doc), self.excludeHandler(keyword,doc), self.demoteHandler(keyword,doc),self.forceAddHandler(doc)));

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

//			return function () {
//				var selector  = "#resultItem_" + doc.EDP + " div#auditHolder";
//
//				$(selector).off().on({
//					click: function(e){
//						$(e.currentTarget).viewaudit({
//							itemDataCallback: function(base, page){
//								AuditServiceJS.getItemTrail(e.data.doc["EDP"], base.options.page, base.options.pageSize, {
//									callback: function(data){
//										var total = data.totalSize;
//										base.populateList(data);
//										base.addPaging(base.options.page, total);
//									},
//									preHook: function(){
//										base.prepareList();
//									},
//									postHook:function(){
//										base.api.reposition();
//									}
//								});
//							}
//						});
//					}
//				},{doc: doc});
//			}
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

		forceAddHandler: function (doc) {
			var self = this;
			var selector  = "#resultItem_" + doc["EDP"] + " div#forceAddHolder";
			var title = "Force Add SKU#: " + doc["DPNo"];

			return function () {
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
							var content = $('div', api.elements.content);
							content.html(AjaxSolr.theme('productForceAdd'));

							content.find('#sku').text(doc["DPNo"]);

							content.find('#validityDate').prop({readonly:true}).datepicker({
								showOn: "both",
								minDate: self.expDateMinDate,
								maxDate: self.expDateMaxDate,
								changeMonth: true,
						        changeYear: true,
								buttonText: "Expiration Date",
								buttonImage: "../images/icon_calendar.png",
								buttonImageOnly: true
							});

							content.find('#addBtn').off().on({
								click: function(e){
									var currKeyword = $.trim(self.manager.store.values('q'));
									var keyword = $.trim(e.data.content.find('#keyword').val());
									var validityDate = $.trim(e.data.content.find('#validityDate').val());
									var comment = $.defaultIfBlank($.trim(e.data.content.find('#comment').val()),"");

									if(!validateGeneric("Keyword", keyword, 2)){
										return
									}

									if (e.data.doc["ElevateType"] === "PART_NUMBER" && keyword.toLowerCase() === currKeyword.toLowerCase()){
										jAlert("SKU# " + e.data.doc["DPNo"] + " is already elevated at position " + e.data.doc["Elevate"], "Search Simulator");
										return
									}
									
									if(!validateComment("Comment", comment, 1, 300)){
										//error alert in function validateComment
										return
									}

									comment = comment.replace(/\n\r?/g, '<br/>');
									
									ElevateServiceJS.addProductItemForceAdd(keyword, e.data.doc["EDP"], 1, validityDate, comment, {
										callback:function(data){
											showActionResponse(data, "force add", "SKU#: " + e.data.doc["DPNo"] + " in " + keyword);
										},
										postHook: function(){
											self.manager.doRequest();
										}
									});
								}
							},{content: content, doc: doc});

							content.find('#cancelBtn').off().on({
								click: function(e){
									api.destroy();
								}
							});
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
								if(!$.startsWith(value,'"') && !$.endsWith(value,'"')){
									value = '"' + value + '"'; 
								}
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

		elevateHandler: function (keyword, doc) {
			var self = this;
			var selector  = "#resultItem_EDP div#elevateHolder".replace("EDP", doc["EDP"]);

			return function(){
				$(selector).ruleitem({
					doc: doc,
					moduleName: "Elevate",
					locked: !allowModify,
					keyword: keyword,
					enableSortable: true,
					enableForceAddStatus: true,
					memberPositionTag: "Elevate",
					memberExpiredTag: "ElevateExpired",
					itemForceAddStatusCallback: function(base, memberIds){
						ElevateServiceJS.isRequireForceAdd(keyword, memberIds, {
							callback:function(data){
								base.updateForceAddStatus(data);
							},
							preHook: function(){
								base.prepareForceAddStatus();
							}
						});
					},

					itemUpdateForceAddStatusCallback: function(base, memberId, status){
						ElevateServiceJS.updateElevateForceAdd(keyword, memberId, status, {
							callback:function(data){
								if (data>0) base.hasChanges++;
								base.getList(); 
							},
							preHook:function(){
								base.prepareList(); 
							}
						});
					},

					itemDataCallback: function(base){
						ElevateServiceJS.getAllElevatedProductsIgnoreKeyword(keyword, 0, 0,{
							callback: function(data){
								base.populateList(data);
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemAddItemCallback:function(base, productId, position, validityDate, comment){
						ElevateServiceJS.addProductItem(keyword, productId, position, validityDate, comment, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemUpdateItemCallback:function(base, memberId, position, validityDate, comment){
						ElevateServiceJS.updateElevateItem(keyword, memberId, position, comment, validityDate, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemDeleteItemCallback:function(base, memberId){
						ElevateServiceJS.deleteItemInRule(keyword, memberId, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemMoveItemPositionCallback: function(base, memberId, destinationIndex){
						ElevateServiceJS.updateElevate(keyword, memberId, destinationIndex,{
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},
					
					afterClose: function(){
						self.manager.doRequest();
					}
				});
			};
		},

		excludeHandler: function (keyword,doc) {
			var self = this;
			var selector  = "#resultItem_EDP div#excludeHolder".replace("EDP", doc["EDP"]);

			return function(){
				$(selector).ruleitem({
					doc: doc,
					moduleName: "Exclude",
					locked: !allowModify,
					keyword: keyword,
					enableSortable: true,
					enableForceAddStatus: true,
					promptPosition: false,
					itemDataCallback: function(base){
						ExcludeServiceJS.getAllExcludedProductsIgnoreKeyword(keyword, 0, 0,{
							callback: function(data){
								base.populateList(data);
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemAddItemCallback:function(base, productId, position, validityDate, comment){
						ExcludeServiceJS.addExclude(keyword, 'PART_NUMBER', productId, validityDate, comment, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemUpdateItemCallback:function(base, memberId, position, validityDate, comment){
						//TODO: optimize and update exclude method name
						ExcludeServiceJS.updateExpiryDate(keyword, memberId, validityDate, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList();
								if ($.isNotBlank(comment) && validateComment("Comment", comment, 1, 300)){
									ExcludeServiceJS.addRuleComment(keyword, memberId, comment, {
										callback : function(data){
											if (data>0) base.hasChanges++;
										}
									});
								}
							}
						});
						
						DemoteServiceJS.updateItem(keyword, memberId, position, comment, validityDate, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
						
					},

					itemDeleteItemCallback:function(base, memberId){
						ExcludeServiceJS.deleteItemInRule(keyword, memberId, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					afterClose: function(){
						self.manager.doRequest();
					}
				});
			};
		},

		demoteHandler: function (keyword,doc) {
			var self = this;
			var selector  = "#resultItem_EDP div#demoteHolder".replace("EDP", doc["EDP"]);

			return function(){
				$(selector).ruleitem({
					doc: doc,
					moduleName: "Demote",
					locked: !allowModify,
					keyword: keyword,
					enableSortable: true,
					memberPositionTag: "Demote",
					memberExpiredTag: "DemoteExpired",
					
					itemDataCallback: function(base){
						DemoteServiceJS.getAllProductsIgnoreKeyword(keyword, 0, 0,{
							callback: function(data){
								base.populateList(data);
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemAddItemCallback:function(base, productId, position, validityDate, comment){
						DemoteServiceJS.add(keyword, 'PART_NUMBER', productId, position, validityDate, comment, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemUpdateItemCallback:function(base, memberId, position, validityDate, comment){
						DemoteServiceJS.updateItem(keyword, memberId, position, comment, validityDate, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemDeleteItemCallback:function(base, memberId){
						DemoteServiceJS.deleteItemInRule(keyword, memberId, {
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},

					itemMoveItemPositionCallback: function(base, memberId, destinationIndex){
						DemoteServiceJS.update(keyword, memberId, destinationIndex,{
							callback : function(data){
								if (data>0) base.hasChanges++;
								base.getList();
							},
							preHook: function() { 
								base.prepareList(); 
							}
						});
					},
					
					afterClose: function(){
						self.manager.doRequest();
					}
				});
			};
		}

	});
})(jQuery);
