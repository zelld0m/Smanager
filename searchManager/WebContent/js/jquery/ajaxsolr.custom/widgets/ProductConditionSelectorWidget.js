(function ($) {

	AjaxSolr.ProductConditionSelectorWidget = AjaxSolr.AbstractWidget.extend({
		SOLRFIELD_REFURBISHED: "Refurbished_Flag",
		SOLRFIELD_OPENBOX: "OpenBox_Flag",
		SOLRFIELD_CLEARANCE: "Clearance_Flag",
		inputToSolrFieldMap: [],
		solrFacetIndices : -1, 
		solrFacetValue : "", 
		
		init: function(){
			var self = this;
			
			self.inputToSolrFieldMap["refurbished"] = self.SOLRFIELD_REFURBISHED;
			self.inputToSolrFieldMap["openbox"] = self.SOLRFIELD_OPENBOX;
			self.inputToSolrFieldMap["clearance"] = self.SOLRFIELD_CLEARANCE;
		},
		
		beforeRequest: function () {
			var self = this;
			
			self.solrFacetIndices = -1, 
			self.solrFacetValue = "", 
			
			$(self.target).find(".condselector").off().prop({
				readonly: true,
				disabled: true
			});
		},
		
		afterRequest: function () {
			var self = this;
			var fields = [self.SOLRFIELD_REFURBISHED, self.SOLRFIELD_OPENBOX, self.SOLRFIELD_CLEARANCE];
			var arrSolrFacetValues = [];
			$(self.target).html(self.getTemplate());
			
			// Find fq for PCM-G selector
			for(var i=0; i< fields.length; i++){
				self.solrFacetIndices = self.manager.store.find('fq', new RegExp('^-?' + fields[i] + ':'));
				
				if($.isEmptyObject(self.solrFacetIndices)){
					
				}else if (self.solrFacetIndices.length > 1){
					console.log('Multiple ' +  fields[i] + ' for product condition selector');
				}else{
					self.solrFacetValue = self.manager.store.findByIndex('fq', self.solrFacetIndices[0]);
					break;
				};
			}
			
			// Persist checkbox value
			if($.isNotBlank(self.solrFacetValue)){
				arrSolrFacetValues = self.solrFacetValue.split(' ');
				
				$(self.target).find(".condselector").prop({
					checked: false
				});
				
				for(var i=0; i< arrSolrFacetValues.length; i++){
					switch(arrSolrFacetValues[i]){
					case self.SOLRFIELD_REFURBISHED + ":1": 
						$(self.target).find("input#refurbished").prop("checked", true);
						break;
					case self.SOLRFIELD_OPENBOX + ":1": 
						$(self.target).find("input#openbox").prop("checked", true);
						break;
					case self.SOLRFIELD_CLEARANCE + ":1": 
						$(self.target).find("input#clearance").prop("checked", true);
						break;
					}
				}
			}
			
			self.addListener();
		},

		addFacetValue: function(){
			var self = this;
			var arrSelectorId = ["refurbished", "openbox", "clearance"];
			var newFacetValue = "";
			
			if ($.isNotBlank(self.solrFacetValue)) self.manager.store.removeByValue("fq", self.solrFacetValue);
			
			var matched = 0;
			for (var i=0; i<arrSelectorId.length; i++){
				if($(self.target).find("input#" + arrSelectorId[i]).is(":checked")){
					if (matched > 0) newFacetValue += ' '; 
					newFacetValue += self.inputToSolrFieldMap[arrSelectorId[i]] + ":1";
					matched++;
				}
			}
			
			if ($.isNotBlank(newFacetValue)) self.manager.store.addByValue("fq", newFacetValue);
			self.manager.doRequest(0);
		},
		
		addListener: function(){
			var self = this;
			
			$(self.target).find(".condselector").prop({
				readonly: false,
				disabled: false
			}).off().on({
				click:function(e){
					self.addFacetValue();
				}
			});
		},
		
		getTemplate: function(){

			var output  = '';

			output  += '<div class="box marT8">';
			output  += '	<h2>Condition</h2>';
			output  += '	<ul>';
			output  += '		<li><input type="checkbox" id="refurbished" class="condselector"><label for="refurbished" class="label_condition">Refurbished</label></li>';
			output  += '		<li><input type="checkbox" id="openbox" class="condselector"><label for="openbox" class="label_condition">Open Box</label></li>';
			output  += '		<li><input type="checkbox" id="clearance" class="condselector"><label for="clearance" class="label_condition">Clearance</label></li>';
			output  += '	</ul>';
			output  += '</div>';
			
			return output;
		}
	});
})(jQuery);