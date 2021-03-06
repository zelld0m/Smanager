(function($){
	var synonym = {
		fileName: "synonyms.txt",
		defaultFilename: "synonyms",
		downloadFile: function(customFilename){
			var self = this;
			LinguisticsServiceJS.downloadFile(2,self.defaultFilename, customFilename, {
				callback: function(data){
					dwr.engine.openInDownload(data);
				}
			});
		},
		loadItems: function($tablePattern, list){
			var ctr = 1;
			for(var key in list){
				var $tmpLi = $tablePattern.find("li#item").clone().prop("id","item"+parseInt(ctr));
				$tmpLi.html(list[key]);
				if(parseInt(ctr++)%2==0) {
					$tmpLi.removeClass("alt");
					ctr = 1;
				}
				$tablePattern.find("ul#itemList").append($tmpLi);
			}
			$tablePattern.find("li#item").remove();
		},
		showSynonym: function(){
			var self = this;
			LinguisticsServiceJS.getSynonyms(self.fileName, {
				callback:function(data){
					self.loadItems($("table#itemPattern"), data);
					$("a#downloadBtn").download({
						headerText:"Download Synonym",
						fileFormat:['Text'],
						requestCallback:function(e){
							self.downloadFile(e.data.filename);
						}
					});
				},
				preHook: function(){
					
				}
			});		
		},
		
		init: function(){
			var self= this;
			self.showSynonym();
		}
	};
$(document).ready(function() {
	synonym.init();
	});	

})(jQuery);	