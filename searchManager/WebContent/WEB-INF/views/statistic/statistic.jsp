<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="statistic"/>
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
		
		<div class="buttonHolder marB20">
			<a id="" href="javascript:void(0);" class="buttons btnLStyA clearfix"><div class="buttons fontBold">Refresh</div></a>
			<a id="" href="javascript:void(0);" class="buttons btnLStyA clearfix"><div class="buttons fontBold">Select All</div></a>
			<a id="" href="javascript:void(0);" class="buttons btnLStyA clearfix"><div class="buttons fontBold">Select None</div></a>
			<a id="" href="javascript:void(0);" class="buttons btnLStyA clearfix"><div class="buttons fontBold">To Prod</div></a>
		</div>
		
		<h2 class="fDGray">Elevate</h2>
		<table class="tblItems w100p">
			<tr>
				<th width="13%">Select</th>
				<th>Resource</th>
				<th width="13%">In Stage</th>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>prodPage</td>
				<td class="txtAC"></td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicesOverview</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicePlans</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
		</table>
		
		
		<h2 class="fDGray marT20">Exclude</h2>
		<table class="tblItems w100p">
			<tr>
				<th width="13%">Select</th>
				<th>Resource</th>
				<th width="13%">In Stage</th>
			</tr>			
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicesOverview</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicePlans</td>
				<td class="txtAC"></td>
			</tr>
		</table>
		
		<h2 class="fDGray marT20">Query Cleaning</h2>
		<table class="tblItems w100p">
			<tr>
				<th width="13%">Select</th>
				<th>Resource</th>
				<th width="13%">In Stage</th>
			</tr>			
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicesOverview</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicePlans</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
		</table>
		
		
		<h2 class="fDGray marT20">Ranking Rule</h2>
		<table class="tblItems w100p">
			<tr>
				<th width="13%">Select</th>
				<th>Resource</th>
				<th width="13%">In Stage</th>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>prodPage</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicesOverview</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
			<tr>
				<td class="txtAC"><input type="checkbox"></td>
				<td>servicePlans</td>
				<td class="txtAC"><img src="<spring:url value="/images/icon_check.png" />"></td>
			</tr>
		</table>
		
		
		
	
	<div class="clearB"></div>
</div><!--  end bf page -->

</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	