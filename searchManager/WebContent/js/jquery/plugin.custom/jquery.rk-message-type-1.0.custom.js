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
			var name = "rkMessageTypeOpt_" +  base.options.id;
			var selectedIndex = base.options.defaultSelectedOption;
			
			if(base.options.rule && $.isNumeric(base.options.rule["replaceKeywordMessageType"]) && base.options.rule["replaceKeywordMessageType"] > 0){
				selectedIndex = base.options.rule["replaceKeywordMessageType"];
			}
			
			base.$el.find('input:radio[name=' + name + ']:nth(' +  (selectedIndex - 1) + ')').prop("checked", true);
			
			base.$el.find('input:radio[name=' + name + ']').off('change').on({
				change: function(evt){
					var selectedOption = $(evt.currentTarget).val();
					var customText = $.trim($(evt.currentTarget).parent('.optionContainer').find('#customText_' + base.options.id).val());
					customText = $.trim(customText.replace(/ +(?= )/g,''));
					
					// Custom text validation, allowing set of characters
					var customTextRegExpression = /^[a-zA-Z0-9\s\-\.,:;\?!\(\)'"\\\/]{0,160}$/;
					if(customText.length > 160){
						alert('Must be at most 160 characters long.');
						return false;
					}else if (!customTextRegExpression.test(customText)) {
						alert('Custom text contains invalid character/s');
						return false;
					}else{
						RedirectServiceJS.updateRKMessageType(evt.data.rule["ruleId"], selectedOption, customText, {
							callback: function(e){
								
							},
							preHook: function(){
								
							},
							postHook: function(){
								
							},
						});
					}
				}
			}, {rule: base.options.rule});
			
		};

		base.getTemplate = function(){
			var template = '';  
			template += '<div id="rkMessageType_' + base.options.id + '" class="rkMessageType">';
			template += '	<div id="rkMessageType1_' + base.options.id + '" class="optionContainer">';
			template += '		<input type="radio" id="rkMessageTypeOpt1_' + base.options.id + '" name="rkMessageTypeOpt_' + base.options.id + '" value="1">';
			template +=	'		<label for="rkMessageTypeTxt1_' + base.options.id + '">';
			template += '			Do not display any additional text in search results (<span class="fbold">Search Result for <span class="fitalic">"original keyword"</span></span>)';
			template += '		</label>';
			template += '	</div>';
			template += '	<div id="rkMessageType2_' + base.options.id + '" class="optionContainer">';
			template += '		<input type="radio" id="rkMessageTypeOpt2_' + base.options.id + '" name="rkMessageTypeOpt_' + base.options.id + '" value="2">';
			template += '		<label for="rkMessageTypeTxt2_' + base.options.id + '">';
			template += '			Display standard text (<span class="fbold">Showing Result for <span class="fitalic">"replacement keyword"</span> / Search instead for: original Keyword</span>)';
			template += '		</label>';
			template += '	</div>';
			template += '	<div id="rkMessageType3_' + base.options.id + '" class="optionContainer">';
			template += '		<input type="radio" id="rkMessageTypeOpt3_' + base.options.id + '" name="rkMessageTypeOpt_' + base.options.id + '" value="3">';
			template += '		<label for="rkMessageTypeTxt3_' + base.options.id + '">';
			template += ' 			Display custom text &nbsp;<input type="text" id="customText_' + base.options.id + '" class="w500" placeholder="Your search query did not yield any results. You might be interested in the following items instead:" />';
			template += '		</label>';
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
			rule: null
	};

	$.fn.rkMessageType = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.rkMessageType(this, options));
			});
		};
	};
})(jQuery);