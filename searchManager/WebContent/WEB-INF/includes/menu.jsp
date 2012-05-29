  <!--PC Mall Menu-->
   <!-- TODO: Dynamically modify mall based on logged user -->
   
	<spring:eval expression="T(com.search.manager.service.UtilityService).getStoreName()" var="store" />

   <div class="clearB floatR bgTopHeaderMnu">
    <div class="mar0" style="padding-left:280px; width: 700px" >
      <ul class="mnuHeader farial fsize12 fbold" >
              <li ${topmenu eq 'home'? 'class="active"': ''}><span><a href="<spring:url value="/" />">Home</a></span></li>
              <li ${topmenu eq 'browse'? 'class="active"': ''}><span><a href="<spring:url value="/browse/"/>${store}">Simulator</a></span></li>
              <!-- 
              <li ${topmenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/"/>${store}">Relevancy</a></li>
              <li ${topmenu eq 'synonyms'? 'class="active"': ''}><a href="<spring:url value="/synonyms/"/>${store}">Synonyms</a></li>
               -->
              <li ${topmenu eq 'bigbets'? 'class="active"': ''}><span><a href="<spring:url value="/elevate/"/>${store}">Search Rules</a></span></li>
              <li ${topmenu eq 'advertise'? 'class="active"': ''}><span><a href="<spring:url value="/campaign/"/>${store}">Search Ads</a></span></li>
              <li ${topmenu eq 'statistic'? 'class="active"': ''}><span><a href="<spring:url value="/statistic/"/>${store}">Statistics</a></span></li>
              <li ${topmenu eq 'lexicon'? 'class="active"': ''}><span><a href="<spring:url value="/synonym/"/>${store}">Linguistics</a></span></li>
              <li ${topmenu eq 'setting'? 'class="active"': ''}><span><a href="<spring:url value="/audit/"/>${store}">Settings</a></span></li>
              <!-- li ${topmenu eq 'migrator'? 'class="active"': ''}><a href="<spring:url value="/migrator/"/>${store}">Migrator</a></li -->
      </ul>
      </div>
      <div class="clearB"></div>
      
      <div class="subMenuHolder">
      	<div class="w980 mar0">
	      <c:if test="${topmenu eq 'browse'}">
		       <!--ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'product'? 'class="active"': ''}><a href="<spring:url value="/browse/"/>${store}">Product</a></li>
		      		<li ${submenu eq 'sitecontent'? 'class="active"': ''}><a href="<spring:url value="/browse/"/>${store}">Site Content</a></li>     		
		       </ul-->
	      </c:if>
	      
	      <c:if test="${topmenu eq 'bigbets'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'elevate'? 'class="active"': ''}><a href="<spring:url value="/elevate/"/>${store}">Elevate</a></li>
		      		<li ${submenu eq 'exclude'? 'class="active"': ''}><a href="<spring:url value="/exclude/"/>${store}">Exclude</a></li>
		      		<li ${submenu eq 'feature'? 'class="active"': ''}><a href="<spring:url value="/feature/"/>${store}">Feature</a></li>
		      		<li ${submenu eq 'redirect'? 'class="active"': ''}><a href="<spring:url value="/redirect/"/>${store}">Query Cleaning</a></li>		      		
		      		<li ${submenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/"/>${store}">Ranking Rule</a></li>
		      		<li ${submenu eq 'catalog'? 'class="active"': ''}><a href="<spring:url value="/catalog/"/>${store}">Catalog</a></li>  		      		
		      		<li ${submenu eq 'facet'? 'class="active"': ''}><a href="<spring:url value="/facet/"/>${store}">Facet Rule</a></li>  		      		
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'advertise'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'campaign'? 'class="active"': ''}><a href="<spring:url value="/campaign/"/>${store}">Campaign</a></li>
		      		<li ${submenu eq 'banner'? 'class="active"': ''}><a href="<spring:url value="/banner/"/>${store}">Banner</a></li>	      		
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
		      		<li ${submenu eq 'security'? 'class="active"': ''}><a href="<spring:url value="/security/"/>${store}">Security</a></li>	      		
		      		<li ${submenu eq 'sponsor'? 'class="active"': ''}><a href="<spring:url value="/sponsor/"/>${store}">Partners</a></li>	      		
		      		<li ${submenu eq 'approval'? 'class="active"': ''}><a href="<spring:url value="/approval/"/>${store}">Pending Approval</a></li>	      		
		      		<li ${submenu eq 'production'? 'class="active"': ''}><a href="<spring:url value="/production/"/>${store}">Push to Prod</a></li>	      		
		      		<li ${submenu eq 'monitor'? 'class="active"': ''}><a href="<spring:url value="/monitor/" />">Monitor</a></li>	      		
		      		<li ${submenu eq 'template'? 'class="active"': ''}><a href="<spring:url value="/template/" />">Template</a></li>	      		
		       </ul>
	      </c:if>
	          
      	</div>
      </div>    
   </div>
   <div class="clearB"> &nbsp; </div>
<div class="mar0 w980">