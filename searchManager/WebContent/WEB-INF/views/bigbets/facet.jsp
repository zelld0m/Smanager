<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="facet"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/bigbets/facet.css" />">
  
<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
 	<div class="clearB floatL w240"> 
    	<div id="keywordSidePanel"></div> &nbsp;
        <div class="clearB"></div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
    <div class="floatL w730 titlePlacer">
      <div class="w535 padT10 padL10 floatL fsize20 fnormal">Facet Rule</div>
    </div>
    
    <div class="AlphaCont bgboxAlpha w46p minHeight155 floatL marT20 fsize12">
    	<h3 class="borderB padB8">Lorem ipsum dolor</h3>
    	
    	<label class="floatL w100">Name :</label> 
    	<label class="floatL"> <input type="text" class="w190"> </label>
    	
    	<div class="clearB"></div>
    	
    	<label class="floatL w100">Lorem ipsum:</label>
    	<label class="floatL w100"> 
    		<select class="mar0">
    			<option>Alphabetical<option>
    		</select>
    	</label>
    	<label class="floatL w100"> 
    		<select class="mar0">
    			<option>Ascending</option>
    		</select>
    	</label>
    </div>
    
    <div class="AlphaCont bgboxAlpha w46p minHeight155 floatL marT20 marL20 fsize12">
    	<h3 class="borderB padB8">Using this Rule 
    		<div class="floatR"> <input type="text" class="searchBoxIconLBg posRel topn4"> 
    		<a href="#"><img src="<spring:url value="/images/icon_addField2.png" />" class="posRel top2"></a>
    		</div>
    	</h3>
    	
    	<ul>
    		<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a> Lorem dolor sit amet</li>
    		<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a>item sample</li>
    		<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a>sample item </li>
    	</ul>
    </div>
    <div class="clearB"></div>
     
    <div class="landingCont bgboxGray marB10 txtAL marT20 w97p fsize12 clearfix padT10">
    	<div class="w32p floatL">
    		<h3 class="fsize14">Select Facet</h3>
    		<div class="colTop topRed" >
    			<select class="w185 posRel top2">
    				<option> sample </option>
    				<option> lorem ipsum </option>
    			</select> <a href="#"><img src="<spring:url value="/images/icon_addField2.png" />" class="posRel bottomn7"></a>
    		</div>
    			<ul class="facetList marT10">
					<li> dddddd <a href="">(100)</a> </li>
					<li> adfasfasfasfasfsdf <a href="">(0)</a></li>
					<li> rrrasfasfasfasdffs <a href="">(20)</a></li>
					<li> wfrwfsdfsfsd <a href="">(5)</a></li>
					<li> saffsdf <a href="">(7)</a></li>
					<li> dddd <a href="">(33)</a></li>
				</ul>
    	</div>
    	<div class="floatL w68p">
    		<h3 class="fsize14">Facet Value</h3>
    		<div class="w49p floatL">
				<div class="colTop topBlue posRel">
					<img src="<spring:url value="../css/theme/default/images/redBlue_Arrow.png" />" class="posAbs"> 
					<h3 class="padL25 fsize14 padT8">Available</h3>
				</div>
				<ul class="marT10">
					<li><a href="#"><img src="<spring:url value="/images/icon_addField.png" />" class="iconBtn"></a> dddddd <a href=""></a> </li>
					<li><a href="#"><img src="<spring:url value="/images/icon_addField.png" />" class="iconBtn"></a> adfasfasfasfasfsdf <a href=""></a></li>
					<li><a href="#"><img src="<spring:url value="/images/icon_addField.png" />" class="iconBtn"></a> rrrasfasfasfasdffs <a href=""> </a></li>
					<li><a href="#"><img src="<spring:url value="/images/icon_addField.png" />" class="iconBtn"></a> wfrwfsdfsfsd <a href=""></a></li>
					<li><a href="#"><img src="<spring:url value="/images/icon_addField.png" />" class="iconBtn"></a> saffsdf <a href=""></a></li>
				</ul>
				
			</div>
    		<div class="w50p floatL">
				<div class="colTop topGreen">
					<img src="<spring:url value="../css/theme/default/images/bluegreen_Arrow.png" />" class="posAbs">
					<h3 class="padL25 fsize14 padT8">Selected</h3>
				</div>
				<div style="overflow-y:scroll; max-height: 123px">
				<ul class="marT10">
					<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a> <input type="text" class="w30"> dddddd</li>
					<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a> <input type="text" class="w30"> adfasfasfasfasfsdf</li>
					<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a> <input type="text" class="w30"> rrrasfasfasfasdffs</li>
					<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a> <input type="text" class="w30"> wfrwfsdfsfsd</li>
					<li><a href="#"><img src="<spring:url value="/images/icon_delete2.png" />" class="delete"></a> <input type="text" class="w30"> saffsdf</li>
				</ul>				
			</div>
    	</div>
    	
    </div>
  </div>  
    
    
    <div class="clearB"></div>
</div> 

<!-- End Right Side -->	
<%@ include file="/WEB-INF/includes/footer.jsp" %>	