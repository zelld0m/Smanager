(function($){

	$(document).ready(
			
		function(){
		excelFileReport = {
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
						click: excelFileUploaded.addToRule
					});	
					$(".showHide").on({
						click: excelFileReport.showHide
					});					
				}				
		};
		excelFileReport.init();		
	});
})(jQuery);	
