(function($){

	$.importas = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("importas", base);

		base.init = function(){
			base.options = $.extend({},$.importas.defaultOptions, options);
			base.setTemplate();
			
			if (base.options.inPreview || !$.importas.isLazyLoaded(base.options.rule.ruleEntity)) {
				base.getRules();
			} else {
				base.handleScroll();
			}
		};

		base.handleScroll = function() {
			var timer;
			
			$(base.options.container).on({
				scroll: function() {
					if (timer) {
						clearTimeout(timer);
					}
					timer = setTimeout(base.populateSelect, 400);
				}
			});
		};
		
		base.populateSelect = function() {
			if (!base.processed) {
				var rect1 = base.options.container.getBoundingClientRect();
				var rect2 = base.$el.find("select#importAsSelect")[0].getBoundingClientRect();

				if (rect1.top < rect2.bottom && rect1.bottom > rect2.top) {
					base.getRules();
					base.processed = true;
				}
			}
		};

		base.getRules = function(){
			var rule = base.options.rule;
			var ruleEntity = rule["ruleEntity"];
			base.automap = base.options.ruleTransferMap;

			if(base.options.ruleStatusList!=null && base.options.ruleStatusList.length > 0){
				base.populateOptions(base.options.ruleStatusList);
			}else{
				DeploymentServiceJS.getAllRuleStatus(ruleEntity, {
					callback: function(rs){
						var list = rs.list;
						base.populateOptions(list);
						base.options.setRuleStatusListCallback(base, list);		
					}
				});
			}			
		};

		base.setTemplate = function(){
			var template = "";

			template += '<div>';
			template += '	<img id="preloader" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
			template += '	<select id="importAsSelect" title="Select rule" class="searchable">';
			template += '		<option value="0">' + base.options.newRuleText + '</option>';
			template += '	</select>';
			template += '	<div id="replacement" style="display:none">';
			template += '		<p>Use custom name for <strong><a id="selectedRule" href="javascript:void(0);"></a></strong> rule: </p>';
			template += '		<input id="newName" type="text"/>';
			template += '	</div>';
			template += '	<div id="importAlert" style="display:none">';
			template += '		<p><img src="' + GLOBAL_contextPath + '/images/icon_alert.png">';
			template += '		<span id="status"></span></p>';
			template +=	'	</div>';
			template += '</div>';

			base.$el.append(template);
		};

		base.showAlert = function(id){
			var ruleStatus = base.rsLookup[id];
			var $importAlert = base.$el.find("#importAlert");

			if(ruleStatus!=undefined && (ruleStatus["approvalStatus"] === "PENDING" || ruleStatus["approvalStatus"] === "APPROVED")){
				$importAlert.find("#status").text("Rule is in " + getRuleNameSubTextStatus(ruleStatus));
				$importAlert.show();
			}else{
				$importAlert.find("#status").empty();
				$importAlert.hide();
			};

			base.options.targetRuleStatusCallback(base, base.options.rule, ruleStatus);
		};

		base.toggleFields = function(u, evt, rule, selectRule){
			var $replacement = $(u).parents("tr").find("#replacement");
			base.options.selectedOptionChanged(u.value);

			if(selectRule){
				$(u).show();
				$replacement.slideUp('slow', function(){
					$(this).hide();
				});
			}else{
				$(u).hide();
				$replacement.find("#selectedRule").text($(u).val()==="0"? "Pending for Import":  rule["ruleName"]);

				var $input = $replacement.find("input#newName");

				$input.val($(u).val()==="0"? rule["ruleName"]:  $(u).find("option:gt(0):selected:eq(0)").text());

				$replacement.slideDown('slow', function() {
					$(u).parents("tr").find("#selectedRule").off().on({
						click: function(e){
							base.toggleFields(e.data.u, e.data.evt, e.data.rule, true);
						}
					},{u: u, evt: evt, rule: rule});
				});

				$input.off().on({
					focusin: function(e){
						if($(e.currentTarget).val().toLowerCase()===base.options.newRuleText.toLowerCase()){
							$(e.currentTarget).val("");
						}
					},
					focusout: function(e){
						if($.isBlank($(e.currentTarget).val())){
							$(e.currentTarget).val(u.value);
						}
					}
				});
			}

			base.showAlert(u.value);
		};

		base.populateOptions = function(list){
			var $importAsSelect = base.$el.find("select#importAsSelect");
			var rule = base.options.rule;
			var ruleEntity = rule["ruleEntity"];

			var optionString = "";
			base.rsLookup = new Array();
			base.rsLookupByName = new Array();
			
			$.each(list, function() {
				base.rsLookup[this.ruleId] = this;
				base.rsLookupByName[this.ruleName] = this;

				if (!$.importas.selectOptions[ruleEntity]) {
					switch (ruleEntity) {
					case "QUERY_CLEANING":
					case "RANKING_RULE":
						optionString += "<option value='" + this.ruleId + "'>"
								+ this.ruleName + "</option>";
					}
				}
			});

			switch(ruleEntity) {
			    case "QUERY_CLEANING":
			    case "RANKING_RULE":
					if (!$.importas.selectOptions[ruleEntity]) {
						$.importas.selectOptions[ruleEntity] = optionString;
					}

					if ($.isEmptyObject(base.autoMap) || (!$.isEmptyObject(base.autoMap) && $.isEmptyObject(base.autoMap[rule["ruleId"]]))) {
						$importAsSelect.append($.importas.selectOptions[ruleEntity]);
					}
			}

			base.$el.find("#preloader").hide();

			var $replacement = base.$el.find("#replacement");
			var $option = $importAsSelect.find('option:eq(0)');

			switch(ruleEntity){
			case "ELEVATE": 
			case "EXCLUDE": 
			case "DEMOTE":
				$option.attr({value: rule["ruleName"], selected: true});
				$option.text(rule["ruleName"]);
				$replacement.find("input#newName").val(rule["ruleName"]);
				break;
			case "FACET_SORT": 
				$option.attr({value: rule["ruleId"], selected: true});
				$option.text(rule["ruleName"]);
				$replacement.find("input#newName").val(rule["ruleName"]);
				break;
			case "RANKING_RULE":	
			case "QUERY_CLEANING":
				if(!$.isEmptyObject(base.autoMap) && !$.isEmptyObject(base.autoMap[rule["ruleId"]])){ //TODO:
					$option.attr({value: base.autoMap[rule["ruleId"]]["ruleIdTarget"], selected: true});
					$option.text(base.autoMap[rule["ruleId"]]["ruleNameTarget"]);
					$replacement.find("input#newName").val(base.autoMap[rule["ruleId"]]["ruleNameTarget"]);
				}
				break;
			}

			$importAsSelect.searchable({
				rule: rule,
				maxListSize: 10, 
				maxMultiMatch: 10,
				exactMatch: true,
				change: function(u, e, rule){
					if(ruleEntity==="RANKING_RULE" || ruleEntity==="QUERY_CLEANING"){
						base.toggleFields(u, e, rule, false);
					}
				}
			});

			if(ruleEntity==="FACET_SORT"){
				var rs = base.rsLookupByName[rule["ruleName"]];
				
				if (rs)
					base.showAlert(rs["ruleId"]);
			}else{
				base.showAlert($importAsSelect.find("option:selected").val());
			};

			base.options.afterUIRendered();
		};

		// Run initializer
		base.init();
	};

	$.importas.defaultOptions = {
			rule: null,
			ruleStatusList: null,
			newRuleText: "Import As New Rule",
			inPreview: false,
			targetRuleStatusCallback: function(base, rule, ruleStatus){},
			selectedOptionChanged: function(ruleId){},
			setRuleStatusListCallback: function(base, list){},
			afterUIRendered: function(){}
	};

	$.importas.selectOptions = new Array();
	$.importas.isLazyLoaded = function(ruleEntity) {
		switch(ruleEntity) {
		case "RANKING_RULE":
		case "QUERY_CLEANING":
			return true;
	    default:
			return false;
		}
	};

	$.fn.importas = function(options){
		if (this.length) {
			return this.each(function() {
				$(this).empty();
				(new $.importas(this, options));
			});
		};
	};

})(jQuery);