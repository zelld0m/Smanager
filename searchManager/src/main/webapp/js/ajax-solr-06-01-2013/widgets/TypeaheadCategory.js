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

	AjaxSolr.TypeaheadCategoryWidget = AjaxSolr.AbstractWidget.extend({

		expDateMinDate: 0,
		expDateMaxDate: "+1Y",
		roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
		brandCountMap: null,
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

			brandCountMap = new Object();

			var categories = self.manager.response.FacetTemplate.Level1;

			if(self.countAttributes(categories) > 1) {
				var counter = 0;
				for(obj in categories) {
					if(counter == GLOBAL_storeMaxCategory)
						break;
					$(self.target).append(self.getContent(obj, categories[obj]));
					counter ++;
				}
			}

			if(self.mode == 'simulator')
				self.addListener('.keywordListener');		
		},
		getContent: function(category, count, keyword) {
			var self = this;
			var html = '';

			html += '<div class="'+(self.mode == 'simulator' ? 'itemNameCat' : 'itemNamePreviewCat')+'">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener">';
			html += '		<span id="category">'+category+'</span> ('+count+')';
			if(self.mode == 'simulator')
				html += '</a>';
			html += '</div>';

			return html;
		},
		addListener: function(selector) {
			var self = this;
			$(self.target).find(selector).on({
				click: function() {
					var keyword = $(this).closest('#categoryFirst').prev().find('a.autocompleteLink').text();
					$(self.searchBox).val(keyword);

					var searchManager = self.manager.searchManager;

					self.clearFilters(searchManager);

					searchManager.store.addByValue("fq", GLOBAL_storeFacetTemplate + ':' + self.escapeValue($(this).find('span#category').text())+'*');
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
		},
		countAttributes : function(object) {
			var counter = 0;

			for(obj in object) {
				counter++;
			}

			return counter;
		},
		escapeValue: function(text){
			if($.isNotBlank(text)) // Dependency: CurrentSearchWidget.js - display text
				return ("" + text).replace(/\s/g,"?").
						replace(/\(/g,"\\(").
						replace(/\)/g,"\\)");
			
			return text;
		}
	});

})(jQuery);
