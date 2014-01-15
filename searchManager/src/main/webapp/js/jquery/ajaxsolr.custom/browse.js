(function ($) {

	$(document).ready(function() {
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
        
        Manager.addWidget(new AjaxSolr.SearchRuleModeWidget({
        	id: WIDGET_ID_searchRuleMode,
        	target: WIDGET_TARGET_searchRuleMode
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
        } else if(GLOBAL_storeFacetTemplateType === 'CNET') {
        	// CNET
            facetTemplate = ['Manufacturer', 'Platform', GLOBAL_storeFacetTemplateName];
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
    });
})(jQuery);
