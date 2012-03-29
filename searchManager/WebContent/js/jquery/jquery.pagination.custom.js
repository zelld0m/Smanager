(function($){

	$.paginate = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("paginate", base);

		base.init = function(){
			base.options = $.extend({},$.paginate.defaultOptions, options);
			
			var itemStart = (((base.options.currentPage-1)*base.options.pageSize)+1);
			var itemEnd = (((base.options.currentPage*base.options.pageSize) > base.options.totalItem) ? base.options.totalItem : (base.options.currentPage*base.options.pageSize));
			base.options.totalPages = Math.ceil(base.options.totalItem/base.options.pageSize);

			var typeOfPaging = $.trim(base.options.type).toLowerCase();
			
			if (typeOfPaging === "short") base.useShortPaging(itemStart, itemEnd);
			if (typeOfPaging === "long") base.useLongPaging(itemStart, itemEnd);
			
		};

		base.useShortPaging = function(itemStart, itemEnd){

			var shortPaging = "";
			var style = $.trim(base.options.pageStyle).toLowerCase();
			
			if (style === "style1"){
				shortPaging += '<div class="txtDisplay styleB" >';
				shortPaging += base.options.callbackText(itemStart, itemEnd, base.options.totalItem);
				shortPaging += '</div>';

				shortPaging += '<div class="pagingArrow styleB floatR w160 txtAR padTB5 marT0">';
				shortPaging += '<a id="firstPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBlueFirst marT3"></div></a>';
				shortPaging += '<a id="prevPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBluePrev mar3"></div></a>';
				shortPaging += '<span class="padLR3 fsize11 floatL marT3">Page </span>';
				shortPaging += '<span class="padLR3 fsize11 floatL marT3"><input id="currentPage" type="text" class="w30 fsize11 txtAR floatL marR3 posRel" style="height:15px; top:-3px" value="' + base.options.currentPage + '"> of ' + base.options.totalPages + '</span>';
				shortPaging += '<a id="nextPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBlueNext mar3"></div></a>';
				shortPaging += '<a id="lastPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL btnBlueLast marT3"></div></a>';
				shortPaging += "</div>";
			}

			if (style === "style2"){
				shortPaging += '<div class="txtDisplay styleB" >';
				shortPaging += base.options.callbackText(itemStart, itemEnd, base.options.totalItem);
				shortPaging += '</div>';

				shortPaging += '<div class="pagingArrow styleA floatR w160 txtAR padTB5 marT0 marR5">';
				shortPaging += '<a id="firstPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL styleAFirst marT3"></div></a>';
				shortPaging += '<a id="prevPage" href="javascript:void(0)" class="btnGraph"><div class="btnGraph floatL styleAPrev mar3"></div></a>';
				shortPaging += '<span class="padLR3 fsize11 floatL marT3">Page </span>';
				shortPaging += '<span class="padLR3 fsize11 floatL marT3"><input id="currentPage" type="text" class="w30 fsize11 txtAR floatL marR3 posRel" style="height:15px; top:-3px" value="' + base.options.currentPage + '"> of ' + base.options.totalPages + '</span>';
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

			if (base.options.totalPages == base.options.currentPage){
				$nextPage.off();
				$lastPage.off();
			}else{
				$nextPage.on({click:base.options.nextLinkCallback}, { page:base.options.currentPage});
				$lastPage.on({click:base.options.lastLinkCallback}, { page:base.options.currentPage, totalPages:base.options.totalPages});
			}

			if (base.options.currentPage == 1){
				$firstPage.off();
				$prevPage.off();
			}else{
				$firstPage.on({click:base.options.firstLinkCallback}, { page:base.options.currentPage});
				$prevPage.on({click:base.options.prevLinkCallback}, { page:base.options.currentPage});
			}

			$currPage.on(
					{	keypress:base.jumpToPage,
						focus: function(e){
							$(e.target).val("");
						},
						blur: function(e){
							$(e.target).val(base.options.currentPage);
						}},
						{	currentPage:base.options.currentPage, 
							totalPages:base.options.totalPages }
			);

			return base.$el.append($shortPaging);
		};

		base.useLongPaging = function(itemStart, itemEnd){
			var windowFrom = base.options.currentPage - 1;
			var windowTo = base.options.currentPage + 1;
			var outerWindow = 1;
			var longPaging = "";

			// If the window is truncated on one side, make the other side longer
			if (windowTo > base.options.totalPages) {
				windowFrom = Math.max(0, windowFrom - (windowTo - base.options.totalPages));
				windowTo = base.options.totalPages;
			}
			if (windowFrom < 1) {
				windowTo = Math.min(base.options.totalPages, windowTo + (1 - windowFrom));
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
			for (var i = Math.max(2, windowFrom); i <= Math.min(windowTo, base.options.totalPages - 1); i++) {
				visible.push(i);
			}
			// If the gap is just one page, close the gap
			if (base.options.totalPages - outerWindow == windowTo + 2) {
				visible.push(windowTo + 1);
			}
			// Don't add inner window pages twice
			for (var i = Math.max(base.options.totalPages - outerWindow, windowTo + 1); i < base.options.totalPages; i++) {
				visible.push(i);
			}
			// Always show the last page, unless it's the first page
			if (base.options.totalPages > 1) {
				visible.push(base.options.totalPages);
			}

			longPaging += '<div class="txtDisplay floatL farial fsize11 fDblue padT10 marL8">';
			longPaging += base.options.callbackText(itemStart, itemEnd, base.options.totalItem);
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
				var activePage = (base.options.currentPage == visible[i])? "pager-current" : "";
				var $listItem = $('<li><span class="' + activePage + '"><a href="javascript:void(0);">' + visible[i] + '</a></span></li>');
				$listItem.find("a").on({click:base.options.pageLinkCallback}, { page:visible[i] });
				links.push($listItem);
				prev = visible[i];
			}

			if (base.options.currentPage != base.options.totalPages){
				var $nextLink = $('<li><a href="javascript:void(0);">Next &raquo;</a></li>');
				$nextLink.find("a").on({click:base.options.nextLinkCallback}, { page:base.options.currentPage });
				links.push($nextLink);
			}

			if (base.options.currentPage > 1){
				var $prevLink = $('<li><a href="javascript:void(0);">Prev &raquo;</a></li>');
				$prevLink.find("a").on({click:base.options.prevLinkCallback}, { page:base.options.currentPage });
				links.unshift($prevLink);
			}

			for (var i = 0, l = links.length; i < l; i++) {
				$longPaging.find("ul.pagination").append(links[i]);
			}

			return base.$el.append($longPaging);
		};
		
		base.jumpToPage = function(e){
			var charCode = (e.which) ? e.which : e.keyCode;

			if (charCode > 31 && (charCode < 48 || charCode > 57)){
				return false;
			}

			if (charCode == 13){
				e.data.page = $.trim($(e.target).val());
				if (e.data.page != e.data.currentPage && e.data.page <= e.data.totalPages){
					base.options.pageLinkCallback(e);
				}else{
					if (e.data.currentPage != e.data.page) alert("Page number is not valid");	
				}
			}

			return true;
		};
		
		
		// Run initializer
		base.init();
	};

	$.paginate.defaultOptions = {
			type: "long",
			currentPage:1,
			pageSize: 10, 
			totalItem: 10,
			totalPages: 0,
			pageStyle: "style1",
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

	$.fn.paginate = function(options){

		if (this.length) {
			return this.each(function() {
				$(this).empty();
				(new $.paginate(this, options));
			});
		};
	};
})(jQuery);
