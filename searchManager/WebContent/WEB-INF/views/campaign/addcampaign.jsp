<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="advertise"/>
<c:set var="submenu" value="campaign"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/campaign/campaign.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/campaign/campaign.css" />" rel="stylesheet" type="text/css">



<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<div class="w460 padT10 padL10 floatL fsize20 fnormal">Campaign List</div>
		<div class="floatL w245 txtAR padT7"> <!--  input type="text" name="query" id="query" class="farial fsize12 fgray searchBox searchBoxIconLBg w175" -->
		<div class="w175 advanceSearchtxt floatL marL22"> <input name="query" id="query" class="farial fsize12 fgray w145 advanceSearchtxtBox"><a href="/"><img src="<spring:url value="/images/btnIcon_advanceSearch.png" />" class="floatR"></a></div>
		<a class="btnGraph" id="addCampaignImg" href="javascript:void(0);"><div class="btnGraph btnAddGrayL floatR marT1"></div></a>
		</div>
	</div>
	<div class="clearB"></div>	
	
	<div id="addCampaignTemplate" style="display: none">
	<table width="96%" class="fsize12 mar20 tblNoBorder">
		<tr>
		   	<td width="150px"> Campaign Name</td>
		    <td><input type="text" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230" /></td>
		</tr>
		<tr>
		   	<td>Start Date</td>
	        <td>
	        	<div class="floatL txtAL dpickericon1">
                       <input name="" id="startdatepicker" type="text" class="txtfieldbg border1px fsize12 farial fgray w100 pad2">
                       <a href="javascript:;" id="startdatepicker" title="Choose date"></a> 
                   </div>
	        </td>
	   	</tr>
	   	<tr>
		   	<td>End Date</td>
	        <td>
	        	<div class="floatL txtAL dpickericon1">
                       <input name="" id="enddatepicker" type="text" class="txtfieldbg border1px fsize12 farial fgray w100 pad2">
                       <a href="javascript:;" id="enddatepicker" title="Choose date"></a> 
                   </div>
               </td>
	   	</tr>
	   	<tr>
	   		<td></td>
		   	<td class="padL212">		   				   		
		   		<a href="#" class="buttons btnGray clearfix">
		   			<div class="buttons fontBold">Add</div>
		   		</a>
			</td>
	   	</tr>
   	</table>
   	</div>
    	
                    
    <div style="96%">
     <!--Pagination-->
      <div class="mar0">
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
      </div>
      <!--Pagination-->
      
      <div class="clearB floatR farial fsize12 fDGray fbold txtAR w730 marT10" style="background:url(<spring:url value="/images/bgSort.jpg" />) repeat-x; padding-top:8px;"></div>
      <div class="clearB"></div>   
      
    	<table width="100%" class="fsize12 tblAlpha">
    		<tr>
    			<th></th>
    			<th width="45%">Campaign</th>
    			<th width="15%">Start Date</th>
    			<th width="15%">End Date</th>
    			<th width="14%">No of Banners</th>
    			<th></th>
    		</tr>
  			<c:forEach var="i" begin="1" end="10" step="1">
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td><a href="<spring:url value="/campaign/pcmall" />">Back to School</a></td>
    			<td class="txtAC">June 1</td>
    			<td class="txtAC">July 27</td>
    			<td class="txtAC">5</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/page_edit.png" />"></a></td>
    		</tr>
    		</c:forEach>
    		
    	</table>
    	
	</div>
	<div class="clearB"></div>
</div>
<!--  end right side -->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	