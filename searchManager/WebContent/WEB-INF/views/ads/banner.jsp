<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="ads"/>
<c:set var="submenu" value="banner"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/ads/banner.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/ads/banner.css" />" rel="stylesheet" type="text/css">

<!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
	<div class="clearB floatL w240">
		<div id="rulePanel"></div>
		<div class="clearB"></div>
	</div>
</div>
<!-- End Left Side -->

<!-- Start Right Side -->
<div class="contentArea ban_edit floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer">
    	<div class="w535 padT10 padL10 floatL fsize20 fnormal breakWord">
			<span id="ruleTypeIcon" class="ruleTypeIcon marR5 posRel top3"></span>
			<span id="titleText"></span>
			<span id="titleHeader" class="fLblue fnormal"></span>
		</div>
		<div id="addBannerBtn" class="btn_add_banner round_btn fRight">
			<span class="btn_wrap"><a href="javascript:void(0)">Add Banner</a></span>
		</div>
	</div>
	<div class="clearB"></div>
	<div id="ruleStatus"></div>
	<div class="clearB"></div>
	
	<div id="ruleContent" class="">

		<div id="preloader" class="circlePreloader" style="display:none">
			<img src="<spring:url value="/images/ajax-loader-circ.gif" />">
		</div>

		<div id="infographic">
			<img id="no-items-img" src="../images/bannerGuidelines.jpg">
		</div>
	
		<div id="ruleContent" class="ruleContent fsize12" style="display:none">
			<div id="ruleItemOptions" class="page_nav_group clearfix">
				<ul class="page_nav fRight">
					<li>	
						<select id="itemFilter">
							<option value="all">All</option>
							<option value="active">Active</option>
							<option value="expired">Expired</option>
							<option value="disabled">Disabled</option>
						</select>
					</li>
					<li><div class="ico_graph ico" alt="Show All Item Statistics" title="Show All Item Statistics"></div></li>
					<li>
						<a id="downloadRuleIcon" href="javascript:void(0);" alt="Download All Item" title="Download All Item">
							<div class="ico_download2 ico"></div>
						</a>
					</li>
					<li>
						<a id="deleteAllItemIcon" href="javascript:void(0);" alt="Delete All Item" title="Delete All Item">
							<div class="ico_delete ico"></div>
						</a>
					</li>
				</ul>
				<div id="ruleItemPagingTop"></div>
			</div>		
			<div id="ruleItemHolder" class="ban_container" style="display:none">
				<div id="ruleItemPattern" class="ruleItem ban_group">
					<span id="imageTitle" class="banner_title fLeft fBold cBlue"></span>
					<ul class="display_settings fRight clearfix">
						<li >Priority <input type="text" name="priority" id="priority"/></li>
						<li class="bLeft duration">
							<span id="daysLeft" class="cGreen"></span>
						</li>
						<li class="schedule">
							Schedule <input type="text" id="startDate" class="startDate"/>
						</li>	
						<li class="schedule">
							<input type="text" id="endDate" class="endDate"/>
						</li>
					</ul>
				
					<div id="preview" class="preview_container">
						<img id="imagePreview" src="<spring:url value="/images/nopreview.png" />" onerror="this.onerror=null;this.src='<spring:url value="/images/nopreview.png" />';"/>
					</div>
				
					<ul class="banner_info clearfix">
						<li ><span id="toggleIcon" class="ico_minus ico fLeft"></span>
							 <a id="toggleText" href="javascript:void(0);" class="show_what">Show Less</a></li>
						<li>
							<div id="copyToBtn" class="btn_copy_to round_btn fLeft">
								<span class="btn_wrap"><a href="javascript:void(0);">Copy To</a></span>
							</div>							
						</li>
						<li class="bRight">
							<div id="keywordBtn" class="btn_keywords round_btn fLeft">
								<span class="btn_wrap"><a href="javascript:void(0);">Keyword (<span id="keywordCount">4</span>)</a></span>
							</div>							
						</li>	
						<li class="bRight"><div id="auditIcon" class="ico_history ico" alt="Show Audit" title="Show Audit"></div></li>
						<li class="bRight"><div id="itemStatIcon" class="ico_graph ico" alt="Show Statistics" title="Show Statistics"></div></li>
						<li class="bRight"><div id="lastModifiedIcon" class="ico_user ico"></div></li>		
						<li><div id="commentIcon" class="ico_comments ico" alt="Show Comment" title="Show Comment"></div></li>								
					</ul>
				
					<div id="bannerInfo" class="banner_info_more clearfix">
						<label for="imagePath">Image Path</label>
						<input type="text" class="imagePath w565px" name="imagePath" id="imagePath" />
						
						<label for="imageAlias">Image Alias</label>
						<input type="text" class="imageAlias w218px" name="imageAlias" id="imageAlias" />
						
						<div id="setAliasBtn" class="setAliasBtn btn_update_alias round_btn fLeft clearfix">
							<span class="btn_wrap"><a id="setAliasText" href="javascript:void(0);">Set Alias</a></span>
						</div>
							
						<label for="imageAlt" class="lbl_imgAlt">Image Alt:</label>
						<input type="text" class="w218px" name="imageAlt" id="imageAlt" />
						
						<label for="linkPath">Link Path</label>
						<input type="text" class="w533px" name="linkPath" id="linkPath" />

                        <div class="clearfix openNewWindowContainer">
                            <input type="checkbox" name="openNewWindow" id="openNewWindow" />
                            <label for="openNewWindow" class="fBold">Open In New Window</label>
                        </div>
						<!-- div class="ico_check ico fLeft"></div -->
						
						<label for="description">Description</label>
						<textarea class="w565px" name="description" id="description" ></textarea>
						
						<div class="button_group clearfix">
							<input type="checkbox" name="temporaryDisable" id="temporaryDisable" />
							<label for="temporaryDisable" class="cRed fBold fLeft lbl_temporaryDisable">Temporary Disable</label>
						
							<div class="fRight">
							<div id="updateBtn" class="btn_update round_btn fLeft">
								<span class="btn_wrap"><a href="javascript:void(0);">Update</a></span>
							</div>	
							<div id="deleteBtn" class="btn_delete round_btn fLeft">
								<span class="btn_wrap"><a href="javascript:void(0);">Delete</a></span>
							</div>	
							</div>
						</div>
					</div>				
				</div>
				<div class="clearB"></div>
			</div>
		</div>
	</div>
</div>
<!--  end right side -->
<%@ include file="/WEB-INF/includes/footer.jsp" %>	