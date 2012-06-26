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
			base.getData();
		};

		base.getTemplate = function(){
			var template  = '<div class="versions marT20 w99p marRLauto">';
				template += '	<ul id="verItemList">';
				template += '		<li id="verItemPattern" class="verItems borderB padTB5 clearfix w99p padL5 fsize12" style="display:none">';
				template += '			<label class="select floatL fbold w55" style="display:none"><input type="checkbox"></label>';
				template += '			<label class="ver w100 floatL"></label>';
				template += '			<label class="verDate w200 floatL"></label>';
				template += '			<label class="previewIcon floatL w20 posRel topn2"><img src="' + GLOBAL_contextPath + '/images/icon_reviewContent2.png" class="top2 posRel"></label>';
				template += '			<label class="verName floatL fbold w340"></label>';
				template += '			<label class="restoreIcon floatL w20 posRel topn2"><img src="' + GLOBAL_contextPath + '/images/icon_restore2.png" class="top2 posRel"></label>';
				template += '			<label class="deleteIcon floatL w20 posRel topn2"><img src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="top2 posRel"></label>';
				template += '		</li>';
				template += '	</ul>';
				template += '</div>';
			return template;
		};

		base.getData = function(){
			base.$el.html(base.getTemplate());

			RuleVersioningServiceJS.getRuleVersions(base.options.ruleType,base.options.ruleId, {
				callback: function(data){
					
					var $ul = $("ul#verItemList");
					$ul.find("li.verItems:not(#verItemPattern)").remove();
					
					if(data!=null && data.length>0){
						for (var i=0; i < data.length ; i++){
							var $li = $ul.find("li#verItemPattern").clone();
							var item = data[i];
							$li.prop("id", "row" + $.formatAsId(item["ruleId"]));
							$li.find("label.ver").html(item["version"]);
							$li.find("label.verDate").html(item["dateCreated"].toUTCString());
							$li.find("label.verName").html(item["reason"]);
							
							$li.find("label.previewIcon > img").preview({
								ruleType: base.options.ruleType,
								ruleId: item["ruleId"]
							});
							
							$li.find("label.deleteIcon > img").off().on({
								click:function(evt){
									if (confirm("Delete restore point version " + item["version"] + "?")){
										RuleVersioningServiceJS.deleteRuleVersion(base.options.ruleType ,item["ruleId"], item["version"], {
											callback:function(data){
												
											},
											preHook:function(){
												
											},
											postHook:function(){
												
											}
										});
									}
								}
							});
							
							$li.show();
							$ul.append($li);
						}
					}
				},
				preHook: function(){
					base.options.beforeRequest();
				},
				postHook: function(){
					base.options.afterRequest();
				}
			});
		},

		// Run initializer
		base.init();
	};

	$.version.defaultOptions = {
			headerText: "",
			ruleType: "",
			ruleId: "",
			beforeRequest: function(){},
			afterRequest: function(){}
	};

	$.fn.version = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.version(this, options));
			});
		};
	};

})(jQuery);