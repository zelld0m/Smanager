(function($){

	$.selectitem = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;
		
		base.searchText = "";
		
		// Access to jQuery and DOM versions of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("selectitem", base);

		base.init = function(){
			base.options = $.extend({},$.selectitem.defaultOptions, options);

			base.$el.on({
				click: function(e){
					base.showQtipPreview();
				}
			});

		};

		base.prepareForceAddStatus = function(contentHolder){
			contentHolder.find('div#forceAdd').show();
		};

		base.updateForceAddStatus = function(contentHolder, data, memberIdToItemMap){
			for(var mapKey in data){
				var $tr = contentHolder.find('tr#item' + $.formatAsId(mapKey));
				var $item = memberIdToItemMap[mapKey];

				// Force Add Color Coding
				if(!$item){
					
				}
				else if(data[mapKey] && !$item[0]["forceAdd"]){

				}else if(data[mapKey] && $item[0]["forceAdd"]){
					$tr.addClass("forceAddBorderErrorClass");
				}else if(!data[mapKey] && $item[0]["forceAdd"]){
					$tr.addClass("forceAddClass");
				}else if(!data[mapKey] && !$item[0]["forceAdd"]){
					$tr.addClass("forceAddErrorClass");
				}
			}

			contentHolder.find('div#forceAdd').hide();
		};

		base.setImage = function(tr, item){

			var imagePath = item["imagePath"];
			switch(base.getItemType(item)){
			case "ims" : imagePath = GLOBAL_contextPath + '/images/ims_img.jpg'; break;
			case "cnet" : imagePath = GLOBAL_contextPath + '/images/productSiteTaxonomy_img.jpg'; break;
			case "facet" : imagePath = GLOBAL_contextPath + '/images/facet_img.jpg'; break;
			default: if ($.isBlank(imagePath)) imagePath = GLOBAL_contextPath + "/images/no-image60x60.jpg"; break;
			}

			setTimeout(function(){	
				tr.find("td#itemImage > img").attr("src", imagePath).off().on({
					error:function(){ 
						$(this).unbind("error").attr("src", GLOBAL_contextPath + "/images/no-image60x60.jpg"); 
					}
				});
			},10);
		};

		base.getItemType = function(item){
			var $condition = item.condition;
			var type = "unknown";

			if($.isBlank($condition)){
				return type;
			}

			if (!$condition["CNetFilter"] && !$condition["IMSFilter"]){
				type="facet";
			}else if($condition["CNetFilter"]){
				type="cnet";
			}else if($condition["IMSFilter"]){
				type="ims";
			}

			return type;
		};
		
		base.populateSelectedList = function(data, contentHolder){
			
		};
		
		base.populateSelectionList = function(data, contentHolder){
			var list = data.list;

			contentHolder.find("ul#fieldListing > li").not("#fieldListingPattern").remove();
			contentHolder.find("tbody#fieldSelectedBody > tr").not("#fieldSelectedPattern").remove();

			for (var i=0; i< data.totalSize; i++){
				var name = list[i].campaignName;
				var id = list[i].campaignId;

				if ($.isNotBlank(campaignName)){
					$.pushIfNotExist(self.excFields, campaignName, function(el){ return el === name; });
					self.populateSelectedCampaignField($contentHolder, campaignName, campaignId);
				}
			}	

			//base.populateList(contentHolder,1);
		};

		base.getDefaultTemplate = function(){
			var template = '';
			template = '<div style="width:610px;">';
			
			template += '<div id="fieldListing" class="fieldListing floatL w240 minHeight300">';			
			template += '	<h3 class="borderB fsize14 fbold padB4 mar0 marT15">Campaign List<span id="sfCount" class="txtAR fsize11 floatR"></span></h3>';
			template += '	<input id="searchBoxField" name="searchBoxField" type="text" class="farial fsize12 fgray searchBoxIconBg w233">';
			template += '	<div class="borderT marT8"></div>';
			
			template += '	<div id="preloader" class="marT30 txtAC"><img src="../images/preloader30x30Trans.gif"></div>';
			template += '	<div id="content">';
			template += '		<ul id="fieldListing" class="menuFields">';
			template += '			<li id="fieldListingPattern" class="fieldListingItem" style="display:none">';
			template += '				<a href="javascript:void(0);"><img src="../images/icon_addField.png" style="margin-bottom:-3px"></a>';
			template += '				<span></span>';
			template += '			</li>';
			template += '		</ul>';
			template += '		<div id="fieldsBottomPaging"></div>';			
			template += '	</div>';
			template += '</div>';

			template += '<div id="fieldSelected" class="floatL marL3 w350">';
			template += '	<h3 class="fsize14 fbold pad8 mar0" style="background:#cacaca">Campaigns Using This Banner<span id="sfSelectedCount" class="txtAR fsize11 floatR"></span></h3>';
			template += '	<div style="overflow-y:scroll; height: 250px">';
			template += '		<table class="tblfields" style="width:100%" cellpadding="0" cellspacing="0">';
			template += '			<tbody id="fieldSelectedBody">';
			template += '				<tr id="fieldSelectedPattern" style="display: none" class="fieldSelectedItem">';
			template += '					<td class="pad0 txtAC"><a class="removeSelected" href="javascript:void(0);"><img src="../images/icon_delete2.png" class="marL3"></a></td>';
			template += '					<td class="fields">';
			template += '						<div class="fieldsHolder marL3">';
			template += '							<span class="txtHolder"></span>';
			template += '							<div class="bargraph borderR3 height24">';							
			template += '								<div class="clearB"></div>';
			template += '							</div>';																		
			template += '						</div>';
			template += '					</td>';
			template += '				</tr>';
			template += '			</tbody>';
			template += '		</table>';					
			template += '	</div>';
			template += '	<div align="right" class="marT15"><a id="closeBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Close</div></a></div>';
			template += '</div>';
		
			template += '</div>';
			
			return template;
		};
				
		base.showQtipPreview = function(){
			base.$el.qtip({
				content: {
					text: $('<div/>'),
					title: { 
						text: base.options.headerText, 
						button: true
					}
				},
				position:{
					at: 'center',
					my: 'center',
					target: $(viewport)
				},
				show:{
					ready: true,
					modal: true
				},
				style: {
					width: 'auto'
				},
				events: { 
					show: function(event, api){
						base.contentHolder = $("div", api.elements.content);
						base.api = api;

						//show popup template
						base.contentHolder.append(base.getDefaultTemplate());
		
						//populate items
						//base.options.itemImportTypeListCallback(base, base.contentHolder.find("#leftPreview"));

						//add button listener
						$contentHolder.find('a#closeBtn').on({
							click: function(e){
								api.hide();
							}
						});
						
						//add search input listener
						$contentHolder.find('input[id="searchBoxField"]').val(base.searchText).on({
							blur: function(e){
								if ($.trim($(e.target).val()).length == 0) 
									$(e.target).val(base.searchText);
								timeout(e);
							},
							focus: function(e){
								if ($.trim($(e.target).val()) == base.searchText) 
									$(e.target).val("");
								timeout(e);
							},
							keyup: timeout 
						});
					},
					
					hide:function(event, api){
						base.searchText = "";
						base.options.postHookCallback(base, base.contentHolder, base.options.rule);
						base.api.destroy();
					}
				}
			});
		};

		// Run initializer
		base.init();
	};
	
	$.selectitem.defaultOptions = {
			headerText:"Select Item",
			transferType: "",
			ruleType: "",
			rule: null,
			enablePreTemplate: false,
			enablePostTemplate: false,
			preTemplate: "",
			postTemplate: "",
			itemImportTypeListCallback: function(base, contentHolder){},
			itemImportAsListCallback: function(base, contentHolder){},
			
			checkUncheckCheckboxCallback: function(base, ruleId, pub){},
			changeImportTypeCallback: function(base, ruleId, importType){},
			changeImportAsCallback: function(base, ruleId, importAs, ruleName, newName){},
			
			postButtonClick: function(base){},
			
			populatedSelectedItemCallback: function(base, contentHolder, rule){},
			
			populateItemListCallback: function(base, contentHolder, rule){},
			
			postHookCallback: function(base, contentHolder, rule){}
	};

	$.fn.selectitem = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.selectitem(this, options));
			});
		};
	};
})(jQuery);
