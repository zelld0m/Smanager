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

  <spring:eval expression="T(com.search.manager.service.UtilityService).getSolrConfig()" var="solrConfig" />
  <spring:eval expression="T(com.search.manager.service.UtilityService).getStoreParameters()" var="storeParameters" />
  <spring:eval expression="T(com.search.manager.service.UtilityService).getIndexedSchemaFields()" var="schemaFields" />
  <spring:eval expression="T(com.search.manager.service.UtilityService).getStoreId()" var="storeId" />
  <spring:eval expression="T(com.search.manager.service.UtilityService).getStoreName()" var="storeName" />
  
  <script>
	var allowModify = <%= request.isUserInRole("CREATE_RULE") %>;
    
	// Request server details
	var GLOBAL_scheme = "<%=request.getScheme()%>";  
    var GLOBAL_serverName = "<%=request.getServerName()%>";  
    var GLOBAL_serverPort = "<%=request.getServerPort()%>";  
	var GLOBAL_contextPath = "<%=request.getContextPath()%>";	
	
	//store schema indexed fields
	var GLOBAL_schemaFields = $.parseJSON('${schemaFields}');
	var GLOBAL_indexedFields = GLOBAL_schemaFields["indexedFields"];
	var GLOBAL_indexedWildcardFields = GLOBAL_schemaFields["indexedWildcardFields"];
	
	if(GLOBAL_indexedWildcardFields){
		for(var i=0; i < GLOBAL_indexedWildcardFields.length; i++) {
			GLOBAL_indexedWildcardFields[i] = '^' + GLOBAL_indexedWildcardFields[i].replace(/\*/, '.*') + '$';
		}
	}
	
	// Store parameters
	var GLOBAL_storeParameters = $.parseJSON('${storeParameters}');
	var GLOBAL_username = GLOBAL_storeParameters["username"];
	var GLOBAL_solrSelectorParam = GLOBAL_storeParameters["solrSelectorParam"];
	var GLOBAL_storeId = GLOBAL_storeParameters["storeId"];
	var GLOBAL_storeCore = GLOBAL_storeParameters["storeCore"];
	var GLOBAL_storeName = GLOBAL_storeParameters["storeName"];
	var GLOBAL_storeFacetName = GLOBAL_storeParameters["storeFacetName"];
	var GLOBAL_storeFacetTemplate = GLOBAL_storeParameters["storeFacetTemplate"];
	var GLOBAL_storeFacetTemplateName = GLOBAL_storeParameters["storeFacetTemplateName"];
	
	var GLOBAL_storeGroupMembership = GLOBAL_storeParameters["storeGroupMembership"];
	
	var GLOBAL_storeGroupLookup = {"BD":false,"Store":false,"PCM":false,"MacMall":false,"PCMBD":false,"MacMallBD":false};
	var GLOBAL_storeGroupTotal = GLOBAL_storeGroupMembership.length;
	
	if(GLOBAL_storeGroupTotal>0){
		for(var i=0; i<GLOBAL_storeGroupTotal; i++){
			GLOBAL_storeGroupLookup[GLOBAL_storeGroupMembership[i]]= true;
		}
	};
	
	var GLOBAL_BDGroup = GLOBAL_storeGroupLookup['BD'];
	var GLOBAL_StoreGroup = GLOBAL_storeGroupLookup['Store'];
	var GLOBAL_PCMGroup = GLOBAL_storeGroupLookup['PCM'];
	var GLOBAL_MacMallGroup = GLOBAL_storeGroupLookup['MacMall'];
	var GLOBAL_PCMBDGroup = GLOBAL_storeGroupLookup['PCMBD'];
	var GLOBAL_MacMallBDGroup = GLOBAL_storeGroupLookup['MacMallBD'];
	
	var GLOBAL_solrConfig = '${solrConfig}';
	var GLOBAL_solrUrl = $.parseJSON(GLOBAL_solrConfig)["solrUrl"];
	var GLOBAL_isFromGUI = $.parseJSON(GLOBAL_solrConfig)["isFmGui"];
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
  <script type="text/javascript" src="<spring:url value="/dwr/interface/EnumUtilityServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/FacetSortServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/DemoteServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/RelevancyServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/DeploymentServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/CategoryServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/RedirectServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/DAOCacheServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/SecurityServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/UserSettingServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/RuleVersionServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/LinguisticsServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/TopKeywordServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/ReportGeneratorServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/RuleTransferServiceJS.js"/>"></script>  
  <script type="text/javascript" src="<spring:url value="/dwr/interface/KeywordTrendsServiceJS.js"/>"></script>
  <script type="text/javascript" src="<spring:url value="/dwr/interface/ReportGeneratorServiceJS.js"/>"></script>  
  <script type="text/javascript" src="<spring:url value="/dwr/interface/ZeroResultServiceJS.js"/>"></script>
  
  <!-- jQuery custom plugin -->
  <script type="text/javascript" src="<spring:url value="/js/utility.custom/jquery-array-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/utility.custom/jquery-date-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/utility.custom/jquery-string-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/utility.custom/jquery-store-functions.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.pagination.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.viewaudit.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.comment.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.sidepanel.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.viewfacetvalues.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.auditpanel.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.download.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.preview.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.xmlpreview.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.activerule.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.version.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.rulestatus.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.ruleitem.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.addproduct.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.slidecheckbox.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.importas.custom.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.rk-message-type-1.0.custom.js" />" ></script>
  
  <script type="text/javascript"  src="<spring:url value="/js/jquery/searchabledropdown-1.0.8/jquery.searchabledropdown-1.0.8-modified.src.js" />"></script>  
 
  <!--  msDropdown plugin-->
  <link rel="stylesheet" type="text/css" href="<spring:url value="/js/jquery/msdropdown/dd.css" />" />
  <script type="text/javascript" src="<spring:url value="/js/jquery/msdropdown/jquery.dd.min.js" />"></script>
 
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
  <script type="text/javascript" src="<spring:url value="/js/jquery/utility.custom/jquery.cookie-1.0.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.validation.js" />" ></script>
  <!--[if lt IE 9]><script type="text/javascript" src="<spring:url value="/js/jquery/canvas/excanvas.js" />"></script><![endif]-->
  <script type="text/javascript" src="<spring:url value="/js/jquery/canvas/jquery.tagcanvas-1.13.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.ticker.custom.js" />" ></script>

  <!--  scroller  -->
	<script type="text/javascript" src="<spring:url value="/js/tinyscrollbar/jquery.tinyscrollbar.min.js" />"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$('#scrollbar1').tinyscrollbar();	
		});
	</script>	

  <!-- jQuery alert  -->
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.alerts/jquery.alerts.js" />" ></script>
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jquery.alerts/jquery.alerts.css" />" />
    
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.backgroundPosition.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jquery.flip.min.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/microgallery/jquery.microgallery.js" />" ></script>
  <script type="text/javascript" src="<spring:url value="/js/jquery/jqplot/jquery.jqplot.min.js" />" ></script>
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/microgallery/css/style.css" />" />
  <link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/jqplot/jquery.jqplot.min.css" />" />

  <!-- /Init -->	  
  <script type="text/javascript" src="<spring:url value="/js/init.js" />" ></script>
</head>

<body>

<div class="cssReset">
 
 <!--PC Mall Header-->
 <div class="clearB floatL bgTopHeader">  
    <div class="mar0 w980 posRel">
     <table width="980" border="0" style="height: 49px" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td align="left" class="padTB5">
          	<div class="clearB floatL farial fsize12 fLgray2">
	          	<img src="<spring:url value="/js/jquery/ajaxsolr.custom/images/user.png" />" style="margin-bottom:-3px"> Welcome <span class="fbold"><sec:authentication property="principal.username" /></span> <span class="fsize10">|</span> 
			    <span class="topHelp fLALink fdecoNone fsize11 txtCapitalize"><a href=""> help</a></span> <span class="fsize10">|</span>
			    <span class="fLALink fdecoNone fsize11 txtCapitalize"><a href="<spring:url value="/logout" />">Logout</a></span>
		    </div>
          </td>
          <td align="right" class="padTB5">	          
         	  <div class="clearB"></div>
         	  <div id="menuTop">
         	  <ul class="topNavMenu topmenu">
		      		<li class="submenu">
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
      		<script type="text/javascript">
      			document.write('<a href="javascript:void(0);"><img src="' + GLOBAL_contextPath + '/images/logo-' + GLOBAL_storeId + '.png"></a>');
      		</script>
      	<div class="clearB"></div>
      </div>
    </div>
  </div>
  
  <div class="clearB"></div>
