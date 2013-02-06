<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="statistic"/>
<c:set var="submenu" value="topkeyword"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" href="<spring:url value="/css/statistic/topkeywords.css" />" rel="stylesheet">
<script type="text/javascript" src="<spring:url value="/js/statistics/topkeyword.js" />"></script>
	    
<!-- Left Menu-->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
	</div>
</div>
<!--Left Menu-->

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	
	<div class="floatL w730 titlePlacer">		
	  <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText">Top Keyword</span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>  
	
	<div class="clearB"></div>
	
	<div style="width:95%" class="dashboard marT20 mar0 fsize12">
	This displays keywords statistics for a given period of time(e.g. <i>${store}_<b>summary</b>_&lt;date&gt;-splunk.csv</i> shows statistics for the whole week ending on the given date while <i>${store}_<b>daily_summary</b>_&lt;date&gt;-splunk.csv</i> shows daily statistics for the given day).
	<ul class="marL15">
		<li>You may specify custom date range of keyword statistics in the "Custom Range" tab.</li>
		<li>If you want to obtain a copy of the report, click on the download icon to download as CSV or send as email attachment to a specific recipient. The CSV file contains the list of keywords and search count.
		The search result count and first SKU takes a time to process so it is available only in email. If you want to obtain these information as well, please send report as email attachment.</li>
	</ul>
	</div>
	
	<div class="clearB"></div>
	
	<div id="tabs">
		<ul>
		    <li><a id="report-link" href="#report-tab">Reports</a></li>
		    <li><a id="custom-link" href="#custom-tab">Custom Range</a></li>
		</ul>
		<div id="report-tab">
			<div class="filter padT5 fsize12 marT8 w95p marRLauto">
				<div class="floatL w50p">
					<span>Show:</span> 
					<select id="fileFilter"></select>
					<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">			
				</div>
				<div class="floatL w50p" id="countSec">
					<div class="floatR marL5"><a href="javascript:void(0);" id="downloadBtn"><div class="btnGraph btnDownload"></div></a></div>
					<div class="floatR padT3" id="keywordCount"></div>		
			    </div>
			</div>

			<div class="clearB"></div>

			<div class="w100p padT0 marT20 marL15 fsize12" style="max-height:365px;">	
				<div id="itemHeader1" class="items border clearfix" style="display:none">
					<label class="iter floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>
					<label class="count floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>
					<label class="floatL w535 txtAC fbold padTB5" style="background:#eee">Keyword</label>
				</div>
				<div id="itemHeader2" class="items border clearfix" style="display:none">
					<label class="iter floatL w45 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>
					<label class="count floatL w70 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>
					<label class="floatL w320 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Keyword</label>
					<label class="results floatL w70 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Results</label>
					<label class="sku floatL w70 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">SKU</label>
					<label class="toggle floatL w120 txtAC fbold padTB5" style="background:#eee"> &nbsp; </label>
				</div>
			</div>	
			<div id="itemList" class="w95p marRLauto padT0 marT0 fsize12" style="max-height:365px; overflow-y:auto">
				<div id="itemPattern1" class="items pad5 borderB mar0 clearfix" style="display:none">
					<label class="iter floatL w80"></label>
					<label class="count floatL w80"></label>
					<label class="floatL w500">
						<label class="keyword floatL w400"></label> 
						<label class="floatL fsize11 w100">
							<a class="toggle" href="javascript:void(0);"></a>
						</label>
						<div class="rules" style="display:none"></div>
					</label>
				</div>
				<div id="itemPattern2" class="items pad5 borderB mar0 clearfix" style="display:none">
					<label class="iter floatL w45"></label>
					<label class="count floatL w70"></label>
					<label class="floatL w320">
						<label class="keyword floatL w310"></label> 
						<div class="rules" style="display:none"></div>
					</label>
					<label class="results floatL w70"></label>
					<label class="sku floatL w70"></label> 
					<label class="floatR fsize11 w90 txtAC">
						<a class="toggle" href="javascript:void(0);"></a>
					</label>
				</div>
			</div>
		</div>
		<div id="custom-tab">
			<div class="filter padT5 fsize12 marT8 w95p marRLauto">
				<div class="floatL w50p">
					<div class="floatL w500 padTB5 padR5 marL20">
					    <span>From:</span>
					    <input type="text" id="fromDate" class="dateLabel" readonly="readonly"/>
					    <span>To:</span>
					    <input type="text" id="toDate" class="dateLabel" readonly="readonly"/>
					    <a class="buttons btnGray clearfix" href="javascript:void(0);" id="updateDateBtn" style="vertical-align: middle;">
					        <div class="buttons fontBold">View</div>
					    </a>
			    		<img id="customPreloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>" style="display:none">		
					</div>
				</div>
				<div class="floatL w50p" id="customCountSec" style="display:none;">
					<div class="floatR marL5"><a href="javascript:void(0);" id="customDownloadBtn"><div class="btnGraph btnDownload"></div></a></div>
					<div class="floatR padT3" id="customKeywordCount"></div>		
			    </div>
			</div>

			<div class="clearB"></div>

			<div class="w100p padT0 marT20 marL15 fsize12" style="max-height:365px;">	
				<div id="itemHeader" class="items border clearfix" style="display:none">
					<label class="iter floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>
					<label class="count floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>
					<label class="floatL w535 txtAC fbold padTB5" style="background:#eee">Keyword</label>
				</div>
			</div>	
			<div id="customRangeItemList" class="w95p marRLauto padT0 marT0 fsize12" style="max-height:365px; overflow-y:auto">
				<div id="itemPattern" class="items pad5 borderB mar0 clearfix" style="display:none">
					<label class="iter floatL w80"></label>
					<label class="count floatL w80"></label>
					<label class="floatL w500">
						<label class="keyword floatL w400"></label> 
						<label class="floatL fsize11 w100">
							<a class="toggle" href="javascript:void(0);"></a>
						</label>
						<div class="rules" style="display:none"></div>
					</label>
				</div>
			</div>
		</div>
	</div>
</div> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	