(function ($) {

	AjaxSolr.AdRotatorWidget = AjaxSolr.AbstractWidget.extend({
		init: function(){
			var self = this;
			$(self.target).html(self.getTemplate());
			var $selectBySize = $(self.target).find('#selectBySize');
			
			$.each(GLOBAL_storeAllowedBannerSizes, function (index, value) {
				$selectBySize.append($('<option>', { 
					value: value,
					text : value,
					selected: GLOBAL_storeDefaultBannerSize === value
				}));
			});
		},
		
		setUpImages: function(size){
			var self = this;
			var slidesGroup = $(self.target).find("#slidesGroup");
			var response = self.manager.response; 
			var banners = response.banners;
			var $selectBySize = $(self.target).find('#selectBySize');
			
			if (response && banners && banners.length > 0){
				var withItemArr = new Array();
				var countLookUp = {};
				
				$.each(GLOBAL_storeAllowedBannerSizes, function (index, value) {
					slidesGroup.append($('<div>').prop({
						id: value,
					}));
				});
				
				for(var i=0; i<banners.length; i++){
					var banner = banners[i];
					if ($.inArray(banner["size"], withItemArr) == -1){
						withItemArr.push(banner["size"]);
						countLookUp[banner["size"]] = 0;
					} 
					
					var image = $("<img>", {
							src: banner["imagePath"],
							alt: banner["imageAlt"],
							title: banner["imageAlt"]
							});
					
					countLookUp[banner["size"]]++;
					slidesGroup.find("div#"+banner["size"]).append(image);
				}

				$.each(GLOBAL_storeAllowedBannerSizes, function (index, value) {
					if($.inArray(value, withItemArr) == -1){
						slidesGroup.find("div#" + value).remove();
						$selectBySize.find("option[value='" + value + "']").remove();
					}else{
						slidesGroup.find("div#" + value).attr("data-item", countLookUp[value]);
					}
				});
			}	
		},
		
		toSlides: function(size){
			var self = this;
			$(self.target).find("#slides").empty().show();
			var slidesGroup = $(self.target).find("#slidesGroup");
			var imageSource = slidesGroup.find("div#" + size).clone();
			var dimension = imageSource.attr("id").split("x");
			var imageItem = imageSource.attr("data-item");
			
			imageSource.prop({
				id: "show" + imageSource.attr("id")
			}).show();
			
			$(self.target).find("#slides").html(imageSource);
			
			$(self.target).find("#show"+ size).slidesjs({
				width: parseInt(dimension[0]),
				height: parseInt(dimension[1]),
				navigation: false,
				play:{
					auto: parseInt(imageItem) > 1
				},
				pagination: {
					active: parseInt(imageItem) > 1
				}
			});
			
		},
		
		beforeRequest: function () {
			var self = this;
			$(self.target).find("#slides-images").empty();
			$(self.target).find("#slides").empty();
		},

		afterRequest: function () {
			var self = this;
			var $selectBySize = $(self.target).find('#selectBySize');
			
			self.setUpImages();
			self.toSlides($selectBySize.find("option:selected").val());
			
			$selectBySize.off().on({
				change: function(e){
					self.toSlides($(e.currentTarget).val());
				}
			});

		},

		getTemplate: function(){
			var self = this;
			var template = "";

			template += '<div class="floatR">';
			template += '	<span class="floatL w150 padT5">Select Banner Size: </span>';
			template += '	<select id="selectBySize"></select>';
			template += '</div>';
			template += '<div class="clearB"></div>';
			template += '<div id="slidesGroup" style="display:none"></div>';
			template += '<div id="slides"></div>';
			
			return template;
		}
	});
})(jQuery);