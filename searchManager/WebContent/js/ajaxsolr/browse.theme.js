(function ($) {

	AjaxSolr.theme.prototype.noSearchResult = function (keyword) {
		var output  = '';

		output  +='<div class="containerAB txtAL">';
		output  +='<h2>No products found for "' + keyword + '"</h2>';
		output  +='<p class="contentAB">Proin varius dapibus metus, ac gravida enim pretium sed. Phasellus varius, elit id posuere vestibulum, justo metus consectetur odio, in consectetur turpis metus et odio. </p>';
		output  +='<ol class="marT10 marRL20">';
		output  +='<li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>';
		output  +='<li>Morbi eget leo sit amet sapien commodo ultricies sed id turpis. Aenean tortor arcu, porttitor sed placerat at, adipiscing vitae metus. </li>';
		output  +='<li>Donec vitae metus lacus, at sollicitudin leo. Proin metus tellus, molestie in fermentum eu, congue a risus.</li>';
		output  +='</ol>';
		output  +='</div>';

		output  +='<div class="containerAC txtAL clearfix">';
		output  +='<h2>Browse By Product Category</h2>';
		output  +='<p class="contentAC">';
		output  +='<ul>';
		output  +='	<li>Computers</li>';
		output  +='<li>Servers</li>';
		output  +='<li>Storage</li>';
		output  +='<li>Printers</li>';
		output  +='<li>Software</li>';
		output  +='<li>Networking</li>';
		output  +='<li>Displays</li>';
		output  +='<li>Accessories</li>';
		output  +='<li>Power</li>';
		output  +='<li>Memory</li>';
		output  +='<li>Scanners</li>';
		output  +='<li>Electronics</li>';
		output  +='<li>Supplies</li>';
		output  +='<li>Projectors</li>';
		output  +='<li>Browse All Categories</li>';
		output  +='</ul>';
		output  +='</p>';
		output  +='</div>';

		return $(output);
	};
	
	AjaxSolr.theme.prototype.searchWithin = function () {
		var output  = '<div class="leftContainerT">Search Within</div>';
		output += '<div class="leftContainerBody">';
		output += '<input type="text" id="searchWithinInput" class="searchBoxIconLBg w163 padTB4 fgray">';
		output += '<a href="javascript:void(0)" id="searchbutton" class="btnGraph"><div class="btnGraph btnGoB floatR"></div></a>';
		output += '</div>';
		output += '<div class="leftContainerB"></div>';
		
		return $(output);
	};
	
	AjaxSolr.theme.prototype.filterByType = function(headerText) {
		var output  = '';
		
		output  += '<div id="sideHeader" class="sideHeader posRel clearB" style="margin-top: 27px;">';
		output  += '<img src="../images/corner_tl.png" class="curveTL"/>';
		output  += '<img src="../images/corner_tr.png" class="curveTR"/>';
		output  += headerText;
		output  += '<img src="../images/corner_bl.png" class="curveBL"/>';
		output  += '<img src="../images/corner_br.png" class="curveBR"/>';
		output  += '</div>';
		output  += '<div class="clearB floatL w230 padL5 fsize12 marT8">';
		output  += '<label class="floatL w30 list"><input type="checkbox"></label><label class="floatL w170 list padT3">Remove Product Descriptions</label>';
		output  += '<div class="clearB borderT padB5 fsize12 padT10">License Product</div>';
		output  += '<select class="w100p mar0">';
		output  += '<option value="both">Both License & Non-License</option>';
		output  += '<option value="license">License Products Only</option>';
		output  += '<option value="non-license">Non-License Products Only</option>';
		output  += '</select>';
		output  += '<div class="clearB"></div>';
		output  += '</div>';
	
		return $(output);
	};

	AjaxSolr.theme.prototype.createFacetHolder = function (facetLabel, facet) {
		var output  = '<div class="clearB floatL w240">';
		output += '<div id="' + facet + '" class="facetHeader farial fsize16 fwhite" style="padding-left:10px; padding-top:7px; margin-top:27px">' + facetLabel + '</div>';
		output += '<div class="' + facet +' clearB floatL w230 padL10"></div>';  
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

	AjaxSolr.theme.prototype.result = function (i, hasKeyword, doc, snippet, auditHandler, docHandler, debugHandler, featureHandler, elevateHandler, excludeHandler) {

		var altclass ="";

		if (i % 2 ==1)
			altclass=" alt-bgGray";

		var output = '<li id="resultItem_' + doc.EDP + '" class="handle' + altclass + '">';
		output += '<table width="100%" border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray marT10 ">';
		output += '	<tr>';
		output += '      <td width="28%" rowspan="5" align="center" valign="top"><div style="width:116px; height:100px" class="border itemImg">';
		output += '	   		<img src="' + doc.ImagePath + '"></div>';
		output += '	   </td>';
		output += '      <td colspan="2" align="left" valign="top" class="fbold borderB">';
		output += '			<div class="floatL"> ';
		output += '				<div id="auditHolder" class="iconHolder floatL"></div>';
		output += '				<div class="floatL marL10">' + doc.Manufacturer + '</div>';
		output += '			</div>';
		output += '        <div class="floatR ruleOptionHolder" >'; 
		output += '			<div id="expiredHolder" class="elevTxtHolder" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/expired_stamp50x16.png") + '"></div>';
		output += '			<div id="featureHolder" class="iconHolder" style="margin-top:-1px; margin-left:3px"></div>';
		output += '			<div id="elevateHolder" class="iconHolder"></div>';
		output += '			<div id="excludeHolder" class="iconHolder"></div>';
		output += '        </div>';
		output += '      </td>';
		output += '	</tr>';
		output += '   <tr>';
		output += '		<td width="59%" align="left" valign="top" class="padT5">';
		output += '       	<div class="floatL"><div id="docHolder"></div></div>';
		output += '		</td>';
		output += '      	<td width="13%" rowspan="2" align="right" valign="top" class="padT5">';
		output += '       	<div>';
		output += '				<div id="cartPriceHolder" class="fred fbold"></div>';
		output += '        		<div class="padT5">';
		output +=	'					<div class="txtAR" style="width:116px; margin:0 auto"><img src="' + AjaxSolr.theme('getAvailability', doc, "icon") + '" style="margin-bottom:-5px"> ' + AjaxSolr.theme('getAvailability', doc) + '</div>';
		output += '		 		</div>';
		output += '			</div>';
		output +=	'		</td>';
		output += '   </tr>';
		output += '   <tr>';
		output += '		<td align="left" valign="top" class="padT5">' + snippet + '</td>';
		output += '   </tr>';
		output += '   <tr>';
		output += '		<td colspan="2" align="left" valign="top" class="padTB7 fgray">';
		output += '			<table width="100%" border="0" cellpadding="0" cellspacing="0">';
		output += '				<tr>';
		output += '       			<td width="60"><span class="fgreen"><div><div id="debugHolder" class="floatL"></div></div></td>';
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
		secObj.find("div #cartPriceHolder").append('$' + doc.CartPrice);

		//TODO: make this dynamic
		var name = $.isNotBlank(doc.MacMall_Name)? doc.MacMall_Name : doc.Name;
		
		secObj.find("div #docHolder").wrapInner(AjaxSolr.theme('createLink', name, docHandler));

		//Add Audit Button
		secObj.find("div #auditHolder").html(AjaxSolr.theme('createLink', '', auditHandler));
		secObj.find("div #auditHolder a").html('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/icon_history.png') + '" alt="Audit Trail" title="Audit Trail">');
		
		//Add Debug link
		if (doc.Elevate == undefined){
			secObj.find("div #debugHolder").wrapInner(AjaxSolr.theme('createLink', 'Score: ' + doc.score, debugHandler));
			secObj.find("div #debugHolder a").addClass("btnShade btnCream");
			secObj.find("div #debugHolder a").wrapInner("<span class='btnShade'></span>");
		}else{		  
			secObj.find("div #debugHolder").wrapInner(AjaxSolr.theme('createLink', 'Elevated Position: ' +  doc.Elevate, null));
			secObj.find("div #debugHolder a").addClass("btnShade btnGreen");
			secObj.find("div #debugHolder a").wrapInner("<span class='btnShade'></span>");
		}

		//Add Elevate Button if search has keyword
		if (hasKeyword){
			var bigbetsicon = 'images/icon_arrowUpDisable.png';
			var featureicon = 'images/icon_starGray.png';
			var deleteicon = 'images/btn_delete.png';

			var feaHover = "Feature";
			var eleHover = "Elevate";

			if (doc.Elevate != undefined){
				bigbetsicon = 'images/icon_arrowUp.png'; 
				eleHover = "Remove Elevate";
			} 

			if (doc.Feature != undefined){
				featureicon = 'images/icon_star.png'; 
				feaHover = "Remove Feature";
			}

			//Add Feature Button
			secObj.find("div #featureHolder").append(AjaxSolr.theme('createLink', '', elevateHandler));
			secObj.find("div #featureHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', featureicon) + '" alt="' + feaHover + '" title="' + feaHover + '">');

			//Add Elevate Button
			secObj.find("div #elevateHolder").append(AjaxSolr.theme('createLink', '', elevateHandler));
			secObj.find("div #elevateHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', bigbetsicon) + '" alt="' + eleHover + '" title="' + eleHover + '">');

			//Add Exclude Button
			secObj.find("div #excludeHolder").append(AjaxSolr.theme('createLink', '', excludeHandler));
			secObj.find("div #excludeHolder a").append('<img src="' + AjaxSolr.theme('getAbsoluteLoc', deleteicon) + '" alt="Exclude" title="Exclude">');
		}

		return secObj;
	};

	AjaxSolr.theme.prototype.snippet = function (doc) {
		var output = '';

		//TODO: make this dynamic
		var description = $.isNotBlank(doc.MacMall_Description)? doc.MacMall_Description : doc.Description;  
			
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

	AjaxSolr.theme.prototype.displayFacetMoreOptions = function (value, title, facets) {
		var output = ''; 

		output += '<div class="tblcontContainer">';
		output += '<table cellspacing="0" cellpadding="0" border="0" width="360">';
		output += '<tr>';
		output += '<td>';
		output += ' <table cellspacing="0" cellpadding="0" width="360px" class="marT10">';
		output += '<tr>';
		output += '<td colspan="2" class="top"><div class="floatL w240">Search: <input type="text" id="searchField" class="searchBoxIconBg"></div> <div class="searchCount fsize11 fgray w110 floatL txtAR padT3"></div></td>';
		output += '</tr>';
		output += '<tr><td colspan="2"> &nbsp; </td></tr>'
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
				output += '<td class="values"><span class="value">' + facet + '</span></td>';
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
		output += '<div class="fsize16 titleToggle" style="margin:0 "><h2 style="padding-top:8px; margin:0 10px">Current Elevation</h2></div >';
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
		output += '						<li class="label">SKU #:</li><li class="value" id="partNo">846896</li>'; 
		output += '						<li class="label">Mfr Part #:</li><li class="value" id="mfrNo">ERgt129</label>';
		output += '						<li id="validityText" class="label"></li><li class="value" id="expiryDate">02/21/2010</li>';
		output += '				  	</ul>';
		output += '					</div>';
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

	AjaxSolr.theme.prototype.createSelectionLink = function (list, items, separator) {

		var selDiv = $('.' + list);

		for (var i = 0, l = items.length; i < l; i++) {
			if (AjaxSolr.isArray(items[i])) {
				for (var j = 0, m = items[i].length; j < m; j++) {
					if (separator && j > 0) {
						selDiv.append(separator);
					}
					selDiv.append($('<div class="' + list + i + ' farial fsize12 fDGray w220 borderB padTB5"><span class="lnk">'));
					selDiv.find('.'+list + i+' span.lnk').append(items[i][j]);
					if (items.length==1 || items.length > 1 && i> 0)
						selDiv.find('.'+list + i+' span.lnk a').prepend('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/btn_delete_big.jpg') + '" width="10" height="10" style="margin-right:5px">');
				}
			}
			else {
				if (separator && i > 0) {
					selDiv.append(separator);
				}
				selDiv.append($('<div class="' + list + i + ' farial fsize12 fDGray w220 borderB padTB5"><span class="lnk">'));
				selDiv.find('.'+list + i+' span.lnk').append(items[i]);
				if (items.length==1 || items.length > 1 && i>0)
					selDiv.find('.'+list + i+' span.lnk a').prepend('<img src="' + AjaxSolr.theme('getAbsoluteLoc', 'images/btn_delete_big.jpg') + '" width="10" height="10" style="margin-right:5px">');
			}
		}

		return selDiv;
	};

	AjaxSolr.theme.prototype.createFacetLink = function (facetId, facetField, facet, count, handler) {

		var output = '<div class="' + facetId + ' farial fsize12 fDGray w220 borderB padTB5">';
		output += '	<div id="facetFilterHolder"><span class="lnk"></span></div>';
		output += '</div>';

		var scbObj= $("." + facetField).append($(output));
		scbObj.find(" ." + facetId + " div#" + "facetFilterHolder span.lnk").append(AjaxSolr.theme('createLink', facet + " (" + count + ")", handler));

	};

	AjaxSolr.theme.prototype.createFacetMoreOptionsLink = function (facetField, facetValues, value, handler) {
		$('.' + facetField).append('<div id="more' + facetField + '" class="farial fsize12 fDGray w220 borderB padTB5">');
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


	AjaxSolr.theme.prototype.createLink = function (value, handler) {
		return $('<a href="javascript:void(0)"/>').text(value).click(handler);
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
