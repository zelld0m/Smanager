  <!--PC Mall Menu-->
   <div class="clearB floatR bgTopHeaderMnu">
    <div class="mar0" style="padding-left:280px; width: 700px" >
      <ul class="mnuHeader farial fsize12 fbold" >
              <li ${topmenu eq 'home'? 'class="active"': ''}><span><a href="<spring:url value="/" />">Home</a></span></li>
              <li ${topmenu eq 'browse'? 'class="active"': ''}><span><a href="<spring:url value="/browse/"/>${store}">Simulator</a></span></li>
              <!-- 
              <li ${topmenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/"/>${store}">Relevancy</a></li>
              <li ${topmenu eq 'synonyms'? 'class="active"': ''}><a href="<spring:url value="/synonyms/"/>${store}">Synonyms</a></li>
               -->
              <li ${topmenu eq 'rules'? 'class="active"': ''}><span><a href="<spring:url value="/elevate/"/>${store}">Search Rules</a></span></li>
              <li ${topmenu eq 'advertise'? 'class="active"': ''}><span><a href="<spring:url value="/campaign/"/>${store}">Search Ads</a></span></li>
              <li ${topmenu eq 'statistic'? 'class="active"': ''}><span><a href="<spring:url value="/topkeyword/"/>${store}">Statistics</a></span></li>
              <li ${topmenu eq 'lexicon'? 'class="active"': ''}><span><a href="<spring:url value="/synonym/"/>${store}">Linguistics</a></span></li>
              <li ${topmenu eq 'setting'? 'class="active"': ''}><span><a href="<spring:url value="/audit/"/>${store}">Settings</a></span></li>
              <!-- li ${topmenu eq 'migrator'? 'class="active"': ''}><a href="<spring:url value="/migrator/"/>${store}">Migrator</a></li -->
      </ul>
      </div>
      <div class="clearB"></div>
      
      <div class="subMenuHolder">
      	<div class="w980 mar0">
	       <c:if test="${topmenu eq 'dashboard'}">
		       <!-- ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'topkeyword'? 'class="active"': ''}><a href="<spring:url value="/"/>">Top Keywords</a></li>
		      		<li ${submenu eq 'zeroresult'? 'class="active"': ''}><a href="<spring:url value="/zeroresult/"/>${store}">Zero Results</a></li>     		
		       </ul -->
	      </c:if>
	      
	      <c:if test="${topmenu eq 'browse'}">
		       <!--ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'product'? 'class="active"': ''}><a href="<spring:url value="/browse/"/>${store}">Product</a></li>
		      		<li ${submenu eq 'sitecontent'? 'class="active"': ''}><a href="<spring:url value="/browse/"/>${store}">Site Content</a></li>     		
		       </ul-->
	      </c:if>
	      
	      <c:if test="${topmenu eq 'rules'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'elevate'? 'class="active"': ''}><a href="<spring:url value="/elevate/"/>${store}">Elevate</a></li>
		      		<li ${submenu eq 'exclude'? 'class="active"': ''}><a href="<spring:url value="/exclude/"/>${store}">Exclude</a></li>
		      		<li ${submenu eq 'demote'? 'class="active"': ''}><a href="<spring:url value="/demote/"/>${store}">Demote</a></li>
		      		<li ${submenu eq 'facet'? 'class="active"': ''}><a href="<spring:url value="/facet/"/>${store}">Facet Sort</a></li>  		      		
<%-- 		      		<li ${submenu eq 'feature'? 'class="active"': ''}><a href="<spring:url value="/feature/"/>${store}">Feature</a></li> --%>
		      		<li ${submenu eq 'redirect'? 'class="active"': ''}><a href="<spring:url value="/redirect/"/>${store}">Query Cleaning</a></li>		      		
		      		<li ${submenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/"/>${store}">Ranking Rule</a></li>
<%-- 		      		<li ${submenu eq 'catalog'? 'class="active"': ''}><a href="<spring:url value="/catalog/"/>${store}">Catalog</a></li>  		      		 --%>
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'advertise'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'campaign'? 'class="active"': ''}><a href="<spring:url value="/campaign/"/>${store}">Campaign</a></li>
		      		<li ${submenu eq 'banner'? 'class="active"': ''}><a href="<spring:url value="/banner/"/>${store}">Banner</a></li>	      		
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'statistic'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'topkeyword'? 'class="active"': ''}><a href="<spring:url value="/topkeyword/"/>${store}">Top Keyword</a></li> 		
		      		<li ${submenu eq 'zeroresult'? 'class="active"': ''}><a href="<spring:url value="/zeroresult/"/>${store}">Zero Result</a></li> 	
		      		<li ${submenu eq 'keywordtrends'? 'class="active"': ''}><a href="<spring:url value="/keywordtrends/"/>${store}">Keyword Trends</a></li> 		
		       		<li ${submenu eq 'reportgenerator'? 'class="active"': ''}><a href="<spring:url value="/reportgenerator/"/>${store}">Report Generator</a></li>
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'lexicon'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'synonym'? 'class="active"': ''}><a href="<spring:url value="/synonym/"/>${store}">Synonym</a></li>
		      		<li ${submenu eq 'stopword'? 'class="active"': ''}><a href="<spring:url value="/stopword/"/>${store}">Stopword</a></li>	      		
		      		<li ${submenu eq 'protword'? 'class="active"': ''}><a href="<spring:url value="/protword/"/>${store}">Protword</a></li>	      		
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'setting'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'audit'? 'class="active"': ''}><a href="<spring:url value="/audit/"/>${store}">Audit Trail</a></li>
		      		<li ${submenu eq 'setting'? 'class="active"': ''}><a href="<spring:url value="/setting/"/>${store}">User Setting</a></li>	  
		      		<sec:authorize access="hasRole('MANAGE_USER')">    		
		      			<li ${submenu eq 'security'? 'class="active"': ''}><a href="<spring:url value="/security/"/>${store}">Security</a></li>
		      		</sec:authorize>	      		
		      		<sec:authorize access="hasRole('APPROVE_RULE')">    		
			      		<li ${submenu eq 'approval'? 'class="active"': ''}><a href="<spring:url value="/approval/"/>${store}">Pending Approval</a></li>
		      		</sec:authorize>
		      		<sec:authorize access="hasRole('PUBLISH_RULE')">    		
			      		<li ${submenu eq 'production'? 'class="active"': ''}><a href="<spring:url value="/production/"/>${store}">Push to Prod</a></li>
		      		</sec:authorize>
		      		
		      		<c:if test="${store eq 'pcmallcap'}">
		      		<sec:authorize access="hasAnyRole('CREATE_RULE','APPROVE_RULE','PUBLISH_RULE')">    		
			      		<li ${submenu eq 'import'? 'class="active"': ''}><a href="<spring:url value="/import/"/>${store}">Import Rule</a></li>
		      		</sec:authorize>
		      		</c:if>
		      		
		      		<c:if test="${store eq 'pcmall'}">
		      		<sec:authorize access="hasRole('PUBLISH_RULE')">    		
			      		<li ${submenu eq 'export'? 'class="active"': ''}><a href="<spring:url value="/export/"/>${store}">Export Rule</a></li>
		      		</sec:authorize>	      		
		      		</c:if>
		      		
		      		<li ${submenu eq 'monitor'? 'class="active"': ''}><a href="<spring:url value="/monitor/" />">Monitor</a></li>	      		
		       </ul>
	      </c:if>
	          
      	</div>
      </div>    
   </div>
   <div class="clearB"> &nbsp; </div>
<div class="mar0 w980">
