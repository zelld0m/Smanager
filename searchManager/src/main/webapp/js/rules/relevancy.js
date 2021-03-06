(function($){
	var moduleName="Ranking Rule";
	var headerText = "Relevancy Rule";
	var selectedRule = null;
	var selectedRuleStatus = null;
	var rulePageSize = 5;
	var ruleKeywordPageSize = 5;
	var keywordInRulePageSize = 5;
	var deleteRuleConfirmText = "Delete this relevancy rule?";
	var ruleNameErrorText = "Please provide a valid relevancy rule name.";

	var schemaFieldsPageSize = 8;
	var schemaFieldsTotal = 0;
	var schemaFieldsSearchText = "Enter Field Name";
	var relFieldMaxValue = 10;
	var sfExcFields = new Array();
	var sfSearchKeyword = "";
	var reloadRate = 1000;

	var bqExcFields = new Array();
	var bqSearchKeyword = "";
	var bqFacetValuesPageSize = 5;
	var bqSearchText = "Enter Field Value";

	var ruleFilterText = "";
	var keywordFilterText = "";
	var rulePage = 1;
	var keywordPage = 1;
	var errorMessageMap = {	   
			'-1':'Error encountered while processing request.',
	        '0':'Invalid Field Input.',
	        '1':'Update Successful.'
	};

	/** BELOW: BF */
	var setupFieldS4 = function(field){
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
	};

	/** BELOW: BQ */
	var setupFieldS3 = function(field){

		var updateFacetValueBarLength = function(content, fieldValue, fieldBoost){

			var $field = content.find('#fieldSelected' + $.formatAsId(fieldValue) + ' input[type="text"]');

			$field.val($field.val().replace(/^\./,'0.')); // insert leading 0 before .
			$field.val($field.val().replace(/\.$/,'')); // remove trailing .

			if ($field.val() > relFieldMaxValue){
				$field.val(relFieldMaxValue);
			}

			var perBar = ($field.val()/relFieldMaxValue)*100; 
			content.find('#fieldSelected' + $.formatAsId(fieldValue) + ' div.bargraph').attr("style", "background:#9d79b2; width:" + perBar + "%");
		};

		var removeSelectedFacetValue = function(content, fieldValue){
			if (confirm("Delete " + fieldValue + " from selection?")){
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
					if (e.charCode == 0){
						if (charCode == 13){ updateFacetValueBarLength(content, fieldValue, $(e.target).val());}
						return; // allow non-printable characters
					}
					if (charCode == 46){ 
						return $(e.target).val().indexOf('.') == -1;
					} 
					else if (charCode > 47 && charCode < 58){
						return true;
					}
					else {
						return false;
					}
				},
				keydown:function(e){
					var charCode = (e.which) ? e.which : e.keyCode;
					var ctrlDown = e.ctrlKey||e.metaKey ;
					if (ctrlDown) {
						return false;
					}
				},
				contextmenu:function(e){
					return false;
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

					bqSearchKeyword = ""; 
					bqExcFields = new Array();

					var currVal = $('div[id="' + field.id + '"] input[type="text"]').val();

					//TODO: initialize selected
					RelevancyServiceJS.getValuesByString(currVal,{
						callback: function(data){
							var list = data.boostQuery;
							$content.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
							$content.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();
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

										var val = $.addSlashes($.trim($(value).find(".txtHolder").text())); 
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

							var searchActivated = false;
							var newSearch = "";
							var oldSearch = "";
							
							var sendRequest = function(event){
								setTimeout(function(){
									bqSearchKeyword = newSearch = $.trim($(event.target).val());

									if (newSearch === bqSearchText) {
										newSearch = "";
									};

									if (oldSearch !== newSearch) {
										populateFieldValues($content, 1);
										oldSearch = newSearch;
										sendRequest(event);
										newSearch = "";
									}
									else {
										searchActivated = false;
									}
								}, reloadRate);  
							};

							var timeout = function(event){
								if (!searchActivated) {
									searchActivated = true;
									sendRequest(event);
								}
							};
							
							$content.find('input[id="searchBoxField"]').val(bqSearchText).on({
								blur: function(e){
									if ($.trim($(e.target).val()).length == 0) 
										$(e.target).val(bqSearchText);
									timeout(e);
								},
								focus: function(e){
									if ($.trim($(e.target).val()) == bqSearchText) 
										$(e.target).val("");
									timeout(e);
								},
								keyup: timeout
							});

							$content.find('select[id="facetName"]').on({
								change: function(e){
									$content.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();
									populateFieldValues($content, 1);
								}
							});
							api.reposition();
						}
					});
				},
				hide: function (e, api){
					api.destroy();
				}
			}

		}).click(function(e) { e.preventDefault(); });
	};

	/** BELOW: MM */
	var setupFieldS2 = function(field){
		$('div[id="' + field.id + '"] a.editIcon, div[id="' + field.id + '"] input[type="text"]').attr("readonly", "readonly").qtip({
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
								},
								keydown:function(e){
									var charCode = (e.which) ? e.which : e.keyCode;
									var ctrlDown = e.ctrlKey||e.metaKey ;
									if (ctrlDown) {
										return false;
									}
								},
								contextmenu:function(e){
									return false;
								}
							});

							$contentHolder.find('li.multiRuleItem input#ruleFieldMatch, input#singleRuleFieldMatch').on({
								blur:function(e){
									$(e.target).val($(e.target).val().replace(/^\./,'0.')); // insert leading 0 before .
									$(e.target).val($(e.target).val().replace(/\.$/,'')); // remove trailing .
								},
								keypress:function(e){
									var charCode = (e.which) ? e.which : e.keyCode;
									var currVal = $(e.target).val();

									if (e.charCode == 0){
										return; // allow non-printable characters
									}
									if (charCode == 45){
										return $.isBlank(currVal);
									}
									if (charCode == 46){ 
										return ($(e.target).val().indexOf('.') == -1); // allow only one .
									} 
									if (charCode == 37){
										return ($.isNotBlank(currVal) && $(e.target).val().indexOf('%') == -1); // allow only one %
									}
									else if (charCode > 47 && charCode < 58){
										return true;
									}
									return false;
								},
								keydown:function(e){
									var charCode = (e.which) ? e.which : e.keyCode;
									var ctrlDown = e.ctrlKey||e.metaKey ;
									if (ctrlDown) {
										return false;
									}
								},
								contextmenu:function(e){
									return false;
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
										var conditionCount = $contentHolder.find('input#ruleFieldCondition').length;
										if (conditionCount > 5) {
											jAlert("Maximum no of rules allowed is 5!","Relevancy Rule");
											return;
										}
										for ( var i = 1; i < conditionCount; i++) {
											var value = $contentHolder.find('li#multiRule' + i + ' input#ruleFieldCondition').val();
											if (value == condition) {
												jAlert("Rule already exists for " + condition + ".","Relevancy Rule");
												return;
											}
										}
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
	var setupFieldS1 = function(field){
		var sfPage = 1;

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

			$field.val($field.val().replace(/^\./,'0.')); // insert leading 0 before .
			$field.val($field.val().replace(/\.$/,'')); // remove trailing .

			if($.isBlank($field.val())){
				$field.val("0");
			}
			
			if ($field.val() > relFieldMaxValue){
				$field.val(relFieldMaxValue);
			}

			var perBar = ($field.val()/relFieldMaxValue)*100; 

			content.find('#fieldSelected_' + fieldName + ' div.bargraph').attr("style", "background:#9d79b2; width:" + perBar + "%");

		},

		removeSelected = function(content, fieldName){
			if (confirm("Delete " + fieldName + " from selection?")){
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
					if (e.charCode == 0){
						if (charCode == 13){ updateBarLength(content, fieldValue, $(e.target).val()); }
						return; // allow non-printable characters
					}
					if (charCode == 46){ 
						return $(e.target).val().indexOf('.') == -1;
					} 
					else if (charCode > 47 && charCode < 58){
						return true;
					}
					else {
						return false;
					}
				},
				keydown:function(e){
					var charCode = (e.which) ? e.which : e.keyCode;
					var ctrlDown = e.ctrlKey||e.metaKey ;
					if (ctrlDown) {
						return false;
					}
				},
				contextmenu:function(e){
					return false;
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

					sfSearchKeyword = "";

					var currVal = $('div[id="' + field.id + '"] input[type="text"]').val();

					RelevancyServiceJS.getQueryFields(currVal, {
						callback:function(data){
							$contentHolder.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
							$contentHolder.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();
							
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
							api.reposition();
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
								var fieldVal = $(value).find('input[type="text"]').val();
								
								if (index > 0) finalVal += " ";
								finalVal += $(value).find(".txtHolder").html();
								finalVal += '^';
								finalVal += $.isBlank(fieldVal) ? "0" : fieldVal;
							});

							api.hide();

							$('div[id="' + field.id + '"] input[type="text"]').val(finalVal);
						}
					});
					
					var searchActivated = false;
					var newSearch = "";
					var oldSearch = "";
					
					var sendRequest = function(event){
						setTimeout(function(){
							sfSearchKeyword = newSearch = $.trim($(event.target).val());

							if (newSearch === schemaFieldsSearchText) {
								newSearch = "";
							};

							if (oldSearch !== newSearch) {
								populateSchemaFields($contentHolder,1);
								oldSearch = newSearch;
								sendRequest(event);
								newSearch = "";
							}
							else {
								searchActivated = false;
							}
						}, reloadRate);  
					};

					var timeout = function(event){
						if (!searchActivated) {
							searchActivated = true;
							sendRequest(event);
						}
					};

					$contentHolder.find('input[id="searchBoxField"]').val(schemaFieldsSearchText).on({
						blur: function(e){
							if ($.trim($(e.target).val()).length == 0) 
								$(e.target).val(schemaFieldsSearchText);
							timeout(e);
						},
						focus: function(e){
							if ($.trim($(e.target).val()) == schemaFieldsSearchText) 
								$(e.target).val("");
							timeout(e);
						},
						keyup: timeout 
					});

				},
				hide: function (e, api){
					api.destroy();
				}
			}

		});
	};
	
	var setRuleFieldValue = function(){

		for (var field in selectedRule.parameters){
			$('div[id="' + field + '"] input[type="text"]').val(selectedRule.parameters[field]);
		}

		getRelevancyField = function(e){
			var field = new Object();
			var $parent = $(e.target).parents('div.AlphaCont');
			field.id = $parent.attr("id");
			field.value = $parent.find('input[type="text"]').val();
			field.label = $parent.find('span[id="fieldLabel"]').html();
			return field; 
		};

		$('div#relevancy .saveIcon').off().on({
			click:function(e){
				var field = getRelevancyField(e);
				if (!e.data.locked && allowModify )  {
					if(field.value == selectedRule.parameters[field.id]) {
						jAlert("Nothing to update.","Relevancy Rule");
						return false;
					} else {
						addRuleFieldValue(field.id, field.value);
					}
				}
			},
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify});

		showGraph = function(e){
			var field = getRelevancyField(e);
			if ($.inArray(field.id,["qf", "pf"]) >= 0) setupFieldS1(field);
			if (field.id=="mm") setupFieldS2(field);
			if (field.id=="bq") setupFieldS3(field);
			//if (field.id=="bf") setupFieldS4(field);
		};

		$('div#relevancy .editIcon, div.AlphaCont input[type="text"]').on({
			click: showGraph
		});

		$('div.AlphaCont input[type="text"]').on({
			focus:showGraph 
		});

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

					if(!$content.get(0))						
						$content = api.elements.content;

					if (field==="qf") text = 'List of fields and the "boosts" to associate with each of them';
					if (field==="bf") text = 'Functions that will be included in the user\'s query to influence the score';
					if (field==="pf") text = 'Used as a boost in cases where the score of documents matched appear in close proximity';
					if (field==="bq") text = 'A raw query string that will be included with the user\'s query to influence the score';
					if (field==="mm") text = 'The minimum number of words specified in the keyword phrase that should match';
					if (field==="qs") text = 'Amount of slop on phrase queries explicitly included in the user\'s query string (affects matching)';
					if (field==="ps") text = 'Amount of slop (distance between words) on phrase queries built for "pf" fields (affects boosting)';
					if (field==="tie") text = 'In case documents have the same score, this is used to determine which document is prioritized. Computation is done via (score of matching clause with the highest score) + ( (tie paramenter) * (scores of any other matching clauses) ) ';

					$content.html(text);

				}
			}
		});

		// add field restrictions
		$('div[id="q.alt"] input[type="text"]').attr("readonly", "readonly").on({
			focus: function(e){jAlert("Contact administrator to modify this field.","Relevancy Rule");}
		});

		$('div[id="q.alt"]').hide();

		$('div[id="tie"] input[type="text"]').off('blur keypress keydown').on({
			blur:function(e){
				$(e.target).val($(e.target).val().replace(/^\./,'0.')); // insert leading 0 before .
				$(e.target).val($(e.target).val().replace(/\.$/,'')); // remove trailing .
				if ($(e.target).val() > 1){
					jAlert("Tie value should be between 0 - 1.","Relevancy Rule");
				}
			},
			keypress:function(e){
				var charCode = (e.which) ? e.which : e.keyCode;
				if (e.charCode == 0){
					return; // allow non-printable characters
				}
				if (charCode == 46){ 
					return $(e.target).val().indexOf('.') == -1;
				} 
				else if (charCode > 47 && charCode < 58){
					return true;
				}
				return false;
			},
			keydown:function(e){
				var charCode = (e.which) ? e.which : e.keyCode;
				var ctrlDown = e.ctrlKey||e.metaKey ;
				if (ctrlDown) {
					return false;
				}
			},
			contextmenu:function(e){
				return false;
			}
		});

		$('div[id="qs"] input[type="text"], div[id="ps"] input[type="text"]').off('keypress keydown').on({
			keypress:function(e){
				var charCode = (e.which) ? e.which : e.keyCode;
				if (e.charCode == 0) {
					return; // allow non-printable characters
				}
				if($(e.target).length > 4){
					return false;					
				}
				if(charCode > 47 && charCode < 58){
					return true;
				}
				return false;
			},
			keydown:function(e){
				var charCode = (e.which) ? e.which : e.keyCode;
				var ctrlDown = e.ctrlKey||e.metaKey ;
				if (ctrlDown) {
					return false;
				}
			},
			contextmenu:function(e){
				return false;
			}
		});
	};

	var addRuleFieldValue = function(field, value){
		var $parent = $('div#relevancy div[id="' + field + '"]');
		var label = $parent.find('span[id="fieldLabel"]').html();
		
		//Save validation TODO: field validation
		
		if (field=="tie" && !(value >= 0 && value <= 1)){
			jAlert("Tie value should be between 0 - 1.","Relevancy Rule");
			return false;
		}

		// validation for qs and ps
		if ((field === "qs" || field === "ps") && !$.isBlank(value) &&!isDigit(value)){
			if (field==="qs") {
				jAlert("Query slop should be a positive number.","Relevancy Rule");
			}
			else if (field==="ps") {
				jAlert("Phrase slop should be a positive number.","Relevancy Rule");
			}
			return false;
		}

		RelevancyServiceJS.addRuleFieldValue(selectedRule.ruleId, field, value, {
			callback: function(code){
				if (field !== "q.alt") {
					showActionResponse(code, "update", label, errorMessageMap, moduleName);					
				}
				selectedRule.parameters[field] = value;
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

	var checkIfUpdateAllowed = function(){
		var ruleName = $.trim($('div#relevancy input[id="name"]').val()); 
		var description = $.trim($('div#relevancy textarea[id="description"]').val()); 
		var startDate = $.trim($('div#relevancy input[name="startDate"]').val());
		var endDate = $.trim($('div#relevancy input[name="endDate"]').val());

		isDirty = false;

		isDirty = isDirty || (ruleName.toLowerCase()!==$.trim(selectedRule.ruleName).toLowerCase());
		isDirty = isDirty || (description.toLowerCase()!==$.trim(selectedRule.description).toLowerCase());
		isDirty = isDirty || (startDate.toLowerCase()!==$.trim(selectedRule.startDate));
		isDirty = isDirty || (endDate.toLowerCase()!==$.trim(selectedRule.endDate));

		// Required field
		isDirty = isDirty;

		return isDirty;
	};

	var getUnSavedRelevancyFields = function(){
		var relevancyFields = {};

		//fields comparison, save if not equal
		for (var field in selectedRule.parameters){
			if(field!=="q.alt"){
				var currentValue = selectedRule.parameters[field];
				var newValue = $('div[id="' + field + '"] input[type="text"]').val();

				if ($.trim(currentValue)!=$.trim(newValue)){
					relevancyFields[field] = $.trim(newValue);
				}
			}
		}

		return relevancyFields;
	};

	var cloneRule = function(e){
		if (e.data.locked || !allowModify) return;
		$(this).qtip({
			content: {
				text: $('<div/>'),
				title: { text: 'Clone Relevancy', button: true }
			},
			show: { 
				solo: true,
				ready:true
			},
			style: {width: 'auto'},
			events: { 
				show: function(rEvt, api){
					var $contentHolder = $("div", api.elements.content).html($("#addRelevancyTemplate").html());

					$contentHolder.find('input, textarea').each(function(index, value){ $(this).val("");});

					if ($.isNotBlank(selectedRule.ruleName)) $contentHolder.find('input[id="popName"]').val("Copy of " + selectedRule.ruleName);

					$contentHolder.find('input[name="popStartDate"]').attr('id', 'popStartDate');
					$contentHolder.find('input[name="popEndDate"]').attr('id', 'popEndDate');

					var popDates = $contentHolder.find("#popStartDate, #popEndDate").prop({readonly: false}).datepicker({			
						minDate: 0,
						maxDate: '+1Y',			
						showOn: "both",
						buttonImage: "../images/icon_calendar.png",
						buttonImageOnly: true,
						changeMonth: true,
					    changeYear: true,
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
								jAlert("Rule name is required.","Relevancy Rule");
							}
							else if (!isAllowedName(popName)){
								jAlert("Rule name contains invalid value.","Relevancy Rule");
							}
							else if (!isAscii(popDescription)) {
								jAlert("Description contains non-ASCII characters.","Relevancy Rule");										
							}
							else if (!isXSSSafe(popDescription)){
								jAlert("Description contains XSS.","Relevancy Rule");
							}
							else if(($.isNotBlank(popStartDate) && !$.isDate(popStartDate)) || ($.isNotBlank(popEndDate) && !$.isDate(popEndDate))){
								jAlert("Please provide a valid date range.","Relevancy Rule");
							} else if ($.isNotBlank(popStartDate) && $.isDate(popStartDate) && $.isNotBlank(popEndDate) && $.isDate(popEndDate) && (new Date(popStartDate).getTime() > new Date(popEndDate).getTime())) {
								jAlert("End date cannot be earlier than start date!","Relevancy Rule");
							}
							else {
								RelevancyServiceJS.checkForRuleNameDuplicate('', popName, {
									callback: function(data){
										if (data==true){
											jAlert("Another relevancy rule is already using the name provided.","Relevancy Rule");
										}else{
											RelevancyServiceJS.cloneRule(selectedRule.ruleId, popName, popStartDate, popEndDate, popDescription, {
												callback:function(data){
													showActionResponse(data==null?0:1, "clone", popName);
													if(data!=null) {
														setRelevancy(data);
													}else{
														setRelevancy(selectedRule);
													}
												},
												preHook: function(){
													prepareRelevancy();
												}
											});
										}
									}
								});
							}
						}
					});

					$contentHolder.find('a#clearButton').on({
						click: function(e){
							$contentHolder.find('input[type="text"], textarea').val("");
						}
					});
				},
				hide: function(hEvt, api){
					api.destroy();
				}
			}
		});
	};

	var updateRule = function(e){
		var self = this;
		
		if (e.data.locked || !allowModify) return;

		var unSaved = getUnSavedRelevancyFields();
		var ruleName = $.trim($('div#relevancy input[id="name"]').val()); 
		var description = $.trim($('div#relevancy textarea[id="description"]').val()); 
		var startDate = $.trim($('div#relevancy input[name="startDate"]').val());
		var endDate = $.trim($('div#relevancy input[name="endDate"]').val());

		var isRelevancyFieldsValid = true;

		if (!$.isEmptyObject(unSaved)){
			$.map(unSaved, function(value, index) {
				isRelevancyFieldsValid = addRuleFieldValue(index, value);
			}); 
		}

		if (checkIfUpdateAllowed() && isRelevancyFieldsValid){
			var response = 0;
			if ($.isBlank(ruleName)){
				jAlert("Rule name is required.", headerText);
			}
			else if (!isAllowedName(ruleName)){
				jAlert("Rule name contains invalid value.", headerText);
			}
			else if (ruleName.length>100){
				jAlert("Name should not exceed 100 characters.", headerText);
			}
			else if (!isAscii(description)) {
				jAlert("Description contains non-ASCII characters.", headerText);
			}
			else if (!isXSSSafe(description)){
				jAlert("Description contains XSS.", headerText);
			}
			else if (description.length>255){
				jAlert("Description should not exceed 255 characters.", headerText);
			}
			else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
				jAlert("Please provide a valid date range!",headerText);
			} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
				jAlert("End date cannot be earlier than start date!",headerText);
			}
			else {
				RelevancyServiceJS.checkForRuleNameDuplicate(selectedRule.ruleId, ruleName, {
					callback: function(data){
						if (data==true){
							jAlert("Another relevancy rule is already using the name provided.","Relevancy Rule");
						}else{
							RelevancyServiceJS.updateRule(selectedRule.ruleId, ruleName, description, startDate, endDate, {
								callback: function(data){
									response = data;
									showActionResponse(response, "update", ruleName);
								},
								preHook: function(){
									prepareRelevancy();
								},
								postHook: function(){
									if(response==1){
										RelevancyServiceJS.getRule(selectedRule.ruleId,{
											callback: function(data){
												setRelevancy(data);
											},
											preHook: function(){
												prepareRelevancy();
											}
										});						
									}
									else{
										setRelevancy(selectedRule);
									}
								}
							});
						}
					}
				});
			}
		}
	};

	var deleteRule = function(e) { 
		if (e.data.locked) return;
		
		jConfirm(deleteRuleConfirmText, "Delete Rule", function(result){
			if(result){
				RelevancyServiceJS.deleteRule(selectedRule.ruleId,{
					callback: function(code){
						if (code > 0) {
							jAlert(selectedRule.ruleName + " was successfully deleted.","Relevancy Rule");
						}
						if(code==1) {
							setRelevancy(null);
							showRelevancy();
						}
					}
				});
			}
		});
	};

	var prepareRelevancy = function(){
		clearAllQtip();
		$("#preloader").show();
		$("#submitForApproval, #relevancy, #noSelected").hide();
		$("#titleText").html(headerText);
		$("#versions,#titleHeader").empty();
	};

	var showRelevancy = function(){

		getRelevancyRuleList(1);
		getRelevancyRuleKeywordList(1);

		if(selectedRule==null){
			$("#preloader, #relevancy").hide();
			$("#noSelected").show();
			$("#titleText").html(headerText);
			$("#titleHeader").html("");
			$("#submitForApproval").hide();
			return;
		}

		$("#submitForApproval").rulestatusbar({
			moduleName: moduleName,
			ruleType: "Ranking Rule",
			rule: selectedRule,
			enableVersion: true,
			authorizeRuleBackup: allowModify,
			authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
			afterSubmitForApprovalRequest:function(ruleStatus){
				selectedRuleStatus = ruleStatus;
				showRelevancy();
			},
			beforeSubmitForApprovalRequest:function(ruleStatus){
				prepareRelevancy()
			},
			beforeRuleStatusRequest: function(){
				prepareRelevancy();
			},
			preRestoreCallback: function(){

			},
			postRestoreCallback: function(base, rule){
				base.api.destroy();
				RelevancyServiceJS.getRule(selectedRule.ruleId, {
					callback:function(data){
						setRelevancy(data);
					},
					preHook:function(){
						prepareRelevancy();
					}
				});
			},
			afterRuleStatusRequest: function(ruleStatus){
				resetInputFields("#relevancy");

				$("#preloader").hide();
				$("#submitForApproval").show();
				$("#relevancy").show();
				selectedRuleStatus = ruleStatus;

				$("#titleText").html(headerText + " for ");
				$("#titleHeader").text(selectedRule.ruleName);

				$("#name").val(selectedRule.ruleName);
				$("#description").val(selectedRule.description);
				$("#startDate").val(selectedRule.formattedStartDate);
				$("#endDate").val(selectedRule.formattedEndDate);
				$("#startDate, #endDate").datepicker("destroy");

				var dates = $("#startDate, #endDate").prop({readonly: false}).datepicker({
					minDate: 0,
					maxDate: '+1Y',
					showOn: "both",
					buttonImage: "../images/icon_calendar.png",
					buttonImageOnly: true,
					//disabled: selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify,
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

				getKeywordInRuleList(1);
				setRuleFieldValue();

				$("#saveBtn").off().on({
					click: updateRule,
					mouseenter: showHoverInfo
				},{locked:selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify});

				$("#cloneBtn").off().on({
					click: cloneRule,
					mouseenter: showHoverInfo
				},{locked:!allowModify});

				$("#deleteBtn").off().on({
					click: deleteRule,
					mouseenter: showHoverInfo
				},{locked:selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify});

				$("a#downloadIcon").download({
					headerText:"Download Ranking Rule",
					requestCallback:function(e){
						var params = new Array();
						var url = document.location.pathname + "/xls";
						var urlParams = "";
						var count = 0;
						params["id"] = selectedRule["ruleId"];
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

				$('#auditIcon').off().on({
					click: function(e){
						$(e.currentTarget).viewaudit({
							itemDataCallback: function(base, page){
								AuditServiceJS.getRelevancyTrail(selectedRule["ruleId"], base.options.page, base.options.pageSize, {
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
	};

	var setRelevancy = function(rule){
		selectedRule = rule;
		showRelevancy();
	};

	var getRelevancyRuleList = function(page){
		$("#rulePanel").sidepanel({
			moduleName: moduleName,
			fieldName: "relevancyName",
			page: rulePage,
			pageSize: rulePageSize,
			customAddRule: true,
			headerText : "Relevancy Rule",
			searchText : "Search Name",
			showAddButton: allowModify,
			filterText: ruleFilterText,

			itemAddCallback: function(base, name){
				$("a#addButton").qtip({
					id: "add-relevancy",
					content: {
						text: $('<div/>'),
						title: { text: 'New Relevancy', button: true }
					},
					position: {
						target: $("a#addButton")
					},
					show: {
						ready: true
					},
					style: {width: 'auto'},
					events: { 
						show: function(e, api){
							var $contentHolder = $("div", api.elements.content).html($("#addRelevancyTemplate").html());

							$contentHolder.find('input, textarea').each(function(index, value){ $(this).val("");});

							if ($.isNotBlank(name)) $contentHolder.find('input[id="popName"]').val(name);

							$contentHolder.find('input[name="popStartDate"]').attr('id', 'popStartDate');
							$contentHolder.find('input[name="popEndDate"]').attr('id', 'popEndDate');

							var popDates = $contentHolder.find("#popStartDate, #popEndDate").prop({readonly: false}).datepicker({
								minDate: 0,
								maxDate: '+1Y',	
								showOn: "both",
								buttonImage: "../images/icon_calendar.png",
								buttonImageOnly: true,
								changeMonth: true,
							    changeYear: true,
								onSelect: function(selectedDate) {
									var option = this.id == "popStartDate" ? "minDate" : "maxDate",
											instance = $(this).data("datepicker"),
											date = $.datepicker.parseDate( instance.settings.dateFormat ||
													$.datepicker._defaults.dateFormat, selectedDate, instance.settings);
									popDates.not(this).datepicker("option", option, date);
								}
							});

							$contentHolder.find('a#addButton').off().on({
								click: function(e){
									var popName = $.trim($contentHolder.find('input[id="popName"]').val());
									var popStartDate = $.trim($contentHolder.find('input[id="popStartDate"]').val()); 
									var popEndDate =  $.trim($contentHolder.find('input[id="popEndDate"]').val()); ; 
									var popDescription =  $.trim($contentHolder.find('textarea[id="popDescription"]').val()); ; 

									if ($.isBlank(popName)){
										jAlert("Ranking rule name is required.","Relevancy Rule");
									}
									else if (!isAllowedName(popName)) {
										jAlert(ruleNameErrorText,"Relevancy Rule");
									}
									else if (!isAscii(popDescription)) {
										jAlert("Description contains non-ASCII characters.","Relevancy Rule");										
									}
									else if (!isXSSSafe(popDescription)){
										jAlert("Description contains XSS.","Relevancy Rule");
									}
									else if (popDescription.length>255){
										jAlert("Description should not exceed 255 characters.","Relevancy Rule");
									}
									else if(($.isNotBlank(popStartDate) && !$.isDate(popStartDate)) || ($.isNotBlank(popEndDate) && !$.isDate(popEndDate))){
										jAlert("Please provide a valid date range","Relevancy Rule");
									}else if ($.isNotBlank(popStartDate) && $.isDate(popStartDate) && $.isNotBlank(popEndDate) && $.isDate(popEndDate) && (new Date(popStartDate).getTime() > new Date(popEndDate).getTime())) {
										jAlert("End date cannot be earlier than start date!","Relevancy Rule");
									}
									else {
										RelevancyServiceJS.checkForRuleNameDuplicate('', popName, {
											callback: function(data){
												if (data==true){
													jAlert("Another relevancy rule is already using the name provided.","Relevancy Rule");
												}else{
													RelevancyServiceJS.cloneRule("",popName, popStartDate, popEndDate, popDescription, {
														callback: function(data){
															if (data!=null){
																base.getList(name, 1);
																setRelevancy(data);
																showActionResponse(1, "add", popName);
																addRuleFieldValue("q.alt", "*:*");
															}else{
																setRelevancy(selectedRule);
															}
														},
														preHook: function(){ 
															base.prepareList(); 
															prepareRelevancy();
														}
													});
												}
											}
										});
									}
								}
							});

							$contentHolder.find('a#clearButton').off().on({
								click: function(e){
									$contentHolder.find('input[type="text"], textarea').val("");
								}
							});
							initTextarea(); 
						},
						hide: function (e, api){
							sfExcFields = new Array();
							api.destroy();
						}
					}
				});

			},

			itemDataCallback: function(base, keyword, page){
				ruleFilterText = keyword;
				rulePage = page;
				RelevancyServiceJS.getAllRule(keyword, page, rulePageSize,{
					callback: function(data){
						base.populateList(data, keyword);
						base.addPaging(keyword, page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},

			itemNameCallback: function(base, item){
				RelevancyServiceJS.getRule(item.model["ruleId"], {
					callback:function(data){
						setRelevancy(data);
					},
					preHook:function(){
						prepareRelevancy();
					}
				});
			},
			
			itemOptionCallback: function(base, item){
				RelevancyServiceJS.getTotalKeywordInRule(item.model["ruleId"],{
					callback: function(count){
						if (count > 0) item.ui.find("#itemLinkValue").html("(" + count + ")");
						
						item.ui.find("#itemLinkValue").on({
							click: function(e){
								RelevancyServiceJS.getRule(item.model["ruleId"], {
									callback:function(data){
										setRelevancy(data);
									},
									preHook:function(){
										prepareRelevancy();
									}
								});	
							}
						});
					},
					preHook: function(){ 
						item.ui.find("#itemLinkValue").hide();
						item.ui.find("#itemLinkPreloader").show();
					},
					postHook: function(){ 
						item.ui.find("#itemLinkValue").show();
						item.ui.find("#itemLinkPreloader").hide();
					}
				});
			}
		});
	};

	var getRelevancyRuleKeywordList = function(page){
		$("#ruleKeywordPanel").sidepanel({
			fieldName: "keyword",
			page: keywordPage,
			pageSize: ruleKeywordPageSize,
			headerText : "Keyword",
			showAddButton: false,
			showStatus: false,
			filterText: keywordFilterText,
			itemDataCallback: function(base, keyword, page){
				keywordFilterText = keyword;
				keywordPage = page;
				StoreKeywordServiceJS.getAllKeyword(keyword, page, ruleKeywordPageSize,{
					callback: function(data){
						base.populateList(data, keyword);
						base.addPaging(keyword, page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},
			
			itemOptionCallback: function(base, item){
				RelevancyServiceJS.getTotalRuleUsedByKeyword(item.name, {
					callback: function(count){
						if (count == 0) return;

						item.ui.find("#itemLinkValue").html("(" + count + ")").on({
							click: function(e){
								$(e.currentTarget).qtip({
									content: {
										text: $('<div/>'),
										title: { text: 'Ranking Rule for ' + item.name, button: true }
									},
									show: { 
										ready: true,
										modal: true 
									},
									events: { 
										show: function(rEvt, api){
											var $content = $("div", api.elements.content).html($("#sortRulePriorityTemplate").html());

											RelevancyServiceJS.getAllRuleUsedByKeyword(item.name, {
												callback: function(data){
													var list = data.list;

													$content.find("ul#ruleListing > li:not(#rulePattern)").remove();

													for(var i=0; i < data.totalSize; i++){
														var rule = list[i].relevancy;
														var suffixId = $.escapeQuotes($.formatAsId(rule["ruleId"]));
														$content.find("li#rulePattern").clone().appendTo("ul#ruleListing").attr("id", "rule" + suffixId).show();
														$content.find("li#rule" + suffixId + " span.ruleName").attr("id", rule["ruleId"]).html(rule["ruleName"]);
													}

													$content.find("ul#ruleListing > li:nth-child(even)").addClass("alt");

													$content.find("ul#ruleListing").sortable({ 
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

															var relId = ui.item.find("span").attr("id");

															RelevancyServiceJS.updateRulePriority(relId, item.name, destinationIndex, {
																callback: function(data){

																}
															});
														}
													});
												}
											});
										},
										hide: function(e, api){
											api.destroy();
										}
									}
								});
							}
						});
							
					},
					preHook: function(){ 
						item.ui.find("#itemLinkValue").hide();
						item.ui.find("#itemLinkPreloader").show();
					},
					postHook: function(){ 
						item.ui.find("#itemLinkValue").show();
						item.ui.find("#itemLinkPreloader").hide();
					}
				});

			}
		});
	};
	var initTextarea =function(){
		$('textarea[maxlength]').on({
			keyup:function(){  
				var limit = parseInt($(this).attr('maxlength'));  

				var text = $(this).val();  

				var chars = text.length;  

				//check if there are more characters then allowed  
				if(chars > limit){  
					//and if there are use substr to get the text before the limit  
					var new_text = text.substr(0, limit);  

					//and change the current text with the new text  
					$(this).val(new_text);  
				}  
			}

		});
	};
	var getKeywordInRuleList = function(page){
		$("#keywordInRulePanel").sidepanel({
			fieldName: "keyword",
			page: page,
			itemTitle: "New Keyword",
			region: "content",
			pageStyle: "style2",
			pageSize: keywordInRulePageSize,
			headerText : "Using This Rule",
			headerTextAlt : "Keyword",
			itemTextClass : "cursorText",
			searchText : "Enter Keyword",
			showAddButton: !selectedRuleStatus.locked && !$.endsWith(selectedRule.ruleId , "_default") && allowModify,
			showStatus: false,
			itemDataCallback: function(base, keyword, page){
				RelevancyServiceJS.getAllKeywordInRule(selectedRule.ruleId, keyword, page, keywordInRulePageSize, {
					callback: function(data){
						base.populateList(data, keyword);
						base.addPaging(keyword, page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},
			
			itemOptionCallback: function(base, item){
				var icon = '<a id="deleteKw" href="javascript:void(0);"><img src="' + GLOBAL_contextPath + '/images/icon_delete2.png"></a>';

				item.ui.find(".itemLink").html($(icon));
				item.ui.find(".itemLink > a#deleteKw").off().on({
					click: function(e){
						if (e.data.locked) return;

						jConfirm('Delete "' + item.name + '" in ' + selectedRule.ruleName  + '?', "Delete Keyword", function(result){
							if(result){
								RelevancyServiceJS.deleteKeywordInRule(selectedRule.ruleId, item.name,{
									callback:function(code){
										showActionResponse(code, "delete", item.name);
										getKeywordInRuleList(1);
										getRelevancyRuleList(1);
										getRelevancyRuleKeywordList(1);
									},
									preHook: function(){ base.prepareList(); }
								});
							}
						});			
					},
					mouseenter: showHoverInfo
				},{locked: selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify});
			},
			
			itemAddCallback: function(base, keyword){
				RelevancyServiceJS.addKeywordToRule(selectedRule.ruleId, keyword, {
					callback: function(code){
						showActionResponse(code, "add", keyword);
						getKeywordInRuleList(1);
						getRelevancyRuleList(1);
						getRelevancyRuleKeywordList(1);
					},
					preHook: function(){ base.prepareList(); }
				});
			}
		});
	};

	$(document).ready(function() { 
		initTextarea();
		showRelevancy();
		getRelevancyRuleList();
		getRelevancyRuleKeywordList();
	});

})(jQuery);
