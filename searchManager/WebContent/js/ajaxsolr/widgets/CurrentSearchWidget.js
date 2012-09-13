(function ($) {

	AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
		afterRequest: function () {
			var self = this;
			var links = [];
			$(self.target).empty();

			var keyword = self.manager.store.values('q'); 
			
			if ($.isBlank(keyword)) return;
			
			links.push(AjaxSolr.theme('createLink', "Search keyword: " + keyword, self.removeKeyword(keyword)));
			
			var fq = self.manager.store.values('fq');
			var searchWithin = self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"];
			var dynamicAttr = self.manager.widgets['dynamicAttribute'].attribMap;
			
			for (var i = 0, l = fq.length; i < l; i++) {
				var facetValue = fq[i];
				var filterFieldName = facetValue.substr(0, facetValue.indexOf(':'));
				var filterFieldValue = facetValue.substr(facetValue.indexOf(':') + 1);
				
				if(GLOBAL_storeFacetTemplate === filterFieldName){ // Facet Hierarchical display
					var facetTempVal = facetValue.substr(GLOBAL_storeFacetTemplate.length + 1);
					var facetTempArr = facetTempVal.split("?|?");

					for(var j=0; j < facetTempArr.length; j++){
						var trimmed = $.trim(facetTempArr[j].replace(/\*/g,'?').replace(/\?/g,' '));
						links.push(AjaxSolr.theme('createLink', j==0? "Category: " + trimmed : trimmed , self.removeFacetTemplate(fq[i], facetTempArr, (parseInt(j) + 1)), "level" + (parseInt(j) + 1)));
					}
				}
				else {
					var isMultipleSelection = filterFieldValue.indexOf('(')==0 && filterFieldValue.indexOf(')')==filterFieldValue.length-1;
					var isDynamicAttr = dynamicAttr && dynamicAttr[filterFieldName];
					var arrSelection = filterFieldValue.match(/("[^"]+")|(\b\w+\b)/g);
					var clickHandler = self.removeFacet(facetValue);
					var displayFieldName = filterFieldName;
					
					if (facetValue === searchWithin){
						displayFieldName = "Search Within";
					}else if($.startsWith(facetValue, GLOBAL_storeFacetTemplateName)){
						displayFieldName = "Or Find By";
					}
					else if(isDynamicAttr){
						displayFieldName = dynamicAttr[filterFieldName].attributeDisplayName;
					}

					if(isMultipleSelection){
						links.push(AjaxSolr.theme('createLink', "Remove All " + displayFieldName, clickHandler, "removeAll"));
					}
					
					for(var k=0; k < arrSelection.length; k++){
						var selectedItem = arrSelection[k];
						
						if(isMultipleSelection){
							clickHandler = self.removeFacetFromSelection(arrSelection, filterFieldName, filterFieldValue, selectedItem);
						}

						if($.startsWith(selectedItem,'"') && $.endsWith(selectedItem,'"')){
							selectedItem = selectedItem.substring(1, selectedItem.length - 1);
						}
						
						if(isDynamicAttr){
							selectedItem = selectedItem.substr(selectedItem.indexOf('|') + 1);
						}
						
						links.push(AjaxSolr.theme('createLink', arrSelection.length==1? displayFieldName + ": " + selectedItem: selectedItem, clickHandler));
					}
				}
			}

			if(links.length > 0){
				$(self.target).append(AjaxSolr.theme('createFacetHolder', "Current Selection", self.id));

				if (links.length > 1) {
					links.unshift(AjaxSolr.theme('createLink', 'Remove All Filters', self.removeAllFilters(), "removeAll"));
				}

				if (links.length) {
					AjaxSolr.theme('createSelectionLink', $.formatAsId(this.id), links);
				}
			}

		},

		removeFacetFromSelection: function(arrSelection, facetName, facetValue, facetRemove) {
			var self = this;
			return function () {
				if (self.manager.store.removeByValue('fq', facetName + ":" + facetValue)) {
					arrSelection = $.grep(arrSelection, function(value) {
						  return value !== facetRemove;
						});
					
					if (arrSelection.length > 0){
						var fqNewValue = arrSelection.length == 1? arrSelection[0] : "(" + arrSelection.join(" ") + ")";  
						self.manager.store.addByValue('fq', facetName + ":" + fqNewValue);
					}
					
					self.manager.doRequest(0);
					
				}
				return false;
			};
		},
		
		removeFacet: function(facetValue) {
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

		removeFacetTemplate: function(fqVal, facetTemplateArr, level) {
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

		removeKeyword: function(keyword) {
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
