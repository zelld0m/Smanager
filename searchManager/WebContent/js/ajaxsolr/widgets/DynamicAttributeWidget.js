(function ($) {

	AjaxSolr.DynamicAttributeWidget = AjaxSolr.AbstractFacetWidget.extend({	
		afterRequest: function () {
			var self = this;
			$(self.target).empty();

			if(self.manager.response.response["numFound"] > 0 && $.isNotBlank(self.manager.store.values('q'))){
				$(this.target).empty();
				var output= '';
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
		
		clickHandler: function (field, value, fieldLabel) {
		    var self = this, meth = this.multivalue ? 'add' : 'set';
		    return function () {
		      self.manager.store.removeByValue('fq', new RegExp('^-?' + field + ':')); // Custom
		      if (self[meth].call(self, field, value)) {
		        self.manager.doRequest(0);
		      }
		      return false;
		    };
		},
		
		getSelectedFacetTemplateName: function(fq){
			var facetTemplateName = "";
			for (var i = 0, l = fq.length; i < l; i++) {
				if(fq[i].indexOf(GLOBAL_storeFacetTemplateName) != -1){// Facet Template Name / Or Find By display
					facetTemplateName = fq[i].substring(GLOBAL_storeFacetTemplateName.length+1,fq[i].length);
				}
			}
			
			return facetTemplateName;
		},
		
		displayDynamicAttributes: function (facetFields, list) {
			var self = this;
			
			var getFacetSelected = function() {
				var i = 0;
				var selectedItems = [];

				$('.firerift-style').each(function() {
					if ($(this).hasClass("on")){
						var sel = $.trim($('#' + $(this).attr('rel')).val());
						if ($.isNotBlank(sel)){
							i++;
							selectedItems.push(AjaxSolr.Parameter.escapeValue(sel));
						}
					}
				});

				if (selectedItems.length == 0) {
					return "";
				}
				return "(" + selectedItems.join(" ") + ")";
			};

			var	getFacetParams = function (){
				var paramString = "";
				var keyword = $.trim(self.manager.store.values('q'));
				
				var relId = $("select#relevancy").val()==="keyword_default" ? "": $("select#relevancy").val();
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

				for (var name in params) {
					if ($.isArray(params[name])){
						for (var param in params[name]){
							paramString += "&" + name + "=" + params[name][param];
						}
					}else{
						paramString += "&" + name + "=" + params[name];
					}
				}

				return paramString;
			};

			var handleResponse = function (data){
				var facetFields = data.facet_counts.facet_fields;
				
				//display dynamic attribute values
				for(facetField in facetFields){
					var items = self.asObjectedItems(facetFields, facetField);
					var counter = items[0].count;
					var objectedItems = items[0].objectedItems;
					self.displayFacet(list[facetField].attributeDisplayName, facetField, objectedItems, $.isNotBlank(self.manager.store.values('q')), "|");
				}
			};
				
			$.getJSON(
				self.manager.solrUrl + 'select' + '?' + getFacetParams() + '&wt=json&json.wrf=?', 
				function (json, textStatus) { 
					if (textStatus!=="success"){
						api.destroy();
					}
					
					handleResponse(json); 
				}
			);
		},
		
		populateIMSTemplateAttributes: function(templateName){
			var self =this;
			
			if($.isNotBlank(templateName)){
				CategoryServiceJS.getIMSTemplateAttributes(templateName, {
					callback: function(data){
						if(data){
							self.attribMap = data;
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
							self.displayDynamicAttributes(Object.keys(data), data);
						}
						
					}
				});
			}
		},
		
		moreOptionsHandler: function (facetField, facetValues, facetFieldLabel, delimiter) {
			var self = this;

			return function () {

				getFacetSelected = function() {
					var i = 0;
					var selectedItems = [];

					$('.firerift-style').each(function() {
						if ($(this).hasClass("on")){
							var sel = $.trim($('#' + $(this).attr('rel')).val());
							if ($.isNotBlank(sel)){
								i++;
								selectedItems.push(AjaxSolr.Parameter.escapeValue(sel));
							}
						}
					});

					if (selectedItems.length == 0) {
						return "";
					}
					return "(" + selectedItems.join(" ") + ")";
				};

				getFacetParams = function (){
					var paramString = "";
					var keyword = $.trim(self.manager.store.values('q'));
					
					var relId = $("select#relevancy").val()==="keyword_default" ? "": $("select#relevancy").val();
					var params = {
							'facet': true,
							'q': keyword,
							'facet.field': [facetField],
							'rows': 0,
							'relevancyId': relId,
							'facet.mincount': 1,
							'facet.limit': -1,
							'facet.sort':'HEX',
							'gui': true,
							'json.nl':'map'
					};

					for (var name in params) {
						if ($.isArray(params[name])){
							for (var param in params[name]){
								paramString += "&" + name + "=" + params[name][param];
							}
						}else{
							paramString += "&" + name + "=" + params[name];
						}
					}

					return paramString;
				};

				handleResponse = function (contentHolder, data){

					contentHolder.html(AjaxSolr.theme('displayFacetMoreOptions',facetField,facetFieldLabel,data.facet_counts.facet_fields[facetField], delimiter));

					SearchableList(contentHolder);
					
					contentHolder.find(".iphone-style").on("click", function(e) {
						checkboxID = '#' + $(this).attr('rel');

						if($(checkboxID)[0].checked == false) {
							$(this).animate({backgroundPosition: '0% 100%'});
							$(checkboxID)[0].checked = true;
							$(this).removeClass('off').addClass('on');
						} else {
							$(this).animate({backgroundPosition: '100% 0%'});
							$(checkboxID)[0].checked = false;
							$(this).removeClass('on').addClass('off');
						}
					});

					contentHolder.find(".firerift-style").on("click", function(e) {
						checkboxID = '#' + $(this).attr('rel');

						if($(checkboxID)[0].checked == false) {
							$(checkboxID)[0].checked = true;
							$(this).removeClass('off').addClass('on');
						} else {
							$(checkboxID)[0].checked = false;
							$(this).removeClass('on').addClass('off');
						}
					});

					contentHolder.find('.iphone-style-checkbox, .firerift-style-checkbox').each(function() {

						thisID		= $(this).attr('id');
						thisClass	= $(this).attr('class');

						switch(thisClass) {
						case "iphone-style-checkbox":
							setClass = "iphone-style";
							break;
						case "firerift-style-checkbox":
							setClass = "firerift-style";
							break;
						}

						$(this).addClass('hidden');

						if($(this)[0].checked == true)
							$(this).after('<div class="'+ setClass +' on" rel="'+ thisID +'">&nbsp;</div>');
						else
							$(this).after('<div class="'+ setClass +' off" rel="'+ thisID +'">&nbsp;</div>');
					});

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
					events: {
						render: function(event, api) {
							contentHolder = $('div', api.elements.content);
							
							contentHolder.html('<div id="preloader" class="txtAC"><img src="../images/ajax-loader-rect.gif"></div>');
							
							$.getJSON(
									self.manager.solrUrl + 'select' + '?' + getFacetParams() + '&wt=json&json.wrf=?', 
									function (json, textStatus) { 
										if (textStatus!=="success"){
											api.destroy();
										}
										
										handleResponse(contentHolder, json); 
										// If there is existing filter to this facet field
										var indices = self.manager.store.find('fq', new RegExp('^-?' + facetField + ':'));

										if (indices && AjaxSolr.isArray(indices) && indices.length===1) {

											var currFacet = self.manager.store.values('fq')[indices[0]];

											if ($.startsWith(currFacet,"(") && $.endsWith(currFacet,")")){
												currFacet = currFacet.substr(facetField.length+1,currFacet.length-(facetField.length+1));
											}

											var splitArray = currFacet.match(/\w+|"[^".*"]+"/g);

											for(var par in splitArray){
												var cBoxId = $('input[value="'+ splitArray[par].replace(/\"/g,'') +'"]').attr("id");
												$('div[rel="'+ cBoxId +'"]').removeClass("off");
												$('div[rel="'+ cBoxId +'"]').addClass("on");
											}

										}

										contentHolder.find('#continueBtn').click(function(e){
											self.manager.store.removeByValue('fq', new RegExp('^-?' + facetField + ':'));
											if ($.isNotBlank(getFacetSelected())) {
												self.manager.store.addByValue('fq', self.fq(facetField, getFacetSelected()));												
											}
											self.manager.store.addByValue('relevancyId', $("select#relevancy").val());
											self.manager.doRequest(0);
										});

										contentHolder.find('#cancelBtn').click(function(e){
											api.destroy();
										});
									}
							);

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
					AjaxSolr.theme('createFacetLink', $.formatAsId(facetField) + i, facetField, delimiter ? facet.split(delimiter)[1] : facet, count, this.clickHandler(facetField, facet));
					if (i == l-1 && isKeywordIncluded){
						AjaxSolr.theme('createFacetMoreOptionsLink', $.formatAsId(facetField), facetValues, '[+] More Options', this.moreOptionsHandler(facetField, facetValues, facetFieldLabel, delimiter));
					}
				}
			}
		}
	});

})(jQuery);
