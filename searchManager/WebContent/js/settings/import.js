(function($){

	var Import = {
			moduleName : "Import Rule",	
			tabSelected : "",
			entityName : "",
			ruleEntityList : null,
			importTypeList : null,
			ruleStatusMap : new Array(),
			ruleTransferMap: null,

			postMsg : function(data,pub){
				var self = this;
				var msg_ = pub ? 'imported:' : 'rejected:';
				var okmsg = '';	

				if(data.length > 0){
					okmsg = 'Following rules were successfully ' + msg_;	

					for(var i=0; i<data.length; i++){	
						okmsg += '\n-'+ data[i];	
					}
				}
				else{
					okmsg = 'No rules were successfully ' + msg_ +'.';
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
							self.initVariables();
						}
					}
				});
			},

			initVariables: function(){
				var ctr = 0, max = 2, self = this;
				DeploymentServiceJS.getAllRuleStatus(self.entityName, {
					callback: function(rs){
						self.ruleStatusMap[self.entityName] = rs.list;
						ctr++;
					}, 
					postHook: function(){
						if (ctr==max) self.getImportList();
					}
				});

				EnumUtilityServiceJS.getImportTypeList(hasPublishRule, {
					callback : function(data){
						self.importTypeList = data;
						ctr++;
					},
					postHook: function(){
						if (ctr==max) self.getImportList();
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
				$selectedTab.find("div#ruleCount").html("");
			},

			cleanUpTabContent:function(){
				$('div.circlePreloader').remove();
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
					var $selectedTr = $selectedTab.find("tr#ruleItem"+$.formatAsId(id));
					selectedImportAsRefId.push($selectedTr.find("td#importAs").find("select#importAsSelect > option:selected").val()); 
				}
				return selectedImportAsRefId;
			}, 

			getSelectedImportType : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems();
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem"+$.formatAsId(id));
					selectedItems.push($selectedTr.find("td#type > select#importTypeList > option:selected").text()); 
				}
				return selectedItems;
			}, 

			checkSelectedImportAsName : function(){
				var self = this;
				var selectedNames = self.getSelectedRuleName();

				if(selectedNames == null || selectedNames.length==0)
					return false;

				for(var i=0; i < selectedNames.length; i++){
					if($.isBlank(selectedNames[i])){
						return false;
					}
				}

				return true;
			},

			hasDuplicateImportAsId: function(){
				var self = this;
				var selectedRuleId = new Array();
				var $selectedTab = $("#" + self.tabSelected);
				var selectedItems = self.getSelectedItems();

				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem" + $.formatAsId(id));
					var $importAsSelect = $selectedTr.find("td#importAs").find("select#importAsSelect > option:selected");
					var ruleId = $importAsSelect.val();
					if ($.inArray(ruleId, selectedRuleId)==-1){
						if(ruleId!=="0"){
							selectedRuleId.push(ruleId);
						};
					}else{
						return true;
					}
				}

				return false;
			},

			hasDuplicateImportAsName: function(){
				var self = this;
				var selectedRuleName = new Array();
				var $selectedTab = $("#" + self.tabSelected);
				var selectedItems = self.getSelectedItems();

				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem" + $.formatAsId(id));
					var ruleName = $selectedTr.find("td#importAs").find("input#newName").val();
					if ($.inArray(ruleName.toLowerCase(), selectedRuleName)==-1){
						selectedRuleName.push(ruleName.toLowerCase());
					}else{
						return true;
					}
				}

				return false;
			},

			getSelectedRuleName : function(){
				var self = this;
				var selectedRuleNames = [];
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems();
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem"+$.formatAsId(id));
					var ruleName = $selectedTr.find("td#importAs").find("input#newName").val();
					selectedRuleNames.push(ruleName);
				}
				return selectedRuleNames;
			},

			getSelectedItems : function(){
				var self = this;
				var selectedItems = [];
				var $selectedTab = $("#"+self.tabSelected);
				$selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:not([readonly]):checked").each(function(index, value){
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

			/*checkSelectHandler : function(){
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
			},*/

			/*checkSelectAllHandler : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);

				$selectedTab.find("th#selectAll > input[type='checkbox']").on({
					click: function(evt){
						var selectAll = $(this).is(":checked");
						$selectedTab.find("tr:not(#ruleItemPattern) > td#select > input[type='checkbox']:not([readonly])").attr("checked", selectAll);
					}
				});
			},*/

			downloadHandler: function(){
				var self = this;
				$("a#downloadIcon").download({
					headerText:"Download " + self.moduleName,
					hasRuleEntityOption: true,
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;
						params["filename"] = e.data.filename;
						params["type"] = e.data.type;
						params["clientTimezone"] = +new Date();
						params["ruleType"] = e.data.ruletype;

						for(var key in params){
							if (count>0) urlParams +='&';
							urlParams += (key + '=' + params[key]);
							count++;
						};

						document.location.href = url + '?' + urlParams;
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
							jAlert("Please select rule.", self.moduleName);
						}else if ($.isBlank(comment)){
							jAlert("Please add comment.", self.moduleName);
						}else if(!isXSSSafe(comment)){
							jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
						}else{
							switch($(evt.currentTarget).attr("id")){
							case "okBtn":
								setTimeout(function() {
									if(self.hasDuplicateImportAsId()){	//check if all selected rules have ruleName value
										jAlert("Duplicate selected import as value. Please check selected rules to import.", self.moduleName);
									}else if(self.hasDuplicateImportAsName()){	//check if all selected rules have ruleName value
										jAlert("Duplicate selected import as new name. Please check selected rules to import.", self.moduleName);
									}else if(!self.checkSelectedImportAsName()){	//check if all selected rules have ruleName value
										jAlert("Import As name is required. Please check selected rules to import.", self.moduleName);
									}else{
										RuleTransferServiceJS.importRules(self.entityName, self.getSelectedRefId(), comment, self.getSelectedImportType(), self.getSelectedImportAsRefId(), self.getSelectedRuleName(), {
											callback: function(data){									
												self.postMsg(data,true);	
												self.getImportList();	
											},
											preHook:function(){ 
												self.prepareTabContent(); 
											}	
										});
									}
								}, 500 );
								break;
							case "rejectBtn": 
								RuleTransferServiceJS.unimportRules(self.entityName, self.getSelectedRefId(), comment, self.getSelectedStatusId(), {
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
				template += '	<div id="btnHolder" align="right" class="padR15 marT10" style="display:none">';
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

				template += '     <div class="rulePreview w590 marB20">';
				template += '           <div class="alert marB10">';
				template += '                 Selected rule below will be overwritten when import button is clicked.';
				template += '                 It is advisable to review both rules as this action cannot be undone.';
				template += '           </div>';
				template += '           <label class="w110 floatL marL20 fbold">Rule Info:</label>';
				template += '           <label class="wAuto floatL" id="ruleInfo" style="margin-left: 90px;"></label>';
				template += '           <div class="clearB"></div>';
				template += '           <label class="w110 floatL marL20 fbold">Import As:</label>';
				template += '           <div id="importAs" class="wAuto floatL" style="margin-left: 90px;"></div>';
				template += '           <div class="clearB"></div>';
				template += '     </div>';

				return template;
			},

			getRuleTransferMap: function(){
				var self = this;
				//TODO: dynamic origin and target
				RuleTransferServiceJS.getMapRuleTransferMap("pcmall", $.makeArray(), self.entityName, {
					callback: function(ruleTransferMap){
						self.ruleTransferMap = ruleTransferMap;
					},
					postHook: function(){
						self.getAllRulesToImport();
					}
				});
			},

			getAllRulesToImport: function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);

				RuleTransferServiceJS.getAllRulesToImport(self.entityName, {
					callback:function(data){
						var list = data;
						var totalSize = (data) ? data.length : 0;

						$selectedTab.html($("div#tabContentTemplate").html());
						var ruleDiv = $selectedTab.find("#rule").parent()[0];

						if (totalSize>0){
							// Populate table row
							for(var i=0; i < totalSize; i++){
								var rule = list[i];
								var ruleId = rule["ruleId"];
								var ruleName = rule["ruleName"];
								var dbRuleId = "";

								switch(self.entityName.toLowerCase()){
								case "elevate":
								case "exclude":
								case "demote":
								case "facetsort":
									dbRuleId = ruleId;
									break;
								default: break;
								}

								var $table = $selectedTab.find("table#rule");
								var $tr = $selectedTab.find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(ruleId)).show();
								var lastPublishedDate = (rule["ruleStatus"] && $.isNotBlank(rule["ruleStatus"]["lastPublishedDate"]))? rule["ruleStatus"]["lastPublishedDate"].toUTCString(): "";

								$tr.find("td#select > input[type='checkbox']").attr({"id":ruleId, "name": rule["ruleName"]});

								$tr.find("td#ruleOption > img.previewIcon").attr("id", ruleId);

								if (rule["updateStatus"]!=="DELETE"){
									$tr.find("td#ruleOption > img.previewIcon")
									.xmlpreview({
										transferType: "import",
										ruleType: self.entityName,
										ruleId: ruleId,
										ruleName: ruleName,
										ruleXml: rule,
										rule: rule,
										ruleStatusList: self.ruleStatusMap==null? null: self.ruleStatusMap[self.entityName],
												ruleTransferMap: self.ruleTransferMap,
												enablePreTemplate: true,
												enablePostTemplate: true,
												leftPanelSourceData: "xml",
												enableRightPanel: true,
												rightPanelSourceData: "database",
												dbRuleId: dbRuleId,
												postTemplate: self.getPostTemplate(),
												preTemplate: self.getPreTemplate(rule["importType"]),
												rightPanelTemplate: self.getRightPanelTemplate(),
												postButtonClick: function(){
													self.getImportList();
												},
												itemImportAsListCallback: function(base, contentHolder, sourceData){
													DeploymentServiceJS.getDeployedRules(self.entityName, "published", {
														callback : function(data){
															base.populateImportAsList(data, contentHolder, sourceData);
														}
													});
												},
												itemImportTypeListCallback: function(base, contentHolder){
													base.populateImportTypeList(self.importTypeList, contentHolder);
												},
												itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap){
													if (self.entityName === "elevate"){
														ElevateServiceJS.isRequireForceAdd(ruleName, memberIds, {
															callback:function(data){
																base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
															},
															preHook: function(){
																base.prepareForceAddStatus(contentHolder);
															}
														});
													}
												},
												itemXmlForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberConditions, memberIdToItemMap){
													if (self.entityName === "elevate"){
														ElevateServiceJS.isItemRequireForceAdd(ruleName, memberIds, memberConditions, {
															callback:function(data){
																base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
															},
															preHook: function(){
																base.prepareForceAddStatus(contentHolder);
															}
														});
													}
												}
									});
								}else{
									$tr.find("td#ruleOption > img.previewIcon").hide();
								}

								if(ruleId.toLowerCase() !== rule["ruleName"].toLowerCase())	
									$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleId"]);

								$tr.find("td#ruleRefId > p#ruleName").html(list[i]["ruleName"]);

								$tr.find("td#publishDate > p#publishDate").html(lastPublishedDate);

								//import type
								var $importTypeSelect = $tr.find("td#type > select#importTypeList");

								if(self.importTypeList){
									for (var importType in self.importTypeList){
										$importTypeSelect.append($("<option>", {value: importType}).text(self.importTypeList[importType]));
									}
								}

								//import as
								$tr.find("td#importAs").importas({
									container: ruleDiv,
									rule: list[i],
									ruleStatusList: self.ruleStatusMap[self.entityName],
									ruleTransferMap: self.ruleTransferMap,
									setRuleStatusListCallback: function(base, list){
										self.ruleStatusMap[self.entityName]= list;
									},
									targetRuleStatusCallback: function(item, r, rs){
										var locked = rs!=undefined && (rs["approvalStatus"]==="PENDING" || rs["approvalStatus"]==="APPROVED");

										item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].selectItem:eq(0)').prop({
											disabled: locked,
											readonly: locked
										});

										if(locked){
											item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].selectItem:eq(0)').prop({checked:false});
										}
									}/*,
									selectedOptionChanged: function(ruleId){
										if ($('input[type="checkbox", class="selectItem"]:not([readonly])').length <= 1){
											$selectedTab.find('th#selectAll > input[type="checkbox"]').hide();
										}else{
											$selectedTab.find('th#selectAll > input[type="checkbox"]').show();
										}
									}*/
								});

								$tr.appendTo($table);
							}

							$selectedTab.find("div#ruleCount").html(totalSize + (totalSize == 1 ? " Rule" : " Rules"));
							$(ruleDiv).scroll();

							// Alternate row style
							$selectedTab.find("tr:not(#ruleItemPattern):even").addClass("alt");

							//self.checkSelectHandler();
							//self.checkSelectAllHandler();
							self.importHandler();

						}else{
							$selectedTab.find("table#rule").append('<tr><td class="txtAC" colspan="5">No pending rules found</td></tr>');
							$selectedTab.find('div#actionBtn').hide();
						}

						/*if(totalSize <= 1){
							$selectedTab.find('th#selectAll > input[type="checkbox"]').hide();
						}else{
							$selectedTab.find('th#selectAll > input[type="checkbox"]').show();
						}*/
					},
					preHook:function(){ 
						self.prepareTabContent();
					},
					postHook:function(){ 
						self.cleanUpTabContent();
					}
				});			
			},

			getImportList : function(){
				var self = this;

				if(GLOBAL_store==="pcmallcap"){
					self.getRuleTransferMap();
				}else{
					self.getAllRulesToImport();
				}
			},

			init : function() {
				var self = this;
				$("#titleText").html(self.moduleName);
				self.downloadHandler();
				self.getRuleEntityList();
				self.populateTabContent();
			}
	};

	$(document).ready(function() {
		Import.init();
	});	

})(jQuery);	
