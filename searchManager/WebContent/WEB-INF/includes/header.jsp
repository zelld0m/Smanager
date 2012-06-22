<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=100" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Search Manager</title>
  <!-- jQuery dependencies -->
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery-1.7.1.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery-ui-1.8.16.custom.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.core.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.autocomplete.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.button.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.combobox.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.datepicker.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.sortable.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.tabs.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.ui.widget.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/min.1.8.16/jquery.effects.slide.min.js" />" ></script>

  <!-- TODO: Dynamically modify mall based on logged user -->
  <spring:eval expression="T(com.search.manager.service.UtilityService).getStoreName()" var="store" />
  <spring:eval expression="T(com.search.manager.service.UtilityService).getStoreLogo()" var="storeLogo" />
  <spring:eval expression="T(com.search.manager.service.UtilityService).getSolrConfig()" var="solrConfig" />

  <script>
	var contextPath = "<%=request.getContextPath()%>";	
	var GLOBAL_contextPath = "<%=request.getContextPath()%>";	
	var allowModify = <%= request.isUserInRole("CREATE_RULE") %>;
	var GLOBAL_store = "${store}";
	var GLOBAL_solrConfig = '${solrConfig}';
	var GLOBAL_solrUrl = $.parseJSON(GLOBAL_solrConfig)["solrUrl"];
  </script>
  
  <link type="text/css" rel="stylesheet" href="<spring:url value="/css/cssReset.css" />">
  <link type="text/css" rel="stylesheet" href="<spring:url value="/css/default.css" />">
  <!--  theme -->
  <link type="text/css" rel="stylesheet" href="<spring:url value="/css/theme/default/style.css" />">

  <!-- cross-browser css compatibility util -->
  <script type="text/javascript" src="<spring:url value="/js/oscss.js" />"></script>
  <script type="text/javascript" src="<spring:url value="/js/init-validate.js" />" ></script>
  
  <!-- DWR dependencies -->
  <script type="text/javascript" src="<spring:url value="/dwr/util.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/engine.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/AuditServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/CommentServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/UtilityServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/StoreKeywordServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/ExcludeServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/ElevateServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/RelevancyServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/DeploymentServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/CategoryServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/RedirectServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/DAOCacheServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/SecurityServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/UserSettingServiceJS.js"/>"></script>
  
  <!-- jQuery custom plugin -->
  <script type="text/javascript" src="<spring:url value="/js/jquery-array-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery-string-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery-store-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.pagination.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.sidepanel.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.auditpanel.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.download.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.preview.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.activerule.custom.js" />" ></script>
  
  <!--  /TinyMCE -->
  <script type="text/javascript"  src="<spring:url value="/js/jquery/tinymce-3.5b3/tiny_mce/jquery.tinymce.js" />"></script>  
  
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/ext/smoothness/jquery-ui.css" />" />
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/ext/smoothness/ui.theme.css" />" />
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/qTip/jquery.qtip.css" />" />  
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/checkbox/checkbox.custom.css" />" />  

   <!-- /Qtip2 -->
  <script type="text/javascript" src="<spring:url value="/js/jquery/qTip/jquery.qtip.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/qTip/jquery.qtip.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/qTip/jquery.qtip.common.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.cookie-1.0.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.validation.js" />" ></script>
  <!--[if lt IE 9]><script type="text/javascript" src="<spring:url value="/js/jquery/canvas/excanvas.js" />"></script><![endif]-->
  <script type="text/javascript" src="<spring:url value="/js/jquery/canvas/jquery.tagcanvas-1.13.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/checkbox/jquery.checkbox.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.ticker.custom.js" />" ></script>

  <!--  scroller  -->
	<script type="text/javascript" src="<spring:url value="/js/tinyscrollbar/jquery.tinyscrollbar.min.js" />"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#scrollbar1').tinyscrollbar();	
		});
	</script>	
  
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.backgroundPosition.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.flip.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/microgallery/jquery.microgallery.js" />" ></script>
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/microgallery/css/style.css" />" />

 <!-- jQuery highcharts, licensed graph -->
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/highcharts.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/modules/canvas-tools.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/modules/exporting.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/themes/dark-blue.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/themes/dark-green.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/themes/gray.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/themes/grid.js" />" ></script> --%>
<%--   <script type="text/javascript" src="<spring:url value="/js/jquery/highcharts/themes/skies.js" />" ></script> --%>
  
  <script type="text/javascript" src="<spring:url value="/js/jquery/jqplot/jquery.jqplot.min.js" />" ></script>
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jqplot/jquery.jqplot.min.css" />" />

  <!-- /Init -->	  
  <script type="text/javascript" src="<spring:url value="/js/init.js" />" ></script>
</head>

<body>

<div class="cssReset">
 
 <!--PC Mall Header-->
 <div class="clearB floatL bgTopHeader">  
    <div class="mar0 w980 posRel">
      <table width="980" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td align="left" class="padTB5">
          	<div class="clearB floatL farial fsize12 fLgray2">
	          	<img src="<spring:url value="/js/ajaxsolr/images/user.png" />" style="margin-bottom:-3px"> Welcome <span class="fbold"><sec:authentication property="principal.username" /></span> <span class="fsize10">|</span> 
			    <span class="topHelp fLALink fdecoNone fsize11 txtCapitalize"><a href=""> help</a></span> <span class="fsize10">|</span>
			    <span class="fLALink fdecoNone fsize11 txtCapitalize"><a href="<spring:url value="/logout" />">Logout</a></span>
		    </div>
          </td>
          <td align="right" class="padTB5">	          
         	  <div class="clearB"></div>
         	  <div id="menuTop">
         	  <ul class="topNavMenu topmenu">
		      		<li class="submenu last">
		         		<sec:authorize access="hasRole('MANAGE_USER')">
		         			<span>Search Server</span>
		         	  		<select id="select-server"></select>
		         	  	</sec:authorize>
		        	</li>
		        </ul>
         	  </div>
	      </td>
        </tr>
      </table>
      <div class="companyLogoImg">
      	<a href="javascript:void(0);">
      		<img src="<spring:url value="${storeLogo}" />">
      	</a>
      	<div class="clearB"></div>
      </div>
    </div>
  </div>
  
  <div class="clearB"></div>
