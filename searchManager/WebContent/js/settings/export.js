(function($){

	var Export = {
		moduleName : "Export Rule",	
		tabSelected : "",
		entityName : "",
		autoExport : false,
		ruleEntityList : null,
		
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
			$selectedTab.find("div#ruleCount").html("");
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
						urlParams += (key + '=' + encodeURIComponent(params[key]));
						count++;
					};

					document.location.href = url + '?' + urlParams;
				}
			});
		},
		
		exportHandler : function(){
			var self = this;
			var $selectedTab = $("#"+self.tabSelected);
			
			$selectedTab.find("a#okBtn").on({
				click: function(evt){
					var comment = $.defaultIfBlank($.trim($selectedTab.find("#comment").val()), "");
					
					if(self.getSelectedRefId().length==0){
						jAlert("Please select rule", self.moduleName);
					}else if (validateComment(self.moduleName, comment, 1, 300)){
						var selRuleFltr = $selectedTab.find("#ruleFilter").val();
						var a = [];
						var arrSelectedKeys = Object.keys(self.getSelectedItems());
						
						
						$.each(arrSelectedKeys, function(k){ 
							a.push($selectedTab.find("#ruleItem" + $.formatAsId(arrSelectedKeys[k])).find("#ruleName").text());
						});
						
						var confirmMsg = "Continue export of the following rules:<ul class='mar0 padL30'><li>" + a.join('</li><li>') + "</li></ul>";
 
						comment = comment.replace(/\n\r?/g, '<br/>');
						jConfirm(confirmMsg, "Confirm Export", function(status){
							if(status){
								RuleTransferServiceJS.exportRule(self.entityName, self.getSelectedRefId(), comment, {
									callback: function(data){
										showActionResponseFromMap(data, "export", "Export",
										"Unable to find published data for this rule. Please contact Search Manager Team.");
										self.getExportList();	
									},
								});
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
			template += '	<label class="floatL w480"><textarea id="comment" rows="5" class="w460" style="height:32px"></textarea></label>';
			template += '	<div class="clearB"></div>';
			template += '	<div align="right" class="padR15 marT10">';
			template += '		<a id="okBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
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
					var totalSize = (data) ? data.totalSize : 0;
					
					$selectedTab.html($("div#tabContentTemplate").html());

					if (totalSize>0){
						// Populate table row
						for(var i=0; i < totalSize; i++){
							var rule = list[i];
							var $table = $selectedTab.find("table#rule");
							var $tr = $selectedTab.find("tr#ruleItemPattern").clone().attr("id","ruleItem" + $.formatAsId(rule["ruleRefId"])).show();
							var lastPublishedDate = $.isNotBlank(rule["formattedLastPublishedDateTime"])? rule["formattedLastPublishedDateTime"]: "";
							var lastExportedDate = $.isNotBlank(rule["formattedLastExportDateTime"])? rule["formattedLastExportDateTime"]: "";
							var showId = rule["ruleRefId"].toLowerCase() !== rule["description"].toLowerCase();

							$tr.find("td#select > input[type='checkbox']").attr("id", rule["ruleRefId"]);
							$tr.find("td#select > input[type='checkbox']").attr("name", rule["ruleStatusId"]);

							//TODO: Get delete details from file
							if (rule["updateStatus"]!=="DELETE"){
								$tr.find("td#ruleOption > img.previewIcon").attr("id", rule["ruleRefId"]);
								
								if (self.entityName === "didYouMean") {
									var preview = $tr.find("td#ruleOption > img.previewIcon");
									preview.attr("src", "/searchManager/images/iconDownload.png");
									preview.download({
										headerText:"Download Did You Mean Rules",
										moduleName: self.entityName,
										ruleType: self.entityName,  
										solo: $(".internal-tooltip"),
										classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped internal-tooltip',
										requestCallback:function(e2) {
											var params = new Array();
											var url = GLOBAL_contextPath + "/spell/" + GLOBAL_storeId + "/export/xls";
											var urlParams = "";
											var count = 0;

											params["filename"] = e2.data.filename;
											params["type"] = e2.data.type;
											params["id"] = "spell_rule";
											params["clientTimezone"] = +new Date();

											for(var key in params){
												if (count>0) urlParams +='&';
												urlParams += (key + '=' + encodeURIComponent(params[key]));
												count++;
											};
											document.location.href = url + '?' + urlParams;
										}
									});
								}
								else {
									$tr.find("td#ruleOption > img.previewIcon").xmlpreview({
										transferType: "export",
										ruleType: self.entityName,
										ruleId: rule["ruleRefId"],
										ruleName: rule["ruleName"],
										ruleInfo: rule["description"],
										requestType: rule["updateStatus"],
										enablePreTemplate: true,
										enablePostTemplate: true,
										leftPanelSourceData: "xml",
										postTemplate: self.getPostTemplate(),
										postButtonClick: function(){
											self.getExportList();
										},
										itemGetRuleXmlCallback: function(base, contentHolder, ruleType, ruleId, sourceData){
											RuleTransferServiceJS.getRuleToExport(self.entityName, ruleId,{
												callback: function(xml){
													if (xml == null || $.isEmptyObject(xml)) {
														jAlert("Unable to find published data for this rule. Please contact Search Manager Team.", self.moduleName,
																function() {base.api.hide()});
													}
													else {
														base.options.ruleXml = xml;
														base.getRuleData(contentHolder, ruleType, ruleId, sourceData);
													}
												}
											});
										},
										postButtonClick: function(){
											self.getExportList();
										},
										itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap){
											if (self.entityName.toLowerCase() === "elevate"){
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
											if (self.entityName.toLowerCase() === "elevate"){
												ElevateServiceJS.isItemInNaturalResult(ruleName, memberIds, memberConditions, {
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
								}

							}else{
								$tr.find("td#ruleOption > img.previewIcon").hide();
							}

							//if(showId) 
							//	$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleRefId"]);

							$tr.find("td#ruleRefId > p#ruleName").html(list[i]["description"]);
							$tr.find("td#type").html(list[i]["exportType"]);
							
							//TODO
							$tr.find("td#publishDate > p#requestedBy").html(list[i]["publishedStatus"]);
							$tr.find("td#publishDate > p#requestedDate").html(lastPublishedDate);
							
							$tr.find("td#exportDate > p#requestedBy").html(list[i]["exportBy"]);
							$tr.find("td#exportDate > p#requestedDate").html(lastExportedDate);
							$tr.appendTo($table);
						}

						$selectedTab.find("div#ruleCount").html(totalSize + (totalSize == 1 ? " Rule" : " Rules"));
						
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

						var text = "ON: all rules that you publish will be automatically exported. <br/> OFF: all rules that you publish needs to be manually exported";

						if(!$content.get(0))						
							$content = api.elements.content;

						$content.html(text);

					}
				}
			});
			
			self.getRuleEntityList();
			self.populateTabContent();
			
			self.downloadHandler();

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
