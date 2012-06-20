(function ($) {

	AjaxSolr.AnimatedTagCloudWidget = AjaxSolr.AbstractWidget.extend({
		init: function(){
			var self = this;
			$(self.target).html(AjaxSolr.theme('animatedTagCloud'));
			self.showTagCloud();
		},
		
		showTagCloud: function(){
			var self = this;
			$(self.target).show();
			
			$ul = $('ul#tagList');
			$ul.empty();

			StoreKeywordServiceJS.getAll(self.limit, {
				callback: function(data){
					var list = data.list;
					var colorCSSList= ["fColorOne","fColorTwo","fColorThree","fColorFour"];
					var sizeCSSList = ["fontxSmall","fontSmall","fontMedium","fontLarge"];

					for (var i=0; i < list.length; i++){
						var randomColor = colorCSSList[Math.floor(Math.random()*colorCSSList.length)];
						var randomSize = sizeCSSList[Math.floor(Math.random()*sizeCSSList.length)];
						$ul.append('<li><a class="' + randomColor + ' ' + randomSize + '" href="javascript:void(0)">' + list[i].keyword.keyword + '</a></li>');	
					}

					$ul.find('li > a').click(function(e){
						$(self.target).hide();
						self.manager.store.addByValue('q',$.trim($(e.target).html()));
						self.manager.doRequest(0);
					});
				},
				postHook:function(){
					self.animatedTagCloud();
				}
			});
		},
		
		animatedTagCloud: function(){
			var self = this;
			
			if (!$(self.target).find("#canvas").tagcanvas({
				textFont: null,
				textColour: null,
				weight: true,
				outlineThickness : 1,
				maxSpeed : 0.05,
				depth : 0.8,
				reverse: true,
				freezeActive: true,
				shape: "sphere"
			}, 'tagContainer')) {
				$(self.target).hide();
			}
		},
		
		afterRequest: function () {
			var self = this;
			
			if($.isNotBlank(self.manager.store.values('q'))){
				$(self.target).hide();
			}else{
				$(self.target).show();
			}	
		}
	});

})(jQuery);