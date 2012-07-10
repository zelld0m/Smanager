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
		
		<div id="redirect-type" class="tabs">
    		<ul>
    			<li><a href="#filter"><span>Filter</span></a></li>
    			<li><a href="#keyword"><span>Replace KW</span></a></li>
    			<li><a href="#page"><span>Direct Hit</span></a></li>
    		</ul>
    		
    		<div id="page">
    			<div class="landingCont bgboxGray w96p50p floatL marT20">
    				<input type="checkbox" id="activate" class="activate"> Use this action (<span class="fitalic">Note: Checking this box will uncheck the other actions.</span>)
    			</div>
    			<div class="clearB"></div>
    		</div>
			
    		<div id="keyword">
    			<div class="landingCont bgboxGray w96p50p floatL marT20">
    				<input type="checkbox" id="activate" class="activate"> Use this action (<span class="fitalic">Note: Checking this box will uncheck the other actions.</span>)
    			</div>
    			<div class="landingCont bgboxGray w96p50p floatL">
					<div class="fsize14 txtAL borderB padB4 marB8 fbold">
						Replace Keyword
					</div>
					<input type="text" class="floatL w200" id="changeKeyword"> 
					<div class="floatL marL5 posRel topn1">
						<div class="floatL"><a id="changeKeywordBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a></div> 
						<img id="preloader" alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>" style="display: none;" class="floatL top5 posRel marL3">	
					</div>
					<div id="activerules" style="display: none">
						<div class="clearB marT10 padT8 borderT"></div>						
						<div class="alert">This keyword has <span id="rules"></span></div>
						<div id="activerules"></div>
					</div>				
				</div>
				<div class="clearB"></div>
			</div>
    		
    		<div id="filter">
    			<div class="landingCont bgboxGray w96p50p floatL marT20">
    				<input type="checkbox" id="activate" class="activate"> Use this action (<span class="fitalic">Note: Checking this box will uncheck the other actions.</span>)
    			</div>
    			
	        	<div class="dropdownArea">
	        	<h2 class="borderB padB3">Create Filter Group : 
	        	<select id="filterGroup" name="select" class="selectCombo w178" >
					<option value="ims">IMS Categories</option>
					<option value="facet">Facets</option>
				</select>
				<a id="addFilterGroupBtn" href="javascript:void(0);" class="btnGraph btnAddGrayMid clearfix"><div class="btnGraph marB8"></div></a>
	        	</h2>
	        	<div class="clearB"></div>
	        	<!--  general scrollbar -->
	        	<div id="conditionList" class="marT20" style="overflow-y:auto; max-height:1000px">
	        	<div  id="preloader" class="bgf6f6f6 pad10 txtAC">
	        		<img alt="Retrieving" src="<spring:url value="/images/ajax-loader-rect.gif"/>">
	        	</div>
	        	<div  id="emptyConditionItem" class="bgf6f6f6 pad10 txtAC" style="display:none">
	        		No rule conditions specified
	        	</div>
	        	<!--  group 1 -->
	        	<div id="conditionItemPattern" class="conditionItem bgf6f6f6 pad10" style="display:none">
		        	<div class="topHeader">
			        	<div class="txtAL floatL w50p">
			        		<img class="toggleIcon" src="<spring:url value="/images/icon_expand.png" />">
			        	</div>
			        	<div class="txtAR floatR w50p">
			        		<img class="cloneIcon" src="<spring:url value="/images/icon_clone.png" />"> 
			        		<img class="deleteIcon" src="<spring:url value="/images/icon_delete2.png" />">
			        	</div>
		        	</div>
		        	
		        	<div class="clearB"></div>
	        		<div class="bgfff border pad8">
		        		<h3 class="textSummary fLblue2">
		        			<a class="conditionFormattedText" href="javascript:void(0);"></a>
		        		</h3>
	        		
	        			<div class="conditionFields" style="display: none">
	        				<div class="ims">
	        				<h3 class="marT10">IMS Categories / Manufacturers</h3>	        		
	        				<div class="fsize12 marTB20 marRL50">
							<table class="imsFields">							
								<tr class="catName">
									<td class="w140">Category :</td>
									<td class="iepadBT0">
										<img id="preloaderCategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="categoryList" class="selectCombo w235" title="Select Category"></select>
										</div>
									</td>
								</tr>
								<tr class="catName">
									<td>SubCategory :</td>
									<td>
										<img id="preloaderSubCategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="subCategoryList" class="selectCombo w235" title="Select SubCategory"></select>
										</div>
									</td>
								</tr>
								<tr class="catName">
									<td>Class :</td>
									<td>
										<img id="preloaderClassList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="classList" class="selectCombo w235" title="Select Class"></select>
										</div>
									</td>
								</tr>
								<tr class="catName">
									<td valign="top">SubClass :</td>
									<td>
										<img id="preloaderMinorList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="minorList" class="selectCombo w235" title="Select SubClass"></select>
										</div>
										<div class="clearB"></div>
										<div class="floatL fsize11 marB8 txtDecoUL padT3">
											<a class="switchToCatCode" href="javascript:void(0);">Use category codes instead &raquo;</a>
										</div>
									</td>
								</tr>
								<tr class="catCode">
									<td  class="w140" valign="top">Category Code :</td>
									<td>
										<input id="catcode" type="text">
										<div class="clearB"></div>
										<div class="floatL fsize11 marB8 txtDecoUL padT3">
											<a class="switchToCatName" href="javascript:void(0);">Use category names instead &raquo;</a>
										</div>
									</td>
								</tr>
								<tr>
									<td valign="top">Manufacturer :</td>
									<td>
										<img id="preloaderManufacturerList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="manufacturerList" class="selectCombo w235" title="Select Manufacturer"></select>
										</div>	
									</td>
								</tr>
							</table>						
						</div><!--  end item 1 -->
					</div>
					
					<div class="facet">
						<h3 class="marT10">Facets</h3>	 
						<div class="fsize12 marT10 marB20 marRL50">
							<table>							
								<tr>
									<td class="w140">Platform :</td>
									<td class="iepadBT0">
										<select name="select" id="platformList" class="selectCombo w235" title="Select Platform" >
											<option value="all"></option>
											<option value="universal">Universal</option>
											<option value="pc">PC</option>
											<option value="linux">Linux</option>
											<option value="mac">Macintosh</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w140">Condition :</td>
									<td class="iepadBT0">
										<select name="select" id="conditionList" class="selectCombo w235" title="Select Condition" >
											<option value="all"></option>
											<option value="refurbished">Refurbished</option>
											<option value="open">Open Box</option>
											<option value="clearance">Clearance</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w140">Availability :</td>
									<td class="iepadBT0">
										<select name="select" id="availabilityList" class="selectCombo w235" title="Select Availability" >
											<option value="all"></option>
											<option value="instock">In Stock</option>
											<option value="call">Call</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w140">License :</td>
									<td class="iepadBT0">
										<select name="select" id="licenseList" class="selectCombo w235" title="Select License" >
											<option value="all"></option>
											<option value="license">Show License Products Only</option>
											<option value="nonlicense">Show Non-License Products Only</option>
										</select>
									</td>
								</tr>
												
							</table>						
						</div><!--  end item 2 -->
						</div>
						<div class="txtAR borderT padT8">
							<a href="javascript:void(0);" class="saveBtn buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
							<a href="javascript:void(0);" class="deleteBtn buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
						</div>
					</div><!--  end white container -->
	        	</div>
	        	<div class="clearB"></div>
	        </div><!--  end group 1 -->
		</div><!--  end general scrollbar -->	
	</div>
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
