function showMainPage() {
	var ruleType = $("#titleText").text().toLowerCase();
	$("#ruleItemContainer").empty().append("Loading....");
	$("#ruleItemContainer").load("/searchManager/excelFileUploaded/" + ruleType);
}
(function($){
	$(document).ready(function(){
		excelFileUploadedPlugin = {		
				viewMain : function(){
					showMainPage();
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
