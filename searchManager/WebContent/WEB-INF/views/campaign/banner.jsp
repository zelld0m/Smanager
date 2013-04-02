<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="advertise"/>
<c:set var="submenu" value="banner"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/campaign/banner.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/campaign/banner.css" />" rel="stylesheet" type="text/css">

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
<div class="contentArea floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
    	<div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
			<span id="ruleTypeIcon" class="ruleTypeIcon marR5 posRel top3"></span>
			<span id="titleText"></span>
			<span id="titleHeader" class="fLblue fnormal"></span>
		</div>
	</div>
	<div class="clearB"></div>
	
	<div id="submitForApproval"></div>
	<div class="clearB"></div>
	
	<div id="versions"></div>
	<div class="clearB"></div>
	
	<div id="ruleContainer" style="width:95%" class="marT20 mar0">
		<div id="preloader" class="circlePreloader" style="display:none"><img src="<spring:url value="/images/ajax-loader-circ.gif" />"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/bannerGuidelines.jpg"></div>
	
		<div id="ruleContent" class="ruleContent fsize12">
			<div class="landingCont bgboxGray w45p83 minHeight185 floatL">	
				<div class="fsize14 txtAL borderB padB4 marB8 fbold">
					<div class="floatL">Rule Name</div>
					<div class="floatR">
						<span class="floatR"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1 marL3" id="downloadIcon" alt="Download" title="Download"></div></a></span>
						<span class="floatR"><img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History"></span>
					</div>
					<div class="clearB"></div>
				</div>		
				<label class="floatL w70 marT5 padT3">Name</label>
				<label><input id="name" type="text" class="w240 marT5"/></label>
				<div class="clearB"></div>			
				<label class="floatL w70 marT8 padT3">Description</label>
				<label><textarea id="description" rows="3" class="marT8" style="width:240px"></textarea></label>
				<div class="borderT txtAR padT10">
					<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
					<a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
				</div>
			</div>
			
			<div class="landingCont bgboxGray w45p83 minHeight185 floatL marL13">
				<div id="campaignWithBannerPanel"></div>
			</div>
			
			<div id="bannerImage" class="AlphaCont bgboxAlpha marB10 floatL txtAC" style="width:98%">
				<label class="marT5 txtAL w120"><span id="fieldLabel" class="fbold fsize13">Banner Image</span></label>
				<label class="marT5"><input type="text" class="w460" readonly/></label> 
				<label class="marT6">
					<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
					<span class="crudIcon">
						<a class="saveIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_disk.png" />"></a>
						<a class="infoIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_info.png" />"></a>
					</span>
				</label>
				<div class="clearB"></div>
				
				<div class="imagePreview minHeight185 floatL">
					<h2 class="marT5 txtAL w120 borderB">Preview</h2>
				</div>
			</div>
			<div class="clearB"></div>
		</div>
		<div class="clearB"></div>
	</div>
	
</div>
<!--  end right side -->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	