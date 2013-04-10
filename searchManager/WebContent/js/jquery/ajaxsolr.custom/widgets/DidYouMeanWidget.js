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
			var $dym = self.manager.response.responseHeader["did_you_mean"];
			var template = '';

			var maxDidyouMean = self.maxDidyouMean;
			var didYouMean = "";
			var i=0;
			
			if(!$.isEmptyObject($dym)) {
				if(maxDidyouMean == 0) {
					maxDidyouMean = Object.keys($dym).length;
				}
				
				template += '<div>';
				template += '	<div id="didYouMeanLine">';
				template += '		<span class="label did-you-mean">Did You Mean: </span>';
				template += '		<span class="val">%%keyword%%</span>';
				template += '	</div>';
				template += '</div>';
	
				for (var key in $dym){
					if($.isNotBlank(key)) {
						i++;
						didYouMean += i <= maxDidyouMean ? '<a href="javascript:void(0);" alt="' + key + '" title="' + key + '">' + key + '</a>': "";
						didYouMean += Object.keys($dym).length > i && i < maxDidyouMean ? ", ": "";
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