(function ($) {

	AjaxSolr.ProductAttributeFilterWidget = AjaxSolr.AbstractWidget.extend({
	
		afterRequest: function () {
			var self = this;
			$(self.target).empty();
			
			if (self.manager.response.response["numFound"] > 0 && $.isNotBlank(self.manager.store.values('q'))){
				$(self.target).html(AjaxSolr.theme('productAttributeFilter'));
				self.addCheckboxListener();
				self.addDropdownListener();
			}
		},
		
		/**
		 * Dependency:
		 * Id of checkbox element should be the Solr field to filter
		 */
		addCheckboxListener: function(){
			var self = this;

			$(self.target).find('input[type="checkbox"].checkboxFilter').each(function(key, el){
				
				var checked = $.isArray(self.manager.store.find("fq", $(el).prop("id")+":1"));
					
				$(el).prop("checked", checked).on({
					click: function(evt){
						
						var elVal =  $(this).prop("id") + ":1";
						
						if ($(this).is(":checked")){
							self.manager.store.addByValue('fq', elVal);
						}else{
							self.manager.store.removeByValue('fq', elVal);
						}
						
						self.manager.doRequest();
					}
				});
				
			});
		},
		
		/**
		 * Dependency:
		 * value of select option should be the Solr fq parameter, and "all" for reset filter
		 */
		addDropdownListener: function(){
			var self = this;
			
			$(self.target).find('select.dropdownFilter').each(function(key, el){
				
				$(el).prop("selectedIndex", 0);
				
				$(el).find('option').each(function(ky,vl){
					if ($.isArray(self.manager.store.find('fq', $(vl).val()))){
						$(el).prop("selectedIndex", $(vl).index());
					}
				});
				
				$(el).on({
					change: function(evt){
						var elVal = $.trim($(this).val());
						
						$(this).find('option').each(function(ky,vl){
							if($.trim($(vl).val()).toLowerCase()!=="all"){
								self.manager.store.removeByValue('fq', $(vl).val());
							}
						});
						
						if ($.isNotBlank(elVal) && elVal.toLowerCase() !== "all"){
							self.manager.store.addByValue('fq', elVal);
						}
						
						self.manager.doRequest();
					}
				});
			});
		}
	});
})(jQuery);