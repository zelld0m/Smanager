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
			$(self.target).find('input[type="text"]').prop("disabled", false);
			$(self.target).find('input[type="checkbox"]').prop("disabled", false);
			var keyword = $.trim(self.manager.store.values('q'));

			$(self.target).find('input').val(keyword);
			$(self.target).find('input').focus();

			RelevancyServiceJS.getAllRule("", 0, 0, {
				callback:function(data){
					var list = data.list;
					var total = data.totalSize;

					$('select#relevancy > option').remove();

					$('select#relevancy').append($("<option>", { value : "keyword_default", selected: true}).text("[ Auto-Match ]"));
					for(var i=0; i<total; i++){
						$('select#relevancy').append($("<option>", { value : list[i].relevancyId}).text(list[i].relevancyName));
					}

					var relevancyId = $.trim(self.manager.store.values('relevancyId'));

					if ($.isNotBlank(relevancyId)) {
						$('select#relevancy > option[value="' + relevancyId + '"]').attr('selected', 'selected');
					}

					$("select#relevancy").combobox({
						selected: function(event, ui){
							var key = self.manager.store.values('q');
							var selectedVal = $(this).val();
							self.manager.store.addByValue('relevancyId', selectedVal==="keyword_default"? "":selectedVal);
							if($.isNotBlank(key)) self.makeRequest(key);
						}
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