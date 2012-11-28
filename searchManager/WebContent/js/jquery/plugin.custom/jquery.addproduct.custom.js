(function($){

	$.addproduct = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM add products of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("addproduct", base);

		base.init = function(){
			base.options = $.extend({},$.addproduct.defaultOptions, options);

			base.promptRuleItemDetails(this, base.options.type);

		};

		base.getAddProductItemTemplate = function(){
			var template ='';

			template  += '<div>';
			template  += '	<div id="addItemTemplate" class="mar0 w250">';
//			template  += '		<h3 class="padT10"></h3>';
			template  += '		<div class="clearB"></div>';
			template  += '		<div class="floatL marT5 marL5">';
			template  += '			<label class="w60 floatL padT5">SKU #: </label>';
			template  += '			<label>';
			template  += '				<span class="fsize10 fgray txtAR" style="bottom:-3px; position:relative">(separated by whitespaces or commas)</span>';
			template  += '				<textarea id="addItemDPNo" style="width: 230px; float: left; margin-bottom: 7px"></textarea>';
			template  += '			</label>';
			template  += '		</div>';
			template  += '		<div class="floatL w155 marT5">';
			template  += '			<label class="floatL w60 marL5 padT5">Valid Until:</label> ';
			template  += '			<label class="ddate"><input id="addItemDate" type="text" class="w65"></label>';
			template  += '		</div>';

			if (base.options.showPosition){
				template  += '		<div class="floatL marT5" style="width: 97px">';
				template  += '			<label class="floatL marL5 padT5 w60">Position:</label>';
				template  += '			<label><input id="addItemPosition" maxlength="2" type="text" class="w25"></label>';
				template  += '		</div>';
				template  += '		<div class="clearB"></div>';
				template  += '		<div class="floatL marT5 marL5">';
			}
			template  += '		<div class="clearB"></div>';
			template  += '			<label class="w60 floatL padT5">Comment: </label> ';
			template  += '			<label><textarea id="addItemComment" style="margin-bottom: 7px" class="floatL w230"></textarea></label>';
			template  += '		</div>';
			template  += '		<div class="clearB"></div>';
			template  += '		<div align="right">';
			template  += '			<a id="addItemToRuleBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '				<div class="buttons fontBold">Add</div>';
			template  += '			</a>';
			template  += '			<a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '				<div class="buttons fontBold">Clear</div>';
			template  += '			</a>';
			template  += '		</div>';
			template  += '		<div class="clearB"></div>';
			template  += '	</div>';
			template  += '</div>';

			return template;
		},

		base.getAddFacetItemTemplate = function(){
			var template ='';

			template  += '<div id="facetItem">';
			template  += '	<h3 id="conditionText" class="fLblue w500 breakWord fsize12"></h3>';
			template  += '	<div class="clearB"></div>';
			template  += '	<div id="tabHeight" style="height:29.5px" class="borderB marT20">';
			template  += '	<ul>';
			template  += '		<li><a href="#ims"><span>IMS Categories/Manufacturer</span></a></li>';
			template  += '		<li><a href="#cnet"><span>Product Site Taxonomy/Manufacturer</span></a></li>';
			template  += '		<li><a href="#dynamicAttribute"><span>Dynamic Attributes</span></a></li>';
			template  += '		<li><a href="#facet"><span>Facets</span></a></li>';
			template  += '	</ul>';
			template  += '	<div class="clearB"></div>';
			template  += '	</div>';

			template  += '	<div id="ims" class="w500">';
			template  += '		<div class="holder fsize12 padT40 marRL20">';
			template  += '			<table class="imsFields">';				
			template  += '				<tr class="catName">';
			template  += '					<td class="w175 padB8" valign="bottom">Category :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<div class="floatL fsize11 txtDecoUL padB10">';
			template  += '							<a class="switchToCatCode" href="javascript:void(0);">Use category codes instead &raquo;</a>';
			template  += '						</div>';
			template  += '						<div style="clear:both"></div>';
			template  += '						<img id="preloaderCategoryList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="categoryList" class="categoryList selectCombo w229" title="Select Category"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="subcategory">';
			template  += '					<td class="w175">SubCategory :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderSubCategoryList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="subCategoryList" class="subCategoryList selectCombo w229" title="Select SubCategory"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="class">';
			template  += '					<td class="w175">Class :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderClassList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="classList" class="classList selectCombo w229" title="Select Class"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="minor">';
			template  += '					<td class="w175">SubClass :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderMinorList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';

			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="minorList" class="minorList selectCombo w229" title="Select SubClass"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catCode">';
			template  += '					<td  class="w175 padB8" valign="bottom">Category Code :</td>';
			template  += '					<td class="padB5">';
			template  += '						<div class="floatL fsize11 marB8 txtDecoUL padT3">';
			template  += '							<a class="switchToCatName" href="javascript:void(0);">Use category names instead &raquo;</a>';
			template  += '						</div>';
			template  += '						<div class="clearB"></div>';
			template  += '						<input id="catcode" type="text" maxlength="4">';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Manufacturer :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderManufacturerList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';

			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="manufacturerList" class="manufacturerList selectCombo w229" title="Select Manufacturer"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';						
			template  += '		</div>';
			template  += '	</div>';

			template  += '	<div id="cnet" class="w500">';
			template  += '		<div class="holder fsize12 padT40 marRL20">';
			template  += '			<table class="cnetFields">';							
			template  += '				<tr class="catName" id="level1Cat">';
			template  += '					<td class="w175 padB8" valign="bottom">Level 1 Category :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<img id="preloaderLevel1CategoryList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="level1CategoryList" class="level1CategoryList selectCombo w229" title="Select Category"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="level2Cat">';
			template  += '					<td class="w175">Level 2 Category :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderLevel2CategoryList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="level2CategoryList" class="level2CategoryList selectCombo w229" title="Select SubCategory"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="level3Cat">';
			template  += '					<td class="w175">Level 3 Category :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderLevel3CategoryList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="level3CategoryList" class="level3CategoryList selectCombo w229" title="Select Class"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Manufacturer :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderCNETManufacturerList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="cnetmanufacturerList" class="cnetmanufacturerList selectCombo w229" title="Select Manufacturer"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';						
			template  += '		</div>';
			template  += '	</div>';

			template  += '	<div id="dynamicAttribute" class="w500">';
			template  += '		<div class="holder fsize12 padT40 marRL20">';

			template  += '				<table class="w460">';
			template  += '				<tr>';
			template  += '					<td class="w175 padB8" valign="bottom">Template Name :</td>';
			template  += '					<td class="iepadBT0 w278 padT1">';
			template  += '						<img id="preloaderTemplateNameList" class="floatR loadIcon marL3 posRel top6" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="templateNameList" class="templateNameList selectCombo w229" title="Select Template Name"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				</table>';

			template  += '    <div style="max-height:300px;  width:460px; overflow-y:auto;" class="marB10 floatL">';
			template  += '			<table class="dynamicAttributeFields">';							
			template  += '				<tr id="dynamicAttributeValue">';
			template  += '					<td colspan="2" class="padT1">';
			template  += '						<div id="dynamicAttributeItemList">';							
			template  += '							<div id="dynamicAttributeItemPattern" class="dynamicAttributeItem marL30" style="display:none">';
			template  += '								<div class="clearB"></div>';
			template  += '								<div class="w146 floatL padL25 marT8"><span id="dynamicAttributeLabel"></span></div>';
			template  += '								<img src="' +  GLOBAL_contextPath + '/images/iconDelete.png" class="deleteAttrIcon posRel floatR marT8 marR4 marL5" alt="Delete Attribute" title="Delete Attribute">';

			template  += '								<div class="w240 floatL marT8 border" style="overflow-y:auto; max-height: 107px">';												
			template  += '									<div id="dynamicAttributeValues">';
			template  += '										<div id="dynamicAttributeValuesPattern" style="display: none;">';
			template  += '											<div class="w240">';
			template  += '												<input type="checkbox" class="checkboxFilter">';
			template  += '												<span id="attributeValueName"></span>';
			template  += '											</div>';
			template  += '										</div>';
			template  += '									</div>';
			template  += '								</div>';
			template  += '							</div>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';
			template  += '			<div class="clearB"></div>';
			template  += '		</div>';
			template  += '			<table id="addDynamicAttributeName" class="w460">';						
			template  += '				<tr id="dynamicAttributeName">';
			template  += '					<td class="w175">Add Dynamic Attribute :</td>';
			template  += '					<td class="iepadBT0 w278 padT1">';
			template  += '						<img id="preloaderTemplateNameList" class="floatR loadIcon marL3 posRel top3" src="' +  GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL posRel leftn2">';
			template  += '							<select name="select" id="dynamicAttributeList" class="dynamicAttributeList selectCombo w205" title="Add Dynamic Attribute"></select>';
			template  += '						</div>';
			template  += '						<a href="javascript:void(0);" src="" class="addDynamicAttrBtn btnGraph btnAddGrayMid floatL leftn22 posRel top3" id="addButton"></a>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';
			template  += '			<div class="clearB"></div>';
			template  += '		</div>';
			template  += '	</div>';

			template  += '	<div id="facet" class="w500">';
			template  += '		<div class="holder fsize12 padT40 marRL20">';
			template  += '			<table>';
			template  += '				<tr>';
			template  += '					<td class="w175">Name (contains) :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<input id="nameContains" type="text" class="w249"/>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Description (contains):</td>';
			template  += '					<td class="iepadBT0 padB8">';
			template  += '						<input id="descriptionContains" type="text" class="w249"/>';
			template  += '					</td>';
			template  += '				</tr>';		
			template  += '				<tr>';
			template  += '					<td class="w175">Platform :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="platformList" class="selectCombo w229" title="Select Platform" >';
			template  += '							<option value="all"></option>';
			template  += '							<option value="universal">Universal</option>';
			template  += '							<option value="pc">PC</option>';
			template  += '							<option value="linux">Linux</option>';
			template  += '							<option value="mac">Macintosh</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Condition :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="conditionList" class="selectCombo w229" title="Select Condition" >';
			template  += '							<option value="all"></option>';
			template  += '							<option value="refurbished">Refurbished</option>';
			template  += '							<option value="open">Open Box</option>';
			template  += '							<option value="clearance">Clearance</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Availability :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="availabilityList" class="selectCombo w229" title="Select Availability" >';
			template  += '							<option value="all"></option>';
			template  += '							<option value="instock">In Stock</option>';
			template  += '							<option value="call">Call</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">License :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="licenseList" class="selectCombo w229" title="Select License" >';
			template  += '							<option value="all"></option>';
			template  += '							<option value="license">Show License Products Only</option>';
			template  += '							<option value="nonlicense">Show Non-License Products Only</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';
			template  += '			<div class="clearB"></div>';	
			template  += '		</div>';
			template  += '	</div>';
			template  += '</div>';

			template  += '<div class="fcetItem marT20 w500">';
			template  += '	<h3 id="" class="breakWord borderB padB5 txtAL fsize14">Rule Item Details</h3>';
			template  += '			<table class="fsize12 marRL20">';
			template  += '				<tr>';
			template  += '					<td class="w175">Valid Until: </td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<div class="floatL w100 marT5">';
			template  += '							<label class="ddate"><input id="addItemDate" type="text" class="w65"></label>';
			template  += '						</div>';

			if (base.options.showPosition){
				template  += '						<div class="floatL marT5" style="width: 97px">';
				template  += '							<label class="floatL marL5 padT5 w60">Position:</label>';
				template  += '							<label><input id="addItemPosition" maxlength="2" type="text" class="w25"></label>';
				template  += '						</div>';
			}

			template  += '					</td>';
			template  += '			    </tr>';		
			template  += '				<tr>';
			template  += '					<td class="w175">Comment :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<textarea id="addItemComment" style="width: 245px; float: left; margin-bottom: 7px"></textarea>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';
			template  += '</div>';


			if (!base.options.locked){
				template  += '<div align="right">';
				template  += '	<a id="addFacetItemToRuleBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template  += '		<div class="buttons fontBold">' + (base.options.newRecord ? 'Add' : 'Update')  + '</div>';
				template  += '	</a>';
				template  += '	<a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template  += '		<div class="buttons fontBold">Clear</div>';
				template  += '	</a>';
				template  += '</div>';
			}


			return template;
		};

		base.getSelectedFacetFieldValues= function(){
			var condMap = new Object();
			var catCode = new Array();
			var category = new Array();
			var subCategory = new Array();
			var clazz = new Array();
			var minor = new Array();
			var manufacturer = new Array();
			var level1Cat = new Array();
			var level2Cat = new Array();
			var level3Cat = new Array();
			var cnetManufacturer = new Array();

			var $imsTab = base.contentHolder.find("div#ims"); 

			if ($imsTab.length){
				catCode[0] = $.trim($imsTab.find("input#catcode").val().toUpperCase());
				category[0] = $.trim($imsTab.find("input#categoryList").val());
				subCategory[0] = $.trim($imsTab.find("input#subCategoryList").val());
				clazz[0] = $.trim($imsTab.find("input#classList").val());
				minor[0] = $.trim($imsTab.find("input#minorList").val());
				manufacturer[0] = $.trim($imsTab.find("input#manufacturerList").val());

				if ($.isNotBlank(catCode[0])) condMap["CatCode"] = catCode;
				if ($.isNotBlank(category[0])) condMap["Category"] = category; 	
				if ($.isNotBlank(subCategory[0])) condMap["SubCategory"] = subCategory; 	
				if ($.isNotBlank(clazz[0])) condMap["Class"] = clazz; 	
				if ($.isNotBlank(minor[0])) condMap["SubClass"] = minor; 	
				if ($.isNotBlank(manufacturer[0])) condMap["Manufacturer"] = manufacturer; 	
			}

			var $cnetTab = base.contentHolder.find("div#cnet"); 

			if ($cnetTab.length){
				level1Cat[0] = $.trim($cnetTab.find("input#level1CategoryList").val());
				level2Cat[0] = $.trim($cnetTab.find("input#level2CategoryList").val());
				level3Cat[0] = $.trim($cnetTab.find("input#level3CategoryList").val());
				cnetManufacturer[0] = $.trim($cnetTab.find("input#cnetmanufacturerList").val());

				if ($.isNotBlank(level1Cat[0])) condMap["Level1Category"] = level1Cat; 	
				if ($.isNotBlank(level2Cat[0])) condMap["Level2Category"] = level2Cat; 	
				if ($.isNotBlank(level3Cat[0])) condMap["Level3Category"] = level3Cat; 	

				if ($.isNotBlank(cnetManufacturer[0])) condMap["Manufacturer"] = cnetManufacturer; 	
			}

			var $dynamicAttribute = base.contentHolder.find("div#dynamicAttribute"); 

			if($dynamicAttribute.length){
				var inTemplateName = $dynamicAttribute.find("input#templateNameList").val();
				var $divDynamicAttrItems = $dynamicAttribute.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)");

				if($.isNotBlank($.trim(inTemplateName))){
					condMap[GLOBAL_storeFacetTemplateName] = $.makeArray($.trim(inTemplateName));

					$divDynamicAttrItems.find("ul").each(function(ulInd, uEl){
						var attributeItem = $(uEl).prop("title");
						var attributeValues = new Array();

						$divDynamicAttrItems.find('input:checkbox[name="' + attributeItem + '"]:checked').each(function(inInd, inEl){
							attributeValues.push($(inEl).val()); 
						});

						if(attributeValues.length > 0)
							condMap[attributeItem] = attributeValues;
					});
				}
			}

			// Facet tab is always visible
			var $facetTab = base.contentHolder.find("div#facet"); 

			if($facetTab.length){
				var platform = $facetTab.find("input#platformList").val();
				var condition = $facetTab.find("input#conditionList").val();
				var availability = $facetTab.find("input#availabilityList").val();
				var license = $facetTab.find("input#licenseList").val();
				var nameContains = $.trim($facetTab.find("input#nameContains").val());
				var descriptionContains = $.trim($facetTab.find("input#descriptionContains").val());

				switch($.trim(platform.toLowerCase())){
				case "universal": condMap["Platform"] = ["Universal"]; break;
				case "pc": condMap["Platform"] = ["PC"]; break;
				case "linux": condMap["Platform"] = ["Linux"]; break;
				case "macintosh": condMap["Platform"] = ["Macintosh"]; break;
				}

				switch($.trim(condition.toLowerCase())){
				case "refurbished": condMap["Condition"] = ["Refurbished"]; break;
				case "open box": condMap["Condition"] = ["Open Box"]; break;
				case "clearance": condMap["Condition"] = ["Clearance"]; break;
				}

				switch($.trim(availability.toLowerCase())){
				case "in stock": condMap["Availability"] = ["In Stock"]; break;
				case "call": condMap["Availability"] = ["Call"]; break;
				}

				switch($.trim(license.toLowerCase())){
				case "show license products only": condMap["License"] = ["Show License Products Only"]; break;
				case "show non-license products only": condMap["License"] = ["Show Non-License Products Only"]; break;
				}

				if($.isNotBlank(nameContains))
					condMap["Name"] = $.makeArray(nameContains);

				if($.isNotBlank(descriptionContains))
					condMap["Description"] = $.makeArray(descriptionContains);
			}

			return condMap;
		},

		base.populateCategories = function(e){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#categoryList");
			var $input = $tab.find("input#categoryList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSCategories({
				callback: function(data){
					var list = data;

					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if($.isNotBlank($input.val())) base.populateSubcategories(e);
				},
				preHook:function(){
					$tab.find("img#preloaderCategoryList").show();
					base.clearIMSComboBox("category");
					$table.find("tr#subcategory,tr#class,tr#minor").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["Category"])){
						$select.prop("selectedText",$item.condition.IMSFilters["Category"]);
						$input.val($item.condition.IMSFilters["Category"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderCategoryList").hide();
					if($.isBlank($input.val()))
						base.populateIMSManufacturers(e);
				}
			});
		},

		base.populateSubcategories= function(e){
			var $tab = base.contentHolder.find("div#ims");
			var inCategory = $.trim($tab.find("input#categoryList").val());
			var $select = $tab.find("select#subCategoryList");
			var $input = $tab.find("input#subCategoryList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSSubcategories(inCategory, {
				callback: function(data){
					var list = data;

					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#subcategory").show();
					}else{
						$table.find("tr#subcategory").hide();
					}  

					if($.isNotBlank($input.val())) base.populateClass(e);
				},
				preHook:function(){
					$tab.find("img#preloaderSubCategoryList").show();
					base.clearIMSComboBox("subcategory");
					$table.find("tr#class,tr#minor").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["SubCategory"])){
						$select.prop("selectedText",$item.condition.IMSFilters["SubCategory"]);
						$input.val($item.condition.IMSFilters["SubCategory"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderSubCategoryList").hide();
					if($.isNotBlank(inCategory) && $.isBlank($input.val()))
						base.populateIMSManufacturers(e);
				}
			});
		},

		base.populateClass = function(e){
			var $tab = base.contentHolder.find("div#ims");
			var inCategory = $.trim($tab.find("input#categoryList").val());
			var inSubCategory = $.trim($tab.find("input#subCategoryList").val());
			var $select = $tab.find("select#classList");
			var $input = $tab.find("input#classList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSClasses(inCategory,inSubCategory, {
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#class").show();
					}else{
						$table.find("tr#class").hide();
					}  

					if($.isNotBlank($input.val())) base.populateMinor(e);
				},
				preHook:function(){
					$tab.find("img#preloaderClassList").show();
					base.clearIMSComboBox("class");
					$table.find("tr#minor").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["Class"])){
						$select.prop("selectedText",$item.condition.IMSFilters["Class"]);
						$input.val($item.condition.IMSFilters["Class"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderClassList").hide();
					if($.isNotBlank(inSubCategory) && $.isBlank($input.val()))
						base.populateIMSManufacturers(e);
				}
			});
		},

		base.populateMinor = function(e){
			var $tab = base.contentHolder.find("div#ims");
			var inCategory = $.trim($tab.find("input#categoryList").val());
			var inSubCategory = $.trim($tab.find("input#subCategoryList").val());
			var inClass = $.trim($tab.find("input#classList").val());
			var $select = $tab.find("select#minorList");
			var $input = $tab.find("input#minorList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSMinors(inCategory,inSubCategory, inClass, {
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#minor").show();
					}else{
						$table.find("tr#minor").hide();
					}  
				},
				preHook:function(){
					$tab.find("img#preloaderMinorList").show();
					base.clearIMSComboBox("minor");
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["SubClass"])){
						$select.prop("selectedText",$item.condition.IMSFilters["SubClass"]);
						$input.val($item.condition.IMSFilters["SubClass"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderMinorList").hide();
					base.populateIMSManufacturers(e);
				}
			});
		},

		base.populateIMSManufacturers= function(e){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#manufacturerList");
			var $input = $tab.find("input#manufacturerList");
			var $table = $tab.find("table.imsFields");
			var $catcode = $table.find("input#catcode");
			var $item = base.options.item;

			var inCatCode = "";
			var inCategory = "";
			var inSubCategory = "";
			var inClass = "";
			var inMinor = "";

			if ($.isNotBlank($catcode.val())){
				inCatCode = $.trim($catcode.val());
			}else{
				inCategory = $.trim($tab.find("input#categoryList").val());
				inSubCategory = $.trim($tab.find("input#subCategoryList").val());
				inClass = $.trim($tab.find("input#classList").val());
				inMinor = $.trim($tab.find("input#minorList").val());
			}

			CategoryServiceJS.getIMSManufacturers(inCatCode, inCategory, inSubCategory, inClass, inMinor, {
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderManufacturerList").show();
					base.clearIMSComboBox("manufacturer");
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["Manufacturer"])){
						$select.prop("selectedText",$item.condition.IMSFilters["Manufacturer"]);
						$input.val($item.condition.IMSFilters["Manufacturer"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderManufacturerList").hide();
				}
			});
		},  

		base.clearIMSComboBox = function(trigger){
			var $tab = base.contentHolder.find("div#ims");

			if ($.isBlank(trigger)){
				$tab.find("input").val("");
				$tab.find("select.selectCombo option").remove();
			}else{
				switch (trigger.toLowerCase()){
				case "category": 
					$tab.find("input#categoryList").val("");
					$tab.find("select#categoryList option").remove();
				case "subcategory": 
					$tab.find("input#subCategoryList").val("");
					$tab.find("select#subCategoryList option").remove();
				case "class": 
					$tab.find("input#classList").val("");
					$tab.find("select#classList option").remove();
				case "minor": 
					$tab.find("input#minorList").val("");
					$tab.find("select#minorList option").remove();
				case "manufacturer": 
					$tab.find("input#manufacturerList").val("");
					$tab.find("select#manufacturerList option").remove();	
				}
			}
		},

		base.updateIMSCombobox = function(target, e, u){
			var $tab = base.contentHolder.find("div#ims");

			switch($(target).attr("id").toLowerCase()){
			case "categorylist" :
				if(u.item){
					$tab.find("input#categoryList").val(u.item.text);
					$tab.find("input#categoryList").prop("selectedText", u.item.text);
					base.populateSubcategories(e);
				}
				else base.populateCategories(e);

				$tab.find("input#subCategoryList").val("");
				$tab.find("input#classList").val("");
				$tab.find("input#minorList").val("");
				$tab.find("input#manufacturerList").val("");
				break;
			case "subcategorylist" :
				if(u.item){
					$tab.find("input#subCategoryList").val(u.item.text);
					$tab.find("input#subCategoryList").prop("selectedText", u.item.text);
					base.populateClass(e);
				}
				else base.populateSubcategories(e);

				$tab.find("input#classList").val("");
				$tab.find("input#minorList").val("");
				$tab.find("input#manufacturerList").val("");
				break;
			case "classlist" : 
				if(u.item){
					$tab.find("input#classList").val(u.item.text);
					$tab.find("input#classList").prop("selectedText", u.item.text);
					base.populateMinor(e);
				}
				else base.populateClass(e);

				$tab.find("input#minorList").val("");
				$tab.find("input#manufacturerList").val("");
				break;
			case "minorlist" : 
				if(u.item){
					$tab.find("input#minorList").val(u.item.text);
					$tab.find("input#minorList").prop("selectedText", u.item.text); 
					base.populateIMSManufacturers(e);
				}
				else base.populateMinor(e);

				$tab.find("input#manufacturerList").val("");
				break;
			case "manufacturerlist" : 
				if(u.item){
					$tab.find("input#manufacturerList").val(u.item.text);
					$tab.find("input#manufacturerList").prop("selectedText", u.item.text); 
				}
				break;
			}
		},

		base.addIMSListener = function(){

			var $tab = base.contentHolder.find("div#ims");
			var $table = $tab.find("table.imsFields");
			var usingCategory = false;
			var $item = base.options.item;
			var $catcode = $table.find("input#catcode");

			$tab.find("select.selectCombo").combobox({
				change: function(e, u){
					base.updateIMSCombobox(this, e, u);
				},
				selected: function(e, u){
					base.updateIMSCombobox(this, e, u);
				}
			});

			$table.find("tr.catCode").hide();

			if ($.isNotBlank($item)){
				usingCategory = $item.condition["imsUsingCategory"] || ($.isBlank($item) && base.options.defaultIMSType === "CatName");
			}

			if (usingCategory){
				$table.find("tr.catCode").hide();
				$table.find("tr.catName").show();
				base.populateCategories();
			}
			else{
				if ($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["CatCode"])){
					$catcode.val($item.condition.IMSFilters["CatCode"]);
				}
				$table.find("tr.catName").hide();
				$table.find("tr.catCode").show();
				base.populateIMSManufacturers();
			}

			$tab.find("a.switchToCatCode,a.switchToCatName").off().on({
				click: function(e){
					var $table = $tab.find("table.imsFields");

					switch($(e.currentTarget).attr("class")){
					case "switchToCatName" : 
						$table.find("input#catcode").val("");
						$table.find("tr.catCode").hide();
						$table.find("tr.catName").show();
						base.populateCategories();
						break;
					case "switchToCatCode" : 
						base.clearIMSComboBox();
						if ($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["CatCode"])){
							$catcode.val($item.condition.IMSFilters["CatCode"]);
						}
						$table.find("tr.catCode").show();
						$table.find("tr.catName").hide();
						base.populateIMSManufacturers();
						break;
					}
				}
			});

			$catcode.off().on({
				mouseleave: function(e){
					if ($catcode.is(":visible"))
						base.populateIMSManufacturers(e);
				}
			});

			base.addDynamicAttributeListener();
			base.addFacetListener();
		},

		base.populateLevel1Categories= function(e){
			var $tab = base.contentHolder.find("div#cnet");
			var $select = $tab.find("select#level1CategoryList");
			var $input = $tab.find("input#level1CategoryList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETLevel1Categories({
				callback: function(data){
					var list = data;

					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if($.isNotBlank($input.val())) base.populateLevel2Categories(e);
				},
				preHook:function(){
					$tab.find("img#preloaderLevel1CategoryList").show();
					base.clearCNETComboBox("level1Cat");
					$table.find("tr#level2Cat, tr#level3Cat").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Level1Category"])){
						$select.prop("selectedText",$item.condition.CNetFilters["Level1Category"]);
						$input.val($item.condition.CNetFilters["Level1Category"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderLevel1CategoryList").hide();
					if($.isBlank($input.val()))
						base.populateCNETManufacturers(e);
				}
			});
		},

		base.populateLevel2Categories= function(e){
			var $tab = base.contentHolder.find("div#cnet");
			var inLevel1Category = $.trim($tab.find("input#level1CategoryList").val());
			var $select = $tab.find("select#level2CategoryList");
			var $input = $tab.find("input#level2CategoryList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETLevel2Categories(inLevel1Category, {
				callback: function(data){
					var list = data;

					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#level2Cat").show();
					}else{
						$table.find("tr#level2Cat").hide();
					}  

					if($.isNotBlank($input.val())) base.populateLevel3Categories(e);
				},
				preHook:function(){
					$tab.find("img#preloaderLevel2CategoryList").show();
					base.clearCNETComboBox("level2Cat");
					$table.find("tr#level3Cat").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Level2Category"])){
						$select.prop("selectedText",$item.condition.CNetFilters["Level2Category"]);
						$input.val($item.condition.CNetFilters["Level2Category"]);
					};
				},
				postHook:function(){
					$tab.find("img#preloaderLevel2CategoryList").hide();
					if($.isNotBlank(inLevel1Category) && $.isBlank($input.val()))
						base.populateCNETManufacturers(e);
				}
			});
		},

		base.populateLevel3Categories= function(e){
			var $tab = base.contentHolder.find("div#cnet");
			var inLevel1Category = $.trim($tab.find("input#level1CategoryList").val());
			var inLevel2Category = $.trim($tab.find("input#level2CategoryList").val());
			var $select = $tab.find("select#level3CategoryList");
			var $input = $tab.find("input#level3CategoryList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETLevel3Categories(inLevel1Category, inLevel2Category, {
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#level3Cat").show();
					}else{
						$table.find("tr#level3Cat").hide();
					}  

				},
				preHook:function(){
					$tab.find("img#preloaderLevel3CategoryList").show();
					base.clearCNETComboBox("level3Cat");
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Level3Category"])){
						$select.prop("selectedText",$item.condition.CNetFilters["Level3Category"]);
						$input.val($item.condition.CNetFilters["Level3Category"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderLevel3CategoryList").hide();
					base.populateCNETManufacturers(e);
				}
			});
		},

		base.populateCNETManufacturers= function(e){
			var $tab = base.contentHolder.find("div#cnet");
			var $select = $tab.find("select#cnetmanufacturerList");
			var $input = $tab.find("input#cnetmanufacturerList");
			var $item = base.options.item;

			var inLevel1Category = $.trim($tab.find("input#level1CategoryList").val());
			var inLevel2Category = $.trim($tab.find("input#level2CategoryList").val());
			var inLevel3Category = $.trim($tab.find("input#level3CategoryList").val());

			CategoryServiceJS.getCNETManufacturers(inLevel1Category, inLevel2Category, inLevel3Category, {
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderCNETManufacturerList").show();
					base.clearCNETComboBox("cnetmanufacturer");
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Manufacturer"])){
						$select.prop("selectedText",$item.condition.CNetFilters["Manufacturer"]);
						$input.val($item.condition.CNetFilters["Manufacturer"]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderCNETManufacturerList").hide();
				}
			});
		}, 

		base.updateCNETCombobox = function (target, e, u){
			var $tab = base.contentHolder.find("div#cnet");

			switch($(target).attr("id").toLowerCase()){
			case "level1categorylist" :
				if(u.item){
					$tab.find("input#level1CategoryList").val(u.item.text);
					$tab.find("input#level1CategoryList").prop("selectedText", u.item.text);
					base.populateLevel2Categories(e);
				}
				else base.populateLevel1Categories(e);

				$tab.find("input#level2CategoryList").val("");
				$tab.find("input#level3CategoryList").val("");
				$tab.find("input#cnetmanufacturerList").val("");
				break;
			case "level2categorylist" : 
				if(u.item){
					$tab.find("input#level2CategoryList").val(u.item.text);
					$tab.find("input#level2CategoryList").prop("selectedText", u.item.text);
					base.populateLevel3Categories(e);
					$tab.find("input#level3CategoryList").val("");
				}
				else base.populateLevel2Categories(e);

				$tab.find("input#cnetmanufacturerList").val("");
				break;
			case "level3categorylist" : 
				if(u.item){
					$tab.find("input#level3CategoryList").val(u.item.text);
					$tab.find("input#level3CategoryList").prop("selectedText", u.item.text);
					base.populateCNETManufacturers(e);
				}
				else base.populateLevel3Categories(e);

				$tab.find("input#cnetmanufacturerList").val("");
				break;
			case "cnetmanufacturerlist" : 
				if(u.item){
					$tab.find("input#cnetmanufacturerList").val(u.item.text);
					$tab.find("input#cnetmanufacturerList").prop("selectedText",u.item.text);
				} 
				break;
			}
		};

		base.clearCNETComboBox= function(trigger){
			var $tab = base.contentHolder.find("div#cnet");

			if ($.isBlank(trigger)){
				$tab.find("input").val("");
				$tab.find("select.selectCombo option").remove();
			}else{
				switch (trigger.toLowerCase()){
				case "level1cat": 
					$tab.find("input#level1CategoryList").val("");
					$tab.find("select#level1CategoryList option").remove();
				case "level2cat": 
					$tab.find("input#level2CategoryList").val("");
					$tab.find("select#level2CategoryList option").remove();
				case "level3cat": 
					$tab.find("input#level3CategoryList").val("");
					$tab.find("select#level3CategoryList option").remove();
				case "cnetmanufacturer": 
					$tab.find("input#cnetmanufacturerList").val("");
					$tab.find("select#cnetmanufacturerList option").remove();	
				}
			}
		},

		base.addCNETListener = function(){
			var $tab = base.contentHolder.find("div#cnet");

			$tab.find("select.selectCombo").combobox({
				change: function(e, u){
					base.updateCNETCombobox(this, e, u);
				},
				selected: function(e, u){
					base.updateCNETCombobox(this, e, u);
				}
			});

			base.populateLevel1Categories();
			base.addDynamicAttributeListener();
			base.addFacetListener();
		},

		base.populateDynamicAttributeValues = function(){
			var $attributeMap = base.templateAttributes;
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $item = base.options.item;

			if($item.condition["dynamicAttributes"]){
				var $divItemList = $tab.find('div#dynamicAttributeItemList');
				$divItemList.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)").remove();

				$.each($item.condition["dynamicAttributes"], function(attrName, attrData) { 
					if(attrName !== "TemplateName" || attrName !== GLOBAL_storeFacetTemplateName){
						var $divDynamicAttributeItem = $divItemList.find('div#dynamicAttributeItemPattern').clone();
						var $ulAttributeValues = $divDynamicAttributeItem.find("div#dynamicAttributeValues");

						$ulAttributeValues.prop({id: $.formatAsId(attrName), title:attrName});
						var currCondCount = parseInt($divItemList.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern):last").attr("id"));
						if (!$.isNumeric(currCondCount)){
							currCondCount = 0; 
						}

						var countId = 1 + parseInt(currCondCount);
						$divDynamicAttributeItem.prop("id", "dynamicAttributeItem");

						if($attributeMap && $attributeMap[attrName]){
							var attributeValues = $attributeMap[attrName].attributeValues;
							$divDynamicAttributeItem.find('span#dynamicAttributeLabel').html($attributeMap[attrName].attributeDisplayName + ":");

							if(attributeValues){
								for(var i=0; i<attributeValues.length; i++){
									var $liAttributeValue = $ulAttributeValues.find("div#dynamicAttributeValuesPattern").clone();
									$liAttributeValue.show();
									$liAttributeValue.prop("id", "dynamicAttributeValues" + countId);
									$liAttributeValue.find("input.checkboxFilter").prop({name:attrName, value:attributeValues[i], checked: ($.inArray(attributeValues[i], attrData) > -1)});
									$liAttributeValue.find("span#attributeValueName").text(attributeValues[i].split("|")[1]);
									$ulAttributeValues.append($liAttributeValue);
								}
							}

							$divDynamicAttributeItem.show();
							$divDynamicAttributeItem.prop("id", countId);
							$divDynamicAttributeItem.addClass("tempDynamicAttributeItem");
							$divItemList.append($divDynamicAttributeItem);

							base.addDeleteDynamicAttributeButtonListener($divDynamicAttributeItem, $attributeMap[attrName].attributeDisplayName);
						}
					}
				});
			}
		},

		base.populateCNETDynamicAttributes = function(e){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#dynamicAttributeList");
			var $templateName = $tab.find("input#templateNameList");
			var $item = base.options.item;

			var inTemplateName = $.trim($templateName.val());

			CategoryServiceJS.getCNETTemplateAttributes(inTemplateName, {
				callback: function(data){
					base.templateAttributes = data;
					var isEmpty = true;

					$.each(base.templateAttributes, function(attrName, attrData) { 
						$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
						isEmpty = false;
					});

					if (!isEmpty){
						$tab.find("table#addDynamicAttributeName").show();
					}else{
						$tab.find("table#addDynamicAttributeName").hide();
					}
				},
				preHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").show();
					base.clearDynamicAttributeComboBox("attributevaluelist");
				},
				postHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition["dynamicAttributes"])){
						base.populateDynamicAttributeValues();
					}
				}
			});
		},

		base.populateCNETTemplateNames= function(e){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#templateNameList");
			var $input = $tab.find("input#templateNameList");
			var $item = base.options.item;

			CategoryServiceJS.getCNETTemplateNames({
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderTemplateNameList").show();
					base.clearDynamicAttributeComboBox("templateNameList");
					$tab.find("table#addDynamicAttributeName").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.dynamicAttributes)){
						$select.prop("selectedText",$item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateName]);
						$input.val($item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateName]);
					}

				},
				postHook:function(){
					$tab.find("img#preloaderTemplateNameList").hide();
					if($.isNotBlank($.trim($input.val()))) 
						base.populateCNETDynamicAttributes(e);
				}
			});
		},

		base.clearDynamicAttributeComboBox= function(trigger){
			var $tab = base.contentHolder.find("div#dynamicAttribute");

			if ($.isBlank(trigger)){
				$tab.find("input").val("");
				$tab.find("select.selectCombo option").remove();
			}else{
				switch (trigger.toLowerCase()){
				case "templatenamelist": 
					$tab.find("input#templateNameList").val("");
					$tab.find("select#templateNameList option").remove();
				case "attributevaluelist":
					$tab.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)").remove();
				case "dynamicattributelist": 
					$tab.find("input#dynamicAttributeList").val("");
					$tab.find("select#dynamicAttributeList option").remove();
				}
			}
		},

		base.updateFacetTemplateCombobox = function (target, e, u){
			var $tab = base.contentHolder.find("div#dynamicAttribute");

			switch($(target).attr("id").toLowerCase()){
			case "templatenamelist" :
				if(u.item){
					$tab.find("input#templateNameList").val(u.item.text);
					$tab.find("input#templateNameList").prop("selectedText", u.item.text);

					if (base.contentHolder.find("div#ims").length)
						base.populateIMSDynamicAttributes(e);
					else if (base.contentHolder.find("div#cnet").length)
						base.populateCNETDynamicAttributes(e);
				}
				else{
					if (base.contentHolder.find("div#ims").length)
						base.populateIMSTemplateNames(e);
					else if (base.contentHolder.find("div#cnet"))
						base.populateCNETTemplateNames(e);
				}
				$tab.find("input#dynamicAttributeList").val("");
				break;

			case "dynamicattributelist" :
				if(u.item){
					$tab.find("input#dynamicAttributeList").val(u.item.text);
					$tab.find("input#dynamicAttributeList").prop("selectedText", u.item.text);
					base.addDynamicAttributeButtonListener(u.item.value);
				}
				else{
					jAlert("Please specify a valid attribute name.");
					base.addDynamicAttributeButtonListener("");
				}
				break;
			}
		},

		base.addDynamicAttributeButtonListener= function(attrName){
			var $tab = base.contentHolder.find("div#dynamicAttribute");

			$tab.find("a.addDynamicAttrBtn").off().on({
				click: function(e){
					if (!e.data.locked){
						var $divItemList = $tab.find('div#dynamicAttributeItemList');
						var $divDynamicAttributeItem = $divItemList.find('div#dynamicAttributeItemPattern').clone();
						var $input = $tab.find("input#dynamicAttributeList");
						var inDynamicAttribute = $.trim($input.val());
						var $ulAttributeValues = $divDynamicAttributeItem.find("div#dynamicAttributeValues");

						if($.isNotBlank(inDynamicAttribute)){
							if($divItemList.find("div#"+$.formatAsId(attrName)).length > 0){
								jAlert("Attribute already added. Please select a different attribute name.");
							}
							else{
								$ulAttributeValues.prop({id: $.formatAsId(attrName), title: attrName});
								var currCondCount = parseInt($divItemList.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern):last").attr("id"));
								if (!$.isNumeric(currCondCount)){
									currCondCount = 0; 
								}

								var countId = 1 + parseInt(currCondCount);
								$divDynamicAttributeItem.prop("id", "dynamicAttributeItem");

								var $dynamicAttributeLabel = $divDynamicAttributeItem.find('span#dynamicAttributeLabel');
								$dynamicAttributeLabel.html(inDynamicAttribute + ":");

								var attributeMap = base.templateAttributes;
								if(attributeMap && attributeMap[attrName]){
									var attributeValues = attributeMap[attrName].attributeValues;
									if(attributeValues){
										for(var i=0; i<attributeValues.length; i++){
											var $liAttributeValue = $ulAttributeValues.find("div#dynamicAttributeValuesPattern").clone();
											$liAttributeValue.show();
											$liAttributeValue.prop("id", "dynamicAttributeValues" + countId);
											$liAttributeValue.find("input.checkboxFilter").prop({name:attrName, value:attributeValues[i]});
											$liAttributeValue.find("span#attributeValueName").text(attributeValues[i].split("|")[1]);
											$ulAttributeValues.append($liAttributeValue);
										}
									}
								}

								$divDynamicAttributeItem.prop("id", countId);
								$divDynamicAttributeItem.addClass("tempDynamicAttributeItem");
								$divDynamicAttributeItem.show();
								$divItemList.append($divDynamicAttributeItem);
								base.addDeleteDynamicAttributeButtonListener($divDynamicAttributeItem, inDynamicAttribute);
							}
						}
						else{
							jAlert("Please select a dynamic attribute.");
						}
					}
				},
				mouseenter: showHoverInfo
			},{locked: base.options.locked});	
		},

		base.addDeleteDynamicAttributeButtonListener= function(attribItem, attribName){
			attribItem.find("img.deleteAttrIcon").off().on({
				click: function(e){
					if (e.data.locked) return;
					var $item = $(this).parents(".dynamicAttributeItem");
					jConfirm("Delete " + e.data.attrib + "?", "Delete Attribute", function(result){
						if(result){
							$item.remove();
						}
					});
				},
				mouseenter: showHoverInfo
			},{locked: base.options.locked, attrib: attribName});
		},

		base.populateIMSDynamicAttributes= function(e){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#dynamicAttributeList");
			var $templateName = $tab.find("input#templateNameList");
			var $item = base.options.item;

			var inTemplateName = $.trim($templateName.val());

			CategoryServiceJS.getIMSTemplateAttributes(inTemplateName, {
				callback: function(data){
					base.templateAttributes = data;
					var isEmpty = true;

					$.each(base.templateAttributes, function(attrName, attrData) { 
						$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
						isEmpty = false;
					});

					if (!isEmpty){
						$tab.find("table#addDynamicAttributeName").show();
					}else{
						$tab.find("table#addDynamicAttributeName").hide();
					}
				},
				preHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").show();
					base.clearDynamicAttributeComboBox("attributevaluelist");
				},
				postHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition.dynamicAttributes)){
						base.populateDynamicAttributeValues();
					}
				}
			});
		},

		base.populateIMSTemplateNames= function(e){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#templateNameList");
			var $input = $tab.find("input#templateNameList");
			var $item = base.options.item;

			CategoryServiceJS.getIMSTemplateNames({
				callback: function(data){
					var list = data;
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderTemplateNameList").show();
					base.clearDynamicAttributeComboBox("templateNameList");
					$tab.find("table#addDynamicAttributeName").hide();
					if (!e && $.isNotBlank($item) && $.isNotBlank($item.condition["dynamicAttributes"])){
						$select.prop("selectedText",$item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateName]);
						$input.val($item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateName]);
					}
				},
				postHook:function(){
					$tab.find("img#preloaderTemplateNameList").hide();
					if($.isNotBlank($.trim($input.val())))
						base.populateIMSDynamicAttributes(e);
				}
			});
		},

		base.addDynamicAttributeListener = function(){
			var $tab = base.contentHolder.find("div#dynamicAttribute");

			$tab.find("select.selectCombo").combobox({
				change: function(e, u){
					base.updateFacetTemplateCombobox(this, e, u);
				},
				selected: function(e, u){
					base.updateFacetTemplateCombobox(this, e, u);
				}
			});

			if (base.contentHolder.find("div#ims").length){
				base.populateIMSTemplateNames();
			}else if(base.contentHolder.find("div#cnet").length){
				base.populateCNETTemplateNames();
			}
		},

		base.addFacetListener = function(){

			var $facet = base.contentHolder.find("div#facet");

			$facet.find("select.selectCombo").combobox({

			});

			if ($.isBlank(base.options.item)) return;

			var $condition = base.options.item.condition;

			$facet.find("input#platformList").val($condition.facets["Platform"]);
			$facet.find("select#platformList").prop("selectedText", $condition.facets["Platform"]);

			$facet.find("input#conditionList").val($condition.facets["Condition"]);
			$facet.find("select#conditionList").prop("selectedText", $condition.facets["Condition"]);

			$facet.find("input#availabilityList").val($condition.facets["Availability"]);
			$facet.find("select#availabilityList").prop("selectedText", $condition.facets["Availability"]);

			$facet.find("input#licenseList").val($condition.facets["License"]);
			$facet.find("select#licenseList").prop("selectedText", $condition.facets["License"]);

			$facet.find("input#nameContains").val($condition.facets["Name"]);
			$facet.find("input#descriptionContains").val($condition.facets["Description"]);

		};

		base.promptAddFacetItem = function(type){
			base.contentHolder.html(base.getAddFacetItemTemplate());

			if ($.isBlank(base.options.item)){
				base.contentHolder.find("#conditionText").hide();
			}else{
				base.contentHolder.find("#conditionText").html(base.options.item.condition["readableString"]);

				var formattedExpiryDate = base.options.item["formattedExpiryDate"];
				if($.isNotBlank(formattedExpiryDate)){
					base.contentHolder.find("#addItemDate").val(formattedExpiryDate);
				};

				if (base.options.showPosition)
					base.contentHolder.find("#addItemPosition").val(base.options.item["location"]);
			}
			base.contentHolder.find("#addItemPosition").on({
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

			switch(type){
			case "facet": 
				//contentHolder.find('a[href="#facet"]').parents('div#tabHeight').remove();
				base.contentHolder.find('a[href="#ims"],a[href="#cnet"],a[href="#dynamicAttribute"]').parent('li').remove();
				base.contentHolder.find("div#ims,div#cnet,div#dynamicAttribute").remove();
				base.addFacetListener();
				break;
			case "cnet": 
				base.contentHolder.find('a[href="#ims"]').parent('li').remove();
				base.contentHolder.find("div#ims").remove(); 
				base.addCNETListener();
				break;
			case "ims": 
				base.contentHolder.find('a[href="#cnet"]').parent('li').remove();
				base.contentHolder.find("div#cnet").remove();

				if(GLOBAL_store==="pcmall" || GLOBAL_store==="pcmallcap" || GLOBAL_store==="sbn"){
					base.contentHolder.find('a[href="#dynamicAttribute"]').parent('li').remove();
					base.contentHolder.find("div#dynamicAttribute").remove();
				}

				base.addIMSListener();
				break;
			}

			base.contentHolder.find("#facetItem").tabs("destroy").tabs({
				show: function(){
				}
			});

			base.contentHolder.find("#addItemDate").attr('id', 'addItemDate_1');

			base.contentHolder.find("#addItemDate_1").datepicker({
				showOn: "both",
				minDate: base.options.dateMinDate,
				maxDate: base.options.dateMaxDate,
				buttonText: "Expiration Date",
				buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
				buttonImageOnly: true
			});

			base.contentHolder.find("#addFacetItemToRuleBtn").off().on({
				click: function(e){
					setTimeout(function() {

						var position = 1;
						var valid = true;
						
						if (base.options.showPosition){
							position = base.contentHolder.find("#addItemPosition").val();
						}
						
						var expiryDate = $.trim(base.contentHolder.find("#addItemDate_1").val());
						var comment= $.defaultIfBlank($.trim(base.contentHolder.find("#addItemComment").val()), "").replace(/\n\r?/g, '<br/>');

						if ($.isNotBlank(expiryDate) && !validateGeneric("Validity Date", expiryDate)){
							valid = false;
						}

						if ($.isNotBlank(comment) && !validateGeneric("Comment", comment)){
							valid = false;
						}
						
						var condMap = base.getSelectedFacetFieldValues();
						
						if ($.isEmptyObject(condMap)){
							valid = false;
							jAlert('Please specify at least one filter condition');
						}
						
						if (!$.isBlank(condMap["CatCode"]) && !validateAlphanumeric("Catergory Code", condMap["CatCode"])){
							valid = false;
						}
						
						if (valid){
							$.each(condMap, function(idx, el){
								$.each(el, function(i,elem){
									if(!validateGeneric("Input", elem)) {
										valid = false;
									}
								});
							});
						}

						if (valid){
							if (base.options.newRecord){
								base.api.destroy();
								base.options.addFacetItemCallback(position, expiryDate, comment, condMap, base.getTypeLabel(type));
							}else{
								base.api.destroy();
								base.options.updateFacetItemCallback(base.options.item["memberId"], position, expiryDate, comment, condMap);
							}
						}
					}, 500 );
				}
			});

		};

		base.promptAddProductItem = function(){
			base.contentHolder.html(base.getAddProductItemTemplate());

			base.contentHolder.find("#addItemDate").attr('id', 'addItemDate_1');

			base.contentHolder.find("#addItemDate_1").datepicker({
				showOn: "both",
				minDate: base.options.dateMinDate,
				maxDate: base.options.dateMaxDate,
				buttonText: "Expiration Date",
				buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
				buttonImageOnly: true
			});


			base.contentHolder.find("#addItemPosition").on({
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

			base.contentHolder.find("#addItemToRuleBtn").on({
				click: function(evt){

					var commaDelimitedNumberPattern = /^\s*\d+\s*(,?\s*\d+\s*)*$/;

					var skus = $.trim(base.contentHolder.find("#addItemDPNo").val());
					var sequence = $.trim(base.contentHolder.find("#addItemPosition").val());
					var expDate = $.trim(base.contentHolder.find("#addItemDate_1").val());
					var comment= $.defaultIfBlank($.trim(base.contentHolder.find("#addItemComment").val()), "").replace(/\n\r?/g, '<br/>');
					var today = new Date();
					var valid = false;

					today.setHours(0,0,0,0); //ignore time of current date 

					base.contentHolder.find("#addItemDate_1").datepicker('disable');

					if ($.isBlank(skus)) {
						jAlert("There are no SKUs specified in the list.", "Invalid Input");
					}
					else if (!commaDelimitedNumberPattern.test(skus)) {
						jAlert("List contains an invalid SKU.", "Invalid Input");
					}							
					else if (!$.isBlank(expDate) && !$.isDate(expDate)){
						jAlert("Invalid date specified.", "Invalid Input");
					}
					else if(today.getTime() > new Date(expDate).getTime())
						jAlert("Start date cannot be earlier than today", "Invalid Input");
					else if (!isXSSSafe(comment)){
						jAlert("Invalid comment. HTML/XSS is not allowed.", "Invalid Input");
					}
					else {
						valid = true;
						base.api.destroy();
						base.options.addProductItemCallback(sequence, expDate, comment, skus.split(/[\s,]+/));						
					}

					if(!valid)
						base.contentHolder.find("#addItemDate_1").datepicker('enable');

				}
			});
		};

		base.getTypeLabel = function(type){
			switch(type){
			case "product":
				return 'Product Item';
			case "ims":
				return 'IMS Categories';
			case "cnet":
				return 'Product Site Taxonomy';
			case "facet":
				return 'Facets';
			default:
				return '';
			}
		};

		base.promptRuleItemDetails = function(target, type){

			var typeLabel = base.getTypeLabel(type);


			$(target).qtip("destroy").qtip({
				content: {
					text: $('<div/>'),
					title: {text: typeLabel, button: true }
				},
				position: {
					my: 'center',
					at: 'center',
					target: $(window)
				},
				show:{
					ready: true,
					modal:true
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);

						switch(type){
						case "product": base.promptAddProductItem(); break; 
						case "ims": base.promptAddFacetItem(type); break;
						case "cnet": base.promptAddFacetItem(type); break;
						case "facet": base.promptAddFacetItem(type); break;
						};				

						base.contentHolder.find("#clearBtn").on({
							click: function(evt){
								base.contentHolder.find("input,textarea").val("");
							}
						});

					},
					hide: function(event, api){
						api.destroy();
					}
				}
			});
		};

		// Run initializer
		base.init();
	};

	$.addproduct.defaultOptions = {
			type: "product",
			locked: true,
			newRecord: true,
			item: null,
			dateMinDate: 0,
			dateMaxDate: "+1Y",
			defaultIMSType: "CatCode",
			showPosition:false,
			addProductItemCallback: function(position, expiryDate, comment, skus){},
			addFacetItemCallback: function(position, expiryDate, comment, selectedFacetFieldValues, ruleType){},
			updateFacetItemCallback: function(memberId, position, expiryDate, comment, selectedFacetFieldValues){}
	};

	$.fn.addproduct = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.addproduct(this, options));
			});
		};
	};

})(jQuery);
