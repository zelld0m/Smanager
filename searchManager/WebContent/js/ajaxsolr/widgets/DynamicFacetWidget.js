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

					if ( facetFields === undefined) {
						$(this.target).html(AjaxSolr.theme('no_items_found'));
						return;
					}

					$(this.target).empty();

					var output= '';

					for (var facetField in facetFields) {
						$(this.target).append(AjaxSolr.theme('createFacetHolder',facetField, facetField));

						var maxCount = 0;
						var objectedItems = [];
						var limit = this.limit;
						var counter = 0;
						facetValues = facetFields[facetField];

						for (var facetValue in facetValues) {
							if (counter == limit) break;

							var count = parseInt(facetValues[facetValue]);
							if (count > maxCount) {
								maxCount = count;
							}
							objectedItems.push({ facet: facetValue, count: count });
							counter++;
						}

						objectedItems.sort(function (a, b) {
							return a.facet < b.facet ? -1 : 1;
						});

						for (var i = 0, l = objectedItems.length; i < l; i++) {
							var facet = objectedItems[i].facet;
							var count = objectedItems[i].count;
							AjaxSolr.theme('createFacetLink', facetField + i,facetField, facet, count, this.clickHandler(facetField, facet));
							if (i == l-1 && $.isNotBlank(self.manager.store.values('q')))
								AjaxSolr.theme('createFacetMoreOptionsLink', facetField, facetValues, '[+] More Options', this.moreOptionsHandler(facetField, facetValues));
						}
					}	
				}
			}
		},

		moreOptionsHandler: function (facetField,facetValues) {
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

					return "(" + selectedItems.join(" ") + ")";
				};

				getFacetParams = function (){
					var paramString = "";
					var keyword = $.trim(self.manager.store.values('q'));
					
					var relId = $("select#relevancy").val();
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

					contentHolder.html(AjaxSolr.theme('displayFacetMoreOptions',facetField,facetField,data.facet_counts.facet_fields[facetField]));

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

				$('#more' + facetField + " .lnk").qtip({
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
											self.manager.store.addByValue('fq', self.fq(facetField, getFacetSelected()));
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
		}

	});

})(jQuery);
