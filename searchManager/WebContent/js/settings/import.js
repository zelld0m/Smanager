(function($){

	var Import = {
		moduleName : "Import Rule",	
		tabSelected : "",
		entityName : "",
		ruleEntityList : null,
		
		populateTabContent: function(){
			var self = this;

			$("#import").tabs("destroy").tabs({
				cookie: {
					expires: 0
				},
				show: function(event, ui){
					if(ui.panel){
						self.tabSelected = ui.panel.id;
						self.entityName = self.tabSelected.substring(0, self.tabSelected.length-3);
						self.getImportList();
					}
				}
			});
		},
		
		prepareTabContent:function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected);
			if (!$("div.circlePreloader").is(":visible")){
				$('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>').prependTo($selectedTab);
			} 
			$selectedTab.find('table.tblItems, div#actionBtn').hide();
		},
		
		cleanUpTabContent:function(){
			$('div.circlePreloader').remove();
		},
		
		getImportTypeList : function(){
			var self = this;
			EnumUtilityServiceJS.getImportTypeList({
				callback : function(data){
					self.importTypeList = data;
				}
			});
		},
		
		getRuleEntityList : function(){
			var self = this;
			EnumUtilityServiceJS.getRuleEntityList({
				callback: function(data){
					self.ruleEntityList = data;
				}
			});
		},
		
		getRuleType : function(ruleTypeId){
			var self = this;
			if(self.ruleEntityList)
				return self.ruleEntityList[ruleTypeId];
			return "";
		},
		
		checkSelectHandler : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected);
			
			$selectedTab.find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").on({
				click: function(evt){
					var selected = $selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").length;
					if (selected==0){
						$selectedTab.find("th#selectAll > input[type='checkbox']").attr("checked", false); 
					}
				}
			});
		},
		
		checkSelectAllHandler : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected);
			
			$selectedTab.find("th#selectAll > input[type='checkbox']").on({
				click: function(evt){
					var selectAll = $(this).is(":checked");
					$selectedTab.find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']").attr("checked", selectAll);
				}
			});
		},
		
		importHandler : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected);
			
			$selectedTab.find("a#importBtn").on({
				click: function(evt){
					var comment = $.trim($selectedTab.find("#importComment").val());
					
					if(self.getSelectedRefId().length==0){
						jAlert("Please select rule", self.moduleName);
					}else if ($.isBlank(comment)){
						jAlert("Please add comment", self.moduleName);
					}else if(!isXSSSafe(comment)){
						jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
					}else{
						var selRuleFltr = $selectedTab.find("#ruleFilter").val();
						
						//TODO
						alert(self.getSelectedRefId());
						/*DeploymentServiceJS.importRule(self.entityName, self.getSelectedRefId(), comment, self.getSelectedStatusId(),{
							callback: function(data){									
								postMsg(data,true);	
								self.getImportList();	
							},
							preHook:function(){ 
								self.prepareTabContent(); 
							},
							postHook:function(){ 
								self.cleanUpTabContent(); 
							}	
						});*/
					}
				}
			});
		},
		
		getPreTemplate : function(){
			var template = '';
			template  = '<div class="rulePreview w590 marB20">';
			template += '	<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>';
			template += '	<label class="w110 floatL fbold">Rule Info:</label>';
			template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
			template += '	<div class="clearB"></div>';
			template += '	<label class="w110 floatL marL20 fbold">Import Type:</label>';
			template += '	<label class="wAuto floatL" id="importType">';
			template += '		<select id="importType"><option value="">-- Import Type --</option></select>';
			template += '	</label>';
			template += '	<div class="clearB"></div>';
			template += '</div>';
			
			return template;
		},
		
		getPostTemplate : function(){
			var template = "";
			
			template  = '<div id="actionBtn" class="floatR fsize12 border pad5 w580 marB20" style="background: #f3f3f3;">';
			template += '	<h3 class="padL15" style="border:none">Import Rule Guidelines</h3>';
			template += '	<div class="fgray padL15 padR12 padB15 fsize11">';
			template += '		<p align="justify">';
			template += '			If the published rule is ready to be imported, click on <strong>Import</strong>. Provide notes in the <strong>Comment</strong> box.';
			template += '		<p>';
			template += '	</div>';
			template += '	<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>';
			template += '	<label class="floatL w480"><textarea id="importComment" rows="5" class="w460" style="height:32px"></textarea></label>';
			template += '	<div class="clearB"></div>';
			template += '	<div align="right" class="padR15 marT10">';
			template += '		<a id="approveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Import</div>';
			template += '		</a>';
			template += '		<a id="rejectBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Reject</div>';
			template += '		</a>';
			template += '	</div>';
			template += '</div>';
			
			return template;
		},
		
		getRightPanelTemplate : function(){
			var template = "";
			
			template  = '<div id="actionBtn" class="floatR fsize12 border pad5 w580 marB20" style="background: #f3f3f3;">';
			template += '	<h3 class="padL15" style="border:none">Import Rule Guidelines</h3>';
			template += '	<div class="fgray padL15 padR12 padB15 fsize11">';
			template += '		<p align="justify">';
			template += '			If the published rule is ready to be imported, click on <strong>Import</strong>. Provide notes in the <strong>Comment</strong> box.';
			template += '		<p>';
			template += '	</div>';
			template += '	<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>';
			template += '	<label class="floatL w480"><textarea id="importComment" rows="5" class="w460" style="height:32px"></textarea></label>';
			template += '	<div class="clearB"></div>';
			template += '	<div align="right" class="padR15 marT10">';
			template += '		<a id="approveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Import</div>';
			template += '		</a>';
			template += '		<a id="rejectBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Reject</div>';
			template += '		</a>';
			template += '	</div>';
			template += '</div>';
			
			return template;
		},
		
		getImportList : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected); 
			
			DeploymentServiceJS.getDeployedRules(self.entityName, "published", {
				callback:function(data){
					var list = data.list;

					$selectedTab.html($("div#tabContentTemplate").html());

					if (data.totalSize>0){
						var totalSize = data.totalSize;
						// Populate table row
						for(var i=0; i < totalSize; i++){
							var rule = list[i];
							var $table = $selectedTab.find("table#rule");
							var $tr = $selectedTab.find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(rule["ruleRefId"])).show();
							var lastPublishedDate = $.isNotBlank(rule["lastPublishedDate"])? rule["lastPublishedDate"].toUTCString(): "";
							var lastExportedDate = $.isNotBlank(rule["lastExportDate"])? rule["lastExportDate"].toUTCString(): "";
							var showId = rule["ruleRefId"] !== rule["description"];

							$tr.find("td#select > input[type='checkbox']").attr("id", rule["ruleRefId"]);
							$tr.find("td#select > input[type='checkbox']").attr("name", rule["ruleStatusId"]);

							//TODO: Get delete details from file
							if (rule["updateStatus"]!=="DELETE"){
								$tr.find("td#ruleOption > img.previewIcon").attr("id", rule["ruleRefId"]);
								$tr.find("td#ruleOption > img.previewIcon").preview({
									ruleType: self.getRuleType(rule["ruleTypeId"]),
									ruleId: rule["ruleRefId"],
									enablePreTemplate: true,
									enablePostTemplate: true,
									//enableRightPanel: true,
									postTemplate: self.getPostTemplate(),
									preTemplate: self.getPreTemplate(),
									//rightPanelTemplate: self.getRightPanelTemplate(),
									itemForceAddStatusCallback: function(base, memberIds){
										if (rule["ruleTypeId"].toLowerCase() === "elevate")
										ElevateServiceJS.isRequireForceAdd(keyword, memberIds, {
											callback:function(data){
												base.updateForceAddStatus(data);
											},
											preHook: function(){
												base.prepareForceAddStatus();
											}
										});
									}
								});
							}else{
								$tr.find("td#ruleOption > img.previewIcon").hide();
							}

							if(showId) 
								$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);

							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
							
							//TODO
							$tr.find("td#publishDate > p#publishDate").html(lastPublishedDate);
							$tr.find("td#type").html(list[i]["importStatus"]);
							
							//import type
							var $importTypeSelect = $tr.find("td#type > select#importTypeList");
							if(self.importTypeList){
								for (var importType in self.importTypeList){
									$importTypeSelect.append($("<option>", {value: importType, selected: rule["importType"] === importType}).text(self.importTypeList[importType]));
								}
							}
							
							$tr.appendTo($table);
						}

						// Alternate row style
						$selectedTab.find("tr:not(#ruleItemPattern):even").addClass("alt");

						self.checkSelectHandler();
						self.checkSelectAllHandler();
						self.importHandler();

						if (totalSize==1){
							$selectedTab.find('th#selectAll > input[type="checkbox"]').remove();
						}

					}else{
						$selectedTab.find("table#rule").append('<tr><td class="txtAC" colspan="5">No pending rules found</td></tr>');
						$selectedTab.find('th#selectAll > input[type="checkbox"]').remove();
						$selectedTab.find('div#actionBtn').hide();
					}
				},
				preHook:function(){ 
					self.prepareTabContent(); 
				},
				postHook:function(){ 
					self.cleanUpTabContent(); 
				}
			});
		},
		
		init : function() {
			var self = this;
			$("#titleText").html(self.moduleName);
			self.getImportTypeList();
			self.getRuleEntityList();
			self.populateTabContent();
		}
	};

	$(document).ready(function() {
		Import.init();
	});	

})(jQuery);	