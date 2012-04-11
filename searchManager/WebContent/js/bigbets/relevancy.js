(function($){
	// TODO: Some codes should be converted to plugins
	var keywordPageSize = 5;
	var selectedRelevancy = null;
	var currentRelevancyPage = 1;
	var relevancyPageSize = 5;
	var schemaFieldsPageSize = 8;
	var keywordInRulePageSize = 3;
	var schemaFieldsTotal = 0;
	var schemaFieldsSearchText = "Enter Field Name";
	var relFieldMaxValue = 10;
	var sfExcFields = new Array();
	var sfSearchKeyword = "";
	var reloadRate = 500;

	var bqExcFields = new Array();
	var bqSearchKeyword = "";
	var bqFacetValuesPageSize = 5;
	var bqSearchText = "Enter Field Value";

	$(document).ready(function() { 

		/** BELOW: BF */
		setupFieldS4 = function(field){
			$('div[id="' + field.id + '"] a.editIcon, div[id="' + field.id + '"] input[type="text"]').qtip({
				content: { text: $('<div>'), title: { text: "Edit " + field.label, button: true }},
				show: {modal:true},
				events: { 
					render: function(e, api){
						var $contentHolder = $("div", api.elements.content).html($("#setupFieldValueS4").html());
						var currVal = $('div[id="' + field.id + '"] input[type="text"]').val();

					},
					hide: function (e, api){
						api.destroy();
					}
				}

			}).click(function(e) { e.preventDefault(); });
		},

		/** BELOW: BQ */
		setupFieldS3 = function(field){

			var updateFacetValueBarLength = function(content, fieldValue, fieldBoost){

				var $field = content.find('#fieldSelected' + $.formatAsId(fieldValue) + ' input[type="text"]');

				if ($field.val() > relFieldMaxValue){
					$field.val(relFieldMaxValue);
				}

				var perBar = ($field.val()/relFieldMaxValue)*100; 
				content.find('#fieldSelected' + $.formatAsId(fieldValue) + ' div.bargraph').attr("style", "background:#9d79b2; width:" + perBar + "%");
			};

			var removeSelectedFacetValue = function(content, fieldValue){
				if (confirm("Remove " + fieldValue + " from selection?")){
					content.find('tr#fieldSelected' + $.formatAsId(fieldValue)).remove();
					var idx = bqExcFields.indexOf(fieldValue);
					if (idx!=-1) bqExcFields.splice(idx,1);
					populateFieldValues(content, 1);
					content.find('tr.fieldSelectedItem').removeClass("alt");
					content.find('tr.fieldSelectedItem:even').addClass("alt");
				}
			};

			var populateSelectedFacetValue = function(content, fieldValue, boost){
				content.find("tr#fieldSelectedPattern").clone().prependTo("tbody#fieldSelectedBody").attr("id","fieldSelected"+$.formatAsId(fieldValue)).attr("style","display:float");
				content.find("tr#fieldSelected" + $.formatAsId(fieldValue) + " .txtHolder").html(fieldValue);
				content.find('tr#fieldSelected' + $.formatAsId(fieldValue)+ ' input[type="text"]').val(boost);
				updateFacetValueBarLength(content, fieldValue, boost);

				content.find('tr#fieldSelected' + $.formatAsId(fieldValue) + ' input[type="text"]').on({
					blur:function(e){
						updateFacetValueBarLength(content, fieldValue, $(e.target).val());
					},

					keypress:function(e){
						var charCode = (e.which) ? e.which : e.keyCode;
						if (charCode > 57 || (charCode > 31 && charCode < 46 || 
								charCode == 47) || (charCode == 46 && $(this).val().indexOf(".")>=0))
							return false;
						if (charCode == 13){ updateFacetValueBarLength(content, fieldValue, $(e.target).val());}
					}

				}, { name:fieldValue} );

				content.find('tr#fieldSelected' + $.formatAsId(fieldValue) + ' a.removeSelected').on({ click:function(e){removeSelectedFacetValue(content, fieldValue);}});
				content.find('tr.fieldSelectedItem').removeClass("alt");
				content.find('tr.fieldSelectedItem:even').addClass("alt");
			};

			var addFieldValuesPaging = function(content, page, totalItem){
				if(totalItem==0){
					content.find("div#fieldsBottomPaging").empty();
				}else{
					content.find("div#fieldsBottomPaging").paginate({
						currentPage: page, 
						pageSize: bqFacetValuesPageSize,
						totalItem: totalItem,
						type: 'short',
						pageStyle: 'style2',
						callbackText: function(itemStart, itemEnd, itemTotal){
							return itemStart + "-" + itemEnd + " of " + itemTotal;
						},
						pageLinkCallback: function(e){ populateFieldValues(content, e.data.page); },
						nextLinkCallback: function(e){ populateFieldValues(content, e.data.page+1);},
						prevLinkCallback: function(e){ populateFieldValues(content, e.data.page-1);},
						firstLinkCallback: function(e){populateFieldValues(content, 1);},
						lastLinkCallback: function(e){ populateFieldValues(content, e.data.totalPages);}
					});
				}

			};

			var populateFieldValues = function(content, page){

				var facetField = content.find("select#facetName option:selected").val();

				RelevancyServiceJS.getValuesByField(bqSearchKeyword, page, bqFacetValuesPageSize, facetField, bqExcFields, {
					callback: function(data){
						var list = data.list;
						content.find("ul#fieldListing > li").filter(":not(#fieldListingPattern)").remove();
						for (var i=0; i < data.totalSize ; i++){
							if ($.isNotBlank(list[i])){
								content.find("ul#fieldListing > li#fieldListingPattern").clone().appendTo("ul#fieldListing").attr("id","fieldListing" + $.formatAsId(list[i])).show();
								content.find("li#fieldListing" + $.formatAsId(list[i]) + " > span").text(list[i]);

								content.find('#fieldListing' + $.formatAsId(list[i]) + ' a').on({click: function(e){
									var element = e.data.name;

									if($.pushIfNotExist(bqExcFields, element, function(el){ return el === element; })){
										populateSelectedFacetValue(content,element,0);
										populateFieldValues(content, 1);
									}

								}},{name:list[i]});
							}
						}

						addFieldValuesPaging(content, page, data.totalSize);
					},
					preHook:function(){
						content.find("ul#fieldListing > li").filter(":not(#fieldListingPattern)").remove();
						content.find("div#fieldListing div#preloader").show();					
						content.find("div#fieldListing div#content").hide();					
					},
					postHook:function(){
						content.find("div#fieldListing div#preloader").hide();	
						content.find("div#fieldListing div#content").show();						
					}
				});
			};

			$('div[id="' + field.id + '"] a.editIcon, div[id="' + field.id + '"] input[type="text"]').qtip({
				content: { text: $('<div>'), title: { text: "Edit " + field.label, button: true }},
				show: {modal:true},
				events: { 
					render: function(e, api){
						var $content = $("div", api.elements.content).html($("#setupFieldValueS3").html());

						$content.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
						$content.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();
						bqSearchKeyword = ""; 
						bqExcFields = new Array();

						var currVal = $('div[id="' + field.id + '"] input[type="text"]').val();

						//TODO: initialize selected
						RelevancyServiceJS.getValuesByString(currVal,{
							callback: function(data){
								var list = data.boostQuery;

								// set to selected
								if (data!=null && list!=null){
									if (data.isCategoryOnly){
										$content.find('select[id="facetName"]').val("Category");
									}

									for (var boostQuery in list){
										var boost = list[boostQuery].boost.boost;
										var fieldName = list[boostQuery].expression.LValue.expression.LValue;
										$.pushIfNotExist(bqExcFields, $.stripSlashes(fieldName), function(el){ return el === $.stripSlashes(fieldName); })		
										populateSelectedFacetValue($content, $.stripSlashes(fieldName), boost);
									}
								}

								populateFieldValues($content, 1);

								$content.find('a#clearBtn').on({
									click: function(e){
										$content.find('.fieldSelectedItem input[type="text"]').val(0);
										$content.find('.fieldSelectedItem .bargraph').attr("style","width:0%");
									}
								});

								$content.find('a#applyBtn').on({
									click: function(e){
										var finalVal = "";

										$content.find('.fieldSelectedItem').not('#fieldSelectedPattern').each(function(index, value){

											var val = $.addSlashes($.trim($(value).find(".txtHolder").html())); 
											if (index >0) finalVal += " ";											
											finalVal += $content.find("select#facetName").val();
											finalVal += ":(";

											finalVal += val;
											finalVal += ")";
											finalVal += '^';
											finalVal += $(value).find('input[type="text"]').val();
										});

										api.hide();

										$('div[id="' + field.id + '"] input[type="text"]').val($.stripSlashes(finalVal));
									}
								});

								$content.find('input[id="searchBoxField"]').val(bqSearchText).on({
									blur: function(e){if ($.trim($(e.target).val()).length == 0) $(e.target).val(bqSearchText);},
									focus: function(e){if ($.trim($(e.target).val()) == bqSearchText) $(e.target).val("");},
									keyup: function(e){ 
										setTimeout(function(){ 
											bqSearchKeyword = $(e.target).val();
											populateFieldValues($content, 1);
										}, reloadRate);  	
									}
								});

								$content.find('select[id="facetName"]').on({
									change: function(e){
										$content.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();
										populateFieldValues($content, 1);
									}
								});

							}
						});
					},
					hide: function (e, api){
						api.destroy();
					}
				}

			}).click(function(e) { e.preventDefault(); });
		},

		/** BELOW: MM */
		setupFieldS2 = function(field){
			$('div[id="' + field.id + '"] a.editIcon, div[id="' + field.id + '"] input[type="text"]').qtip({
				content: { text: $('<div>'), title: { text: "Edit " + field.label, button: true }},
				show: {modal:true},
				events: { 
					render: function(e, api){
						var $contentHolder = $("div", api.elements.content).html($("#setupFieldValueS2").html());
						var currVal = $('div[id="' + field.id + '"] input[type="text"]').val();

						$("ul#multiRule > li").not("#multiRulePattern").remove();

						RelevancyServiceJS.getMinShouldMatch(currVal, {
							callback: function(data){
								if (data!=null){
									if(data.isSingleRule){
										$contentHolder.find('select[id="type"]').val("sr");
										$contentHolder.find('div.MM').filter('div#sr').show();
										$contentHolder.find('div.MM').not('div#sr').hide();
										$contentHolder.find('div#sr input[id="singleRuleFieldMatch"]').val(data.parameters[0].match);
									}else{
										$contentHolder.find('select[id="type"]').val("mr");
										$contentHolder.find('div.MM').filter('div#mr').show();
										$contentHolder.find('div.MM').not('div#mr').hide();

										for(var i=0; i< data.parameters.length; i++){
											var count = i+1;
											$contentHolder.find('li#multiRulePattern').clone().insertAfter($('li#multiRulePattern')).attr("id","multiRule" + count);
											$contentHolder.find('li#multiRule' + count + ' input#ruleFieldCondition').val(data.parameters[i].condition);
											$contentHolder.find('li#multiRule' + count + ' input#ruleFieldMatch').val(data.parameters[i].match);

											$contentHolder.find('li#multiRule' + count + ' a#deleteRule').show().on({
												click: function(dEvt){ $(dEvt.target).parents('li.multiRuleItem').remove(); }
											});

											$contentHolder.find('li#multiRule' + count + ' label#ruleField').html("");
											$contentHolder.find('li#multiRule' + count + ' a#addRule').remove();
										}
									}
								}

								$contentHolder.find('li.multiRuleItem input#ruleFieldCondition').on({
									keypress:function(e){
										var charCode = (e.which) ? e.which : e.keyCode;
										if (charCode > 31 && (charCode < 48 || charCode > 57))
											return false;
									}

								});

								$contentHolder.find('li.multiRuleItem input#ruleFieldMatch, input#singleRuleFieldMatch').on({
									keypress:function(e){
										var charCode = (e.which) ? e.which : e.keyCode;
										var currVal = $(e.target).val();
										if(($.isBlank(currVal)|| currVal.indexOf("-") == -1) && charCode == 45) return true;
										if(charCode == 8) return true;
										if($.isNotBlank(currVal) && currVal.indexOf(".") == -1 && charCode == 46) return true;
										if($.isNotBlank(currVal) && $.isNumeric(currVal) && !$.endsWith(currVal,'.') && currVal.indexOf("%") == -1 && charCode == 37) return true;
										if(currVal.indexOf("%") == -1 && (charCode < 32 || (charCode > 47 && charCode < 58))){
											return true;
										}
										else{
											return false;
										}
									}
								});

								$contentHolder.find('select[id="type"]').on({
									change: function(){
										$contentHolder.find('div.MM').filter('#' + $(this).val()).show();
										$contentHolder.find('div.MM').not('#' + $(this).val()).hide();
									}
								});

								$contentHolder.find('li#multiRulePattern a#deleteRule').hide();

								$contentHolder.find('a#addRule').on({
									click: function(e){
										var condition = $contentHolder.find('li#multiRulePattern input#ruleFieldCondition').val();
										var match = $contentHolder.find('li#multiRulePattern input#ruleFieldMatch').val();
										if ($.isNotBlank(condition) && $.isNotBlank(match)){
											var count = $contentHolder.find('li.multiRuleItem').length;
											$contentHolder.find('li#multiRulePattern').clone().insertAfter($('li#multiRulePattern')).attr("id","multiRule" + count);
											$contentHolder.find('li#multiRule' + count + ' a#deleteRule').show().on({
												click: function(dEvt){ $(dEvt.target).parents('li.multiRuleItem').remove(); }
											});
											$contentHolder.find('li#multiRule' + count + ' label#ruleField').html("");
											$contentHolder.find('li#multiRule' + count + ' a#addRule').remove();

											$contentHolder.find('li#multiRulePattern input#ruleFieldCondition').val("");
											$contentHolder.find('li#multiRulePattern input#ruleFieldMatch').val("");
										}
									}
								});		

							}
						});

						$contentHolder.find('a#clearBtn').on({
							click: function(e){
								var ruleType = $contentHolder.find('select[id="type"]').val();
								if (ruleType=="sr"){
									$contentHolder.find('input[id="singleRuleFieldMatch"]').val("");
								}else if(ruleType=="mr"){
									$contentHolder.find('ul#multiRule >li:not(#multiRulePattern)').remove();
								}
							}
						});

						$contentHolder.find('a#applyBtn').on({
							click: function(e){
								var ruleType = $contentHolder.find('select[id="type"]').val();
								var minToMatchVal = "";

								if (ruleType=="sr"){
									minToMatchVal = $contentHolder.find('input[id="singleRuleFieldMatch"]').val();
								}else if(ruleType=="mr"){
									$.each($contentHolder.find('ul#multiRule >li:not(#multiRulePattern)'), function(index, value){
										if(index > 0) minToMatchVal += " ";
										minToMatchVal += $(this).find('input[id="ruleFieldCondition"]').val();
										minToMatchVal += $(this).find('select[id="ruleFieldMid"]').val();
										minToMatchVal += $(this).find('input[id="ruleFieldMatch"]').val();
									});
								}

								api.hide();
								$('div[id="' + field.id + '"] input[type="text"]').val(minToMatchVal);
							}
						});

					},
					hide: function (e, api){
						api.destroy();
					}
				}

			}).click(function(e) { e.preventDefault(); });
		};

		/** BELOW: QF and PF */
		setupFieldS1 = function(field){
			var sfPage = 1;
			var sfContentId = selectedRelevancy.relevancyId;
			sfExcFields = new Array();

			addSchemaFieldsPaging = function(content, page){
				if(schemaFieldsTotal==0){
					content.find("div#fieldsBottomPaging").empty();
				}else{

					content.find("div#fieldsBottomPaging").paginate({
						currentPage:page, 
						pageSize:schemaFieldsPageSize,
						totalItem:schemaFieldsTotal,
						type: 'short',
						pageStyle: 'style2',
						callbackText: function(itemStart, itemEnd, itemTotal){
							return itemStart + "-" + itemEnd + " of " + itemTotal;
						},
						pageLinkCallback: function(e){populateSchemaFields(content, e.data.page);},
						nextLinkCallback: function(e){populateSchemaFields(content, e.data.page+1);},
						prevLinkCallback: function(e){populateSchemaFields(content, e.data.page-1);},
						firstLinkCallback: function(e){populateSchemaFields(content, 1);},
						lastLinkCallback: function(e){populateSchemaFields(content, e.data.totalPages);}
					});
				}
			},

			updateBarLength = function(content, fieldName, fieldBoost){

				var $field = content.find('#fieldSelected_' + fieldName+ ' input[type="text"]');

				if ($field.val() > relFieldMaxValue){
					$field.val(relFieldMaxValue);
				}

				var perBar = ($field.val()/relFieldMaxValue)*100; 

				content.find('#fieldSelected_' + fieldName + ' div.bargraph').attr("style", "background:#9d79b2; width:" + perBar + "%");

			},

			removeSelected = function(content, fieldName){
				if (confirm("Remove " + fieldName + " from selection?")){
					content.find('tr#fieldSelected_' + fieldName).remove();
					var idx = sfExcFields.indexOf(fieldName);
					if (idx!=-1) sfExcFields.splice(idx,1);
					populateSchemaFields(content, 1);
					content.find('tr.fieldSelectedItem').removeClass("alt");
					content.find('tr.fieldSelectedItem:even').addClass("alt");
				}
			},

			populateSelectedSchemaField = function(content, fieldName, boost){
				content.find("tr#fieldSelectedPattern").clone().prependTo("tbody#fieldSelectedBody").attr("id","fieldSelected_"+fieldName).attr("style","display:float");
				content.find("tr#fieldSelected_" + fieldName + " .txtHolder").html(fieldName);
				content.find('tr#fieldSelected_' + fieldName+ ' input[type="text"]').val(boost);
				updateBarLength(content, fieldName, boost);

				content.find('tr#fieldSelected_' + fieldName + ' input[type="text"]').on({
					blur:function(e){
						updateBarLength(content, fieldName, $(e.target).val());
					},

					keypress:function(e){
						var charCode = (e.which) ? e.which : e.keyCode;
						if (charCode > 57 || (charCode > 31 && charCode < 46 || 
								charCode == 47) || (charCode == 46 && $(this).val().indexOf(".")>=0))
							return false;
						if (charCode == 13){ updateBarLength(content, fieldName, $(e.target).val());}
					}

				}, { name:fieldName} );

				content.find('tr#fieldSelected_' + fieldName + ' a.removeSelected').on({ click:function(e){removeSelected(content, fieldName);}});
				content.find('tr.fieldSelectedItem').removeClass("alt");
				content.find('tr.fieldSelectedItem:even').addClass("alt");
			},

			populateSchemaFields = function(content, page){
				RelevancyServiceJS.getIndexedFields(page, schemaFieldsPageSize, sfSearchKeyword, sfExcFields, {
					callback:function(data){
						var list = data.list;
						schemaFieldsTotal = data.totalSize;

						content.find("ul#fieldListing > li").not("#fieldListingPattern").remove();

						for (var i=0; i< data.totalSize; i++){
							if(list[i]!=null){
								content.find("#fieldListingPattern").clone().appendTo("ul#fieldListing").attr("id","fieldListing_"+i).attr("style","display:float");
								content.find('#fieldListing_' + i + ' span').html(list[i].name);

								content.find('#fieldListing_' + i + ' a').on({click: function(e){
									var element = e.data.name;

									if($.pushIfNotExist(sfExcFields, element, function(el){ return el === element; })){
										populateSelectedSchemaField(content, element, 0);
										populateSchemaFields(content, 1);
									}

								}},{name:list[i].name});
							}	
						}
						
						content.find('span#sfCount').html(schemaFieldsTotal + " Record" + (schemaFieldsTotal > 1 ? "s":""));
						var selectedCount = content.find('tr.fieldSelectedItem:not(#fieldSelectedPattern)').length;
						content.find('span#sfSelectedCount').html(selectedCount + " Record" + (selectedCount > 1 ? "s":""));
						addSchemaFieldsPaging(content, page);
					},
					preHook:function(){
						content.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
						content.find("div#fieldListing div#preloader").show();					
						content.find("div#fieldListing div#content").hide();					
					},
					postHook:function(){
						content.find("div#fieldListing div#preloader").hide();	
						content.find("div#fieldListing div#content").show();						
					}
				});
			};

			$('div[id="' + field.id + '"] a.editIcon, div[id="' + field.id + '"] input[type="text"]').qtip({
				content: { text: $('<div>'), title: { text: "Edit " + field.label, button: true }},
				show: {modal:true},
				events: { 
					render: function(e, api){
						var $contentHolder = $("div", api.elements.content).html($("#setupFieldValueS1").html());

						$contentHolder.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
						$contentHolder.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();
						sfSearchKeyword = "";

						var currVal = $('div[id="' + field.id + '"] input[type="text"]').val();

						RelevancyServiceJS.getQueryFields(currVal, {
							callback:function(data){
								var qfList = data.list;

								for (var i=0; i< data.totalSize; i++){
									var name = qfList[i].field.name;
									var boost = qfList[i].boost.boost;

									if ($.isNotBlank(name)){
										$.pushIfNotExist(sfExcFields, name, function(el){ return el === name; });
										populateSelectedSchemaField($contentHolder, name, boost);
									}
								}	

								populateSchemaFields($contentHolder,1);
							},
							preHook: function(){
								$contentHolder.find("tbody#fieldSelectedPattern > tr").not("#fieldSelectedPattern").remove();
							}
						});


						$contentHolder.find('a#clearBtn').on({
							click: function(e){
								$('.fieldSelectedItem input[type="text"]').val(0);
								$('.fieldSelectedItem .bargraph').attr("style","width:0%");
							}
						});

						$contentHolder.find('a#applyBtn').on({
							click: function(e){
								var finalVal = "";

								$contentHolder.find('.fieldSelectedItem').not('#fieldSelectedPattern').each(function(index, value){
									if (index > 0) finalVal += " ";
									finalVal += $(value).find(".txtHolder").html();
									finalVal += '^';
									finalVal += $(value).find('input[type="text"]').val();
								});

								api.hide();

								$('div[id="' + field.id + '"] input[type="text"]').val(finalVal);
							}
						});

						$contentHolder.find('input[id="searchBoxField"]').val(schemaFieldsSearchText).on({
							blur: function(e){if ($.trim($(e.target).val()).length == 0) $(e.target).val(schemaFieldsSearchText);},
							focus: function(e){if ($.trim($(e.target).val()) == schemaFieldsSearchText) $(e.target).val("");},
							keyup: function(e){ 
								setTimeout(function(){ 
									sfSearchKeyword = $(e.target).val();
									populateSchemaFields($contentHolder,1);
								}, reloadRate);  	
							}
						});

					},
					hide: function (e, api){
						api.destroy();
					}
				}

			}).click(function(e) { e.preventDefault(); });
		};


		/** BELOW: Relevancy Landing Page */
		refreshRelevancyList = function(page){
			$("#relevancySidePanel").empty();
			$("#relevancySidePanel").sidepanel({
				fieldId: "relevancyId",
				fieldName: "relevancyName",
				page: page,
				type: 'relevancy',
				pageSize: relevancyPageSize,
				headerText : "Ranking Rule",
				searchText : "Enter Name",
				itemDataCallback: function(base, keyword, page){
					RelevancyServiceJS.getAllByName(keyword, page, base.options.pageSize,{
						callback: function(data){
							base.populateList(data);
							base.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
					});
				},

				itemOptionCallback: function(base, id, name){
					var icon = "";
					var suffixId = $.escapeQuotes($.formatAsId(id));

					icon = '<a id="clone' + suffixId + '" href="javascript:void(0);"><img src="../images/icon_clone.png" class="marRL3"></a>';
					icon += '<a id="edit' + suffixId + '" href="javascript:void(0);"><img src="../images/page_edit.png"></a>';

					base.$el.find('#itemPattern' + suffixId + ' div.itemLink').html($(icon));

					base.$el.find('#itemPattern' + suffixId + ' div.itemLink a#clone' + suffixId).qtip({
						content: {
							text: $('<div/>'),
							title: { text: 'Clone Relevancy', button: true }
						},
						show: { modal: true },
						events: { 
							render: function(rEvt, api){
								var $contentHolder = $("div", api.elements.content).html($("#addRelevancyTemplate").html());

								$contentHolder.find('input, textarea').each(function(index, value){ $(this).val("");});

								if ($.isNotBlank(name)) $contentHolder.find('input[id="popName"]').val("Copy of " + name);

								$contentHolder.find('input[name="popStartDate"]').attr('id', 'popStartDate');
								$contentHolder.find('input[name="popEndDate"]').attr('id', 'popEndDate');

								var popDates = $contentHolder.find("#popStartDate, #popEndDate").datepicker({
									defaultDate: "+1w",
									showOn: "both",
									buttonImage: "../images/icon_calendar.png",
									buttonImageOnly: true,
									onSelect: function(selectedDate) {
										var option = this.id == "popStartDate" ? "minDate" : "maxDate",
												instance = $(this).data("datepicker"),
												date = $.datepicker.parseDate( instance.settings.dateFormat ||
														$.datepicker._defaults.dateFormat, selectedDate, instance.settings);
										popDates.not(this).datepicker("option", option, date);
									}
								});

								$contentHolder.find('a#addButton').on({
									click: function(e){
										var popName = $.trim($contentHolder.find('input[id="popName"]').val());
										var popStartDate = $.trim($contentHolder.find('input[id="popStartDate"]').val()); 
										var popEndDate =  $.trim($contentHolder.find('input[id="popEndDate"]').val()); ; 
										var popDescription =  $.trim($contentHolder.find('textarea[id="popDescription"]').val()); ; 

										if ($.isBlank(popName)){
											alert("Relevancy name is required");
											return;
										}

										var addedId = "";

										RelevancyServiceJS.addRelevancyByCloning(id, popName, popStartDate, popEndDate, popDescription, {
											callback:function(relevancyId){
												api.hide();
												getRelevancy(relevancyId, popName);
												refreshRelevancyList(1);
												addedId = relevancyId;
											},
											postHook:function(e){
												addUpdateField(addedId, "q.alt", "*:*");
											}
										});


									}
								});

								$contentHolder.find('a#clearButton').on({
									click: function(e){
										$contentHolder.find('input[type="text"], textarea').val("");
									}
								});
							}
						}
					});

					base.$el.find('#itemPattern' + suffixId + ' div.itemLink a#edit' + suffixId).on({
						click: function(e){
							getRelevancy(id, name);
						}
					},{});

				},

				itemAddCallback: function(base, name){ addRelevancy(name); },
				itemNameCallback: function(e){ getRelevancy(e.data.id, e.data.name); },
				pageChangeCallback: function(n){ currentRelevancyPage = n; }
			});
		};

		refreshKeywordList = function(page){
			$("#keywordSidePanel").empty();
			$("#keywordSidePanel").sidepanel({
				fieldId: "keywordId",
				fieldName: "keyword",
				page: page,
				pageSize: keywordPageSize,
				headerText : "Ranking Rule Keyword",
				searchText : "Enter Keyword",
				itemDataCallback: function(base, keyword, page){
					StoreKeywordServiceJS.getAllKeyword(keyword, page, base.options.pageSize,{
						callback: function(data){
							base.populateList(data);
							base.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
					});
				},
				itemOptionCallback: function(base, id, name){
					var suffixId = $.escapeQuotes($.formatAsId(id));
					
					RelevancyServiceJS.getRelevancyCount(name, {
						callback: function(data){
							base.$el.find('#itemPattern' + suffixId + ' div.itemLink a').html((data == 0) ? "-" :(data == 1) ? "1 Item" : data + " Items");
						
							if (data > 0)
							base.$el.find('#itemPattern' + suffixId + ' div.itemLink a').qtip({
								content: {
									text: $('<div/>'),
									title: { text: 'Ranking Rule for ' + name, button: true }
								},
								show: { modal: true },
								events: { 
									render: function(rEvt, api){
										var $content = $("div", api.elements.content).html($("#sortRankingPriorityTemplate").html());
										RelevancyServiceJS.getRelevancy(name, {
											callback: function(data){
												var list = data.list;
												
												$content.find("ul#rankingRuleListing > li:not(#rankingRulePattern)").remove();
												
												for(var i=0; i<data.totalSize; i++){
													var suffixId = $.escapeQuotes($.formatAsId(list[i].relevancy.relevancyId));
													$content.find("li#rankingRulePattern").clone().appendTo("ul#rankingRuleListing").attr("id", "rankingRule" + suffixId).show();
													$content.find("li#rankingRule" + suffixId + " span.rankingRuleName").html(list[i].relevancy.relevancyName);
													$content.find("li#rankingRule" + suffixId + " span.rankingRuleName").attr("id", list[i].relevancy.relevancyId)
												}
												
												$content.find("ul#rankingRuleListing > li").removeClass("alt");
												$content.find("ul#rankingRuleListing > li:nth-child(even)").addClass("alt");
												
												$content.find("ul#rankingRuleListing").sortable({ 
													handle : '.handle',
													cursor : 'move',
													start: function(e, ui) {
														ui.item.data('start_pos', ui.item.index());
													},     
													change: function(e, ui) {
														var index = ui.placeholder.index();
														if (ui.item.data('start_pos') < index ) {
															$(this).find('li:nth-child(' + index + ') div').addClass('highlight');
														} else {
															$(this).find('li:eq(' + (index + 1) + ') div').addClass('highlight');
														}		    
													},
													update: function(e, ui) {
														$(this).find('li div').removeClass('highlight');
														$(this).find('li').removeClass("alt");
														$(this).find('li:nth-child(even)').addClass("alt");
													},
													stop: function(e, ui) {
														var sourceIndex = (ui.item.data('start_pos'));
														var destinationIndex = (ui.item.index());
														
														//TODO: move processing to SP
														var relIds = new Array();
														
														$(this).find('li:visible span.rankingRuleName').each(function(index, value){
															relIds.push($(value).attr("id"));
															alert($(value).attr("id"));
														});
														
														RelevancyServiceJS.updateRelevancyKeyword(relIds, name, {
															callback: function(data){
																
															}
														});
													}
												});
											},
											preHook: function(){
												
											}
										});
									}
								}
							});
						},
						preHook: function(){ 
							base.$el.find('#itemPattern' + $.escapeQuotes($.formatAsId(id)) + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
						}
					});
					
				},
				pageChangeCallback: function(n){ }
			});
		};

		refreshKeywordInRuleList = function(page){
			$("#keywordInRulePanel").empty();
			$("#keywordInRulePanel").sidepanel({
				fieldId: "keywordId",
				fieldName: "keyword",
				page: page,
				region: "content",
				pageStyle: "style2",
				pageSize: keywordInRulePageSize,
				headerText : "Using This Rule",
				searchText : "Enter Keyword",
				itemDataCallback: function(base, keyword, page){
					RelevancyServiceJS.getKeywordInRule(selectedRelevancy.relevancyId, keyword, page, keywordInRulePageSize, {
						callback: function(data){
							base.populateList(data);
							base.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ base.prepareList(); }
					});
				},
				itemOptionCallback: function(base, id, name){
					var icon = "";
					var suffixId = $.escapeQuotes($.formatAsId(id));

					icon = '<a id="delete' + suffixId + '" href="javascript:void(0);"><img src="../images/icon_delete2.png"></a>';
					base.$el.find('#itemPattern' + suffixId + ' div.itemLink').html($(icon));
					
					base.$el.find('#itemPattern' + suffixId + ' div.itemLink a#delete' + suffixId).on({
						click: function(e){
							if (confirm('Remove "' + name + '" in ' + selectedRelevancy.relevancyName  + '?'))
							RelevancyServiceJS.deleteKeywordInRule(selectedRelevancy.relevancyId, name,{
								callback:function(data){
									refreshKeywordInRuleList(1);
								},
								preHook: function(){ base.prepareList(); }
							});
						}
					});
				},
				itemAddCallback: function(base, keyword){
					RelevancyServiceJS.addKeywordToRule(selectedRelevancy.relevancyId, keyword, {
						callback: function(data){
							alert(keyword + " added successfully");
							refreshKeywordInRuleList(1);
							refreshKeywordList(1);
						},
						preHook: function(){ base.prepareList(); }
					});
				}
			});
		};

		addRelevancy = function(name){

			$("a#addButton").qtip({
				content: {
					text: $('<div/>'),
					title: { text: 'New Relevancy', button: true }
				},
				events: { 
					render: function(e, api){
						var $contentHolder = $("div", api.elements.content).html($("#addRelevancyTemplate").html());

						$contentHolder.find('input, textarea').each(function(index, value){ $(this).val("");});

						if ($.isNotBlank(name)) $contentHolder.find('input[id="popName"]').val(name);

						$contentHolder.find('input[name="popStartDate"]').attr('id', 'popStartDate');
						$contentHolder.find('input[name="popEndDate"]').attr('id', 'popEndDate');

						var popDates = $contentHolder.find("#popStartDate, #popEndDate").datepicker({
							defaultDate: "+1w",
							showOn: "both",
							buttonImage: "../images/icon_calendar.png",
							buttonImageOnly: true,
							onSelect: function(selectedDate) {
								var option = this.id == "popStartDate" ? "minDate" : "maxDate",
										instance = $(this).data("datepicker"),
										date = $.datepicker.parseDate( instance.settings.dateFormat ||
												$.datepicker._defaults.dateFormat, selectedDate, instance.settings);
								popDates.not(this).datepicker("option", option, date);
							}
						});

						$contentHolder.find('a#addButton').on({
							click: function(e){
								var popName = $.trim($contentHolder.find('input[id="popName"]').val());
								var popStartDate = $.trim($contentHolder.find('input[id="popStartDate"]').val()); 
								var popEndDate =  $.trim($contentHolder.find('input[id="popEndDate"]').val()); ; 
								var popDescription =  $.trim($contentHolder.find('textarea[id="popDescription"]').val()); ; 

								if ($.isBlank(popName)){
									alert("Relevancy name is required");
									return;
								}

								var addedId = "";
								RelevancyServiceJS.addRelevancy(popName, popDescription, popStartDate, popEndDate, {
									callback:function(relevancyId){
										api.hide();
										getRelevancy(relevancyId, popName);
										refreshRelevancyList(1);
										addedId = relevancyId;
									},
									postHook:function(e){
										addUpdateField(addedId, "q.alt", "*:*");
									}
								});


							}
						});

						$contentHolder.find('a#clearButton').on({
							click: function(e){
								$contentHolder.find('input[type="text"], textarea').val("");
							}
						});

					}
				,
				hide: function (e, api){
					sfExcFields = new Array();
					api.destroy();
				}
				}

			}).click(function(e) { e.preventDefault(); });
		};

		getRelevancy = function(id, name){
			RelevancyServiceJS.getById(id,{
				callback: function(data){
					selectedRelevancy = data;
					showRelevancyFields();
					setRelevancyFields();
				},
				preHook: function(){
					prepareRelevancyFields();
				}
			});
		};

		//Start TODO: Convert this to plugin
		prepareKeywordInRule = function(){
			$("div#keywordInRule > div#preloader").show();
			$("div#keywordInRuleContent ul > li:not(#keywordInRulePattern)").remove();
			$("div#keywordInRuleContent > div#keywordInRulePagingBottom").hide();
		};

		populateKeywordInRule = function(page){
			RelevancyServiceJS.getKeywordInRule(selectedRelevancy.relevancyId, page, keywordInRulePageSize, {
				callback: function(data){
					var list = data.list;

					for (var i=0; i<data.totalSize ; i++){
						var keyword = list[i].keyword.keyword;
						$('li#keywordInRulePattern').clone().appendTo('div#keywordInRuleContent ul').attr("id","keywordInRule" + $.formatAsId(keyword)).show();
						$('li#keywordInRule' + $.formatAsId(keyword) + ' > label').html(keyword);

						$('li#keywordInRule' + $.formatAsId(keyword) + ' > a.deleteKeywordInRuleBtn').on({
							click: function(e){
								RelevancyServiceJS.deleteKeywordInRule(selectedRelevancy.relevancyId, keyword, {
									callback:function(data){
										//TODO: delete keyword-relevancy relationship
									},
									postHook:function(){
										populateKeywordInRule(1);
									}
								});
							}
						}, {keyword: keyword});			
					}
				},
				preHook:function(){
					prepareKeywordInRule();
				},
				postHook:function(){
					$("div#keywordInRule > div#preloader").hide();
					$("div#keywordInRuleContent > div#keywordInRulePagingBottom").show();
				}
			});
		};
		//End

		setRelevancyFields = function(){
			var relevancy = selectedRelevancy;

			$('div#relevancy input').each(function(index, value){ $(this).val(""); });

			$('div#relevancy input[id="name"]').val(relevancy.relevancyName);
			$('div#relevancy textarea[id="description"]').val(relevancy.description);
			$('div#relevancy input[name="startDate"]').val(relevancy.formattedStartDate);
			$('div#relevancy input[name="endDate"]').val(relevancy.formattedEndDate);

			$('div#relevancy input[name="startDate"]').attr('id', 'startDate');
			$('div#relevancy input[name="endDate"]').attr('id', 'endDate');

			var dates = $("div#relevancy #startDate, div#relevancy #endDate").datepicker({
				defaultDate: "+1w",
				showOn: "both",
				buttonImage: "../images/icon_calendar.png",
				buttonImageOnly: true,
				onSelect: function(selectedDate) {
					var option = this.id == "startDate" ? "minDate" : "maxDate",
							instance = $(this).data("datepicker"),
							date = $.datepicker.parseDate(
									instance.settings.dateFormat ||
									$.datepicker._defaults.dateFormat,
									selectedDate, instance.settings);
					dates.not(this).datepicker("option", option, date);
				}
			});

			refreshKeywordInRuleList(1);

			for (var field in relevancy.parameters){
				$('div[id="' + field + '"] input[type="text"]').val(relevancy.parameters[field]);
			}

			getRelevancyField = function(e){
				var field = new Object();
				var $parent = $(e.target).parents('div.AlphaCont');
				field.id = $parent.attr("id");
				field.value = $parent.find('input[type="text"]').val();
				field.label = $parent.find('span[id="fieldLabel"]').html();
				return field; 
			};

			$('div#relevancy .saveIcon').off('click').on({
				click:function(e){
					var relevancy = selectedRelevancy;
					var relevancyId = relevancy.relevancyId;
					var field = getRelevancyField(e);
					addUpdateField(relevancyId, field.id, field.value);
				}
			});

			showGraph = function(e){
				var field = getRelevancyField(e);
				if ($.inArray(field.id,["qf", "pf"]) >= 0) setupFieldS1(field);
				if (field.id=="mm") setupFieldS2(field);
				if (field.id=="bq") setupFieldS3(field);
				//if (field.id=="bf") setupFieldS4(field);
			};

			$('div#relevancy .editIcon, div.AlphaCont input[type="text"]').off('click').on({click: showGraph});
			$('div.AlphaCont input[type="text"]').off('focus').on({focus:showGraph });
			$('div#relevancy a#deleteButton').off('click').on({click:deleteRelevancy});
			$('div#relevancy a#saveButton').off('click').on({click:saveRelevancy});
			
			//TODO: Message Resource
			$('a.infoIcon').qtip({
				content: { 
					text: $('<div>')
				},
				show:{ modal:true },
				style:{
					width:'150px'
				},
				events: {
					render:function(rEvt, api){
						var $content = $("div", api.elements.content);
						$content.html("");
					},
					
					show:function(rEvt, api){
						var $content = $("div", api.elements.content);
						var field =	api.elements.target.parents('div.AlphaCont').attr("id");
						var text = "";
						
						if (field==="qf") text = 'List of fields and the "boosts" to associate with each of them';
						if (field==="bf") text = 'Functions that will be included in the user\'s query to influence the score';
						if (field==="pf") text = 'Used as a boost in cases where the score of documents matched appear in close proximity';
						if (field==="bq") text = 'A raw query string that will be included with the user\'s query to influence the score';
						if (field==="mm") text = 'The minimum number of words specified in the keyword phrase that should match';
						if (field==="qs") text = 'Amount of slop on phrase queries explicitly included in the user\'s query string (affects matching)';
						if (field==="ps") text = 'Amount of slop (distance between words) on phrase queries built for "pf" fields (affects boosting)';
						if (field==="tie") text = 'in case documents have the same score, this is used to determine which document is prioritized. Computation is done via (score of matching clause with the highest score) + ( (tie paramenter) * (scores of any other matching clauses) ) ';
						
						$content.html(text);
						
					}
				}
			});
			
			// add field restrictions
			$('div[id="q.alt"] input[type="text"]').attr("readonly", "readonly").on({
				focus: function(e){alert("Contact administrator to modify this field");}
			});

			$('div[id="q.alt"]').hide();

			$('div[id="tie"] input[type="text"]').off('blur focus keypress').on({
				blur: function(e){if ($.trim($(e.target).val()).length == 0) $(e.target).val(relevancy.parameters["tie"]);},
				focus: function(e){if ($.trim($(e.target).val()) == relevancy.parameters["tie"]) $(e.target).val("");},
				keypress:function(e){
					var charCode = (e.which) ? e.which : e.keyCode;

					if(charCode == 46 && ($.trim($(e.target).val()) == 0 || $.isBlank($(e.target).val()))) return true;
					if(charCode == 8 || ($.inArray(charCode,[48,96])!=-1 && $.isBlank($(e.target).val()))) return true;
					if($.isNotBlank($(e.target).val()) && $(e.target).val().indexOf('.') != -1 && (charCode < 32 || (charCode > 47 && charCode < 58))) return true;

					alert("Tie value should be between 0 - 1");
					return false;

				}
			});

			$('div[id="qs"] input[type="text"], div[id="ps"] input[type="text"]').off('blur focus keypress').on({
				blur: function(e){if ($.trim($(e.target).val()).length == 0) $(e.target).val(relevancy.parameters[getRelevancyField(e).id]);},
				focus: function(e){if ($.trim($(e.target).val()) == relevancy.parameters[getRelevancyField(e).id]) $(e.target).val("");},
				keypress:function(e){
					var charCode = (e.which) ? e.which : e.keyCode;

					if(charCode == 8) return true;
					if($.inArray(charCode,[48,96]) != -1 && $.isBlank($(e.target).val())) return false;
					if(charCode < 32 || (charCode > 47 && charCode < 58)) return true;

					alert(getRelevancyField(e).label + " value should be numeric");
					return false;
				}
			});

		};

		showRelevancyFields = function(){
			updateHeaderText();
			$("#noSelected").attr("style", selectedRelevancy!=null ? "display:none":"display:float");
			$("#relevancy").attr("style", selectedRelevancy==null ? "display:none":"display:float");
			$("#preloader").hide();
		};

		updateHeaderText = function(){
			$("#subTitleText").html(selectedRelevancy==null ? "" : ' for <span class="fLblue fnormal">' + selectedRelevancy.relevancyName + '</span>');		
		};

		prepareRelevancyFields = function(){
			$("#preloader").show();
			$("#noSelected,#relevancy").hide();
		};

		addUpdateField = function(relevancyId, field, value){
			var $parent = $('div#relevancy div[id="' + field + '"]');

			//Save validation
			if (field=="tie" && !(value > 0 && value < 1)){
				alert("Tie value should be between 0 - 1");
				return;
			}

			RelevancyServiceJS.addOrUpdateRelevancyField(relevancyId, field, value, {
				callback: function(data){
					//TODO: Revert value of field if add or update failed
					//$parent.find('input[type="text"]').val(e.data.data.parameters[field]);

					selectedRelevancy.parameters[field] = value;
				},
				preHook: function(){
					$parent.find("span.crudIcon").hide();
					$parent.find("span.preloader").show();
				},
				postHook: function(){
					$parent.find("span.crudIcon").show();
					$parent.find("span.preloader").hide();
				}
			});
		};

		deleteRelevancy = function(e){
			var relevancy = selectedRelevancy;
			var relevancyId = relevancy.relevancyId;

			if (confirm("Delete " + relevancy.relevancyName + "?"))
				RelevancyServiceJS.deleteRelevancy(relevancyId, {
					callback: function(data){
						selectedRelevancy = null;
						showRelevancyFields();
						refreshRelevancyList(1);
					},
					preHook: function(){
						prepareRelevancyFields();
					}
				});
		};

		saveRelevancy = function(e){
			var relevancy = selectedRelevancy;
			var relevancyId = relevancy.relevancyId;
			var unSaved = getUnSavedRelevancyFields();

			var name = $('div#relevancy input[id="name"]').val(); 
			var description = $('div#relevancy textarea[id="description"]').val(); 
			var startDate = $('div#relevancy input[name="startDate"]').val();
			var endDate = $('div#relevancy input[name="endDate"]').val();

			// Check if with updates
			var hasUpdate = $.trim(name)!=$.trim(relevancy.relevancyName) || $.trim(description)!=$.trim(relevancy.description) ||
			$.trim(startDate)!=$.trim(relevancy.formattedStartDate) || $.trim(endDate)!=$.trim(relevancy.formattedEndDate);

			if (hasUpdate)
				RelevancyServiceJS.updateRelevancy(relevancyId, name, description, startDate, endDate, {
					callback: function(data){
						//TODO: update details
						selectedRelevancy.relevancyName = name;
						selectedRelevancy.description = description;
						updateHeaderText();
						refreshRelevancyList(currentRelevancyPage);
					}
				});

			if (!$.isEmptyObject(unSaved)){
				$.map(unSaved, function(value, index) {
					addUpdateField(relevancyId, index, value);
				}); 
			}
		};

		getUnSavedRelevancyFields = function(){
			var relevancy = selectedRelevancy;
			var relevancyFields = {};

			//fields comparison, save if not equal
			for (var field in relevancy.parameters){
				var currentValue = relevancy.parameters[field];
				var newValue = $('div[id="' + field + '"] input[type="text"]').val();

				if ($.trim(currentValue)!=$.trim(newValue)){
					relevancyFields[field] = $.trim(newValue);
				}
			}

			return relevancyFields;
		};

		showRelevancyFields();
		refreshRelevancyList(1);
		refreshKeywordList(1);
	});

})(jQuery);