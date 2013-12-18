
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
					$("#ruleItemContainer").empty().append("Loading....");	
					$("#ruleItemContainer").load("/searchManager/excelFileUploaded/paging/" + storeId + "/" + ruleType + "/" + pageNumber);	
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
				
				viewDetails : function(el, options){
					$(this).qtip({
						id: "plugin-uploadimage-qtip",
						content: {
							text: $('<div/>'),
							title: { text: 'Excel File Reports', button: true
							}
						},
						position:{
							at: 'right top',
							my: 'middle left'
						},
						show:{
							solo: true,
							ready: true
						},
						style: {
							width: 'auto'
						},
						events: { 
							show: function(event, api){
								var contentHolder = $("div", api.elements.content);
								var excelFileUploadedId=$("#excelFileUploadedId").val();
								var ruleType = $("#titleText").text().toLowerCase();
								contentHolder.empty().load("/searchManager/excelFileUploaded/details/"+ ruleType + "/" + excelFileUploadedId,function() {
					        	    $( "#tabs" ).tabs().scrollabletab();
					        	 });								
							},
							hide:function(evt, api){
								api.destroy();
							}
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
				        	        	$("#ruleItemContainer").empty().append("Please wait while process in progress.");
				        	        	var ruleType = $("#titleText").text().toLowerCase();
				        	        	ExcelFileUploadedServiceJS.addExcelFileUploadeds(ruleType,{
											callback: function(count){ 
												var ruleType = $("#titleText").text().toLowerCase();
												var storeId = GLOBAL_storeId;
												$("#ruleItemContainer").empty().append("Loading....");	
												$("#ruleItemContainer").load("/searchManager/excelFileUploaded/" + storeId + "/" + ruleType);
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
