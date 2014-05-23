(function($){

	var Typeahead = {
			moduleName: "Typeahead",
			selectedRule:  null,
			selectedRuleStatus: null,
			currentRuleMap: null,
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
			elContainer: "listContainer",
			$elObject: null,
			$dialogObject: null,
			rulePage: 1,
			rulePageSize: 15,

			removeFacetGroupItemConfirmText: "Delete facet value?",

			facetFields : ["Category", "Manufacturer"],	//TODO This might be retrieved from a lookup table
			facetValueList: null,
			sortOrderList: null,
			facetGroupIdList: null,

			keywordIconPath: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_keyword.png'/>",
			templateIconPath:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_template.png'/>",
			saveIconPath:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_disk.png'/>",
			rectLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-rect.gif'/>",
			roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
			magniIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_magniGlass13.png'/>",
			lockIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_lock.png'/>",
			searchReloadRate: 1000,
			typingTimer: null,
			typeaheadManager: null,
			typeaheadBrandManager: null,
			typeaheadCategoryManager: null,

			prepareTypeahead : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #noSelected").hide();
				$("#facetsorting").hide();
				$("#titleHeader").empty();
				$("#ruleTypeIcon").html("");
			},

			showTypeahead : function(){
				var self = this;
				self.prepareTypeahead();

				if(self.selectedRule==null){
					$("#preloader").hide();
					$("#noSelected").show();
					$("#titleText").html(self.moduleName);
					return;
				}

				$("#submitForApproval").rulestatusbar({
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
						$("#preloader").hide();
						$("#submitForApproval").show();
						$("#titleText").html(self.moduleName + " for ");
						$("#titleHeader").text(self.selectedRule["ruleName"]);
						$("#readableString").html(self.selectedRule["readableString"]);

						$('#auditIcon').off().on({
							click: function(e){
								$(e.currentTarget).viewaudit({
									itemDataCallback: function(base, page){
										AuditServiceJS.getFacetSortTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
											callback: function(data){
												var total = data.totalSize;
												base.populateList(data);
												base.addPaging(base.options.page, total);
											},
											preHook: function(){
												base.prepareList();
											}
										});
									}
								});
							}
						});
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
				$('input.searchTextInput').val(rule.ruleName);
				$('a.searchButtonList').show();
				$('div#searchResult, div#category, div#brand').show();
				self.startIndex = 0;
//				self.loadRuleList(0, self.rulePage);
//				self.keyword = "";
//				self.fq = "";
				$('input.searchTextInput, a.searchButton').hide();
				$('div#listContainer').hide();
//				$('div#itemList, div#itemHeaderMain, div.listSearchDiv').hide();

				$('#searchResult, #category, #brand').find(':not(#docs, #categoryDocs, #brandDocs)').remove();
				
				TypeaheadRuleServiceJS.getAllRules(rule.ruleName, 0, 1, 1, 5, {
					callback: function(response) {
						var data = response["data"];
						var list = data.list;

						if(list.length > 0) {
							for(var i=0; i < list.length; i++) {
								var html = '<div class="fsize15"><strong>'+list[i].ruleName+'</strong></div>';
								if(i == 0) {
									$('#category, #brand').prepend(html);
								} else {
									if(i < GLOBAL_storeKeywordMaxBrand)
										$('#brand').append(html);
									$('#category').append(html);
								}
							}
							
							self.typeaheadManager.store.addByValue('q', $.trim(list[0].ruleName)); //AjaxSolr.Parameter.escapeValue(value.trim())
							self.typeaheadManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
							self.typeaheadManager.store.addByValue('fl', 'Name,ImagePath_2,EDP'); 
							self.typeaheadManager.doRequest(0);

							self.typeaheadBrandManager.store.addByValue('q', $.trim(list[0].ruleName)); //AjaxSolr.Parameter.escapeValue(value.trim())
							self.typeaheadBrandManager.store.addByValue('rows', GLOBAL_storeMaxBrand);
							self.typeaheadBrandManager.store.addByValue('json.nl', "map");
							self.typeaheadBrandManager.store.addByValue('group', 'true'); 
							self.typeaheadBrandManager.store.addByValue('group.field', 'Manufacturer');
							self.typeaheadBrandManager.store.addByValue('group.limit', 1);
							self.typeaheadBrandManager.store.addByValue('group.main', 'true');
							self.typeaheadBrandManager.store.addByValue('fl', 'Manufacturer,Name,ImagePath_2');
							self.typeaheadBrandManager.store.addByValue('facet', 'true');
							self.typeaheadBrandManager.store.addByValue('facet.field', 'Manufacturer');
							self.typeaheadBrandManager.store.addByValue('facet.mincount', 1);
							self.typeaheadBrandManager.doRequest(0);

							self.typeaheadCategoryManager.store.addByValue('q', $.trim(list[0].ruleName)); //AjaxSolr.Parameter.escapeValue(value.trim())
							self.typeaheadCategoryManager.store.addByValue('rows', 1);
							self.typeaheadCategoryManager.store.addByValue('json.nl', "map");
							self.typeaheadCategoryManager.store.addByValue('facet', 'true');
							self.typeaheadCategoryManager.store.addByValue('facet.field', 'Category');
							self.typeaheadCategoryManager.store.addByValue('facet.field', 'PCMall_FacetTemplateName'); 
							self.typeaheadCategoryManager.store.addByValue('facet.mincount', 1);
							self.typeaheadCategoryManager.store.addByValue('facet.limit', 5);
							self.typeaheadCategoryManager.doRequest(0);
						}
					}
				});

				self.showTypeahead();
			},
			resetTypeahead : function() {
				var self = this;
				self.selectedRule = null;
				$('input.searchTextInput').val('');
				$('span#titleHeader').html('');
				$('span#titleText').html(self.moduleName);
			},
			resetRuleListTable: function() {
				var self = this;
				$('#updateDialog').dialog('destroy').remove();
				self.$elObject.empty();
				$("div#fieldsBottomPaging, , div#fieldsTopPaging").empty();
			},
			setCurrentRuleList: function(list) {
				var self = this;
				var newMap = new Object();

				for(var i = 0; i<list.length; i++) {
					newMap[list[i].ruleId] = list[i];
				}

				self.currentRuleMap = newMap;
			},
			loadRuleList: function(matchType, page) {
				var self = this;
				var searchText = $('input.searchTextInput').val();
				$('input.searchTextInput, a.searchButton').show();

				$('a.searchButtonList').hide();
				$("#submitForApproval").html('');
				$('div#searchResult, div#category, div#brand').hide();

				self.resetRuleListTable();
				TypeaheadRuleServiceJS.getAllRules(searchText, matchType, 1, page, self.rulePageSize, {
					callback: function(response){
						var data = response["data"];
						var list = data.list;

						self.setCurrentRuleList(list);
						self.$elObject.html(self.getListTemplate());
						var $divList = self.$elObject.find("div#itemList");
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


						self.$elObject.find("div#fieldsBottomPaging, div#fieldsTopPaging").paginate({
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
						$('div#preloader').show();
					},
					postHook:function(){
						$('div#preloader').hide();
						self.$elObject.show();
						self.searchTriggered = false;
					}
				});
			},
			loadSplunkData: function() {
				var self = this;
				self.$elObject.html(self.getListTemplate());
				TopKeywordServiceJS.getFileList({
					callback: function(files){
						self.latestFile = files[0];
						TopKeywordServiceJS.getFileContents(self.latestFile, {
							callback: function(data){
								var list = data.list;
								var $divList = self.$elObject.find("div#itemList");
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
								$('div#preloader').show();
							},
							postHook:function(){
								$('div#preloader').hide();
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
					$divItem.find("label.count").html('<input type="hidden" class="sortOrder" size="3" value="'+rule['priority']+'"/><input type="hidden" class="ruleId" value="'+rule["ruleId"]+'"/>'+rule['priority']);
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
								$checkboxDiv.html('<input type="checkbox" class="ruleVisibility" value="false"/>');
							}
							var status = ruleStatus['approvalStatus'];
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
			getRulesToUpdate: function() {
				var $container = $('#updateDialog');
				var array = new Array();

				$container.find('div:not(.border).items').each(function() {
					var $row = $(this);
					var rule = {
							ruleId: $row.find('input.ruleId').val(),
							storeId: GLOBAL_storeId,
							priority: $row.find('input.sortOrder').val(),
							disabled: $row.find('input.ruleVisibility').is(':checked')
					};

					array[array.length] = rule;
				});

				return array;
			},
			updateTypeaheadList: function(array) {
				var self = this;

				var dwrFunction = self.$elObject.find('select.actionType').val();
				var action = dwrFunction == 'updateRules' ? 'updated' : 'deleted';

				if('submitForApproval' == dwrFunction) {
					var completedRequests = 0;
					for(var i=0; i < array.length; i++) {
						DeploymentServiceJS.submitRuleForApproval(GLOBAL_storeId, "typeahead", array[i].ruleId, "batch", false, {
							callback: function(ruleStatus) {
								completedRequests++;

								if(completedRequests == array.length) {
									jAlert("The rules were succesfully submitted.", self.moduleName);
								}
							},
							errorHandler: function(e) {
								completedRequests++;
								jAlert('An error occurred while processing the request. Please contact your system administrator.', self.moduleName);
							},
							postHook: function() {
								if(completedRequests == array.length) {
									self.$dialogObject.dialog('close');
									self.loadRuleList(0, self.rulePage);
								}
							},
						});
					}
				} else {

					if('updateRules' == dwrFunction) {
						for(var i=0; i<array.length; i++) {
							if(!validateIntegerValue(array[i].priority)) {
								jAlert('Please enter a valid integer value.', self.moduleName);

								return;
							}
						}
					}

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
								jAlert(errorMessage, self.moduleName);
							} else {
								jAlert('The selected rules were successfuly '+action+'.', self.moduleName);
							}
						},
						postHook: function() {
							self.$dialogObject.dialog('close');
							self.loadRuleList(0, self.rulePage);
						},
						errorHandler: function(e) {
							jAlert('An error occurred while processing the request. Please contact your system administrator.', self.moduleName);
						}
					});
				}
			},
			initializeTypeaheadAction: function() {
				var self = this;
				self.$elObject.find('div#updateDialog').dialog({
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

				self.$elObject.find('a.dialogBtn').off().on({
					click: function() {
						if($('input.ruleVisibility:checked').size() < 1) {
							jAlert('Please select a rule.', self.moduleName);
							return;
						}
						$('div#updateDialog').html(self.initializeDialogContent());	
						$('div#updateDialog').find("div.items").removeClass("alt");
						$('div#updateDialog').find("div.items:even").addClass("alt");
						$('div#updateDialog').dialog('open');
					}
				});
			},
			initializeDialogContent: function() {
				var html = '';
				var self = this;
				var actionType = self.$elObject.find('select.actionType').val();
				var isDelete = actionType == 'deleteRules';
				var isForApproval = actionType == 'submitForApproval';

				if(isDelete) {
					html += '<label>Are you sure you want to delete these items?</label>';
				} else if(isForApproval) {
					html += '<label>Are you sure you want to submit these items for approval?</label>';
				}

				$header = self.$elObject.find('div#itemHeaderMain').clone();
				$header.find('label.iter').html('Disabled');
				$header.find('label.toggle').html('&nbsp;');
				html += $('<div></div>').append($header).html();

				var $divItemTable = self.$elObject.find("div#itemList").clone();

				$divItemTable.html('');

				self.$elObject.find('div#itemList').find('input.ruleVisibility:checked').each(function(i) {
					var $divRow = $(this).parent().parent();
					var ruleId = $divRow.find('input.ruleId').val();
					var currentRuleMap = self.currentRuleMap;
					var priority = currentRuleMap[ruleId].priority == null ? 0 : currentRuleMap[ruleId].priority;
					var keyword = currentRuleMap[ruleId].ruleName;
					var itemPattern = (isDelete || isForApproval) ? "#itemPattern3" : "#itemPattern2";
					var $divItem = self.$elObject.find("div#itemList").find(itemPattern).clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));

					$divItem.find("label.keyword").html(keyword);
					if(isDelete || isForApproval) {
						$divItem.find("label.count").html('&nbsp;&nbsp;&nbsp;'+priority+'<input type="hidden" class="ruleId" value="'+ruleId+'"/>');
						$divItem.find("label.iter").html(currentRuleMap[ruleId].disabled == true ? 'Yes' : 'No');
					} else {
						var checked = currentRuleMap[ruleId].disabled == true ? 'CHECKED' : '';

						$divItem.find("label.count").html('<input type="text" class="sortOrder" size="3" value="'+priority+'"/><input type="hidden" class="ruleId" value="'+ruleId+'"/>');
						$divItem.find("label.iter").html('<input type="checkbox" '+checked+' class="ruleVisibility" value="false"/>');
					}

					$divItem.show();
					$divItemTable.append($divItem);

				});

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
				var $divHeader1 = self.$elObject.find('div#itemHeaderMain').find("div#itemHeader1");
				var $divHeader2 = self.$elObject.find('div#itemHeaderMain').find("div#itemHeader2");
				var $divHeader3 = self.$elObject.find('div#itemHeaderMain').find("div#itemHeader3");
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
						TypeaheadRuleServiceJS.getAllRules(ruleName, 0, 0, page, base.options.pageSize, {
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
			addDownloadListener: function(){
				var self = this;
				$("a#downloadIcon").download({
					headerText:"Download Facet Sort",
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;
						params["id"] = self.selectedRule["ruleId"];
						params["filename"] = e.data.filename;
						params["type"] = e.data.type;
						params["clientTimezone"] = +new Date();

						for(var key in params){
							if (count>0) urlParams +='&';
							urlParams += (key + '=' + encodeURIComponent(params[key]));
							count++;
						};

						document.location.href = url + '?' + urlParams;
					}
				});
			},

			addDeleteRuleListener: function(){
				var self = this;
				$("#deleteBtn").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm("Delete " + self.selectedRule["ruleName"] + "'s rule?", self.moduleName, function(result){
							if(result){
								FacetSortServiceJS.deleteRule(self.selectedRule["ruleId"],{
									callback: function(code){
										showActionResponse(code, "delete", self.selectedRule["ruleName"]);
										if(code==1) {
											self.setTypeahead(null);
										}
									}
								});
							}
						});
					},
					mouseenter: showHoverInfo
				},{locked:self.selectedRuleStatus["locked"] || !allowModify});
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
				template += '<div id="itemHeaderMain" class="w100p padT0 marT5 marL15 fsize12" style="max-height:365px;">';
				template += '	<div id="itemHeader1" class="items border clearfix" style="display:none">';
				template += '		<label class="iter floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>';
				template += '		<label class="count floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>';
				template += '		<label class="floatL w535 txtAC fbold padTB5" style="background:#eee">Keyword</label>';
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
				template += '		<label class="floatL w450 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Keyword</label>';
				template += '		<label class="toggle floatL w115 txtAC fbold padTB5" style="background:#eee"> Status </label>';
				template += '	</div>';
				template += '</div>';	
				template += '<div id="itemList" class="w95p marRLauto padT0 marT0 fsize12" style="max-height:565px; overflow-y:auto;">';
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
				template += '		<label class="floatL w310">';
				template += '			<label class="keyword floatL w310"></label>'; 
				template += '			<div class="rules" style="display:none"></div>';
				template += '		</label>';
				template += '		<label class="results floatL w70">&nbsp;</label>';
				template += '		<label class="sku floatL w60">&nbsp;</label>'; 
				template += '		<label class="floatR fsize11 w110 status txtAL">';
				template += '			&nbsp;<a class="toggle" href="javascript:void(0);"></a>';
				template += '		</label>';
				template += '	</div>';
				template += '	<div id="itemPattern3" class="items pad5 borderB mar0 clearfix" style="display:none">';
				template += '		<label class="iter floatL w45"></label>';
				template += '		<label class="count floatL w70"></label>';
				template += '		<label class="floatL w435">';
				template += '			<label class="keyword floatL w310"></label>'; 
				template += '			<div class="rules" style="display:none"></div>';
				template += '		</label>';
				template += '		<label class="results floatL w70"></label>';
				template += '		<label class="sku floatL w70"></label>'; 
				template += '		<label class="floatR fsize11 w110 txtAC">';
				template += '			<a class="toggle" href="javascript:void(0);"></a>';
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
				self.$elObject = $('#'+self.elContainer);
				self.getTypeaheadRuleList(1);
				self.showTypeahead();
				//self.loadSplunkData();
				self.loadRuleList(0, self.rulePage);

				var $searchDiv = $('div.listSearchDiv');
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
					target: WIDGET_TARGET_searchResult
				}));

				self.typeaheadBrandManager = new AjaxSolr.Manager({
					solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
					store: (new AjaxSolr.ParameterStore())
				});

				self.typeaheadBrandManager.addWidget(new AjaxSolr.TypeaheadBrandWidget({
					id: WIDGET_ID_brand,
					target: WIDGET_TARGET_brand
				}));

				self.typeaheadCategoryManager = new AjaxSolr.Manager({
					solrUrl: GLOBAL_solrUrl + GLOBAL_storeCore + '/',
					store: (new AjaxSolr.ParameterStore())
				});

				self.typeaheadCategoryManager.addWidget(new AjaxSolr.TypeaheadCategoryWidget({
					id: WIDGET_ID_category,
					target: WIDGET_TARGET_category
				}));
			}
	};

	$(document).ready(function() {
		Typeahead.init();
	});
})(jQuery);	
