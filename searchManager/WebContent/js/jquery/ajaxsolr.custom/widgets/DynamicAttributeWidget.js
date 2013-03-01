(function ($) {

	AjaxSolr.DynamicAttributeWidget = AjaxSolr.AbstractFacetWidget.extend({	
		moreOptionContainer: null,
		afterRequest: function () {
			var self = this;
			$(self.target).empty();

			if(self.manager.response.response["numFound"] > 0 && $.isNotBlank(self.manager.store.values('q'))){
				$(this.target).empty();
				var facetFields = this.manager.response.facet_counts.facet_fields;
				var selectedFacetTemplateName = "";

				//Display Or Find By
				if(GLOBAL_storeFacetTemplateName !== ''){
					var items = self.asObjectedItems(facetFields, GLOBAL_storeFacetTemplateName);
					var counter = items[0].count;
					var objectedItems = items[0].objectedItems;

					selectedFacetTemplateName = self.getSelectedFacetTemplateName(this.manager.response.responseHeader.params.fq);

					if(counter == 1 && (selectedFacetTemplateName === "" || selectedFacetTemplateName.indexOf(objectedItems[0].facet) != -1)){
						selectedFacetTemplateName = objectedItems[0].facet;
					}
					else if(counter > 0){
						self.displayFacet('Or Find By', GLOBAL_storeFacetTemplateName, objectedItems, $.isNotBlank(self.manager.store.values('q')));
					}
				}

				/* If facet template name is selected, display dynamic attributes */
				if($.isNotBlank(selectedFacetTemplateName)){
					switch(GLOBAL_storeFacetTemplateName){
					case "TemplateName":
						self.populateIMSTemplateAttributes(selectedFacetTemplateName);
						break;
					case GLOBAL_storeFacetTemplate+"Name": //e.g. "PCMall_FacetTemplateName"
						self.populateCNETTemplateAttributes(selectedFacetTemplateName);
						break;
					}
				}
			}
		},

		getSelectedFacetTemplateName: function(fq){
			var facetTemplateName = "";

			if ($.isNotBlank(fq)){
				for (var i = 0, l = fq.length; i < l; i++) {
					if(fq[i].indexOf(GLOBAL_storeFacetTemplateName) != -1){// Facet Template Name / Or Find By display
						facetTemplateName = fq[i].substring(GLOBAL_storeFacetTemplateName.length+1,fq[i].length);
					}
				}
			}

			return facetTemplateName;
		},

		escapeValue: function (value) {
			return '"' + value + '"';
		},

		displayDynamicAttributes: function (facetFields, list) {
			var self = this;

			var handleResponse = function (data){
				var facetFieldsRes = data.facet_counts.facet_fields;
				
				//display dynamic attribute values
				for(facetField in facetFieldsRes){
					var items = self.asObjectedItems(facetFieldsRes, facetField);
					var counter = items[0].count;
					var objectedItems = items[0].objectedItems;

					if(counter && list[facetField]){
						self.displayFacet(list[facetField].attributeDisplayName, facetField, objectedItems, $.isNotBlank(self.manager.store.values('q')), "|");
					}
				}
			};

			$.getJSON(
					self.manager.solrUrl + 'select' + '?' + self.getFacetParams(facetFields) + '&wt=json&json.wrf=?', 
					function (json, textStatus) { 
						if (textStatus!=="success"){
							api.destroy();
						}

						handleResponse(json); 
					}
			);
		},
		
		getFacetSelected : function() {
			var self = this;
			var i = 0;
			var selectedItems = [];

			self.moreOptionContainer.find('.firerift-style').each(function() {
				if ($(this).hasClass("on")){
					var sel = $.trim($('#' + $(this).attr('rel')).val());
					if ($.isNotBlank(sel)){
						i++;
						selectedItems.push(self.escapeValue(sel));
					}
				}
			});

			if (selectedItems.length == 0) {
				return "";
			}
			return "(" + selectedItems.join(" ") + ")";
		},
		
		getFacetParams: function (facetFields){
			var self = this;
			var paramString = "";
			var keyword = $.trim(self.manager.store.values('q'));

			var relId = $("select#relevancy").val();
			if (relId == undefined || selectedRelevancy === "keyword_default") {
				relId = "";
			}
			var params = {
					'facet': true,
					'q': keyword,
					'facet.field': facetFields,
					'rows': 0,
					'relevancyId': relId,
					'facet.mincount': 1,
					'facet.limit': -1,
					'facet.sort':'HEX',
					'gui': true,
					'json.nl':'map'
			};

			var storeparams = self.manager.store.params;
			//merge params
			for(var name in storeparams){
				if(!params[name]){
					if ($.isArray(storeparams[name])){
						if(!$.isEmptyObject(storeparams[name])){
							params[name] = storeparams[name];
						}
					}
					else{
						if(storeparams[name]){
							params[name] = storeparams[name].value;
						}
					}
				}
			}

			for (var name in params) {
				if ($.isArray(params[name])){
					for (var param in params[name]){
						var paramVal = "";
						
						if(params[name][param].value){ //if Object
							paramVal = params[name][param].value;
						}
						else if(params[name][param]){
							paramVal = params[name][param];
						}
						else{
							continue;
						}
						paramString += "&" + name + "=" + (name.toLowerCase()==='q' || name.toLowerCase()==='fq' ? encodeURIComponent(paramVal):paramVal);
					}
				}else{
					if(name.toLowerCase() !== "sort".toLowerCase())
						paramString += "&" + name + "=" + (name.toLowerCase()==='q' || name.toLowerCase()==='fq' ? encodeURIComponent(params[name]): params[name]);
				}
			}

			return paramString;
		},

		populateIMSTemplateAttributes: function(templateName){
			var self =this;

			if($.isNotBlank(templateName)){
				CategoryServiceJS.getIMSTemplateAttributes(templateName, {
					callback: function(data){
						if(data){
							self.attribMap = data;
							if(!$.isEmptyObject(Object.keys(data)))
								self.displayDynamicAttributes(Object.keys(data), data);
						}
					}
				});
			}
		},

		populateCNETTemplateAttributes: function(templateName){
			var self =this;

			if($.isNotBlank(templateName)){
				CategoryServiceJS.getCNETTemplateAttributes(templateName, {
					callback: function(data){
						if(data){
							self.attribMap = data;
							if(!$.isEmptyObject(Object.keys(data)))
								self.displayDynamicAttributes(Object.keys(data), data);
						}

					}
				});
			}
		},

		moreOptionsHandler: function (facetField, facetValues, facetFieldLabel, delimiter) {
			var self = this;

			return function () {
				handleResponse = function (contentHolder, data){
					contentHolder.html(AjaxSolr.theme('displayFacetMoreOptions',facetField,facetFieldLabel,data.facet_counts.facet_fields[facetField], delimiter));
					SearchableList(contentHolder);
					contentHolder.find('.firerift-style-checkbox').slidecheckbox();
				};

				$('#more' + $.formatAsId(facetField) + " .lnk").qtip({
					content: {
						text: $('<div/>'),
						title: {
							text: 'Select ' + facetFieldLabel ,
							button: true
						}
					},
					position: {
						my: 'left top',
						at: 'right top'
					},
					style:{
						width: 'auto'
					},
					show:{
						ready: true,
						modal: true
					},
					events: {
						show: function(event, api) {
							contentHolder = $('div', api.elements.content);
self.moreOptionContainer = contentHolder;
							contentHolder.html('<div id="preloader" class="txtAC"><img src="../images/ajax-loader-rect.gif"></div>');

							$.getJSON(
									self.manager.solrUrl + 'select' + '?' + self.getFacetParams([facetField]) + '&wt=json&json.wrf=?', 
									function (json, textStatus) { 
										if (textStatus!=="success"){
											api.destroy();
										}

										handleResponse(contentHolder, json); 
										// If there is existing filter to this facet field
										var indices = self.manager.store.find('fq', new RegExp('^-?' + facetField + ':'));

										if (indices && AjaxSolr.isArray(indices) && indices.length===1) {

											var currFacet = self.manager.store.values('fq')[indices[0]];
											currFacet = currFacet.substr(facetField.length+1,currFacet.length-(facetField.length+1));

											if ($.startsWith(currFacet,"(") && $.endsWith(currFacet,")")){
												currFacet = currFacet.substr(1,currFacet.length-2);
											}

											var splitArray = currFacet.match(/\w+|"[^"*"]+"/g);  

											for(var par in splitArray){
												var cBoxId = $('input[value="'+ splitArray[par].replace(/\"/g,'') +'"]').attr("id");
												$('div[rel="'+ cBoxId +'"]').removeClass("off")
												.addClass("on")
												.css("background-position", "0% 100%");
											}

										}

										contentHolder.find('#continueBtn').click(function(e){
											self.manager.store.removeByValue('fq', new RegExp('^-?' + facetField + ':'));
											if ($.isNotBlank(self.getFacetSelected())) {
												self.manager.store.addByValue('fq', self.fq(facetField, self.getFacetSelected()));												
											}
											self.manager.store.addByValue('relevancyId', $("select#relevancy").val());
											self.manager.doRequest(0);
										});

										contentHolder.find('#cancelBtn').click(function(e){
											api.destroy();
										});
									}
							);
						},
						hide: function(event, api){
							api.destroy();
						}
					}
				}).click(function(event) { event.preventDefault(); });	  
			};		
		},

		asObjectedItems: function(facetFields, facetField){
			var items = [];
			var maxCount = 0;
			var objectedItems = [];
			var limit = this.limit;
			var counter = 0;
			var facetValues = facetFields[facetField];

			for (var facetValue in facetValues) {
				if (counter == limit) break;

				var count = parseInt(facetValues[facetValue]);
				if (count > maxCount) {
					maxCount = count;
				}
				objectedItems.push({ facet: facetValue, count: count });
				counter++;
			}

			items.push({ count: counter, objectedItems: objectedItems});

			return items;
		},

		displayFacet: function (facetFieldLabel, facetField, objectedItems, isKeywordIncluded, delimiter){
			$(this.target).append(AjaxSolr.theme('createFacetHolder', facetFieldLabel, facetField));

			objectedItems.sort(function (a, b) {
				return a.facet < b.facet ? -1 : 1;
			});

			for (var i = 0, l = objectedItems.length; i < l; i++) {
				var facet = objectedItems[i].facet;

				var count = objectedItems[i].count;

				if ($.isNotBlank(facet)){
					AjaxSolr.theme('createFacetLink', $.formatAsId(facetField) + i, facetField, delimiter ? facet.split(delimiter)[1] : facet, count, this.clickHandler(facetField, this.escapeValue(facet)));
					if (i == l-1 && isKeywordIncluded){
						AjaxSolr.theme('createFacetMoreOptionsLink', $.formatAsId(facetField), facetValues, '[+] More Options', this.moreOptionsHandler(facetField, facetValues, facetFieldLabel, delimiter));
					}
				}
			}
		}
	});

})(jQuery);
