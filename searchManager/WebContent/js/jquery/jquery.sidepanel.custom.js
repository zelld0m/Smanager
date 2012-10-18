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

		base.addAddButtonListener = function(){
			base.$el.find("#addButton").on({
				click: function(e){
					var name = $.trim(base.$el.find('input[type="text"]').val());
					if (base.options.customAddRule){
						base.options.itemAddCallback(base, name.toLowerCase()!==base.options.searchText.toLowerCase()? name: ""); 
					}else{
						base.getDefaultAddRule(name.toLowerCase()!==base.options.searchText.toLowerCase()? name: "");
					}
				}
			});
		};

		base.getDefaultAddRuleTemplate = function(){
			var template ='';
			
			template += '<div>';
			template += '	<input id="ruleName">';
			template += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="addBtn"><div class="buttons fontBold">Add</div></a>';
			template += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="cancelBtn"><div class="buttons fontBold">Cancel</div></a>';
			template += '</div>';
			
			return template;
		};
		
		base.addDefaultAddRuleButtonListener = function(){
			base.contentHolder.find("#addBtn,#cancelBtn").off().on({
				click: function(e){
					switch($(e.currentTarget).attr("id").toLowerCase()){
					case "addbtn": 
						var ruleName = base.contentHolder.find("input#ruleName").val();
						
						if ($.isBlank(ruleName)){
							jAlert(base.options.headerText + " is required.", base.options.headerText);
						}else if (!isAllowedName(ruleName)){
							jAlert(base.options.headerText + " contains invalid value.", base.options.headerText);
						}else{
							base.options.itemAddCallback(base, ruleName); 
						}
						
						break;
					case "cancelbtn": base.api.destroy(); break;
					}
				}
			});
		};
		
		base.getDefaultAddRule =function(name){
			base.$el.find("a#addButton").qtip({
				content: {
					text: $('<div/>'),
					title: { text: base.options.itemTitle, button: true }
				},
				position: {
					target: base.$el.find("a#addButton")
				},
				show: {
					ready: true
				},
				style: {width: 'auto'},
				events: { 
					show: function(e, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getDefaultAddRuleTemplate());
						
						if($.isNotBlank(name))
							base.contentHolder.find("input#ruleName").val(name);
						
						base.addDefaultAddRuleButtonListener();
					},
					hide: function(e, api){
						api.destroy();
					}
				}
			});	
		};
		
		base.sendRequest = function(event){
			setTimeout(function(){
				base.newSearch = $.trim($(event.target).val());

				if (base.newSearch === base.options.searchText) {
					base.newSearch = "";
				};

				base.addAddButtonListener();

				if (base.oldSearch !== base.newSearch) {
					base.getList(base.newSearch, 1);
					base.oldSearch = base.newSearch;
					base.sendRequest(event);
					base.newSearch = "";
				}
				else {
					base.searchActivated = false;
				}
			}, base.options.reloadRate);  
		};

		base.timeout = function(event){
			if (!base.searchActivated) {
				base.searchActivated = true;
				base.sendRequest(event);
			}
		};

		base.init = function(){
			base.options = $.extend({},$.sidepanel.defaultOptions, options);

			base.populateTemplate();
			base.getList(base.options.filterText, base.options.page);

			base.searchActivated = false;
			base.oldSearch = "";
			base.newSearch = "";			

			if($.isNotBlank(base.options.filterText)){
				base.$el.find('input[id="searchTextbox"]').val(base.options.filterText);
			}

			base.$el.find('input[id="searchTextbox"]').on({
				// TODO: this does not detect when entries are pasted
				blur: function(e){
					if ($.trim($(e.target).val()).length == 0) 
						$(e.target).val(base.options.searchText);
					base.timeout(e);
				},
				focus: function(e){
					if ($.trim($(e.target).val()) == base.options.searchText)
						$(e.target).val("");
					base.timeout(e);
				},
				keyup: base.timeout
			});

			base.addAddButtonListener();
		};

		base.populateTemplate = function(){

			var content ='<div id="sideHeader" class="sideHeader posRel">';

			content+= '<img src="../images/corner_tl.png" class="curveTL"/>';
			content+= '<img src="../images/corner_tr.png" class="curveTR"/>';
			content+= base.options.headerText;
			content+= '<img src="../images/corner_bl.png" class="curveBL"/>';
			content+= '<img src="../images/corner_br.png" class="curveBR"/>';

			content+= '</div>';

			if (base.options.showSearch){
				content+= '<div class="sideSearch">';
				content+= '<span style="padding-top:7px">';

				var textClass="";

				switch(base.options.region){
				case "left": textClass = base.options.showAddButton? "w70p": "w88p"; break;
				case "content": textClass = base.options.showAddButton? "w78p": "w90p"; break;
				}

				if (base.options.showAddButton){
					content+= '<a id="addButton" class="btnGraph btnAddGreen floatR" href="javascript:void(0);"></a>';
				}

				content+= '<div class="searchBoxHolder ' + textClass + '"><input id="searchTextbox" maxlength="' + base.options.maxCharacter + '" class="farial fsize12 fgray w99p" type="text" value="' + base.options.searchText + '"></div><div class="clearB"></div>';

				content+= '</span>';
				content+= '</div>';
			}

			content+= '<div id="sideContent" class="sideContent">';
			content+= '	<table width="100%" id="itemListing">';
			content+= '		<tr id="itemPattern" class="itemRow" style="display:none;">';
			content+= '			<td class="padR10 padL10">';
			content+= '				<div class="itemHolder clearfix">';	
			content+= '					<div style="width:155px; float:left;">';
			content+= '						<div class="itemIcon lnk floatL w20"><a href="javascript:void(0);"></a></div>';
			content+= '						<div class="floatL w135">';
			content+= '							<div class="itemText lnk"><a href="javascript:void(0);"></a></div>';
			content+= '							<div class="itemSubText fgray" style="float:left; font-size:11px;"></div>';
			content+= '						</div>';
			content+= '					</div>';
			content+= '					<div class="itemLink">';
			content+= '						<img id="itemLinkPreloader" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif" style="display:none">';
			content+= '						<a id="itemLinkValue" href="javascript:void(0);">&#133;</a>';
			content+= '					</div>';
			content+= '				</div>';		
			content+= '			</td>';
			content+= '		</tr>';
			content+= '		<tr id="sideContentItemPreloader">';
			content+= '			<td>';
			content+= '				<div class="pad10 txtAC w200" style="padding:10px; text-align:center; width:200px;">';
			content+= '					<img src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
			content+= '				</div>';
			content+= '			</td>';
			content+= '		</tr>';
			content+= '	</table>'; 
			content+= '</div>'; 

			content+= '<div id="sideFooter" class="sideFooter" >';
			content+= '<div id="sideBottomPaging" class="sideBottomPaging"></div>';
			content+= '</div>';

			base.$el.append(content);
		};

		base.getList = function(keyword, page) {
			base.options.page = page;
			base.options.itemDataCallback(base, keyword, page);
		};

		base.prepareList = function(){
			var $table = base.$el.find("table#itemListing");
			$table.find("tr.itemRow:not(#itemPattern)").remove();
			$table.find("tr#sideContentItemPreloader").show();
			base.$el.find("#sideBottomPaging").hide();
		};

		base.populateList = function(data){
			var name = "";
			var id = "";
			var $tr = null;
			var list = data.list;
			var $table = base.$el.find("table#itemListing");
			
			$table.find("tr#sideContentItemPreloader").hide();
			
			// Delete all the rows except for the "pattern" row
			$table.find("tr.itemRow:not(#itemPattern)").remove();

			// populate list
			for (var i = 0; i < data.list.length; i++) {
				name = list[i][base.options.fieldName];
				id = "item" + $.formatAsId(i+1);
				
				$tr = $table.find("tr#itemPattern").clone();
				$tr.attr("id", id).show();
				$table.append($tr);
				
				$tr.find(".itemText > a").text(name).on({
					click: function(e){
						base.options.itemNameCallback(e.data.base, e.data);	
					}
				},{base: base, ui: $tr, name:name, id:id, model:list[i]});
				
				base.options.itemOptionCallback(base, {ui:$tr, name:name, id:id, model:list[i]});
				
				if(base.options.showStatus) base.getRuleStatus($tr, list[i]);
			}
		};

		base.getRuleStatus = function(tr, item){
			var $tr = tr;
			DeploymentServiceJS.getRuleStatus(base.options.moduleName, item["ruleId"], {
				callback:function(data){
					$tr.find('.itemSubText').html(getRuleNameSubTextStatus(data));
				}
			});
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
					base.getList(keyword, parseInt(e.data.page)+1); 
					base.options.pageChangeCallback(parseInt(e.data.page)+1); 
				},
				prevLinkCallback: function(e){ 
					base.getList(keyword, parseInt(e.data.page)-1); 
					base.options.pageChangeCallback(parseInt(e.data.page)-1);
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
			pageSize: 10,
			region: "left",
			pageStyle: "style1",
			headerText: "Keyword",
			searchText: "Search Keyword",
			searchLabel: "",
			filterText:"",
			maxCharacter: 50,
			customAddRule: false,
			itemTitle: "New Rule",
			showAddButton: true,
			showSearch: true,
			showStatus: true,
			itemDataCallback: function(e){},
			itemOptionCallback: function(base, item){},
			itemNameCallback: function(base, item){},
			itemAddCallback: function(e){},
			pageChangeCallback: function(e){},
			reloadRate: 2000
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