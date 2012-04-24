(function($){

	$(selector).sidepanel({
		fieldId: "keywordId",
		fieldName: "keyword",
		headerText : headerText,
		searchText : searchText,
		page: itemPage,
		pageSize: itemPageSize,

		itemNameCallback: showItem,
		itemDataCallback: function(base, keyword, page){
			StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
				callback: function(data){
					base.populateList(data);
					base.addPaging(keyword, page, data.totalSize);
				},
				preHook: function(){ base.prepareList(); }
			});
		}
	});

})(jQuery);	