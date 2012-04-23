<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="production"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/settings/production.js" />"></script> 

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
 	<div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27 txtAL">

	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">Push to Production</h1>
	</div>
	
	<div class="clearB"></div>
	
	<div style="width:95%" class="dashboard marT20 mar0">
		<!-- tabs -->
		<div id="production" class="tabs">
      		<ul>
		        <li><a href="#elevateTab"><span>Elevate</span></a></li>
		        <li><a href="#excludeTab"><span>Exclude</span></a></li>
		        <li><a href="#queryCleaningTab"><span>Query Cleaning</span></a></li>
		        <li><a href="#rankingRuleTab"><span>Ranking Rule</span></a></li>
		    </ul>
		
			<div class="minHeight400" id="elevateTab"></div>
			<div class="minHeight400" id="excludeTab"></div>
			<div class="minHeight400" id="queryCleaningTab"></div>
			<div class="minHeight400" id="rankingRuleTab"></div>
		</div><!--  end tabs -->
		
		<div id="tabContentTemplate" style="display: none">
			<div>
				<table class="tblItems w100p marT5">
					<tr>
						<th width="24px" id="selectAll"><input type="checkbox"></th>
						<th width="268px" class="txtAL">Rule ID</th>
						<th>Status</th>
						<th width="110px">Production Status</th>
						<th width="85px">Last Publish</th>
					</tr>
				</table>
			</div>
			<div style="max-height:360px; overflow-y:scroll">
				<table class="tblItems w100p">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem">
							<td width="24px" class="txtAC" id="select"><input type="checkbox"></td>
							<td width="268px" id="ruleRefId"></td>
							<td class="txtAC" id="updateStatus"></td>
							<td width="110px" class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
							<td width="85px" class="txtAC">04/23/2012</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
			
	</div><!-- End Main Content -->
</div><!-- End Right Side --> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	