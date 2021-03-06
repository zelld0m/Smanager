(function($){

	$.excelupload = function(el, options) {
		var base = this;

		var destroy = true;

		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("excelupload", base);

		base.options = $.extend({},$.excelupload.defaultOptions, options);

		base.init = function() {
			var self = this;
			self.$preloader = $("#preloader");
			$("#uploadFromExcel").on({
				click: function() {self.showMainPage();}
			});
			
		};
		
		base.validateFileName = function(fileName) {
			return fileName.indexOf('xlsx', this.length - 'xlsx'.length) !== -1;
		};

		base.initializeAjaxForm = function () {
			var self = this;
			self.$el.find('#excelFileUpload').ajaxForm({
				beforeSubmit: function() {
					self.$el.find("#uploadButtonContainer").hide();
					
					if(!$.isNotBlank(self.$el.find("#files").val())) {
						jAlert("Please select a file.", base.options.moduleName != null ? base.options.moduleName : "");
						self.$el.find("#uploadButtonContainer").show();
						return false;
					}
					
					if(!self.validateFileName(self.$el.find("#files").val())) {
						jAlert("Invalid file type.", base.options.moduleName != null ? base.options.moduleName : "");
						self.$el.find("#uploadButtonContainer").show();
						return false;
					}
					
				},
				success: function(data) {
					if(data.status < 0){
						jAlert(data.errorMessage.message, base.options.moduleName != null ? base.options.moduleName : "");
						self.$el.find("#uploadButtonContainer").show();
						self.$el.find("#files").val('');
					}else{
						var $dialogPopUp = $('<div></div>');
						$dialogPopUp.dialog({
							autoOpen: false,
							position: 'center' ,
							title: 'Preview',
							modal: true,
							width: "600px",
							create: function() {
						        $(this).css("maxHeight", 550);        
						        $(this).css("maxWidth", 850);
						    },
							buttons: {
								"Process File": function() {
									var excel = $( this ).data('excel');
									var $dialog = $(this);
									self.processExcelFile({id : excel.excelId, fileName : excel.fileName},
											function() {
										$dialog.dialog( "close" );
										jAlert('This excel file has now been queued', base.options.moduleName);
										self.showMainPage();
									});
								},
								"Process Later": function() {$( this ).dialog( "close" ); self.showMainPage();}
							}
						});
						$dialogPopUp.data('excel', data.data[0]);
						$dialogPopUp.empty().append(Mustache.to_html(base.options.mustachePreviewTemplate, data.data[0]));
												
						$dialogPopUp.dialog("open");
						self.$el.find("#uploadButtonContainer").show();
						self.showMainPage();
					}
				},
				dataType: 'json'

			});		
		};

		base.addToRule = function() {
			if(base.options.addToRule) { base.options.addToRule(); return; }

			alert("Please override the addToRule function");
		};

		base.initializeViewDetails = function($links){
			$links.each(function() {
				var $link = $(this);
				var fileId = $link.siblings("input.excelId").val();
				var text = $link.html();
				$link.off().on({
					click: function() {
						var url = base.options.baseViewExcelUrl + fileId + '?' + new Date().getTime();
						$link.html(base.options.rectLoader);
						$.ajax(url, {
							dataType : 'json',
							type : 'POST',
							cache : false,
							beforeSend : function() {},
							success: function(data) {
								//$( "#dialog-modal-details" ).dialog('destroy');
								var $dialog = $('<div></div>');
								$dialog.html(Mustache.to_html(base.options.mustachePreviewTemplate, data.data[0]));
								$dialog.dialog({
									autoOpen: false,
									position: 'center' ,
									title: 'Details',	 
									width: '600',
									create: function() {
										$(this).css("maxHeight", 550);        
										$(this).css("maxWidth", 850);
									},
									modal: true});								
								$dialog.dialog("open");
								$dialog.bind('dialogclose', function(event) {$dialog.dialog('destroy');});
								$link.html(text);
							}
						});
					}
				});
			});
		};
		
		base.initializeDeleteEvent = function($deleteLinks) {
			var self = this;
			$deleteLinks.each(function() {
				var $deleteLink = $(this);
				$deleteLink.off().on({
					click: function() {
						self.deleteExcelFile($deleteLink);
					}
				});
			});
		};
		
		base.initializeQueueEvent = function($queueLinks) {
			var self = this;
			$queueLinks.each(function() {
				var $queueLink = $(this);
				$queueLink.off().on({
					click : function() {
						var $tr = $queueLink.closest('tr');
						var fileName = $tr.find('a.detail').text();
						jConfirm("Are you sure you want to process the Excel file [" + fileName + "] ?", base.options.moduleName, function(status){
							if(status) {
								var id = $tr.find('a.detail').siblings("input.excelId").val();

								self.processExcelFile({id : id, fileName : fileName},
										function() {
									$( "#dialog-modal" ).dialog( "close" );
									jAlert('This excel file has now been queued', base.options.moduleName);
									self.showMainPage();
								});
							}
						});
					}
				});
			});
		};
		
		base.initializeActions = function() {
			var self = this;
			self.$preloader.hide();
			self.$el.show();
			self.showPaging();
			self.initializeAjaxForm();
			self.initializeViewDetails(self.$el.find('a.detail'));
			self.initializeDeleteEvent(self.$el.find('a.delete'));
			self.initializeQueueEvent(self.$el.find('a.queue'));
			self.getExcelStatus();
		};

		base.changePage = function (pageNumber){
			var self = this;
			var ruleType = base.options.ruleType;
			var storeId = GLOBAL_storeId;
			self.$el.hide();
			self.$preloader.show();
			self.$el.empty().load("/searchManager/excelFileUploaded/paging/" + storeId + "/" + ruleType + "/" + pageNumber + "/" + (Math.random()*99999),function(){
				self.initializeActions();
			});	
		};

		base.showMainPage = function() {
			var self = this;
			base.options.beforeLoad();
			self.$preloader.show();
			self.$el.hide();
			self.$el.load(base.options.homeUrl + (new Date()).getTime(),function(){
				self.initializeActions();
			});
			
			self.$el.find('#excelFileUpload').submit(function() {
				$(this).ajaxSubmit();
				return false;
			});
			
			$(".plugin-rulestatusbar").hide();
			$("#ruleSelected").hide();
			$("#ruleItemPagingTop").hide();
			$("#ruleItemPagingBottom").hide();
			$("#ruleItemDisplayOptions").hide();
		};
		base.showPaging = function (){
			var self = this;
			var currentPage = $('#currentPageNumber').val();
			var totalItem = $('#totalItem').val();	
			if (parseInt(currentPage)-1 >= parseInt(totalItem)/this.rowPerPage){
				currentPage=parseInt(currentPage)-1;	
				self.changePage(currentPage);
			}	
			$("#sortablePagingTop, #sortablePagingBottom").paginate({
				currentPage:currentPage == undefined || currentPage == null ? 1 : currentPage, 
						pageSize:base.options.rulePageSize,
						totalItem:totalItem,
						callbackText: function(itemStart, itemEnd, itemTotal){
							var displayText = 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal;
							return displayText;
						},
						pageLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page);
							self.changePage(pageNumber);
						},
						nextLinkCallback: function(e){ 
							var pageNumber=parseInt(e.data.page) + 1;
							self.changePage(pageNumber);			
						},
						prevLinkCallback: function(e){
							var pageNumber=parseInt(e.data.page) - 1;
							self.changePage(pageNumber);
						}
			});
		};

		base.loadPaging = function(){
			var self = this;
			var currentPage = $('#currentPageNumber').val();
			self.changePage(currentPage);

		};

		base.deleteExcelFile = function($deleteLink){
			var self = this;
			var fileName = $deleteLink.closest('tr').find('a.detail').text();
			var id = $deleteLink.closest('tr').find('a.detail').siblings("input.excelId").val();
			var ruleType = base.options.ruleType;
			jConfirm("Are you sure you want to delete the Excel file [" + fileName + "] ?", base.options.moduleName, function(status){					
				if(status){
					$("#dialog-modal-details").dialog("close");
												
					ExcelFileUploadedServiceJS.deleteExcelFile(id,GLOBAL_storeId, ruleType, fileName,{
						callback: function(count){ 
							self.showMainPage();
						}
					});												
				}
			});

		};
		
		base.getExcelStatus = function() {
			var self = this;
			
			self.$el.find('input.excelId').each(function() {
				var $input = $(this);
				var $row = $input.closest('tr');
				var $deleteLink = $row.find('a.delete'); 
				var $queueLink = $row.find('a.queue');
				
				$deleteLink.hide();
				$queueLink.hide();
				$deleteLink.before(base.options.rectLoader);
				var url = base.options.baseViewExcelUrl + $input.val();
				$.ajax(url, {
					dataType : 'json',
					type : 'POST',
					cache : false,
					beforeSend : function() {},
					success: function(data) {
						var status = data.data[0].status; 
						$row.find('td.statusColumn').html(status);
						$deleteLink.prev().remove();
						if(status === 'QUEUED' || status === 'IN_PROCESS') {
							$deleteLink.before(base.options.lockIcon);
							return;
						}
												
						$deleteLink.show();
						$queueLink.show();
					}
				});
				
			});
		};
		
		base.processExcelFile = function(excelFile, afterCallback) {
			ExcelFileUploadedServiceJS.processExcelFiles(excelFile.id, GLOBAL_storeId, base.options.ruleType, excelFile.fileName,{
				callback: function(success){ 
					if(!success) {
						jAlert("The File processing failed. Please contact your system aministrator.", base.options.moduleName);
						return;
					}
					self.showMainPage();
				},
				postHook : function() {
					afterCallback();
				}
			});		
		};

		base.init();
	};


	$.excelupload.defaultOptions = {
			ruleType: "typeahead",
			rulePage: 1,
			rulePageSize: 10,
			rectLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-rect.gif'/>",
			roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
			homeUrl: "/searchManager/excelFileUploaded/" + GLOBAL_storeId + "/typeahead/",
			lockIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_lock.png'/>",
			baseViewExcelUrl: "",
			mustachePreviewTemplate: "",
			beforeLoad: function(){},
	};

	$.fn.excelupload = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.excelupload(this, options));
			});
		};
	};

})(jQuery);	