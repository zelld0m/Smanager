(function($){

	var TopKeyword = {		
			initialNoOfItems: 100,
			itemsPerScroll: 100,
			startIndex: 0,
			reportType: {basic: 1, withStats: 2, custom: 3},

			initTabs: function() {
				$("#tabs").tabs();
			},

			initCustomTab: function() {
				var self = this;

				// initialize date pickers
				$("#fromDate").datepicker({
					constrainInput : true,
					defaultDate : "-7",
					dateFormat : 'M d, yy',
					changeMonth: true,
				    changeYear: true
				}).datepicker("setDate", "-7");

				$("#toDate").datepicker({
					constrainInput : true,
					defaultDate : "0",
					dateFormat : 'M d, yy',
					changeMonth: true,
				    changeYear: true
				}).datepicker("setDate", "0");

				$("#updateDateBtn").click(function(){
					self.customStartIndex = 0;
					self.getCustomRangeList();
				});

				$("a#customDownloadBtn").download({
					headerText:"Download Top Keyword",
					defaultFilename: "",
					fileFormat: ['CSV'],
					sendMail: true,
					requestCallback:function(e){
						if (e.data.type==="csv") self.downloadCustomRangeAsCSV(e.data.filename);
						if (e.data.type==="mail"){
							var recipientArrCleaned = [];
							var recipientToArr = e.data.recipient.split(',');

							for(var recipient in recipientToArr){
								recipientArrCleaned.push($.trim(recipientToArr[recipient]));
							}

							self.sendCustomRangeAsEmail(e.data.filename, recipientArrCleaned);
						}
					}
				});
			},

			downloadCustomRangeAsCSV: function(customFilename) {
				var self = this;

				TopKeywordServiceJS.downloadCustomRangeAsCSV(self.fromDate, self.toDate, customFilename, {
					callback: function(data){
						dwr.engine.openInDownload(data);
					}
				});
			},

			sendCustomRangeAsEmail: function(customFilename, recipients) {
				var self = this;

				for (var i = 0; i < recipients.length; i++) {
					if (!validateEmail('Recipient',recipients[i],1)) {
						return;
					}
				}

				TopKeywordServiceJS.sendCustomRangeAsEmail(self.fromDate, self.toDate, customFilename, recipients, {
					callback: function(data){
						if (data == true) {
							jAlert("Email request being processed. It will be sent to the specified email addresses once done.","Top Keyword");
						}
						else {
							jAlert("Unable to send email.","Top Keyword");
						}
					}
				});
			},

			sendFileAsEmail: function(customFilename, recipients){

				for (var i = 0; i < recipients.length; i++) {
					if (!validateEmail('Recipient',recipients[i],1)) {
						return;
					}
				}

				TopKeywordServiceJS.sendFileAsEmail($("select#fileFilter").val(), customFilename, recipients, {
					callback: function(data){
						if (data == true) {
							jAlert("Email sent.","Top Keyword");
						}
						else {
							jAlert("Unable to send email.","Top Keyword");
						}
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

			loadItems: function($divList, list, start, noOfItems, type){
				var listLen = list.length;
				var patternId;
				var isType2 = false;

				if (type == this.reportType.custom) {
					patternId = "div#itemPattern";
				} else {
					isType2 = $("select#fileFilter").val().indexOf("-splunk") > 0;
					patternId = isType2 ? "div#itemPattern2" : "div#itemPattern1";
				}

				for (var i=start; i < start + noOfItems ; i++){
					if(i == listLen)
						break;

					var $divItem = $divList.find(patternId).clone().prop("id", "row" + $.formatAsId(parseInt(i)+1));
					$divItem.find("label.iter").html(parseInt(i)+1);
					$divItem.find("label.keyword").html(list[i]["keyword"]);
					$divItem.find("label.count").html(list[i]["count"]);

					if (isType2) {
						$divItem.find("label.results").html(list[i]["resultCount"]);
						$divItem.find("label.sku").html($.isNotBlank(list[i]["sku"]) ? list[i]["sku"]: "&nbsp;");
					}

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

			resetHeader: function() {
				var $divHeader1 = $("div#itemHeader1");
				var $divHeader2 = $("div#itemHeader2");
				
				if ($("select#fileFilter").val().indexOf("-splunk") > 0) {
					$divHeader1.hide();
					$divHeader2.show();
				} else {
					$divHeader2.hide();
					$divHeader1.show();
				}
			},

			getKeywordList: function(){
				var self = this;

				TopKeywordServiceJS.getFileContents($("select#fileFilter").val(), {
					callback: function(data){
						var list = data.list;
						var $divList = $("div#itemList");
						$divList.find("div.items:not(#itemPattern1, #itemPattern2)").remove();

						if (list.length > 0){
							self.resetHeader();
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

			getCustomRangeList: function() {
				var self = this;
				var from = $.asUTC($("#fromDate").datepicker("getDate"));
				var to = $.asUTC($("#toDate").datepicker("getDate"));

				if (from > to) {
					jAlert("Invalid date range.");
				} else {
					self.fromDate = from;
					self.toDate = to;

					TopKeywordServiceJS.getTopKeywords(from, to, {
						callback: function(data){
							var $divList = $("div#customRangeItemList");
							$divList.find("div.items:not(#itemPattern)").remove();
	
							if (data.length > 0){
								$("#itemHeader").show();
								self.loadItems($divList, data, self.customStartIndex, self.initialNoOfItems, self.reportType.custom);
								self.customStartIndex = self.initialNoOfItems;
	
								$("#customKeywordCount").html(data.length == 1 ? "1 Keyword" : data.length + " Keywords");
								$("div#customCountSec").show();
	
								$divList.off().on({
									scroll: function(e){
										if(data.length > self.customStartIndex){
											if ($divList[0].scrollTop == $divList[0].scrollHeight - $divList[0].clientHeight) {
												self.loadItems($divList, data, self.customStartIndex, self.itemsPerScroll, self.reportType.custom);
												self.customStartIndex = self.customStartIndex + self.itemsPerScroll;
											}
										}
									}
								},{list: data});
							}else{
								$empty = '<div id="empty" class="items txtAC borderB">Unable to retrieve top keywords for specified date range.</div>';
								$divList.append($empty);
								$("#itemHeader").hide();
								$("div#customCountSec").hide();
							}
						},
						preHook:function(){
							$("img#customPreloader").show();
						},
						postHook:function(){
							$("img#customPreloader").hide();
						}
					});
				}
			},

			getFileList: function(){
				var self = this;
				TopKeywordServiceJS.getFileList({
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
							headerText:"Download Top Keyword",
							defaultFilename: "",
							fileFormat: ['CSV'],
							sendMail: true,
							requestCallback:function(e){
								if (e.data.type==="csv") { self.downloadFileAsCSV(e.data.filename); }
								else if (e.data.type==="mail"){
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
				self.initTabs();
				self.getFileList();
				self.initCustomTab();
			}		
	};

	$(document).ready(function() {
		TopKeyword.init();
	});	

})(jQuery);
