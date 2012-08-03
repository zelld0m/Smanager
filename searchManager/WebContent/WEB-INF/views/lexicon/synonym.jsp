<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="lexicon"/>
<c:set var="submenu" value="synonym"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!--  slider checkbox -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/linguistics.css" />">
<script type="text/javascript" src="<spring:url value="/js/lexicon/synonym.js" />" ></script>

 <!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
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
	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Synonym
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
    <h2 class="fsize12 txtAR borderB2 padB8"> Keyword: 
    	<div class="floatR" style="margin-top:-6px; margin-left:1px"><a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a></div>
    	<div class="searchBoxHolder w160 floatR" style="margin-top:-5px; margin-left:4px;"><input type="text" class="w99p"> </div>  
    </h2>
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
      	<div class="clearB"></div>
      	<div class="linguistics marT20">
	    	<table id="itemPattern">
		    	<tr>
			    	<td>
				    	<ul id="itemList">
				    		<li id="item" class="alt"></li>
				    	</ul>
			    	</td>
		    	</tr>
	    	</table>
    	</div>
	<div class="clearB"></div>
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	