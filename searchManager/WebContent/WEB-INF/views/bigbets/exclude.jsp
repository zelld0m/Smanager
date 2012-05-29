<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="exclude"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<link type="text/css" href="<spring:url value="/css/bigbets/bigbets.css" />" rel="stylesheet">
<script type="text/javascript" src="<spring:url value="/js/bigbets/bigbets.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/bigbets/exclude.js" />"></script>   

<!--Left Menu-->
<div class="clearB floatL sideMenuArea">
	<div class="companyLogo">
		<a href="javascript:void()"><img src="<spring:url value="${storeLogo}" />"></a>
	</div>
	
	<div class="clearB floatL w240">
		<div id="rulePanel"></div>
	    <div class="clearB"></div>
	</div>
</div>
<!--Left Menu-->
    
<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">

	<div class="floatL w730 titlePlacer">
      <div class="w535 padT10 padL10 floatL fsize20 breakWord">
		<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
	  </div>
      <div id="addItemHolder" class="floatL w180 txtAR padT7" style="display: none">
      	<!--  input id="addItem" type="text" class="farial fsize12 fgray searchBox searchBoxIconLBg w90 marT1" maxlength="10"-->
      	<a href="javascript:void(0);" id="addItemBtn" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a>
      </div>
	</div>
    
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
	 
	 <div class="clearB"></div>	
	  
   	 	 <!--Add Item Content-->
	<div id="addItemTemplate" style="display: none">
		<div id="addOption" style="width: 257px">
			<ul>
				<li><a href="#addBySKU"><span>By SKU</span></a></li>
			</ul>
			
			<!--  tab -->
			<div id="addBySKU" class="mar0 borderT">
				<h3 class="padT10"></h3>
				<div class="clearB"></div>
				<div class="floatL marT5 marL5">
					<label class="w60 floatL padT5">SKU #: (comma-delimited)</label> 
					<label><textarea id="addItemDPNo" style="width: 180px; float: left; margin-bottom: 7px"></textarea>
					</label>
				</div>
				<div class="clearB"></div>
				<div class="floatL w155 marT5">
					<label class="floatL w60 marL5 padT5">Valid Until:</label> 
					<label class="ddate"><input id="addItemDate" type="text" class="w65"></label>
				</div>
				<div class="clearB"></div>
				<div class="floatL marT5 marL5">
					<label class="w60 floatL padT5">Comment: </label> 
					<label><textarea id="addItemComment" style="width: 180px; float: left; margin-bottom: 7px"></textarea>
					</label>
				</div>
				<div align="right">
					<a id="addItemToRuleBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Exclude</div>
					</a>
					<a id="clearBtn" href="javascript:void(0);"
						class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div>
					</a>
				</div>
				<div class="clearB"></div>
			</div>
			<!-- end tab -->
		</div>
	</div>
		       
	<!--Start Pagination-->
	<div id="sortableDisplayOptions" style="display: none">
	<ul class="viewSelect marT6">
		<li class="fLgray2">|</li>
		<li class="padR5 fLgray2">
		<select id="filterDisplay" class="marTn3">
			<option value="all">All</option>
			<option value="active">Active</option>
			<option value="expired">Expired</option>
		</select></li>
		<li class="padT1"><a href="javascript:void(0);" id="sortableTile" class="btnGraph" alt="Grid View" title="Grid View"><div class="btnGraph btnViewTile"></div></a></li>
		<li class="padT1"><a href="javascript:void(0);" id="sortableList" class="btnGraph" alt="List View" title="List View"><div class="btnGraph btnViewList"></div></a></li>
		<li class="padT1"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1 marL3" alt="Download" title="Download" ></div></a></li>
		<li class="padT1"><a href="javascript:void(0);" id="clearRuleBtn"><div class="btnGraph btnClearDel marT1" alt="Remove All" title="Remove All"></div></a></li>
	</ul>
	</div>

	  <div id="sortablePagingTop" class="floatL txtAL w550"></div>
      
      <!--End Pagination-->
      <div class="clearB floatR farial fsize12 fDGray fbold txtAR w730 marT10 padT8"></div>
      <div class="clearB"></div>
       
      <!--Start Displaying Items-->
      <div id="sortable-bigbets-container" class="clearB floatL w730" style="width:730px">
      	<div class="circlePreloader" id="preloader"><img src="../images/ajax-loader-circ.gif"></div>
		<div id="noSelected"><img id="no-items-img" src="../images/ElevatePageisBlank.jpg"></div>
		<div id="exclude">
        <ul id="sortable-bigbets">
        	<li id="sItemPattern" style="display: none; position:relative ">
	        	<div id="addCommentTemplate" style="display: none">
				   <div class="elevateItemPW w282">
				   <div class="borderB padB8 autoScroll height120">
				   <div id="commentTemplate" style="display: none">
				        <div class="pad5">
					   		<div class="padR8 floatL wordwrap padT3" style="width:60px">%%timestamp%%</div>
				            <div class="floatL w150 padT7">
				            	<img src="<spring:url value="/images/user13x13.png" />" class="marBn3 marR3">
				            	<span class="fDblue">%%commentor%%</span>
				                <span>%%comment%%</span>   
				            </div>
				            <div class="clearB"></div>
			            </div>
				    </div>
				   	<div id="commentHolder"></div> 
				   </div>
				   <div class="floatL marT5 w282"> <p>Comment:</p>
				   <textarea id="newComment" class="w278 marB7 resizeNone"></textarea></div>
				   <div align="right"><a id="addCommentBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a></div>
				   </div>
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
		        <div id="sortableBox" class="handle sortableBox">
		          <div class="floatR posRel padR10" style="z-index:1"><a id="sItemDelete" href="javascript:void(0);"><img src="<spring:url value="/images/btn_delete_graybg.jpg" />"></a></div>
		          <div class="txtAC posRel">
		          <div id="sItemStampExp" class="stampExpired"></div>
		         	<div class="elevateItemImg"><img id="sItemImg" src="" style="width:100px; height:100px">
		         	<div id="sItemMan" class="manfcName"></div>
		         	</div>
		         	
		         	<div class="listInfo">
			         	<div class="listTitle"><a href="javascript:void(0)" id="sItemName"></a></div>
			         	<p class="textInfo"><span class="fgreen">SKU #: </span><span id="sItemDPNo"></span></p>
			         	<p class="textInfo"><span class="fgreen">Mfr. Part #: </span><span id="sItemMfrPN"></span></p>
		
			         	<div class="borderT clearB"></div> 
			         	<div class="bigbetsShade" style="padding:5px; min-height: 75px">
			         	<strong>Exclude Info:</strong>
			         	<div class="txtAR w60 floatR fgray fsize11">
			         		<img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> 
			         		<img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History">
						</div>
			         	<div class="clearB"></div>
			         	<div class="txtAR w65 floatR fgray padT5 endingTxt">
			         		<span id="sItemValidityText" class="fDblue"></span>
						</div>
									        
						<div class="listbeta">
			         			Valid Until<input id="sItemExpDate" type="text" class="txtBoxSmall farial marL3 w60" style="margin-top:-4px" />
			         	</div>
			         	<div class="txtAR w65 floatL fgray padT5 endingTxt">
			         		<span id="sItemValidityText" class="fDblue"></span>	
						</div>
						<div class="clearB"></div>
			         	<p class="fgray padT5 fsize11">
			         		<img src="<spring:url value="/images/user_red.png" />" class="marBn4 marR3">
			         		<span id="sItemModBy" class="fbold"></span> on <span id="sItemModDate" class="fDblue"></span></p>
			           </div>
		         	</div>
		          </div>
		          <div class="clearB"></div>
		        </div>		        
	        </li>
        </ul>
        </div>
      </div>
      
      <div id="sortablePagingBottom" class="w730 floatL txtAL marT20"></div>
      
		<div id="ruleIsLocked" class="w180" style="display:none;">
	  		<div class="w180 alert">You are not allowed to perform this action because  you do not have the required permission or rule is temporarily locked.</div>
	  	</div>
</div><!--End Right Side-->
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>	
