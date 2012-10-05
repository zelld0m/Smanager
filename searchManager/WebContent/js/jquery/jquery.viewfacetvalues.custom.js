(function($){
	$.viewfacetvalues = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("viewfacetvalues", base);
		
		// Added search functionality to list
		base.searchableList = function(){
			var content = base.$el.find(".facetValueList");
			addRecordCount = function(selector, target){ 
				var count = $(selector).length;  
				if (count==0){
					$(target).html("No Records Found!");
				}
				else{	
					$(target).html(count);
					$(target).append(' ');
					$(target).append(count == 1 ? "Record" : "Records");
				}
			};
			
			content.find('input#searchField').val('');
			addRecordCount(content.find('li#facetValue'), content.find('.searchCount'));

			/*content.find('#facetValue tbody tr').hover(function(){  
				$(this).find('td').addClass('hovered');  
			}, function(){  
				$(this).find('td').removeClass('hovered');  
			});*/ 

			content.find('#facetValue').addClass('visible');  

			content.find('input#searchField').keyup(
					function(event) {  
				//if esc is pressed or nothing is entered  

				if (event.keyCode == 27 || $(this).val() == '') {  
					//if esc is pressed we want to clear the value of search box  
					$(this).val('');  

					//we want each row to be visible because if nothing  
					//is entered then all rows are matched.  
					content.find('li#facetValue').removeClass('visible').show().addClass('visible');  
				}  

				//if there is text, lets filter  
				else {  
					query = $.trim($(this).val()); //trim white space  
					query = query.replace(new RegExp("[.*+?|()\\[\\]{}\\\\]", "g"), "\\$&");
					query = query.replace(/ /gi, '|'); //add OR for regex query  

					content.find('li#facetValue').each(function() {  
						($(this).text().search(new RegExp(query, "i")) < 0) ? $(this).hide().removeClass('visible') : $(this).show().addClass('visible');  
					}); 
				}  

				addRecordCount(content.find('li#facetValue.visible'), content.find('.searchCount'));
			});
		};

		base.init = function(){
			base.options = $.extend({},$.viewfacetvalues.defaultOptions, options);
			base.populateTemplate();
			base.getFacetValueList();
		};

		base.getFacetValueList = function () {
			var self = this;

			var getFacetSelected = function() {
				var i = 0;
				var selectedItems = [];

				$('.firerift-style').each(function() {
					if ($(this).hasClass("on")){
						var sel = $.trim($('#' + $(this).attr('rel')).val());
						if ($.isNotBlank(sel)){
							i++;
							selectedItems.push(self.escapeValue(sel));
						}
					}
				});

				if (selectedItems.length == 0) {
					return "";
				}
				return "(" + selectedItems.join(" ") + ")";
			};

			var	getFacetParams = function (keyword){
				var paramString = "";

				var params = {
						'facet': true,
						'q': base.options.keyword,
						'facet.field': base.options.facetField,
						'rows': 0,
						'fq' : base.options.fq,
						'facet.mincount': 1,
						'facet.limit': -1,
						'facet.sort':'HEX',
						'gui': true,
						'json.nl':'map'
				};

				for (var name in params) {
					if ($.isArray(params[name])){
						for (var param in params[name]){
							paramString += "&" + name + "=" + params[name][param];
						}
					}else{
						if(name.toLowerCase() !== "sort".toLowerCase())
							paramString += "&" + name + "=" + params[name];
					}
				}

				return paramString;
			};

			var handleResponse = function (data){
				var facetFields = data.facet_counts.facet_fields;
				var $ul = base.$el.find("ul#facetValues");
				var selectedList = base.options.selectedList;

				if(facetFields){
					var facetValues = facetFields[base.options.facetField];

					for (var facetValue in facetValues) {
						if($.isBlank(facetValue))
							continue;
						
						var $li = $ul.find("li#facetValuePattern").clone();
						var count = parseInt(facetValues[facetValue]);
						
						if(selectedList && ($.inArray(facetValue, selectedList) < 0)){
						$li.prop({id: 'facetValue'});
						$li.show();

						$li.find("span#facetName").text(facetValue);
						$li.find("span#facetCount").text('(' + count + ')');
						
						//TODO is facet selected
						//$li.find("span#selectedIcon").show();
						//$li.find("span#selectedIcon").text(base.options.selectedIconText);
						 }
						
						$ul.append($li);
					}
				}
				
				base.searchableList();
			};

			$.getJSON(
					GLOBAL_solrUrl + GLOBAL_store + '/select' + '?' + getFacetParams() + '&wt=json&json.wrf=?', 
					function (json, textStatus) { 
						if (textStatus!=="success"){
							api.destroy();
						}
						
						handleResponse(json); 
						base.options.afterSolrRequestCallback(json);
					}
			);
		};

		base.populateTemplate = function(){
			var content = '<div class="facetValueList floatL w46p marL20 borderL padL15">';
			content+= '<p class="fbold">';
			content+= base.options.headerText;
			content+= '</p>';
			content+= '<div class="clearB"></div>';

			if (base.options.showSearch){
				content+= '<div>';
				content+= '<div class="searchBoxHolder w120 marT10 marR8 floatL">';
				content+= 	'<input type="text" class="farial fsize12 fgray pad3 w100" id="searchField" name="searchField">';				
				content+= '</div>';
				content+= 	'<div class="floatL fsize11 searchCount marT10 w80 padT8 fLgray"></div>';
				content+= '</div>';
			}

			content+= '<div class="clearB"></div>';
			content+= '<div style="overflow-y:auto; height: 190px" class="marT10">';
			content+= 	'<ul id="facetValues" class="facetList">';
			content+= 		'<li id="facetValuePattern" style="display: none">';
			content+= 			'<span id="facetName"></span>';
			content+= 			'<span id="facetCount" class="fLblue"></span>';
			content+= 			'<span id="selectedIcon" class="fsize10 fLgray" style="display: none"></span>';
			content+=		'</li>';
			content+= 	'</ul>';
			content+= '</div>';
			content+= '</div>';

			base.$el.append(content);
		};

		base.populateList = function(data){
			var list = data.list;

			// Delete all the rows except for the "pattern" row
			base.$el.find("tbody#itemListing").children().not("#itemPattern").remove();

			// populate list
			//dwr.engine.beginBatch(); 
			for (var i = 0; i < data.list.length; i++) {
				var id = list[i][base.options.fieldId]==undefined? i+1 : list[i][base.options.fieldId];
				var name = list[i][base.options.fieldName];
				var suffixId = $.formatAsId(id);

				base.$el.find("tr#itemPattern").clone().appendTo("tbody#itemListing").attr("id","itemPattern" + suffixId);
				suffixId = $.escapeQuotes(suffixId);

				base.$el.find('#itemPattern' + suffixId + ' div.itemText a').html(name);
				base.$el.find('#itemPattern' + suffixId + ' div.itemText a').on({click:base.options.itemNameCallback},{name:name, id:id, model:list[i]});
				base.$el.find('#itemPattern' + suffixId).show();

				base.options.itemOptionCallback(base, id, name, list[i]);

			}
			//dwr.engine.endBatch();
		};

		// Run initializer
		base.init();
	};

	$.viewfacetvalues.defaultOptions = {
			headerText: "",
			showSearch: true,
			keyword: "",
			fq: "",
			facetField: "Category",
			displayType: "Scrollable",
			sortOrder: "asc",
			selectedIconText : "elevated",
			selectedList : [""],
			afterSolrRequestCallback: function(json){}
	};

	$.fn.viewfacetvalues = function(options){

		if (this.length) {
			return this.each(function() {
				$(this).empty();
				(new $.viewfacetvalues(this, options));
			});
		};
	};
})(jQuery);
