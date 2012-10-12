(function($){

	$.activerule = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("activerule", base);

		base.init = function(){
			base.options = $.extend({},$.activerule.defaultOptions, options);
			base.$el.empty();
			base.getData();
		};

		base.getTemplate = function(){
			var template =  '<div class="activerules marT20 w500">';
			template += '	<ul id="arItemList">';
			template += '		<li id="arItemPattern" class="arItems borderB padTB5 clearfix w500 padL5" style="display:none">';
			template += '			<label class="ruleType floatL fbold w220"></label>';
			template += '			<label class="imageIcon floatL w20 posRel topn2"><img src="' + GLOBAL_contextPath + '/images/icon_reviewContent2.png" class="top2 posRel"></label>';
			template += '			<label class="name w225 floatL"><span class="fbold"></span></label>';
			template += '		</li>';
			template += '	</ul>';
			template += '</div>';
			return template;
		};

		
		base.getData = function(){
			base.options.beforeRequest();
			
			var currentURL  = GLOBAL_serverName;
				currentURL += $.isNotBlank(GLOBAL_serverPort)? ":" + GLOBAL_serverPort : "";
				
			var configURL = GLOBAL_solrUrl.replace("http://", "").split("/")[0];
			
			$.getJSON(
					GLOBAL_solrUrl.replace(configURL,currentURL) + GLOBAL_store + "/select?rows=0&wt=json&json.nl=map&gui=" + $.parseJSON(GLOBAL_solrConfig)["isFmGui"] + "&q=" + base.options.keyword,
					function (json, textStatus) { 
						base.$el.html(base.getTemplate());
						var rules = json.responseHeader["search_rules"];
						var $ul = base.$el.find("ul#arItemList");
						$ul.find("li.items:not(#arItemPattern)").remove();

						for(var i=0; i<rules.length; i++){
							var rule = rules[i]["rule"];
							$li = $ul.find("li#arItemPattern").clone().prop("id", $.formatAsId(rule["id"]));
							$li.find("label.ruleType").html(rule["type"]);
							$li.find("label.name").html(rule["name"]);

							$li.find("label.imageIcon > img").preview({
								ruleType: rule["type"],
								ruleId: rule["id"]
							});

							$li.show();
							$ul.append($li);
						}
						
						base.options.afterRequest();

					}
			);
		},

		// Run initializer
		base.init();
	};

	$.activerule.defaultOptions = {
			headerText: "",
			keyword: "",
			beforeRequest: function(){},
			afterRequest: function(){}
	};

	$.fn.activerule = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.activerule(this, options));
			});
		};
	};
})(jQuery);
