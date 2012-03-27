<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="redirect"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- tabber -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/tabber.css" />">

<!-- page specific dependencies -->
<link href="<spring:url value="/css/redirect/redirect.css" />" rel="stylesheet" type="text/css">

<!-- DWR dependencies -->
<script type="text/javascript" src="<spring:url value="/dwr/interface/CategoryServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/RedirectServiceJS.js"/>"></script>

<script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/jquery.ui.combobox.js" />" ></script>

<script type="text/javascript" src="<spring:url value="/js/bigbets/redirect.js" />"></script>   

<!--Left Menu-->
    <div class="clearB floatL sideMenuArea">
    <div class="pcmallhdlogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
      <!-- Keyword -->
      <div class="clearB floatL w240">
        <div class="sideHeader">Redirect Rules</div>
        <div class="clearB floatL w230 padL10">
          
          <!--Start Keyword Listing-->
          <table>
     		<tbody id="keywordBody">
     			<tr id="kDispPattern" style="display:none;"><td>
		          <div class="keywordHolder farial fsize12 fDGray w220 borderB padTB4 clearfix">
		          	<div class="keywordText floatL lnk w160"><a href="javascript:void(0);"></a></div>
		          </div>     			
     			</td></tr>
     		</tbody>
          </table>
          <!--Start Keyword Listing-->
          
        </div>
        <div class="clearB"></div>
        <div id="keywordPagingResult" class="keywordPagingResult marT10"></div>
      </div>
    </div>
    <!--Left Menu-->


<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal">Redirect Rule : <span id="headerRuleName" class="fLblue fnormal">Rule Name</span></div>
        <div class="floatL w180 txtAR padT7"><input id="addRuleName" type="text" class="farial fsize12 fgray  padLR3 padTB4 border marL10 w110 " maxlength="30" value=""><a href="javascript:void(0);" id="add" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> </div>
	</div>
     
	<div class="clearB"></div>
	
    <!-- content tabs -->
    <div class="tabber mar0" style="margin-top:20px; width:98%">
		<div class="tabbertab">
        <h2 class="tabMenu"><span>Redirect Rule Configuration</span></h2>
			<table class="fsize12 mar10 tblCatalog">
				<tr>
					<td width="250px"><input id="ruleId" type="hidden" value="">
						<input id="ruleName" type="hidden" value="Rule Name">
						<p/><input id="searchTerm" type="text" value="Add Search Term" class="farial fsize12  fgray padLR3 padTB4 border marL10 w220">
					</td>
					<td width="25px">
						<!-- a href="#" class="buttons btnGray clearfix"><div id="editSearchTerm" class="buttons fontBold"><<</div></a><br /--> 
						<a href="#" class="buttons btnGray clearfix"><div id="addSearchTerm" class="buttons fontBold">>></div></a></td>
					<td>
						<select id="searchTermList" class="farial fsize12  fgray padLR3 padTB4 border marL10 w220" size="7"/>
					</td>
				</tr>
			</table>

			<div class="round3 marB10" style="padding:1px; background:#c7c7c7" >
         	<div class="contentTitle round3 fsize12 fbold pad5">Select Category / Manufacturer</div>
        </div>
			<table class="fsize12 mar10 tblCatalog">
				<tr>
					<td>
						<table class="fsize12 mar10 tblCatalog">
							<tr>
								<td width="100px">CatCode</td>
								<td width="250px"><input id="catcodetb" type="text" value="" class="farial fsize12  fgray padLR3 padTB4 border marL10" size="10" maxlength="4"></td>
							</tr>
							<tr>
								<td width="100px">Category</td>
								<td width="250px"><select name="select" id="categoryList"
									class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
										<option>All Categories</option>
								</select></td>
							</tr>
							<tr>
								<td>Sub-Category</td>
								<td><select name="select" id="subCategoryList"
									class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
										<option></option>
								</select></td>
							</tr>
							<tr>
								<td>Class</td>
								<td><select name="select" id="classList"
									class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
										<option></option>
								</select></td>
							</tr>
							<tr>
								<td>Minor</td>
								<td><select name="select" id="minorList"
									class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
										<option></option>
								</select></td>
							</tr>
							<tr>
								<td>Manufacturer List</td>
								<td><select name="select" id="manufacturerList"
									class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
										<option>All Manufacturers</option>
								</select></td>
							</tr>
						</table>
					</td>
					<td><a href="#" class="buttons btnGray clearfix"><div
								class="buttons fontBold" id="addRule">>></div></a><br />
						 <!-- a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="editRule"><<</div></a></td-->
					<td>
						<select id="ruleList" class="farial fsize12  fgray padLR3 padTB4 border marL10 w220" size="10">
						</select>
					</td>
				</tr>
			</table>
			<div align="right" class="padR5">
			    <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="save">&nbsp;Save&nbsp;</div></a>
				<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="delete">Delete</div></a>
			</div>

		</div>
   
	</div>
    <!-- end content tabs -->

<div class="clearBce"></div>
</div>
<!--End Right Side-->
   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	