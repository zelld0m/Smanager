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
      <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
      	<span id="titleText"></span>
		<span id="titleHeader" class="fLblue fnormal"></span>
		</div>
    </div>
    <div class="clearB"></div>
    
    <div id="facetsorting" class="facetsorting w700 border pad10 marT20 fsize12 verticalTabs"><!--  start horizontal tab -->
    <div>
    	<img src="<spring:url value="/images/icon_keyword.png" />" class="marR5 posRel top3" /><span class="fLblue fbold">Computer</span> Categories Elevated : Computer, Keyboard, Cables, Mouse, Router
    	<a href=""><img src="/searchManager/images/icon_delete2.png" class="posRel floatR deleteRuleItemIcon pointer"></a>
    </div>
    <div class="clearB"></div>
    
    <div class="sort marT10 fgray">
    	<ul>
    		<li class="square"><a href="">All</a> &nbsp; |</li>
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
			<li><a href="#category"><span>Categories</span></a></li>
			<li><a href="#manufacturer"><span>Manufacturer</span></a></li>
		</ul>
		
		<div id="category"></div><!--  end categories content -->
		
		<div id="manufacturer"></div><!--  end manufacturer content -->
		
		<div id="facetTabPattern" style="display: none">
			 <div class="sort marT10 fgray">
		    	<ul>
		    		<li class="square"><a href="">All</a> &nbsp; |</li>
		    		<li>Sort Order</li>
		    		<li>
		    			<select class="posRel topn3">
		    				<option>A-Z</option>
		    			</select>
		    		</li>
		    	</ul>
    		</div>
    		<div class="clearB"></div>
			
			<div class="catValueTop floatL w47p">
				<p class="fbold"><span id="addFacetSortTitleHeader"></span></p>
				<ul class="marT8">
					<li id="addFacetValuePattern" style="display: none">
						<label><img src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
						<label>
							<select class="w195">
								<option>Computer</option>								
							</select>
						</label>
						<label class="marL5"><img src="<spring:url value="/images/btn_delete_big.png" />" class="posRel top3" /></label>
					</li>
				</ul>
				<div class="fsize11 txtAR"><a id="addNewFacetValue" href="javascript:void(0);"><span id="addNewLink">[ add new category value ]</span></a></div>
			</div><!-- end category value -->
			
			<div id="facetvaluelist"></div>
			<!--  end category values list -->
			<div class="clearB"></div>
		</div><!--  end categories content -->
		
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