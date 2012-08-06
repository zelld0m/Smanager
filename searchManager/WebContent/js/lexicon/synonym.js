(function($){
	var synonym = {
		fileName: "synonyms.txt",
		serverName: "afs-pl-schpd07",
		defaultFilename: "synonyms",
		downloadFile: function(customFilename){
			var self = this;
			LinguisticsServiceJS.downloadFile(this.defaultFilename, customFilename, {
				callback: function(data){
					dwr.engine.openInDownload(data);
				}
			});
		},
		loadItems: function($tablePattern,map){
		
			var ctr = 1;
			for(var key in map){
				var $tmpLi = $tablePattern.find("li#item").clone().prop("id","item"+parseInt(ctr));
					$tmpLi.html(key+" => "+map[key]);
					if(parseInt(ctr)%2==0)
						$tmpLi.removeClass("alt");
					$tablePattern.find("ul#itemList").append($tmpLi);
				ctr++;
			}
			$tablePattern.find("li#item").remove();
		},
		showSynonym: function(){
			var self = this;
			LinguisticsServiceJS.getSynonyms(self.fileName, self.serverName, {
				callback:function(data){
					self.loadItems($("table#itemPattern"), data);
					$("a#downloadBtn").download({
						headerText:"Download Stop Words",
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