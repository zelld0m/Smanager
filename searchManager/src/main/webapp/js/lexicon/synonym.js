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
		
		setSynonymFile: function(){
			var self = this;
			var COOKIE_STORE_SELECTION = "store.selection";
			var COOKIE_STORE_SELECTED = "store.selected";
			var storeSelected = $.trim($.cookie(COOKIE_STORE_SELECTED));
			var storeSelection = $.trim($.cookie(COOKIE_STORE_SELECTION));
			if($.isNotBlank(storeSelection)){
				parseData = JSON.parse($.trim($.cookie(COOKIE_STORE_SELECTION)));
				for (key in parseData){
					var keyVal = parseData[key];
					var syn = keyVal['syn'];
					if (storeSelected == key){
						self.fileName = self.defaultFilename + '_' + syn + '.txt';
						self.defaultFilename = self.defaultFilename + '_' + syn;
					}		
				}
			}
		},
		
		init: function(){
			var self= this;
			self.setSynonymFile();
			self.showSynonym();
		}
	};
$(document).ready(function() {
	synonym.init();
	});	

})(jQuery);	