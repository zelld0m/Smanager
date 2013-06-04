(function ($) {

	AjaxSolr.AdRotatorWidget = AjaxSolr.AbstractWidget.extend({
		beforeRequest: function () {
			var self = this;
			$(self.target).empty();
		},
		afterRequest: function () {
			var self = this;
			$(self.target).html(self.getTemplate());
			var slides = $(self.target).find("#slides");

			var banners = self.manager.response.banners;

			if (banners.length > 0){
				for(var i=0; i<banners.length; i++){
					var banner = banners[i];
					var image = $("<img>", {
						src: banner["imagePath"],
						alt: banner["imageAlt"]
					});

					slides.append(image);
				}

				slides.slidesjs({
					width: 600,
					height: 150,
					navigation: false,
					play:{
						auto: banners.length > 1
					},
					pagination: {
						active: false
					}      
				});
			}
		},

		getTemplate: function(){
			var self = this;
			var template = "";

			template += '<div id="slides"></div>';

			return template;
		}
	});
})(jQuery);