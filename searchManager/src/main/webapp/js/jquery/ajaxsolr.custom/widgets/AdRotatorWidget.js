(function ($) {

	AjaxSolr.AdRotatorWidget = AjaxSolr.AbstractWidget.extend({
		sizesWithItemArr: null,
		sizesItemCountLookup: null,

		init:function(){
			if($.isBlank($.cookie('simulate.banner.size')))
				$.cookie('simulate.banner.size',GLOBAL_storeDefaultBannerSize,{path:GLOBAL_contextPath});
		},

		setUpImages: function(banners){
			var self = this;
			var groupBySize = $(self.target).find("#groupBySize");
			var sizeSelector = $(self.target).find('#selectBySize');

			var cachedSizeSelected = $.cookie('simulate.banner.size');

			self.sizesWithItemArr = new Array();
			self.sizesItemCountLookup = {};

			// Reset size selector
			sizeSelector.find("option").remove();

			// Create container for images based on size
			$.each(GLOBAL_storeAllowedBannerSizes, function (index, value) {
				groupBySize.append($('<div>').prop({
					id: value,
				}));
			});

			for(var i=0; i<banners.length; i++){
				var banner = banners[i];

				// Process only allowed sizes
				if($.inArray(banner["size"], GLOBAL_storeAllowedBannerSizes) >= 0){
					var dimension = banner["size"].split("x"); 

					if ($.inArray(banner["size"], self.sizesWithItemArr) == -1){
						self.sizesWithItemArr.push(banner["size"]);
						self.sizesItemCountLookup[banner["size"]] = 0;
					} 

					var image = $("<img>", {
						src: banner["imagePath"],
						alt: banner["imageAlt"],
						title: banner["imageAlt"],
						width: parseInt(dimension[0]),
						height: parseInt(dimension[1]),
					});

					self.sizesItemCountLookup[banner["size"]]++;
					groupBySize.find("div#"+banner["size"]).append(image);
				}
			}

			// Remove container with no image
			$.each(GLOBAL_storeAllowedBannerSizes, function (index, value) {
				if($.inArray(value, self.sizesWithItemArr) == -1){
					groupBySize.find("div#" + value).remove();
					sizeSelector.find("option[value='" + value + "']").remove();
				}else{
					groupBySize.find("div#" + value).attr("data-item", self.sizesItemCountLookup[value]);
				}
			});

			$.each(self.sizesWithItemArr, function (index, value) {
				sizeSelector.append($('<option>', { 
					value: value,
					text : value,
					selected: $.isNotBlank(cachedSizeSelected) && cachedSizeSelected=== value || GLOBAL_storeDefaultBannerSize === value
				}));
			});
		},

		toSlides: function(size){
			var self = this;
			$(self.target).find("#slides").empty().show();
			var groupBySize = $(self.target).find("#groupBySize");
			var imageSource = groupBySize.find("div#" + size).clone();
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
			$(self.target).empty();
		},

		afterRequest: function () {
			var self = this;
			var response = self.manager.response; 
			var banners = response.banners;

			if (response && banners && banners.length > 0){
				$(self.target).html(self.getTemplate());
				var sizeSelector = $(self.target).find('#selectBySize');
				self.setUpImages(banners);

				if(self.sizesWithItemArr && self.sizesWithItemArr.length > 0){
					var selectedSize = sizeSelector.find("option:selected").length == 0 ? sizeSelector.find("option:eq(0)").val(): sizeSelector.find("option:selected").val()
					self.toSlides(selectedSize);

					sizeSelector.off().on({
						change: function(e){
							e.data.self.toSlides($(e.currentTarget).val());
						}
					}, {self:self});
				}
			}
		},

		getTemplate: function(){
			var template = "";

			template += '<div class="floatR">';
			template += '	<span class="floatL w150 padT5">Select Banner Size: </span>';
			template += '	<select id="selectBySize"></select>';
			template += '</div>';
			template += '<div class="clearB"></div>';
			template += '<div id="groupBySize" style="display:none"></div>';
			template += '<div id="slides"></div>';

			return template;
		}
	});
})(jQuery);