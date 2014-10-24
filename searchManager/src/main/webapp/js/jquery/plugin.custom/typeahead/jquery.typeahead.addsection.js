(function($){
	
	$.typeaheadaddsection = function(el, options) {
		var base = this;
		
		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("typeaheadaddsection", base);
		
		base.options = $.extend({},$.typeaheadsortable.defaultOptions, options);
		
		base.init = function() {
			var self = this;
			self.initializeAddEvent();
		};
		
		base.initializeAddEvent = function() {
			var self = this;
			var $sectionTable = self.$el.find('table#section');
			
			$sectionTable.find('a#btnAddSection').off().on('click', function() {
				var inputValue = $sectionTable.find('input').val();
				if(!$.isNotBlank(inputValue)) {
					jAlert("Section Name cannot be empty.", base.options.moduleName);
					return;
				}
				
				if($sectionTable.find('div#sectionBox').find('div:contains('+inputValue+')').size() > 0) {
					jAlert("Section Name already taken.", base.options.moduleName);
					return;
				}
				
				var $newSection = $(self.getSectionTableTemplate());
				$newSection.find('div.sectionName').html($sectionTable.find('input').val());
				$sectionTable.find('div#sectionBox').append($newSection);
			});
		};
		
		base.getSectionTableTemplate = function() {
			var html = '';
			
			html +=	'					<table id="sectionTemplate" class="tblItems marL8 marT15 marB10 sectionTable">';
			html +=	'						<tr>';
			html +=	'							<td class="pad1" valign="bottom">';
			html +=	'								<div class="floatL marT5 sectionName">Dynamic Section</div>';
			html +=	'								<div class="floatL marT3"><input type="checkbox"/></div>';
			html +=	'								<div class="floatR">';
			html +=	'									<input type="text" class="w150 marB6"/>';
			html +=	'									<img class="padT5" src="'+GLOBAL_contextPath+'/images/add.png"/>';
			html +=	'									<img class="padL5 marT6" src="'+GLOBAL_contextPath+'/images/icon_delete2.png"/>';
			html +=	'								</div>';
			html +=	'							</td>';
			html +=	'						</tr>';
			html +=	'						<tr>';
			html +=	'							<td class="w650">';
			html +=	'								&nbsp;';
			html +=	'							</td>';
			html +=	'						</tr>';
			html +=	'					</table>';
			
			return html;
		};
		
		base.init();
	};
	
	$.typeaheadaddsection.defaultOptions = {
			elevateIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/page_white_get.png'/>",
			deleteIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/btn_delete_big.png'/>",
			dragIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_drag.png'/>",
	};
	
	$.fn.typeaheadaddsection = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.typeaheadaddsection(this, options));
			});
		};
	};
})(jQuery);