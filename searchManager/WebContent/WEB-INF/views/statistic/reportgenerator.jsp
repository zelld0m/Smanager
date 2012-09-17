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
	
	<div>
		<select id="fileFormat">
			<option>Select a File Format</option>
		</select>
		<select id="reportType">
			<option>Select a Report</option>
		</select>
		<a href="javascript:void(0);" id="generateBtn"> generate from file: </a>			
		<input type="file" id="keywordFile"/>
	</div>
	
	
</div> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	