(function ($) {

	AjaxSolr.CNETFacetWidget = AjaxSolr.AbstractFacetWidget.extend({

		makeRequest: function (e) {
		
			self.manager.doRequest(0);
		},
		
		afterRequest: function () {
			var self = this;
			$(self.target).empty();

			var $facetTemplate = self.manager.response.FacetTemplate;

			if ($.isNotBlank(self.manager.store.values('q')) && $.isNotBlank($facetTemplate)){
				$(self.target).html(AjaxSolr.theme('cnetFacets'));
				var $firstLevel = $(self.target).find("ul#facetHierarchy");
				var $secondLevel = $("ul");
				var $thirdLevel = $("ul");

				var getSize = function(obj){
					var count = 0;

					for (i in obj) {
						if (obj.hasOwnProperty(i)) {
							count++;
						}
					}
					return count;
				};

				if(!$.isEmptyObject($facetTemplate["Level3"])){
					if (count > 1){
						for (var level3 in $facetTemplate["Level3"]){
							$thirdLevel.append('<li id="' + $.formatAsId(level3) + '"><span class="lnk"><a href="javascript:void(0);">' + level3 + " (" + $facetTemplate["Level3"][level3] + ')</a></span></li>');
							$thirdLevel.find('li#' + $.formatAsId(level3)).on({
								click: self.makeRequest
							},{level: 3, text: level3});
						}
					}else{
						$thirdLevel.append('<li>' + $facetTemplate["Level3"][0] + '</li>');
					}
				};

				if(!$.isEmptyObject($facetTemplate["Level2"])){
					var count = getSize($facetTemplate["Level2"]);

					if (count > 1){
						for (var level2 in $facetTemplate["Level2"]){
							$secondLevel.append('<li id="' + $.formatAsId(level2) + '"><span class="lnk"><a href="javascript:void(0);">' + level2 + " (" + $facetTemplate["Level2"][level2] + ')</a></span></li>');
							$secondLevel.find('li#' + $.formatAsId(level2)).on({
								click: self.makeRequest
							},{level: 2, text: level2});
						}
					}else{
						$secondLevel.append('<li>' + $facetTemplate["Level2"][0] + '</li>');
						$secondLevel.append($thirdLevel);
					}
				}

				if(!$.isEmptyObject($facetTemplate["Level1"])){
					var count = getSize($facetTemplate["Level1"]);
					
					if (count > 1){
						for (var level1 in $facetTemplate["Level1"]){
							$firstLevel.append('<li id="' + $.formatAsId(level1) + '"><span class="lnk"><a href="javascript:void(0);">' + level1 + " (" + $facetTemplate["Level1"][level1] + ')</a></span></li>');
							$firstLevel.find('li#' + $.formatAsId(level1)).on({
								click:self.makeRequest
							},{level: 1, text: level1});
						}
					}else{
						$firstLevel.append('<li>' + $facetTemplate["Level1"][0] + '</li>');
						$firstLevel.append($secondLevel);
					}
				}


			};

		}
	});

})(jQuery);