(function ($) {

	AjaxSolr.TypeaheadCategoryWidget = AjaxSolr.AbstractWidget.extend({

		expDateMinDate: 0,
		expDateMaxDate: "+1Y",
		roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",
		brandCountMap: null,

		beforeRequest: function () {
			$(this.target).html(this.roundLoader);
		},

		errorRequest: function () {
			$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty(); 

			brandCountMap = new Object();
			
			var categories = self.manager.response.facet_counts.facet_fields.Category;
			
			$(self.target).wrapInner("<ul class='searchList'>");
			
			for(obj in categories) {
				$(self.target).append(self.getContent(obj, categories[obj]));
			}
							
		},
		getContent: function(category, count) {
			var html = '';
			
			html += '<div>';
			html += '	<ul>';
			html += '		<li>'+category+' ('+count+')';
			html += '		</li>';
			html += '	</ul>';
			html += '</div>';
			
			return html;
		}
	});

})(jQuery);
