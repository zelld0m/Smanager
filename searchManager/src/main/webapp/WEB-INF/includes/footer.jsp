<!--  scroller 
<script type="text/javascript" src="<spring:url value="/js/scroller/dom-drag.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/scroller/ypSimpleScrollC.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/scroller/scroller.js" />"></script>
<script type="text/javascript">
instantiateScroller(0, "scroll0", 7, 0, 220, 260, 260);
</script>-->


	<!--Footer-->
    <div class="clearB"></div>
    <div class="clearB floatL w980 padT20 padB25">
      <div class="farial fgray bgFooter" style="margin-bottom:40px">
      		<div class="floatL" style="width:600px">1940 E. Mariposa Ave, El Segundo, CA 90245 - 800.555.6255</div>
      		<div class="floatL txtcopyright txtAR w360">&#169; <script> document.write(new Date().getFullYear()); document.write(' ' + GLOBAL_storeName);</script> Sales, Inc.</div>
      </div>

    </div>
	</div>
  <div class="clearB"></div>
  
  <div class="dockFooter">
  <div class="footerFix">
  	<div style="width:1024px; margin: 0 auto; padding:5px 10px"> 
  		<div class="floatL w30p fsize12 padT7">
  			<img src="<spring:url value="/js/jquery/ajaxsolr.custom/images/user.png" />" class="marBn3 marR3"> 
  			Welcome <sec:authentication property="principal.username" />
  		</div>
  		<div  class="floatL w50p fsize12 padT7">
  			<img src="<spring:url value="/images/timezone.png" />" class="marBn3 marR3"> 
  			${timeZoneId}
  		</div>
  		<div class="floatR w20p posRel" >			
  			<ul id="dockIcon">
  				<li id="Basket" class="basket">
  				<a href="javascript:void(0);">
  				<div class="noticeBox" style=" ">134</div> 
				</a>
				</li>
  				<li id="Online" class="online"><a href="javascript:void(0);"></a></li>
  				<li id="Notification" class="notifications"><a href="javascript:void(0);"></a></li>
  				<li id="Alert" class="alertsFF"><a href="javascript:void(0);"></a></li>
  			</ul>
	  		<div id="dockItem">
				<div class="infoContainer" id="dockBasket" style="display:none">
					<!-- basket
						<div id="" class="sideHeader posRel">
							<h2 class="dockTitle">Basket </h2>
						</div>
						<div class="root" id="root0">
						<div class="scrollContainer" id="scroll0Container">
						<div class="scrollContent" id="scroll0Content">
						<div>
							<ul class="listSU fsize11">
								<li class="clearfix marT8"><img src="<spring:url value="/images/productItems/item.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px">afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
								<li class="clearfix marT8"><img src="<spring:url value="/images/productItems/item.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px">afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
								<li class="clearfix marT8"><img src="<spring:url value="/images/productItems/item.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px">afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
								<li class="clearfix marT8"><img src="<spring:url value="/images/productItems/item.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px">afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
								<li class="clearfix marT8"><img src="<spring:url value="/images/productItems/item.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px">afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
								<li class="clearfix marT8"><img src="<spring:url value="/images/productItems/item.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px">afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							</ul>
						</div>
						</div>
						</div>
						</div> 					
					end basket -->
				</div>
				
				<div class="infoContainer" id="dockOnline" style="display:none">
					<div id="onlineList"></div>			
				</div>
				
				<div class="infoContainer" id="dockNotification" style="display:none">
					<div id="notificationList"></div>
				</div>
				<div class="infoContainer" id="dockAlert" style="display:none">
				<!-- alerts --
					<div class="sideHeader posRel">
						<h2 class="dockTitle">Alerts </h2>
					</div>
					<div class="root" id="root0">
					<div class="scrollContainer" id="scroll0Container">
					<div class="scrollContent" id="scroll0Content">
					<div>
						<ul class="listSU fsize11">
							<li><p class="dAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
						</ul>
					</div>
					</div>
					</div>
					</div> 							
				 end alerts -->	
				</div>
	  		</div>
	  		</div>
  	</div>
  </div>
  </div> <!--  end dockFooter  -->
</div>
</body>
</html>