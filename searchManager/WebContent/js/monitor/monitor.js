(function($){
	$(document).ready(function(){
		var page = {
				tabSelectedText: "",

				getCache: function(e){
					var key = e.data.key;
					CacheService.get(key, {
						callback: function(data){

						}, 
						preHook:function(){

						},
						postHook:function(){

						}
					});
				},

				prepareCacheTab: function(){

				},
				
				prepareLogTab: function(){

				},

				init:function(){
					tabSelectedText = $("li.ui-tabs-selected > a").find("span").html();
					
					switch(tabSelectedText){
						case "cache" : prepareCacheTab(); break;
						case "log" : prepareLogTab(); break;
					};
				}

		};

		page.init();
	});
})(jQuery);