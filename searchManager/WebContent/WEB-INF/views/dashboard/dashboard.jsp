<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="dashboard"/>
<c:set var="submenu" value="topkeyword"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/statistics/topkeyword.js" />"></script>	    
<!-- Left Menu-->
<div class="clearB floatL minW240 sideMenuArea">
	&nbsp;
    <div class="clearB floatL w240">
		<!-- div class="sidebarHeader farial fsize16 fwhite bluebgTitle">Keyword Trends</div>
		<div class="leftStatus">
			<img src="<spring:url value="/images/information.png" />" class="marR3 marBn3">
			<span class="fgreen">Most Searched</span>
		</div>
		<table border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray w220 marL8">
		    <c:forEach var="i" begin="1" end="5" step="1">
		    <tr>
		   	<td class="borderB padTB5">
		    	<span class="lnk"><a href="#">
		    	 Keyword ${i}</a>
		    	</span>
		   	</td>
		       <td class="borderB padTB5 txtAR scorelink">
		       	<span class="lnk"><a href="#">Related Info</a></span>
		       </td>
		    </tr>
		    </c:forEach>
        </table>
        
        <div class="leftStatus">
        	<img src="<spring:url value="/images/information.png" />" class="marR3 marBn3">
        	<span class="fgreen">Latest Searched</span>
        </div>
	   <table border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray w220 marL8">
		   <c:forEach var="i" begin="1" end="5" step="1">
		   <tr>
		   	<td class="borderB padTB5">
		    	<span class="lnk"><a href="#">
		    	 Keyword ${i}</a>
		    	</span>
		   	</td>
		       <td class="borderB padTB5 txtAR scorelink">
		       	<span class="lnk"><a href="#">Related Info</a></span>
		       </td>
		   </tr>
		   </c:forEach>
	  </table -->
	</div>
</div>
<!-- Left Menu-->

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

<%@ include file="/WEB-INF/includes/footer.jsp" %>	