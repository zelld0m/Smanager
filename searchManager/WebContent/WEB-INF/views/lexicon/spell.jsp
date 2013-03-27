<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="lexicon" />
<c:set var="submenu" value="spell" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<!--  slider checkbox -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/linguistics.css" />" />
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/spell.css" />" />
<link type="text/css" rel="stylesheet" href="<spring:url value="/js/jquery/qTip/jquery.qtip.css" />" />
<script type="text/javascript" src="<spring:url value="/js/jquery/qTip/jquery.qtip.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/jquery/plugin.custom/jquery.cutebar.custom.js" />"></script>
<script type="text/javascript" src="<spring:url value="/js/lexicon/spell.js" />"></script>

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
  <div class="clearB floatL w240">
    <div class="sideHeader">Filters</div>
    <div class="clearB floatL w230 padL5">
      <div class="box marT8">
        <h2 style="font-size: 12px;">Status</h2>
        <select id="status-filter" class="dropDownFilter mar10 w200">
          <option value=""></option>
          <option value="new">New</option>
          <option value="modified">Updated</option>
          <option value="pending">Pending</option>
          <option value="not-modified">Published</option>
        </select>
      </div>

      <div class="box marT8">
        <h2 style="font-size: 12px;">Search Term</h2>
        <input id="searchTerm-filter" type="text" maxlength="100" class="mar10 w200"></input>
      </div>
      
      <div class="box marT8">
        <h2 style="font-size: 12px;">Suggestion</h2>
        <input id="suggestion-filter" type="text" maxlength="100" class="mar10 w200"></input>
      </div>
      <div id="action-buttons" class="floatR marT20">
          <div class="button-group">
            <a class="new-button" href="javascript:void(0);" id="clear-button">Clear</a>
          </div>
      </div>
    </div>
  </div>
</div>
<!--  end left side -->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
  <div class="floatL w730 titlePlacer breakWord">
    <h1 class="padT7 padL15 fsize20 fnormal">Did You Mean</h1>
  </div>
  <div class="clearB"></div>
  <div class="circlePreloader" id="preloader"><img src="../images/ajax-loader-circ.gif"></div>
  <div id="ruleStatus"></div>
  <%--
  <div id="ruleStatusTemplate" class="clearB floatR farial fsize12 fDGray txtAR w730 GraytopLine">
    <div class="txtAL w730 minHeight36" style="background: #e8e8e8">
      <div class="floatL padT10 padL10" style="width: 70%">
        <div id="versionHolder" style="">
          <label class="floatL wAuto padL5 fsize11 fLgray">
            <span title="Download Rule Versions">
              <img id="downloadVersionIcon" class="pointer" src="<spring:url value="/images/icon_download.gif"/>" alt="Download">
            </span>
          </label> 
          <label class="floatL marTn7">
            <a id="backupBtn" title="Backup Now" href="javascript:void(0);" class="btnGraph btnBackUp clearfix">
              <div class="btnGraph btnBackUp"></div>
            </a>
          </label>
          <label class="floatL wAuto marRL5 fLgray2">|</label>
        </div>
        <div id="commentHolder" style="display: none;">
          <label class="floatL wAuto padL5 fsize11 fLgray">
            <span id="commentIcon" title="Rule Comment">
              <img src="<spring:url value="/images/icon_comment.png" />" class="pointer">
            </span>
          </label>
        </div>
        <div id="statusHolder" style="display: none;">
          <label class="floatL wAuto marRL5 fLgray2">|</label>
          <label class="floatL wAuto">Status:</label>
          <label class="floatL wAuto padL5 fsize11 fLgray">
            <span id="status"></span>
            <span id="statusMode" class="fsize11 forange padL5"></span>
          </label>
        </div>
        <div id="publishHolder" style="display: none;">
          <label class="floatL wAuto marRL5 fLgray2">|</label>
          <label class="floatL wAuto">Last Published:</label>
          <label class="padL5 fLgray fsize11">
            <span id="statusDate"></span>
          </label>
        </div>
      </div>
      <div class="floatR marL8 marR3 padT5">
	    <a href="javascript:void(0);" id="add-button" class="buttons btnGray clearfix button-group-0" style="display:none;"><div class="buttons fontBold">Add</div></a>
	    <a href="javascript:void(0);" id="edit-button" class="buttons btnGray clearfix button-group-0" style="display:none;"><div class="buttons fontBold">Edit</div></a>
	    <a href="javascript:void(0);" id="submit-button" class="buttons btnGray clearfix button-group-0" title="Submit for approval" style="display:none;"><div class="buttons fontBold">Submit</div></a>
	    <a href="javascript:void(0);" id="save-button" class="buttons btnGray clearfix button-group-1" style="display:none;"><div class="buttons fontBold">Save</div></a>
	    <a href="javascript:void(0);" id="cancel-button" class="buttons btnGray clearfix button-group-1" style="display:none;"><div class="buttons fontBold">Cancel</div></a>
      </div>
    </div>
    <div class="clearB"></div>
  </div>
  --%>
  <div class="clearB"></div>
  <div id="spell-rules" class="padT20" style="min-height: 600px; max-width: 100%; display:none;">
    <div id="action-buttons" class="floatR marL8 marR3 padB5" style="display:none;">
      <span class="button-group">
        <a href="javascript:void(0);" id="add-button" class="new-button button-group-0" style="display:none;">Add</a>
        <a href="javascript:void(0);" id="edit-button" class="new-button button-group-0" style="display:none;">Edit</a>
      </span>
      <span class="button-group">
        <a href="javascript:void(0);" id="save-button" class="new-button button-group-1" style="display:none;">Save</a>
        <a href="javascript:void(0);" id="cancel-button" class="new-button button-group-1" style="display:none;">Cancel></a>
      </span>
    </div>
    <div class="clearB"></div>
    <div class="linguistics">
      <div id="topPaging"></div>
      <div class="clearB"></div>
      <div style="max-height: 600px; overflow-y: auto">
        <table id="spell-table" width="100%" class="fsize12 tblAlpha marT8">
          <tr id="header">
            <th width="50%">Search Terms</th>
            <th width="50%">Suggestions</th>
          </tr>
          <tr id="itemTemplate" class="spell-rule" style="display:none;">
            <td id="searchTerms" class="term-list"></td>
            <td id="suggestions" class="term-list"></td>
          </tr>
          <tr id="noResultsFound" style="display:none;">
             <td colspan="2" class="txtAL">No results found.</td>
          </tr>
          <tr id="spell-table-footer" style="display:none;">
            <td colspan="2">
                  &lt;Click here to add new rule&gt;
            </td>
          </tr>
        </table>
      </div>
      <div class="clearB"></div>
      <div id="bottomPaging"></div>
    </div>
    <div class="clearB"></div>
    <div id="templates" style="display:none;">
	  <div class="icons">
	    <!-- visible when rule is pending approval/publishing in edit mode -->
	    <a href="javascript:void(0);" title="Locked" id="edit-locked"><img src="<spring:url value="/images/noedit.png" />"></img></a>
	    <!-- visible in edit/add modes -->
	    <a href="javascript:void(0);" title="Delete" id="delete-link"><img src="<spring:url value="/images/delete_icon.png" />"></img></a>
	    <!-- revert to original value | visible in edit mode -->
	    <a href="javascript:void(0);" title="Revert" id="undo-link"><img src="<spring:url value="/images/icon-undo.png" />"></img></a>
	  </div>
	</div>
  </div>
</div>
<%@ include file="/WEB-INF/includes/footer.jsp"%>
