<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="elevate"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- Page specific file dependencies --> 
<link type="text/css" href="<spring:url value="/css/bigbets/bigbets.css" />" rel="stylesheet">
<script type="text/javascript" src="<spring:url value="/js/bigbets/bigbets.js" />"></script>   
<script type="text/javascript" src="<spring:url value="/js/bigbets/elevate.js" />"></script>   

<!-- Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="clearB floatL w240">
		<div id="rulePanel"></div>
	    <div class="clearB"></div>
	</div>
</div>
<!--Left Menu-->

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">

	<div class="floatL w730 titlePlacer">	
	  <div id="addRuleItemContainer" class="floatR txtAR padT7" style="display: none"></div>
      <div class="w480 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>
	<div class="clearB"></div>	 
	
	<div id="submitForApproval"></div>
	<div class="clearB"></div>	
	
	<!--Top Paging-->
  	<div id="ruleItemPagingTop" class="floatL txtAL w550"></div>
   
	<!--Start Rule Item Options-->
	<div id="ruleItemDisplayOptions" style="display: none">
		<ul class="viewSelect marT6">
			<li id="optionSplitter" class="fLgray2">|</li>
			<li class="padR5 fLgray2">
				<select id="filterDisplay" class="marTn3">
					<option value="all">All</option>
					<option value="active">Active</option>
					<option value="expired">Expired</option>
				</select>
			</li>
			<li class="padT1"><a href="javascript:void(0);" id="tileViewIcon" class="btnGraph" alt="Grid View" title="Grid View"><div class="btnGraph btnViewTile"></div></a></li>
			<li class="padT1"><a href="javascript:void(0);" id="listViewIcon" class="btnGraph" alt="List View" title="List View"><div class="btnGraph btnViewList"></div></a></li>
			<li class="padT1"><a href="javascript:void(0);" id="downloadRuleItemIcon"><div class="btnGraph btnDownload marT1 marL3" alt="Download" title="Download" ></div></a></li>
			<li class="padT1"><a href="javascript:void(0);" id="clearRuleItemIcon"><div class="btnGraph btnClearDel marT1" alt="Remove All" title="Remove All"></div></a></li>
		</ul>
	</div>
	<!--End Rule Item Options-->
	
   	<!--Start Content Area -->
  	<div id="ruleItemContainer" class="listView">
  		<div id="preloader" class="circlePreloader"><img src="<spring:url value="/images/ajax-loader-circ.gif" />"></div>
		<div id="noSelected"><img src="<spring:url value="/images/elevateRuleGuidelines.jpg" />"></div>
		<div id="ruleSelected" style="display:none">
	  		<ul id="ruleItemHolder" class="boxContainer">
	  			<li id="ruleItemPattern" class="ruleItem" style="display:none">
	  				<div class="bgShade pad8">	
	  					<div class="iconGroup">
	  						<label class="iconGroupW floatL fsize11">
	  							<span class="validityDays fLblue fbold"></span>
	  							<span style="display: none" class="validityDaysExpired fLblue fbold">
	  								<img src="<spring:url value="/images/expired_stamp50x16.png" />">
	  							</span>
	  							&nbsp;
	  						</label>
	  						<label class="floatR">
								<ul class="listIcons">
									<li><img src="<spring:url value="/images/icon_comment.png" />"></li>
									<li><img class="auditRuleItemIcon" src="<spring:url value="/images/icon_history.png" />"></li>
									<li><img src="<spring:url value="/images/user_red.png" />"></li>
									<li><img src="<spring:url value="/images/icon_date.png" />"></li>
									<li><img class="deleteRuleItemIcon" src="<spring:url value="/images/icon_delete2.png" />"></li>
								</ul>
			  				</label>
	  					</div>
	  				
		  				<div class="sortOrder">
		  					<label class="w90">Elevation : </label>
		  					<label>
		  						<input type="text" class="sortOrderTextBox txtBoxSmall w30">
		  					</label>
		  				</div>
					
		  				<div class="validityDate padT3">
		  					<label class="w90">Valid Until : </label>
		  					<label>
		  						<input type="text" class="validityDateTextBox txtBoxSmall w60 floatL marR3" />		  						
		  					</label>
		  					<label class="padL3"><img class="clearDate" src="<spring:url value="/images/icon_calendarDelete.png" />"></label>
		  				</div>
		  				<div class="clearB"></div>
	  				</div>
	  				
	  				<div class="picArea">
	  					<img class="itemImg" src="<spring:url value="/images/no-image.jpg" />">
	  				</div>  					
		  			
		  			<div class="proInfo">
		  				<div class="manufacturer titleArea"></div>	
		  				<div class="clearB"></div>
			  			<div class="name proName breakWord"></div>
			  			<p class="textInfo">
			  				<span class="fgreen">SKU #:</span>
			  				<span class="sku"></span>
			  			</p>
			  			<p class="textInfo">
			  				<span class="fgreen">Mfr. Part #:</span>
			  				<span class="mfrpn"></span>
			  			</p>
		  			</div>
	  			</li>
	  		</ul>
  		</div>
  	</div>
  	<div class="clearB"></div>
   	<!--End Content Area -->
   
  	<!--Bottom Paging-->
  	<div id="ruleItemPagingBottom" class="w730 floatL txtAL marT20"></div>
  	<div class="clearB"></div>

</div>
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>	