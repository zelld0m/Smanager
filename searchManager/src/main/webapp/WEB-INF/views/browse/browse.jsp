<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="browse" />
<c:set var="submenu" value="product" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<script type="text/javascript">
	 	var WIDGET_ID_redirectUrl = 'redirectUrl';
        var WIDGET_ID_redirectUrlToggle = 'redirectUrlToggle';
		var WIDGET_ID_searchWithin = 'searchWithin';
		var WIDGET_ID_searchKeyword = 'searchKeyword';
		var WIDGET_ID_searchResult = 'searchResult';
		var WIDGET_ID_cnetFacet = 'cnetFacet';
		var WIDGET_ID_pager = 'pager';
		var WIDGET_TARGET_redirectUrl = '#redirectUrl';
        var WIDGET_TARGET_redirectUrlToggle = '#redirectUrlToggle';
		var WIDGET_TARGET_searchWithin = '#searchWithin';
		var WIDGET_TARGET_searchResult = '#docs';
		var WIDGET_TARGET_searchKeyword = '#searchKeyword';
		var WIDGET_TARGET_cnetFacet = '#cnetFacets';
		var WIDGET_TARGET_pager = '#top-pager,#bottom-pager';
		var WIDGET_TEXTDEFAULT_searchKeyword = 'Enter Keyword';
		var WIDGET_TEXTDEFAULT_searchWithin = 'Search Within';
	</script>
<script type="text/javascript"
	src="<spring:url value="/js/dwr/browse.js" />"></script>


<!-- AdRotator Plugin -->
<script type="text/javascript"
	src="<spring:url value="/js/jquery/slidesjs-3.0/jquery.slides.min.js" />"></script>
<link type="text/css" rel="stylesheet"
	href="<spring:url value="/js/jquery/slidesjs-3.0/slides.css" />">

<!-- Ajax Solr Dependencies -->
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/Core.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/AbstractManager.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/managers/Manager.jquery.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/Parameter.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/ParameterStore.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/helpers/jquery/ajaxsolr.theme.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/helpers/ajaxsolr.support.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/helpers/ajaxsolr.theme.js" />"></script>

<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/AbstractWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/AbstractFacetWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/widgets/jquery/CustomPagerWidget.js" />"></script>

<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/browse.theme.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchResultWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/CurrentDateWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/CurrentSearchWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/PCMGSingleSelectorWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SortResultWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchResultHeaderWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/DidYouMeanWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchKeywordWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/AdRotatorWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/DynamicFacetWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/DynamicAttributeWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/MultiSearchWithinWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchWithinWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/ProductConditionSelectorWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/ProductAttributeFilterWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/ActiveRuleWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/AnimatedTagCloudWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/RuleSelectorWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/CNETFacetWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/RedirectUrlWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/RedirectUrlToggleWidget.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/AvailabilityWidget.js" />"></script>

<script type="text/javascript"
	src="<spring:url value="/js/jquery/ajaxsolr.custom/browse.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/ajax-solr-06-01-2013/widgets/TypeaheadSearchResult.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/ajax-solr-06-01-2013/widgets/TypeaheadBrand.js" />"></script>
<script type="text/javascript"
	src="<spring:url value="/js/ajax-solr-06-01-2013/widgets/TypeaheadCategory.js" />"></script>

<link type="text/css" rel="stylesheet"
	href="<spring:url value="/css/search/search.css" />">
<link type="text/css" rel="stylesheet"
	href="<spring:url value="/js/jquery/ajaxsolr.custom/css/browse.css" />">
<style>
.ui-autocomplete {
	list-style: none;
	padding-left: 5px;
	max-height: 1080px !important;
	overflow-x: hidden;
	overflow-y: hidden !important;
	width: 330px;
}

.typeahead {
	background-color: #fff;
	border: 1px solid #cfcfcf;
	border-bottom-left-radius: 5px;
	border-bottom-right-radius: 5px;
	box-shadow: 0 5px 5px #c9c8c8;
	display: none;
	position: absolute;
	z-index: 500;
}

.dft, .dft * {
    -moz-font-feature-settings: inherit;
    -moz-font-language-override: inherit;
    border: 0 none;
    color: #333;
    font-family: "Open Sans",Arial,sans-serif;
    font-size: inherit;
    font-size-adjust: inherit;
    font-stretch: inherit;
    font-style: inherit;
    font-variant: inherit;
    font-weight: inherit;
    line-height: inherit;
    margin: 0;
    padding: 0;
    vertical-align: baseline;
}

.typeahead ul {
	list-style-type: none;
}
.typeahead .suggest .tbl {
    border-collapse: collapse;
    border-spacing: 0;
    display: table;
    width: 100%;
}
.typeahead .suggest .tr {
    display: table-row;
}
.typeahead .suggest .td {
    display: table-cell;
}
.typeahead {
    background-color: #fff;
    border: 1px solid #cfcfcf;
    border-bottom-left-radius: 5px;
    border-bottom-right-radius: 5px;
    box-shadow: 0 5px 5px #c9c8c8;
    display: none;
    position: absolute;
    z-index: 500;
}
.typeahead h3 {
    background-color: #f1f1f1;
    color: #000;
    font-weight: bold;
    line-height: 30px;
    padding: 0 20px;
}
.typeahead a {
    display: block;
    position: relative;
    text-decoration: none !important;
    z-index: 200;
}
.typeahead a, .typeahead a span, .typeahead h3 {
    font-size: 12px;
    text-align: left;
}
.typeahead .txt {
    font-weight: bold;
    margin: 0 5px 0 0;
}
.typeahead .normal {
    font-weight: normal;
}
.typeahead .count {
    color: #0088cc;
}
.typeahead .first-lvl {
    padding: 5px 0;
}
.typeahead .first-lvl .second-lvl {
    padding: 0;
}
.typeahead .first-lvl > li > a {
    padding: 0 20px;
}
.typeahead .first-lvl > li > a {
    line-height: 31px;
}
.typeahead .second-lvl {
    padding: 5px 0;
}
.typeahead .second-lvl > li > a {
    color: #666;
    padding: 0 30px;
}
.typeahead .second-lvl > li > a {
    line-height: 24px;
}
.typeahead .suggest .td {
    height: 92px;
    vertical-align: middle;
}
.typeahead .suggest li {
    border-top: 1px solid #dbdbdb;
}
.typeahead .suggest li:first-child {
    border: medium none;
}
.typeahead .suggest .col-img {
    text-align: center;
    width: 92px;
}
.typeahead .suggest .offer {
    color: #ca272d;
    font-weight: bold;
}
.typeahead .suggest .prod-title {
    color: #000;
}
.typeahead .suggest .offer, .typeahead .suggest .prod-title {
    display: block;
    line-height: 145%;
    margin: 0 20px 0 5px;
}
.typeahead .highlight {
    background-color: #f1f1f1;
    position: absolute;
    right: 0;
    top: 0;
    width: 100%;
    z-index: 100;
}

.dft, .dft * {
    -moz-font-feature-settings: inherit;
    -moz-font-language-override: inherit;
    border: 0 none;
    color: #333;
    font-family: "Open Sans",Arial,sans-serif;
    font-size: inherit;
    font-size-adjust: inherit;
    font-stretch: inherit;
    font-style: inherit;
    font-variant: inherit;
    font-weight: inherit;
    line-height: inherit;
    margin: 0;
    padding: 0;
    vertical-align: baseline;
}
</style>
<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="clearB marT27"></div>
	<div id="dynamicSelection"></div>
	<div class="clearB"></div>
	<div id="cnetFacets"></div>
	<div class="clearB"></div>
	<div id="dynamicFacets"></div>
	<div class="clearB"></div>
	<div id="availabilityFacets"></div>
	<div class="clearB"></div>
	<div id="dynamicAttributes"></div>
	<div class="clearB"></div>
	<div id="prodCondSelector"></div>
	<div class="clearB"></div>
	<div id="prodAttribFilter"></div>
	<div class="clearB"></div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27" style="min-height: 550px">
	<!-- Redirect Url Widget -->
	<div id="redirectUrlDiv" style="display: none; overflow: auto;">
		<div class="floatL w150 padT5" style="width: 100%;">
			URL:&nbsp;<span id="redirectUrlTextDiv"></span> <span
				id="redirectUrlLoading" class="circlePreloader"
				style="display: none"> <img
				src="<spring:url value="/images/ajax-loader-rect.gif" />">
			</span>
		</div>

		<div id="IframeWrapper" style="position: relative;">
			<div id="iframeBlocker"
				style="position: absolute; top: 0; left: 0; width: 750px; height: 500px; background-color: white; opacity: 0.0;">
			</div>
		</div>
		<iframe id="redirectUrlDivIFrame"
			style="border: 0px; width: 750px; height: 450px;" src="about:blank"
			scrolling="no" onload="$('#redirectUrlLoading').hide();"></iframe>
	</div>

	<!-- Text Widget -->
	<div id="search" class="floatL w730 titlePlacer marB10">
		<div class="w245 padT10 padL10 floatL fsize20 fnormal breakWord">Search
			Product</div>
		<div class="floatR">
			<div id="currentDate"></div>
			<div id="pcmgSelector"></div>
		</div>
	</div>

	<!-- Rule Selector Widget && Redirect URL Toggle Widget  -->
	<div id="ruleSelector" class="clearfix pad5 fsize12 txtAL w720"
		style="background: #e8e8e8">
		<div class="floatR marL8 marTn2 marR3 padT2 dropdownArea w350 txtAR"
			style="height: 32px;">
			<div id="searchKeyword"></div>
		</div>
		<div class="floatL w350 dropdownArea">
			<div class="floatL w150 padT5">Select Relevancy Rule:</div>
			<div class="floatL w200">
				<select id="rankingRule" class="w178 marT5"></select>
			</div>
		</div>
		<div id="redirectUrlToggle" class="floatL w350">
			<div class="floatL w150 padT5">Redirect Rule:</div>
			<div class="floatL w200 padT5">
				<input id="enableRedirectToPage" type="checkbox" />&nbsp;Enable Page
				Redirection
			</div>
		</div>
	</div>

	<!-- Active Rule Widget -->
	<div id="activeRule"></div>

	<!-- Sort Result Widget -->
	<div class="borderT padL5 padB5 padR0" style="background: #f2f2f2">
		<div id="searchWithin" class="floatL"></div>
		<div id="sortResult" class="floatR marL8 marT4 fsize12"></div>
		<div class="clearB"></div>
	</div>

	<!-- AdRotator Widget -->
	<div id="adRotator" class="padT10"></div>

	<!-- Search Result Header Widget -->
	<div id="searchResultHeader" class="padT10"></div>

	<!-- Did You Mean Widget -->
	<div id="didYouMean" class="padT10"></div>

	<!-- Result & Pager Widget -->
	<div>
		<div class="farial fsize11 fgray padT10">
			<div id="top-pager-text" class="floatL fDblue w300"></div>
			<div id="top-pager" class="floatR fDblue w300"></div>
		</div>
		<div class="clearB"></div>

		<div id="tagCloud" class="clearB floatL w730"></div>

		<div id="docs" class="clearB floatL w730"></div>

		<div class="clearB"></div>
		<div class="farial fsize11 fgraypadT10">
			<div id="bottom-pager-text" class="floatL fDblue w300"></div>
			<div id="bottom-pager" class="floatR fDblue w300"></div>
		</div>
	</div>

	<div id="viewAuditTemplate" style="display: none">
		<div class="elevateItemPW">
			<div class="w265 padB8">
				<div id="auditTemplate" style="display: none;">
					<div class="pad8 borderB">
						<div class="padR8 floatL wordwrap" style="width: 60px">%%timestamp%%</div>
						<div class="floatL w175">
							<img src="<spring:url value="/images/user13x13.png" />"
								class="marBn3 marR3"> <span class="fDblue">%%commentor%%</span>
							<span>%%comment%%</span>
						</div>
						<div class="clearB"></div>
					</div>
				</div>
				<div id="auditPagingTop"></div>
				<div class="clearB"></div>
				<div id="auditHolder"></div>
				<div class="clearB"></div>
				<div id="auditPagingBottom" style="margin-top: 8px"></div>

			</div>
		</div>
	</div>
	<div id="mockTypeahead" style="display: none;">
		<div class="dft typeahead"
			style="display: none; width: 414px; top: 36px;" id="typeahead">
			<div class="highlight"
				style="overflow: visible; box-sizing: content-box; width: 100%; min-width: 0px; max-width: none; height: 24px; min-height: 0px; max-height: none; padding-left: 0px; display: block; top: 271px;"></div>
			<h3 class="first">Matching Keywords</h3>
			<ul class="first-lvl">
				<li><a href="/s?rch&includeImage=true&q=acer&ssrc=box"> <span
						class="txt">acer</span> <span class="count">(855)</span>
				</a>
					<ul class="second-lvl">
						<li><a data-keyword="acer"
							href="/s?rch&includeImage=true&q=acer&stn=Tablet Cases/Covers&ssrc=box">in
								Tablet Cases/Covers</a></li>
						<li><a data-keyword="acer"
							href="/s?rch&includeImage=true&q=acer&stn=Software&ssrc=box">in
								Software</a></li>
						<li><a data-keyword="acer"
							href="/s?rch&includeImage=true&q=acer&stn=Computers&ssrc=box">in
								Computers</a></li>
					</ul></li>
				<li><a href="/s?rch&includeImage=true&q=acer notebook&ssrc=box">
						<span class="txt">acer notebook</span> <span class="count">(221)</span>
				</a></li>
				<li><a
					href="/s?rch&includeImage=true&q=acer desktop computer&ssrc=box">
						<span class="txt">acer desktop computer</span> <span class="count">(141)</span>
				</a></li>
				<li><a href="/s?rch&includeImage=true&q=acer veriton&ssrc=box">
						<span class="txt">acer veriton</span> <span class="count">(119)</span>
				</a></li>
			</ul>
			<h3>Matching Brands</h3>
			<ul class="second-lvl">
				<li><a data-keyword="acer"
					href="/s?rch&includeImage=true&q=acer&man=Acer&ssrc=box">by
						Acer</a></li>
			</ul>
			<h3>Suggestions for acer</h3>
			<ul class="suggest">
				<li><a data-keyword="8305920"
					title="V206HQL - LED monitor - 20" " href="/p/8305920"> <span
						class="tbl"> <span class="tr"> <span
								class="td col-img"> <img width="67"
									e_src="/mall/widgetti/images/shared/noImageMed.jpg"
									src="http://image1.cc-inc.com/prod/9690000/9690259_sm.jpg">
							</span> <span class="td col-desc"> <span class="offer">Acer</span>
									<span class="prod-title" style="color: rgb(0, 0, 0);">V206HQL
										- LED monitor - 20"</span>
							</span>
						</span>
					</span>
				</a></li>
				<li><a data-keyword="8144474"
					title="V226WLbmd - LED monitor - 22" " href="/p/8144474"> <span
						class="tbl"> <span class="tr"> <span
								class="td col-img"> <img width="67"
									e_src="/mall/widgetti/images/shared/noImageMed.jpg"
									src="http://image1.cc-inc.com/prod/9527000/9527846_sm.jpg">
							</span> <span class="td col-desc"> <span class="offer">Acer</span>
									<span class="prod-title" style="color: rgb(0, 0, 0);">V226WLbmd
										- LED monitor - 22"</span>
							</span>
						</span>
					</span>
				</a></li>
				<li><a data-keyword="8128928"
					title="V196WL bd - LED monitor - 19" " href="/p/8128928"> <span
						class="tbl"> <span class="tr"> <span
								class="td col-img"> <img width="67"
									e_src="/mall/widgetti/images/shared/noImageMed.jpg"
									src="http://image1.cc-inc.com/prod/9512000/9512302_sm.jpg">
							</span> <span class="td col-desc"> <span class="offer">Acer</span>
									<span class="prod-title" style="color: rgb(0, 0, 0);">V196WL
										bd - LED monitor - 19"</span>
							</span>
						</span>
					</span>
				</a></li>
			</ul>
		</div>
	</div>
</div>
<!-- End Right Side -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>
