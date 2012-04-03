<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="bigbets"/>
<c:set var="submenu" value="redirect"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- tabber -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/tabber.css" />">

<!-- page specific dependencies -->
<link href="<spring:url value="/css/redirect/redirect.css" />" rel="stylesheet" type="text/css">

<!-- DWR dependencies -->
<script type="text/javascript" src="<spring:url value="/dwr/interface/CategoryServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/RedirectServiceJS.js"/>"></script>

<script type="text/javascript" src="<spring:url value="/js/bigbets/redirect.js" />"></script>   

<script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/jquery.ui.combobox.js" />" ></script>

<!--Left Menu-->
    <div class="clearB floatL sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
    
    <div class="clearB floatL w240">
    	<div id="redirectSidePanel"></div>
        <div class="clearB"></div>
    </div>

    </div>
    <!--Left Menu-->


<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
        <div class="w535 padT10 padL10 floatL fsize20 fnormal">Query Cleaning Rule : <span id="headerRuleName" class="fLblue fnormal">Rule Name</span></div>
   </div>
     
	<div class="clearB"></div>
	
    <!-- content tabs -->
    <div class="titleBlue borderB marRL5">Configuration</div>
			<table class="fsize12 mar10 tblCatalog">
				<tr>
					<td width="250px"><input id="ruleId" type="hidden" value="">
						<input id="ruleName" type="hidden" value="Rule Name">
						<p/><input id="searchTerm" type="text" value="Add Search Term" class="farial fsize12  fgray padLR3 padTB4 marL10 w220">
					</td>
					<td width="25px">
						<!-- a href="#" class="buttons btnGray clearfix"><div id="editSearchTerm" class="buttons fontBold"><<</div></a><br /--> 
						<a href="#" class="buttons btnGray clearfix"><div id="addSearchTerm" class="buttons fontBold">>></div></a></td>
					<td>
						<select id="searchTermList" class="farial fsize12  fgray padLR3 padTB4 marL10 w220" size="7"/>
					</td>
				</tr>
			</table>
    <div class="tabber mar0" style="margin-top:20px; width:98%" id="maintab">
		<div class="tabbertab">
        <h2 class="tabMenu"><span>Category/Manufacturer</span></h2>
			<table class="fsize12 mar10 tblCatalog">
				<tr>
					<td>
						<table class="fsize12 mar10 tblCatalog">
							<tr>
								<td width="100px">CatCode</td>
								<td width="250px"><input id="catcodetext" type="text" value="" class="farial fsize12  fgray padLR3 padTB4 marL10" size="10" maxlength="4"></td>
							</tr>
							<tr>
								<td width="100px">Category</td>
								<td width="250px"><select name="select" id="categoryList"
									class="farial fsize12 fgray padLR3 padTB4 marL10 w230">
										<option>All Categories</option>
								</select></td>
							</tr>
							<tr>
								<td>Sub-Category</td>
								<td><select name="select" id="subCategoryList"
									class="farial fsize12 fgray padLR3 padTB4 marL10 w230">
										<option></option>
								</select></td>
							</tr>
							<tr>
								<td>Class</td>
								<td><select name="select" id="classList"
									class="farial fsize12 fgray padLR3 padTB4 marL10 w230">
										<option></option>
								</select></td>
							</tr>
							<tr>
								<td>Minor</td>
								<td><select name="select" id="minorList"
									class="farial fsize12 fgray padLR3 padTB4 marL10 w230">
										<option></option>
								</select></td>
							</tr>
							<tr>
								<td>Manufacturer List</td>
								<td><select name="select" id="manufacturerList"
									class="farial fsize12 fgray padLR3 padTB4 marL10 w230">
										<option>All Manufacturers</option>
								</select></td>
							</tr>
						</table>
					</td>
					<td><a href="#" class="buttons btnGray clearfix"><div
								class="buttons fontBold" id="addRule">>></div></a><br />
					<td>
						<select id="ruleList" class="farial fsize12  fgray padLR3 padTB4 marL10 w220" size="10">
						</select>
					</td>
				</tr>
			</table>
		</div>
		<!--Redirect to page commented out-->
		<!-- div class="tabbertab">
			<h2>Redirect to Page</h2>


			<div>
				<table width="98%" class="fsize12 tblCatalog">
					<tr>
						<td width="50px">URL:</td>
						<td><input id="url" type="text" value="http://"
							class="farial fsize12 fgray padLR3 padTB4 border marL10 w400" /></td>
					</tr>
				</table>
			</div>


		</div-->
		<input id="url" type="hidden"/>
		<div align="right" class="padR5">
			    <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="save">&nbsp;Save&nbsp;</div></a>
				<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold" id="delete">Delete</div></a>
			</div>
   
	</div>
    <!-- end content tabs -->

<div class="clearBce"></div>
</div>
<!--End Right Side-->
   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	