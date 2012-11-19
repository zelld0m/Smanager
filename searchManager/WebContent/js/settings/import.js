(function($){

	var Import = {
			moduleName : "Import Rule",	
			tabSelected : "",
			entityName : "",
			ruleEntityList : null,
			importTypeList : null,
			importAsList : null,

			postMsg : function(data,pub){
				var self = this;
				var msg_ = pub ? 'imported:' : 'rejected:';

				var okmsg = 'Following rules were successfully ' + msg_;	

				for(var i=0; i<data.length; i++){	
					okmsg += '\n-'+ $("tr#ruleItem" + $.formatAsId(data[i]) + " > td#ruleRefId > p#ruleName").html();	
				}

				jAlert(okmsg, self.entityName);
			},

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

			getSelectedImportAsRefId : function(){
				var self = this;
				var selectedImportAsRefId = [];
				var $selectedTab = $("#"+self.tabSelected);

				var selectedItems = self.getSelectedItems();
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem_"+id);
					selectedImportAsRefId.push($selectedTr.find("td#importAs > select#importAsList > option:selected")[0].value); 
				}
				return selectedImportAsRefId;
			}, 

			getSelectedImportType : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems();
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem_"+id);
					selectedItems.push($selectedTr.find("td#type > select#importTypeList > option:selected")[0].text); 
				}
				return selectedItems;
			}, 

			getSelectedRuleName : function(){
				var self = this;
				var selectedRuleNames = [];
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems();
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem_"+id);
					selectedRuleNames.push($selectedTr.find("td#importAs > select#importAsList > option:selected")[0].text); 
				}
				return selectedRuleNames;
			},

			getSelectedItems : function(){
				var self = this;
				var selectedItems = [];
				var $selectedTab = $("#"+self.tabSelected);
				$selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:checked").each(function(index, value){
					selectedItems[$(this).attr("id")] = $(this).attr("name");
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

			importHandler : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);

				$selectedTab.find("a#okBtn, a#rejectBtn").on({
					click: function(evt){
						var comment = $.trim($selectedTab.find("#comment").val());

						if(self.getSelectedRefId().length==0){
							jAlert("Please select rule", self.moduleName);
						}else if ($.isBlank(comment)){
							jAlert("Please add comment", self.moduleName);
						}else if(!isXSSSafe(comment)){
							jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
						}else{
							switch($(evt.currentTarget).attr("id")){
							case "okBtn":
								RuleTransferServiceJS.importRules(self.entityName, self.getSelectedRefId(), comment, self.getSelectedImportType(), self.getSelectedImportAsRefId(), self.getSelectedRuleName(), {
									callback: function(data){									
										self.postMsg(data,true);	
										self.getImportList();	
									},
									preHook:function(){ 
										self.prepareTabContent(); 
									}	
								});
								break;
							case "rejectBtn": 
								RuleTransferServiceJS.unimportRules(self.entityName, self.getSelectedRefId(), comment, {
									callback: function(data){
										self.postMsg(data,false);	
										self.getImportList();
									},
									preHook:function(){ 
										self.prepareTabContent(); 
									}	
								});
								break;
							}
						}
					}
				});
			},

			getPreTemplate : function(selectedType){
				var template = '';
				template  = '<div class="rulePreview w590 marB20">';
				template += '	<div class="alert marB10">The rule below is pending for import. Please examine carefully the details</div>';
				template += '	<label class="w110 floatL fbold">Rule Info:</label>';
				template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '	<div class="clearB"></div>';
				template += '	<label class="w110 floatL marL20 fbold">Import Type:</label>';
				template += '	<label class="wAuto floatL" id="importType">';
				template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
				template += '	</label>';
				template += '	<div class="clearB"></div>';
				template += '</div>';

				return template;
			},

			getPostTemplate : function(){
				var template = "";

				template  = '<div id="actionBtn" class="marT10 fsize12 border pad10 w580 mar0 marB20" style="background: #f3f3f3;">';
				template += '	<h3 style="border:none">Import Rule Guidelines</h3>';
				template += '	<div class="fgray padL15 padR10 padB15 fsize11">';
				template += '		<p align="justify">';
				template += '			Before importing any rule, it is advisable to review rule details.<br/><br/>';
				template += '			If the rule is ready to be imported, click on <strong>Import</strong>. Provide notes in the <strong>Comment</strong> box.';
				template += '		<p>';
				template += '	</div>';
				template += '	<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>';
				template += '	<label class="floatL w480"><textarea id="comment" rows="5" class="w460" style="height:32px"></textarea></label>';
				template += '	<div class="clearB"></div>';
				template += '	<div align="right" class="padR15 marT10">';
				template += '		<a id="okBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
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

				template += '	<div class="rulePreview w590 marB20">';
				template += '		<div class="alert marB10">';
				template += '			Selected rule below will be overwritten when import button is clicked.';
				template += '			It is advisable to review both rules as this action cannot be undone.';
				template += '		</div>';
				template += '		<label class="wAuto floatL" id="ruleInfo"></label>';
				template += '		<div class="clearB"></div>';
				template += '		<label class="w110 floatL marL20 fbold">Import As:</label>';
				template += '		<label class="wAuto floatL">';
				template += '			<select id="importAs">';
				template += '				<option>Import As New Rule</option>';
				template += '    		</select>';
				template += '		</label>';
				template += '		<div class="clearB"></div>';
				template += '	</div>';

				return template;
			},

			getImportList : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected); 

				RuleTransferServiceJS.getAllRulesToImport(self.entityName, {
					callback:function(data){
						var list = data;
						var totalSize = data.length;

						$selectedTab.html($("div#tabContentTemplate").html());

						if (totalSize>0){
							// Populate table row
							for(var i=0; i < totalSize; i++){
								var rule = list[i];
								var ruleId = rule["ruleId"];
								var $table = $selectedTab.find("table#rule");
								var $tr = $selectedTab.find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(ruleId)).show();
								var lastPublishedDate = $.isNotBlank(rule["lastPublishedDate"])? rule["lastPublishedDate"].toUTCString(): "";
								var lastExportedDate = $.isNotBlank(rule["lastExportDate"])? rule["lastExportedDate"].toUTCString(): "";
								$tr.find("td#select > input[type='checkbox']").attr({"id":ruleId, "name": rule["ruleName"]});

								//TODO: Get delete details from file
								if (rule["updateStatus"]!=="DELETE"){
									$tr.find("td#ruleOption > img.previewIcon")
									.attr("id", ruleId)
									.xmlpreview({
										transferType: "import",
										ruleType: self.entityName,
										ruleId: ruleId,
										ruleXml: rule,
										enablePreTemplate: true,
										enablePostTemplate: true,
										leftPanelSourceData: "xml",
										enableRightPanel: true,
										rightPanelSourceData: "database",
										postTemplate: self.getPostTemplate(),
										preTemplate: self.getPreTemplate(rule["importType"]),
										rightPanelTemplate: self.getRightPanelTemplate(),
										postButtonClick: function(){
											self.getImportList();
										},
										itemImportAsListCallback: function(base, contentHolder){
											DeploymentServiceJS.getDeployedRules(self.entityName, "published", {
												callback : function(data){
													base.populateImportAsList(data, contentHolder);
												}
											});
										},
										itemImportTypeListCallback: function(base, contentHolder){
											EnumUtilityServiceJS.getImportTypeList({
												callback : function(data){
													base.populateImportTypeList(data, contentHolder);
												}
											});
										},
										itemForceAddStatusCallback: function(base, memberIds){
											if (self.entityName === "elevate"){
												ElevateServiceJS.isRequireForceAdd(ruleId, memberIds, {
													callback:function(data){
														base.updateForceAddStatus(data);
													},
													preHook: function(){
														base.prepareForceAddStatus();
													}
												});
											}
										}
									});
								}else{
									$tr.find("td#ruleOption > img.previewIcon").hide();
								}

								if(ruleId !== rule["ruleName"])	
									$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleId"]);

								$tr.find("td#ruleRefId > p#ruleName").html(list[i]["ruleName"]);

								//TODO
								$tr.find("td#publishDate > p#publishDate").html(lastPublishedDate);
								$tr.find("td#type").html(list[i]["importStatus"]);

								//import type
								var $importTypeSelect = $tr.find("td#type > select#importTypeList");

								if(self.importTypeList){
									for (var importType in self.importTypeList){
										$importTypeSelect.append($("<option>", {value: importType}).text(self.importTypeList[importType]));
									}
								}

								//import as
								var $importAsSelect = $tr.find("td#importAs > select#importAsList");
								switch(list[i]["ruleEntity"]){
								case "ELEVATE": 
								case "EXCLUDE": 
								case "DEMOTE": 
								case "FACET_SORT": 
									$importAsSelect.append($("<option>", {value: list[i]["ruleId"]})
											.text(list[i]["ruleName"]))
											.attr({
												disabled: "disabled"
											}).val(list[i]["ruleId"]);
									break;
								case "RANKING_RULE":	
								case "QUERY_CLEANING":
									if(self.importAsList){
										for (var index in self.importAsList){
											$importAsSelect.append($("<option>", {value: self.importAsList[index]["ruleRefId"]}).text(self.importAsList[index]["description"]));
										}
									}
									break;
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
						self.getImportTypeList();
					},
					postHook:function(){ 
						self.cleanUpTabContent(); 
					}
				});
			},

			getImportAsList : function(){
				var self = this;

				DeploymentServiceJS.getDeployedRules(self.entityName, "published", {
					callback : function(data){
						self.importAsList = data.list;
					},
					postHook: function(){
						self.getRuleEntityList();
						self.populateTabContent();
					}
				});
			},

			init : function() {
				var self = this;
				$("#titleText").html(self.moduleName);
				self.getImportAsList();
			}
	};

	$(document).ready(function() {
		Import.init();
	});	

})(jQuery);	