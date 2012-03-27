<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="lexicon"/>
<c:set var="submenu" value="synonym"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!--  slider checkbox -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/synonym.css" />">
<script type="text/javascript" src="<spring:url value="/js/lexicon/synonym.js" />" ></script>

 <!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
    
	<div class="clearB floatL w240">
    	<div class="sideHeader">Site Updates</div>
        <div class="clearB floatL w230 padL5">
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
				<li class="textAR"><a href="#">see all updates  &raquo;</a></li>
			</ul>
    	</div> 
	</div>
</div>
<!--  end left side -->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Synonym
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
    <h2 class="fsize12 txtAR borderB2"> Keyword: <input type="text" class="marTn3 w160 searchBoxIconBg" style="margin-bottom:4px; margin-left:4px;">  <div class="floatR" style="margin-top:-5px; margin-left:1px"><a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a></div>  </h2>
    <!--Pagination-->
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
    			<th></th>
    			<th width="28%">Word</th>
    			<th width="20%">Date Added</th>
    			<th width="12%">Enabled</th>
    			<th width="20%">Username</th>
    			<th width="14%">Action</th>
    			
    		</tr>
    		<c:forEach var="i" begin="1" end="10" step="1">
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td>Report</td>
    			<td class="txtAC">11/${i}/2011 12:30:00</td>
    			<td class="txtAC"><div class="slideCheckbox"><input type="checkbox" id="checkbox-4" class="firerift-style-checkbox" /></div></td>
    			<td class="txtAC">mpedcp</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/page_edit.png" />"></a> <a href="#"><img src="<spring:url value="/images/icon_history.png" />" class="marRL5"></a> <a href="#"><img src="<spring:url value="/images/add.png" />"></a></td>
    		</tr>
    		</c:forEach>
    	
    	</table>    	
	</div>
	<div class="clearB"></div>
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	