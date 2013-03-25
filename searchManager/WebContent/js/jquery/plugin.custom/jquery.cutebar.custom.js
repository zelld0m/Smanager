(function($) {

	$.cutebar = function(el, options) {
		var base = this;

		base.options = $.extend({}, $.cutebar.defaultOptions, options, true);
		base.$el = $(el);
		base.$groups = {};

		for (g in base.options.groups) {
			base.$groups[g] = $("<div style='white-space: nowrap;'>").attr("id", g);

			for (var i = 0; i < base.options.groups[g].length; i++) {
				var $button = base.$el.find("#" + base.options.groups[g][i]);
				var id = $button.attr("id");

				base.$groups[g].append($button);

				if (base.options.events[id]) {
					$button.on({click: base.options.events[id]});
				}
			}

			if (base.options.events[g]) {
				base.$groups[g].on(base.options.events[g]);
			}

			base.$el.append(base.$groups[g]);
		}

		base.qtipOptions = {
				content  : base.$el, 
				position : base.options.qtip.position,
				hide     : base.options.qtip.hide,
				style    : base.options.qtip.style,
				events   : base.options.qtip.events,
				show     : base.options.qtip.show
			};
		$(base.options.container).qtip(base.qtipOptions);

		// associate this cutebar with the element
		base.$el.data('cutebar', base);

		base.redraw = function() {
			if (base.$el.parent().parent().data('qtip')) {
				base.$el.parent().parent().data('qtip').hide().redraw().reposition().show();
			}
		};
	};

	$.cutebar.defaultOptions = {
		qtip : {
			position : "topRight",
			hide     : { fixed : true },
			style    : { tip : false, classes : 'actions' },
			events   : {},
			show     : {ready: false}
		}
	};
	
	var methods = {
		showGroup: function(groups) {
			var self = this;
			$.each(groups, function() {
				self.$groups[this].show();
			});
			this.redraw();
		},
		hideGroup: function(groups) {
			var self = this;
			$.each(groups, function() {
				self.$groups[this].hide();
			});
			this.redraw();
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