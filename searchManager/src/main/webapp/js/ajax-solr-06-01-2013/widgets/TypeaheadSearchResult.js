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

				$(self.target).find("img.itemImg, img.normalImg").on({
					error:function(){ $(this).unbind("error").attr("src", "../images/no-image.jpg"); 
					}
				});						

			}

			if(self.mode == 'simulator')
				self.addListener('.keywordListener');

		},
		getContent: function(doc) {
			var self = this;

			var html = '';

			html += '<div class="itemImgWp floatL">';
			if(self.mode == 'simulator')
				html += '<a href="javascript:void(0);" class="keywordListener">';
			html += '<span id="dpno" style="display:none">'+doc.EDP+'</span>';
			html += '		<img class="'+(self.mode == 'simulator' ? 'itemImg':'normalImg')+' floatL" width="60" src="'+doc.ImagePath_2+'"/>&nbsp;';
			html += '		<div class="'+(self.mode == 'simulator'? 'itemNameSuggest':'itemNameSuggestPreview')+'">'+doc.Name+'</div>';
			if(self.mode == 'simulator')
				html += '</a>';
			html += '</div>';
			html += '<div class="clearB"></div>';
			html += '<div class="sep"></div>';

			return html;
		},
		addListener: function(selector) {
			var self = this;
			$(self.target).find(selector).each(function(){
				$(this).off().on({
					click: function() {
						$(self.searchBox).val($(this).find('span#dpno').text());
						var searchManager = self.manager.searchManager;
						
						self.clearFilters(searchManager);
						
						searchManager.store.addByValue('q', $(this).find('span#dpno').text());
						searchManager.doRequest(0);
						$(self.searchBox).autocomplete("close");
					}
				});
			});

		},
		clearFilters : function(manager) {			
			var indices = manager.store.find("fq", new RegExp('^' + GLOBAL_storeFacetTemplate + ':'));

			if ($.isNotBlank(indices) && indices.length > 0){
				manager.store.remove("fq", indices[0]);
			}
			
			indices = manager.store.find("fq", new RegExp('^Manufacturer:'));

			if ($.isNotBlank(indices) && indices.length > 0){
				manager.store.remove("fq", indices[0]);
			}
		}
	});

})(jQuery);
