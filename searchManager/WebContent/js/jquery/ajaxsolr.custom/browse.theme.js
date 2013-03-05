(function ($) {

	AjaxSolr.theme.prototype.errorRequest = function(error){
		var template = '';
		template += '<div class="error border fsize14 marB20">';
		template += '	<h1>Error Code: ' + error["status"] + " " + error["statusText"] + '</h1>';
		template += '	<div class="clearB"/>';
		template += '	<span>An error was encountered while processing your request. Kindly refresh this page and retry your request.</span>';
		template += '	<div class="clearB"/>';
		template += '	<span>If problem persists, please contact the Search Team and inform them of the time the error occurred, and anything you might have done that may have caused the error.</span>';
		template += '	<div class="clearB"/>';
		template += '</div>'; 
		return $(template);
	};
	
	AjaxSolr.theme.prototype.searchKeyword = function(){
		var template = '';
		template += '<a id="statisticIcon" href="javascript:void(0);">';
		template += '	<img align="absmiddle" class="marR3 marT5 floatR  posRel" src="' + GLOBAL_contextPath + '/images/icon_statistics.png">';
		template += '</a>'; 
		template += '<a id="searchBtn" href="javascript:void(0);">';
		template += '	<img align="absmiddle" class="marR5 marLn4 marT1 floatR  posRel" src="' + AjaxSolr.theme('getAbsoluteLoc', "images/btn_GO.png") + '">';
		template += '</a> '; 
		template += '<div class="searchBoxHolder w150 floatR marT1 marR8">';
		template += '	<input type="text" class="farial fsize12 fgray pad3 w145" id="keyword" name="keyword">';
		template += '</div>'; 
		template += '<div class="floatR posRel txtAL w240 marR5" id="refinementHolder" style="display:none">';
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
	
	AjaxSolr.theme.prototype.productForceAdd = function(){
		var template = '';
		template += '<div class="w250">';
		template += '	<div>';
		template += '		<ul class="listItemInfo">';
		template += '			<li><label class="floatL fbold title w100 padTB2">SKU #: </label><label class="floatL w100 padTB2"><span id="sku"></span></label></li>';
		template += '			<li><label class="floatL fbold title w100 padTB2">Keyword: </label><label class="floatL w100 padTB2"><input type="text" id="keyword"></label></li>';
		template += '			<li><label class="floatL fbold title w100 padTB2">Valid Until: </label><label class="floatL w100 padTB2"><input type="text" id="validityDate" style="width:65px"></label></li>';
		template += '			<li><label class="floatL fbold title padTB2">Comments:</label><label class="floatL padTB2"><textarea id="comment" class="w240"></textarea></label></li>';
		template += '		</ul>';
		template += '		<div class="clearB"></div>';
		template += '	</div>';
		template += '	<div class="marT10 marB10 txtAR">';
		template += '		<a class="buttons btnGray clearfix" href="javascript:void(0);" id="addBtn"><div class="buttons fontBold">Force Add</div></a>';
		template += '		<a class="buttons btnGray clearfix" href="javascript:void(0);" id="cancelBtn"><div class="buttons fontBold">Cancel</div></a>';
		template += '	</div>';
		template += '</div>';
		return template;
	};
	
	AjaxSolr.theme.prototype.cnetFacets = function () {
		var output  = '<div class="clearB floatL w240 marB27">';
		output += '<div class="facetHeader farial fsize16 fwhite" style="padding-left:10px; padding-top:7px; margin-bottom:8px">Category</div>';
		output += '<div class="clearB w230 padL10"></div>';  
		output +='<div style="width:220px; margin:5px auto">';
		output +='	<ul id="facetHierarchy" class="itemCatList">';
		output +='</ul>';
		output +='</div>';
		output += '</div>';

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
		
		output  += '<div class="box marT8">';
		output  += '	<h2>Product Image</h2>';
		output  += '	<select class="dropdownFilter mar10 w215" id="imageExistsFilter">';
		output  += '		<option value="all">Both With & Without Product Image</option>';
		output  += '		<option value="ImageExists:1">With Product Image Only</option>';
		output  += '		<option value="ImageExists:0">Without Product Image Only</option>';
		output  += '	</select>';
		output  += '</div>';

		return $(output);
	};
	
	AjaxSolr.theme.prototype.productAttributeFilter = function() {
		var output  = '';

		output  += '<div class="box marT8">';
		output  += '	<h2>License Product</h2>';
		output  += '	<select class="dropdownFilter mar10 w215" id="licenseFilter">';
		output  += '		<option value="all">Both License & Non-License</option>';
		output  += '		<option value="Licence_Flag:1">License Product Only</option>';
		output  += '		<option value="Licence_Flag:0">Non-License Product Only</option>';
		output  += '	</select>';
		output  += '</div>';
		
		output  += '<div class="box marT8">';
		output  += '	<h2>Product Image</h2>';
		output  += '	<select class="dropdownFilter mar10 w215" id="imageExistsFilter">';
		output  += '		<option value="all">Both With & Without Product Image</option>';
		output  += '		<option value="ImageExists:1">With Product Image Only</option>';
		output  += '		<option value="ImageExists:0">Without Product Image Only</option>';
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

	AjaxSolr.theme.prototype.result = function (i, hasKeyword, doc, snippet, auditHandler, docHandler, debugHandler, featureHandler, elevateHandler, excludeHandler, demoteHandler, forceAddHandler) {

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
		//output += '		    	<div id="auditHolder" class="floatL marR5"></div>';
		output += '				<div id="debugHolder" class="floatL marB6"></div>';
		output += '				<div id="elevatePosition" class="floatL"></div>';
		output += '				<div id="demotePosition" class="floatL"></div>';
		output += '			</div>';
		output += '         <div class="floatR ruleOptionHolder marR5">'; 
		output += '				<div id="expiredHolder" class="elevTxtHolder" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/expired_stamp50x16.png") + '"></div>';
		//output += '			<div id="featureHolder" class="iconHolder" style="margin-top:-1px; margin-left:3px"></div>';
		output += '				<div id="forceAddHolder" class="iconHolder"></div>';
		output += '				<div id="elevateHolder" class="iconHolder"></div>';
		output += '				<div id="demoteHolder" class="iconHolder"></div>';
		output += '				<div id="excludeHolder" class="iconHolder"></div>';
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
		var priceDisplay = doc[GLOBAL_storeFacetName + "_CartPrice"];
		if(GLOBAL_storeFacetName.toLowerCase()==="pcmallgov"){
			priceDisplay = doc[GLOBAL_storeFacetName + "_GovCartPrice"];
		}
		
		secObj.find("div#cartPriceHolder").append($.toCurrencyFormat('$', priceDisplay));

		var name = $.isNotBlank(doc[GLOBAL_storeFacetName + "_Name"])? doc[GLOBAL_storeFacetName + "_Name"] : doc.Name;
		var manufacturer = '<span class="txtManufact fbold">' + doc.Manufacturer + '</span> ';
		
		secObj.find("div#docHolder").wrapInner(AjaxSolr.theme('createLink', name, docHandler)).prepend(manufacturer);

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
			var displayText = (doc["ForceAdd"]!=undefined? "Force add": "Elevated") + " at position " + doc["Elevate"];
			
			if(doc["ElevateType"] === "FACET")
				displayText = 'Included in <a href="javascript:void(0);"><span class="fgray">Facet Rule</span></a> ' + (doc["ForceAdd"]!=undefined? "force add": "elevated") + ' at position '+ doc["Elevate"];

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
			var forceAddIcon = 'images/icon_forceAdd_disable.png';
			var elevateIcon = 'images/icon_elevate_disable.png';
			var excludeIcon = 'images/icon_exclude_disable.png';
			var demoteIcon = 'images/icon_demote_disable.png';
			var featureIcon = 'images/icon_starGray.png';

			var forceAddHover = "Force Add";
			var elevateHover = "Elevate";
			var excludeHover = "Exclude";
			var demoteHover = "Demote";
			var featureHover = "Feature";

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
				forceAddHover = "Force Add";
			}
			
			if (doc.Feature != undefined){
				featureIcon = 'images/icon_star.png'; 
				featureHover = "Remove Feature";
			}

			//Add Feature Button
			//secObj.find("div #featureHolder").append(AjaxSolr.theme('createLink', '', elevateHandler));
			//secObj.find("div #featureHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', featureicon) + '" alt="' + feaHover + '" title="' + feaHover + '">');

			//Add Force Add Button
			secObj.find("div#forceAddHolder").append(AjaxSolr.theme('createLink', '', forceAddHandler));
			secObj.find("div#forceAddHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', forceAddIcon) + '" alt="' + forceAddHover + '" title="' + forceAddHover + '">');
			
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
				output += '<td width="25%" class="exclude"><input type="checkbox" id="checkbox-' + i + '" class="firerift-style-checkbox" value="' + facet.replace(/\"/g,'&quot;') + '"/></td>';
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

	AjaxSolr.theme.prototype.createSelectionLink = function (id, items) {

		var selection = $('.' + id);

		for (var i = 0, l = items.length; i < l; i++) {
			var classSelector = id + i;
			selection.append($('<div class="' + classSelector + ' farial fsize12 fDGray w220 padTB5 borderB wordwrap"><span class="lnk">'));
			selection.find('.' + classSelector + ' span.lnk').append(items[i]);
			selection.find('.' + classSelector + ' span.lnk a:not(#removeAll):not(#removeMultiple)').prepend('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/btn_delete_big.jpg') + '" width="10" height="10" style="margin-right:5px">');
			selection.find('.' + classSelector + ' span.lnk a#removeMultiple').prepend('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/icon_deleteFilter.png') + '" width="10" height="10" style="margin-right:5px">').addClass("fbold");
			selection.find('.' + classSelector + ' span.lnk a#removeMultiple').parents('div.' + classSelector).addClass("alt marT5 borderT");
			selection.find('.' + classSelector + ' span.lnk a#single').parents('div.' + classSelector).addClass("alt marT5 borderT");
			selection.find('.' + classSelector + ' span.lnk a#multiple').addClass("padL10");
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
		var $a = $('<a href="javascript:void(0)"/>').text(value).click(handler);
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
		return GLOBAL_contextPath + "/js/jquery/ajaxsolr.custom/" + filename;
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
