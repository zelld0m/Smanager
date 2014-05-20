(function ($) {

	AjaxSolr.TypeaheadBrandWidget = AjaxSolr.AbstractWidget.extend({

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
			
			var manufacturers = self.manager.response.facet_counts.facet_fields.Manufacturer;
						
			for (var i = 0, l = self.manager.response.response.docs.length; i < l; i++) {
				var doc = self.manager.response.response.docs[i];
//				var debug = self.manager.response.debug.explain[doc.EDP]; 

				$(self.target).append(self.getContent(doc, manufacturers));

			}

		},
		getContent: function(doc, brandCountMap) {
			var html = '';
			
			html += '<div>';
			html += '		'+doc.Manufacturer+' ('+brandCountMap[doc.Manufacturer]+')';
			html += '</div>';
			html += '<div class="clearB"></div>';
			
			return html;
		}
	});

})(jQuery);
