<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="rules"/>
<c:set var="submenu" value="redirect"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<link href="<spring:url value="/css/redirect/redirect.css" />" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<spring:url value="/js/rules/redirect.js" />"></script>   

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
	
	<div id="submitForApproval"></div>
	
	<div class="clearB"></div>	
	<div id="redirectContainer" style="width:95%" class="marT20 mar0">
		<div class="circlePreloader" id="preloader"><img src="../images/ajax-loader-circ.gif"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/queryCleaningRuleGuidelines.jpg"></div>

		<div id="redirect" class="redirect fsize12">	
			<div class="landingCont bgboxGray w45p83 minHeight185 floatL">	
					<div class="fsize14 txtAL borderB padB4 marB8 fbold">
						<div class="floatL">Rule Name</div>
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
    		
    		<div id="page" class="containerRedirect">
    			<div class="landingCont bgboxGray w96p50p floatL marB10">
    				<input type="checkbox" id="activate" class="activate"> Use this action (<span class="fitalic">Note: Checking this box will uncheck the other actions.</span>)
    			</div>
    			<div class="clearB"></div>
    		</div>
			
    		<div id="keyword" class="containerRedirect">
    			<div class="landingCont bgboxGray w96p50p floatL marB10">
    				<input type="checkbox" id="activate" class="activate"> Use this action (<span class="fitalic">Note: Checking this box will uncheck the other actions.</span>)
    			</div>
    			
    			<!-- div class="landingCont bgboxGray w96p50p floatL">
    				<div class="fsize14 txtAL borderB padB4 marB8 fbold">
						Search Header Text
					</div>
    				<div id="searchHeaderText"></div>
    				<div class="clearB"></div>
    			</div -->
    			
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
						<div class="alert">This keyword is using the following rules: <span id="rules"></span></div>
						<div id="activerules"></div>
					</div>				
				</div>
				<div class="clearB"></div>
			</div>
    		
    		<div id="filter" class="containerRedirect">
    			<div class="landingCont bgboxGray w96p50p floatL marB10">
    				<input type="checkbox" id="activate" class="activate floatL marR8"> <p class="flaotL marT2">Use this action (<span class="fitalic">Note: Checking this box will uncheck the other actions.</span>)</p>
    			</div>
    			<div class="landingCont bgboxGray w96p50p floatL marB10">
    				<input type="checkbox" id="includeKeyword" class="includeKeyword floatL marR8"> <p class="flaotL marT2">Include keyword in search (<span class="fitalic">Note: Checking this box will allow Ranking Rule to influence search results based on keyword matches.</span>)</p>
    			</div>
    			<div class="clearB"></div>
	        	<div class="dropdownArea marT10">
	        	<h2 class="borderB padB3">Create Filter Group : 
	        	<select id="filterGroup" name="select" class="selectCombo w178" >
					<option value="ims">IMS Categories</option>
					<c:if test="${store eq 'pcmall' or store eq 'pcmallcap' or store eq 'pcmgbd'}">
					<option value="cnet">Product Site Taxonomy</option>
					</c:if>
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
		        	<div class="clearB"></div>
	        		<div class="bgfff border pad8">
		        		<h3 class="textSummary fLblue2">
							<div class="txtAL floatL w90p">
								<img class="toggleIcon floatL marR8" src="<spring:url value="/images/icon_expand.png" />">
								<div class="floatL wordwrap w95p"><a class="conditionFormattedText" href="javascript:void(0);"></a></div>
							</div>
							<div class="txtAR floatR w10p">
								<img class="cloneIcon" src="<spring:url value="/images/icon_clone.png" />"> 
								<img class="deleteIcon" src="<spring:url value="/images/icon_delete2.png" />">
							</div> 
							<div class="clearB"></div>
						</h3>

	        		
	        			<div class="conditionFields" style="display: none">
	        				<div class="ims">
	        				<h3 class="marT10">IMS Categories / Manufacturers</h3>	        		
	        				<div class="fsize12 marTB20 marRL50">
							<table class="imsFields">							
								<tr class="catName" id="category">
									<td class="w175 padB8" valign="bottom">Category :</td>
									<td class="iepadBT0">
										<div class="floatL fsize11 marB8 txtDecoUL padT3">
											<a class="switchToCatCode" href="javascript:void(0);">Use category codes instead &raquo;</a>
										</div>
										<div class="clearB"></div>
										<img id="preloaderCategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="categoryList" class="categoryList selectCombo w229" title="Select Category"></select>
										</div>
									</td>
								</tr>
								<tr class="catName" id="subcategory">
									<td class="w175">SubCategory :</td>
									<td>
										<img id="preloaderSubCategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="subCategoryList" class="subCategoryList selectCombo w229" title="Select SubCategory"></select>
										</div>
									</td>
								</tr>
								<tr class="catName" id="class">
									<td class="w175">Class :</td>
									<td>
										<img id="preloaderClassList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="classList" class="classList selectCombo w229" title="Select Class"></select>
										</div>
									</td>
								</tr>
								<tr class="catName" id="minor">
									<td class="w175" valign="top">SubClass :</td>
									<td>
										<img id="preloaderMinorList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="minorList" class="minorList selectCombo w229" title="Select SubClass"></select>
										</div>
									</td>
								</tr>
								<tr class="catCode" id="catCode">
									<td  class="w175 padB8" valign="bottom">Category Code :</td>
									<td>
										<div class="floatL fsize11 marB8 txtDecoUL padT3">
											<a class="switchToCatName" href="javascript:void(0);">Use category names instead &raquo;</a>
										</div>
										<div class="clearB"></div>
										<input id="catcode" type="text" maxlength="4">
									</td>
								</tr>
								<tr id="manufacturer">
									<td class="w175" valign="top">Manufacturer :</td>
									<td>
										<img id="preloaderManufacturerList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="manufacturerList" class="manufacturerList selectCombo w229" title="Select Manufacturer"></select>
										</div>	
									</td>
								</tr>
							</table>						
						</div><!--  end item 1 -->
					</div>
					
					<div class="cnet">
	        				<h3 class="marT10">Product Site Taxonomy / Manufacturers</h3>	        		
	        				<div class="fsize12 marTB20 marRL50">
							<table class="cnetFields">							
								<tr class="catName" id="level1Cat">
									<td class="w175 padB8" valign="bottom">Level 1 Category :</td>
									<td class="iepadBT0">
										<img id="preloaderLevel1CategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="level1CategoryList" class="level1CategoryList selectCombo w250" title="Select Category"></select>
										</div>
									</td>
								</tr>
								<tr class="catName" id="level2Cat">
									<td class="w175">Level 2 Category :</td>
									<td>
										<img id="preloaderLevel2CategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="level2CategoryList" class="level2CategoryList selectCombo w250" title="Select SubCategory"></select>
										</div>
									</td>
								</tr>
								<tr class="catName" id="level3Cat">
									<td class="w175">Level 3 Category :</td>
									<td>
										<img id="preloaderLevel3CategoryList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="level3CategoryList" class="level3CategoryList selectCombo w250" title="Select Class"></select>
										</div>
									</td>
								</tr>
								<tr>
									<td class="w175" valign="top">Manufacturer :</td>
									<td>
										<img id="preloaderCNETManufacturerList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="cnetmanufacturerList" class="cnetmanufacturerList selectCombo w250" title="Select Manufacturer"></select>
										</div>	
									</td>
								</tr>
							</table>						
						</div><!--  end item 2 -->
					</div>
					
					<div class="dynamicAttribute">
	        				<h3 class="marT10">Dynamic Attribute</h3>	        		
	        				<div class="fsize12 marTB20 marRL50">
							<table class="dynamicAttributeFields">							
								<tr>
									<td class="w175 padB8" valign="bottom">Template Name :</td>
									<td class="iepadBT0 w278">
										<img id="preloaderTemplateNameList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="templateNameList" class="templateNameList selectCombo w250" title="Select Template Name"></select>
										</div>
									</td>
								</tr>
								<!--  sample added line -->
								<tr id="dynamicAttributeValue">
									<td colspan="2">
										<div id="dynamicAttributeItemList">							
											<div id="dynamicAttributeItemPattern" class="dynamicAttributeItem" style="display:none">
												<div class="clearB"></div>
												<div class="w150 floatL padL25 marT8"><span id="dynamicAttributeLabel"></span></div>
												<img src="../images/iconDelete.png" class="deleteAttrIcon posRel floatR marT8 marR8" alt="Delete Attribute" title="Delete Attribute">
												<div class="w235 floatL marT8 border pad10" style="overflow-y:auto; max-height: 107px">													
													<div id="dynamicAttributeValues">
														<div id="dynamicAttributeValuesPattern" style="display: none;">
															<div>
																<input type="checkbox" class="checkboxFilter">
																<span id="attributeValueName"></span>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</td>
								</tr>
								<!--  end sample addded line -->
								<tr id="dynamicAttributeName">
									<td class="w175"><p class="padL25">Add Dynamic Attribute :</p></td>
									<td>
										<img id="preloaderDynamicAttributeList" class="floatR loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
										<div class="floatL">
											<select name="select" id="dynamicAttributeList" class="dynamicAttributeList selectCombo w210" title="Add Dynamic Attribute"></select>
										</div>
										<a href="javascript:void(0);" class="addDynamicAttrBtn btnGraph btnAddGrayMid floatR marT3 leftn22 posRel" id="addButton"></a>
									</td>
								</tr>
								
								
							</table>						
							</div><!--  end item 3 -->
						</div>
					
					<div class="facet">
						<h3 class="marT10">Facets</h3>	 
						<div class="fsize12 marT10 marB20 marRL50">
							<table>
								<tr>
									<td class="w175">Name (contains) :</td>
									<td class="iepadBT0">
										<input id="nameContains" type="text" style="width: 244px;"/>
									</td>
								</tr>
								<tr>
									<td class="w175">Description (contains):</td>
									<td class="iepadBT0">
										<input id="descriptionContains" type="text" style="width: 244px;"/>
									</td>
								</tr>							
								<tr>
									<td class="w175">Platform :</td>
									<td class="iepadBT0">
										<select name="select" id="platformList" class="selectCombo w250" title="Select Platform" >
											<option value="all">-Select Platform-</option>
											<option value="universal">Universal</option>
											<option value="pc">PC</option>
											<option value="linux">Linux</option>
											<option value="mac">Macintosh</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w175">Condition :</td>
									<td class="iepadBT0">
										<select name="select" id="conditionList" class="selectCombo w250" title="Select Condition" >
											<option value="all">-Select Condition-</option>
											<option value="refurbished">Refurbished</option>
											<option value="open">Open Box</option>
											<option value="clearance">Clearance</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w175">Availability :</td>
									<td class="iepadBT0">
										<select name="select" id="availabilityList" class="selectCombo w250" title="Select Availability" >
											<option value="all">-Select Availability-</option>
											<option value="instock">In Stock</option>
											<option value="call">Call</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w175">License :</td>
									<td class="iepadBT0">
										<select name="select" id="licenseList" class="selectCombo w250" title="Select License" >
											<option value="all">-Select License-</option>
											<option value="license">License Products Only</option>
											<option value="nonlicense">Non-License Products Only</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="w175">Product Image :</td>
									<td class="iepadBT0">
										<select name="select" id="imageExistsList" class="selectCombo w250" title="Select Product Image" >
											<option value="all">-Select Image Option-</option>
											<option value="withImage">Products With Image Only</option>
											<option value="noImage">Products Without Image Only</option>
										</select>
									</td>
								</tr>
							</table>						
						</div><!--  end item 4 -->
						</div>
						
						
						
						<div class="clearB"></div>	
						<div class="borderT padT8">
							<img id="preloaderUpdating" class="floatL loadIcon marT6 marL5" src="../images/ajax-loader-rect.gif" style="display: none"/>
							<div class="floatR">
								<a href="javascript:void(0);" class="saveBtn buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
								<a href="javascript:void(0);" class="cloneBtn buttons btnGray clearfix"><div class="buttons fontBold">Clone</div></a> 
								<a href="javascript:void(0);" class="deleteBtn buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
							</div>
							<div class="clearB"></div>	
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
