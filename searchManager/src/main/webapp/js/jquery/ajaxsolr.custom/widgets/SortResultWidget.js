(function ($) {

	AjaxSolr.SortResultWidget = AjaxSolr.AbstractWidget.extend({
		priceSuffix: "_CartPrice",
		sortOrder: "%s %sO, score desc, Popularity desc",
		
		beforeRequest: function () {
			var self = this;
			$(self.target).find("select").prop("disabled", true);
		
			// synch price sorting to catalog changes
			var sorting = self.manager.store.values('sort')[0];
			var isSortByPrice = $.endsWith(sorting.split(",")[0].split(" ")[0], "CartPrice");
			var priceBySortOrder = sorting.split(",")[0].split(" ")[1];
			
			if(GLOBAL_storeId==="pcmallgov" && isSortByPrice){
				
				self.manager.store.removeByValue('sort', sorting);
				
				switch(GLOBAL_PCMGCatalog.toLowerCase()){
				case "open": 
					self.priceSuffix = "_OpenCartPrice";
					break; 
				case "government": 
					self.priceSuffix = "_GovCartPrice";
					break; 
				case "academic": 
					self.priceSuffix = "_ACACartPrice";
					break; 	
				}
				self.manager.store.addByValue('sort', self.sortOrder.replace("%s", GLOBAL_storeFacetName + self.priceSuffix).replace("%sO", priceBySortOrder));
			}
		},
		
		afterRequest: function () {
			var self = this;
			$(self.target).empty();

			if (self.manager.response.response.docs.length > 0 && $.isNotBlank(self.manager.store.values('q'))){
				var defaultPageOptions = 5;
				var defaultPageInterval = 5;
				var totalResults = this.manager.response.response.numFound;

				var solrSortBest = self.sortOrder.replace("%s","CatCodeOrder").replace("%sO", "asc");
				
				if(GLOBAL_storeId==="pcmallgov"){
					switch(GLOBAL_PCMGCatalog.toLowerCase()){
					case "open": 
						self.priceSuffix = "_OpenCartPrice";
						break; 
					case "government": 
						self.priceSuffix = "_GovCartPrice";
						break; 
					case "academic": 
						self.priceSuffix = "_ACACartPrice";
						break; 	
					}
				}
				
				var priceSorting = GLOBAL_storeFacetName + self.priceSuffix;
				var solrSortLowest = self.sortOrder.replace("%s",priceSorting).replace("%sO", "asc");;
				var solrSortHighest = self.sortOrder.replace("%s",priceSorting).replace("%sO", "desc");	

				var sort = {
						best: 'Best Match',
						lowest: 'Lowest Price First',
						highest: 'Highest Price First'
				};

				var rows = self.manager.store.values('rows');
				var perPageOptions = {};

				for (var i = 1 ; i < parseInt(this.pageOptions || defaultPageOptions) + 1; i++) {
					var displayText = parseInt(this.perPageInterval || defaultPageInterval)*i;
					if (parseInt(totalResults) >= displayText || displayText == rows )
						perPageOptions[parseInt(this.perPageInterval || defaultPageInterval)*i] = displayText;
				}

				var selectedSort = "best";
				var selectedPageOptions = parseInt(this.perPageInterval || defaultPageInterval);

				var sorting = self.manager.store.values('sort');

				if (rows)
					selectedPageOptions = rows;

				if (sorting == solrSortLowest){
					selectedSort = "lowest";
				}

				if (sorting == solrSortHighest){
					selectedSort = "highest";
				}

				if ($(this.perPageLabel)){
					$(this.target).append(this.perPageLabel);
				}

				$(this.target).append(AjaxSolr.theme('select_tag', 'itemsPerPage', AjaxSolr.theme('options_for_select', perPageOptions, selectedPageOptions)));
				$(this.target).append(" ");
				
				if ($(this.sortLabel)){
					$(this.target).append(this.sortLabel);
				}

				$(this.target).append(AjaxSolr.theme('select_tag', 'sortBy', AjaxSolr.theme('options_for_select', sort, selectedSort)));
				
				$(this.target).find('#sortBy').change(function () {
					var value = ($(this).val()).trim();
					if (value=='best') {
						self.manager.store.addByValue('sort', solrSortBest);
						self.manager.doRequest(0);
					}else if (value=='lowest') {
						self.manager.store.addByValue('sort', solrSortLowest);
						self.manager.doRequest(0);
					}else if (value=='highest') {
						self.manager.store.addByValue('sort', solrSortHighest);
						self.manager.doRequest(0);
					}
				});

				$(this.target).find('#itemsPerPage').change(function () {
					var value = ($(this).val()).trim();
					if (value){
						self.manager.store.addByValue('rows', value);
						self.manager.doRequest(0);
					}
				});
			}
		}
	});
})(jQuery);