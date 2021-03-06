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

		base.options = $.extend({}, $.addbanner.defaultOptions, options);

		// Run initializer
		base.init();
	};

	$.addbanner.prototype.setId = function(id) {
		var base = this;
		var ui = base.$el;
		
		ui.find("div:first").prop({
			id: $.isNotBlank(id)? id: "plugin-addbanner-" + base.options.id
		});
	};

	$.addbanner.prototype.init = function() {
		var base = this;
		
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
					render: function(event, api) {
						base.api = api;
						base.$el = $("div", api.elements.content);
					},

					show: function(event, api){
						base.$el.empty().append(base.getTemplate());
						base.setId();
						base.populateContents.call(base);
					}
				}
			});
		} else {
			base.$el.empty().append(base.getTemplate());
			base.setId();
			base.populateContents.call(base);
		}
	};

	$.addbanner.prototype.populateContents = function() {
		var base = this;
		var ui = base.$el;
		
		// at any mode, make sure alias field is always readonly and disabled
		ui.find('.imageAlias').prop({
			readonly: true,
			disabled: true
		}).removeProp("id");

		if(base.options.ruleItem != null) {
			var ruleItem = base.options.ruleItem;

			ui.find('input#startDate').val(ruleItem['formattedStartDate']).end()
			.find('input#endDate').val(ruleItem['formattedEndDate']).end()
			.find('input#imagePath').val(ruleItem['imagePath']['path']).attr("data-size", ruleItem['imagePath']['size']).end()
			.find('input.imageAlias').val(ruleItem['imagePath']['alias']).prop({
				id: ruleItem['imagePath']['id']
			}).end()
			.find('input#imageAlt').val(ruleItem['imageAlt']).end()
			.find('input#linkPath').val(ruleItem['linkPath']).attr("data-valid", true).end()
			.find('textarea#description').val(ruleItem['description']).end()
			.find('#temporaryDisable').prop({
				checked: ruleItem["disabled"] == true
			}).end()
			.find('#openNewWindow').prop({
				checked: ruleItem["openNewWindow"] == true
			});
			
			base.previewImage(ruleItem['imagePath']['path']);

			if(base.options.mode){
				switch(base.options.mode.toLowerCase()){
				case 'copy': 
					// Do not allow modification of image path and alias
					base.$el.find('input#imagePath').prop({
						readonly: true,
						disabled: true
					});
					break;
				case 'add': 
					break;
				};
			}
		}

		// Select a date range
		ui.find("#startDate").prop({ id: "startDate_" + base.options.rule["ruleId"]}).datepicker({
			minDate: GLOBAL_currentDate,
			defaultDate: GLOBAL_currentDate,
			changeMonth: true,
			changeYear: true,
			showOn: "both",
			buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
			buttonImageOnly: true,
			buttonText: "Select start date",
			onClose: function(selectedDate) {
				ui.find("#endDate_" + base.options.rule["ruleId"]).datepicker("option", "minDate", selectedDate);
			}
		}).end()

		.find("#endDate").prop({ id: "endDate_" + base.options.rule["ruleId"]}).datepicker({
			minDate: base.$el.find("#startDate_" + base.options.rule["ruleId"]).datepicker("getDate"),
			defaultDate: GLOBAL_currentDate,
			changeMonth: true,
			changeYear: true,
			showOn: "both",
			buttonImage: "../images/icon_calendar.png",
			buttonImageOnly: true,
			buttonText: "Select end date",
			onClose: function(selectedDate) {
				if(!ui.find("#startDate_" + base.options.rule["ruleId"]).datepicker("isDisabled")){
					ui.find("#startDate_" + base.options.rule["ruleId"]).datepicker("option", "maxDate", selectedDate);
				}
			}
		});

		base.registerEventListener();
	};

	$.addbanner.prototype.reposition = function() {
		var base = this;
		base.api && base.api.reposition();
	};

	$.addbanner.prototype.registerEventListener = function(){
		var base = this;
		var ui = base.$el;
		
		base.reposition();
		base.addInputFieldListener("input#imagePath", ui.find("#imagePath").val(), base.getImagePath);
		base.addInputFieldListener("input#linkPath", ui.find("#linkPath").val(), base.validateLinkPath);
		base.addButtonListener();
	};
	
	$.addbanner.prototype.validateLinkPath = function(linkPath){
		var base = this;
		var ui = base.$el;
		
		var validURL = false;
		ui.find("#linkPath").attr("data-valid", false);

		if($.isNotBlank(linkPath)){
			if(!$.startsWith(linkPath,"//")){
				jAlert("Link path value must start with //", "Banner");
				return;
			}
			
			var url = GLOBAL_storeDefaultBannerLinkPathProtocol + ':' + linkPath;
			
			if(!$.isValidURL(url)){
				jAlert("Please specify a valid link path", "Banner");
				return;
			}
	
			var $a = $('<a/>');
			$a.attr("href", url);
			var hostname = $a[0]["hostname"];
			
			for(var i = 0; i < GLOBAL_storeDomains.length; i++){
				if($.isNotBlank(GLOBAL_storeDomains[i]) && $.endsWith(hostname, GLOBAL_storeDomains[i])){
					var hostnamePrefix = hostname.replace(GLOBAL_storeDomains[i],'');
					validURL = validURL || $.isBlank(hostnamePrefix);
					
					if($.isNotBlank(hostnamePrefix) && hostnamePrefix !== hostname){
						validURL = validURL || /([A-Z0-9]*\.){0,1}/i.test(hostnamePrefix);
					}
				}
			}
			
			ui.find("#linkPath").attr("data-valid", validURL);

			if(!validURL){
				ui.find("#linkPath").attr("data-valid", "domain");
				jAlert("Only the following domain are allowed value in link path: " + GLOBAL_storeDomains.join(','), "Banner");
			}
			
		}else{
			jAlert("Please specify a link path", "Banner");
		}
		
	};

	$.addbanner.prototype.addButtonListener = function() {	
		var base = this;
		var ui = base.$el;
		
		ui.find(".buttons").off().on({
			click: function(e){
				
				switch($(e.currentTarget).prop("id")){
				case "okButton":
					var startDate = $.trim(e.data.base.$el.find('input.startDate').val());
					var endDate = $.trim(e.data.base.$el.find('input.endDate').val());
					var imagePath = $.trim(e.data.base.$el.find('input#imagePath').val());
					var imagePathId = $.trim(e.data.base.$el.find('input.imageAlias').prop("id"));
					var imageAlias = $.trim(e.data.base.$el.find('input.imageAlias').val());
					var imageAlt = $.trim(e.data.base.$el.find('input#imageAlt').val());
					var linkPath = $.trim(e.data.base.$el.find('input#linkPath').val());
					var description = $.trim(e.data.base.$el.find('textarea#description').val());
					var keywords = $.trim(e.data.base.$el.find('textarea#keyword').val());
					var disable = e.data.base.$el.find('#temporaryDisable').is(":checked");
					var openNewWindow = e.data.base.$el.find('#openNewWindow').is(":checked");
					var imageSize = e.data.base.$el.find('input#imagePath').attr("data-size");
					var isValidURL = e.data.base.$el.find("#linkPath").attr("data-valid")==="true";
					var isRestrictDomain =e.data.base.$el.find("#linkPath").attr("data-valid")==="domain";
					
					var keywordArray = new Array();
					var lines = keywords.split('\n');
					var attemptAddToSelectedRule = false;
					
					$.each(lines, function(ix, el){
						if($.isNotBlank(el) && !$.iequals($.trim(el), base.options.rule["ruleName"])){
							keywordArray.push($.trim(el).toLowerCase());
						}else if(($.isNotBlank(el) && $.iequals($.trim(el), base.options.rule["ruleName"]))){
							attemptAddToSelectedRule = attemptAddToSelectedRule || true;
						}
					});
					
					if($.isBlank(imagePath)) {
						jAlert("Image path is required.", "Banner");
					}else if(imageSize==="error") {
						jAlert("Failed to load the image", "Banner");
					}else if(imageSize==="loading") {
						jAlert("Please wait, image is currently loading", "Banner");
					}else if($.inArray(imageSize, GLOBAL_storeAllowedBannerSizes)==-1) {
						jAlert("Image size " + imageSize + " is not allowed", "Banner");
					} else if($.isBlank(imageAlias)) {
						jAlert("Image alias is required.", "Banner");
					} else if(!isXSSSafe(imageAlias)) {
						jAlert("Invalid image alias. HTML/XSS is not allowed.", "Banner");
					} else if($.isBlank(imageAlt)) {
						jAlert("Image alt is required.", "Banner");
					} else if (!isXSSSafe(imageAlt)){
						jAlert("Image alt contains XSS.","Banner");
					}else if($.isBlank(linkPath)) {
						jAlert("Link path is required.", "Banner");
					}else if(isRestrictDomain) {
						jAlert("Only the following domain are allowed value in link path: " + GLOBAL_storeDomains.join(','), "Banner");
					}else if(!isValidURL) {
						jAlert("Please specify a valid link path.", "Banner");
					}else if($.isBlank(startDate) || !$.isDate(startDate)){
						jAlert("Please provide a valid start date", "Banner");
					} else if($.isBlank(endDate) || !$.isDate(endDate)){
						jAlert("Please provide a valid end date", "Banner");
					} else if (!validateDescription("Banner", description, 1, 150)) {
						// error alert in function	
					} else if (attemptAddToSelectedRule){
						jAlert($.formatText("Duplicate instance of this banner is not allowed in {0}", base.options.rule["ruleName"]), "Banner");
					} else if (e.data.base.options.mode === "copy" && keywordArray.length <=0){
						jAlert("Specify keywords where to copy this banner", "Banner");
					} else if (!isXSSSafe(keywords)){
						jAlert("Keywords contains XSS.","Banner");
					} else {
						e.data['ruleId'] = base.options.rule["ruleId"];
						e.data['ruleName'] = base.options.rule["ruleName"];
						e.data['priority'] = base.options.priority;
						e.data['startDate'] = startDate;
						e.data['endDate'] = endDate;

						e.data['imagePathId'] = imagePathId;
						e.data['imagePath'] = imagePath;
						e.data['imageAlias'] = imageAlias;
						
						e.data['imageAlt'] = imageAlt;
						e.data['linkPath'] = linkPath;
						e.data['description'] = description;
						e.data['keywords'] = keywordArray;
						
						e.data['disable'] = disable;
						e.data['openNewWindow'] = openNewWindow;
						e.data['imageSize'] = imageSize;
						e.data['mode'] = base.options.mode;
						base.options.addBannerCallback(base, e);
					}
					break;
				case "cancelButton": 
					e.data.base.api.hide(); 
					break;
				}
			}
		}, {base: base});
	};

	$.addbanner.prototype.addInputFieldListener = function(id, currValue, callback) {
		var base = this;
		var ui = base.$el;
		
		ui.find(id).off().on({
			focusin: function(e) {
				if(e.data.locked) return
				e.data.input = $.trim($(e.currentTarget).val());
			},
			focusout: function(e) {
				if (e.data.locked) return;

				if($.isNotBlank($(e.currentTarget).val()) && 
						currValue !== $(e.currentTarget).val() &&
						e.data.valueWhenRequestSent !== $(e.currentTarget).val()
						) {
					e.data.valueWhenRequestSent = $(e.currentTarget).val();
					if(callback) callback.call(e.data.base, $(e.currentTarget).val());
				}
				
				if ($.isBlank($(e.currentTarget).val())){
					e.data.valueWhenRequestSent = "";
					$(e.currentTarget).val(currValue);
					if(callback) callback.call(e.data.base, $(e.currentTarget).val());
				}
			}
		}, {base: base, locked: base.options.isLocked, valueWhenRequestSent: ""});
	};

	$.addbanner.prototype.previewImage = function(imagePath) {
		var base = this;
		var ui = base.$el;

		if($.isBlank(imagePath)) {
			imagePath = base.options.noPreviewImage;
		}

		ui.find("img#imagePreview").attr("src",imagePath).off().on({
			error:function(){ 
				$(this).unbind("error").attr("src", base.options.noPreviewImage); 
			}
		});

	};

	$.addbanner.prototype.getImagePath = function(imagePath) {
		var base = this;
		var ui = base.$el;
		
		// Create new offscreen image to test
		var theImage = new Image();
		theImage.src = imagePath;
		ui.find("#imagePath").attr("data-size", "loading");
		
		$(theImage).off().on({
			error: function(e){
				var ui = e.data.ui;
				ui.find("#imagePath").attr("data-size", "error");
				base.previewImage(base.options.noPreviewImage);
			},
			load: function(e) {
				var ui = e.data.ui;
				var base = e.data.base;
				
				// Get accurate measurements from that.
				var imageWidth = theImage.width;
				var imageHeight = theImage.height;
				var dimension = imageWidth + 'x' + imageHeight;
				
				if ($.inArray(dimension, GLOBAL_storeAllowedBannerSizes)==-1){
					base.previewImage(imagePath);
					jAlert("Only the following sizes are allowed: " + GLOBAL_storeAllowedBannerSizes.join(','), "Banner Size");
				}else{
					ImagePathService.getImagePath(GLOBAL_storeId, imagePath, {
						callback: function(sr){
							if (sr!=null && sr["data"]!=null){
								var iPath = sr["data"];
								
								ui.find(".imageAlias").prop({
									id: iPath["id"],
									readonly: true,
									disabled: true
								}).val(iPath["alias"]);
								
							}else{
								ui.find(".imageAlias").prop({
									id:"",
									readonly: false,
									disabled: false
								});
							}
						},
						preHook: function(e){
							ui.find("span.preloader").show();
							ui.find(".imageAlias").val("");
						},
						postHook: function(e){
							ui.find("span.preloader").hide();
							base.previewImage(imagePath);
						}
					});
				}
				
				ui.find("#imagePath").attr("data-size", dimension);
			}
		}, {ui: ui, base:base});
		
	};

	$.addbanner.prototype.getTemplate = function() {
		var base = this;
		var template = '';

		template += '<div class="plugin-addbanner ban_edit">';
		template += '	<div id="preview">';
		template += '		<div id="preloader" class="circlePreloader" style="display:none">';
		template += '			<img src="' + GLOBAL_contextPath + '/images/ajax-loader-circ.gif" />';
		template += '		</div>';
		template += '		<div id="preview" >';
		template += '		<img id="imagePreview" src="' + GLOBAL_contextPath + '/images/nopreview.png" onError="this.onerror=null;this.src=\'' + GLOBAL_contextPath + '/images/nopreview.png\';" />';
		template += '		</div>';
		template += '	</div>';
		template += '	<div id="addItemTemplate" class="mar0 banner_info_more" style="width:642px">';
		template += '		<label class="txtLabel">Image Path: </label> ';
		template += '		<input id="imagePath" class="w565px" type="text">';
		template += '		<label class="txtLabel">Image Alias: </label> ';
		template += '		<input id="imageAlias" class="imageAlias w218px" type="text">';
		template += '		<div class="fRight">';
		template += '			<label class="txtLabel lblImageAlt">Image Alt: </label> ';
		template += '			<input id="imageAlt" class="w218px" type="text">';
		template += '		</div>';
		template += '		<label class="txtLabel mBottom0px">Link Path: </label> ';
		template += '		<input id="linkPath" class="w565px pad0" type="text">';
		template += '		<div class="clearfix openNewWindowContainer">';
		template += '	         <input type="checkbox" name="openNewWindow" id="openNewWindow" class="marL0"/>';
		template += '	         <label for="openNewWindow" class="fBold banner_info_more lHeight100">Open In New Window</label>';
		template += '	         </div>';
		template += '		<label class="txtLabel">Schedule:</label> ';
		template += '		<input id="startDate" class="startDate schedule" type="text">';
		template += '		<input id="endDate" class="endDate schedule"  type="text">';
		template += '		<label class="txtLabel">Description: </label> ';
		template += '		<textarea id="description" class="w565px"></textarea>';
		template += '		<div class="clearB"></div>';

		if (base.options.mode
				&& base.options.mode.toLowerCase() == 'copy') {
			template += '	<label class="txtLabel">Keyword: </label> ';
			template += '	<textarea id="keyword" class="w565px"></textarea>';
			template += '	<div class="clearB"></div>';
			template += '	<label class="note">One keyword per line</label>';
			template += '	<div class="clearB"></div>';
		}

		if (!base.options.isLocked) {
			var type = 'Add';

			if (base.options.ruleItem) {
				if (base.options.mode
						&& base.options.mode.toLowerCase() == 'update') {
					type = 'Update';
				} else if (base.options.mode
						&& base.options.mode.toLowerCase() == 'copy') {
					type = 'Copy';
				}
			}

			template += '<div id="buttonset">';
			template += '<input type="checkbox" name="temporaryDisable" id="temporaryDisable" />';
			template += '<label for="temporaryDisable" class="cRed fBold lbl_temporaryDisable">Temporary Disable</label>';
			template += '	<div class="floatR">';
			template += '		<div id="okButton" class="btn_ok round_btn fLeft buttons">';
			template += '			<span class="btn_wrap"><a href="javascript:void(0);">' + type + '</a></span>';
			template += '		</div>';
			template += '		<div id="cancelButton" class="btn_cancel round_btn fLeft buttons">';
			template += '			<span class="btn_wrap"><a href="javascript:void(0);">Cancel</a></span>';
			template += '		</div>';	
			template += '	</div>';	
			template += '</div>';
		}

		template += '	</div>';
		template += '</div>';

		return template;
	};

	$.addbanner.prototype.getLabel = function() {
		var base = this;
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

	$.addbanner.defaultOptions = {
			id: 1,
			rule: null,
			ruleItem: null,
			noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",
			title: "Add Banner",
			isPopup: false,
			isLocked: false,
			mode: "Add",
			priority: 1,
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
