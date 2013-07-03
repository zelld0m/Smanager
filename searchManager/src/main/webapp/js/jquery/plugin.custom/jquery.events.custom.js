(function($) {

    /////////////////////////////////////////////// 
    ///// Add beforeShow and afterShow events /////
    ///////////////////////////////////////////////
	var _oldShow = $.fn.show;

	$.fn.show = function() {
		var _args = arguments;

		return $(this).each(function() {
			var obj = $(this);
			obj.trigger('beforeShow');
			_oldShow.apply(obj, _args);
			obj.trigger('afterShow');
		});
	};

    /////////////////////////////////////////////// 
    ///// Add beforeHide and afterHide events /////
    ///////////////////////////////////////////////
	var _oldHide = $.fn.hide;

	$.fn.hide = function() {
		var _args = arguments;
		return $(this).each(function() {
			var obj = $(this);
			obj.trigger('beforeHide');
			_oldHide.apply(obj, _args);
			obj.trigger('afterHide');
		});
	};
})(jQuery);