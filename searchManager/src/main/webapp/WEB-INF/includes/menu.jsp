  <!--PC Mall Menu-->
   <div class="clearB floatR bgTopHeaderMnu">
    <div class="mar0" style="padding-left:280px; width: 700px" >
      <ul class="mnuHeader farial fsize12 fbold" >
              <li ${topmenu eq 'home'? 'class="active"': ''}><span><a href="<spring:url value="/" />">Home</a></span></li>
              <li ${topmenu eq 'browse'? 'class="active"': ''}><span><a href="<spring:url value="/browse/"/>${storeId}">Simulator</a></span></li>
              <!--
              <li ${topmenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/"/>${storeId}">Relevancy</a></li>
              <li ${topmenu eq 'synonyms'? 'class="active"': ''}><a href="<spring:url value="/synonyms/"/>${storeId}">Synonyms</a></li>
               -->
              <li ${topmenu eq 'rules'? 'class="active"': ''}><span><a href="<spring:url value="/elevate/"/>${storeId}">Search Rules</a></span></li>
              <li ${topmenu eq 'ads'? 'class="active"': ''}><span><a href="<spring:url value="/banner/"/>${storeId}">Search Ads</a></span></li>
              <sec:authorize access="hasRole('APPROVE_RULE')">
                  <li ${topmenu eq 'workflow'? 'class="active"': ''}><span><a href="<spring:url value="/approval/"/>${storeId}">Workflow</a></span></li>
              </sec:authorize>
              <sec:authorize access="hasRole('PUBLISH_RULE') and !hasRole('APPROVE_RULE')">
                  <li ${topmenu eq 'workflow'? 'class="active"': ''}><span><a href="<spring:url value="/production/"/>${storeId}">Workflow</a></span></li>
              </sec:authorize>
              <sec:authorize access="hasRole('CREATE_RULE') and !hasAnyRole('APPROVE_RULE', 'PUBLISH_RULE')">
                  <c:if test="${storeId eq 'pcmallcap' or storeId eq 'pcmallgov' or storeId eq 'macmallbd'}">
                      <li ${topmenu eq 'workflow'? 'class="active"': ''}><span><a href="<spring:url value="/import/"/>${storeId}">Workflow</a></span></li>
                  </c:if>
              </sec:authorize>
              <li ${topmenu eq 'statistic'? 'class="active"': ''}><span><a href="<spring:url value="/topkeyword/"/>${storeId}">Statistics</a></span></li>
              <li ${topmenu eq 'lexicon'? 'class="active"': ''}><span><a href="<spring:url value="/synonym/"/>${storeId}">Linguistics</a></span></li>
              <li class="icon-only ${topmenu eq 'setting'? 'active': ''}"><span><a href="<spring:url value="/audit/"/>${storeId}">
                  <img src="<spring:url value="/images/settings-icon.png"/>" title="Settings">
              </a></span></li>
              <!-- li ${topmenu eq 'migrator'? 'class="active"': ''}><a href="<spring:url value="/migrator/"/>${storeId}">Migrator</a></li -->
      </ul>
      </div>
      <div class="clearB"></div>

      <div class="subMenuHolder">
          <div class="w980 mar0">
           <c:if test="${topmenu eq 'dashboard'}">
               <!-- ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'topkeyword'? 'class="active"': ''}><a href="<spring:url value="/"/>">Top Keywords</a></li>
                      <li ${submenu eq 'zeroresult'? 'class="active"': ''}><a href="<spring:url value="/zeroresult/"/>${storeId}">Zero Results</a></li>
               </ul -->
          </c:if>

          <c:if test="${topmenu eq 'browse'}">
               <!--ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'product'? 'class="active"': ''}><a href="<spring:url value="/browse/"/>${storeId}">Product</a></li>
                      <li ${submenu eq 'sitecontent'? 'class="active"': ''}><a href="<spring:url value="/browse/"/>${storeId}">Site Content</a></li>
               </ul-->
          </c:if>

          <c:if test="${topmenu eq 'rules'}">
               <ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'elevate'? 'class="active"': ''}><a href="<spring:url value="/elevate/"/>${storeId}">Elevate</a></li>
                      <li ${submenu eq 'exclude'? 'class="active"': ''}><a href="<spring:url value="/exclude/"/>${storeId}">Exclude</a></li>
                      <li ${submenu eq 'demote'? 'class="active"': ''}><a href="<spring:url value="/demote/"/>${storeId}">Demote</a></li>
                      <li ${submenu eq 'facet'? 'class="active"': ''}><a href="<spring:url value="/facet/"/>${storeId}">Facet Sort</a></li>
<%--                       <li ${submenu eq 'feature'? 'class="active"': ''}><a href="<spring:url value="/feature/"/>${storeId}">Feature</a></li> --%>
                      <li ${submenu eq 'redirect'? 'class="active"': ''}><a href="<spring:url value="/redirect/"/>${storeId}">Query Cleaning</a></li>
                      <li ${submenu eq 'relevancy'? 'class="active"': ''}><a href="<spring:url value="/relevancy/"/>${storeId}">Ranking Rule</a></li>
<%--                       <li ${submenu eq 'catalog'? 'class="active"': ''}><a href="<spring:url value="/catalog/"/>${storeId}">Catalog</a></li>                         --%>
               </ul>
          </c:if>

          <c:if test="${topmenu eq 'ads'}">
               <ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'banner'? 'class="active"': ''}><a href="<spring:url value="/banner/"/>${storeId}">Banner</a></li>
               </ul>
          </c:if>

          <c:if test="${topmenu eq 'workflow'}">
              <ul class="subMenu floatL txtAL">
                    <sec:authorize access="hasRole('APPROVE_RULE')">
                        <li ${submenu eq 'approval'? 'class="active"': ''}><a href="<spring:url value="/approval/"/>${storeId}">Pending Approval</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasRole('PUBLISH_RULE')">
                        <li ${submenu eq 'production'? 'class="active"': ''}><a href="<spring:url value="/production/"/>${storeId}">Push to Prod</a></li>
                    </sec:authorize>
                    <c:if test="${storeId eq 'pcmallcap' or storeId eq 'pcmallgov' or storeId eq 'macmallbd'}">
                    <sec:authorize access="hasRole('CREATE_RULE')">
                        <li ${submenu eq 'import'? 'class="active"': ''}><a href="<spring:url value="/import/"/>${storeId}">Import Rule</a></li>
                    </sec:authorize>
                    </c:if>
                    <c:if test="${storeId eq 'pcmall' or storeId eq 'macmall'}">
                        <sec:authorize access="hasRole('PUBLISH_RULE')">
                            <li ${submenu eq 'export'? 'class="active"': ''}><a href="<spring:url value="/export/"/>${storeId}">Export Rule</a></li>
                        </sec:authorize>
                    </c:if>
              </ul>
          </c:if>

          <c:if test="${topmenu eq 'statistic'}">
               <ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'topkeyword'? 'class="active"': ''}><a href="<spring:url value="/topkeyword/"/>${storeId}">Top Keyword</a></li>
                      <li ${submenu eq 'zeroresult'? 'class="active"': ''}><a href="<spring:url value="/zeroresult/"/>${storeId}">Zero Result</a></li>
                      <li ${submenu eq 'keywordtrends'? 'class="active"': ''}><a href="<spring:url value="/keywordtrends/"/>${storeId}">Keyword Trends</a></li>
                       <%--<li ${submenu eq 'reportgenerator'? 'class="active"': ''}><a href="<spring:url value="/reportgenerator/"/>${storeId}">Report Generator</a></li> --%>
               </ul>
          </c:if>

          <c:if test="${topmenu eq 'lexicon'}">
               <ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'synonym'? 'class="active"': ''}><a href="<spring:url value="/synonym/"/>${storeId}">Synonym</a></li>
                      <li ${submenu eq 'stopword'? 'class="active"': ''}><a href="<spring:url value="/stopword/"/>${storeId}">Stopword</a></li>
                      <li ${submenu eq 'protword'? 'class="active"': ''}><a href="<spring:url value="/protword/"/>${storeId}">Protword</a></li>
                      <li ${submenu eq 'spell'? 'class="active"': ''}><a href="<spring:url value="/spell/"/>${store}">Did You Mean</a></li>
               </ul>
          </c:if>

          <c:if test="${topmenu eq 'setting'}">
               <ul class="subMenu floatL txtAL">
                      <li ${submenu eq 'audit'? 'class="active"': ''}><a href="<spring:url value="/audit/"/>${storeId}">Audit Trail</a></li>
                      <li ${submenu eq 'setting'? 'class="active"': ''}><a href="<spring:url value="/setting/"/>${storeId}">User Setting</a></li>
                      <sec:authorize access="hasRole('MANAGE_USER')">
                          <li ${submenu eq 'security'? 'class="active"': ''}><a href="<spring:url value="/security/"/>${storeId}">Security</a></li>
                      </sec:authorize>
                      <li ${submenu eq 'monitor'? 'class="active"': ''}><a href="<spring:url value="/monitor/" />">Monitor</a></li>
               </ul>
          </c:if>
          </div>
      </div>
   </div>
   <div class="clearB"> &nbsp; </div>
<div class="mar0 w980">