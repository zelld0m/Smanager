<%@ include file="/WEB-INF/includes/includes.jsp" %>
<%@ include file="/WEB-INF/includes/header.jsp" %>
	<script src="<spring:url value="/js/changePassword.js" />"></script>
			<br><br>
			<div id="usual2" class="usual" style="height: 29px; font-size: 17px; color: #FFFFFF; vertical-align: middle; padding: 12px 10px 0px 18px;"><h1>Change Password</h1></div>
			<div id="tabs5" class="clearB fsize12 farial fgray">
				<div class="borderLR w980">
					<form:form modelAttribute="changePasswordForm" method="post" onsubmit="return checkPasswordDetails();">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<c:if test="${message != null}">
							<tr>
								<td colspan=2><div id="message">${message}</div></td>
							</tr>
						</c:if>	
						<c:if test="${detailed_message != null}">
							<tr>
								<td colspan=2><div class="span_message">${detailed_message}</div></td>
							</tr>
						</c:if>
						
						<c:if test="${changePasswordForm != null}" >
						<tr>
							<td class="layoutcolumn3">		
								<table class="tblepad" style="padding: 2px">
								<tr>
									<td style="width: 150px;">Current password</td>
									<!-- used to use errors_nowrap class here -->
									<td><form:password path="password" /> <form:errors path="password" cssClass="error hackwrap w100" /></td>
								</tr>
								<tr>
									<td>New password</td>
									<td><form:password path="newPassword1" /> <form:errors path="newPassword1" cssClass="error hackwrap w100" /></td>
								</tr>
								<tr>
									<td>New password again</td>
									<td><form:password path="newPassword2" /> <form:errors path="newPassword2" cssClass="error hackwrap w100" /></td>
								</tr>
								<tr>
									<td><input type="submit" value="Change Password" ></td>
								</tr>
								</table>
							</td>
						</tr>
						</c:if>
						
						</table>
					</form:form>
					</div>
				<div class="clearB"></div>
              	<div><img src="<spring:url value="/images/tblbottombg.png" htmlEscape="true" />" alt=""></div>
			</div> 
<%@ include file="/WEB-INF/includes/footer.jsp" %>