var rowPerPage=10;
var moduleName="Auto Import";
function changePage(pageNumber){
	$("#mainContainer").empty().append("Loading....");	
	$("#mainContainer").load("/searchManager/autoimport/" + GLOBAL_storeId + "/page/" + pageNumber,function(){showPaging();});	
}
function showPaging(){
	var currentPage = $('#currentPageNumber').val();
	var totalItem = $('#totalItem').val();	
	if (parseInt(currentPage)-1 >= parseInt(totalItem)/rowPerPage){
		currentPage=parseInt(currentPage)-1;
		changePage(currentPage);
	}
	$("#sortablePagingTop, #sortablePagingBottom").paginate({
		currentPage:currentPage, 
		pageSize:rowPerPage,
		totalItem:totalItem,
		callbackText: function(itemStart, itemEnd, itemTotal){
			var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
			return displayText;
		},
		pageLinkCallback: function(e){ 
			var pageNumber=parseInt(e.data.page);
			changePage(pageNumber);
		},
		nextLinkCallback: function(e){ 
			var pageNumber=parseInt(e.data.page) + 1;
			changePage(pageNumber);			
		},
		prevLinkCallback: function(e){
			var pageNumber=parseInt(e.data.page) - 1;
			changePage(pageNumber);
		}
	});
}
function loadPaging() {
	var currentPage = $('#currentPageNumber').val();
	changePage(currentPage);
}
(function($){

	$(document).ready(function(){
		importRuleTask = {
				init : function(){		
					showPaging();
					$("#titleText").html(moduleName);
				}				
		};
		importRuleTask.init();
	});
})(jQuery);	
