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

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
		
	  <div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">Banner List</div>
        <div class="floatL w180 txtAR padT7">
        <a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> 
        <div class="searchBoxHolder w85 marT1 floatR">
        	<input id="addSortable" type="text" class="farial fsize12 fgray w85" maxlength="10" value="Esdsd"> 	
        </div>       
      </div>

	<div class="clearB"></div>
	
	<div id="content" class="bannerContent">
		<ul class="sortableTile">
		    <c:forEach var="i" begin="1" end="6" step="1">
		    <li>
				<div class="sortableBox clearfix">
					<div class="marB10 bannerName">sample name  </div>
					<div class="floatR posRel padR10" style="z-index:1"><a id="sItemDelete" href="javascript:void(0);"><img src="<spring:url value="/images/btn_delete_graybg.jpg" />"></a></div>
					<img src="http://t2.gstatic.com/images?q=tbn:ANd9GcRTqfWIn_gA1034cj3zFLElp62rHyJj8Zrxc8KMvx-WVuu5gSHP" alt="logo" width="320px"/>
					<div class="listalpha">
			         	<input id="sItemPosition" type="text" class="sItemPosition txtBoxSmall farial marL4 w30"/> Included in 3 campaign
			         </div>
			         <div class="txtAR w60 floatL  fgray fsize11 padT5">
			         	<img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> 
			         	<img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History">
					 </div>
					 <div class="clearB"></div>
			         <p class="fgray padT5 fsize11 txtAL">
			         	<img src="<spring:url value="/images/user_red.png" />" class="marBn4 marR3">
			         	<span id="sItemModBy" class="fbold"></span> on <span id="sItemModDate" class="fDblue"></span>
			         </p>
				</div>
			</li>
			</c:forEach>
		</ul>
	</div>
	
		
	<div class="clearB"></div>
</div>
<!--  end right side -->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	