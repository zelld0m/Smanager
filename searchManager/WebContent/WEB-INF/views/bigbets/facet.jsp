<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="bigbets" />
<c:set var="submenu" value="facet" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<!-- page specific dependencies -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/bigbets/facet.css" />">
<script type="text/javascript" src="<spring:url value="/js/bigbets/facet.js" />"></script>

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="clearB floatL w240">
		<div id="keywordSidePanel"></div>
		&nbsp;
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
	<div id="submitForApproval"></div>
	
	<div class="clearB"></div>

	<div id="facetsorting" class="facetsorting w700 border pad10 marT20 fsize12 verticalTabs">
		<!--  start horizontal tab -->
		<div>
			<img src="<spring:url value="/images/icon_keyword.png" />" class="marR5 posRel top3" />
			<span class="fLblue fbold">Computer</span>
				Categories Elevated : Computer, Keyboard, Cables, Mouse, Router
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

			<div id="category"></div>
			<!--  end categories content -->

			<div id="manufacturer"></div>
			<!--  end manufacturer content -->

			<div id="facetTabPattern" style="display: none">
				<div class="sort marT10 fgray">
					<ul>
						<li class="square"><a href="">All</a> &nbsp; |</li>
						<li>Sort Order</li>
						<li><select id="facetSortPattern" class="posRel topn3">
								<option>A-Z</option>
								<option>Z-A</option>
								<option>0-9</option>
								<option>9-0</option>
						</select></li>
					</ul>
				</div>
				<div class="clearB"></div>

				<div class="catValueTop floatL w47p">
					<p class="fbold">
						<span id="addFacetSortTitleHeader"></span>
					</p>
					<ul class="marT8">
						<li id="addFacetValuePattern" style="display: none">
							<label><img	src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
							<label>
								<select id="facetValuesPattern" class="w195">
									<option>Computer</option>
								</select>
							</label>
							<label class="marL5"><img src="<spring:url value="/images/btn_delete_big.png" />" class="posRel top3" /></label>
						</li>
					</ul>
					<div class="fsize11 txtAR">
						<a id="addNewFacetValue" href="javascript:void(0);"><span id="addNewLink"></span></a>
					</div>
				</div>
				<!-- end category value -->

				<div id="facetvaluelist"></div>
				<!--  end category values list -->
				<div class="clearB"></div>
			</div>
			<!--  end categories content -->

			<div class="marT10 txtAR">
				<a id="saveBtn" class="buttons btnGray clearfix" href="javascript:void(0);"><div class="buttons fontBold">Save</div></a>
				<a id="" class="buttons btnGray clearfix" href="javascript:void(0);" id=""><div class="buttons fontBold">Cancel</div></a>
			</div>
		</div>
	</div>
	<!--  end horizontal tab -->

	<div id="addFacetSortTemplate" style="display:none">
	  	<div class="w282 padT10 newFacetSort">
	  		<label class="floatL w80 txtLabel">Rule Type </label>
		   	<label class="floatL">
		    	<select name="select" id="popType" class="mar0 w191 floatR marT6" title="Select Rule Type" >
		     		<option value="keywordType">Keyword</option>
		     		<option value="templateNameType">Template Name</option>
		    	</select>
		    </label>
		   	<div class="clearB height8"></div>
		   	
		   	<div id="keywordinput">
	  		<label class="floatL w80 txtLabel">Name </label> 
			<label class="floatL"><input id="popName" type="text" class="w185" maxlength="100"></label>
			</div>
			<div class="clearB"></div>
			
			<div id="templatelist" style="display: none;">
		   		<span class="floatL w80 txtLabel">Template Name</span>
		   		<span class="floatL w190">
		   			<select name="select" id="popName" class="selectCombo mar0 w168 marT6" title="Select Template Name" ></select>
		   		</span>
		   	</div>
		   	<div class="clearB"></div>
		   	
		   	<label class="floatL w80 txtLabel">Sort Order</label>
		   	<label class="floatL">
		   		<select name="select" id="popSortOrder" class="mar0 w191 floatR marT6" title="Select Sort Order" ></select>
		   	</label>
			<div class="clearB"></div>
			
			<div class="txtAR pad3 marT10">
				<a id="addButton" href="javascript:void;" class="buttons btnGray clearfix"> <div class="buttons fontBold">Save</div> </a> 
				<a id="clearButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Clear</div> </a>
			</div> 
		</div>
	</div>

	<div class="clearB"></div>
</div>

<!-- End Right Side -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>
