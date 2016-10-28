(function($){

	$.rulestatusbar = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("rulestatusbar", base);

		base.options = $.extend({},$.rulestatusbar.defaultOptions, options);
		
		// Run initializer
		base.init();
	};
	
	$.rulestatusbar.prototype.cancelRuleTask = function(){
		var base = this;
		
		$('div.autoImportWarning').each(function() {
			var container = this;
			var link = $(this).find('a.cancel');
			link.off().on({
				click:function(e){
					var id = container.id;
					jConfirm('Are you sure you want to cancel the task for this rule?', "Cancel Task", function(result) {
						if (result) {
							ImportRuleTaskService.cancelTask(GLOBAL_storeId, id, {
								callback:function(serviceResponse){
									if(serviceResponse.data == true) {
										
										jAlert('The task for this rule has been canceled.', base.options.moduleName);
									}  else if(serviceResponse.errorMessage != null) {
										jAlert(serviceResponse.errorMessage, moduleName);
									}
								},
								postHook: function() {
									$(container).hide();
								}
							});
						}
					});
				}
			});
		});
	};
	
	$.rulestatusbar.prototype.getImportTaskPreview = function(){
		var base = this;
		
		$('div.autoImportWarning').each(function() {
			var div = this;
			var compareLink = $(div).find('a.compare'); 
			compareLink.hide();
			ImportRuleTaskService.getRule(this.id, {
				callback:function(serviceResponse){
					var ruleXml = serviceResponse.data;
					if(ruleXml != null) {
						base.bindXmlPreview(compareLink[0], ruleXml, base.getXmlPostTemplate(), base.getXmlPreTemplate(ruleXml.ruleEntity.replace('_', '').toLowerCase()), base.getXmlRightPanelTemplate());
					} else if(serviceResponse.errorMessage != null) {
						jAlert(serviceResponse.errorMessage, moduleName);
					}
				},
				postHook:function(){
					compareLink.show();
					$(div).find('span.loading').hide();
				},
			});
		});
	};
	
	$.rulestatusbar.prototype.bindXmlPreview = function(element, ruleXml, postTemplate, preTemplate, rightPanelTemplate) {
		var ruleEntity = ruleXml.ruleEntity.replace('_', '').toLowerCase();
		
		$(element).xmlpreview({
			transferType: "import",
			ruleId: ruleXml.ruleId,
			ruleName: ruleXml.ruleName,
			ruleType: ruleEntity,
			enablePreTemplate: true,
			enablePostTemplate: true,
			leftPanelSourceData: "xml",
			enableRightPanel: true,
			rightPanelSourceData: "database",
			dbRuleId: ruleXml.ruleId,
			ruleXml: ruleXml,
			rule: ruleXml,
			postTemplate: postTemplate,
			preTemplate: preTemplate,
			rightPanelTemplate: rightPanelTemplate,
			viewOnly: true,
			itemImportAsListCallback: function(base, contentHolder, sourceData) {
				DeploymentServiceJS.getDeployedRules(ruleEntity, "published", {
					callback: function(data) {
						base.populateImportAsList(data, contentHolder, sourceData);
					}
				});
			},
			itemImportTypeListCallback: function(base, contentHolder) {
				base.populateImportTypeList(self.importTypeList, contentHolder);
			},
			itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap) {
				if (ruleEntity === "elevate") {
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
				if (ruleEntity === "elevate") {
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
				if (ruleEntity === "elevate") {
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
				if (ruleEntity === "elevate") {
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
	};
	
	$.rulestatusbar.prototype.getXmlPreTemplate = function(entityName) {
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
	};
	
	$.rulestatusbar.prototype.getXmlPostTemplate = function() {
		var template = "";

		template = '<div>';
	
		template += '</div>';

		return template;
	};
	
	$.rulestatusbar.prototype.getXmlRightPanelTemplate = function() {
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
	};
	
	$.rulestatusbar.prototype.getImportTask = function(){
		var base = this;
		var autoImportWarnContainer = base.$el.find("#autoImportWarningContainer");
		autoImportWarnContainer.find(".autoImportWarning:not(#autoImportWarningPattern)").remove();
		
		ImportRuleTaskService.getTasks(GLOBAL_storeId, base.options.moduleName, base.options.rule["ruleId"], {
			callback:function(serviceResponse){
				var list = serviceResponse["data"];

				if(list){
					for(var i=0; i< list.length; i++){
						var task = list[i];
						var autoImportWarnItem = autoImportWarnContainer.find("#autoImportWarningPattern").clone();
						var storeId = task["sourceStoreId"];
						var createdDate = $.toStoreFormat(task["createdDate"]);
						var warningText = base.options.autoImportWarningText.replace(/storeName/g,GLOBAL_allStoresDisplayName[storeId]).replace(/createdDate/g,createdDate);
						autoImportWarnItem.prop({id: task["taskId"]});
						autoImportWarnItem.find(".autoImportWarningText").text(warningText);
						autoImportWarnItem.show();
						autoImportWarnContainer.append(autoImportWarnItem);
					}
					autoImportWarnContainer.show();
				}
			},
			preHook:function(){
				autoImportWarnContainer.hide();
			},
			postHook:function(){
				base.getRuleStatus();
				base.getImportTaskPreview();
				base.cancelRuleTask();
			},
			
		});
	};
	
	$.rulestatusbar.prototype.getRuleStatus = function(){
		var base = this;
		DeploymentServiceJS.getRuleStatus(GLOBAL_storeId, base.options.moduleName, base.options.rule["ruleId"], {
			postHook: function(){
				base.$el.find("#ruleStatusContainer").show();
			},
			
			preHook: function(){
				base.options.beforeRuleStatusRequest();
			},

			callback:function(ruleStatus){
				base.options.ruleStatus = ruleStatus;
			
				if(ruleStatus){
					if($.isNotBlank(ruleStatus["approvalStatus"])){
						base.$el.find("#status").text(getRuleNameSubTextStatus(ruleStatus));
					}
					
					var fomattedPublishedDate = $.toStoreFormat(ruleStatus["lastPublishedDate"]);
					
					if (ruleStatus["ruleSource"]==="AUTO_IMPORT")
						base.$el.find("#autoImport").show();
						
					if($.isNotBlank(fomattedPublishedDate)){
						base.$el.find("#publishedDate").text(fomattedPublishedDate);
					}
					
					if(ruleStatus["locked"]){
						base.$el.find("#statusMode").text("[ Read-Only ]");
						base.$el.find("#submitForApprovalBtn").parent().remove();
					}

					base.$el.find("#commentBtn").off().on({
						click:function(e){
							$(this).comment({
								title: "Rule Comment",
								itemDataCallback: function(plugin, page){
									if(base.options.ruleStatus && $.isNotBlank(base.options.ruleStatus["ruleStatusId"])){
										CommentServiceJS.getComment("Rule Status", base.options.ruleStatus["ruleStatusId"], plugin.options.page, plugin.options.pageSize, {
											callback: function(data){
												var total = data.totalSize;
												plugin.populateList(data);
												plugin.addPaging(plugin.options.page, total);
											},
											preHook: function(){
												plugin.prepareList();
											}
										});
									}
									plugin.populateList(null);
								}
							});
						}
					});
				}

				base.addSubmitForApprovalListener();
				base.options.afterRuleStatusRequest(ruleStatus);
				base.addCopyToStoreListener();

				if ($.endsWith(base.options.rule["ruleId"], "_default")){
					base.$el.empty();
				}

				if (base.options.enableVersion){
					//base.$el.find("#versionIcon, #backupBtn")
					base.$el.find("#backupBtn").off().on({
						click: function(e){
							$(e.currentTarget).version({
								moduleName: base.options.moduleName,
								ruleType: base.options.ruleType,  
								rule: base.options.rule,
								locked: e.data.selectedRuleStatus.locked || $.endsWith(e.data.rule.ruleId, "_default") || !e.data.allowModify,
								deletePhysically: base.options.deleteVersionsPhysically,
								enableCompare: base.options.enableCompare,
								enableSingleVersionDownload: base.options.enableSingleVersionDownload,
								preRestoreCallback: function(el){
									base.options.preRestoreCallback(el);
								},
								postRestoreCallback: function(el, rule){
									base.options.postRestoreCallback(el, rule);
								}
							});
						}
					}, {selectedRuleStatus: ruleStatus, rule : base.options.rule, allowModify: allowModify});

					base.$el.find("#downloadVersionIcon").download({
						headerText:"Download " + base.options.moduleName + " Rule Versions",
						moduleName: base.options.moduleName,
						ruleType: base.options.ruleType,  
						rule: base.options.rule,
//						locked: selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify,
						requestCallback:function(e){
							RuleVersionServiceJS.getRuleVersionsCount(base.options.ruleType, base.options.rule["ruleId"], {
								callback: function(data){
									if(data > 0){
										var params = new Array();
										var url = document.location.pathname + "/version/xls";
										var urlParams = "";
										var count = 0;

										params["filename"] = e.data.filename;
										params["type"] = e.data.type;
										params["keyword"] = base.options.rule["ruleName"];
										params["id"] = base.options.rule["ruleId"];
										//params["filter"] = base.getRuleItemFilter();
										params["clientTimezone"] = +new Date();

										for(var key in params){
											if (count>0) urlParams +='&';
											urlParams += (key + '=' + encodeURIComponent(params[key]));
											count++;
										};

										document.location.href = url + '?' + urlParams;
									}
									else{
										jAlert("No available version to download for this rule.", "Download Rule Versions");
									}
								}
							});

						}
					});
				}
			}
		});	
	};
	
	$.rulestatusbar.prototype.init = function(){
		var base = this;
		base.$el.empty();
		base.$el.html(base.getTemplate());
		base.getImportTask();
	};

	$.rulestatusbar.prototype.addSubmitForApprovalListener = function(){
		var base = this;
		base.$el.find("#submitForApprovalBtn").off().on({
			click: function(e){
				jConfirm(base.options.moduleName + " " + base.options.rule["ruleName"] + " will be locked for approval. Continue?", "Submit For Approval", function(result){
					if(result){
						DeploymentServiceJS.submitRuleForApproval(GLOBAL_storeId, base.options.moduleName, base.options.rule["ruleId"], base.options.rule["ruleName"], false,{
							callback: function(ruleStatus){
								base.options.afterSubmitForApprovalRequest(ruleStatus);
							},
							preHook:function(){
								base.options.beforeSubmitForApprovalRequest();
							}
						});
					}
				});
			}
		});
	};
	
	$.rulestatusbar.prototype.addCopyToStoreListener = function(){
		var base = this;
		base.$el.find("#copyBtn").off().on({
			click: function(e){
				$(this).copy({
					title: "Copy",
					requestCallback:function(e){
						var keyword = base.options.ruleStatus["ruleName"];
						var storeCode = e.data.storeCode;
						var ruleStatusId = base.options.ruleStatus["ruleStatusId"];
						StoreKeywordServiceJS.getKeywordByStore(keyword, storeCode, {
							callback : function(data){
								if(data==null){
									base.options.copyKeywordAndRuleRequest(keyword, storeCode, ruleStatusId);
								}
								else {
									base.options.copyRuleRequest(keyword, storeCode, ruleStatusId);
								}
							}
						});
					}
				});
			}
		});
	};

	$.rulestatusbar.prototype.getTemplate = function(){
		var base = this;
		var template = "";
		template += '<div class="plugin-rulestatusbar">';

		template += '	<div id="autoImportWarningContainer">';
		template += '		<div id="autoImportWarningPattern" class="autoImportWarning warning border fsize12 marT5" style="display:none">';
		template += '			<span class="autoImportWarningText"></span>';
		template += '			<span class="floatR">';
		template += '				<span class="loading"><img src="'+GLOBAL_contextPath+'/images/ajax-loader-rect.gif"/></span>';
		template += '				<a href="javascript:void(0);" class="compare">Compare</a>';
		template += '				<a href="javascript:void(0);" class="cancel">Cancel</a>';
		template += '			</span>';
		template += '		</div>'; 
		template += '	</div>'; 
		
		template += '	<ul id="ruleStatusContainer" class="page_info clearfix" style="display:none">';
		template += '		<li class="fLeft fBold">';
		template += '			<span>Status:</span>';
		template += '			<span id="status" class="cRed">Setup a Rule</span>';
		template += '			<span id="statusMode" class="cOrange"></span>';
		template += '		</li>';

		if(base.options.authorizeSubmitForApproval){
			template += '		<li class="fLeft bRight">';
			template += '			<a id="submitForApprovalBtn" class="btn_submit_approval btn" href="javascript:void(0);" alt="Submit For Approval" title="Submit For Approval">Submit for Approval</a>';
			template += '		</li>';
		}

		if(base.options.enableVersion){
			if(base.options.authorizeRuleBackup){
				template += '		<li class="fLeft bRight">';
				template += '			<a id="backupBtn" class="btn_backup_now btn" href="javascript:void(0);" alt="Backup Rule" title="Backup Rule">Backup Now</a>';
				template += '		</li>';
				template += '		<li class="fLeft bRight">';
				template += '			<div id="downloadVersionIcon" class="ico_download icon" alt="Download Rule Version" title="Download Rule Version"></div>';
				template += '		</li>';	
			}
		}	

		template += '		<li class="fLeft bRight">';
		template += '			<div id="commentBtn" class="ico_comments icon" alt="Show Rule Comment" title="Show Rule Comment"></div>';
		template += '		</li>';
		template += '		<li class="fLeft bRight">';
		template += '			<div id="copyBtn" class="ico_comments icon" alt="Copy" title="Copy"></div>';
		template += '		</li>';	
		template += '		<li class="fRight">';
		template += '			<span class="fbold">Last Published</span>:';
		template += '			<span id="publishedDate">' + base.options.noPublishedDateText + '</span>';
		template += '			<span id="autoImport" style="display:none"> | Auto-Import</span>';
		template += '		</li>';		
		template += '	</ul>';
		template += '</div>';
		
		return $(template);
	};
	
	$.rulestatusbar.defaultOptions = {
			rule: null,
			rulestatusbar: null,
			moduleName: "",
			ruleType: "",
			enableVersion: false,
			authorizeRuleBackup: false,
			authorizeSubmitForApproval: false,
			deleteVersionsPhysically: false,
			enableCompare: true,
			enableSingleVersionDownload: false,
			beforeSubmitForApprovalRequest: function(){}, 
			afterSubmitForApprovalRequest: function(status){}, 
			beforeRuleStatusRequest: function(){}, 
			afterRuleStatusRequest: function(status){},
			preRestoreCallback: function(base){},
			postRestoreCallback: function(base, rule){},
			noPublishedDateText: "No data available",
			autoImportWarningText: "Rule from storeName is queued on createdDate to replace this rule",
			copyKeywordAndRuleRequest: function(keyword, storeCode, ruleStatusId){},
			copyRuleRequest: function(keyword, storeCode, ruleStatusId){},
	};

	$.fn.rulestatusbar = function(options){

		if (this.length) {
			return this.each(function() {
				(new $.rulestatusbar(this, options));
			});
		};
	};
})(jQuery);
