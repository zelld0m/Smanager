<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="statistic"/>
<c:set var="submenu" value="keywordtrends"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" href="<spring:url value="/css/statistic/keywordTrends.css" />" rel="stylesheet">

<script type="text/javascript" src="<spring:url value="/js/jquery/jqplot/plugins/jqplot.canvasTextRenderer.min.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/jqplot/plugins/jqplot.canvasAxisTickRenderer.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/jqplot/plugins/jqplot.dateAxisRenderer.min.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/jqplot/plugins/jqplot.highlighter.min.js" />"></script>

<script type="text/javascript" src="<spring:url value="/js/statistics/keywordtrends.js" />"></script>
	    
<!-- Left Menu-->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="clearB floatL w240">
		<div class="sideHeader farial fsize16 fwhite bluebgTitle">Keywords</div>
		<div class="sideSearch">
		    <span style="padding-top:7px">
			    <a id="addButton" class="btnGraph btnAddGreen floatR" href="javascript:void(0);"></a>
			    <div class="searchBoxHolder w70p">
			        <input id="searchTextbox" maxlength="50" class="farial fsize12 fgray w99p" type="text" value="Search Keyword">
			    </div>
			    <div class="clearB"></div>
		    </span>
		</div>
		<div id="keyword-list" class="filter padT5 padL5 fsize12 marT8 w95p marRLauto">
		    <div id="keywordWidgetTemplate" class="itemRow" style="display:none">
	            <div class="keywordIcons floatR">
	                <a href="javascript:void();" class="active-chart">
	                    <img src="/searchManager/images/chart_active.png">
	                </a>
	                <a href="javascript:void();" class="inactive-chart" style="display:none">
	                    <img src="/searchManager/images/chart_inactive.png">
	                </a>
	                <a href="javascript:void();" class="keyword-delete">
	                    <img src="/searchManager/images/icon_del.png">
	                </a>
	            </div>
		        <div class="itemHolder padB5">
	                <div class="keyword w180">Keyword</div>
	            </div>
	            <div class="clearB"></div>
	        </div>
		</div>
			    
	</div>
</div>
<!--Left Menu-->

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	
	<div class="floatL w730 titlePlacer">		
	  <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText">Keyword Trends</span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>  
	
	<div class="clearB"></div>
	<div id="tabs">
		<ul>
		    <li><a id="daily-link" href="#tabs-1">Daily</a></li>
		    <li><a id="weekly-link" href="#tabs-2">Weekly</a></li>
		    <li><a id="monthly-link" href="#tabs-3">Monthly</a></li>
		</ul>
		<div id="tabs-1" class="filter padT5 fsize12 marT1">
			<div class="floatL w180 padTB5 padR5 marL20">
			    <span class="fbold">From:</span>
			    <input type="text" id="fromDate" class="dateLabel" readonly="readonly"/>
			</div>
			<div class="floatL w180 padTB5 padR5">
			    <span class="fbold">To:</span>
			    <input type="text" id="toDate" class="dateLabel" readonly="readonly"/>
			</div>		
			<div class="floatL marT4">
			    <a class="buttons btnGray clearfix" href="javascript:void(0);" id="updateDateBtn">
			        <div class="buttons fontBold">Update Chart</div>
			    </a>
			</div>
			<div class="clearB"></div>
			<hr />
			<div id="daily-chart" class="chart" style="font-size: 12px;"></div>
		</div>
		<div id="tabs-2" class="filter padT5 fsize12 marT1">
		    <p>Weekly tab</p>
		    <div class="clearB"></div>
		</div>
		<div id="tabs-3" class="filter padT5 fsize12 marT1">
			<div class="floatL padTB5 padR5 marL20">
			    <span class="fbold">From:</span>
			    <select id="fromYear">
			        <option>2012</option>
			    </select>
			    <select id="fromMonth">
			        <option value="01">January</option>
			        <option value="02">February</option>
			        <option value="03">March</option>
			        <option value="04">April</option>
			        <option value="05">May</option>
			        <option value="06">June</option>
			        <option value="07">July</option>
			        <option value="08">August</option>
			        <option value="09">September</option>
			        <option value="10">October</option>
			        <option value="11">November</option>
			        <option value="12">December</option>
			    </select>
			</div>
			<div class="floatL padTB5 padR5 marL20">
			    <span class="fbold">To:</span>
			    <select id="toYear">
			        <option>2012</option>
			    </select>
			    <select id="toMonth">
			        <option value="01">January</option>
			        <option value="02">February</option>
			        <option value="03">March</option>
			        <option value="04">April</option>
			        <option value="05">May</option>
			        <option value="06">June</option>
			        <option value="07">July</option>
			        <option value="08">August</option>
			        <option value="09">September</option>
			        <option value="10">October</option>
			        <option value="11">November</option>
			        <option value="12">December</option>
			    </select>
			</div>		
			<div class="floatL marT6 marL20">
			    <a class="buttons btnGray clearfix" href="javascript:void(0);" id="updateMonthlyBtn">
			        <div class="buttons fontBold">Update Chart</div>
			    </a>
			</div>
			<div class="clearB"></div>
			<hr />
			<div id="monthly-chart" class="chart" style="font-size: 12px;"></div>
		</div>
	</div>
</div> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	