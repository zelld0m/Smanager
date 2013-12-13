(function($){

	$.listbox = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("listbox", base);

		base.options = $.extend({}, $.listbox.defaultOptions, options);

		// Run initializer
		base.init();
	};

	$.listbox.prototype.setId = function(id) {
		var base= this;
		var ui = base.$el;

		ui.find("div:first").prop({
			id: $.isNotBlank(id)? id: "plugin-listbox-" + base.options.id
		});
	};

	$.listbox.prototype.init = function(){
		var base = this;

		if(base.options.isPopup) {
			base.$el.qtip({
				id: "plugin-listbox-qtip",
				content: {
					text: $('<div/>'),
					title: {text: base.options.title, button: true }
				},
				position: {
					my: 'bottom center',
					at: 'top center'
				},
				style: {
					width: 'auto'
				},
				events: {
					render: function(event, api) {
						base.api = api; 
						base.$el = ($("div", api.elements.content));
					},
					show: function(event, api){
						base.$el.html(base.getTemplate());
						base.setId.call(base);
						base.getList.call(base, base.options.page);
					}
				}
			});
		} else {
			base.$el.html(base.getTemplate());
			base.setId(base);
			base.getList(base.options.page);
		}
	};

	$.listbox.prototype.getTemplate = function(){
		var base = this;
		var template  = '';

		template += '<div class="plugin-listbox">';
		template += '	<div class="w265 padB8">';
		template += '		<div id="emptyText" class="txtAC">' + base.options.emptyText + '</div>';
		template += '		<div id="preloader" class="txtAC"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif"></div>';
		template += '		<div id="itemPagingTop"></div>';
		template += '		<div class="clearB"></div>';
		template += '		<div id="header" style="display:none">';
		template += '			<div id="columnHeader">';
		template += '				<span class="deleteIconHeader"></span>';
		template += '				<span id="itemNameHeader">Keyword</span>';        
		template += '				<span id="itemScheduleHeader">Schedule</span>';        
		template += '				<span id="itemStatusHeader">Status</span>';        
		template += '			</div>';
		template += '		</div>';
		template += '		<div class="clearB"></div>';
		template += '		<div id="itemHolder">';
		template += '			<div id="itemPattern" class="item" style="display: none; width: 100%">';
		template += '				<span class="deleteIcon">';
		template += '					<div class="delete_wrap">';
		template += '						<img src="' + GLOBAL_contextPath + '/images/icon_delete2_gray.png">';
		template += '					</div>';
		template += '				</span>';
		template += '				<span class="itemName"></span>';        
		template += '				<span class="itemSchedule"></span>';        
		template += '				<span class="itemStatus"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif"></span>';        
		template += '			</div>';
		template += '		</div>';
		template += '		<div class="clearB"></div>';
		template += '		<div id="itemPagingBottom" style="margin-top:8px"></div>';
		template += '	</div>';
		template += '</div>';

		return $(template);
	};

	$.listbox.prototype.getList = function(page) {
		var base = this;
		base.options.page = page;
		base.options.itemDataCallback.call(base);
	};

	$.listbox.prototype.prepareList = function(){
		var base = this;
		var ui = base.$el;
		
		ui.find("#itemHolder > div:not(#itemPattern), #itemPagingTop, #itemPagingBottom").empty();
		ui.find("div#emptyText, #header").hide();
		ui.find("div#preloader").show();
	};

	$.listbox.prototype.reposition = function() {
		var base = this;
		base.api && base.api.reposition();
	};

	$.listbox.prototype.populateList = function(data){
		var base = this;
		var baseUI = base.$el;

		baseUI.find('#preloader, #emptyText, #header').hide();
		baseUI.find('#itemHolder > div:not(#itemPattern)').remove();

		if(data && data["totalSize"] > 0){
			baseUI.find('#header').show();
			var itemHolder = baseUI.find('#itemHolder');

			for (var i = 0; i < data["list"].length; i++) {

				var item = data["list"][i];
				var itemUI = itemHolder.find('#itemPattern').clone();
				
				itemUI.prop({
					id: item["memberId"]
				});
				
				base.populateItemFields.call(base, itemUI, item);
				itemUI.show();
				itemHolder.append(itemUI);
			}
		}else{
			baseUI.find('div#emptyText').show();
			return;
		}
	};

	$.listbox.prototype.populateItemFields = function(ui, item){
		var base = this;
		
		ui.find(".itemName").text(item["rule"]["ruleName"]);
		ui.find(".itemSchedule").text($.toStoreFormat(item["startDate"],GLOBAL_storeDateFormat) + ' - ' + $.toStoreFormat(item["endDate"],GLOBAL_storeDateFormat));
		base.options.itemRuleStatusCallback.call(base, ui, item);
	};

	$.listbox.prototype.addPaging = function(page, total){
		var base = this;
		
		if (total > 0)
			base.$el.find("#itemPagingTop, #itemPagingBottom").paginate({
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

	$.listbox.defaultOptions = {
			id: 1,
			ruleType: "Banner",
			title: "Item Listing",
			isPopup: true,
			locked: false,
			page: 1,
			pageSize: 5,
			emptyText: "No item available.",
			itemDataCallback: function(base, page){},
			itemDeleteCallback: function(base, rule, ruleItem){}
	};

	$.fn.listbox = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.listbox(this, options));
			});
		};
	};

})(jQuery);
