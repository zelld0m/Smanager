<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="approval"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/settings/approval.js" />"></script> 

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/settings/settings.css" />">

     
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

	<div class="floatL w730 titlePlacer breakWord">
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
			<div class="">
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="24px" id="selectAll"><input type="checkbox"></th>
							<th width="50px" class="txtAL"></th>
							<th width="230px" class="txtAL">Rule Info</th>
							<th width="85px">Request Type</th>
							<th>Request Details</th>
						</tr>
					<tbody>
				</table>
			</div>
			<div style="max-height:360px; overflow-y:auto">
				<table id="rule" class="tblItems w100p">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem" style="display: none">
							<td width="24px" class="txtAC" id="select"><input type="checkbox"></td>
							<td class="txtAC" width="50px" id="ruleOption">
								<img class="previewIcon pointer" src="<spring:url value="/images/icon_preview.png" />" alt="Preview Content" title="Preview Content"> 
							</td>
							<td width="230px" id="ruleRefId">
								<p class="w230 breakWord" id="ruleName"></p>
								<p id="ruleId" class="fsize11 w230 breakWord"></p>
							</td>
							<td width="85px" class="txtAC" id="type"></td>
							<td class="txtAL" id="requested">
								<p id="requestedBy"></p>
								<p id="requestedDate" class="fsize11"></p>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn" class="floatR marT10 fsize12 border pad10 w650 marB20" style="display:none; background: #f3f3f3;">
				<h3 style="border:none;">Approval Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">lorem ipsum hey ultricies faucibus ultricies. Etiam sit amet nibh id lorem malesuada congue at et lacus. Curabitur eget ligula quis libero porta lacinia. Morbi accumsan suscipit diam, id placerat ante euismod et. Pellentesque convallis lectus eget nibh condimentum nec suscipit nisi euismod. Vivamus accumsan, dolor non porttitor convallis, velit nulla vehicula sapien, quis mattis sapien urna ac massa.</div>
				<label class="floatL padL13 w100"><span class="fred">*</span> Comment: </label>
				<label class="floatL w480"><textarea id="approvalComment" class="w510" style="height:32px"></textarea>	</label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="approveBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Approve</div>
					</a>
					<a id="rejectBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Reject</div>
					</a>
				</div>
			</div>
			<div class="clearB"></div>
		</div>
		
		<div id="previewTemplate1" style="display: none;">
			<div class="rulePreview w600">
				<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>
				<label class="w110 floatL fbold">Rule Info:</label>
				<label class="wAuto floatL" id="ruleInfo"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Request Type:</label>
				<label class="wAuto floatL" id="requestType"></label>					
				<div class="clearB"></div>
			</div>
			<div class="clearB"></div>
			
			<div class="w600 mar0 pad0">
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="20px">#</th>
							<th width="60px" id="selectAll">Image</th>
							<th width="94px">Manufacturer</th>
							<th width="70px" class="txtAL">SKU #</th>
							<th width="160px" class="txtAL">Name</th>
							<th width="90px">Validity</th>
						</tr>
					<tbody>
				</table>
			</div>
			<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;">
				<table id="item" class="tblItems w100p">
					<tbody>
						<tr id="itemPattern" class="itemRow" style="display: none">
							<td width="20px" class="txtAC" id="itemPosition"></td>
							<td width="60px" class="txtAC" id="itemImage"><img src="" width="50"/></td>
							<td width="94px" class="txtAC" id="itemMan"></td>
							<td width="70px" class="txtAC" id="itemDPNo"></td>
							<td width="162px" class="txtAC" id="itemName"></td>
							<td class="txtAC" id="itemValidity"></td>
						</tr>
					</tbody>
				</table>
			</div>
			
			<div id="actionBtn" class="marT10 fsize12 border pad10 w580 mar0 marB20" style="background: #f3f3f3;">
				<h3 style="border:none">Approval Guidelines</h3>
				<div class="fgray padL15 padR10 padB15 fsize11">Rule info Suspendisse ultricies faucibus ultricies. Etiam sit amet nibh id lorem malesuada congue at et lacus. Curabitur eget ligula quis libero porta lacinia. Morbi accumsan suscipit diam, id placerat ante euismod et. Pellentesque convallis lectus eget nibh condimentum nec suscipit nisi euismod. Vivamus accumsan, dolor non porttitor convallis, velit nulla vehicula sapien, quis mattis sapien urna ac massa.</div>
				<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>
				<label class="floatL w480"><textarea id="approvalComment" rows="5" class="w460" style="height:32px"></textarea>	</label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="approveBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Approve</div>
					</a>
					<a id="rejectBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Reject</div>
					</a>
				</div>
			</div>
		</div>
		
		<div id="previewTemplate2" style="display: none;">
			<div class="rulePreview w590 marB20">
				<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>
				<label class="w110 floatL fbold">Rule Info:</label>
				<label class="wAuto floatL" id="ruleInfo"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Request Type:</label>
				<label class="wAuto floatL" id="requestType"></label>
				<div class="clearB"></div>					
			</div>
			
			<div id="rankingSummary" class="infoTabs marB20 tabs">
			
				<ul class="posRel top6" style="z-index:100">
					<li><a href="#ruleRanking"><span>Rule Info</span></a></li>
					<li><a href="#ruleField"><span>Rule Field</span></a></li>
					<li><a href="#ruleKeyword"><span>Keyword</span></a></li>
					<div class="clearB"></div>
				</ul>
								
				<div id="ruleRanking" class="ruleRanking">					
					<ul id="relevancyInfo" class="relevancyInfo">
						<li class="clearfix"><label class="floatL w150 padT8">Start Date:</label><label class="floatL padT8"><span id="startDate" class="padT8"></span></label></li>
						<li class="clearfix"><label class="floatL w150 padT8">End Date:</label><label class="floatL padT8"><span id="endDate" class="padT8"></span></label></li>
						<li class="clearfix"><label class="floatL w150 padT8">Description:</label><label class="floatL padT8"><span id="description" class="padT8"></span></label></li>
					</ul>
				</div>
				
				<div class="clearB"></div>	
				<div id="ruleField" class="ruleField">
					<div class="w580 mar0 padLR5">
						<table class="tblItems w100p marT10" id="itemHeader">
							<tbody>
								<tr>
									<th id="fieldNameHeader" class="w70 txtAC">Field Name</th>
									<th id="fieldValueHeader" class="wAuto txtAC">Field Value</th>
								</tr>
							<tbody>
						</table>
					</div>
					<div style="max-height:180px; overflow-y:auto;" class="w580 mar0">
						<table id="item" style="border-collapse:collapse" class="tblItems w100p marB10">
							<tbody>
								<tr id="itemPattern" class="itemRow" style="display: none">
									<td class="txtAC w70" id="fieldName"></td>
									<td id="fieldValue" class="wAuto"></td>
								</tr>
							</tbody>
						</table>
					</div>					
				</div>
				
				<div class="clearB"></div>	
				<div id="ruleKeyword" class="ruleKeyword">
					<ul id="keywordInRule" class="keywordInRule"></ul>
				</div>
				<div class="clearB"></div>
			</div>
				
			<div class="clearB"></div>
			<div id="actionBtn" class="floatR fsize12 border pad5 w580 marB20" style="background: #f3f3f3;">
				<h3 class="padL15" style="border:none">Approval Guidelines</h3>
				<div class="fgray padL15 padR12 padB15 fsize11">1111 ffff Suspendisse ultricies faucibus ultricies. Etiam sit amet nibh id lorem malesuada congue at et lacus. Curabitur eget ligula quis libero porta lacinia. Morbi accumsan suscipit diam, id placerat ante euismod et. Pellentesque convallis lectus eget nibh condimentum nec suscipit nisi euismod. Vivamus accumsan, dolor non porttitor convallis, velit nulla vehicula sapien, quis mattis sapien urna ac massa.</div>
				<label class="floatL w85 padL13"><span class="fred">*</span> Comment: </label>
				<label class="floatL w480"><textarea id="approvalComment" rows="5" class="w460" style="height:32px"></textarea>	</label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="approveBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Approve</div>
					</a>
					<a id="rejectBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Reject</div>
					</a>
				</div>
			</div>
		</div>
		
	</div><!-- End Main Content -->
</div><!-- End Right Side --> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	