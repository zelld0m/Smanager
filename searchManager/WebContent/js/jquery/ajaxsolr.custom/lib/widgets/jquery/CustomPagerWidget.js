(function ($) {

AjaxSolr.CustomPagerWidget = AjaxSolr.AbstractWidget.extend({
  beforeRequest: function(){

  },
  
  afterRequest: function () {
	var self = this;
    $(self.target).html("hello");
    
    var perPage = parseInt(self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.rows || 10);
    var offset = parseInt(self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.start || 0);
    var totalItem = parseInt(self.manager.response.response.numFound);
    var qTime = parseInt(self.manager.response.responseHeader.QTime);
    
    // Normalize the offset to a multiple of perPage.
    offset = offset - offset % perPage;

    var currentPage = Math.ceil((offset + 1) / perPage);
    self.totalPages = Math.ceil(totalItem / perPage);
return;
    $(self.target).paginate({
		type: 'short',
		pageStyle: $(self.style),
		currentPage: currentPage, 
		pageSize: perPage,
		totalItem: totalItem,
		callbackText: function(itemStart, itemEnd, itemTotal){
			return itemStart + "-" + itemEnd + " of " + itemTotal;
		},
		pageLinkCallback: function(e){ 
		
		},
		nextLinkCallback: function(e){ 
			 
		},
		prevLinkCallback: function(e){ 
			
		},
		firstLinkCallback: function(e){ 
			
		},
		lastLinkCallback: function(e){ 
			
		}
	});
  }
});

})(jQuery);