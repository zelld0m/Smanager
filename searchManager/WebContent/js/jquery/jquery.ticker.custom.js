(function($){
	$.fn.ticker = function(options){

		$.fn.ticker.defaults = {
				el: '.listticker',
				page:1,
				pageSize: 10,
				headerText: "",
				speed: 700,
				pause: 3000
		};

		var opts = $.extend({}, $.fn.ticker.defaults, options);

		return this.each(function() {
			var $this = $(this);
			var first = 0;
			
			function removeFirst(){
				first = $this.find('ul' + opts.el + ' li:first').innerHTML;
				$this.find('ul' + opts.el + ' li:first')
				.animate({opacity: 0}, opts.speed)
				.fadeOut('slow', function() {$('ul' + opts.el + ' li:first').remove();});
				addLast($(first));
			}
			
			function addLast(first){
				$last = '<li>' + first.html() + '</li>';
				$this.find('ul' + opts.el).append($last);
				$this.find('ul'+ opts.el + ' li:last')
				.animate({opacity: 1}, opts.speed)
				.fadeIn('slow');
			}

			interval = setInterval(removeFirst, opts.pause);
		});
	};
})(jQuery);