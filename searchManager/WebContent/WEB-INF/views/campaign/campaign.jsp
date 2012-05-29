<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="advertise"/>
<c:set var="submenu" value="campaign"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/campaign/campaign.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/campaign/campaign.css" />" rel="stylesheet" type="text/css">

<!--Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="companyLogo">
		<a href="#"><img src="<spring:url value="${storeLogo}" />"></a>
	</div>
	<div class="clearB floatL w240">&nbsp;</div>
	<div class="sideHeader">Most Popular</div>
	<div class="marT10 txtAC"><a href=""><img src="<spring:url value="/images/productItems/imgMostPopular.jpg" />">
		<p style="color:#4e4d4d; font-size:11px">Lore ipsum dolor  <span style="color:#005da2">(26901)</span></p></a>
	</div>
	
	<div class="sideHeader">Recently Added</div>
		<div class="padL10 padT10">
			<ul class="recentlyAdded">
				<li><a href=""><img src="<spring:url value="/images/productItems/resentlyAdded1.jpg" />"><p>Lorem <span>(26901)</span></p></a></li>
				<li><a href=""><img src="<spring:url value="/images/productItems/resentlyAdded2.jpg" />"><p>Lorem <span>(26901)</span></p></a></li>
				<li><a href=""><img src="<spring:url value="/images/productItems/resentlyAdded1.jpg" />"><p>Lorem <span>(26901)</span></p></a></li>
				<li><a href=""><img src="<spring:url value="/images/productItems/resentlyAdded2.jpg" />"><p>Lorem <span>(26901)</span></p></a></li>
			</ul>
		</div>
	
</div>
<!--Left Menu-->



<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">Campaign List</div>
        <div class="floatL w180 txtAR padT7">
        <a href="javascript:void(0);" id="addSortableImg" class="btnGraph">
        <div class="btnGraph btnAddGrayL floatR marT1"></div></a> 
        <div class="searchBoxHolder w85 marT1 floatR">
        	<input id="addSortable" type="text" class="farial fsize12 fgray w85" maxlength="10" value="">
        </div>
        </div>
    </div>
	<div class="clearB"></div>
	
	<div class="campaignContent">
		<ul>
		    <c:forEach var="i" begin="1" end="6" step="1">
		    <li>
				<div id="mG${i}" class="microGallery" title="Campaign Name|Jan 15 - Dec 15">
						<img src="http://t0.gstatic.com/images?q=tbn:ANd9GcRZynMzjEr8vMvQbt5zUmCkI52bxXkW4ElcIIfRU9csAjE9tAqRN2bk3AkW"/>
						<img src="http://t2.gstatic.com/images?q=tbn:ANd9GcRTqfWIn_gA1034cj3zFLElp62rHyJj8Zrxc8KMvx-WVuu5gSHP"/>
						<img src="http://t3.gstatic.com/images?q=tbn:ANd9GcSEuglQt5IId5Y_UfQOP55l8V2uPLb8KGtbL7t8s7iXB2zucT2lnw"/>
						<img src="http://t1.gstatic.com/images?q=tbn:ANd9GcT_rEtxR8JPtb3MDtn5do3GOHyvgHJ6mkvK_wvxZ6t_P7ASqjeQ"/>
						<img src="http://t1.gstatic.com/images?q=tbn:ANd9GcQLKMYGMHenPHLWQggtsIgn5l8HtQYQv4cWKsS6ZaY5xWQ5SKOsmA"/>
						<img src="http://t2.gstatic.com/images?q=tbn:ANd9GcREnV_irVsWDX8Q8LSlji5BA1Cz0hNtFCnc6zZitSaroYEFKYSKQA"/>
				</div>
			</li>
			</c:forEach>
		</ul>
	</div>
	
		
	<div class="clearB"></div>
</div>
<!--  end right side -->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	