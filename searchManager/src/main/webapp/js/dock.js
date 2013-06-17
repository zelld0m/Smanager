(function($){
	$(document).ready(function() {
		$('li.notifications').qtip({
					content: {
						text: $('<div/>')
					},
					position: {
						at: 'top center',
						my: 'bottom center',
						target: $(window),
						effect: false
					},
					show: {
						event: 'click', // Show it on click...
						solo: true, // ...and hide all other tooltips...
						modal: true, // ...and make it modal
						effect: function(offset) {
							$(this).animate({
							    height: 'toggle'
							  }, {
							    duration: 1000,
							    specialEasing: {
							      width: 'linear'
							    },
							    complete: function() {
							      $(this).after('<div>Animation complete.</div>');
							    }
							  });
						}
					},
					events:{
						render:function(rEvt,api){
							var content = $("div", api.elements.content);
							content.html("hello");
						}
					},
					style: 'ui-tooltip-light'
				});
	});
})(jQuery);	