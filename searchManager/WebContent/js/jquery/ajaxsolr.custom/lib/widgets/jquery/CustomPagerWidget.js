(function ($) {

AjaxSolr.CustomPagerWidget = AjaxSolr.AbstractWidget.extend({
  beforeRequest: function(){

  },
  
  afterRequest: function () {
	var self = this;
    $(self.target).empty();
    var perPage = parseInt(self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.rows || 10);
    var offset = parseInt(self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.start || 0);
    var totalItem = parseInt(self.manager.response.response.numFound);
    var qTime = parseInt(self.manager.response.responseHeader.QTime);
    
    // Normalize the offset to a multiple of perPage.
    offset = offset - offset % perPage;

    var currentPage = Math.ceil((offset + 1) / perPage);
    self.totalPages = Math.ceil(totalItem / perPage);

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
			base.getList(keyword, e.data.page); 
			base.options.pageChangeCallback(e.data.page); 
		},
		nextLinkCallback: function(e){ 
			base.getList(keyword, parseInt(e.data.page)+1); 
			base.options.pageChangeCallback(parseInt(e.data.page)+1); 
		},
		prevLinkCallback: function(e){ 
			base.getList(keyword, parseInt(e.data.page)-1); 
			base.options.pageChangeCallback(parseInt(e.data.page)-1);
		},
		firstLinkCallback: function(e){ 
			base.getList(keyword, 1); 
			base.options.pageChangeCallback(1);
		},
		lastLinkCallback: function(e){ 
			base.getList(keyword, e.data.totalPages); 
			base.options.pageChangeCallback(e.data.totalPages);
		}
	});
  }
});

})(jQuery);