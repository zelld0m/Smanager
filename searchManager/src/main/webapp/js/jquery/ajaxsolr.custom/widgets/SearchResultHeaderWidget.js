(function ($) {

	AjaxSolr.SearchResultHeaderWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest: function () {
			var self = this;
			$(self.target).empty();
		},

		afterRequest: function () {
			var self = this;
			$(self.target).html(self.getTemplate());
			$(self.target).find('#line2 > .val > a').off().on({
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
			var $rk = self.manager.response.responseHeader["redirect_keyword"];
			var $ft = self.manager.response.facet_counts.facet_fields[GLOBAL_storeFacetTemplateName];
			var template = '';

			template += '<div>';
			template += '	<div id="line1">';
			template += '		<span class="label">%%label1%%</span>';
			template += '		<span class="label-to-val">for</span>';
			template += '		<span class="val">"%%keyword1%%"</span>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="line2">';
			template += '		<span class="label related-search">%%label2%%</span>';
			template += '		<span class="val">%%keyword2%%</span>';
			template += '	</div>';
			template += '</div>';

			var setDefaultTextDisplay = function(){
				template = template.replace("%%label1%%", "Search Results")
				.replace("%%keyword1%%", self.manager.store.values('q'));

				var maxRelatedSearch = self.maxRelatedSearch;
				var relatedSearch = "";
				var i=0;

				for (var key in $ft){
					if($.isNotBlank(key)){
						i++;
						relatedSearch += i <= maxRelatedSearch ? '<a href="javascript:void(0);" alt="' + key + '" title="' + key + '">' + key + '</a>': "";
						relatedSearch += Object.keys($ft).length > i && i < maxRelatedSearch ? ", ": "";
					}
				}

				if($.isNotBlank(relatedSearch)){
					template = template.replace("%%label2%%", "Related Searches: ")
					.replace("%%keyword2%%", relatedSearch);
					return template;
				}else{
					var $template = $(template);
					$template.find('span:contains("%%label2%%"):eq(0)').parent().remove();
					return $template;
				}
				
			};
			
			if(!$.isEmptyObject($rk) && $.isNotBlank($rk["message_type"])){
				switch($rk["message_type"]){
				case "2": 
					template = template.replace("%%label1%%", "Showing Results")
					.replace("%%keyword1%%", $rk["replacement_keyword"])
					.replace("%%label2%%", "Search instead for:")
					.replace("%%keyword2%%", $rk["original_keyword"]);
					break;	
				case "3": 
					template = template.replace("%%label1%%", "You searched")
					.replace("%%keyword1%%", $rk["original_keyword"]);

					if($.isNotBlank($rk["custom_text"])){
						template = template.replace("%%label2%%", $rk["custom_text"])
						.replace("%%keyword2%%", " ");
					}else{
						var $template = $(template);
						$template.find('span:contains("%%label2%%"):eq(0)').parent().remove();
						return $template;
					}
					break;
				default:
					return setDefaultTextDisplay();	
				};			
			}else{
				return setDefaultTextDisplay();
			}

			return template;
		}
	});

})(jQuery);