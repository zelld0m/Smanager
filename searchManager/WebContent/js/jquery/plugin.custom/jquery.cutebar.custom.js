(function($) {

	$.cutebar = function(el, options) {
		var base = this;

		base.opt = $.extend({}, $.cutebar.defaultOptions, options, true);
		base.$el = $(el);
		base.$groups = {};

		for (g in base.opt.groups) {
			base.$groups[g] = $("<div style='white-space: nowrap;'>").attr("id", g);

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

		base.qtipOptions = {
				content  : base.$el, 
				position : base.opt.qtip.position,
				hide     : base.opt.qtip.hide,
				style    : base.opt.qtip.style,
				events   : base.opt.qtip.events,
				show     : base.opt.qtip.show
			};
		$(base.opt.container).qtip(base.qtipOptions);

		// associate this cutebar with the element
		base.$el.data('cutebar', base);
		
		base.redraw = function() {
			if (base.$el.parent().parent().data('qtip')) {
			    base.$el.parent().parent().data('qtip').redraw();
			    
			    if (base.$el.parent().parent().data('qtip').rendered) {
				    base.$el.parent().parent().data('qtip').hide();
				    base.$el.parent().parent().data('qtip').show();
				    base.$el.parent().parent().data('qtip').show();
			    }
			}
		};
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
			this.redraw();
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