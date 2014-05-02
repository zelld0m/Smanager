(function ($) {

	AjaxSolr.TypeaheadSearchResultWidget = AjaxSolr.AbstractWidget.extend({

		expDateMinDate: 0,
		expDateMaxDate: "+1Y",
		roundLoader:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-circ.gif'/>",

		beforeRequest: function () {
			$(this.target).html(this.roundLoader);
		},

		errorRequest: function () {
			$(this.target).empty().append(AjaxSolr.theme('errorRequest', this.manager.response));
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

				if (i+1 == l){
					$(self.target).wrapInner("<ul class='searchList'>");
				}
			}
		},
		getContent: function(doc) {
			var html = '';
			
			html += '<div>';
			html += '	<ul>';
			html += '		<li"><img class="itemImg" style="width:40%; height:40%;" src="'+doc.ImagePath_2+'"/>';
			html += '		'+doc.Name+'</li>';
			html += '	</ul>';
			html += '</div>';
			
			return html;
		}
	});

})(jQuery);
