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
			
			$selectBySize.off().on({
				change: function(e){
					self.updateSlides.call(e.data.self);
				}
			},{self: self});
		},
		
		updateSlides: function(){
			var self = this;
			var response = self.manager.response; 
			var banners = response.banners;
			var slides = $(self.target).find("#slides");
			slides.empty();
			
			if (response && banners && banners.length > 0){
				slides.show();
				var matchSize = 0;
				
				for(var i=0; i<banners.length; i++){
					var banner = banners[i];
					if($(self.target).find('#selectBySize >option:selected').val()===banner["size"]){
						var image = $("<img>", {
							src: banner["imagePath"],
							alt: banner["imageAlt"],
							title: banner["imageAlt"]
						});
						matchSize++;
						slides.append(image);
					}
				}

				if(matchSize > 0){
					slides.slidesjs({
						width: 600,
						height: 150,
						navigation: false,
						play:{
							auto: matchSize > 1,
						},
						pagination: {
							active: matchSize > 1,
						}
					});
				}
			}
		},
		 
		beforeRequest: function () {
			var self = this;
			var slides = $(self.target).find("#slides");
			slides.empty();
		},

		afterRequest: function () {
			var self = this;
			self.updateSlides.call(self);
		},

		getTemplate: function(){
			var self = this;
			var template = "";

			template += '<div class="floatR">';
			template += '	<span class="floatL w150 padT5">Select Banner Size: </span>';
			template += '	<select id="selectBySize"></select>';
			template += '</div>';
			template += '<div class="clearB"></div>';
			template += '<div id="slides"></div>';

			return template;
		}
	});
})(jQuery);