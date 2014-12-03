<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="lexicon" />
<c:set var="submenu" value="typeahead" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>
<style>
.ui-sortable-placeholder {
  display: inline-block;
height: 1px;
}
</style>
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
<link type="text/css" rel="stylesheet"
	href="<spring:url value="/css/typeahead.css" />">
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
				<a href="javascript:void(0);" class="searchButtonList padR10" style="display:none;"><img
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
	            <div class="floatL w330">
		            <label class="floatL w70 marT5">Enabled</label>
		            <label class="floatL marT5"><input id="disabledEdit" type="checkbox"/></label>
		            <label class="floatR marT5"><a href="javascript:void(0);" id="suggestQtip"></a></label>
	            </div>
	            
	            <div class="clearB"></div>
	        </div>
	    </div>
	    <div class="clearB"></div>
	    <div style="display:none;">
	    	<div id="searchResult" class="floatR w245 marL10 fsize11">
	    		<div><h3>Suggestions</h3></div>
				<div id="docs" class="w350">
					
				</div>
			</div>
	    </div>
		<table id="typeaheadTable" class="tblItems marL10">
			<tr class="itemRow">
				<th>Related Keywords</th>
				<th><input type="checkbox" class="floatL"/>Category</th>
				<th> <input type="checkbox" class="floatL"/>Brand</th>
				
			</tr>
			<tr class="itemRow">
				<td valign="top">
					<div id="relatedKeywords" class="floatR w165 marL10 fsize11" style="height:280px; overflow-y:auto">
						<div id="docs">
							
						</div>
					</div>
				</td>
				<td valign="top">
					<div id="category" class="floatL w240 marL10 fsize11" style="height:280px; overflow-y:auto">
						<div id="sortedCategoryDocs" class="sortDiv">
							<ul>
							</ul>
						</div>
						<div class="clearB"></div>
						<hr/>
						<div class="clearB"></div>
						<div id="categoryDocs">
						
						</div>
					</div>
				</td>
				<td valign="top">
					<div id="brand" class="floatR w215 marL10 fsize11" style="height:280px; overflow-y:auto">
						<div id="sortedBrandDocs" class="sortDiv">
							<ul>
							</ul>
						</div>
						<div class="clearB"></div>
						<hr/>
						<div class="clearB"></div>
						<div id="brandDocs">
						
						</div>
					</div>
				</td>
			</tr>
			
			
		</table>
		<div class="clearB"></div>
		<div id="sectionTableContainer">
			<table id="section" class="tblItems marL10 marT15 marB10">
				<tr>
					<td>
						<div style="width:685px;" id="addSectionForm">
							<div class="marL5 marT10 floatL">
								<strong>Add Section:</strong> 
								<input type="text" class="w160"/>
								<a href="javascript:void(0);" class="btnGraph btnAddGrayMid clearfix" id="btnAddSection"><div class="btnGraph marB8"></div></a>
							</div>
							<div class="clearB"></div>
							<hr/>
							<div class="clearB"></div>
						</div>
						
						<div id="sectionBox">
						</div>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="clearB"></div>
		<div class="txtAR padT5 floatR w125">
		    <a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
		    <a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
		    &nbsp;
		</div>
		
		<div id="sectionSort" style="display:none;">
			<ul id="sortableSectionList">
			</ul>
		</div>
		
	</div>
	<!--  <div id="noSelected"><img id="no-items-img" src="../images/facetSortRuleGuidelines.jpg"></div>-->


	<div class="clearB"></div>
</div>

<!-- End Right Side -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>