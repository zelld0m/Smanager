<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="facet"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/bigbets/facet.css" />">
<script type="text/javascript" src="<spring:url value="/js/bigbets/facet.js" />"></script>
  
<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
 	<div class="clearB floatL w240"> 
    	<div id="keywordSidePanel"></div> &nbsp;
        <div class="clearB"></div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w730 marL10 marT27">
    <div class="floatL w730 titlePlacer">
      <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">Facet Sorting </div>
    </div>
    <div class="clearB"></div>
    
    <div class="w700 border pad10 marT20 fsize12 verticalTabs"><!--  start horizontal tab -->
    <div>
    	<img src="<spring:url value="/images/icon_keyword.png" />" class="marR5 posRel top3" /><span class="fLblue fbold">Computer</span> Categories Elevated : Computer, Keyboard, Cables, Mouse, Router
    	<a href=""><img src="/searchManager/images/icon_delete2.png" class="posRel floatR deleteRuleItemIcon pointer"></a>
    </div>
    <div class="clearB"></div>
    
    <div class="sort marT10">
    	<ul>
    		<li><a href="">All</a> &nbsp; |</li>
    		<li>Sort Order</li>
    		<li>
    			<select class="posRel topn3">
    				<option>A-Z</option>
    			</select>
    		</li>
    	</ul>
    </div>
    <div class="clearB"></div>
    
    <div id="facetsort" class="tabvs marT10">
		<ul>
			<li><a href="#categories"><span>Categories</span></a></li>
			<li><a href="#manufacturer"><span>Manufacturer</span></a></li>
		</ul>
		
		<div id="categories">
			<div class="catValueTop floatL w38p">
				<p class="fbold">Elevated Category Values</p>
				<ul>
					<li>
						<label><img src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
						<label>
							<select class="w180">
								<option>Computer</option>								
							</select>
						</label>
						<label><img src="<spring:url value="/images/iconDelete.png" />" class="posRel top3" /></label>
					</li>
					<li>
						<label><img src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
						<label>
							<select class="w180">
								<option>Keyboard</option>								
							</select>
						</label>
						<label><img src="<spring:url value="/images/iconDelete.png" />" class="posRel top3" /></label>
					</li>
					<li>
						<label><img src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
						<label>
							<select class="w180">
								<option>Cables</option>								
							</select>
						</label>
						<label><img src="<spring:url value="/images/iconDelete.png" />" class="posRel top3" /></label>
					</li>					
					<li>
						<label><img src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
						<label>
							<select class="w180">
								<option>CAT3</option>								
							</select>
						</label>
						<label><img src="<spring:url value="/images/iconDelete.png" />" class="posRel top3" /></label>
					</li>
					<li>
						<label><img src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
						<label>
							<select class="w180">
								<option>Mouse</option>								
							</select>
						</label>
						<label><img src="<spring:url value="/images/iconDelete.png" />" class="posRel top3" /></label>
					</li>
				</ul>
				<div class="fsize11 txtAR"><a href="">[ add new category value ]</a></div>
			</div><!-- end category value -->
			
			<div class="catValueList floatL w60p">
				<p class="fbold">Select  Category Values</p>
				<div class="searchBoxHolder w150 marT1 marR8">
					<input type="text" class="farial fsize12 fgray pad3 w145" id="keyword" name="keyword">
				</div>
				<div class="clearB"></div>
				<div style="overflow-y:auto; height: 200px">
					<ul>
						<li>Computer <span class="fLblue">(5)</span> <span class="fsize10 fLgray">elevate</span> </li>
						<li>Keyboard <span class="fLblue">(6)</span> </li>
						<li>Cables <span class="fLblue">(33)</span> </li>
						<li>CAT3 <span class="fLblue">(25)</span> <span class="fsize10 fLgray">elevate</span></li>
						<li>Mouse <span class="fLblue">(4)</span> </li>
						<li>Router <span class="fLblue">(6)</span> <span class="fsize10 fLgray">elevate</span></li>
						<li>Keyboard <span class="fLblue">(10)</span> </li>
						<li>Cables <span class="fLblue">(24)</span> </li>
						<li>CAT3 <span class="fLblue">(1)</span> </li>
						<li>Mouse <span class="fLblue">(3)</span> <span class="fsize10 fLgray">elevate</span></li>
						<li>Router <span class="fLblue">(6)</span> <span class="fsize10 fLgray">elevate</span></li>
						<li>Keyboard <span class="fLblue">(10)</span> </li>
						<li>Cables <span class="fLblue">(24)</span> </li>
						<li>CAT3 <span class="fLblue">(1)</span> </li>
						<li>Mouse <span class="fLblue">(3)</span> <span class="fsize10 fLgray">elevate</span></li>
					</ul>
				</div>
			</div><!--  end category values list -->
			<div class="clearB"></div>
		</div><!--  end categories content -->
		
		<div id="manufacturer">
			manufacturer content
		</div><!--  end manufacturer content -->
		
		<div class="marT10 txtAR">
			<a id="saveBtn" class="buttons btnGray clearfix" href="javascript:void(0);"><div class="buttons fontBold">Save</div></a>
			<a id="" class="buttons btnGray clearfix" href="javascript:void(0);" id=""><div class="buttons fontBold">Cancel</div></a>
		</div>
	</div>
</div><!--  end horizontal tab -->
    
    
    
    	
 
    
    
    <div class="clearB"></div>
</div> 

<!-- End Right Side -->	
<%@ include file="/WEB-INF/includes/footer.jsp" %>	