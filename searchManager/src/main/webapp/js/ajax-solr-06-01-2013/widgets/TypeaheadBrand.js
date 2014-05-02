(function ($) {

	AjaxSolr.TypeaheadBrandWidget = AjaxSolr.AbstractWidget.extend({

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
			
			var manufacturers = self.manager.response.facet_counts.facet_fields.Manufacturer;
						
			for (var i = 0, l = self.manager.response.response.docs.length; i < l; i++) {
				var doc = self.manager.response.response.docs[i];
//				var debug = self.manager.response.debug.explain[doc.EDP]; 

				$(self.target).append(self.getContent(doc, manufacturers));

				if (i+1 == l){
					$(self.target).wrapInner("<ul class='searchList'>");
				}
			}
			
			$(self.target).find("img.itemImg").on({
				error:function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); 
				}
			});	
		},
		getContent: function(doc, brandCountMap) {
			var html = '';
			
			html += '<div>';
			html += '	<ul>';
			html += '		<li><span class="fsize13">'+doc.Manufacturer+' ('+brandCountMap[doc.Manufacturer]+')</span>';
			html += '			<ul>';
			html += '				<li>';
			html += '					<img class="itemImg" style="width:40%; height:40%;" src="'+doc.ImagePath_2+'"/>&nbsp;'+doc.Name;
			html += '				</li>';
			html += '			</ul>';
			html += '		</li>';
			html += '	</ul>';
			html += '</div>';
			
			return html;
		}
	});

})(jQuery);
