<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="sponsor"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script src="<spring:url value="/js/dashboard/dashboard.js" />" type="text/javascript"></script>  

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/settings/sponsor.css" />">
<script type="text/javascript" src="<spring:url value="/js/settings/sponsor.js" />" ></script>
  
 <!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
    &nbsp;
</div>
<!--  end left side -->


<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
      <div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal">Sponsors</div>
        <div class="floatL w180 txtAR padT7"><input id="addSortable" type="text" class="farial fsize12 fgray searchBox searchBoxIconLBg w85 marT1" maxlength="10" value="Esdsd"><a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> </div>
      </div>
    <div class="clearB"></div>
    
    <!--Pagination-->
      <div class="mar0">
        <div class="clearB floatL farial fsize12 fDblue w300 padT10 marL10">Displaying 1 to 25 of 26901 Products</div>
        <div class="floatR farial fsize12 fgray txtAR padT10">
          <div class="txtAR">
            <ul class="pagination">
              <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">1</a></li>
              <li><a href="#">2</a></li>
              <li><a href="#">3</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
            </ul>
          </div>
        </div>
      </div>
      <!--Pagination-->
    
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
	
	
	
	
	
	
	</div> <!--  end contentSponsor -->
	

</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	