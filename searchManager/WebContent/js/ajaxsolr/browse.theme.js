(function ($) {

	AjaxSolr.theme.prototype.searchKeyword = function(){
		var template = '';
		template += '<a id="statisticIcon" href="javascript:void(0);">';
		template += '	<img align="absmiddle" class="marR3 marT5 floatR  posRel" src="' + GLOBAL_contextPath + '/images/icon_statistics.png">';
		template += '</a>'; 
		template += '<a id="searchBtn" href="javascript:void(0);">';
		template += '	<img align="absmiddle" class="marR5 marLn4 marT1 floatR  posRel" src="' + GLOBAL_contextPath + '/js/ajaxsolr/images/btn_GO.png">';
		template += '</a> '; 
		template += '<div class="searchBoxHolder w150 floatR marT1 marR8">';
		template += '	<input type="text" class="farial fsize12 fgray pad3 w150" id="keyword" name="keyword">';
		template += '</div>'; 
		template += '<div class="floatR posRel txtAL marR5" id="refinementHolder" style="display:none">';
		template += '	<input id="keepRefinement" name="keepRefinement" type="checkbox">';
		template += '	<span class="fsize11">Keep Refinements</span>';
		template += '</div>';     	 
		return template;
	};
	
	AjaxSolr.theme.prototype.searchWithin = function(){
		var template = '';
		template += '<div class="h27">';
		template += '	<div class="searchBoxHolder w145 floatL marT4 marR5">';
		template += '		<span><input type="text" id="searchWithin" name="searchWithin" class="w140 farial fsize12 fgray pad3"></span>';
		template += '	</div>';
		template += '	<a href="javascript:void(0)" id="searchBtn" class="btnGraph">';
		template += '		<div class="btnGraph btnGoB floatR marT3"></div>';
		template += '	</a>';
		template += '</div>';
		return template;
	};
	
	AjaxSolr.theme.prototype.cnetFacets = function () {
		var output  = '<div class="clearB floatL w240">';
		output += '<div class="facetHeader farial fsize16 fwhite" style="padding-left:10px; padding-top:7px; margin-top:27px; margin-bottom:8px">Category</div>';
		output += '<div class="clearB w230 padL10"></div>';  
		output += '</div>';

		output +='<div style="width:220px; margin:5px auto">';
		output +='	<ul id="facetHierarchy" class="itemCatList">';
		output +='</ul>';
		output +='</div>';

		return $(output);
	};

	AjaxSolr.theme.prototype.animatedTagCloud = function () {
		var output  = '';

		output  +='<canvas width="740" height="500" id="canvas">';
		output  +='<p>In Internet Explorer versions up to 8, things inside the canvas are inaccessible!</p>';
		output  +='</canvas>';
		output  +='<div id="tagContainer">';
		output  +='<ul id="tagList"></ul>';
		output  +='</div>';

		return $(output);
	};

	AjaxSolr.theme.prototype.activeRule = function () {
		var output  = '';

		output  +='<div style="display:block;" class="fsize12 marT10 fDGray border">';
		output  +='	<ul id="itemListing" class="mar16 marB20 marL20" >';
		output  +='		<li id="itemPattern" class="items borderB padTB5 clearfix" style="display:none; width:690px">';
		output  +='			<label class="w30 preloader floatR" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/ajax-loader-rect.gif")  + '"></label>';
		output  +='			<label class="select floatL w20 posRel topn3"><input type="checkbox" class="ruleControl"></label>';
		output  +='			<label class="ruleType floatL fbold w310"></label>';
		output  +='			<label class="imageIcon floatL w20 posRel topn2"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/icon_reviewContent2.png")  + '" class="top2 posRel"></label>';
		output  +='			<label class="name w310 floatL"><span class="fbold"></span></label>';
		output  +='		</li>';
		output  +='	</ul>';
		output  +='<div class="clearB"></div>';
		output  +='</div>';
		output  +='<a href="javascript:void(0);">';
		output  +='<div class="minW100 floatR borderB borderR borderL height23 posRel topn1 fbold fsize11 padT8 marL5" style="display:block; background: #fff; z-index:500; color:#329eea;">';
		output  +='	<img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/icon_arrowDownBlue.png")  + '" class="top2 posRel marL5 marR3">';
		output  +='	<span>Active Rules</span>';
		output  +='</div>';
		output  +='</a>';

		return $(output);
	};

	AjaxSolr.theme.prototype.noSearchResult = function (keyword) {
		var output  = '';

		output  +='<div class="marT10 txtAL">';
		output  +='<h2>No products found for "' + keyword + '"</h2>';
//		output  +='<p class="contentAB">Proin varius dapibus metus, ac gravida enim pretium sed. Phasellus varius, elit id posuere vestibulum, justo metus consectetur odio, in consectetur turpis metus et odio. </p>';
//		output  +='<ol class="marT10 marRL20">';
//		output  +='<li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>';
//		output  +='<li>Morbi eget leo sit amet sapien commodo ultricies sed id turpis. Aenean tortor arcu, porttitor sed placerat at, adipiscing vitae metus. </li>';
//		output  +='<li>Donec vitae metus lacus, at sollicitudin leo. Proin metus tellus, molestie in fermentum eu, congue a risus.</li>';
//		output  +='</ol>';
		output  +='</div>';

//		output  +='<div class="containerAC txtAL clearfix">';
//		output  +='<h2>Browse By Product Category</h2>';
//		output  +='<p class="contentAC">';
//		output  +='<ul>';
//		output  +='	<li>Computers</li>';
//		output  +='<li>Servers</li>';
//		output  +='<li>Storage</li>';
//		output  +='<li>Printers</li>';
//		output  +='<li>Software</li>';
//		output  +='<li>Networking</li>';
//		output  +='<li>Displays</li>';
//		output  +='<li>Accessories</li>';
//		output  +='<li>Power</li>';
//		output  +='<li>Memory</li>';
//		output  +='<li>Scanners</li>';
//		output  +='<li>Electronics</li>';
//		output  +='<li>Supplies</li>';
//		output  +='<li>Projectors</li>';
//		output  +='<li>Browse All Categories</li>';
//		output  +='</ul>';
//		output  +='</p>';
//		output  +='</div>';

		return $(output);
	};

	AjaxSolr.theme.prototype.dynamicAttributeFilter = function() {
		var output  = '';

		output  += '<div class="box marT8">';
		output  += '	<h2>Condition</h2>';
		output  += '	<ul>';
		output  += '		<li><input type="checkbox" id="Refurbished_Flag" class="checkboxFilter"> Refurbished </li>';
		output  += '		<li><input type="checkbox" id="OpenBox_Flag" class="checkboxFilter"> Open Box </li>';
		output  += '		<li><input type="checkbox" id="Clearance_Flag" class="checkboxFilter"> Clearance </li>';
		output  += '	</ul>';
		output  += '</div>';

		output  += '<div class="box marT8">';
		output  += '	<h2>License Product</h2>';
		output  += '	<select class="dropdownFilter mar10 w215" id="licenseFilter">';
		output  += '		<option value="all">Both License & Non-License</option>';
		output  += '		<option value="Licence_Flag:1">License Product Only</option>';
		output  += '		<option value="Licence_Flag:0">Non-License Product Only</option>';
		output  += '	</select>';
		output  += '</div>';

		return $(output);
	};
	
	AjaxSolr.theme.prototype.productAttributeFilter = function() {
		var output  = '';

		output  += '<div class="box marT8">';
		output  += '	<h2>Condition</h2>';
		output  += '	<ul>';
		output  += '		<li><input type="checkbox" id="Refurbished_Flag" class="checkboxFilter"> Refurbished </li>';
		output  += '		<li><input type="checkbox" id="OpenBox_Flag" class="checkboxFilter"> Open Box </li>';
		output  += '		<li><input type="checkbox" id="Clearance_Flag" class="checkboxFilter"> Clearance </li>';
		output  += '	</ul>';
		output  += '</div>';

		output  += '<div class="box marT8">';
		output  += '	<h2>License Product</h2>';
		output  += '	<select class="dropdownFilter mar10 w215" id="licenseFilter">';
		output  += '		<option value="all">Both License & Non-License</option>';
		output  += '		<option value="Licence_Flag:1">License Product Only</option>';
		output  += '		<option value="Licence_Flag:0">Non-License Product Only</option>';
		output  += '	</select>';
		output  += '</div>';

		return $(output);
	};

	AjaxSolr.theme.prototype.createFacetHolder = function (facetLabel, facet) {
		var output  = '<div class="clearB floatL w240 marB27">';
		output += '<div id="' + $.formatAsId(facet) + '" class="facetHeader farial fsize16 fwhite" style="padding-left:10px; padding-top:7px;">' + facetLabel + '</div>';
		output += '<div class="' + $.formatAsId(facet) +' clearB floatL w230 padL10"></div>';  
		output += '</div>';

		return $(output);	
	};

	AjaxSolr.theme.prototype.getAvailability = function (doc, output){
		var stockText = "Call Us!";
		var stockIcon = "availabityImg0.png";

		if (doc.NextDayUnits>0){
			stockText = "In Stock";
			stockIcon = "availabityImg1.png";
		}else if(doc.SecondDayUnits > 0){
			stockText = "In Stock";
			stockIcon = "availabityImg2.png";
		}

		if(output=="icon")
			return AjaxSolr.theme('getAbsoluteLoc', 'images/'+stockIcon);

		return stockText;
	};

	AjaxSolr.theme.prototype.result = function (i, hasKeyword, doc, snippet, auditHandler, docHandler, debugHandler, featureHandler, elevateHandler, excludeHandler, demoteHandler) {

		var altclass ="";

		if (i % 2 ==1) altclass=" alt-bgGray";
		if (doc["ForceAdd"] != undefined) altclass=" forceAddClass";

		var output = '<li id="resultItem_' + doc.EDP + '" class="handle' + altclass + '">';
		output += '<table width="100%" border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray marT10 ">';
		output += '	<tr>';
		output += '      <td width="28%" rowspan="5" align="center" valign="top"><div style="width:116px; height:100px" class="border itemImg">';
		output += '	   		<img src="' + doc.ImagePath + '"></div>';
		output += '	   </td>';
		output += '      <td colspan="2" align="left" valign="top" class="borderB">';
		output += '			<div class="floatL">';
		output += '				<div id="debugHolder" class="floatL marB6"></div>';
		output += '				<div id="elevatePosition" class="floatL"></div>';
		output += '			</div>';
		output += '			<div class="floatR marR5">';
		output += '		    	<div id="auditHolder" class="iconHolder"></div>';
		output += '			</div>';
		output += '        <div class="floatR ruleOptionHolder marR5">'; 
		output += '			<div id="expiredHolder" class="elevTxtHolder" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/expired_stamp50x16.png") + '"></div>';
		//output += '			<div id="featureHolder" class="iconHolder" style="margin-top:-1px; margin-left:3px"></div>';
		output += '			<div id="elevateHolder" class="iconHolder"></div>';
		output += '			<div id="demoteHolder" class="iconHolder"></div>';
		output += '			<div id="excludeHolder" class="iconHolder"></div>';
		output += '        </div>';
		output += '      </td>';
		output += '	</tr>';
		output += '   <tr>';
		output += '		<td width="59%" align="left" valign="top" class="padT5">';
		output += '			<div>';
		//output += '				<div class="fbold marR5">' + doc.Manufacturer + '</div>';
		output += '       		<div id="docHolder"></div>';
		output += '			</div>';
		output += '		</td>';
		output += '      	<td width="13%" rowspan="2" align="right" valign="top" class="padT5">';
		output += '       	<div>'; 
		output += '				<div id="cartPriceHolder" class="fred fbold"></div>';
		output += '        		<div class="padT5">';
		output += '					<div class="txtAR" style="width:116px; margin:0 auto"><img src="' + AjaxSolr.theme('getAvailability', doc, "icon") + '" style="margin-bottom:-5px"> ' + AjaxSolr.theme('getAvailability', doc) + '</div>';
		output += '		 		</div>';
		output += '			</div>';
		output += '		</td>';
		output += '   </tr>';
		output += '   <tr>';
		output += '		<td align="left" valign="top" class="padT5">' + snippet + '</td>';
		output += '   </tr>';
		output += '   <tr>';
		output += '		<td colspan="2" align="left" valign="top" class="padTB7 fgray">';
		output += '			<table width="100%" border="0" cellpadding="0" cellspacing="0">';
		output += '				<tr>';
		output += '       			<td width="60"><span class="fgreen">SKU #: </span>' + doc.DPNo + '</td>';
		output += '       			<td width="130"><span class="fgreen">Mfr. Part #:</span>' + doc.MfrPN + '</td>';
		output += '				</tr>';
		output += '      		</table>';
		output += '		</td>';
		output += '  </tr>';
		output += '</table>';

		output += '</li>';

		var secObj = $(output);

		//Add Cart Price
		secObj.find("div#cartPriceHolder").append('$' + doc[GLOBAL_storeFacetName + "_CartPrice"]);

		var name = $.isNotBlank(doc[GLOBAL_storeFacetName + "_Name"])? doc[GLOBAL_storeFacetName + "_Name"] : doc.Name;
		var manufacturer = '<span class="txtManufact">' + doc.Manufacturer + '</span>';
		
		secObj.find("div#docHolder").wrapInner(AjaxSolr.theme('createLink', manufacturer + name, docHandler));

		//Add Audit Button
		secObj.find("div#auditHolder").html(AjaxSolr.theme('createLink', '', auditHandler));
		secObj.find("div#auditHolder a").html('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/icon_history.png') + '" alt="Audit Trail" title="Audit Trail">');

		//Add Debug link
		if ($.isBlank(doc.Elevate) || doc["ElevateType"] === "FACET"){
			secObj.find("div#debugHolder").wrapInner(AjaxSolr.theme('createLink', 'Score: ' + doc.score, debugHandler));
			secObj.find("div#debugHolder a").addClass("btnShade btnCream");
			secObj.find("div#debugHolder a").wrapInner("<span class='btnShade'></span>");
		}
		
		if ($.isNotBlank(doc.Elevate)){		  
			var displayText = "Elevated at position " + doc["Elevate"];
			
			if(doc["ElevateType"] === "FACET")
				displayText = 'Included in <a href="javascript:void(0);"><span class="fgray">Facet Rule Item</span></a> elevated at position '+ doc["Elevate"];

			secObj.find("div#elevatePosition").html(displayText);
			secObj.find("div#elevatePosition a").off().on({
				click: function(e){
					showMessage(this, e.data.doc["ElevateCondition"]);
				},
				mouseenter: function(e){
					showMessage(this, e.data.doc["ElevateCondition"]);
				}
			}, {doc: doc});
		}
		
		if ($.isNotBlank(doc.Demote)){		  
			var displayText = "Demoted at position " + doc["Demote"];
			
			if(doc["DemoteType"] === "FACET")
				displayText = 'Included in <a href="javascript:void(0);"><span class="fgray">Facet Rule Item</span></a> demoted at position '+ doc["Demote"];

			secObj.find("div#demotePosition").html(displayText);
			secObj.find("div#demotePosition a").off().on({
				click: function(e){
					showMessage(this, e.data.doc["DemoteCondition"]);
				},
				mouseenter: function(e){
					showMessage(this, e.data.doc["DemoteCondition"]);
				}
			}, {doc: doc});
		}

		//Add Elevate Button if search has keyword
		if (hasKeyword){
			var elevateIcon = 'images/icon_elevate_disable.png';
			var excludeIcon = 'images/icon_exclude_disable.png';
			var demoteIcon = 'images/icon_demote_disable.png';
			var featureIcon = 'images/icon_starGray.png';

			var featureHover = "Feature";
			var elevateHover = "Elevate";
			var excludeHover = "Exclude";
			var demoteHover = "Demote";

			if (doc.Elevate != undefined){
				elevateIcon = 'images/icon_elevate.png';
				elevateHover = "Update Elevate";
			} 

			if (doc.Demote != undefined){
				demoteIcon = 'images/icon_demote.png'; 
				demoteHover = "Update Demote";
			} 
			
			if (doc.ForceAdd != undefined){
				forceAddIcon = 'images/icon_forceAdd.png'; 
				forceAddHover = "Update Demote";
			}
			
			if (doc.Feature != undefined){
				featureIcon = 'images/icon_star.png'; 
				featureHover = "Remove Feature";
			}

			//Add Feature Button
			//secObj.find("div #featureHolder").append(AjaxSolr.theme('createLink', '', elevateHandler));
			//secObj.find("div #featureHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', featureicon) + '" alt="' + feaHover + '" title="' + feaHover + '">');

			//Add Elevate Button
			secObj.find("div#elevateHolder").append(AjaxSolr.theme('createLink', '', elevateHandler));
			secObj.find("div#elevateHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', elevateIcon) + '" alt="' + elevateHover + '" title="' + elevateHover + '">');
	
			//Add Demote Button
			secObj.find("div#demoteHolder").append(AjaxSolr.theme('createLink', '', demoteHandler));
			secObj.find("div#demoteHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', demoteIcon) + '" alt="' + demoteHover + '" title="' + demoteHover + '">');
			
			//Add Exclude Button
			secObj.find("div#excludeHolder").append(AjaxSolr.theme('createLink', '', excludeHandler));
			secObj.find("div#excludeHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', excludeIcon) + '" alt="Exclude" title="Exclude">');		

		}

		return secObj;
	};

	AjaxSolr.theme.prototype.snippet = function (doc) {
		var output = '';

		var description = $.isNotBlank(doc[GLOBAL_storeFacetName + "_Description"])? doc[GLOBAL_storeFacetName + "_Description"] : doc.Description;  

		if (description && description.length > 300) {
			output += description.substring(0, 300);
			output += '<span style="display:none;">' + description.substring(300) + '</span>';
			output += '<a href="javascript:void(0);" class="more">...more</a>';
		}
		else {
			output += description;
		}

		return output;
	};

	AjaxSolr.theme.prototype.displayFacetMoreOptions = function (value, title, facets, delimiter) {
		var output = ''; 

		output += '<div class="tblcontContainer">';
		output += '<table cellspacing="0" cellpadding="0" border="0" width="360">';
		output += '<tr>';
		output += '<td>';
		output += ' <table cellspacing="0" cellpadding="0" width="360px" class="marT10">';
		output += '<tr>';
		output += '<td colspan="2" class="top"><div class="floatL w240">Search: <input type="text" id="searchField" class="searchBoxIconBg"></div> <div class="searchCount fsize11 fgray w110 floatL txtAR padT3"></div></td>';
		output += '</tr>';
		output += '<tr><td colspan="2"> &nbsp; </td></tr>';
		output += '<tr>';
		output += '<th width="25%" class="pad3"></th>';
		output += '<th class="pad3 txtAL fbold">Content Type</th>';
		output += '</tr>';
		output += '</table>';
		output += '</td>';
		output += '</tr>';
		output += '<tr>'; 
		output += '<td>';
		output += '<div style="max-height:300px; overflow-y:auto; overflow-x:hidden" class="resultTable">';
		output += '<table width="360px">';

		var i=0;
		for (var facet in facets){
			if($.isNotBlank(facet)){ //TODO: should not hide data related issue
				i++;
				var count = parseInt(facets[facet]);
				output += '<tr>';
				output += '<td width="25%" class="exclude"><input type="checkbox" id="checkbox-' + i + '" class="firerift-style-checkbox" value="' + facet + '"/></td>';
				output += '<td class="values"><span class="value">' + (delimiter ? facet.split(delimiter)[1] : facet) + '</span></td>';
				//output += '<td class="values"><span class="value">' + facet + '</span><span dir="ltr" class="count">(' + count + ')</span></td>';
				output += '</tr>';		
			}
		}

		output += '</table>';
		output += '</div>';
		output += '</td>	';
		output += '</tr> ';
		output += '<tr> ';
		output += '<td align="right" style="padding:10px 20px 0 0"><a class="buttons btnGray clearfix" href="javascript:void(0);" id="continueBtn"><div class="buttons fontBold">Continue</div></a> <a class="buttons btnGray clearfix" href="javascript:void(0);" id="cancelBtn"><div class="buttons fontBold">Cancel</div></a> ';
		output += '</td> ';
		output += '</tr> ';
		output += '</table>';
		output += '</div>';

		return output; 
	};

	AjaxSolr.theme.prototype.displayDoc = function (doc) {
		var output = '';   
		output += '<div class="farial">';
		output += '	<div class="searchbox" style="background:#f4f4f4; padding:5px">';
		output += '		<span class="fsize14 fbold">Search:</span>';
		output += '		<input type="text" id="searchField"/>';
		output += '		<div class="searchCount fsize12"></div>';
		output += '	</div>';
		output += '';  

		output += '<table cellpadding="1" cellspacing="1" style="width:465px">';  
		output += '<tr>';
		output += '<td>';
		output += '<table cellpadding="1" cellspacing="1" style="width:465px">';
		output += '  <tr>';
		output += '    <th align="left" width="51%">Attribute Field</th>';  
		output += '    <th align="left" width="49%">Attribute Value</th>';  
		output += '  </tr>';  
		output += '</table>';  
		output += '</td>';
		output += '</tr>'; 
		output += '<tr>';
		output += '<td>';
		output += '<div style="465px; height:250px; overflow:auto">';
		output += '<table cellpadding="1" cellspacing="1" style="width:445px" class="resultTable" >';
		output += '  <tbody>';  

		for (var docField in doc){
			output += '  <tr>';  
			output += '    <td class="w220"><div style="width:220px; word-wrap: break-word;">' + docField + '</div></td>';  
			output += '    <td class="w205"><a href="javascript:void(0);" class="attributes"><div style="width:205px; word-wrap: break-word;">' + doc[docField] + '</div></a>';
			output += '		<div>';
			output += '			<input type="hidden" class="attribField" value="' + docField + '">';
			output += '			<input type="hidden" class="attribValue" value="' + doc[docField] + '">';
			output += '       </div>';
			output += '    </td>';
			output += ' </tr>';  
		}
		output += '  </tbody>';
		output += '</table>';  
		output += '</div>';       
		output += '</td>';
		output += '</tr>';
		output += '</table>';
		output += '</div>';

		return output;

	};

	AjaxSolr.theme.prototype.createConfirmDialog = function (doc, headerTitle, confirmMessage) {
		var idSuffix = "_" + doc.EDP;
		var output  = '<div class="elevateProduct">';
		output += '<div id="dialog-confirm" title="' + headerTitle + '" class="farial" style="float:left; width:225px">';
		output += '	<div class="marB10"><span>' + confirmMessage + '</span></div>';
		//output += '	<div id="aStampExpired' + idSuffix + '" ><div class="posAbs" style="top:80px; left:80px"><img src="../images/expired_stamp90x40.png"></div></div>';
		output += '	<div><center><img id="aProductImage' + idSuffix + '" src="' + doc.ImagePath + '" class="border" style="width:116px; height:100px"></center></div>';
		output += '	<div><center><span class="fbold">' + doc.Manufacturer + '</span></div>';
		output += ' <div style="position:absolute; float:right; top:50px; left:224px"><a href="javascript:void(0);" id="toggleCurrent"><img src="../images/btnTonggleShow.png"></a></div>';
		output += '	<div>';
		output += '		<ul class="listProd">';
		output += '			<li><label class="fbold title">SKU #: </label><span id="aPartNo' + idSuffix + '">' + doc.DPNo + '</span></li>';
		output += '			<li><label class="fbold title">Elevate: </label><input type="text" id="aElevatePosition' + idSuffix + '" style="width:30px"></li>';
		output += '			<li><label class="fbold title">Valid Until: </label><input type="text" id="aExpiryDate' + idSuffix + '" style="width:65px"></li>';
		output += '			<li><label class="fbold title">Comments:</label><div id="aStampExpired"><img id="aStampExpired' + idSuffix + '" src="../images/expired_stamp50x16.png" style="display:none"></div><textarea id="aComment' + idSuffix + '"></textarea></li>';
		output += '		</ul>';
		output += '	</div>';
		output += '<div id="btnHolder' + idSuffix + '" class="marB10 txtAC">';
		output += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="saveBtn"><div class="buttons fontBold">Save</div></a>';
		output += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="removeBtn"><div class="buttons fontBold">Remove</div></a>';
		output += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="cancelBtn"><div class="buttons fontBold">Cancel</div></a>';
		output += '</div>';
		output += '</div>';
		output += '<div id="current" style="float:left; margin-left:7px" class="toggleDiv">';
		output += '<div class="fsize16 titleToggle" style="margin:0 "><h2 style="padding-top:8px; margin:0 10px">Current Elevations</h2></div >';
		output += '<div id="toggleItems" style="overflow:auto; overflow-y:auto; overflow-x:hidden; height:340px; width:220px">';
		output += '		<ul id="listItems' + idSuffix + '" class="listItems">';
		output += '			<li id="listItemsPattern" class="clearfix" style="display:none">'; 
		output += '				<div class="handle">';
		output += ' 				<div class="floatR posRel padR10" style="z-index:1; top:-8px"><a id="deleteIcon" class="deleteIcon" href="javascript:void(0);"><img src="../images/iconDelete.png"></a></div>';
		output += '					<img id="productImage" src="' + doc.ImagePath + '" class="border floatL" width="60px" >';
		//output += '					<div id="stampExpired"><img src="../images/expired_stamp50x16.png"></div>';	
		//output += '				<div id="stampExpired" class="posAbs" style="top:30px; display:none"><img src="../images/expired_stamp60x28.png" class="noborder"></div>';	
		output += '					<div class="w125 floatL marL8 posRel" style="top:-8px">';
		output += '				  	<ul class="listItemInfo">';
		output += '						<li class="label">Elevation:</li><li class="value" id="elevatePosition">1</li>';
		output += '						<li class="label partNoLabel">SKU #:</li><li class="value" id="partNo">846896</li>'; 
		output += '						<li class="label mfrNoLabel">Mfr Part #:</li><li class="value" id="mfrNo">ERgt129</label>';
		output += '						<li id="validityText" class="label"></li><li class="value" id="expiryDate">02/21/2010</li>';
		output += '				  	</ul>';
		output += '					</div>';
		output += '					<div class="label w125 floatL marL8 posRel" id="readableStr"></div>';
		output += '				</div>';
		output += '			</li>';
		output += '		</ul>';
		output += '</div>';
		output += '</div>';

		var secObj = $(output);

		// on error detection is upon element creation
		secObj.find("#aProductImage" + idSuffix).error(function(){
			$(this).unbind("error").attr("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image.jpg'));
		});

		//if (doc.Expired == undefined) secObj.find("#aStampExpired" + idSuffix).attr("style","display:none");

		return secObj;
	};
	
	AjaxSolr.theme.prototype.createDemoteConfirmDialog = function (doc, headerTitle, confirmMessage) {
		var idSuffix = "_" + doc.EDP;
		var output  = '<div class="demoteProduct">';
		output += '<div id="dialog-confirm" title="' + headerTitle + '" class="farial" style="float:left; width:225px">';
		output += '	<div class="marB10"><span>' + confirmMessage + '</span></div>';
		//output += '	<div id="aStampExpired' + idSuffix + '" ><div class="posAbs" style="top:80px; left:80px"><img src="../images/expired_stamp90x40.png"></div></div>';
		output += '	<div><center><img id="aProductImage' + idSuffix + '" src="' + doc.ImagePath + '" class="border" style="width:116px; height:100px"></center></div>';
		output += '	<div><center><span class="fbold">' + doc.Manufacturer + '</span></div>';
		output += ' <div style="position:absolute; float:right; top:50px; left:224px"><a href="javascript:void(0);" id="toggleCurrent"><img src="../images/btnTonggleShow.png"></a></div>';
		output += '	<div>';
		output += '		<ul class="listProd">';
		output += '			<li><label class="fbold title">SKU #: </label><span id="aPartNo' + idSuffix + '">' + doc.DPNo + '</span></li>';
		output += '			<li><label class="fbold title">Demote: </label><input type="text" id="aDemotePosition' + idSuffix + '" style="width:30px"></li>';
		output += '			<li><label class="fbold title">Valid Until: </label><input type="text" id="aExpiryDate' + idSuffix + '" style="width:65px"></li>';
		output += '			<li><label class="fbold title">Comments:</label><div id="aStampExpired"><img id="aStampExpired' + idSuffix + '" src="../images/expired_stamp50x16.png" style="display:none"></div><textarea id="aComment' + idSuffix + '"></textarea></li>';
		output += '		</ul>';
		output += '	</div>';
		output += '<div id="btnHolder' + idSuffix + '" class="marB10 txtAC">';
		output += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="saveBtn"><div class="buttons fontBold">Save</div></a>';
		output += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="removeBtn"><div class="buttons fontBold">Remove</div></a>';
		output += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="cancelBtn"><div class="buttons fontBold">Cancel</div></a>';
		output += '</div>';
		output += '</div>';
		output += '<div id="current" style="float:left; margin-left:7px" class="toggleDiv">';
		output += '<div class="fsize16 titleToggle" style="margin:0 "><h2 style="padding-top:8px; margin:0 10px">Current Elevations</h2></div >';
		output += '<div id="toggleItems" style="overflow:auto; overflow-y:auto; overflow-x:hidden; height:340px; width:220px">';
		output += '		<ul id="listItems' + idSuffix + '" class="listItems">';
		output += '			<li id="listItemsPattern" class="clearfix" style="display:none">'; 
		output += '				<div class="handle">';
		output += ' 				<div class="floatR posRel padR10" style="z-index:1; top:-8px"><a id="deleteIcon" class="deleteIcon" href="javascript:void(0);"><img src="../images/iconDelete.png"></a></div>';
		output += '					<img id="productImage" src="' + doc.ImagePath + '" class="border floatL" width="60px" >';
		//output += '					<div id="stampExpired"><img src="../images/expired_stamp50x16.png"></div>';	
		//output += '				<div id="stampExpired" class="posAbs" style="top:30px; display:none"><img src="../images/expired_stamp60x28.png" class="noborder"></div>';	
		output += '					<div class="w125 floatL marL8 posRel" style="top:-8px">';
		output += '				  	<ul class="listItemInfo">';
		output += '						<li class="label">Demote:</li><li class="value" id="demotePosition">1</li>';
		output += '						<li class="label partNoLabel">SKU #:</li><li class="value" id="partNo">846896</li>'; 
		output += '						<li class="label mfrNoLabel">Mfr Part #:</li><li class="value" id="mfrNo">ERgt129</label>';
		output += '						<li id="validityText" class="label"></li><li class="value" id="expiryDate">02/21/2010</li>';
		output += '				  	</ul>';
		output += '					</div>';
		output += '					<div class="label w125 floatL marL8 posRel" id="readableStr"></div>';
		output += '				</div>';
		output += '			</li>';
		output += '		</ul>';
		output += '</div>';
		output += '</div>';

		var secObj = $(output);

		// on error detection is upon element creation
		secObj.find("#aProductImage" + idSuffix).error(function(){
			$(this).unbind("error").attr("src", AjaxSolr.theme('getAbsoluteLoc', 'images/no-image.jpg'));
		});

		return secObj;
	};

	AjaxSolr.theme.prototype.createSelectionLink = function (id, items) {

		var selection = $('.' + id);

		for (var i = 0, l = items.length; i < l; i++) {
			var classSelector = id + i;
			selection.append($('<div class="' + classSelector + ' farial fsize12 fDGray w220 padTB5 borderB wordwrap"><span class="lnk">'));
			selection.find('.' + classSelector + ' span.lnk').append(items[i]);
			selection.find('.' + classSelector + ' span.lnk a:not(#removeAll)').prepend('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/btn_delete_big.jpg') + '" width="10" height="10" style="margin-right:5px">');
			selection.find('.' + classSelector + ' span.lnk a#removeAll').addClass("fbold");
		}

		// For aesthetic, need to tag the last item
		if(selection.find("span.lnk a#level3").is(":visible")){
			selection.find("span.lnk a#level1, span.lnk a#level2").parent().parent().removeClass("borderB");
		}else if(selection.find("span.lnk a#level2").is(":visible")){
			selection.find("span.lnk a#level1").parent().parent().removeClass("borderB");
		}else if(selection.find("span.lnk a#level1").is(":visible")){
			selection.find("span.lnk a#level1").parent().parent().addClass("last");
		}

		return selection;
	};

	AjaxSolr.theme.prototype.createFacetLink = function (facetId, facetField, facet, count, handler) {

		var output = '<div class="' + facetId + ' farial fsize12 fDGray w220 borderB padTB5 wordwrap">';
		output += '	<div id="facetFilterHolder"><span class="lnk"></span></div>';
		output += '</div>';

		var scbObj= $("." + $.formatAsId(facetField)).append($(output));
		scbObj.find(" ." + facetId + " div#" + "facetFilterHolder span.lnk").append(AjaxSolr.theme('createLink', facet + " (" + count + ")", handler));

	};

	AjaxSolr.theme.prototype.createFacetMoreOptionsLink = function (facetField, facetValues, value, handler) {
		$('.' + facetField).append('<div id="more' + facetField + '" class="farial fsize12 fDGray w220 borderB padTB5 wordwrap">');
		$('div#more' + facetField).append('<span class="lnk">');

		return $('div#more' + facetField + ' span.lnk').append(AjaxSolr.theme('createLink', value, handler));
	};


	AjaxSolr.theme.prototype.formatDebug = function (value){
		var output = "<pre>";
		output += value;
		output += "</pre>";

		var secObj = $(output);

		return secObj;
	};


	AjaxSolr.theme.prototype.createLink = function (value, handler, id) {
		var $a = $('<a href="javascript:void(0)"/>').html(value).click(handler);
		if ($.isNotBlank(id)){
			$a.prop("id", id);
		}
		return $a;
	};

	AjaxSolr.theme.prototype.no_items_found = function () {
		return 'No items found in current selection';
	};

	AjaxSolr.theme.prototype.showAjaxLoader = function (text) {

		return $('<div id="ajaxloader"><img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/ajax-loader.gif') + '"/><div>');
	};

	AjaxSolr.theme.prototype.getAbsoluteLoc = function (filename){
		return "../js/ajaxsolr/" + filename;
	};

	AjaxSolr.theme.prototype.buildHTML = function(tag, html, attrs) {
		// you can skip html param
		if (typeof(html) != 'string') {
			attrs = html;
			html = null;
		}
		var h = '<' + tag;
		for (attr in attrs) {
			if(attrs[attr] === false) continue;
			h += ' ' + attr + '="' + attrs[attr] + '"';
		}
		return h += html ? ">" + html + "</" + tag + ">" : "/>";
	};

	$.fn.outerHTML = function() {
		var doc = this[0] ? this[0].ownerDocument : document;
		return $('<div>', doc).append(this.eq(0).clone()).html();
	};

})(jQuery);
