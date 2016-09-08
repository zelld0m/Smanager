<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="workflow" />
<c:set var="submenu" value="production" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<script type="text/javascript" src="<spring:url value="/js/workflow/production.js" />"></script>
	
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/workflow/workflow.css" />">

<!-- Start Right Side -->
<div class="floatL w980 marT27 txtAL">

	<div class="floatL w980 titlePlacer breakWord">
	   <div class="floatR padT7">
	  	<div class="floatL fbold fsize14 marT4 marR5 autoImportDiv"><label class="floatL wAuto marRL5 fLgray2">|</label>Target Auto-import:</div> 
	  	<div class="floatR marT4 marR5 autoImportDiv"><a class="infoIcon autoImportIcon" href="javascript:void(0);" title="What's this?"><img src="/searchManager/images/icon_info.png"></a></div>
	  	<div class="floatR marR5 autoImportDiv"><input id="autoimport" type="checkbox" class="firerift-style-checkbox on-off autoImport"/></div>
       </div>
      <div class="w480 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText">Push to Production</span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>
	<div class="clearB"></div>
	<div style="width: 97%" class="dashboard marT20 mar0">
		<c:if test="${storeId eq 'pcmall' or storeId eq 'macmall'}">
			<div id="autoExportStatus" class="info notification border fsize14 marB20" style="display:none">
			Auto-export setting is currently set to <span id="autoExportValue" class="fbold"></span>
			<br/>
			<span class="fsize12 fitalic">To modify this setting, go to <label class="fbold">Workflow &gt; Export Rule</label></span>
			</div> 
		</c:if>
		<!-- tabs -->
		<div id="production" class="tabs">
			<ul>
				<li><a href="#elevateTab"><span>Elevate</span></a></li>
				<li><a href="#excludeTab"><span>Exclude</span></a></li>
				<li><a href="#demoteTab"><span>Demote</span></a></li>
				<li><a href="#facetSortTab"><span>Facet Sort</span></a></li>
				<li><a href="#queryCleaningTab"><span>Redirect Rule</span></a></li>
				<li><a href="#rankingRuleTab"><span>Relevancy Rule</span></a></li>
		        <li><a href="#didYouMeanTab"><span>Did You Mean</span></a></li>
		        <li><a href="#bannerTab"><span>Banner</span></a></li>
		        <li><a href="#typeaheadTab"><span>Typeahead</span></a></li>
			</ul>

			<div class="minHeight400 marL3" id="elevateTab"></div>
			<div class="minHeight400 marL3" id="excludeTab"></div>
			<div class="minHeight400 marL3" id="demoteTab"></div>
			<div class="minHeight400 marL3" id="facetSortTab"></div>
			<div class="minHeight400 marL3" id="queryCleaningTab"></div>
			<div class="minHeight400 marL3" id="rankingRuleTab"></div>
			<div class="minHeight400 marL3" id="didYouMeanTab"></div>
			<div class="minHeight400 marL3" id="bannerTab"></div>
			<div class="minHeight400 marL3" id="typeaheadTab"></div>
		</div>
		<!--  end tabs -->

		<div id="tabContentTemplate" style="display: none">
			<div class="filter padT5 fsize12 marT8">
				<div class="floatL">
					<span>Show:</span> 
					<select id="ruleFilter">
						<option value="">All Rules</option>
						<option value="delete">Approved Rules for Deletion</option>
						<option value="approved">Approved Rules for Publishing</option>
						<option value="published">Published Rules</option>
					</select>
				</div>
				<div class="floatR padT3" id="ruleCount"></div>
			</div>
			<div class="clearB"></div>
			<div>
				<table class="tblItems w100p marT5">
					<tr>
						<th width="24px" id="selectAll">
						<input type="checkbox">
						</th>
						<th width="430px">Rule Info</th>
						<th width="100px">Approval Status</th>
						<th width="100px">Request Type</th>
						<th>Production Status</th>
					</tr>
				</table>
			</div>
			<div style="max-height: 430px; overflow-y: auto">
				<table class="tblItems w100p" id="rule">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem" style="display: none">
							<td width="24px" class="txtAC" id="select"><input
								type="checkbox">
							</td>
							<td width="430px" id="ruleRefId">
								<p id="ruleName" class="w230 breakWord"></p>
								<p id="ruleId" class="fsize11 w230 breakWord">
									<a href="javascript:void(0);"></a>
								</p></td>
							<td width="100px" class="txtAC" id="approvalStatus"></td>
							<td width="100px" class="txtAC" id="requestType"></td>
							<td class="txtAC" id="production">
								<p id="productionStatus"></p>
								<p id="productionDate" class="fsize11"></p></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="paginationDiv" class="paginationDiv" style="display: none;">
				<div class="page-cnt">
					<div class="page-label">Page</div>				
					<div class="page-counter fbold"></div>
					<div class="page-label-of">of</div>
					<div class="page-total fbold"></div>
				</div>
				<div class="page-arrows">
					<a class="left-arrow" href="javascript:void(0);">Prev</a>
					<a class="right-arrow" href="javascript:void(0);">Next</a>
				</div>
			</div>
			<div id="actionBtn"
				class="floatR marT10 fsize12 border pad10 marB20"
				style="background: #f3f3f3; width:97.5%">
				<h3 style="border: none">Publishing Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">
					<div id="pgApprovedForPublishing" class="publishingGuidelines" style="display:none">To push previously approved rules to production:<br>
					<p align="justify" class="padL10">In the <strong>Show</strong> dropdown box, select <span class="fitalic">Approved Rules for Publishing.</span><br>
					When the list has refreshed, tick all applicable rules.<br>
					Provide notes in the <strong>Comment</strong> box and click the <strong>Publish</strong> button.</p></div>
					                                                                                                                                                                                   
					<div id="pgApprovedForDeletion" class="publishingGuidelines" style="display:none">To unpublish rules that have been approved for deletion:<br>
					<p align="justify" class="padL10">In the <strong>Show</strong> dropdown box, select <span class="fitalic">Approved Rules for Deletion.</span><br>
					When the list has refreshed, tick all applicable rules.<br>
					Provide notes in the <strong>Comment</strong> box and click the <strong>Unpublish</strong> button.</p></div>
						
					<div id="pgPublished" class="publishingGuidelines" style="display:none">To unpublish previously published rules:<br>
					<p align="justify" class="padL10">In the <strong>Show</strong> combo box, select <span class="fitalic">Published Rules.</span><br>
					When the list has refreshed, tick all applicable rules.<br>
					Provide notes in the <strong>Comment</strong> box and click the <strong>Unpublish</strong> button.</p></div>
				</div>
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

		<div id="tabContentTemplateLinguistics" style="display: none">
			<div class="filter padT5 fsize12 marT8">
				<div class="floatR padT3" id="ruleCount"></div>
			</div>
			<div id = "requestDetails" class="fsize12">
				<label class="name floatL w150">Max suggestion count:</label><label id="numSuggestions" class="fbold"></label><p>	
				<label class="name floatL w150">Production Status:</label> <label id="productionStatus" class="fbold"></label><p>	
				<label class="name floatL w150">Last Published Date:</label> <label id="productionDate" class="fbold"></label><p>
			</div> 		
			<div class="clearB"></div>
			<div>
				<table class="tblItems w100p marT5">
					<tr>
						<th width="152px">Keyword Terms</th>
						<th width="152px">Suggestions</th>
						<th width="85px">Request Type</th>
					</tr>
				</table>
			</div>
			<div style="max-height: 360px; overflow-y: auto">
				<table class="tblItems w100p" id="rule">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem" style="display: none">
							<td width="152px" id="searchTerms" class="term-list"></td>
							<td width="152px" id="suggestions" class="term-list"></td>
							<td width="85px" class="txtAC" id="type"></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn"
				class="floatR marT10 fsize12 border pad10 w650 marB20"
				style="background: #f3f3f3; width:97.5%;">
				<h3 style="border: none">Publishing Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">
					<div id="pgApprovedForPublishing" class="publishingGuidelines" style="display:none">To push previously approved rules to production:<br>
					<p align="justify" class="padL10">In the <strong>Show</strong> dropdown box, select <span class="fitalic">Approved Rules for Publishing.</span><br>
					When the list has refreshed, tick all applicable rules.<br>
					Provide notes in the <strong>Comment</strong> box and click the <strong>Publish</strong> button.</p></div>
					                                                                                                                                                                                   
					<div id="pgApprovedForDeletion" class="publishingGuidelines" style="display:none">To unpublish rules that have been approved for deletion:<br>
					<p align="justify" class="padL10">In the <strong>Show</strong> dropdown box, select <span class="fitalic">Approved Rules for Deletion.</span><br>
					When the list has refreshed, tick all applicable rules.<br>
					Provide notes in the <strong>Comment</strong> box and click the <strong>Unpublish</strong> button.</p></div>
						
					<div id="pgPublished" class="publishingGuidelines" style="display:none">To unpublish previously published rules:<br>
					<p align="justify" class="padL10">In the <strong>Show</strong> combo box, select <span class="fitalic">Published Rules.</span><br>
					When the list has refreshed, tick all applicable rules.<br>
					Provide notes in the <strong>Comment</strong> box and click the <strong>Unpublish</strong> button.</p></div>
					
					<div id="pgDidYouMean" class="publishingGuidelines" style="display:none">
					<p align="justify">To push new 'Did You Mean' entries to production, provide notes in the <strong>Comment</strong> box and click the <strong>Publish</strong> button.<br>
					Please wait for a few minutes for the updated dictionary to take effect in production.</p></div>
				</div>
				<label class="floatL w100 padL13"><span class="fred">*</span>
					Comment: </label> <label class="floatL w510"><textarea
						id="approvalComment" rows="5" class="w510" style="height: 32px"></textarea>
				</label>
				<div class="clearB"></div>
				<div align="right" class="padR15 marT10">
					<a id="publishBtn" href="javascript:void(0);" class="buttons btnGray clearfix">
						<div class="buttons fontBold">Publish</div>
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
