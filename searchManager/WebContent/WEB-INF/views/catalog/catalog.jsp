<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="catalog"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- tabber -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/tabber.css" />">

<!-- page specific dependencies -->
<script src="<spring:url value="/js/catalog/catalog.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/catalog/catalog.css" />" rel="stylesheet" type="text/css">

<!--Start Left Side-->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>

	<div class="clearB floatL w240">
    	<div class="sideHeader">Catalog</div>
        <div class="clearB floatL w230 padL10">
	       
	        <div class="farial fsize12 fDGray w220 borderB padTB5"> <span class="padT7">
            <input name="textfield2" type="text" class="farial fsize12 fgray leftSearch" id="textfield2" value="Search">
            <a href="#"><img src="<spring:url value="/images/btn-plus.jpg" />"  align="absmiddle"></a> </span></div>
            
            
	        <div class="leftStatus"><img src="<spring:url value="/images/information.png" />" class="marR3 marBn3><span class="fgreen">Active :</span> Catalog 8</div>
		        <table border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray w220">
		        <c:forEach var="i" begin="1" end="10" step="1">
		        <tr>
		        	<td class="borderB padTB5">
			        	<span class="lnk"><a href="#">
			        	<img src="<spring:url value="/images/btn_delete_big.jpg" />" width="10" height="10" class="marR3"> Catalog ${i}</a>
			        	</span>
		        	</td>
		            <td class="borderB padTB5 txtAR scorelink">
		            	<img src="<spring:url value="/images/icon_notactive.png" />">
		            	<img src="<spring:url value="/images/page_edit.png" />">
		            </td>
		        </tr>
		        </c:forEach>
	        </table>
        </div>
	    <img src="<spring:url value="/images/imgPaging.jpg" />" align="absmiddle" class="marT10">
    </div> 
</div>
<!--End Left Side-->


<!--Start Right Side-->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Edit Catalog
		</h1>
	</div>
     
	<div class="clearB"></div>
	<div class="titleBlue borderB marRL5">Catalog Details </div>
  	
  	<table width="98%" class="fsize12 mar20 tblCatalog">
		<tr>
		   	<td width="150px"> Catalog Name</td>
		    <td><input type="text" value="Apple Products" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230" /></td>
		</tr>
		<tr>
		   	<td>Description</td>
	        <td><input type="text" value="Apple products only" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230" /></td>
	   	</tr>
    </table>
     
    <div class="titleBlue borderB marRL5">Catalog Rules </div>
    
    <!-- content tabs -->
    <div class="tabber mar0" style="margin-top:20px; width:98%">
        <div class="tabbertab">
         <h2 class="tabMenu"><span>IMS</span></h2>
         <div class="round3 marB10" style="padding:1px; background:#c7c7c7" >
         	<div class="contentTitle round3 fsize12 fbold pad5">Lock in Category / Manufacturer</div>
         </div>
         <table class="fsize12 mar10 tblCatalog">
         	<tr>
           	<td width="150px">Category</td>
               <td width="250px">
               <select name="select" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
                <option>All Categories</option>
               </select>
             </td>
           </tr>
           <tr>
           	<td>Sub-Category</td>
               <td>
               <select name="select" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
                <option></option>
               </select>
               </td>
           </tr>
           <tr>
           	<td>Manufacturer List</td>
               <td>
               <select name="select" class="farial fsize12 fgray padLR3 padTB4 border marL10 w230">
                <option>All Manufacturers</option>
               </select>
               </td>
           </tr>
           <tr>
           	<td></td>
               <td align="right" class="padR5">
               	<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Include</div></a>
                   <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Exclude</div></a>
               </td>
           </tr>
         </table>
         
         <div class="round3 marB10" style="padding:1px; background:#c7c7c7" >
         	<div class="contentTitle round3 fsize12 fbold pad5">Lock in Individual PC Mall Part Number / MFR Part Number</div>
         </div>
         <table class="fsize12 mar10 tblCatalog">
         	<tr>
           	<td width="150px">DP No.</td>
               <td width="250px">
               <input type="text" value="All Categories" class="farial fsize12  fgray padLR3 padTB4 border marL10 w220">
             </td>
             <td>
               	   <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Include</div></a>
                   <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Exclude</div></a>
               </td>
           </tr>
           <tr>
           	<td>MFR</td>
               <td>
                <input type="text" class="farial fsize12  fgray padLR3 padTB4 border marL10 w220">
               </td>
                <td>
               	<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Include</div></a>
                   <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Exclude</div></a>
               </td>
           </tr>
         </table>
        </div>
   
        <div class="tabbertab">
         <h2>CNET</h2>
      
         
         <div>
         	<table width="98%" class="fsize12 tblCatalog">
         	<c:forEach var="i" begin="1" end="5" step="1">
         		<tr>
         			<td width="180px">Primary Alt Category ${i}</td>
         			<td>
	         			<select name="select" class="farial fsize12 fgray padLR3 padTB4 border marL10 w210">
	                		<option>Primary Alt Category ${i}</option>
	                	</select>
                	</td>
         		</tr>
            </c:forEach>
         		
         		
         	</table>
         	<div class="fsize12">
         	
         		<span></span>
         		
         	</div>
            <table width="98%"  class="fsize12 tblCatalog">
         		<tr>
         			<td class="w180">Primary Alt Category Code</td>
            		<td><input type="text" value="Category Code" class="farial fsize12 fgray padLR3 padTB4 border marL10 w203" /></td>
            	</tr>
            	<tr>
	           		<td></td>
	               <td align="left" class="padL100">
	               	<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Include</div></a>
	                   <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Exclude</div></a>
	               </td>
	           </tr>
            </table>
         </div> 
         
              
        </div>
     </div>
     <!-- end content tabs -->

<table class="tblRules">
	<tr>
  	<th></th>
      <th>Rules</th>
      <th>Values</th>
      <th>Include / Exclude</th>
      <th></th>
  </tr>
  <c:forEach var="i" begin="1" end="3" step="1">	
  <tr>
  	<td class="vtop"><img src="<spring:url value="/images/close1.png" />"></td>
      <td width="30%" class="vtop">Lock in Category / Manufacturer</td>
      <td width="40%"  class="vtop">Category: All Categories, Sub-Category: All Sub-Categories Brand: Apple</td>
      <td class="vtop">
       <select name="select" class="farial fsize14 fgray w120 padLR3 padTB4 border marL10">
           <option>Include</option>
           <option>Exclude</option>
        </select></td>
      <td><img src="<spring:url value="/images/page_edit.png" />"></td>
  </tr>
  </c:forEach>
</table>
<div align="right" class="padR5">
	<a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Cancel</div></a>
    <a href="#" class="buttons btnGray clearfix"><div class="buttons fontBold">Save</div></a>
</div>

<div class="clearBce"></div>
</div>
<!--End Right Side-->


<%@ include file="/WEB-INF/includes/footer.jsp" %>	