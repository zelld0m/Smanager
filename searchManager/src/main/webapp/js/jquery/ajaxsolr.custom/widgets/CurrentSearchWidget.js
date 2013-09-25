(function ($) {

	AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
		solrFieldToTextDisplayMap: [],
		
		init: function(){
			var self = this;
			
			self.solrFieldToTextDisplayMap["Licence_Flag:1"] = "License Products Only";
			self.solrFieldToTextDisplayMap["Licence_Flag:0"] = "Non-License Products Only";
			self.solrFieldToTextDisplayMap["ImageExists:1"] = "With Product Image Only";
			self.solrFieldToTextDisplayMap["ImageExists:0"] = "Without Product Image Only";

			self.solrFieldToTextDisplayMap["PCMG_ACAStoreFlag:true"] = "Academic Catalog";
			self.solrFieldToTextDisplayMap["PCMG_OpenStoreFlag:true"] = "Open Catalog";
			self.solrFieldToTextDisplayMap["PCMG_GovStoreFlag:true"] = "Government Catalog";

			self.solrFieldToTextDisplayMap["Refurbished_Flag:1"] = "Refurbished Products";
			self.solrFieldToTextDisplayMap["OpenBox_Flag:1"] = "Open Box Products";
			self.solrFieldToTextDisplayMap["Clearance_Flag:1"] = "Clearance Products";
			
		},
		
		afterRequest: function () {
			var self = this;
			var links = [];
			$(self.target).empty();

			var keyword = self.manager.store.values('q'); 
			
			if ($.isBlank(keyword)) return;
			
			links.push(AjaxSolr.theme('createLink', "Search keyword: " + keyword, self.removeKeyword(keyword)));
			
			var fq = self.manager.store.values('fq');
			var searchWithin = GLOBAL_searchWithinEnabled || self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"];
			var dynamicAttr = self.manager.widgets['dynamicAttribute'].attribMap;
			
			for (var i = 0, l = fq.length; i < l; i++) {
				var facetValue = fq[i];
				var filterFieldName = facetValue.substr(0, facetValue.indexOf(':'));
				var filterFieldValue = facetValue.substr(facetValue.indexOf(':') + 1);
				
				var conditionSelector = facetValue.match(/Refurbished_Flag|OpenBox_Flag|Clearance_Flag/g);
				var pcmgSelector = facetValue.match(/PCMG_ACAStoreFlag|PCMG_OpenStoreFlag|PCMG_GovStoreFlag/g);
				
				var multiFieldFq = !$.isEmptyObject(conditionSelector) && conditionSelector.length > 0  ||
				!$.isEmptyObject(pcmgSelector) && pcmgSelector.length > 0;
				
				
				if (!GLOBAL_searchWithinEnabled && facetValue === searchWithin){
					links.push(AjaxSolr.theme('createLink', "Search Within: " + facetValue  , self.removeFacetTemplate(facetValue), "single"));
				}else if(GLOBAL_storeFacetTemplate === filterFieldName){ // Facet Hierarchical display
					var facetTempVal = facetValue.substr(GLOBAL_storeFacetTemplate.length + 1);
					var facetTempArr = facetTempVal.split("?|?");

					for(var j=0; j < facetTempArr.length; j++){
						var trimmed = $.trim(facetTempArr[j].replace(/\*/g,'?').replace(/\?/g,' ').replace(/\\\(/g,"\(").replace(/\\\)/g,"\)")); // Dependency: CNetFacetWidget.js - escape function
						links.push(AjaxSolr.theme('createLink', j==0? "Category: " + trimmed : trimmed , self.removeFacetTemplate(fq[i], facetTempArr, (parseInt(j) + 1)), "level" + (parseInt(j) + 1)));
					}
				}
				// Multiple solr field in one fq
				else if(multiFieldFq){ 
					var clickHandler = self.removeFacet(facetValue);
					var displayFieldName = filterFieldName;

					displayFieldName = !$.isEmptyObject(conditionSelector) && conditionSelector.length > 0 ? "Condition" : displayFieldName;
					displayFieldName = !$.isEmptyObject(pcmgSelector) && pcmgSelector.length > 0 ? "Catalog" : displayFieldName;
					
					links.push(AjaxSolr.theme('createLink', "Remove All " + displayFieldName, clickHandler, "removeMultiple"));
					
					//TODO: remove single value in one fq
					var arrCurrentSelection = facetValue.split(' ');
					if(!$.isEmptyObject(arrCurrentSelection)){
						for(var j=0; j < arrCurrentSelection.length; j++){
							var displayOverride = self.solrFieldToTextDisplayMap[arrCurrentSelection[j]];
							if($.isNotBlank(displayOverride)){
								displayText = displayOverride;
							}else{
								displayText = arrCurrentSelection[i];
							}
							links.push(AjaxSolr.theme('createLink', displayText, self.removeMultiFieldFacet(arrCurrentSelection, j, facetValue), "multiple"));
						}
					}	
				}
				else { // Multiple value for single field
					var isMultipleSelection = filterFieldValue.indexOf('(')==0 && filterFieldValue.indexOf(')')==filterFieldValue.length-1;
					var isDynamicAttr = dynamicAttr && dynamicAttr[filterFieldName];
					var arrSelection = filterFieldValue.replace(/\\\"/g, "\%\%\%").match(/("[^"]+")|(\b\w+\b)/g);
					var clickHandler = self.removeFacet(facetValue);
					var displayFieldName = filterFieldName;
					var hasDisplayOverride = false;
					
					if($.startsWith(facetValue, GLOBAL_storeFacetTemplateName)){
						displayFieldName = "Or Find By";
					}
					else if(isDynamicAttr){
						displayFieldName = dynamicAttr[filterFieldName].attributeDisplayName;
					}else{
						var displayOverride = self.solrFieldToTextDisplayMap[displayFieldName + ":" + arrSelection[0]];

						if($.isNotBlank(displayOverride)){
							displayOverrideText = displayOverride;
							hasDisplayOverride = true;
						}
					}

					if(isMultipleSelection && arrSelection.length>1){
						links.push(AjaxSolr.theme('createLink', "Remove All " + displayFieldName, clickHandler, "removeMultiple"));
					}
					
					for(var k=0; k < arrSelection.length; k++){
						var selectedItem = arrSelection[k];
						
						if(isMultipleSelection){
							clickHandler = self.removeFacetFromSelection(arrSelection, filterFieldName, filterFieldValue.replace(/\%\%\%/g,"\""), selectedItem.replace(/\%\%\%/g,"\""));
						}

						if($.startsWith(selectedItem,'"') && $.endsWith(selectedItem,'"')){
							selectedItem = selectedItem.substring(1, selectedItem.length - 1);
						}
						
						if(isDynamicAttr){
							selectedItem = selectedItem.substr(selectedItem.indexOf('|') + 1);
						}
						
						selectedItem = selectedItem.replace(/\%\%\%/g,"\"");
						links.push(AjaxSolr.theme('createLink', arrSelection.length==1? (hasDisplayOverride? displayOverrideText :  displayFieldName + ": " + selectedItem): selectedItem, clickHandler, isMultipleSelection && arrSelection.length > 1? "multiple" : "single"));
					}
				}
			}

			var multiSearchWithin = GLOBAL_searchWithinEnabled && self.manager.widgets[WIDGET_ID_searchWithin];

			if (multiSearchWithin && !multiSearchWithin.isEmpty()) {
				links.push(AjaxSolr.theme('createLink', "Search Within", self.removeSearchWithin(), "removeMultiple"));

				$.each(multiSearchWithin.params, function(idx) {
					if (this.length > 0) {
						links.push(AjaxSolr.theme('createLink', multiSearchWithin.getLabel(idx), self.removeSearchWithin(idx), "level1"));
						$.each(this, function(){
							links.push(AjaxSolr.theme('createLink', this.toString(), self.removeSearchWithin(idx, this.toString()), "level2"));
						});
					}
				});
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
						  return value.replace(/\%\%\%/g,"\"") !== facetRemove;
						});
					
					if (arrSelection.length > 0){
						var fqNewValue = arrSelection.length == 1? arrSelection[0] : "(" + arrSelection.join(" ") + ")";  
						self.manager.store.addByValue('fq', facetName + ":" + fqNewValue.replace(/\%\%\%/g,"\\\""));
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
					if (!GLOBAL_searchWithinEnabled && facetValue === self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"]) {
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
					if (GLOBAL_searchWithinEnabled) {
						self.manager.widgets[WIDGET_ID_searchWithin].clear();
						self.manager.store.remove(self.manager.widgets[WIDGET_ID_searchWithin].paramName);
					} else {
						self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"] = "";
					}
					self.manager.store.remove('fq');
					self.manager.doRequest(0);
				}
				return false;
			};
		},

		removeAllFilters: function() { 
			var self = this;
			return function () {
				if (GLOBAL_searchWithinEnabled) {
					self.manager.widgets[WIDGET_ID_searchWithin].clear();
					self.manager.store.remove(self.manager.widgets[WIDGET_ID_searchWithin].paramName);
				} else {
					self.manager.widgets[WIDGET_ID_searchWithin]["searchWithin"] = "";
				}
				self.manager.store.remove('fq');
				self.manager.doRequest(0);
				return false;
			};
		},
		
		removeMultiFieldFacet: function(arr, ind, fVal){
			var self = this;
			return function () {
				arr.splice(ind,1);
				self.manager.store.removeByValue("fq", fVal);
				arr.length && self.manager.store.addByValue("fq", arr.join(' '));
				self.manager.doRequest(0);
				return false;
			};
		} 

		// Use only for multi search within version
		removeSearchWithin: function(type, text) {
			return function() {
				if (GLOBAL_searchWithinEnabled) {
					var widget = this.manager.widgets[WIDGET_ID_searchWithin];
					this.manager.store.remove(widget.paramName);

					widget.clear(type, text);
					widget.isEmpty() || this.manager.store.addByValue(widget.paramName, widget.paramsAsString());

					this.manager.doRequest(0);
				}
				return false;
			}.bind(this);
		}
	});

})(jQuery);
