<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="import"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/settings/import.js" />"></script>

<script>
	var hasPublishRule = <%= request.isUserInRole("PUBLISH_RULE") %>;
</script>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/settings/settings.css" />">

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="clearB floatL w240">
		<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27 txtAL">

	<div class="floatR padT7">
	  	<div class="floatL fbold fsize14 marT4 marR5"></div>
	  	<div class="floatR"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1 marL3" id="downloadIcon" alt="Download" title="Download"></div></a></div>
	  	<div class="clearB"></div>
    </div>
	<div class="floatL w730 titlePlacer breakWord">
		<h1 id="titleText" class="padT7 padL15 fsize20 fnormal"></h1>
	</div>
	
	<div class="clearB"></div>
	
	<!-- Start Main Content -->
	<div style="width:95%" class="dashboard marT20 mar0">
		<!-- tabs -->
		<div id="import" class="tabs">
      		<ul>
		        <li><a href="#elevateTab"><span>Elevate</span></a></li>
		        <li><a href="#excludeTab"><span>Exclude</span></a></li>
		        <li><a href="#demoteTab"><span>Demote</span></a></li>
		        <li><a href="#facetSortTab"><span>Facet Sort</span></a></li>
		        <li><a href="#queryCleaningTab"><span>Query Cleaning</span></a></li>
		        <li><a href="#rankingRuleTab"><span>Ranking Rule</span></a></li>
		    </ul>
		   
			<div class="minHeight400" id="elevateTab"></div>
			<div class="minHeight400" id="excludeTab"></div>
			<div class="minHeight400" id="demoteTab"></div>
			<div class="minHeight400" id="facetSortTab"></div>
			<div class="minHeight400" id="queryCleaningTab"></div>
			<div class="minHeight400" id="rankingRuleTab"></div>
		</div><!--  end tabs -->
		
		<div id="tabContentTemplate" style="display: none">
			<div class="">
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="24px" id="selectAll"><input type="checkbox" style="display:none"></th>
							<th width="30px">Content</th>
							<th width="150px">Rule Info</th>
							<th width="50px">Published Date</th>
							<th width="85px">Import Type</th>
							<th>Import As</th>
						</tr>
					<tbody>
				</table>
			</div>
			<div style="max-height:360px; overflow-y:auto">
				<table id="rule" class="tblItems w100p">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem" style="display: none">
							<td width="24px" class="txtAC" id="select"><input type="checkbox" class="selectItem"></td>
							<td class="txtAC" width="30px" id="ruleOption">
								<img class="previewIcon pointer" src="<spring:url value="/images/icon_reviewContent.png" />" alt="Preview Content" title="Preview Content"> 
							</td>
							<td width="150px" id="ruleRefId">
								<p class="breakWord" id="ruleName"></p>
								<p id="ruleId" class="fsize11 breakWord"></p>
							</td>
							<td width="50px" class="txtAL" id="publishDate">
								<p id="publishDate" class="fsize11"></p>
							</td>
							<td width="85px" class="txtAC" id="type">
								<select id="importTypeList">
								</select>
							</td>
							<td class="txtAL" id="importAs">
								<select id="importAsList">
									<option value="">Import as New Rule</option>
								</select>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn" class="floatR marT10 fsize12 border pad10 w650 marB20" style="background: #f3f3f3;">
				<h3 style="border:none;">Import Rule Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">
					<p align="justify">
					Before importing any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>
					If the published rule is ready to be imported, click on <strong>Import</strong>. Provide notes in the <strong>Comment</strong> box.
					<p>
				</div>
				<label class="floatL padL13 w100"><span class="fred">*</span> Comment: </label>
				<label class="floatL w480"><textarea id="comment" class="w510" style="height:32px"></textarea></label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="okBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Import</div>
					</a>
					<a id="rejectBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Reject</div>
					</a>
				</div>
			</div>
			<div class="clearB"></div>
		</div>
	</div><!-- End Main Content -->
</div><!-- End Right Side --> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	
