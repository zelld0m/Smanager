<%@ include file="/WEB-INF/includes/includes.jsp" %> 
    	<input id="totalItem" type="hidden" value="${totalCount}"/>
    	<input id="currentPageNumber" type="hidden" value="${currentPage}"/>
    	<input id="filter" type="hidden" value="${filter}"/>
    	<input id="reason" type="hidden" value=""/>
		<div id="sortablePagingTop" class="floatL txtAL w100p"></div>
		<div class="clearB"></div>	
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
		<c:choose>
			<c:when test="${fn:length(importRuleTasks) > 0}">		
		        <c:forEach items="${importRuleTasks}" var="importRuleTask" varStatus="status">
		        <c:set var="alt" value=""/>
		        <c:if test="${status.count %2 == 0}">
		        	<c:set var="alt" value="alt"/>
		        </c:if>
				    <tr class="conTableItem ${alt}">
				   		<td align="center">
							${importRuleTask.ruleEntity.name}
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
				        	<c:if test="${importRuleTask.taskExecutionResult.taskStatus.displayText == 'Failed'}">
				        		<a href="javascript:void(0);" onmousemove="$('#reason').val('${importRuleTask.taskExecutionResult.taskErrorMessage} <br/><br/>runAttempt:${importRuleTask.taskExecutionResult.runAttempt}')" class="failedReason" reason = "xx"><img src="/searchManager/images/icon_alert.png"/></a>
				        	</c:if>
				        </td>
				        <td align="center">
				        	<joda:format pattern="${dateFormat}" value="${importRuleTask.taskExecutionResult.taskStartDateTime}"/>
				        </td>
				        <td align="center">
				        	<joda:format pattern="${dateFormat}" value="${importRuleTask.taskExecutionResult.taskEndDateTime}"/>
				        </td>		        		        		        		        		        		        		        		        			        			        			        			        
				    </tr>        
				</c:forEach>			    
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="9" align="center">
						No record found.
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
		</tbody>
    	</table> 
    	<div class="clearB"></div>	
    	<div id="sortablePagingBottom" class="floatL txtAL w100p"></div>
    	<div id="dialog-modal">
    		
    	</div>
    	<div id="dialog-modal-details">
    		
    	</div>
  	<div id="ruleItemPagingBottom" class="w730 floatL txtAL marT20"></div>