(function($){

	var ZeroResult = {		
			initialNoOfItems: 100,
			itemsPerScroll: 100,
			startIndex: 0,

			sendFileAsEmail: function(customFilename, recipients){
				for (var i = 0; i < recipients.length; i++) {
					if (!validateEmail('Recipient',recipients[i],1)) {
						return;
					}
				}

				ZeroResultServiceJS.sendFileAsEmail($("select#fileFilter").val(), customFilename, recipients, {
					callback: function(data){
						if (data == true) {
							jAlert("Email sent.","Zero Result");
						}
						else {
							jAlert("Unable to send email.","Zero Result");
						}
					}
				});
			},

			downloadFileAsCSV: function(customFilename){
				ZeroResultServiceJS.downloadFileAsCSV($("select#fileFilter").val(), customFilename, {
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
				var self = this;

				ZeroResultServiceJS.getFileContents($("select#fileFilter").val(), {
					callback: function(data){
						var list = data.list;
						var $divList = $("div#itemList");
						$divList.find("div.items:not(#itemPattern)").remove();

						if (list.length > 0){
							self.loadItems($divList, list, self.startIndex, self.initialNoOfItems);
							self.startIndex = self.initialNoOfItems;

							$divList.off().on({
								scroll: function(e){
									if(list.length > self.startIndex){
										if ($divList[0].scrollTop == $divList[0].scrollHeight - $divList[0].clientHeight) {
											self.loadItems($divList, list, self.startIndex, self.itemsPerScroll);
											self.startIndex = self.startIndex + self.itemsPerScroll;
										}
									}
								}
							},{list: list});

							$("#keywordCount").html(data.totalSize == 1 ? "1 Keyword" : data.totalSize + " Keywords");
							$("div#countSec").show();
						}else{
							$empty = '<div id="empty" class="items txtAC borderB">File selected has no records to display</div>';
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
				var self = this;
				ZeroResultServiceJS.getFileList({
					callback: function(data){
						var $select = $("select#fileFilter");

						for (var i=0; i < data.length ; i++){
							var $option = $("<option>", {value:data[i]}).text(data[i]);
							$select.append($option);
						}

						$select.on({
							change: function(){
								self.startIndex = 0;
								self.getKeywordList();
							}
						});

						$("a#downloadBtn").download({
							headerText:"Download Zero Result",
							defaultFilename: "",
							sendMail: true,
							requestCallback:function(e){
								if (e.data.type==="excel") self.downloadFileAsCSV(e.data.filename);
								if (e.data.type==="mail"){
									var recipientArrCleaned = [];
									var recipientToArr = e.data.recipient.split(',');

									for(var recipient in recipientToArr){
										recipientArrCleaned.push($.trim(recipientToArr[recipient]));
									}

									self.sendFileAsEmail(e.data.filename, recipientArrCleaned);
								}
							}
						});

					},
					postHook: function(){
						self.getKeywordList();
					}
				});
			},

			init: function(){
				var self = this;
				self.getFileList();
			}		
	};

	$(document).ready(function() {
		ZeroResult.init();
	});	

})(jQuery);