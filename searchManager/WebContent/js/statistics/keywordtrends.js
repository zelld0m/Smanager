(function($) {
	// Keywords
	var Keywords = {};

	// Tabs
	var Tabs = {};

	// object containing utility methods
	var Utils = {};
	
	// Event handlers
	var Handler = {};

	// keyword class
	var Keyword = function(keyword) {
		var k = {
			el : $("#keywordWidgetTemplate").clone(),
			keyword : keyword,
			chartVisible : true,
			init : function() {
				// use self to refer to this object inside event handlers
				var self = this;

				this.el.attr("id", "keyword-" + keyword);
				this.el.find(".keyword").html(keyword);
				$("#keyword-list").append(this.el);

				// add event listeners
				this.el.find(".keyword-delete").click(function() {
					self.destroy();
				});
				this.el.find(".active-chart").click(function() {
					self.hideChart();
				});
				this.el.find(".inactive-chart").click(function() {
					self.showChart();
				});
				this.show();
			},
			show : function() {
				this.el.show();
			},
			hide : function() {
				this.el.hide();
			},
			showChart : function() {
				this.chartVisible = true;
				this.el.find(".inactive-chart").hide();
				this.el.find(".active-chart").show();
				Utils.setChartVisible(keyword, true);
			},
			hideChart : function() {
				this.chartVisible = false;
				this.el.find(".active-chart").hide();
				this.el.find(".inactive-chart").show();
				Utils.setChartVisible(keyword, false);
			},
			destroy : function() {
				for (tab in Tabs) {
					var idx = -1;

					for ( var i = 0; i < Tabs[tab].seriesList.length; i++) {
						if (Tabs[tab].seriesList[i].label == keyword) {
							idx = i;
							break;
						}
					}

					if (idx >= 0) {
						Tabs[tab].seriesList.splice(idx, 1);
						Utils.drawChart(Tabs[tab]);
					}
				}

				this.el.remove();
			}
		};

		k.init();

		return k;
	};

	///////////////////////////////////////////////////////////
	//                   Tab definitions                     //
	///////////////////////////////////////////////////////////

	/*
	 * Tab object template.
	 */
	var TAB_TEMPLATE = {
		seriesList : [],
		chart : null,
		ready : false,
		fromDate : null,
		toDate : null,
		data : function() {
			var data = [];

			for ( var i = 0; i < this.seriesList.length; i++) {
				data.push(this.seriesList[i].data);
			}

			return data;
		},
		setData: function(keyword, data) {
			for ( var i = 0; i < this.seriesList.length; i++) {
				if (keyword == this.seriesList[i].label) {
					this.seriesList[i].data = Utils.filterData(data);
					break;
				}
			}
		}
	};

	/*
	 * Daily tab.
	 */
	Tabs.daily = $.extend(true, {
		el : $("#tabs-1"),
		activator : $("#daily-link"),
		isValid : function() {
			var fromDate = $("#fromDate").datepicker("getDate");
			var toDate = $("#toDate").datepicker("getDate");

			if (fromDate >= toDate) {
				jAlert("Invalid date range.");
				return false;
			} else {
				this.fromDate = fromDate;
				this.toDate = toDate;
				return true;
			}
		},
		collation : 'daily',
		init: function() {
			var self = this;
			KeywordTrendsServiceJS.getMostRecentStatsDate({callback: function(date) {
				var from = new Date(date);
				var to = new Date(date);

				from.setDate(from.getDate() - 9);

				// initialize date pickers
				$("#fromDate").datepicker({
					constrainInput : true,
					defaultDate : from,
					dateFormat : 'M d, yy'
				}).datepicker("setDate", from);

				$("#toDate").datepicker({
					constrainInput : true,
					defaultDate : to,
					dateFormat : 'M d, yy'
				}).datepicker("setDate", to);

				self.fromDate = $("#fromDate").datepicker("getDate");
				self.toDate = $("#toDate").datepicker("getDate");
			}});
			$("#updateDateBtn").click(new Handler.UpdateHandler(this));
		}
	}, TAB_TEMPLATE);

	/*
	 * Weekly tab.
	 */
	Tabs.weekly = $.extend(true, {
		el : $("#tabs-2"),
		activator : $("#weekly-link"),
		isValid : function() {
			var fromDate = $.getFirstDayOfWeek($("#fromWeek").datepicker("getDate"));
			var toDate = $.getFirstDayOfWeek($("#toWeek").datepicker("getDate"));

			if (fromDate >= toDate) {
				jAlert("Invalid date range.");
				return false;
			} else {
				this.fromDate = fromDate;
				this.toDate = $.getLastDayOfWeek(toDate);
				return true;
			}
		},
		collation : 'weekly',
		init: function() {
			var self = this;
			var now = $.getFirstDayOfWeek(new Date());
			var before = new Date(now);
			
			before.setDate(before.getDate() - 70);
			before = $.getFirstDayOfWeek(before);
			
			// initialize date pickers
			$("#fromWeek").datepicker({
				constrainInput : true,
				defaultDate : before,
				dateFormat : 'M d, yy',
				showWeek: true,
				firstDay: 1,
				onSelect: function(dateStr, obj) {
					var date = $.getFirstDayOfWeek($("#fromWeek").datepicker("getDate"));

					$("#fromWeek").datepicker("option", "defaultDate", date);
					$("#fromWeek").datepicker("setDate", date);
				}
			}).datepicker("setDate", before);

			$("#toWeek").datepicker({
				constrainInput : true,
				defaultDate : now,
				dateFormat : 'M d, yy',
				showWeek: true,
				firstDay: 1,
				onSelect: function(dateStr, obj) {
					var date = $.getFirstDayOfWeek($("#toWeek").datepicker("getDate"));

					$("#toWeek").datepicker("option", "defaultDate", date);
					$("#toWeek").datepicker("setDate", date);
				}
			}).datepicker("setDate", now);

			self.fromDate = $.getFirstDayOfWeek($("#fromWeek").datepicker("getDate"));
			self.toDate = $.getLastDayOfWeek($("#toWeek").datepicker("getDate"));

			$("#updateWeeklyBtn").click(new Handler.UpdateHandler(this));
		}
	}, TAB_TEMPLATE);

	/*
	 * Monthly tab.
	 */
	Tabs.monthly = $.extend(true, {
		el : $("#tabs-3"),
		activator : $("#monthly-link"),
		isValid : function() {
			var fromYear = $("#fromYear").val();
			var toYear = $("#toYear").val();
			var fromMonth = $("#fromMonth").val();
			var toMonth = $("#toMonth").val();

			if (fromYear > toYear || fromYear === toYear
					&& fromMonth >= toMonth) {
				jAlert("Invalid date range.");
				return false;
			} else {
				this.fromDate = new Date(fromYear + "/" + fromMonth + "/01");
				this.toDate = Utils.getLastDayOfMonth(new Date(toYear + "/"
						+ toMonth + "/01"));
				return true;
			}
		},
		collation : 'monthly',
		init: function() {
			var date = new Date();
			var toMonth = date.getMonth() + 1;
			var toYear = date.getFullYear();
			
			date.setMonth(date.getMonth() - 9);

			var fromMonth = date.getMonth() + 1;
			var fromYear = date.getFullYear();

			$("#fromMonth").val(fromMonth < 10 ? "0" + fromMonth : fromMonth);
			$("#fromYear").val(fromYear);
			$("#toMonth").val(toMonth < 10 ? "0" + toMonth : toMonth);
			$("#toYear").val(toYear);

			this.fromDate = new Date(fromYear + "/" + fromMonth + "/01");
			this.toDate = Utils.getLastDayOfMonth(new Date(toYear + "/" + toMonth + "/01"));

			$("#updateMonthlyBtn").click(new Handler.UpdateHandler(this));
		}
	}, TAB_TEMPLATE);

	/*
	 * Series class containing series data and options for plotting.
	 */
	var Series = function(data) {
		return {
			data : Utils.filterData(data.stats),
			label : data.keyword,
			show : true,
			highlighter: {formatString: '<div class="highlighter-tooltip-keyword">'
				+ data.keyword + '</div><table class="highlighter-tooltip"><tr><th>Date</th><td>%s</td></tr><tr><th>Count</th><td>%s</td></tr></table>'}
		};
	};

	////////////////////////////////////////////////////////////////
	//                     Event Handlers                         //
	////////////////////////////////////////////////////////////////

	/*
	 * Handler for search box focus/blur events.
	 */
	Handler.TextBoxFocusHandler = function(onFocus) {
		return function() {
			var obj = $("#searchTextbox");

			if (onFocus && obj.val() == "Search Keyword") {
				obj.val("");
			} else if (!onFocus && !obj.val()) {
				obj.val("Search Keyword");
			}
		};
	};
	
	/*
	 * Handler for add button click event.
	 */
	Handler.AddKeywordHandler = function() {
		return function(e) {
			Utils.addKeyword($("#searchTextbox").val());
			$("#searchTextbox").val("Search Keyword");
		};
	};

	/*
	 * Handler for update chart button click event.
	 */
	Handler.UpdateHandler = function(tab) {
		return function() {
			if (tab.isValid()) {
				Utils.updateAllStats(tab);
			}
		};
	};

	//////////////////////////////////////////////////////////////////
	//                     Utility methods                          //
	//////////////////////////////////////////////////////////////////

	/*
	 * Filter series data. Remove null values.
	 */
	Utils.filterData = function(data) {
		var filteredData = [];

		for (i in data) {
			if (data[i] || data[i] === 0) {
				filteredData.push([ i, data[i] ]);
			}
		}

		return filteredData;
	};

	/*
	 * Get all listed keywords.
	 */
	Utils.getSelectedKeywords = function() {
		var k = [];

		for (key in Keywords) {
			k.push(key);
		}

		return k;
	};

	/*
	 * Retrieve stats for all keywords for a given tab.
	 */
	Utils.updateAllStats = function(tab) {
		// Get stats for all listed keywords
		KeywordTrendsServiceJS.getStats(Utils.getSelectedKeywords(),
				$.asUTC(tab.fromDate), $.asUTC(tab.toDate), tab.collation, {
					callback : function(data) {
						for ( var i = 0; i < data.length; i++) {
							tab.setData(data[i].keyword, data[i].stats);
						}

						// draw chart after data update
						Utils.drawChart(tab);
					}
				});
	};
	
	/*
	 * Add new keyword.
	 */
	Utils.addKeyword = function(keyword) {
		keyword = keyword && keyword.toLowerCase().trim();

		if (keyword && keyword != "search keyword" && !Keywords[keyword]) {
			Keywords[keyword] = new Keyword(keyword);

			for (tab in Tabs) {
				Utils.getStatsForNewKeyword(keyword, Tabs[tab]);
			}
		}
	};

	/*
	 * Retrieve stats for newly added keyword.
	 */
	Utils.getStatsForNewKeyword = function(keyword, tab) {
		KeywordTrendsServiceJS.getStats(keyword, $.asUTC(tab.fromDate), $.asUTC(tab.toDate),
				tab.collation, {
					callback : function(data) {
						var series = new Series(data);

						tab.seriesList.push(series);
						Utils.drawChart(tab);
					}
				});
	};

	var DateTickFormatter = $.jqplot.DateTickFormatter;
	var WeekFormatter = function(format, value) {
		var weekno = $.getWeekNumber(new Date(value));
		return weekno[0] + " w" + (weekno[1] < 10 ? "0" + weekno[1] : weekno[1]) + "   ";
    };
	
	Utils.drawChart = function(tab) {
		if (!tab.visible) {
			return;
		}

		var tickInterval = '1 day';
		var min = tab.fromDate;
		var max = tab.toDate;
		var xaxisFormatString = "%Y-%m-%d   ";
		var formatter = DateTickFormatter;

		if (tab.collation == 'monthly') {
			tickInterval = '1 month';
			max = new Date(tab.toDate);
			max.setDate(1);
			xaxisFormatString = "%b %Y   ";
		} else if (tab.collation == 'weekly') {
			tickInterval = '7 days';
			xaxisFormatString = "%Y wk%V   ";
			formatter = WeekFormatter;
		}

		$.jqplot.DateTickFormatter = formatter;
		var options = {
			gridPadding : {
				right : 35
			},
			axes : {
				xaxis : {
					renderer : $.jqplot.DateAxisRenderer,
					tickRenderer : $.jqplot.CanvasAxisTickRenderer,
					tickOptions : {
						formatString : xaxisFormatString,
						angle : -60,
						fontSize : '11px',
						fontFamily : 'Arial',
						labelPosition : 'end'
					},
					tickInterval : tickInterval,
					min : min,
					max : max
				},
				yaxis : {
					tickOptions : {
						formatString : '%s   ',
					}
				}
			},
			legend : {
				show : true
			},
			highlighter : {
				show : true,
				sizeAdjust : 7.5,
				useAxesFormatters : true,
				tooltipAxes : 'xy',
				tooltipLocation: 'ne'
			},
			series : tab.seriesList
		};
		
		if (tab.chart) {
			tab.chart.destroy();
		}
		
		tab.chart = $.jqplot(tab.collation + '-chart', tab.data(), options);
	};

	/*
	 * Set plot for given keyword visible/invisible on all charts.
	 */
	Utils.setChartVisible = function(keyword, visibility) {
		for (tab in Tabs) {
			for (var i = 0; i < Tabs[tab].seriesList.length; i++) {
				if (Tabs[tab].seriesList[i].label == keyword) {
					Tabs[tab].seriesList[i].show = visibility;
					break;
				}
			}
			
			Utils.drawChart(Tabs[tab]);
		}
	};
	
	Utils.getLastDayOfMonth = function(date) {
		var lastDayOfMonth = new Date(date);

		lastDayOfMonth.setMonth(lastDayOfMonth.getMonth() + 1);
		lastDayOfMonth.setDate(0);

		return lastDayOfMonth;
	};

	///////////////////////////////////////////////////////
	//              Initialization                       //
	///////////////////////////////////////////////////////

	/*
	 * Initialize side bar.
	 */
	var initSideBar = function() {
		$("#searchTextbox").focus(new Handler.TextBoxFocusHandler(true));
		$("#searchTextbox").blur(new Handler.TextBoxFocusHandler(false));
		
		var addKeywordHandler = new Handler.AddKeywordHandler();
		$("#addButton").click(addKeywordHandler);
		$("#searchTextbox").keyup(function(e) {
			if (e.which === 13) {
				addKeywordHandler(e);
			}
		});
	};

	/*
	 * Initialize tabs.
	 */
	var initTabs = function() {
		for (tab in Tabs) {
			Tabs[tab].init();
		}

		$("#tabs").tabs();
		$("#tabs").bind("tabsshow", function(e, ui) {
			for (tab in Tabs) {
				Tabs[tab].visible = !Tabs[tab].el.hasClass("ui-tabs-hide");
				Utils.drawChart(Tabs[tab]);
			}
		});

		for (tab in Tabs) {
			Tabs[tab].visible = !Tabs[tab].el.hasClass("ui-tabs-hide");
		}
	};

	/*
	 * Load top ten keywords from most recent log. 
	 */
	var loadInitialData = function() {
		KeywordTrendsServiceJS.getTopTenKeywords({callback: function(list) {
			for (var i = 0; i < list.length; i++) {
				Utils.addKeyword(list[i]);
			}
		}});
	};

	/*
	 * Main initialization function.
	 */
	var init = function() {
		initSideBar();
		initTabs();
		loadInitialData();
	};

	$(document).ready(init);
})(jQuery);