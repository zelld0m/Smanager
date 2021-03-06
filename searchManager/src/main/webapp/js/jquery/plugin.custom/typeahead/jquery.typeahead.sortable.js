(function($){
	
	$.typeaheadsortable = function(el, options) {
		var base = this;
		
		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("typeaheadsortable", base);
		
		base.options = $.extend({},$.typeaheadsortable.defaultOptions, options);
		
		var editable = (base.options.editable == true);
		
		base.init = function() {
			var self = this;
			self.$el.find('div.sortDiv').children('ul').sortable({handle:'.dragHandler'});
			self.initializeElevatedItems();
			self.initializeSortableItems(self.$el.find(base.options.itemSelector));
		};
		
		base.initializeSortableItems = function($itemList) {
			var self = this;
			
			$itemList.each(self.initializeSortableIcons);
		};
		
		base.initializeSortableIcons = function() {
			var $item = $(this);
			base.addSortableEvents($item);
		};
		
		base.initializeElevatedItems = function() {
			var self = this;
			self.$el.find(base.options.sortedItemSelector).each(function() {
				var $item = $(this);
				self.addItemEvent($item);
				$item.remove();
			});
		};
		
		base.addSortableEvents = function($item) {
			var self = this;
			var $itemSpan = $item.find('span');
			$itemSpan.addClass('floatL').addClass('w80p');
			$item.addClass('div_'+$itemSpan.text().split(' ').join('_').replace(/[^\w\s]/gi, ''));
			if(editable) {
				$item.append('<div class="floatR"><a href="javascript:void(0);" class="elevateButton">'+base.options.elevateIcon+'</a></div>');
				
				$item.find('a.elevateButton').on("click", function() {
					self.addItemEvent($item);
				});
			}
		};
		
		base.addItemEvent = function($item) {
			$item.hide();
			
			var $sortableList = $item.parent().siblings('div.sortDiv').find('ul');
			
			var html = '<li class="padB5 w100p"><span class="floatL w80p">'+$item.find('span').html()+'</span>';
			if(editable) {
				html += '<div class="floatR padR5"><a href="javascript:void(0)" class="deleteHandler">'+base.options.deleteIcon+'</a>&nbsp;<a href="javascript:void(0);" class="dragHandler">'+base.options.dragIcon+'</a></div>';
			}
			html += '</li>';
			
			$sortableList.append(html);
			
			$deleteIconList = $sortableList.find('a.deleteHandler');
			$deleteIconList.each(function() {
				var $deleteLink = $(this);
				
				$deleteLink.off().on("click", function() {
					var text = $deleteLink.parent().siblings('span').text();
					$deleteLink.parent().parent().remove();
					base.$el.find('.div_'+text.split(' ').join('_').replace(/[^\w\s]/gi, '')).show();
				});
			});
			
			$sortableList.sortable('refresh');
		};
		
		// Run initializer
		base.init();
	};
	
	$.typeaheadsortable.defaultOptions = {
			elevateIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/page_white_get.png'/>",
			deleteIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/btn_delete_big.png'/>",
			dragIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_drag.png'/>",
	};
	
	$.fn.typeaheadsortable = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.typeaheadsortable(this, options));
			});
		};
	};
})(jQuery);