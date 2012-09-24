(function($){

	var FacetSort = {
			tabSelectedId: 1,
			
			showFacetSort : function(){
				var self = this;
				
				self.addTabListener();
			},
			
			setActivetab: function(){
				switch(parseInt(self.tabSelectedId)){
				case 1: break;
				case 2: break;
				};
			},
			
			addTabListener: function(){
				var self = this;
				
				$("#facetsort").tabs("destroy").tabs({
					show: function(event, ui){
						var tabNumber = ui.index;
						self.tabSelectedId = tabNumber + 1;
						self.setActiveTab();
						switch(self.tabSelectedId){
							case 1: break;
							case 2: break;
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