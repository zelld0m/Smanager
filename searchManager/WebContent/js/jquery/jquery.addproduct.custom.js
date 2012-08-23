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
			template  += '	</div>';
			
			template  += '	<div id="cnet">';
			template  += '		<div class="fsize12 padT40">';
			template  += '			lorem ipsum dolor sit amet';
			template  += '		</div>';
			template  += '	</div>';
			
			template  += '	<div id="dynamicAttribute">';
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
			template  += '			<div style="clear:both">';	
			template  += '		</div>';
			template  += '	</div>';
			
			template  += '</div>';

			return template;
		};

		base.promptAddFacetItem = function(api, contentHolder){
			contentHolder.html(base.getAddFacetItemTemplate());

			contentHolder.find("#facetItem").tabs({

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