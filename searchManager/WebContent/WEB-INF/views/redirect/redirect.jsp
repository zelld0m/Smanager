<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="redirect"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- tabber -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/tabber.css" />">

<!-- page specific dependencies -->
<link href="<spring:url value="/css/redirect/redirect.css" />" rel="stylesheet" type="text/css">

<!-- DWR dependencies -->
<script type="text/javascript" src="<spring:url value="/dwr/interface/CategoryServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/RedirectServiceJS.js"/>"></script>

<script type="text/javascript" src="<spring:url value="/js/bigbets/redirect.js" />"></script>   

<script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery.ui.combobox.js" />" ></script>

<!--Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="companyLogo">
		<a href="javascript:void()"><img src="<spring:url value="/images/logoMacMall.png" />"></a>
	</div>
	
	<div class="clearB floatL w240">
		<div id="redirectSidePanel"></div>
	    <div class="clearB"></div>
	</div>
</div>
<!--Left Menu-->


<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal">Query Cleaning: <span id="headerRuleName" class="fLblue fnormal">Rule Name</span></div>
   	</div>
   
   	<div class="clearB"></div>
	
	<div id="submitForApproval" class="clearB floatR farial fsize12 fDGray txtAR w730 GraytopLine" style="display:none"> 
	        <div id="" class="txtAL w730 minHeight36" style="background: #e8e8e8">       	
	        	<div class="floatL padT10 padL10" style="width:60%" >
	        		<div id="statusHolder">
			        	<label class="floatL wAuto">Status:</label>
			        	<label class="floatL wAuto padL5 fsize11 fLgray">
			        		<span id="status"></span> 
			        		<span id="statusMode" class="fsize11 forange padL5"></span> 
			        	</label>
			        	<label class="floatL wAuto marRL5 fLgray2">|</label>
		        	</div>
		        	<div id="publishHolder">
			        	<label class="floatL wAuto">Last Published:</label>
			        	<label class="padL5 fLgray fsize11">
			        		<span id="statusDate"></span> 
			        	</label>
		        	</div>
			  	</div>   			  	
	        	<div class="floatR marL8 marR3 padT5"> 	        		
	        		<a id="submitForApprovalBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Submit for Approval</div></a>
	        	</div>
	        </div>	
	        <div class="clearB"></div>	
	 </div>
	
	<div id="redirectContainer" style="width:95%" class="marT20 mar0">
		<div id="redirect" class="redirect fsize12">	
			<div class="landingCont w45p83 minHeight185 floatL">	
				<div class="fsize14 txtAL borderB padB4 marB8 fbold">Query Cleaning</div>		
					<label class="floatL w70 marT5 padT3">Name</label>
					<label><input id="name" type="text" class="w240 marT5"/></label>
					<div class="clearB"></div>			
					<label class="floatL w70 marT8 padT3">Description</label>
					<label><textarea id="description" rows="4" class="marT8" style="width:240px"></textarea></label>
			</div>
			
			<div class="landingCont w45p83 minHeight185 floatL marL13">
					<div id="keywordInRulePanel"></div>
			</div>
		</div>
	</div> 
	
</div>
<!--End Right Side-->
   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	