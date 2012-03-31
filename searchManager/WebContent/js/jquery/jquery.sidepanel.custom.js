(function($){

	$.sidepanel = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("sidepanel", base);

		base.init = function(){
			base.options = $.extend({},$.sidepanel.defaultOptions, options);

			base.populateTemplate();
			base.getList("", base.options.page);

			base.$el.find('input[id="searchTextbox"]').on({
				blur: function(e){if ($.trim($(e.target).val()).length == 0) $(e.target).val(base.options.searchText);},
				focus: function(e){if ($.trim($(e.target).val()) == base.options.searchText) $(e.target).val("");},
				keyup: function(e){
					setTimeout(function(){
						base.getList($.trim($(e.target).val()), 1);
					}, base.options.reloadRate);  
				}
			});

			base.$el.find('a#addButton').on({
				click: function(e){
					var name = $.trim(base.$el.find('input[type="text"]').val());
					base.options.itemAddCallback(base, $.isNotBlank(name) && name != base.options.searchText? name : "");
				}
			});
		};

		base.populateTemplate = function(){

			if (base.options.region="left"){}
			if (base.options.region="content"){}
			
			
			var content ='<div id="sideHeader" class="sideHeader posRel">';

			content+= '<img src="../images/corner_tl.png" class="curveTL"/>';
			content+= '<img src="../images/corner_tr.png" class="curveTR"/>';
			content+= base.options.headerText;
			content+= '<img src="../images/corner_bl.png" class="curveBL"/>';
			content+= '<img src="../images/corner_br.png" class="curveBR"/>';
			
			content+= '</div>';
			content+= '<div class="sideSearch">';
			content+= '<span style="padding-top:7px">';
			content+= '<a id="addButton" class="btnGraph btnAddGreen floatR" href="javascript:void(0);"></a>';
			content+= '<input id="searchTextbox" class="farial fsize12 fgray leftSearch" type="text" value="' + base.options.searchText + '">';
			content+= '</span>';
			content+= '</div>';

			content+= '<div id="sideContent" class="sideContent">';
			content+= '<table width="100%">';
			content+= '	<tbody id="itemListing">';
			content+= '		<tr id="itemPattern" style="display:none;">';
			content+= '			<td class="padR10 padL10">';
			content+= '				<div class="itemHolder clearfix">';			      
			content+= '					<div class="itemText lnk"><a href="javascript:void(0);"></a></div>';
			content+= '					<div class="itemLink"><a href="javascript:void(0);"></a></div>';
			content+= '				</div>';		
			content+= '			</td>';
			content+= '		</tr>';
			content+= '	</tbody>';
			content+= '</table>'; 
			content+= '</div>'; 

			content+= '<div id="sideFooter" class="sideFooter" >';
			content+= '<div id="sideBottomPaging" class="sideBottomPaging"></div>';
			content+= '</div>';

			base.$el.append(content);
		};

		base.getList = function(keyword, page) {
			base.options.itemDataCallback(base, keyword, page);
		};

		base.prepareList = function(){
			base.$el.find("tbody#itemListing").children().not("#itemPattern").remove();
			base.$el.find("#itemListing").prepend('<div class="pad10 txtAC w200"><p style="width:16px; text-align:center; margin:0 auto;"><img src="../images/ajax-loader-rect.gif"></p></div>'); 
			base.$el.find("#sideBottomPaging").attr("style", "display:none");
		};

		base.populateList = function(data){
			var list = data.list;

			// Delete all the rows except for the "pattern" row
			base.$el.find("tbody#itemListing").children().not("#itemPattern").remove();

			// populate list
			dwr.engine.beginBatch(); 
			for (var i = 0; i < data.list.length; i++) {
				var id = list[i][base.options.fieldId];
				var name = list[i][base.options.fieldName];
				var suffixId = $.formatAsId(id);

				base.$el.find("tr#itemPattern").clone().appendTo("tbody#itemListing").attr("id","itemPattern" + suffixId);
				suffixId = $.escapeQuotes(suffixId);
				
				base.$el.find('#itemPattern' + suffixId + ' div.itemText a').html(name);
				base.$el.find('#itemPattern' + suffixId + ' div.itemText a').on({click:base.options.itemNameCallback},{name:name, id:id});
				base.$el.find('#itemPattern' + suffixId).show();
				
				base.options.itemOptionCallback(base, id, name);
			}
			dwr.engine.endBatch();
		};

		base.addPaging = function(keyword, page, total){
			base.$el.find("#sideBottomPaging").paginate({
				type: 'short',
				pageStyle: base.options.pageStyle,
				currentPage:page, 
				pageSize:base.options.pageSize,
				totalItem:total,
				callbackText: function(itemStart, itemEnd, itemTotal){
					return itemStart + "-" + itemEnd + " of " + itemTotal;
				},
				pageLinkCallback: function(e){ 
					base.getList(keyword, e.data.page); 
					base.options.pageChangeCallback(e.data.page); 
				},
				nextLinkCallback: function(e){ 
					base.getList(keyword, e.data.page+1); 
					base.options.pageChangeCallback(e.data.page+1); 
				},
				prevLinkCallback: function(e){ 
					base.getList(keyword, e.data.page-1); 
					base.options.pageChangeCallback(e.data.page-1);
				},
				firstLinkCallback: function(e){ 
					base.getList(keyword, 1); 
					base.options.pageChangeCallback(1);
				},
				lastLinkCallback: function(e){ 
					base.getList(keyword, e.data.totalPages); 
					base.options.pageChangeCallback(e.data.totalPages);
				}
			});

			base.$el.find("#sideBottomPaging").attr("style", total > 0 ? "display:float" : "display:none");
		};

		// Run initializer
		base.init();
	};

	$.sidepanel.defaultOptions = {
			page:1,
			region: "left",
			pageSize: 10,
			pageStyle: "style1",
			headerText: "",
			searchText: "",
			searchLabel: "",
			itemDataCallback: function(e){},
			itemOptionCallback: function(e){},
			itemNameCallback: function(e){},
			itemAddCallback: function(e){},
			pageChangeCallback: function(e){},
			reloadRate: 250
	};

	$.fn.sidepanel = function(options){

		if (this.length) {
			return this.each(function() {
				$(this).empty();
				(new $.sidepanel(this, options));
			});
		};
	};
})(jQuery);