(function ($) {

AjaxSolr.CustomPagerWidget = AjaxSolr.AbstractWidget.extend({
  beforeRequest: function(){
	  $(this.target).empty();
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

    $(self.target).paginate({
		type: 'short',
		pageStyle: self.style,
		currentPage: currentPage, 
		pageSize: perPage,
		totalItem: totalItem,
		callbackText: function(itemStart, itemEnd, itemTotal){
			return "";
		},
		
		pageLinkCallback: function(e){ 
			var start = (e.data.page - 1) * (self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.rows || 10);
			self.manager.store.get('start').val(start);
		    self.manager.doRequest();
		},
		
		nextLinkCallback: function(e){
			var start = (e.data.page) * (self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.rows || 10);
			self.manager.store.get('start').val(start);
		    self.manager.doRequest();
		},
		
		prevLinkCallback: function(e){ 
			var start = (e.data.page - 2) * (self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.rows || 10);
			self.manager.store.get('start').val(start);
		    self.manager.doRequest();
		},
		
		firstLinkCallback: function(e){ 
			self.manager.store.get('start').val(0);
		    self.manager.doRequest();
		},
		
		lastLinkCallback: function(e){ 
			var start = (e.data.totalPages-1) * (self.manager.response.responseHeader.params && self.manager.response.responseHeader.params.rows || 10);
			self.manager.store.get('start').val(start);
			self.manager.doRequest();
		}
	});
    
    self.renderHeader(perPage, offset, totalItem, qTime);
  }
});

})(jQuery);