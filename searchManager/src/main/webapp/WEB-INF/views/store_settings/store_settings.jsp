<%@ include file="/WEB-INF/includes/includes.jsp" %>
<%@ include file="/WEB-INF/includes/header.jsp" %>

<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="store_settings"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/dwr/interface/PropertiesManagerServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/dwr/interface/PropertiesReaderServiceJS.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/util/StringBuilder.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/js/store_settings/store_settings.js"/>"></script>

<!-- Left Menu-->
<div class="clearB floatL sideMenuArea">
    <div class="clearB floatL w240">
        <div>&nbsp;</div>
        <div class="clearB"></div>
    </div>
</div>
<!--Left Menu-->

<!--Main Menu-->
<div class="floatL w730 marL10 marT27">
    <div class="floatL w730 titlePlacer">		
        <div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
            <span id="titleText">Store Settings</span>
            <span id="titleHeader" class="fLblue fnormal"></span>
        </div>
    </div>

    <div class="clearB"></div>
    
    <div id="no_store_message" style="width:95%" class="dashboard marT20 mar0 fsize14">
        <p>
            Unable to find the store properties XML file, please check if it is in the 
            appropriate directory and reload the page.
        </p>
    </div>
    
    <div id="store_config" style="width:95%" class="dashboard marT20 mar0">
        <div id="store_tabs" class="tabs">
            <ul>
                <!-- Dynamic tabs to be added here! -->  
            </ul>
        </div>

        <div id="settingsSaveBtnDiv" align="right" class="padR15 marT10" style="display: none;">
            <a id="settingsSaveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">
                <div class="buttons fontBold">Save</div>
            </a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>