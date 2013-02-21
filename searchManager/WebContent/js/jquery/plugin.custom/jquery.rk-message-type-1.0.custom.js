(function($){
	$.rkMessageType = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("rkMessageType", base);

		base.init = function(){
			base.options = $.extend({},$.rkMessageType.defaultOptions, options);
			base.$el.html(base.getTemplate());
			base.setInitSelectedOption();
		};
		
		base.setInitSelectedOption = function(){
			var name = "rkMessageTypeOpt" +  $.formatAsId(base.options.id);
			var selectedIndex = base.options.defaultSelectedOption;
			
			if(base.options.rule && $.isNumeric(base.options.rule["replaceKeywordMessageType"]) && base.options.rule["replaceKeywordMessageType"] > 0){
				selectedIndex = base.options.rule["replaceKeywordMessageType"];
			}
			
			base.$el.find('input:radio[name=' + name + ']:nth(' +  (selectedIndex - 1) + ')').prop("checked", true);
			
			var customTextListener = function(e){
				var customText = $.trim($(e.currentTarget).val().replace(/\s+(?=\s)/g,'')); 
				var customTextRegExpression = /^[a-zA-Z0-9\s\-\.,:;\?!\(\)'"\\\/]{0,160}$/;
				
				if(customText.length > 160){
					jAlert('Must be at most 160 characters long.');
				}else if (!customTextRegExpression.test(customText)) {
					jAlert('Custom text contains invalid character/s');
				}else{
					if($.isNotBlank(customText) && 
							customText.toLowerCase() !== base.options.customText.toLowerCase() && 
							customText.toLowerCase() !== e.data.rule["replaceKeywordMessageCustomText"].toLowerCase()){
						
						RedirectServiceJS.updateRKMessageType(e.data.rule["ruleId"], 3, customText, {
							callback: function(data){
								if (data > 0){
									e.data.rule["replaceKeywordMessageCustomText"] = customText;
									base.options.successCustomTextUpdateCallback(customText);
								}
							},
							preHook: function(){
								base.$el.find('.preloader').show();
								base.$el.find('input:radio[name=' + name + ']').prop({
									disabled: true
								});
							},
							postHook: function(){
								base.$el.find('.preloader').hide();
								base.$el.find('input:radio[name=' + name + ']').prop({
									disabled: false
								});
							},
						});						
					}
				}				
			};			
			
			if(base.options.rule && $.isNotBlank(base.options.rule["replaceKeywordMessageCustomText"])){
				base.$el.find('#customText' + $.formatAsId(base.options.id)).val(base.options.rule["replaceKeywordMessageCustomText"]);
			}
			
			// Add listener to custom text
			base.$el.find('#customText' + $.formatAsId(base.options.id)).prop({
				readonly: base.$el.find('input:radio[id=' + "rkMessageTypeOpt3" +  $.formatAsId(base.options.id) + ']').is(':not(:checked)'),
				disabled: base.$el.find('input:radio[id=' + "rkMessageTypeOpt3" +  $.formatAsId(base.options.id) + ']').is(':not(:checked)')
			}).off('focusout blur mouseleave').on({
				focusout: customTextListener,
				mouseleave: customTextListener
			}, {rule: base.options.rule});
			
			// add listener to radio button
			base.$el.find('input:radio[name=' + name + ']').off('change').on({
				change: function(evt){
					var selectedOption = $(evt.currentTarget).val();
					
					base.$el.find('#customText_' + base.options.id).prop({
						readonly: selectedOption!=3,
						disabled: selectedOption!=3
					});
					
					RedirectServiceJS.updateRKMessageType(evt.data.rule["ruleId"], selectedOption, null, {
							callback: function(e){
								if (e > 0){
									base.options.successTypeUpdateCallback(selectedOption);
								}
							},
							preHook: function(){
								base.$el.find('.preloader').show();
								base.$el.find('input:radio[name=' + name + ']').prop({
									disabled: true
								});
							},
							postHook: function(){
								base.$el.find('.preloader').hide();
								base.$el.find('input:radio[name=' + name + ']').prop({
									disabled: false
								});
							},
						});
				}
			}, {rule: base.options.rule});
			
		};

		base.getTemplate = function(){
			var template = '';  
			template += '<div id="rkMessageType_' + base.options.id + '" class="rkMessageType">';
			template += '	<div id="rkMessageType1_' + base.options.id + '" class="optionContainer">';
			template += '		<input type="radio" id="rkMessageTypeOpt1_' + base.options.id + '" name="rkMessageTypeOpt_' + base.options.id + '" value="1">';
			template +=	'		<label for="rkMessageTypeTxt1_' + base.options.id + '">';
			template += '			Default text: <span class="fbold">Search Result for <span class="fitalic">"original keyword"</span></span>';
			template += '		</label>';
			template += '	</div>';
			template += '	<div id="rkMessageType2_' + base.options.id + '" class="optionContainer">';
			template += '		<input type="radio" id="rkMessageTypeOpt2_' + base.options.id + '" name="rkMessageTypeOpt_' + base.options.id + '" value="2">';
			template += '		<label for="rkMessageTypeTxt2_' + base.options.id + '">';
			template += '			Standard text: <span class="fbold">Showing Result for <span class="fitalic">"replacement keyword"</span> / Search instead for: original Keyword</span>';
			template += '		</label>';
			template += '	</div>';
			template += '	<div id="rkMessageType3_' + base.options.id + '" class="optionContainer">';
			template += '		<input type="radio" id="rkMessageTypeOpt3_' + base.options.id + '" name="rkMessageTypeOpt_' + base.options.id + '" value="3">';
			template += '		<label for="rkMessageTypeTxt3_' + base.options.id + '">';
			template += ' 			Custom text: &nbsp;<input type="text" readonly="readonly" disabled="disabled" id="customText_' + base.options.id + '" class="w500" placeholder="' + base.options.customText + '"/>';
			template += '		</label>';
			template += '		<label class="w30 preloader posRel floatR" style="display:none"><img src="' + GLOBAL_contextPath  + '/images/ajax-loader-rect.gif"></label>';
			template += '	</div>';
			template += '</div>';
			return template;
		};

		// Run initializer
		base.init();
	};

	$.rkMessageType.defaultOptions = {
			id: 1,
			defaultSelectedOption: 1, 
			customText: "Your search query did not yield any results. You might be interested in the following items instead.",
			rule: null,
			successTypeUpdateCallback: function(value){},
			successCustomTextUpdateCallback: function(customText){}
	};

	$.fn.rkMessageType = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.rkMessageType(this, options));
			});
		};
	};
})(jQuery);