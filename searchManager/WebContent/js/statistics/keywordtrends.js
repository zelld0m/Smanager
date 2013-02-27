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
			color: Utils.color(),
			chartVisible : true,
			init : function() {
				// use self to refer to this object inside event handlers
				var self = this;

				this.el.attr("id", "keyword-" + keyword);
				this.el.find(".keyword").text(keyword);
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
				Utils.setControlButtons();
			},
			show : function() {
				this.el.show();
			},
			hide : function() {
				this.el.hide();
			},
			showChart : function(defer) {
				this.chartVisible = true;
				this.el.find(".inactive-chart").hide();
				this.el.find(".active-chart").show();
				Utils.setChartVisible(keyword, true, defer);
				Utils.setControlButtons();
			},
			hideChart : function(defer) {
				this.chartVisible = false;
				this.el.find(".active-chart").hide();
				this.el.find(".inactive-chart").show();
				Utils.setChartVisible(keyword, false, defer);
				Utils.setControlButtons();
			},
			destroy : function() {
				delete Keywords[keyword];

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

				Utils.refreshSelection();
				Utils.setControlButtons();
				Utils.returnColor(this.color);
				this.el.remove();
			}
		};

		Keywords[keyword] = k;
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
		},
		hasData: function() {
			for ( var i = 0; i < this.seriesList.length; i++) {
				if (this.seriesList[i].data.length > 0) {
					return true;
				} 
			}

			return false;
		}
	};

	/*
	 * Daily tab.
	 */
	Tabs.daily = $.extend(true, {
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
			this.el = $("#tabs-1");
			this.activator = $("#daily-link");
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
				Utils.dateLoaded = true;

				if (!Utils.fullyLoaded) {
					loadInitialData();
				}
			}});
			$("#updateDateBtn").click(new Handler.UpdateHandler(this));
		}
	}, TAB_TEMPLATE);

	/*
	 * Weekly tab.
	 */
	Tabs.weekly = $.extend(true, {
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
			
			this.el = $("#tabs-2");
			this.activator = $("#weekly-link");
			
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
			
			this.el = $("#tabs-3");
			this.activator = $("#monthly-link");
			
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
			$("#searchTextbox").val("");
			$("#searchTextbox").blur();
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
	
	/*
	 * Click handlers for buttons
	 */
	Handler.showAll = function() {
		for(key in Keywords) {
			Keywords[key].showChart(true);
		}

		for (tab in Tabs) {
			Utils.drawChart(Tabs[tab]);
		}
	};

	Handler.hideAll = function() {
		for(key in Keywords) {
			Keywords[key].hideChart(true);
		}

		for (tab in Tabs) {
			Utils.drawChart(Tabs[tab]);
		}
	};

	Handler.clear = function() {
		for(key in Keywords) {
			Keywords[key].destroy();
		}
	};

	Handler.reset = function() {
		loadInitialData();
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
	Utils.updateAllStats = function(tab, callback) {
		// Get stats for all listed keywords
		KeywordTrendsServiceJS.getStats(Utils.getSelectedKeywords(),
				$.asUTC(tab.fromDate), $.asUTC(tab.toDate), tab.collation, {
					callback : callback || function(data) {
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

		if (keyword == "search keyword") {
			keyword = "";
		}

		var valid = validateGeneric("Keyword", keyword, 0, 50);

		if (valid && !keyword) {
			jAlert("Keyword is required.");
		} else if (valid && Keywords[keyword]) {
			jAlert("Keyword <b>" + keyword + "</b> already exists.");
		} else if (valid) {
			var kw = new Keyword(keyword);

			for (tab in Tabs) {
				Utils.getStatsForNewKeyword(kw, Tabs[tab]);
			}

			Utils.addToSelection(keyword);
		}
	};

	Utils.addToSelection = function(keyword) {
		var currentSelection = Utils.getCurrentSelection();

		currentSelection.push(keyword);
		Utils.setCurrentSelection(currentSelection);
	}; 

	Utils.getCurrentSelection = function() {
		var str = $.cookie("selected-keywords");

		if (str) {
			return JSON.parse(str);
		} else {
			return new Array();
		}
	};

	Utils.setCurrentSelection = function(keywords) {
		$.cookie("selected-keywords", JSON.stringify(keywords), {path:GLOBAL_contextPath});
	};

	Utils.refreshSelection = function() {
		Utils.setCurrentSelection(Utils.getSelectedKeywords());
	};

	Utils.addAll = function(keywords) {
		for (var i = 0; i < keywords.length; i++) {
			new Keyword(keywords[i]);
			Utils.addToSelection(keywords[i]);
		}

		for (tab in Tabs) {
			Utils.updateAllStats(Tabs[tab], new function(curtab) {
				return function(data) {
					for ( var i = 0; i < data.length; i++) {
						var series = new Series(data[i]);
						series.color = Keywords[data[i].keyword].color;

						curtab.seriesList.push(series);
						Utils.drawChart(curtab);
					}
				};
			}(Tabs[tab]));
		}
	};

	/*
	 * Retrieve stats for newly added keyword.
	 */
	Utils.getStatsForNewKeyword = function(keyword, tab) {
		KeywordTrendsServiceJS.getStats(keyword.keyword, $.asUTC(tab.fromDate), $.asUTC(tab.toDate),
				tab.collation, {
					callback : function(data) {
						var series = new Series(data);
						series.color = keyword.color;

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
		
		if (tab.hasData() && Utils.hasVisibleKeyword()) {
			tab.el.find("#message").hide();
			tab.chart = $.jqplot(tab.collation + '-chart', tab.data(), options);
		} else {
			tab.el.find("#message").show();
		}
	};
	
	Utils.setControlButtons = function() {
		if (Object.keys(Keywords).length) {
			$("#button-controls #hide-all-button").show();
			$("#button-controls #show-all-button").show();
			$("#button-controls #reset-button").text("Clear").off().on({click: Handler.clear});

			if (Utils.hasVisibleKeyword()) {
				$("#button-controls #hide-all-button").removeClass("disabled-button").addClass("enabled-button").off().on({click: Handler.hideAll});
			} else {
				$("#button-controls #hide-all-button").removeClass("enabled-button").addClass("disabled-button").off();
			}

			if (Utils.hasInvisibleKeyword()) {
				$("#button-controls #show-all-button").removeClass("disabled-button").addClass("enabled-button").off().on({click: Handler.showAll});
			} else {
				$("#button-controls #show-all-button").removeClass("enabled-button").addClass("disabled-button").off();
			}
		} else {
			$("#button-controls #hide-all-button").hide();
			$("#button-controls #show-all-button").hide();
			$("#button-controls #reset-button").text("Reset").off().on({click: Handler.reset});
		}
	};

	/*
	 * Set plot for given keyword visible/invisible on all charts.
	 */
	Utils.setChartVisible = function(keyword, visibility, deferDrawing) {
		for (tab in Tabs) {
			for (var i = 0; i < Tabs[tab].seriesList.length; i++) {
				if (Tabs[tab].seriesList[i].label == keyword) {
					Tabs[tab].seriesList[i].show = visibility;
					break;
				}
			}
			
			if (!deferDrawing) {
				Utils.drawChart(Tabs[tab]);
			}
		}
	};
	
	Utils.colors =  ["#4bb2c5", "#EAA228", "#c5b47f", "#579575", "#839557", "#958c12", "#953579", "#4b5de4", "#d8b83f", "#ff5800", "#0085cc", "#c747a3", "#cddf54", "#FBD178", "#26B4E3", "#bd70c7",
          "#498991", "#C08840", "#9F9274", "#546D61", "#646C4A", "#6F6621", "#6E3F5F", "#4F64B0", "#A89050", "#C45923", "#187399", "#945381", "#959E5C", "#C7AF7B", "#478396", "#907294"];

	Utils.color = function() {
		if (Utils.colors.length == 0) {
			Utils.colors.push("#" + ("000000" + Math.floor(Math.random() * 0x1000000).toString(16)).slice(-6));
		}

		var c = Utils.colors[0];
		Utils.colors.splice(0, 1);
		return c;
	};

	Utils.returnColor = function(c) {
		Utils.colors.push(c);
	};

	/*
	 * Set plot for given keyword visible/invisible on all charts.
	 */
	Utils.hasVisibleKeyword = function() {
		for (key in Keywords) {
			if (Keywords[key].chartVisible) {
				return true;
			}
		}
		
		return false;
	};

	/*
	 * Set plot for given keyword visible/invisible on all charts.
	 */
	Utils.hasInvisibleKeyword = function() {
		for (key in Keywords) {
			if (!Keywords[key].chartVisible) {
				return true;
			}
		}

		return false;
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
		if (Utils.dateLoaded) {
			var currentSelection = Utils.getCurrentSelection();
	
			if (currentSelection.length) {
				Utils.setCurrentSelection([]);
				Utils.addAll(currentSelection);
			} else {
				KeywordTrendsServiceJS.getTopTenKeywords({callback: Utils.addAll});
			}

			Utils.fullyLoaded = true;
		}
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