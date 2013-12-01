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

		base.getRuleStatus = function(){

			DeploymentServiceJS.getRuleStatus(base.options.moduleName, base.options.rule["ruleId"], {
				preHook: function(){
					base.options.beforeRuleStatusRequest();
				},

				callback:function(ruleStatus){
					base.options.ruleStatus = ruleStatus;
					base.$el.html(base.getTemplate());

					if(ruleStatus!=null){
						if($.isNotBlank(ruleStatus["approvalStatus"])){
							base.$el.find("#status").text(getRuleNameSubTextStatus(ruleStatus));
						}

						if($.isNotBlank(ruleStatus["formattedLastPublishedDateTime"])){
							base.$el.find("#publishedDate").text(ruleStatus["formattedLastPublishedDateTime"]);
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
//							locked: selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify,
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

		base.init = function(){
			base.options = $.extend({},$.rulestatusbar.defaultOptions, options);
			base.$el.empty();
			base.getRuleStatus();
		};

		base.addSubmitForApprovalListener = function(){
			base.$el.find("#submitForApprovalBtn").off().on({
				click: function(e){
					jConfirm(base.options.moduleName + " " + base.options.rule["ruleName"] + " will be locked for approval. Continue?", "Submit For Approval", function(result){
						if(result){
							DeploymentServiceJS.processRuleStatus(GLOBAL_storeId, base.options.moduleName, base.options.rule["ruleId"], base.options.rule["ruleName"], false,{
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

		base.getTemplate = function(){
			var template = "";

			template += '<div class="plugin-rulestatusbar">';
			template += '	<ul class="page_info clearfix">';
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
			template += '		<li class="fRight">';
			template += '			<span class="fbold">Published</span>:';
			template += '			<span id="publishedDate">No Published Date Yet</span>';
			template += '		</li>';		
			template += '	</ul>';
			template += '</div>';

			return $(template);
		};

		// Run initializer
		base.init();
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
			beforerulestatusbarRequest: function(){}, 
			afterrulestatusbarRequest: function(status){},
			preRestoreCallback: function(base){},
			postRestoreCallback: function(base, rule){}
	};

	$.fn.rulestatusbar = function(options){

		if (this.length) {
			return this.each(function() {
				(new $.rulestatusbar(this, options));
			});
		};
	};
})(jQuery);