<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="browse"/>
<c:set var="submenu" value="product"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

  <script type="text/javascript" src="<spring:url value="/js/dwr/browse.js" />"></script>
	
  <!-- Ajax Solr Dependencies -->
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/core/Core.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/core/AbstractManager.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/managers/Manager.jquery.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/core/Parameter.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/core/ParameterStore.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/helpers/jquery/ajaxsolr.theme.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/helpers/ajaxsolr.support.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/helpers/ajaxsolr.theme.js" />"></script>
  
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/core/AbstractWidget.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/core/AbstractFacetWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/lib/widgets/jquery/PagerWidget.js" />" ></script>

  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/browse.theme.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/ResultWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/CurrentSearchWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/SortResultWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/TextWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/DynamicFacetWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/SearchWithinWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/ProductAttributeFilterWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/ActiveRuleWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/AnimatedTagCloudWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/RuleSelectorWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/CNETFacetWidget.js" />" ></script>
  
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/browse.js" />" ></script>
  
  <link type="text/css" rel="stylesheet" href="<spring:url value="/css/search/search.css" />">
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/ajaxsolr/css/browse.css" />">
	
    <!-- Start Left Side -->	   
	<div class="clearB floatL minW240 sideMenuArea">
	    <div class="clearB marT27"></div>
	    <div id="searchWithin" class="leftContainer"></div>
	    <div class="clearB"></div>
		<div id="dynamicSelection"></div>
		<div class="clearB"></div>
		<div id="cnetFacets"></div>
		<div class="clearB"></div>
		<div id="dynamicFacets"></div>
		<div class="clearB"></div>
		<div id="prodAttribFilter"></div>
		<div class="clearB"></div>
	</div>
	<!-- End Left Side -->
	
    <!-- Start Right Side -->	  
	<div class="floatL w730 marL10 marT27" style="min-height:550px">
		  <!-- Text Widget -->
		  <div id="search" class="floatL w730 titlePlacer">
			<div class="w245 padT10 padL10 floatL fsize20 fnormal breakWord">Search Product</div>			         	
        	<div class="floatL w460 txtAR padT7"> 
        	    <a id="statisticIcon" href="javascript:void(0);"><img align="absmiddle" class="marR3 marT5 floatR  posRel" src="<spring:url value="/images/icon_statistics.png"/>"></a>
	        	<a id="searchbutton" href="javascript:void(0)"><img align="absmiddle" class="marR5 marLn4 marT1 floatR  posRel" src="<spring:url value="/js/ajaxsolr/images/btn_GO.png"/>"></a> 
				<div class="searchBoxHolder w150 floatR marT1 marR8"><input type="text" class="farial fsize12 fgray pad3 w150" id="query" name="query"></div>
				<div class="floatR posRel txtAL marR5" id="refinementHolder" style="display:none"><input id="keepRefinement" name="keepRefinement" type="checkbox"><span class="fsize11">Keep Refinements</span></div>    	 
			</div>			
		   </div>
		   
		   <!-- Rule Selector Widget -->
		   <div id="ruleSelector"  class="clearfix pad5 fsize12 txtAL w720" style="background:#e8e8e8">
	        	<div class="floatR marL8 marTn2 marR3 padT2 dropdownArea w350 txtAR" style="display:none">
	        	 	<label class="floatR"> 
			        	<select>	
			        		<option>DEFAULT</option>
			        	</select>
		        	</label>
	        	 	<label class="floatR w90 padT5">Select Catalog:</label>		        	
	        	</div>
	        	<div class="floatL w350 dropdownArea" >
	        		<label class="floatL w150 padT5">Select Ranking Rule:</label>
		        	<label class="floatL w200">
			        	<select id="rankingRule" class="w178"></select>
		        	</label>	        	
			  	</div>
	      </div>
		  
		  <!-- Active Rule Widget -->
		  <div id="activeRule"></div>
		  
		  <!-- Sort Result Widget -->
		  <div class="borderT padL5 padB5 padR0" style="background:#f2f2f2">
			<div id="sortResult" class="floatR marL8 marT4 fsize12"></div>
			<div class="clearB"></div>
		  </div>
		  
		  <!-- Result & Pager Widget -->
		  <div>
		 	  <div id="top-pager-text" class="clearB floatL farial fsize11 fDblue w300 padT10"></div>
			  <div class="floatR farial fsize11 fgray txtAR padT10">
			  <div class="txtAR">
				  <ul id="top-pager" class="pagination"></ul>
				  </div>
			  </div>
			  <div class="clearB"></div>
		  	  
		  	  <div id="tagCloud" class="clearB floatL w730"></div>
		  	
		  	  <div id="docs" class="clearB floatL w730"></div>
		  	  
		  	  <div class="clearB"></div>
		  	  <div id="bottom-pager-text" class="clearB floatL farial fsize11 fDblue w300 padT10"></div>
			  <div class="floatR farial fsize11 fgray txtAR padT10">
				  <div class="txtAR">
				  	<ul id="bottom-pager" class="pagination"></ul>
				  </div>
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