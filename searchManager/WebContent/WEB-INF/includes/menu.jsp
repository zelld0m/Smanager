  <!--PC Mall Menu-->
   <!-- TODO: Dynamically modify mall based on logged user -->
   <div class="clearB floatR bgTopHeaderMnu">
    <div class="mar0" style="padding-left:280px; width: 700px" >
      <ul class="mnuHeader farial fsize12 fbold" >
              <li ${topmenu eq 'home'? 'class="active"': ''}><span><a href="<spring:url value="/" />">Home</a></span></li>
              <li ${topmenu eq 'browse'? 'class="active"': ''}><span><a href="<spring:url value="/browse/macmall" />">Simulator</a></span></li>
              <!-- 
              <li ${topmenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/macmall"/>">Relevancy</a></li>
              <li ${topmenu eq 'synonyms'? 'class="active"': ''}><a href="<spring:url value="/synonyms/macmall"/>">Synonyms</a></li>
               -->
              <li ${topmenu eq 'bigbets'? 'class="active"': ''}><span><a href="<spring:url value="/elevate/macmall"/>">Search Rules</a></span></li>
              <li ${topmenu eq 'advertise'? 'class="active"': ''}><span><a href="<spring:url value="/campaign/macmall"/>">Search Ads</a></span></li>
              <li ${topmenu eq 'statistic'? 'class="active"': ''}><span><a href="<spring:url value="/statistic/macmall"/>">Statistics</a></span></li>
              <li ${topmenu eq 'lexicon'? 'class="active"': ''}><span><a href="<spring:url value="/synonym/macmall"/>">Linguistics</a></span></li>
              <li ${topmenu eq 'setting'? 'class="active"': ''}><span><a href="<spring:url value="/audit/macmall"/>">Settings</a></span></li>
              <!-- li ${topmenu eq 'migrator'? 'class="active"': ''}><a href="<spring:url value="/migrator/macmall"/>">Migrator</a></li -->
      </ul>
      </div>
      <div class="clearB"></div>
      
      <div class="subMenuHolder">
      	<div class="w980 mar0">
	      <c:if test="${topmenu eq 'browse'}">
		       <!--ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'product'? 'class="active"': ''}><a href="<spring:url value="/browse/macmall" />">Product</a></li>
		      		<li ${submenu eq 'sitecontent'? 'class="active"': ''}><a href="<spring:url value="/browse/macmall" />">Site Content</a></li>     		
		       </ul-->
	      </c:if>
	      
	      <c:if test="${topmenu eq 'bigbets'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'elevate'? 'class="active"': ''}><a href="<spring:url value="/elevate/macmall" />">Elevate</a></li>
		      		<li ${submenu eq 'exclude'? 'class="active"': ''}><a href="<spring:url value="/exclude/macmall" />">Exclude</a></li>
		      		<li ${submenu eq 'feature'? 'class="active"': ''}><a href="<spring:url value="/feature/macmall" />">Feature</a></li>
		      		<li ${submenu eq 'redirect'? 'class="active"': ''}><a href="<spring:url value="/redirect/macmall" />">Query Cleaning</a></li>		      		
		      		<li ${submenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/macmall" />">Ranking Rule</a></li>
		      		<li ${submenu eq 'catalog'? 'class="active"': ''}><a href="<spring:url value="/catalog/macmall"/>">Catalog</a></li>  		      		
		      		<li ${submenu eq 'facet'? 'class="active"': ''}><a href="<spring:url value="/facet/macmall"/>">Facet Rule</a></li>  		      		
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'advertise'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'campaign'? 'class="active"': ''}><a href="<spring:url value="/campaign/macmall" />">Campaign</a></li>
		      		<li ${submenu eq 'banner'? 'class="active"': ''}><a href="<spring:url value="/banner/macmall" />">Banner</a></li>	      		
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'lexicon'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'synonym'? 'class="active"': ''}><a href="<spring:url value="/synonym/macmall" />">Synonym</a></li>
		      		<li ${submenu eq 'stopword'? 'class="active"': ''}><a href="<spring:url value="/stopword/macmall" />">Stopword</a></li>	      		
		      		<li ${submenu eq 'protword'? 'class="active"': ''}><a href="<spring:url value="/protword/macmall" />">Protword</a></li>	      		
		       </ul>
	      </c:if>
	      
	      <c:if test="${topmenu eq 'setting'}">
		       <ul class="subMenu floatL txtAL">
		      		<li ${submenu eq 'audit'? 'class="active"': ''}><a href="<spring:url value="/audit/macmall" />">Audit Trail</a></li>
		      		<li ${submenu eq 'setting'? 'class="active"': ''}><a href="<spring:url value="/setting/macmall" />">User Setting</a></li>	      		
		      		<li ${submenu eq 'security'? 'class="active"': ''}><a href="<spring:url value="/security/macmall" />">Security</a></li>	      		
		      		<li ${submenu eq 'sponsor'? 'class="active"': ''}><a href="<spring:url value="/sponsor/macmall" />">Partners</a></li>	      		
		      		<li ${submenu eq 'approval'? 'class="active"': ''}><a href="<spring:url value="/approval/macmall" />">Pending Approval</a></li>	      		
		      		<li ${submenu eq 'production'? 'class="active"': ''}><a href="<spring:url value="/production/macmall" />">Push to Prod</a></li>	      		
		       </ul>
	      </c:if>
	          
      	</div>
      </div>    
   </div>
   <div class="clearB"> &nbsp; </div>
<div class="mar0 w980">