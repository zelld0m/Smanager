<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="audit"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page css --> 
<link type="text/css" href="<spring:url value="/css/settings/audit.css" />" rel="stylesheet">

<script type="text/javascript">
(function($){
	$(document).ready(function() { 
		$(".ticker").ticker({
			el: ".listSU"
		});
	});	
})(jQuery);
</script>

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
    <h2 class="fsize12 txtAR borderB2"> Keyword: <input type="text" class="marTn3 w230 searchBoxIconBg" style="margin-bottom:4px; margin-left:4px;">  <div class="floatR" style="margin-top:-4px; margin-left:6px"><a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">GO</div></a></div>  </h2>
    <!--Pagination-->
    
    <!--  info -->
    <div class="info fsize12 clearfix">
    	<label class="txtLabel">Reference ID:</label>
    	<label class="details marR15"><input type="text" class="w200"></label>
    	<label class="txtLabel">Date Range:</label>
    	<label class="details"><input type="text" class="w70" /> <img src="<spring:url value="/images/icon_calendar.png" />" style="margin-bottom:-5px"> to <input type="text" class="w70" /> <img src="<spring:url value="/images/icon_calendar.png" />" style="margin-bottom:-5px"></label> 
    	<div class="clearB"></div>
    	
    	<label class="txtLabel">User Name:</label>
    	<label class="details marR15">
	    	<select class="w205">
	    		<option>-- Select Name --</option>
	    		<option>Lorem ipsum dolor</option>
	    	</select>
    	</label>
    	<label class="txtLabel">Type:</label>
    	<label class="details">
	    	<select class="w205">
	    		<option>-- Select Type --</option>
	    		<option>Lorem ipsum dolor</option>
	    	</select>
    	</label>
    	
    	<div class="clearB"></div>
    	<label class="txtLabel">Action:</label>
    	<label class="details">
	    	<select class="w205">
	    		<option>-- Select Action --</option>
	    		<option>Lorem ipsum dolor</option>
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
    		<tr>
    			<th width="20%">Date</th>
    			<th width="20%">Username</th>
    			<th class="hl" width="20%">Type</th>
    			<th width="15%">Action</th>
    			<th width="15%">Description</th>
    		</tr>
    		<c:forEach var="i" begin="1" end="10" step="1">
    		<tr>
    			<td class="txtAC">11/${i}/2011 12:30:00</td>
    			<td class="txtAC">mpedcp</td>
    			<td class="txtAC hl">Elevate</td>
    			<td class="txtAC">Add</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/page_edit.png" />"></a></td>
    		</tr>
    		</c:forEach>
    	
    	</table>    	
	</div>
	<div class="clearB"></div>
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	