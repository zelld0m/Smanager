(function($){

	$.version = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("version", base);

		base.init = function(){
			base.options = $.extend({},$.version.defaultOptions, options);
			base.$el.empty();
			base.showVersion();
		};

		base.showVersion = function(){
			$(base.$el).qtip({
				content: {
					text: $('<div/>'),
					title: { text: base.options.moduleName + " Version", button: true }
				},
				position: {
					my: 'center',
					at: 'center',
					target: $(window)
				},
				style: {
					width: "auto"
				},
				show: {
					ready: true,
					modal:true
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getTemplate());
					},
					hide: function(event, api){
						base.options.afterClose();
						api.destroy();
					}
				}
			});
		};
		
		base.getTemplate = function(){
			var template  = '';
			
			template  = '<div id="reasonView">';
			template += '		<div class="marB20">';
			template += '			<label class="w70 marT10 floatL fbold">Name</label>';
			template += '			<label><input id="backupName" type="text" class="w240 marT5"/></label>';
			template += '			<div class="clearB"></div>';
			template += '			<label class="w70 marT4 floatL fbold">Reason</label>';
			template += '			<label><textarea id="reason" class="w240 marT5"/></label>';
			template += '		</div>';
			template += '		<div align="right" class="padR3 marT10">';
			template += '			<a id="rsaveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Save</div>';
			template += '			</a>';
			template += '			<a id="rcancelBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
			template += '				<div class="buttons fontBold">Cancel</div>';
			template += '			</a>';
			template += '		</div>';
			template += '		<div class="clearB"></div>';
			template += '</div>';
			
			template += '<div id="versions" class="versions marT20 w99p marRLauto">';
			template += '		<div style="max-height:365px;" class="w100p padT0 fsize12">';
			template += '			<div class="items border clearfix" id="itemPattern">';
			template += '				<label style="border-right:1px solid #cccccc; background:#eee" class="floatL w100 txtAC fbold padTB5"> Version # </label>';
			template += '				<label style="border-right:1px solid #cccccc; background:#eee" class="floatL w340 txtAC fbold padTB5"> Version Name </label>';
			template += '				<label style="border-right:1px solid #cccccc; background:#eee" class="floatL w200 txtAC fbold padTB5"> Version Date </label>';
			template += '				<label style="border-right:0px solid #cccccc; background:#eee" class="floatL w20 txtAC fbold padTB5"> &nbsp; </label>';
			template += '				<label style="border-right:0px solid #cccccc; background:#eee" class="floatL w20 txtAC fbold padTB5"> &nbsp; </label>';
			template += '				<label style="background:#eee" class="floatL w30 txtAC fbold padTB5"> &nbsp; </label>';
			template += '			</div>';
			template += '		</div>';
			template += '		<ul id="verItemList">';
			template += '			<li id="verItemPattern" class="verItems borderB padTB5 clearfix w99p padL5 fsize12" style="display:none">';
			template += '				<label class="select floatL fbold w55" style="display:none"><input type="checkbox"></label>';
			template += '				<label class="ver w100 floatL txtAC"></label>';
			template += '				<label class="verDetail floatL w340">';
			template +=	'					<p id="verName" class="breakWord fbold"></p>';
			template +=	'					<p id="verReason" class="fsize11 breakWord"></p>';
			template +=	'				</label>';
			template += '				<label class="verDate w200 floatL"></label>';
			template += '				<label class="previewIcon floatL w20 posRel topn2"><img alt="Preview Content" title="Preview Content" src="' + GLOBAL_contextPath + '/images/icon_reviewContent2.png" class="top2 posRel"></label>';
			
			if(!base.options.locked){
				template += '				<label class="restoreIcon floatL w20 posRel topn2"><img alt="Restore Backup" title="Restore Backup" src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"></label>';
				template += '				<label class="deleteIcon floatL w20 posRel topn2"><img alt="Delete Backup" title="Delete Backup" src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="top2 posRel"></label>';
			}
			
			template += '			</li>';
			template += '		</ul>';
			template += '</div>';
			return template;
		};

		// Run initializer
		base.init();
	};

	$.version.defaultOptions = {
			moduleName: "",
			headerText: "",
			ruleType: "",
			ruleId: "",
			limit: 3,
			locked: true,
			buttonHolderId: "",
			beforeRequest: function(){},
			afterRequest: function(){},
			restoreCallback: function(rule){},
			afterClose:function(){}
	};

	$.fn.version = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.version(this, options));
			});
		};
	};

})(jQuery);