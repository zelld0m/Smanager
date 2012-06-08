(function($){

	$(document).ready(function(){

		topkeyword = {		
				
				sendFileAsEmail: function(customFilename, recipients){
					TopKeywordServiceJS.sendFileAsEmail($("select#fileFilter").val(), customFilename, recipients, {
						callback: function(data){
							 
						}
					});
				},
				
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
							
							if (list.length > 0){
								for (var i=0; i < list.length ; i++){
									var $tr = $("tr#rowPattern").clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));
									$tr.find("td#iter").html(parseInt(i)+1);
									$tr.find("td#keyword").html(list[i]["keyword"]);
									$tr.find("td#count").html(list[i]["count"]);
									$tr.show();
									$table.append($tr);
								}
								
								$("#keywordCount").html(data.totalSize == 1 ? "1 Keyword" : data.totalSize + " Keywords");
								$("div#countSec").show();
							}else{
								$empty = '<tr class="rowItem"><td colspan="3" class="txtAC">No matching records found</td></tr>';
								$table.append($empty);
								$("div#countSec").hide();
							}
						},
						preHook:function(){
							$("img#preloader").show();
						},
						postHook:function(){
							$("img#preloader").hide();
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
								sendMail: true,
								requestCallback:function(e){
									if (e.data.type==="excel") topkeyword.downloadFileAsCSV(e.data.filename);
									if (e.data.type==="mail"){
										var recipientArrCleaned = []
										var recipientToArr = e.data.recipient.split(',');
										
										for(var recipient in recipientToArr){
											recipientArrCleaned.push($.trim(recipientToArr[recipient]));
										}
										
										topkeyword.sendFileAsEmail(e.data.filename, recipientArrCleaned);
									}
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