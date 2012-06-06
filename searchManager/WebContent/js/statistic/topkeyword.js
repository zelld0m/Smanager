(function($){

	$(document).ready(function(){

		topkeyword = {		
				
				downloadFileAsCSV: function(customFilename){
					TopKeywordServiceJS.downloadFileAsCSV($("select#fileFilter").val(), customFilename, {
						callback: function(data){
							 dwr.engine.openInDownload(data);
						}
					});
				},
				
				getKeywordList: function(){
					TopKeywordServiceJS.getFileContents($("select#fileFilter").val(), {
						callback: function(data){
							var list = data.list;
							var $table = $("table#keywordTable");
							
							$table.find("tr.rowItem:not(#rowPattern)").remove();
							
							for (var i=0; i < list.length ; i++){
								var $tr = $("tr#rowPattern").clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));
								$tr.find("td#iter").html(parseInt(i)+1);
								$tr.find("td#keyword").html(list[i]["keyword"]);
								$tr.find("td#count").html(list[i]["count"]);
								$tr.show();
								$table.append($tr);
							}
							
							$("#keywordCount").html(data.totalSize == 1 ? "1 Keyword" : data.totalSize + " Keywords");
							
						}
					});
				},
				
				getFileList: function(){
					TopKeywordServiceJS.getFileList({
						callback: function(data){
							var $select = $("select#fileFilter");
							
							for (var i=0; i < data.length ; i++){
								var $option = $("<option>", {value:data[i]}).text(data[i]);
								$select.append($option);
							}
							
							$select.on({
								change: function(){
									topkeyword.getKeywordList();
								}
							});
							
							$("a#downloadBtn").download({
								headerText:"Download Top Keyword",
								defaultFilename: "",
								requestCallback:function(e){
									if (e.data.type==="excel") topkeyword.downloadFileAsCSV(e.data.filename);
								}
							});
							
						},
						postHook: function(){
							topkeyword.getKeywordList();
						}
					});
				},

				init: function(){
					topkeyword.getFileList();
				}		
		};

		topkeyword.init();	
	});
})(jQuery);