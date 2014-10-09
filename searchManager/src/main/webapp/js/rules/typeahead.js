(function($){

	var Typeahead = {
			moduleName: "Typeahead",
			maxHighlightedFacet: 5,

			initialNoOfItems: 100,
			itemsPerScroll: 100,
			startIndex: 0,
			reportType: {basic: 1, withStats: 2, custom: 3},
			latestFile: null,

			tabSelectedId: 1,
			tabSelectedName: "",
			keyword: "",
			fq: "",
			typeaheadListContainerSelector: "#listContainer",
			preloaderSelector: "div#preloader",
			typeaheadPanelSelector: "#typeaheadPanel",
			editPanelSelector: "#editPanel",
			rulePage: 1,
			rulePageSize: 15,

			removeFacetGroupItemConfirmText: "Delete facet value?",
			keywordIconPath: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_keyword.png'/>",
			templateIconPath:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_template.png'/>",
			saveIconPath:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_disk.png'/>",
			rectLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-rect.gif'/>",
			roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
			magniIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_magniGlass13.png'/>",
			lockIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_lock.png'/>",
			searchReloadRate: 1000,
			selectedRuleList: new Object(),
			
			prepareTypeahead : function(){
				var self = this;
				clearAllQtip();
				
				self.$preloader.show();
				self.$typeaheadPanel.find("#submitForApproval, #noSelected").hide();
				self.$typeaheadPanel.find("#titleHeader").empty();
				self.$typeaheadPanel.find("#ruleTypeIcon").html("");
			},

			showTypeahead : function(){
				var self = this;
				self.prepareTypeahead();

				if(self.selectedRule==null){
					self.$preloader.hide();
					self.$typeaheadPanel.find("#noSelected").show();
					self.$typeaheadPanel.find("#titleText").html(self.moduleName);
					return;
				}

				self.$typeaheadPanel.find("#submitForApproval").rulestatusbar({
					moduleName: self.moduleName,
					rule: self.selectedRule,
					ruleType: "Typeahead",
					enableVersion: true,
					authorizeRuleBackup: allowModify,
					authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
					postRestoreCallback: function(base, rule){
						base.api.destroy();
						TypeaheadRuleServiceJS.getByRuleId(rule["ruleId"],{
							callback: function(data){
								self.setTypeahead(data['data']);
							},
							preHook: function(){
								self.prepareTypeahead();
							}
						});
					},
					afterSubmitForApprovalRequest:function(ruleStatus){
						self.showTypeahead();
					},
					beforeRuleStatusRequest: function(){
						self.prepareTypeahead();

					},
					afterRuleStatusRequest: function(ruleStatus){
						self.$typeaheadPanel.find("#titleText").html(self.moduleName + " for ");
						self.$typeaheadPanel.find("#titleHeader").text(self.selectedRule["ruleName"]);
						self.$typeaheadPanel.find("#readableString").html(self.selectedRule["readableString"]);
						self.$preloader.hide();
						self.$typeaheadPanel.find("#submitForApproval").show();
						self.$editPanel.show();
					}
				});
			},
			populateSortOrderList : function(contentHolder, selectedOrder){
				var self = this;
				contentHolder.find("option").remove();

				if($.isNotBlank(selectedOrder)){
					$.each(self.sortOrderList, function(sortName, sortDisplayText) { 
						contentHolder.append($("<option>", {value: sortDisplayText, selected: sortName===selectedOrder}).text(sortDisplayText));
					});
				}
				else{
					$.each(self.sortOrderList, function(sortName, sortDisplayText) { 
						contentHolder.append($("<option>", {value: sortDisplayText}).text(sortDisplayText));
					});
				}
			},			
			setTypeahead : function(rule){
				var self = this;
				self.selectedRule = rule;
				self.$typeaheadPanel.find('input.searchTextInput').val(rule.ruleName);
				self.$typeaheadPanel.find('#priorityEdit').val(rule.priority);
				self.$typeaheadPanel.find('#disabledEdit').prop('checked', rule.disabled);
				self.$typeaheadPanel.find('a.searchButtonList').show();
				self.$editPanel.hide();
				self.startIndex = 0;
//				self.loadRuleList(0, self.rulePage);
//				self.keyword = "";
//				self.fq = "";
				self.$typeaheadPanel.find('input.searchTextInput, a.searchButton').hide();
				self.$typeaheadList.hide();
//				$('div#itemList, div#itemHeaderMain, div.listSearchDiv').hide();

				self.$typeaheadPanel.find('#searchResult, #category, #brand').find(':not(#docs, #categoryDocs, #brandDocs)').remove();
				self.$editPanel.find('#typeaheadTable').find('tr:gt(1)').remove();
				
				TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, rule.ruleName, 0, 0, 1, 1, {
					callback: function(response) {
						var data = response["data"];
						var list = data.list;

						if(list.length > 0) {
							for(var i=0; i < list.length; i++) {
								var countDiv = '';
								if(i==0){
									countDiv = '<span id="count"></span>';
								}
								
								var html = '<div class="fsize15"><strong>'+list[i].ruleName+countDiv+'</strong></div>';
								
								if(i == 0) {
									self.$typeaheadPanel.find('#category').prepend(html);
								} else {
									if(i < GLOBAL_storeKeywordMaxCategory) {
										var keywordsRow = '<tr><td><div class="marL10 fsize11">'+html+'</div></td><td></td><td></td></tr>';
										self.$editPanel.find('#typeaheadTable tr:last').after(keywordsRow);
									}
								}
							}
							self.loadTypeaheadSolrDetails(list[0].ruleName);
						}
					}
				});

				self.showTypeahead();
			},
			loadTypeaheadSolrDetails: function(keyword) {
				var params = GLOBAL_typeaheadSolrParams;
				var self = this;
				
				self.typeaheadManager.store.addByValue('q', $.trim(keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
				self.typeaheadManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
				self.typeaheadManager.store.addByValue('fl', 'Name,ImagePath_2,EDP'); 
				self.typeaheadManager.store.addByValue('facet', 'true');
				self.typeaheadManager.store.addByValue('facet.field', 'Manufacturer');
				self.typeaheadManager.store.addByValue('facet.mincount', 1);
				
				if(GLOBAL_storeFacetTemplateType === 'CNET') {
					self.typeaheadManager.store.addByValue('facet.field', GLOBAL_storeFacetTemplate);
				} else {
					self.typeaheadCategoryManager.store.addByValue('facet.field', 'Category');
				}
				
				self.typeaheadManager.store.addByValue('facet.mincount', 1);
				self.typeaheadManager.store.addByValue('divCount', 'countDiv');
				self.typeaheadManager.countDiv = $('#category').find('span#count');
				self.typeaheadManager.store.addByValue('storeAlias', GLOBAL_storeId);

				for(name in params) {
					self.typeaheadManager.store.addByValue(name, params[name]);
				}
				self.typeaheadManager.doRequest(0);
			},
			resetTypeahead : function() {
				var self = this;
				self.selectedRule = null;
				self.$typeaheadPanel.find('input.searchTextInput').val('');
				self.$typeaheadPanel.find('span#titleHeader').html('');
				self.$typeaheadPanel.find('span#titleText').html(self.moduleName);
			},
			resetRuleListTable: function() {
				var self = this;
				$('#updateDialog').dialog('destroy').remove();
				self.$typeaheadList.empty();
				self.$typeaheadPanel.find("div#fieldsBottomPaging, , div#fieldsTopPaging").empty();
			},
			setCurrentRuleList: function(list) {
				var self = this;
				var newMap = new Object();

				for(var i = 0; i<list.length; i++) {
					newMap[list[i].ruleId] = list[i];
				}

				self.currentRuleMap = newMap;
			},
			loadRuleList: function(matchType, page, functionHook) {
				var self = this;
				var searchText = self.$typeaheadPanel.find('input.searchTextInput').val();
				self.$typeaheadPanel.find('input.searchTextInput, a.searchButton').show();

				self.$typeaheadPanel.find('a.searchButtonList').hide();
				self.$typeaheadPanel.find("#submitForApproval").html('');
				self.$editPanel.hide();
				
				self.resetRuleListTable();
				TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, searchText, matchType, 1, page, self.rulePageSize, {
					callback: function(response){
						var data = response["data"];
						var list = data.list;

						self.setCurrentRuleList(list);
						self.$typeaheadList.html(self.getListTemplate());
						var $divList = self.$typeaheadList.find("div#itemList");
						$divList.find("div.items:not(#itemPattern1, #itemPattern2, #itemPattern3)").remove();
						if (list.length > 0){
							self.resetHeader();
							self.loadTypeaheadList($divList, list, self.startIndex, self.initialNoOfItems);
							self.initializeTypeaheadAction();
						}else{
							$empty = '<div id="empty" class="items txtAC borderB">File selected has no records to display</div>';
							$divList.append($empty);
							$("div#countSec").hide();
						}


						self.$typeaheadList.find("div#fieldsBottomPaging, div#fieldsTopPaging").paginate({
							currentPage: page, 
							pageSize: self.rulePageSize,
							totalItem: data.totalSize,
							type: 'short',
							pageStyle: 'style2',
							callbackText: function(itemStart, itemEnd, itemTotal){
								return itemStart + "-" + itemEnd + " of " + itemTotal;
							},
							pageLinkCallback: function(e){ self.loadRuleList(matchType, e.data.page); },
							nextLinkCallback: function(e){ self.loadRuleList(matchType, e.data.page+1);},
							prevLinkCallback: function(e){ self.loadRuleList(matchType, e.data.page-1);},
							firstLinkCallback: function(e){self.loadRuleList(matchType, 1);},
							lastLinkCallback: function(e){ self.loadRuleList(matchType, e.data.totalPages);}
						});
					},
					preHook:function(){
						self.searchTriggered = true;
						self.$preloader.show();
					},
					postHook:function(){
						self.$preloader.hide();
						self.$typeaheadList.show();
						self.searchTriggered = false;
						if(functionHook)
							functionHook();
					}
				});
			},
			loadSplunkData: function() {
				var self = this;
				self.$typeaheadList.html(self.getListTemplate());
				TopKeywordServiceJS.getFileList({
					callback: function(files){
						self.latestFile = files[0];
						TopKeywordServiceJS.getFileContents(self.latestFile, {
							callback: function(data){
								var list = data.list;
								var $divList = self.$typeaheadList.find("div#itemList");
								$divList.find("div.items:not(#itemPattern1, #itemPattern2, #itemPattern3)").remove();
								if (list.length > 0){
									self.resetHeader();
									self.loadItems($divList, list, self.startIndex, self.initialNoOfItems);
									self.startIndex = self.initialNoOfItems;
									$divList.off().on({
										scroll: function(e){
											if(list.length > self.startIndex){
												if ($divList[0].scrollTop == $divList[0].scrollHeight - $divList[0].clientHeight) {
													self.loadItems($divList, list, self.startIndex, self.itemsPerScroll);
													self.startIndex = self.startIndex + self.itemsPerScroll;
												}
											}
										}
									},{list: list});

									$("#keywordCount").html(data.totalSize == 1 ? "1 Keyword" : data.totalSize + " Keywords");
									$("div#countSec").show();
								}else{
									$empty = '<div id="empty" class="items txtAC borderB">File selected has no records to display</div>';
									$divList.append($empty);
									$("div#countSec").hide();
								}
							},
							preHook:function(){
								self.$preloader.show();
							},
							postHook:function(){
								self.$preloader.hide();
							}
						});
					}}
				);
			},
			updateTypeaheadRule: function($divRow) {
				var self = this;
				var typeaheadRule = {ruleId : $divRow.find('.ruleId').val(), sortOrder : $divRow.find('.sortOrder').val(), storeId: GLOBAL_storeId, disabled: $divRow.find('.ruleVisibility')};
				TypeaheadRuleServiceJS.updateRule(typeaheadRule, {
					callback: function(response){
						if(response && response.success != null)
							jAlert('Rule successfuly saved.', self.moduleName);
						else
							jAlert('Rule was not successfuly saved.', self.moduleName);
					},
					preHook:function(){
						$divRow.find("a.toggle").html(self.rectLoader);
					},
					postHook:function(){
						$divRow.find("a.toggle").html(self.saveIconPath);
					},
					errorHandler:function(){
						jAlert('Rule was not successfuly saved.', self.moduleName);
					}
				});
			},
			loadTypeaheadList: function($divList, list, start, noOfItems, type){
				var listLen = list.length;
				var patternId;
				var self = this;
				patternId = "div#itemPattern2";

				for (var i=start; i < start + noOfItems ; i++){
					if(i == listLen)
						break;
					var rule = list[i];
					var $divItem = $divList.find(patternId).clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));

					$divItem.find("label.keyword").html(rule["ruleName"]);
					$divItem.find("label.keyword").html('<a href="javascript:void(0);" class="keywordLink">'+rule["ruleName"]+'</a>');

					$divItem.find('a.keywordLink').off().on({
						click : function(e) {
							self.setTypeahead(e.data.rule);
						}
					}, {rule: rule});
					$divItem.find("label.count").html('<input type="hidden" class="sortOrder" size="3" value="'+rule['priority']+'"/><input type="hidden" class="ruleId" value="'+rule["ruleId"]+'"/><input type="hidden" class="disabled" value="'+rule["disabled"]+'"/>'+rule['priority']);
					//$divItem.find("a.toggle").html(self.saveIconPath);
					$divItem.find("a.toggle").off().on({click : function() {
						self.updateTypeaheadRule($(this).parent().parent());
					}});
					$divItem.show();
					$divList.append($divItem);
				}

				$divList.find("div.items").removeClass("alt");
				$divList.find("div.items:even").addClass("alt");

				self.checkRuleStatus($divList);
			},
			checkRuleStatus: function($divList) {
				var self = this;

				$divList.find('input.ruleId').each(function() {
					var $element = $(this);
					var $row = $element.parent().parent();
					var $checkboxDiv = $row.find('label.iter');
					var $statusDiv = $row.find('label.status');
					var $keywordDiv = $row.find('label.keyword');

					DeploymentServiceJS.getRuleStatus(GLOBAL_storeId, self.moduleName, $element.val(), {
						callback: function(ruleStatus) {
							if(ruleStatus.locked) {
								$checkboxDiv.html(self.lockIcon);
							} else {
								$checkboxDiv.html('<input '+(self.selectedRuleList[$element.val()] != null ? 'CHECKED' : '')+' type="checkbox" id="'+$element.val()+'" class="ruleVisibility" value="'+$element.val()+'"/>');
								self.bindCheckboxAction($checkboxDiv);
							}
							
							$statusDiv.html(getRuleNameSubTextStatus(ruleStatus));

						},
						preHook: function() {
							$checkboxDiv.html(self.rectLoader);
						},
						postHook: function() {

						}
					});
				});
			},
			bindCheckboxAction: function($checkboxContainerElement) {
				var self = this;
				var $checkbox = $checkboxContainerElement.find(':checkbox');
				$checkbox.on("click", function() {
					var checkbox = $(this);
					var $row = $checkboxContainerElement.parent();
					
					if(checkbox.is(':checked')) {
						
						if(Object.keys(self.selectedRuleList).length >= self.rulePageSize) {
							checkbox.attr("checked", false);
							jAlert('You can only select up to '+self.rulePageSize+' rules.', self.moduleName);
							return;
						}
						
						var rule = {
								ruleId: $row.find('input.ruleId').val(),
								storeId: GLOBAL_storeId,
								priority: $row.find('input.sortOrder').val(),
								disabled: $row.find('input.disabled').val(),
								ruleName: $row.find('label.keyword').find('.keywordLink').text()
						};
						
						self.selectedRuleList[ $row.find('input.ruleId').val()] = rule;
						
					} else {
						self.selectedRuleList[ $row.find('input.ruleId').val()] = null;
						delete self.selectedRuleList[ $row.find('input.ruleId').val()];
					}
					
				});
			},
			updateTypeaheadList: function(array) {
				var self = this;

				var dwrFunction = self.$typeaheadList.find('select.actionType').val();
				var action = dwrFunction == 'updateRules' ? 'updated' : 'deleted';

				if('submitForApproval' == dwrFunction) {
					var completedRequests = 0;
					var msg = '';
					for(var i=0; i < array.length; i++) {
						DeploymentServiceJS.submitRuleForApproval(GLOBAL_storeId, "typeahead", array[i].ruleId, array[i].ruleName, false, {
							callback: function(ruleStatus) {
								completedRequests++;

								if(completedRequests == array.length) {
									msg = 'The rules were succesfully submitted.';
								}
							},
							preHook: function() {
								self.$typeaheadList.hide();
								self.$preloader.show();
								self.$dialogObject.dialog( "close" );
							},
							errorHandler: function(e) {
								completedRequests++;
								msg = 'An error occurred while processing the request. Please contact your system administrator.';
							},
							postHook: function() {
								if(completedRequests == array.length) {
									self.$dialogObject.dialog('close');
									self.selectedRuleList = new Object();
									self.loadRuleList(0, self.rulePage, function(){jAlert(msg, self.moduleName);});
								}
							},
						});
					}
				} else {

					if('updateRules' == dwrFunction) {
						for(var i=0; i<array.length; i++) {
							var rule = array[i];
							var $divRow = self.$dialogObject.find('input#'+rule.ruleId).parent().parent();
							if(!validateIntegerValue($divRow.find('input.sortOrder').val())) {
								jAlert('Please enter a valid integer value.', self.moduleName);
								
								return;
							}
						}
					}
					
					//update priority
					for(var i=0; i<array.length; i++) {
						var rule = array[i];
						var $divRow = self.$dialogObject.find('input#'+rule.ruleId).parent().parent();
						rule.priority = $divRow.find('input.sortOrder').val();
						array[i] = rule;
					}
					
					var msg = '';
					TypeaheadRuleServiceJS[dwrFunction](array, {
						callback: function(result){
							var data = result['data'];
							var errorMessage = '';
							for(var i=0; i<array.length; i++) {
								var response = data[array[i]['ruleId']];

								if(response.errorMessage != null) {
									errorMessage += response.errorMessage.message + '<br/><br/>';
								}
							}

							if(errorMessage != '') {
								msg = errorMessage;
							} else {
								msg = "The selected rules were successfuly "+action+".";
							}
						},
						preHook: function() {
							self.$typeaheadList.hide();
							self.$preloader.show();
							self.$dialogObject.dialog( "close" );
						},
						postHook: function() {
							self.$dialogObject.dialog('close');
							self.selectedRuleList = new Object();
							self.loadRuleList(0, self.rulePage, function(){jAlert(msg, self.moduleName);});
						},
						errorHandler: function(e) {
							msg = 'An error occurred while processing the request. Please contact your system administrator.';
						}
					});
				}
			},
			initializeTypeaheadAction: function() {
				var self = this;
				
				self.$typeaheadList.find('div#updateDialog').dialog({
					title: self.moduleName,
					autoOpen: false,
					modal: true,
					width:755,
					open: function(event, ui){$(ui).css('overflow','hidden');$('.ui-widget-overlay').css('width','100%'); }, 
					close: function(event, ui){$(ui).css('overflow','auto'); } ,
					buttons: {
						"Submit": function() {
							self.updateTypeaheadList(self.getRulesToUpdate());
						},
						Cancel: function() {
							$( this ).dialog( "close" );
						}
					}
				});

				self.$dialogObject = $('div#updateDialog');

				self.$typeaheadList.find('a.dialogBtn').off().on({
					click: function(e) {
						if(Object.keys(self.selectedRuleList).length < 1) {
							jAlert('Please select a rule.', self.moduleName);
							return;
						}
						
						var $dialogObject = e.data.$dialogObject;
						
						$dialogObject.html(self.initializeDialogContent());	
						$dialogObject.find("div.items").removeClass("alt");
						$dialogObject.find("div.items:even").addClass("alt");
						$dialogObject.find('input.ruleVisibility').each(function() {
							var checkbox = this;
							
							$(checkbox).on('click', function() {
								var checked = $(this).is(':checked');
								
								self.selectedRuleList[checkbox.id].disabled = checked;
							});
						});
						
						$dialogObject.dialog('open');
					}
				}, {$dialogObject : self.$dialogObject});
			},
			getRulesToUpdate : function() {
				var self = this;
				var keys = Object.keys(self.selectedRuleList);
				
				var array = new Array();
				
				for(var i=0; i<keys.length; i++) {
					array[array.length] = self.selectedRuleList[keys[i]];
				}
				
				return array;
			},
			initializeDialogContent: function() {
				var html = '';
				var self = this;
				var actionType = self.$typeaheadList.find('select.actionType').val();
				var isDelete = actionType == 'deleteRules';
				var isForApproval = actionType == 'submitForApproval';

				if(isDelete) {
					html += '<label>Are you sure you want to delete these items?</label>';
				} else if(isForApproval) {
					html += '<label>Are you sure you want to submit these items for approval?</label>';
				}

				$header = self.$typeaheadList.find('div#itemHeaderMain').clone();
				$header.find('div#itemHeader4').show();
				$header.find('div#itemHeader3').hide();
				$header.find('label.iter').html('Disabled');
				$header.find('label.toggle').html('&nbsp;');
				html += $('<div></div>').append($header).html();

				var $divItemTable = self.$typeaheadList.find("div#itemList").clone();

				$divItemTable.html('');
				
				var ruleList = self.selectedRuleList;
				var keys = Object.keys(ruleList);
				
				for(var i=0; i<keys.length; i++) {
					if(ruleList[keys[i]] == null)
						continue;
					var ruleId = ruleList[keys[i]].ruleId;
					var priority = ruleList[keys[i]].priority == null ? 0 : ruleList[keys[i]].priority;
					var keyword = ruleList[keys[i]].ruleName;
					var itemPattern = (isDelete || isForApproval) ? "#itemPattern3" : "#itemPattern2";
					var $divItem = self.$typeaheadList.find("div#itemList").find(itemPattern).clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));

					$divItem.find("label.keyword").html(keyword);
					if(isDelete || isForApproval) {
						$divItem.find("label.count").html('&nbsp;&nbsp;&nbsp;'+priority+'<input type="hidden" class="ruleId" value="'+ruleId+'"/>');
						$divItem.find("label.iter").html(ruleList[keys[i]].disabled == 'true' ? 'Yes' : 'No');
					} else {
						var checked = ruleList[keys[i]].disabled == 'true' ? 'CHECKED' : '';
						
						$divItem.find('label').removeClass('w120').addClass('w130');
						$divItem.find("label.count").html('<input type="text" class="sortOrder" size="3" value="'+priority+'"/><input type="hidden" class="ruleId" value="'+ruleId+'"/>');
						$divItem.find("label.iter").html('<input id="'+ruleList[keys[i]].ruleId+'" type="checkbox" '+checked+' class="ruleVisibility" value="false"/>');
					}

					$divItem.show();
					$divItemTable.append($divItem);
				}

				html += $('<div></div>').append($divItemTable).html();

				return html;
			},
			loadItems: function($divList, list, start, noOfItems, type){
				var listLen = list.length;
				var patternId;
				var isType2 = false;
				var self = this;
				if (type == this.reportType.custom) {
					patternId = "div#itemPattern";
				} else {
					isType2 = self.latestFile.indexOf("-splunk") > 0;
					patternId = isType2 ? "div#itemPattern2" : "div#itemPattern1";
				}

				for (var i=start; i < start + noOfItems ; i++){
					if(i == listLen)
						break;

					var $divItem = $divList.find(patternId).clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));
					$divItem.find("label.iter").html('<input type="checkbox" class="ruleVisibility" value="false"/>');
					$divItem.find("label.keyword").html(list[i]["keyword"]);
					$divItem.find("label.count").html(list[i]["count"]);

					if (isType2) {
						$divItem.find("label.results").html(list[i]["resultCount"]);
						$divItem.find("label.sku").html($.isNotBlank(list[i]["sku"]) ? list[i]["sku"]: "&nbsp;");
					}

					$divItem.find("a.toggle").text("Show Active Rule").on({
						click:function(data){
							var toggle = this;
							var $itm = $(toggle).parents("div.items");
							var  key = $itm.find(".keyword").html();

							if($itm.find("div.rules").is(":visible")){
								$(toggle).html("Show Active Rule");
								$itm.find("div.rules").empty().hide();
							}else{
								var $loader = $('<img id="preloader" alt="Retrieving..." src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">');
								$itm.find("div.rules").show().activerule({
									keyword: key,
									beforeRequest: function(){
										$(toggle).hide();
										$loader.insertAfter(toggle);
									},
									afterRequest: function(){
										$(toggle).show().html("Hide Active Rule");
										$(toggle).nextAll().remove();
									}
								});
							}
						}
					});

					$divItem.show();
					$divList.append($divItem);
				}

				$divList.find("div.items").removeClass("alt");
				$divList.find("div.items:even").addClass("alt");
			},

			resetHeader: function() {
				var self = this;
				var $divHeader1 = self.$typeaheadList.find('div#itemHeaderMain').find("div#itemHeader1");
				var $divHeader2 = self.$typeaheadList.find('div#itemHeaderMain').find("div#itemHeader2");
				var $divHeader3 = self.$typeaheadList.find('div#itemHeaderMain').find("div#itemHeader3");
				var self = this;
				if(!self.latestFile) {
					$divHeader3.show();
					$divHeader1.hide();
					$divHeader2.hide();
				} else if (self.latestFile.indexOf("-splunk") > 0) {
					$divHeader1.hide();
					$divHeader2.show();
					$divHeader3.hide();
				} else {
					$divHeader3.hide();
					$divHeader2.hide();
					$divHeader1.show();
				}
			},
			getTypeaheadRuleList : function(page) { 
				var self = this;

				$("#keywordSidePanel").sidepanel({
					moduleName: self.moduleName,
					fieldName: "ruleName",
					page: page,
					pageSize: self.rulePageSize,
					headerText : "Typeahead Rule",
					customAddRule: true,
					showAddButton: allowModify,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, ruleName, page){
						self.rulePage = page;
						self.ruleFilterText = ruleName;
						TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, ruleName, 0, 0, page, base.options.pageSize, {
							callback: function(response){
								var data = response["data"];
								base.populateList(data, ruleName);
								base.addPaging(ruleName, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemAddCallback: function(base, name){

						var popName = name;
						popName = $.trim(popName.replace(/\s+(?=\s)/g,''));

						if ($.isBlank(popName)){
							jAlert("Search Keyword is required.",self.moduleName);
						}
						else if (!isAllowedName(popName)){
							jAlert("Search Keyword contains invalid value.",self.moduleName);
						}
						else {
							TypeaheadRuleServiceJS.addRule(GLOBAL_storeId, popName, {
								callback: function(response){
									if(response.status < 0) {
										jAlert(response.errorMessage.message, self.moduleName);
										self.getTypeaheadRuleList(1);
									} else {
										var data = response['data'];
										if (data != null){
											showActionResponse(1, "add", popName);
											self.setTypeahead(data);
											self.getTypeaheadRuleList(1);
										}

									}
								},
								preHook: function(){ 
									base.prepareList(); 
								}
							});
						}
					},

					itemNameCallback: function(base, item){
						self.setTypeahead(item.model);
					},

					itemOptionCallback: function(base, item){
						var iconPath = "";

						item.ui.find("#itemLinkValue").empty();
						iconPath = self.keywordIconPath;

						if ($.isNotBlank(iconPath)) item.ui.find(".itemIcon").html(iconPath);
					}
				});
			},
			getListTemplate: function() {
				var template = '';
				template += '<div class="padT20 fsize14">';
				template += '	<label class="txtAC fbold">Action: </label>';
				template += '	<select class="actionType">';
				template += '		<option value="updateRules">Update</option>';
				template += '		<option value="submitForApproval">Submit for Approval</option>';
				template += '		<option value="deleteRules">Delete</option>';
				template += '	</select>';
				template += '   <a href="javascript:void(0);" class="dialogBtn" style="background: url(\'image/orange_gradient.png\') repeat-x scroll 0 0 rgba(0, 0, 0, 0);">Submit</a>';
				template += '</div>';
				template += '<div class="clearB"></div>';
				template += '<div id="fieldsTopPaging"></div>';
				template += '<div class="clearB"></div>';
				template += '<div id="itemHeaderMain" class="w100p padT0 marT5 fsize12" style="max-height:365px;">';
				template += '	<div id="itemHeader1" class="items border clearfix" style="display:none">';
				template += '		<label class="iter floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>';
				template += '		<label class="count floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>';
				template += '		<label class="floatL w520 txtAC fbold padTB5" style="background:#eee">Keyword</label>';
				template += '	</div>';
				template += '	<div id="itemHeader2" class="items border clearfix" style="display:none">';
				template += '		<label class="iter floatL w45 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>';
				template += '		<label class="count floatL w70 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>';
				template += '		<label class="floatL w320 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Keyword</label>';
				template += '		<label class="results floatL w70 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Results</label>';
				template += '		<label class="sku floatL w70 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">SKU</label>';
				template += '		<label class="toggle floatL w120 txtAC fbold padTB5" style="background:#eee"> &nbsp; </label>';
				template += '	</div>';
				template += '	<div id="itemHeader3" class="items border clearfix" style="display:none">';
				template += '		<label class="iter floatL w55 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> Select </label>';
				template += '		<label class="count floatL w55 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Priority</label>';
				template += '		<label class="floatL txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee; width:475px;">Keyword</label>';
				template += '		<label class="toggle floatL w130 txtAC fbold padTB5" style="background:#eee"> Status </label>';
				template += '	</div>';
				template += '	<div id="itemHeader4" class="items border clearfix" style="display:none">';
				template += '		<label class="iter floatL w55 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> Select </label>';
				template += '		<label class="count floatL w55 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Priority</label>';
				template += '		<label class="floatL txtAC fbold padTB5" style="width:615px;border-right:1px solid #cccccc; background:#eee">Keyword</label>';
				template += '	</div>';
				template += '</div>';	
				template += '<div id="itemList" class="w100p marRLauto padT0 marT0 fsize12" style="max-height:565px; overflow-y:auto;">';
				template += '	<div id="itemPattern1" class="items pad5 borderB mar0 clearfix" style="display:none">';
				template += '		<label class="iter floatL w80"></label>';
				template += '		<label class="count floatL w80"></label>';
				template += '		<label class="floatL w500">';
				template += '			<label class="keyword floatL w400"></label>'; 
				template += '			<label class="floatL fsize11 w100">';
				template += '				<a class="toggle" href="javascript:void(0);"></a>';
				template += '			</label>';
				template += '			<div class="rules" style="display:none"></div>';
				template += '		</label>';
				template += '	</div>';
				template += '	<div id="itemPattern2" class="items pad5 borderB mar0 clearfix" style="display:none">';
				template += '		<label class="iter floatL w60"></label>';
				template += '		<label class="count floatL w60"></label>';
				template += '		<label class="float" style="width:480px;">';
				template += '			<label class="keyword floatL" style="width:470px;"></label>'; 
				template += '			<div class="rules" style="display:none"></div>';
				template += '		</label>';
				template += '		<label class="floatR fsize11 w120 status txtAL">';
				template += '			&nbsp;<a class="toggle" href="javascript:void(0);"></a>';
				template += '		</label>';
				template += '	</div>';
				template += '	<div id="itemPattern3" class="items pad5 borderB mar0 clearfix" style="display:none">';
				template += '		<label class="iter floatL w45"></label>';
				template += '		<label class="count floatL w70"></label>';
				template += '		<label class="floatL" style="width:365px">';
				template += '			<label class="keyword floatL" style="width:365px"></label>'; 
				template += '			<div class="rules" style="display:none"></div>';
				template += '		</label>';
				template += '		<label class="results floatL w70">&nbsp;</label>';
				template += '		<label class="sku floatL w60">&nbsp;</label>'; 
				template += '		<label class="floatR fsize11 w110 txtAC">';
				template += '			&nbsp;<a class="toggle" href="javascript:void(0);"></a>';
				template += '		</label>';
				template += '	</div>';
				template += '</div>';
				template += '<div class="clearB"></div>';
				template += '<div id="fieldsBottomPaging"></div>';
				template += '<div id="updateDialog" class="marRLauto padT0 marT0 fsize12" style="display:none; width:755; overflow:hidden"><img class="itemIcon" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif"/></div>';

				return template;
			},
			init : function() {
				var self = this;
				self.$typeaheadPanel = $(self.typeaheadPanelSelector);
				self.$preloader = self.$typeaheadPanel.find(self.preloaderSelector);
				self.$typeaheadList = $(self.typeaheadListContainerSelector);
				self.$editPanel = $(self.editPanelSelector);
				self.getTypeaheadRuleList(1);
				self.showTypeahead();
				//self.loadSplunkData();
				self.loadRuleList(0, self.rulePage);

				var $searchDiv = self.$typeaheadPanel.find('div.listSearchDiv');
				$searchDiv.find('a.searchButton').off().on({
					click : function() {
						self.startIndex = 0;
						self.loadRuleList(0, self.rulePage);
					}
				});

				$searchDiv.find('input.searchTextInput').on({
					keyup: function() {
						clearTimeout(self.typingTimer);
						self.typingTimer = setTimeout(function(){
							self.startIndex = 0;
							self.loadRuleList(0, self.rulePage);
						}, self.searchReloadRate);
					},
					keydown: function() {
						clearTimeout(self.typingTimer);
					}
				});

				$searchDiv.find('a.searchButtonList').off().on({
					click : function() {
						self.resetTypeahead();
						self.loadRuleList(0, self.rulePage);
						$searchDiv.find('a.searchButtonList').hide();
					}
				});

				self.typeaheadManager = new AjaxSolr.Manager({
					solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
					store: (new AjaxSolr.ParameterStore())
				});

				self.typeaheadManager.addWidget(new AjaxSolr.TypeaheadSearchResultWidget({
					id: WIDGET_ID_searchResult,
					target: WIDGET_TARGET_searchResult,
					brandId: WIDGET_ID_brand,
					brandTarget: WIDGET_TARGET_brand,
					categoryId: WIDGET_ID_category,
					categoryTarget: WIDGET_TARGET_category
				}));
			}
	};

	$(document).ready(function() {
		Typeahead.init();
	});
})(jQuery);	
