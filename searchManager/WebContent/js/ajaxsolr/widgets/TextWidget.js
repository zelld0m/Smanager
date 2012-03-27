(function ($) {

	AjaxSolr.TextWidget = AjaxSolr.AbstractFacetWidget.extend({
		init: function () {
			var self = this;

			makeRequest = function(keyword){
				if ($.isNotBlank(keyword)){
					var isKeepChecked= $('input[name="keepRefinement"]').is(':checked');
					
					if (!isKeepChecked){
						self.manager.store.removeByValue('fq', new RegExp('\w*'));
					}
					
					self.manager.store.addByValue('q', $.trim(keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
					self.manager.doRequest(0);
				}
			},
			
			$(this.target).find('input[name="query"]').bind('keydown', function(e) {
				var code = (e.keyCode ? e.keyCode : e.which);

				if (code == 13) makeRequest($(self.target).find('input[name="query"]').val()); 
			}); 

			$(this.target).find('#searchbutton').click(function() {
				makeRequest($(self.target).find('input[name="query"]').val());
			});

		},

		afterRequest: function () {
			var self = this;
			var keyword = $.trim(self.manager.store.values('q'));

			$(this.target).find('input').val(keyword);
			$(this.target).find('input').focus();
			
			RelevancyServiceJS.getAll({
				callback:function(data){
					var list = data.list;
					var total = data.totalSize;
					
					$('select#relevancy > option').not("#norelevancy").remove();
					
					for(var i=0; i<total; i++){
						$("select#relevancy").append('<option value="' + list[i].relevancyId + '">' + list[i].relevancyName + '</option>');
					}
					
					$('select#relevancy > option#norelevancy').attr('selected', 'selected');
					
					var relevancyId = $.trim(self.manager.store.values('relevancyId'));
					if ($.isNotBlank(relevancyId)) {
						$('select#relevancy > option[value="' + relevancyId + '"]').attr('selected', 'selected');
					}
					
					$("select#relevancy").on("change", function(changeEvt){
						var key = self.manager.store.values('q');
						self.manager.store.addByValue('relevancyId', $(changeEvt.target).val());
						if($.isNotBlank(key)) makeRequest(key);
					});
				}
			});
			
			$('a#searchOptionsIcon > div').qtip({
				content: {
					text: $('<div/>'),
					title: { text: "Search Options", button: true }
				},
				events: {
					render: function(event, api) {
						var content = $('div', api.elements.content);
						content.html($("#searchOptionsTemplate").html());
					}
				}
			});
		}
	});

})(jQuery);