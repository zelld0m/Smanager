
(function($){
	$(document).ready(function(){
		excelFileUploadedPlugin = {
				showMainPage:function() {
					var ruleType = $("#titleText").text().toLowerCase();
					var storeId = GLOBAL_storeId;
					$("#ruleItemContainer").empty().append("Loading....");
					$("#ruleItemContainer").load("/searchManager/excelFileUploaded/" + storeId + "/" + ruleType);
				},
				viewMain : function(){
					excelFileUploadedPlugin.showMainPage();
				},
				init : function(){
					$("#uploadFromExcel").on({
						click: excelFileUploadedPlugin.viewMain
					});
					}
		};
		
		excelFileUploadedPlugin.init();
	});
})(jQuery);
