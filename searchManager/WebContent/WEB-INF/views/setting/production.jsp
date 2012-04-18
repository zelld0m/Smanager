<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="production"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
 	<div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
    </div>
</div>
<!-- End Left Side -->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27 txtAL">

	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">Push to Production</h1>
	</div>
	
	<div class="clearB"></div>
	
	<div style="width:95%" class="dashboard marT20 mar0">
		
		<!-- tabs -->
		<div class="tabs">
      		<ul>
		        <li><a href="#elevateTab"><span>Elevate</span></a></li>
		        <li><a href="#excludeTab"><span>Exclude</span></a></li>
		        <li><a href="#queryCleaningTab"><span>Query Cleaning</span></a></li>
		        <li><a href="#rankingRuleTab"><span>Ranking Rule</span></a></li>
		    </ul>
		
		<!--  push tab -->
		<div id="elevateTab" class="mar0 borderT">
		<h2 class="fDGray marT20">Push to Production</h2>
		<table class="tblItems w100p marT5">
			<tr>
				<th width="5%"><input type="checkbox"></th>
				<th class="txtAL">Rule ID </th>
				<th width="19%"> Production Status </th>
				<th width="15%"> Last Publish </th>
				<th width="15%"> Staging Action </th>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">deleted</td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Apple</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_conflict.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">new</td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">deleted</td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">deleted</td>
			</tr>
			
		</table>
		<!--  end table inside tab -->
		</div>
		
		<div id="excludeTab" class="mar0 borderT"></div>
		<div id="queryCleaningTab" class="mar0 borderT"></div>
		<div id="rankingRuleTab" class="mar0 borderT"></div>
		
		</div><!--  end tabs -->
		
	<div class="clearB"></div>
</div>

</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	