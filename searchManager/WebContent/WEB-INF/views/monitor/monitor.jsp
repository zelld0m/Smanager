<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="monitor"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
 	<div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
    </div>
</div>

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">Monitor</h1>
	</div>
	
	<div class="clearB"></div>
	
	<div id="monitool" class="tabs">
		<ul>
			<li><a href="#cache"><span>Cache</span></a></li>
			<li><a href="#log"><span>Log</span></a></li>
		</ul>
		
		<div id="cache">
			<div>
				<input type="text">
				<div class="floatR marL8 marR3 padT5"> 	        		
	        		<a id="checkBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Check Cache</div></a>
	        	</div>
			</div>
			
			<div>
				<div>
					<span>Cache Type: </span><span>Object</span>
					<span>Cache Date: </span><span>Im a date</span>
				</div>
				<div></div>
			</div>
		</div>
		
		<div id="log">
		
		</div>
	</div>
	
	
</div>
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>	
