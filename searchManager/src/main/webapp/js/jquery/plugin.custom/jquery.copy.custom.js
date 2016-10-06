(function($){

	$.copy = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("copy", base);

		var COOKIE_STORE_SELECTION = "store.selection";
		var COOKIE_STORE_SELECTED = "store.selected";
		var parseData;
		
		var storeSelected = $.trim($.cookie(COOKIE_STORE_SELECTED));
		var storeSelection = $.trim($.cookie(COOKIE_STORE_SELECTION));
		if($.isNotBlank(storeSelection)){
			parseData = JSON.parse($.trim($.cookie(COOKIE_STORE_SELECTION)));
		}		
		
		base.init = function(){
			base.options = $.extend(true, {},$.copy.defaultOptions, options);
			base.options.storeList = parseData;
			base.options.currentStore = storeSelected;
			base.$el.off().on({
				click: base.showCopyOption()
			});
		};
		
		base.getTemplate = function(){
			var template = '<div>';		
			template += '<div><span class="floatL marT10 marR5">Store: </span>';
			template += '<span>';
			template += '<select id="storeCode" name="storeCode" class="mar0 w168 floatR marT6">';
			
			for (key in base.options.storeList){
				var keyVal = base.options.storeList[key];
				var storeName = keyVal['name'];
				if(key !== base.options.currentStore){
					template += '<option value="' +  key + '">' + storeName + '</option>';
				}
			}
			
			template += '</select>';
			template += '</span></div>';
			template += '<div class="clearB marT8 txtAR">';
			template += '<a id="copy" href="javascript:void(0);" class="buttons btnGray clearfix marT8"><div class="buttons fontBold">Copy</div></a>';
			template += '</div>';
			template += '</div>';
			
			return template;
		};

		base.showCopyOption = function(){
			base.$el.qtip({
				content: {
					text: $('<div/>'),
					title: { 
						text: base.options.headerText, 
						button: true
					}
				},
				position:{
					at: 'top center',
					my: 'bottom center',
					target: base.$el
				},
				style: {
					width: 'auto',
					classes: base.options.classes,
				},
				show: {
					event: 'click',
					solo: base.options.solo
				},
				events: { 
					show: function(event, api){
						var $content = $("div", api.elements.content);
						$content.html(base.getTemplate());
						$content.find("a#copy").off().on({
							click: function(e){
								e.data = { storeCode: $content.find('select#storeCode option:selected').val() };
								base.options.requestCallback(e);
							}
						});
					}
				}
			});
		};
		
		// Run initializer
		base.init();
	};
	
	$.copy.defaultOptions = {
			headerText:"Copy",
			storeList: '',
			currentStore: '',
			solo: true,
			classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped',
			requestCallback: function(e){}
	};

	$.fn.copy = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.copy(this, options));
			});
		};
	};
})(jQuery);