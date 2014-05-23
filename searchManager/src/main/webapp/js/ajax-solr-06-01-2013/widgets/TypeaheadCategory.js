(function ($) {

	AjaxSolr.TypeaheadCategoryWidget = AjaxSolr.AbstractWidget.extend({

		expDateMinDate: 0,
		expDateMaxDate: "+1Y",
		roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
		brandCountMap: null,

		beforeRequest: function () {
			$(this.target).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
		},

		errorRequest: function () {
			//$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty(); 

			brandCountMap = new Object();
			
			var categories = self.manager.response.facet_counts.facet_fields.Category;
			
			for(obj in categories) {
				$(self.target).append(self.getContent(obj, categories[obj]));
			}
			if(self.mode == 'simulator')
				self.addListener('.keywordListener');		
		},
		getContent: function(category, count) {
			var self = this;
			var html = '';
			
			html += '<div>';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener">';
			html += '		<span>'+category+'</span> ('+count+')';
			if(self.mode == 'simulator')
				html += '</a>';
			html += '</div>';
			
			return html;
		},
		addListener: function(selector) {
			$(selector).on({
				click: function() {
					$('#keyword').val($(this).find('span').html());
					$('#searchKeyword').find('a#searchBtn').click();
					$("#keyword").autocomplete("close");
				}
			});
		}
	});

})(jQuery);
