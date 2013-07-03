<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="statistic"/>
<c:set var="submenu" value="topkeyword"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/dwr/interface/TopKeywordServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/statistic/topkeyword.js" />"></script>
	    
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
	<div class="w95p marRLauto">
		<table class="tblAlpha w100p marT8" >
			<tr>
				<th width="75px" id="selectAll"></th>
				<th width="480px" class="txtAL">Keyword</th>
				<th width="135px">Count</th>
			</tr>
		</table>
	</div>
	<div class="w95p marRLauto padT0 marT0" style="max-height:365px; overflow-y:auto">
		<table id="keywordTable" class="tblAlpha padT0 marT0" width="100%">
			<tr id="rowPattern" class="rowItem" style="display: none">
				<td width="69px" id="iter"></td>
				<td width="461px" id="keyword"></td>
				<td id="count"></td>
			</tr>
		</table>
	</div>
</div> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	