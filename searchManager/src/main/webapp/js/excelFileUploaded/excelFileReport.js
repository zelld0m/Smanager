(function($){

	$(document).ready(
			
		function(){
		excelFileReport = {	
				addToRule : function(e){
					var fileName=$("#fileName").val();
					var msg = "Are you sure you want to add to rule all entry in [" + fileName + "] excel file Upload?";
					jConfirm(msg, "Confirm", function(status){					
						if(status){					
								var excelFileUploadedId=$("#excelFileUploadedId").val();
								var isClear = $('#clearRuleFirst').is(":checked");
								var ruleType = $("#titleText").text().toLowerCase();
								var storeId = GLOBAL_storeId;
								ExcelFileUploadedServiceJS.updateExcelFileUploaded(excelFileUploadedId,storeId,ruleType,isClear,{
									callback: function(count){
										excelFileUploaded.loadPaging();									
									},
									preHook: function(){ 
									},
									postHook: function(){ 	
									}
								});						
							}
					});
				},				
				showHide : function(){
					var keyword=$("#keyword").val();
					
					if ($( this ).text() == "+"){
						$("#"+keyword).show();
						$( this ).text("-");
					}else{
						$("#"+keyword).hide();
						$( this ).text("+");						
					}					
					 
				},				
				init : function(){
					$("#delete").on({
						click: excelFileUploaded.deleteExcelFileUploaded
					});
					$("#addToRule").on({
						click: excelFileReport.addToRule
					});	
					$(".showHide").on({
						click: excelFileReport.showHide
					});					
				}				
		};
		excelFileReport.init();		
	});
})(jQuery);	
