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

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27 txtAL">

	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">Pending Approval</h1>
	</div>
	
	<div class="clearB"></div>
	
	<!-- Start Main Content -->
	<div style="width:95%" class="dashboard marT20 mar0">
		<!-- tabs -->
		<div id="approval" class="tabs">
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
					<tbody>
						<tr>
							<th width="24px" id="selectAll"><input type="checkbox"></th>
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
							<td width="24px" class="txtAC" id="select"><input type="checkbox"></td>
							<td width="268px" id="ruleRefId" class="ruleRefId"><a href="javascript:void(0);"></a></td>
							<td width="85px" class="txtAC" id="updateStatus"></td>
							<td width="110px" class="txtAC" id="approvalStatus"></td>
							<td class="txtAC"><img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> </td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn" class="floatR marT10" style="display:none">
				<a id="approveBtn" href="javascript:void(0);"
					class="buttons btnGray clearfix"><div class="buttons fontBold">Approve</div>
				</a>
				<a id="rejectBtn" href="javascript:void(0);"
					class="buttons btnGray clearfix"><div class="buttons fontBold">Reject</div>
				</a>
			</div>
		</div>
		
		<div id="previewTemplate" style="display: none">
			<div>
				<div class="alert">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>
				<div><span>Rule ID:</span><span>xerox</span></div>
				<div><span>Type:</span><span>New</span></div>
				<div><span>Status:</span><span>PENDING</span></div>
				<div><span>Request By:</span><span>admin</span></div>
				<div><span>Request Date:</span><span>admin</span></div>
				<div><span>Request Comment</span><span>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</span></div>
				<div>Approval Comment<textarea></textarea></div>
				<div id="actionBtn">
					<a id="approveBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Approve</div>
					</a>
					<a id="rejectBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Reject</div>
					</a>
				</div>
			</div>
			<div>
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="10px">#</th>
							<th width="50px" id="selectAll">Image</th>
							<th width="85px">Manufacturer</th>
							<th width="50px" class="txtAL">SKU #</th>
							<th width="50px" class="txtAL">Name</th>
							<th width="110px">Validity</th>
						</tr>
					<tbody>
				</table>
			</div>
			<div style="max-height:360px; overflow-y:scroll">
				<table id="item" class="tblItems w100p">
					<tbody>
						<tr id="itemPattern" class="itemRow" style="display: none">
							<td width="10px" id="itemPosition"></td>
							<td width="50px" id="itemImage"><img src="" width="50"/></td>
							<td width="85px" class="txtAC" id="itemMan"></td>
							<td width="50px" class="txtAC" id="itemDPNo"></td>
							<td width="50px" class="txtAC" id="itemName"></td>
							<td width="110px" class="txtAC" id="itemValidity"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
	</div><!-- End Main Content -->
</div><!-- End Right Side --> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	