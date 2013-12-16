<%@ include file="/WEB-INF/includes/includes.jsp" %> 
    	<input id="totalItem" type="hidden" value="${totalCount}"/>
    	<input id="currentPageNumber" type="hidden" value="${currentPage}"/>
		<div id="sortablePagingTop" class="floatL txtAL w100p"></div>
		<div style="width:730px;height:350px;overflow:auto;">
		<table class="tblItems w100p marT5">
		<thead>
            <tr class="alt">
                <th>Rule</th>
                <th>Source Rule</th>
                <th>Target Store</th>
                <th>Target Rule</th>
                <th>Type</th>
                <th>Status</th>
                <th>Task Start</th>  
                <th>Task End</th>            
            </tr>
        </thead>
		<tbody>
        <c:forEach items="${importRuleTasks}" var="importRuleTask" varStatus="status">
        <c:set var="alt" value=""/>
        <c:if test="${status.count %2 == 0}">
        	<c:set var="alt" value="alt"/>
        </c:if>
		    <tr class="conTableItem ${alt}">
		   		<td align="center">
					${importRuleTask.ruleEntity}
		        </td>
		        <td align="center">
					${importRuleTask.sourceRuleName}
		        </td>
		        <td align="center">
		        	${importRuleTask.targetStoreId}
		        </td>
		        <td align="center">
		        	${importRuleTask.targetRuleName}
		        </td>
		        <td align="center">
		        	${importRuleTask.importType.displayText}
		        </td>	
		        <td align="center">
		        	${importRuleTask.taskExecutionResult.taskStatus.displayText}
		        </td>
		        <td align="center">
		        	<joda:format pattern="${dateFomat}" value="${importRuleTask.taskExecutionResult.taskStartDateTime}"/>
		        </td>
		        <td align="center">
		        	<joda:format pattern="${dateFomat}" value="${importRuleTask.taskExecutionResult.taskEndDateTime}"/>
		        </td>		        		        		        		        		        		        		        		        			        			        			        			        
		    </tr>        
		</c:forEach>			    
		</tbody>
    	</table> 
    	</div>
    	<div id="sortablePagingBottom" class="floatL txtAL w100p"></div>
    	<div id="dialog-modal">
    		
    	</div>
    	<div id="dialog-modal-details">
    		
    	</div>
  	<div id="ruleItemPagingBottom" class="w730 floatL txtAL marT20"></div>