<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="browse"/>
<c:set var="submenu" value="product"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

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
    var WIDGET_TEXTDEFAULT_searchWithin = 'Search Within';</script>
<script type="text/javascript" src="<spring:url value="/js/dwr/browse.js" />"></script>

<!-- AdRotator Plugin -->
<script type="text/javascript" src="<spring:url value="/js/jquery/slidesjs-3.0/jquery.slides.min.js" />"></script>
<link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/slidesjs-3.0/slides.css" />">

<!-- Ajax Solr Dependencies -->
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/Core.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/AbstractManager.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/managers/Manager.jquery.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/Parameter.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/ParameterStore.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/helpers/jquery/ajaxsolr.theme.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/helpers/ajaxsolr.support.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/helpers/ajaxsolr.theme.js" />"></script>

<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/AbstractWidget.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/core/AbstractFacetWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/lib/widgets/jquery/CustomPagerWidget.js" />" ></script>

<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/browse.theme.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchResultWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/CurrentDateWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/CurrentSearchWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/PCMGSingleSelectorWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SortResultWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchResultHeaderWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/DidYouMeanWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchKeywordWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/AdRotatorWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/DynamicFacetWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/DynamicAttributeWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/SearchWithinWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/ProductConditionSelectorWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/ProductAttributeFilterWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/ActiveRuleWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/AnimatedTagCloudWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/RuleSelectorWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/CNETFacetWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/RedirectUrlWidget.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/widgets/RedirectUrlToggleWidget.js" />" ></script>

<script type="text/javascript" src="<spring:url value="/js/jquery/ajaxsolr.custom/browse.js" />" ></script>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/search/search.css" />">
<link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/ajaxsolr.custom/css/browse.css" />">

<!-- Start Left Side -->	   
<div class="clearB floatL minW240 sideMenuArea">
    <div class="clearB marT27"></div>
    <div id="dynamicSelection"></div>
    <div class="clearB"></div>
    <div id="cnetFacets"></div>
    <div class="clearB"></div>
    <div id="dynamicFacets"></div>
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
<div class="floatL w730 marL10 marT27" style="min-height:550px">
    <!-- Redirect Url Widget -->
    <div id="redirectUrlDiv" style="display: none; overflow: auto;">
        <div class="floatL w150 padT5" style="width: 100%;">
            URL:&nbsp;<span id="redirectUrlTextDiv"></span>
            <span id="redirectUrlLoading" class="circlePreloader" style="display:none">
                <img src="<spring:url value="/images/ajax-loader-rect.gif" />">
            </span>
        </div>

        <div id="IframeWrapper" style="position: relative;">
            <div id="iframeBlocker" style="position: absolute; top: 0; left: 0; width: 800px; height: 700px; background-color: white; opacity: 0.0;">
            </div>
        </div>
        <iframe id="redirectUrlDivIFrame" style="border: 0px; width: 800px; height: 600px;" 
                src="about:blank" scrolling="no" onload="$('#redirectUrlLoading').hide();"></iframe>
    </div>

    <!-- Text Widget -->
    <div id="search" class="floatL w730 titlePlacer marB10">
        <div class="w245 padT10 padL10 floatL fsize20 fnormal breakWord">Search Product</div>
        <div class="floatR">
            <div id="currentDate"></div>
            <div id="pcmgSelector"></div>
        </div>			         	
    </div>

    <!-- Rule Selector Widget && Redirect URL Toggle Widget -->
    <div id="ruleSelector"  class="clearfix pad5 fsize12 txtAL w720" style="background:#e8e8e8">
        <div class="floatR marL8 marTn2 marR3 padT2 dropdownArea w350 txtAR">
            <div id="searchKeyword"></div>		        	
        </div>
        <div class="floatL w350 dropdownArea">
            <div class="floatL w150 padT5">Select Ranking Rule:</div>
            <div class="floatL w200">
                <select id="rankingRule" class="w178 marT5"></select>
            </div>	        	
        </div>
        <div id="redirectUrlToggle" class="floatL w350">
            <div class="floatL w150 padT5">Query Cleaning:</div>
            <div class="floatL w200 padT5">
                <input id="enableRedirectToPage" type="checkbox"/>&nbsp;Enable Redirect To Page
            </div>
        </div>
    </div>



    <!-- Active Rule Widget -->
    <div id="activeRule"></div>

    <!-- Sort Result Widget -->
    <div class="borderT padL5 padB5 padR0" style="background:#f2f2f2">
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
</div>
<!-- End Right Side -->	  

<%@ include file="/WEB-INF/includes/footer.jsp" %>	