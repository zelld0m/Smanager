<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="relevancy"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- Start Slider -->
<link  type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.css" />">
<link  type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.blue.css" />">
<link  type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.plastic.css" />">
<link  type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.round.css" />">
<link  type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.round.plastic.css" />">

<!-- jQuery functions --> 
<link type="text/css" href="<spring:url value="/css/relevancy/relevancy.css" />" rel="stylesheet">

<!--[if IE 6]>
<link type="text/css" media="screen" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.ie6.css"/>" >
<link type="text/css" media="screen" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.blue.ie6.css"/>">
<link type="text/css" media="screen" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.plastic.ie6.css"/>">
<link type="text/css" media="screen" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.round.ie6.css"/>">
<link type="text/css" media="screen" rel="stylesheet" href="<spring:url value="/js/jquery/jSlider/css/jslider.round.plastic.ie6.css"/>">
<![endif]-->

<script type="text/javascript" src="<spring:url value="/js/jquery/jSlider/jquery.dependClass.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/jSlider/jquery.slider.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/bigbets/bigbets.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/bigbets/relevancy.js" />"></script> 

<style type="text/css" media="screen">
.layout {padding: 50px;font-family: Georgia, serif;}
.layout-slider {margin-bottom: 60px;width: 95%; margin}
.layout-slider-settings {font-size: 12px; padding-bottom: 10px;}
.layout-slider-settings pre {font-family: Courier;}
</style>
<!-- End Slider -->

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="${storeLogo}" />"></a></div>
       
    <div class="clearB floatL w240">
		<div id="rulePanel"></div>
	    <div class="clearB"></div>
	</div>
	
    <div class="clearB floatL w240">
    	<div id="ruleKeywordPanel"></div>
        <div class="clearB"></div>
    </div>

</div>
<!--  end left side -->

<!-- add contents here -->
<div class="contentArea floatL w730 marL10 marT27">

	<!--  landing page -->
	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">
			<span id="titleText"></span>
			<span id="titleHeader" class="fLblue fnormal"></span>
		</h1>
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
	<div id="relevancyContainer" style="width:95%" class="marT20 mar0">
		<div class="circlePreloader" id="preloader"><img src="../images/ajax-loader-circ.gif"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/rankingRuleGuidelines.jpg"></div>
		<div id="relevancy" class="relevancy fsize12">		
			<div class="landingCont w45p83 minHeight185 floatL">	
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
					<label class="floatL w70 marT5 padT3">Schedule</label> 
					<label><input id="startDate" name="startDate" type="text" class="w70 marT5"></label> 
					<label class="txtLabel"> - </label> 
					<label><input id="endDate" name="endDate" type="text" class="w70 marT5"></label>	
				<div class="clearB"></div>
					<label class="floatL w70 marT8 padT3">Description</label>
					<label><textarea id="description" rows="4" class="marT8" style="width:240px"></textarea> </label>
			</div>
			
			<div class="landingCont w45p83 minHeight185 floatL marL13">
				<div id="keywordInRulePanel"></div>
			</div>

			<c:forEach items="${longFields}" var="field">
				<div id="${field.key}" class="AlphaCont marB10 floatL txtAC" style="width:98%">
					<label class="marT5 txtAL w120"><span id="fieldLabel" class="fbold fsize13">${field.value}</span></label>
					<label class="marT5"><input type="text" class="w460" ${field.key ne "bf" ? 'readonly="readonly"' : ''}/></label> 
					<label class="marT6">
						<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
						<span class="crudIcon">
							<a class="saveIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_disk.png" />"></a>
							<!--a class="editIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_editGraph.png" />"></a-->
							<a class="infoIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_info.png" />"></a>
						</span>
					</label>
				</div>
			</c:forEach>
			
			<c:forEach items="${shortFields}" var="field" varStatus="i">
			<div id="${field.key}" class="AlphaCont marB10 w47p floatL ${i.count % 2 == 0? 'marL13': ''}">
				<label class="marT5 w95"><span id="fieldLabel" class="fbold fsize12">${field.value}</span></label>
				<label class="marT5"><input type="text" class="w135"  maxlength="4"/></label> 
				<label class="marT6">
					<span class="preloader" style="display:none"><img src="../images/ajax-loader-rect.gif"></span>
					<span class="crudIcon">
						<a class="saveIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_disk.png" />"></a>
						<!--a class="editIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_editGraph.png" />"></a-->
						<a class="infoIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_info.png" />"></a>
					</span>				
				</label>
			</div>
			</c:forEach>
			
			<div class="clearB"></div>
			<div class="borderT txtAR padT10">
				<a id="saveBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> 
				<a id="cloneBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clone</div></a> 
				<a id="deleteBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Delete</div></a>
			</div>
	</div>
	
	<div id="addRelevancyTemplate" style="display:none">
		<div class="w282 padT10 newRelevancy">
			<label class="w72 txtLabel">Name</label> <label><input id="popName" type="text" class="w185"></label><div class="clearB"></div>
			<label class="w72 txtLabel">Schedule </label> <label><input name="popStartDate" type="text" class="w65 fsize11"></label> <label class="txtLabel"> - </label> <label><input name="popEndDate" type="text" class="w65 fsize11"></label><div class="clearB"></div>
			<label class="w72 txtLabel">Description</label> <label><textarea id="popDescription" rows="1" class="w185"></textarea> </label><div class="clearB"></div>
			<div class="txtAR pad3"><a id="addButton" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a> <a id="clearButton" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>
		</div>
	</div>
	
	<!-- QF and PF -->
	<div id="setupFieldValueS1" style="display:none">
		<div style="width:610px;">
			<!-- Menu fields -->
			<div id="fieldListing" class="fieldListing floatL w240 minHeight300">			
				<h3 class="borderB fsize14 fbold padB4 mar0 marT15">Schema Fields<span id="sfCount" class="txtAR fsize11 floatR"></span></h3>
				<input id="searchBoxField" name="searchBoxField" type="text" class="farial fsize12 fgray searchBoxIconBg w233">
				<div class="borderT marT8"></div>
				
				<div id="preloader" class="marT30 txtAC"><img src="../images/preloader30x30Trans.gif"></div>
				
				<div id="content">
					<ul id="fieldListing" class="menuFields">
						<li id="fieldListingPattern" class="fieldListingItem" style="display:none">
							<a href="javascript:void(0);"><img src="../images/icon_addField.png" style="margin-bottom:-3px"></a>
							<span></span>
						</li>
					</ul>
					<div id="fieldsBottomPaging"></div>			
				</div>
			</div>
			<!--  end menu fields -->
			<div id="fieldSelected" class="floatL marL3 w350">
			<h3 class="fsize14 fbold pad8 mar0" style="background:#cacaca">Selected Fields<span id="sfSelectedCount" class="txtAR fsize11 floatR"></span></h3>
				<div style="overflow-y:scroll; height: 250px">
					<table class="tblfields" style="width:100%" cellpadding="0" cellspacing="0">
						<tbody id="fieldSelectedBody">
							<tr id="fieldSelectedPattern" style="display: none" class="fieldSelectedItem">
								<td class="pad0 txtAC"><a class="removeSelected" href="javascript:void(0);"><img src="../images/icon_delete2.png" class="marL3"></a></td>
								<td class="fields">
									<div class="fieldsHolder marL3">
										<span class="txtHolder"></span>
										<div class="bargraph borderR3 height24">							
											<div class="clearB"></div>
										</div>																		
									</div>
								</td>
								<td class="txtAR"><input type="text" class="w30"></td>
							</tr>
						</tbody>
					</table>					
				</div>
				<div align="right" class="marT15"><a id="applyBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Apply</div></a>  <a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>
			</div>
		
		</div>
	</div>
	
	<!-- MM -->
	<div id="setupFieldValueS2" style="display:none">
		<div class="w300">
			<div>
			Select Rule Type:
				<select id="type">
					<option value="sr" selected="selected">Single Rule</option>
					<option value="mr">Multiple Rule</option>
				</select>
			</div>
			
			<div id="sr" class="MM marT10 clearfix">
				<label class="marT8 fsize12 fbold w70">Enter Rule:</label>
				<label> <input id="singleRuleFieldMatch" type="text" class="w45" value="" maxlength="4"></label>
			</div>
			<div class="clearB"></div>
			
			<div id="mr" class="MM marT10 clearfix" style="display: none">
			<ul id="multiRule">
				<li id="multiRulePattern" class="multiRuleItem">
					<label id="ruleField" class="marT8 fsize12 fbold w70">Enter Rule:</label>
					<label><input id="ruleFieldCondition" type="text" class="w45" value="" maxlength="4"></label>
					<label><select id="ruleFieldMid"><option value="&lt;">&lt;</option></select></label>
					<label><input id="ruleFieldMatch" type="text" class="w45" value="" maxlength="4"></label>
					<a id="addRule" href="javascript:void(0);"><img src="../images/icon_addField2.png" class="marT3"></a>
					<a id="deleteRule" href="javascript:void(0);"><img src="../images/icon_delete2.png" class="marL3 marT3"></a>
				</li>	
			</ul>
			</div>
			
			<div class="">
			
			<div class="clearB"></div>
			<div align="right" class="marT15"><a id="applyBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Apply</div></a>  <a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>
			
			<div class="clearB"></div>
		</div>
	</div>
	
	<!-- BQ -->
	<div id="setupFieldValueS3" style="display:none">
		<div style="width:610px;">
		<div class="marB8"><h3 class="fsize14">Select Field: 
			<select id="facetName">
				<option value="Manufacturer">Manufacturer</option>
				<option value="Category">Category</option>
			</select>
			</h3>
		</div>
		
		<div class="clearB"></div>
			<!-- Menu fields -->
			<div id="fieldListing" class="fieldListing floatL w240 minHeight200">				
				<h3 class="borderB fsize14 fbold padB4 mar0 marT5">Schema Fields<span id="sfCount" class="txtAR fsize11 floatR"></span></h3>
				<input id="searchBoxField" name="searchBoxField" type="text" class="farial fsize12 fgray searchBoxIconBg w233">
				<div class="borderT marT8"></div>
				
				<div id="preloader" class="marT30 txtAC"><img src="../images/preloader30x30Trans.gif"></div>
				
				<div id="content">
					<ul id="fieldListing" class="menuFields">
						<li id="fieldListingPattern" class="fieldListingItem" style="display:none">
							<a href="javascript:void(0);"><img src="../images/icon_addField.png" style="margin-bottom:-3px; border:0"></a>
							<span></span>
						</li>
					</ul>
					<div id="fieldsBottomPaging"></div>
					<div class="clearB"></div>			
				</div>
			</div>
			<!--  end menu fields -->
			<div id="fieldSelected" class="floatL marL3 w350">
			<h3 class="fsize14 fbold pad8 mar0" style="background:#cacaca">Selected Fields<span id="sfSelectedCount" class="txtAR fsize11 floatR"></span></h3>
				<div style="overflow-y:scroll; height: 150px">
					<table class="tblfields" style="width:100%" cellpadding="0" cellspacing="0">
						<tbody id="fieldSelectedBody">
							<tr id="fieldSelectedPattern" style="display: none" class="fieldSelectedItem">
								<td class="pad0 txtAC"><a class="removeSelected" href="javascript:void(0);"><img src="../images/icon_delete2.png" class="marL3" style="border:0" ></a></td>
								<td class="fields">
									<div class="fieldsHolder marL3">
										<span class="txtHolder"></span>
										<div class="bargraph borderR3 height24">							
											<div class="clearB"></div>
										</div>																		
									</div>
								</td>
								<td class="txtAR"><input type="text" class="w30"></td>
							</tr>
						</tbody>
					</table>					
				</div>
				<div align="right" class="marT15"><a id="applyBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Apply</div></a>  <a id="clearBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>
			</div>
		
		</div>
	</div>

    <!-- BF -->
	<div id="setupFieldValueS4" style="display:none">

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
	
	</div>
	</div><!-- end landing page -->
	
  <div id="ruleIsLocked" class="w180" style="display:none;">
  	<div class="w180 alert">You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked.</div>
  </div>	
</div>  <!--  end content page --> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>