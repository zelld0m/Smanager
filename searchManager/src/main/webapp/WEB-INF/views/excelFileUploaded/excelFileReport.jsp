<%@ include file="/WEB-INF/includes/includes.jsp" %>
<script type="text/javascript" src="<spring:url value="/js/excelFileUploaded/excelFileReport.js" />"></script>	   
<table class="tblItems w100p marT5">
	<tr>
		 <th>
			FileName:
		 </th>
		 <td>
		 	${excelFileUploaded.fileName}       	 
		</td>           	  
	</tr>
</table> 
<input type="hidden" id = "keyword"/>
<table  class="tblItems w100p marT5">
	<thead>
		<tr class="alt">			                
			<th>Keyword/s</th>
		</tr>
	</thead>
</table>
<div style="overflow:auto; height:300px; width:400px" class="tblItems w100p marT5">
	<table  class="tblItems w100p marT5">
		<tbody>	
	  	<c:forEach items="${keywords}" var="keyword" varStatus="status">
	  	<tr>
	  		<td>
	  			<div>
	  				[<a href="javascript:void(0);" onclick="$('#keyword').val('${keyword.key}');" class="showHide">+</a>]
	  				${keyword.key} 
	  			</div>
	  			<div id="${keyword.key}" style="display:none;">
					<table id="uploadedRules" class="tblItems w100p marT5">
						<thead>
		            		<tr class="alt">			                
		                		<th>Rank</th>
		                		<th>SKU</th>
		                		<th>Name</th>
		                		<th>Expiration</th>
		            		</tr>
		        		</thead>
					<tbody>
		        	<c:forEach items="${keyword.value}" var="excelFileReport">
				    <tr>
				        <td align="center">
				        	<fmt:formatNumber value="${excelFileReport.rank}"/>
				        </td>
						<td align="center">
				        	<fmt:formatNumber pattern="#####" value="${excelFileReport.sku}" />
				        </td>		        
				        <td align="center">
				        	${excelFileReport.name}
				        </td>
				        <td align="center">
				        	 <joda:format  value="${excelFileReport.expiration}"/> 
				        </td>			        			        			        			        			        
				    </tr>        
					</c:forEach>			    
				</tbody>
		    </table>  		
	  	</div>
	  	</c:forEach>
	  	</tbody>
	</table>
</div> 	  	
<table class="tblItems w100p marT5">
	<tr>
    	<td align="right">
  		<c:choose>
  			<c:when test="${excelFileUploaded.addedOnRuleBy == null}">
				<a id="delete" href="javascript:void(0)" class="buttons btnGray clearfix">
					<div class="buttons fontBold">Delete</div>
				</a> 
				<a id="addToRule" href="javascript:void(0)" class="buttons btnGray clearfix">
					<div class="buttons fontBold">Add to Rules</div>
				</a> 
   			</c:when>
   			<c:otherwise>
				File already been added to the rule. 
   			</c:otherwise>
   		</c:choose>	
    	</td>           	  
	</tr>
</table> 
	 