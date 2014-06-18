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
                <th>Action</th>   
            </tr>
        </thead>
		<tbody>
		<c:choose>
			<c:when test="${fn:length(importRuleTasks) > 0}">	
				
				<script text="text/javascript">var importRuleTaskList = new Object();</script>	
		        
		        <c:forEach items="${importRuleTasks}" var="importRuleTask" varStatus="status">
		        <script text="text/javascript">importRuleTaskList['${importRuleTask.taskId}'] = new Object();</script>	
		        <c:set var="alt" value=""/>
		        <c:if test="${status.count %2 == 0}">
		        	<c:set var="alt" value="alt"/>
		        </c:if>
				    <tr class="conTableItem ${alt}">
				   		<td align="center">
				   			<script text="text/javascript">importRuleTaskList['${importRuleTask.taskId}'].entityName = '${importRuleTask.ruleEntity.name}';</script>
							${importRuleTask.ruleEntity.name}
				        </td>
				        <td align="center" class="ruleName">
				        	<script text="text/javascript">
				        		importRuleTaskList['${importRuleTask.taskId}'].sourceRuleName = '${importRuleTask.sourceRuleName}';
				        		importRuleTaskList['${importRuleTask.taskId}'].sourceRuleId = '${importRuleTask.sourceRuleId}';
				        		importRuleTaskList['${importRuleTask.taskId}'].targetRuleName = '${importRuleTask.targetRuleName}';
				        		importRuleTaskList['${importRuleTask.taskId}'].targetRuleId = '${importRuleTask.targetRuleId}';
				        	</script>
				        	${importRuleTask.sourceRuleName}
				        </td>
				        <td align="center">
				        	<script text="text/javascript">importRuleTaskList['${importRuleTask.taskId}'].targetStoreId = '${importRuleTask.targetStoreId}';</script>
							<c:if test="${!isTargetStore}">
                				${importRuleTask.targetStoreId}
                			</c:if>
                			<c:if test="${isTargetStore}">
                				${importRuleTask.sourceStoreId}
                			</c:if>
				        </td>
				        <td align="center">
				        	<script text="text/javascript">importRuleTaskList['${importRuleTask.taskId}'].targetRuleName = '${importRuleTask.targetRuleName}';</script>
				        	${importRuleTask.targetRuleName}
				        </td>
				        <td align="center">
				        	<script text="text/javascript">importRuleTaskList['${importRuleTask.taskId}'].importType = '${importRuleTask.importType.displayText}';</script>
				        	${importRuleTask.importType.displayText}
				        </td>	
				        <td align="center">
				        	${importRuleTask.taskExecutionResult.taskStatus.displayText}
				        	<c:if test="${importRuleTask.taskExecutionResult.taskStatus.displayText == 'Failed'}">
				        		<a href="javascript:void(0);" onclick="$('#reason').val('${importRuleTask.taskExecutionResult.taskErrorMessage} <br/><br/>Run attempt/s:${importRuleTask.taskExecutionResult.runAttempt}')" class="failedReason"><img src="/searchManager/images/icon_alert.png"/></a>
				        	</c:if>
				        </td>
				        <td align="center">
				        	 <c:forEach items="${importRuleTask.taskMessages}" var="taskMessage" varStatus="status">
				        		<a href="javascript:void(0);" 
				        		onclick="$('#reason').val('${taskMessage.dateLabel1}<joda:format pattern="${dateFormat}" value="${taskMessage.displayDate1}"/><c:if test="${!empty taskMessage.displayDate2}"><br/>${taskMessage.dateLabel2}<joda:format pattern="${dateFormat}" value="${taskMessage.displayDate2}"/></c:if>')" class="failedReason">
									${taskMessage.message}
								</a><br/>
							</c:forEach>
				        </td>
				        <td nowrap>
				        	
				        	<c:set var="enableRequeue" value="${taskXmlMap[importRuleTask.taskId] && (importRuleTask.taskExecutionResult.taskStatus eq 'FAILED' || importRuleTask.taskExecutionResult.taskStatus eq 'CANCELED' || importRuleTask.taskExecutionResult.taskStatus eq 'AUTO_CANCELED')}"/>
				        	<c:set var="enableCancel" value="${importRuleTask.taskExecutionResult.taskStatus eq 'QUEUED' || importRuleTask.taskExecutionResult.taskStatus eq 'FAILED'}"/>
				        	<script text="text/javascript">importRuleTaskList['${importRuleTask.taskId}'].hasXml = ${taskXmlMap[importRuleTask.taskId] == true};</script>
				        	<c:if test="${importRuleTask.ruleEntity ne 'SPELL' and importRuleTask.ruleEntity ne 'BANNER'}">
				        		<input class="${taskXmlMap[importRuleTask.taskId] ? 'btnPreview' : 'btnPreviewOff' }" type="image" id="${importRuleTask.taskId }" src="<spring:url value="/images/${taskXmlMap[importRuleTask.taskId] ? 'ajax-loader-rect.gif' : 'icon_notactive.png' }" />"/>
				        	</c:if>
				        	<input class="${enableRequeue ? 'btnRequeue' : 'btnRequeueOff' }" id="${importRuleTask.taskId }" type="image" src="<spring:url value="/images/icon_globe25${enableRequeue ? '_active' : '' }.png" />"/>
				        	<input class="${enableCancel ? 'btnCancel' : 'btnCancelOff' }" id="${importRuleTask.taskId }" type="image" src="<spring:url value="/images/icon_delete2${enableCancel ? '' : '_gray' }.png" />"/>
				        	
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