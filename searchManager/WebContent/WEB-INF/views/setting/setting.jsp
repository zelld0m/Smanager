<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="setting"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!--  tabber side -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/tabberSide.css" />">
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/settings/settings.css" />">


 <!-- Start content tabs -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
    &nbsp;
</div>
<!--  end left side -->
<div class="clearB"></div>
<!--  start content tab -->
<div class="tabber mar0" style="margin-top:20px; width:98%">
        <div class="tabbertab">
        <h2 class="tabMenu"><span>Home</span></h2>

		Home 1 content
		</div>

		<div class="tabbertab">
		    <h2 class="tabMenu"><span>Search</span></h2>
		    Search 2 content
		</div>
		
		<div class="tabbertab">
		    <h2>Big Bets</h2>
		    Big Bets 3 content
	    </div>
	    
	    <div class="tabbertab">
		    <h2 class="tabMenu"><span>Search Ads</span></h2>
		    Search Ads 2 content
		</div>
		
		<div class="tabbertab">
		    <h2 class="tabMenu">Catalog</h2>
		    Catalog 3 content
	    </div>
	    
	    <div class="tabbertab">
		    <h2 class="tabMenu"><span>Statistics</span></h2>
		    Statistics 2 content
		</div>
		
		<div class="tabbertab">
		    <h2 class="tabMenu">Big Bets</h2>
		    Big Bets 3 content
	    </div>
	    
	    <div class="tabbertab">
		    <h2 class="tabMenu"><span>Lexicon</span></h2>
		    Lexicon Ads 2 content
		</div>
		
		<div class="tabbertab">
		    <h2 class="tabMenu">Settings</h2>
		    Settings 3 content
	    </div>
	    
	    <div class="tabbertab">
		    <h2 class="tabMenu">Date and Time</h2>
		    <h1 class="borderB fnormal pad5">Date and Time</h1>
		    
		    <div class="contentTab clearFix">
		    	<label class="text">Date Format	</label>
		    	<label class="info">
			    	<select class="w215">
			    		<option>YYYY-MM-DDThh:mmTZD</option>
			    		<option>MM-DD-YYYYThh:mmTZD</option>
			    	</select>
		    	</label><br>
		    	<label class="text">Sample Output</label>
		    	<label class="info"><input type="text"  class="w210"></label><br>
		    	
		    	<div class="clearB padT10"></div>
		    	<div class="txtAR borderT padT10">
			    	<a href="javascript:void(0);" id="addSortable" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a>
			    	<a href="javascript:void(0);" id="addSortable" class="buttons btnGray clearfix"><div class="buttons fontBold">Cancel</div></a>
	    		</div>
		    </div>
		    
	    </div>
	    
	    

</div><!--  end content tab -->
 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	