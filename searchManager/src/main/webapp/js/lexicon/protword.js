(function($){
	var ProtWord = {
		fileName: "protwords.txt",
		defaultFilename: "protwords",
		downloadFile: function(customFilename){
			var self = this;
			LinguisticsServiceJS.downloadFile(1,self.defaultFilename, customFilename, {
				callback: function(data){
					dwr.engine.openInDownload(data);
				}
			});
		},
		getObjectSize : function(object){
			var ctr = 0;
			for(var obj in object)
				ctr++;
			return parseInt(ctr);
		},
		loadItems : function($tablePattern,map){
			var self = this;
			var ctr = 1;
			var row = 1;
			var $trItem = $tablePattern.find("tr#itemRow").clone().prop("id", "row" + $.formatAsId(parseInt(row)));
			var mapSize = self.getObjectSize(map);

			for(var key in map){				
				var $tdItem = $trItem.find("td#itemList").clone().prop("id", "itemList" + $.formatAsId(parseInt(ctr)));
				
				if(parseInt(ctr)%2==0)
					$tdItem.removeClass("alt");
				for(var x=0;x < map[key].length;x++){
					var tmpLi = $tablePattern.find("li#item").clone().prop("id", "item" + $.formatAsId(parseInt(x+1)));;
					$tdItem.find("ul").append(tmpLi.html(map[key][x]));
				}
				$trItem.find("li#item").remove();
				$tdItem.find("div").html(key);
				$trItem.append($tdItem);
				if(mapSize > 4){
					if(parseInt(ctr)%5==0){
						row++;
						$tablePattern.append($trItem);
						$trItem = $tablePattern.find("tr#itemRow").clone().prop("id", "row" + $.formatAsId(parseInt(row)));
					}
				}
				
				if(ctr==mapSize){
					$tablePattern.append($trItem);
				}
				ctr++;
			}
			$tablePattern.find("tr#itemRow").remove();
			$tablePattern.find("td#itemList").remove();
		},
		showProtWord : function(){
			var self = this;
			LinguisticsServiceJS.getProtStopWord(self.fileName, {
				callback:function(data){
					self.loadItems($("table#itemPattern"),data);
					$("a#downloadBtn").download({
						headerText:"Download Protword",
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
		init : function(){
			var self= this;
			this.showProtWord();
		}
	};
$(document).ready(function() {
		ProtWord.init();
	});	

})(jQuery);	