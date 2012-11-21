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
			base.$el.html(base.getTemplate());	
			base.getRules();
		};

		base.getRules = function(){
			var ruleEntity = base.options.rule["ruleEntity"];

			DeploymentServiceJS.getAllRuleStatus(ruleEntity, {
				callback: function(rs){
					var list = rs.list;
					base.populateOptions(list);
				}
			});

		};

		base.getTemplate = function(){
			var template = "";

			template += '<div>';
			template += '	<img id="preloader" src="' + GLOBAL_contextPath + '/images/preloader30x30Trans.gif">';
			template += '	<select id="importAsSelect" title="Select rule" style="display:none">';
			template += '		<option value="asNewRule">' + base.options.newRuleText + '</option>';
			template += '	</select>';
			template += '	<div id="replacement" style="display:none">';
			template += '		<label>Enter new name: </label>';
			template += '		<input id="newName" type="text"/>';
			template += '		<p>for replacement of <strong><a id="selectedRule" href="javascript:void(0);"></a></strong> rule</p>';
			template += '	</div>';
			template += '	<div id="importAlert" style="display:none">';
			template += '		<img src="' + GLOBAL_contextPath + '/images/icon_alert.png">';
			template += '		<p id="status"></p>';
			template +=	'	</div>';
			template += '</div>';

			return template;
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
		};

		base.showSelector = function(){
			var $importAsSelect = base.$el.find("select#importAsSelect");
			var rule = base.options.rule;
			var ruleEntity = rule["ruleEntity"];
			base.$el.find("#preloader").hide();
			var $option = $importAsSelect.find('option:eq(0)');

			switch(ruleEntity){
			case "ELEVATE": 
			case "EXCLUDE": 
			case "DEMOTE": 
			case "FACET_SORT": 
				$option.attr({value: rule["ruleName"]});
				$option.text(rule["ruleName"]);
				break;
			case "RANKING_RULE":	
			case "QUERY_CLEANING":
				break;
			}

			$importAsSelect.combobox({
				change: function(e, u){
					base.toggleFields($(this), u, false);
				},
				selected: function(e, u){
					base.toggleFields($(this), u, false);
				}
			});	
			
			var $allSpan = $importAsSelect.nextAll("span");
			switch(ruleEntity){
			case "ELEVATE": 
			case "EXCLUDE": 
			case "DEMOTE": 
			case "FACET_SORT": 
				$allSpan.eq(0).find("input").attr({
					disabled: "disabled"
				});
				$allSpan.eq(1).hide();
				break;
			case "RANKING_RULE":	
			case "QUERY_CLEANING":
				break;
			}

			base.showAlert($importAsSelect.find("option:selected").val());
		};

		base.toggleFields = function($select, u, selectRule){
			var $allSpan = $select.nextAll("span");
			var $replacement = base.$el.find("#replacement");
			var rule = base.options.rule;
			var ruleEntity = rule["ruleEntity"];
			$select.val(u.item.value);

			$allSpan.eq(0).hide();
			$allSpan.eq(1).hide();

			if(selectRule){
				$allSpan.eq(0).show();
				
				switch(ruleEntity){
				case "ELEVATE": 
				case "EXCLUDE": 
				case "DEMOTE": 
				case "FACET_SORT": 
					break;
				case "RANKING_RULE":	
				case "QUERY_CLEANING":
					$allSpan.eq(1).show();
					break;
				}

				$replacement.slideUp('slow', function(){
					$(this).hide();
				});
			}else{
				$replacement.find("#selectedRule").text(u.item.text);
				$replacement.find("input#newName").val(u.item.text);

				$replacement.slideDown('slow', function() {
					base.$el.find("#selectedRule").off().on({
						click: function(e){
							base.toggleFields($select, u, true);
						}
					});
				});
			}

			base.showAlert(u.item.value);
		};

		base.populateOptions = function(list){
			var $importAsSelect = base.$el.find("select#importAsSelect");
			var rule = base.options.rule;
			var ruleEntity = rule["ruleEntity"];

			var ruleStatus = null;
			base.rsLookup = new Array();

			for(var idx=0; idx < list.length; idx++){
				ruleStatus = list[idx];
				base.rsLookup[ruleStatus["ruleId"]] = ruleStatus;

				switch(ruleEntity){
				case "ELEVATE": 
				case "EXCLUDE": 
				case "DEMOTE": 
				case "FACET_SORT": break;
				case "RANKING_RULE":	
				case "QUERY_CLEANING":
					$importAsSelect.append($("<option>", {value: ruleStatus["ruleId"]}).text(ruleStatus["ruleName"]));
					break;
				}
			}

			base.showSelector();
		};

		// Run initializer
		base.init();
	};

	$.importas.defaultOptions = {
			rule: null,
			newRuleText: "Import As New Rule"
	};

	$.fn.importas = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.importas(this, options));
			});
		};
	};

})(jQuery);