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
    <div class="companyLogo"><a href="#"><img src="<spring:url value="${storeLogo}" />"></a></div>
    
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
	<textarea rows="15" cols="15" class="tinymce"></textarea>
	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Statistics
		</h1>
	</div>
	
	
	
	
	<a href="javascript:void(0);" class="buttons btnDisable clearfix"> <div class="buttons fontBold">Approve</div> 	</a>
	<div class="clearB"></div>
	
	
	
	<div class="w95p marRLauto">

	</div>
	<div class="w95p marRLauto padT0 marT0 fsize12" style="max-height:365px; overflow-y:auto">	
		<div class="pad5 borderB clearfix">
			<label class="floatL w80">1</label>
			<label class="floatL w80">422</label>
			<label class="floatL w500">
				<label class="floatL w400">western digital external hard drive</label> <label class="floatL fsize11 w100"><a href="/">show active rules</a></label>
				<div class="marT20 w500">
					<ul>
						<li class="items borderB padTB5 clearfix w500 padL5">
							<label class="w30 preloader floatR"><img src="<spring:url value="/images/ajax-loader-rect.gif"/>"></label>
							<label class="ruleType floatL fbold w220">_appleTV</label>
							<label class="imageIcon floatL w20 posRel topn2"><img src="<spring:url value="/images/icon_reviewContent2.png"/>" class="top2 posRel"></label>
							<label class="name w225 floatL"><span class="fbold">Query Cleaning</span></label>
						</li>
						<li class="items padTB5 clearfix w500 padL5">
							<label class="w30 preloader floatR"><img src="<spring:url value="/images/ajax-loader-rect.gif"/>"></label>
							<label class="ruleType floatL fbold w220">Query Cleaning</label>
							<label class="imageIcon floatL w20 posRel topn2"><img src="<spring:url value="/images/icon_reviewContent2.png"/>" class="top2 posRel"></label>
							<label class="name w225 floatL"><span class="fbold">_appleTV</span></label>
						</li>
						
						<!-- please remove the class borderB for the last item in list thanks -->
						
					</ul>
				</div>
			</label>
		</div>
		
		<div class="pad5 borderB clearfix alt">
			<label class="floatL w80">2</label>
			<label class="floatL w80">423</label>
			<label class="floatL w500"><label class="floatL w400">western digital external hard drive</label> <label class="floatL fsize11 w100"><a href="/">show active rules</a></label></label>			
		</div>
		

	</div>
	
</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	