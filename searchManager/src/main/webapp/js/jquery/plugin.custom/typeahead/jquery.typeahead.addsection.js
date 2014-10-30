(function($){
		
	$.typeaheadaddsection = function(el, options) {
		var base = this;
		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("typeaheadaddsection", base);
		
		base.options = $.extend({},$.typeaheadaddsection.defaultOptions, options);
		
		var editable = (base.options.editable == true);
		
		base.init = function() {
			var self = this;
			self.initializeAddEvent();
			self.initializeExistingData();
			if(editable) {
				self.$el.find('div#addSectionForm').show();
			} else {
				self.$el.find('div#addSectionForm').hide();
			}
		};
		
		base.initializeExistingData = function() {
			var self = this;
			
			var dummySection = new Array();
			dummySection[0] = {"name":"Hot Deals", "sectionItems":[454909, 13222995, 9111111, 7268082, 8037454, 9232266, 944015]};
						
			var sectionList = dummySection;
			
			for(var i=0; i < sectionList.length; i++) {
				var section = sectionList[i];
				var $newSection = $(self.getSectionTableTemplate());
				
				$newSection = self.addSection(self.$el.find('table#section').find('div#sectionBox'), $newSection, section.name);
				
				self.addProductToSection($newSection, section.sectionItems);
				
			}
		};
				
		base.initializeAddEvent = function() {
			var self = this;
			var $sectionTable = self.$el.find('table#section');
			
			if(!editable) {
				return;
			}
			
			$sectionTable.find('a#btnAddSection').off().on('click', function() {
				var $sectionInput = $sectionTable.find('input');
				var inputValue = $sectionInput.val().trim();
				var $sectionBox = $sectionTable.find('div#sectionBox');
				if(!$.isNotBlank(inputValue)) {
					jAlert("Section Name cannot be empty.", base.options.moduleName);
					return;
				}
				
				if($sectionBox.find('div:contains('+inputValue+')').size() > 0) {
					jAlert("Section Name already taken.", base.options.moduleName);
					return;
				}
				
				var $newSection = $(self.getSectionTableTemplate());
				self.addSection($sectionBox, $newSection, inputValue);
				$sectionInput.val('');
			});
						
		};
		
		base.addSection = function($sectionBox, $newSection, inputValue) {
			var self = this;
			
			$newSection.find('div.sectionName').html(inputValue);
			$sectionBox.append($newSection);
			
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
			
			$section.find('a.addProduct').off().on({click : function() {
				if($section.runningDWR == true) {
					return;
				}
				
				var dpNum = $section.find('input:text').val().trim();
				
				if(!$.isNotBlank(dpNum)) {
					jAlert('DP Number cannot be empty.', base.options.moduleName);
					return;
				}
				
				if($section.find('div#'+dpNum).length > 0) {
					jAlert('Product already in the list.', base.options.moduleName);
					return;
				}
							
				self.addProductToSection($section, [dpNum]);
				
			}}, {$section: $section});
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
						for(edp in productsMap) {
							
							var product = productsMap[edp];
							
							if(!$.isNotBlank(product.edp)) {
								jAlert('Not a valid product number.', base.options.moduleName);
								continue;
							}
							
							var $product = self.getSectionItemProduct(product);
							$productList.append($product);
							$productList.sortable('refresh');
							self.initializeItemProductEvents($product);
						}
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
			$newItem.append('<p align="center">'+product.dpNo+'<p>');
			
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
			html +=	'								<div class="floatL marT3"><input type="checkbox"/></div>';
			html +=	'								<div class="floatR preloader padT9 padB10" style="display:none;">'+base.options.rectLoader+'</div>';
			if(editable) {
				html +=	'								<div class="floatR sectionIcons">';
				html +=	'									<input type="text" class="w150 marB6"/>';
				html +=	'									<a href="javascript:void(0);" class="addProduct"><img class="padT5" src="'+GLOBAL_contextPath+'/images/add.png"/></a>';
				html +=	'									<a href="javascript:void(0);" class="deleteSection"><img class="padL2 marT6" src="'+GLOBAL_contextPath+'/images/icon_delete2.png"/></a>';
				html +=	'								</div>';
			}
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
			rectLoader: "<img class='itemIcon' src='"+ GLOBAL_contextPath +"/images/ajax-loader-rect.gif'/>"
	};
	
	$.fn.typeaheadaddsection = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.typeaheadaddsection(this, options));
			});
		};
	};
})(jQuery);