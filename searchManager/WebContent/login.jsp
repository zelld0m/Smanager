<%@ include file="/WEB-INF/includes/includes.jsp"%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=100" />

<title>Search Manager</title>

<link href="<spring:url value="/css/login.css" htmlEscape="true" />" rel="stylesheet" type="text/css">
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery-1.7.1.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery-ui-1.8.16.custom.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.core.min.js" />" ></script>
<!-- jQuery alert  -->
<script type="text/javascript" src="<spring:url value="/js/jquery/jquery.alerts/jquery.alerts.js" />" ></script>
<link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jquery.alerts/jquery.alerts.css" />" />

<!-- cross-browser css compatibility util -->
<script type="text/javascript" src="<spring:url value="/js/oscss.js" />"></script>

<script type="text/javascript" src="<spring:url value="/js/utility.custom/jquery-string-functions.js" />" ></script>

<script type="text/javascript" src="<spring:url value="login.js" />"></script>

</head>
<body>
<div class="w635 h353 txtAC mar0 posRel top100 bgsrclogo">

<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION.message}">
     <div id="error" class="clearB posAbs w100p farial fsize12 fred logAlert">
     	<p class="mar0">Login failed. Please try again.</p>
     	<p class="mar0">Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" /></p>
     </div>    
</c:if>

<c:remove var = "SPRING_SECURITY_LAST_EXCEPTION" scope = "session" />

<form name="f" id="f" action="<c:url value='j_spring_security_check'/>" method="POST">
<div class="formContainer">
	<label class="labelTxt">Username :</label><label class="labelTxtBox"><input type="text" name='j_username' id='j_username' class="txtbox" /></label><br>
	<label class="labelTxt">Password :</label><label class="labelTxtBox"><input type='password' name='j_password' id='j_password' class="txtbox" /></label>
    
    <div class="clearboth"></div>
    <div class="clearB floatR padT10 marRn5">
    	<input type="hidden" name="submitBtn" value="Submit" />								
		<button type="submit" id="signinBtn" class="btnSignin" ></button>
    </div>
</div>
</form>


</div>
</body>

</html>
