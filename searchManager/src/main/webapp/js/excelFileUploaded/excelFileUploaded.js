var rowPerPage=10;
function setValue(excelFileUploadedId,storeId,fileName){
	$('#excelFileUploadedId').val(excelFileUploadedId);
	$('#storeId').val(storeId);
	$('#fileName').val(fileName);	
}
function changePage(pageNumber){
	var ruleType = $("#titleText").text().toLowerCase();
	$("#ruleItemContainer").empty().append("Loading....");	
	$("#ruleItemContainer").load("/searchManager/excelFileUploaded/paging/" + ruleType+"/"+pageNumber);	
}
function showPaging(){
	var currentPage = $('#currentPageNumber').val();
	var totalItem = $('#totalItem').val();	
	if (parseInt(currentPage)-1 >= parseInt(totalItem)/rowPerPage){
		currentPage=parseInt(currentPage)-1;
		changePage(currentPage);
	}
	$("#sortablePagingTop, #sortablePagingBottom").paginate({
		currentPage:currentPage, 
		pageSize:rowPerPage,
		totalItem:totalItem,
		callbackText: function(itemStart, itemEnd, itemTotal){
			var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
			return displayText;
		},
		pageLinkCallback: function(e){ 
			var pageNumber=parseInt(e.data.page);
			changePage(pageNumber);
		},
	nextLinkCallback: function(e){ 
		var pageNumber=parseInt(e.data.page) + 1;
		changePage(pageNumber);			
		},
	prevLinkCallback: function(e){
		var pageNumber=parseInt(e.data.page) - 1;
		changePage(pageNumber);
	}
	});
}
function loadPaging() {
	var currentPage = $('#currentPageNumber').val();
	changePage(currentPage);
	
}
(function($){

	$(document).ready(function(){
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
									showMainPage();
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
	        	loadPaging();
	        }
				        
	    });
		excelFileUploaded = {		
				deleteExcelFileUploaded : function(e){
					var fileName=$("#fileName").val();
					var msg = "Are you sure delete Excel file Upload [" + fileName + "] ?";
					jConfirm(msg, "Confirm", function(status){					
						if(status){
							var excelFileUploadedId=$("#excelFileUploadedId").val();
							var storeId=$("#storeId").val();							
							ExcelFileUploadedServiceJS.deleteExcelFileUploaded(excelFileUploadedId,storeId,fileName,{
								callback: function(count){ 
									loadPaging();
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
					showPaging();
				}				
		};
		excelFileUploaded.init();
	});
})(jQuery);	
