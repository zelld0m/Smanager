(function($){

	$.typeahead = function(el, options) {
		var base = this;
		
		var destroy = true;

		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("typeahead", base);
		
		base.options = $.extend({},$.typeahead.defaultOptions, options);
		
		base.init = function() {
			var self = this;
			self.$typeaheadPanel = self.$el;
			self.$preloader = self.$typeaheadPanel.find(base.options.preloaderSelector);
			self.$typeaheadList = $(base.options.typeaheadListContainerSelector);
			self.$editPanel = $(base.options.editPanelSelector);
			self.selectedRuleList = new Object();
			self.sectionSortMap = {"Category":0, "Brand":1, "Suggestion":2};
			self.getTypeaheadRuleList(1);
			//self.loadSplunkData();
			self.loadRuleList(0, base.options.rulePage);

			var $searchDiv = self.$typeaheadPanel.find('div.listSearchDiv');
			$searchDiv.find('a.searchButton').off().on({
				click : function() {
					base.options.startIndex = 0;
					self.loadRuleList(0, base.options.rulePage);
				}
			});

			$searchDiv.find('input.searchTextInput').on({
				keyup: function() {
					clearTimeout(base.options.typingTimer);
					self.typingTimer = setTimeout(function(){
						base.options.startIndex = 0;
						self.loadRuleList(0, base.options.rulePage);
					}, base.options.searchReloadRate);
				},
				keydown: function() {
					clearTimeout(base.options.typingTimer);
				}
			});

			$searchDiv.find('a.searchButtonList').off().on({
				click : function() {
					self.resetTypeahead();
					self.loadRuleList(0, base.options.rulePage);
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
				categoryTarget: WIDGET_TARGET_category,
				mode: 'edit'
			}));
		};
		
		base.prepareTypeahead = function() {
			var self = this;
			clearAllQtip();
			
			self.$preloader.show();
			self.$typeaheadPanel.find("#submitForApproval, #noSelected").hide();
			self.$typeaheadPanel.find("#titleHeader").empty();
			self.$typeaheadPanel.find("#ruleTypeIcon").html("");
			
			var sectionList = self.selectedRule.sectionList;
			self.sectionSortNap = new Object();
			
			// Load default section sorting
			if(sectionList == null || sectionList.length == 0) {
				self.sectionSortNap = {'Category':0, 'Brand':1, 'Suggestion':2};
			} else {
				for(var i=0; i<sectionList.length; i++) {
					var section = sectionList[i];
					self.sectionSortNap[section.inputValue] = i;
				}
			}
		};
		
		base.showTypeahead = function() {
			var self = this;
			self.prepareTypeahead();

			if(self.selectedRule==null){
				self.$preloader.hide();
				self.$typeaheadPanel.find("#noSelected").show();
				self.$typeaheadPanel.find("#titleText").html(base.options.moduleName);
				return;
			}

			self.$typeaheadPanel.find("#submitForApproval").rulestatusbar({
				moduleName: base.options.moduleName,
				rule: base.selectedRule,
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
					self.setTypeahead(self.selectedRule);
				},
				beforeRuleStatusRequest: function(){
					self.prepareTypeahead();

				},
				afterRuleStatusRequest: function(ruleStatus){
					self.$typeaheadPanel.find("#titleText").html(base.options.moduleName + " for ");
					self.$typeaheadPanel.find("#titleHeader").text(self.selectedRule["ruleName"]);
					self.$typeaheadPanel.find("#readableString").html(self.selectedRule["readableString"]);
					self.loadTypeaheadSolrDetails(base.selectedRule.ruleName, !ruleStatus.locked);
					self.$preloader.hide();
					self.initializeEditEvents(!ruleStatus.locked);
					self.$editPanel.find('div#sectionTableContainer').typeaheadaddsection({moduleName:base.options.moduleName, sectionList : self.getSectionList(self.selectedRule), editable:!ruleStatus.locked});
					self.$typeaheadPanel.find("#submitForApproval").show();
					self.$editPanel.show();

					self.initializeDisabledCheckbox(self.selectedRule.sectionList);
					
					self.$typeaheadPanel.find('table#typeaheadTable').find('.disabled-flag').each(function(){
						var $checkbox = $(this);
						$checkbox.slidecheckbox({
							initOn: !$checkbox.is(':checked'),
							disabled: ruleStatus.locked, //TODO:
							changeStatusCallback: function(base, dt){
								
							}
						});
					});
					
					self.sectionSortMap = {"Category":0, "Brand":1, "Suggestion":2};
					self.initializeCurrentSectionSorting(self.selectedRule.sectionList);
					self.initializeSortingDialog(!ruleStatus.locked);
					
					self.$editPanel.find('a#dialogSortIcon').off().on({
						click: function() {
							$('#sortDialog').dialog('open');
						}
					});
					
				}
			});
		};
		
		base.getSectionList = function(rule) {
			
			var sectionList = rule.sectionList;
			var sectionArray = new Array();
			
			for(var i=0; sectionList && i< sectionList.length ; i++) {
				var section = sectionList[i];
				
				if(section.keywordAttributeType != 'SECTION') {
					continue;
				}
				
				var arrayObject = new Object();
				
				arrayObject.name = section.inputValue;
				arrayObject.disabled = section.disabled;
				arrayObject.sectionItems = section.keywordItemValues;
				
				sectionArray[sectionArray.length] = arrayObject;
			}
			
			return sectionArray;
			
		};
		
		base.initializeEditEvents = function(editable) {
			var self = this;
			
			if(editable == false) {
				self.$typeaheadPanel.find("#priorityEdit").prop('readonly', true);
				self.$typeaheadPanel.find("#disabledEdit").click(function(){return false;});
				self.$typeaheadPanel.find("#suggestionDisabled").click(function(){return false;});
				self.$typeaheadPanel.find("#saveBtn").hide();
				self.$typeaheadPanel.find("#deleteBtn").hide();
				return;
			}
			
			self.$typeaheadPanel.find("#priorityEdit").prop('readonly', false);
			self.$typeaheadPanel.find("#disabledEdit").off('click');
			self.$typeaheadPanel.find("#saveBtn").show();
			self.$typeaheadPanel.find("#deleteBtn").show();
			
			self.$typeaheadPanel.find("#saveBtn").off().on({
				click:function(){
					var typeaheadRule = self.selectedRule;
					typeaheadRule.priority = self.$typeaheadPanel.find("#priorityEdit").val();
					typeaheadRule.disabled = !self.$typeaheadPanel.find("#disabledEdit").is(":checked");
					
					self.buildKeywordAttributeList(typeaheadRule);
					
					self.updateTypeaheadRule(typeaheadRule, function(){self.$preloader.show(); self.$editPanel.hide();}, function(){self.setTypeahead(typeaheadRule);});
				}
			});
			
			self.$typeaheadPanel.find("#deleteBtn").off().on({
				click:function(){
					var typeaheadRule = self.selectedRule;
					jConfirm("Are you sure you want to delete this rule?", base.options.moduleName, function(result){
						if(result) {
							self.deleteTypeaheadRule(typeaheadRule, function(){self.$preloader.show(); self.$editPanel.hide();}, function(){self.resetTypeahead(); self.loadRuleList(0, base.options.rulePage);});
						}
					});
					
				}
			});
		};
		
		base.buildKeywordAttributeList = function(typeaheadRule) {
			var self = this;
			var sectionList = new Array();
			var $typeaheadTable = self.$editPanel.find('table#typeaheadTable');
			//Types: Category 0, Brand 1, Suggestion 2, Section 3, Section Item 4
			
			// Update to latest sorting
			self.initializeCurrentSectionSorting();
			var sortMap = self.sectionSortMap;
			//build category section
			var categorySection = self.buildKeywordAttribute("Category", sortMap['Category'], !$typeaheadTable.find('input#categoryDisabled').is(':checked'), 'KEY_CAT');
			var categoryItemArray = self.buildSectionItemList(self.$editPanel.find("div#sortedCategoryDocs").find('ul.ui-sortable').find('li'));
			categorySection.keywordAttributeItems = categoryItemArray;
			sectionList[sectionList.length] = categorySection;
			
			//build brand section
			var brandSection = self.buildKeywordAttribute("Brand", sortMap['Brand'], !$typeaheadTable.find('input#brandDisabled').is(':checked'), 'KEY_BRAND');
			var brandItemArray = self.buildSectionItemList(self.$editPanel.find("div#sortedBrandDocs").find('ul.ui-sortable').find('li'));
			brandSection.keywordAttributeItems = brandItemArray;
			sectionList[sectionList.length] = brandSection;
						
			//build suggestion section
			var suggestionSection = self.buildKeywordAttribute("Suggestion", sortMap['Suggestion'], !self.$editPanel.find('input#suggestionDisabled').is(':checked'), 'KEY_SUGGESTION');
			sectionList[sectionList.length] = suggestionSection;
			
			self.$editPanel.find('table.sectionTable').each(function() {
				var $sectionTable = $(this);
				var sectionName = $sectionTable.find('div.sectionName').text();
				var dynamicSection = self.buildKeywordAttribute(sectionName, sortMap[sectionName], !$sectionTable.find('input[type=checkbox]').is(':checked'), 'KEY_SECTION');
				
				var $sectionItemValues = $sectionTable.find('.sectionItemValue');
				
				var itemCount = 0;
				var dynamicItems = new Array();
				$sectionItemValues.each(function() {
					var value = $(this).text();
					
					var dynamicSectionItem = self.buildKeywordAttribute(value, itemCount, false, 'KEY_SEC_ITEM');
					dynamicItems[dynamicItems.length] = dynamicSectionItem;
					itemCount ++;
				});
				
				if(dynamicItems.length > 0){
					dynamicSection.keywordAttributeItems = dynamicItems;
				}
				
				sectionList[sectionList.length] = dynamicSection;
				
			}); 
			
			typeaheadRule.sectionList = sectionList;						
		};
		
		base.buildSectionItemList = function($itemContainer) {
			var self = this;
			var count = 0;
			var sectionArray = new Array();
			$itemContainer.each(function() {
				var itemValue = $(this).find('span').text();
				var itemSection = self.buildKeywordAttribute(itemValue, count, false, 'KEY_SEC_ITEM');
				sectionArray[sectionArray.length] = itemSection;
				count++;
			});
			
			return sectionArray;
		};
		
		base.buildKeywordAttribute = function(value, priority, disabled, type) {
			var section = new Object();
			
			section.inputValue = value;
			section.priority = priority;
			section.disabled = disabled;
			section.inputParamEnumId = type;
			
			return section;
		};
		
		base.setTypeahead = function(rule) {
			var self = this;
			self.selectedRule = rule;
			self.$typeaheadPanel.find('input.searchTextInput').val(rule.ruleName);
			self.$typeaheadPanel.find('#priorityEdit').val(rule.priority);
			self.$typeaheadPanel.find('#disabledEdit').prop('checked', !rule.disabled);
			self.$typeaheadPanel.find('a.searchButtonList').show();
			self.$editPanel.hide();
			self.startIndex = 0;
			self.$typeaheadPanel.find('input.searchTextInput, a.searchButton').hide();
			self.$typeaheadList.hide();
			
			self.$typeaheadPanel.find('div.sortDiv').children('ul').sortable('destroy');
			self.$typeaheadPanel.find('div#sectionBox').empty();
			self.$typeaheadPanel.find('#searchResult, #category, #brand').find(':not(.clearB, hr, #docs, #sortedCategoryDocs, #categoryDocs, #sortedBrandDocs, #brandDocs)').remove();
			self.$typeaheadPanel.find('div.sortDiv').append('<ul></ul>');
			
			TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, rule.ruleName, 0, 0, 1, 1, true, {
				callback: function(response) {
					var data = response["data"];
					var list = data.list;

					if(list.length > 0) {
						for(var i=0; i < list.length; i++) {
							var html = ''; //'<div class="fsize15"><strong>'+list[i].ruleName+countDiv+'</strong></div>';
							
							if(i == 0) {
								self.selectedRule = list[0];
								self.$typeaheadPanel.find('#category').prepend(html);
							} else {
								if(i < GLOBAL_storeKeywordMaxCategory) {
									var keywordsRow = '<tr><td><div class="marL10 fsize11">'+html+'</div></td><td></td><td></td></tr>';
									self.$editPanel.find('#typeaheadTable tr:last').after(keywordsRow);
								}
							}
						}
						self.loadRelatedKeywords(rule.ruleName);
					}
					
					self.showTypeahead();
				},
				preHook : function() {
					self.$preloader.show();
				}
			});
			
			
		};
		
		base.initializeCurrentSectionSorting = function(sectionList) {
			var self = this;
			var sectionSortMap = self.sectionSortMap;
			
			if(sectionList != null && sectionList != undefined) {
				self.sectionSortMap = {"Category":0, "Brand":1, "Suggestion":2};
				
				for(var i=0; i<sectionList.length; i++) {
					var section = sectionList[i];
					
					self.sectionSortMap[section.inputValue] = i;
				}
				
				return;
			}
						
			var newSectionSortMap = new Object();
			newSectionSortMap['Category'] = self.sectionSortMap['Category'];
			newSectionSortMap['Brand'] = self.sectionSortMap['Brand'];
			newSectionSortMap['Suggestion'] = self.sectionSortMap['Suggestion'];
			
			var newEntries = new Array();
			
			self.$typeaheadPanel.find('div#sectionBox').find('div.sectionName').each(function() {
				var sectionName = $(this).text();
				
				if(sectionSortMap[sectionName] != undefined) {
					newSectionSortMap[sectionName] = sectionSortMap[sectionName]; 
				} else {
					newEntries[newEntries.length] = sectionName;
				}
			});
			
			for(var i=0; i<newEntries.length; i++) {
				newSectionSortMap[newEntries[i]] = Object.keys(newSectionSortMap).length;
			}
			
			self.sectionSortMap = newSectionSortMap;						
		};
		
		base.initializeSortingDialogContent = function($dialogElement, editable) {
			var self = this;
			
			var sortMap = self.sectionSortMap;
			
			var sortedSection = [];
			
			for(var key in sortMap) {
				sortedSection[sortMap[key]] = key;
			}
			
			var $ul = $dialogElement.find('ul');
			
			$ul.empty();
			
			var dragHandler = editable == false ? '' : '<a href="javascript:void(0);" class="dragHandler floatR">'+base.options.dragIcon+'</a>';
			
			for(var i=0; i<sortedSection.length; i++) {
				var section = sortedSection[i];
				if(section != undefined) {
					$ul.append('<li><span class="inputValue">'+section+ '</span>' + dragHandler +'</li>');
				}
			}
		};
		
		base.initializeSortingDialog = function(editable) {
			var self = this;
			var $sortDialog = $('div#sortDialog').dialog('destroy');
						
			$sortDialog.dialog({
				autoOpen: false,
				modal: true,
				open : function( event, ui) {
					self.initializeCurrentSectionSorting();
					self.initializeSortingDialogContent($sortDialog, editable);
					if(editable != false) {
						$sortDialog.find('ul').sortable({handle:'.dragHandler',});
					}
				},
				close: function( event, ui) {
					$sortDialog.find('ul').sortable('destroy');
					self.sectionSortMap = new Object();
					
					$sortDialog.find('li').each(function(index, li) {
						var $li = $(li);
						self.sectionSortMap[$li.find('span.inputValue').text()] = (index);
					});
				},
				buttons: [{
					text: "OK",
					click: function() {
						$(this).dialog('close');
					}
				}]
			});
			
		};
		
		base.initializeDisabledCheckbox = function(sectionList) {
			var self = this;
			if(sectionList == null || sectionList == undefined || sectionList.length  == 0) {
				self.$editPanel.find('input#categoryDisabled').attr('checked', false);
				self.$editPanel.find('input#brandDisabled').attr('checked', false);
				self.$editPanel.find('input#suggestionDisabled').attr('checked', true);
				return;
			}
			
			for(var i=0; sectionList && i<sectionList.length; i++) {
				var section = sectionList[i];
				
				switch(section.keywordAttributeType) {
				case "CATEGORY":
					self.$editPanel.find('input#categoryDisabled').attr('checked', section.disabled);
					break;
				case "BRAND": 
					self.$editPanel.find('input#brandDisabled').attr('checked', section.disabled);
					break;
				case "SUGGESTION": 
					self.$editPanel.find('input#suggestionDisabled').attr('checked', !section.disabled);
					break;
				}
			}
		};
		
		base.loadRelatedKeywords = function(keyword) {
			var self = this;
			TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, keyword, 0, 1, 1, GLOBAL_storeKeywordMaxCategory, {
				callback: function(response) {
					var data = response["data"];
					var list = data.list;
					
					var html = '';
					
					for(var i=0; i<list.length; i++) {
						var typeahead = list[i];
						html += '<div class="padB10">';
						html += typeahead['ruleName'];
						html += '</div>';
						html += '<div class="clearB"></div>';
					}
					
					self.$typeaheadPanel.find("div#relatedKeywords").html(html);
				},
				preHook: function() {
					self.$typeaheadPanel.find("div#relatedKeywords").html(base.options.rectLoader);
				}
			});
		};
		
		base.loadTypeaheadSolrDetails = function(keyword, editable) {
			var params = GLOBAL_typeaheadSolrParams;
			var self = this;
			var sectionList = self.selectedRule.sectionList;
			
			self.typeaheadManager.store.addByValue('q', $.trim(keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
			self.typeaheadManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
			self.typeaheadManager.store.addByValue('fl', 'Name,ImagePath_2,EDP'); 
			self.typeaheadManager.store.addByValue('facet', 'true');
			self.typeaheadManager.store.addByValue('facet.field', 'Manufacturer');
			self.typeaheadManager.store.addByValue('facet.mincount', 1);
			
			if(GLOBAL_storeFacetTemplateType === 'CNET') {
				self.typeaheadManager.store.addByValue('facet.field', GLOBAL_storeFacetTemplate);
			} else {
				self.typeaheadManager.store.addByValue('facet.field', 'Category');
			}
			
			self.typeaheadManager.store.addByValue('facet.mincount', 1);
			self.typeaheadManager.store.addByValue('divCount', 'countDiv');
			self.typeaheadManager.countDiv = $('#category').find('span#count');
			self.typeaheadManager.store.addByValue('storeAlias', GLOBAL_storeId);

			for(name in params) {
				self.typeaheadManager.store.addByValue(name, params[name]);
			}
			
			for(var i=0; sectionList && i<sectionList.length; i++) {
				var section = sectionList[i];
				
				if('CATEGORY' == section.keywordAttributeType) {
					self.typeaheadManager.elevatedCategoryList = section.keywordItemValues;
				} else if('BRAND' == section.keywordAttributeType) {
					self.typeaheadManager.elevatedBrandList = section.keywordItemValues;
				}
			}
			
			self.typeaheadManager.preHook = function() {
				self.$editPanel.find('#suggestQtip').html(base.options.rectLoader);
				self.$editPanel.find('#suggestQtip').qtip("destroy");
			};
			
			self.typeaheadManager.postHook = function() {
				self.setupItemEvents(editable);
				self.$editPanel.find('a#suggestQtip').html("Product Suggestions");
				self.$editPanel.find('a#suggestQtip').qtip({
					content:function() {
						var html = '<div class="bgboxGray"><h3 align="center">Suggestions</h3></div>';
						html += self.$editPanel.find("div#searchResult").html();
						return html;
					},
					hide: {event: 'click unfocus', fixed: true, effect:false, delay:0}
				});
			};
			self.typeaheadManager.doRequest(0);
		};
		
		base.setupItemEvents = function(editable) {
			var self = this;
			
			self.$editPanel.find('div#category').typeaheadsortable({itemSelector:'div.itemNamePreviewCat', sortedItemSelector:'div.elevatedCategory', editable: editable});
			self.$editPanel.find('div#brand').typeaheadsortable({itemSelector:'div.itemNamePreviewBrand', sortedItemSelector: 'div.elevatedBrand', editable: editable});
			
		};
		
		base.resetTypeahead = function() {
			var self = this;
			self.selectedRule = null;
			self.$typeaheadPanel.find('input.searchTextInput').val('');
			self.$typeaheadPanel.find('span#titleHeader').html('');
			self.$typeaheadPanel.find('span#titleText').html(base.options.moduleName);
			
		};
		
		base.resetRuleListTable = function() {
			var self = this;
			$(base.options.updateDialogSelector).dialog('destroy').remove();
			self.$typeaheadList.empty();
			self.$typeaheadPanel.find("div#fieldsBottomPaging, , div#fieldsTopPaging").empty();
		};
		
		base.setCurrentRuleList = function(list) {
			var self = this;
			var newMap = new Object();

			for(var i = 0; i<list.length; i++) {
				newMap[list[i].ruleId] = list[i];
			}

			self.currentRuleMap = newMap;
		};
		
		base.loadRuleList = function(matchType, page, functionHook) {
			var self = this;
			var searchText = self.$typeaheadPanel.find('input.searchTextInput').val();
			self.$typeaheadPanel.find('input.searchTextInput, a.searchButton').show();

			self.$typeaheadPanel.find('a.searchButtonList').hide();
			self.$typeaheadPanel.find("#submitForApproval").html('');
			self.$editPanel.hide();
			
			self.resetRuleListTable();
			TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, searchText, matchType, 1, page, base.options.rulePageSize, {
				callback: function(response){
					var data = response["data"];
					var list = data.list;

					self.setCurrentRuleList(list);
					self.$typeaheadList.html(self.getListTemplate());
					var $divList = self.$typeaheadList.find("div#itemList");
					$divList.find("div.items:not(#itemPattern1, #itemPattern2, #itemPattern3)").remove();
					if (list.length > 0){
						self.resetHeader();
						self.loadTypeaheadList($divList, list, base.options.startIndex, base.options.initialNoOfItems);
						self.initializeTypeaheadAction();
					}else{
						$empty = '<div id="empty" class="items txtAC borderB">File selected has no records to display</div>';
						$divList.append($empty);
						$("div#countSec").hide();
					}


					self.$typeaheadList.find("div#fieldsBottomPaging, div#fieldsTopPaging").paginate({
						currentPage: page, 
						pageSize: base.options.rulePageSize,
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
		};
		
		base.loadSplunkData = function() {
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
								self.loadItems($divList, list, base.options.startIndex, base.options.initialNoOfItems);
								base.options.startIndex = base.options.initialNoOfItems;
								$divList.off().on({
									scroll: function(e){
										if(list.length > base.options.startIndex){
											if ($divList[0].scrollTop == $divList[0].scrollHeight - $divList[0].clientHeight) {
												self.loadItems($divList, list, base.options.startIndex, base.options.itemsPerScroll);
												base.options.startIndex = base.options.startIndex + base.options.itemsPerScroll;
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
		};
		
		base.updateTypeaheadRule = function(typeaheadRule, customPreHook, customPostHook) {
			var preHook = customPreHook != null ? customPreHook : function(){};
			var postHook = customPostHook != null ? customPostHook : function(){};
			
			TypeaheadRuleServiceJS.updateRule(typeaheadRule, {
				callback: function(response){
					if(response && response.data != null)
						jAlert('Rule successfuly saved.', base.options.moduleName);
					else
						jAlert('Rule was not successfuly saved.', base.options.moduleName);
				},
				preHook:preHook,
				postHook:postHook,
				errorHandler:function(){
					jAlert('Rule was not successfuly saved.', base.options.moduleName);
				}
			});
		};
		
		base.deleteTypeaheadRule = function(typeaheadRule, customPreHook, customPostHook) {
			var preHook = customPreHook != null ? customPreHook : function(){};
			var postHook = customPostHook != null ? customPostHook : function(){};
			
			TypeaheadRuleServiceJS.deleteRule(typeaheadRule, {
				callback: function(response){
					if(response && response.data != null && response.data == true) {
						jAlert('Rule successfuly deleted.', base.options.moduleName);
					} else {
						jAlert('Rule was not successfuly deleted.', base.options.moduleName);
					}
				},
				preHook:preHook,
				postHook:postHook,
				errorHandler:function(){
					jAlert('Rule was not successfuly deleted.', base.options.moduleName);
				}
			});
		};
		
		base.loadTypeaheadList = function($divList, list, start, noOfItems, type) {
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
				
				$divItem.show();
				$divList.append($divItem);
			}

			$divList.find("div.items").removeClass("alt");
			$divList.find("div.items:even").addClass("alt");

			self.checkRuleStatus($divList);
		};
		
		base.checkRuleStatus = function($divList) {
			var self = this;

			$divList.find('input.ruleId').each(function() {
				var $element = $(this);
				var $row = $element.parent().parent();
				var $checkboxDiv = $row.find('label.iter');
				var $statusDiv = $row.find('label.status');

				DeploymentServiceJS.getRuleStatus(GLOBAL_storeId, base.options.moduleName, $element.val(), {
					callback: function(ruleStatus) {
						if(ruleStatus.locked) {
							$checkboxDiv.html(base.options.lockIcon);
						} else {
							$checkboxDiv.html('<input '+(self.selectedRuleList[$element.val()] != null ? 'CHECKED' : '')+' type="checkbox" id="'+$element.val()+'" class="ruleVisibility" value="'+$element.val()+'"/>');
							self.bindCheckboxAction($checkboxDiv);
							
						}
						
						$statusDiv.html(getRuleNameSubTextStatus(ruleStatus));

					},
					preHook: function() {
						$checkboxDiv.html(base.options.rectLoader);
					},
					postHook: function() {

					}
				});
			});
		};
		
		base.bindCheckboxAction = function($checkboxContainerElement) {
			var self = this;
			var $checkbox = $checkboxContainerElement.find(':checkbox');
			$checkbox.on("click", function() {
				var checkbox = $(this);
				var $row = $checkboxContainerElement.parent();
				
				if(checkbox.is(':checked')) {
					
					if(Object.keys(self.selectedRuleList).length >= base.options.rulePageSize) {
						checkbox.attr("checked", false);
						jAlert('You can only select up to '+base.options.rulePageSize+' rules.', base.options.moduleName);
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
		};
		
		base.updateTypeaheadList = function(array) {
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
								self.loadRuleList(0, base.options.rulePage, function(){jAlert(msg, base.options.moduleName);});
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
							jAlert('Please enter a valid integer value.', base.options.moduleName);
							
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
						self.loadRuleList(0, base.options.rulePage, function(){jAlert(msg, base.options.moduleName);});
					},
					errorHandler: function(e) {
						msg = 'An error occurred while processing the request. Please contact your system administrator.';
					}
				});
			}
		};
		
		base.initializeTypeaheadAction = function() {
			var self = this;
			
			self.$typeaheadList.find(base.options.updateDialogSelector).dialog({
				title: base.options.moduleName,
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

			self.$dialogObject = $(base.options.updateDialogSelector);

			self.$typeaheadList.find('a.dialogBtn').off().on({
				click: function(e) {
					if(Object.keys(self.selectedRuleList).length < 1) {
						jAlert('Please select a rule.', base.options.moduleName);
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
		};
		
		base.getRulesToUpdate = function() {
			var self = this;
			var keys = Object.keys(self.selectedRuleList);
			
			var array = new Array();
			
			for(var i=0; i<keys.length; i++) {
				array[array.length] = self.selectedRuleList[keys[i]];
			}
			
			return array;
		};
		
		base.initializeDialogContent = function() {
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
		};
		
		base.loadItems = function($divList, list, start, noOfItems, type){
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
		};
		
		base.resetHeader = function() {
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
		};
		
		base.getTypeaheadRuleList = function(page) { 
			var self = this;

			$("#keywordSidePanel").sidepanel({
				moduleName: base.options.moduleName,
				fieldName: "ruleName",
				page: page,
				pageSize: base.options.rulePageSize,
				headerText : "Typeahead Rule",
				customAddRule: true,
				showAddButton: allowModify,
				filterText: base.options.ruleFilterText,

				itemDataCallback: function(base, ruleName, page){
					base.options.rulePage = page;
					base.options.ruleFilterText = ruleName;
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
						jAlert("Search Keyword is required.",base.options.moduleName);
					}
					else if (!isAllowedName(popName)){
						jAlert("Search Keyword contains invalid value.",base.options.moduleName);
					}
					else {
						TypeaheadRuleServiceJS.addRule(GLOBAL_storeId, popName, {
							callback: function(response){
								if(response.status < 0) {
									jAlert(response.errorMessage.message, base.options.moduleName);
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
					iconPath = base.options.keywordIconPath;

					if ($.isNotBlank(iconPath)) item.ui.find(".itemIcon").html(iconPath);
				}
			});
		};
		
		base.getListTemplate = function() {
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
		};
		
		// Run initializer
		base.init();
	};
	
	
	
	$.typeahead.defaultOptions = {
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
			updateDialogSelector: "#updateDialog",
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
			elevateIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/page_white_get.png'/>",
			deleteIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/btn_delete_big.png'/>",
			dragIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_drag.png'/>",
			suggestionIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/page_find.png'/>",
			sortIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/table_sort.png'/>",
			searchReloadRate: 1000,
			selectedRuleList: new Object(),
	};
	
	$.fn.typeahead = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.typeahead(this, options));
			});
		};
	};

	$(document).ready(function() {
		$("#typeaheadPanel").typeahead();
	});
	
})(jQuery);	
