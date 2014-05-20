(function ($) {

	AjaxSolr.TypeaheadSearchResultWidget = AjaxSolr.AbstractWidget.extend({

		expDateMinDate: 0,
		expDateMaxDate: "+1Y",
		roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",

		beforeRequest: function () {
			$(this.target).html('<img alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif">');
		},

		errorRequest: function () {
			//$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty(); 
			
			for (var i = 0, l = self.manager.response.response.docs.length; i < l; i++) {
				var doc = self.manager.response.response.docs[i];
//				var debug = self.manager.response.debug.explain[doc.EDP]; 

				$(self.target).append(self.getContent(doc));

				if (doc.Expired != undefined)
					$(this.target).find("li#resultItem_" + doc.EDP + " div#expiredHolder").attr("style","display:float");

				$(self.target).find("img.itemImg").on({
					error:function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); 
					}
				});						
				
			}
						
		},
		getContent: function(doc) {
			var html = '';
			
			html += '<div class="floatL">';
			html += '		<img class="itemImg" width="60" src="'+doc.ImagePath_2+'"/>&nbsp;';
			html += '		'+doc.Name+'';
			html += '</div>';
			html += '<div class="clearB"></div>';
			
			return html;
		}
	});

})(jQuery);
