(function ($) {

	AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
		afterRequest: function () {
			var self = this;
			var links = [];
			$(self.target).empty();

			var keyword = self.manager.store.values('q'); 
			
			if ($.isBlank(keyword)) return;
			
			links.push(AjaxSolr.theme('createLink', 'Search keyword: ' + keyword, self.removeKeyword(keyword)));
			
			var fq = self.manager.store.values('fq');
			var searchWithin = self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"];
			var dynamicAttr = self.manager.widgets['dynamicAttribute'].attribMap;
			
			for (var i = 0, l = fq.length; i < l; i++) {
				if (fq[i] === searchWithin) {
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
					var filterFieldName = displayString.substr(0, displayString.indexOf(':'));
					var filterFieldValue = displayString.substr(displayString.indexOf(':') + 1, displayString.length);
					var isMultipleSelection = filterFieldValue.indexOf('(')==0 && filterFieldValue.indexOf(')')==filterFieldValue.length-1;
					var isDynamicAttr = dynamicAttr && dynamicAttr[filterFieldName];
					var regX = /("[^"]+")|(\b\w+\b)/g;
					var arrSelection = filterFieldValue.match(regX);
					
					if(isMultipleSelection){
						links.push(AjaxSolr.theme('createLink', "Remove All " + filterFieldName, self.removeFacet(fq[i])));
					}
					
					if(isDynamicAttr){	// TODO Dynamic Attribute
						var displayName = dynamicAttr[filterFieldName].attributeDisplayName;
						var displayValue = displayString.substr(displayString.indexOf(':')); 

						for (var currIndex = displayValue.indexOf(':'); currIndex < displayValue.length; currIndex++) {
							if (displayValue.charAt(currIndex) === '|' && currIndex >= 2) {
								displayValue = displayValue.substr(0, currIndex-2) + displayValue.substr(currIndex + 1);
								currIndex = currIndex - 2;
							}
						}
						
						links.push(AjaxSolr.theme('createLink', displayName + displayValue, self.removeFacet(fq[i])));
					}else{
						for(var i=0; i < arrSelection.length; i++){
							links.push(AjaxSolr.theme('createLink', arrSelection[i],  self.removeFacet(fq[i])));
						}
					}
				}
			}

			if(links.length > 0){
				$(self.target).append(AjaxSolr.theme('createFacetHolder', "Current Selection", self.id));

				if (links.length > 1) {
					links.unshift(AjaxSolr.theme('createLink', 'Remove All Filters', self.removeAllFilters()));
				}

				if (links.length) {
					AjaxSolr.theme('createSelectionLink', $.formatAsId(this.id), links);
				}
			}

		},

		removeFacet: function (facetValue) {
			var self = this;
			return function () {
				if (self.manager.store.removeByValue('fq', facetValue)) {
					if (facetValue === self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"]) {
						self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"] = "";
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

		removeKeyword: function (keyword) {
			var self = this;
			return function () {
				if (self.manager.store.removeByValue('q', keyword)) {
					self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"] = "";
					self.manager.store.remove('fq');
					self.manager.doRequest(0);
				}
				return false;
			};
		},

		removeAllFilters: function() { 
			var self = this;
			return function () {
				self.manager.store.remove('fq');
				self.manager.doRequest(0);
				return false;
			};
		}

	});

})(jQuery);
