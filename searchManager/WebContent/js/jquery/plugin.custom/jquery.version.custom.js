(function($){

	$.version = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;
		var requestOngoing = false;
		
		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("version", base);

		base.init = function(){
			base.options = $.extend({},$.version.defaultOptions, options);
			base.showVersion();
		};

		base.showVersion = function(){
			$(base.$el).qtip({
				content: {
					text: $('<div/>'),
					title: { text: base.options.moduleName + " Version", button: true }
				},
				position: {
					my: 'center',
					at: 'center',
					target: $(window)
				},
				style: {
					width: "auto"
				},
				show: {
					ready: true,
					modal:{on: true, blur: false}
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getTemplate());
						
						if (base.options.enableCompare) {
							base.contentHolder.find("#versionWrapper").before(base.getItemListTemplate());
						}
						base.getAvailableVersion();
						base.addSaveButtonListener();
					},
					hide: function(event, api){
						base.options.afterClose();
						api.destroy();
					}
				}
			});
		};

		base.addCompareButtonListener = function(){
			var $content = base.contentHolder;
			base.selectedVersion = [];
			base.selectedVersion.push("current");

			$content.find("a#compareBtn").off().on({
				click: function(e){
					base.selectedVersion = [];
					base.selectedVersion.push("current");
					$content.find("table#versionList").find("tr.itemRow:not(#itemPattern) > td#itemSelect > input[type='radio']:checked").each(function(index, value){
						base.selectedVersion.push($(value).parents("tr.itemRow").attr("id").split("_")[1]);
					});
					
					if(base.selectedVersion.length != 2){
						jAlert("Please select a version to compare.");
					}
					else{
						base.setCompare();
						$('ul#rowLabel').css('margin-top', '25px');
					}
				}
			});

			base.setCompare();

		};

		base.getItemType = function(item){
			var $condition = item.condition;
			var type = "unknown";

			if($.isBlank($condition)){
				return type;
			}

			if (!$condition["CNetFilter"] && !$condition["IMSFilter"]){
				type="facet";
			}else if($condition["CNetFilter"]){
				type="cnet";
			}else if($condition["IMSFilter"]){
				type="ims";
			}

			return type;
		};

		base.setCompare = function(){
			var $content = base.contentHolder;
			var index = 0;
			var item, $li, $vItem = null;
			var $vDiv = $content.find("div#vHeaderList");
			var $vPattern = $vDiv.find("div#vPattern");
			var $ul = $content.find("ul#versionList");
			var $liPattern = $ul.find("li#itemPattern");

			var $rowLabelUl = $content.find("ul#rowLabel");

			$ul.find("li.item:not(#itemPattern)").remove();
			$vDiv.find("div.vHeader:not(#vPattern)").remove();
			$rowLabelUl.find("li.dynamic").remove();

			for(var ver in base.selectedVersion){
				index = base.selectedVersion[ver];
				$li = $liPattern.clone();
				$vItem = $vPattern.clone();
				item = base.ruleMap[index];
				rule = item["rule"];

				$vItem.attr("id","vHeader_" + index);
				$vItem.find("#ver").text(index==="current" ? "Current Rule": "Version " + item["version"]);
				$vItem.show();
				$vDiv.append($vItem);

				$li.attr("id","ver_" + index);
				if($.isNotBlank(item["createdBy"])) $li.find("#verCreatedBy").text(item["createdBy"]);
				if($li.find("#restoreLink").hide()){
					$('li#ver_current').css('margin-top', '31px');
					
				}
				
				
				if(index !== "current"){
					$li.find("label.restoreIcon").off().on({
						click:function(e){
							if (!e.data.locked) {
								jConfirm("Restore data to version " + e.data.item["name"] + "?" , "Restore Version", function(result){
									if(result){
										base.restoreVersion(e.data.item);
									}
								});
							}
						},
						mouseenter: showHoverInfo
					},{item: item, locked: base.options.locked, message: "You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked."})
					$li.find("#restoreLink").show();
					$li.find("#verName").text(item["name"]);
					$li.find("#verNote").text(item["notes"]);
					$li.find("#verDate").text(item["formattedCreatedDateTime"] ? item["formattedCreatedDateTime"] : "");
				}
				else {
					$li.find("#verName").text("Not Available");
					$li.find("#verNote").text("Not Available");
					if(item["formattedLastModifiedDateTime"]) $li.find("#verDate").text(item["formattedLastModifiedDateTime"]);
				}
				$li.find("#ruleId").text(item["ruleId"]);
				$li.find("#ruleName").text(item["ruleName"]);

				switch(base.options.ruleType){
				case "Elevate": 
				case "Exclude": 
				case "Demote":
					base.setProductCompare($li, $rowLabelUl, item); break; 
				case "Facet Sort": 
					base.setFacetItemCompare($li, $rowLabelUl, item); break; 
				case "Query Cleaning": 
					base.setQueryCleaningCompare($li, $rowLabelUl, item); break; 
				case "Ranking Rule": 
					base.setRankingRuleCompare($li, $rowLabelUl, item); break; 
				}

				$li.show();
				$ul.append($li);
			};
		};

		base.setQueryCleaningCompare = function(li, rowLabelUl, item){
			var $li = li;
			var $rowLabelUl = rowLabelUl;
			var $ruleCondition = item["ruleCondition"];
			var conditions = null;
			base.setRuleKeyword(li, rowLabelUl, item);
			if ($ruleCondition!=null) conditions = $ruleCondition["ruleCondition"];

			$rowLabelUl.find("li#redirectType").text("Active Type").show();
			$li.find("#redirectType").show();

			if ($.isNotBlank(item["redirectType"])){
				$li.find("#redirectType").text(item["redirectType"]);
			}

			$rowLabelUl.find("li#redirectKeyword").text("Replace Keyword").show();
			$li.find("#redirectKeyword").show();

			if ($.isNotBlank(item["replacementKeyword"])){
				$li.find("#redirectKeyword").text(item["replacementKeyword"]);
			}

			$rowLabelUl.find("li#conditions").text("Conditions").show();

			if($ruleCondition!=null && $ruleCondition["includeKeyword"]){
				$li.find("#includeKeyword").text("YES");
			}

			if(conditions!=null && conditions.length > 0){
				var $conditionUl = $li.find("ul#conditionList");
				var $conditionLiPattern = $conditionUl.find("li#conditionPattern");
				var $conditionLi = null;
				$conditionUl.parent().show();

				for(var i=0; i<conditions.length; i++){
					$conditionLi = $conditionLiPattern.clone();
					$conditionLi.attr("id", i+1);
					$conditionLi.find("#condition").text(conditions[i]["readableString"]);
					$conditionLi.show();
					$conditionUl.append($conditionLi);
				}
			}		
		};

		base.setRankingRuleCompare = function(li, rowLabelUl, item){
			var $li = li;
			var $rowLabelUl = rowLabelUl;
			var $parameters = item["parameters"];
			base.setRuleKeyword(li, rowLabelUl, item);

			if($parameters!=null){
				var $parameterUl = $li.find("ul#parameterList");
				var $parameterLiPattern = $parameterUl.find("li#parameterPattern");
				var $parameterLi = null;
				$parameterUl.parent().show();
				$rowLabelUl.find("li#parameters").text("Parameters").show();

				for(var factor in $parameters){
					$parameterLi = $parameterLiPattern.clone();
					$parameterLi.attr("id", $.formatAsId(factor));
					$parameterLi.find("#factor").text(factor);
					$parameterLi.find("#parameter").text($parameters[factor]);
					$parameterLi.show();
					$parameterUl.append($parameterLi);
				}
			}
		};

		base.setRuleKeyword = function(li, rowLabelUl, item){
			var $li = li;
			var $rowLabelUl = rowLabelUl;
			var $ruleKeyword = item["ruleKeyword"];
			var keywords = null;
			if ($ruleKeyword!=null) keywords = item["ruleKeyword"]["keyword"];

			if(keywords!=null && keywords.length > 0){
				var $keywordUl = $li.find("ul#keywordList");
				var $keywordLiPattern = $keywordUl.find("li#keywordPattern");
				var $keywordLi = null;
				$keywordUl.parent().show();
				$rowLabelUl.find("li#keywords").text("Keywords").show();

				for(var i=0; i<keywords.length; i++){
					$keywordLi = $keywordLiPattern.clone();
					$keywordLi.attr("id", i+1);
					$keywordLi.find("#keyword").text(keywords[i]);
					$keywordLi.show();
					$keywordUl.append($keywordLi);
				}
			}		
		};

		base.setFacetItemCompare = function(li, rowlabel, item){
			var $li = li;
			var groups = item["groups"];
			var $rowLabelUl = rowlabel;
			var $group, $groupItems = null;

			var $groupUl = $li.find("ul#groupList");
			var $groupLiPattern = $groupUl.find("li#groupPattern");
			var groupItemName = "";

			if(groups.length){
				$groupUl.parent().show();
				$rowLabelUl.find("li#groups").text("Highlighted").show();
				for (var idx in groups){
					$group = groups[idx];
					$groupItems = $group["groupItem"];

					$groupLi = $groupLiPattern.clone();
					$groupLi.attr("id", $.formatAsId($group["groupName"]));
					$groupLi.find("#groupName").text($group["groupName"]);
					$groupLi.find("#groupSort").text($group["sortTypeLabel"]);
					$groupLi.show();

					$groupItemUl = $groupLi.find("ul#groupItemList");
					$groupItemLiPattern = $groupItemUl.find("li#groupItemPattern"); 

					//Populate items
					for (var itemIdx in $groupItems){
						$groupItemLi = $groupItemLiPattern.clone();
						groupItemName = $groupItems[itemIdx];
						$groupItemLi.attr("id", $.formatAsId(groupItemName));
						$groupItemLi.text(groupItemName);
						$groupItemLi.show();
						$groupItemUl.append($groupItemLi);
					}

					$groupUl.append($groupLi);
				}
			}

		};
		
		base.setProduct = function(li, item){
			var $pLi = li;
			var product = item;
			
			if(product["memberType"]==="FACET"){
				var imagePath ="";
				switch(base.getItemType(product)){
				case "ims": imagePath = GLOBAL_contextPath + '/images/ims_img.jpg'; break;
				case "cnet": imagePath = GLOBAL_contextPath + '/images/productSiteTaxonomy_img.jpg'; break;
				case "facet":  imagePath = GLOBAL_contextPath + '/images/facet_img.jpg'; break;
				};

				if($.isNotBlank(imagePath)){
					setTimeout(function(){
					$pLi.find("#prodImage").attr("src", imagePath).off().on({
						error:function(){ 
							$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image.jpg"); 
						}
					});
					},10);
				}

				$pLi.find("#prodInfo").text(product["condition"]["readableString"]);
			}else if(product["memberType"]==="PART_NUMBER"){
				if($.isNotBlank(product["dpNo"])){
					if($.isNotBlank(product["imagePath"])){
						setTimeout(function(){
						$pLi.find("#prodImage").attr("src", product["imagePath"]).off().on({
							error:function(){ 
								$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image.jpg"); 
							}
						});
						},10);
					}
					$pLi.find("#prodInfo > #prodSKU").text(product["dpNo"]);
					$pLi.find("#prodInfo > #prodBrand").text(product["manufacturer"]);
					$pLi.find("#prodInfo > #prodMfrNo").text(product["mfrPN"]);							
				}else{
					$pLi.find("#prodImage").attr("src", GLOBAL_contextPath + '/images/padlock_img.jpg').off().on({
						error:function(){ 
							$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image.jpg"); 
						}
					});
					$pLi.find("#prodInfo").text("Product details not available. Product id is " + product["edp"]);
				}
			}
		};

		base.setProductCompare = function(li, rowlabel, item){
			var $li = li;
			var products = item["products"];
			var $rowLabelUl = rowlabel;

			if(products!=null && products.length){
				var $ul = $li.find("ul#prodList");
				var $pattern = $ul.find("li#prodPattern");
				var $pLi = null;

				$ul.parent().show();
				$rowLabelUl.find("li#products").text("Products").show();

				for (var pXml in products){
					var product = products[pXml];
					$pLi = $pattern.clone();
					$pLi.attr("id", product["memberId"]);
					base.setProduct($pLi, product);
					$pLi.show();
					$ul.append($pLi);
				}
			}

		};

		base.addSaveButtonListener = function(){
			var $content = base.contentHolder;

			$content.find("a#cancelBtn, a#saveBtn").off().on({
				click: function(e){

					var name = $content.find("input#name").val();
					var notes = $content.find("textarea#notes").val();

					switch($(e.currentTarget).attr("id")){
					case "saveBtn":
						if (!requestOngoing) {
							requestOngoing = true;
							
							if(!validateGeneric('Name', name, 1, 100) || !validateGeneric('Notes', notes, 1, 255)){
								requestOngoing = false;
								return;
							}
							base.createVersion(name, notes);
						}
						break;
					case "cancelBtn": 
						base.api.destroy();
						break;
					}	
				}
			});
		};

		base.createVersion = function(name, notes){
			RuleVersionServiceJS.createRuleVersion(base.options.ruleType, base.options.rule["ruleId"], name, notes, {
				callback: function(data){
					if (data) {
						jAlert("Successfully created back up!");
						base.getAvailableVersion();
					} else {
						jAlert("Failed creating back up!");
					}
				},
				postHook:function(){
					requestOngoing = false;
				}
			});
		};

		base.addDeleteVersionListener = function(tr, item){
			var $tr = tr;
			var $item = item;
			var $content = base.contentHolder;

			$tr.find(".deleteIcon").off().on({
				click:function(e){
					jConfirm("Delete restore point version " + e.data.item["name"] + "?" , "Delete Version", function(result){
						if(result){
							if (base.options.deletePhysically) {
								RuleVersionServiceJS.deleteRuleVersionPhysically(base.options.ruleType, base.options.rule["ruleId"], e.data.item["version"], {
									callback:function(data){
										if(data){
											jAlert("Rule version "+e.data.item["name"]+" successfully deleted.","Delete Version");
											$content.find("li#ver_" + e.data.item["version"]).remove();
											$content.find("div#vHeader_" + e.data.item["version"]).remove();
											base.getAvailableVersion();
										}
										else{
											jAlert("Failed to delete rule version "+e.data.item["name"] + ".","Delete Version");
										}
									}
								});
								
							} else {
								RuleVersionServiceJS.deleteRuleVersion(base.options.ruleType, base.options.rule["ruleId"], e.data.item["version"], {
									callback:function(data){
										if(data){
											jAlert("Rule version "+e.data.item["name"]+" successfully deleted.","Delete Version");
											$content.find("li#ver_" + e.data.item["version"]).remove();
											$content.find("div#vHeader_" + e.data.item["version"]).remove();
											base.getAvailableVersion();
										}
										else{
											jAlert("Failed to delete rule version "+e.data.item["name"] + ".","Delete Version");
										}
									}
								});
							}
						}
					});
				}
			},{item: $item});
		};

		base.addRestoreVersionListener = function(tr, item){
			var $tr = tr;
			var $item = item;

			$tr.find(".restoreIcon").off().on({
				click:function(e){
					if (!e.data.locked) {
						jConfirm("Restore data to version " + e.data.item["name"] + "?" , "Restore Version", function(result){
							if(result){
								base.restoreVersion(e.data.item);
							}
						});
					}
				},
				mouseenter: showHoverInfo
			},{item: $item, locked: base.options.locked, message: "You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked."});
		};

		base.restoreVersion = function(item){
			RuleVersionServiceJS.restoreRuleVersion(base.options.ruleType, base.options.rule["ruleId"], item["version"], {
				callback:function(data){

				},
				preHook:function(){
					base.options.preRestoreCallback(base);
				},
				postHook:function(){
					base.options.postRestoreCallback(base, base.options.rule);
				}
			});
		};

		base.addDownloadVersionListener = function(tr, item){
			var $tr = tr;

			$tr.find(".downloadIcon").download({
				headerText:"Download Version " + item['version'],
				moduleName: base.options.moduleName,
				ruleType: base.options.ruleType,  
				rule: base.options.rule,
				solo: $(".internal-tooltip"),
				classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped internal-tooltip',
				requestCallback:function(e) {
					base.downloadVersion(e, item);
				}
			});
		};

		base.downloadVersion = function(e, item){
			var params = new Array();
			var url = document.location.pathname + "/version/xls/" + item['version'];
			var urlParams = "";
			var count = 0;

			params["filename"] = e.data.filename;
			params["type"] = e.data.type;
			params["keyword"] = base.options.rule["ruleName"];
			params["id"] = base.options.rule["ruleId"];
			params["clientTimezone"] = +new Date();

			for(var key in params){
				if (count>0) urlParams +='&';
				urlParams += (key + '=' + encodeURIComponent(params[key]));
				count++;
			};

			document.location.href = url + '?' + urlParams;
		};

		base.getAvailableVersion = function(){
			var $content = base.contentHolder;
			base.ruleMap = {};

			$content.find("#preloader").show();
			
			$content.find("input#name").val('');
			$content.find("textarea#notes").val('');
			
			$content.find("#compareSection").hide();
			
			if (base.options.enableCompare) {
				RuleVersionServiceJS.getCurrentRuleXml(base.options.ruleType, base.options.rule["ruleId"],{
					callback: function(data){
						if(data!=null){
							base.ruleMap["current"] = data;
						}
					},
					postHook: function(){
						$content.find("#preloader").hide();
						$content.find("#compareSection").show();
						base.getRuleVersions();
					}
				});
			} else {
				base.getRuleVersions();
			}
		};

		base.getRuleVersions = function() {
			var $content = base.contentHolder;
			var $table = $content.find("table#versionList");
			RuleVersionServiceJS.getRuleVersions(base.options.ruleType,base.options.rule["ruleId"], {
				callback: function(data){
					$table.find("tr.itemRow:not(#itemPattern)").remove();

					if(data.length>0){
						$table.find("tr#empty_row").hide();
					}else{
						$table.find("tr#empty_row").show();
					}

					for (var i in data){
						var item = data[i];
						var version = item["version"];
						var $tr = $table.find("tr#itemPattern").clone();

						if(!item["deleted"]){
							base.ruleMap[version] = item;
							$tr.prop("id", "item" + $.formatAsId(version));

							$tr.find("td#itemId").html(item["version"]);
							$tr.find("td#itemDate").text(item["formattedCreatedDateTime"]);
							$tr.find("td#itemInfo > p#name").html(item["name"]);
							$tr.find("td#itemInfo > p#notes").html(item["notes"]);

							base.addDeleteVersionListener($tr, item);
							base.addRestoreVersionListener($tr, item);
							base.addDownloadVersionListener($tr, item);
							$tr.show();
							$table.append($tr);
						}
					}
					$table.find("tr.itemRow:not(#itemPattern):even").addClass("alt");
				},
				postHook:function(){
					$table.find("tr#preloader").remove();
					
					if (base.options.enableCompare) {
						base.addCompareButtonListener();
						$table.find("input.selectOne").off().on({
							click:function(e){
								if($(this).is(':checked')==true) {
									$table.find("input.selectOne").each(function() {
										$(this).prop('checked',false);
									});

									$(this).prop('checked',true);
								};
							}
						});
					}
				}
			});
		};

		base.getTemplate = function(){
			var template  = '';

			if (base.options.enableCompare) {
				template += '<div style="width:845px">';
			} else {
				template += '<div>';
			}

			template += '<div id="versionWrapper" style="floatL w400">';		

			template += '	<div id="version" class="floatL w400">';
			template += '		<div class="w400 mar0 pad0">';
			template += '			<table class="tblItems w100p marT5">';
			template += '				<tbody>';
			template += '					<tr>';

			if (base.options.enableCompare) {
				template += '						<th class="displayBlock w65">';
				template += '							<a id="compareBtn" href="javascript:void(0);" class="btnGraph btnCompare clearfix">';
				template += '								<div class="btnGraph btnCompare"></div>';
				template += '							</a>';
				template += '						</th>';
			} else {
				template += '						<th width="48px">';
				template += '						</th>';
			}

			template += '						<th class="w155">Name</th>';
			template += '						<th class="w140">Date</th>';
			template += '						<th class="w55"></th>';
			template += '					</tr>';
			template += '				<tbody>';
			template += '			</table>';
			template += '		</div>';
			template += '		<div class="w400 mar0 pad0" style="max-height:180px; overflow-y:auto;">';
			template += '			<table id="versionList" class="tblItems w100p">';
			template += '				<tbody>';
			template += '					<tr id="itemPattern" class="itemRow" style="display: none">';

			if (base.options.enableCompare) {
				template += '						<td width="24px" class="txtAC" id="itemSelect">';
				template += '	                   	<input id="select" type="radio" class="selectOne"/>';
				template += '						</td>';
				template += '						<td width="28px" class="txtAC" id="itemId"></td>';
			} else {
				template += '						<td width="52px" class="txtAC" id="itemId" colspan="2"></td>';
			}

			template += '						<td width="120px" class="txtAC" id="itemInfo">';
			template +=	'							<p id="name" class="w120 breakWord fbold"></p>';
			template +=	'							<p id="notes" class="w120 fsize11 breakWord"></p>';
			template += '						</td>';
			template += '						<td width="120px" class="txtAC" id="itemDate"></td>';
			if (base.options.enableSingleVersionDownload) {
				template += '						<td width="auto" style="min-width:60px" class="txtAC">';
				template += '                           <label class="downloadIcon floatL w20 posRel topn2" style="cursor:pointer"><img alt="Download" title="Download" src="' + GLOBAL_contextPath + '/images/iconDownload.png" class="top2 posRel"></label>';
			} else {
				template += '						<td width="auto" style="min-width:40px" class="txtAC">';
			}
			
			template += '							<label class="restoreIcon floatL w20 posRel topn2" style="cursor:pointer"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"></label>';
			template += '							<label class="deleteIcon floatL w20 posRel topn2" style="cursor:pointer"><img alt="Delete Backup" title="Delete Backup" src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="top2 posRel"></label>';
			template += '						</td>';
			template += '					</tr>';
			template += '					<tr id="preloader">';
			template += '						<td colspan="6" class="txtAC">';
			template += '							<img id="preloader" alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">';	
			template += '						</td>';
			template += '					</tr>';
			template += '					<tr id="empty_row" style="display:none">';
			template += '						<td colspan="6" class="txtAC">';
			template += '							No available version for this rule';	
			template += '						</td>';
			template += '					</tr>';
			template += '				</tbody>';
			template += '			</table>';
			template += '		</div>';
			
			template += '	<div id="addVersion">';
			template += '		<div id="actionBtn" class="floatL marT10 fsize12 border marB20" style="background: #f3f3f3; width:400px;" >';
			template += '			<table class="tblItems" style="width:100%;">';
			template += '				<tbody>';
			template += '					<tr>';
			template += '						<th colspan="4" style="font-weight:bold;font-size:13px;">Create New Rule Version</th>';
			template += '					</tr>';			
			template += '				</tbody>';
			template += '			</table>';

			template += '			<table style="width:100%;border-top:1px solid #ccc;">';
			template += '				<tbody>';
			template += '					<tr>';
			template += '					</tr>';			
			template += '					<tr>';
			template += '						<td colspan="4">&nbsp;</td>';		
			template += '					</tr>';			
			template += '					<tr>';
			template += '						<td>&nbsp;</td>';		
			template += '						<td><span class="fred">*</span> Name:</td>';			
			template += '						<td>&nbsp;</td>';	
			template += '						<td><input type="text" id="name" class="w260" style="height:20px;"></td>';
			template += '					</tr>';		
			template += '					<tr>';
			template += '						<td>&nbsp;</td>';			
			template += '						<td><span class="fred">*</span> Notes:</td>';
			template += '						<td>&nbsp;</td>';	
			template += '						<td><textarea id="notes" class="w260" style="height:50px"></textarea></td>';
			template += '					</tr>';
			template += '				<tbody>';
			template += '			</table>';
			template += '		<div align="right" style="margin:5px;">';
			template += '			<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Create Version</div>';
			template += '			</a>';
			template += '			<a id="cancelBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Cancel</div>';
			template += '			</a>';
			template += '		</div>';

			template += '	</div>'; // end addVersion
			template += '	</div>';	// end w400		
			template += '</div>'; // end w700
			template += '	</div>'; //end version
			return template;
		};

		base.getItemListTemplate =function(){
			var template  = '';

			template += '	<div class="version floatR border" style="width: 435px;">';
			template += '	<div id="preloader"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-circ.gif"></div>';
			template += '	<div id="compareSection" style="display:none">';
			template += '	<div style="width:100%;height:28px;padding-top:5px;background:#3f63a1;border-bottom:2px solid">';
			template += '	<div id="vHeaderList">';
			template += '		<div class="floatL" style="padding:5px; width:110px;"> &nbsp; </div>';
			template += '		<div id="vPattern" class="vHeader" style="display:none">';
			template += '			<div id="ver" class="floatL" style="background:#3f63a1;font:Arial, Helvetica, sans-serif;color:#fff; font-size:13px;padding:5px; width:129px; margin-left:10px"></div>';
			template += '		</div>';
			template += '	</div>';
			template += '	</div>';

			template += '	<div class="clearB"></div>';
			template += '	<div style="overflow-x:hidden;overflow-y:auto; height:343px">';			
			template += '		<div style="float:left; width:120px">';// label
			template += '			<ul id="rowLabel" class="w100p" style="font-weight:bold;margin-top:-6px;background:#cbd4e6;">';
			template += '				<li style="background:#fff;"></li>';
			template += '				<li>Created By</li>';
			template += '				<li style="height:24px">Date</li>';
			template += '				<li>Name</li>';
			template += '				<li>Notes</li>';
			template += '				<li>Rule ID</li>';
			template += '				<li>Rule Name</li>';
			template += '				<li id="products" style="display:none"></li>';
			template += '				<li id="groups" style="display:none"></li>';
			template += '				<li id="keywords" style="display:none"></li>';
			template += '				<li id="parameters" style="display:none"></li>';
			template += '				<li id="redirectType" style="display:none"></li>';
			template += '				<li id="redirectKeyword" style="display:none"></li>';
			template += '				<li id="conditions" style="display:none"></li>';
			template += '			</ul>';
			template += '		</div>';// end label

			template += '		<div class="horizontalCont" style="float:left; width:290px;">';// content
			template += '			<ul id="versionList">';
			template += '				<li id="itemPattern" class="item" style="display:none;border:0; padding-top: 5px;">';
			template += '					<ul id="ruleDetails" style="border:0">';
			template += '						<li id="restoreLink" style="border-bottom:2px solid #0C2A62;padding-right:0px;"><label class="restoreIcon topn2" style="background:#f5f8ff"><a id="restoreBtn" href="javascript:void(0);"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"> Restore </a></label></li>';
			template += '						<li id="verCreatedBy">Not Available</li>'; 
			template += '						<li id="verDate" style="height: 24px;">Not Available</li>';
			template += '						<li id="verName">Not Available</li>'; 
			template += '						<li id="verNote">Not Available</li>'; 
			template += '						<li id="ruleId"></li>'; 
			template += '						<li id="ruleName"></li>';
			template += '						<li id="products" style="display:none;border:0;background:#f1f4fb;">';
			template += '							<ul id="prodList">';
			template += '								<li id="prodPattern" class="prod" style="display:none">';
			template += '									<img id="prodImage" src="' + GLOBAL_contextPath + '/images/no-image.jpg"/>';
			template += '									<div id="prodInfo">';
			template += '										<p id="prodSKU"></p>';
			template += '										<p id="prodBrand"></p>';
			template += '										<p id="prodMfrNo"></p>';
			template += '									<div>';
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '						<li class="groups" style="display:none;background:#ecf0f8;border:0">';
			template += '							<ul id="groupList">';
			template += '								<li id="groupPattern" class="group" style="display:none;border:0">';
			template += '									<p id="groupName" style="background:#f1f4fb;font-weight:bold; border-bottom:1px solid #ccc;" ></p>';
			template += '									<ul id="groupItemList">';
			template += '										<li id="groupItemPattern" class="groupItem" style="display:none;border:0;"></li><li style="border:0;"></li>';
			template += '									</ul>';			
			template += '									<p id="groupSort" style="border-bottom:1px solid #ccc;font-style:italic;"></p>';
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '						<li id="keywords" style="display:none">';
			template += '							<ul id="keywordList">';
			template += '								<li id="keywordPattern" class="keyword" style="display:none">';
			template += '									<p id="keyword" style="font-weight:bold;"></p>';
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '						<li id="parameters" style="display:none;border:0;background:#f1f4fb;">';
			template += '							<ul id="parameterList">';
			template += '								<li id="parameterPattern" class="parameter" style="display:none;border:0">';
			template += '									<p id="factor" style="border-bottom:1px solid #ccc;font-weight:bold;"></p>';
			template += '									<p id="parameter" style="border:0;"></p>';
			template += '									<p style="border-top:1px solid #ccc;"></p>';			
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '						<li id="redirectType" style="display:none">UNKNOWN</li>';
			template += '						<li id="redirectKeyword" style="display:none">NONE</li>';
			template += '						<li id="conditions" style="display:none">';
			template += '							<p>Include Keyword: <span id="includeKeyword" class="fbold">NO</span></p>';
			template += '							<ul id="conditionList">';
			template += '								<li id="conditionPattern" class="condition" style="display:none">';
			template += '									<p id="condition"></p>';
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '					</ul>';
			template += '				</li>';
			template += '			</ul>';
			template += '		</div>';// end content
			template += '	</div>';
			template += '	</div>';
			template += '	</div>';

			return template;
		};

		// Run initializer
		base.init();
	};

	$.version.defaultOptions = {
			moduleName: "",
			headerText: "",
			ruleType: "",
			rule: null,
			limit: 3,
			locked: true,
			deletePhysically: false,
			enableCompare: true,
			enableSingleVersionDownload: false,
			beforeRequest: function(){},
			afterRequest: function(){},
			preRestoreCallback: function(base){},
			postRestoreCallback: function(base, rule){},
			afterClose:function(){}
	};

	$.fn.version = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.version(this, options));
			});
		};
	};

})(jQuery);
