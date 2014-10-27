(function($){
	
	$.typeaheadaddsection = function(el, options) {
		var base = this;
		
		base.$el = $(el);
		base.el = el;
		
		// Add a reverse reference to the DOM object
		base.$el.data("typeaheadaddsection", base);
		
		base.options = $.extend({},$.typeaheadsortable.defaultOptions, options);
		
		base.init = function() {
			var self = this;
			self.initializeAddEvent();
		};
		
		base.initializeAddEvent = function() {
			var self = this;
			var $sectionTable = self.$el.find('table#section');
			
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
				$newSection.find('div.sectionName').html(inputValue);
				$sectionBox.append($newSection);
				
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
				$sectionInput.val('');
			});
						
		};
		
		
		base.initializeSectionItemEvents = function($sectionItem) {
						
			var self = this;
			
			$sectionItem.find('a.deleteSection').off().on({click : function() {
				jConfirm('Are you sure you want to delete this section?', base.options.moduleName, function(result){
					if(result) {
						$sectionItem.remove();
					}
				});
				
			}}, {$sectionItem: $sectionItem});
			
			$sectionItem.find('a.addProduct').off().on({click : function() {
				if($sectionItem.runningDWR == true) {
					return;
				}
				
				var dpNum = $sectionItem.find('input:text').val().trim();
				
				if(!$.isNotBlank(dpNum)) {
					jAlert('DP Number cannot be empty.', base.options.moduleName);
					return;
				}
				
				if($sectionItem.find('div#'+dpNum).length > 0) {
					jAlert('Product already in the list.', base.options.moduleName);
					return;
				}
								
				TypeaheadRuleServiceJS.getProducts(GLOBAL_storeId, [dpNum], {callback: function(productsMap){
						if(productsMap && Object.keys(productsMap).length > 0) {
							var $productList = $sectionItem.find('div.productList');
							for(edp in productsMap) {
								
								var product = productsMap[edp];
								$productList.append(self.getSectionItemProduct(product));
								$productList.sortable('refresh');
							}
						}
					},
					preHook: function() {
						$sectionItem.runningDWR = true;
					},
					postHook: function() {
						$sectionItem.runningDWR = false;
					}
				});
			}}, {$sectionItem: $sectionItem});
		};
		
		base.getSectionItemProduct = function(product) {
			var $newItem = $('<div class="w80 marR10" id="'+product.dpNo+'" style="display:inline-block;"></div>');
			var $iconContainer = $('<div class="floatR"></div>');
			
			$iconContainer.append('<a href="javascript:void(0);" class="drag padR5">'+base.options.dragIcon+'</a>');
			$iconContainer.append('<a href="javascript:void(0);" class="delete padR5">'+base.options.deleteIcon+'</a>');
			$newItem.append($iconContainer);
			$newItem.append('<div class="clearB"></div>');
			$newItem.append('<img width="64" src="'+product.imagePath+'"/>');
			
			return $newItem;
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
			html +=	'								<div class="floatR">';
			html +=	'									<input type="text" class="w150 marB6"/>';
			html +=	'									<a href="javascript:void(0);" class="addProduct"><img class="padT5" src="'+GLOBAL_contextPath+'/images/add.png"/></a>';
			html +=	'									<a href="javascript:void(0);" class="deleteSection"><img class="padL2 marT6" src="'+GLOBAL_contextPath+'/images/icon_delete2.png"/></a>';
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
	};
	
	$.fn.typeaheadaddsection = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.typeaheadaddsection(this, options));
			});
		};
	};
})(jQuery);