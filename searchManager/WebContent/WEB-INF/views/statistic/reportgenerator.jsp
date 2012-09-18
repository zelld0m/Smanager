<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="statistic"/>
<c:set var="submenu" value="reportgenerator"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/statistics/reportgenerator.js" />"></script>
	    
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
		<span id="titleText">Report Generator</span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>  
	
	<div class="clearB"></div>
	
	<div class="report marT30 marL30 fsize12 ">
		<label class="floatL w100 padTB7">File Format : </label>
		<label class="padTB7 padL10">
			<select id="fileFormat" class="w215"> 
				<option value="">Select a File Format</option>
			</select>
		</label>
		<div class="clearB"></div>
		
		<label  class="floatL w100 padTB7">Report :</label>
		<label class="padTB7 padL10">
			<select id="reportType" class="w215">
				<option value="">Select a Report</option>
			</select>
		</label>
		<div class="clearB"></div>
		
		<label  class="floatL w100 padTB7">Browse :</label>
		<label class="padTB7"><input type="file" id="file" class="w120 marL20"/></label>
		<div class="clearB"></div>
		
		<label class="floatL w340 padTB2 txtAR">
			<a class="buttons btnGray clearfix" href="javascript:void(0);" id="generateBtn"><div class="buttons fontBold">Generate from file</div></a>
		</label>
		
		
		

	</div>
	
	
</div> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	