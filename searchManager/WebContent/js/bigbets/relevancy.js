(function($){
	var moduleName="Ranking Rule";
	var selectedRule = null;
	var selectedRuleStatus = null;
	var rulePageSize = 5;
	var ruleKeywordPageSize = 5;
	var keywordInRulePageSize = 5;
	var deleteRuleConfirmText = "Delete this ranking rule?";
	var ruleNameErrorText = "Please provide a valid ranking rule name.";

	var schemaFieldsPageSize = 8;
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
	
	var ruleFilterText = "";
	var keywordFilterText = "";
	var rulePage = 1;
	var keywordPage = 1;

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
	};

	/** BELOW: MM */
	var setupFieldS2 = function(field){
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
									if($.isNotBlank(currVal) && (charCode == 37 || charCode == 39)) return true;
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
										var conditionCount = $contentHolder.find('input#ruleFieldCondition').length;
										if (conditionCount > 5) {
											alert("Maximum no of rules allowed is 5!");
											return;
										}
										for ( var i = 1; i < conditionCount; i++) {
											var value = $contentHolder.find('li#multiRule' + i + ' input#ruleFieldCondition').val();
											if (value == condition) {
												alert("Rule already exists for " + condition + ".");
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
				if (!e.data.locked || allowModify ) 
					addRuleFieldValue(field.id, field.value);
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
			focus: function(e){alert("Contact administrator to modify this field.");}
		});

		$('div[id="q.alt"]').hide();

		$('div[id="tie"] input[type="text"]').off('blur focus keypress').on({
			focus: function(e){if ($.trim($(e.target).val()) == selectedRule.parameters["tie"]) $(e.target).val("");},
			keypress:function(e){
				var charCode = (e.which) ? e.which : e.keyCode;

				if(charCode == 46 && ($.trim($(e.target).val()) == 0 || $.isBlank($(e.target).val()))) return true;
				if(charCode == 8 || ($.inArray(charCode,[48,49,96])!=-1 && $.isBlank($(e.target).val()))) return true;
				if($.isNotBlank($(e.target).val()) && $(e.target).val().indexOf('.') != -1 && (charCode < 32 || (charCode > 47 && charCode < 58))) return true;
				if(($(e.target).val()==1 || $(e.target).val()==0) && (charCode == 49 || charCode == 48)) {
					if ($('div[id="tie"] input[type="text"]').length == 1) {
						$(e.target).val(String.fromCharCode(charCode));
						return false;
					}
				}
				alert("Tie value should be between 0 - 1.");
				return false;

			}
		});

		$('div[id="qs"] input[type="text"], div[id="ps"] input[type="text"]').off('blur focus keypress').on({
			focus: function(e){if ($.trim($(e.target).val()) == selectedRule.parameters[getRelevancyField(e).id]) $(e.target).val("");},
			keypress:function(e){
				var charCode = (e.which) ? e.which : e.keyCode;

				if(charCode == 8) return true;
				if($(e.target).length > 4) 
					return false;
				if($.inArray(charCode,[48,96]) != -1 && $.isBlank($(e.target).val())) return false;
				if(charCode < 32 || (charCode > 47 && charCode < 58)) return true;
				alert(getRelevancyField(e).label + " value should be numeric.");
				return false;
			}
		});
	};

	var addRuleFieldValue = function(field, value){
		var $parent = $('div#relevancy div[id="' + field + '"]');
		var label = $parent.find('span[id="fieldLabel"]').html();
		
		//Save validation TODO: field validation
		if (field=="tie" && !(value >= 0 && value <= 1)){
			alert("Tie value should be between 0 - 1.");
			return;
		}
		
		// validation for qs and ps
		if ((field === "qs" || field === "ps") && !$.isBlank(value) &&!isDigit(value)){
			if (field==="qs") {
				alert("Query slop should be a positive number.");
			}
			else if (field==="ps") {
				alert("Phrase slop should be a positive number.");
			}
			return;
		}
		
		RelevancyServiceJS.addRuleFieldValue(selectedRule.ruleId, field, value, {
			callback: function(code){
				if (field !== "q.alt") {
					showActionResponse(code, "update", label);					
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

					var popDates = $contentHolder.find("#popStartDate, #popEndDate").datepicker({			
						minDate: 0,
						maxDate: '+1Y',			
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
								alert("Rule name is required.");
							}
							else if (!isAllowedName(popName)){
								alert("Rule name contains invalid value.");
							}
							else if (!isAscii(popDescription)) {
								alert("Description contains non-ASCII characters.");										
							}
							else if (!isXSSSafe(popDescription)){
								alert("Description contains XSS.");
							}
							else if(($.isNotBlank(popStartDate) && !$.isDate(popStartDate)) || ($.isNotBlank(popEndDate) && !$.isDate(popEndDate))){
								alert("Please provide a valid date range.");
							} else if ($.isNotBlank(popStartDate) && $.isDate(popStartDate) && $.isNotBlank(popEndDate) && $.isDate(popEndDate) && (new Date(popStartDate).getTime() > new Date(popEndDate).getTime())) {
								alert("End date cannot be earlier than start date!");
							}
							else {
								RelevancyServiceJS.checkForRuleNameDuplicate('', popName, {
									callback: function(data){
										if (data==true){
											alert("Another ranking rule is already using the name provided.");
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
		if (e.data.locked || !allowModify) return;

		var unSaved = getUnSavedRelevancyFields();
		var ruleName = $.trim($('div#relevancy input[id="name"]').val()); 
		var description = $.trim($('div#relevancy textarea[id="description"]').val()); 
		var startDate = $.trim($('div#relevancy input[name="startDate"]').val());
		var endDate = $.trim($('div#relevancy input[name="endDate"]').val());

		if (checkIfUpdateAllowed()){
			var response = 0;
			if ($.isBlank(ruleName)){
				showMessage("#name", "Rule name is required.");
			}
			else if (!isAllowedName(ruleName)){
				showMessage("#name", "Rule name contains invalid value.");
			}
			else if (!isAscii(description)) {
				showMessage("textarea#description", "Description contains non-ASCII characters.");										
			}
			else if (!isXSSSafe(description)){
				showMessage("textarea#description", "Description contains XSS.");
			}
			else if (description.length>255){
				showMessage("textarea#description","Description should not exceed 255 characters.");
			}
			else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
				alert("Please provide a valid date range!");
			} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
				alert("End date cannot be earlier than start date!");
			}
			else {
				RelevancyServiceJS.checkForRuleNameDuplicate(selectedRule.ruleId, ruleName, {
					callback: function(data){
						if (data==true){
							showMessage("#name", "Another ranking rule is already using the name provided.");
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

		if (!$.isEmptyObject(unSaved)){
			$.map(unSaved, function(value, index) {
				addRuleFieldValue(index, value);
			}); 
		}
	};

	var deleteRule = function(e) { 
		if (!e.data.locked && allowModify && confirm(deleteRuleConfirmText)){
			RelevancyServiceJS.deleteRule(selectedRule.ruleId,{
				callback: function(code){
					if (code > 0) {
						alert(selectedRule.ruleName + " was successfully deleted.");
					}
					if(code==1) setRelevancy(null);
				}
			});
		}
	};

	var prepareRelevancy = function(){
		clearAllQtip();
		$("#preloader").show();
		$("#submitForApproval").hide();
		$("#noSelected").hide();
		$("#relevancy").hide();
		$("#titleText").html(moduleName);
	};

	var showRelevancy = function(){
		prepareRelevancy();
		$("#preloader").hide();
		resetInputFields("#relevancy");

		getRelevancyRuleList(1);
		getRelevancyRuleKeywordList(1);

		if(selectedRule==null){
			$("#noSelected").show();
			$("#titleText").html(moduleName);
			return;
		}

		$.endsWith(selectedRule.ruleId, "_default") ? $("#submitForApproval").hide() : $("#submitForApproval").show();

		$("#relevancy").show();

		$("div#versions").version({
			ruleType: "Ranking Rule",
			ruleId: selectedRule["ruleId"]
		});
		
		$("#titleText").html(moduleName + " for ");
		$("#titleHeader").html(selectedRule.ruleName);

		$("#name").val(selectedRule.ruleName);
		$("#description").val(selectedRule.description);
		$("#startDate").val(selectedRule.formattedStartDate);
		$("#endDate").val(selectedRule.formattedEndDate);
		$("#startDate, #endDate").datepicker("destroy");

		var dates = $("#startDate, #endDate").datepicker({
			minDate: 0,
			maxDate: '+1Y',
			showOn: "both",
			buttonImage: "../images/icon_calendar.png",
			buttonImageOnly: true,
			disabled: selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify,
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

		$("#backupBtn").off().on({
			click: backup,
			mouseenter: showHoverInfo
		},{locked:!allowModify});

		$("#backupBtn").off().on({
			click: backup,
			mouseenter: showHoverInfo
		},{locked:!allowModify});

		$("#deleteBtn").off().on({
			click: deleteRule,
			mouseenter: showHoverInfo
		},{locked:selectedRuleStatus.locked || $.endsWith(selectedRule.ruleId, "_default") || !allowModify});
		
		$("#versionIcon").off().on({
			click: preview,
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
					urlParams += (key + '=' + params[key]);
					count++;
				};

				document.location.href = url + '?' + urlParams;
			}
		});
		
		$("#submitForApprovalBtn").off().on({
			click: function(e){
				var ruleStatus = null;
				var data = e.data;

				if(confirm(e.data.module + " " + e.data.ruleRefName + " will be locked for approval. Continue?")){
					DeploymentServiceJS.processRuleStatus(e.data.module, e.data.ruleRefId, e.data.ruleRefName, e.data.isDelete,{
						callback: function(data){
							ruleStatus = data;
						},
						preHook:function(){
							prepareRelevancy();
						},
						postHook: function(){
							setRelevancy(selectedRule);
						}
					});
				}
			}
		}, { module: moduleName, ruleRefId: selectedRule.ruleId , ruleRefName: selectedRule.ruleName, isDelete: false});

		$('#auditIcon').on({
			click: showAuditList
		}, {locked: !allowModify, type:moduleName, ruleRefId: selectedRule.ruleId, name: selectedRule.ruleName});

	};

	var setRelevancy = function(rule){
		selectedRule = rule;

		if (rule!=null){
			DeploymentServiceJS.getRuleStatus(moduleName, selectedRule.ruleId, {
				callback:function(data){
					selectedRuleStatus = data;
					$('#itemPattern' + $.escapeQuotes($.formatAsId(selectedRule.ruleId)) + ' div.itemSubText').html(getRuleNameSubTextStatus(selectedRuleStatus));
					showDeploymentStatusBar(moduleName, selectedRuleStatus);
					
					showRelevancy();
				},
				preHook: function(){
					prepareRelevancy();
				}
			});	
		}else{
			showRelevancy();
		}
	};

	var getRelevancyRuleList = function(page){
		$("#rulePanel").sidepanel({
			fieldId: "relevancyId",
			fieldName: "relevancyName",
			page: rulePage,
			pageSize: rulePageSize,
			headerText : "Ranking Rule",
			searchText : "Enter Name",
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

							var popDates = $contentHolder.find("#popStartDate, #popEndDate").datepicker({
								minDate: 0,
								maxDate: '+1Y',	
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
										alert("Ranking rule name is required.");
									}
									else if (!isAllowedName(popName)) {
										alert(ruleNameErrorText);
									}
									else if (!isAscii(popDescription)) {
										alert("Description contains non-ASCII characters.");										
									}
									else if (!isXSSSafe(popDescription)){
										alert("Description contains XSS.");
									}
									else if (popDescription.length>255){
										alert("Description should not exceed 255 characters.");
									}
									else if(($.isNotBlank(popStartDate) && !$.isDate(popStartDate)) || ($.isNotBlank(popEndDate) && !$.isDate(popEndDate))){
										alert("Please provide a valid date range");
									}else if ($.isNotBlank(popStartDate) && $.isDate(popStartDate) && $.isNotBlank(popEndDate) && $.isDate(popEndDate) && (new Date(popStartDate).getTime() > new Date(popEndDate).getTime())) {
											alert("End date cannot be earlier than start date!");
									}
									else {
										RelevancyServiceJS.checkForRuleNameDuplicate('', popName, {
											callback: function(data){
												if (data==true){
													alert("Another ranking rule is already using the name provided.");
												}else{
													RelevancyServiceJS.cloneRule("",popName, popStartDate, popEndDate, popDescription, {
														callback: function(data){
															if (data!=null){
																base.getList(name, 1);
																setRelevancy(data);
																showActionResponse(1, "add", name);
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

							$contentHolder.find('a#clearButton').on({
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
						base.populateList(data);
						base.addPaging(keyword, page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},

			itemOptionCallback: function(base, id, name, model){
				var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));

				RelevancyServiceJS.getTotalKeywordInRule(id,{
					callback: function(count){

						var totalText = (count == 0) ? "&#133;": "(" + count + ")"; 
						base.$el.find(selector + ' div.itemLink a').html(totalText);

						base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').on({
							click: function(e){
								RelevancyServiceJS.getRule(model.ruleId, {
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
						base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
					}
				});

				DeploymentServiceJS.getRuleStatus(moduleName, id, {
					callback:function(data){
						base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
					}
				});
			}
		});
	};

	var getRelevancyRuleKeywordList = function(page){
		$("#ruleKeywordPanel").sidepanel({
			fieldId: "keywordId",
			fieldName: "keyword",
			page: keywordPage,
			pageSize: ruleKeywordPageSize,
			headerText : "Ranking Rule Keyword",
			searchText : "Enter Keyword",
			showAddButton: false,
			filterText: keywordFilterText,
			itemDataCallback: function(base, keyword, page){
				keywordFilterText = keyword;
				keywordPage = page;
				StoreKeywordServiceJS.getAllKeyword(keyword, page, ruleKeywordPageSize,{
					callback: function(data){
						base.populateList(data);
						base.addPaging(keyword, page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},
			itemOptionCallback: function(base, id, name){
				var suffixId = $.escapeQuotes($.formatAsId(id));

				RelevancyServiceJS.getTotalRuleUsedByKeyword(name, {
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
										var $content = $("div", api.elements.content).html($("#sortRulePriorityTemplate").html());

										RelevancyServiceJS.getAllRuleUsedByKeyword(name, {
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
														
														RelevancyServiceJS.updateRulePriority(relId, name, destinationIndex, {
															callback: function(data){

															}
														});
													}
												});
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
			fieldId: "keywordId",
			fieldName: "keyword",
			page: page,
			region: "content",
			pageStyle: "style2",
			pageSize: keywordInRulePageSize,
			headerText : "Using This Rule",
			searchText : "Enter Keyword",
			showAddButton: !selectedRuleStatus.locked && !$.endsWith(selectedRule.ruleId , "_default") && allowModify,
			itemDataCallback: function(base, keyword, page){
				RelevancyServiceJS.getAllKeywordInRule(selectedRule.ruleId, keyword, page, keywordInRulePageSize, {
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
						if (!e.data.locked && allowModify && confirm('Delete "' + name + '" in ' + selectedRule.ruleName  + '?'))
							RelevancyServiceJS.deleteKeywordInRule(selectedRule.ruleId, name,{
								callback:function(code){
									showActionResponse(code, "delete", name);
									getKeywordInRuleList(1);
									getRelevancyRuleList(1);
									getRelevancyRuleKeywordList(1);
								},
								preHook: function(){ base.prepareList(); }
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

	var preview = function(evt){

		$(this).qtip({
			id: "rule-preview",
			content: {
				text: $('<div/>'),
				title: { 
					text: " Rule Versions", button:true
				}
			},
			position: {
				my: 'center',
				at: 'center',
				target: $(window)
			},
			show: {
				modal: true,
				solo: true,
				ready: true
			},
			style: {
				width: 'auto'
			},
			events: {
				show: function(event, api) {
					var $content = $("div", api.elements.content);
					populatePreview(api, $content);
				},
				hide: function(event, api) {
					api.destroy();
				}
			}
		});
	};

	var populatePreview = function(api, $content){
		
		$content.html($("#previewTemplate2").html());

		RuleVersioningServiceJS.getRankingRuleVersion(selectedRule.ruleId, 1, {
			callback: function(data){
				
				$content.find("#ruleInfo").text($.trim(data["relevancyName"]) + " [ " + $.trim(data["relevancyId"] + " ]"));
				$content.find("#ruleVersion").text(data["version"]);
				$content.find("#startDate").html(data["startDate"]);
				$content.find("#endDate").html(data["endDate"]);
				$content.find("#description").html(data["description"]);

				var $table = $content.find("div.ruleFieldV table#item");
				$table.find("tr:not(#itemPattern)").remove();
					
				for(var field in data.parameters){
					$tr = $content.find("div.ruleFieldV tr#itemPattern").clone().attr("id","item0").show();
					$tr.find("td#fieldName").html(field);
					$tr.find("td#fieldValue").html(data.parameters[field]);
					$tr.appendTo($table);
				}	
					
				$table.find("tr:even").addClass("alt");

				var list = data.relKeyword;
				var $table = $content.find("div.ruleKeyword table#item");
				$table.find("tr:not(#itemPattern)").remove();
	
				if (list.length==0){
					$tr = $content.find("div.ruleKeyword tr#itemPattern").clone().attr("id","item0").show();
					$tr.find("td#fieldName").html("No keywords associated to this rule").attr("colspan","2");
					$tr.find("td#fieldValue").remove();
					$tr.appendTo($table);
				}else{
					for(var i=0; i< list.length; i++){
						$tr = $content.find("div.ruleKeyword tr#itemPattern").clone().attr("id","item" + $.formatAsId(list[i]["keyword"].keyword)).show();
						$tr.find("td#fieldName").html(parseInt(i)+1);
						$tr.find("td#fieldValue").html(list[i]["keyword"].keyword);
						$tr.appendTo($table);
					}	
				}
				
				$table.find("tr:even").addClass("alt");
			},
			errorHandler:function(errorString, exception) { alert(errorString); }
		});

		$content.find("a#restoreBtn").on({
			click: function(evt){
				alert("Restoring version to...");
			}
		});

		return $content;
	};

	var backup = function(evt){

		$(this).qtip({
			id: "rule-backup",
			content: {
				text: $('<div/>'),
				title: { 
					text: "Backup Rule", button:true
				}
			},
			position: {
				my: 'center',
				at: 'center',
				target: $(window)
			},
			show: {
				modal: true,
				solo: true,
				ready: true
			},
			style: {
				width: 'auto'
			},
			events: {
				show: function(event, api) {
					var $content = $("div", api.elements.content);
					renderBackupConfirm(api, $content);
				},
				hide: function(event, api) {
					api.destroy();
				}
			}
		});
	};

	var renderBackupConfirm = function(api, $content){
		
		$content.html($("#reasonView").html());

		$content.find("a#rcancelBtn, a#rsaveBtn").on({
			click: function(evt){
				var reason = $content.find("#reason").val();

				if ($.isNotBlank(reason)){
					switch($(evt.currentTarget).attr("id")){
					case "rsaveBtn": 
						RuleVersioningServiceJS.createRuleVersion("ranking rule", selectedRule.ruleId,reason, {
							callback: function(data){
								if (data) {
									alert("Successfully created back up!");
								} else {
									alert("Failed creating back up!");
								}
							},
							preHook: function(){
								api.destroy();
							}
						});break;

					case "rcancelBtn": 
							api.destroy();
							break;
					}	
				}else{
					alert("Reason can not be blank!");
				}
			}		
		});

		return $content;
	};

	$(document).ready(function() { 
		initTextarea();
		showRelevancy();
		getRelevancyRuleList();
		getRelevancyRuleKeywordList();
	});

})(jQuery);
