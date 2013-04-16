(function ($) {

	AjaxSolr.DidYouMeanWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest: function () {
			var self = this;
			$(self.target).empty();
		},

		afterRequest: function () {
			var self = this;
			$(self.target).html(self.getTemplate());
			$(self.target).find('#didYouMeanLine > .val > a').off().on({
				click: function(e){
					if($.isNotBlank($(this).prop("title"))){
						self.manager.store.addByValue('q', $.trim($(this).prop("title")));
						self.manager.doRequest(0);
					}
				}
			});
		},

		getTemplate : function(){
			var self = this;
			var $sc = self.manager.response.spellcheck['suggestions'];
			var zeroResult = self.manager.response.response['numFound'];
			var template = '';
			var didYouMean = '';
			var hasPrev = false;
			
			if(!$.isEmptyObject($sc)) {
				if(zeroResult != 0) {
					template += '<div class="info notification border fsize11 marB10 marT10"> 		Did You Mean will only appear in production if there are no search results. 	</div>';
				}
				
				template += '<div>';
				template += '	<div id="didYouMeanLine">';
				template += '		<span class="label did-you-mean">Did You Mean: </span>';
				template += '		<span class="val">%%keyword%%</span>';
				template += '	</div>';
				template += '</div>';
				
				for(var temp in $sc) {
					var obj = $sc[temp];
					for(var key in obj['suggestion']) {
						if($.isNotBlank(obj['suggestion'][key])) {
							if(hasPrev) {
								didYouMean += ', ';
							}
							didYouMean += '<a href="javascript:void(0);" alt="' + obj['suggestion'][key] + '" title="' + obj['suggestion'][key] + '">' + obj['suggestion'][key] + '</a>';
							hasPrev = true;
						}
					}
				}
				
				if($.isNotBlank(didYouMean)) {
					template = template.replace("%%keyword%%", didYouMean);
				}
			}
			
			return template;
		}
	});
	
})(jQuery);