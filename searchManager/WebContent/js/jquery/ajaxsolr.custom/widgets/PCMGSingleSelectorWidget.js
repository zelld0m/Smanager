(function ($) {

	AjaxSolr.PCMGSingleSelectorWidget = AjaxSolr.AbstractWidget.extend({
		SOLRFIELD_OPEN: "PCMallGov_OpenStoreFlag",
		SOLRFIELD_GOVT: "PCMallGov_GovStoreFlag",
		SOLRFIELD_ACAD: "PCMallGov_ACAStoreFlag",
		inputToSolrFieldMap: [],
		solrFacetIndices : -1, 
		solrFacetValue : "",
		multipleDetected: false,

		init: function(){
			var self = this;

			self.fields = [self.SOLRFIELD_OPEN, self.SOLRFIELD_ACAD, self.SOLRFIELD_GOVT];
			self.inputToSolrFieldMap["open"] = self.SOLRFIELD_OPEN;
			self.inputToSolrFieldMap["govt"] = self.SOLRFIELD_GOVT;
			self.inputToSolrFieldMap["acad"] = self.SOLRFIELD_ACAD;
		},

		beforeRequest: function () {
			var self = this;
			var hasSelection = false;
			var countSelection = 0;

			self.solrFacetIndices = -1, 
			self.solrFacetValue = "",
			self.multipleDetected = false,

			$(self.target).find(".pcmgselector").off().prop({
				readonly: true,
				disabled: true
			});

			//Check if request has selected catalog
			for(var i=0; i< self.fields.length; i++){
				var indices = self.manager.store.find('fq', self.fields[i] + ':true');
				hasSelection = hasSelection ||  indices.length > -1;
				if(hasSelection && indices.length > 0){
					countSelection++;
					self.solrFacetValue = self.manager.store.findByIndex('fq', indices[0]);
				}
			}

			self.multipleDetected = countSelection > 1;

			// Set default selected value
			if(!hasSelection ){
				self.solrFacetValue = "PCMallGov_OpenStoreFlag:true";
				self.manager.store.addByValue("fq", "PCMallGov_OpenStoreFlag:true");
			}
			
			switch(self.solrFacetValue){
			case self.SOLRFIELD_OPEN + ":true": 
				GLOBAL_PCMGCatalog = "Open";
				break;
			case self.SOLRFIELD_GOVT + ":true": 
				GLOBAL_PCMGCatalog = "Government";
				break;
			case self.SOLRFIELD_ACAD + ":true": 
				GLOBAL_PCMGCatalog = "Academic";
				break;
			}
			
		},

		afterRequest: function () {
			var self = this;

			if(self.multipleDetected){
				$(self.target).html("Multiple Catalog Selected");
				return;
			}

			$(self.target).html(self.getTemplate())
			.find(".pcmgselector").prop({
				checked: false
			});

			switch(self.solrFacetValue){
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

			// Add radio listener
			$(self.target).find(".pcmgselector").prop({
				readonly: false,
				disabled: false
			}).off().on({
				click:function(e){

					var arrSelectorId = ["open", "acad", "govt"];
					var newFacetValue = "";

					if ($.isNotBlank(e.data.currValue)){
						e.data.self.manager.store.removeByValue("fq", e.data.currValue);
					}

					for (var i=0; i<arrSelectorId.length; i++){
						if($(e.data.self.target).find("input#" + arrSelectorId[i]).is(":checked")){
							newFacetValue = e.data.self.inputToSolrFieldMap[arrSelectorId[i]] + ":true";
							break;
						}
					}

					if ($.isNotBlank(newFacetValue)){
						e.data.self.manager.store.addByValue("fq", newFacetValue);
						e.data.self.manager.doRequest(0);
					}
				}
			},{currValue: self.solrFacetValue, self: self});
		},

		getTemplate: function(){
			var output = "";
			output += '<div class="padT12 padR10 fsize12">';
			output += '	<span class="fbold">Catalog: </span>';
			output += '		<input type="radio" id="open" name="selector" class="pcmgselector"><label for="open" class="label_catalog">Open</label>';
			output += '		<input type="radio" id="acad" name="selector" class="pcmgselector"><label for="acad" class="label_catalog">Academic</label>';
			output += '		<input type="radio" id="govt" name="selector" class="pcmgselector"><label for="govt" class="label_catalog">Government</label>';
			output += '</div>';
			return output;
		}
	});
})(jQuery);