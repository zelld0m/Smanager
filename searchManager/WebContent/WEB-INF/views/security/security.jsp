<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="security"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/security/security.js" />"></script> 

<!--Start Left Side-->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>

	<div class="clearB floatL w240">
    	<div id="sideHeader" class="sideHeader posRel">
    		<img src="../images/corner_tl.png" class="curveTL"/>
    		<img src="../images/corner_tr.png" class="curveTR"/>
			Role
			<img src="../images/corner_bl.png" class="curveBL"/>
			<img src="../images/corner_br.png" class="curveBR"/>
    	</div>
        <div class="clearB floatL w230 padL5">
			<ul class="listRole fsize12 marT10 rolUl">
			</ul>
    	</div> 

    	<div class="clearB"></div>
    	<div id="sideHeader" class="sideHeader posRel">
    		<img src="../images/corner_tl.png" class="curveTL"/>
    		<img src="../images/corner_tr.png" class="curveTR"/>
			Refine Search
			<img src="../images/corner_bl.png" class="curveBL"/>
			<img src="../images/corner_br.png" class="curveBR"/>
    	</div>
        <div class="clearB floatL w230 padL5 padT10 fsize12">
        	<div class="w200 searchBoxHolder floatL">
       			<input type="text" name="refsrc" id="refsrc" class="farial fsize12 fgray w99p">
       		</div>
       		<div class="clearB"></div>     		
       		<label class="marT10 floatL w100p">Member</label> 
       		<label class="marT3 floatL w55p"><input type="text" class="w90" id="refmem" name="refmem"></label>
       		<label class="marT8 floatL w100p">Locked</label> 
       		<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refstat" style="cursor:pointer">
				</select>
			</label>
			<label class="marT8 floatL w100p">Expired</label> 
			<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refexp" style="cursor:pointer">
				</select>
			</label>
			<div class="clearB"></div>
			<div align="right" class="txtAR marT10 padT5"> 	        		
	        	<a id="refFilBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Filter</div></a>
	        </div>
			
    	</div> 
	</div>
</div>
<!--End Left Side-->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord rolH">
	  </div>
	 <div id="addItemHolder" class="floatL w180 txtAR padT7">
       	<div class="floatL w185 txtAR padT4">   		
			<a href="javascript:void(0);" id="addUserBtn" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> 
		</div>
	</div>		
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
   <div class="clearB"></div>
    <!--Pagination-->
      <div id="sortablePagingTop" class="floatL txtAL w99p"></div>
      <!--Pagination-->
     <div id="preloader" style="display:none;"><img src="../images/ajax-loader-circ.gif" style="position:absolute; top:300px; left: 750px"></div>
    	<table width="100%" class="fsize12 tblAlpha conTable">
    		<tr class="conTr1">
    			<th width="7%"></th>
    			<th width="35%">Username</th>
    			<th width="10%">Locked</th>
    			<th width="10%">Expired</th>
    			<th width="15%">Member Since</th>
    			<th width="17%">Last Access</th>
    		</tr>
    	</table>   

      <div id="sortablePagingBottom" class="w99p floatL txtAL"></div>
      <!--Pagination--> 	
	</div>

	<div class="clearB"></div>
	
	<div id="userInfoTemplate" style="display:none">
		<div class="w300 security fsize11">
			<div class="w80 floatL txtAC"><img src="<spring:url value="/images/noAvatar.jpg" />" class="border"></div>
			<div class="w200 floatL">
			            <label class="floatL w70">Username :</label><label class="w120 floatL padL5 fbold shuser"></label><div class="clearB"></div>
			            <label class="floatL w70">Fullname :</label><label class="w120 floatL padL5 fbold shfname"></label>
			      </div>
			      <div class="clearB"></div>
			      <div class="marB10">
			            <label class="floatL w80 marL10">Last Access :</label><label class="floatL shlacss"></label><div class="clearB"></div>
			            <label class="floatL w80 marL10">IP Address :</label><label class="floatL ship"></label><div class="clearB"></div>
			            <label class="floatL w80 marL10">Email</label><label><input type="text" class="w200" id="shemail"></label>
			            <div class="clearB marT10"></div>
			            <label class="floatL w80 marL10"></label><label><input type="checkbox" id="shlck"> Locked</label>
			            <label class="marL10">Expired <input type="text" id="shexp" class="w70"> </label>
			            <div class="txtAR marT8">
			            	<a id="shsv" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">save</div></a>
			            </div>
			     </div>
			                        
			      <div class="borderT">
			            <h2 class="padT5"> Change Password </h2>
			            <label class="floatL" style="width:150px"><input type="password" id="shpass"></label> <label class="floatL marT0 padT0"> <div class="marT4"><a id="resetBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Reset Password</div></a></div> </label>
			            <div class="clearB"></div>
			      </div>
		</div>      
	</div>
	
	<div id="addUserInfoTemplate" style="display:none">
		<div class="w300 security fsize11">
			<div class="w80 floatL txtAC"><img src="../images/uploadImage.jpg" class="border" /></div>
			<div class="w200 floatL">
			            <label class="floatL w70">Username :</label><label class="w120 floatL padL5 fbold"><input type="text" id="aduser"></label><div class="clearB"></div>
			            <label class="floatL w70">Fullname :</label><label class="w120 floatL padL5 fbold"><input type="text" id="adfull"></label>
			      </div>
			      <div class="clearB"></div>
			      <div class="marB10">
			            <div class="clearB marT5"></div>
			            <label class="floatL w80 marL10 marB5">Email :</label><label><input type="text" class="w200" id="ademail"></label>
			            <div class="clearB marT5"></div>
			            <label class="floatL w80 marL10"> </label><label><input type="checkbox" id="adlck" class="posRel top2">Locked</label>
			            <label class="marL10">Expired</label><input type="text" id="adexp" class="w70 adexp">
			     </div>
			                        
			      <div class="borderT">
			            <h2 class="padT5"> Set Password </h2>
			            <label class="floatL" style="width:150px"><input type="password" id="adpass"></label>
			            <div class="clearB"></div>
			      </div>
			      
			      <div class="txtAR">
			      		<div class="marT4"><a id="adaddBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Add</div></a>
			      		<a id="adclrBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>
			      </div>
		</div>      
	</div>
	
	<div class="clearB"></div> 
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	