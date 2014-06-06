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
			if(self.mode == 'simulator')
				self.addListener('.keywordListener');
		},
		getContent: function(doc, brandCountMap) {
			var self = this;
			var html = '';
			
			html += '<div class="itemNameBrand">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener">';
			html += '		<span>'+doc.Manufacturer+'</span> ('+brandCountMap[doc.Manufacturer]+')';
			if(self.mode == 'simulator')
				html += '</a">';
			html += '</div>';
			html += '<div class="clearB"></div>';
			
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
