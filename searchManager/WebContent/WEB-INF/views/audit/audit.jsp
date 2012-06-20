<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="audit"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page css --> 
<link type="text/css" href="<spring:url value="/css/settings/audit.css" />" rel="stylesheet">

<script type="text/javascript" src="<spring:url value="/js/jquery.dateFormat-1.0.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/AuditServiceJS.js"/>"></script>

<script type="text/javascript" src="<spring:url value="/js/settings/audit.js" />"></script>   

<!--Start Left Side-->
<div class="clearB floatL minW240 sideMenuArea">
		<div class="clearB floatL w240">
    	<div id="sideHeader" class="sideHeader posRel">
    		<img src="../images/corner_tl.png" class="curveTL"/>
    		<img src="../images/corner_tr.png" class="curveTR"/>
			Search Refinement
			<img src="../images/corner_bl.png" class="curveBL"/>
			<img src="../images/corner_br.png" class="curveBR"/>
    	</div>

    	<!--  info -->
    <div class="info fsize12 clearfix">
    	<label class="txtLabel">Keyword:</label>
    	<label class="details marR15"><input type="text" class="w200" id="keyword" maxlength="200"/></label>

    	<label class="txtLabel">Type:</label>
    	<label class="details">
	    	<select class="w205" id="typeList">
	    		<option value="">-- Select Type --</option>
	    	</select>
    	</label>
    	
    	<div class="clearB"></div>
    	<label class="txtLabel">Reference ID:</label>
    	<label class="details marR15"><select class="w205" id="refList">
	    		<option value="">-- Select Ref ID --</option>
	    	</select>
    	</label>
    	<label class="txtLabel">Action:</label>
    	<label class="details">
	    	<select class="w205" id="actionList">
	    		<option value="">-- Select Action --</option>
	    	</select>
    	</label>
    	
    	<label class="txtLabel">User Name:</label>
    	<label class="details marR15">
	    	<select class="w205" id="userList">
	    		<option value="">-- Select Name --</option>
	    	</select>
    	</label>
    	
    	<label class="txtLabel">Date Range:</label>
    	<div class="clearB"></div>
    	<label>
	    	<input type="text" class="w70 floatL marL3" id="startDate"/>
	    </label>
	    <label>
	    	<input type="text" class="w70 floatL marL5" id="endDate"/>
	    </label>
    	<div class="clearB"></div>

    	<div class="clearB"></div>
		<div align="right" class="padR5 marT10">
		    <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="goBtn">Filter</div></a>
			<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="resetBtn">Reset</div></a>
		</div>
    </div>
    <!--  end info -->
	</div>
	
	
	
	
	
	
</div>
<!--End Left Side-->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Audit Trail
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
    
        	
      <!-- Pagination-->     
      <div class="floatR padT10">
        <a href="javascript:void(0);" id="exportBtn"><div class="btnGraph btnDownload"></div></a>
	  </div>
	  <div id="resultsTopPaging" class="marTn2"></div>
       <div class="clearB"></div>
      <!--end Pagination-->
      
    	<table width="100%" class="fsize12 tblAlpha marT8">
    		<tbody id="resultsBody">
    		<tr>
    			<th width="10%">Date</th>
    			<th width="13%">Ref ID</th>
    			<th width="12%">Username</th>
    			<th width="12%">Type</th>
    			<th width="11%">Action</th>
    			<th width="12%">Keyword</th>
    			<th width="30%">Description</th>
    		</tr>
    		</tbody>
    	</table>  
    	 <div class="mar0">
        <div id="resultsBottomPaging"></div>	
      </div>  	
	</div>
	<div class="clearB"></div>
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	