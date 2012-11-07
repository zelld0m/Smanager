(function($) {
	var _seriesList = [];
	var _effectiveFromDate = null;
	var _effectiveToDate = null;
	var _chart = null;

	/* Event Handlers */

	var SearchTextBoxFocusHandler = function(onFocus) {
		return function() {
			var obj = $("#searchTextbox");

			if (onFocus && obj.val() == "Search Keyword") {
				obj.val("");
			} else if (!onFocus && !obj.val()) {
				obj.val("Search Keyword");
			}
		};
	};

	var dateUpdateHandler = function() {
		var fromDate = $("#fromDate").datepicker("getDate");
		var toDate = $("#toDate").datepicker("getDate");

		if (fromDate >= toDate) {
			alert("Invalid date range.");
		} else {
			_effectiveFromDate = fromDate;
			_effectiveToDate = toDate;

			KeywordTrendsServiceJS.getStats(_getSelectedKeywords(),
					_effectiveFromDate, _effectiveToDate, {
						callback : function(data) {
							for ( var i = 0; i < data.length; i++) {
								_setNewData(data[i]);
							}
							_initChart();
						}
					});
		}
	};

	var _setNewData = function(keywordStats) {
		for ( var i = 0; i < _seriesList.length; i++) {
			if (keywordStats.keyword == _seriesList[i].label) {
				_seriesList[i].data = filterData(keywordStats.stats);
				break;
			}
		}
	};

	var _getSelectedKeywords = function() {
		var keywords = [];
		
		for (var i = 0; i < _seriesList.length; i++) {
			keywords.push(_seriesList[i].label);
		}
		
		return keywords;
	};

	var _addKeywordHandler = function(e) {
		var keyword = $("#searchTextbox").val().toLowerCase().trim();

		if (keyword && keyword != "search keyword"
				&& _getSelectedKeywords().indexOf(keyword) < 0) {
			KeywordTrendsServiceJS.getStats(keyword, _effectiveFromDate,
					_effectiveToDate, {
						callback : function(data) {
							var series = new Series(data, true);

							_seriesList.push(series);
							_initChart();
						}
					});
		}

		$("#searchTextbox").val("Search Keyword");
	};

	var filterData = function(stats) {
		var filteredData = [];

		for (s in stats) {
			if (stats[s] || stats[s] === 0) {
				filteredData.push([ s, stats[s] ]);
			}
		}

		return filteredData;
	};

	// Series object containing functions needed for plotting graph
	var Series = function(data) {
		var stackElement = $("#keywordWidgetTemplate").clone();
		stackElement.attr("id", "keyword-" + data.keyword);
		stackElement.find(".keyword").html(data.keyword);
		$("#keyword-list").append(stackElement);

		var series = {
			el : stackElement,
			data : filterData(data.stats),
			label : data.keyword,
			show : true,

			showHideStack : function(visible) {
				if (visible === true) {
					this.el.show();
				} else {
					this.el.hide();
				}
			},

			showHideChart : function(visible) {
				this.show = visible === true;

				if (this.show) {
					this.el.find(".inactive-chart").hide();
					this.el.find(".active-chart").show();
				} else {
					this.el.find(".active-chart").hide();
					this.el.find(".inactive-chart").show();
				}
			},

			destroy : function() {
				this.el.remove();
				var idx = _seriesList.indexOf(this);

				_seriesList.splice(idx, 1);
				_initChart();
			},
		};

		stackElement.find(".keyword-delete").click(function() {
			series.destroy();
		});
		stackElement.find(".active-chart").click(function() {
			series.showHideChart(false);
			_initChart();
		});
		stackElement.find(".inactive-chart").click(function() {
			series.showHideChart(true);
			_initChart();
		});

		series.showHideChart(true);
		stackElement.show();

		return series;
	};
	
	var _getSeriesData = function() {
		var data = [];

		for ( var i = 0; i < _seriesList.length; i++) {
			data.push(_seriesList[i].data);
		}
		
		return data;
	};

	var _initChart = function() {
		if (_chart) {
			_chart.destroy();
		}

		_chart = $.jqplot('chart2', _getSeriesData(), {
			gridPadding : {
				right : 35
			},
			axes : {
				xaxis : {
					renderer : $.jqplot.DateAxisRenderer,
					tickRenderer : $.jqplot.CanvasAxisTickRenderer,
					tickOptions : {
						formatString : '%m-%d-%Y  ',
						angle : -60,
						fontSize: '11px',
						fontFamily: 'Arial',
						labelPosition: 'end'
					},
					tickInterval : '1 day',
					min : _effectiveFromDate,
					max : _effectiveToDate
				},
				yaxis: {
					tickOptions: {
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
				useAxesFormatters : false,
				tooltipAxes : 'y'
			},
			series : _seriesList
		});
	};

	var _initSideBar = function() {
		$("#addButton").click(_addKeywordHandler);
		$("#searchTextbox").focus(new SearchTextBoxFocusHandler(true));
		$("#searchTextbox").blur(new SearchTextBoxFocusHandler(false));
	};

	var _initDateFields = function(data) {

		if (data) {
			_effectiveToDate = data;
			_effectiveFromDate = new Date(data - 1000 * 9 * 60 * 60 * 24);
		}

		// initialize date pickers
		$("#fromDate").datepicker({
			constrainInput : true,
			defaultDate : _effectiveFromDate || -9,
			dateFormat : 'M d, yy'
		}).datepicker("setDate", _effectiveFromDate || "-9");

		$("#toDate").datepicker({
			constrainInput : true,
			defaultDate : _effectiveToDate || 0,
			dateFormat : 'M d, yy'
		}).datepicker("setDate", _effectiveToDate || "0");

		if (!data) {
			_effectiveFromDate = $("#fromDate").datepicker("getDate");
			_effectiveToDate = $("#toDate").datepicker("getDate");
		}

		KeywordTrendsServiceJS.getTopTenKeywords(_effectiveFromDate,
				_effectiveToDate, {
					callback : function(data) {
						for ( var i = 0; i < data.length; i++) {
							var series = new Series(data[i]);

							_seriesList.push(series);
						}

						_initChart();
					}
				});

		$("#updateDateBtn").click(dateUpdateHandler);
	};

	var KeywordTrends = {
		init : function() {
			$("#tabs").tabs();
			_initSideBar();
			KeywordTrendsServiceJS.getMostRecentStatsDate({
				callback : _initDateFields
			});
		}
	};

	$(document).ready(function() {
		KeywordTrends.init();
	});

})(jQuery);