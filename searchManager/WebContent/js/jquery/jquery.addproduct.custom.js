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
			if (!base.options.locked){
				base.$el.empty().html(base.getTemplate());
				base.addButtonListener();
			}
		};

		base.getTemplate = function(){
			var template ='';

			template  += '<select id="selectRuleItemType" class="selectCombo w178">';
			template  += '	<option value="product">Product Item</option>';
			template  += '	<option value="ims">IMS Categories</option>';
			template  += '	<option value="cnet">Facet Template Categories</option>';
			template  += '	<option value="facet">Facets</option>';
			template  += '</select>';
			template  += '<a id="addRuleItemIcon" href="javascript:void(0);" class="btnGraph btnAddGrayMid clearfix marR10 marL3">';
			template  += '	<div class="btnGraph marB8"></div>';
			template  += '</a>';

			return template;
		};

		base.getAddProductItemTemplate = function(){
			var template ='';

			template  += '<div>';
			template  += '	<div id="addItemTemplate" class="mar0 w250">';
			template  += '		<h3 class="padT10"></h3>';
			template  += '		<div class="clearB"></div>';
			template  += '		<div class="floatL marT5 marL5">';
			template  += '			<label class="w60 floatL padT5">SKU #: </label>';
			template  += '			<label>';
			template  += '				<span class="fsize10 fgray txtAR">(separated by whitespaces or commas)</span>';
			template  += '				<textarea id="addItemDPNo" style="width: 230px; float: left; margin-bottom: 7px"></textarea>';
			template  += '			</label>';
			template  += '		</div>';
			template  += '		<div class="floatL w155 marT5">';
			template  += '			<label class="floatL w60 marL5 padT5">Valid Until:</label> ';
			template  += '			<label class="ddate"><input id="addItemDate" type="text" class="w65"></label>';
			template  += '		</div>';

			template  += '		<div class="floatL marT5" style="width: 97px">';
			template  += '			<label class="floatL marL5 padT5 w60">Elevation:</label>';
			template  += '			<label><input id="addItemPosition" type="text" class="w25"></label>';
			template  += '		</div>';
			template  += '		<div class="clearB"></div>';
			template  += '		<div class="floatL marT5 marL5">';
			template  += '			<label class="w60 floatL padT5">Comment: </label> ';
			template  += '			<label><textarea id="addItemComment" style="width: 230px; float: left; margin-bottom: 7px"></textarea></label>';
			template  += '		</div>';
			template  += '		<div align="right">';
			template  += '			<a id="addItemToRuleBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '				<div class="buttons fontBold">Elevate</div>';
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
			template  += '	<ul>';
			template  += '		<li><a href="#ims"><span>IMS Categories/Manufacturer</span></a></li>';
			template  += '		<li><a href="#cnet"><span>Facet Template/Manufacturer</span></a></li>';
			template  += '		<li><a href="#dynamicAttribute"><span>Dynamic Attributes</span></a></li>';
			template  += '		<li><a href="#facet"><span>Facets</span></a></li>';
			template  += '	</ul>';

			template  += '	<div id="ims">';
			template  += '		<div class="fsize12 marTB20 marRL50">';
			template  += '			<table class="imsFields">';				
			template  += '				<tr class="catName">';
			template  += '					<td class="w175 padB8" valign="bottom">Category :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<div class="floatL fsize11 marB8 txtDecoUL padT3">';
			template  += '							<a class="switchToCatCode" href="javascript:void(0);">Use category codes instead &raquo;</a>';
			template  += '						</div>';
			template  += '						<div class="clearB"></div>';
			template  += '						<img id="preloaderCategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="categoryList" class="categoryList selectCombo w229" title="Select Category"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="subcategory">';
			template  += '					<td class="w175">SubCategory :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderSubCategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="subCategoryList" class="subCategoryList selectCombo w229" title="Select SubCategory"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="class">';
			template  += '					<td class="w175">Class :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderClassList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="classList" class="classList selectCombo w229" title="Select Class"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="minor">';
			template  += '					<td class="w175" valign="top">SubClass :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderMinorList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="minorList" class="minorList selectCombo w229" title="Select SubClass"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catCode">';
			template  += '					<td  class="w175 padB8" valign="bottom">Category Code :</td>';
			template  += '					<td>';
			template  += '						<div class="floatL fsize11 marB8 txtDecoUL padT3">';
			template  += '							<a class="switchToCatName" href="javascript:void(0);">Use category names instead &raquo;</a>';
			template  += '						</div>';
			template  += '						<div class="clearB"></div>';
			template  += '						<input id="catcode" type="text">';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175" valign="top">Manufacturer :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderManufacturerList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="manufacturerList" class="manufacturerList selectCombo w235" title="Select Manufacturer"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';						
			template  += '		</div>';
			template  += '	</div>';

			template  += '	<div id="cnet">';
			template  += '		<div class="fsize12 marTB20 marRL50">';
			template  += '			<table class="cnetFields">';							
			template  += '				<tr class="catName" id="level1Cat">';
			template  += '					<td class="w175 padB8" valign="bottom">Level 1 Category :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<img id="preloaderLevel1CategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="level1CategoryList" class="level1CategoryList selectCombo w235" title="Select Category"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="level2Cat">';
			template  += '					<td class="w175">Level 2 Category :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderLevel2CategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="level2CategoryList" class="level2CategoryList selectCombo w235" title="Select SubCategory"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr class="catName" id="level3Cat">';
			template  += '					<td class="w175">Level 3 Category :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderLevel3CategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="level3CategoryList" class="level3CategoryList selectCombo w235" title="Select Class"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175" valign="top">Manufacturer :</td>';
			template  += '					<td>';
			template  += '						<img id="preloaderCNETManufacturerList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="cnetmanufacturerList" class="cnetmanufacturerList selectCombo w235" title="Select Manufacturer"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';						
			template  += '		</div>';
			template  += '	</div>';

			template  += '	<div id="dynamicAttribute">';
			template  += '		<div class="fsize12 marTB20 marRL50">';
			template  += '			<table class="dynamicAttributeFields">';							
			template  += '				<tr>';
			template  += '					<td class="w175 padB8" valign="bottom">Template Name :</td>';
			template  += '					<td class="iepadBT0 w278">';
			template  += '						<img id="preloaderTemplateNameList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="templateNameList" class="templateNameList selectCombo w235" title="Select Template Name"></select>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr id="dynamicAttributeValue">';
			template  += '					<td colspan="2">';
			template  += '						<div id="dynamicAttributeItemList">';							
			template  += '							<div id="dynamicAttributeItemPattern" class="dynamicAttributeItem" style="display:none">';
			template  += '								<div class="clearB"></div>';
			template  += '								<div class="w150 floatL padL25 marT8"><span id="dynamicAttributeLabel"></span></div>';
			template  += '								<img src="../images/iconDelete.png" class="deleteAttrIcon posRel floatR marT8 marR8" alt="Delete Attribute" title="Delete Attribute">';
			template  += '								<div class="w235 floatL marT8 border pad10" style="overflow-y:auto; max-height: 107px">';												
			template  += '									<ul id="dynamicAttributeValues">';
			template  += '										<li id="dynamicAttributeValuesPattern" style="display: none;">';
			template  += '											<div>';
			template  += '												<input type="checkbox" class="checkboxFilter">';
			template  += '												<span id="attributeValueName"></span>';
			template  += '											</div>';
			template  += '										</li>';
			template  += '									</ul>';
			template  += '								</div>';
			template  += '							</div>';
			template  += '						</div>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr id="dynamicAttributeName">';
			template  += '					<td class="w175"><p class="padL25">Add Dynamic Attribute :</p></td>';
			template  += '					<td>';
			template  += '						<img id="preloaderDynamicAttributeList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>';
			template  += '						<div class="floatL">';
			template  += '							<select name="select" id="dynamicAttributeList" class="dynamicAttributeList selectCombo w210" title="Add Dynamic Attribute"></select>';
			template  += '						</div>';
			template  += '						<a href="javascript:void(0);" class="addDynamicAttrBtn btnGraph btnAddGrayMid floatR marT3 leftn22 posRel" id="addButton"></a>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '			</table>';						
			template  += '		</div>';
			template  += '	</div>';

			template  += '	<div id="facet">';
			template  += '		<div class="fsize12 padT40">';
			template  += '			<table>';
			template  += '				<tr>';
			template  += '					<td class="w175">Name (contains) :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<input id="nameContains" type="text" class="w250"/>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Description (contains):</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<input id="descriptionContains" type="text" class="w250"/>';
			template  += '					</td>';
			template  += '				</tr>';		
			template  += '				<tr>';
			template  += '					<td class="w175">Platform :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<select name="select" id="platformList" class="selectCombo w235" title="Select Platform" >';
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
			template  += '					<td class="iepadBT0">';
			template  += '						<select name="select" id="conditionList" class="selectCombo w235" title="Select Condition" >';
			template  += '							<option value="all"></option>';
			template  += '							<option value="refurbished">Refurbished</option>';
			template  += '							<option value="open">Open Box</option>';
			template  += '							<option value="clearance">Clearance</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">Availability :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<select name="select" id="availabilityList" class="selectCombo w235" title="Select Availability" >';
			template  += '							<option value="all"></option>';
			template  += '							<option value="instock">In Stock</option>';
			template  += '							<option value="call">Call</option>';
			template  += '						</select>';
			template  += '					</td>';
			template  += '				</tr>';
			template  += '				<tr>';
			template  += '					<td class="w175">License :</td>';
			template  += '					<td class="iepadBT0">';
			template  += '						<select name="select" id="licenseList" class="selectCombo w235" title="Select License" >';
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

			template  += '<div align="right">';
			template  += '	<a id="addItemToRuleBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '		<div class="buttons fontBold">Elevate</div>';
			template  += '	</a>';
			template  += '	<a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '		<div class="buttons fontBold">Clear</div>';
			template  += '	</a>';
			template  += '</div>';
			
			return template;
		};

		base.promptAddFacetItem = function(api, contentHolder){
			contentHolder.html(base.getAddFacetItemTemplate());

			contentHolder.find("#facetItem").tabs({

			});
			
			contentHolder.find("select.selectCombo").combobox({
				
			});
			

		};

		base.promptAddProductItem = function(api, contentHolder){
			contentHolder.html(base.getAddProductItemTemplate());

			contentHolder.find("#addItemDate").attr('id', 'addItemDate_1');

			contentHolder.find("#addItemDate_1").datepicker({
				showOn: "both",
				minDate: base.options.dateMinDate,
				maxDate: base.options.dateMaxDate,
				buttonText: "Expiration Date",
				buttonImage: "../images/icon_calendar.png",
				buttonImageOnly: true
			});

			contentHolder.find("#clearBtn").on({
				click: function(evt){
					contentHolder.find("input,textarea").val("");
				}
			});

			contentHolder.find("#addItemToRuleBtn").on({
				click: function(evt){

					var commaDelimitedNumberPattern = /^\s*\d+\s*(,?\s*\d+\s*)*$/;

					var skus = $.trim(contentHolder.find("#addItemDPNo").val());
					var sequence = $.trim(contentHolder.find("#addItemPosition").val());
					var expDate = $.trim(contentHolder.find("#addItemDate_1").val());
					var comment = $.trim(contentHolder.find("#addItemComment").val().replace(/\n\r?/g, '<br />'));
					var today = new Date();

					today.setHours(0,0,0,0); //ignore time of current date 

					if ($.isBlank(skus)) {
						alert("There are no SKUs specified in the list.");
					}
					else if (!commaDelimitedNumberPattern.test(skus)) {
						alert("List contains an invalid SKU.");
					}							
					else if (!$.isBlank(expDate) && !$.isDate(expDate)){
						alert("Invalid date specified.");
					}
					else if(today.getTime() > new Date(expDate).getTime())
						alert("Start date cannot be earlier than today");
					else if (!isXSSSafe(comment)){
						alert("Invalid comment. HTML/XSS is not allowed.");
					}
					else {
						api.destroy();
						base.options.addProductItemCallback(skus.split(/[\s,]+/), expDate, sequence, comment);						
					}
				}
			});
		};

		base.promptRuleItemDetails = function(target, type){

			$(target).qtip("destroy").qtip({
				content: {
					text: $('<div/>'),
					title: { text: type==="product"? 'Product Item' : 'Facet Item', button: true }
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
						var contentHolder = $("div", api.elements.content);

						switch(type){
						case "product": base.promptAddProductItem(api, contentHolder); break; 
						case "ims": base.promptAddFacetItem(api, contentHolder); break;
						case "cnet": base.promptAddFacetItem(api, contentHolder); break;
						case "facet": base.promptAddFacetItem(api, contentHolder); break;
						};

					},
					hide: function(event, api){
						api.destroy();
					}
				}
			});
		};

		base.addButtonListener = function(){
			base.$el.find('a#addRuleItemIcon').off().on({
				click: function(e){
					base.promptRuleItemDetails(this, $.trim(base.$el.find('select#selectRuleItemType').val()).toLowerCase());
				}
			});
		};

		// Run initializer
		base.init();
	};

	$.addproduct.defaultOptions = {
			locked: true,
			dateMinDate: 0,
			dateMaxDate: "+1Y",
			addProductItemCallback: function(skus, expDate, sequence, comment){},
			addFacetItemCallback: function(){}
	};

	$.fn.addproduct = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.addproduct(this, options));
			});
		};
	};

})(jQuery);