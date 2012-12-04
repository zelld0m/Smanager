(function ($) {

	AjaxSolr.DynamicFacetWidget = AjaxSolr.AbstractFacetWidget.extend({
		afterRequest: function () {

			var self = this;

			$(this.target).empty();

			if($.isNotBlank(self.manager.store.values('q'))){
				$('#refinementHolder').attr("style", self.manager.store.values('fq').length > 0 ? "display:float" : "display:none");
				if (self.manager.store.values('fq').length > 0) $('input#keepRefinement').prop("checked", true);
				
				if (self.manager.response.response.docs.length > 0){

					var facetFields = this.manager.response.facet_counts.facet_fields;

					if ($.isBlank(facetFields)) {
						$(this.target).html(AjaxSolr.theme('no_items_found'));
						return;
					}

					$(this.target).empty();
				
					for (var facetField in facetFields) {
						var maxCount = 0;
						var objectedItems = [];
						var limit = this.limit;
						var counter = 0;
						facetValues = facetFields[facetField];
						
						if($.isEmptyObject(facetValues) || facetField === GLOBAL_storeFacetTemplateName)
							continue;
							
						$(this.target).append(AjaxSolr.theme('createFacetHolder',facetField, facetField));

						for (var facetValue in facetValues) {
							if (counter == limit) break;

							var count = parseInt(facetValues[facetValue]);
							if (count > maxCount) {
								maxCount = count;
							}
							objectedItems.push({ facet: facetValue, count: count });
							counter++;
						}

						for (var i = 0, l = objectedItems.length; i < l; i++) {
							var facet = objectedItems[i].facet;
							var count = objectedItems[i].count;
							
							if ($.isNotBlank(facet)){
								facetValue = (facet.match(/^["\(].*["\)]$/)) ? facet :  '"' + facet + '"';
								AjaxSolr.theme('createFacetLink', $.formatAsId(facetField) + i,facetField, facet, count, this.clickHandler(facetField, facetValue));
								if (i == l-1 && $.isNotBlank(self.manager.store.values('q'))){
									AjaxSolr.theme('createFacetMoreOptionsLink', $.formatAsId(facetField), facetValues, '[+] More Options', this.moreOptionsHandler(facetField, facetValues));
								}
							}
						
						}
					}	
				}
			}
		},

		moreOptionsHandler: function (facetField) {
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
								sel = (sel.match(/^["\(].*["\)]$/)) ? sel :  '"' + sel + '"';
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
					var relId = $("select#relevancy").val();
					if (relId == undefined || selectedRelevancy === "keyword_default") {
						relId = "";
					}
					var params = {
							'facet': true,
							'q': keyword,
							'facet.field': ['Category','Manufacturer','Platform'],
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
								params[name] = storeparams[name];
							}
							else{
								params[name] = storeparams[name].value;
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
								paramString += "&" + name + "=" + paramVal;
							}
						}else{
							if(name.toLowerCase() !== "sort".toLowerCase())
								paramString += "&" + name + "=" + params[name];
						}
					}

					return paramString;
				};

				handleResponse = function (contentHolder, data){
					contentHolder.html(AjaxSolr.theme('displayFacetMoreOptions',facetField,facetField,data.facet_counts.facet_fields[facetField]));
					SearchableList(contentHolder);
					contentHolder.find('.firerift-style-checkbox').slidecheckbox();
				};

				$('#more' + $.formatAsId(facetField) + " .lnk").qtip({
					content: {
						text: $('<div/>'),
						title: {
							text: 'Select ' + facetField ,
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
											currFacet = currFacet.substr(facetField.length+1,currFacet.length-(facetField.length+1));
											
											if ($.startsWith(currFacet,"(") && $.endsWith(currFacet,")")){
												currFacet = currFacet.substr(1,currFacet.length-2);
											}

											var splitArray = currFacet.match(/\w+|"[^".*"]+"/g);

											for(var par in splitArray){
												var cBoxId = $('input[value="'+ splitArray[par].replace(/\"/g,'') +'"]').attr({checked:true}).attr("id");
												$('div[rel="'+ cBoxId +'"]').removeClass("off")
												                            .addClass("on")
												                            .css("background-position", "0% 100%");
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

						},
						hide: function(event, api){
							api.destroy();
						}
					}
				}).click(function(event) { event.preventDefault(); });	  
			};		
		}

	});

})(jQuery);
