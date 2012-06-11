<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="redirect"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<link href="<spring:url value="/css/redirect/redirect.css" />" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<spring:url value="/js/bigbets/bigbets.js" />"></script>   
<script type="text/javascript" src="<spring:url value="/js/bigbets/redirect.js" />"></script>   

<!--Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="companyLogo">
		<a href="javascript:void()"><img src="<spring:url value="${storeLogo}" />"></a>
	</div>
	<div class="clearB floatL w240">
		<div id="rulePanel"></div>
	    <div class="clearB"></div>
	</div>
	    <div class="clearB floatL w240">
    	<div id="ruleKeywordPanel"></div>
        <div class="clearB"></div>
    </div>
</div>
<!--Left Menu-->

<!--Start Right Side-->
<div class="contentArea floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
      <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>	
	</div>
   
   	<div class="clearB"></div>
	
	<div id="submitForApproval" class="clearB floatR farial fsize12 fDGray txtAR w730 GraytopLine" style="display:none"> 
	        <div class="txtAL w730 minHeight36" style="background: #e8e8e8">       	
	        	<div class="floatL padT10 padL10" style="width:70%" >
	        		<div id="commentHolder">
			        	<label class="floatL wAuto padL5 fsize11 fLgray">
			        		<span id="commentIcon"><img src="../images/icon_comment.png"></span>  
			        	</label>
		        	</div>
	        		<div id="statusHolder">
			        	<label class="floatL wAuto marRL5 fLgray2">|</label>
			        	<label class="floatL wAuto">Status:</label>
			        	<label class="floatL wAuto padL5 fsize11 fLgray">
			        		<span id="status"></span> 
			        		<span id="statusMode" class="fsize11 forange padL5"></span> 
			        	</label>
		        	</div>
		        	<div id="publishHolder">
		        		<label class="floatL wAuto marRL5 fLgray2">|</label>
			        	<label class="floatL wAuto">Last Published:</label>
			        	<label class="padL5 fLgray fsize11">
			        		<span id="statusDate"></span> 
			        	</label>
		        	</div>
			  	</div>   			  	
	        	<div class="floatR marL8 marR3 padT5"> 	    
	        		<sec:authorize access="hasRole('CREATE_RULE')">
        	    		<a id="submitForApprovalBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Submit for Approval</div></a>
        	    	</sec:authorize>
	        	</div>
	        </div>	
	        <div class="clearB"></div>	
	 </div>
	 
	<div id="viewAuditTemplate" style="display: none">
	   <div class="elevateItemPW">
		   <div class="w265 padB8">
	            <div id="auditTemplate" style="display: none;" >
			   		<div class="pad8 borderB"> 
				   		<div class="padR8 floatL wordwrap" style="width:60px">%%timestamp%%</div>
			            <div class="floatL w175">
			            	<img src="<spring:url value="/images/user13x13.png" />" class="marBn3 marR3">
			            	<span class="fDblue">%%commentor%%</span>
			                <span>%%comment%%</span>
			            </div>
		            <div class="clearB"></div>
		            </div>         
			    </div>
			    <div id="auditPagingTop"></div>
			    	<div class="clearB"></div>	
	            <div id="auditHolder"></div>
	            	<div class="clearB"></div>	
	            <div id="auditPagingBottom" style="margin-top:8px"></div>
		   </div>
	   </div>
	</div>
				
	<div class="clearB"></div>	
	<div id="redirectContainer" style="width:95%" class="marT20 mar0">
		<div class="circlePreloader" id="preloader"><img src="../images/ajax-loader-circ.gif"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/queryCleaningRuleGuidelines.jpg"></div>

		<div id="redirect" class="redirect fsize12">	
			
			<div class="landingCont bgboxGray w96p50p floatL">
				<div class="fsize14 txtAL borderB padB4 marB8 fbold">
					Replacement Keyword
				</div>
				<input type="text" class="floatL w200"> 
				<div class="floatL marL5">
					<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
				</div>
				<div>
					<div class="clearB marT10 padT8 borderT"></div>						
					<div class="alert">Warning: This keyword has 3 active elevate rules</div>
				</div>				
			</div>
		
			<div class="landingCont bgboxGray w45p83 minHeight185 floatL">	
					<div class="fsize14 txtAL borderB padB4 marB8 fbold">
						<div class="floatL">Rule Info</div>
						<div class="floatR">
							<span class="floatR"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1 marL3" id="downloadIcon" alt="Download" title="Download"></div></a></span>
							<span class="floatR"><img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History"></span>
						</div>
						<div class="clearB"></div>
					</div>		
					<label class="floatL w70 marT5 padT3">Name</label>
					<label><input id="name" type="text" class="w240 marT5"/></label>
					<div class="clearB"></div>			
					<label class="floatL w70 marT8 padT3">Description</label>
					<label><textarea id="description" rows="3" class="marT8" style="width:240px"></textarea></label>
					<div class="borderT txtAR padT10">
							<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
							<a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
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
						<td valign="top">
							<div class="fsize12 mar10 dropdownArea">
								<label class="floatL w100">CatCode</label><label class="floatL w200"><input id="catcodetext" type="text" value="" class="farial fsize12  fgray padLR3 padTB4" size="10" maxlength="4"></label>
								<div class="clearB"></div>
								<label class="floatL w100">Category</label><label class="floatL w200"><select name="select" id="categoryList" class="selectCombo w178" title="Select Category">
								</select><img class="loadIcon" src="../images/ajax-loader-rect.gif"/></label>
								<div class="clearB"></div>
								<label class="floatL w100">Sub-Category</label>
								<label class="floatL w200"><select name="select" id="subCategoryList" class="selectCombo w178" title="Select SubCategory">
									   		<option></option>
								</select><img class="loadIcon" src="../images/ajax-loader-rect.gif"/></label>
								<div class="clearB"></div>
								<label class="floatL w100">Class</label>
								<label class="floatL w200"><select name="select" id="classList" class="selectCombo w178" title="Select Class">
											<option></option>
								</select><img class="loadIcon" src="../images/ajax-loader-rect.gif"/></label>
								<div class="clearB"></div>
								<label class="floatL w100">Minor</label>
								<label class="floatL w200"><select name="select" id="minorList" class="selectCombo w178" title="Select Minor">
									 		<option></option>
								</select><img class="loadIcon" src="../images/ajax-loader-rect.gif"/></label>
								<div class="clearB"></div>
								<label class="floatL w100">Manufacturer</label>
								<label class="floatL w200"><select name="select" id="manufacturerList" class="w178" title="Select Manufacturer">
										<option></option>
								</select><img class="loadIcon" src="../images/ajax-loader-rect.gif"/></label>
								<div class="clearB"></div>							
							</div>
							
							
						</td>
						<td class="w60"><a href="#" class="buttons btnGray marL15 clearfix">
							<div class="buttons fontBold" id="addRuleCondition">+</div></a><br/>
						</td>
						<td class="landingCont bgboxGray" style="vertical-align: top; width: 240px">
							<div id="ruleConditionPanel"></div>
						</td>
					</tr>
				</table>
			</div>
    	</div>

		</div>
	</div> 
	
	<div id="sortRulePriorityTemplate"  class="pad0" style="display:none;">
		<div class="listRule">
			<ul id="ruleListing" class="listItems">
				<li id="rulePattern" class="ruleItem" style="display: none">
					<div class="handle">
						<span class="ruleName"></span>
					</div>
				</li>	
			</ul>	
		</div>		
	</div>
	
   <div id="ruleIsLocked" class="w180" style="display:none;">
  	<div class="w180 alert">You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked.</div>
  </div>
	
</div>
<!--End Right Side-->
   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	