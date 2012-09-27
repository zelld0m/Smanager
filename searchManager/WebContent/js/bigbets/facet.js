(function($){

	var FacetSort = {
			moduleName: "Facet Sort",
			selectedRule:  null,
			tabSelectedId: 1,
			
			rulePage: 1,
			
			rulePageSize: 5,
			
			prepareFacetSort : function(){
				clearAllQtip();
				$("#preloader").show();
				$("#submitForApproval, #facetsorting, #noSelected").hide();
				$("#titleHeader").html("");
			},
			
			showFacetSort : function(){
				var self = this;
				
				self.prepareFacetSort();
				$("#preloader").hide();
				self.getFacetSortRuleList(1);
				
				if(self.selectedRule==null){
					$("#noSelected").show();
					$("#titleText").html(self.moduleName);
					return;
				}
				
				$("#facetsorting").show();
				self.addTabListener();
			},
			
			setFacetSort : function(rule){
				var self = this;
				self.selectedRule = rule;
				self.showFacetSort();
			},
			
			setActiveTab: function(facetName){
				var facetNameLower = facetName.toLowerCase();
				
				var $facet = $('div#'+facetNameLower);
				var $facetTab = $('div#facetTabPattern').clone();
				
				$facet.html("");
				
				$facetTab.show();
				$facetTab.prop({id : facetNameLower});
				
				$facetTab.find("span#addFacetSortTitleHeader").text("Elevated " + facetName + " Values");
				$facetTab.find("span#addNewLink").text("[add new " + facetNameLower + " value]");
				
				$facetTab.find("div#facetvaluelist").prop({id : facetNameLower +'list'});
				
				$facet.append($facetTab);
			},
			
			getFacetValueList : function(facet){
				var self = this;
				
				$("#"+facet.toLowerCase()+"list").viewfacetvalues({
					keyword:"",
					facetField: facet
				});
			},
			
			getFacetSortRuleList : function(page) { 
				var self = this;

				$("#keywordSidePanel").sidepanel({
					fieldId: "ruleId",
					fieldName: "ruleName",
					page: self.rulePage,
					pageSize: self.rulePageSize,
					headerText : "Facet Sorting Rule",
					searchText : "Enter Keyword",
					showAddButton: allowModify,
					filterText: self.ruleFilterText,

					itemDataCallback: function(base, keyword, page){
						self.rulePage = page;
						self.ruleFilterText = keyword;
						RedirectServiceJS.getAllRule(keyword, page, base.options.pageSize, {
							callback: function(data){
								base.populateList(data);
								base.addPaging(keyword, page, data.totalSize);
							},
							preHook: function(){ base.prepareList(); }
						});
					},

					itemAddCallback: function(base, name){
						jAlert("Adding " + name, "Facet Sort");
						//TODO
						/*FacetSortServiceJS.checkForRuleNameDuplicate("", name, {
							callback: function(data){
								if (data==true){
									jAlert("Another facet sorting rule is already using the name provided.","Facet Sort");
								}else{
									RedirectServiceJS.addRuleAndGetModel(name, {
										callback: function(data){
											if (data!=null){
												base.getList(name, 1);
												self.selectedRule = data;
												self.setFacetSort(data);
											}else{
											}
										},
										preHook: function(){ 
											base.prepareList(); 
										}
									});
								}
							}
						});*/
					},

					itemOptionCallback: function(base, id, name, model){
						var selector = '#itemPattern' + $.escapeQuotes($.formatAsId(id));

						//TODO
						//FacetSortServiceJS.getTotalKeywordInRule(id, {
						RedirectServiceJS.getTotalKeywordInRule(id,{
							callback: function(count){

								var totalText = (count == 0) ? "&#133;": "(" + count + ")"; 
								base.$el.find(selector + ' div.itemLink a').html(totalText);

								base.$el.find(selector + ' div.itemLink a,' + selector + ' div.itemText a').off().on({
									click: function(e){
										self.setFacetSort(model);
									}
								});
							},
							preHook: function(){ 
								base.$el.find(selector + ' div.itemLink a').html('<img src="../images/ajax-loader-rect.gif">'); 
							}
						});

						DeploymentServiceJS.getRuleStatus(self.moduleName, id, {
							callback:function(data){
								base.$el.find(selector + ' div.itemSubText').html(getRuleNameSubTextStatus(data));	
							}
						});
					}
				});
			},
						
			addTabListener: function(){
				var self = this;
				
				$("#facetsort").tabs("destroy").tabs({
					show: function(event, ui){
						var tabNumber = ui.index;
						self.tabSelectedId = tabNumber + 1;
						
						switch(self.tabSelectedId){
							case 1: self.setActiveTab("Category"); self.getFacetValueList("Category"); break;
							case 2: self.setActiveTab("Manufacturer"); self.getFacetValueList("Manufacturer"); break;
						}
					}
				});
			},
			
			init : function() {
				var self = this;
				self.showFacetSort();
			}
	};
	

	$(document).ready(function() {
		FacetSort.init();
	});
})(jQuery);	