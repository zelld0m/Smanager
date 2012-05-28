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
        <div id="user-pref" class="tabs">
        	<ul>
		        <li><a href="#home"><span>Profile</span></a></li>
		        <li><a href="#search"><span>Search</span> <span class="fsize11"></span></a></li>
		        <li><a href="#bigBets"><span>Big Bets</span> <span class="fsize11"></span></a></li>
		        <li><a href="#searchAds"><span>Search Ads</span><span class="fsize11"></span></a></li>
		        <li><a href="#catalog"><span>Catalog</span><span class="fsize11"></span></a></li>
		        <li><a href="#statistics"><span>Statistics</span><span class="fsize11"></span></a></li>
		        <li><a href="#lexicon"><span>Lexicon</span><span class="fsize11"></span></a></li>
		        <li><a href="#settings"><span>Settings</span><span class="fsize11"></span></a></li>
		        <li><a href="#dateAndTime"><span>Date and Time</span><spkan class="fsize11"></span></a></li>
		    </ul>
		
		<div id="home" class="txtAL padL0 marT0">
			<h2 class="txtAL marT10 padL10 borderB">Profile</h2>
			<table class="fsize12 marT20 marL20">
				<tr>
					<td><img src="../images/uploadImage.jpg" class="border marR10" /></td>
					<td class="valignTop">
						<label class="floatL w70">Username :</label><label class="w135 padL5 fbold"><input type="text" id="aduser" class="w135"></label><div class="clearB"></div>
						<label class="floatL w70 marT5">Fullname :</label><label class="w135 padL5 fbold floatL marT5"><input type="text" id="adfull" class="w135"></label>
					</td>
				</tr>
				<tr>
					<td>Email :</td>
					<td><input type="text" class="w210" id="ademail"></td>
				</tr>
				<tr>
					<td></td>
					<td class="padT5"><label><input type="checkbox" id="adlck" class="posRel top2">Locked</label> <label class="marL10">Expired</label><input type="text" id="adexp" class="w70 adexp marL5"></td>
				</tr>
				</table>
				
				<table class="fsize12 marT10 marL20">
				<tr class="borderT">
					<td colspan="2"><h2 class="padT5"> Change Password </h2></td>
				</tr>
				<tr>
					<td width="130px">Old Password</td>
					<td><input type="password" id="" class="w150"></td>
				</tr>
				<tr>
					<td>New Password</td>
					<td><input type="password" id="" class="w150"></td>
				</tr>
				<tr>
					<td>Re-Type Password</td>
					<td><input type="password" id="" class="w150"></td>
				</tr>
				<tr>
					<td colspan="2" class="txtAR padT10">
	      				<a id="" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Update</div></a>
		      		</td>
				</tr>
			</table>
		</div>
		
		<div id="search">Search	</div>
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