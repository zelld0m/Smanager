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
				<div id="linkPath">
					<label class="marT5 txtAL w80"><span id="fieldLabel" class="fbold fsize13">Link Path</span></label>
					<label class="marT5"><input id="linkPath" type="text" class="w460"/></label> 
				</div>
				<div class="clearB"></div>
				<div id="imageAlt">
					<label class="marT5 txtAL w80"><span id="fieldLabel" class="fbold fsize13">Image Alt</span></label>
					<label class="marT5"><input id="imageAlt" type="text" class="w460"/></label> 
				</div>
				<table class="bannerImage">							
					<tr class="imageLink" id="imageLink">
						<td class="padB8" valign="top"><label class="marT5 txtAL w80"><span id="fieldLabel" class="fbold fsize13">Image Path</span></label></td>
						<td class="iepadBT0">
							<label class="imageLink marT5"><input id="imageURL" type="text" class="w460"/></label>
							<label class="uploadImage txtAL marT5" style="display:none;"><input id="imageURL" type="file"/></label>
							<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
							<span class="crudIcon floatL marL10 padT8">
								<a class="infoIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_info.png" />"></a>
							</span>
							<div class="clearB"></div>
							<div class="imageLink floatL fsize11 marL10 txtDecoUL padT5">
								<label class="txtAL fbold fsize12">Paste image URL | <a class="switchToUploadImage" href="javascript:void(0);">Upload an image</a></label>
							</div>
							<div class="uploadImage floatL fsize11 marL10 txtDecoUL padT5" style="display:none;">
								<label class="txtAL fbold fsize12"><a class="switchToImageLink" href="javascript:void(0);">Paste image URL</a> | Upload an image</label>
							</div>
						</td>
					</tr>
				</table>
				
				<div class="imagePreview minHeight185 floatL w650 marL10">
					<h2 class="marT5 txtAL borderB">Preview</h2>
					<div class="marT5">
						<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
						<img id="bannerPreview" src="<spring:url value="/images/nopreview.png" />" onerror="this.onerror=null;this.src='<spring:url value="/images/nopreview.png" />';"/>
					</div>
				</div>
				
				<div class="txtAR padT10">
					<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
					<a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Reset</div></a>
				</div>
			</div>
			<div class="clearB"></div>
		</div>
		<div class="clearB"></div>
	</div>
	
</div>
<!--  end right side -->

<div id="setupFieldValueS1" style="display:none">
	<div style="width:610px;">
		<!-- Menu fields -->
		<div id="fieldListing" class="fieldListing floatL w240 minHeight300">			
			<h3 class="borderB fsize14 fbold padB4 mar0 marT15">Campaign List<span id="sfCount" class="txtAR fsize11 floatR"></span></h3>
			<input id="searchBoxField" name="searchBoxField" type="text" class="farial fsize12 fgray searchBoxIconBg w233">
			<div class="borderT marT8"></div>
			
			<div id="preloader" class="marT30 txtAC"><img src="../images/preloader30x30Trans.gif"></div>
			
			<div id="content">
				<ul id="fieldListing" class="menuFields">
					<li id="fieldListingPattern" class="fieldListingItem" style="display:none">
						<a href="javascript:void(0);"><img src="../images/icon_addField.png" style="margin-bottom:-3px"></a>
						<span></span>
					</li>
				</ul>
				<div id="fieldsBottomPaging"></div>			
			</div>
		</div>
		<!--  end menu fields -->
		<div id="fieldSelected" class="floatL marL3 w350">
		<h3 class="fsize14 fbold pad8 mar0" style="background:#cacaca">Campaigns Using This Banner<span id="sfSelectedCount" class="txtAR fsize11 floatR"></span></h3>
			<div style="overflow-y:scroll; height: 250px">
				<table class="tblfields" style="width:100%" cellpadding="0" cellspacing="0">
					<tbody id="fieldSelectedBody">
						<tr id="fieldSelectedPattern" style="display: none" class="fieldSelectedItem">
							<td class="pad0 txtAC"><a class="removeSelected" href="javascript:void(0);"><img src="../images/icon_delete2.png" class="marL3"></a></td>
							<td class="fields">
								<div class="fieldsHolder marL3">
									<span class="txtHolder"></span>
									<div class="bargraph borderR3 height24">							
										<div class="clearB"></div>
									</div>																		
								</div>
							</td>
						</tr>
					</tbody>
				</table>					
			</div>
			<div align="right" class="marT15"><a id="closeBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Close</div></a></div>
		</div>
	
	</div>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>	