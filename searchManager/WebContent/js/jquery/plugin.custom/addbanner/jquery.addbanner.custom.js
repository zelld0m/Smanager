(function($){
	
	$.addbanner = function(el, options) {
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;
		
		// Access to jQuery and DOM of element
		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("addbanner", base);
		
		base.setId = function(ui, id) {
			ui.find("div:first").prop({
				id: $.isNotBlank(id)? id: "plugin-addbanner-" + base.options.id
			});
		};
		
		base.init = function() {
			base.options = $.extend({}, $.addproduct.defaultOptions, options);
			
			if(base.options.isPopup) {
				base.$el.qtip({
					id: "plugin-addbanner-qtip",
					content: {
						text: $('<div/>'),
						title: {text: base.getLabel(), button: true }
					},
					position: 'center',
					show: {
						modal: true
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
							base.populateContents();
						}
					}
				});
			} else {
				base.$el.empty().append(base.getTemplate());
				base.setId(base.$el);
				base.populateContents();
			}
		};
		
		base.populateContents = function() {
			if(base.options.rule != null) {
				var rule = base.options.rule;
				base.$el.find('input#startDate').val(rule['startDate']);
				base.$el.find('input#endDate').val(rule['endDate']);
				base.$el.find('input#imagePath').val(rule['imagePath']['path']);
				base.$el.find('input#imageAlias').val(rule['imagePath']['alias']);
				base.$el.find('input#imageAlt').val(rule['imageAlt']);
				base.$el.find('input#linkPath').val(rule['linkPath']);
				base.$el.find('textarea#description').val(rule['description']);
				// base.$el.find('textarea#keyword').val(rule['keyword']);
				base.previewImage(base.$el, rule['imagePath']['path']);
				
				if(base.options.mode && base.options.mode.toLowerCase() == 'copy') {
					base.$el.find('input#imagePath').attr('readonly', true).attr('disabled', '');
					base.$el.find('input#imageAlias').attr('readonly', true).attr('disabled', '');
					
					if(rule['startDate'] && new Date(rule['startDate']).getTime() < new Date().getTime() ) {
						var date = new Date();
						var day = date.getDate();
						var month = date.getMonth()+1;
						var year = date.getFullYear();
						if(day<10){day='0'+day;}
						if(month<10){month='0'+month;}
						base.$el.find('input#startDate').val(month+'/'+day+'/'+year);
					}
					if(rule['endDate'] && new Date(rule['endDate']).getTime() < new Date().getTime() ) {
						var date = new Date();
						var day = date.getDate();
						var month = date.getMonth()+1;
						var year = date.getFullYear();
						if(day<10) {day='0'+day;}
						if(month<10){month='0'+month;}
						base.$el.find('input#endDate').val(month+'/'+day+'/'+year);
					}
				}
			}
			
			var popDates = base.$el.find("input#startDate, input#endDate").prop({readonly: false}).datepicker({
				minDate: 0,
				maxDate: '+1Y',
				showOn: "both",
				buttonImage: "../images/icon_calendar.png",
				buttonImageOnly: true,
				changeMonth: true,
			    changeYear: true,
				onSelect: function(selectedDate) {
					var option = this.id == "startDate" ? "minDate" : "maxDate",
							instance = $(this).data("datepicker"),
							date = $.datepicker.parseDate(instance.settings.dateFormat ||
									$.datepicker._defaults.dateFormat, selectedDate, instance.settings);
					popDates.not(this).datepicker("option", option, date);
				}
			});
			
			base.registerEventListener(base.options.rule);
		};
		
		base.populateImageAlias = function(ui, item) {
			// TODO
		};
		
		base.registerEventListener = function(){
			base.buttonListener();
			base.previewImageListener();
		};
		
		base.buttonListener = function() {	
			base.$el.find("a.buttons").off().on({
				click: function(e){
					switch($(e.currentTarget).prop("id")){
					case "okButton":
						var rule = e.data.base.options.rule;
						
						var startDate = e.data.base.$el.find('input#startDate').val();
						var endDate = e.data.base.$el.find('input#endDate').val();
						var imagePath = e.data.base.$el.find('input#imagePath').val();
						var imageAlias = e.data.base.$el.find('input#imageAlias').val();
						var imageAlt = e.data.base.$el.find('input#imageAlt').val();
						var linkPath = e.data.base.$el.find('input#linkPath').val();
						var description = e.data.base.$el.find('textarea#description').val();
						var keyword = e.data.base.$el.find('textarea#keyword').val();
						
						if($.isBlank(imagePath)) {
							jAlert("Image path is required.", "Banner");
						} else if($.isBlank(imageAlias)) {
							jAlert("Image alias is required.", "Banner");
						} else if($.isBlank(imageAlt)) {
							jAlert("Image alt is required.", "Banner");
						} else if($.isBlank(linkPath)) {
							jAlert("Link path is required.", "Banner");
						} else if($.isBlank(keyword)) {
							jAlert("Keyword is required.", "Banner");
						} else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
							jAlert("Please provide a valid date range.", "Banner");
						} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
							jAlert("End date cannot be earlier than start date.", "Banner");
						} else if ($.isNotBlank(description) && !validateDescription("Description", description, 1, 150)) {
							// error alert in function validateComment
						} else if(!base.validateLinkPath()) {
							jAlert("Link path is invalid.", "Banner");
						} else {
							e.data['startDate'] = startDate;
							e.data['endDate'] = endDate;
							e.data['imagePath'] = imagePath;
							e.data['imageAlias'] = imageAlias;
							e.data['imageAlt'] = imageAlt;
							e.data['linkPath'] = linkPath;
							e.data['description'] = description;
							e.data['keyword'] = keyword;
							e.data['mode'] = base.options.mode;
							base.options.addBannerCallback(e);
						}
						break;
					case "cancelButton": 
						e.data.base.api.hide(); 
						break;
					}
				}
			}, {base: base});
		};
		
		base.previewImageListener = function() {
			var $input = base.$el.find("input#imagePath");
			$input.off().on({
				mouseenter: function(e) {
					if(e.data.locked) {
						showHoverInfo;
					} else {
						e.data.input = $.trim($(e.currentTarget).val());
					}
				},
				focusin: function(e) {
					if(e.data.locked) {
						showHoverInfo;
					} else {
						e.data.input = $.trim($(e.currentTarget).val());
					}
				},
				mouseleave: function(e) {
					if (e.data.locked) return;
					
					if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()) {
						base.previewImage(e.data.ui, $(e.currentTarget).val());
					}
				},
				focusout: function(e) {
					if (e.data.locked) return;
					
					if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase()) {
						base.previewImage(e.data.ui, $(e.currentTarget).val());
					}
				}
			}, {ui: base.$el , locked: base.options.isLocked, input: ""});
		};
		
		base.previewImage = function(ui, imagePath) {
			var $previewHolder = ui.find("#preview");
			
			if($.isBlank(imagePath)) {
				imagePath = base.options.noPreviewImage;
			}
			
			$previewHolder.find("div#preloader").show();
			
			setTimeout(function(e) {
				$previewHolder.find("img#imagePreview").prop("src", imagePath).off().on({
					error:function() {
						$(this).unbind("error").prop("src", base.options.noPreviewImage);
					}
				});
			}, 10);
			
			$previewHolder.find("div#preloader").hide();
		};
		
		base.getTemplate = function() {
			var template = '';
			
			template += '<div class="plugin-addbanner">';
			template += '	<div id="preview">';
			template += '		<div id="preloader" class="circlePreloader" style="display:none">';
			template += '			<img src="' + GLOBAL_contextPath + '/images/ajax-loader-circ.gif" />';
			template += '		</div>';
			template += '		<img id="imagePreview" src="' + GLOBAL_contextPath + '/images/nopreview.png" onerror="this.onerror=null; this.src=\'' + GLOBAL_contextPath + '/images/nopreview.png\';"/>';
			template += '	</div>';
			template += '	<div id="addItemTemplate" class="mar0">';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Image Path: </label> ';
			template += '			<label><input id="imagePath" type="text"></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Image Alias: </label> ';
			template += '			<label><input id="imageAlias" type="text"></label>';
			template += '			<a id="updateButton" class="buttons btnGray clearfix" href="javascript:void(0);" style="display:none">';
			template += '				<div class="buttons fontBold">Update Alias</div>';
			template += '			</a>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Image Alt: </label> ';
			template += '			<label><input id="imageAlt" type="text"></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Link Path: </label> ';
			template += '			<label><input id="linkPath" type="text"></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Schedule:</label> ';
			template += '			<label><input id="startDate" type="text"></label>';
			template += ' 			<label> - </label>';
			template += '			<label><input id="endDate" type="text"></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Description: </label> ';
			template += '			<label><textarea id="description"></textarea></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Keyword: </label> ';
			template += '			<label><textarea id="keyword"></textarea></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			
			if (!base.options.isLocked) {
				var type = 'Add';
				if(base.options.rule) {
					if(base.options.mode && base.options.mode.toLowerCase() == 'update') {
						type = 'Update';
					} else if(base.options.mode && base.options.mode.toLowerCase() == 'copy') {
						type = 'Copy';
					}
				}
				template += '		<div id="buttonset">';
				template += '			<a id="okButton" class="buttons btnGray clearfix" href="javascript:void(0);">';
				template += '				<div class="buttons fontBold">' + type + '</div>';
				template += '			</a>';
				template += '			<a id="cancelButton" class="buttons btnGray clearfix" href="javascript:void(0);">';
				template += '				<div class="buttons fontBold">Cancel</div>';
				template += '			</a>';
				template += '		</div>';
			}
			
			template += '	</div>';
			template += '</div>';

			return template;
		};
		
		base.getLabel = function() {
			switch(base.options.mode) {
			case 'add':
				return 'Add';
			case 'update':
				return 'Update';
			case 'copy':
				return 'Copy To';
			default:
				return;
			}
		};
		
		base.validateLinkPath = function() {
			// TODO
			return true;
		};
		
		// Run initializer
		base.init();
	},
	
	$.addbanner.defaultOptions = {
			id: 1,
			rule: null,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",
			title: "Add Banner",
			isPopup: false,
			isLocked: false,
			mode: "Add",
			addBannerCallback: function(e){},
			imageAliasCallback: function(base, imagePath){}
	};
	
	$.fn.addbanner = function(options) {
		if (this.length) {
			return this.each(function() {
				(new $.addbanner(this, options));
			});
		};
	};
	
})(jQuery);