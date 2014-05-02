<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/ajaxsolr.jsp"%>
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
<script type="text/javascript"
	src="<spring:url value="/js/ajax-solr-06-01-2013/widgets/TypeaheadSearchResult.js" />"></script>
	<script type="text/javascript"
	src="<spring:url value="/js/ajax-solr-06-01-2013/widgets/TypeaheadBrand.js" />"></script>
	<script type="text/javascript"
	src="<spring:url value="/js/ajax-solr-06-01-2013/widgets/TypeaheadCategory.js" />"></script>
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
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
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
	<div id="preloader" class="circlePreloader" style="display: none">
		<img src="<spring:url value="/images/ajax-loader-circ.gif" />">
	</div>
	<div class="clearB"></div>

	<div id="listContainer" class="floatL w730 marL10 marT27"></div>
	<div id="searchResult" class="floatL w215 marL10 marT27 fsize11">
		<div id="docs">
		
		</div>
	</div>
	<div id="category" class="floatR w215 marL10 marT27 fsize11">
		<div id="categoryDocs">
		
		</div>
	</div>
	<div id="brand" class="floatR w215 marL10 marT27 fsize11">
		<div id="brandDocs">
		
		</div>
	</div>
	
	<!--  <div id="noSelected"><img id="no-items-img" src="../images/facetSortRuleGuidelines.jpg"></div>-->


	<div class="clearB"></div>
</div>

<!-- End Right Side -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>