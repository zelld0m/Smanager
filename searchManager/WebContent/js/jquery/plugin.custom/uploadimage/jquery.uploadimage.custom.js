(function($){

	$.uploadimage = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("uploadimage", base);
		
		base.setId = function(ui,id){
			ui.find("div:first").prop({
				id: $.isNotBlank(id)? id: "plugin-uploadimage-" + base.options.id
			});
		};
		
		base.init = function(){
			base.options = $.extend({},$.addproduct.defaultOptions, options);
			
			if(base.options.isPopup){
				base.$el.qtip({
					id: "plugin-uploadimage-qtip",
					content: {
						text: $('<div/>'),
						title: {text: "Upload Image", button: true }
					},
					position: 'center',
					show:{
						modal: true
					},
					style: {
						width: 'auto'
					},
					events: { 
						show: function(event, api){
							base.api = api;
							base.$el = $("div", api.elements.content);
							base.$el.empty().append(base.getTemplate());
							base.setId(base.$el);
							base.populateFieldsValue();
						}
					}
				});
			}else{
				base.$el.empty().append(base.getTemplate());
				base.setId(base.$el);
				base.populateFieldsValue();
			}
			
		};

		base.populateFieldsValue = function(){
			if(base.options.rule!=null){
				var rule = base.options.rule; 
				base.$el.find("input#imagePath").val(rule["imagePath"]);
				base.$el.find("input#linkPath").val(rule["linkPath"]);
				base.$el.find("input#imageAlt").val(rule["imageAlt"]);
				base.previewImageURL(base.$el, rule["imagePath"]);
			}
			base.registerEventListener(base.options.rule);
		};
		
		base.registerEventListener = function(){
			base.imagePathListener();
			base.buttonListener();
		};
		
		base.buttonListener = function(){
			
			base.$el.find("a.buttons").off().on({
				click: function(e){
					switch($(e.currentTarget).prop("id")){
					case "okButton": 
						var rule = e.data.base.options.rule;
						
						if(rule!=null){
							e.data["imagePath"] = e.data.base.$el.find("input#imagePath").val();
							e.data["linkPath"] = e.data.base.$el.find("input#linkPath").val();
							e.data["imageAlt"] = e.data.base.$el.find("input#imageAlt").val();
						} 
						
						if(e.data["imagePath"]!= rule["imagePath"] ||
						   e.data["linkPath"] != rule["linkPath"]  ||
						   e.data["imageAlt"] != rule["imageAlt"]){
							e.data.base.api.hide(); 
							e.data.base.options.imageChangeCallback(e);
						}
						
						break;
					case "cancelButton": 
						e.data.base.api.hide(); 
						break;
					}
				}
			}, {base: base});
		};
		
		base.imagePathListener = function(){
			var $input = base.$el.find("input#imagePath");
			var rule = base.options.rule;
			
			$input.off().on({
				mouseenter: function(e){
					if(e.data.locked){
						showHoverInfo;
					}
					else{
						e.data.input = $.trim($(e.currentTarget).val());
					}
				},
				focusin: function(e){
					if(e.data.locked){
						showHoverInfo;
					}
					else{
						e.data.input = $.trim($(e.currentTarget).val());
					}
				},
				mouseleave: function(e){
					if (e.data.locked) return;
					
					if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()){
						base.previewImageURL(e.data.ui, $(e.currentTarget).val());
					}
				},
				focusout: function(e){
					if (e.data.locked) return;
					
					if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()){
						base.previewImageURL(e.data.ui, $(e.currentTarget).val());
					}
				}
			},{ui: base.$el , locked: base.options.isLocked, input: ""});
		};
		
		base.previewImageURL = function(ui, imagePath){
			var $previewHolder = ui.find("#preview");
			
			if($.isBlank(imagePath)){
				imagePath = base.options.noPreviewImage;
			}
			
			$previewHolder.find("span.preloader").show();
			
			setTimeout(function(e){
				$previewHolder.find("img#imagePreview").prop("src",imagePath).off().on({
					error:function(){ 
						$(this).unbind("error").prop("src", base.options.noPreviewImage); 
					}
				});
			},10);
			
			$previewHolder.find("span.preloader").hide();
		},
		
		base.getTemplate = function(){
			var template = '';

			template += '<div class="plugin-uploadimage">';
			template += '	<div id="preview">';
			template += '		<span id="preloader" style="display:none"></span>';
			template += '		<img id="imagePreview" src="' + GLOBAL_contextPath + '/images/nopreview.png" onerror="this.onerror=null; this.src=\'' + GLOBAL_contextPath + '/images/nopreview.png\';"/>';
			template += '	</div>';
			template += '	<div>';
			template += '		<span class="fieldLabel"><label for="imagePath">Image Path</label></span>';
			template += '		<span>';
			template += '			<div>';
			template += '				<span>';
			template += '					<input id="imagePath" type="text">';
			template += '				</span>';
			template += '			</div>';
			template += '		</span>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div class="floatL">';
			template += '		<span class="fieldLabel"><label for="imageAlt">Image Alt</label></span>';
			template += '		<span><input id="imageAlt" type="text"></span>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div class="floatL">';
			template += '		<span class="fieldLabel"><label for="linkPath">Link Path</label></span>';
			template += '		<span><input id="linkPath" type="text"></span>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="buttonset">';
			template += '		<a id="okButton" class="buttons btnGray clearfix" href="javascript:void(0);">';
			template += '			<div class="buttons fontBold">OK</div>';
			template += '		</a>';
			template += '		<a id="cancelButton" class="buttons btnGray clearfix" href="javascript:void(0);">';
			template += '			<div class="buttons fontBold">Cancel</div>';
			template += '		</a>';
			template += '	</div>';
			template += '</div>';
			
			return template;
		};

		// Run initializer
		base.init();
	},

	$.uploadimage.defaultOptions = {
			id: 1,
			rule: null,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",
			imageChangeCallback: function(e){},
			isPopup: false,
			isLocked: false,
	};

	$.fn.uploadimage = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.uploadimage(this, options));
			});
		};
	};

})(jQuery);