(function ($) {

	AjaxSolr.CNETFacetWidget = AjaxSolr.AbstractFacetWidget.extend({

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
					for (var level3 in $facetTemplate["Level3"]){
						$thirdLevel.append('<li>' + level3 + " (" + $facetTemplate["Level3"][level3] + ')</li>');
					};
				};

				if(!$.isEmptyObject($facetTemplate["Level2"])){
					var count = getSize($facetTemplate["Level2"]);

					if (count > 1){
						for (var level2 in $facetTemplate["Level2"]){
							$secondLevel.append('<li>' + level2 + " (" + $facetTemplate["Level2"][level2] + ')</li>');
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
							$firstLevel.append('<li>' + level1 + " (" + $facetTemplate["Level1"][level1] + ')</li>');
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