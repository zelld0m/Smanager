<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="workflow"/>
<c:set var="submenu" value="import"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<script type="text/javascript" src="<spring:url value="/js/workflow/import.js" />"></script>
<spring:eval expression="@propertiesDwrService.getAllStoreProperties()" var="allStoreProperties" />
<script>
    var hasPublishRule = <%= request.isUserInRole("PUBLISH_RULE")%>;
    var GLOBAL_allStoreProperties = $.parseJSON('${allStoreProperties}');
</script>

<link type="text/css" rel="stylesheet" href="<spring:url value="/css/workflow/workflow.css" />">

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
    <div class="clearB floatL w240">
        <div class="sidebarHeader farial fsize16 fwhite bluebgTitle">&nbsp;</div>
    </div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="floatL w980 marT27 txtAL">
    <div class="floatL w980 titlePlacer breakWord">
        <h1 id="titleText" class="padT7 padL15 fsize20 fnormal floatL"></h1>
        <div class="floatR padT7 autoImportDiv">
		  	<div class="floatL fbold fsize14 marT4 marR5"><label class="floatL wAuto marRL5 fLgray2">|</label> Auto-import: </div>
		  	<div class="floatR marT4 marR5"><a class="infoIcon autoImportIcon" href="javascript:void(0);" title="What's this?"><img src="/searchManager/images/icon_info.png"></a></div>
		  	<div class="floatR marR5"><input id="autoimport" type="checkbox" class="firerift-style-checkbox on-off autoImport"/></div>
	  	<div class="clearB"></div>
      </div>
    </div>
    <div class="clearB"></div>

    <!-- Start Main Content -->
    <div style="width:97%" class="dashboard marT20 mar0">
        <!-- tabs -->
        <div id="import" class="tabs">
            <ul>
                <li><a href="#elevateTab"><span>Elevate</span></a></li>
                <li><a href="#excludeTab"><span>Exclude</span></a></li>
                <li><a href="#demoteTab"><span>Demote</span></a></li>
                <li><a href="#facetSortTab"><span>Facet Sort</span></a></li>
                <li><a href="#queryCleaningTab"><span>Redirect Rule</span></a></li>
                <li><a href="#rankingRuleTab"><span>Relevancy Rule</span></a></li>
                <li><a href="#didYouMeanTab"><span>Did You Mean</span></a></li>
                <li><a href="#typeaheadTab"><span>Typeahead</span></a></li>
            </ul>

            <div class="minHeight400" id="elevateTab"></div>
            <div class="minHeight400" id="excludeTab"></div>
            <div class="minHeight400" id="demoteTab"></div>
            <div class="minHeight400" id="facetSortTab"></div>
            <div class="minHeight400" id="queryCleaningTab"></div>
            <div class="minHeight400" id="rankingRuleTab"></div>
            <div class="minHeight400" id="didYouMeanTab"></div>
            <div class="minHeight400" id="typeaheadTab"></div>
        </div><!--  end tabs -->

        <div id="tabContentTemplate" style="display: none">
            <div class="filter padT5 fsize12 marT8">
                <div id="ruleFilterDiv" class="floatL">
                    <span>Show:</span> 
                    <select id="ruleFilter">
                        <option value="rejected">Previously Rejected Rules</option>
                        <option value="nonrejected">Non-rejected Rules</option>
                        <option value="all">All Rules</option>
                    </select>
                </div>
                <a id="searchBtn" href="javascript:void(0);">
                    <img class="marR5 marLn4 marT1 floatR posRel" align="absmiddle" src="/searchManager/js/jquery/ajaxsolr.custom/images/btn_GO.png">
                </a>
                <div class="searchBoxHolder w150 floatR marT1 marR8">
                    <input type="text" class="farial fsize12 fgray pad3 w145" id="keyword" name="keyword">
                </div>
            </div>
            <div class="clearB"></div>
            <!-- Pagination-->     
            <div class="floatR padT10">
                <a href="javascript:void(0);" id="downloadIcon" style="display:none;"><div class="btnGraph btnDownload"></div></a>
            </div>
            <div id="resultsTopPaging" class="marTn2"></div>
            <div class="clearB"></div>
            <!--end Pagination-->

            <div class="">
                <table class="tblItems w100p marT5">
                    <tbody>
                        <tr>
                            <th width="48px" id="selectAll"><input id="selectAllCheckbox" type="checkbox" title="Import All"/></th>
                            <th width="180px">Rule Name
                                <img id="ruleNameSort" class="ruleNameSort pointer" src="<spring:url value="/images/tablesorter/bg.gif" />"/>
                                <input id="ruleNameInp" name="sortGroup" type="radio" class="sortGroup" style="display: none;"/>
                            </th>
                            <th width="50px">Published Date 
                                <img id="publishDateSort" class="publishDateSort pointer" src="<spring:url value="/images/tablesorter/bg.gif" />"/>
                                <input id="pubDateInp" name="sortGroup" type="radio" class="sortGroup" style="display: none;"/>
                            </th>
                            <th width="85px">Import Type</th>
                            <th>Import As</th>
                        </tr>
                    <tbody>
                </table>
            </div>
            <div>
                <table id="rule" class="tblItems w100p">
                    <tbody>
                        <tr id="ruleItemPattern" class="ruleItem" style="display: none">
                            <td width="48px" class="txtAC" id="select">
                                <input class="import selectItem" type="checkbox"><div class="approve_btn"><a href="javascript:void(0);" id="link_btn">Import</a></div>
                                <input class="reject selectItem" type="checkbox"><div class="reject_btn"><a href="javascript:void(0);" id="link_btn">Reject</a></div>
                            </td>
                            <td width="150px" id="ruleRefId">
                                <img class="previewIcon pointer" src="<spring:url value="/images/icon_reviewContent.png" />" alt="Preview Content" title="Preview Content"> 
                                <p class="breakWord" id="ruleName"></p>
                                <!--p id="ruleId" class="fsize11 breakWord"></p-->
                            </td>
                            <td width="50px" class="txtAL" id="publishDate">
                                <p id="publishDate" class="fsize11"></p>
                            </td>
                            <td width="85px" class="txtAC" id="type">
                                <select id="importTypeList">
                                </select>
                            </td>
                            <td class="txtAL" id="importAs">
                                <select id="importAsList">
                                    <option value="">Import as New Rule</option>
                                </select>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="mar0">
                <div id="resultsBottomPaging"></div>	
            </div>
            <div id="actionBtn" class="floatR marT10 fsize12 border pad10 marB20" style="background: #f3f3f3;width:97.5%"">
                <h3 style="border:none;">Import Rule Guidelines</h3>
                <div class="fgray padL10 padR10 padB15 fsize11">
                    <p align="justify">
                        Before importing any rule, it is advisable to review each one. Click on <strong>Preview Content</strong> to view the rule details.<br/><br/>
                        If the published rule is ready to be imported, click on <strong>Import</strong>. Provide notes in the <strong>Comment</strong> box.
                    <p>
                </div>
                <label class="floatL padL13 w100"><span class="fred">*</span> Comment: </label>
                <label class="floatL w480"><textarea id="comment" class="w510" style="height:32px"></textarea></label>
                <div class="clearB"></div>
                <div align="right" class="padR15 marT10">
                    <a id="sbmtBtn" href="javascript:void(0);"
                       class="buttons btnGray clearfix"><div class="buttons fontBold">Submit</div>
                    </a>
                </div>
            </div>
            <div class="clearB"></div>
        </div>
    </div><!-- End Main Content -->
</div><!-- End Right Side --> 

<%@ include file="/WEB-INF/includes/footer.jsp" %>	
