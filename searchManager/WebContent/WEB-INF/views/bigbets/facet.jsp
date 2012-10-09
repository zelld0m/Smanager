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
			<img id="ruleTypeIcon" src="" class="ruleTypeIcon marR5 posRel top3" />
			<span id="titleText"></span>
			<span id="titleHeader" class="fLblue fnormal"></span>
		</div>
	</div>
	<div class="clearB"></div>
	<div id="submitForApproval"></div>
	
	<div class="clearB"></div>
	<div id="preloader" class="circlePreloader" style="display:none"><img src="<spring:url value="/images/ajax-loader-circ.gif" />"></div>
	<div id="facetsorting" class="facetsorting w700 border pad10 marT20 fsize12 verticalTabs" style="display:none">
		<!--  start horizontal tab -->
		<div>
			<div id="readableString"></div>
			<div class="floatR">
				<span class="floatR"><a href="javascript:void(0);" id="downloadIcon"><div class="btnGraph btnDownload marT1 marL3" id="downloadIcon" alt="Download" title="Download"></div></a></span>
				<span class="floatR"><img class="pointer" id="auditIcon" src="<spring:url value="/images/icon_history.png" />" alt="History" title="History"></span>
			</div>
		</div>
		<div class="clearB"></div>

		<div class="sort marT10 fgray">
			<ul >
				<li class="square"><a href="">All</a> &nbsp; |</li>
				<li>Sort Order</li>
				<li>
					<select id="facetSortOrder" class="posRel topn3"></select>
				</li>
			</ul>
		</div>
		<div class="clearB"></div>

		<div id="facetsort" class="tabvs marT10">
			<ul id="facetGroupTab">
				<li class="facetGroupTabPattern" style="display:none;"><a href="#"><span class="facetGroupName"></span></a></li>
			</ul>

			<div class="facetTabPattern contentWrapper" style="display:none;">
				<div class="sort marT10 fgray">
					<ul>
						<li><input id="facetGroupCheckbox" type="checkbox"/></li>
						<li>Sort Order</li>
						<li><select class="facetGroupSortOrder posRel topn3"></select></li>
					</ul>
				</div>
				<div class="clearB"></div>
				
				<div id="facetvaluelist"></div>
				
				<div class="catValueTop floatL w47p">
					<p class="fbold">
						<span id="addFacetSortTitleHeader"></span>
					</p>
					<div>
						<ul id="selectedFacetValueList" class="marT8">
							<li id="addFacetValuePattern" class="addFacetValuePattern handle" style="display: none;">
								<label class="floatL"><img	src="<spring:url value="/images/icon_move.png" />" class="posRel top3" /></label>
								<label class="floatL marL5">
									<div class="w214">
									<select id="facetValuesPattern" class="selectCombo mar0 w185 marT6">
										<option value="all"></option>
									</select>
									</div>
								</label>
								<label class="floatL marL0"><img class="delFacetValueIcon" src="<spring:url value="/images/btn_delete_big.png" />" class="posRel top3" /></label>
							</li>
						</ul>
					</div>
					<div class="clearB"></div>
					<div class="fsize11 txtAR">
						<a id="addNewFacetValue" href="javascript:void(0);"><span id="addNewLink"></span></a>
					</div>
				</div>
				<!-- end category value -->

				
				<!--  end category values list -->
				<div class="clearB"></div>
			</div>
			<!--  end categories content -->
			
			<div class="marT10 txtAR">
				<a id="saveBtn" class="buttons btnGray clearfix" href="javascript:void(0);"><div class="buttons fontBold">Save</div></a>
				<a id="deleteBtn" class="buttons btnGray clearfix" href="javascript:void(0);" id=""><div class="buttons fontBold">Delete</div></a>
			</div>
		</div>
	</div>
	<!--  end horizontal tab -->

	<div id="addFacetSortTemplate" style="display:none">
	  	<div class="w282 padT10 newFacetSort">
	  		<label class="floatL w80 txtLabel">Rule Type </label>
		   	<label class="floatL">
		    	<select name="select" id="popType" class="selectCombo mar0 w168 floatR marT6" title="Select Rule Type" >
		     		<option value="Keyword">Keyword</option>
		     		<option value="Template">Template</option>
		    	</select>
		    </label>
		   	<div class="clearB height8"></div>
		   	
		   	<div id="keywordinput">
	  		<label class="floatL w80 txtLabel">Keyword </label> 
			<label class="floatL"><input id="popKeywordName" type="text" class="w188" maxlength="100"></label>
			</div>
			<div class="clearB"></div>
			
			<div id="templatelist" style="display: none;">
		   		<label class="floatL w80 txtLabel">Template Name</label>
		   		<label class="floatL w195">
		   			<select name="select" id="popName" class="selectCombo mar0 w168 marT6" title="Select Template Name" ></select>
		   		</label>
		   	</div>
		   	<div class="clearB"></div>
		   	
		   	<label class="floatL w80 txtLabel marT8">Sort Order</label>
		   	<label class="floatL marT8">
		   		<select name="select" id="popSortOrder" class="selectCombo mar0 w168 floatR marT6" title="Select Sort Order" ></select>
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
