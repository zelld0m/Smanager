(function ($) {

	var Banner = {
		moduleName: "Banner",
		selectedRule:  null,
		rulePageSize: 15,
		selectedRuleStatus: null,
		
		getBannerList : function(page) {
			var self = this;
			$("#keywordSidePanel").sidepanel({
				moduleName: self.moduleName,
				headerText : "Banner",
				page: page,
				pageSize: self.rulePageSize,
				showAddButton: !self.selectedRuleStatus["locked"] && allowModify,
			});
		},
		
		getAddBannerTemplate : function(){
			var template = "";
			template += '<div id="addBannerTemplate">';
			template += '<div class="w282 padT10 newBanner">';
			
			template += '	<div id="keywordinput">';
			template += '		<label class="floatL w80 txtLabel">Name: </label>'; 
			template += '		<label class="floatL"><input id="popKeywordName" type="text" class="w188" maxlength="100"></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div id="bannerImageMode">';
			template += '		<label class="floatL w80 txtLabel"></label>'; 
			template += '		<label class="floatL"><label class="floatL padTB2">Paste image URL | Upload an image</label></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			template += '	<div id="bannerImage">';
			template += '		<label class="floatL w80 txtLabel">Image: </label>'; 
			template += '		<label class="floatL"><label class="floatL padTB2"><textarea id="comment" class="w240"></textarea></label></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div id="description">';
			template += '		<label class="floatL w80 txtLabel">Description: </label>'; 
			template += '		<label class="floatL"><input id="description" type="text" class="w188" maxlength="200"></label>';
			template += '	</div>';
			template += '	<div class="clearB"></div>';
			
			template += '	<div class="txtAR pad3 marT10">';
			template += '		<a id="addButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Save</div> </a>'; 
			template += '		<a id="clearButton" href="javascript:void(0);" class="buttons btnGray clearfix"> <div class="buttons fontBold">Clear</div> </a>';
			template += '	</div>'; 
			template += '</div>';
			template += '</div>';
			return template;
		},
		
		prepareBanner : function(){
			clearAllQtip();
			$("#preloader").show();
			$("#submitForApproval").hide();
			$("#titleHeader").empty();
		},
		
		setBanner : function(rule){
			var self = this;
			self.selectedRule = rule;

			self.showBannerContent();
		},
		
		showBannerContent : function(){
			var self = this;
			
			self.prepareBanner();
			self.getBannerList(1);

			if(self.selectedRule==null){
				$("#preloader").hide();
				$("#titleText").html(self.moduleName);
				return;
			}
			
			$("#submitForApproval").rulestatus({
				moduleName: self.moduleName,
				rule: self.selectedRule,
				ruleType: "Facet Sort",
				enableVersion: true,
				authorizeRuleBackup: allowModify,
				authorizeSubmitForApproval: allowModify, // TODO: verify if need to be controlled user access
				postRestoreCallback: function(base, rule){
					base.api.destroy();
					FacetSortServiceJS.getRuleById(self.selectedRule["ruleId"],{
						callback: function(data){
							self.setFacetSort(data);
						},
						preHook: function(){
							self.prepareFacetSort();
						}
					});
				},
				afterSubmitForApprovalRequest:function(ruleStatus){
					self.showBannerContent();
				},
				beforeRuleStatusRequest: function(){
					self.prepareFacetSort();	
				},
				afterRuleStatusRequest: function(ruleStatus){
					$("#preloader").hide();
					$("#submitForApproval").show();
					$("#titleText").html(self.moduleName + " for ");
					$("#titleHeader").text(self.selectedRule["ruleName"]);
					$("#readableString").html(self.selectedRule["readableString"]);

					self.selectedRuleStatus = ruleStatus;

					$('#itemPattern' + $.escapeQuotes($.formatAsId(self.selectedRule["ruleId"])) + ' div.itemSubText').html(getRuleNameSubTextStatus(self.selectedRuleStatus));

					self.addSaveRuleListener();
					self.addDeleteRuleListener();
					self.addDownloadListener();

					$('#auditIcon').off().on({
						click: function(e){
							$(e.currentTarget).viewaudit({
								itemDataCallback: function(base, page){
									AuditServiceJS.getFacetSortTrail(self.selectedRule["ruleId"], base.options.page, base.options.pageSize, {
										callback: function(data){
											var total = data.totalSize;
											base.populateList(data);
											base.addPaging(base.options.page, total);
										},
										preHook: function(){
											base.prepareList();
										}
									});
								}
							});
						}
					});
				}
			});
			
			/*$("a#addPromoBannerIcon").qtip({
				id: "add-banner",
				content: {
					text: $('<div/>'),
					title: { text: 'New Promo Banner', button: true }
				},
				position: {
					target: $("a#addPromoBannerIcon")
				},
				show: {
					ready: true
				},
				style: {width: 'auto'},
				events: { 
					show: function(e, api){
						var $contentHolder = $("div", api.elements.content).html(self.getAddBannerTemplate());
					},
					hide: function (e, api){
						api.destroy();
					}
				}
			});*/
		},
		
		init : function() {
			var self = this;
			self.showBannerContent();
		}
	};
	
	$(document).ready(function() {
		Banner.init();
	});	
})(jQuery);