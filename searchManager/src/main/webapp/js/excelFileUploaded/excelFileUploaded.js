
(function($){

	$(document).ready(function(){
		excelFileUploaded = {		
				rowPerPage : 10,
				setValue : function(excelFileUploadedId,storeId,fileName){
					$('#excelFileUploadedId').val(excelFileUploadedId);
					$('#storeId').val(storeId);
					$('#fileName').val(fileName);	
				},
				changePage : function (pageNumber){
					var ruleType = $("#titleText").text().toLowerCase();
					var storeId = GLOBAL_storeId;
					$("#noSelected").hide();
					$("#preloader").show();
					$("#noSelected").empty().load("/searchManager/excelFileUploaded/paging/" + storeId + "/" + ruleType + "/" + pageNumber + "/" + (Math.random()*99999),function(){
						$("#preloader").hide();
						$("#noSelected").show();
					});	
				},
				showPaging : function (){
					var currentPage = $('#currentPageNumber').val();
					var totalItem = $('#totalItem').val();	
					if (parseInt(currentPage)-1 >= parseInt(totalItem)/this.rowPerPage){
						currentPage=parseInt(currentPage)-1;	
						excelFileUploaded.changePage(currentPage);
					}	
					$("#sortablePagingTop, #sortablePagingBottom").paginate({
						currentPage:currentPage, 
						pageSize:this.rowPerPage,
						totalItem:totalItem,
						callbackText: function(itemStart, itemEnd, itemTotal){
							var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
							return displayText;
						},
						pageLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page);
							excelFileUploaded.changePage(pageNumber);
						},
						nextLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page) + 1;
							excelFileUploaded.changePage(pageNumber);			
						},
						prevLinkCallback: function(e){
							var pageNumber=parseInt(e.data.page) - 1;
							excelFileUploaded.changePage(pageNumber);
					}
					});
				},
				loadPaging : function(){
					var currentPage = $('#currentPageNumber').val();
					excelFileUploaded.changePage(currentPage);
					
				},				
				deleteExcelFileUploaded : function(e){
					var fileName=$("#fileName").val();
					var msg = "Are you sure delete Excel file Upload [" + fileName + "] ?";
					jConfirm(msg, "Confirm", function(status){					
						if(status){
							$("#dialog-modal").dialog("close");
							var excelFileUploadedId=$("#excelFileUploadedId").val();
							var storeId=$("#storeId").val();							
							ExcelFileUploadedServiceJS.deleteExcelFileUploaded(excelFileUploadedId,storeId,fileName,{
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
				
				viewDetails : function(){
								$( "#dialog-modal-details" ).dialog({
							    autoOpen: false,
							    position: 'center' ,
							    title: 'Details',	        		
								height: 500,
								width: 430,
							    modal: true});								
							var excelFileUploadedId=$("#excelFileUploadedId").val();
							var ruleType = $("#titleText").text().toLowerCase(); 							
							$("#dialog-modal-details").empty().load("/searchManager/excelFileUploaded/details/"+ ruleType + "/" + excelFileUploadedId + "/" + (Math.random()*99999),function() {			        	    
				        	    $("#dialog-modal-details").dialog("open");				        	    
				        	 });							
							
				},
				addToRule : function(e){
					var fileName=$("#fileName").val();
					var msg = "Are you sure you want to add to rule all entry in [" + fileName + "] excel file Upload?";
					jConfirm(msg, "Confirm", function(status){					
						if(status){				
							  	$("#dialog-modal").dialog("close");
								var excelFileUploadedId=$("#excelFileUploadedId").val();								
								var ruleType = $("#titleText").text().toLowerCase();
								var storeId = GLOBAL_storeId;
								var isClear = false;
								$("#ui-tooltip-excelFileReportPage").hide();
								if ($("#clearRuleFirst").length > 0){
									isClear = $("#clearRuleFirst").is(":checked");
								}								
								$("#noSelected").hide();
								$("#preloader").show();
								ExcelFileUploadedServiceJS.updateExcelFileUploaded(excelFileUploadedId,storeId,ruleType,isClear,{
									callback: function(message){
										jAlert(message, "Add to Rules Result", function(){
											excelFileUploaded.loadPaging();
										});									
									},
									preHook: function(){ 
									},
									postHook: function(){ 	
									}
								});						
							}
					});
				},				
				init : function(){
					$(".viewDetails").on({
						click: excelFileUploaded.viewDetails
					});
					$(".delete").on({
						click:	excelFileUploaded.deleteExcelFileUploaded
					});	
					$(".addToRule").on({
						click:	excelFileUploaded.addToRule
					});						
					$("#uploadButtonContainer").hide();
					$('input[type=file]').change(function(e){
						$("#uploadButtonContainer").show();
					});			
					excelFileUploaded.showPaging();
					$('#excelFileUpload').ajaxForm({
				        beforeSubmit: function() {
				        	$("#uploadButtonContainer").hide();	        	
				        },
				        success: function(data) {	        	
				        	$( "#dialog-modal" ).dialog({
				        	    autoOpen: false,
				        	    position: 'center' ,
				        	    title: 'Preview',	        		
				        		height: 550,
				        		width: 850,
				        	    modal: true,
				        	    buttons: {
				        	        "Proceed": function() {
				    					$("#preloader").show();
				    					$("#noSelected").hide();
				        	        	var ruleType = $("#titleText").text().toLowerCase();
				        	        	ExcelFileUploadedServiceJS.addExcelFileUploadeds(ruleType,{
											callback: function(count){ 
												excelFileUploaded.loadPaging();
											},
											preHook: function(){ 
											},
											postHook: function(){ 	
											}
										});	
				        	        	$(this).dialog( "close" );
				        	        },
				        	        Cancel: function() {
				        	          $( this ).dialog( "close" );
				        	        }
				        	      }
				        	 });
				        	$("#dialog-modal").empty().append(data);
				        	$("#dialog-modal").dialog("open");
				        	$(function() {
				        	    $( "#tabs" ).tabs().scrollabletab();
				        	 });	     
				        	excelFileUploaded.loadPaging();
				        }
							        
				    });					
				}				
		};
		excelFileUploaded.init();
	});
})(jQuery);	
