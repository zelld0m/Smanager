<%@ include file="/WEB-INF/includes/includes.jsp"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<c:set var="topmenu" value="lexicon" />
<c:set var="submenu" value="spell" />
<%@ include file="/WEB-INF/includes/menu.jsp"%>

<!--  slider checkbox -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/linguistics.css" />" />
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/spell.css" />" />
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
          <option value="published">Published</option>
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
  <div class="clearB"></div>
  <div id="spell-rules" class="padT20" style="min-height: 600px; max-width: 100%; display:none;">
    <div class="floatL">
      <label for="max-suggest" style="font-size:12px">Max suggestion count</label>
      <input type="text" id="max-suggest" maxlength="3" size="5" disabled/>
    </div>
    <div id="action-buttons" class="floatR marL8 marR3 padB5" style="display:none;">
      <span class="button-group">
        <a href="javascript:void(0);" id="add-button" class="new-button button-group-0" style="display:none;">Add</a>
        <a href="javascript:void(0);" id="edit-button" class="new-button button-group-0" style="display:none;">Edit</a>
      </span>
      <span class="button-group">
        <a href="javascript:void(0);" id="save-button" class="new-button button-group-1" style="display:none;">Save</a>
        <a href="javascript:void(0);" id="cancel-button" class="new-button button-group-1" style="display:none;">Cancel</a>
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
	    <!-- visible in edit/add modes -->
	    <a href="javascript:void(0);" title="Delete" id="delete-link"><img src="<spring:url value="/images/delete_icon.png" />"></img></a>
	    <!-- revert to original value | visible in edit mode -->
	    <a href="javascript:void(0);" title="Revert" id="undo-link"><img src="<spring:url value="/images/icon-undo.png" />"></img></a>
	  </div>
	</div>
  </div>
</div>
<%@ include file="/WEB-INF/includes/footer.jsp"%>
