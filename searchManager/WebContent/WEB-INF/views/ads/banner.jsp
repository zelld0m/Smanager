<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="ads"/>
<c:set var="submenu" value="banner"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/ads/banner.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/ads/banner.css" />" rel="stylesheet" type="text/css">

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="clearB floatL w240">
		<div id="rulePanel"></div>
		<div class="clearB"></div>
	</div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="contentArea floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
    	<div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
			<span id="ruleTypeIcon" class="ruleTypeIcon marR5 posRel top3"></span>
			<span id="titleText"></span>
			<span id="titleHeader" class="fLblue fnormal"></span>
		</div>
		<div id="addBannerBtn" class="floatR padT10 padL10">	        	
			<a id="addRuleItem" href="javascript:void(0);" class="buttons btnGray clearfix">
				<div class="buttons fontBold">Add Banner</div>
			</a>
		</div>
	</div>
	<div class="clearB"></div>
	
	<div id="ruleStatus"></div>
	<div class="clearB"></div>
	
	<div id="ruleContent" style="width:95%" class="marT20 mar0">

		<div id="preloader" class="circlePreloader" style="display:none">
			<img src="<spring:url value="/images/ajax-loader-circ.gif" />">
		</div>

		<div id="infographic">
			<img id="no-items-img" src="../images/bannerGuidelines.jpg">
		</div>
	
		<div id="ruleContent" class="ruleContent fsize12" style="display:none">
		
			<div id="ruleItemHolder">
				<div id="ruleItemPattern" class="ruleItem" style="display:none">
					 <div id="bannerHeader">	
					 	<div class="floatL">
					 		<span>
					 			<label>Priority</label>
					 			<input type="text">
					 		</span>
							<span>Banner Alias Here</span>
					 	</div>
					 	<div class="floatL">
					 		<span>
					 			<label>Schedule</label>
					 			<input class="startDate" type="text">
					 			<input class="endDate" type="text">
					 		</span>
					 	</div>
					 </div>
					 
					 <div id="bannerImage" class="marB10 floatL txtAC" style="width:98%">	
						<div id="preview" class="imagePreview minHeight185 floatL w650 marL10 marT5">
							<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
							<img id="imagePreview" src="<spring:url value="/images/nopreview.png" />" onerror="this.onerror=null;this.src='<spring:url value="/images/nopreview.png" />';"/>
						</div>
					 </div>
					 
					 <div id="bannerInfo" class="marB10 floatL txtAC" style="width:98%">
					 	<div id="bannerInfoHeader" class="marB10 floatL txtAC" style="width:98%">
					 
					 	</div>	
					  	<div id="bannerInfoContent" class="marB10 floatL txtAC" style="width:98%">
					 		<div>
					 			<span><label for="imagePath">Image Path:</label></span>
					 			<span><input id="imagePath" type="text"></span>
					 		</div>
					 		<div>
						 		<div class="clearfix">
						 			<span><label for="alias">Image Alias:</label></span>
						 			<span><input id="alias" type="text" readonly="readonly" disabled="disabled"></span>
						 			<span>
							 			<div id="updateAliasBtn" class="floatR padT10 padL10">	        	
											<a id="updateAlias" href="javascript:void(0);" class="buttons btnGray clearfix">
												<div class="buttons fontBold">Update Alias</div>
											</a>
										</div>
						 			</span>
						 		</div>
						 		<div class="clearfix">
						 			<span><label for="imageAlt">Image Alt:</label></span>
						 			<span><input id="imageAlt" type="text"></span>
						 		</div>
					 		</div>
					 		<div>
					 			<span><label for="linkPath">Link Path:</label></span>
					 			<span><input id="linkPath" type="text"></span>
					 		</div>
					 		<div>
					 			<span><label for="description">Description:</label></span>
					 			<span><textarea id="description"></textarea></span>
					 		</div>
					 		<div>
						 		<div>
						 			<span></span>
						 			<span><input id="disabled" type="checkbox"><label for="disabled">Temporarily Disable</label></span>
						 		</div>
						 		<div>
							 		<div id="deleteBannerBtn" class="floatR padT10 padL10">	        	
										<a id="deleteRuleItem" href="javascript:void(0);" class="buttons btnGray clearfix">
											<div class="buttons fontBold">Delete</div>
										</a>
									</div>
									<div id="updateBannerBtn" class="floatR padT10 padL10">	        	
										<a id="updateRuleItem" href="javascript:void(0);" class="buttons btnGray clearfix">
											<div class="buttons fontBold">Update</div>
										</a>
									</div>
						 		</div>
					 		</div>
					 	</div>	
					 </div>	
					 <div class="clearB"></div>
				</div>
			</div>
			
		</div>
		
		<div class="clearB"></div>
	</div>
	
</div>
<!--  end right side -->
<%@ include file="/WEB-INF/includes/footer.jsp" %>	