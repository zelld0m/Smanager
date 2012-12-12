(function($){

	$.version = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("version", base);

		base.init = function(){
			base.options = $.extend({},$.version.defaultOptions, options);
			base.$el.empty();
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
					modal:true
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getTemplate());
						base.contentHolder.find("#versionWrapper").before(base.getItemListTemplate());
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

			$content.find("a#compareBtn").on({
				click: function(e){
					base.selectedVersion = [];
					base.selectedVersion.push("current");
					$content.find("table#versionList").find("tr.itemRow:not(#itemPattern) > td#itemSelect > input[type='checkbox']:checked").each(function(index, value){
						base.selectedVersion.push($(value).parents("tr.itemRow").attr("id").split("_")[1]);
					});
					base.setCompare();
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
				$li.find("#restoreLink").hide();
				
				if(index !== "current"){
					$li.find("#restoreLink").show();
					$li.find("#verDate").text(item["createdDate"].toUTCString());
					$li.find("#verName").text(item["name"]);
					$li.find("#verNote").text(item["notes"]);
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
					$groupLi.find("#groupSort").text($group["sortType"]);
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

					if(product["memberType"]==="FACET"){
						var imagePath ="";
						switch(base.getItemType(product)){
						case "ims": imagePath = GLOBAL_contextPath + '/images/ims_img.jpg'; break;
						case "cnet": imagePath = GLOBAL_contextPath + '/images/productSiteTaxonomy_img.jpg'; break;
						case "facet":  imagePath = GLOBAL_contextPath + '/images/facet_img.jpg'; break;
						};

						if($.isNotBlank(imagePath))
							$pLi.find("#prodImage").attr("src", imagePath);

						$pLi.find("#prodInfo").text(product["condition"]["readableString"]);
					}else if(product["memberType"]==="PART_NUMBER"){
						if($.isNotBlank(product["dpNo"])){
							$pLi.find("#prodImage").attr("src", product["imagePath"]);
							$pLi.find("#prodInfo > #prodSKU").text(product["dpNo"]);
							$pLi.find("#prodInfo > #prodBrand").text(product["manufacturer"]);
							$pLi.find("#prodInfo > #prodMfrNo").text(product["mfrPN"]);							
						}else{
							$pLi.find("#prodImage").attr("src", GLOBAL_contextPath + '/images/padlock_img.jpg');
							$pLi.find("#prodInfo").text("Product details not available. Product id is " + product["edp"]);
						}
					}

					$pLi.show();
					$ul.append($pLi);
				}
			}

		};

		base.addSaveButtonListener = function(){
			var $content = base.contentHolder;

			$content.find("a#cancelBtn, a#saveBtn").on({
				click: function(e){

					var name = $content.find("input#name").val();
					var notes = $content.find("textarea#notes").val();

					switch($(e.currentTarget).attr("id")){
					case "saveBtn": 

						if(!validateField('Name', name, 1) || !validateField('Notes', notes, 1)){
							return;
						}

						if (name.length>100){
							jAlert("Name should not exceed 100 characters.");
							return
						}

						if (notes.length>255){
							jAlert("Notes should not exceed 255 characters.");
							return
						}

						base.createVersion(name, notes);

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
							RuleVersionServiceJS.deleteRuleVersion(base.options.ruleType, base.options.rule["ruleId"], e.data.item["version"], {
								callback:function(data){
									$content.find("li#ver_" + e.data.item["version"]).remove();
									$content.find("div#vHeader_" + e.data.item["version"]).remove();
									base.getAvailableVersion();
								}
							});
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
					jConfirm("Restore data to version " + e.data.item["name"] + "?" , "Restore Version", function(result){
						if(result){
							RuleVersionServiceJS.restoreRuleVersion(base.options.ruleType, base.options.rule["ruleId"], e.data.item["version"], {
								callback:function(data){

								},
								preHook:function(){
									base.options.preRestoreCallback(base);
								},
								postHook:function(){
									base.options.postRestoreCallback(base, base.options.rule);
								}
							});
						}
					});
				}
			},{item: $item});
		};

		base.getAvailableVersion = function(){
			var $content = base.contentHolder;
			var $table = $content.find("table#versionList");
			base.ruleMap = {};

			RuleVersionServiceJS.getCurrentRuleXml(base.options.ruleType, base.options.rule["ruleId"],{
				callback: function(data){
					if(data!=null){
						base.ruleMap["current"] = data;
					}
				},
				postHook: function(){
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

								base.ruleMap[version] = item;
								$tr.prop("id", "item" + $.formatAsId(version));
								$tr.find("td#itemId").html(item["version"]);
								$tr.find("td#itemDate").html(item["createdDate"].toUTCString());
								$tr.find("td#itemInfo > p#name").html(item["name"]);
								$tr.find("td#itemInfo > p#notes").html(item["notes"]);
								base.addDeleteVersionListener($tr, item);
								base.addRestoreVersionListener($tr, item);
								$tr.show();
								$table.append($tr);
							}
							$table.find("tr.itemRow:not(#itemPattern):even").addClass("alt");
						},
						postHook:function(){
							$table.find("tr#preloader").remove();
							base.addCompareButtonListener();
						}
					});
				}
			});


		};

		base.getTemplate = function(){
			var template  = '';

			template += '<div style="width:845px">';
			template += '<div id="versionWrapper" style="floatL w400">';
			template += '	<h2 class="notification">This is the rule status section</h2>';			

			template += '	<div id="version" class="floatL w400">';
			template += '		<div class="w400 mar0 pad0">';
			template += '			<table class="tblItems w100p marT5">';
			template += '				<tbody>';
			template += '					<tr>';
			template += '						<th class="displayBlock w60">';
			template += '							<a id="compareBtn" href="javascript:void(0);" class="btnGraph btnCompare clearfix">';
			template += '								<div class="btnGraph btnCompare"></div>';
			template += '							</a>';
			template += '						</th>';
			template += '						<th class="w160">Name</th>';
			template += '						<th class="w135">Date</th>';
			template += '						<th class="w55"></th>';
			template += '					</tr>';
			template += '				<tbody>';
			template += '			</table>';
			template += '		</div>';
			template += '		<div class="w400 mar0 pad0" style="max-height:180px; overflow-y:auto;">';
			template += '			<table id="versionList" class="tblItems w100p">';
			template += '				<tbody>';
			template += '					<tr id="itemPattern" class="itemRow" style="display: none">';
			template += '						<td width="24px" class="txtAC" id="itemSelect">';
			template += '	                   	<input id="select" type="checkbox"/>';
			template += '						</td>';
			template += '						<td width="28px" class="txtAC" id="itemId"></td>';
			template += '						<td width="120px" class="txtAC" id="itemInfo">';
			template +=	'							<p id="name" class="w120 breakWord fbold"></p>';
			template +=	'							<p id="notes" class="w120 fsize11 breakWord"></p>';
			template += '						</td>';
			template += '						<td width="120px" class="txtAC" id="itemDate"></td>';
			template += '						<td width="auto" style="min-width:40px" class="txtAC">';
			template += '							<label class="restoreIcon floatL w20 posRel topn2"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"></label>';
			template += '							<label class="deleteIcon floatL w20 posRel topn2"><img alt="Delete Backup" title="Delete Backup" src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="top2 posRel"></label>';
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
			template += '	</div>'; //end version

			template += '	<div id="addVersion">';
			template += '		<div id="actionBtn" class="floatL marT10 fsize12 border pad10 w380 marB20" style="background: #f3f3f3;">';
			template += '			<h3 style="border:none;">Rule Version</h3>';
			template += '			<div class="fgray padL10 padR10 padB15 fsize11">';
			template += '			<p align="justify">';
			template += '				Before approving any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>';
			template += '				If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.';
			template += '			</p>';
			template += '		</div>';

			template += '		<div>';
			template += '			<label class="floatL padL13 w100 marT5"><span class="fred">*</span>Name:</label>';
			template += '			<label class="floatL w260 marT5"><input type="text" id="name" class="w260"></label>';
			template += '			<div class="clearB"></div>';
			template += '			<label class="floatL padL13 marT5 w100"><span class="fred">*</span>Notes:</label>';
			template += '			<label class="floatL w260 marT5"><textarea id="notes" class="w260" style="height:32px"></textarea></label>';
			template += '		</div>';

			template += '		<div class="clearB"></div>';
			template += '		<div align="right" class="marT10">';
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

			return template;
		};

		base.getItemListTemplate =function(){
			var template  = '';

			template += '	<div class="version w425 floatR border">';

			template += '	<div id="vHeaderList">';
			template += '		<div class="floatL" style="padding:5px; width:110px;"> &nbsp; </div>';
			template += '		<div id="vPattern" class="vHeader" style="display:none">';
			template += '			<div id="ver" class="floatL titleVersion" style="padding:5px; width:129px;"></div>';
			template += '		</div>';
			template += '	</div>';

			template += '	<div class="clearB"></div>';
			template += '	<div style="overflow-x:hidden; overflow-y:auto; height:343px">';
			template += '		<div style="float:left; width:120px">';// label
			template += '			<ul id="rowLabel" class="w100p" style="margin-top:23px">';
			template += '				<li></li>';
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

			template += '		<div class="horizontalCont" style="float:left; width:280px;">';// content
			template += '			<ul id="versionList">';
			template += '				<li id="itemPattern" class="item" style="display:none">';
			template += '					<ul id="ruleDetails">';
			template += '						<li id="restoreLink" style="display:none"><label class="restoreIcon topn2"><a id="restoreBtn" href="javascript:void(0);"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"> Restore </a></label></li>';
			template += '						<li id="verCreatedBy">Not Available</li>'; 
			template += '						<li id="verDate">Not Available</li>';
			template += '						<li id="verName">Not Available</li>'; 
			template += '						<li id="verNote">Not Available</li>'; 
			template += '						<li id="ruleId"></li>'; 
			template += '						<li id="ruleName"></li>';
			template += '						<li id="products" style="display:none">';
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
			template += '						<li class="groups" style="display:none">';
			template += '							<ul id="groupList">';
			template += '								<li id="groupPattern" class="group" style="display:none">';
			template += '									<p id="groupName"></p>';
			template += '									<p id="groupSort"></p>';
			template += '									<ul id="groupItemList">';
			template += '										<li id="groupItemPattern" class="groupItem" style="display:none"></li>';
			template += '									</ul>';
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '						<li id="keywords" style="display:none">';
			template += '							<ul id="keywordList">';
			template += '								<li id="keywordPattern" class="keyword" style="display:none">';
			template += '									<p id="keyword"></p>';
			template += '								</li>';
			template += '							</ul>';
			template += '						</li>';
			template += '						<li id="parameters" style="display:none">';
			template += '							<ul id="parameterList">';
			template += '								<li id="parameterPattern" class="parameter" style="display:none">';
			template += '									<p id="factor"></p>';
			template += '									<p id="parameter"></p>';
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