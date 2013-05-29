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

		base.setId = function(ui, id) {
			ui.find("div:first").prop({
				id: $.isNotBlank(id)? id: "plugin-listbox-" + base.options.id
			});
		};
		
		base.init = function(){
			base.options = $.extend({},$.listbox.defaultOptions, options);
			
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
						show: function(event, api) {
							base.api = api;
							base.$el = $("div", api.elements.content);
							base.$el.empty().append(base.getTemplate());
							base.setId(base.$el);
							base.getList(base.options.page);
						}
					}
				});
			} else {
				base.$el.empty().append(base.getTemplate());
				base.setId(base.$el);
				base.getList(base.options.page);
			}
		};

		base.getTemplate = function(){
			var template  = '';

			template += '<div class="plugin-listbox">';
			template += '	<div class="w265 padB8">';
			template += '		<div id="emptyText" class="txtAC">' + base.options.emptyText + '</div>';
			template += '		<div id="preloader" class="txtAC"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif"></div>';
			template += '		<div id="itemPagingTop"></div>';
			template += '		<div class="clearB"></div>';
			template += '		<div id="itemHolder">';
			template += '			<div id="itemPattern" class="item" style="display: none;">';
			template += '				<span class="deleteIcon"><img src="' + GLOBAL_contextPath + '/images/icon_delete2.png"></span>';
			template += '				<span class="itemName"></span>';        
			template += '			</div>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div id="itemPagingBottom" style="margin-top:8px"></div>';
			template += '	</div>';
			template += '</div>';

			return $(template);
		};

		base.getList = function(page) {
			base.options.page = page;
			base.options.itemDataCallback(base, page);
		};

		base.prepareList = function(){
			base.$el.find("#itemHolder > div:not(#itemPattern), #itemPagingTop, #itemPagingBottom").empty();
					base.$el.find("div#emptyText").hide();
					base.$el.find("div#preloader").show();
		};

		base.populateList = function(data){
			
			base.$el.find('div#preloader, div#emptyText').hide();

			if(data && data["totalSize"] > 0){
				var itemHolder = base.$el.find('div#itemHolder');
				for (var i = 0; i < data["list"].length; i++) {
					var item = data["list"][i];
					var ui = itemHolder.find('div#itemPattern').clone();
					base.populateItemFields(ui, item);
					ui.prop({
						id: item[base.options.idField]
					});
					ui.show();
					itemHolder.append(ui);
				}
				
				itemHolder.find('div:nth-child(even)').addClass("alt");
			}else{
				base.$el.find('div#emptyText').show();
				return;
			}

		};
		
		base.populateItemFields = function(ui, item){
			ui.find(".deleteIcon").off().on({
				click: function(e){
					jConfirm("Delete " + e.data.base.options.parentNameText + " in " + e.data.item[base.options.nameField] + "?", "Linked Keyword", function(result){
						if(result) base.itemDeleteCallback(base, e.data.item, e.data.parentItem);
					});
				}
			}, {item: item, base: base, parentItem: base.options.ruleItem});
			
			ui.find(".itemName").text(item[base.options.nameField]);
		};

		base.addPaging = function(page, total){
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
		
		// Run initializer
		base.init();
	};

	$.listbox.defaultOptions = {
			id: 1,
			title: "Item Listing",
			isPopup: true,
			locked: false,
			page: 1,
			pageSize: 5,
			emptyText: "No item available.",
			idField: "ruleId",
			nameField: "ruleName",
			parentNameField: "ruleName",
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
