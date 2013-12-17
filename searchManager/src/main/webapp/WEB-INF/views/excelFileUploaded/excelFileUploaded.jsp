<%@ include file="/WEB-INF/includes/includes.jsp" %>	
<script type="text/javascript" src="<spring:url value="/js/jquery/jquery.form.js" />" ></script>
<script type="text/javascript" src="<spring:url value="/js/excelFileUploaded/excelFileUploaded.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/scrollableTab/jquery.scrollabletab.js" />"></script>	
    <div class="clearB"></div>
    	<input id="ruletype" type="hidden" value="${ruleType}"/>
    	<input id="excelFileUploadedId" type="hidden" value=""/>
    	<input id="storeId" type="hidden" value=""/>
    	<input id="fileName" type="hidden" value=""/>
    	<input id="totalItem" type="hidden" value="${totalCount}"/>
    	<input id="currentPageNumber" type="hidden" value="${currentPage}"/>
        <form:form id="excelFileUpload" method="POST"
                   action="/searchManager/excelFileUploaded/upload/${ruleType}/"
                   commandName="uploadForm"
                   enctype="multipart/form-data">
           <table class="tblItems marT5">
           <tr>
           		<th>
           	  		Upload Excel File/s
           	  </th>
           </tr>
           	<tr>           	 
           	  <td>
           	  	<input type="file" id="files" name="files" multiple="multiple"/>           	  	
           	  </td>           	  
           	 </tr>
           	<tr id="uploadButtonContainer">
           	  <td align="right">
					<a href="javascript:$('#excelFileUpload').submit();" class="buttons btnGray clearfix">
						<div class="buttons fontBold">Upload</div>
					</a>       	  	
           	  </td>
           	 </tr>	
           	</table>           	 		
		</form:form>
		<div id="sortablePagingTop" class="floatL txtAL w100p"><div class="txtDisplay floatL farial fsize11 fDblue padT10">Displaying 1 to 10 of 54 Users</div><div class="floatR farial fsize11 fgray txtAR padT10">	<div class="txtAR">		<ul class="pagination"><li><span class="pager-current"><a href="javascript:void(0);">1</a></span></li><li><span class=""><a href="javascript:void(0);">2</a></span></li><li><span class=""><a href="javascript:void(0);">3</a></span></li><li><span class=""><a href="javascript:void(0);">4</a></span></li><li><span class=""><a href="javascript:void(0);">5</a></span></li><li><span class=""><a href="javascript:void(0);">6</a></span></li><li><a href="javascript:void(0);">Next</a></li></ul>	</div></div></div>
		<table id="uploadedRules" class="tblItems w100p marT5">
		<thead>
            <tr class="alt">
             	<th>...</th>
                <th>FileName</th>
                <th>Uploaded By</th>
                <th>Uploaded Date</th>
                <th>Added By</th>
                <th>Added Date</th>               
            </tr>
        </thead>
		<tbody>
        <c:forEach items="${excelFileUploadeds}" var="excelFileUploaded">
		    <tr>
		   		<td align="center">
		   			<c:choose>
		   				<c:when test="${excelFileUploaded.addedOnRuleBy == null}">
					   		<a href="javascript:void(0);" 
					   		onclick="setValue('${excelFileUploaded.excelFileUploadedId}','${excelFileUploaded.storeId}','${excelFileUploaded.fileName}');"
					   		class="delete"">
					   			<img src="../images/icon_del.png">
					   		</a>
			   			</c:when>
			   			<c:otherwise>
			   				added
			   			</c:otherwise>
			   		</c:choose>        	
		        </td>
		    	<td>
		    		<a href="javacript:void(0);" onclick="setValue('${excelFileUploaded.excelFileUploadedId}','${excelFileUploaded.storeId}','${excelFileUploaded.fileName}');" class="viewDetails" >${excelFileUploaded.fileName}</a> 			    				    		
		    	</td>
		        <td align="center">
		        	${excelFileUploaded.createdBy}
		        </td>
		        <td align="center">
		        	<joda:format  pattern="MMM dd, yyyy HH:mm:ss" value="${excelFileUploaded.createdStamp}"/>
		        </td>
		        <td align="center">
		        	${excelFileUploaded.addedOnRuleBy}
		        </td>
		        <td align="center">
		        	<joda:format pattern="MMM dd, yyyy HH:mm:ss" value="${excelFileUploaded.addedOnRuleDate}"/>
		        </td>			        			        			        			        			        
		    </tr>        
		</c:forEach>			    
		</tbody>
    	</table> 
    	<div id="sortablePagingBottom" class="floatL txtAL w100p"><div class="txtDisplay floatL farial fsize11 fDblue padT10">Displaying 1 to 10 of 54 Users</div><div class="floatR farial fsize11 fgray txtAR padT10">	<div class="txtAR">		<ul class="pagination"><li><span class="pager-current"><a href="javascript:void(0);">1</a></span></li><li><span class=""><a href="javascript:void(0);">2</a></span></li><li><span class=""><a href="javascript:void(0);">3</a></span></li><li><span class=""><a href="javascript:void(0);">4</a></span></li><li><span class=""><a href="javascript:void(0);">5</a></span></li><li><span class=""><a href="javascript:void(0);">6</a></span></li><li><a href="javascript:void(0);">Next</a></li></ul>	</div></div></div>
    	<div id="dialog-modal">
    		
    	</div>
    	<div id="dialog-modal-details">
    		
    	</div>    	