<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="monitor"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="companyLogo">
		<a href="javascript:void()"><img src="<spring:url value="/images/logoMacMall.png" />"></a>
	</div>
	
	<div class="clearB floatL w240">
		<div>&nbsp;</div>
	    <div class="clearB"></div>
	</div>
</div>
<!--Left Menu-->

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	
	<div class="floatL w730 titlePlacer">		
	  <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>
	
	<div id="monitool" class="tabs">
		<ul>
			<li><a href="#cache"><span>Cache</span></a></li>
			<li><a href="#log"><span>Log</span></a></li>
		</ul>
		
		<div id="cache">
			<div>
				<input type="text">
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
