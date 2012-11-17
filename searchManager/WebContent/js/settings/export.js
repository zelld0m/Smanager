(function($){

	var Export = {
		moduleName : "Export Rule",	
		tabSelected : "",
		entityName : "",
		autoExport : false,
		ruleEntityList : null,
		
		postMsg : function(data,msg_){
			var self = this;

			var okmsg = 'Following rules were successfully ' + msg_;	

			for(var i=0; i<data.length; i++){	
				okmsg += '\n-'+ $("tr#ruleItem" + $.formatAsId(data[i]) + " > td#ruleRefId > p#ruleName").html();	
			}

			jAlert(okmsg, self.entityName);
		},

		populateTabContent: function(){
			var self = this;

			$("#export").tabs("destroy").tabs({
				cookie: {
					expires: 0
				},
				show: function(event, ui){
					if(ui.panel){
						self.tabSelected = ui.panel.id;
						self.entityName = self.tabSelected.substring(0, self.tabSelected.length-3);
						self.getExportList();
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
		
		getSelectedItems : function(){
			var self = this;
			var selectedItems = [];
			var $selectedTab = $("#"+self.tabSelected);
			$selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").each(function(index, value){
				selectedItems[$(this).attr("id")]=$(this).attr("name");
			});
			return selectedItems;
		},
		
		getSelectedRefId : function(){
			var self = this;
			var selectedRefIds = [];
			var selectedItems = self.getSelectedItems();
			for (var i in selectedItems){
				selectedRefIds.push(i); 
			}
			return selectedRefIds; 
		},

		getSelectedStatusId : function(){
			var self = this;
			var selectedStatusId = [];
			var selectedItems = self.getSelectedItems();
			for (var i in selectedItems){
				selectedStatusId.push(selectedItems[i]); 
			}
			return selectedStatusId; 
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
		
		exportHandler : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected);
			
			$selectedTab.find("a#exportBtn").on({
				click: function(evt){
					var comment = $.trim($selectedTab.find("#exportComment").val());
					
					if(self.getSelectedRefId().length==0){
						jAlert("Please select rule", self.moduleName);
					}else if ($.isBlank(comment)){
						jAlert("Please add comment", self.moduleName);
					}else if(!isXSSSafe(comment)){
						jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
					}else{
						RuleTransferServiceJS.exportRule(self.entityName, self.getSelectedRefId(), comment, self.getSelectedStatusId(),{
							callback: function(data){									
								self.postMsg(data, "exported");	
								self.getExportList();	
							},
							preHook:function(){ 
								self.prepareTabContent(); 
							},
							postHook:function(){ 
								self.cleanUpTabContent(); 
							}	
						});
					}
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
		
		getPostTemplate : function(){
			var template = "";
			
			template  = '<div id="actionBtn" class="marT10 fsize12 border pad10 w580 mar0 marB20" style="background: #f3f3f3;">';
			template += '	<h3 style="border:none">Export Rule Guidelines</h3>';
			template += '	<div class="fgray padL15 padR10 padB15 fsize11">';
			template += '		<p align="justify">';
			template += '			Before exporting any rule, it is advisable to review rule details.<br/><br/>';
			template += '			If the published rule is ready to be exported, click on <strong>Export</strong>. Provide notes in the <strong>Comment</strong> box.';
			template += '		<p>';
			template += '	</div>';
			template += '	<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>';
			template += '	<label class="floatL w480"><textarea id="exportComment" rows="5" class="w460" style="height:32px"></textarea></label>';
			template += '	<div class="clearB"></div>';
			template += '	<div align="right" class="padR15 marT10">';
			template += '		<a id="exportBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '			<div class="buttons fontBold">Export</div>';
			template += '		</a>';
			template += '	</div>';
			template += '</div>';
			
			return template;
		},
		
		getExportList : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected); 
			
			RuleTransferServiceJS.getPublishedRules(self.entityName, {
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
								
								$tr.find("td#ruleOption > img.previewIcon").xmlpreview({
									ruleType: self.entityName,
									ruleId: rule["ruleRefId"],
									ruleInfo: rule["description"],
									requestType: rule["updateStatus"],
									enablePreTemplate: true,
									enablePostTemplate: true,
									leftPanelSourceData: "xml",
									postTemplate: self.getPostTemplate(),
									itemGetRuleXmlCallback: function(base, contentHolder, ruleType, ruleId){
										RuleTransferServiceJS.getRuleToExport(self.entityName, rule["ruleRefId"],{
											callback: function(xml){
												base.options.ruleXml = xml;
												base.getRuleData(contentHolder, ruleType, ruleId);
											}
										});
									},
									itemForceAddStatusCallback: function(base, memberIds){
										if (self.entityName.toLowerCase() === "elevate")
										ElevateServiceJS.isRequireForceAdd(rule["ruleRefId"], memberIds, {
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
							$tr.find("td#type").html(list[i]["exportType"]);
							
							//TODO
							$tr.find("td#publishDate > p#requestedBy").html(list[i]["publishedStatus"]);
							$tr.find("td#publishDate > p#requestedDate").html(lastPublishedDate);
							
							$tr.find("td#exportDate > p#requestedBy").html(list[i]["exportBy"]);
							$tr.find("td#exportDate > p#requestedDate").html(lastExportedDate);
							$tr.appendTo($table);
						}

						// Alternate row style
						$selectedTab.find("tr:not(#ruleItemPattern):even").addClass("alt");

						self.checkSelectHandler();
						self.checkSelectAllHandler();
						self.exportHandler();

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
			
			self.getRuleEntityList();
			self.populateTabContent();

			RuleTransferServiceJS.getAutoExport({
				callback: function(isAutoExport){
					$('.firerift-style-checkbox').slidecheckbox({
						initOn: isAutoExport,
						locked: false, //TODO:
						changeStatusCallback: function(base, dt){
							RuleTransferServiceJS.setAutoExport(dt.status, {
								callback: function(set){
									
								}
							});
						}
					});
				}
			});
			
		}
	};

	$(document).ready(function() {
		Export.init();
	});	

})(jQuery);	