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

			var fileFormat = base.options.fileFormat;
			
			if (base.options.sendMail) fileFormat.push("Mail");
			
			base.options.fileFormat = fileFormat;
			
			base.$el.off().on({
				click: base.showDownloadOption()
			});
		};
		
		base.getTemplate = function(){
			var template = '<div>';
			template += '<div><span class="floatL marT10 marR5" >Filename: </span>';
			template += '<span><input type="text" id="filename" name="filename" class="w163 marT6 floatR"></span>';
			template += '<div class="clearB"></div></div>';
			
			if(base.options.hasPageOption){
				template += '<div><span class="floatL marT10 marR5">Pages: </span>';
				template += '<span>';
				template += '<select id="page" name="page" class="mar0 w168 floatR marT6">';
				template += '<option value="all">All</option>';
				template += '<option value="current" selected="selected">Current</option>';
				template += '</select>';
				template += '</span></div>';
				template += '<div class="clearB"></div>';
			}
			
			template += '<div><span class="floatL marT10 marR5">Type: </span>';
			template += '<span>';
			template += '<select id="type" name="type" class="mar0 w168 floatR marT6">';
			
			for (var i=0; i < base.options.fileFormat.length; i++){
				template += '<option value="' +  base.options.fileFormat[i].toLowerCase() + '">' + base.options.fileFormat[i] + '</option>';
			}
			
			template += '</select>';
			template += '</span></div>';
			template += '<div class="clearB marT8 txtAR">';
			template += '<div id="mailrecipient"><span class="floatL marT10 marR5" >Recipient: </span>';	
			template += '<span><textarea id="recipient" class="w163 marT6 floatR"></textarea></span>';
			template += '<div class="clearB"></div></div>';
			template += '<a id="downloadBtn" href="javascript:void(0);" class="buttons btnGray clearfix marT8"><div class="buttons fontBold">Download</div></a>';
			template += '<a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix marT8"><div class="buttons fontBold">Clear</div></a>';
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
					my: 'bottom center',
					target: base.$el
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						var $content = $("div", api.elements.content);
						$content.html(base.getTemplate());
						
						if ($content.find("select#type:first").val().toLowerCase()==="mail"){
							$content.find("div#mailrecipient").show();
						}else{
							$content.find("div#mailrecipient").hide();
						}
						
						$content.find("select#type").on({
							change: function(e){
								if($(this).val().toLowerCase()==="mail"){
									$content.find("div#mailrecipient").show();
								}else{
									$content.find("div#mailrecipient").hide();
								}
								$content.find("textarea#recipient").val("");
							}
						});
						
						$content.find("a#downloadBtn").off().on({
							click: function(e){
								e.data = {
										  filename: $.trim($content.find("input#filename").val()),
										  recipient: $.trim($content.find("textarea#recipient").val()),
										  page: $content.find('select#page option:selected').val(),
										  type: $content.find('select#type option:selected').val()
										};
								
								if ($.isBlank(e.data.filename) || ($.isNotBlank(e.data.filename) && isAllowedName(e.data.filename))){
									e.data.filename = $.isBlank(e.data.filename)? $.trim(base.options.defaultFilename) : $.formatAsId(e.data.filename).substring(1);
								}else{
									alert("Please provide a valid filename");
									return;
								}
								
								//TODO: add check valid email and input cleaning
								if (e.data.type.toLowerCase()==="mail" && $.isNotBlank(e.data.recipient)){ 
									
								}else{
									alert("Please provide at least one email recipient");
									return;
								}
										
								base.options.requestCallback(e);
							}
						});
						
						$content.find("a#clearBtn").off().on({
							click: function(e){
								$content.find("input,textarea").val("");
								$content.find("select").prop("selectedIndex",0);
								if ($content.find("select#type:first").val().toLowerCase()==="mail"){
									$content.find("div#mailrecipient").show();
								}else{
									$content.find("div#mailrecipient").hide();
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
			defaultFilename:new Date().getTime(),
			fileFormat: ['Excel'],
			sendMail: false,
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
