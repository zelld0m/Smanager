(function($){

	$.ruleitem = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM add products of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("ruleitem", base);

		base.init = function(){
			base.options = $.extend({},$.ruleitem.defaultOptions, options);
			base.doc = base.options.doc;
			base.isMember = base.doc[base.options.memberTypeTag] === "PART_NUMBER" || base.doc[base.options.memberExpiredTag] != undefined;
			base.showItems();
		};

		base.prepareForceAddStatus = function(){
			base.contentHolder.find('.firerift-style').hide();
		};

		base.updateForceAddStatus = function(data){
			for(var mapKey in data){
				var $li = base.contentHolder.find('li#item' + $.formatAsId(mapKey));
				var $item = base.items[mapKey];
				
				base.contentHolder.find('.firerift-style').show();

				$li.find('input.firerift-style-checkbox').slidecheckbox({
					id: $item["memberId"],
					initOn: $item["forceAdd"],
					changeStatusCallback: function(memberId, status){
						base.options.itemUpdateForceAddStatusCallback(base, memberId, status);
					}
				});
				
				// Force Add Color Coding
				if(data[mapKey] && !$item["forceAdd"]){
					$li.find('.firerift-style').remove();
				}else if(data[mapKey] && $item["forceAdd"]){
					$li.addClass("forceAddBorderErrorClass");
				}else if(!data[mapKey] && $item["forceAdd"]){
					$li.addClass("forceAddClass");
				}else if(!data[mapKey] && !$item["forceAdd"]){
					$li.addClass("forceAddErrorClass");
				}

				$li.find('#preloaderForceAdd').remove();	
			}
		};

		base.updateSelectedItem = function(){
			base.contentHolder.find("#selItemPosition").prop({readonly:base.options.locked});
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

		base.setImage = function(li, item){
			var $li = li;
			var imagePath = item["imagePath"];

			switch(base.getItemType(item)){
			case "ims": imagePath = GLOBAL_contextPath + '/images/ims_img.jpg'; break;
			case "cnet": imagePath = GLOBAL_contextPath + '/images/productSiteTaxonomy_img.jpg'; break;
			case "facet":  imagePath = GLOBAL_contextPath + '/images/facet_img.jpg'; break;
			};

			if($.isNotBlank(imagePath)){
				setTimeout(function(){	
					$li.find("img#productImage").prop("src", imagePath).off().on({
						error:function(){ 
							$(this).unbind("error").prop("src", GLOBAL_contextPath + '/images/no-image60x60.jpg'); 
						}
					});
				},10);
			}
		};

		base.prepareList = function(){
			base.contentHolder.find("#preloaderItem").show();
			base.contentHolder.find("ul#itemList > li:not(#itemPattern)").remove();
		};

		base.setItemValues = function(li, item){
			var $li = li;
			var isPartNumber = $.isNotBlank(item["memberTypeEntity"]) && item["memberTypeEntity"] === "PART_NUMBER";
			var isFacet = $.isNotBlank(item["memberTypeEntity"]) && item["memberTypeEntity"] === "FACET";

			$li.find("#position").html(item["location"]);

			if(isPartNumber){
				if (item["dpNo"] === base.doc["DPNo"])
					$li.addClass("selected");

				$li.find("#conditionText").remove();
				$li.find("#partNo").html(item["dpNo"]);
				$li.find("#mfrNo").html(item["mfrPN"]);

			}else if(isFacet){

				$li.find("#partNoLabel, #partNo").remove();
				$li.find("#mfrNoLabel, #mfrNo").remove();

				var conditionText = item.condition['readableString'];

				if(conditionText.length > 100){
					conditionText  = conditionText.substring(0,100) + "...";
				}

				$li.find("#conditionText").html(conditionText);
			}

			var validityDate = item["formattedExpiryDate"];

			if (!item["isExpired"]) $li.find("#validityText").html("Validity: ");
			$li.find("#validityDate").text($.isBlank(validityDate)? "Indefinite" : validityDate);
		};

		base.populateList = function(data){
			var list = data.list;
			var $ul = base.contentHolder.find("ul#itemList");
			base.items = new Array; 
			var memberIds = new Array();
			
			for (var itm in list){
				base.items[list[itm]["memberId"]] = list[itm];
			}
			
			base.contentHolder.find("#preloaderItem").hide();

			// Delete all the rows except for the "pattern" row
			$ul.children().not("#itemPattern").remove();

			// populate list
			for (var i = 0; i < list.length; i++) {
				var id = list[i]["memberId"];
				memberIds.push(id);
				var $li = base.contentHolder.find("li#itemPattern").clone();
				$li.prop({id:"item" + $.formatAsId(id)});
				base.setImage($li, list[i]);
				base.addDeleteItemListener($li, list[i]);
				base.setItemValues($li, list[i]);
				$li.show();
				$ul.append($li);
			}

			if (base.options.enableForceAddStatus && memberIds.length>0){
				base.options.itemForceAddStatusCallback(base, memberIds);
			} 

			if (base.options.enableSortable) 
				base.addSortableOption();

			base.contentHolder.find('ul#itemList > li:not(#itemPattern)').removeClass("alt");
			base.contentHolder.find('ul#itemList > li:not(#itemPattern):nth-child(even)').addClass("alt");
			base.updateSelectedItem();
		};

		base.addSortableOption = function(){
			base.contentHolder.find("ul#itemList").sortable("destroy").sortable({ 
				handle : '.handle',
				cursor : 'move',
				disabled: base.options.locked,
				start: function(event, ui) {
					ui.item.data('start_pos', ui.item.index());
				},     
				change: function(event, ui) {
					var index = ui.placeholder.index();
					if (ui.item.data('start_pos') < index){
						base.contentHolder.find('ul#itemList > li:nth-child(' + index + ')').addClass('sortableHighlights');
					}else{
						base.contentHolder.find('ul#itemList > li:eq(' + (index + 1) + ')').addClass('sortableHighlights');
					}
				},
				update: function(event, ui) {
					base.contentHolder.find('ul#itemList > li').removeClass('sortableHighlights');
					base.contentHolder.find('ul#itemList > li:not(#itemPattern)').removeClass("alt");
					base.contentHolder.find('ul#itemList > li:not(#itemPattern):nth-child(even)').addClass("alt");
				},
				stop: function(event, ui) {
					var sourceIndex = ui.item.data('start_pos');
					var destinationIndex = ui.item.index();

					base.contentHolder.find('ul#itemList > li:not(#itemPattern)').removeClass("alt");
					base.contentHolder.find('ul#itemList > li:not(#itemPattern):nth-child(even)').addClass("alt");

					// update position
					if(sourceIndex != destinationIndex){
						base.options.itemMovePositionCallback(base, ui.item.attr("id").split('_')[1], destinationIndex);
					}	 
				}
			});
		};

		base.getList = function(){
			base.options.itemDataCallback(base);
		};

		base.getTemplate = function(){
			var template = "";

			template += '<div>';
			template += '	<div id="dialog-confirm" title="This is a header title" class="farial" style="float:left; width:225px">';
			template += '		<div class="marB10"><span>This is a confirm message</span></div>';
			template += '		<div><center><img id="selItemProductImage" src="' + GLOBAL_contextPath + '/images/no-image.jpg" class="border" style="width:116px; height:100px"></center></div>';
			template += '		<div><center><span id="selItemManufacturer" class="fbold"></span></div>';
			template += ' 		<div style="position:absolute; float:right; top:50px; left:224px">';
			template += '			<a href="javascript:void(0);" id="toggleCurrent"><img src="../images/btnTonggleShow.png"></a>';
			template += '		</div>';
			template += '	<div>';
			template += '	<ul class="listProd">';
			template += '		<li><label class="fbold title">SKU #: </label><span id="selItemPartNo">' + base.doc["DPNo"] + '</span></li>';
			template += '		<li><label class="fbold title">Position: </label><input type="text" id="selItemPosition" style="width:30px"></li>';
			template += '		<li><label class="fbold title">Valid Until: </label><input type="text" id="selItemValidityDate" style="width:65px"></li>';
			template += '		<li><label class="fbold title">Comments:</label><div id="aStampExpired"><img id="selItemStampExpired" src="../images/expired_stamp50x16.png" style="display:none"></div><textarea id="selItemComment"></textarea></li>';
			template += '	</ul>';
			template += '</div>';

			template += '<div id="btnHolder" class="marB10 txtAC">';
			template += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="saveBtn"><div class="buttons fontBold">Save</div></a>';
			template += '	<a class="buttons btnGray clearfix" href="javascript:void(0);" id="removeBtn"><div class="buttons fontBold">Remove</div></a>';
			template += '</div>';

			template += '<div id="current" style="float:left; margin-left:7px" class="toggleDiv">';
			template += '	<div class="fsize16 titleToggle" style="margin:0 "><h2 style="padding-top:8px; margin:0 10px">List of Elevated Items</h2></div >';
			template += '	<div id="toggleItems" style="overflow:auto; overflow-y:auto; overflow-x:hidden; height:340px; width:220px">';
			template += '	<div id="preloaderItem" style="display:none" class="circlePreloader"><img src="' + GLOBAL_contextPath +  '/images/ajax-loader-circ.gif' + '"/></div>';
			template += '		<ul id="itemList" class="listItems">';
			template += '			<li id="itemPattern" class="clearfix" style="display:none">'; 
			template += '				<div class="handle">';
			template += '                   <div class="borderB height8">';
			template += ' 					<div class="floatR w10 posRel padR10" style="z-index:1; top:-8px">';
			template += '						<a id="deleteIcon" class="deleteIcon" href="javascript:void(0);">';
			template += '					  		<img src="' + GLOBAL_contextPath + '/images/iconDelete.png">';
			template += '						</a>';			
			template += '					</div>';
			template += '                   <div class="floatL w100 marTn12">';
			template += '					  	<div class="floatL w20" style="display:block"><img src="' + GLOBAL_contextPath + '/images/icon_move.png"></div>';
			template += '                   	<div>';
			template += '							<img id="preloaderForceAdd" class="preloaderForceAdd" src="' + GLOBAL_contextPath + '/images/horizontalPreloaderBlue.gif">';
			template += '							<input id="setForceAdd" type="checkbox" class="firerift-style-checkbox small-normal-forceadd" style="display:none; margin-top:0">';
			template += '						</div>';
			template += '					</div>';
			template += '					</div>';
			template += '					<div class="clearB"></div>';
			
			template += '					<img id="productImage" src="' + GLOBAL_contextPath + '/images/no-image60x60.jpg" class="border floatL" width="60px" >';
			template += '					<div class="w125 floatL marL8 posRel">';
			template += '				  		<ul class="listItemInfo">';
			template += '							<li class="label">Position:</li><li class="value" id="position"></li>';
			template += '							<li id="validityText" class="label"><img id="stampExpired" src="' + GLOBAL_contextPath + '/images/expired_stamp50x16.png"></li><li class="value" id="validityDate"></li>';
			template += '							<li id="partNoLabel" class="label">SKU #:</li><li class="value" id="partNo"></li>'; 
			template += '							<li id="mfrNoLabel" class="label">Mfr Part #:</li><li class="value" id="mfrNo"></label>';
			template += '				  		</ul>';
			template += '					</div>';
			template += '					<div id="conditionText" class="label w125 floatL marL8 posRel" ></div>';
			template += '				</div>';
			template += '			</li>';
			template += '		</ul>';
			template += '	</div>';
			//template += '</div>';

			return template;
		};

		base.getStatus = function(){
			DeploymentServiceJS.getRuleStatus(base.options.moduleName, base.options.keyword, {
				callback:function(ruleStatus){
					base.ruleStatus = ruleStatus;
					base.options.locked = base.options.locked || (base.ruleStatus!=null && $.inArray(base.ruleStatus["approvalStatus"],["PENDING","APPROVED"])>=0);
				},
				preHook: function(){
					base.prepareList();
				},
				postHook:function(){
					base.getList();
					base.contentHolder.find("#selItemValidityDate").datepicker({
						showOn: "both",
						disabled: base.options.locked,
						minDate: base.options.validityDateMinDate,
						maxDate: base.options.validityDateMaxDate,
						buttonText: "Validity Date",
						buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
						buttonImageOnly: true
					});

					if (base.options.locked){
						base.contentHolder.find("#btnHolder").hide();
					}
				}
			});
		};

		base.showItems = function(){
			$(base.$el).qtip({
				content: {
					text: $('<div/>'),
					title: { text: base.options.moduleName + " Item", button: true }
				},
				position: {
					my: 'right center',
					at: 'top center'
				},
				style: {
					width: "auto"
				},
				show: {
					ready: true,
					modal:  true
				},
				events: { 
					show: function(event, api){
						base.api = api;
						base.contentHolder = $("div", api.elements.content);
						base.contentHolder.html(base.getTemplate());
						base.getStatus();
					},
					hide: function(event, api){
						api.destroy();
					}
				}
			});
		};

		base.addDeleteItemListener = function(li, item){
			var $li = li;

			$li.find("a.deleteIcon").off().on({
				click: function(e){
					if (!e.data.locked){
						var itemName = "";
						switch(e.data.item["memberTypeEntity"]){
						case "PART_NUMBER": itemName = " product item with SKU#: " + e.data.item["dpNo"] + " at position " + e.data.item["location"]; break;
						case "FACET": itemName = " facet item with condition " + e.data.item.condition["readableString"]  + " at position " + e.data.item["location"]; break;
						}

						jConfirm("Delete " + itemName + "?", "Delete Item", function(result){
							if(result) base.options.itemDeleteCallback(base, e.data.item["memberId"]);
						});
					}
				},
				mouseenter: showHoverInfo
			},{locked: base.options.locked, item: item});
		};	

		// Run initializer
		base.init();
	};

	$.ruleitem.defaultOptions = {
			moduleName: "",
			keyword: "",
			locked: false,
			doc: null,
			validityMinDate: 0,
			validityMaxDate: "+1Y",
			memberTypeTag: "",
			memberExpiredTag: "",
			enableSortable: false,
			enableForceAddStatus: false,
			itemForceAddStatusCallback: function(base, memberIds){},
			itemUpdateForceAddStatusCallback: function(base, memberId, status){},
			itemDataCallback: function(base){},
			itemSelectedItemCallback: function(base){},
			itemDeleteCallback: function(base){},
			itemMovePositionCallback: function(base, memberId, destinationIndex){}
	};

	$.fn.ruleitem = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.ruleitem(this, options));
			});
		};
	};

})(jQuery);