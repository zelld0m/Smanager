(function ($) {

AjaxSolr.CurrentSearchWidget = AjaxSolr.AbstractWidget.extend({
  afterRequest: function () {
    var self = this;
    var links = [];

	var q = this.manager.store.values('q'); 
	
    for (var i = 0, l = q.length; i < l; i++) {
		links.push(AjaxSolr.theme('createLink', 'Search keyword: ' + q[i], self.removeKeyword(q[i])));
    }
	
    var fq = this.manager.store.values('fq');
    var searchWithin = $.cookie('searchWithin');
    for (var i = 0, l = fq.length; i < l; i++) {
    	if (fq[i] == searchWithin) {
        	links.push(AjaxSolr.theme('createLink', "Search Within: " + fq[i], self.removeFacet(fq[i])));
    	}
    	else {
    		links.push(AjaxSolr.theme('createLink', fq[i], self.removeFacet(fq[i])));
    	}
    }

	$(this.target).empty();
	
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
