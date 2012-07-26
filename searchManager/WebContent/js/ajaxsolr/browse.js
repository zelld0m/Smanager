(function ($) {

	$.cookie('searchWithin', '', {expires: 1});

	var Manager;
	var isFmGui;

	UtilityServiceJS.getSolrConfig({
		callback:function(data){	
			
			console.log(GLOBAL_isFromGUI);
		
			Manager = new AjaxSolr.Manager({
				solrUrl: GLOBAL_solrUrl + GLOBAL_store + '/'
			});

			Manager.addWidget(new AjaxSolr.ResultWidget({
				id: 'result',
				target: '#docs'
			}));
			
			Manager.addWidget(new AjaxSolr.CNETFacetWidget({
				id: 'cnet',
				target: '#cnetFacets'
			}));

			Manager.addWidget(new AjaxSolr.PagerWidget({
				id: 'pager',
				target: '#top-pager,#bottom-pager',
				innerWindow: 1,
				renderHeader: function (perPage, offset, total, qTime) {
					var $pagerText = $('<span/>').text('Showing ' + Math.min(total, offset + 1) + '-' + Math.min(total, offset + perPage) + ' of ' + total + " Products");
					$pagerText.append('<span class="fgray"> (' + qTime/1000 + ' seconds)</span>');
					$('#top-pager-text,#bottom-pager-text').html($pagerText);
				}
			}));

			Manager.addWidget(new AjaxSolr.SearchWithinWidget({
				id: 'subsearch',
				defaultText: 'Enter keyword',
				target: '#searchWithin',
				searchWithinInput: '#searchWithinInput'
			}));

			Manager.addWidget(new AjaxSolr.CurrentSearchWidget({
				id: 'currentSearch',
				target: '#dynamicSelection'
			}));

			Manager.addWidget(new AjaxSolr.RuleSelectorWidget({
				id: 'ruleSelector',
				target: '#ruleSelector'
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

			Manager.addWidget(new AjaxSolr.TextWidget({
				id: 'text',
				defaultText: 'Enter keyword',
				target: '#search'
			}));

			Manager.addWidget(new AjaxSolr.DynamicFacetWidget({
				id: 'dynamicFacet',
				target: '#dynamicFacets',
				limit: 5
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

			Manager.addWidget(new AjaxSolr.ProductAttributeFilterWidget({
				id: 'prodAttribFilter',
				target: '#prodAttribFilter'
			}));

			Manager.init();

			var params = {
					'facet': true,
					'debugQuery': true,
					'fl': '*,score',
					'facet.field': ['Category', 'Manufacturer', 'Platform'],
					'rows': sortWidget.perPageInterval,
					'facet.mincount': 1,
					'sort':'CatCodeOrder asc, score desc, Popularity desc',
					'relevancyId': '',
					'spellcheck': true,
					'spellcheck.count': 3,
					'spellcheck.collate': true,
					'gui': GLOBAL_isFromGUI,
					'json.nl': 'map'
			};

			for (var name in params) {
				Manager.store.addByValue(name, params[name]);
			}

			Manager.store.addByValue("store", GLOBAL_store);

			if ($("#select-server").is(":visible")){
				$("#select-server").on({
					change: function(event, data){
						var reload;
						if (data != undefined) {
							reload = data["reload"];
						}
						if (reload == undefined || reload == true) {
							
									Manager.setSolrUrl(GLOBAL_solrUrl + GLOBAL_store + '/');
								
									Manager.doRequest();						
								
						}
					}
				});
			}
		}
	});
		
})(jQuery);