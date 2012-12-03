(function ($) {
	$.slidecheckbox = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM add products of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("slidecheckbox", base);

		base.getImage = function(){
			var imageLabel = "check-default.png";

			if (base.$el.hasClass("active-locked")) imageLabel = "check-active-locked.png";
			if (base.$el.hasClass("on-off")) imageLabel = "check-on-off.png";
			if (base.$el.hasClass("normal-forceadd")) imageLabel = "check-normal-forceadd.png";
			if (base.$el.hasClass("small-normal-forceadd")) imageLabel = "check-small-normal-forceadd.png";

			return GLOBAL_contextPath + "/js/jquery/checkbox/" + imageLabel;
		};

		base.transformCheckbox = function(){
			var thisID		= base.$el.attr('id');
			var thisClass	= base.$el.attr('class');
			var setClass	= thisClass.substring(0, thisClass.indexOf("-checkbox"));
			var imagePath	= base.getImage();

			var divClass = imagePath.substring(imagePath.indexOf('-')+1, imagePath.lastIndexOf('.'));

			base.$el.addClass('hidden');

			base.$el.attr({"checked":base.options.initOn});

			if(base.$el.siblings("div." + setClass).length==0)
				base.$el.after('<div class="' + setClass + '" rel="'+ thisID +'"/>');

			base.$el.siblings("div." + setClass)
					.addClass(base.$el.is(":checked")? "on":"off")
					.addClass(divClass)
					.show()
					.css("background", "url('" + imagePath + "') no-repeat")
					.css("background-position", base.options.initOn? "0% 100%": "100% 0%")
					.off().on({
						click:function(e){
							var $slideCheckbox = $(e.currentTarget);
							var checkboxID = '#' + $slideCheckbox.attr('rel');
					
							if(!$(checkboxID).is(":checked")) {
								$(checkboxID).attr({"checked":true});
								$slideCheckbox.removeClass('off')
											  .addClass('on')
											  .css("background-position", "0% 100%");
							}else{
								$(checkboxID).attr({"checked":false});
								$slideCheckbox.removeClass('on')
								              .addClass('off')
								              .css("background-position", "100% 0%");
							}
		
							base.options.changeStatusCallback(base, {id: e.data.id, item:e.data.item, status: $(checkboxID).is(":checked")});
						}
					},{initOn: base.options.initOn, id: base.options.id, item: base.options.item});
			
			if(base.options.locked){
				base.$el.siblings("div." + setClass).remove();
			}
		};

		base.init = function(){
			base.options = $.extend({},$.slidecheckbox.defaultOptions, options);
			base.transformCheckbox();
		};

		// Run initializer
		base.init();
	};

	$.slidecheckbox.defaultOptions = {
			id: "",
			item: null,
			initOn: false,
			locked: false,
			changeStatusCallback: function(base, data){}
	};

	$.fn.slidecheckbox = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.slidecheckbox(this, options));
			});
		};
	};
})(jQuery);