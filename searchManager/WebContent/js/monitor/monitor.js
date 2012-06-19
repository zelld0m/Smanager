(function($){
	$(document).ready(function(){

		$("div#cache").find("a#checkCacheBtn").click(function(evt){
				
				CacheServiceJS.get($("div#cache").find("input#cacheKey").val(), {
					callback: function(data){
						
						var $table = $("div#cache").find("table#contentTable");
						$table.find("tr.rowItem:not(#rowPattern)").remove();
						if(data==null){
							var $tr = $table.find("tr#rowPattern").clone();
							$tr.prop("id", "row0");
							$tr.find("td#field")
							   .prop("colspan", "2")
							   .removeClass("txtAL")
							   .addClass("txtAC")
							   .html("No cache data retrieved");
							$tr.find("td#value").remove();
							$tr.show();
							$table.append($tr);
						}else{
							for(var field in data){
								var i=0;
								var $tr = $table.find("tr#rowPattern").clone();
								$tr.prop("id", "row" + parseInt(++i));
								$tr.find("td#field").html(field);
								$tr.find("td#value").html(data[field]);
								$tr.show();
								$table.append($tr);
							}
						}
					},
				postHook:function(){
					$("div#contentArea").show();
				}
				});
			}
		);
	});
})(jQuery);