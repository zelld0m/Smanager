<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="exclude"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- jQuery functions --> 
<link type="text/css" href="<spring:url value="/css/bigbets/bigbets.css" />" rel="stylesheet">

<!-- DWR dependencies -->
<script type="text/javascript" src="<spring:url value="/dwr/interface/StoreKeywordServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/ExcludeServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/bigbets/bigbets.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/bigbets/exclude.js" />"></script>   

<script type="text/javascript">
var W3CDOM = (document.createElement && document.getElementsByTagName);

function initFileUploads() {
	if (!W3CDOM) return;
	var fakeFileUpload = document.createElement('div');
	fakeFileUpload.className = 'fakefile';
	fakeFileUpload.appendChild(document.createElement('input'));
	var image = document.createElement('img');
	image.src='../images/img_uploadfile.jpg';
	fakeFileUpload.appendChild(image);
	var x = document.getElementsByTagName('input');
	for (var i=0;i<x.length;i++) {
		if (x[i].type != 'file') continue;
		if (x[i].parentNode.className != 'fileinputs') continue;
		x[i].className = 'file hidden';
		var clone = fakeFileUpload.cloneNode(true);
		x[i].parentNode.appendChild(clone);
		x[i].relatedElement = clone.getElementsByTagName('input')[0];
		x[i].onchange = x[i].onmouseout = function () {
			this.relatedElement.value = this.value;
		}
	}
}
</script>

    <!--Left Menu-->
    <div class="clearB floatL sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
      <!-- Audit -->
      <div class="clearB floatL w240">
       	<div id="auditList"></div>
      </div>
      <!-- Keyword -->
      <div class="clearB floatL w240">
       	<div id="keywordList"></div>
      </div>
    </div>
    <!--Left Menu-->
        
    <div id="downloadTemplate" style="display: none">
    	<div>
	    	<form id="downloadForm">
		    	<label class="text60 marT6">Filename: </label>
		    	<label class="marT6"><input type="text" name="filename" class="w163"></label>
					<div class="clearB"></div>
		    	<label class="text60 marT6">Pages: </label>
		    	<label class="marT6">
		    		<select name="page" class="mar0 w168"><option value="all">All</option><option value="current" selected="selected">Current</option></select>
		    	</label>
					<div class="clearB marT6"></div>
		    	<label class="text60">Type: </label>
		    	<label class="marT6">
		    		<select name="type" disabled="disabled" class="mar0 w168"><option value="excel" selected="selected">Excel</option><option value="pdf">PDF</option><option value="csv">CSV</option></select>
		    	</label>
		    	<div class="clearB marT8 txtAR">
		    		<a id="downloadBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Download</div></a>
		    	</div>
	    		
	    	</form>
    	</div>
    </div>
    
    <!--Main Menu-->
    <div class="floatL w730 marL10 marT27">
      <div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal">
			<span id="titleText"></span>
        	<span id="keywordHeader" class="fLblue fnormal"></span>
		</div>
        <div id="addSortableHolder" class="floatL w180 txtAR padT7" style="display: none"><input id="addSortable" type="text" class="farial fsize12 fgray searchBox searchBoxIconLBg w90 marT1" maxlength="10" value="Esdsd"><a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> </div>
      </div>
     
     <div id="submitForApproval" class="clearB floatR farial fsize12 fDGray txtAR w730 GraytopLine" style="display:none"> 
	        <div id="" class="clearfix txtAL w730" style="background:#e8e8e8">	        	
	        	<div class="floatL padT10 padL10" style="width:60%" >
	        	<label class="floatL wAuto fbold">Status:</label><label class="padL5"> <span>Approved</span> <span class="fsize11 forange padL5">[ 04/12/12  8:00PM ]</span> </label>		        	
		        <!--  label class="floatL wAuto fbold">Status Date : </label> <label  class="floatL w100 padL5">04/12/12  8:00PM</label -->
			  	</div>   			  	
	        	<div class="floatR marL8 marR3 padT5"> 	        		
	        		<a id="submitForApproval" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Submit for Approval</div></a>
	        	</div>
	        </div>	
	        <div class="clearB"></div>	
	 </div>
	 
    <!--Add Item Content--> 
     <div id="addItemTemplate" style="display: none">
      	<div id="tabs" style="width:257px">
      		<ul>
		        <li><a href="#singleAdd"><span>Single Add</span></a></li>
		        <li><a href="#multiAdd"><span>Multiple Add</span></a></li>
		    </ul>
      		
      		<!--  tab -->
		    <div id="singleAdd" class="mar0 borderT">
		      	<h3></h3>
				<div class="floatL w170"> 
			        <label class="floatL w60 marL5 padT5">%%store%%:</label>
					<label><input id="addItemDPNo" type="text" class="w83 fgray fsize11" value="SKU #"></label> 
			    </div>
				<div class="clearB"></div>
				<div class="floatL w155 marT5"> 
			    	<label class="floatL w60 marL5 padT5">Valid Until:</label>
					<label class="ddate"><input id="addItemDate" type="text" class="w65"></label>
			    </div>
			   
				<div class="floatL marT5" style="width:97px">
			    	<label class="floatL marL5 padT5" style="width:55px">Elevation:</label> 
					<label><input id="addItemPosition" type="text" class="w25"></label>
			    </div>
			    
			    <div class="clearB"></div>
				<div class="floatL marT5 marL5"> 
			    	<label class="w60 floatL padT5">Comment: </label> 
					<label><textarea id="addItemComment" style="width:180px; float:left; margin-bottom:7px"></textarea></label>
			    </div>
				<div align="right"><a id="addItemBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Elevate</div></a></div>		        
			    <div class="clearB"></div>
		   </div>
		        
	       <div id="multiAdd">
	       		<h3></h3>
	        	<div class="alert mar0"> This will overwrite existing elevation. </div>
	        	<div align="right" class="marTB5 marR3 txtAL">Lorem ipsum dolor <a class="infoIcon" href="javascript:void(0);"><img src="<spring:url value="/images/icon_info.png" />" class="floatR"></a></div>
				<textarea class="w245"></textarea>
				
				<div align="right" class="marT5 marR3"><a id="addItemBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Elevate</div></a></div>
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
		<li class="padT1"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1" ></div></a></li>
	</ul>
	</div>

	  <div id="sortablePagingTop" class="floatL txtAL w550"></div>
      
      <!--End Pagination-->
      <div class="clearB floatR farial fsize12 fDGray fbold txtAR w730 marT10" style="background:url(../images/bgSort.jpg) repeat-x; padding-top:8px;"></div>
      <div class="clearB"></div>
       
      <!--Start Displaying Items-->
      <div id="sortable-bigbets-container" class="clearB floatL w730" style="width:730px">
        <ul id="sortable-bigbets">
        	<li id="sItemPattern" style="display: none; position:relative ">
	        	<div id="addCommentTemplate" style="display: none">
				   <div class="elevateItemPW">
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
			         	<p class="textInfo"><span class="fgreen">%%store%% SKU #: </span><span id="sItemDPNo"></span></p>
			         	<p class="textInfo"><span class="fgreen">Mfr. Part #: </span><span id="sItemMfrPN"></span></p>
		
			         	<div class="borderT clearB"></div> 
			         	<div class="bigbetsShade" style="padding:5px">
			         	<strong>Exclude Info:</strong>
			         	<div class="txtAR w60 floatR fgray fsize11">
			         		<img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> 
			         		<img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History">
						</div>
			         	<div class="clearB"></div>
			         	<div class="txtAR w60 floatR fgray fsize11 padT5">
			         		<span id="sItemValidityText" class="fDblue"></span>
						</div>
									        
						<div class="listbeta padT5 marL3">
			         			Valid Until<input id="sItemExpDate" type="text" class="txtBoxSmall farial marL3 w60" style="margin-top:-4px" />
			         	</div>
			         	<div class="txtAR w65 floatL fgray fsize11 padT5">
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
      <!--End Displaying Items-->
       <div id="sortablePagingBottom" class="w730 floatL txtAL marT20"></div>
      <!--Pagination-->
      
      <div id="removeItems" class="txtAR padT10">
		<a id="removeBtn" href="javascript:void;" class="buttons btnGray clearfix"><div class="buttons fontBold">Remove Items</div></a>
	  </div>
    </div>
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>	