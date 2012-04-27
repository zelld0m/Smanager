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
<div class="contentArea floatL w730 marL10 marT27">
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
	 
	<div class="clearB"></div>	
	<div id="redirectContainer" style="width:95%" class="marT20 mar0">
		<div class="circlePreloader" id="preloader"><img src="../images/ajax-loader-circ.gif"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/ElevatePageisBlank.jpg"></div>
		<div id="redirect" class="redirect fsize12">	
			<div class="landingCont bgboxGray w45p83 minHeight185 floatL">	
				<div class="fsize14 txtAL borderB padB4 marB8 fbold">Rule Info</div>		
					<label class="floatL w70 marT5 padT3">Name</label>
					<label><input id="name" type="text" class="w240 marT5"/></label>
					<div class="clearB"></div>			
					<label class="floatL w70 marT8 padT3">Description</label>
					<label><textarea id="description" rows="3" class="marT8" style="width:240px"></textarea></label>
					<div class="borderT txtAR padT10">
						<a id="saveButton" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
						<a id="deleteButton" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
					</div>
				</div>
			
			<div class="landingCont bgboxGray w45p83 minHeight185 floatL marL13">
					<div id="keywordInRulePanel"></div>
			</div>
		
		
		<div class="clearB"></div>	
		
		<div id="redirect-source" class="tabs">
    		<ul>
    			<li><a href="#ims"><span>IMS</span></a></li>
    		</ul>
    		
    		<div id="ims">
	        	<h3 class="marT20">Category / Manufacturer</h3>
				<table class="fsize12 mar10 tblCatalog">
					<tr>
						<td>
							<table class="fsize12 mar10 tblCatalog">
								<tr>
									<td width="100px">CatCode</td>
									<td width="230px"><input id="catcodetext" type="text" value="" class="farial fsize12  fgray padLR3 padTB4" size="10" maxlength="4"></td>
								</tr>
								<tr>
									<td>Category</td>
									<td>
										<select name="select" id="categoryList">
										
											<option></option>
										</select>
									</td>
								</tr>
								<tr>
									<td>Sub-Category</td>
									<td>
										<select name="select" id="subCategoryList">
												<option></option>
										</select>
									</td>
								</tr>
								<tr>
									<td>Class</td>
									<td>
										<select name="select" id="classList">
												<option></option>
										</select>
									</td>
								</tr>
								<tr>
									<td>Minor</td>
									<td>
										<select name="select" id="minorList">
											<option></option>
										</select>
									</td>
								</tr>
								<tr>
									<td>Manufacturer </td>
									<td>
									<select name="select" id="manufacturerList" class="w200">
											<option></option>
									</select></td>
								</tr>
							</table>
						</td>
						<td style="width:60px"><a href="#" class="buttons btnGray clearfix"><div
									class="buttons fontBold" id="addRule">>></div></a><br />
						<td class="landingCont bgboxGray" style="vertical-align: top; width: 240px">
							<div id="ruleItemPanel"></div>
						</td>
					</tr>
				</table>
			</div>
    	</div>
    	
		
		</div>
	</div> 
	
	
	
	
	
</div>
<!--End Right Side-->
   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	