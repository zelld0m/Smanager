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
			var self = this;
			if(self.mode != 'simulator') {
				$(this.target).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
				$(this.brandTarget).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
				$(this.categoryTarget).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
			}
		},

		errorRequest: function () {
			//$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
		},

		afterRequest: function () {
			var self = this;
			self.categoryAfterRequest();
			self.brandAfterRequest();
			self.suggestionAfterRequest();
			
			self.manager.elevatedBrandList = undefined;
			self.manager.elevatedCategoryList = undefined;
			
			if(self.manager.postHook) {
				self.manager.postHook();
			}

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

			var total = 0;
			
			
			var elevatedCategoryList = self.manager.elevatedCategoryList != undefined ? self.manager.elevatedCategoryList : [];
			
			for(var i=0; i < elevatedCategoryList.length; i++) {
				var elevatedCategory = elevatedCategoryList[i];
				
				if(elevatedCategory == undefined || elevatedCategory == null || categories[elevatedCategory] == undefined) {
					
					continue;
				}
				
				if(total == GLOBAL_storeMaxCategory && self.mode != 'edit') {
					break;
				}
				
				$(self.categoryTarget).append(self.getCategoryContent(elevatedCategoryList, elevatedCategory, categories[elevatedCategory], true));
				total ++;
			}
			
			
			if(Object.keys(categories).length > 0) {
				for(name in categories) {
					if(total == GLOBAL_storeMaxCategory && self.mode != 'edit') {
						break;
					}
					$(self.categoryTarget).append(self.getCategoryContent(elevatedCategoryList, name, categories[name], false));
					total++;
				}
			}
			console.log($(self.categoryTarget).html());
			if(self.mode == 'simulator')
				self.addCategoryListener('.categoryKeywordListener');	
			
			self.manager.elevatedCategoryList = undefined;
		},
		brandAfterRequest: function() {
			var self = this;
			$(self.brandTarget).empty(); 
			
			var manufacturers = self.manager.response.facet_counts.facet_fields.Manufacturer;

			var total = 0;
			
			var elevatedBrandList = self.manager.elevatedBrandList != undefined ? self.manager.elevatedBrandList : [];

			for(var i=0; i < elevatedBrandList.length; i++) {
				var elevatedBrand = elevatedBrandList[i];
				
				if(elevatedBrand == undefined || elevatedBrand == null || manufacturers[elevatedBrand] == undefined) {
					
					continue;
				}
				
				if(total == GLOBAL_storeMaxBrand && self.mode != 'edit') {
					break;
				}
				
				$(self.brandTarget).append(self.getBrandContent(elevatedBrandList, elevatedBrand, manufacturers[elevatedBrand], true));
				total ++;
			}
			
			for(name in manufacturers) {
				if(total == GLOBAL_storeMaxBrand && self.mode != 'edit') {
					break;
				}
				
				$(self.brandTarget).append(self.getBrandContent(elevatedBrandList, name, manufacturers[name], false));
				total++;
			}

			if(self.mode == 'simulator')
				self.addBrandListener('.brandKeywordListener');
			
			self.manager.elevatedBrandList = undefined;
		},
		getContent: function(doc) {
			var self = this;

			var html = '';
			var tag = (self.mode == 'simulator') ? 'li' : 'div';
			
			
			html += '<'+tag+' class="itemImgWp floatL">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener suggest">';
			html += '<span id="dpno" style="display:none">'+doc.EDP+'</span>';
			
			html += '		<img class="'+(self.mode == 'simulator' ? 'itemImg':'normalImg')+' floatL" width="60" src="'+doc.ImagePath_2+'"/>&nbsp;';
			html += '		<span class="'+(self.mode == 'simulator'? 'itemNameSuggest offer':'itemNameSuggestPreview')+'">'+doc.Manufacturer+'</span>';
			html += '		<span class="'+(self.mode == 'simulator'? 'itemNameSuggest prod-title':'itemNameSuggestPreview')+'">'+doc.Name+'</span>';
			if(self.mode == 'simulator') {
				html += '</a>';
				html += '<div class="clearB padB10"></div>';
				html += '<div class="clearB padB10"></div>';
			}
			html += '</'+tag+'>';
			
			if(self.mode != 'simulator') {
				html += '<div class="clearB"></div>';
				html += '<div class="sep"></div>';
			}

			return html;
		},
		getCategoryContent: function(elevatedCategoryList, category, count, isElevated) {
			var self = this;
			var html = '';
			
			var tag = (self.mode == 'simulator') ? 'li' : 'div';
			
			var visibilityStyle = (isElevated == false && $.inArray(category, elevatedCategoryList) > -1) ? 'style="display:none;"' : '';
			
			html += '<'+tag+' class="'+(self.mode == 'simulator' ? '' : 'itemNamePreviewCat')+' '+(isElevated == true ? 'elevatedCategory' : '')+'" '+visibilityStyle+'>';
			if(self.mode == 'simulator') {
				html += '<a href="javascript:void(0);" class="categoryKeywordListener">';
			}
			html += '		<span id="category">'+category+'</span>';
			if(self.mode == 'simulator') {
				html += '</a>';
			}
			html += '</'+tag+'>';
			
			if(self.mode != 'simulator') {
				html += '<div class="clearB"></div>';
			}
			return html;
		},
		getBrandContent: function(elevatedBrandList, name, count, isElevated) {
			var self = this;
			var html = '';
			
			var tag = (self.mode == 'simulator') ? 'li' : 'div';
			
			var visibilityStyle = (isElevated == false && $.inArray(name, elevatedBrandList) > -1) ? 'style="display:none;"' : '';
			
			html += '<'+tag+' class="'+(self.mode == 'simulator' ? '' : 'itemNamePreviewBrand')+' '+(isElevated == true ? 'elevatedBrand' : '')+'" '+visibilityStyle+'>';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="brandKeywordListener">';
			html += '		<span id="brand">'+name+'</span>';
			if(self.mode == 'simulator')
				html += '</a">';
			html += '</'+tag+'>';
			
			if(self.mode != 'simulator') {
				html += '<div class="clearB"></div>';
			}

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
						$("#typeahead").hide();
					}
				});
			});

		},
		addCategoryListener: function(selector) {
			var self = this;
			$(self.categoryTarget).find(selector).on({
				click: function() {
					var keyword = self.manager.store.get('q').value;
					$(self.searchBox).val(keyword);

					var searchManager = self.manager.searchManager;

					self.clearFilters(searchManager);

					searchManager.store.addByValue("fq", GLOBAL_storeFacetTemplate + ':' + self.escapeValue($(this).find('span#category').text())+'*');
					searchManager.store.addByValue("q", keyword);

					for(obj in self.defaultParams) {
						searchManager.store.addByValue(obj, self.defaultParams[obj]);
					}

					searchManager.doRequest(0);
					$("#typeahead").hide();
				}
			});
		},
		addBrandListener: function(selector) {
			var self = this;
			$(self.brandTarget).find(selector).on({
				click: function() {
					var keyword = self.manager.store.get('q').value;
					$(self.searchBox).val(keyword);

					var searchManager = self.manager.searchManager;

					self.clearFilters(searchManager);

					searchManager.store.addByValue("fq", 'Manufacturer:"'+$(this).find('span').text()+'"');
					searchManager.store.addByValue("q", keyword);
					for(obj in self.defaultParams) {
						searchManager.store.addByValue(obj, self.defaultParams[obj]);
					}
					searchManager.doRequest(0);

					$("#typeahead").hide();
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
