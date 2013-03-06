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
    <div class="sideHeader">Site Updates</div>
    <div class="clearB floatL w230 padL5">
      <ul class="listSU fsize11 marT10">
        <li><p class="notification">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li class="alt"><p class="notification">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li><p class="alert">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li class="alt"><p class="notification">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li><p class="alert">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li class="alt"><p class="alert">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li><p class="alert">
            <strong>lorem 2 items</strong> Etiam dui justo, consect<br> <a href="#">20 minutes ago</a>
          </p></li>
        <li class="textAR"><a href="#">see all updates &raquo;</a></li>
      </ul>
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
  <div style="width: 95%" class="marT20 mar0">
    <div class="clearB"></div>
    <div class="linguistics marT20">
      <div style="height: 600px; overflow-y: auto">
        <table id="spell-table" width="100%" class="fsize12 tblAlpha marT8">
          <tr>
            <th width="50%">Misspellings</th>
            <th width="50%">Suggestions</th>
          </tr>
          <tr id="itemTemplate" style="display:none;">
            <td>
              <div class="misspell-list term-list">
                  &lt;Click here to add new term&gt;
              </div>
            </td>
            <td>
              <div class="suggest-list term-list">
                  &lt;Click here to add new term&gt;
              </div>
              <div class="icons" style="display: none;">
                <a href="javascript:void(0);" title="Locked" id="edit-locked"><img src="<spring:url value="/images/noedit.png" />"></img></a>
                <a href="javascript:void(0);" title="Edit" id="edit-link"><img src="<spring:url value="/images/edit_pencil.png" />"></img></a>
                <a href="javascript:void(0);" title="Delete" id="delete-link"><img src="<spring:url value="/images/delete_icon.png" />"></img></a>
                <a href="javascript:void(0);" title="Save" id="save-link"><img src="<spring:url value="/images/save_icon.png" />"></img></a>
                <a href="javascript:void(0);" title="Cancel" id="cancel-link"><img src="<spring:url value="/images/stop_icon.png" />"></img></a>
              </div>
            </td>
          </tr>
          <tr id="spell-table-footer">
            <td colspan="2">
                  &lt;Click here to add new rule&gt;
            </td>
          </tr>
        </table>
      </div>
    </div>
    <div class="clearB"></div>
	</div>
</div>
<%@ include file="/WEB-INF/includes/footer.jsp"%>
