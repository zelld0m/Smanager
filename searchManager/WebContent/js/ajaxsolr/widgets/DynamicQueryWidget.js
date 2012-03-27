(function ($) {

AjaxSolr.DynamicQueryWidget = AjaxSolr.AbstractFacetWidget.extend({
  
  beforeRequest: function () {
	var self = this;
	for (var i = 0; i < AjaxSolr.size(self.fields); i++) {
        var field = self.fields[i];
		for (var j = 0; j <  AjaxSolr.size(self.fields[field]); j++) {
			var template = "{0}:{1}";
			if (self[field][j].indexOf("TO") >= 0) template = "{0}:[{1}]";
			self.manager.store.addByValue('facet.query', String.prototype.formatString(template,field,self[field][j]));
		}	
	}
	
  },
  
  afterRequest: function () {
	var self = this;
  
    if (this.manager.response.facet_counts.facet_queries === undefined) {
      $(this.target).html(AjaxSolr.theme('no_items_found'));
      return;
    }

	$(this.target).empty();
	
	var output = ''
	for (var i = 0; i < self.fields.length; i++) {
		output += AjaxSolr.theme('createFacetHolder', self.fields[i], self.fields[i]);
	}
	
	$(this.target).html(output);
	
	for (var i = 0; i < self.fields.length; i++) {
        var field = self.fields[i];
		
		var objectedItems =[];
        alert(AjaxSolr.size(this.manager.response.facet_counts.facet_queries));
		for (facetValues in this.manager.response.facet_counts.facet_queries) {
			
		  var count = parseInt(this.manager.response.facet_counts.facet_queries[facetValues]);
		  alert(facetValues + "c: " + count );
		  if (count == 0) continue;
		  
		  objectedItems.push({ facet: facetValues, count: count });
		}

		for (var i = 0, l = objectedItems.length; i < l; i++) {
			var facet = objectedItems[i].facet;
			var count = objectedItems[i].count;
			
			$('#' + field).append(AjaxSolr.theme('createFacetLink', removeFieldName(facet) + " (" + count + ")", this.clickHandler(field, facet)));
			
			if (i == l-1 && AjaxSolr.size(facetValues) > displayLimit)
				$('#' + field).append(AjaxSolr.theme('displayFacetMoreOptions','[+] More Options', facetField, facetValues));
		}		
		
	}
	
	removeFieldName: function(field){
		var output = field;
		
		if (output.indexOf(':') >= 0) 
			output = output.split(':')[2];
		
		if (String.prototype.trim(output) != "" && output.length > 0)
			return output;
			
		return field;
	}
  }
});

})(jQuery);