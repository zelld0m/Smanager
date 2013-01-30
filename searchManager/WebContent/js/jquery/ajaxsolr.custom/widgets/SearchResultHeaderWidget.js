(function ($) {
	
	AjaxSolr.SearchResultHeaderWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest: function () {
			var self = this;
			$(self.target).empty();
		},
		
		afterRequest: function () {
			var self = this;
			$(self.target).html(self.getTemplate());
		},
	
		getTemplate : function(){
			var self = this;
			var $rk = self.manager.response.responseHeader["replacement_keyword"];
			var $ft = self.manager.response.facet_counts.facet_fields[GLOBAL_storeFacetTemplateName];
			var template = '';
			
			template += '<div>';
			template += '	<div><span>%%label1%%</span> for <span>"%%keyword1%%"</span></div>';
			template += '	<div class="clearB"></div>';
			template += '	<div><span>%%label2%%</span><span>%%keyword2%%</span></div>';
			template += '</div>';
			
			if(!$.isEmptyObject($rk) && $.isNotBlank($rk["type"])){
				switch($rk["type"]){
					case "1": 
						template = template.replace("%%label1%%", "Search Results")
										   .replace("%%keyword1%%", $rk["original_keyword"]);
										   
						if(!$.isEmptyObject($ft)){
							var maxRelatedSearch = 3;
							var relatedSearch = "";
							var i=0;
							
							for (var key in $ft){
								i++;
								relatedSearch += key;
								relatedSearch += $ft.length > 1 && i < maxRelatedSearch ",";
							}
							
							template = template.replace("%%label2%%", "Related Searches:")
							.replace("%%keyword2%%", relatedSearch);
						}				   
						break;
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
						}
						
						break;	
				};			
			}
			
			return template;
		}
	});

})(jQuery);