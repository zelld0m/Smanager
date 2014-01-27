
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
						ImportRuleTaskService.resetAttempts(GLOBAL_storeId, id, {
							callback:function(serviceResponse){
								if(serviceResponse.data == true) {
									jAlert('The task for the rule "'+ruleName+'" was successfully requeued.', moduleName);
									reloadFunction();
								} else if(serviceResponse.errorMessage != null) {
									jAlert(serviceResponse.errorMessage, moduleName);
								}
							}}
						);
					}
				});
			},
			cancel: function(id, moduleName, ruleName, reloadFunction) {
				jConfirm('Are you sure you want to cancel the task "'+ruleName+'"?', "Cancel Task", function(result) {
					if (result) {
						ImportRuleTaskService.cancelTask(GLOBAL_storeId, id, {
							callback:function(serviceResponse){
								if(serviceResponse.data == true) {
									jAlert('The task for the rule "'+ruleName+'" has been canceled.', moduleName);
									reloadFunction();
								} else if(serviceResponse.errorMessage != null) {
									jAlert(serviceResponse.errorMessage, moduleName);
								}
							}}
						);
					}
				});
			},
			preview: function(element, postTemplate, preTemplate, rightPanelTemplate) {
				var task = importRuleTaskList[element.id];
				ImportRuleTaskService.getRule(element.id, {
					callback:function(serviceResponse){
						if(serviceResponse.data != null) {
							$(element).xmlpreview({
								transferType: "import",
								ruleId: task.sourceRuleId,
								ruleName: task.sourceRuleName,
								ruleType: task.entityName.replace(' ', '').toLowerCase(),
								enablePreTemplate: true,
								enablePostTemplate: true,
								leftPanelSourceData: "xml",
								enableRightPanel: false,
								rightPanelSourceData: "database",
								dbRuleId: task.targetRuleId,
								ruleXml: serviceResponse.data,
								rule: serviceResponse.data,
								postTemplate: postTemplate,
								preTemplate: preTemplate,
								rightPanelTemplate: rightPanelTemplate,
								viewOnly: true,
								itemImportAsListCallback: function(base, contentHolder, sourceData) {
									DeploymentServiceJS.getDeployedRules(task.entityName, "published", {
										callback: function(data) {
											base.populateImportAsList(data, contentHolder, sourceData);
										}
									});
								},
								itemImportTypeListCallback: function(base, contentHolder) {
									base.populateImportTypeList(self.importTypeList, contentHolder);
								},
								itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap) {
									if (task.entityName.toLowerCase() === "elevate") {
										ElevateServiceJS.isRequireForceAdd(ruleName, memberIds, {
											callback: function(data) {
												base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
											},
											preHook: function() {
												base.prepareForceAddStatus(contentHolder);
											}
										});
									}
								},
								itemXmlForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberConditions, memberIdToItemMap) {
									if (task.entityName.toLowerCase() === "elevate") {
										ElevateServiceJS.isItemInNaturalResult(ruleName, memberIds, memberConditions, {
											callback: function(data) {
												base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
											},
											preHook: function() {
												base.prepareForceAddStatus(contentHolder);
											}
										});
									}
								},
								checkUncheckCheckboxCallback: function(base, ruleId, pub) {
									switch (pub) {
									case 'import':
										self.toggleImportCheckbox($.formatAsId(ruleId));
										break;
									case 'reject':
										self.toggleRejectCheckbox($.formatAsId(ruleId));
										break;
									}
								},
								changeImportTypeCallback: function(base, ruleId, opt) {
									$("#ruleItem" + $.formatAsId(ruleId) + " #type select").val(opt);
								},
								changeImportAsCallback: function(base, ruleId, importAs, ruleName, newName) {
									if (importAs != 0 || newName.length > 0) {
										$("#ruleItem" + $.formatAsId(ruleId) + " #importAs select").val(importAs).change();
										$("#ruleItem" + $.formatAsId(ruleId) + " #importAs #replacement #newName").val(newName);
									}
								},
								itemImportTypeListCallback: function(base, contentHolder) {
									base.populateImportTypeList(self.importTypeList, contentHolder);
								},
								itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap) {
									if (task.entityName.toLowerCase() === "elevate") {
										ElevateServiceJS.isRequireForceAdd(ruleName, memberIds, {
											callback: function(data) {
												base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
											},
											preHook: function() {
												base.prepareForceAddStatus(contentHolder);
											}
										});
									}
								},
								itemXmlForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberConditions, memberIdToItemMap) {
									if (task.entityName.toLowerCase() === "elevate") {
										ElevateServiceJS.isItemInNaturalResult(ruleName, memberIds, memberConditions, {
											callback: function(data) {
												base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
											},
											preHook: function() {
												base.prepareForceAddStatus(contentHolder);
											}
										});
									}
								}
							});
							
							$(element).attr('src',$(element).attr('src').replace('ajax-loader-rect.gif', 'icon_reviewContent.png'));
						} else if(serviceResponse.errorMessage != null) {
							jAlert(serviceResponse.errorMessage, moduleName);
						}
					}}
				);
			},
			getPreTemplate: function(entityName, selectedType) {
				var template = '';

				switch (entityName.toLowerCase()) {
				case "facetsort":
					template = '<div class="rulePreview w590 marB20">';
					template += '	<div class="alert marB10">The rule below is pending for import. Please examine carefully the details</div>';
					template += '	<label class="w110 floatL fbold">Rule Name:</label>';
					template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
					template += '	<div class="clearB"></div>';
					template += '	<label class="w110 floatL fbold">Rule Type:</label>';
					template += '	<label class="wAuto floatL" id="ruleType"></label>';
					template += '	<div class="clearB"></div>';
					template += '	<div class="clearB"></div>';
					template += '</div>';
					break;
				default:	//template for elevate, exclude, demote, redirect and relevancy rule
					template = '<div class="rulePreview w590 marB20">';
				template += '	<div class="alert marB10">The rule below is pending for import. Please examine carefully the details</div>';
				template += '	<label class="w110 floatL fbold">Rule Name:</label>';
				template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '	<div class="clearB"></div>';
				template += '	<div class="clearB"></div>';
				template += '</div>';
				}
				return template;
			},
			getPostTemplate: function() {
				var template = "";

				template = '<div>';
			
				template += '</div>';

				return template;
			},
			getRightPanelTemplate: function() {
				var template = "";

				template += '	<div class="rulePreview w590 marB20">';
				template += '		<div class="alert marB10">';
				template += '			Selected rule below will be overwritten when import task is executed.';
				template += '		</div>';
				template += '		<label class="w110 floatL marL20 fbold">Rule Name:</label>';
				template += '		<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '		<div class="clearB"></div>';
				template += '	</div>';

				return template;
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
				$('input.btnRequeue').on({
					click: function(e){
						var ruleName = $(this).parents('.conTableItem ').children('.ruleName').html();
						self.requeue(this.id, self.moduleName, ruleName, function(){self.filterPage();});
					}
				});
				$('input.btnCancel').on({
					click: function(e){
						var ruleName = $(this).parents('.conTableItem ').children('.ruleName').html();
						self.cancel(this.id, self.moduleName, ruleName, function(){self.filterPage();});
					}
				});
				$('input.btnPreview').each(function() {

					var entityName = importRuleTaskList[this.id].entityName;
					var importType = importRuleTaskList[this.id].importType;
					var postTemplateString = self.getPostTemplate();
					var preTemplateString = self.getPreTemplate(importType, entityName);
					var rightPanelStringTemplate = self.getRightPanelTemplate();
					self.preview(this, postTemplateString, preTemplateString, rightPanelStringTemplate);
				});
				$('input.btnPreviewOff, input.btnRequeueOff, input.btnCancelOff').each(function() {
					var id = this.id;
					var buttonClassName = $(this).attr('class');
					$(this).qtip({
						content: { 
							text: $('<div>')
						},
						show:{ modal:true },
						style:{
							width:'250px'
						},
						events: {
							render:function(rEvt, api){
								var $content = $("div", api.elements.content);
								$content.html("");
							},
							show:function(rEvt, api){
								var $content = $("div", api.elements.content);
								var text = "";
								var task = importRuleTaskList[id];
								if(buttonClassName == 'btnPreviewOff') {
									text = "Data for rule task '"+task.sourceRuleName+"' is not available.";
								} else if(buttonClassName == 'btnRequeueOff') {
									if(task.hasXml != true)
										text = "Data for rule task '"+task.sourceRuleName+"' is not available.";
									else
										text = "The current status of rule task '"+task.sourceRuleName+"' does not allow requeueing.";
								} else if(buttonClassName == 'btnCancelOff') {
									text = "The current status of rule task '"+task.sourceRuleName+"' does not allow cancellation.";
								}
								
								if(!$content.get(0))						
									$content = api.elements.content;
								$content.html(text);

							}
						}
					});
				});
			}
	};

	$(document).ready(function(){	
		ImportRuleTaskPage.init();
	});
})(jQuery);	
