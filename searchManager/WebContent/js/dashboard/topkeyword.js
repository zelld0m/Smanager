(function($){

	$(document).ready(function(){

		topkeyword = {		
				initialNoOfItems: 1000,
				itemsPerScroll: 100,
				startIndex: 0,
				
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
				
				loadItems: function($divList, list, start, noOfItems){
					var listLen = list.length;
					for (var i=start; i < start + noOfItems ; i++){
						if(i == listLen)
							break;
						
						var $divItem = $divList.find("div#itemPattern").clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));
						$divItem.find("label.iter").html(parseInt(i)+1);
						$divItem.find("label.keyword").html(list[i]["keyword"]);
						$divItem.find("label.count").html(list[i]["count"]);
						
						$divItem.find("a.toggle").text("Show Active Rule").on({
							click:function(data){
								var toggle = this;
								var $itm = $(toggle).parents("div.items");
								var  key = $itm.find(".keyword").html();
								
								if($itm.find("div.rules").is(":visible")){
									$(toggle).html("Show Active Rule");
									$itm.find("div.rules").empty().hide();
								}else{
									var $loader = $('<img id="preloader" alt="Retrieving..." src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">');
									$itm.find("div.rules").show().activerule({
										keyword: key,
										beforeRequest: function(){
											$(toggle).hide();
											$loader.insertAfter(toggle);
										},
										afterRequest: function(){
											$(toggle).show().html("Hide Active Rule");
											$(toggle).nextAll().remove();
										}
									});
								}
							}
						});
						
						$divItem.show();
						$divList.append($divItem);
					}
					
					
					$divList.find("div.items").removeClass("alt");
					$divList.find("div.items:even").addClass("alt");
				},
				
				getKeywordList: function(){
					TopKeywordServiceJS.getFileContents($("select#fileFilter").val(), {
						callback: function(data){
							var list = data.list;
							var $divList = $("div#itemList");
							$divList.find("div.items:not(#itemPattern)").remove();
							
							if (list.length > 0){
								topkeyword.loadItems($divList, list, topkeyword.startIndex, topkeyword.initialNoOfItems);
								topkeyword.startIndex = topkeyword.initialNoOfItems;
								
								$divList.off().on({
									scroll: function(e){
										if(list.length > topkeyword.startIndex){
											if ($divList[0].scrollTop == $divList[0].scrollHeight - $divList[0].clientHeight) {
												topkeyword.loadItems($divList, list, topkeyword.startIndex, topkeyword.itemsPerScroll);
												topkeyword.startIndex = topkeyword.startIndex + topkeyword.itemsPerScroll;
										    }
										}
									}
								},{list: list});
								
								$("#keywordCount").html(data.totalSize == 1 ? "1 Keyword" : data.totalSize + " Keywords");
								$("div#countSec").show();
							}else{
								$empty = '<tr class="rowItem"><td colspan="3" class="txtAC">No matching records found</td></tr>';
								$divList.append($empty);
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
									topkeyword.startIndex = 0;
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