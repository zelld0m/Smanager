(function($){
$.fn.qtip.defaults = $.extend(true, {}, $.fn.qtip.defaults, {
	position: {
		at: 'bottom center', // Position the tooltip above the link
		my: 'top center',
		viewport: $(window), // Keep the tooltip on-screen at all times
		adjust: { screen: true },
		effect: false // Disable positioning animation
	},
	show: {
		  event: 'click',
		  solo: true
	},
	hide: {
		event: 'click'
	},	  
	style: {
		classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped'
	}
});
})(jQuery);	