<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="lexicon" />
<c:set var="submenu" value="typeahead" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<script type="text/javascript">
	var WIDGET_ID_searchResult = 'searchResult';
	var WIDGET_TARGET_searchResult = '#docs';
	
	var WIDGET_ID_brand = 'brand';
	var WIDGET_TARGET_brand = '#brandDocs';
	
	var WIDGET_ID_category = 'category';
	var WIDGET_TARGET_category = '#categoryDocs';
</script>
<!-- page specific dependencies -->
<link type="text/css" rel="stylesheet"
	href="<spring:url value="/css/bigbets/facet.css" />">
<script type="text/javascript"
	src="<spring:url value="/js/rules/typeahead.js" />"></script>

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="clearB floatL w240">
		<div id="keywordSidePanel"></div>
		&nbsp;
		<div class="clearB"></div>
	</div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w720 marL10 marT27" id="typeaheadPanel">
	<div class="floatL w720 titlePlacer">
		<div class="w530 padT10 padL10 floatL fsize20 fnormal breakWord">
			<span id="ruleTypeIcon" class="ruleTypeIcon marR5 posRel top3"></span>
			<span id="titleText"></span> <span id="titleHeader"
				class="fLblue fnormal"></span>
		</div>
		<div class="listSearchDiv padT10 floatR">
			<input type="text" class="searchTextInput" /><a
				href="javascript:void(0);" class="searchButton"><img
				src="<spring:url value="/images/icon_magniGlass13.png" />"></a> 
				<a href="javascript:void(0);" class="searchButtonList" style="display:none;"><img
				src="<spring:url value="/images/icon_list.png" />">Back to list</a>

		</div>
	</div>
	<div class="clearB"></div>
	<div id="submitForApproval"></div>
	<div id="preloader" class="circlePreloader" style="display: none;">
		<img src="<spring:url value="/images/ajax-loader-circ.gif" />">
	</div>
	<div class="clearB"></div>

	<div id="listContainer" class="floatL w720 marT27"></div>
	<div id="editPanel">
		<div class="landingCont bgboxGray w450p83 floatL marL10 fsize12 marB0">	
	        <div class="fsize14 txtAL padB1 marB1 fbold">
	        	<div class="floatL w350">
		            <label class="floatL w70 marT5">Priority</label>
		            <label class="floatL"><input id="priorityEdit" type="text" class="w240 marT5"/></label>
	            </div>
	            <div class="floatL w225">
		            <label class="floatL w70 marT5">Disabled</label>
		            <label class="floatL marT5"><input id="disabledEdit" type="checkbox"/></label>
	            </div>
	            <div class="txtAR padT5 floatR">
		            <a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
		            <a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
		        </div>
	            <div class="clearB"></div>
	        </div>
	    </div>
	    <div class="clearB"></div>
		<table id="typeaheadTable" class="tblItems marL10">
			<tr class="itemRow">
				<th>Category</th>
				<th> Brand</th>
				<th>Suggestion</th>
			</tr>
			<tr class="itemRow">
				<td valign="top">
					<div id="category" class="floatL w205 marL10 fsize11">
						<div id="categoryDocs">
						
						</div>
					</div>
				</td>
				<td valign="top">
					<div id="brand" class="floatR w170 marL10 marT27 fsize11">
						<div id="brandDocs">
						
						</div>
					</div>
				</td>
				<td valign="top">
					<div id="searchResult" class="floatR w245 marL10 marT27 fsize11">
						<div id="docs">
						
						</div>
					</div>
				</td>
			</tr>
		</table>
	</div>
	<!--  <div id="noSelected"><img id="no-items-img" src="../images/facetSortRuleGuidelines.jpg"></div>-->


	<div class="clearB"></div>
</div>

<!-- End Right Side -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>