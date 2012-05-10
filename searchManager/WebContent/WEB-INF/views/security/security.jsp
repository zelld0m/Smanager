<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="security"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!--Start Left Side-->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>

	<div class="clearB floatL w240">
    	<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">Role</div>
        <div class="clearB floatL w230 padL5">
			<ul class="listRole fsize12 marT10">
				<li><a href="">Administrator</a></li>
				<li class="alt"><a href="">Encoder</a></li>
				<li><a href="">Developer</a></li>
				<li class="alt"><a href="">Lorem ipsum</a></li>
				<li><a href="">Dolor sit amet</a></li>
				<li class="alt"><a href="">Lorem dolor amet</a></li>
			</ul>
    	</div> 

    	<div class="clearB"></div>
    	<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">Site Updates</div>
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
<!--End Left Side-->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<div class="w460 padT10 padL10 floatL faNarrow fsize20 fnormal breakWord">Security</div>
       	<div class="floatL w245 txtAR padT7">   	
       		<div class="w175 searchBoxHolder floatL">
       			<input type="text" name="query" id="query" class="farial fsize12 fgray w99p">
       		</div>   		
			<a href="javascript:void(0)" id="searchbutton"><img src="<spring:url value="/js/ajaxsolr/images/btn_GO.png" />"  align="absmiddle"></a> 
		</div>		
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
    <h2 class="fsize16 fDblue fnormal txtAL borderB"> Administrator <a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a>  <div class="floatR" style="margin-left:6px"><a href="#" class="buttons btnLGray clearfix"><div class="buttons fontBold">Modify Permission</div></a></div>  <div class="clearB"></div> </h2>
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
    			<th width="7%"></th>
    			<th width="35%">Username</th>
    			<th class="hl" width="15%">Status</th>
    			<th width="20%">Date Started</th>
    			<th width="17%">Action</th>
    		</tr>
    		<c:forEach var="i" begin="1" end="10" step="1">
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td>mpedcp</td>
    			<td class="txtAC hl">Enabled</td>
    			<td class="txtAC">11/${i}/2011 12:30:00</td>
    			<td class="txtAC"><a href="#">Reset Password</a></td>
    		</tr>
    		</c:forEach>
    	
    	</table>   
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
	</div>
	<div class="clearB"></div>
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	