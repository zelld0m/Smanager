(function ($) {

	AjaxSolr.AnimatedTagCloud = AjaxSolr.AbstractWidget.extend({
		
		afterRequest: function () {
			var keyword = $.trim(this.manager.store.values('q'));
			var hasKeyword = $.isNotBlank(keyword);

			if (!hasKeyword){
				$(this.target).empty(); 
			}
		}
	});

})(jQuery);