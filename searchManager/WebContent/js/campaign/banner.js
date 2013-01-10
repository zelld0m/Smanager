(function ($) {

	var Banner = {
		moduleName: "Banner",
		
		getAddBannerTemplate : function(){
			var template = "";
			template += '<div id="addBannerTemplate">';
			template += '<div class="w282 padT10 newBanner">';
			
			template += '	<div id="keywordinput">';
			template += '		<label class="floatL w80 txtLabel">Name: </label>'; 
			template += '		<label class="floatL"><input id="popKeywordName" type="text" class="w188" maxlength="100"></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div id="bannerImageMode">';
			template += '		<label class="floatL w80 txtLabel"></label>'; 
			template += '		<label class="floatL"><label class="floatL padTB2">Paste image URL | Upload an image</label></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="bannerImage">';
			template += '		<label class="floatL w80 txtLabel">Image: </label>'; 
			template += '		<label class="floatL"><label class="floatL padTB2"><textarea id="comment" class="w240"></textarea></label></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div id="description">';
			template += '		<label class="floatL w80 txtLabel">Description: </label>'; 
			template += '		<label class="floatL"><input id="description" type="text" class="w188" maxlength="200"></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div class="txtAR pad3 marT10">';
			template += '		<a id="addButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Save</div> </a>'; 
			template += '		<a id="clearButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Clear</div> </a>';
			template += '	</div>'; 
			template += '</div>';
			template += '</div>';
			return template;
		},
		
		showBannerContent : function(){
			var self = this;
			
			$("a#addPromoBannerIcon").qtip({
				id: "add-banner",
				content: {
					text: $('<div/>'),
					title: { text: 'New Promo Banner', button: true }
				},
				position: {
					target: $("a#addPromoBannerIcon")
				},
				show: {
					ready: true
				},
				style: {width: 'auto'},
				events: { 
					show: function(e, api){
						var $contentHolder = $("div", api.elements.content).html(self.getAddBannerTemplate());
					},
					hide: function (e, api){
						api.destroy();
					}
				}
			});
		},
		
		init : function() {
			var self = this;
			self.showBannerContent();
		}
	};
	
	$(document).ready(function() {
		Banner.init();
	});	
})(jQuery);