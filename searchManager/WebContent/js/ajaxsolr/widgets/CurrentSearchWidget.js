(function ($) {

	AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
		afterRequest: function () {
			var self = this;
			var links = [];
			$(this.target).empty();

			var q = this.manager.store.values('q'); 

			if ($.isBlank(q)) return;
			
			for (var i = 0, l = q.length; i < l; i++) {
				links.push(AjaxSolr.theme('createLink', 'Search keyword: ' + q[i], self.removeKeyword(q[i])));
			}

			var fq = this.manager.store.values('fq');
			var searchWithin = $.cookie('searchWithin');
			for (var i = 0, l = fq.length; i < l; i++) {
				if (fq[i] == searchWithin) {
					links.push(AjaxSolr.theme('createLink', "Search Within: " + fq[i], self.removeFacet(fq[i])));
				}else if($.startsWith(fq[i],GLOBAL_storeFacetTemplateName)){ // Facet Template Name / Or Find By display
					var facetTempVal = fq[i].substring(GLOBAL_storeFacetTemplateName.length+1,fq[i].length);
					links.push(AjaxSolr.theme('createLink', "Or Find By: " + facetTempVal, self.removeFacetTemplate(fq[i], facetTempArr, (parseInt(item) + 1)), "level" + (parseInt(item) + 1)));
				}else if($.startsWith(fq[i],GLOBAL_storeFacetTemplate)){ // Facet Hierarchical display
					var facetTempVal = fq[i].substring(GLOBAL_storeFacetTemplate.length+1,fq[i].length);
					var facetTempArr = facetTempVal.split("?|?");

					for (var item in facetTempArr){
						var trimmed = $.trim(facetTempArr[item].replace(/\*/g,'?').replace(/\?/g,' '));
						links.push(AjaxSolr.theme('createLink', item==0? "Category: " + trimmed : trimmed , self.removeFacetTemplate(fq[i], facetTempArr, (parseInt(item) + 1)), "level" + (parseInt(item) + 1)));
					}
				}
				else {
					var displayString = fq[i];
					var inDoubleQuote = false;
					for (var currIndex = displayString.indexOf(':'); currIndex < displayString.length; currIndex++) {
						if (displayString.charAt(currIndex) === ' ' && !inDoubleQuote) {
							displayString = displayString.substr(0, currIndex) + ', ' + displayString.substr(currIndex + 1);
							currIndex++;
						}
						else if (displayString.charAt(currIndex) === '"') {
							inDoubleQuote = !inDoubleQuote;
						} 
					}
					links.push(AjaxSolr.theme('createLink', displayString, self.removeFacet(fq[i])));
				}
			}

			

			if(links.length > 0){
				$(this.target).append(AjaxSolr.theme('createFacetHolder', "Current Selection", this.id));

				if (links.length > 1) {
					links.unshift(AjaxSolr.theme('createLink', 'Remove All Filters', self.removeAllFilters()));
				}

				if (links.length) {
					AjaxSolr.theme('createSelectionLink', this.id, links);
				}
			}

		},

		removeFacet: function (facet) {
			var self = this;
			return function () {
				if (self.manager.store.removeByValue('fq', facet)) {
					if (facet == $.cookie('searchWithin')) {
						$.cookie('searchWithin', '', {expires: 1});
					}
					self.manager.doRequest(0);
				}
				return false;
			};
		},

		removeFacetTemplate: function (fqVal, facetTemplateArr, level) {
			var self = this;
			return function () {
				if (self.manager.store.removeByValue('fq', fqVal)) {
					switch(level){
					case 3:
						self.manager.store.addByValue('fq', GLOBAL_storeFacetTemplate + ":" + $.trim(facetTemplateArr[0]) + "?|?" + $.trim(facetTemplateArr[1]) + "*");
						break;
					
					case 2:
						self.manager.store.addByValue('fq', GLOBAL_storeFacetTemplate + ":" + $.trim(facetTemplateArr[0]) + "*");
						break;

					case 1:
						break;
					}
					self.manager.doRequest(0);
				}
				return false;
			};
		},

		removeKeyword: function (facet) {
			var self = this;
			return function () {
				if (self.manager.store.removeByValue('q', facet)) {
					self.manager.doRequest(0);
				}
				return false;
			};
		},

		removeAllFilters: function() { 
			var self = this;
			return function () {
				$.cookie('searchWithin', '', {expires: 1});
				self.manager.store.remove('fq');
				self.manager.doRequest(0);
				return false;
			};
		}

	});

})(jQuery);
