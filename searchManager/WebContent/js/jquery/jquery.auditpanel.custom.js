(function($){

	$.auditpanel = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("auditpanel", base);

		base.init = function(){
			base.options = $.extend({},$.auditpanel.defaultOptions, options);
			base.populateTemplate();
			base.getList(1);
		    setInterval(function() {
		    	base.getList(base.options.page);
		    }, base.options.refreshRate);
		};

		base.populateTemplate = function(){
			
			var content ='<div id="auditPanelHeader" class="sideHeader posRel">';

			content+= '<img src="../images/corner_tl.png" class="curveTL"/>';
			content+= '<img src="../images/corner_tr.png" class="curveTR"/>';
			content+= base.options.headerText;
			content+= '<img src="../images/corner_bl.png" class="curveBL"/>';
			content+= '<img src="../images/corner_br.png" class="curveBR"/>';
			content+= '</div>';
			
			content+= '<div id="auditPanelContent" class="sideContent">';
			content+= '<ul id="itemListing" class="listSU fsize11 marT10">';
			content+= '<li id="itemPattern" class="items" style="display:none">';
			content+= '	<p class="notification">';
			content+= '		<span class="user"></span>';
			content+= '		<span class="changedesc"></span><br/>';
			content+= '		<span class="elapsedtime"></span>';
			content+= '	</p>';
			content+= '</li>';
			content+= '</ul>';
			content+= '</div>'; 
			
			content+= '<div id="auditPanelFooter" class="sideFooter" >';
			//content+= '<div id="auditPanelBottomPaging" class="sideBottomPaging"></div>';
			content+= '</div>';

			base.$el.append(content);
		};

		base.getList = function(page) {
			base.options.itemDataCallback(base, page);
		};

		base.prepareList = function(){
//			base.$el.find("ul#itemListing").children().not("#itemPattern").remove();
//			base.$el.find("ul#itemListing").prepend('<div class="pad10 txtAC w200"><p style="width:16px; text-align:center; margin:0 auto;"><img src="../images/ajax-loader-rect.gif"></p></div>'); 
//			base.$el.find("#auditPanelBottomPaging").hide();
		};

		base.populateList = function(data){
			var list = data.list;

			// Delete all the rows except for the "pattern" row
			base.$el.find("ul#itemListing").children().not("#itemPattern").remove();

			// populate list
			for (var i = 0; i < list.length; i++) {
				var clonedId = "item" + $.formatAsId(i+1); 
				base.$el.find("li#itemPattern").clone().appendTo("ul#itemListing").show().attr("id",clonedId);
				base.$el.find("li#" + clonedId + " span.user").text(list[i]["username"]);
				base.$el.find("li#" + clonedId + " span.changedesc").text(list[i]["keyword"] + " " + list[i]["details"]);
				base.$el.find("li#" + clonedId + " span.elapsedtime").text(list[i]["elapsedTime"]);
			}
			
			base.$el.find("li.items:nth-child(even)").addClass("alt");
		};

		base.addPaging = function(page, total){
			
			base.$el.find("#auditPanelBottomPaging").paginate({
				type: 'short',
				pageStyle: base.options.pageStyle,
				currentPage:page, 
				pageSize:base.options.pageSize,
				totalItem:total,
				callbackText: function(itemStart, itemEnd, itemTotal){
					return itemStart + "-" + itemEnd + " of " + itemTotal;
				},
				pageLinkCallback: function(e){ 
					base.getList(e.data.page);
				},
				nextLinkCallback: function(e){ 
					base.getList(e.data.page+1);
				},
				prevLinkCallback: function(e){ 
					base.getList(e.data.page-1);
				},
				firstLinkCallback: function(e){ 
					base.getList(1); 
				},
				lastLinkCallback: function(e){ 
					base.getList(e.data.totalPages);
				}
			});

			base.$el.find("#auditPanelBottomPaging").attr("style", total > 0 ? "display:float" : "display:none");
		};

		// Run initializer
		base.init();
	};

	$.auditpanel.defaultOptions = {
			page:1,
			region: "left",
			pageSize: 10,
			pageStyle: "style1",
			headerText: "",
			itemDataCallback: function(e){},
			itemDifferentialCallback: function(e){},
			refreshRate: 10000
	};

	$.fn.auditpanel = function(options){

		if (this.length) {
			return this.each(function() {
				$(this).empty();
				(new $.auditpanel(this, options));
			});
		};
	};
})(jQuery);