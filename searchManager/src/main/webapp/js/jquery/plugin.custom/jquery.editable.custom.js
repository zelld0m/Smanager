(function($) {
	$.editable = function(el, options) {
		var base = this;

		base.el = el;
		base.$el = $(el);

		base.$el.data('editable', base);

		base.init = function() {
			base.options = $.extend({}, $.editable.defaultOptions, options);
			base.$el.attr('spellcheck', base.options.spellcheck);
			base.$el.on({
				click : function() {
					if (base.options.editEnabled()) {
						base.$el.attr('contentEditable', true);
						base.$el.focus();
					}
					return false;
				},
				blur : function() {
					base.$el.removeAttr('contentEditable');
					return false;
				},
				keydown : function(e) {
					switch (e.keyCode) {
					case 27: // esc
						e.preventDefault();
						document.execCommand('undo');
						break;
					}
				},
			});
		};
		
		base.init();
	};

	$.editable.defaultOptions = {
		editEnabled : function() {
			return true;
		},
		spellcheck: false
	};

	$.fn.editable = function(options) {
		if (this.length) {
			return this.each(function() {
				(new $.editable(this, options));
			});
		}
	};
})(jQuery);