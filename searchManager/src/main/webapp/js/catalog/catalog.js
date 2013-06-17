(function ($) {
	$(document).ready(function() { 
		var catalogListPageSize = 10;
		var catalogListHeaderText = "Catalog";
		var catalogListSearchText = "Enter Name";
		
		var populateCatalogList = function(keyword, page){
			$("#catalogList").sidepanel({
				page: page,
				pageSize: catalogListPageSize,
				headerText: catalogListHeaderText,
				searchText: catalogListSearchText
			});
		};
		
		var initPage = function(){
			
		};

		
		
		initPage();
	
	});
})(jQuery);