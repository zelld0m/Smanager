<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="export"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/settings/export.js" />"></script>

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

	<div class="floatL w730 titlePlacer breakWord">
	  <div class="floatR padT7">
	  	<div class="floatL fbold fsize14 marT4 marR5"><label class="floatL wAuto marRL5 fLgray2">|</label> Auto-export: </div>
	  	<div class="floatR marT4 marR5"><a class="infoIcon" href="javascript:void(0);" title="What's this?"><img src="/searchManager/images/icon_info.png"></a></div>
	  	<div class="floatR marR5"><input id="autoexport" type="checkbox" class="firerift-style-checkbox on-off"/></div>
	  	<div class="clearB"></div>
      </div>
	  <div class="floatR padT7">
	  	<div class="floatL fbold fsize14 marT4 marR5"></div>
	  	<div class="floatR"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1 marL3" id="downloadIcon" alt="Download" title="Download"></div></a></div>
	  	<div class="clearB"></div>
      </div>
	  <div class="w480 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>
	
	<div class="clearB"></div>
	
	<div style="width:95%" class="dashboard marT20 mar0 fsize12">
	These are all the rule that are available for Export. You can manually export rules by selecting the checkboxes and clicking on the 'Export' button.
	<ul class="marL15">
		<li>If you have Auto-Export turned on, all rules that you publish to ${storeLabel} will be automatically exported. There is no need to manually export any rules.</li>
		<li>Exporting a rule will not remove it from the list.</li>
	</ul>
	</div>
	
	<div class="clearB"></div>
	
	<!-- Start Main Content -->
	<div style="width:95%" class="dashboard marT20 mar0">
		<!-- tabs -->
		<div id="export" class="tabs">
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
			<div class="filter padT5 fsize12 marT8">
				<div class="floatR padT3" id="ruleCount"></div>
			</div>
			<div class="clearB"></div>
			<div class="">
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="24px" id="selectAll"><input type="checkbox"></th>
							<th width="50px">Content</th>
							<th width="230px">Rule Name</th>
							<th width="85px">Published Date</th>
							<th width="85px">Export Date</th>
							<th>Export Type</th>
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
								<img class="previewIcon pointer" src="<spring:url value="/images/icon_reviewContent.png" />" alt="Preview Content" title="Preview Content"> 
							</td>
							<td width="230px" id="ruleRefId">
								<p class="w230 breakWord" id="ruleName"></p>
								<p id="ruleId" class="fsize11 w230 breakWord"></p>
							</td>
							<td width="85px" class="txtAL" id="publishDate">
								<p id="requestedBy"></p>
								<p id="requestedDate" class="fsize11"></p>
							</td>
							<td width="85px" class="txtAL" id="exportDate">
								<p id="requestedBy"></p>
								<p id="requestedDate" class="fsize11"></p>
							</td>
							<td class="txtAC" id="type"></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn" class="floatR marT10 fsize12 border pad10 w650 marB20" style="background: #f3f3f3;">
				<h3 style="border:none;">Export Rule Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">
					<p align="justify">
					Before exporting any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>
					If the published rule is ready to be exported, click on <strong>Export</strong>. Provide notes in the <strong>Comment</strong> box.
					<p>
				</div>
				<label class="floatL padL13 w100"><span class="fred">*</span> Comment: </label>
				<label class="floatL w480"><textarea id="comment" class="w510" style="height:32px"></textarea>	</label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="okBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Export</div>
					</a>
				</div>
			</div>
			<div class="clearB"></div>
		</div>
	</div><!-- End Main Content -->
</div><!-- End Right Side --> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	