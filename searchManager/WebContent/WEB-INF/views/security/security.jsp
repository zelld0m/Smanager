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
				<!--<li><a href="">Administrator</a></li>
				<li class="alt"><a href="">Encoder</a></li>
				<li><a href="">Approver</a></li>
				<li class="alt"><a href="">Publisher</a></li>-->
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
       		<label class="marT3 floatL w100p"><input type="text" class="w90" id="refmem" name="refmem"></label>
       		<label class="marT8 floatL w100p">Status</label> 
       		<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refstat" style="cursor:pointer">
					<!--<option id="" value="">sample</option>
					<option>sample 1</option>-->
				</select>
			</label>
			<label class="marT8 floatL w100p">Expired</label> 
			<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refexp" style="cursor:pointer">
					<!--<option>sample</option>
					<option>sample 1</option>-->
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
		<!--<span id="titleText">User list for</span>
		<span id="titleHeader" class="fLblue fnormal">Administrator</span>-->
	  </div>
	 <div id="addItemHolder" class="floatL w180 txtAR padT7" style="display: none">
       	<div class="floatL w245 txtAR padT7">   		
			<a href="javascript:void(0);" id="addItemBtn" class="btnGraph"><div class="btnGraph btnAddGrayL floatR marT1"></div></a> 
		</div>
	</div>		
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
   <div class="clearB"></div>
    <!--Pagination-->
     <!--<div class="mar0">
        <div class="clearB floatL farial fsize12 fDblue w300 padT10 marL10">Displaying 1 to 25 of 26901 Products</div>
        <div class="floatR farial fsize12 fgray txtAR padT10">
          <div class="txtAR">
            <ul class="pagination">
              <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">1</a></li>
              <li><a href="#">2</a></li>
              <li><a href="#">3</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
            </ul>
          </div>
        </div>
      </div> -->
      <div id="sortablePagingTop" class="floatL txtAL w550"></div>
      <!--Pagination-->
    	<table width="100%" class="fsize12 tblAlpha conTable">
    		<tr>
    			<th width="7%"></th>
    			<th width="35%">Username</th>
    			<th width="10%">Status</th>
    			<th width="10%">Expired</th>
    			<th width="15%">Member Since</th>
    			<th width="17%">Last Access</th>
    		</tr>
    		<!-- 
    		<c:forEach var="i" begin="1" end="10" step="1">
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td><a href="/">mpedcp</a></td>
    			<td class="txtAC">Enabled</td>
    			<td class="txtAC">Yes</td>
    			<td class="txtAC">11/${i}/2011 12:30:00</td>
    			<td class="txtAC">6 days ago</td>
    		</tr>
    		</c:forEach>-->
    	
    	</table>   
    	<!--Pagination-->
      <!--<div class="mar0">
        <div class="clearB floatL farial fsize12 fDblue w300 padT10 marL10">Displaying 1 to 25 of 26901 Products</div>
        <div class="floatR farial fsize12 fgray txtAR padT10">
          <div class="txtAR">
            <ul class="pagination">
              <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">1</a></li>
              <li><a href="#">2</a></li>
              <li><a href="#">3</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
            </ul>
          </div>
        </div>
      </div>-->
      <div id="sortablePagingBottom" class="w550 floatL txtAL"></div>
      <!--Pagination--> 	
	</div>

	<div class="clearB"></div>
	
	<div id="userInfoTemplate" style="display:none">
		<div class="w300 security fsize11">
			<div class="w80 floatL txtAC"><img src="<spring:url value="/images/noAvatar.jpg" />" class="border"></div>
			<div class="w200 floatL">
			            <label class="floatL w70">Username :</label><label class="w120 floatL padL5 fbold">lorem ipsum dolor</label><div class="clearB"></div>
			            <label class="floatL w70">Fullname :</label><label class="w120 floatL padL5 fbold">lorem ipsum</label>
			      </div>
			      <div class="clearB"></div>
			      <div class="marB10">
			            <label class="floatL w80 marL10">Last Access :</label><label class="floatL">dolor sit</label><div class="clearB"></div>
			            <label class="floatL w80 marL10">IP Address :</label><label class="floatL">dignissim sit amet tellus</label><div class="clearB"></div>
			            <div class="clearB marT5"></div>
			            <label class="floatL w80 marL10"> </label><label><input type="checkbox"> Locked</label>
			            <label><input type="checkbox"> Expired</label>
			     </div>
			                        
			      <div class="borderT">
			            <h2 class="padT5"> Change Password </h2>
			            <label class="floatL" style="width:150px"><input type="text"></label> <label class="floatL marT0 padT0"> <div class="marT4"><a id="applyBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Reset Password</div></a></div> </label>
			            <div class="clearB"></div>
			      </div>
			      
			      <div class="alert">keep blank to auto generate password</div>
		</div>      
	</div>
	
	<div id="addUserInfoTemplate" style="display:none">
		<div class="w300 security fsize11">
			<div class="w80 floatL txtAC"><img src="<spring:url value="/images/uploadImage.jpg" />" class="border"></div>
			<div class="w200 floatL">
			            <label class="floatL w70">Username :</label><label class="w120 floatL padL5 fbold"><input type="text"></label><div class="clearB"></div>
			            <label class="floatL w70">Fullname :</label><label class="w120 floatL padL5 fbold"><input type="text"></label>
			      </div>
			      <div class="clearB"></div>
			      <div class="marB10">
			            <label class="floatL w80 marL10">Last Access :</label><label class="floatL"><input type="text" class="w200"></label><div class="clearB"></div>
			            <label class="floatL w80 marL10">IP Address :</label><label class="floatL"><input type="text" class="w200"></label><div class="clearB"></div>
			            <div class="clearB marT5"></div>
			            <label class="floatL w80 marL10"> </label><label><input type="checkbox">Locked</label>
			            <label><input type="checkbox"> Expired</label>
			     </div>
			                        
			      <div class="borderT">
			            <h2 class="padT5"> Set Password </h2>
			            <label class="floatL" style="width:150px"><input type="text"></label> <label class="floatL marT0 padT5"><input type="checkbox"> auto generate keyowrd </label>
			            <div class="clearB"></div>
			      </div>
			      
			      <div class="txtAR">
			      		<div class="marT4"><a id="applyBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Add</div></a>
			      		<a id="applyBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Clear</div></a></div>
			      </div>
		</div>      
	</div>
	
	<div class="clearB"></div> 
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	