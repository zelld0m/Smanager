(function($){

	$(document).ready(function(){
		importRuleTask = {
				rowPerPage:10,
				moduleName:"Auto Import",
				changePage: function(pageNumber){
					$("#mainContainer").empty().append("Loading....");	
					$("#mainContainer").load("/searchManager/autoimport/" + GLOBAL_storeId + "/page/" + pageNumber,function(){importRuleTask.showPaging();});	
				},
				showPaging : function showPaging(){
					var self = this;
					var currentPage = $('#currentPageNumber').val();
					var totalItem = $('#totalItem').val();	
					if (parseInt(currentPage)-1 >= parseInt(totalItem)/self.rowPerPage){
						currentPage=parseInt(currentPage)-1;
						importRuleTask.changePage(currentPage);
					}
					$("#sortablePagingTop, #sortablePagingBottom").paginate({
						currentPage:currentPage, 
						pageSize:self.rowPerPage,
						totalItem:totalItem,
						callbackText: function(itemStart, itemEnd, itemTotal){
							var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
							return displayText;
						},
						pageLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page);
							importRuleTask.changePage(pageNumber);
						},
						nextLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page) + 1;
							changePage(pageNumber);			
						},
						prevLinkCallback: function(e){
							var pageNumber=parseInt(e.data.page) - 1;
							importRuleTask.changePage(pageNumber);
						}
					});
				},
				loadPaging : function() {
					var currentPage = $('#currentPageNumber').val();
					importRuleTask.changePage(currentPage);
				},				
				init : function(){		
					importRuleTask.showPaging();
					$("#titleText").html(this.moduleName);
				}				
		};
		importRuleTask.init();
	});
})(jQuery);	
