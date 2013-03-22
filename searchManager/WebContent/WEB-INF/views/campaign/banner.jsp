<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="advertise"/>
<c:set var="submenu" value="banner"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/campaign/banner.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/campaign/banner.css" />" rel="stylesheet" type="text/css">

  <!--Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="clearB floatL w240">&nbsp;</div>
	
</div>
<!--Left Menu-->

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="clearB floatL w240">
		<div id="keywordSidePanel"></div>
		&nbsp;
		<div class="clearB"></div>
	</div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
    	<div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">Banner List</div>
        <div class="floatL w180 txtAR padT7">
	        <a href="javascript:void(0);" id="addPromoBannerIcon" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a>
	        <div class="searchBoxHolder w85 marT1 floatR">
	        	<input id="addSortable" type="text" class="farial fsize12 fgray w85" maxlength="10" value="Esdsd"> 	
	        </div>       
		</div>
	</div>
	<div class="clearB"></div>
	
	<div id="submitForApproval"></div>
	<div class="clearB"></div>
	<div id="bannerContainer" style="width:95%" class="marT20 mar0">
		<div id="preloader" class="circlePreloader" style="display:none"><img src="<spring:url value="/images/ajax-loader-circ.gif" />"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/bannerGuidelines.jpg"></div>
	
		<div id="bannerContent" class="bannerContent">
		</div>
		<div class="clearB"></div>
	</div>
	
</div>
<!--  end right side -->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	