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
    &nbsp;
</div>
<!--  end left side -->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27 txtAL">

<!--  start bf page -->
	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Relevancy
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">

	
	<div class="relevancy fsize12">	
		<div id="wrapper" class="wrapper floatL" style="width:100%;">
		<div class="rfield floatL" style="width:32%">
			<div class="field titleAplha marB8">Field</div>	
			<ul>
				<li class="rfieldchild">lorem ipsum dolor sit amet</li> 
				<li class="rfieldchild">amet lorem</li> 
				<li class="rfieldchild">lorem</li> 
			</ul>
		</div>
		
		<div class="rnumber marR10 marL10 floatL" style="width:32%">
			<div class="number titleAplha marB8">Number</div>
			<ul>
				<li class="rnumberchild"><input type="text"></li> 
			</ul>
		</div>
		
		<div class="rfunction floatL" style="width:32%">
			<div class="function titleAplha marB8">Function</div>
			<ul>
				<li class="rfunctionchild">lorem ipsum dolor sit</li> 
				<li class="rfunctionchild">amet lorem</li> 
				<li class="rfunctionchild">lorem</li> 
			</ul>
		</div>
		
		<div class="floatL clearfix containerHolder" style="width:49%;">
	   		<p class="txtAC"> lorem ipsum dolor sit amet </p>   
	   		<ul>   		 	
	   			<li class="posRel">
	   				<div class="posAbs floatR right4"><a href="#"><img src="<spring:url value="/images/iconDeleteBlack.png" />"></a></div>
	   				<span>Function 1 Name</span><ul class="container"> <div class="clearB"></div></ul>
	   			</li>     
		    </ul>		    
	    </div>
	    <div class="floatL clearfix containerHolder" style="width:49%;">	    
		    <p class="txtAC"> lorem ipsum dolor sit amet </p>   
		    <ul>
	   		 	<li class="posRel">
	   		 		<div class="posAbs floatR right4"><a href="#"><img src="<spring:url value="/images/iconDeleteBlack.png" />"></a></div>
	   		 		<span>Function 1 Name</span><ul class="container"></ul>
	   		 	</li> 	        
		    </ul>
	    </div>
	    <div class="floatL clearfix containerHolder" style="width:49%">	    
		    <p class="txtAC"> lorem ipsum dolor sit amet </p>   
		    <ul>
	   		 	<li class="posRel"><div class="posAbs floatR right4"><a href="#"><img src="<spring:url value="/images/iconDeleteBlack.png" />"></a></div>
	   		 		<span>Function 1 Name</span><ul class="container"></ul>
	   		 	</li>        
		    </ul >		   
	    </div>
	    <div class="floatL clearfix containerHolder" style="width:49%">	
	    	<p class="txtAC"> lorem ipsum dolor sit amet </p>   
		    <ul>
	   		 	<li class="posRel"><div class="posAbs floatR right4"><a href="#"><img src="<spring:url value="/images/iconDeleteBlack.png" />"></a></div>
	   		 		<span>Function 1 Name</span><ul class="container"></ul>
	   		 	</li>        
		    </ul>		   
		</div>
			
		</div>		
	</div>
	<div class="clearB"></div>
</div><!--  end bf page -->

</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	