(function( $ ){

	var ReportGenerator = {
			
			generateZeroResults: function(file,format){
				
				ReportGeneratorServiceJS.generateZeroResults(file,format,{
					callback: function(data){
						if(typeof(data) === "number")
							jAlert("Invalid format for " + format + ".","Report Generator");
						else if(typeof(data) === "string")
							dwr.engine.openInDownload(data);
						else
							jAlert("No zero result found.","Report Generator");
					}
				});
			},
			generateTopKeywords: function(file,format){
				
				ReportGeneratorServiceJS.generateTopKeywords(file,format,{
					callback: function(data){
						if(typeof(data) === "number")
							jAlert("Invalid format for " + format + ".","Report Generator");
						else if(typeof(data) === "string")
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
						var file = dwr.util.getValue('file');
						var $selectFormat = $("select#fileFormat");
						var $selectReport = $("select#reportType");
						var $fileName = $("input#file");
						var error = "";
						var parts = $fileName.val().split('.');
							
						if($fileName.val()=="")
							error = error + "Please input file.\n";
						else if(parts[parts.length - 1].toLowerCase() != "csv")
							error = error + "Invalid file type. Please choose a .csv file.\n";
						if($selectFormat.val()=="")
							error = error + "Please choose file format.\n";
						if($selectReport.val()=="")
							error = error + "Please choose report.";
						
						
						if(error!="")
							jAlert(error,"Report Generator");
						else{						
							if($selectReport.val()=="Zero Resuts")
								self.generateZeroResults(file,$selectFormat.val());
							if($selectReport.val()=="Top Keywords")
								self.generateTopKeywords(file,$selectFormat.val());
						}
					}
				});
			}
	};
	
			$(document).ready(function() {
				ReportGenerator.init();
			});	
})(jQuery);