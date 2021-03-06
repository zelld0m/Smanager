(function($){
		
	$.typeaheadaddsection = function(el, options) {
		var base = this;
		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("typeaheadaddsection", base);
		
		base.options = $.extend({},$.typeaheadaddsection.defaultOptions, options);
		
		var editable = (base.options.editable == true);
		var isAccordion = (base.options.accordion == true);
		
		base.init = function() {
			var self = this;
			self.$el.find('table#section');
			self.initializeAddEvent();
			self.initializeExistingData();
			if(editable) {
				self.$el.find('div#addSectionForm').show();
			} else {
				self.$el.find('div#addSectionForm').hide();
			}
			
			if(isAccordion) {
				self.initializeAccordion();
			}
		};
		
		base.initializeExistingData = function() {
			var self = this;
			
			var sectionList = base.options.sectionList;
			
			for(var i=0; sectionList && i < sectionList.length; i++) {
				var section = sectionList[i];
				var $newSection = $(self.getSectionTableTemplate());
				
				$newSection = self.addSection(self.$el.find('table#section').find('div#sectionBox'), $newSection, section.name, section.disabled);
				
				self.addProductToSection($newSection, section.sectionItems);
				
			}
		};
		
		base.initializeAccordion = function() {
			var self = this;
			var $el = self.$el;
			
			$el.find('div.sectionName').each(function() {
				var $sectionNameDiv = $(this);
				var $tableContainer = $sectionNameDiv.closest("table#sectionTemplate");
				
				$tableContainer.before("<a href='javascript:void(0)' style='text-decoration:none;'><div class=\"pad5\" style=\"width:590px; border:1px solid #cfcfcf; color:#333333; background: none repeat scroll 0 0 #dfdfdf;\">"+$sectionNameDiv.html()+"</div></a>");
				$tableContainer.wrap('<div class="content" style="width:600px; border:1px solid #cfcfcf;"></div>');
				$sectionNameDiv.parent().parent().remove();
			});
			
			$el.find('div#sectionBox').find('a').click(function(e) {
			    //Close all <div> but the <div> right after the clicked <a>
			    $(e.target).parent().next('div.content').siblings('div.content').slideUp({duration:200});
			    //Toggle open/close on the <div> after the <a>, opening it if not open.
			    $(e.target).parent().next('div.content').slideDown({duration:200});
			});
			
			$el.find('div#sectionBox').find('div.content:gt(0)').hide();
		};
				
		base.initializeAddEvent = function() {
			var self = this;
			var $sectionTable = self.$el.find('table#section');
			
			if(!editable) {
				return;
			}
			
			$sectionTable.find('a#btnAddSection').off().on('click', function() {
				var $sectionInput = $sectionTable.find('input[type=text]');
				var inputValue = $sectionInput.val().trim();
				var $sectionBox = $sectionTable.find('div#sectionBox');
					            
				if(!$.isNotBlank(inputValue)) {
					jAlert("Section Name cannot be empty.", base.options.moduleName);
					return;
				}
				
				if(inputValue.match(/^[a-z\d\-_\s]+$/i) == null) {
					jAlert("Only alphanumeric characters, space, dash and underscore are allowed.", base.options.moduleName);
					return;
				}
				
				if(inputValue.length > 50) {
					jAlert("Section Name should only contain 50 characters or less.", base.options.moduleName);
					return;
				}
				
				var hasError = false;
				
				$sectionBox.find('div.sectionName').each(function() {
					var text = $(this).text();
					if(inputValue.toUpperCase() === text.toUpperCase()) {
						hasError = true;
					}
				});
				
				for(var i=0; i < base.options.defaultSections.length; i++) {
					var text =  base.options.defaultSections[i];
					if(inputValue.toUpperCase() === text.toUpperCase()) {
						hasError = true;
					}
				}
				
				if(hasError) {
					jAlert("Section Name already taken.", base.options.moduleName);
					return;
				}
				
				var $newSection = $(self.getSectionTableTemplate());
				self.addSection($sectionBox, $newSection, inputValue);
				$sectionInput.val('');
			});			
		};
		
		base.addSection = function($sectionBox, $newSection, inputValue, slideOn) {
			var self = this;
			
			$newSection.find('div.sectionName').html(inputValue);
			$sectionBox.append($newSection);
			
			$newSection.find('.disabled-flag').slidecheckbox({
				initOn: slideOn != true,
				disabled: !editable, //TODO:
				changeStatusCallback: function(base, dt){
					
				}
			});
			
			if(!editable) {
				return $newSection;
			}
			
			$newSection.find('div.productList').sortable({tolerance: "intersect",
		        axis: "x",
		        opacity: 0.5,
		        containment: 'div.productList',
		        handle:'.drag',
		        scroll: true,
		        sort: function (event, ui) {
		            var that = $(this),
		                w = ui.helper.outerWidth();
		            that.children().each(function () {
		                if ($(this).hasClass('ui-sortable-helper') || $(this).hasClass('ui-sortable-placeholder')) 
		                    return true;
		                // If overlap is more than half of the dragged item
		                var dist = Math.abs(ui.position.left - $(this).position().left),
		                    before = ui.position.left > $(this).position().left;
		                if ((w - dist) > (w / 2) && (dist < w)) {
		                    if (before)
		                        $('.ui-sortable-placeholder', that).insertBefore($(this));
		                    else
		                        $('.ui-sortable-placeholder', that).insertAfter($(this));
		                    return false;
		                }
		            });
		        }});
			
			self.initializeSectionItemEvents($newSection);
			
			return $newSection;
		};		
		
		base.initializeSectionItemEvents = function($section) {
						
			var self = this;
			
			$section.find('a.deleteSection').off().on({click : function() {
				jConfirm('Are you sure you want to delete this section?', base.options.moduleName, function(result){
					if(result) {
						self.deleteSection($section);
					}
				});
				
			}}, {$section: $section});
			
			$section.find('input[type=text]').off().on({keyup : function(e) {
				if(e.which === 13){
					$(this).siblings('a.addProduct').click();
				}
			}});
			
			$section.find('a.addProduct').off().on({click : function() {
				if($section.runningDWR == true) {
					return;
				}
				
				var dpNum = $section.find('input:text').val().trim();
				
				if(!$.isNotBlank(dpNum)) {
					jAlert('DP Number cannot be empty.', base.options.moduleName);
					return;
				}
				
				var dpNumList = dpNum.split(" ");
				
				if(self.hasDuplicate(dpNumList)) {
					jAlert('Input has duplicate DP numbers.', base.options.moduleName);
					return;
				}
				
				var existingItemList = new Array();
				for(var i=0; i<dpNumList.length; i++) {
					var dpItem = dpNumList[i].trim();
					
					if($section.find('div#'+dpItem).length > 0) {
						existingItemList[existingItemList.length] = dpItem;
					}
				}
				
				if(existingItemList.length > 0) {
					jAlert(existingItemList + ' already in the list.', base.options.moduleName);
					return;
				}
				self.addProductToSection($section, dpNum.split(" "));
				
			}}, {$section: $section});
		};
		
		base.hasDuplicate = function(array) {
			var obj = new Object();
			
			for(var i=0; i<array.length; i++) {
				var dpItem = array[i].trim();
				
				if(obj[dpItem]) {
					return true;
				}
				
				obj[dpItem] = true;
			}
			return false;
		};
		
		base.deleteSection = function($section) {
			$section.find('div.productList').each(function() {
				$(this).sortable('destroy');
			});
			$section.remove();
		};
		
		base.addProductToSection = function($section, dpNumList) {
			var self = this;
			
			TypeaheadRuleServiceJS.getProducts(GLOBAL_storeId, dpNumList, {
				callback: function(productsMap){
					if(productsMap && Object.keys(productsMap).length > 0) {
						var $productList = $section.find('div.productList');
						var position = 0;
						for(edp in productsMap) {
							
							var product = productsMap[edp];
							
							if(!$.isNotBlank(product.edp)) {
								jAlert('"'+dpNumList[position]+'" Not a valid product number.', base.options.moduleName);
								continue;
							}
							
							var $product = self.getSectionItemProduct(product);
							$productList.append($product);
							$productList.sortable('refresh');
							self.initializeItemProductEvents($product);
							position ++;
						}
						
						$section.find('input[type=text]').val('');
					}
				},
				preHook: function() {
					$section.runningDWR = true;
					$section.find('div.preloader').show();
					$section.find('div.sectionIcons').hide();
				},
				postHook: function() {
					$section.runningDWR = false;
					$section.find('div.preloader').hide();
					$section.find('div.sectionIcons').show();
				}
			});
		};
		
		base.getSectionItemProduct = function(product) {
			var $newItem = $('<div class="w85 pad5 marR10" id="'+product.dpNo+'" style="display:inline-block; border:1px solid #cfcfcf;;"></div>');
			var $iconContainer = $('<div class="floatR"></div>');
			if(editable) {
				$iconContainer.append('<a href="javascript:void(0);" class="drag padR5">'+base.options.dragIcon+'</a>');
				$iconContainer.append('<a href="javascript:void(0);" class="delete padR5">'+base.options.deleteIcon+'</a>');
			}
			$newItem.append($iconContainer);
			$newItem.append('<div class="clearB"></div>');
			$newItem.append('<img width="64" style="margin-left:10px" src="'+product.imagePath+'"/>');
			$newItem.append('<div class="clearB"></div>');
			$newItem.append('<p align="center" class="sectionItemValue">'+product.dpNo+'<p>');
			
			return $newItem;
		};
		
		base.initializeItemProductEvents = function($sectionItem) {
			
			$sectionItem.find('a.delete').off().on({
				click: function() {
					jConfirm('Are you sure you want to delete this product?', base.options.moduleName, function(result){
						if(result) {
							var $listContainer = $sectionItem.parent().parent().parent();
							$sectionItem.remove();
							$listContainer.sortable('refresh');
						}
					});
				}
			});
		};
		
		base.getSectionTableTemplate = function() {
			
			if(base.options.sectionTableTemplate) {
				return base.options.sectionTableTemplate;
			}
				
			var html = '';
			
			html +=	'					<table id="sectionTemplate" class="tblItems marL8 marT15 marB10 sectionTable">';
			html +=	'						<tr>';
			html +=	'							<td class="pad1" valign="bottom">';
			html +=	'								<div class="floatL marT5 sectionName">Dynamic Section</div>';
			html +=	'								<div class="floatR preloader padT9 padB10" style="display:none;">'+base.options.rectLoader+'</div>';
			html +=	'								<div class="floatR sectionIcons">';
			html +=	'								<div class="floatL marT3 marR5"><input type="checkbox" class="firerift-style-checkbox on-off disabled-flag"/></div>';
			if(editable) {
				html +=	'									<input type="text" class="w150 marB6"/>';
				html +=	'									<a href="javascript:void(0);" class="addProduct"><img class="padT5" src="'+GLOBAL_contextPath+'/images/add.png"/></a>';
				html +=	'									<a href="javascript:void(0);" class="deleteSection"><img class="padL2 marT6" src="'+GLOBAL_contextPath+'/images/icon_delete2.png"/></a>';
			}
			html +=	'								</div>';
			html +=	'							</td>';
			html +=	'						</tr>';
			html +=	'						<tr>';
			html +=	'							<td class="w650" nowrap>';
			html +=	'								<div class="productList w650" style="overflow-x:auto; overflow-y:hidden; white-space:nowrap; vertical-align:top"></div>';
			html +=	'							</td>';
			html +=	'						</tr>';
			html +=	'					</table>';
			
			return html;
		};
		
		base.init();
	};
	
	$.typeaheadaddsection.defaultOptions = {
			elevateIcon:"<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/page_white_get.png'/>",
			deleteIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/btn_delete_big.png'/>",
			dragIcon: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/icon_drag.png'/>",
			rectLoader: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-rect.gif'/>",
			defaultSections: ["Category", "Brand", "Suggestion"],
			sectionList : []
			
	};
	
	$.fn.typeaheadaddsection = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.typeaheadaddsection(this, options));
			});
		};
	};
})(jQuery);