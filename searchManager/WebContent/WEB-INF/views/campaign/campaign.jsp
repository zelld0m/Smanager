<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="advertise"/>
<c:set var="submenu" value="campaign"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/campaign/campaign.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/campaign/campaign.css" />" rel="stylesheet" type="text/css">

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
        <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
			<span id="ruleTypeIcon" class="ruleTypeIcon marR5 posRel top3"></span>
			<span id="titleText"></span>
			<span id="titleHeader" class="fLblue fnormal"></span>
		</div>
    </div>
	<div class="clearB"></div>
	
	<div id="submitForApproval"></div>
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
				<label class="floatL w70 marT5 padT3">Schedule</label> 
				<label><input id="startDate" name="startDate" type="text" class="w70 marT5"></label> 
				<label class="txtLabel"> - </label> 
				<label><input id="endDate" name="endDate" type="text" class="w70 marT5"></label>	
				<div class="clearB"></div>			
				<label class="floatL w70 marT8 padT3">Description</label>
				<label><textarea id="description" rows="3" class="marT8" style="width:240px"></textarea></label>
				<div class="borderT txtAR padT10">
					<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
					<a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
				</div>
			</div>
			
			<div id="mG1" class="microGallery marL13" title="Early Holiday Treats | Jan 1 - Jan 30">
				<img src="http://t0.gstatic.com/images?q=tbn:ANd9GcRZynMzjEr8vMvQbt5zUmCkI52bxXkW4ElcIIfRU9csAjE9tAqRN2bk3AkW"/>
				<img src="http://t2.gstatic.com/images?q=tbn:ANd9GcRTqfWIn_gA1034cj3zFLElp62rHyJj8Zrxc8KMvx-WVuu5gSHP"/>
				<img src="http://t3.gstatic.com/images?q=tbn:ANd9GcSEuglQt5IId5Y_UfQOP55l8V2uPLb8KGtbL7t8s7iXB2zucT2lnw"/>
				<img src="http://t1.gstatic.com/images?q=tbn:ANd9GcT_rEtxR8JPtb3MDtn5do3GOHyvgHJ6mkvK_wvxZ6t_P7ASqjeQ"/>
				<img src="http://t1.gstatic.com/images?q=tbn:ANd9GcQLKMYGMHenPHLWQggtsIgn5l8HtQYQv4cWKsS6ZaY5xWQ5SKOsmA"/>
				<img src="http://t2.gstatic.com/images?q=tbn:ANd9GcREnV_irVsWDX8Q8LSlji5BA1Cz0hNtFCnc6zZitSaroYEFKYSKQA"/>
			</div>
			<div class="clearB"></div>
		</div>
	</div>
	<div class="clearB"></div>
</div>
<!--  end right side -->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	