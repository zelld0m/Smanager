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
	
	<div class="filter padT5 fsize12 marT1">
		<div class="floatL w180 padTB5 padR5 marL20"><span class="fbold">From:</span> <input type="text" id="fromDate" class="dateLabel" readonly="readonly"/></div>
		<div class="floatL w180 padTB5 padR5"><span class="fbold">To:</span> <input type="text" id="toDate" class="dateLabel" readonly="readonly"/></div>		
		<div class="floatL marT4"><a class="buttons btnGray clearfix" href="javascript:void(0);" id="updateDateBtn"><div class="buttons fontBold">Update Date Range</div></a></div>
		<div class="clearB"></div>
		<!-- <button id="updateDateBtn">Update Date Range</button>  -->
	</div>

	<div class="clearB"></div>
	
	<div id="chart2" style="font-size: 12px;"></div>
</div> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	