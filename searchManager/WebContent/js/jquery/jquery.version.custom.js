(function($){

	$.version = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("version", base);

		
		base.addButtonTrigger = function(){
			base.imagePath = GLOBAL_contextPath + (base.$el.find("div#versions").is(":visible:not(:empty)")? "/images/icon_version_gray.png":"/images/icon_version.png");
			
			$(base.options.buttonHolderId).find("img#versionIcon").prop("src", base.imagePath).off().on({
				click: function(evt){
					if(base.$el.find("div#versions").is(":visible")){
						$(this).prop("src", GLOBAL_contextPath + "/images/icon_version.png");
						base.$el.find("div#versions").slideUp('slow', function(){

						});
					}
					else{
						$(this).prop("src", GLOBAL_contextPath + "/images/icon_version_gray.png");
						base.getData();
					}
				}
			});

			$(base.options.buttonHolderId).find("#backupBtn").off().on({
				click:function(evt){
					RuleVersioningServiceJS.getRuleVersions(base.options.ruleType,base.options.ruleId, {
						callback: function(data){
							if(data!=null && data.length >= base.options.limit){
								alert("Only maximum of "+base.options.limit+" backups is allowed!");
							}
							else{
								$(evt.currentTarget).qtip({
									id: "rule-backup",
									content: {
										text: $('<div/>'),
										title: { 
											text: "Backup Rule", button:true
										}
									},
									position: {
										my: 'center',
										at: 'center',
										target: $(window)
									},
									show: {
										modal: true,
										solo: true,
										ready: true
									},
									style: {
										width:'auto'
									},
									events: {
										show: function(event, api) {
											var $content = $("div", api.elements.content);
											$content.html(base.getVersionNameTemplate());

											$content.find("a#rcancelBtn, a#rsaveBtn").on({
												click: function(evt){

													var reason = $content.find("#reason").val();
													var backupName = $content.find("#backupName").val();

													switch($(evt.currentTarget).attr("id")){
													case "rsaveBtn": 
														
														if ($.isNotBlank(reason) && $.isNotBlank(backupName)){
															if(!validateField('Name', backupName, 1)){
																return;
															}
															else if (backupName.length>100){
																//showMessage("#backupName","Name should not exceed 100 characters.");
																alert("Name should not exceed 100 characters.");
															}
															else if(!validateField('Reason', reason, 1)){
																return;
															}
															else if (reason.length>255){
																//showMessage("#reason","Reason should not exceed 255 characters.");
																alert("Reason should not exceed 255 characters.");
															}
															else{
																RuleVersioningServiceJS.createRuleVersion(base.options.ruleType, base.options.ruleId, backupName, reason, {
																	callback: function(data){
																		if (data) {
																			alert("Successfully created back up!");
																			base.getData();
																		} else {
																			alert("Failed creating back up!");
																		}
																	},
																	preHook: function(){
																		api.destroy();
																	}
															});
															}

														}else{
															alert("Name and Reason can not be blank!");
														}
														break;
													case "rcancelBtn": 
														api.destroy();
														break;
													}	
												}
											});
										},
										hide: function(event, api) {
											api.destroy();
										}
									}
								});
							}
						}
					});
				}
			}).parent().css(base.options.locked?{display: "none"}:{display: "block"});
		};
		
		base.init = function(){
			base.options = $.extend({},$.version.defaultOptions, options);
			base.$el.empty();
			
			base.addButtonTrigger();
		};

		base.getTemplate = function(){
			var template  = '<div id="versions" class="versions marT20 w99p marRLauto">';
			template += '		<div class="clearB"/>';
			template += '		<div style="max-height:365px;" class="w100p padT0 fsize12">';
			template += '			<div class="items border clearfix" id="itemPattern">';
			template += '				<label style="border-right:1px solid #cccccc; background:#eee" class="floatL w100 txtAC fbold padTB5"> Version # </label>';
			template += '				<label style="border-right:1px solid #cccccc; background:#eee" class="floatL w340 txtAC fbold padTB5"> Version Name </label>';
			template += '				<label style="border-right:1px solid #cccccc; background:#eee" class="floatL w200 txtAC fbold padTB5"> Version Date </label>';
			template += '				<label style="border-right:0px solid #cccccc; background:#eee" class="floatL w20 txtAC fbold padTB5"> &nbsp; </label>';
			template += '				<label style="border-right:0px solid #cccccc; background:#eee" class="floatL w20 txtAC fbold padTB5"> &nbsp; </label>';
			template += '				<label style="background:#eee" class="floatL w30 txtAC fbold padTB5"> &nbsp; </label>';
			template += '			</div>';
			template += '		</div>';
			template += '		<ul id="verItemList">';
			template += '			<li id="verItemPattern" class="verItems borderB padTB5 clearfix w99p padL5 fsize12" style="display:none">';
			template += '				<label class="select floatL fbold w55" style="display:none"><input type="checkbox"></label>';
			template += '				<label class="ver w100 floatL txtAC"></label>';
			template += '				<label class="verDetail floatL w340">';
			template +=	'					<p id="verName" class="breakWord fbold"></p>';
			template +=	'					<p id="verReason" class="fsize11 breakWord"></p>';
			template +=	'				</label>';
			template += '				<label class="verDate w200 floatL"></label>';
			template += '				<label class="previewIcon floatL w20 posRel topn2"><img alt="Preview Content" title="Preview Content" src="' + GLOBAL_contextPath + '/images/icon_reviewContent2.png" class="top2 posRel pointer"></label>';
			if(!base.options.locked){
				template += '				<label class="restoreIcon floatL w20 posRel topn2"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel pointer"></label>';
				template += '				<label class="deleteIcon floatL w20 posRel topn2"><img alt="Delete Backup" title="Delete Backup" src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="top2 posRel pointer"></label>';
			}
			template += '			</li>';
			template += '		</ul>';
			template += '</div>';
			return template;
		};

		base.getVersionNameTemplate =  function(){
			var template  = '<div id="reasonView">';
			template += '		<div class="marB20">';
			template += '			<label class="w70 marT10 floatL fbold">Name</label>';
			template += '			<label><input id="backupName" type="text" class="w240 marT5"/></label>';
			template += '			<div class="clearB"></div>';
			template += '			<label class="w70 marT4 floatL fbold">Reason</label>';
			template += '			<label><textarea id="reason" class="w240 marT5"/></label>';
			template += '		</div>';
			template += '		<div align="right" class="padR3 marT10">';
			template += '			<a id="rsaveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Save</div>';
			template += '			</a>';
			template += '			<a id="rcancelBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Cancel</div>';
			template += '			</a>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '	</div>';
			return template;
		};

		base.getData = function(){
			RuleVersioningServiceJS.getRuleVersions(base.options.ruleType,base.options.ruleId, {
				callback: function(data){

					var $ul = $("ul#verItemList");
					$ul.find("li.verItems:not(#verItemPattern)").remove();

					if(data!=null && data.length>0){
						for (var i=0; i < data.length ; i++){
							var $li = $ul.find("li#verItemPattern").clone();
							var item = data[i];
							$li.prop("id", "row" + $.formatAsId(item["ruleId"]));
							$li.find("label.ver").html(item["version"]);
							$li.find("label.verDate").html(item["dateCreated"].toUTCString());
							$li.find("label.verDetail > p#verName").html(item["name"]);
							$li.find("label.verDetail > p#verReason").html(item["reason"]);
							$li.show();
							$ul.append($li);
						}
						
						$ul.find("li").removeClass("alt");
						$ul.find("li:even").addClass("alt");

						$ul.find("li.verItems:not(#verItemPattern) > label.previewIcon > img").on({
							click:function(evt){
								var verNum = $(this).parent().siblings("label.ver").html();
								$(evt.currentTarget).preview({
									ruleType: base.options.ruleType,
									ruleId: base.options.ruleId,
									version: verNum
								});
							}
						});

						$ul.find("li.verItems:not(#verItemPattern) > label.deleteIcon > img").off().on({
							click:function(evt){
								var verNum = $(this).parent().siblings("label.ver").html();

								if (confirm("Delete restore point version " +  verNum + "?")){
									RuleVersioningServiceJS.deleteRuleVersion(base.options.ruleType, base.options.ruleId, verNum, {
										callback:function(data){
											base.getData();
										},
										preHook:function(){

										},
										postHook:function(){

										}
									});
								}
							}
						});

						$ul.find("li.verItems:not(#verItemPattern) > label.restoreIcon > img").off().on({
							click:function(evt){
								var verNum = $(this).parent().siblings("label.ver").html();

								if (confirm("Restore data to version " + verNum + "?")){

									RuleVersioningServiceJS.restoreRuleVersion(base.options.ruleType, base.options.ruleId, verNum, {
										callback:function(data){

										},
										preHook:function(){
											base.$el.empty();

											var template = '	<div id="preloader" class="txtAC">';
											template += '			<img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
											template += '			<div class="clearB">';
											template += '			<div>Restoring Backup Data</div>';
											template += '		</div>';

											base.$el.html(template);
										},
										postHook:function(){
											RuleVersioningServiceJS.getRankingRuleVersion(base.options.ruleId, verNum, {
												callback: function(data){
													base.options.restoreCallback(data);
												}
											});
										}
									});
								}
							}
						});
					}else{
						var $li = $ul.find("li#verItemPattern").clone();
						$li.prop("id", "row_empty");
						$li.find("label:not(.ver)").remove();
						$li.find("label.ver").removeClass("w100").addClass("w99p").html("No available version for this rule");
						$li.show();
						$ul.append($li);
					}
				},
				preHook: function(){
					base.$el.empty();
					base.$el.html(base.getTemplate());
					var $ul = $("ul#verItemList");
					var $li = $ul.find("li#verItemPattern").clone();
					$li.prop("id", "row_empty");
					$li.find("label:not(.ver)").remove();
					$li.find("label.ver").removeClass("w100").addClass("w99p").html('<img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">');
					$li.show();
					$ul.append($li);
					base.options.beforeRequest();
				},
				postHook: function(){
					base.addButtonTrigger();
					base.options.afterRequest();
				}
			});
		},

		// Run initializer
		base.init();
	};

	$.version.defaultOptions = {
			headerText: "",
			ruleType: "",
			ruleId: "",
			limit: 3,
			locked: true,
			buttonHolderId: "",
			beforeRequest: function(){},
			afterRequest: function(){},
			restoreCallback: function(rule){}
	};

	$.fn.version = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.version(this, options));
			});
		};
	};

})(jQuery);