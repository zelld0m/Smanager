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
			
			<div class="floatR marR8 marT4"><a id="downloadBtn" href="javascript:void(0);" rel="nofollow" >
				<div class="btnGraph btnDownload"></div>
			</a>
			</div>
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
      	<div class="clearB"></div>
      	<div class="linguistics marT20">
      	<div style="height:600px; overflow-y:auto">
	    	<table id="itemPattern">
		    	<tr>
			    	<td>
				    	<ul id="itemList">
				    		<li id="item" class="alt txtAL pad2"></li>
				    	</ul>
			    	</td>
		    	</tr>
	    	</table>
	    </div>
    	</div>
	<div class="clearB"></div>
	</div>
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	