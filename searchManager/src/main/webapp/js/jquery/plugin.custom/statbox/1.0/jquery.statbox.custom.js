(function($){

	$.statbox = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("statbox", base);

		base.options = $.extend({}, $.statbox.defaultOptions, options);

		// Run initializer
		base.init();
	};

	$.statbox.prototype.setId = function(id) {
		var base= this;
		var ui = base.$el;

		ui.find("div:first").prop({
			id: $.isNotBlank(id)? id: "plugin-statbox-" + base.options.id
		});
	};

	$.statbox.prototype.init = function(){
		var base = this;

		if(base.options.isPopup) {
			base.$el.qtip({
				id: "plugin-statbox-qtip",
				content: {
					text: $('<div/>'),
					title: {text: base.options.title, button: true }
				},
				position: {
					my: 'bottom center',
					at: 'top center'
				},
				style: {
					width: 'auto'
				},
				events: {
					render: function(event, api) {
						base.api = api; 
						base.$el = ($("div", api.elements.content));
					},
					show: function(event, api){
						base.$el.html(base.getTemplate());
						base.setId.call(base, base.$el);
						base.getList.call(base);
					}
				}
			});
		} else {
			base.$el.html(base.getTemplate());
			base.setId.call(base, base.$el);
			base.getList.call(base);
		}
	};

	$.statbox.prototype.getTemplate = function(){
		var base = this;
		var template  = '';

		template += '<div class="plugin-statbox">';
		template += '	<div class="w265 padB8">';
		template += '		<div>';
		template += '			<label>Select Date Range</label>';
		template += '			<input type="text" id="statFilterStartDate">';
		template += '			<label>-</label>';
		template += '			<input type="text" id="statFilterEndDate">';
		template += '			<div id="goBtn" class="btn round_btn">';
		template += '				<span class="btn_wrap">';
		template += '					<a href="javascript:void(0);">GO</a>';
		template += '				</span>';
		template += '			</div>';
		template += '		</div>';
		template += '		<div id="header">';
		template += '			<div id="columnHeader">';
		template += '				<span class="itemName">Keyword</span>';        
		template += '				<span class="itemSchedule">Schedule</span>';        
		template += '				<span class="itemClick">Click</span>';        
		template += '				<span class="itemImpression">Impression</span>';        
		template += '				<span class="itemPercentage">Ratio C/I</span>';        
		template += '			</div>';
		template += '		</div>';
		template += '		<div class="clearB"></div>';
		template += '		<div id="emptyText" class="txtAC">' + base.options.emptyText + '</div>';
		template += '		<div id="preloader" class="txtAC"><img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif"></div>';
		template += '		<div class="clearB"></div>';
		template += '		<div id="itemHolder">';
		template += '			<div id="itemPattern" class="item" style="display: none; width: 100%">';
		template += '				<span class="itemName"><img src="' + GLOBAL_contextPath +'/images/ajax-loader-rect.gif"></span>';        
		template += '				<span class="itemSchedule"><img src="' + GLOBAL_contextPath +'/images/ajax-loader-rect.gif"></span>';        
		template += '				<span class="itemClick"></span>';        
		template += '				<span class="itemImpression"></span>';        
		template += '				<span class="itemPercentage"></span>';        
		template += '			</div>';
		template += '		</div>';
		template += '		<div class="clearB"></div>';
		template += '	</div>';
		template += '</div>';

		return $(template);
	};

	$.statbox.prototype.getList = function() {
		var base = this;
		base.options.itemDataCallback.call(base);
	};

	$.statbox.prototype.prepareList = function(){
		var base = this;
		base.$el.find("#itemHolder > div:not(#itemPattern), #itemPagingTop, #itemPagingBottom").empty();
		base.$el.find("div#emptyText").hide();
		base.$el.find("div#preloader").show();
	};

	$.statbox.prototype.reposition = function() {
		var base = this;
		base.api && base.api.reposition();
	};

	$.statbox.prototype.populateList = function(data, statType){
		var base = this;
		var ui = base.$el; 

		ui.find('#preloader, #emptyText').hide();
		ui.find('#itemHolder > div:not(#itemPattern)').remove();

		var calendarOpts = {
				defaultDate: GLOBAL_currentDate,
				changeMonth: true,
				changeYear: true,
				showOn: "both",
				buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
				buttonImageOnly: true,
				buttonText: "Date to simulate",				
		};

		ui.find("#statFilterStartDate").prop({
			id: "statFilterStartDate_" +  statType
		}).datepicker($.extend({}, calendarOpts, {
			onClose: function(selectedDate) {
				ui.find("#statFilterEndDate_" + statType).datepicker("option", "minDate", selectedDate);
			}
		}));

		ui.find("#statFilterEndDate").prop({
			id: "statFilterEndDate_" +  statType
		}).datepicker($.extend({}, calendarOpts, {
			onClose: function(selectedDate) {
				ui.find("#statFilterStartDate_" + statType).datepicker("option", "maxDate", selectedDate);
			}
		}));
		
		ui.find("#goBtn").off().on({
			click: function(e){
				
				var startDateText = e.data.base.$el.find("#statFilterStartDate_" + e.data.type).val(); 
				var endDateText = e.data.base.$el.find("#statFilterEndDate_" + e.data.type).val();
				
				if($.isNotBlank(startDateText) && $.isNotBlank(endDateText) && $.isDate(startDateText) && $.isDate(endDateText)){
					e.data.base.options.itemDataCallback.call(e.data.base, startDateText, endDateText);
				}else{
					jAlert("Please specify a valid date range","Banner Statistic");
				}
			}
		}, {base: base, type: statType});

		if(data && data["totalSize"] > 0){
			var itemHolder = ui.find('#itemHolder');

			for (var i = 0; i < data["list"].length; i++) {

				var item = data["list"][i];
				var itemUI = itemHolder.find('#itemPattern').clone();
				itemUI.prop({
					id: item["memberId"]
				});

				switch(statType.toLowerCase()){
				case "keyword": 
					base.populateItemFieldsByBanner.call(base, itemUI, item);
					break;
				case "memberid": 
					base.populateItemFieldsByKeyword.call(base, itemUI, item);
					break;
				}

				itemUI.show();
				itemHolder.append(itemUI);
			}

			itemHolder.find('div:nth-child(even)').addClass("alt");
		}else{
			ui.find("#emptyText").show();
			return;
		}
	};

	$.statbox.prototype.populateItemFieldsByKeyword = function(itemUI, item){
		var base = this;

		itemUI.find(".itemName").text(item["keyword"]);
		base.options.itemScheduleCallback.call(itemUI, base.options.rule["ruleId"],item["memberId"]);
		itemUI.find(".itemClick").text(item["clicks"]);
		itemUI.find(".itemImpression").text(item["impressions"]);
		var percent = $.toPercentFormat("%", (item["clicks"]/item["impressions"])*100);
		itemUI.find(".itemPercentage").text(percent);
	};

	$.statbox.prototype.populateItemFieldsByBanner = function(itemUI, item){
		var base = this;
		var ui = base.$el;
		
		ui.find("#columnHeader > .itemName").text("Alias");
		ui.find("#columnHeader > .itemSchedule").text("Link Path");
		
		base.options.itemImagePathCallback.call(itemUI, item["imagePath"]);
		itemUI.find(".itemSchedule").text(item["linkPath"]);
		itemUI.find(".itemClick").text(item["clicks"]);
		itemUI.find(".itemImpression").text(item["impressions"]);
		var percent = $.toPercentFormat("%", (item["clicks"]/item["impressions"])*100);
		itemUI.find(".itemPercentage").text(percent);
	};

	$.statbox.defaultOptions = {
			id: 1,
			rule: null, 
			ruleType: "Banner",
			title: "Banner Statistics",
			isPopup: true,
			locked: false,
			emptyText: "No data available.",
			itemDataCallback: function(base){},
	};

	$.fn.statbox = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.statbox(this, options));
			});
		};
	};

})(jQuery);
