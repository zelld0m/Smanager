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
	  				[<a href="javascript:void(0);" onclick="$('#keyword').val('${status.index}');" class="showHide">+</a>]
	  				${keyword.key} 
	  			</div>
	  			<div id="${status.index}" style="display:none;">
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
				        	${excelFileReport.rank}
				        </td>
						<td align="center">
				        	${excelFileReport.sku}
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
<input type="checkbox" id="clearRuleFirst"/>Clear keyword/s on Add to Rule
<div class="ui-dialog-buttonpane ui-widget-content ui-helper-clearfix">
	<div class="ui-dialog-buttonset">
		<button id="addToRule" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-hover" role="button" aria-disabled="false">
			<span class="ui-button-text">Add to Rule</span>
		</button>	
		<c:if test="${excelFileUploaded.addedOnRuleBy == null}">
			<button id="delete" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" aria-disabled="false">
				<span class="ui-button-text">Delete</span>
			</button>
		</c:if>		
</div></div>
	 