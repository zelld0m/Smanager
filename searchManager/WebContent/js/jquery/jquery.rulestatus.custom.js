(function($){

	$.rulestatus = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("rulestatus", base);

		base.getRuleStatus = function(){

			DeploymentServiceJS.getRuleStatus(base.options.moduleName, base.options.rule["ruleId"], {
				callback:function(ruleStatus){
					base.options.ruleStatus=ruleStatus;
					base.$el.html(base.getTemplate());
					base.$el.find("span#status,span#statusMode,span#statusDate").empty();

					if(ruleStatus!=null){
						ruleId = ruleStatus["ruleStatusId"];
						if (ruleId == null) {
							ruleId = "";
						}

						base.$el.find("#submitForApprovalTemplate").show();
						base.$el.find("div#statusHolder").hide();

						if($.isNotBlank(ruleStatus["approvalStatus"])){
							base.$el.find("div#statusHolder").show();
							base.$el.find("span#status").html(getRuleNameSubTextStatus(ruleStatus));
						}

						base.$el.find("div#publishHolder").hide();
						if($.isNotBlank(ruleStatus["lastPublishedDate"])){
							base.$el.find("div#publishHolder").show();
							base.$el.find("span#statusDate").html(ruleStatus["lastPublishedDate"].toUTCString());
						}

						base.$el.find("a#submitForApprovalBtn").show();
						if(ruleStatus["locked"]){
							base.$el.find("span#statusMode").append("[Read-Only]");
							base.$el.find("a#submitForApprovalBtn").hide();
						}

						base.$el.find("div#commentHolder span#commentIcon").off().on({
							click:function(e){
								$(this).comment({
									title: "Rule Comment",
									itemDataCallback: function(plugin, page){
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
								});
							}
						});
					}

					base.addSubmitForApprovalListener();
					base.options.afterRuleStatusRequest(ruleStatus);

					if ($.endsWith(base.options.rule["ruleId"], "_default")){
						base.$el.empty();
					}
				}
			});	
		};

		base.init = function(){
			base.options = $.extend({},$.rulestatus.defaultOptions, options);
			base.$el.empty();
			base.options.beforeRuleStatusRequest();
			base.getRuleStatus();
		};

		base.addSubmitForApprovalListener = function(){
			base.$el.find("#submitForApprovalBtn").off().on({
				click: function(e){
					jConfirm(base.options.moduleName + " " + base.options.rule["ruleName"] + " will be locked for approval. Continue?", "Submit For Approval", function(result){
						if(result){
							DeploymentServiceJS.processRuleStatus(base.options.moduleName, base.options.rule["ruleId"], base.options.rule["ruleName"], false,{
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

			template += '<div id="ruleStatusTemplate" class="clearB floatR farial fsize12 fDGray txtAR w730 GraytopLine">'; 
			template += '	<div class="txtAL w730 minHeight36" style="background: #e8e8e8">';       	
			template += '		<div class="floatL padT10 padL10" style="width:70%">';

			if(base.options.enableVersion){
				template += '			<div id="versionHolder">';
				template += '				<label class="floatL wAuto padL5 fsize11 fLgray">';
				template += '					<span><img id="versionIcon" class="pointer" src="../images/icon_version.png"  alt="Rule Versions" title="Rule Versions"></span>';			        		 
				template += '				</label>';

				if(base.options.authorizeRuleBackup){
					template += '				<label class="floatL marTn7">';
					template += '					<a id="backupBtn" href="javascript:void(0);" class="btnGraph btnBackUp clearfix">';
					template += '						<div class="btnGraph btnBackUp"></div>';
					template += '					</a>'; 
					template += '				</label>';
				}

				template += '				<label class="floatL wAuto marRL5 fLgray2">|</label>';
				template += '			</div>';
			}

			template += '			<div id="commentHolder">';
			template += '				<label class="floatL wAuto padL5 fsize11 fLgray">';
			template += '					<span id="commentIcon"><img src="' + GLOBAL_contextPath + '/images/icon_comment.png" class="pointer"></span>';  
			template += '				</label>';
			template += '			</div>';

			template += '			<div id="statusHolder">';
			template += '				<label class="floatL wAuto marRL5 fLgray2">|</label>';
			template += '				<label class="floatL wAuto">Status:</label>';
			template += '				<label class="floatL wAuto padL5 fsize11 fLgray">';
			template += '					<span id="status"></span>'; 
			template += '					<span id="statusMode" class="fsize11 forange padL5"></span>'; 
			template += '				</label>';
			template += '			</div>';

			template += '			<div id="publishHolder">';
			template += '				<label class="floatL wAuto marRL5 fLgray2">|</label>';
			template += '				<label class="floatL wAuto">Last Published:</label>';
			template += '				<label class="padL5 fLgray fsize11">';
			template += '					<span id="statusDate"></span>';
			template += '				</label>';
			template += '			</div>';
			template += '		</div>';  			  	
			template += '		<div class="floatR marL8 marR3 padT5">'; 	        	

			if(base.options.authorizeSubmitForApproval){
				template += '		<a id="submitForApprovalBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template += '			<div class="buttons fontBold">Submit for Approval</div>';
				template += '		</a>';
			}

			template += '		</div>';
			template += '	</div>';	
			template += '	<div class="clearB"></div>';	
			template += '</div>';

			return $(template);
		};

		// Run initializer
		base.init();
	};

	$.rulestatus.defaultOptions = {
			rule: null,
			ruleStatus: null,
			moduleName: "",
			enableVersion: false,
			authorizeRuleBackup: false,
			authorizeSubmitForApproval: false,
			beforeSubmitForApprovalRequest: function(){}, 
			afterSubmitForApprovalRequest: function(status){}, 
			beforeRuleStatusRequest: function(){}, 
			afterRuleStatusRequest: function(status){}
	};

	$.fn.rulestatus = function(options){

		if (this.length) {
			return this.each(function() {
				(new $.rulestatus(this, options));
			});
		};
	};
})(jQuery);