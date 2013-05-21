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
	
			<!-- Banner Details -->		
			<div id="rule" class="marB10 floatL txtAC" style="width:98%">
				<div>
					<span class="floatL">Hello</span>
					<span class="floatR">This</span>
				</div>
				<div class="clearB"></div>

				<div id="name">
					<label class="marT5 txtAL w80"><span id="fieldLabel" class="fbold fsize13">Name</span></label>
					<label class="marT5"><input id="name" type="text" class="w460"/></label> 
				</div>
				<div class="clearB"></div>
				
				<div id="description">
					<label class="marT5 txtAL w80"><span id="fieldLabel" class="fbold fsize13">Description</span></label>
					<label class="marT5">
						<textarea id="description" rows="1" cols="55"></textarea>
					</label> 
				</div>
			</div>
			 
			 <div id="bannerImage" class="marB10 floatL txtAC" style="width:98%">	
				<div>
					<span class="floatL">Banner Preview</span>
					<span class="floatR"><a id="editImageLink" href="javascript:void(0);">Edit Image</a></span>
				</div>
				<div id="preview" class="imagePreview minHeight185 floatL w650 marL10 marT5">
					<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
					<img id="imagePreview" src="<spring:url value="/images/nopreview.png" />" onerror="this.onerror=null;this.src='<spring:url value="/images/nopreview.png" />';"/>
				</div>
			</div>
			<div class="clearB"></div>
			
			<!-- Banner Relationships -->
			<div id="bannerRelations">
				<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
				<div id="bannerToCampaign"></div>
			</div>
			<div class="clearB"></div>
			
			<div class="txtAR padT10">
				<a id="updateBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Update</div></a> 
				<a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
			</div>
		</div>
		<div class="clearB"></div>
	</div>
	
</div>
<!--  end right side -->
<%@ include file="/WEB-INF/includes/footer.jsp" %>	