(function ($) {

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

	AjaxSolr.TypeaheadBrandWidget = AjaxSolr.AbstractWidget.extend({

		expDateMinDate: 0,
		expDateMaxDate: "+1Y",
		roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
		defaultParams: {
			'facet': true,
			'debugQuery': true,
			'fl': '*,score',
			'facet.field': facetTemplate,
			'rows': this.rows,
			'facet.mincount': 1,
			'start': 0,
			'sort': GLOBAL_storeSort,
			'relevancyId': '',
			'spellcheck': true,
			'spellcheck.count': 3,
			'spellcheck.collate': true,
			'gui': GLOBAL_isFromGUI,
			'json.nl': 'map'
		},
		beforeRequest: function () {
			$(this.target).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
		},

		errorRequest: function () {
			//$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty(); 
			
			var manufacturers = self.manager.response.facet_counts.facet_fields.Manufacturer;

			var counter = 0;

			for(name in manufacturers) {
				if(counter == GLOBAL_storeMaxBrand)
					break;
				$(self.target).append(self.getContent(name, manufacturers[name]));
				counter++;
			}

			if(self.mode == 'simulator')
				self.addListener('.keywordListener');
		},
		getContent: function(name, count) {
			var self = this;
			var html = '';

			html += '<div class="'+(self.mode == 'simulator' ? 'itemNameBrand' : 'itemNamePreviewBrand')+'">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener">';
			html += '		<span id="brand">'+name+'</span> ('+count+')';
			if(self.mode == 'simulator')
				html += '</a">';
			html += '</div>';
			html += '<div class="clearB"></div>';

			return html;
		},
		addListener: function(selector) {
			var self = this;
			$(self.target).find(selector).on({
				click: function() {
					var keyword = $(this).closest('#brandFirst').prev().find('a.autocompleteLink').text();
					$(self.searchBox).val(keyword);

					var searchManager = self.manager.searchManager;

					self.clearFilters(searchManager);

					searchManager.store.addByValue("fq", 'Manufacturer:"'+$(this).find('span').text()+'"');
					searchManager.store.addByValue("q", keyword);
					for(obj in self.defaultParams) {
						searchManager.store.addByValue(obj, self.defaultParams[obj]);
					}
					searchManager.doRequest(0);

					$(self.searchBox).autocomplete("close");
				}
			});
		},
		clearFilters : function(manager) {			
			var indices = manager.store.find("fq", new RegExp('^' + GLOBAL_storeFacetTemplate + ':'));

			if ($.isNotBlank(indices) && indices.length > 0){
				for(var i=0; i< indices.length; i++)
					manager.store.remove("fq", indices[i]);
			}

			indices = manager.store.find("fq", new RegExp('^Manufacturer:'));

			if ($.isNotBlank(indices) && indices.length > 0){
				for(var i=0; i< indices.length; i++)
					manager.store.remove("fq", indices[i]);
			}
		}
	});

})(jQuery);
