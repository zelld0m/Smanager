<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="security"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/security/security.js" />"></script> 
<sec:authorize access="hasRole('MANAGE_USER')">
<!--Start Left Side-->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="companyLogo"><a href="#"><img src="<spring:url value="${storeLogo}" />"></a></div>

	<div class="clearB floatL w240">
    	<div id="sideHeader" class="sideHeader posRel">
    		<img src="../images/corner_tl.png" class="curveTL"/>
    		<img src="../images/corner_tr.png" class="curveTR"/>
			Search Refinement
			<img src="../images/corner_bl.png" class="curveBL"/>
			<img src="../images/corner_br.png" class="curveBR"/>
    	</div>
        <div class="clearB floatL w230 padL5 padT10 fsize12">
        	<label class="marT10 floatL w100p">Name or Username</label> 
        	<div class="marT3 w200 searchBoxHolder floatL">
       			<input type="text" name="refsrc" id="refsrc" class="farial fsize12 fgray w99p">
       		</div>
       		<div class="clearB"></div>     		
       		<label class="marT10 floatL w100p">Member Since</label> 
       		<label class="marT3 floatL w55p"><input type="text" class="w90" id="refmem" name="refmem"></label>
       		<label class="marT8 floatL w100p">Account Role</label> 
       		<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refrole" style="cursor:pointer">
					<option value="">All Roles</option>
				</select>
			</label>
       		<label class="marT8 floatL w100p">Account Status</label> 
       		<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refstat" style="cursor:pointer">
					<option value="">Both Locked & Active Account</option>
					<option value="yes">Locked Account Only</option>
					<option value="no">Active Account Only</option>
				</select>
			</label>
			<label class="marT8 floatL w100p">Account Validity</label> 
			<label class="marT3 floatL w100p">
				<select class="w90p mar0" id="refexp" style="cursor:pointer">
					<option value="">Both Expired & Valid Account</option>
					<option value="yes">Expired Account Only</option>
					<option value="no">Valid Account Only</option>
				</select>
			</label>
			<div class="clearB"></div>
			<div align="right" class="txtAR marT10 padT5"> 	        		
	        	<a id="refFilBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Filter</div></a>
	        	<a id="clrFilBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Reset</div></a>
	        </div>
			
    	</div> 
	</div>
</div>
<!--End Left Side-->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">		
	  <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
		<span id="titleText">User</span>
		<span id="titleHeader" class="fLblue fnormal"></span>
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
    	<div class="clearB"></div>
    	<table width="100%" class="fsize12 tblAlpha conTable marT3">
    		<tr class="conTr1">
    			<th width="7%"></th>
    			<th width="30%">User</th>
    			<th width="12%">Role</th>
    			<th width="15%">Member Since</th>
    			<th width="10%">Status</th>
    			<th width="10%">Validity</th>
    			<th width="28%">Last Access</th>
    		</tr>
    		<tr id="conTr1Pattern" class="conTableItem" style="display: none">
    			<td class="txtAC" id="delIcon">
    				<a href="javascript:void(0);"><img src="../images/icon_del.png"></a>
    			</td>
    			<td id="userInfo">
    				<span id="username"><a href="javascript:void(0);"></a></span>
    				<div class="clearB"></div>
    				<span id="fullName"></span> 
    				<div class="clearB"></div>
    				<span id="email"></span>   								
    			</td>
    			<td  id="role">
    				<span></span>  
    			</td>
    			<td id="memberSince">
    				<span></span>
    			</td>
    			<td id="status">
    				<span></span>
    			</td>
    			<td id="validity">
    				<span></span>
    			</td>
    			<td id="lastAccess">
    				<span id="dateAccess"></span>
    				<div class="clearB"></div>
    				<span id="ipAccess" class="forange"></span>
    			</td>
    		</tr>
    	</table>   

      <div id="sortablePagingBottom" class="w99p floatL txtAL"></div>
      <!--Pagination--> 	
	</div>

	<div class="clearB"></div>
	
	<div id="userInfoTemplate" style="display:none">
		<div class="w300 security fsize11">
			<div class="w80 floatL txtAC"><img src="<spring:url value="/images/noAvatar.jpg" />" class="border"></div>
			<div class="w220 floatL">
			            <label class="floatL w70 padT3">Username :</label><label class="w120 floatL padL5 fbold shuser"></label><div class="clearB"></div>
			            <label class="floatL w70 padT3">Full Name :</label><label class="w120 floatL padL5 fbold shfname"></label><div class="clearB"></div>
			            <label class="floatL w70 padT3">Last Access :</label><label class="floatL shlacss"></label><div class="clearB"></div>
			            
			      </div>
			     
			      <div class="clearB"></div>
			      
			      <div id="view-profile">
			      	<ul class="marB0">
				      	<li><a href="#acctinfo"><span>Account Info</span></a></li>
				      	<li><a href="#acctact"><span>Account Activities</span></a></li>
				    </ul>
				    
				    <div id="acctinfo">
				    	<div class="clearB"></div>
				    	<h2 class="txtAL marT0 padL10 padT8 borderT">Account Info</h2>
				    	<div class="marB10">
				    		<div class="clearB marT5"></div>
				      		
				      		<label class="floatL w80 padT3 padL5">Email: </label>
				            <label class="floatL w200">
				            	<input type="text" class="w200" id="shemail">
				            </label>
				            <div class="clearB marT10"></div>
				      		
				      		<label class="floatL w80 padT3 padL5">Role: </label>
				            <label class="floatL w100">
								<select id="shrole" class="mar0"></select>
							</label>
				            <div class="clearB marT10"></div>
				            
				            <label class="floatL w80 padT3 padL5">Validity:</label>
				            <label class="floatL w140">
				            	<input type="text" class="w70" id="shexp">
				            </label>
				            <div class="clearB marT10"></div>
				            
				            <label class="floatL w80 padT3 padL5">Status:</label>
				            <label class="floatL w140">
				            	<input type="checkbox" id="shlck" class="firerift-style-checkbox">
				            </label>
				            <div class="clearB marT10"></div>
				            
				            <div class="txtAR marT10">
				            	<a id="shsv" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Update</div></a>
				            </div>
			            </div>
			            
			             <div class="clearB"></div>
			      
					      <div class="borderT">
					            <h2 class="padT5"> Change Password </h2>
					      		<div class="alert">Keep blank to auto generate password</div>
					            <label class="floatL" style="width:150px"><input type="password" id="shpass"></label> <label class="floatL marT0 padT0"> <div class="marT4"><a id="resetBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Reset Password</div></a></div> </label>
					            <div class="clearB"></div>
					      </div>    
				    </div>
				    
				    <div id="acctact">				    	
				    	<div class="clearB"></div>
				    	<h2 class="txtAL marT0 padL10 padT5 borderT">Account Activities</h2>
			    		This feature will be available soon
				    </div>
			     
			     
			     </div>
			     
		</div>      
	</div>
	
	<div id="addUserInfoTemplate" style="display:none">
		<div class="w300 security fsize11">
			<div class="w80 floatL txtAC"><img src="../images/noAvatar.jpg" class="border" /></div>
			<div class="w200 floatL">
			            <label class="floatL w70">Username :</label><label class="w120 floatL padL5 fbold"><input type="text" id="aduser"></label><div class="clearB"></div>
			            <label class="floatL w70">Fullname :</label><label class="w120 floatL padL5 fbold"><input type="text" id="adfull"></label>
			      </div>
			      <div class="clearB"></div>
			      <div class="marB10">
			            <div class="clearB marT5"></div>
			            <label class="floatL w80 marL10 marB5">Email :</label>
			            <label>
			            	<input type="text" class="w200" id="ademail">
			            </label>
			            <div class="clearB marT10"></div>
			            
			            <label class="floatL w80 marL10 marB5">Role :</label>
			            <label>
			            	<select id="adrole" class="mar0"></select>
			            </label>
			            <div class="clearB marT5"></div>
			            
			            <label class="floatL w80 padT3 marL10">Validity:</label>
				            <label class="floatL w140">
				            	<input type="text" class="w70" id="adexp">
				            </label>
				        <div class="clearB marT10"></div>
				        
				        <label class="floatL w80 padT3 marL10">Status:</label>
				            <label class="floatL w140">
				            	<input type="checkbox" id="adlck" class="firerift-style-checkbox">
				            </label>
			     </div>
			                        
				       <div class="clearB marT10"></div>     
			      <div class="borderT">
			            <h2 class="padT5"> Set Password </h2>
			            <label class="floatL" style="width:150px"><input type="password" id="adpass"></label>
			            <label><input type="checkbox" class="posRel top4" id="adgen"> Auto-generate password </label>
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
</sec:authorize>
<%@ include file="/WEB-INF/includes/footer.jsp" %>	
