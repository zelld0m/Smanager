<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="deployment"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" href="<spring:url value="/css/relevancy/relevancy.css" />" rel="stylesheet">

<!-- search -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/search/search.css" />">
 <script type="text/javascript">
 (function($) {
			$(document).ready(function() {	
				$(".wrapper ul").sortable({
				    connectWith: ".wrapper ul",
				    placeholder: "ui-state-highlight"
				});
			});
		})(jQuery);
</script>
     
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
<div class="floatL w730 marL10 marT27 txtAL">

<!--  start bf page -->
	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Dashboard
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="dashboard marT20 mar0">
		
		<!-- tabs -->
		<div class="tabs">
      		<ul>
		        <li><a href="#Approval"><span>Approval</span></a></li>
		        <li><a href="#PushtoProduction"><span>Push to Production</span></a></li>
		    </ul>
		   
		<!--  approval tab -->
		<div id="Approval">
		<h2 class="fDGray marT20">Approval</h2>
		<table class="tblItems w100p marT5">
			<tr>
				<th width="5%"><input type="checkbox"></th>
				<th class="txtAL">Rule ID </th>
				<th width="17%"> Approve Status  </th>
				<th width="15%"> Status </th>
				<th width="14%"> Comment </th>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC">Approve</td>
				<td class="txtAC">Updated</td>
				<td class="txtAC"><img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> </td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Apple</td>
				<td class="txtAC">Reject</td>
				<td class="txtAC">Updated</td>
				<td class="txtAC"><img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> </td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>HP</td>
				<td class="txtAC">Approve</td>
				<td class="txtAC">Updated</td>
				<td class="txtAC"><img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> </td>
			</tr>
		</table>
		<!--  end table inside tab -->
		</div>
		
		<!--  push tab -->
		<div id="PushtoProduction" class="mar0 borderT">
		<h2 class="fDGray marT20">Push to Production</h2>
		<table class="tblItems w100p marT5">
			<tr>
				<th width="5%"><input type="checkbox"></th>
				<th class="txtAL">Rule ID </th>
				<th width="19%"> Production Status </th>
				<th width="15%"> Last Publish </th>
				<th width="15%"> Staging Action </th>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">deleted</td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Apple</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_conflict.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">new</td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">deleted</td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>Lenovo</td>
				<td class="txtAC"><img class="pointer" id="" src="<spring:url value="/images/icon_check.png" />" alt="Comment" title="Comment"></td>
				<td class="txtAC">04/23/2012</td>
				<td class="txtAC">deleted</td>
			</tr>
			
		</table>
		<!--  end table inside tab -->
		</div>
		
		</div><!--  end tabs -->
		
	<div class="clearB"></div>
</div><!--  end bf page -->

</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	