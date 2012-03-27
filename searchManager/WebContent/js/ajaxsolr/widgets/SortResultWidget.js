(function ($) {

	AjaxSolr.SortResultWidget = AjaxSolr.AbstractFacetWidget.extend({
		afterRequest: function () {
		$(this.target).empty();
		$(this.target).parent("div").find("#importToExcel").empty();
		
		var self = this;
		
		if (self.manager.response.response.docs.length > 0 && $.isNotBlank(self.manager.store.values('q'))){

			var defaultPageOptions = 5;
			var defaultPageInterval = 5;
			var totalResults = this.manager.response.response.numFound;

			var solrSortBest = "CatCodeOrder asc, score desc, Popularity desc";
			var solrSortLowest = "CartPrice asc, CatCodeOrder asc, score desc, Popularity desc";
			var solrSortHighest = "CartPrice desc, CatCodeOrder asc, score desc, Popularity desc";	

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

			if ($(this.sortLabel)){
				$(this.target).append(this.sortLabel);
			}

			$(this.target).append(AjaxSolr.theme('select_tag', 'sortBy', AjaxSolr.theme('options_for_select', sort, selectedSort)));
			$(this.target).prepend('<a id="downloadIcon" href="javascript:void(0);"><div class="btnGraph btnDownload marT2 floatR"></div></a>');

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
