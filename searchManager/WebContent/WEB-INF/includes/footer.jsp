<!--  scroller -->
<script type="text/javascript" src="<spring:url value="/js/scroller/dom-drag.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/scroller/ypSimpleScrollC.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/scroller/scroller.js" />"></script>
<script type="text/javascript">
instantiateScroller(0, "scroll0", 7, 0, 220, 260, 260);
</script>

	<!--Footer-->
    <div class="clearB"></div>
    <div class="clearB floatL w980 padT20 padB25">
      <div class="farial fgray bgFooter" style="margin-bottom:40px">
      		<div class="floatL" style="width:600px">2555 West 190th Street - Torrance, CA 90504 - 800.555.6255</div>
      		<div class="floatL txtcopyright txtAR w360">&#169; 2011 PC Mall Sales, Inc.</div>
      </div>

    </div>
	</div>
  <div class="clearB"></div>
  
  <div class="dockFooter">
  <div class="footerFix">
  	<div style="width:1024px; margin: 0 auto; padding:5px 10px"> 
  		<div class="floatL w45p fsize12 padT7"><img src="<spring:url value="/js/ajaxsolr/images/user.png" />" class="marBn3 marR3"> Welcome <sec:authentication property="principal.username" /></span></div>
  		<div class="floatR w45p posRel" >			
  			<ul id="dockIcon">
  				<li id="Basket" class="basket">
  				<a href="javascript:void(0);">
  				<div style="position:absolute; background:#c40000; color:#fff; padding:1px; font-size:10px; font-family: arial; margin:1px; -webkit-border-radius: 2px; 
-moz-border-radius:2px; border-radius: 2px; top:-4px; shadow:1px 3px 3px 1px #333 ">134</div> 
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
				<!-- online 
					<div class="sideHeader posRel">
						<h2 class="dockTitle">Online </h2>
					</div>
					<div class="root" id="root0">
					<div class="scrollContainer" id="scroll0Container">
					<div class="scrollContent" id="scroll0Content">
					<div>
						<ul class="listSU fsize11">
							<li class="clearfix marT5"><img src="<spring:url value="/images/noAvatar.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px"><div class="user">admin</div> online 2 hours </p></li>
							<li class="clearfix marT5"><img src="<spring:url value="/images/noAvatar.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px"><div class="user">admin</div> online 2 hours </p></li>
							<li class="clearfix marT5"><img src="<spring:url value="/images/noAvatar.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px"><div class="user">admin</div> online 2 hours </p></li>
							<li class="clearfix marT5"><img src="<spring:url value="/images/noAvatar.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px"><div class="user">admin</div> online 2 hours </p></li>
							<li class="clearfix marT5"><img src="<spring:url value="/images/noAvatar.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px"><div class="user">admin</div> online 2 hours </p></li>
							<li class="clearfix marT5"><img src="<spring:url value="/images/noAvatar.jpg"/>" class="floatL marR8 marL5" width="45px" > <p class="breakWord floatL" style="width:143px"><div class="user">admin</div> online 2 hours </p></li>
						</ul>
					</div>
					</div>
					</div>
					</div> 							
				 end online -->	
							
				</div>
				
				<div class="infoContainer" id="dockNotification" style="display:none">
					<div id="notificationList"></div>
				</div>
				<div class="infoContainer" id="dockAlert" style="display:none">
				<!-- alerts -->
					<div class="sideHeader posRel">
						<h2 class="dockTitle">Alerts </h2>
					</div>
					<div class="root" id="root0">
					<div class="scrollContainer" id="scroll0Container">
					<div class="scrollContent" id="scroll0Content">
					<div>
						<ul class="listSU fsize11">
							<li><p class="dockAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dockAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dockAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dockAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dockAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
							<li><p class="dockAlert"><span class="user">admin</span> afasfsffasfasdfasdffasfsfsdfsdfdf</p></li>
						</ul>
					</div>
					</div>
					</div>
					</div> 							
				<!-- end alerts -->	
				</div>
	  		</div>
	  		</div>
  	</div>
  </div>
  </div> <!--  end dockFooter  -->
</div>
</body>
</html>