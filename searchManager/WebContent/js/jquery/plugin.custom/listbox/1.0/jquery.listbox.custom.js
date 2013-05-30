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

	$.listbox.prototype.setId = function(ui, id) {
		var base= this;

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
						base.setId(base.$el);
						base.getList(base.options.page);
					}
				}
			});
		} else {
			base.$el.html(base.getTemplate());
			base.setId(base.$el);
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

	$.listbox.prototype.getList = function(page) {
		var base = this;
		base.options.page = page;
		base.options.itemDataCallback(base, page);
	};

	$.listbox.prototype.prepareList = function(){
		var base = this;
		base.$el.find("#itemHolder > div:not(#itemPattern), #itemPagingTop, #itemPagingBottom").empty();
		base.$el.find("div#emptyText").hide();
		base.$el.find("div#preloader").show();
	};

	$.listbox.prototype.reposition = function() {
		var base = this;
		base.api && base.api.reposition();
	};

	$.listbox.prototype.populateList = function(data){
		var base = this;

		base.$el.find('#preloader, #emptyText').hide();
		base.$el.find('#itemHolder > div:not(#itemPattern)').remove();

		if(data && data["totalSize"] > 0){
			var itemHolder = base.$el.find('#itemHolder');

			for (var i = 0; i < data["totalSize"]; i++) {

				var item = data["list"][i];
				console.log(itemHolder);
				var ui = itemHolder.find('#itemPattern').clone();
				ui.prop({
					id: item[base.options.idField]
				});
				base.populateItemFields(ui, item);
				ui.show();
				itemHolder.append(ui);
			}

			itemHolder.find('div:nth-child(even)').addClass("alt");
		}else{
			base.$el.find('div#emptyText').show();
			return;
		}
	};

	$.listbox.prototype.populateItemFields = function(ui, item){
		var base = this;
		
		if(item[base.options.nameField] !== base.options.rule[base.options.nameField]){
			ui.find(".deleteIcon").off().on({
				click: function(e){
					jConfirm("Delete " + e.data.base.options.parentNameText + " in " + e.data.item[base.options.nameField] + "?", "Linked Keyword", function(result){
						if(result) base.itemDeleteCallback(base, e.data.item, e.data.parentItem);
					});
				}
			}, {item: item, base: base, parentItem: base.options.ruleItem});
		}

		ui.find(".itemName").text(item[base.options.nameField]);
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