<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="approval"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/settings/approval.js" />"></script> 
     
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
		<h1 class="padT7 padL15 fsize20 fnormal">Pending Approval</h1>
	</div>
	
	<div class="clearB"></div>
	
	<div style="width:95%" class="dashboard marT20 mar0">
		
		<!-- tabs -->
		<div id="approval" class="tabs">
      		<ul>
		        <li><a href="#elevateTab"><span>Elevate</span></a></li>
		        <li><a href="#excludeTab"><span>Exclude</span></a></li>
		        <li><a href="#queryCleaningTab"><span>Query Cleaning</span></a></li>
		        <li><a href="#rankingRuleTab"><span>Ranking Rule</span></a></li>
		    </ul>
		   
			<!--  approval tab -->
			<div class="minHeight400" id="elevateTab">
				
				<!--  end table inside tab -->
			</div>
			
			<div class="minHeight400" id="excludeTab">
			
			</div>
			
			<div class="minHeight400" id="queryCleaningTab">
			
			</div>
			<div class="minHeight400" id="rankingRuleTab">
			
			</div>
		</div><!--  end tabs -->
		
		<div id="tabContentTemplate" style="display: none">
			<div>
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="24px"><input type="checkbox"></th>
							<th width="268px" class="txtAL">Rule ID</th>
							<th width="85px">Status</th>
							<th width="110px">Approve Status</th>
							<th>Comment</th>
						</tr>
					<tbody>
				</table>
			</div>
			<div style="max-height:360px; overflow-y:scroll">
				<table id="rule" class="tblItems w100p">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem">
							<td width="24px" class="txtAC"><input type="checkbox"></td>
							<td width="268px" id="ruleRefId"></td>
							<td width="85px" class="txtAC" id="updateStatus"></td>
							<td width="110px" class="txtAC" id="approvalStatus"></td>
							<td class="txtAC"><img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> </td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
	<div class="clearB"></div>
</div>

</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	
