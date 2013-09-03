<%@ include file="/WEB-INF/includes/includes.jsp" %>
<%@ include file="/WEB-INF/includes/header.jsp" %>

<c:set var="topmenu" value="setting"/>
<c:set var="submenu" value="store_settings"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/dwr/interface/PropertiesManagerServiceJS.js"/>"></script>
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

    <div style="width:95%" class="dashboard marT20 mar0">
        <div id="store_tabs" class="tabs">
            <ul>
              <!-- Dynamic tabs to be added here! -->  
            </ul>
        </div>
    </div>

    <!--    <div id="monitool" class="tabs marT10">
            <ul>
                <li><a href="#mail"><span>Mail</span></a></li>
                <li><a href="#settings"><span>Settings</span></a></li>
            </ul>
    
            <div id="mail" class="txtAL padL0 marT0">
                <table class="fsize12 marT20 marL20">
                    <tr>
                        <td colspan="2">
                            <h2 class="padT5">Mail Server</h2>
                        </td>
                    </tr>
                    <tr>
                        <td>Host:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailServerHost"/></td>
                    </tr>
                    <tr>
                        <td>Port:&nbsp;</td>
                        <td><input type="text" class="w45" id="mailServerPort"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td colspan="2">
                            <h2 class="padT5">Mail Template</h2>
                        </td>
                    </tr>
    
                    <tr>
                        <td>From:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailTemplateFrom"/></td>
                    </tr>
                    <tr>
                        <td>CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailTemplateCC"/></td>
                    </tr>
                    <tr>
                        <td>BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailTemplateBCC"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Debug:&nbsp;</td>
                        <td>
                            <input id="debug" 
                                   type="checkbox" 
                                   class="firerift-style-checkbox on-off"/>
                        </td>
                    </tr>
                </table>
    
                <table class="fsize12 marT20 marL20">
                    <tr>
                        <td colspan="2">
                            <h2 class="padT5">Notifications</h2>
                        </td>
                    </tr>
                    <tr>
                        <td>Pending Notification:&nbsp;</td>
                        <td>
                            <input id="pendingNotification" 
                                   type="checkbox" 
                                   class="firerift-style-checkbox on-off"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Approval Notification:&nbsp;</td>
                        <td>
                            <input id="approvalNotification" 
                                   type="checkbox" 
                                   class="firerift-style-checkbox on-off"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Push To Prod Notification:&nbsp;</td>
                        <td>
                            <input id="pushToProdNotification" 
                                   type="checkbox" 
                                   class="firerift-style-checkbox on-off"/>
                        </td>
                    </tr>
                    </tr>
                </table>
    
                <table class="fsize12 marT20 marL20">
                    <tr>
                        <td colspan="2">
                            <h2 class="padT5">Mail Workflow</h2>
                        </td>
                    </tr>
                    <tr>
                        <td>CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowCC"/></td>
                    </tr>
                    <tr>
                        <td>BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowBCC"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Approver Group:&nbsp;</td>
                        <td>
                            <select class="w245 mar0" id="mailWorkflowApproverGroup" style="cursor:pointer">
                                <option value="">All Roles</option>
                            </select>
                        </td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Pending CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowPendingCC"/></td>
                    </tr>
                    <tr>
                        <td>Pending BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowPendingBCC"/></td>
                    </tr>
                    <tr>
                        <td>Pending Subject:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowPendingSubject"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Approved CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowApprovedCC"/></td>
                    </tr>
                    <tr>
                        <td>Approved BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowApprovedBCC"/></td>
                    </tr>
                    <tr>
                        <td>Approved Subject:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowApprovedSubject"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Rejected CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowRejectedCC"/></td>
                    </tr>
                    <tr>
                        <td>Rejected BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowRejectedBCC"/></td>
                    </tr>
                    <tr>
                        <td>Rejected Subject:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowRejectedSubject"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Published CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowPublishedCC"/></td>
                    </tr>
                    <tr>
                        <td>Published BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowPublishedBCC"/></td>
                    </tr>
                    <tr>
                        <td>Published Subject:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowPublishedSubject"/></td>
                    </tr>
    
                    <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
    
                    <tr>
                        <td>Unpublished CC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowUnpublishedCC"/></td>
                    </tr>
                    <tr>
                        <td>Unpublished BCC:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowUnpublishedBCC"/></td>
                    </tr>
                    <tr>
                        <td>Unpublished Subject:&nbsp;</td>
                        <td><input type="text" class="w240" id="mailWorkflowUnpublishedSubject"/></td>
                    </tr>
                </table>
    
                <div align="right" class="padR15 marT10">
                    <a id="mailSaveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">
                        <div class="buttons fontBold">Save</div>
                    </a>
                </div>
            </div>
    
            <div id="settings" class="txtAL padL0 marT0">
                <table class="fsize12 marT20 marL20">
                    <tr>
                        <td>Site Domain:&nbsp;</td>
                        <td><input type="text" class="w240" id="siteDomain"/></td>
                    </tr>
                    <tr>
                        <td>Default Banner Linkpath Protocol:&nbsp;</td>
                        <td><input type="text" class="w240" id="defaultBannerLinkpathProtocol"/></td>
                    </tr>
                    <tr>
                        <td>Autoprefix Banner Linkpath Protocol:&nbsp;</td>
                        <td>
                            <input id="autoprefixBannerLinkpathProtocol" 
                                   type="checkbox" 
                                   class="firerift-style-checkbox on-off"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Default Banner Size:&nbsp;</td>
                        <td><input type="text" class="w240" id="defaultBannerSize"/></td>
                    </tr>
                    <tr>
                        <td>Allowed Banner Sizes:&nbsp;</td>
                        <td><input type="text" class="w240" id="allowedBannerSizes"/></td>
                    </tr>
                    <tr>
                        <td>Auto Export:&nbsp;</td>
                        <td>
                            <input id="autoExport" 
                                   type="checkbox" 
                                   class="firerift-style-checkbox on-off"/>
                        </td>
                    </tr>
                    <tr>
                        <td>Export Target:&nbsp;</td>
                        <td><input type="text" class="w240" id="exportTarget"/></td>
                    </tr>
                </table>
                
                <div align="right" class="padR15 marT10">
                    <a id="settingsSaveBtn" href="javascript:void(0);" class="buttons btnGray clearfix">
                        <div class="buttons fontBold">Save</div>
                    </a>
                </div>
            </div>
        </div>-->
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>