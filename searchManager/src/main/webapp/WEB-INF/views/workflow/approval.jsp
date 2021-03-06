<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="workflow"/>
<c:set var="submenu" value="approval"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/workflow/approval.js" />"></script> 

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/workflow/workflow.css" />">

<!-- Start Right Side -->
<div class="floatL w980 marT27 txtAL">

	<div class="floatL w980 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">Pending Approval</h1>
	</div>
	
	<div class="clearB"></div>
	
	<!-- Start Main Content -->
	<div style="width:97%" class="dashboard marT20 mar0">
		<!-- tabs -->
		<div id="approval" class="tabs">
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
		   
			<div class="minHeight400" id="elevateTab"></div>
			<div class="minHeight400" id="excludeTab"></div>
			<div class="minHeight400" id="demoteTab"></div>
			<div class="minHeight400" id="facetSortTab"></div>
			<div class="minHeight400" id="queryCleaningTab"></div>
			<div class="minHeight400" id="rankingRuleTab"></div>
			<div class="minHeight400" id="didYouMeanTab"></div>
			<div class="minHeight400" id="bannerTab"></div>
			<div class="minHeight400" id="typeaheadTab"></div>
		</div><!--  end tabs -->
		
		<div id="tabContentTemplate" style="display: none">
			<div class="">
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="24px" id="selectAll"><input type="checkbox"></th>
							<th width="50px">Content</th>
							<th width="430px">Rule Info</th>
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
								<img class="previewIcon pointer" src="<spring:url value="/images/icon_reviewContent.png" />" alt="Preview Content" title="Preview Content"> 
							</td>
							<td width="430px" id="ruleRefId">
								<p class="w230 breakWord" id="ruleName"></p>
								<p id="ruleId" class="fsize11 w230 breakWord"></p>
							</td>
							<td width="85px" class="txtAC" id="type"></td>
							<td class="txtAC" id="requested">
								<p id="requestedBy"></p>
								<p id="requestedDate" class="fsize11"></p>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn" class="floatR marT10 fsize12 border pad10 w950 marB20" style="background: #f3f3f3;">
				<h3 style="border:none;">Approval Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">
					<p align="justify">
					Before approving any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>
					If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.
					<p>
				</div>
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
		
		<div id="tabContentTemplateLinguistics" style="display: none">
			<div class="fsize12">
				<div id = "requestDetails">
					<label class="name floatL w150">Max suggestion count:</label><label id="numSuggestions" class="fbold"></label><p>	
					<label class="name floatL w150">Requested by:</label> <label id="requestedBy" class="fbold"></label><p>	
					<label class="name floatL w150">Requested date:</label> <label id="requestedDate" class="fbold"></label><p>	
				</div> 
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="152px">Keyword Terms</th>
							<th width="152px">Suggestions</th>
							<th width="85px">Request Type</th>
						</tr>
					<tbody>
				</table>
			</div>
			<div style="max-height:360px; overflow-y:auto">
				<table id="rule" class="tblItems w100p">
					<tbody>
						<tr id="ruleItemPattern" class="ruleItem" style="display: none">
							<td width="152px" id="searchTerms" class="term-list"></td>
							<td width="152px" id="suggestions" class="term-list"></td>
							<td width="85px" class="txtAC" id="type"></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div id="actionBtn" class="floatR marT10 fsize12 border pad10 w750 marB20" style="background: #f3f3f3;">
				<h3 style="border:none;">Approval Guidelines</h3>
				<div class="fgray padL10 padR10 padB15 fsize11">
					<p align="justify">
					Before approving any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>
					If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.
					<p>
				</div>
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
				<label class="w110 floatL fbold">Rule Name:</label>
				<label class="wAuto floatL" id="ruleInfo"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Request Type:</label>
				<label class="wAuto floatL" id="requestType"></label>					
				<div class="clearB"></div>
			</div>
			<div class="clearB"></div>
			
			<div id="forceAdd" class="loadingWrapper" style="display:none"><img src="../images/ajax-loader-circ16x16.gif"><span class="fsize12 posRel topn3 padL5">Retrieving Force Add Status</span></div>
			<div class="w600 mar0 pad0">
				<table class="tblItems w100p marT5">
					<tbody>
						<tr>
							<th width="20px">#</th>
							<th width="60px" id="selectAll">Image</th>
							<th width="94px">Manufacturer</th>
							<th width="70px">SKU #</th>
							<th width="160px">Name</th>
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
							<td width="160px" class="txtAC" id="itemName"></td>
							<td class="txtAC">
								<div id="itemValidity" class="w74 wordwrap"></div>
								<div id="itemValidityDaysExpired"><img src="<spring:url value="/images/expired_stamp50x16.png" />"/>
								</div>
							</td>
						</tr>
						<tr>
							<td colspan="6" class="txtAC">
								<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">	
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			
			<div id="actionBtn" class="marT10 fsize12 border pad10 w580 mar0 marB20" style="background: #f3f3f3;">
				<h3 style="border:none">Approval Guidelines</h3>
				<div class="fgray padL15 padR10 padB15 fsize11">
					<p align="justify">
					Before approving any rule, it is advisable to review rule details.<br/><br/>
					If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.
					<p>
				</div>
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
		
		<div id="facetSortTemplate" style="display: none">
			<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>
			<div id="ruleInfo">
				<label class="w70 floatL fbold">Rule Name:</label>
				<label class="wAuto floatL" id="ruleName">
					<img id="preloader" alt="Retrieving" src="../images/ajax-loader-rect.gif">
				 </label>
				 	    <div class="clearB"></div>
				 <label class="w70 floatL fbold">Rule Type:</label>
				 <label class="wAuto floatL" id="ruleType">
				 	<img id="preloader" alt="Retrieving" src="../images/ajax-loader-rect.gif">
				 </label>
				 	</div>
				 	<div class="clearB"></div>
				 	<div class="w600 mar0 pad0">
				 <table class="tblItems w100p marT5">
				 	<tbody>
				 		<tr>
				 			<th width="60px">Facet Name</th>
				 			<th width="84px">Highlighted Items</th>
				 			<th width="50px">Sorting of Other Items</th>
				 		</tr>
				 	<tbody>
				 </table>
				 	</div>
				 	<div class="w600 mar0 pad0" style="max-height:180px; overflow-y:auto;">
				 <table id="item" class="tblItems w100p">
				 	<tbody>
				 		<tr id="itemPattern" class="itemRow" style="display: none">
				 			<td width="60px" class="txtAC" id="itemName"></td>
				 			<td width="84px" class="txtAL" id="itemHighlightedItem"></td>
				 			<td width="50px" class="txtAC" id="itemSortType"></td>
				 		</tr>
				 		<tr id="preloader">
				 			<td colspan="6" class="txtAC">
				 				<img alt="Retrieving" src="../images/ajax-loader-rect.gif">	
				 			</td>
				 		</tr>
				 	</tbody>
				 </table>
				</div>
				<div id="actionBtn" class="marT10 fsize12 border pad10 w580 mar0 marB20" style="background: #f3f3f3;">
				<h3 style="border:none">Approval Guidelines</h3>
				<div class="fgray padL15 padR10 padB15 fsize11">
					<p align="justify">
					Before approving any rule, it is advisable to review rule details.<br/><br/>
					If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.
					<p>
				</div>
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
				
		<div id="queryCleaningTemplate" style="display: none;">
			<div class="rulePreview w590 marB20">
				<div class="alert marB10">The following rule is pending for your review. This rule will be temporarily locked unless approved or rejected</div>
				<label class="w110 floatL fbold">Rule Name:</label>
				<label class="wAuto floatL" id="ruleInfo"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Request Type:</label>
				<label class="wAuto floatL" id="requestType"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Description:</label>
				<label class="wAuto floatL" id="description">
					<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">
				</label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Active Type:</label>
				<label class="wAuto floatL" id="redirectType">
					<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">
				</label>
				<div class="clearB"></div>							
			</div>
			
			<div id="rankingSummary" class="infoTabs marB20 tabs">
			
				<ul class="posRel top5" style="z-index:100">
					<li><a href="#ruleKeyword"><span>Keyword</span></a></li>
					<li><a href="#ruleFilter"><span>Filter</span></a></li>
					<li><a href="#ruleChange"><span>Replace KW</span></a></li>
				</ul>
				
				<div class="clearB"></div>	
				<div id="ruleChange" class="ruleChange marB10">
					<div id="noChangeKeyword" class="txtAC mar20" style="display:none">
						<span class="fsize11">No replacement keyword associated to this rule</span>
					</div>
					<div id="hasChangeKeyword" style="display:none">
						<div class="fsize12 txtAL mar20">
							Replace Keyword: <span id="changeKeyword" class="fbold"></span>
						</div>						
						<!-- div id="activerules" class="w97p marRLauto marB10">			
							<div class="alert">This keyword has <span id="rules"></span></div>
						</div -->
					</div>				
					<div class="clearB"></div>
				</div>
								
				<div class="clearB"></div>	
				<div id="ruleFilter" class="ruleFilter marB10">
					<div id="includeKeywordInSearchText" class="includeKeywordInSearchText border bgf6f6f6  w570 pad5 mar10">Include keyword in search: <b>NO</b></div>
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
								<tr>
									<td colspan="2" class="itemRow txtAC">
										<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">	
									</td>
								</tr>
							</tbody>
						</table>
					</div>	
							
				</div>
				
				<div class="clearB"></div>	
				<div id="ruleKeyword" class="ruleKeyword marB10">
					<div class="w580 mar0 padLR5">
						<table class="tblItems w100p marT10" id="itemHeader">
							<tbody>
								<tr>
									<th id="fieldNameHeader" class="w70 txtAC">#</th>
									<th id="fieldValueHeader" class="wAuto txtAC">Keyword</th>
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
								<tr>
									<td colspan="2" class="itemRow  txtAC">
										<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">	
									</td>
								</tr>
							</tbody>
						</table>
					</div>	
				</div>
				
			</div>
				
			<div class="clearB"></div>
			<div id="actionBtn" class="floatR fsize12 border pad5 w580 marB20" style="background: #f3f3f3;">
				<h3 class="padL15" style="border:none">Approval Guidelines</h3>
				<div class="fgray padL15 padR12 padB15 fsize11">
				<p align="justify">
				Before approving any rule, it is advisable to review rule details.<br/><br/>
				If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.
				<p>
				</div>
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
				<label class="w110 floatL fbold">Rule Name:</label>
				<label class="wAuto floatL" id="ruleInfo"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Request Type:</label>
				<label class="wAuto floatL" id="requestType"></label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Start Date:</label>
				<label class="wAuto floatL" id="startDate">
					<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">
				</label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">End Date:</label>
				<label class="wAuto floatL" id="endDate">
					<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">
				</label>
				<div class="clearB"></div>
				<label class="w110 floatL marL20 fbold">Description:</label>
				<label class="wAuto floatL" id="description">
					<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">
				</label>
				<div class="clearB"></div>					
			</div>
			
			<div id="rankingSummary" class="infoTabs marB20 tabs">
			
				<ul class="posRel top5" style="z-index:100">
					<li><a href="#ruleKeyword"><span>Keyword</span></a></li>
					<li><a href="#ruleField"><span>Rule Field</span></a></li>
				</ul>
								
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
								<tr>
									<td colspan="2" class="itemRow txtAC">
										<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">	
									</td>
								</tr>
							</tbody>
						</table>
					</div>	
							
				</div>
				
				<div class="clearB"></div>	
				<div id="ruleKeyword" class="ruleKeyword marB10">
					<div class="w580 mar0 padLR5">
						<table class="tblItems w100p marT10" id="itemHeader">
							<tbody>
								<tr>
									<th id="fieldNameHeader" class="w70 txtAC">#</th>
									<th id="fieldValueHeader" class="wAuto txtAC">Keyword</th>
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
								<tr>
									<td colspan="2" class="itemRow txtAC">
										<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">	
									</td>
								</tr>
							</tbody>
						</table>
					</div>	
				</div>
				
			</div>
				
			<div class="clearB"></div>
			<div id="actionBtn" class="floatR fsize12 border pad5 w580 marB20" style="background: #f3f3f3;">
				<h3 class="padL15" style="border:none">Approval Guidelines</h3>
				<div class="fgray padL15 padR12 padB15 fsize11">
				<p align="justify">
				Before approving any rule, it is advisable to review rule details.<br/><br/>
				If the rule is ready to be pushed to production, click on <strong>Approve</strong>. If the rule needs to be modified before it can be pushed to production, click on <strong>Reject</strong>. Provide notes in the <strong>Comment</strong> box.
				<p>
				</div>
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
