(function($){

	$.download = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("download", base);

		base.init = function(){
			base.options = $.extend({},$.download.defaultOptions, options);

			base.$el.off().on({
				click: base.showDownloadOption()
			});
		};
		
		base.getTemplate = function(){
			var template = '<div>';
			template += '<label class="text60 marT6">Filename: </label>';
			template += '<label class="marT6"><input type="text" id="filename" name="filename" class="w163"></label>';
			template += '<div class="clearB"></div>';
			
			if(base.options.hasPageOption){
				template += '<label class="text60 marT6">Pages: </label>';
				template += '<label class="marT6">';
				template += '<select id="page" name="page" class="mar0 w168">';
				template += '<option value="all">All</option>';
				template += '<option value="current" selected="selected">Current</option>';
				template += '</select>';
				template += '</label>';
				template += '<div class="clearB marT6"></div>';
			}
			
			template += '<label class="text60">Type: </label>';
			template += '<label class="marT6">';
			template += '<select id="type" name="type" disabled="disabled" class="mar0 w168">';
			template += '<option value="excel" selected="selected">Excel</option>';
			template += '<option value="pdf">PDF</option>';
			template += '<option value="csv">CSV</option>';
			template += '</select>';
			template += '</label>';
			template += '<div class="clearB marT8 txtAR">';
			template += '<a id="downloadBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Download</div></a>';
			template += '</div>';
			template += '</div>';
			return template;
		};

		base.showDownloadOption = function(){
			base.$el.qtip({
				content: {
					text: $('<div/>'),
					title: { 
						text: base.options.headerText, 
						button: true
					}
				},
				position:{
					at: 'top center',
					my: 'bottom center'
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						var $content = $("div", api.elements.content);
						$content.html(base.getTemplate());
						
						$content.find("a#downloadBtn").off().on({
							click: function(e){
								e.data = {
										  filename: $.trim($content.find("input#filename").val()),
										  page: $content.find('select#page option:selected').val(),
										  type: $content.find('select#type option:selected').val()
										};
								
								if ($.isBlank(e.data.filename) || ($.isNotBlank(e.data.filename) && isXSSSafe(e.data.filename))){
									e.data.filename = $.formatAsId(e.data.filename);
									base.options.requestCallback(e);
								}else{
									alert("Please provide a valid filename");
								}
							}
						});
					}
				}
			});
		};
		
		// Run initializer
		base.init();
	};
	
	$.download.defaultOptions = {
			headerText:"Download",
			hasPageOption: false,
			requestCallback: function(e){} 
	};

	$.fn.download = function(options){

		if (this.length) {
			return this.each(function() {
				(new $.download(this, options));
			});
		};
	};
})(jQuery);
