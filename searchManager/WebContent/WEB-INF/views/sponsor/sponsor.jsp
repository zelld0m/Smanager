<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="sponsor"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/settings/sponsor.css" />">
<script type="text/javascript" src="<spring:url value="/js/settings/sponsor.js" />" ></script>
  
<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
 	<div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
    <div class="floatL w730 titlePlacer">
      <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">Partners</div>
      <div class="floatL w180 txtAR padT7">
      <a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a>
      <div class="searchBoxHolder floatR w85 marT1">
      	<input id="addSortable" type="text" class="farial fsize12 fgray w99p" maxlength="10">
      </div>
       </div>
    </div>
    <div class="clearB"></div>
    
   	<div id="resultsTopPaging" class="w730 floatL txtAL marT20"></div>
    
    <div class="contentSponsor">  
		<ul>
			<li>	
				<div title="Click to flip" class="sponsor">
					<div class="sponsorFlip">
						<img alt="More about google" src="<spring:url value="/images/sponsor/logo/adobe.png" />">
					</div>
						
					<div class="sponsorData">
						<div class="sponsorDescription">
							The company that redefined web search.
						</div>
						<div class="sponsorURL">
							<a href="http://www.google.com/">http://www.google.com/ </a>
						</div>
					</div>				
				</div>
				<div class="slideCheckbox"><input type="checkbox" id="checkbox-4" class="firerift-style-checkbox" /></div>
				<p class="manage"><a href="">Manage</a></p>
			</li>
			<li>
				<div title="Click to flip" class="sponsor">
				<div class="sponsorFlip">
					<img alt="More about adobe" src="<spring:url value="/images/sponsor/logo/google.png" />">
				</div>
			
				<div class="sponsorData">
					<div class="sponsorDescription">
						The leading software developer targeted at web designers and developers.
					</div>
					<div class="sponsorURL">
						<a href="http://www.adobe.com/">http://www.adobe.com/ </a>
					</div>
				</div>
				</div>
				<div class="slideCheckbox"><input type="checkbox" id="checkbox-4" class="firerift-style-checkbox" /></div>
				<p class="manage"><a href="">Manage</a></p>
			</li>
			<li>
				<div title="Click to flip" class="sponsor">
				<div class="sponsorFlip">
					<img alt="More about adobe" src="<spring:url value="/images/sponsor/logo/google.png" />">
				</div>
			
				<div class="sponsorData">
					<div class="sponsorDescription">
						The leading software developer targeted at web designers and developers.
					</div>
					<div class="sponsorURL">
						<a href="http://www.adobe.com/">http://www.adobe.com/ </a>
					</div>
				</div>
				</div>
				<div class="slideCheckbox"><input type="checkbox" id="checkbox-4" class="firerift-style-checkbox" /></div>
				<p class="manage"><a href="">Manage</a></p>
			</li>
			<li>
				<div title="Click to flip" class="sponsor">
				<div class="sponsorFlip">
					<img alt="More about adobe" src="<spring:url value="/images/sponsor/logo/google.png" />">
				</div>
			
				<div class="sponsorData">
					<div class="sponsorDescription">
						The leading software developer targeted at web designers and developers.
					</div>
					<div class="sponsorURL">
						<a href="http://www.adobe.com/">http://www.adobe.com/ </a>
					</div>
				</div>
				</div>
				<div class="slideCheckbox"><input type="checkbox" id="checkbox-4" class="firerift-style-checkbox" /></div>
				<p class="manage"><a href="">Manage</a></p>
			</li>
			<li>
				<div title="Click to flip" class="sponsor">
				<div class="sponsorFlip">
					<img alt="More about adobe" src="<spring:url value="/images/sponsor/logo/google.png" />">
				</div>
			
				<div class="sponsorData">
					<div class="sponsorDescription">
						The leading software developer targeted at web designers and developers.
					</div>
					<div class="sponsorURL">
						<a href="http://www.adobe.com/">http://www.adobe.com/ </a>
					</div>
				</div>
				</div>
				<div class="slideCheckbox"><input type="checkbox" id="checkbox-4" class="firerift-style-checkbox" /></div>
				<p class="manage"><a href="">Manage</a></p>
			</li>
		</ul>
	</div> 
	
	<div id="resultsBottomPaging" class="w730 floatL txtAL marT20"></div>
</div> 

<!-- End Right Side -->	
<%@ include file="/WEB-INF/includes/footer.jsp" %>	