(function ($) {

	$(document).ready(function() {

		var self = this;

		// Initialize manager
		var Manager = new AjaxSolr.Manager({
			solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/'
		});

		// Install component widgets
		Manager.addWidget(new AjaxSolr.SearchKeywordWidget({
			id: WIDGET_ID_searchKeyword,
			target: WIDGET_TARGET_searchKeyword,
			defaultText: WIDGET_TEXTDEFAULT_searchKeyword,
			minCharRequired: 2
		}));

		Manager.addWidget(new AjaxSolr.CurrentDateWidget({
			id: 'currentDate',
			target: '#currentDate'
		}));

		if(GLOBAL_storeId === "pcmallgov"){
			Manager.addWidget(new AjaxSolr.PCMGSingleSelectorWidget({
				id: "pcmgSelector",
				target: "#pcmgSelector"
			}));
		}

		Manager.addWidget(new AjaxSolr.RedirectUrlWidget({
			id: WIDGET_ID_redirectUrl,
			target: WIDGET_TARGET_redirectUrl
		}));

		Manager.addWidget(new AjaxSolr.RedirectUrlToggleWidget({
			id: WIDGET_ID_redirectUrlToggle,
			target: WIDGET_TARGET_redirectUrlToggle,
			minCharRequired: 2
		}));

		Manager.addWidget(new AjaxSolr[GLOBAL_searchWithinEnabled ? "MultiSearchWithinWidget" : "SearchWithinWidget"]({
			id: WIDGET_ID_searchWithin,
			target: WIDGET_TARGET_searchWithin,
			defaultText: WIDGET_TEXTDEFAULT_searchWithin,
			minCharRequired: 2
		}));

		Manager.addWidget(new AjaxSolr.SearchResultWidget({
			id: WIDGET_ID_searchResult,
			target: WIDGET_TARGET_searchResult
		}));

		Manager.addWidget(new AjaxSolr.CNETFacetWidget({
			id: WIDGET_ID_cnetFacet,
			target: WIDGET_TARGET_cnetFacet
		}));

		Manager.addWidget(new AjaxSolr.CustomPagerWidget({
			id: "customPager",
			style: "style2",
			target: '#top-pager, #bottom-pager',
			renderHeader: function(perPage, offset, total, qTime) {
				var $pagerText = $('<span/>').text('Showing ' + Math.min(total, offset + 1) + '-' + Math.min(total, offset + perPage) + ' of ' + total + " Products");
				$pagerText.append('<span class="fgray"> (' + qTime / 1000 + ' seconds)</span>');
				$('#top-pager-text,#bottom-pager-text').html($pagerText);
			}
		}));

		Manager.addWidget(new AjaxSolr.RuleSelectorWidget({
			id: 'ruleSelector',
			target: '#ruleSelector'
		}));

		Manager.addWidget(new AjaxSolr.SearchResultHeaderWidget({
			id: 'searchResultHeader',
			target: '#searchResultHeader',
			maxRelatedSearch: 3
		}));

		Manager.addWidget(new AjaxSolr.AdRotatorWidget({
			id: 'adRotator',
			target: '#adRotator'
		}));

		Manager.addWidget(new AjaxSolr.DidYouMeanWidget({
			id: '',
			target: '#didYouMean'
		}));

		var sortWidget = new AjaxSolr.SortResultWidget({
			id: 'sortResult',
			target: '#sortResult',
			sortLabel: 'Sort By: ',
			perPageLabel: 'Show: ',
			perPageOptions: 5,
			perPageInterval: 5
		});

		Manager.addWidget(sortWidget);

		Manager.addWidget(new AjaxSolr.DynamicFacetWidget({
			id: 'dynamicFacet',
			target: '#dynamicFacets',
			limit: 5
		}));

		Manager.addWidget(new AjaxSolr.DynamicAttributeWidget({
			id: 'dynamicAttribute',
			target: '#dynamicAttributes',
			limit: 5,
			attribMap: null
		}));
		Manager.addWidget(new AjaxSolr.AvailabilityFacetWidget({
			id: 'availabilityFacets',
			target: '#availabilityFacets',
			limit: 5,
			attribMap: null,
			isDisplayWidget: (GLOBAL_storeId == 'macmall')
		}));        

		Manager.addWidget(new AjaxSolr.CurrentSearchWidget({
			id: 'currentSearch',
			target: '#dynamicSelection'
		}));

		Manager.addWidget(new AjaxSolr.ActiveRuleWidget({
			id: 'activeRule',
			target: '#activeRule'
		}));

		Manager.addWidget(new AjaxSolr.AnimatedTagCloudWidget({
			id: 'animatedTagCloud',
			target: '#tagCloud',
			limit: 25
		}));

		Manager.addWidget(new AjaxSolr.ProductConditionSelectorWidget({
			id: "prodCondSelector",
			target: "#prodCondSelector"
		}));

		Manager.addWidget(new AjaxSolr.ProductAttributeFilterWidget({
			id: 'prodAttribFilter',
			target: '#prodAttribFilter'
		}));

		Manager.init();

		// default IMS
		var facetTemplate = ['Category', 'Manufacturer', 'Platform', GLOBAL_storeFacetTemplateName];

		if (GLOBAL_storeFacetTemplateType === 'IMS') {
			// IMS
			facetTemplate = ['Category', 'Manufacturer', 'Platform', GLOBAL_storeFacetTemplateName];
			if (GLOBAL_storeId == 'macmall'){
				facetTemplate = ['Category', 'Manufacturer', 'Platform','InStock_Memphis','InStock_Chicago_Retail','InStock_Huntington_Beach_Retail','InStock_Santa_Monica_Retail','InStock_Torrance_Retail',GLOBAL_storeFacetTemplateName];
			}
		} else if(GLOBAL_storeFacetTemplateType === 'CNET') {
			// CNET        	
			facetTemplate = ['Manufacturer', 'Platform', GLOBAL_storeFacetTemplateName];
			if (GLOBAL_storeId == 'macmall'){
				facetTemplate = ['Manufacturer', 'Platform','InStock_Memphis','InStock_Chicago_Retail','InStock_Huntington_Beach_Retail','InStock_Santa_Monica_Retail','InStock_Torrance_Retail',GLOBAL_storeFacetTemplateName];
			}
		}

		var params = {
				'facet': true,
				'debugQuery': true,
				'fl': '*,score',
				'facet.field': facetTemplate,
				'rows': sortWidget.perPageInterval,
				'facet.mincount': 1,
				'start': 0,
				'sort': GLOBAL_storeSort,
				'relevancyId': '',
				'spellcheck': true,
				'spellcheck.count': 3,
				'spellcheck.collate': true,
				'gui': GLOBAL_isFromGUI,
				'json.nl': 'map'
		};

		params[GLOBAL_solrSelectorParam] = GLOBAL_storeId;

		for (var name in params) {
			Manager.store.addByValue(name, params[name]);
		}

		if ($("#select-server").is(":visible")) {
			$("#select-server").on({
				change: function(event, data) {
					var reload = false;
					if ($.isNotBlank(data)) {
						reload = data["reload"];
					}
					if ($.isBlank(reload) || reload == true) {
						UtilityServiceJS.getSolrConfig({
							callback: function(data) {
								var config = $.parseJSON(data);
								Manager.setSolrUrl(config.solrUrl + GLOBAL_storeCore + '/');
							},
							postHook: function() {
								Manager.doRequest();
							}
						});
					}
				}
			});
		}


		//Typeahead Managers
		var widgetManager = Manager;
		var typeaheadManager = new AjaxSolr.Manager({
			solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
			store: (new AjaxSolr.ParameterStore())
		});
		typeaheadManager.searchManager = widgetManager;

		typeaheadManager.addWidget(new AjaxSolr.TypeaheadSearchResultWidget({
			id: 'suggestion',
			target: '#suggestionFirst',
			mode: 'simulator',
			searchBox: '#keyword',
		}));

		var typeaheadBrandManager = new AjaxSolr.Manager({
			solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
			store: (new AjaxSolr.ParameterStore())
		});
		typeaheadBrandManager.searchManager = widgetManager;

		typeaheadBrandManager.addWidget(new AjaxSolr.TypeaheadBrandWidget({
			id: 'brand',
			target: '#brandFirst',
			mode: 'simulator',
			searchBox: '#keyword'
		}));

		var typeaheadCategoryManager = new AjaxSolr.Manager({
			solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
			store: (new AjaxSolr.ParameterStore()),
		});
		typeaheadCategoryManager.searchManager = widgetManager;
		typeaheadCategoryManager.sortWidget = sortWidget;

		typeaheadCategoryManager.addWidget(new AjaxSolr.TypeaheadCategoryWidget({
			id: 'category',
			target: '#categoryFirst',
			mode: 'simulator',
			searchBox: '#keyword',
			rows: sortWidget.perPageInterval,
			countId: 'span#count'
		}));

		$('#keyword').autocomplete({
			delay: 300
			,source: function(request, response) {
				TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, $('#keyword').val(), 0, 1, 1, GLOBAL_storeMaxTypeahead, {
					callback:function(data) {
						var list =  data['data'].list;

						if(list.length > 0) {

							var responseArray = new Array();



							for(var i=0; i < list.length; i++) {
								if(i == GLOBAL_storeKeywordMaxCategory)
									continue;

								var object = new Object();
								var ruleName = list[i].ruleName;

								object.value = ruleName;
								object.label = ruleName;
								object.rowClass = i == 0 ? 'categoryFirst' : '';
								object.keyword = i == 0 ? ruleName : '';

								responseArray[responseArray.length] = object;

							}

							var brandRow = new Object();

							brandRow.value = 'Matching Brands';
							brandRow.label = 'Brands';
							brandRow.clickable = false;
							brandRow.keyword =  list[0].ruleName;

							responseArray[responseArray.length] = brandRow;

							for(var i=0; i < 1; i++) {
								if(i == GLOBAL_storeKeywordMaxBrand)
									continue;

								var object = new Object();
								var ruleName = list[i].ruleName;

								object.value = ruleName;
								object.label = ruleName;
								object.rowClass = i == 0 ? 'brandFirst' : '';
								object.keyword = i == 0 ? ruleName : '';

								responseArray[responseArray.length] = object;

							}


							var suggestionRow = new Object();

							suggestionRow.value = 'Suggestions';
							suggestionRow.label = 'Suggestions';
							suggestionRow.rowClass = 'suggestionFirst';
							suggestionRow.keyword = list[0].ruleName;
							suggestionRow,clickable = false;

							responseArray[responseArray.length] = suggestionRow;

							response(responseArray);
						} else {
							$("#keyword").autocomplete("close");
						}

					}
				});
			}
		}).data('autocomplete')._renderItem = function(ul, item) {

			var row = $( "<div></div>" );

			var classString = item.clickable != false ? 'autocompleteLink' : '';

			if(item.rowClass == '') {
				row.data("item.autocomplete", item).append('<strong class="first-lvl-sub fsize13"><a href="javascript:void(0);" class="'+classString+'"><span id="keyword">' + item.value + '</span></a></strong>' + '<div></div>');
			} else {
				if(item.rowClass == 'categoryFirst'){					
					row.data("item.autocomplete", item).append('<div class="topHead">Matching Keywords</div>');					
					row.data("item.autocomplete", item).append('<strong class="first-lvl-sub fsize14"><a href="javascript:void(0);" class="'+classString+'"><span id="keyword">' + item.value +'</span> <span id="count"></span></a></strong>' + '<div style="padding-left:10px" id="'+item.rowClass+'"></div>');										
				}else if(item.rowClass == undefined){
					row.data("item.autocomplete", item).append('<div class="topHead">Matching Brands for '+item.keyword+'</div>');										
				}else if(item.rowClass == 'suggestionFirst'){
					row.data("item.autocomplete", item).append('<div class="topHead">Suggestion for '+item.keyword+'</div>' + '<div style="padding-left:10px" id="'+item.rowClass+'"></div>');															
				}else if(! (item.rowClass == undefined)){
					row.data("item.autocomplete", item).append('<strong class="first-lvl-sub fsize14" style="display:none;"><a href="javascript:void(0);" class="'+classString+'">' + item.value + '</a></strong>' + '<div style="padding-left:10px" id="'+item.rowClass+'"></div>');										
				}
			}

			var result = row.appendTo(ul);

			result.find('a.autocompleteLink').on({
				click: function() {
			
					Manager.store.remove('fq');
					Manager.store.remove('disableElevate');
					Manager.store.remove('disableExclude');
					Manager.store.remove('disableDemote');
					Manager.store.remove('disableFacetSort');
					Manager.store.remove('disableRedirect');
					Manager.store.remove('disableRelevancy');
					Manager.store.remove('disableDidYouMean');
					Manager.store.remove('disableBanner');
					Manager.widgets[WIDGET_ID_searchWithin].clear();
					
					$('#keyword').val($(this).find('span#keyword').text());
					$('#searchKeyword').find('a#searchBtn').click();
					$("#keyword").autocomplete("close");
				}
			});

			if(item.rowClass == 'suggestionFirst') {

				typeaheadManager.store.addByValue('q', $.trim(item.keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
				typeaheadManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
				typeaheadManager.store.addByValue('storeAlias', GLOBAL_storeId);

				for(name in params) {
					typeaheadManager.store.addByValue(name, params[name]);
				}
				typeaheadManager.store.addByValue('fl', 'Name,ImagePath_2,EDP, Manufacturer'); 
				typeaheadManager.doRequest(0);
			} else if(item.rowClass == 'brandFirst') {

				typeaheadBrandManager.store.addByValue('q', $.trim(item.keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
				typeaheadBrandManager.store.addByValue('rows', GLOBAL_storeMaxBrand);
				typeaheadBrandManager.store.addByValue('json.nl', "map");
				typeaheadBrandManager.store.addByValue('group', 'true'); 
				typeaheadBrandManager.store.addByValue('group.field', 'Manufacturer');
				typeaheadBrandManager.store.addByValue('group.limit', 1);
				typeaheadBrandManager.store.addByValue('group.main', 'true');
				typeaheadBrandManager.store.addByValue('fl', 'Manufacturer,Name,ImagePath_2,DPNo');
				typeaheadBrandManager.store.addByValue('facet', 'true');
				typeaheadBrandManager.store.addByValue('facet.field', 'Manufacturer');
				typeaheadBrandManager.store.addByValue('facet.mincount', 1);
				typeaheadBrandManager.store.addByValue('storeAlias', GLOBAL_storeId);

				for(name in params) {
					typeaheadBrandManager.store.addByValue(name, params[name]);
				}

				typeaheadBrandManager.doRequest(0);
			} else if(item.rowClass == 'categoryFirst') {

				typeaheadCategoryManager.store.addByValue('q', $.trim(item.keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
				typeaheadCategoryManager.store.addByValue('rows', 1);
				typeaheadCategoryManager.store.addByValue('json.nl', "map");
				typeaheadCategoryManager.store.addByValue('group', 'true'); 
				typeaheadCategoryManager.store.addByValue('group.field', GLOBAL_storeFacetTemplate);
				typeaheadCategoryManager.store.addByValue('group.limit', 1);
				typeaheadCategoryManager.store.addByValue('group.main', 'true');
				typeaheadCategoryManager.store.addByValue('facet', 'true');
				typeaheadCategoryManager.store.addByValue('facet.field', GLOBAL_storeFacetTemplate); 
				typeaheadCategoryManager.store.addByValue('facet.mincount', 1);
				typeaheadCategoryManager.store.addByValue('storeAlias', GLOBAL_storeId);

				for(name in params) {
					typeaheadCategoryManager.store.addByValue(name, params[name]);
				}

				typeaheadCategoryManager.doRequest(0);
			}

			return result;
		};
	});



})(jQuery);