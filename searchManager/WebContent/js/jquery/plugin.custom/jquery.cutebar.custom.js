(function($) {

	$.cutebar = function(el, options) {
		var base = this;

		base.opt = $.extend({}, $.cutebar.defaultOptions, options, true);
		base.$el = $(el);
		base.$groups = {};

		for (g in base.opt.groups) {
			base.$groups[g] = $("<span>").attr("id", g);

			for (var i = 0; i < base.opt.groups[g].length; i++) {
				var $button = base.$el.find("#" + base.opt.groups[g][i]);
				var id = $button.attr("id");

				base.$groups[g].append($button);

				if (base.opt.events[id]) {
					$button.on({click: base.opt.events[id]});
				}
			}

			if (base.opt.events[g]) {
				base.$groups[g].on(base.opt.events[g]);
			}

			base.$el.append(base.$groups[g]);
		}

		$(base.opt.container).qtip({
			content  : base.$el, 
			position : base.opt.qtip.position,
			hide     : base.opt.qtip.hide,
			style    : base.opt.qtip.style,
			events   : base.opt.qtip.events,
			show     : base.opt.qtip.show
		});

		// associate this cutebar with the element
		base.$el.data('cutebar', base);
	};

	$.cutebar.defaultOptions = {
		qtip : {
			position : "topRight",
			hide     : { fixed : true },
			style    : { tip : false, classes : 'actions' },
			events   : {},
			show     : {ready: true}
		}
	};
	
	var methods = {
		showGroup: function(name) {
			this.$groups[name].show();
		},
		hideGroup: function(name) {
			this.$groups[name].hide();
		}
	};

	$.fn.cutebar = function(options) {
		if (typeof (options) == 'string' && methods[options]) {
			return methods[options].apply($(this).data('cutebar'), Array.prototype.slice.call(arguments, 1));
		} else {
			return $.each(this, function() {
				(new $.cutebar(this, options));

			});
		}
	};
})(jQuery);