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
	
	AjaxSolr.TypeaheadSearchResultWidget = AjaxSolr.AbstractWidget.extend({

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
			$(this.brandTarget).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
			$(this.categoryTarget).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
		},

		errorRequest: function () {
			//$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
		},

		afterRequest: function () {
			var self = this;
			self.categoryAfterRequest();
			self.brandAfterRequest();
			self.suggestionAfterRequest();

		},
		suggestionAfterRequest: function() {
			var self = this;
			
			$(self.target).empty(); 

			for (var i = 0, l = self.manager.response.response.docs.length; i < l && i < GLOBAL_storeMaxSuggestion; i++) {
				var doc = self.manager.response.response.docs[i];
//				var debug = self.manager.response.debug.explain[doc.EDP]; 

				$(self.target).append(self.getContent(doc));

				if (doc.Expired != undefined)
					$(this.target).find("li#resultItem_" + doc.EDP + " div#expiredHolder").attr("style","display:float");

				$(self.target).find("img.itemImg, img.normalImg").on({
					error:function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); 
					}
				});						

			}

			if(self.mode == 'simulator')
				self.addListener('.keywordListener');
		},
		categoryAfterRequest: function() {
			var self = this;

			var totalResults = self.manager.response.response.numFound;
			var divProperty = self.manager.response.responseHeader.params.divCount;
			if(self.countId){
				$(self.countId).html('&nbsp;('+totalResults+')');
			} else if(divProperty) {
				self.manager[divProperty].html('&nbsp;('+totalResults+')');
			}

			$(self.categoryTarget).empty(); 

			brandCountMap = new Object();

			var categories = (self.manager.response.FacetTemplate) ? self.manager.response.FacetTemplate.Level1 : self.manager.response.facet_counts.facet_fields.Category;

			if(Object.keys(categories).length > 0) {
				var i=0;
				for(name in categories) {
					if(i == GLOBAL_storeMaxCategory)
						break;
					$(self.categoryTarget).append(self.getCategoryContent(name, categories[name]));
					i++;
				}
			}

			if(self.mode == 'simulator')
				self.addCategoryListener('.categoryKeywordListener');	
		},
		brandAfterRequest: function() {
			var self = this;
			$(self.brandTarget).empty(); 
			
			var manufacturers = self.manager.response.facet_counts.facet_fields.Manufacturer;

			var counter = 0;

			for(name in manufacturers) {
				if(counter == GLOBAL_storeMaxBrand)
					break;
				$(self.brandTarget).append(self.getBrandContent(name, manufacturers[name]));
				counter++;
			}

			if(self.mode == 'simulator')
				self.addBrandListener('.brandKeywordListener');
		},
		getContent: function(doc) {
			var self = this;

			var html = '';

			html += '<div class="itemImgWp floatL">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener">';
			html += '<span id="dpno" style="display:none">'+doc.EDP+'</span>';
			html += '		<img class="'+(self.mode == 'simulator' ? 'itemImg':'normalImg')+' floatL" width="60" src="'+doc.ImagePath_2+'"/>&nbsp;';
			html += '		<div class="'+(self.mode == 'simulator'? 'itemNameSuggest':'itemNameSuggestPreview')+'"><strong>'+doc.Manufacturer+'</strong></div>';
			html += '		<div class="'+(self.mode == 'simulator'? 'itemNameSuggest':'itemNameSuggestPreview')+'">'+doc.Name+'</div>';
			if(self.mode == 'simulator')
				html += '</a>';
			html += '</div>';
			html += '<div class="clearB"></div>';
			html += '<div class="sep"></div>';

			return html;
		},
		getCategoryContent: function(category, count, keyword) {
			var self = this;
			var html = '';

			html += '<div class="'+(self.mode == 'simulator' ? 'itemNameCat' : 'itemNamePreviewCat')+'">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="categoryKeywordListener">';
			html += '		<span id="category">'+category+'</span>';
			if(self.mode == 'simulator')
				html += '</a>';
			html += '</div>';

			return html;
		},
		getBrandContent: function(name, count) {
			var self = this;
			var html = '';

			html += '<div class="'+(self.mode == 'simulator' ? 'itemNameBrand' : 'itemNamePreviewBrand')+'">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="brandKeywordListener">';
			html += '		<span id="brand">'+name+'</span>';
			if(self.mode == 'simulator')
				html += '</a">';
			html += '</div>';
			html += '<div class="clearB"></div>';

			return html;
		},
		addListener: function(selector) {
			var self = this;
			$(self.target).find(selector).each(function(){
				$(this).off().on({
					click: function() {
						$(self.searchBox).val($(this).find('span#dpno').text());
						var searchManager = self.manager.searchManager;
						
						self.clearFilters(searchManager);
						
						searchManager.store.addByValue('q', $(this).find('span#dpno').text());
						searchManager.doRequest(0);
						$(self.searchBox).autocomplete("close");
					}
				});
			});

		},
		addCategoryListener: function(selector) {
			var self = this;
			$(self.categoryTarget).find(selector).on({
				click: function() {
					var keyword = $(this).closest('#categoryFirst').prev().find('a.autocompleteLink').find('span#keyword').text();
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
		addBrandListener: function(selector) {
			var self = this;
			$(self.brandTarget).find(selector).on({
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
			manager.store.remove('fq');
			manager.store.remove('disableElevate');
			manager.store.remove('disableExclude');
			manager.store.remove('disableDemote');
			manager.store.remove('disableFacetSort');
			manager.store.remove('disableRedirect');
			manager.store.remove('disableRelevancy');
			manager.store.remove('disableDidYouMean');
			manager.store.remove('disableBanner');
			manager.widgets[WIDGET_ID_searchWithin].clear();
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
