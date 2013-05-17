(function($){

	$.addproduct = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;
		var destroy = true;

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
			template  += '	<input id="hideCursor" type="txt" style="position:absolute; top:-30px; padding:1px; margin-left:-1px; opacity:0.00; border:none; height:0px; width:0px"/>';
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
			template  += '				<table class="dynamicAttribFields w460">';
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

			template  += '								<div class="w240 floatL marT8 border" style="overflow-y:auto; height: 107px">';												
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
			template  += '						<select name="select" id="platformList" class="selectCombo" style="width: 100%;" title="Select Platform" >';
			template  += '							<option value="">-Select Platform-</option>';
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
			template  += '						<select name="select" id="conditionList" class="selectCombo" style="width: 100%;" title="Select Condition" >';
			template  += '							<option value="">-Select Condition-</option>';
			template  += '							<option value="refurbished">Refurbished</option>';
			template  += '							<option value="open">Open Box</option>';
			template  += '							<option value="clearance">Clearance</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Availability :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="availabilityList" class="selectCombo" style="width: 100%;" title="Select Availability" >';
			template  += '							<option value="">-Select Availability-</option>';
			template  += '							<option value="instock">In Stock</option>';
			template  += '							<option value="call">Call</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">License :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="licenseList" class="selectCombo" style="width: 100%;" title="Select License" >';
			template  += '							<option value="all">-Select License-</option>';
			template  += '							<option value="license">License Products Only</option>';
			template  += '							<option value="nonlicense">Non-License Products Only</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Product Image :</td>';
			template  += '					<td class="iepadBT0 padT1">';
			template  += '						<select name="select" id="imageExistsList" class="selectCombo" style="width: 100%;" title="Select Product Image" >';
			template  += '							<option value="all">-Select Image Option-</option>';
			template  += '							<option value="withImage">Products With Image Only</option>';
			template  += '							<option value="noImage">Products Without Image Only</option>';
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
			template  += '			            <input id="hideCursor" type="txt" style="position:absolute; top:-30px; padding:1px; margin-left:-1px; opacity:0.00; border:none; height:0px; width:0px"/>';
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
				category[0] = $.trim($imsTab.find("select#categoryList > option:gt(0):selected").text());
				subCategory[0] = $.trim($imsTab.find("select#subCategoryList > option:gt(0):selected").text());
				clazz[0] = $.trim($imsTab.find("select#classList > option:gt(0):selected").text());
				minor[0] = $.trim($imsTab.find("select#minorList > option:gt(0):selected").text());
				manufacturer[0] = $.trim($imsTab.find("select#manufacturerList > option:gt(0):selected").text());

				if ($.isNotBlank(catCode[0])) condMap["CatCode"] = catCode;
				if ($.isNotBlank(category[0])) condMap["Category"] = category; 	
				if ($.isNotBlank(subCategory[0])) condMap["SubCategory"] = subCategory; 	
				if ($.isNotBlank(clazz[0])) condMap["Class"] = clazz; 	
				if ($.isNotBlank(minor[0])) condMap["SubClass"] = minor; 	
				if ($.isNotBlank(manufacturer[0])) condMap["Manufacturer"] = manufacturer; 	
			}

			var $cnetTab = base.contentHolder.find("div#cnet"); 

			if ($cnetTab.length){
				level1Cat[0] = $.trim($cnetTab.find("select#level1CategoryList > option:gt(0):selected").text());
				level2Cat[0] = $.trim($cnetTab.find("select#level2CategoryList > option:gt(0):selected").text());
				level3Cat[0] = $.trim($cnetTab.find("select#level3CategoryList > option:gt(0):selected").text());
				cnetManufacturer[0] = $.trim($cnetTab.find("select#cnetmanufacturerList > option:gt(0):selected").val());

				if ($.isNotBlank(level1Cat[0])) condMap["Level1Category"] = level1Cat; 	
				if ($.isNotBlank(level2Cat[0])) condMap["Level2Category"] = level2Cat; 	
				if ($.isNotBlank(level3Cat[0])) condMap["Level3Category"] = level3Cat; 	

				if ($.isNotBlank(cnetManufacturer[0])) condMap["Manufacturer"] = cnetManufacturer; 	
			}

			var $dynamicAttribute = base.contentHolder.find("div#dynamicAttribute"); 

			if($dynamicAttribute.length){
				var inTemplateName = $dynamicAttribute.find("select#templateNameList > option:gt(0):selected:eq(0)").text();
				var $divDynamicAttrItems = $dynamicAttribute.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)");
				
				if($.isNotBlank($.trim(inTemplateName))){
					condMap[GLOBAL_storeFacetTemplateNameField] = $.makeArray($.trim(inTemplateName));

					$divDynamicAttrItems.find("div").each(function(ulInd, uEl){
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
				var platform = $facetTab.find("select#platformList  > option:gt(0):selected:eq(0)").text();
				var condition = $facetTab.find("select#conditionList  > option:gt(0):selected:eq(0)").text();
				var availability = $facetTab.find("select#availabilityList  > option:gt(0):selected:eq(0)").text();
				var license = $facetTab.find("select#licenseList  > option:gt(0):selected:eq(0)").text();
				var nameContains = $.trim($facetTab.find("input#nameContains").val());
				var descriptionContains = $.trim($facetTab.find("input#descriptionContains").val());
				var imageExists = $facetTab.find("select#imageExistsList  > option:gt(0):selected:eq(0)").text();

				if($.isNotBlank(platform)){
					switch(platform.toLowerCase()){
					case "universal": condMap["Platform"] = ["Universal"]; break;
					case "pc": condMap["Platform"] = ["PC"]; break;
					case "linux": condMap["Platform"] = ["Linux"]; break;
					case "macintosh": condMap["Platform"] = ["Macintosh"]; break;
					}
				}

				if($.isNotBlank(condition)){
					switch(condition.toLowerCase()){
					case "refurbished": condMap["Condition"] = ["Refurbished"]; break;
					case "open box": condMap["Condition"] = ["Open Box"]; break;
					case "clearance": condMap["Condition"] = ["Clearance"]; break;
					}
				}

				if($.isNotBlank(availability)){
					switch(availability.toLowerCase()){
					case "in stock": condMap["Availability"] = ["In Stock"]; break;
					case "call": condMap["Availability"] = ["Call"]; break;
					}
				}

				if($.isNotBlank(license)){
					switch(license.toLowerCase()){
					case "license products only": condMap["License"] = ["License Products Only"]; break;
					case "non-license products only": condMap["License"] = ["Non-License Products Only"]; break;
					}
				}

				if($.isNotBlank(imageExists)){
					switch(imageExists.toLowerCase()){
					case "products with image only": condMap["ImageExists"] = ["Products With Image Only"]; break;
					case "products without image only": condMap["ImageExists"] = ["Products Without Image Only"]; break;
					}
				}

				if($.isNotBlank(nameContains))
					condMap["Name"] = $.makeArray(nameContains);

				if($.isNotBlank(descriptionContains))
					condMap["Description"] = $.makeArray(descriptionContains);
			}

			return condMap;
		},

		base.makeSelectSearchable = function(select){
			select.searchable({
				change: function(u, e){
					var $imsTab = base.contentHolder.find("div#ims");
					var selectedCategory = $.trim($imsTab.find("select#categoryList > option:selected:eq(0)").val());
					var selectedSubcategory = $.trim($imsTab.find("select#subCategoryList > option:selected:eq(0)").val());
					var selectedClass = $.trim($imsTab.find("select#classList > option:selected:eq(0)").val());

					var $cnetTab = base.contentHolder.find("div#cnet");
					var selectedLevel1Category = $.trim($cnetTab.find("select#level1CategoryList > option:selected:eq(0)").val());
					var selectedLevel2Category = $.trim($cnetTab.find("select#level2CategoryList > option:selected:eq(0)").val());

					if($.isBlank(u.value)){
						switch($(e.currentTarget).prop("id").toLowerCase()){
						case "categorylist": 
							$imsTab.find("tr#subcategory").hide();
						case "subcategorylist":
							$imsTab.find("tr#class").hide();
						case "classlist": 
							$imsTab.find("tr#minor").hide();
						case "minorlist":
							break;
						case "level1categorylist": 
							$cnetTab.find("tr#level2Cat").hide();
						case "level2categorylist": 
							$cnetTab.find("tr#level3Cat").hide();
						case "level3categorylist": 
							break;
						
						}
					}

					switch($(e.currentTarget).prop("id").toLowerCase()){
					case "categorylist": 
						if($.isNotBlank(selectedCategory)){
							base.populateSubcategories(selectedCategory);
						}else{
							base.populateIMSManufacturers();
						}
						break;
					case "subcategorylist": 
						if($.isNotBlank(selectedCategory) && $.isNotBlank(selectedSubcategory)){
							base.populateClass(selectedCategory, selectedSubcategory);
						}else{
							base.populateIMSManufacturers();
						}
						break;
					case "classlist": 
						if($.isNotBlank(selectedCategory) && $.isNotBlank(selectedSubcategory)  && $.isNotBlank(selectedClass)){
							base.populateMinor(selectedCategory, selectedSubcategory, selectedClass);
						}else{
							base.populateIMSManufacturers();
						}
						break;
					case "minorlist":
						base.populateIMSManufacturers();
						break;
					case "level1categorylist": 
						if($.isNotBlank(selectedLevel1Category)){
							base.populateLevel2Categories(selectedLevel1Category);
						}else{
							base.populateCNETManufacturers();
						}
						break;
					case "level2categorylist": 
						if($.isNotBlank(selectedLevel1Category) && $.isNotBlank(selectedLevel2Category)){
							base.populateLevel3Categories(selectedLevel1Category, selectedLevel2Category);
						}else{
							base.populateCNETManufacturers();
						}
						break;
					case "level3categorylist": 
						base.populateCNETManufacturers();
						break;
					case "templatenamelist": 
						if (base.contentHolder.find("div#ims").length)
							base.populateIMSDynamicAttributes(u.value);
						else if (base.contentHolder.find("div#cnet").length)
							base.populateCNETDynamicAttributes(u.value);
						break;
					case "dynamicattributelist": 
						base.addDynamicAttributeButtonListener(u.value);
						break;
					}
				}
			});
		},

		base.populateCategories = function(){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#categoryList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSCategories({
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Category-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderCategoryList").show();
					base.resetIMSFields("category");
					$table.find("tr#subcategory,tr#class,tr#minor").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderCategoryList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["Category"])){
						$select.val($item.condition.IMSFilters["Category"]);
						$select.change();
						$item.condition.IMSFilters["Category"] = new Array();
					}else{
						base.populateIMSManufacturers();
					}
				}
			});
		},

		base.populateSubcategories= function(selectedCategory){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#subCategoryList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSSubcategories(selectedCategory, {
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select SubCategory-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#subcategory").show();
					}
				},
				preHook:function(){
					$table.find("tr#subcategory").hide();
					$tab.find("img#preloaderSubCategoryList").show();
					base.resetIMSFields("subcategory");
					$table.find("tr#class,tr#minor").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderSubCategoryList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["SubCategory"])){
						$select.val(base.options.item.condition.IMSFilters["SubCategory"]);
						$select.change();
						$item.condition.IMSFilters["SubCategory"] = new Array();
					}else{
						base.populateIMSManufacturers();
					}
				}
			});
		},

		base.populateClass = function(selectedCategory, selectedSubCategory){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#classList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSClasses(selectedCategory, selectedSubCategory, {
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Class-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#class").show();
					}
				},
				preHook:function(){
					$table.find("tr#class").hide();
					$tab.find("img#preloaderClassList").show();
					base.resetIMSFields("class");
					$table.find("tr#minor").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderClassList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["Class"])) {
						$select.val(base.options.item.condition.IMSFilters["Class"]);
						$select.change();
						$item.condition.IMSFilters["Class"] = new Array();
					}else{
						base.populateIMSManufacturers();
					}
				}
			});
		},

		base.populateMinor = function(selectedCategory, selectedSubCategory, selectedClass){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#minorList");
			var $table = $tab.find("table.imsFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSMinors(selectedCategory, selectedSubCategory, selectedClass, {
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select SubClass-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#minor").show();
					}
				},
				preHook:function(){
					$table.find("tr#minor").hide();
					$tab.find("img#preloaderMinorList").show();
					base.resetIMSFields("minor");
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderMinorList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["SubClass"])){
						$select.val(base.options.item.condition.IMSFilters["SubClass"]);
						$select.change();
						$item.condition.IMSFilters["SubClass"] = new Array();
					}else{
						base.populateIMSManufacturers();
					}
				}
			});
		},

		base.populateIMSManufacturers= function(){
			var $tab = base.contentHolder.find("div#ims");
			var $select = $tab.find("select#manufacturerList");
			var $table = $tab.find("table.imsFields");
			var $catcode = $table.find("input#catcode");
			var $item = base.options.item;

			var inCatCode = "";
			var inCategory = "";
			var inSubCategory = "";
			var inClass = "";
			var inMinor = "";

			if ($.isNotBlank($catcode.val())){
				inCatCode = $.trim($catcode.val().toUpperCase());
			}else{
				inCategory = $.trim($tab.find("select#categoryList >option:selected:eq(0)").val());
				inSubCategory = $.trim($tab.find("select#subCategoryList >option:selected:eq(0)").val());
				inClass = $.trim($tab.find("select#classList >option:selected:eq(0)").val());
				inMinor = $.trim($tab.find("select#minorList >option:selected:eq(0)").val());
			}

			CategoryServiceJS.getIMSManufacturers(inCatCode, inCategory, inSubCategory, inClass, inMinor, {
				callback: function(data){
					var list = data;
					$select.empty().append($("<option>", {value: ""}).text("-Select Manufacturer-"));
					for(var i=0; i<list.length; i++){
						if($.isNotBlank(list[i]))
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderManufacturerList").show();
					base.resetIMSFields("manufacturer");
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderManufacturerList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.IMSFilters["Manufacturer"])){
						$select.val(base.options.item.condition.IMSFilters["Manufacturer"]);
						$select.change();
						base.options.item.condition.IMSFilters["Manufacturer"] = new Array();
					}
				}
			});
		},  

		base.resetIMSFields = function(trigger){
			var $tab = base.contentHolder.find("div#ims");

			if ($.isBlank(trigger)){
				$tab.find("select.selectCombo option").remove();
			}else{
				switch (trigger.toLowerCase()){
				case "category": 
					$tab.find("select#categoryList option").remove();
				case "subcategory": 
					$tab.find("select#subCategoryList option").remove();
				case "class": 
					$tab.find("select#classList option").remove();
				case "minor": 
					$tab.find("select#minorList option").remove();
				case "manufacturer": 
					$tab.find("select#manufacturerList option").remove();	
				}
			}
		},

		base.addIMSListener = function(){

			var $tab = base.contentHolder.find("div#ims");
			var $table = $tab.find("table.imsFields");
			var usingCategory = false;
			var $item = base.options.item;
			var $catcode = $table.find("input#catcode");

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
						base.resetIMSFields();
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
				mouseenter: function(e){
					e.data.input = $.trim($(e.currentTarget).val());
				},
				mouseleave: function(e){
					if($(e.currentTarget).is(":visible") && e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()){
						base.populateIMSManufacturers(e);
					}
				},
				focusin: function(e){
					e.data.input = $.trim($(e.currentTarget).val());
				},
				focusout: function(e){
					if($(e.currentTarget).is(":visible") && e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()){
						base.populateIMSManufacturers(e);
					}
				}
			},{input: ""});

			base.addDynamicAttributeListener();
			base.addFacetListener();
		},

		base.populateLevel1Categories= function(){
			var $tab = base.contentHolder.find("div#cnet");
			var $select = $tab.find("select#level1CategoryList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETLevel1Categories({
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Level 1-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderLevel1CategoryList").show();
					base.resetCNETFields("level1Cat");
					$table.find("tr#level2Cat, tr#level3Cat").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderLevel1CategoryList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Level1Category"])){
						$select.val($item.condition.CNetFilters["Level1Category"]);
						$select.change();
						$item.condition.CNetFilters["Level1Category"] = new Array();
					}else{
						base.populateCNETManufacturers();
					}
				}
			});
		},

		base.populateLevel2Categories= function(selectedLevel1Category){
			var $tab = base.contentHolder.find("div#cnet");
			var $select = $tab.find("select#level2CategoryList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETLevel2Categories(selectedLevel1Category, {
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Level 2-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}

					if ($.isNotBlank(list) && list.length>0){
						$table.find("tr#level2Cat").show();
					}else{
						$table.find("tr#level2Cat").hide();
					}  
				},
				preHook:function(){
					$tab.find("img#preloaderLevel2CategoryList").show();
					base.resetCNETFields("level2Cat");
					$table.find("tr#level3Cat").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderLevel2CategoryList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Level2Category"])){
						$select.val($item.condition.CNetFilters["Level2Category"]);
						$select.change();
						$item.condition.CNetFilters["Level2Category"] = new Array();
					}else{
						base.populateCNETManufacturers();
					}
				}
			});
		},

		base.populateLevel3Categories= function(selectedLevel1Category,selectedLevel2Category){
			var $tab = base.contentHolder.find("div#cnet");
			var $select = $tab.find("select#level3CategoryList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETLevel3Categories(selectedLevel1Category, selectedLevel2Category, {
				callback: function(data){
					var list = data;

					$select.append($("<option>", {value: ""}).text("-Select Level 3-"));
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
					base.resetCNETFields("level3Cat");
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderLevel3CategoryList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Level3Category"])){
						$select.val($item.condition.CNetFilters["Level3Category"]);
						$select.change();
						$item.condition.CNetFilters["Level3Category"] = new Array();
					}else{
						base.populateCNETManufacturers();
					}
				}
			});
		},

		base.populateCNETManufacturers= function(){
			var $tab = base.contentHolder.find("div#cnet");
			var $select = $tab.find("select#cnetmanufacturerList");
			var $table = $tab.find("table.cnetFields");
			var $item = base.options.item;

			var inLevel1Category = $.trim($tab.find("select#level1CategoryList >option:selected:eq(0)").val());
			var inLevel2Category = $.trim($tab.find("select#level2CategoryList >option:selected:eq(0)").val());
			var inLevel3Category = $.trim($tab.find("select#level3CategoryList >option:selected:eq(0)").val());

			CategoryServiceJS.getCNETManufacturers(inLevel1Category, inLevel2Category, inLevel3Category, {
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Manufacturer-"));
					for(var i=0; i<list.length; i++){
						if($.isNotBlank(list[i])){
							$select.append($("<option>", {value: list[i]}).text(list[i]));
						}
					}
				},
				preHook:function(){
					$tab.find("img#preloaderCNETManufacturerList").show();
					base.resetCNETFields("cnetmanufacturer");
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderCNETManufacturerList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if($.isNotBlank($item) && $.isNotBlank($item.condition.CNetFilters["Manufacturer"])){
						$select.val($item.condition.CNetFilters["Manufacturer"]);
						$select.change();
						base.options.item.condition.CNetFilters["Manufacturer"] = new Array();
					}
				}
			});
		}, 

		base.resetCNETFields= function(trigger){
			var $tab = base.contentHolder.find("div#cnet");

			if ($.isBlank(trigger)){
				$tab.find("select.selectCombo option").remove();
			}else{
				switch (trigger.toLowerCase()){
				case "level1cat": 
					$tab.find("select#level1CategoryList option").remove();
				case "level2cat": 
					$tab.find("select#level2CategoryList option").remove();
				case "level3cat": 
					$tab.find("select#level3CategoryList option").remove();
				case "cnetmanufacturer": 
					$tab.find("select#cnetmanufacturerList option").remove();	
				}
			}
		},

		base.addCNETListener = function(){
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
					if(attrName !== "TemplateName" || attrName !== "FacetTemplateName"){
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

		base.populateCNETDynamicAttributes = function(selectedTemplateName){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#dynamicAttributeList");
			var $table = $tab.find("table.dynamicAttribFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETTemplateAttributes(selectedTemplateName, {
				callback: function(data){
					base.templateAttributes = data;
					var isEmpty = true;

					$.each(base.templateAttributes, function(attrName, attrData) { 
						$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
						isEmpty = false;
					});

					if (!isEmpty){
						$select.prepend($("<option>", {value: ""}).text("-Select Attribute-"));
						$tab.find("table#addDynamicAttributeName").show();
					}else{
						$tab.find("table#addDynamicAttributeName").hide();
						
						if($.isNotBlank(selectedTemplateName)){
							jAlert("Selected template name does not have any dynamic attributes.", self.moduleName);
						}
					}
				},
				preHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").show();
					base.resetDynamicAttributeFields("attributevaluelist");
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if ($.isNotBlank($item) && !$.isEmptyObject($item.condition["dynamicAttributes"])){
						base.populateDynamicAttributeValues();
					}
				}
			});
		},

		base.populateCNETTemplateNames= function(){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#templateNameList");
			var $table = $tab.find("table.dynamicAttribFields");
			var $item = base.options.item;

			CategoryServiceJS.getCNETTemplateNames({
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Template-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderTemplateNameList").show();
					base.resetDynamicAttributeFields("templateNameList");
					$tab.find("table#addDynamicAttributeName").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderTemplateNameList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if ($.isNotBlank($item) && !$.isEmptyObject($item.condition.dynamicAttributes)){
						$select.val($item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateNameField][0]);
						base.populateCNETDynamicAttributes($item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateNameField][0]);
					}
				}
			});
		},

		base.resetDynamicAttributeFields= function(trigger){
			var $tab = base.contentHolder.find("div#dynamicAttribute");

			if ($.isBlank(trigger)){
				$tab.find("select.selectCombo option").remove();
			}else{
				switch (trigger.toLowerCase()){
				case "templatenamelist": 
					$tab.find("select#templateNameList option").remove();
				case "attributevaluelist":
					$tab.find("div.dynamicAttributeItem:not(#dynamicAttributeItemPattern)").remove();
				case "dynamicattributelist": 
					$tab.find("select#dynamicAttributeList option").remove();
				}
			}
		},

		base.addDynamicAttributeButtonListener= function(attrName){
			var $tab = base.contentHolder.find("div#dynamicAttribute");

			$tab.find("a.addDynamicAttrBtn").off().on({
				click: function(e){
					if (!e.data.locked){
						var $divItemList = $tab.find('div#dynamicAttributeItemList');
						var $divDynamicAttributeItem = $divItemList.find('div#dynamicAttributeItemPattern').clone();
						var inDynamicAttribute = $tab.find("select#dynamicAttributeList >option:gt(0):selected:eq(0)").text();
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

		base.populateIMSDynamicAttributes= function(selectedTemplateName){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#dynamicAttributeList");
			var $table = $tab.find("table.dynamicAttribFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSTemplateAttributes(selectedTemplateName, {
				callback: function(data){
					base.templateAttributes = data;
					var isEmpty = true;

					$.each(base.templateAttributes, function(attrName, attrData) { 
						$select.append($("<option>", {value: attrName}).text(attrData.attributeDisplayName));
						isEmpty = false;
					});

					if (!isEmpty){
						$select.prepend($("<option>", {value: ""}).text("-Select Attribute-"));
						$tab.find("table#addDynamicAttributeName").show();
					}else{
						$tab.find("table#addDynamicAttributeName").hide();
						
						if($.isNotBlank(selectedTemplateName)){
							jAlert("Selected template name does not have any dynamic attributes.", self.moduleName);
						}
					}
				},
				preHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").show();
					base.resetDynamicAttributeFields("attributevaluelist");
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderDynamicAttributeList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if ($.isNotBlank($item) && !$.isEmptyObject($item.condition["dynamicAttributes"])){
						base.populateDynamicAttributeValues();
					}
				}
			});
		},

		base.populateIMSTemplateNames= function(e){
			var $tab = base.contentHolder.find("div#dynamicAttribute");
			var $select = $tab.find("select#templateNameList");
			var $table = $tab.find("table.dynamicAttribFields");
			var $item = base.options.item;

			CategoryServiceJS.getIMSTemplateNames({
				callback: function(data){
					var list = data;
					$select.append($("<option>", {value: ""}).text("-Select Template-"));
					for(var i=0; i<list.length; i++){
						$select.append($("<option>", {value: list[i]}).text(list[i]));
					}
				},
				preHook:function(){
					$tab.find("img#preloaderTemplateNameList").show();
					base.resetDynamicAttributeFields("templateNameList");
					$tab.find("table#addDynamicAttributeName").hide();
					$table.find("select.selectCombo").prop("disabled", true);
				},
				postHook:function(){
					$tab.find("img#preloaderTemplateNameList").hide();
					$table.find("select.selectCombo").prop("disabled", false);
					base.makeSelectSearchable($select);
					if ($.isNotBlank($item) && !$.isEmptyObject($item.condition.dynamicAttributes)){
						$select.val($item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateNameField][0]);
						base.populateIMSDynamicAttributes($item.condition.dynamicAttributes[GLOBAL_storeFacetTemplateNameField][0]);
					}
				}
			});
		},

		base.addDynamicAttributeListener = function(){
			if (base.contentHolder.find("div#ims").length){
				base.populateIMSTemplateNames();
			}else if(base.contentHolder.find("div#cnet").length){
				base.populateCNETTemplateNames();
			}
		},

		base.addFacetListener = function(){

			var $facet = base.contentHolder.find("div#facet");

			if ($.isBlank(base.options.item)) return;

			var $condition = base.options.item.condition;

			if($.isNotBlank($condition.facets["Platform"]))
				$facet.find("select#platformList > option:contains('" + $condition.facets["Platform"][0] + "')").prop("selected", true);

			if($.isNotBlank($condition.facets["Condition"]))
				$facet.find("select#conditionList > option:contains('" + $condition.facets["Condition"][0] + "')").prop("selected", true);

			if($.isNotBlank($condition.facets["Availability"]))
				$facet.find("select#availabilityList > option:contains('" + $condition.facets["Availability"][0] + "')").prop("selected", true);

			if($.isNotBlank($condition.facets["License"]))
				$facet.find("select#licenseList > option:contains('" + $condition.facets["License"][0] + "')").prop("selected", true);

			if($.isNotBlank($condition.facets["ImageExists"]))
				$facet.find("select#imageExistsList > option:contains('" + $condition.facets["ImageExists"][0] + "')").prop("selected", true);

			$facet.find("input#nameContains").val($condition.facets["Name"]);
			$facet.find("input#descriptionContains").val($condition.facets["Description"]);
		};

		base.promptAddFacetItem = function(type){
			base.contentHolder.html(base.getAddFacetItemTemplate());

			if ($.isBlank(base.options.item)){
				base.contentHolder.find("#conditionText").hide();
			}else{
				base.contentHolder.find("#conditionText").text(base.options.item.condition["readableString"]);

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

				if(GLOBAL_PCMGroup){
					base.contentHolder.find('a[href="#dynamicAttribute"]').parent('li').remove();
					base.contentHolder.find("div#dynamicAttribute").remove();
				}
				base.addIMSListener();
				break;
			}

			base.contentHolder.find("#facetItem").tabs("destroy").tabs({
				show: function(event, ui){
					if(ui.panel){
						base.contentHolder.find("#hideCursor").focus();
					}
				}
			});

			base.contentHolder.find("#addItemDate").datepicker({
				showOn: "button",
				minDate: base.options.dateMinDate,
				maxDate: base.options.dateMaxDate,
				changeMonth: true,
			    changeYear: true,
				buttonText: "Expiration Date",
				buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
				buttonImageOnly: true
			});

			base.contentHolder.find("#addFacetItemToRuleBtn").off().on({
				click: function(e){
					setTimeout(function() {
						var position = 1;
						var valid = true;
						var today = new Date();
						today.setHours(0,0,0,0); //ignore time of current date
						
						if (base.options.showPosition){
							position = base.contentHolder.find("#addItemPosition").val();
						}

						position = $.isBlank(position) || isNaN(position)? 1 : position;
						
						var expiryDate = $.trim(base.contentHolder.find("#addItemDate").val());
						var comment = $.defaultIfBlank($.trim(base.contentHolder.find("#addItemComment").val()), "");

						if ($.isNotBlank(expiryDate) && !$.isDate(expiryDate)){
							valid = false;
							jAlert("Invalid date specified.", "Invalid Input");
						} else if(today.getTime() > new Date(expiryDate).getTime()) {
							valid = false;
							jAlert("Date 'Valid Until' cannot be earlier than today", "Invalid Input");
						}
						
						if (valid && !validateGeneric("Validity Date", expiryDate)){
							valid = false;
						}

						if (valid && $.isNotBlank(comment)){
							if(validateComment("Comment", comment, 1, 300)){
								comment = comment.replace(/\n\r?/g, '<br/>');
							}else{
								valid = false;
							}
						}

						var condMap = base.getSelectedFacetFieldValues();

						if (valid && $.isEmptyObject(condMap)){
							valid = false;
							jAlert('Please specify at least one filter condition');
						}
						else if(valid && base.options.showPosition && (position < 1 || position > base.options.maxPosition)){
							valid = false;
							jAlert("Position value should be from 1 - " + base.options.maxPosition + ".", "Max Value Exceeded");
						}

						if (valid && !$.isBlank(condMap["CatCode"]) && !validateCatCode("Catergory Code", condMap["CatCode"])){
							valid = false;
						}

						if (valid){
							var inputFields = ["CatCode","Name","Description"];
							
							$.each(condMap, function(idx, el){
								$.each(el, function(i,elem){
									if($.inArray(idx, inputFields) !== -1){
										if(valid && !validateGeneric(idx, elem)) {
											valid = false;
										}
									}
								});
							});
						}

						if (valid){
							if (base.options.newRecord){
								destroy = false;
								base.options.addFacetItemCallback(position, expiryDate, comment, condMap, base.getTypeLabel(type), base.api);
							}else{
								base.api.destroy();
								base.options.updateFacetItemCallback(base.options.item["memberId"], position, expiryDate, comment, condMap, base.api);
							}
						}
					}, 500);
				}
			});

		};

		base.promptAddProductItem = function(){
			base.contentHolder.html(base.getAddProductItemTemplate());
			base.contentHolder.find("#addItemDate").datepicker({
				showOn: "button",
				minDate: base.options.dateMinDate,
				maxDate: base.options.dateMaxDate,
				changeMonth: true,
			    changeYear: true,
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
					var expDate = $.trim(base.contentHolder.find("#addItemDate").val());
					var comment = $.defaultIfBlank($.trim(base.contentHolder.find("#addItemComment").val()), "");
					var today = new Date();
					var valid = false;

					sequence = $.isBlank(sequence) || isNaN(sequence)? 1 : sequence;
					
					today.setHours(0,0,0,0); //ignore time of current date 
					base.contentHolder.find("#addItemDate").datepicker('disable');

					if ($.isBlank(skus)) {
						jAlert("There are no SKUs specified in the list.", "Invalid Input");
					}
					else if (!commaDelimitedNumberPattern.test(skus)) {
						jAlert("List contains an invalid SKU.", "Invalid Input");
					}							
					else if (!$.isBlank(expDate) && !$.isDate(expDate)){
						jAlert("Invalid date specified.", "Invalid Input");
					}
					else if(base.options.showPosition && (sequence < 1 || sequence > base.options.maxPosition)){
						jAlert("Position value should be from 1 - " + (base.options.maxPosition) + ".", "Max Value Exceeded");
					}
					else if(today.getTime() > new Date(expDate).getTime())
						jAlert("Start date cannot be earlier than today", "Invalid Input");
					else if ($.isNotBlank(comment) && !validateComment("Invalid Input", comment, 1, 300)){
						//error alert in function validateComment
					}
					else {
						valid = true;
						
						if($.isNotBlank(comment)){
							comment = comment.replace(/\n\r?/g, '<br/>');
						}
						destroy = false;
						base.options.addProductItemCallback(sequence, expDate, comment, skus.split(/[\s,]+/), base.api);						
					}

					if(!valid)
						base.contentHolder.find("#addItemDate").datepicker('enable');

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
					render: function(event, api) {
						base.api = api;
						base.contentHolder = $("div", api.elements.content);

						switch(type){
						case "product": base.promptAddProductItem(); break; 
						case "ims": base.promptAddFacetItem(type); break;
						case "cnet": base.promptAddFacetItem(type); break;
						case "facet": base.promptAddFacetItem(type); break;
						};	
						base.addClearButtonListener(type);
					},
					show: function(event, api){
						destroy = true;
						base.contentHolder.find("#addItemDate").datepicker('enable');
						base.addClearButtonListener(type);
					},
					hide: function(event, api) {
						if (destroy === true) {
							base.api.destroy();
						}
					}
				}
			});
		};
		
		base.addClearButtonListener = function(type){
			base.contentHolder.find("#clearBtn").on({
				click: function(e){
					base.contentHolder.find("input,textarea").val("");
					base.contentHolder.find("select").prop("selectedIndex", 0);
					
					switch(e.data.type){
						case "ims":  
							base.contentHolder.find("select#categoryList").change();
							break;
						case "cnet":  
							base.contentHolder.find("select#level1CategoryList").change();
							break;	
					};	
					
					base.contentHolder.find("select#templateNameList").prop("selectedIndex", 0).change();
				}
			}, {type: type, item: base.options.item});
		};

		// Run initializer
		base.init();
	};

	$.addproduct.defaultOptions = {
			type: "product",
			locked: true,
			newRecord: true,
			item: null,
			maxPosition: 1,
			dateMinDate: 0,
			dateMaxDate: "+1Y",
			defaultIMSType: "CatCode",
			showPosition:false,
			addProductItemCallback: function(position, expiryDate, comment, skus, api){},
			addFacetItemCallback: function(position, expiryDate, comment, selectedFacetFieldValues, ruleType, api){},
			updateFacetItemCallback: function(memberId, position, expiryDate, comment, selectedFacetFieldValues, api){}
	};

	$.fn.addproduct = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.addproduct(this, options));
			});
		};
	};

})(jQuery);
