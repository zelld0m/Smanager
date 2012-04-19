<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="feature"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- jQuery functions --> 
<link type="text/css" href="<spring:url value="/css/bigbets/bigbets.css" />" rel="stylesheet">

<!-- DWR dependencies -->
<script type="text/javascript" src="<spring:url value="/dwr/interface/StoreKeywordServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/ElevateServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/bigbets/elevate.js" />"></script>

<!-- carousel -->
<script type="text/javascript" src="<spring:url value="/js/carousel/jquery.tinycarousel.min.js" />"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$('#slider1').tinycarousel();
	});
</script>
	
<!-- Left Menu-->
<div class="clearB floatL sideMenuArea">
<div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
	<!-- Keyword -->
	<div class="clearB floatL w240">
	 	<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
	</div>
</div>
<!--Left Menu-->
    
<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
	
	<div class="floatL w730 titlePlacer">
	  <div class="w535 padT10 padL10 floatL fsize20 fnormal">
		<span id="titleText">Feature List</span>
		<span id="keywordHeader" class="fLblue fnormal"></span>
	  </div>
	  <div id="addSortableHolder" class="floatL w180 txtAR padT7" style="display: none">
		<input id="addSortable" type="text" class="farial fsize12 fgray searchBox searchBoxIconLBg w85 marT1" maxlength="10" value="">
		<a href="javascript:void(0);" id="addSortableImg" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a>
	  </div>
	</div>	
		
	<div class="clearB"></div>
	
 	<!-- Product Item Description -->
    <div class="floatL itemInfo ">		     	       	
     	<p class="fsize14 fbold marB10"><img src="<spring:url value="/images/productItems/item1.jpg" />" class="floatL marTn8" style="width:100px; margin-right:10px">IBM BladeCenter H 8852 - Rack-mountable - 9U - power supply - hot-plug - stealth black - USB (1685589) Server <a href=""><span class="fsize11"> + read more</span></a></p>
     	<div class="clearB"></div>
     	<div class="borderT padB5 marT3"></div>
     	<label class="text">Manufacturer</label> <label class="info">IBM</label><br>
     	<label class="text">Part #.</label><label class="info">7871419</label><br>
     	<label class="text">Mfr. Part #.</label><label class="info">88861TU</label><br>
     	<label class="text">Price</label><label class="info"> $ 2899.99</label><br>
     	<div class="clearB"></div>
     	<div class="borderT padB5 marT3"></div>
     	<label class="text">Lorem ipsum</label><label class="info"> dolor amet</label><br>
     	<label class="text">Dolor sit amet</label><label class="info"> dolor amet</label><br>
     	<label class="text">Lorem sit amet</label><label class="info"> dolor amet</label><br>
     	<label class="text">Phasellus</label><label class="info"> Donec ultrices dolor at leo</label><br>
     	<label class="text marB10">Comment</label><label class="info"> In ultrices, risus ac volutpat blandit, dia nibh auctor enim, ut vehicula mi.</label><br>		      
     	<div class="clearB"></div>	      	
    </div>
		
	<!-- Schedule -->
	<div>	      
	    <div class="itemContainer">
			<div class="floatL fsize12 border" style="width:250px;  margin-left:40px">
	      		<h2 class="pad5" style="background:#eeeeee;">Schedule</h2>
	      		<div class="pad10">
	      		<p class="marB10 fsize11">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam vel dignissim enim. Fusce tristique, augue eget laoreet.</p>
	      		<label class="text">Repeat</label> 
	      		<label class="info">
	      		<select name="" class="mar0 w120">
	      			<option>Every Monday</option>
	      			<option>Lorem ipsum</option>
	      		</select>
	      		</label><br>
	      		<label class="text">Frequency</label> 
	      		<label class="info">
	      			<input type="text" class="w30"> <span class="fsize11 fgray">lorem ipsum dlor</span></input>
	      		</label><br>
	      		<label class="text">Category</label> 
	      		<label class="info"><select name="" class="mar0 w120">
	      			<option>select one</option>
	      			<option>Lorem ipsum</option>
	      		</select></label><br>
	      		<label class="text">Lorem</label> 
	      		<label class="info"><select name="" class="mar0 w120">
	      			<option>select one</option>
	      			<option>Lorem ipsum</option>
	      		</select></label><br>
	      		<label class="text">Valid Until</label> 
	      		<label class="info">
	      			<input type="text" class="w90"> <img src="<spring:url value="/images/icon_calendar.png" />" style="margin-bottom:-5px">
					<p class="fgreen fbold txtAL marT6"> 3 days left</p>
	      		</label><br>
	      		<p class="fgray padT5 fsize11">
	         		<img src="<spring:url value="/images/user_red.png" />" class="marBn4 marR3">
	         		<span id="sItemModBy" class="fbold"></span> on <span id="sItemModDate" class="fDblue"></span>
	        		</p>
	        		<p class="borderT marT5 padT5 txtAR">
	        			<img class="pointer" id="commentIcon" src="<spring:url value="/images/icon_comment.png" />" alt="Comment" title="Comment"> 
			        <img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History">
	        		</p>
	      	</div>
	   	</div>	         
	  </div>       
  </div>    
     
  <div class="clearB"></div>
  
  <div id="slider1" style="margin-left:30px">
  	   <div class="fsize11 fgray txtAR marL30 borderB marB10 pad5 w620">1 - 4 of 49</div>
   <a class="buttons prev" href="#">left</a>
   <div class="viewport">
       <ul class="overview">                   	
           <li>  
            	<div class="iconDel"><a id="sItemDelete" href="javascript:void(0);"><img src="<spring:url value="/images/btn_delete_graybg.jpg" />" class="noborder"></a></div>                     	                                                       
           		<div class="img"><a href="#"><img src="<spring:url value="/images/productItems/item1.jpg" />" style="z-index:1; " ></a></div>
           		<p class="fsize14 fbold txtAC">IBM</p>                            		
            	<p class="shortDesc">PC Mall <span class="fgreen">Part #:</span> 7871419</p>                                                                                                                         
           </li>                     
           <li> 
            	<div class="iconDel"><a id="sItemDelete" href="javascript:void(0);"><img src="<spring:url value="/images/btn_delete_graybg.jpg" />" class="noborder"></a></div>                     	                                                                     
            	<div class="img"><a href="#"><img src="<spring:url value="/images/productItems/item3.jpg" />"></a></div>
            	<p class="fsize14 fbold txtAC">Lenovo</p>                            		
                <p class="shortDesc">onSale <span class="fgreen">Part #:</span> 1234678</p>                                                                 
          </li>
       </ul>
      </div><!--  end viewport -->
      <a class="buttons next" href="#">right</a>
  </div>
  
</div>
       
<%@ include file="/WEB-INF/includes/footer.jsp" %>