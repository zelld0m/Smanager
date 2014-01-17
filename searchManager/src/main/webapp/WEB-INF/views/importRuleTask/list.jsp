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
                <th>
                <c:if test="${!isTargetStore}">
                	Target Store
                </c:if>
                <c:if test="${isTargetStore}">
                	Source Store
                </c:if>                
                </th>
                <th>Target Rule</th>
                <th>Type</th>
                <th>Status</th>
                <th>Duration</th>           
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
							<c:if test="${!isTargetStore}">
                				${importRuleTask.targetStoreId}
                			</c:if>
                			<c:if test="${isTargetStore}">
                				${importRuleTask.sourceStoreId}
                			</c:if>
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
				        		<a href="javascript:void(0);" onmousemove="$('#reason').val('${importRuleTask.taskExecutionResult.taskErrorMessage} <br/><br/>Run attempt/s:${importRuleTask.taskExecutionResult.runAttempt}')" class="failedReason"><img src="/searchManager/images/icon_alert.png"/></a>
				        	</c:if>
				        </td>
				        <td align="center">
				        	 <c:forEach items="${importRuleTask.taskMessages}" var="taskMessage" varStatus="status">
				        		<a href="javascript:void(0);" 
				        		onmousemove="$('#reason').val('${taskMessage.dateLabel1}<joda:format pattern="${dateFormat}" value="${taskMessage.displayDate1}"/><c:if test="${!empty taskMessage.displayDate2}"><br/>${taskMessage.dateLabel2}<joda:format pattern="${dateFormat}" value="${taskMessage.displayDate2}"/></c:if>')" class="failedReason">
									${taskMessage.message}
								</a><br/>
							</c:forEach>
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