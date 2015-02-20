
(function($){
	$(document).ready(function(){
		excelFileUploadedPlugin = {
				showMainPage:function() {
					var ruleType = $("#titleText").text().toLowerCase().replace(" for","").trim();
					var storeId = GLOBAL_storeId;					
					$("#preloader").show();
					$("#noSelected").hide();
					$("#noSelected").load("/searchManager/excelFileUploaded/" + storeId + "/" + ruleType + "/" + (new Date()).getTime(),function(){
						$("#preloader").hide();
						$("#noSelected").show();
					});
					$(".plugin-rulestatusbar").hide();
					$("#ruleSelected").hide();
					$("#ruleItemPagingTop").hide();
					$("#ruleItemPagingBottom").hide();
					$("#ruleItemDisplayOptions").hide();
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
