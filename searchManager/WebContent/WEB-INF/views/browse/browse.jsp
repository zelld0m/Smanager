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

  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/ResultWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/CurrentSearchWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/SortResultWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/TextWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/DynamicFacetWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/SearchWithinWidget.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/widgets/FilterResultByTypeWidget.js" />" ></script>
  
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/browse.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/browse.theme.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/ajaxsolr/jquery.livequery.js" />" ></script>
		
	
  <link type="text/css" rel="stylesheet" href="<spring:url value="/css/search/search.css" />">
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/ajaxsolr/css/browse.css" />">
	

    <!-- Start Left Side -->	   
    <div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="${storeLogo}" />"></a></div>
    <div class="clearB marT27"></div>
	<!-- Search Within Widget -->
    <div id="searchWithin" class="leftContainer"></div>
	<div id="dynamicSelection"></div>
	<div id="dynamicFacets"></div>
	<div class="clearB"></div>
	<div id="filterByType"></div>
	</div>
	<!-- End Left Side -->
	
    <!-- Start Right Side -->	  
	<div class="floatL w730 marL10 marT27" style="min-height:550px">
		  <!-- Search Widget -->
		  <div id="search" class="floatL w730 titlePlacer">
			
			<div class="w245 padT10 padL10 floatL fsize20 fnormal breakWord">Search Product</div>			         	
        	<div class="floatL w460 txtAR padT7"> 
        	    <a id="statisticIcon" href="javascript:void(0);"><img align="absmiddle" class="marR3 marT5 floatR  posRel" src="<spring:url value="/images/icon_statistics.png"/>"></a>
	        	<a id="searchbutton" href="javascript:void(0)"><img align="absmiddle" class="marR5 marLn4 marT1 floatR  posRel" src="<spring:url value="/js/ajaxsolr/images/btn_GO.png"/>"></a> 
				<!-- a id="searchOptionsIcon" href="javascript:void(0)"><div class="btnGraph btnSearchOption floatR  posRel"></div></a -->
				<div class="searchBoxHolder w150 floatR marT1 marR8"><input type="text" class="farial fsize12 fgray pad3 w150" id="query" name="query"></div>
				<div class="floatR posRel txtAL marR5" id="refinementHolder" style="display:none"><input id="keepRefinement" name="keepRefinement" type="checkbox"><span class="fsize11">Keep Refinements</span></div>    	 
			</div>			
		   </div>
		   
		   <div id="searchResultOption"  class="clearfix pad5 fsize12 txtAL w720" style="background:#e8e8e8">
	        	<!-- span class="fsize14 alert" style="color:#a90400">This is redirected</span -->
	        	<div class="floatL w60p dropdownArea" >
	        		<label class="floatL w150 fbold">Select Ranking Rule:</label>
		        	<label class="floatL w200 fbold">
		        	<select id="relevancy" class="w178">
		        		<option value="" id="norelevancy" >&nbsp;</option>
		        	</select>
		        	</label>	        	
			  	</div>   
			  	
	        	<div class="floatR marL8 marTn2 marR3 padT2"> Select Catalog: 
		        	<select>	
		        		<option>DEFAULT</option>
		        	</select>
	        	</div>
	        		
	        </div>
		   
		   <!--  start active rule --><div class="clearB"></div>
		   	<div style="display:block;" class="fsize12 marT10 fDGray border">
				<ul class="mar16 marB20 ">
					<li class="borderB padTB5 clearfix">
						<label class="floatL w20"><input type="checkbox"></label>
						<label class="floatL w20"><img src="<spring:url value="/images/icon_reviewContent2.png" />" class="top2 posRel"></label>
						<label class="floatL fbold w220">Lorem ipsum</label>
						<label class="floatL w425">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque gravida lacus id neque tempor quis suscipit nisl bibendum. Morbi vel tellus in  posu...</label>
					</li>
					<li class="borderB padTB5 clearfix">
						<label class="floatL w20"><input type="checkbox"></label>
						<label class="floatL w20"><img src="<spring:url value="/images/icon_reviewContent2.png" />" class="top2 posRel"></label>
						<label class="floatL fbold w220">Quisque eu feugiat mauris</label>
						<label class="floatL w425">Etiam ornare dictum facilisis. Aliquam erat volutpat. Morbi accumsan, felis nec pretium mattis, sem magna bibendum eros, at ornare purus urna in sem. Do.</label>
					</li>
				</ul>
				<div class="clearB"></div>
			</div>
			<a href="/"><div class="minW110 floatL borderB borderR borderL h27 posRel topn1 fbold fsize12 padT8" style="display:block; background: #fff; z-index:500; left:20px; color:#329eea;"><img src="<spring:url value="/images/icon_arrowDownBlue.png" />" class="top2 posRel marL5 marR3">3 Active Rules</div></a>
		 	<div class="clearB"></div><!--  end active rule -->
		 	
		 	
		  <!-- DidYouMean Widget 
		  <div class="clearB floatL farial fsize12 marT10 w730">Did you mean: <a href="#" class="fDblue fbold">Apple</a></div>-->

		  <!-- Sorting-->
		  <!-- div class="clearB floatR farial fsize12 fDGray fbold txtAR w730 GraytopLine">  		
		  </div -->
		   
		  <div class="posRel" style="display:block; top:-33px">
		  	  <div class="borderT padL5 padB5 padR0 h33" style="background:#f2f2f2">
			  	  <!-- div id="searchAttributeIconHolder" class="floatL displayInline w50p marT8">
			        	<img src="<spring:url value="/images/icon_catalog.png" />" class="marR3">
			        	<img src="<spring:url value="/images/icon_relevancy.png" />" class="marR3">
			        	<img src="<spring:url value="/images/icon_redirect.png" />" >
		          </div -->
		          
			  	  <div id="sortResult" class="floatR marL8 marT4 fsize12"></div>
			  	  <div class="clearB"></div>
			  </div>
			  <div id="pager-header" class="clearB floatL farial fsize11 fDblue w300 padT10"></div>
			  <div class="floatR farial fsize11 fgray txtAR padT10"><div class="txtAR"><ul id="pager" class="pagination"></ul></div></div>
		  </div>
		  
		  <div class="w740 txtAR padT20">
		  	    <div class="clearB"></div>
			    <div id="canvasContainer" class="w460 txtAR">
			    	<canvas width="740" height="500" id="tagCanvas">
						<p>In Internet Explorer versions up to 8, things inside the canvas are inaccessible!</p>
					</canvas>
				</div>
				<div class="clearB"></div>
				<div id="tagContainer">
				  <ul id="tagList"></ul>
			 	</div>
		  </div>			
					
		  <!-- Search Results-->
		  <div class="clearB"></div>
		  	<div id="docs" class="clearB floatL w730 borderB"></div>
		  <div class="clearB"></div>
		  
		   <!-- Search Options-->
		  <div id="searchOptionsTemplate" style="display:none">
		  	<div class="w200 marT10">
		  		<label class="floatL w60 padT5">Catalog:</label>
		  		<label style="float:left; width: 130px;">
			  		<select id="catalog" class="w120">
			  			<option>Default</option>
			  			<option>HP Only</option>
			  		</select>
		  		</label>
		  		<div class="clearB marT6"></div>
		  		<!-- label class="marT6 floatL w60 valignMid">Relevancy:</label>
		  		<label class="marT6" style="float:left; width: 130px;">
			  		<select id="relevancy" class="w120">
			  			<option value="">Schema Default</option>
			  		</select>
		  		</label -->
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