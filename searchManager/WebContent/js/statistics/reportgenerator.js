(function( $ ){

	var ReportGenerator = {
			
			generateZeroResults: function(){
				
				var file = dwr.util.getValue('keywordFile');
				var $selectFormat = $("select#fileFormat");
				
				ReportGeneratorServiceJS.generateZeroResults(file,$selectFormat.val(),{
					callback: function(data){
						if(data!=null)
							dwr.engine.openInDownload(data);
						else
							jAlert("No zero result found.","Report Generator");
					}
				});
			},
			generateTopKeywords: function(){
				
				var file = dwr.util.getValue('keywordFile');
				var $selectFormat = $("select#fileFormat");
				
				ReportGeneratorServiceJS.generateTopKeywords(file,$selectFormat.val(),{
					callback: function(data){
						if(data!=null)
							dwr.engine.openInDownload(data);
						else
							jAlert("No top keyword found.","Report Generator");
					}
				});
			},	
			
			init : function(){
				
				var self = this;
				
				var $selectFormat = $("select#fileFormat");
				$selectFormat.append($("<option>", {value:"Search Manager"}).text("Search Manager"));
				$selectFormat.append($("<option>", {value:"Adobe"}).text("Adobe"));
				
				var $selectReport = $("select#reportType");
				$selectReport.append($("<option>", {value:"Top Keywords"}).text("Top Keywords"));
				$selectReport.append($("<option>", {value:"Zero Resuts"}).text("Zero Resuts"));
					
				$("#generateBtn").on({
					click: function(){
						if($selectReport.val()=="Zero Resuts")
							self.generateZeroResults();
						if($selectReport.val()=="Top Keywords")
							self.generateTopKeywords();
					}
				});
			}
	};
	
			$(document).ready(function() {
				ReportGenerator.init();
			});	
})(jQuery);