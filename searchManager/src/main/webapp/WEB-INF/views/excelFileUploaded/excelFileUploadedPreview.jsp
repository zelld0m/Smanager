<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<c:choose>
	<c:when test="${not empty errorMessage}">
		${errorMessage}
	</c:when>
	<c:otherwise>
		<div id="tabs">	
		 	<ul>
		    	<c:forEach items="${excelFileUploadeds}" var="excelFileUploaded" varStatus="status">
		    	 	<li><a href="#tabs-${status.count}">${excelFileUploaded.fileName}</a></li>
		    	</c:forEach>
		  	</ul>
		  	<c:forEach items="${excelFileUploadeds}" var="excelFileUploaded" varStatus="status">
				<div id="tabs-${status.count}">
					<table id="uploadedRules" class="tblItems w100p marT5">
					<thead>
			            <tr class="alt">
			                <th>Keyword</th>
			                <th>Rank</th>
			                <th>Distributor SKU</th>
			                <th>Name</th>
			                <th>Expiration</th>
			            </tr>
			        </thead>
					<tbody>
			        <c:forEach items="${excelFileUploaded.excelFileReports}" var="excelFileReport">
					    <tr>
					    	<td>
					    		${excelFileReport.keyword} 			    				    		
					    	</td>
					        <td align="center">
					        	${excelFileReport.rank}
					        </td>
							<td align="center">
					        	${excelFileReport.sku}
					        </td>		        
					        <td align="center">
					        	${excelFileReport.name}
					        </td>
					        <td align="center">
					        	 <joda:format pattern="${dateFormat}"  value="${excelFileReport.expiration}"/> 
					        </td>			        			        			        			        			        
					    </tr>        
					</c:forEach>			    
					</tbody>
			    	</table> 	    	
			 	</div>  
		  	</c:forEach>
		</div>	
	</c:otherwise>
</c:choose>