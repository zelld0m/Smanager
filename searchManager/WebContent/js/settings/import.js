(function($){

	var Import = {
			moduleName : "Import Rule",
			tabSelected : "",
			entityName : "",
			ruleEntityList : null,
			importTypeList : null,
			ruleStatusMap : new Array(),
			ruleTransferMap: new Object(),
			ruleTargetList: new Array(),
			pageSize : 10,
			defaultText : "Search Rule Info",
			currentPage : 1,
			searchText : "",
			pubDateAsc : true,


			postMsg : function(data, pub) {
				var self = this;
				var msg_ = pub;
				var okmsg = '';

				if(pub == 'all') { // Rules imported and rejected message.
					if(!$.isEmptyObject(data)) {
						var imported = '';
						var rejected = '';
						
						for(key in data) {
							if(data[key] == 'import_success') {
								imported += '\n-' + key;
							} else {
								rejected += '\n-' + key;
							}
						}
						
						okmsg = 'Following rules were successfully imported:';	
						// Imported rules
						okmsg += imported;
						okmsg += '\nFollowing rules were successfully rejected:';
						// Rejected rules
						okmsg += rejected;
					} else {
						okmsg = 'No rules were successfully imported and rejected.';
					}
				} else {
					if(data.length > 0) {
						okmsg = 'Following rules were successfully ' + msg_ + ':';
						
						for(var i=0; i<data.length; i++){	
							okmsg += '\n-'+ data[i];	
						}	
					} else {
						okmsg = 'No rules were successfully ' + msg_ +'.';
					}
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
						if (ctr==max) self.getImportList(1);
					}
				});

				EnumUtilityServiceJS.getImportTypeList(hasPublishRule, {
					callback : function(data){
						self.importTypeList = data;
						ctr++;
					},
					postHook: function(){
						if (ctr==max) self.getImportList(1);
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
				
				$selectedTab.find("div.searchBoxHolder, a#searchBtn").hide();
				$selectedTab.find("div#resultsTopPaging, div#resultsBottomPaging").empty();
				$selectedTab.find("a#downloadIcon").hide();
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

			getSelectedImportAsRefId : function(value){
				var self = this;
				var selectedImportAsRefId = [];
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems(value);
				
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem"+id);
					selectedImportAsRefId.push($selectedTr.find("td#importAs").find("select#importAsSelect > option:selected").val());
				}
				
				return selectedImportAsRefId;
			},
			
			getSelectedImportType : function(value){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems(value);
				
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem"+id);
					selectedItems.push($selectedTr.find("td#type > select#importTypeList > option:selected").text()); 
				}
				
				return selectedItems;
			},

			checkSelectedImportAsName : function(value){
				var self = this;
				var selectedNames = self.getSelectedRuleName(value);

				if(selectedNames == null || selectedNames.length==0)
					return false;

				for(var i=0; i < selectedNames.length; i++){
					if($.isBlank(selectedNames[i])){
						return false;
					}
				}

				return true;
			},

			hasDuplicateImportAsId: function(value){

				var self = this;
				var selectedRuleId = new Array();
				var $selectedTab = $("#" + self.tabSelected);
				var selectedItems = self.getSelectedItems(value);

				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
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

			hasDuplicateImportAsName: function(value) {
				var self = this;
				var selectedRuleName = new Array();
				var $selectedTab = $("#" + self.tabSelected);
				var selectedItems = self.getSelectedItems(value);

				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
					var ruleName = $selectedTr.find("td#importAs #replacement input#newName").val();
					
					if ($.inArray(ruleName.toLowerCase(), selectedRuleName)==-1){
						selectedRuleName.push(ruleName.toLowerCase());
					}else{
						return true;
					}
				}

				return false;
			},

			getSelectedRuleName : function(value){

				var self = this;
				var selectedRuleNames = [];
				var $selectedTab = $("#"+self.tabSelected);
				var selectedItems = self.getSelectedItems(value);
				
				for (var id in selectedItems){
					var $selectedTr = $selectedTab.find("tr#ruleItem"+ id);
					var ruleName = $selectedTr.find("td#importAs").find("input#newName").val();
					selectedRuleNames.push(ruleName);
				}
				
				return selectedRuleNames;
			},
			
			getSelectedItems : function(flag) {
				var self = this;
				var selectedItems = [];
				var $selectedTab = $("#"+self.tabSelected);
				
				if(flag == 'all') {
					$selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:not([readonly]):checked").each(function(index, value){
						selectedItems[$(this).attr("id")] = $(this).attr("name");
					});
				} else {
					$selectedTab.find("tr:not(#ruleItemPattern) td#select > input."+flag+"[type='checkbox']:not([readonly]):checked").each(function(index, value){
						selectedItems[$(this).attr("id")] = $(this).attr("name");
					});
				}
				
				return selectedItems;
			},
			
			getSelectedRefId : function(flag){
				var self = this;
				var selectedRefIds = [];
				var $selectedTab = $("#"+self.tabSelected);
				
				if(flag == 'all') {
					$selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:not([readonly]):checked").each(function(index, value){
						selectedRefIds.push($(this).attr("value"));
					});
				} else {
					$selectedTab.find("tr:not(#ruleItemPattern) td#select > input."+flag+"[type='checkbox']:not([readonly]):checked").each(function(index, value){
						selectedRefIds.push($(this).attr("value"));
					});
				}
				
				return selectedRefIds; 
			},

			getSelectedStatusId : function(value){
				var self = this;
				var selectedStatusId = [];
				var selectedItems = self.getSelectedItems(value);
				
				for (var i in selectedItems){
					selectedStatusId.push(selectedItems[i]); 
				}
				
				return selectedStatusId; 
			},
			
			addFieldValuesPaging : function(selectedTab, curPage, totalItem, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter){
				var self = this;
				var $selectedTab = selectedTab;
				if(totalItem==0){
					$selectedTab.find("div.searchBoxHolder, a#searchBtn").hide();
					$selectedTab.find("div#resultsTopPaging, div#resultsBottomPaging").empty();
					$selectedTab.find("#downloadIcon").hide();
				}else{
					$selectedTab.find("div.searchBoxHolder, a#searchBtn").show();
					$selectedTab.find("#downloadIcon").show();
					$selectedTab.find("#resultsTopPaging, #resultsBottomPaging").paginate({
						currentPage: curPage, 
						pageSize: self.pageSize,
						totalItem: totalItem,
						callbackText: function(itemStart, itemEnd, itemTotal){
							return "Displaying " + itemStart + "-" + itemEnd + " of " + itemTotal + " Items";
						},
						pageLinkCallback: function(e){ self.getImportList(e.data.page, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter); },
						nextLinkCallback: function(e){ self.getImportList(e.data.page+1, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);},
						prevLinkCallback: function(e){ self.getImportList(e.data.page-1, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);},
						firstLinkCallback: function(e){self.getImportList(1);},
						lastLinkCallback: function(e){ self.getImportList(e.data.totalPages, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);}
					});
					
					$selectedTab.find('input#keyword').off().on({
						focusin: function(e){
							if ($.trim($(e.currentTarget).val()).toLowerCase() === $.trim(self.defaultText).toLowerCase())
								$(e.currentTarget).val("");
						},
						focusout: function(e){
							if ($.isBlank($(e.currentTarget).val())) 
								$(e.currentTarget).val(self.defaultText);
						},
						keydown: function(e){
							var code = (e.keyCode ? e.keyCode : e.which);
							var keyword = $.trim($(e.target).val());

							if (code == 13 && keyword.toLowerCase() !== $.trim(self.defaultText).toLowerCase()) 
								self.getImportList(1, keyword);
						}
					}).val(self.defaultText);
					
					$selectedTab.find("a#searchBtn").off().on({
						click: function(e){
							var keyword = $.trim($selectedTab.find('input#keyword').val());
							
							if(keyword.toLowerCase() !== $.trim(self.defaultText).toLowerCase())
								self.getImportList(1, keyword);
						}
					});
					
					$selectedTab.find("img#publishDateSort").off().on({
						click: function(e){
							var $pubDateCheckbox = $selectedTab.find("input#pubDate");
							var inverse = !$pubDateCheckbox.is(":checked");
							self.getImportList(self.currentPage, self.searchText, inverse);
						}
					});
					
					$selectedTab.find("a#downloadIcon").download({
						headerText:"Download " + self.moduleName,
						requestCallback:function(e){
							var params = new Array();
							var url = document.location.pathname + "/xls";
							var urlParams = "";
							var count = 0;
							params["filename"] = e.data.filename;
							params["type"] = e.data.type;
							params["clientTimezone"] = +new Date();
							params["ruleType"] = self.entityName;

							for(var key in params){
								if (count>0) urlParams +='&';
								urlParams += (key + '=' + params[key]);
								count++;
							};

							document.location.href = url + '?' + urlParams;
						}
					});
				}
			},

			// not in used.
			importHandler : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);
				
				$selectedTab.find("a#okBtn, a#rejectBtn").on({
					click: function(evt){
						var comment = $.trim($selectedTab.find("#comment").val());
						
						if(self.getSelectedRefId('all').length==0){
							jAlert("Please select rule.", self.moduleName);
						}else if ($.isBlank(comment)){
							jAlert("Please add comment.", self.moduleName);
						}else if(!isXSSSafe(comment)){
							jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
						}else{
							switch($(evt.currentTarget).attr("id")){
							case "okBtn":
								setTimeout(function() {
									if(self.hasDuplicateImportAsId('all')){	//check if all selected rules have ruleName value
										jAlert("Duplicate selected import as value. Please check selected rules to import.", self.moduleName);
									}else if(self.hasDuplicateImportAsName('all')){	//check if all selected rules have ruleName value
										jAlert("Duplicate selected import as new name. Please check selected rules to import.", self.moduleName);
									}else if(!self.checkSelectedImportAsName('all')){	//check if all selected rules have ruleName value
										jAlert("Import As name is required. Please check selected rules to import.", self.moduleName);
									}else{
										RuleTransferServiceJS.importRules(self.entityName, self.getSelectedRefId(), comment, self.getSelectedImportType(), self.getSelectedImportAsRefId(), self.getSelectedRuleName(), {
											callback: function(data) {									
												self.postMsg(data, 'imported');	
												self.getImportList(1);	
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
										self.postMsg(data, 'rejected');	
										self.getImportList(1);
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
			
			submitHandler : function(){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);
				
				$selectedTab.find("a#sbmtBtn").on({
					click: function(evt){
						var comment = $.trim($selectedTab.find("#comment").val());
						
						if(self.getSelectedRefId('all').length==0){
							jAlert("Please select rule.", self.moduleName);
						}else if($.isBlank(comment)){
							jAlert("Please add comment.", self.moduleName);
						}else if(!isXSSSafe(comment)){
							jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
						}else{
							var importedItems = [];
							var rejectedItems = [];
							var validImport = true;
							
							$selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:not([readonly]):checked").each(function(index, value){
								switch($(value).attr('class')) {
								case 'import':
									importedItems.push($(value).attr('name'));
									break;
								case 'reject':
									rejectedItems.push($(value).attr('name'));
									break;
								}
							});
							
							if(importedItems.length > 0) {
								if(self.hasDuplicateImportAsId('import')){	//check if all selected rules have ruleName value
									jAlert("Duplicate selected import as value. Please check selected rules to import.", self.moduleName);
									validImport = false;
								}else if(self.hasDuplicateImportAsName('import')){	//check if all selected rules have ruleName value
									jAlert("Duplicate selected import as new name. Please check selected rules to import.", self.moduleName);
									validImport = false;
								}else if(!self.checkSelectedImportAsName('import')){	//check if all selected rules have ruleName value
									jAlert("Import As name is required. Please check selected rules to import.", self.moduleName);
									validImport = false;
								}
							}
							
							if(validImport && importedItems.length > 0 && rejectedItems.length <= 0) {
								RuleTransferServiceJS.importRules(self.entityName, self.getSelectedRefId('import'), comment, self.getSelectedImportType('import'), self.getSelectedImportAsRefId('import'), self.getSelectedRuleName('import'), {
									callback: function(data){									
										self.postMsg(data, 'imported');	
										self.getImportList();	
									},
									preHook:function() { 
										self.prepareTabContent(); 
									}	
								});
							} else if(validImport && importedItems.length <=0 && rejectedItems.length > 0) {
								RuleTransferServiceJS.unimportRules(self.entityName, self.getSelectedRefId('reject'), comment, self.getSelectedStatusId('reject'), {
									callback: function(data){
										self.postMsg(data, 'rejected');
										self.getImportList();
									},
									preHook:function(){
										self.prepareTabContent();
									}
								});
							} else if(validImport) {
								RuleTransferServiceJS.importRejectRules(self.entityName, self.getSelectedRefId('import'), comment, self.getSelectedImportType('import'), self.getSelectedImportAsRefId('import'), self.getSelectedRuleName('import'),
										self.getSelectedRefId('reject'), self.getSelectedStatusId('reject'), {
									callback: function(data){									
										self.postMsg(data, 'all');	
										self.getImportList();
									},
									preHook:function() { 
										self.prepareTabContent(); 
									}
								});
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
				template += '		<p>';
				template += '	</div>';
				template += '	<div id="btnHolder" align="right" class="padR15 marT10" style="display:none">';
				template += '		<a id="setImportBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template += '			<div class="buttons fontBold">Set For Import</div>';
				template += '		</a>';
				template += '		<a id="setRejectBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template += '			<div class="buttons fontBold">Set For Reject</div>';
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
				template += '		<label class="w110 floatL marL20 fbold">Rule Info:</label>';
				template += '		<label class="wAuto floatL" id="ruleInfo" style="margin-left: 90px;"></label>';
				template += '		<div class="clearB"></div>';
				template += '		<label class="w110 floatL marL20 fbold">Import As:</label>';
				template += '		<div id="importAs" class="wAuto floatL" style="margin-left: 90px;"></div>';
				template += '		<div class="clearB"></div>';
				template += '	</div>';

				return template;
			},

			getRuleTransferMap: function(curPage, keywordFilter, ruleFilter, exportDateOrder, publishDateOrder){
				var self = this;
				//TODO: dynamic origin and target
				RuleTransferServiceJS.getExportMapList("pcmall", $.makeArray(), self.entityName, {
					callback: function(exportMapList){
						if(exportMapList){
							for(var index in exportMapList){
								self.ruleTransferMap[exportMapList[index]["ruleIdOrigin"]] = exportMapList[index];
								self.ruleTargetList[exportMapList[index]["ruleIdTarget"]] = exportMapList[index]["ruleIdTarget"];
							}
						}
					},
					postHook: function(){
						self.getAllRulesToImport(curPage, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);
					}
				});
			},

			getAllRulesToImport: function(curPage, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter){
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);

				RuleTransferServiceJS.getRulesToImport(self.entityName, keywordFilter, curPage, self.pageSize, ruleFilter, exportDateOrder, publishDateOrder, {
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
								
								$tr.find("td#select > input[type='checkbox']").attr({"id": $.formatAsId(ruleId), "value": ruleId, "name": rule["ruleName"]});
								
								$tr.find("td#ruleOption > img.previewIcon").attr("id", $.formatAsId(ruleId));

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
													self.getImportList(1);
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
												},
												
												checkUncheckCheckboxCallback : function(base, ruleId, pub) {
													switch(pub) {
													case 'import':
														$("#"+$.formatAsId(ruleId)+".import").attr('checked', true);
														$("#"+$.formatAsId(ruleId)+".reject").attr('checked', false);
														break;
													case 'reject':
														$("#"+$.formatAsId(ruleId)+".import").attr('checked', false);
														$("#"+$.formatAsId(ruleId)+".reject").attr('checked', true);
														break;
													}
												},
												changeImportTypeCallback : function(base, ruleId, opt) {
													$("#ruleItem"+$.formatAsId(ruleId)+" #type select").val(opt);
												},
												changeImportAsCallback : function(base, ruleId, importAs, ruleName, newName) {
													if(importAs != 0 || newName.length>0) {
														$("#ruleItem"+$.formatAsId(ruleId)+" #importAs select").val(importAs).change();
														$("#ruleItem"+$.formatAsId(ruleId)+" #importAs #replacement #newName").val(newName);
													}
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
									ruleTargetList: self.ruleTargetList,
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
									}
								});

								$tr.appendTo($table);
							}

							$selectedTab.find("div#ruleCount").html(totalSize + (totalSize == 1 ? " Rule" : " Rules"));
							$(ruleDiv).scroll();

							// Alternate row style
							$selectedTab.find("tr:not(#ruleItemPattern):even").addClass("alt");

							self.submitHandler();
							self.toggleCheckbox();
						}else{
							$selectedTab.find("table#rule").append('<tr><td class="txtAC" colspan="5">No pending rules found</td></tr>');
							$selectedTab.find('div#actionBtn').hide();
						}
						
						self.populateFilters($selectedTab);
						self.addFieldValuesPaging($selectedTab, curPage, totalSize, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);

					},
					preHook:function(){ 
						self.prepareTabContent();
					},
					postHook:function(){ 
						self.cleanUpTabContent();
					}
				});
			},
			

			populateFilters : function($selectedTab){
				var self = this;
				var sortPath = "";
								
				if(self.pubDateAsc == undefined){
					sortPath = GLOBAL_contextPath + '/images/tablesorter/bg.gif';
					$selectedTab.find('input#pubDate').prop("checked", true);
				}else if(self.pubDateAsc){
					sortPath = GLOBAL_contextPath + '/images/tablesorter/asc.gif';
					$selectedTab.find('input#pubDate').prop("checked", true);
				}else{
					sortPath = GLOBAL_contextPath + '/images/tablesorter/desc.gif';
					$selectedTab.find('input#pubDate').prop("checked", false);
				}
				
				$selectedTab.find('img#publishDateSort').attr('src', sortPath);
				$selectedTab.find('input#keyword').val(self.searchText);
			},

			getImportList : function(curPage, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter){
				var self = this;
				self.currentPage = curPage;
				self.searchText = keywordFilter;
				self.pubDateAsc = publishDateOrder;

				if(GLOBAL_store==="pcmallcap"){
					self.getRuleTransferMap(curPage, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);
				}else{
					self.getAllRulesToImport(curPage, keywordFilter, publishDateOrder, exportDateOrder, ruleFilter);
				}
			},
			
			toggleCheckbox : function() {
				var self = this;
				var $selectedTab = $("#"+self.tabSelected);
				
				$selectedTab.find(".import, .reject").on({
					click: function(evt) {
						var id = $(this).attr('id');
						
						switch($(this).attr('class')) {
						case 'import':
							if($(this).attr('checked') == 'checked') {
								$selectedTab.find("#" + id + ".reject").attr('checked', false);
							}
							break;
						case 'reject':
							if($(this).attr('checked') == 'checked') {
								$selectedTab.find("#" + id + ".import").attr('checked', false);
							}
							break;
						}
					}
				});
			},
			
			init : function() {
				var self = this;
				$("#titleText").html(self.moduleName);
				self.getRuleEntityList();
				self.populateTabContent();
			}
	};

	$(document).ready(function() {
		Import.init();
	});
	
})(jQuery);
