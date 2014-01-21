(function($){
	var ImportRuleTaskPage = {
			rowPerPage:10,
			moduleName:"Auto Import",
			changePage: function(pageNumber){
				var self = this;
				var filter = encodeURI($("#filter").val());
				$("#mainContainer").empty().append("<br/><br/><center><img src='/searchManager/images/ajax-loader-circ.gif'></center>");	
				$("#mainContainer").load("/searchManager/autoimport/" + GLOBAL_storeId + "/page/" + pageNumber + "/" + filter,function(){
					self.init(pageNumber);
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
							contentHolder.empty().append($('#reason').val());
						},
						hide:function(evt, api){
							api.destroy();
						}
					}
				});
			},				
			filterPage : function(){
				var self = this;
				var filter = $("#statusFilter").val() + ",";
				filter = filter + $("#typeFilter").val()  + ",";
				filter = filter + $("#ruleTypeFilter").val()  + ",";
				filter = filter + $("#targetRuleName").val() + ",";
				filter = filter + $("#targetFilter").val();
				$("#filter").val(filter);
				var currentPage = $('#currentPageNumber').val();
				self.changePage(currentPage);
			},
			showPaging : function showPaging(pageNumber){
				var self = this;
				var totalItem = $('#totalItem').val();	

				$("#sortablePagingTop, #sortablePagingBottom").paginate({
					type: "short",
					pageStyle: "style2",
					currentPage: pageNumber, 
					pageSize:self.rowPerPage,
					totalItem:totalItem,
					callbackText: function(itemStart, itemEnd, itemTotal){
						var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
						return displayText;
					},
					pageLinkCallback: function(e){ 
						self.changePage(parseInt(e.data.page));
					},
					nextLinkCallback: function(e){ 
						self.changePage(parseInt(e.data.page) + 1);			
					},
					prevLinkCallback: function(e){
						self.changePage(parseInt(e.data.page) - 1);
					},
					firstLinkCallback: function(e){ 
						self.changePage(1);
					},
					lastLinkCallback: function(e){ 
						self.changePage(e.data.totalPages);
					}
				});
			},
			loadPaging : function() {
				var self = this;
				var currentPage = $('#currentPageNumber').val();
				self.changePage(currentPage);
			},
			requeue: function(id, moduleName, ruleName, reloadFunction) {
				jConfirm('Are you sure you want to requeue the task "'+ruleName+'"?', "Requeue Task", function(result) {
					if (result) {
						ImportRuleTaskService.resetAttempts(GLOBAL_storeId, id, 
							{
								callback:function(serviceResponse){
									if(serviceResponse.data == true) {
										jAlert('The task for the rule "'+ruleName+'" was successfully requeued.', moduleName);
										reloadFunction();
									}
								}
							}
						);
					}
				});
			},
			cancel: function(id, moduleName, ruleName, reloadFunction) {
				jConfirm('Are you sure you want to cancel the task "'+ruleName+'"?', "Cancel Task", function(result) {
					if (result) {
						ImportRuleTaskService.cancelTask(GLOBAL_storeId, id, 
							{
								callback:function(serviceResponse){
									if(serviceResponse.data == true) {
										jAlert('The task for the rule "'+ruleName+'" has been canceled.', moduleName);
										reloadFunction();
									}
								}
							}
						);
					}
				});
			},
			init : function(pageNumber){		
				var self = this;
				$(".failedReason").on({
					mouseover: self.viewDetails
				});
				self.showPaging(pageNumber);
				$("#titleText").html(self.moduleName);
				
				$("#filterBtn").off().on({
					click: function(e){
						e.preventDefault();
						self.filterPage();
					}
				});
				
				$("#resetBtn").off().on({
					click: function(e){
						location.reload();
					}
				});
				$('.btnRequeue').on({
					click: function(e){
						var ruleName = $(this).parents('.conTableItem ').children('.ruleName').html();
						self.requeue(this.id, self.moduleName, ruleName, function(){self.filterPage();});
					}
				});
				$('.btnCancel').on({
					click: function(e){
						var ruleName = $(this).parents('.conTableItem ').children('.ruleName').html();
						self.cancel(this.id, self.moduleName, ruleName, function(){self.filterPage();});
					}
				});
			}
	};

	$(document).ready(function(){	
		ImportRuleTaskPage.init();
	});
})(jQuery);	
