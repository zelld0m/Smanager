(function ($) {

	AjaxSolr.PCMGMultipleSelectorWidget = AjaxSolr.AbstractWidget.extend({
		SOLRFIELD_OPEN: "PCMG_OpenStoreFlag",
		SOLRFIELD_GOVT: "PCMG_GovStoreFlag",
		SOLRFIELD_ACAD: "PCMG_ACAStoreFlag",
		inputToSolrFieldMap: [],
		solrFacetIndices : -1, 
		solrFacetValue : "", 
		
		init: function(){
			var self = this;
			
			self.inputToSolrFieldMap["open"] = self.SOLRFIELD_OPEN;
			self.inputToSolrFieldMap["govt"] = self.SOLRFIELD_GOVT;
			self.inputToSolrFieldMap["acad"] = self.SOLRFIELD_ACAD;
		},
		
		beforeRequest: function () {
			var self = this;
			
			self.solrFacetIndices = -1, 
			self.solrFacetValue = "", 
			
			$(self.target).find(".pcmgselector").off().prop({
				readonly: true,
				disabled: true
			});
		},
		
		afterRequest: function () {
			var self = this;
			var fields = [self.SOLRFIELD_OPEN, self.SOLRFIELD_ACAD, self.SOLRFIELD_GOVT];
			var arrSolrFacetValues = [];
			$(self.target).html(self.getTemplate());
			
			// Find fq for PCM-G selector
			for(var i=0; i< fields.length; i++){
				self.solrFacetIndices = self.manager.store.find('fq', new RegExp('^-?' + fields[i] + ':'));
				
				if($.isEmptyObject(self.solrFacetIndices)){
					
				}else if (self.solrFacetIndices.length > 1){
					console.log('Multiple ' +  fields[i] + ' for PCM-G selector');
				}else{
					self.solrFacetValue = self.manager.store.findByIndex('fq', self.solrFacetIndices[0]);
					break;
				};
			}
			
			// Persist checkbox value
			if($.isNotBlank(self.solrFacetValue)){
				arrSolrFacetValues = self.solrFacetValue.split(' ');
				
				$(self.target).find(".pcmgselector").prop({
					checked: false
				});
				
				for(var i=0; i< arrSolrFacetValues.length; i++){
					switch(arrSolrFacetValues[i]){
					case self.SOLRFIELD_OPEN + ":true": 
						$(self.target).find("input#open").prop("checked", true);
						break;
					case self.SOLRFIELD_GOVT + ":true": 
						$(self.target).find("input#govt").prop("checked", true);
						break;
					case self.SOLRFIELD_ACAD + ":true": 
						$(self.target).find("input#acad").prop("checked", true);
						break;
					}
				}
			}
			
			self.addListener();
		},

		addFacetValue: function(){
			var self = this;
			var arrSelectorId = ["open", "acad", "govt"];
			var newFacetValue = "";
			
			if ($.isNotBlank(self.solrFacetValue)) self.manager.store.removeByValue("fq", self.solrFacetValue);
			
			var matched = 0;
			for (var i=0; i<arrSelectorId.length; i++){
				if($(self.target).find("input#" + arrSelectorId[i]).is(":checked")){
					if (matched > 0) newFacetValue += ' '; 
					newFacetValue += self.inputToSolrFieldMap[arrSelectorId[i]] + ":true";
					matched++;
				}
			}
			
			if ($.isNotBlank(newFacetValue)) self.manager.store.addByValue("fq", newFacetValue);
			self.manager.doRequest(0);
		},
		
		addListener: function(){
			var self = this;
			
			$(self.target).find(".pcmgselector").prop({
				readonly: false,
				disabled: false
			}).off().on({
				click:function(e){
					self.addFacetValue();
				}
			});
		},
		
		getTemplate: function(){
			var output = "";
			output += '<div class="padT12 padR10 fsize12">';
			output += '	<span class="fbold">Catalog: </span>';
			output += '		<input type="checkbox" id="open" class="pcmgselector"><label for="open" class="label_catalog">Open</label>';
			output += '		<input type="checkbox" id="acad" class="pcmgselector"><label for="acad" class="label_catalog">Academic</label>';
			output += '		<input type="checkbox" id="govt" class="pcmgselector"><label for="govt" class="label_catalog">Government</label>';
			output += '</div>';
			return output;
		}
	});
})(jQuery);