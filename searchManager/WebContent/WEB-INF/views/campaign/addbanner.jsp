<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="advertise"/>
<c:set var="submenu" value="banner"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/campaign/campaign.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/campaign/campaign.css" />" rel="stylesheet" type="text/css">

    <!--Left Menu-->
    <div class="clearB floatL sideMenuArea">
    <div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>
      <!-- Keyword -->
      <div class="clearB floatL w240">
        <div class="farial fsize16 fwhite bluebgTitle">Keyword</div>
        <div class="clearB floatL w230 padL10">
          
          <div class="farial fsize12 fDGray w220 borderB padTB5"> 
          	<span style="padding-top:7px">
            	<a href="javascript:void(0);" onclick="javascript:addKeyword();" class="btnGraph btnAddGreen floatR"></a>
            	<input type="text" id="searchFilter" name="searchFilter" class="farial fsize12 fgray leftSearch">           	  
           </span>
          </div>          
         
          <!--Start Keyword Listing-->
          <table>
     		<tbody id="keywordBody">
     			<tr id="kDispPattern" style="display:none;">
     			<td class="borderB">
		        	<a href="#"> <img src="<spring:url value="/images/btn_delete_big.jpg" />" width="10" height="10"></a>		        	
		        </td> 
     			</td>
     			<td class="borderB">
		          <div class="keywordHolder farial fsize12 fDGray w210 padT8 clearfix">			      
		          	<div class="keywordText floatL lnk w155 padL5"><a href="javascript:void(0);"></a></div>
		          	<div class="keywordLink floatL txtAR w55"><a href="javascript:void(0);"></a></div>
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

<!-- End Left Side -->

<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Banner Page
		</h1>
	</div>
	<div class="clearB"></div>	
	<div class="titleBlue borderB marRL5">Banner: All Desktop Sale</div>
		<table width="96%" class="fsize12 mar20 tblNoBorder">
			<tr>
			   	<td width="150px">Banner Name</td>
			    <td><input type="text" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230" value="All Desktop Sale"/></td>
			    <td width="280px" rowspan="5"><img src="<spring:url value="/images/linegraph.gif" />"></td>
			</tr>
			<tr>
			   	<td>Banner URL</td>
		        <td>
		        	<input type="text" class="farial fsize12 fgray padLR3 padTB4 border marL10 w205 pad2" value="www.bannerurl.com"/>
		        	<a href="#"><img src="<spring:url value="/images/icon_magGlasswithBG.png"/>" style="margin-bottom:-10px"></a>
		        </td>
		   	</tr>
		   	<tr>
			   	<td>Link URL</td>
		        <td>
		        	<input type="text" class="farial fsize12 fgray padLR3 padTB4 border marL10 w205 pad2" value="www.linkurl.com"/>
		        	<a href="#"><img src="<spring:url value="/images/icon_magGlasswithBG.png" />" style="margin-bottom:-10px"></a>
		        </td>
		   	</tr>
		   		<tr>
			   	<td class="vtop">Add to Campaigns</td>
		        <td>
		        	<select multiple="multiple" class="farial fsize12 fgray padLR3 padTB4 border marL10 w240" size="3">
		        		<option>Back to School</option>
		        		<option>Black Friday</option>
		        		<option>Christmas</option>
		        	</select>
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
    
    <div style="96%">
    <h2 class="fsize12 txtAR" style="border-bottom:2px solid #c7c7c7"> Search: <input type="text" style="margin-top:-3px; margin-bottom:4px; margin-left:4px;"> <div class="floatR" style="margin-top:-4px; margin-left:6px"><a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">GO</div></a></div> </h2>
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
    	<table width="100%" class="fsize12 tblAlpha">
    		<tr>
    			<th></th>
    			<th width="48%">Catalog</th>
    			<th width="15%">Period</th>
    			<th width="18%">Banner Schedule</th>
    			<th width="10%">Keyword</th>
    			<th></th>
    		</tr>
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td><a href="<spring:url value="/campaign/pcmall" />">Halloween</a></td>
    			<td>Nov 1 - Nov 30</td>
    			<td>Nov 1 - Nov 10</td>
    			<td class="txtAC">3</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/add.png" />"></a></td>
    		</tr>
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td><a href="<spring:url value="/campaign/pcmall" />">Christmas</a></td>
    			<td>Dec 1 - Dec 30</td>
    			<td>Nov 15 - Nov 20</td>
    			<td class="txtAC">5</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/add.png" />"></a></td>
    		</tr>
    		<tr>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/icon_del.png" />"></a></td>
    			<td><a href="<spring:url value="/campaign/pcmall" />">Valentine</a></td>
    			<td>Feb 1 - Feb 28</td>
    			<td></td>
    			<td class="txtAC">10</td>
    			<td class="txtAC"><a href="#"><img src="<spring:url value="/images/add.png" />"></a></td>
    		</tr>
    	</table>
    	<div align="right" class="marT10">
    		<a href="#" class="buttons btnGray clearfix">
			   	<div class="buttons fontBold">Add to Campaign</div>
			</a>
    	</div>
	</div>
	<div class="clearB"></div>
</div>
<!--  end right side -->

<%@ include file="/WEB-INF/includes/footer.jsp" %>	