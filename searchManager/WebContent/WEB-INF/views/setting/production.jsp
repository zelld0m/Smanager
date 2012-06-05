<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="setting" />
<c:set var="submenu" value="production" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<script type="text/javascript"
	src="<spring:url value="/js/settings/production.js" />"></script>
<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="companyLogo">
		<a href="#"><img src="<spring:url value="${storeLogo}" />">
		</a>
	</div>
	<div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
	</div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27 txtAL">

	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">Push to Production</h1>
	</div>

	<div class="clearB"></div>

	<div style="width: 95%" class="dashboard marT20 mar0">
		<!-- tabs -->
		<div id="production" class="tabs">
			<ul>
				<li><a href="#elevateTab"><span>Elevate</span>
				</a>
				</li>
				<li><a href="#excludeTab"><span>Exclude</span>
				</a>
				</li>
				<li><a href="#queryCleaningTab"><span>Query Cleaning</span>
				</a>
				</li>
				<li><a href="#rankingRuleTab"><span>Ranking Rule</span>
				</a>
				</li>
			</ul>

			<div class="minHeight400" id="elevateTab"></div>
			<div class="minHeight400" id="excludeTab"></div>
			<div class="minHeight400" id="queryCleaningTab"></div>
			<div class="minHeight400" id="rankingRuleTab"></div>
		</div>
		<!--  end tabs -->

		<div id="tabContentTemplate" style="display: none">
			<div class="filter padT5">
				<div class="floatL">
					<span>Show:</span> 
					<select id="ruleFilter">
						<option value="">All Rules</option>
						<option value="delete">All Approved Delete Rules Only</option>
						<option value="approved">All Approved Non-Delete Rules Only</option>
						<option value="published">All Published Rules Only</option>
					</select>
				</div>
				<div class="floatR"></div>
			</div>
			<div class="clearB"></div>
			<div>
				<table class="tblItems w100p marT5">
					<tr>
						<th width="24px" id="selectAll">
						<input type="checkbox">
						</th>
						<th width="230px" class="txtAL">Rule Info</th>
						<th width="100px">Approval Status</th>
						<th width="100px">Request Type</th>
						<th>Production Status</th>
					</tr>
				</table>
			</div>
			<div style="max-height: 360px; overflow-y: scroll">
				<table class="tblItems w100p" id="rule">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem" style="display: none">
							<td width="24px" class="txtAC" id="select"><input
								type="checkbox">
							</td>
							<td width="230px" id="ruleRefId">
								<p id="ruleName" class="w230 breakWord"></p>
								<p id="ruleId" class="fsize11 w230 breakWord">
									<a href="javascript:void(0);"></a>
								</p></td>
							<td width="100px" class="txtAC" id="approvalStatus"></td>
							<td width="100px" class="txtAC" id="requestType"></td>
							<td class="txtAL" id="production">
								<p id="productionStatus"></p>
								<p id="productionDate" class="fsize11"></p></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn"
				class="floatR marT10 fsize12 border pad10 w650 marB20"
				style="background: #f3f3f3;">
				<h3 style="border: none">Publishing Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">Suspendisse
					ultricies faucibus ultricies. Etiam sit amet nibh id lorem
					malesuada congue at et lacus. Curabitur eget ligula quis libero
					porta lacinia. Morbi accumsan suscipit diam, id placerat ante
					euismod et. Pellentesque convallis lectus eget nibh condimentum nec
					suscipit nisi euismod. Vivamus accumsan, dolor non porttitor
					convallis, velit nulla vehicula sapien, quis mattis sapien urna ac
					massa.</div>
				<label class="floatL w100 padL13"><span class="fred">*</span>
					Comment: </label> <label class="floatL w510"><textarea
						id="approvalComment" rows="5" class="w510" style="height: 32px"></textarea>
				</label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="publishBtn" href="javascript:void(0);" class="buttons btnGray clearfix">
						<div class="buttons fontBold">Publish</div>
					</a> 
					<a id="unpublishBtn" href="javascript:void(0);" class="buttons btnGray clearfix">
						<div class="buttons fontBold">Unpublish</div>
					</a>
				</div>
			</div>
			<div class="clearB"></div>
		</div>

	</div>
	<!-- End Main Content -->
</div>
<!-- End Right Side -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>
