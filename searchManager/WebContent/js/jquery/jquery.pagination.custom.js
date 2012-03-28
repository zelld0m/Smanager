(function($){
	$.fn.paginate = function(options){

		$.fn.paginate.defaults = {
				type: "long",
				currentPage:1,
				pageSize:10, 
				totalItem:10,
				totalPages: 0,
				shortPagingStyle: "contentStyle",
				callbackText: function(itemStart, itemEnd, itemTotal){
					return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + " Items";
				},
				pageLinkCallback: function(e){}, 
				firstLinkCallback: function(e){}, 
				nextLinkCallback: function(e){}, 
				prevLinkCallback: function(e){},
				lastLinkCallback: function(e){},
				pageChangeCallback: function(e){}
		};

		var opts = $.extend({}, $.fn.paginate.defaults, options);

		getLongPaging = function(itemStart, itemEnd){
			var windowFrom = opts.currentPage - 1;
			var windowTo = opts.currentPage + 1;
			var outerWindow = 1;
			var longPaging = "";

			// If the window is truncated on one side, make the other side longer
			if (windowTo > opts.totalPages) {
				windowFrom = Math.max(0, windowFrom - (windowTo - opts.totalPages));
				windowTo = opts.totalPages;
			}
			if (windowFrom < 1) {
				windowTo = Math.min(opts.totalPages, windowTo + (1 - windowFrom));
				windowFrom = 1;
			}

			var visible = [];

			// Always show the first page
			visible.push(1);
			// Don't add inner window pages twice
			for (var i = 2; i <= Math.min(1 + outerWindow, windowFrom - 1); i++) {
				visible.push(i);
			}
			// If the gap is just one page, close the gap
			if (1 + outerWindow == windowFrom - 2) {
				visible.push(windowFrom - 1);
			}
			// Don't add the first or last page twice
			for (var i = Math.max(2, windowFrom); i <= Math.min(windowTo, opts.totalPages - 1); i++) {
				visible.push(i);
			}
			// If the gap is just one page, close the gap
			if (opts.totalPages - outerWindow == windowTo + 2) {
				visible.push(windowTo + 1);
			}
			// Don't add inner window pages twice
			for (var i = Math.max(opts.totalPages - outerWindow, windowTo + 1); i < opts.totalPages; i++) {
				visible.push(i);
			}
			// Always show the last page, unless it's the first page
			if (opts.totalPages > 1) {
				visible.push(opts.totalPages);
			}

			longPaging += '<div class="txtDisplay floatL farial fsize11 fDblue padT10 marL8">';
			longPaging += opts.callbackText(itemStart, itemEnd, opts.totalItem);
			longPaging += '</div>';
			longPaging += '<div class="floatR farial fsize11 fgray txtAR padT10">';
			longPaging += '	<div class="txtAR">';
			longPaging += '		<ul class="pagination"></ul>';
			longPaging += '	</div>';
			longPaging += '</div>';

			var $longPaging = $(longPaging);

			var links = [];
			var prev = null;

			for (var i = 0, l = visible.length; i < l; i++) {
				if (prev && visible[i] > prev + 1) links.push("<li><span>&hellip;</span></li>");
				var activePage = (opts.currentPage == visible[i])? "pager-current" : "";
				var $listItem = $('<li><span class="' + activePage + '"><a href="javascript:void(0);">' + visible[i] + '</a></span></li>');
				$listItem.find("a").on({click:opts.pageLinkCallback}, { page:visible[i] });
				links.push($listItem);
				prev = visible[i];
			}

			if (opts.currentPage != opts.totalPages){
				var $nextLink = $('<li><a href="javascript:void(0);">Next &raquo;</a></li>');
				$nextLink.find("a").on({click:opts.nextLinkCallback}, { page:opts.currentPage });
				links.push($nextLink);
			}

			if (opts.currentPage > 1){
				var $prevLink = $('<li><a href="javascript:void(0);">Prev &raquo;</a></li>');
				$prevLink.find("a").on({click:opts.prevLinkCallback}, { page:opts.currentPage });
				links.unshift($prevLink);
			}

			for (var i = 0, l = links.length; i < l; i++) {
				$longPaging.find("ul.pagination").append(links[i]);
			}

			return $longPaging;
		};

		getShortPaging = function(itemStart, itemEnd){
			
			
			if (opts.shortPagingStyle == "leftStyle"){
				
			var shortPaging = '<div class="txtDisplay styleB" >';
			shortPaging += opts.callbackText(itemStart, itemEnd, opts.totalItem);
			shortPaging += '</div>';
			
				shortPaging += '<div class="pagingArrow styleB floatR w160 txtAR padTB5 marT0">';
				shortPaging += '<a id="firstPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBlueFirst marT3"></div></a>';
			    shortPaging += '<a id="prevPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBluePrev mar3"></div></a>';
			    shortPaging += '<span class="padLR3 fsize11 floatL marT3">Page </span>';
			    shortPaging += '<span class="padLR3 fsize11 floatL marT3"><input id="currentPage" type="text" class="w30 fsize11 txtAR floatL marR3 posRel" style="height:15px; top:-3px" value="' + opts.currentPage + '"> of ' + opts.totalPages + '</span>';
			    shortPaging += '<a id="nextPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBlueNext mar3"></div></a>';
			    shortPaging += '<a id="lastPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBlueLast marT3"></div></a>';
				shortPaging += "</div>";
			}
			
			if (opts.shortPagingStyle == "contentStyle"){
			
			var shortPaging = '<div class="txtDisplay styleB" >';
			shortPaging += opts.callbackText(itemStart, itemEnd, opts.totalItem);
			shortPaging += '</div>';
				
				shortPaging += '<div class="pagingArrow styleA floatR w160 txtAR padTB5 marT0 marR5">';
				shortPaging += '<a id="firstPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL styleAFirst marT3"></div></a>';
			    shortPaging += '<a id="prevPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL styleAPrev mar3"></div></a>';
			    shortPaging += '<span class="padLR3 fsize11 floatL marT3">Page </span>';
			    shortPaging += '<span class="padLR3 fsize11 floatL marT3"><input id="currentPage" type="text" class="w30 fsize11 txtAR floatL marR3 posRel" style="height:15px; top:-3px" value="' + opts.currentPage + '"> of ' + opts.totalPages + '</span>';
			    shortPaging += '<a id="nextPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL styleANext mar3"></div></a>';
			    shortPaging += '<a id="lastPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL styleALast marT3"></div></a>';
				shortPaging += '</div>';
			}
			
			$shortPaging = $(shortPaging);
			
			var $firstPage = $shortPaging.find("a#firstPage");
			var $prevPage  = $shortPaging.find("a#prevPage");
			var $nextPage  = $shortPaging.find("a#nextPage");
			var $lastPage  = $shortPaging.find("a#lastPage");
			var $currPage  = $shortPaging.find("input#currentPage");
			
			if (opts.totalPages == opts.currentPage){
				$nextPage.off();
				$lastPage.off();
			}else{
				$nextPage.on({click:opts.nextLinkCallback}, { page:opts.currentPage});
				$lastPage.on({click:opts.lastLinkCallback}, { page:opts.currentPage, totalPages:opts.totalPages});
			}
			
			if (opts.currentPage == 1){
				$firstPage.off();
				$prevPage.off();
			}else{
				$firstPage.on({click:opts.firstLinkCallback}, { page:opts.currentPage});
				$prevPage.on({click:opts.prevLinkCallback}, { page:opts.currentPage});
			}
			
			$currPage.on({
				keypress:jumpToPage,
				focus: function(e){$(e.target).val("");},
				blur: function(e){$(e.target).val(opts.currentPage);}},{currentPage:opts.currentPage, totalPages:opts.totalPages});
			
			return $shortPaging;
		};
		
		jumpToPage = function(e){
			var charCode = (e.which) ? e.which : e.keyCode;
			
			if (charCode > 31 && (charCode < 48 || charCode > 57)){
				return false;
			}
			
			if (charCode == 13){
				e.data.page = $.trim($(e.target).val());
				if (e.data.page != e.data.currentPage && e.data.page <= e.data.totalPages){
					opts.pageLinkCallback(e);
				}else{
					if (e.data.currentPage != e.data.page) alert("Page number is not valid");	
				}
			}
			
			return true;
		};

		return this.each(function() {
			var $this = $(this);
			$this.empty();

			var itemStart = (((opts.currentPage-1)*opts.pageSize)+1);
			var itemEnd = (((opts.currentPage*opts.pageSize) > opts.totalItem) ? opts.totalItem : (opts.currentPage*opts.pageSize));
			opts.totalPages = Math.ceil(opts.totalItem/opts.pageSize);
			
			if ($.trim(opts.type).toLowerCase()=="long") $this.append(getLongPaging(itemStart, itemEnd));
			if ($.trim(opts.type).toLowerCase()=="short") $this.append(getShortPaging(itemStart, itemEnd));
		});
	};
})(jQuery);