/*
 * Dependencies:
 * - jQuery.js
 * - Velocity.js
 * 
 * Author: FEJT
 * Modified By: PCM-DEV
 */
var PCM = PCM || {};

var defaultSectionList = [
                    	{"disabled" : false, "keywordAttributeType":"CATEGORY", "keywordAttributeItems":null, "priority" : 0},
                    	{"disabled" : false, "keywordAttributeType":"BRAND", "keywordAttributeItems":null, "priority" : 1},
                    	{"disabled" : false, "keywordAttributeType":"SUGGESTION", "keywordAttributeItems":null, "priority" : 2}
                    ];

PCM.typeAhead = (function(j){	
	jQuery.fn.exists = function(){return this.length>0;}
	var init = function(searchInput, typeaheadDiv, delay, typeaheadManager){
		var typeAhead = typeaheadDiv;
		var highlight = typeAhead.find('.highlight');
		var results = null;
		var jWindow = j(window); 
		var currentIndex = null;
		var disableHover = null;
		var hideTimeout = null;
		var KEY_ARROW_UP = 38;
		var KEY_ARROW_DOWN = 40;
		var KEY_ENTER = 13;
		var KEY_ESCAPE = 27;
		var ARROW_EASING = 'easeInOutQuart';
		var ARROW_SPEED = 1;
		var SCROLL_SPEED = 1000;
		var SCROLL_EASING = 'easeInOutQuart';
		var lastAjaxCall = null;
		var typeaheadDelayTimeout = null;
		var highlightedElement = null;
		var noImageUrl = "$webapp/mall/widgetti/images/shared/noImage.gif";
		var typeaheadFocused = false;
		var queryDelay = delay || 10;
		var SCREEN_ADJUST_MULTIPLY = 3;
		var HIDE_TIMEOUT_SPEED = 200;
		var _typeaheadManager = typeaheadManager;
		var latestCall = null;

		var params = {
				'facet': true,
				'debugQuery': true,
				'fl': '*,score',
				'facet.field': facetTemplate,
				'facet.mincount': 1,
				'start': 0,
				'sort': GLOBAL_storeSort,
				'relevancyId': '',
				'spellcheck': true,
				'spellcheck.count': 3,
				'spellcheck.collate': true,
				'gui': GLOBAL_isFromGUI,
				'json.nl': 'map'
		};

		var initEvents = function(){
			var checkFocus = function() {
				if(!typeaheadFocused)
					hideTypeAheadNoDelay();
			};
			searchInput.blur(function(){
				setTimeout(function(){checkFocus();}, 200);
			});
			searchInput.keyup(function(e){
				if(e.which === KEY_ARROW_UP){
					keyArrowUp();
				}else if(e.which === KEY_ARROW_DOWN){
					if(typeAhead.is(':empty')) {
						abortGenerateTypeaheadContents();
						generateTypeaheadContents();
					} else if(results != null) {
						keyArrowDown();
					}
				}else if(e.which === KEY_ENTER){
					keyEnterResults();
				}else if(e.which === KEY_ESCAPE) {
					hideTypeAhead();
				}else {
					abortGenerateTypeaheadContents();
					typeaheadDelayTimeout = setTimeout(function(){generateTypeaheadContents();}, queryDelay);
				}
			});

		};

		var abortGenerateTypeaheadContents = function() {
			if(lastAjaxCall) {
				lastAjaxCall.abort();
				lastAjaxCall = null;
			}
			if(typeaheadDelayTimeout) {
				clearTimeout(typeaheadDelayTimeout);
			}
		};

		var generateTypeaheadContents = function(data) {
			if(searchInput.val().length == 0) {
				hideTypeAhead();
				return;
			}
			highlightedElement = null;
			var savedBatch = dwr.engine._batch; 
			try { 
				savedBatch.req.abort(); 
			} catch (e) { 
			} 

			var timestamp = new Date();
			latestCall = timestamp;
			var callMetaData = { 
					callback:function(data, arg1) {
						var list =  data['data'].list;
						if(arg1 != latestCall) {
							return;
						}
						if(list.length > 0) {
							typeAhead.empty();
							typeAhead.append('<div class="highlight"></div>');
							var sectionList = list[0].sectionList;
							
							if(sectionList == null || sectionList == undefined || sectionList.length == 0) {
								sectionList = defaultSectionList;
							}
							
							sectionList.sort(function(a, b) {
								return a.priority - b.priority;
							});
							
							for(var i=0; i<sectionList.length; i++) {
								var section = sectionList[i];
								
								if(section.disabled) {
									continue;
								}
								
								switch(section.keywordAttributeType) {
								case 'CATEGORY': 
									typeAhead.append(getKeywordContent(list, section));
									break;
								case 'BRAND': 
									typeAhead.append(getSection(section.keywordAttributeType, 'Matching Brands', 'brandFirst'));
									break;
								case 'SUGGESTION': 
									typeAhead.append(getSection(section.keywordAttributeType, ('Suggestions for '+list[0].ruleName), 'suggestionFirst'));
									break;
								case 'SECTION': 
									typeAhead.append(getSection(section.keywordAttributeType, section.inputValue, section.inputValue, section.keywordAttributeId, section.keywordItemValues));
									break;
								}
							}
			
							results = typeAhead.find('li:visible > a');
							results.mouseenter(function(){
								hoverResults(j(this));
							});
							results.click(function() {
								hoverResults(j(this), true);
								searchKeyword();
							});

							bindEvents();

							querySolr(list[0].ruleName, sectionList);

							showTypeAhead();

						} else {
							searchInput.autocomplete("close");
						}



					},
					arg: timestamp
			};

			TypeaheadRuleServiceJS.getAllRules(GLOBAL_storeId, searchInput.val(), 0, 1, 1, GLOBAL_storeMaxTypeahead, true, false, callMetaData);


		};

		var bindEvents = function() {
			results = typeAhead.find('li:visible > a');
			results.mouseenter(function(){
				hoverResults(j(this));
			});
			highlight = typeAhead.find('.highlight');
			typeAhead.focusin(function() { typeaheadFocused = true;} );
			typeAhead.focusout(function() {  typeaheadFocused = false; });
			typeaheadDelayTimeout = null;
			highlight = typeAhead.find('.highlight');
		};

		var querySolr = function(keyword, sectionList) {
			typeaheadManager.store.addByValue('q', $.trim(keyword)); //AjaxSolr.Parameter.escapeValue(value.trim())
			typeaheadManager.store.addByValue('rows', GLOBAL_storeMaxSuggestion);
			typeaheadManager.store.addByValue('storeAlias', GLOBAL_storeId);
			typeaheadManager.store.addByValue('fl', 'Manufacturer,Name,ImagePath_2,DPNo');
			typeaheadManager.store.addByValue('facet', 'true');
			typeaheadManager.store.addByValue('facet.field', 'Manufacturer');
			typeaheadManager.store.addByValue('facet.mincount', 1);
			typeaheadManager.store.addByValue('facet', 'true');
			typeaheadManager.store.addByValue('facet.field', GLOBAL_storeFacetTemplateName); 

			for(var i=0; i<sectionList.length; i++) {
				var section = sectionList[i];
				
				if('CATEGORY' == section.keywordAttributeType) {
					typeaheadManager.elevatedCategoryList = section.keywordItemValues;
				} else if('BRAND' == section.keywordAttributeType) {
					typeaheadManager.elevatedBrandList = section.keywordItemValues;
				}
			}
			
			for(name in params) {
				typeaheadManager.store.addByValue(name, params[name]);
			}
			typeaheadManager.store.addByValue('fl', 'Name,ImagePath_2,EDP, Manufacturer'); 
			typeaheadManager.postHook = function() {bindEvents();};
			typeaheadManager.doRequest(0);
			
		};

		var getKeywordContent = function(list, section) {
			var html = '<div class="clearB"></div>';
			html += '<h3 class="first">Matching Keywords</h3>';
			html += '<ul class="first-lvl">';
			for(var i=0; i < list.length; i++) {
				var ruleName = list[i].ruleName;
				html += '<li>';
				html += '<a href="javascript:void(0);"> <span class="txt">'+ruleName+'</span></a>';

				if(i == 0) {
					html += getSection(section.keywordAttributeType, null, 'categoryFirst');
				}

				html += '</li>';
			}
			html += '</ul>';

			return html;
		};

		var getSection = function(type, headerText, containerId, sectionId, items) {
			var html = '';

			if(headerText && headerText != null) {
				html += '<div class="clearB"></div>';
				html += '<h3>'+headerText+'</h3>';
				
			}

			var elementId = containerId ? 'id="'+containerId+'"' : '';

			html += '<ul class="second-lvl" '+elementId+'>';
			if(items && (type == 'SECTION')) {
				for(var i=0; i<items.length; i++) {
					
					html += '<li id="'+sectionId+'_'+items[i]+'">';
					html += '</li>';
				}
				
			} else {
				html += '<li><img class="floatL padL30" marB5 alt="Retrieving" src="'+ GLOBAL_contextPath +'/images/ajax-loader-rect.gif"/><div class="clearB"></div></li>';
			}
			html +='</ul>';

			TypeaheadRuleServiceJS.getProducts(GLOBAL_storeId, items, {
				callback: function(productsMap){
					if(productsMap && Object.keys(productsMap).length > 0) {
						
						for(edp in productsMap) {
							
							var product = productsMap[edp];
							
							if(!product || !$.isNotBlank(product.edp)) {
								continue;
							}
							
							$('li#'+sectionId+'_'+product.dpNo).after(getProductHtml(product));
							$('li#'+sectionId+'_'+product.dpNo).remove();
							
						}
					}
				},
				postHook: function() {
					results = typeAhead.find('li:visible > a');
					results.mouseenter(function(){
						hoverResults(j(this));
					});
					results.click(function() {
						hoverResults(j(this), true);
						searchKeyword();
					});
				}
			});
			
			return html;
		};
		
		var getProductHtml = function(product) {

			var html = '';
			
			html += '<li class="itemImgWp floatL">';
			html += '<a href="javascript:void(0);" class="keywordListener suggest">';
			html += '<span id="dpno" style="display:none" class="txt">'+product.edp+'</span>';
			
			html += '		<img class="itemImg floatL" width="60" src="'+product.imagePath+'"/>&nbsp;';
			html += '		<span class="itemNameSuggest offer">'+product.manufacturer+'</span>';
			html += '		<span class="itemNameSuggest prod-title">'+product.name+'</span>';
			
			html += '</a>';
			html += '<div class="clearB padB10"></div>';
			html += '<div class="clearB padB10"></div>';
			
			html += '</li>';
			
			return html;
		};

		var setupStyle = function(){
			typeAhead.css({
				'width':(searchInput.outerWidth()*2.5)+'px',
				'top':(searchInput.offset().top + 23)+'px'
			});
			/*
			 * Temporary implementation in old PCM search
			 * The old PCM search have very poor HTML and CSS structure
			 * Cleaner implementation is to adjust the HTML and CSS of PCM search
			 * Remove the below code in new PCM search
			 */
			//searchInput = searchInput.find('input');
		};

		var showTypeAhead = function(){
			typeAhead.show();
		};

		var hideTypeAhead = function(){
			//clearTypeaheadData();
			clearInterval(hideTimeout);
			hideTimeout = setTimeout(function(){
				typeAhead.hide();
			}, HIDE_TIMEOUT_SPEED);
		};

		var hideTypeAheadNoDelay = function(){
			//clearTypeaheadData();
			typeAhead.hide();
		};

		var animateHighlight = function(base, updateTextbox){
			highlight.stop().show();
			results.find('.prod-title').css({'color':'#000'});
			if(typeof base.parent().parent().attr('class') !== 'undefined' && 
					base.hasClass('suggest') == true){
				highlight.hide();
				base.find('.prod-title').css({'color':'#08c'});
			}else{
				highlight.velocity({
					'height':base.outerHeight()+'px',
					'width':'100%',
					'top':base.position().top+'px'
				}, ARROW_SPEED, ARROW_EASING);
			}
			if(updateTextbox)
				searchInput.val(base.find('.txt').text());
			highlightedElement = base;
		};

		var adjustScreenView = function(base){
			if(typeAhead.outerHeight() > jWindow.outerHeight()){
				results.off('mouseenter');
				base.velocity('scroll', {duration: SCROLL_SPEED, easing: SCROLL_EASING, offset: -base.outerHeight()});
				clearTimeout(disableHover);
				disableHover = setTimeout(function(){
					results.mouseenter(function(){
						hoverResults(j(this));
					});
				}, SCROLL_SPEED);
			}

		};

		var keyArrowUp = function(){
			if(currentIndex !== null){
				currentIndex--;
				if(currentIndex < 0){
					currentIndex = results.length - 1;
				}
			}else{
				currentIndex = results.length - 1;
			}
			animateHighlight(results.eq(currentIndex));
		};

		var keyArrowDown = function(){
			if(currentIndex !== null){
				currentIndex++;
				if(currentIndex >= results.length){
					currentIndex = 0;
				}
			}else{
				currentIndex = 0;
			}
			animateHighlight(results.eq(currentIndex));
		};

		var keyEnterResults = function(){
			if(highlightedElement) {
				highlightedElement.trigger('click');
			} else {
				$('#searchKeyword').find('a#searchBtn').click();
			}
		};
		
		var searchKeyword = function(){
			clearFilters(_typeaheadManager.searchManager);
			hideTypeAhead();
			searchInput.focus();
			$('#searchKeyword').find('a#searchBtn').click();
		};

		var clearFilters = function(manager) {
			manager.store.remove('fq');
			manager.store.remove('disableElevate');
			manager.store.remove('disableExclude');
			manager.store.remove('disableDemote');
			manager.store.remove('disableFacetSort');
			manager.store.remove('disableRedirect');
			manager.store.remove('disableRelevancy');
			manager.store.remove('disableDidYouMean');
			manager.store.remove('disableBanner');
			manager.widgets[WIDGET_ID_searchWithin].clear();
			
		};
		
		var hoverResults = function(base, updateTextbox){
			animateHighlight(base, updateTextbox);
			for(var i = 0; i < results.length; i++){
				if(base.text() === results.eq(i).text()){
					currentIndex = i;
					break;
				}
			}
		};

		setupStyle();
		initEvents();

		return{
			showTypeAhead:showTypeAhead,
			hideTypeAhead:hideTypeAhead,
			keyArrowUp:keyArrowUp,
			keyArrowDown:keyArrowDown
		};
	};

	return{
		init:init
	};
})(jQuery);