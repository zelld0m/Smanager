<%@ include file="/WEB-INF/includes/includes.jsp"%>
<!doctype html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		
		<title>ecost</title>
		<link href="<spring:url value="/css/webadmin.css" htmlEscape="true" />" rel="stylesheet" type="text/css">
		<style>
		.widgettibg {
			background:url('<spring:url value="/images/widgettibg.png" htmlEscape="true" />') no-repeat;
			width:711px;
			height:602px;
			margin:30px auto
		}
		.loginbg {
			background: #87A2B2; /* old browsers */
			background: -moz-linear-gradient(top, #87A2B2 0%, #65757F 16%, #424C54 33%, #3C454C 96%); /* firefox */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #87A2B2), color-stop(16%, #65757F), color-stop(33%, #424C54), color-stop(96%, #3C454C)); /* webkit */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#87A2B2', endColorstr='#3C454C', GradientType=0 ); /* ie */
		}
		</style>
	</head>
	<body class="loginbg" >
		<div style="margin:30px auto; width:100%; position:fixed">
			<div class="widgettibg">
				<div class="webmanagerlogo"><img src="<spring:url value="/images/webmanagerlogo.png" htmlEscape="true" />" alt=""></div>  
					<h1>Exception</h1>
					An exception occurred, it has been logged.
					<a href="<spring:url value="/" htmlEscape="true" />" >Click here to try again.</a>
				</div>
			</div>
		</div>
	</body>
</html>