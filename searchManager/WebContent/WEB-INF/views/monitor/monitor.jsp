<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="monitor"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/dwr/interface/CacheServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/monitor/monitor.js" />"></script>

<!-- Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="clearB floatL w240">
		<div>&nbsp;</div>
	    <div class="clearB"></div>
	</div>
</div>
<!--Left Menu-->

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">		
	  <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
	</div>
	
	<div class="clearB"></div>
	
	<div id="monitool" class="tabs">
		<ul>
			<li><a href="#cache"><span>Cache</span></a></li>
			<li><a href="#log"><span>Log</span></a></li>
		</ul>
		
		<div id="cache">
			<div>

				<div class="floatR marL8 marR3 marT10"> 	        		

				<input id="cacheKey" type="text">
				<div class="floatR marL8 marR3 padT5"> 	        		
	        		<a id="checkCacheBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Check Cache</div></a>
	        	</div>
				<div class="floatR marL8 marT10"><input id="cacheKey" type="text"></div>				
			</div>
			
			<div id="contentArea" style="display:none">
				<div class="w95p marRLauto">
					<table class="tblAlpha w100p marT8" >
						<tr>
							<th width="480px" class="txtAL">Field</th>
							<th width="135px">Value</th>
						</tr>
					</table>
				</div>
				<div class="w95p marRLauto padT0 marT0" style="max-height:365px; overflow-y:auto">
					<table id="contentTable" class="tblAlpha padT0 marT0" width="100%">
						<tr id="rowPattern" class="rowItem" style="display: none">
							<td id="field"></td>
							<td id="value"></td>
						</tr>
					</table>
				</div>
			</div>
			
			<div class="clearB"></div>
		</div>
		
		<div id="log">
		
		</div>
		
		
	</div>
	
	
</div>
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>	
