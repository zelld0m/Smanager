(function ($) {

	AjaxSolr.TextWidget = AjaxSolr.AbstractWidget.extend({
		
		init: function () {
			var self = this;

			$(this.target).find('input[name="query"]').bind('keydown', function(e) {
				var code = (e.keyCode ? e.keyCode : e.which);

				if (code == 13) self.makeRequest($(self.target).find('input[name="query"]').val()); 
			}); 

			$(this.target).find('#searchbutton').click(function() {
				self.makeRequest($(self.target).find('input[name="query"]').val());
			});

		},

		beforeRequest: function(){
			var self = this;
			$(self.target).find('input[type="text"]').prop("disabled", true);
			$(self.target).find('input[type="checkbox"]').prop("disabled", true);
		},

		afterRequest: function () {
			var self = this;
			var keyword = $.trim(self.manager.store.values('q'));

			$(self.target).find('input[type="text"]').prop("disabled", false);
			$(self.target).find('input[type="checkbox"]').prop("disabled", false);

			if ($.isBlank(keyword)) $(self.target).find('div#refinementHolder').hide();
				
			$(self.target).find('input').val(keyword);
			$(self.target).find('input').focus();
		},
		
		makeRequest: function(keyword){
			var self = this;
			
			if ($.isNotBlank(keyword)){
				var isKeepChecked= $('input[name="keepRefinement"]').is(':checked');

				if (!isKeepChecked){
					self.manager.store.removeByValue('fq', new RegExp('\w*'));
				}

				self.manager.store.addByValue('q', $.trim(keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
				self.manager.doRequest(0);
			}
		}
		
	});

})(jQuery);