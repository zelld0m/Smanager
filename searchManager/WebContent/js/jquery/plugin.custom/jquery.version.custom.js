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

		base.init = function(){
			base.options = $.extend({},$.version.defaultOptions, options);
			base.$el.empty();
			base.showVersion();
		};

		base.showVersion = function(){
			$(base.$el).qtip({
				content: {
					text: $('<div/>'),
					title: { text: base.options.moduleName + " Version", button: true }
				},
				position: {
					my: 'center',
					at: 'center',
					target: $(window)
				},
				style: {
					width: "auto"
				},
				show: {
					ready: true,
					modal:true
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getTemplate());
						base.contentHolder.find("#versionWrapper").before(base.getItemListTemplate());
						base.getAvailableVersion();
						base.addSaveButtonListener();
					},
					hide: function(event, api){
						base.options.afterClose();
						api.destroy();
					}
				}
			});
		};

		base.addSaveButtonListener = function(){
			var $content = base.contentHolder;

			$content.find("a#cancelBtn, a#saveBtn").on({
				click: function(e){

					var name = $content.find("input#name").val();
					var notes = $content.find("textarea#notes").val();

					switch($(e.currentTarget).attr("id")){
					case "saveBtn": 

						if(!validateField('Name', name, 1) || !validateField('Notes', notes, 1)){
							return;
						}

						if (name.length>100){
							jAlert("Name should not exceed 100 characters.");
							return
						}

						if (notes.length>255){
							jAlert("Notes should not exceed 255 characters.");
							return
						}

						base.createVersion(name, notes);

						break;
					case "cancelBtn": 
						base.api.destroy();
						break;
					}	
				}
			});
		};

		base.createVersion = function(name, notes){
			RuleVersioningServiceJS.createRuleVersion(base.options.ruleType, base.options.ruleId, name, notes, {
				callback: function(data){
					if (data) {
						jAlert("Successfully created back up!");
						base.getAvailableVersion();
					} else {
						jAlert("Failed creating back up!");
					}
				}
			});
		};

		base.addDeleteVersionListener = function(tr, item){
			var $tr = tr;
			var $item = item;
			
			$tr.find(".deleteIcon").on({
				click:function(e){
					jConfirm("Delete restore point version " + $item["name"] + "?" , "Delete Version", function(result){
						if(result){
							RuleVersioningServiceJS.deleteRuleVersion(base.options.ruleType, base.options.ruleId, $item["version"], {
								callback:function(data){
									base.getAvailableVersion();
								}
							});
						}
					});
				}
			});
		};

		base.addRestoreVersionListener = function(tr, item){
			var $tr = tr;
			var $item = item;
			
			$tr.find(".restoreIcon").on({
				click:function(e){
					jConfirm("Restore data to version " + $item["name"] + "?" , "Restore Version", function(result){
						if(result){
							RuleVersioningServiceJS.restoreRuleVersion(base.options.ruleType, base.options.ruleId, $item["version"], {
								callback:function(data){

								},
								preHook:function(){
									base.options.preRestoreCallback(base);
								},
								postHook:function(){
									RuleVersioningServiceJS.getRankingRuleVersion(base.options.ruleId, $item["version"], {
										callback: function(data){
											base.options.postRestoreCallback(base, data);
										}
									});
								}
							});
						}
					});
				}
			});
		};

		base.getAvailableVersion = function(){
			var $content = base.contentHolder;
			var $table = $content.find("table#versionList");

			RuleVersioningServiceJS.getRuleVersions(base.options.ruleType,base.options.ruleId, {
				callback: function(data){
					$table.find("tr.itemRow:not(#itemPattern)").remove();
					
					if(data.length>0){
						$table.find("tr#empty_row").hide();
					}else{
						$table.find("tr#empty_row").show();
					}
					
					for (var i=0; i < data.length ; i++){
						var item = data[i];
						var $tr = $table.find("tr#itemPattern").clone();
						$tr.prop("id", "item" + $.formatAsId(item["ruleId"]));

						$tr.find("td#itemId").html(item["version"]);
						$tr.find("td#itemDate").html(item["dateCreated"].toUTCString());
						$tr.find("td#itemInfo > p#name").html(item["name"]);
						$tr.find("td#itemInfo > p#notes").html(item["reason"]);
						base.addDeleteVersionListener($tr, item);
						base.addRestoreVersionListener($tr, item);
						$tr.show();
						$table.append($tr);
					}
					$table.find("tr.itemRow:not(#itemPattern):even").addClass("alt");
				},
				postHook:function(){
					$table.find("tr#preloader").remove();
				}
			});
		};

		base.getTemplate = function(){
			var template  = '';

			template += '<div style="width:700px">';
			template += '<div id="versionWrapper" style="floatL w400">';
			template += '	<h2 class="confirmTitle">This is the rule status section</h2>';			
			
			template += '	<div id="version" class="floatL w400">';
			template += '		<div class="w400 mar0 pad0">';
			template += '			<table class="tblItems w100p marT5">';
			template += '				<tbody>';
			template += '					<tr>';
			template += '						<th class="displayBlock w60">';
			template += '							<a id="compareBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '								<div class="buttons fontBold">Compare</div>';
			template += '							</a>';
			template += '						</th>';
			template += '						<th class="w160">Name</th>';
			template += '						<th class="w135">Date</th>';
			template += '						<th class="w55"></th>';
			template += '					</tr>';
			template += '				<tbody>';
			template += '			</table>';
			template += '		</div>';
			template += '		<div class="w400 mar0 pad0" style="max-height:180px; overflow-y:auto;">';
			template += '			<table id="versionList" class="tblItems w100p">';
			template += '				<tbody>';
			template += '					<tr id="itemPattern" class="itemRow" style="display: none">';
			template += '						<td width="24px" class="txtAC" id="itemSelect">';
			template += '	                   	<input id="select" type="checkbox"/>';
			template += '						</td>';
			template += '						<td width="28px" class="txtAC" id="itemId"></td>';
			template += '						<td width="120px" class="txtAC" id="itemInfo">';
			template +=	'							<p id="name" class="w120 breakWord fbold"></p>';
			template +=	'							<p id="notes" class="w120 fsize11 breakWord"></p>';
			template += '						</td>';
			template += '						<td width="120px" class="txtAC" id="itemDate"></td>';
			template += '						<td width="auto" style="min-width:40px" class="txtAC">';
			template += '							<label class="restoreIcon floatL w20 posRel topn2"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"></label>';
			template += '							<label class="deleteIcon floatL w20 posRel topn2"><img alt="Delete Backup" title="Delete Backup" src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="top2 posRel"></label>';
			template += '						</td>';
			template += '					</tr>';
			template += '					<tr id="preloader">';
			template += '						<td colspan="6" class="txtAC">';
			template += '							<img id="preloader" alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';	
			template += '						</td>';
			template += '					</tr>';
			template += '					<tr id="empty_row" style="display:none">';
			template += '						<td colspan="6" class="txtAC">';
			template += '							No available version for this rule';	
			template += '						</td>';
			template += '					</tr>';
			template += '				</tbody>';
			template += '			</table>';
			template += '		</div>';
			template += '	</div>'; //end version

			template += '	<div id="addVersion">';
			template += '		<div id="actionBtn" class="floatL marT10 fsize12 border pad10 w380 marB20" style="background: #f3f3f3;">';
			template += '			<h3 style="border:none;">Rule Version</h3>';
			template += '			<div class="fgray padL10 padR10 padB15 fsize11">';
			template += '			<p align="justify">';
			template += '				Before approving any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>';
			template += '				If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.';
			template += '			</p>';
			template += '		</div>';

			template += '		<div>';
			template += '			<label class="floatL padL13 w100 marT5"><span class="fred">*</span>Name:</label>';
			template += '			<label class="floatL w260 marT5"><input type="text" id="name" class="w260"></label>';
			template += '			<div class="clearB"></div>';
			template += '			<label class="floatL padL13 marT5 w100"><span class="fred">*</span>Notes:</label>';
			template += '			<label class="floatL w260 marT5"><textarea id="notes" class="w260" style="height:32px"></textarea></label>';
			template += '		</div>';

			template += '		<div class="clearB"></div>';
			template += '		<div align="right" class="marT10">';
			template += '			<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Create Version</div>';
			template += '			</a>';
			template += '			<a id="cancelBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Cancel</div>';
			template += '			</a>';
			template += '		</div>';

			template += '	</div>'; // end addVersion
			template += '	</div>';	// end w400		
			template += '</div>'; // end w700


			return template;
		};

		
		base.getItemListTemplate =function(){
			var template  = '';
			
			template += '	<div class="w280 floatR border" style="height:500px">';
			template += '		<div> lorem ipsum dolor sit amet </div>';
			template += '	</div>';
			
			return template;
		};
		
		// Run initializer
		base.init();
	};

	$.version.defaultOptions = {
			moduleName: "",
			headerText: "",
			ruleType: "",
			ruleId: "",
			limit: 3,
			locked: true,
			beforeRequest: function(){},
			afterRequest: function(){},
			preRestoreCallback: function(base){},
			postRestoreCallback: function(base, rule){},
			afterClose:function(){}
	};

	$.fn.version = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.version(this, options));
			});
		};
	};

})(jQuery);
