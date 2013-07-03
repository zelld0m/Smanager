(function($){

	$.selectbox = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("selectbox", base);

		base.setId = function(ui,id){
			ui.find("div:first").prop({
				id: $.isNotBlank(id)? id: "plugin-selectbox-" + base.options.id
			});
		};
		
		base.init = function(){
			base.options = $.extend({},$.selectbox.defaultOptions, options);
			
			if(base.options.isPopup){
				base.$el.qtip({
					id: "plugin-selectbox-qtip",
					content: {
						text: $('<div/>'),
						title: {text: base.options.qTipTitle, button: true }
					},
					show:{
						modal:true
					},
					style: {
						width: 'auto'
					},
					events: { 
						render: function(event, api){
							base.api = api;
							base.$el = $("div", api.elements.content);
							base.$el.empty().append(base.getTemplate());
							base.setId(base.$el);
						},

						show: function(event, api){
							base.populateContent();
						},
						
						hide: function(event, api){
							api.destroy();
						}
					}
				});
			}else{
				base.$el.empty().append(base.getTemplate());
				base.setId(base.$el);
				base.populateContent();
			}
		};
		
		base.populateContent = function(){
			base.populateSelectionList(1);
			base.populateSelectedList();
		};
		
		base.populateSelectedList = function(){
			var $selectedItems = base.options.selectedItems;
			var $selectedList = base.$el.find("#selectedList");
			
			if($selectedItems!=null && $selectedItems.length > 0){
				$selectedList.find("tr#noItem").hide();
				for(var i in $selectedItems){
					base.addItemAsSelected($selectedItems[i]["campaign"]); // TODO: This must be updated to model Campaign
				}
			}
		};
		
		base.populateSelectionList = function(page){
			base.$el.find("#selectionList").sidepanel({
				moduleName: "Campaign",
				fieldName: "ruleName",
				fieldId: "ruleId",
				searchText: "Select Campaign",
				page: page,
				region: "content",
				pageStyle: "style2",
				pageSize: base.options.maxSelectionList,
				headerText : "List of Campaigns",
				headerTextAlt : "Campaigns",
				itemTextClass: "cursorText",
				showAddButton: false,
				showStatus: true,

				itemDataCallback: function(sBase, keyword, page){
					CampaignServiceJS.getRules(keyword, page, sBase.options.pageSize, {
						callback: function(data){
							sBase.populateList(data, keyword);
							sBase.addPaging(keyword, page, data.totalSize);
						},
						preHook: function(){ sBase.prepareList(); }
					});
				},
				itemNameCallback: function(sBase, item){
					base.addItemAsSelected(item.model, true);
				}
			});
			
		};
		
		base.addItemAsSelected = function(rule, asDirty){
			var $table = base.$el.find("#selectedList").find("#selectedTable");
			var $selectedTr = $table.find("tr#"+rule["ruleId"]);
			
			if($selectedTr.length){
				if($selectedTr.hasClass("delTemp")){
					$selectedTr.removeClass("delTemp").find(".itemStatus").empty();
					$selectedTr.find(".itemRemove").show();
					
					jAlert(rule["ruleName"] + " is added back as selected", "Select Campaign");
				}else{
					jAlert(rule["ruleName"] + " is already selected", "Select Campaign");
				}
				return;
			}
			
			var $tr = $table.find("tr#selectedItemPattern").clone();
			
			if(asDirty){
				$tr.addClass("addTemp").find(".itemStatus").text("For Add");
			}
				
			$tr.prop({
				id: rule["ruleId"]
			}).show();
			
			$tr.find(".itemName").text(rule["ruleName"]);
			$tr.find(".itemCreatedDate").text(rule["formattedCreatedDateTime"]);
			$tr.find(".itemRemove").off().on({
				click: function(e){
					var $tr = $(e.currentTarget).parents("tr#" + e.data.rule["ruleId"]);
					
					if($tr.hasClass("addTemp")){
						if($tr.siblings("tr.selectedItem:not(#selectedItemPattern)").length==0){
							$tr.siblings("tr#noItem").show();
						}
						$tr.remove();
					}else{
						$tr.addClass("delTemp").find(".itemStatus").text("For Remove");
						$(this).hide();
					}
				}
			}, {rule: rule});
			
			$table.append($tr);
			
			if ($table.find("tr.selectedItem:not(#selectedItemPattern)").length > 0){
				$table.find("tr#noItem").hide();
			}
			
		};
		
		base.getTemplate = function(){
			var template = '';

			template += '<div class="plugin-selectbox">';
			template += '	<div id="selectionList"></div>';
			
			template += '	<!-- Selected List -->';
			template += '	<div id="selectedList">';
			template += '		<h3 class="fsize14 fbold pad8 mar0">';
			template += '			<span>Campaigns Using This Banner</span>';
			template += '			<span id="selectedTotal" class="txtAR fsize11 floatR"></span>';
			template +=	'		</h3>';
			template += '		<div style="overflow-y:scroll; height: 250px">';
			template += '			<table id="selectedTable" style="border:1px; width:100%" cellpadding="0" cellspacing="0">';
			template += '				<tbody id="selectedTableBody">';
			template += '					<tr class="selectedItem" id="selectedItemPattern" style="display:none">';
			template += '						<td class="pad0 txtAC">';
			template += '							<a class="itemRemove" href="javascript:void(0);">';
			template += '								<img src="' + GLOBAL_contextPath + '/images/icon_delete2.png" class="marL3">';
			template += '							</a>';
			template += '						</td>';
			template += '						<td>';
			template += '							<div class="marL3">';
			template += '								<span class="itemName"></span>';										
			template += '							</div>';
			template += '						</td>';
			template += '						<td>';
			template += '							<div class="marL3">';
			template += '								<span class="itemCreatedDate"></span>';										
			template += '							</div>';
			template += '						</td>';
			template += '						<td>';
			template += '							<div class="marL3">';
			template += '								<span class="itemStatus"></span>';										
			template += '							</div>';
			template += '						</td>';
			template += '					</tr>';
			template += '					<tr id="noItem">';
			template += '						<td colspan="4" class="txtAC">';
			template += '							<span>No records found!</span>';
			template += '						</td>';
			template += '					</tr>';
			template += '				</tbody>';
			template += '			</table>';					
			template += '		</div>';
			template += '	</div>';
			template += '	<!-- End Selected List -->';
			template += '</div>';

			return template;
		};

		// Run initializer
		base.init();
	},

	$.selectbox.defaultOptions = {
			id: 1,
			isPopup: false,
			qTipTitle: "Select Item",
			maxSelectionList: 5,
			selectedItems: null
	};

	$.fn.selectbox = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.selectbox(this, options));
			});
		};
	};

})(jQuery);