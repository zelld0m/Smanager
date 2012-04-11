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
	<div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>

	<div class="clearB floatL w240">
    	<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">Site Updates</div>
    	
        <div class="ticker clearB floatL w230 padL5">
			<ul class="listSU fsize11 marT10">
				<li><p class="notification"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="alt"><p class="notification"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="alt"><p class="notification"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="alt"><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
			</ul>
    	</div> 
	</div>
</div>
<!--End Left Side-->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 faNarrow fsize20 fnormal">
			Audit Trail
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
    <h2 class="fsize12 txtAR borderB2"> Keyword: <input type="text" class="marTn3 w230 searchBoxIconBg" style="margin-bottom:4px; margin-left:4px;" id="keyword">  <div class="floatR" style="margin-top:-4px; margin-left:6px"><a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="goBtn">GO</div></a></div>  </h2>
    <!--Pagination-->
    
    <!--  info -->
    <div class="info fsize12 clearfix">
    	<label class="txtLabel">Reference ID:</label>
    	<label class="details marR15"><select class="w205" id="refList">
	    		<option value="">-- Select Ref ID --</option>
	    	</select>
    	</label>
    	<label class="txtLabel">Date Range:</label>
    	<label class="details"><input type="text" class="w70" id="startDate" /><input type="text" class="w70" id="endDate"/> </label> 
    	<div class="clearB"></div>
    	
    	<label class="txtLabel">User Name:</label>
    	<label class="details marR15">
	    	<select class="w205" id="userList">
	    		<option value="">-- Select Name --</option>
	    	</select>
    	</label>
    	<label class="txtLabel">Type:</label>
    	<label class="details">
	    	<select class="w205" id="typeList">
	    		<option value="">-- Select Type --</option>
	    	</select>
    	</label>
    	
    	<div class="clearB"></div>
    	<label class="txtLabel">Action:</label>
    	<label class="details">
	    	<select class="w205" id="actionList">
	    		<option value="">-- Select Action --</option>
	    	</select>
    	</label>
    </div>
    <!--  end info -->
    
      <div class="mar0">
        <div class="clearB floatL farial fsize12 fDblue w300 padT10 marL10">Displaying 1 to 25 of 26901 Products</div>
        <div class="floatR farial fsize12 fgray txtAR padT10">
          <div class="txtAR">
            <ul class="pagination">
              <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">1</a></li>
              <li><a href="#">2</a></li>
              <li><a href="#">3</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
            </ul>
          </div>
        </div>
      </div>
      <!--Pagination-->
    	<table width="100%" class="fsize12 tblAlpha">
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
    		<tr>
    			<td class="txtAC">11/${i}/2011 12:30:00</td>
    			<td class="txtAC">1234567890</td>
    			<td class="txtAC">mpedcp</td>
    			<td class="txtAC">Elevate</td>
    			<td class="txtAC">Add</td>
    			<td class="txtAC">ipad</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/page_edit.png" />"></a></td>
    		</tr>
    		</tbody>
    	</table>    	
	</div>
	<div class="clearB"></div>
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	