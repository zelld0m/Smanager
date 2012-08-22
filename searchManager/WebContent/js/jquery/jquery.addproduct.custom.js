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

			template  += '<div id="addItemTemplate" class="mar0">';
			template  += '	<h3 class="padT10"></h3>';
			template  += '	<div class="clearB"></div>';
			template  += '	<div class="floatL marT5 marL5">';
			template  += '		<label class="w60 floatL padT5">SKU #: </label>';
			template  += '		<label>';
			template  += '			<span class="fsize10 fgray txtAR">(separated by whitespaces or commas)</span>';
			template  += '			<textarea id="addItemDPNo" style="width: 180px; float: left; margin-bottom: 7px"></textarea>';
			template  += '		</label>';
			template  += '	</div>';
			template  += '	<div class="floatL w155 marT5">';
			template  += '		<label class="floatL w60 marL5 padT5">Valid Until:</label> ';
			template  += '		<label class="ddate"><input id="addItemDate" type="text" class="w65"></label>';
			template  += '	</div>';

			template  += '	<div class="floatL marT5" style="width: 97px">';
			template  += '		<label class="floatL marL5 padT5" style="width: 55px">Elevation:</label>';
			template  += '		<label><input id="addItemPosition" type="text" class="w25"></label>';
			template  += '	</div>';
			template  += '	<div class="clearB"></div>';
			template  += '	<div class="floatL marT5 marL5">';
			template  += '		<label class="w60 floatL padT5">Comment: </label> ';
			template  += '		<label><textarea id="addItemComment" style="width: 180px; float: left; margin-bottom: 7px"></textarea></label>';
			template  += '	</div>';
			template  += '	<div align="right">';
			template  += '		<a id="addItemToRuleBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '			<div class="buttons fontBold">Elevate</div>';
			template  += '		</a>';
			template  += '		<a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template  += '			<div class="buttons fontBold">Clear</div>';
			template  += '		</a>';
			template  += '	</div>';
			template  += '	<div class="clearB"></div>';
			template  += '</div>';

			return template;
		},

		base.promptAddProductItem = function(target){
			$(target).qtip({
				content: {
					text: $('<div/>'),
					title: { text: 'New Product Item', button: true
					}
				},
				position: {
					my: 'center',
					at: 'center',
				},
				show:{
					modal:true
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						var contentHolder = $("div", api.elements.content);
						contentHolder.html(base.getAddProductItemTemplate());
						contentHolder.find("#addOption").tabs({

						});

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
								//ignore time of current date 
								today.setHours(0,0,0,0);

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
									ElevateServiceJS.addItemToRuleUsingPartNumber(selectedRule.ruleId, sequence, expDate, comment, skus.split(/[\s,]+/), {
										callback : function(code){
											showActionResponseFromMap(code, "add", skus, "Please check for the following:\n a) SKU(s) are already present in the list\n b) SKU(s) are actually searchable using the specified keyword.");
											showElevate();
										},
										preHook: function(){ 
											prepareElevate();
										}
									});								
								}
							}
						});
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
					switch($.trim(base.$el.find('select#selectRuleItemType').val()).toLowerCase()){
					case "product": base.promptAddProductItem(this); break; 
					case "ims": break; 
					case "cnet": break; 
					case "facet": break; 
					};
				}
			}, {locked: base.options.locked});
		};

		// Run initializer
		base.init();
	};

	$.addproduct.defaultOptions = {
			locked: true,
			dateMinDate: 0,
			dateMaxDate: "+1Y",

	};

	$.fn.addproduct = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.addproduct(this, options));
			});
		};
	};

})(jQuery);