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
			base.imagePathId = "";

			if(base.options.isPopup) {
				base.$el.qtip({
					id: "plugin-addbanner-qtip",
					content: {
						text: $('<div/>'),
						title: {text: base.getLabel(), button: true }
					},
					position: {
						my: 'center',
						at: 'center',
						target: $(window)
					},
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

			// at any mode, make sure alias field is always readonly and disabled
			base.$el
			.find('input#imageAlias').prop({
				readonly: true,
				disabled: true
			});

			if(base.options.ruleItem != null) {
				var ruleItem = base.options.ruleItem;

				base.$el.find('input#startDate').val(ruleItem['startDate']).end()
				.find('input#endDate').val(ruleItem['endDate']).end()
				.find('input#imagePath').val(ruleItem['imagePath']['path']).end()
				.find('input#imageAlias').val(ruleItem['imagePath']['alias']).end()
				.find('input#imageAlt').val(ruleItem['imageAlt']).end()
				.find('input#linkPath').val(ruleItem['linkPath']).end()
				.find('textarea#description').val(ruleItem['description']).end();

				base.previewImage(base.$el, ruleItem['imagePath']['path']);

				if(base.options.mode){
					switch(base.options.mode.toLowerCase()){
					case 'copy': 
						// Do not allow modification of image path and alias
						base.$el.find('input#imagePath').prop({
							readonly: true,
							disabled: true
						});

						//TODO:
						if(ruleItem['startDate'] && new Date(ruleItem['startDate']).getTime() < new Date().getTime() ) {
							var date = new Date();
							var day = date.getDate();
							var month = date.getMonth()+1;
							var year = date.getFullYear();
							if(day<10){day='0'+day;}
							if(month<10){month='0'+month;}
							base.$el.find('input#startDate').val(month+'/'+day+'/'+year);
						}
						if(ruleItem['endDate'] && new Date(ruleItem['endDate']).getTime() < new Date().getTime() ) {
							var date = new Date();
							var day = date.getDate();
							var month = date.getMonth()+1;
							var year = date.getFullYear();
							if(day<10) {day='0'+day;}
							if(month<10){month='0'+month;}
							base.$el.find('input#endDate').val(month+'/'+day+'/'+year);
						}
						break;
					case 'add': 
						break;
					};
				}
			}

			// Select a date range
			base.$el
			.find("#startDate").prop({ id: "startDate" + base.options.rule["ruleId"]}).datepicker({
				defaultDate: "+1w",
				changeMonth: true,
				changeYear: true,
				showOn: "both",
				buttonImage: "../images/icon_calendar.png",
				onClose: function(selectedDate) {
					base.$el.find("#endDate" + base.options.rule["ruleId"]).datepicker("option", "minDate", selectedDate);
				}
			}).end()

			.find("#endDate").prop({ id: "endDate" + base.options.rule["ruleId"]}).datepicker({
				defaultDate: "+1w",
				changeMonth: true,
				changeYear: true,
				showOn: "both",
				buttonImage: "../images/icon_calendar.png",
				onClose: function(selectedDate) {
					base.$el.find("#startDate" + base.options.rule["ruleId"]).datepicker("option", "maxDate", selectedDate);
				}
			});

			base.registerEventListener();
		};

		base.populateImageAlias = function(ui, item) {
			// TODO
		};

		base.registerEventListener = function(){
			base.addInputFieldListener(base.$el.find("input#imagePath"), base.getImagePath);
			base.addInputFieldListener(base.$el.find("input#linkPath"), base.validateLinkPath);
			base.buttonListener();
		};

		base.validateLinkPath = function(ui, linkPath){
			//Validate if valid url
			
			
			//Check for 200 response
			$.ajax({ 
				type: "GET", 
				url: linkPath, 
				async: false,
				crossDomain: true,
				statusCode: {
					200: function() {
						alert("Valid URL");
					}
				}
			});
		};

		base.buttonListener = function() {	
			base.$el.find("a.buttons").off().on({
				click: function(e){
					switch($(e.currentTarget).prop("id")){
					case "okButton":
						var startDate = e.data.base.$el.find('input.startDate').val();
						var endDate = e.data.base.$el.find('input.endDate').val();
						var imagePath = e.data.base.$el.find('input#imagePath').val();
						var imageAlias = e.data.base.$el.find('input#imageAlias').val();
						var imageAlt = e.data.base.$el.find('input#imageAlt').val();
						var linkPath = e.data.base.$el.find('input#linkPath').val();
						var description = e.data.base.$el.find('textarea#description').val();
						//var keyword = e.data.base.$el.find('textarea#keyword').val();

						if($.isBlank(imagePath)) {
							jAlert("Image path is required.", "Banner");
						} else if($.isBlank(imageAlias)) {
							jAlert("Image alias is required.", "Banner");
						} else if($.isBlank(imageAlt)) {
							jAlert("Image alt is required.", "Banner");
						} else if($.isBlank(linkPath)) {
							jAlert("Link path is required.", "Banner");
						} 

						/*
						else if($.isBlank(keyword)) {
							jAlert("Keyword is required.", "Banner");
						}*/ 

						/* Datepicker will handle validation
						else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
							jAlert("Please provide a valid date range.", "Banner");
						} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
							jAlert("End date cannot be earlier than start date.", "Banner");
						}*/

						else if ($.isNotBlank(description) && !validateDescription("Description", description, 1, 150)) {
							// error alert in function validateComment
						} else if(!base.validateLinkPath()) {
							jAlert("Link path is invalid.", "Banner");
						} else {
							e.data['ruleId'] = base.options.rule["ruleId"];
							e.data['startDate'] = startDate;
							e.data['endDate'] = endDate;
							e.data['imagePathId'] = base.imagePathId;
							e.data['imagePath'] = imagePath;
							e.data['imageAlias'] = imageAlias;
							e.data['imageAlt'] = imageAlt;
							e.data['linkPath'] = linkPath;
							e.data['description'] = description;
							//e.data['keyword'] = keyword;
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

		base.addInputFieldListener = function(input, callback) {
			input.off().on({
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

					if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase() && e.data.sendRequest!=="true") {
						e.data.sendRequest = "true";
						callback(e.data.ui, $(e.currentTarget).val());
					}
				},
				focusout: function(e) {
					if (e.data.locked) return;

					if(e.data.input.toLowerCase() !== $.trim($(e.currentTarget).val()).toLowerCase() && e.data.sendRequest!=="true") {
						e.data.sendRequest = "true";
						callback(e.data.ui, $(e.currentTarget).val());
					}
				}
			}, {ui: base.$el , locked: base.options.isLocked, input: "", sendRequest: ""});
		};

		base.getImagePath = function(ui, imagePath) {
			var $previewHolder = ui.find("#preview");

			BannerServiceJS.getImagePath(imagePath, {
				callback: function(sr){
					if (sr!=null && sr["data"]!=null){
						var iPath = sr["data"];
						base.imagePathId = iPath["id"];

						ui.find("#imageAlias").prop({
							readonly: true,
							disabled: true
						}).val(iPath["alias"]);

					}else{
						ui.find("#imageAlias").prop({
							readonly: false,
							disabled: false
						});
					}
				},
				preHook: function(e){
					$previewHolder.find("span.preloader").show();
					ui.find("#imageAlias").val("");
				},
				postHook: function(e){
					$previewHolder.find("span.preloader").hide();
					base.previewImage(ui, imagePath);
				}
			});
		};


		base.previewImage = function(ui, imagePath) {
			var $previewHolder = ui.find("#preview");

			if($.isBlank(imagePath)) {
				imagePath = base.options.noPreviewImage;
			}

			$previewHolder.find("img#imagePreview").attr("src",imagePath).off().on({
				error:function(){ 
					$(this).unbind("error").attr("src", base.options.noPreviewImage); 
				}
			});
		};

		base.getTemplate = function() {
			var template = '';

			template += '<div class="plugin-addbanner">';
			template += '	<div id="preview">';
			template += '		<div id="preloader" class="circlePreloader" style="display:none">';
			template += '			<img src="' + GLOBAL_contextPath + '/images/ajax-loader-circ.gif" />';
			template += '		</div>';
			template += '		<img id="imagePreview" src="' + GLOBAL_contextPath + '/images/nopreview.png" onError="this.onerror=null;this.src=\'' + GLOBAL_contextPath + '/images/nopreview.png\';" />';
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
			template += '			<label><input id="startDate" class="startDate" type="text"></label>';
			template += ' 			<label> - </label>';
			template += '			<label><input id="endDate" class="endDate"  type="text"></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '		<div class="floatL">';
			template += '			<label class="txtLabel">Description: </label> ';
			template += '			<label><textarea id="description"></textarea></label>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
//			template += '		<div class="floatL">';
//			template += '			<label class="txtLabel">Keyword: </label> ';
//			template += '			<label><textarea id="keyword"></textarea></label>';
//			template += '		</div>';
//			template += '		<div class="clearB"></div>';

			if (!base.options.isLocked) {
				var type = 'Add';

				if(base.options.ruleItem) {
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
			switch(base.options.mode.toLowerCase()) {
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

		// Run initializer
		base.init();
	},

	$.addbanner.defaultOptions = {
			id: 1,
			rule: null,
			ruleItem: null,
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