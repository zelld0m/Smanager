(function($){

	$(document).ready(function(){	
		importRuleTask = {
				rowPerPage:10,
				moduleName:"Auto Import",
				changePage: function(pageNumber){
					var filter = encodeURI($("#filter").val());
					$("#mainContainer").empty().append("<br/><br/><center><img src='/searchManager/images/ajax-loader-circ.gif'></center>");	
					$("#mainContainer").load("/searchManager/autoimport/" + GLOBAL_storeId + "/page/" + pageNumber + "/" + filter,function(){
						importRuleTask.init();
						});				
				},
				viewDetails : function(el, options){
					$(this).qtip({
						id: "excelFileReportPage",
						content: {
							text: $('<div/>'),
							title: { text: 'Failed reason', button: true
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
							width: 300
						},
						events: { 
							show: function(event, api){
								var contentHolder = $("div", api.elements.content);
								contentHolder.empty().append( $('#reason').val());
							},
							hide:function(evt, api){
								api.destroy();
							}
						}
					});
				},				
				filterPage : function(){
					var filter = $("#statusFilter").val() + ",";
					filter = filter + $("#typeFilter").val()  + ",";
					filter = filter + $("#targetRuleName").val() + ",";
					filter = filter + $("#targetFilter").val();
					$("#filter").val(filter);
					var currentPage = $('#currentPageNumber').val();
					importRuleTask.changePage(currentPage);
				},
				showPaging : function showPaging(){
					var self = this;
					var currentPage = $('#currentPageNumber').val();
					var totalItem = $('#totalItem').val();	
					if (parseInt(totalItem)>0 && parseInt(currentPage)-1 >= parseInt(totalItem)/self.rowPerPage){
						currentPage=parseInt(currentPage)-1;
						importRuleTask.changePage(currentPage);
					}
					$("#sortablePagingTop, #sortablePagingBottom").paginate({
						currentPage:currentPage, 
						pageSize:self.rowPerPage,
						totalItem:totalItem,
						callbackText: function(itemStart, itemEnd, itemTotal){
							var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
							return displayText;
						},
						pageLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page);
							importRuleTask.changePage(pageNumber);
						},
						nextLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page) + 1;
							importRuleTask.changePage(pageNumber);			
						},
						prevLinkCallback: function(e){
							var pageNumber=parseInt(e.data.page) - 1;
							importRuleTask.changePage(pageNumber);
						}
					});
				},
				loadPaging : function() {
					var currentPage = $('#currentPageNumber').val();
					importRuleTask.changePage(currentPage);
				},				
				init : function(){		
					$(".failedReason").on({
						mouseover: importRuleTask.viewDetails
					});
					importRuleTask.showPaging();
					$("#titleText").html(this.moduleName);
				}				
		};
		importRuleTask.init();
	});
})(jQuery);	
