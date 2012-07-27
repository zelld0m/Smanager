(function ($) {

	AjaxSolr.CNETFacetWidget = AjaxSolr.AbstractFacetWidget.extend({

		makeRequest: function(e){
			var self = e.data.self; 

			var indices = self.manager.store.find("fq", new RegExp('^' + GLOBAL_storeFacetTemplate + ':'));
			var currFacetTemplate = "";

			if ($.isNotBlank(indices) && indices.length > 0){
				currFacetTemplate = self.manager.store.findByIndex("fq", indices[0]);
				self.manager.store.remove("fq", indices[0]);
			}

			var escapeValue = function(text){
				return text.replace(/\s/g,"?");
			};

			switch(e.data.level){
			case 3: 
				var level3Filter = currFacetTemplate.substring(0, currFacetTemplate.length-1) + "?|?" +  escapeValue(e.data.text);
				self.manager.store.addByValue('fq', level3Filter);
				break;
			case 2: 
				var level2Filter = currFacetTemplate.substring(0, currFacetTemplate.length-1) + "?|?" +  escapeValue(e.data.text) + "*";
				self.manager.store.addByValue('fq', level2Filter);
				break;
			case 1: 
				self.manager.store.addByValue('fq', GLOBAL_storeFacetTemplate + ":" + escapeValue(e.data.text) + "*");
				break;
			};

			self.manager.doRequest(0);
		},

		afterRequest: function () {
			var self = this;
			$(self.target).empty();

			var $facetTemplate = self.manager.response.FacetTemplate;

			if ($.isNotBlank(self.manager.store.values('q')) && $.isNotBlank($facetTemplate)){
				$(self.target).html(AjaxSolr.theme('cnetFacets'));
				var $firstLevel = $(self.target).find("ul#facetHierarchy");
				var $secondLevel = $("<ul>");
				var $thirdLevel = $("<ul>");

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
					var count = getSize($facetTemplate["Level3"]);
					if (count > 1){
						for (var level3 in $facetTemplate["Level3"]){
							$thirdLevel.append('<li id="' + $.formatAsId(level3) + '"><span class="lnk"><a href="javascript:void(0);">' + level3 + " (" + $facetTemplate["Level3"][level3] + ')</a></span></li>');
							$thirdLevel.find('li#' + $.formatAsId(level3)).off().on({
								click: self.makeRequest
							},{self:self, level: 3, text: level3});
						}
					}else{
						for (var level3 in $facetTemplate["Level3"]){
							$thirdLevel.append('<li>' + level3 + '</li>');
						}
					}
				};

				if(!$.isEmptyObject($facetTemplate["Level2"])){
					var count = getSize($facetTemplate["Level2"]);

					if (count > 1){
						for (var level2 in $facetTemplate["Level2"]){
							$secondLevel.append('<li id="' + $.formatAsId(level2) + '"><span class="lnk"><a href="javascript:void(0);">' + level2 + " (" + $facetTemplate["Level2"][level2] + ')</a></span></li>');
							$secondLevel.find('li#' + $.formatAsId(level2)).off().on({
								click: self.makeRequest
							},{self:self, level: 2, text: level2});
						}
					}else{
						for (var level2 in $facetTemplate["Level2"]){
							$secondLevel.append('<li>' + level2 + '</li>');
						}
						if($thirdLevel.find("li").size() > 0){
							$secondLevel.append($thirdLevel);
						}
					}
				}

				if(!$.isEmptyObject($facetTemplate["Level1"])){
					var count = getSize($facetTemplate["Level1"]);

					if (count > 1){
						for (var level1 in $facetTemplate["Level1"]){
							$firstLevel.append('<li id="' + $.formatAsId(level1) + '"><span class="lnk"><a href="javascript:void(0);">' + level1 + " (" + $facetTemplate["Level1"][level1] + ')</a></span></li>');
							$firstLevel.find('li#' + $.formatAsId(level1)).off().on({
								click:self.makeRequest
							},{self:self, level: 1, text: level1});
						}
					}else{
						for (var level1 in $facetTemplate["Level1"]){
							$firstLevel.append('<li>' + level1 + '</li>');
						}
						if($secondLevel.find("li").size() > 0){
							$firstLevel.append($('<li id="second">'));
							$firstLevel.find(">li#second").append($secondLevel);
						}
					}
				}


			};

		}
	});

})(jQuery);