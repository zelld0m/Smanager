<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="facet"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/settings/sponsor.css" />">
<script type="text/javascript" src="<spring:url value="/js/settings/sponsor.js" />" ></script>
  
<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
 	<div class="clearB floatL w240">
    	<div id="keywordSidePanel"></div>
        <div class="clearB"></div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
    <div class="floatL w730 titlePlacer">
      <div class="w535 padT10 padL10 floatL fsize20 fnormal">Facet Rule</div>
      <div class="floatL w180 txtAR padT7"><input id="addSortable" type="text" class="farial fsize12 fgray searchBox searchBoxIconLBg w85 marT1" maxlength="10"><a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> </div>
    </div>
    <div class="clearB"></div>
    
   	
</div> 

<!-- End Right Side -->	
<%@ include file="/WEB-INF/includes/footer.jsp" %>	