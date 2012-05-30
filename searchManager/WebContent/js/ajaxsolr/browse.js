(function ($) {

	$.cookie('searchWithin', '', {expires: 1});

	var Manager;
	var solrurl;
	var isFmGui;

	UtilityServiceJS.getSolrConfig({
		callback:function(data){	
			var config = $.parseJSON(data);
			solrurl = config.solrUrl;
			isFmGui = config.isFmGui;	
		},
		postHook: function(){
			UtilityServiceJS.getStoreName({
				callback:function(storeName){

					Manager = new AjaxSolr.Manager({
						solrUrl: solrurl + storeName + '/'
					});

					Manager.addWidget(new AjaxSolr.ResultWidget({
						id: 'result',
						target: '#docs'
					}));

					Manager.addWidget(new AjaxSolr.PagerWidget({
						id: 'pager',
						target: '#pager',
						innerWindow: 1,
						renderHeader: function (perPage, offset, total, qTime) {
							$('#pager-header').html($('<span/>').text('Displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of ' + total));
							$('#pager-header').append('<span class="fgray"> (' + qTime/1000 + ' seconds)</span>');
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

					Manager.addWidget(new AjaxSolr.FilterResultByTypeWidget({
						id: 'filterByType',
						target: '#filterByType',
						headerText: 'Type'
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
							'gui': isFmGui,
							'json.nl': 'map'
					};

					for (var name in params) {
						Manager.store.addByValue(name, params[name]);
					}

					Manager.store.addByValue("store", storeName);

					Manager.doRequest();

					if ($("#select-server").is(":visible")){
						$("#select-server").on({
							change: function(event, data){
								var reload;
								if (data != undefined) {
									reload = data["reload"];
								}
								if (reload == undefined || reload == true) {
									UtilityServiceJS.getSolrConfig({
										callback:function(data){	
											var config = $.parseJSON(data);
											Manager.setSolrUrl(config.solrUrl + storeName + '/');
										},
										postHook:function() {
											Manager.doRequest();						
										}
									});
								}
							}
						});
					}
				}
			});
		}
	});

	$.fn.showIf = function (condition) {
		if (condition) {
			return this.show();
		}
		else {
			return this.hide();
		}
	};

})(jQuery);