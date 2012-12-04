(function($){

	$.comment = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("viewcomment", base);

		base.init = function(){
			base.options = $.extend({},$.comment.defaultOptions, options);
			base.displayToolTip(base.el);
		};

		base.getCommentTemplate = function(){
			var template  = '';

			template += '<div class="w265 padB8">';
			template += '	<div id="empty" class="txtAC">' + base.options.emptyDisplay + '</div>';
			template += '	<div id="preloader" class="txtAC"><img src="../images/ajax-loader-rect.gif"></div>';
			template += '	<div id="commentEntryTemplate" style="display: none;" >';
			template += '		<div class="pad8 borderB"> ';
			template += '			<div class="padR8 floatL wordwrap" style="width:60px">%%timestamp%%</div>';
			template += '			<div class="floatL w175">';
			template += '				<img src="' + GLOBAL_contextPath +  '/images/user13x13.png" class="marBn3 marR3">';
			template += '	 			<span class="fDblue">%%commentor%%</span>';
			template += '				<span>%%comment%%</span>';
			template += '			</div>';
			template += '			<div class="clearB"></div>';
			template += '		</div>';        
			template += '	</div>';
			template += '	<div id="commentPagingTop"></div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="commentHolder"></div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="commentPagingBottom" style="margin-top:8px"></div>';

			if(!base.options.locked && base.options.showAddComment){
				template += '	<div id="addCommentHolder" class="marT8 w250">';
				template += '		<textarea id="comment" class="w250"></textarea>';
				template += '		<div class="txtAR marT8">';
				template += '			<a id="addCommentBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
				template += '				<div class="buttons fontBold">Add Comment</div>';
				template += '			</a>';
				template += '		</div>';
				template += '	</div>';
			}

			template += '</div>';

			return $(template).html();
		};

		base.getList = function(page) {
			base.options.page = page;
			base.options.itemDataCallback(base, page);
		};

		base.prepareList = function(){
			base.contentHolder.find('div#commentHolder,#commentPagingTop, #commentPagingBottom').empty();
			base.contentHolder.find('div#addCommentHolder > textarea#comment').val("");
			base.contentHolder.find('div#empty, div#addCommentHolder').hide();
			base.contentHolder.find('div#preloader').show();
		};

		base.populateList = function(data){
			var auditItems = "";

			base.contentHolder.find('div#preloader,div#empty').hide();
			base.contentHolder.find('div#addCommentHolder').show();

			if(data==null || data.totalSize==0){
				base.contentHolder.find('div#empty').show();
				return;
			}
			
			for (var i = 0; i < data.list.length; i++) {
				var $item = data.list[i];
				var auditEntryHTML = base.contentHolder.find('div#commentEntryTemplate').html();
				auditEntryHTML = auditEntryHTML.replace("%%timestamp%%", $.defaultIfBlank($item.formatDateTimeUsingConfig,"Date Not Available"));
				auditEntryHTML = auditEntryHTML.replace("%%commentor%%", $.defaultIfBlank($item.username,"User not available"));
				auditEntryHTML = auditEntryHTML.replace("%%comment%%", $.defaultIfBlank($item.comment, "").replace(new RegExp("&",'g'),"&amp;"));
				auditItems += auditEntryHTML;
			}

			base.contentHolder.find('div#commentHolder').html(auditItems);
			base.contentHolder.find('div#commentHolder >div:nth-child(even)').addClass("alt");

		};

		base.addPaging = function(page, total){
			if (total > 0)
				base.contentHolder.find("#commentPagingTop, #commentPagingBottom").paginate({
					type: "short",
					pageStyle: "style2",
					currentPage: page, 
					pageSize: base.options.pageSize,
					totalItem: total,
					callbackText: function(itemStart, itemEnd, itemTotal){
						return itemStart + ' - ' + itemEnd + ' of ' + itemTotal;
					},
					pageLinkCallback: function(e){ base.getList(e.data.page);},
					nextLinkCallback: function(e){ base.getList(parseInt(e.data.page)+1);},
					prevLinkCallback: function(e){ base.getList(parseInt(e.data.page)-1);},
					firstLinkCallback: function(e){ base.getList(1);},
					lastLinkCallback: function(e){ base.getList(e.data.totalPages);}
				});
		};

		base.displayToolTip = function(target){
			$(target).qtip("destroy").qtip({
				content: {
					text: $('<div/>'),
					title: { text: base.options.title, button: true }
				},
				position: {
					at: 'right center',
					my: 'left center',
					target: $(target)
				},
				show:{
					ready: true,
					solo: true
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getCommentTemplate());
						base.getList(1);

						base.contentHolder.find("#addCommentBtn").off().on({
							click: function(e){
								var comment= $.defaultIfBlank($.trim(base.contentHolder.find("#comment").val()), "").replace(/\n\r?/g, '<br/>');
								if(!isXSSSafe(comment)){
									jAlert("Invalid comment. HTML/XSS is not allowed.","Comment");
								}else if ($.isNotBlank(comment))
									base.options.itemAddComment(base, comment);
							}
						});
					},
					hide: function(event, api){
						api.destroy();
					}
				}
			});
		};

		// Run initializer
		base.init();
	};

	$.comment.defaultOptions = {
			title: "Rule Item Comment",
			locked: false,
			page: 1,
			pageSize: 5,
			emptyDisplay: "No comment available.",
			showAddComment: false,
			itemDataCallback: function(base, page){},
			itemAddComment: function(base){}
	};

	$.fn.comment = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.comment(this, options));
			});
		};
	};

})(jQuery);