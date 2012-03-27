

<%@ include file="/WEB-INF/includes/includes.jsp" %>
<%@ include file="/WEB-INF/includes/header.jsp" %>

<link href="<spring:url value="/css/cssReset.css" />" rel="stylesheet" type="text/css">
<link href="<spring:url value="/css/pcmall_browseproducts.css" />" rel="stylesheet" type="text/css">
<link href="<spring:url value="/css/elevateExclude.css" />" rel="stylesheet" type="text/css">

<div class="cssReset">
  <!--PC Mall Header-->
  <div class="clearB floatL bgTopHeader">
    <div class="mar0 w980">
      <table width="980" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td align="left" valign="top"><div class="pcmallhdlogo"></div></td>
          <td align="right" valign="top"><div class="clearB floatR farial fsize12 fgray padTB10">Status: <span class="fgreen">Active</span></div>
            <div class="clearB floatR farial fsize12 fgray w350 fbold txtAR">Select Server:
              <select name="select" class="farial fsize14 fgray w225 padLR3 padTB4 border marL10">
                <option>vtorschdev02</option>
              </select>
            </div></td>
        </tr>
      </table>
    </div>
  </div>
  <!--PC Mall Header-->
  <div class="clearB"></div>
  <!--PC Mall Menu-->
  <div class="clearB floatR bgTopHeaderMnu">
    <div class="mar0 w980">
      <table width="770" border="0" cellspacing="0" cellpadding="0" align="right">
        <tr>
          <td align="right" valign="top"><ul class="mnuHeader farial fsize12 fbold">
              <li class="active"><a href="#">Browse Mall Products</a></li>
              <li><a href="#">Relevancy Fields</a></li>
              <li><a href="#">Synonyms</a></li>
              <li><a href="#">Big Bets</a></li>
              <li><a href="#">Banner</a></li>
              <li><a href="#">Search Redirect</a></li>
              <li><a href="#">Statistics</a></li>
              <li><a href="#">Data Migrator</a></li>
            </ul></td>
        </tr>
      </table>
    </div>
  </div>
  <!--PC Mall Menu-->
  <div class="mar0 w980">
    <!--Left Menu-->
    <div class="clearB floatL">
      <!--Current Selection-->
      <div class="clearB floatL w240">
        <div class="leftTitle farial fsize16 fwhite">Keyword</div>
        <div class="leftKeyword clearB floatL w230 padL10 fsize12">
        	<div class="w110 marT10">Keyword:</div>
            <div class=""><input type="text" class="pad2 border w220"></div>
            <div class="w110 marT8">Related Keywords:</div>
            <div><a href="/" class="btn-add marR10">add</a><input type="text" class="pad2 border w200"> </div>
            <div class="floatL marT10 marB20">
            <p class="borderB" style="padding-bottom:3px; width:96%">Select Visibility:</p>
            	<ul class="listVisibility marT8">
                	<li class="w110"><input type="checkbox"> Banner</li>
                    <li class="w110"><input type="checkbox"> Big Bets</li>
                    <li class="w110"><input type="checkbox"> Redirect</li>
                    <li class="w110"><input type="checkbox"> Elevate</li>
                </ul>
            </div> 
        </div>
      </div>
      
    </div>
    <!--Left Menu-->
    <!--Main Menu-->
    <div class="floatL w730" style="margin-left:10px; margin-top:27px">
      <div class="floatL w730" style=" background:url(<spring:url value="/images/bgGraySearch.jpg" htmlEscape="true" />); height:39px">
        <div style="padding-top:7px; padding-left:15px"> <img src="images/src_ico.jpg" width="29" height="24" align="absmiddle">
          <input type="text" name="textfield" id="textfield" class="farial fsize12 fgray" style="width:334px; height:24px; padding:3px; border:1px solid #ccc">
          <a href="#"><img src="images/btn_src.jpg" width="79" height="24" align="absmiddle"></a> </div>
      </div>
      <div class="clearB floatL farial fsize12 marT10 w730">Did you mean: <a href="#" class="fDblue fbold">Apple</a></div>
      <!--Pagination-->
      <div>
        <div class="clearB floatL farial fsize12 fDblue w300 padT10">Displaying 1 to 25 of 26901 Products</div><br/>
        <div class="floatR farial fsize12 fgray txtAR padT10">
       	  <div class="txtAR"><ul class="pagination">
           	  <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
          </ul></div>
        </div>
      </div>
      <!--Pagination-->
      <div class="clearB floatR farial fsize12 fDGray fbold txtAR w730 marT10" style="background:url(<spring:url value="/images/bgSort.jpg" htmlEscape="true" />) repeat-x; padding-top:8px;"></div>
      <div class="clearBce"></div>
      <!--Item 1-->
      <div class="clearB floatL w730 borderB">
        <table class="tblKeyword fsize12" style="width:90%">
        	<tr>
            	<th></th>
            	<th class="txtAL">Keyword</th>
                <th></th>
                <th>Visibility</th>
            </tr>
            <tr>
            	<td width="20px" class="txtAC"><img src="images/btn_delete.jpg"></td>
                <td width="45%">Apple</td>
                <td class="txtAC">5 related</td>
                <td  width="40%" class="txtAC"><span class="pVisibility"><span class="closeVisibility">x</span> Banner</span>  <span class="pVisibility"><span class="closeVisibility">x</span> Big Bets</span>  <span class="pVisibility"><span class="closeVisibility">x</span> Redirect</span>    <span class="pVisibility"><span class="closeVisibility">x</span> Elevate</span></td>
            </tr>
            <tr>
            	<td class="txtAC"><img src="images/btn_delete.jpg"></td>
                <td>IBM</td>
                <td></td>
                <td></td>
            </tr>
        </table>
      </div>
      <!--Item 1-->
      <!--Pagination-->
      <div>
        <div class="clearB floatL farial fsize12 fDblue w300 padT10">Displaying 1 to 25 of 26901 Products</div><br/>
        <div class="floatR farial fsize12 fgray txtAR padT10">
       	  <div class="txtAR"><ul class="pagination">
           	  <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
          </ul></div>
        </div>
      </div>
      <!--Pagination-->
    </div>
    <!--Main Menu-->
    <div class="clearB"></div>
    <div class="clearB floatL w980 padT20">
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fgray bgFooter">
        <tr>
          <td class="h35">2555 West 190th Street - Torrance, CA 90504 - 800.555.6255</td>
          <td align="right" class="h35">© 2011 PC Mall Sales, Inc.</td>
        </tr>
      </table>
    </div>
  </div>
</div>
<script type="text/javascript">
	properlyLoadImages();
</script>
<%@ include file="/WEB-INF/includes/footer.jsp" %>
