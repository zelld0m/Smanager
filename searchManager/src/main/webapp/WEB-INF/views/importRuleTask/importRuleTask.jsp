<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="workflow"/>
<c:set var="submenu" value="importRuleTask"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- Page specific file dependencies --> 
<link type="text/css" href="<spring:url value="/css/bigbets/bigbets.css" />" rel="stylesheet"> 
<script type="text/javascript" src="<spring:url value="/js/settings/importRuleTask.js" />"></script>  

<!-- Left Menu-->
<div class="clearB floatL sideMenuArea">
	<jsp:include page="sideMenu.jsp"/>
</div>
<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	
	<div class="floatL w730 titlePlacer">
	  <div class="w480 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>	    
	</div>
	<div class="clearB"></div>	 
	<div id="submitForApproval"></div>
	<div class="clearB"></div>	
	<!--Top Paging-->
	<div id="mainContainer">
		<jsp:include page="list.jsp"/>
	</div>
  	<div class="clearB"></div>

</div>
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>	
