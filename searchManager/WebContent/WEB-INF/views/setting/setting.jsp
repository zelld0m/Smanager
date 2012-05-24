<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="setting"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

 <!-- Start content tabs -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
    &nbsp;
</div>
<!--  end left side -->
<div class="clearB"></div>


<!--  start content tab -->
<div class="tabSide mar0" style="margin-top:20px; width:98%"> 
        <div class="tabs">
        	<ul>
		        <li><a href="#home"><span>Home</span><span class="fsize11">Home 1 content</span></a></li>
		        <li><a href="#search"><span>Search</span><span class="fsize11">Search 2 content</span></a></li>
		        <li><a href="#bigBets"><span>Big Bets</span><span class="fsize11">Big Bets 3 content</span></a></li>
		        <li><a href="#searchAds"><span>Search Ads</span><span class="fsize11">Search Ads 2 content</span></a></li>
		        <li><a href="#catalog"><span>Catalog</span><span class="fsize11">Catalog 3 content</span></a></li>
		        <li><a href="#statistics"><span>Statistics</span><span class="fsize11">Statistics 2 content</span></a></li>
		        <li><a href="#lexicon"><span>Lexicon</span><span class="fsize11">Lexicon Ads 2 content</span></a></li>
		        <li><a href="#settings"><span>Settings</span><span class="fsize11">Settings 3 content</span></a></li>
		        <li><a href="#dateAndTime"><span>Date and Time</span><spkan class="fsize11"></span></a></li>
		    </ul>
		
		<div id="home">Home</div>
		<div id="search">Search</div>
		<div id="bigBets">Big Bets</div>
		<div id="searchAds">Search Ads</div>
		<div id="catalog">Catalog</div>
		<div id="statistics">Statistics</div>
		<div id="lexicon">Lexicon</div>
		<div id="settings">Settings</div>
		
	    <div id="dateAndTime">
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
		    
	    </div><!--  end page date and time -->
	    
	    </div>

</div><!--  end content tab -->
 
<%@ include file="/WEB-INF/includes/footer.jsp" %>	