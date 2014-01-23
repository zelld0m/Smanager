(function ($) {

	AjaxSolr.AvailabilityFacetWidget = AjaxSolr.AbstractFacetWidget.extend({	
moreOptionContainer: null,
		afterRequest: function () {

			var self = this;

			$(this.target).empty();

			if($.isNotBlank(self.manager.store.values('q'))){
				if (self.manager.store.values('fq').length > 0) $('input#keepRefinement').prop("checked", true);
				if (self.manager.response.response.docs.length > 0 && this.isDisplayWidget){

					var facetFields = this.manager.response.facet_counts.facet_fields;

					if ($.isBlank(facetFields)) {
						$(this.target).html(AjaxSolr.theme('no_items_found'));
						return;
					}

					$(this.target).empty();
					$(this.target).append(AjaxSolr.theme('createFacetHolder',"Availability", "Availability"));
					var hasSelectedFilter = (self.manager.store.values('fq') + " ").indexOf('InStock') >= 0;							
					var location = "";
					var counter = 0;
					for (var facetField in facetFields) {
						var itemCount=0;
						if((!hasSelectedFilter && facetField.indexOf('InStock') != -1 ) || 
							(hasSelectedFilter && (self.manager.store.values('fq') + " ").indexOf(facetField) != -1 && facetField.indexOf('InStock') != -1)){
							facetValues = facetFields[facetField];
							location = facetField.replace("InStock_","").replace("_Retail","").replace("_"," ");
							for (var facetValue in facetValues) {							
								if (facetValue){
									itemCount = facetValues[facetValue];
								}						
							}						
							if (itemCount>1){
								AjaxSolr.theme('createFacetLink', $.formatAsId("Availability") + counter,"Availability", location, itemCount, this.clickHandler(facetField, "true"));
								counter++;							
							}
						}
					}
					AjaxSolr.theme('createFacetMoreOptionsLink',  $.formatAsId("Availability"), facetFields, '[+] More Options', this.moreOptionsHandler("Availability", facetFields));
				}
			}
		},

		moreOptionsHandler: function (facetField) {
			var self = this;

			return function () {

				getFacetSelected = function() {
					var i = 0;
					var selectedItems = [];

					self.moreOptionContainer.find('.firerift-style').each(function() {
						if ($(this).hasClass("on")){
							var sel = $.trim($('#' + $(this).attr('rel')).val());
							if ($.isNotBlank(sel)){
								i++;								
								selectedItems.push(AjaxSolr.Parameter.escapeValue(sel));
							}
						}
					});
					return selectedItems;
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
							'facet.field': ['InStock_Memphis','InStock_Chicago_Retail','InStock_Huntington_Beach_Retail','InStock_Santa_Monica_Retail','InStock_Torrance_Retail'],
							'rows': 0,
							'relevancyId': relId,
							'facet.mincount': 1,
							'facet.limit': -1,
							'facet.sort':'HEX',
							'gui': true,
							'json.nl':'map'
					};
					params[GLOBAL_solrSelectorParam] = GLOBAL_storeId;
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
				};

				handleResponse = function (contentHolder, data){
					contentHolder.html(AjaxSolr.theme('displayFacetMoreOptionsAvailability',facetField,facetField,data.facet_counts.facet_fields,self.manager.store.values('fq') + " "));
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
							self.moreOptionContainer = contentHolder;
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
											var selectedFilters = getFacetSelected();
											var filters = self.manager.store.values('fq');
											for (var i = 0;i < filters.length;i++){
												if (filters[i].indexOf("InStock")!=-1){
													self.manager.store.removeByValue('fq',filters[i]);	
												}
												
											}
											for (var i = 0; i < selectedFilters.length; i++) {																							
												if ($.isNotBlank(selectedFilters[i])) {
													self.manager.store.addByValue('fq', self.fq(selectedFilters[i], "true"));												
												}												
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
