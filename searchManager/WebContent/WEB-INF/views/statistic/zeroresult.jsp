<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="statistic"/>
<c:set var="submenu" value="zeroresult"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/statistics/zeroresult.js" />"></script>
	    
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
		<span id="titleText">Zero Result</span>
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
	
	<div class="w100p padT0 marT20 marL15 fsize12" style="max-height:365px;">	
		<div id="itemPattern" class="items border clearfix" >
			<label class="iter floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee"> &nbsp; </label>
			<label class="count floatL w80 txtAC fbold padTB5" style="border-right:1px solid #cccccc; background:#eee">Count</label>
			<label class="floatL w535 txtAC fbold padTB5" style="background:#eee">Keyword</label>
		</div>
	</div>	
	<div id="itemList" class="w95p marRLauto padT0 marT0 fsize12" style="max-height:365px; overflow-y:auto">	
		<div id="itemPattern" class="items pad5 borderB clearfix" style="display:none">
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

<%@ include file="/WEB-INF/includes/footer.jsp" %>	