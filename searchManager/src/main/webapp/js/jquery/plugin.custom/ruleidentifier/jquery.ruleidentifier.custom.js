(function($){

	$.ruleidentifier = function(el, options){
		// To avoid scope issues, use 'base' instead of 'this'
		// to reference this class from internal events and functions.
		var base = this;

		// Access to jQuery and DOM of element
		base.$el = $(el);
		base.el = el;

		// Add a reverse reference to the DOM object
		base.$el.data("ruleidentifier", base);

		base.setId = function(ui,id){
			ui.find("div:first").prop({
				id: $.isNotBlank(id)? id: "plugin-ruleidentifier-" + base.options.id
			});
		};

		base.init = function(){
			base.options = $.extend({},$.ruleidentifier.defaultOptions, options);

			if(base.options.isPopup){
				base.$el.qtip({
					id: "plugin-ruleidentifier-qtip",
					content: {
						text: $('<div/>'),
						title: {text: base.options.qTipTitle, button: true }
					},
					show:{
						modal:true
					},
					style: {
						width: 'auto'
					},
					events: { 
						render: function(event, api){
							base.api = api;
							base.$el = $("div", api.elements.content);
							base.$el.empty().append(base.getTemplate());
							base.setId(base.$el);
						},

						show: function(event, api){
							base.populateContent();
						},

						hide: function(event, api){
							api.destroy();
						}
					}
				});
			}else{
				base.$el.empty().append(base.getTemplate());
				base.setId(base.$el);
				base.populateContent();
			}
		};

		base.populateContent = function(){
			
			base.$el.find("#content").find("#ruleId").text(base.options.rule["ruleId"]);
			base.$el.find("#content").find("#ruleName").val(base.options.rule["ruleName"]);
			base.$el.find("#content").find("#ruleDescription").val(base.options.rule["ruleDescription"]);
			
			if($.isNotBlank(base.options.ruleStatus["formattedLastPublishedDateTime"])){
				base.$el.find("#header").find("span#ruleLastPublishedDate").empty().append(ruleStatus["formattedLastPublishedDateTime"]);
			}
			
			base.registerListener();
		};
		
		base.registerListener = function(){
			base.addAuditIconListener(); 
			base.addCommentIconListener(); 
			base.addCommentIconListener(); 
			base.addToggleContent();
		};

		base.addCommentIconListener = function(){
			base.$el.find("#commentIcon").off().on({
				click: function(e){
					$(e.currentTarget).comment({
						title: "Rule Comment",
						showAddComment: true,
						locked: e.data.options.locked,
						itemDataCallback: function(ibase, page){
							if(e.data){
								CommentServiceJS.getComment(e.data.options.moduleName, e.data.options.rule["ruleId"], ibase.options.page, ibase.options.pageSize, {
									callback: function(data){
										var total = data.totalSize;
										ibase.populateList(data);
										ibase.addPaging(ibase.options.page, total);
									},
									preHook: function(){
										ibase.prepareList();
									}
								});
							}
						},
						itemAddComment: function(ibase, comment){
							
							CommentServiceJS.addComment(e.data.options.moduleName, comment, $.makeArray(e.data.options.rule["ruleId"]), {
								callback: function(data){
									if(data==1){
										CommentServiceJS.getComment(e.data.options.moduleName, e.data.options.rule["ruleId"], ibase.options.page, ibase.options.pageSize, {
											callback: function(data){
												var total = data.totalSize;
												ibase.populateList(data);
												ibase.addPaging(ibase.options.page, total);
											},
											preHook: function(){
												ibase.prepareList();
											}
										});
									}
								},
								preHook: function(){
									ibase.prepareList();
								}
							});
						}
					});
				}
			}, {options: base.options});
		};

		base.addAuditIconListener = function(){
			base.$el.find("#auditIcon").off().on({
				click: function(e){
					$(e.currentTarget).viewaudit({
						itemDataCallback: function(ibase, page){
							AuditServiceJS.getBannerTrail(e.data.options.rule["ruleId"], ibase.options.page, ibase.options.pageSize, {
								callback: function(data){
									var total = data.totalSize;
									ibase.populateList(data);
									ibase.addPaging(ibase.options.page, total);
								},
								preHook: function(){
									ibase.prepareList();
								}
							});
						}
					});
				}
			}, {options: base.options});
		};

		base.addToggleContent = function(){
			base.$el.find("#toggler").off().on({
				click: function(e){
					base.$el.find("#content").slideToggle();
				}
			});
		};

		base.getTemplate = function(){
			var template = '';

			template += '<div class="plugin-ruleidentifier">';
			template += '	<div class="bgboxGray marT5">';	
			template += '		<div id="header" class="fsize14 fbold">';
			template += '			<div>';
			template += '				<span id="toggler" class="floatL">';
			template += '					<a href="javascript:void(0);">';
			template += '						<h6 class="clearfix">Linked Campaigns</h6>';
			template += '					</a>';
			template += '				</span>';
			template += '			</div>';
			template += '			<div id="iconHolder" class="floatR">';
			template += '				<a href="javascript:void(0);" id="lastModifiedIcon" class="clearfix">';
			template += '					<div>';
			template += '						<img class="lastModifiedIcon" src="../images/user_red.png">';
			template += '					</div>';
			template += '				</a>';
			template += '				<a href="javascript:void(0);" id="commentIcon" class="clearfix">';
			template += '					<div>';
			template += '						<img class="commentIcon" src="../images/icon_comment.png">';
			template += '					</div>';
			template += '				</a>';
			template += '				<a href="javascript:void(0);" id="auditIcon" class="clearfix">';
			template += '					<div>';
			template += '						<img class="pointer" id="auditIcon" src="../images/icon_history.png" alt="History" title="History">';
			template += '					</div>';
			template += '				</a>';
			template += '				<a href="javascript:void(0);" id="downloadIcon" class="clearfix">';
			template += '					<div class="btnGraph btnDownload" id="downloadIcon" alt="Download" title="Download"></div>';
			template += '				</a>';
			template += '				<a href="javascript:void(0);" id="delIcon" class="clearfix">';
			template += '					<div class="btnGraph btnClearDel" alt="Remove All" title="Remove All"></div>';
			template += '				</a>';
			template += '			</div>';
		
			template += '			<div class="clearB"></div>';
			template += '		</div>';		
			template += '		<div id="content" style="width:100%; display:none" class="borderT">';
			template += '			<div style="width:45%; valign:top" class="clearfix">';
			template += '				<span>';
			template += '					<label>R-ID:</label>';
			template += '					<label id="ruleId">This is a rule id</label>';
			template += '				</span>';
			template += '				<div class="clearB"></div>';
			template += '				<span>';
			template += '					<label for="ruleName">Name: </label>';
			template += '					<input id="ruleName" type="text">';
			template += '				</span>';
			template += '			</div>';
			template += '			<div style="width:45%; valign:top" class="clearfix">';
			
			if (base.options.showValidity){
				template += '				<span>';
				template += '					<label for="fromDate">Schedule</label>';
				template += '					<input id="fromDate" type="text" class="w70">';
				template += '					<input id="toDate" type="text" class="w70">';
				template += '				</span>';
				template += '				<div class="clearB"></div>';
			} 
			
			template += '				<span>';
			template += '					<label for="ruleDescription">Description</label>';
			template += '					<textarea id="ruleDescription"></textarea>';
			template += '				</span>';
			template += '			</div>';
			template += '		</div>';
			template += '	</div>';
			template += '</div>';

			return template;
		};

		// Run initializer
		base.init();
	},

	$.ruleidentifier.defaultOptions = {
			id: 1,
			isPopup: false,
			qTipTitle: "Select Item",
			moduleName: "",
			rule: null,
			ruleStatus: null,
			locked: false,
			showValidity: false,
			downloadCallback: function(){},
			deleteCallback: function(){},
	};

	$.fn.ruleidentifier = function(options){
		if (this.length) {
			return this.each(function() {
				(new $.ruleidentifier(this, options));
			});
		};
	};

})(jQuery);