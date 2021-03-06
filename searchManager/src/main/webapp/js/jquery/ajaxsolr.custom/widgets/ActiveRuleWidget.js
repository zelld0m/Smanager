(function($) {

    AjaxSolr.ActiveRuleWidget = AjaxSolr.AbstractWidget.extend({
        init: function() {
            var settings = $.cookie('ar.' + GLOBAL_username);
            if ($.isBlank(settings)) {
                $.cookie('ar.' + GLOBAL_username, "collapse", {path: GLOBAL_contextPath});
            }
        },
        getRuleStatus: function($li, rule) {
            DeploymentServiceJS.getRuleStatus(GLOBAL_storeId, rule["type"], rule["id"], {
                callback: function(data) {
                    if (!$.isEmptyObject(data)) {
                        $li.find('.ruleStatus > .status').text(getRuleNameSubTextStatus(data));
                        $li.find('.ruleStatus > .statusMode').text($.isNotBlank(data["locked"]) && data["locked"] ? " [ Read-Only ]" : "");
                        $li.find('.lastPublished').text($.isNotBlank($.toStoreFormat(data["lastPublishedDate"])) ? 'Last Published: ' + $.toStoreFormat(data["lastPublishedDate"]) : '');
                        if(data["ruleSource"]==="AUTO_IMPORT"){
                        	$li.find('.lastPublished').append(" | Auto-Import");
                        }
                    
                    }
                }
            });
        },
        beforeRequest: function() {
            var self = this;
            $(self.target).find('#switcher').off();
            $(self.target).find('#switcherText').text("Active Rule");

            //TODO: Remove/Update
            $(self.target).find('ul#itemListing > li.items:not(#itemPattern)').each(function(idx, el) {
                $(el).find('.select > input[type="checkbox"]').prop("disabled", true);
                $(el).find('.preloader').show();
            });
        },
        afterRequest: function() {
            var self = this;
            $(self.target).empty();
            var keyword = $.isArray(self.manager.store.values('q')) ? self.manager.store.values('q')[0] : self.manager.store.values('q');
            if ($.isNotBlank(keyword)) {
                $(self.target).html(self.getTemplate());

                var rules = self.manager.response.responseHeader["search_rules"];
                var $ul = $(self.target).find("ul#itemListing");
                $ul.find("li.items:not(#itemPattern)").remove();
                var activeCount = 0;

                for (var i = 0; i < rules.length; i++) {
                    var rule = rules[i]['rule'];
                    if(rule["type"].toLowerCase() == "facet sort" && rule["id"] == "DEFAULT")
                		continue;
                    if (rule['active'] === 'true') {
                        activeCount++;
                    }
                }

                $(self.target).find('#switcherText').text(activeCount + ' Active ' + (activeCount > 1 ? 'Rules' : 'Rule'));

                for (var i = 0; i < rules.length; i++) {
                	var rule = rules[i]["rule"];
                	
                	if(rule["type"].toLowerCase() == "facet sort" && rule["id"] == "DEFAULT")
                		continue;
                	
                    $li = $ul.find("li#itemPattern").clone().prop("id", $.formatAsId(rule["id"]));

                    $li.removeClass("fgray");
                    if (rule["active"] !== "true") {
                        $li.addClass("fgray");
                    }

                    var checkboxId = "";

                    switch (rule["type"].toLowerCase()) {
                        case "ranking rule":
                            checkboxId = "disableRelevancy";
                            break;
                        case "query cleaning":
                            checkboxId = "disableRedirect";
                            break;
                        case "elevate":
                            checkboxId = "disableElevate";
                            break;
                        case "demote":
                            checkboxId = "disableDemote";
                            break;
                        case "exclude":
                            checkboxId = "disableExclude";
                            break;
                        case "facet sort":
                            checkboxId = "disableFacetSort";
                            break;
                        case "did you mean":
                            checkboxId = "disableDidYouMean";
                            break;
                        case "banner":
                            checkboxId = "disableBanner";
                            break;
                    }
                    
                    $li.find('.select > input[type="checkbox"]').prop({
                        "id": checkboxId
                    }).val(rule["id"]).slidecheckbox({
                        id: checkboxId,
                        initOn: rule["active"] === "true" || rule["active"] === "loop" || rule["active"] === "disabledRedirectToPage",
                        locked: false, //TODO:
                        changeStatusCallback: function(base, dt) {
                            var cid = dt.id;
                            var cval = dt.value;
                            if (dt.status) {
                                self.manager.store.remove(cid);
                            } else {
                                self.manager.store.addByValue(cid, AjaxSolr.Parameter.escapeValue(cval));
                            }

                            self.manager.doRequest();
                        }
                    });
	                    
                    $li.find(".ruleType").text(rule["type"]);
                    $li.find(".name").text(rule["name"]);

                    $li.find(".imageIcon > img").preview({
                        ruleType: rule["type"],
                        ruleId: rule["id"],
                        itemForceAddStatusCallback: function(base, memberIds) {
                            if (rule["type"].toLowerCase() === "elevate")
                                ElevateServiceJS.isRequireForceAdd(keyword, memberIds, {
                                    callback: function(data) {
                                        base.updateForceAddStatus(data);
                                    },
                                    preHook: function() {
                                        base.prepareForceAddStatus();
                                    }
                                });
                        }
                    });

                    if (rule["active"] === "loop") {
                    	$(self.target).find("#hasLoop").show();
                    	$li.find(".alertStatus").text("Rule Ignored").show();
                    } else if (rule["active"] === "disabledRedirectToPage") {
                    	$(self.target).find("#disabledRedirectToPage").show();
                    	$li.find(".alertStatus").text("Rule Ignored").show();
                    }
                    
                    $li.show();
                    self.getRuleStatus($li, rule);
                    $ul.append($li);
                }

                var redirectKeyword = self.manager.response.responseHeader["redirect_keyword"];

                if (!$.isEmptyObject(redirectKeyword) &&
                        $.isNotBlank(redirectKeyword["replacement_keyword"])) {
                    $(self.target).find("#hasReplacement").show().find("#replacement").text(redirectKeyword["replacement_keyword"]);
                }

                $ul.find("li").removeClass("alt");
                $ul.find("li:even").addClass("alt");

                if ($.cookie('ar.' + GLOBAL_username) === "expand") {
                    self.showRules();
                } else {
                    self.hideRules();
                }

                $(self.target).find('#switcher').off().on({
                    click: function(e) {
                        if ($(self.target).find("#collapse").is(':visible')) {
                            $.cookie('ar.' + GLOBAL_username, "expand", {path: GLOBAL_contextPath});
                            self.showRules();
                        } else {
                            $.cookie('ar.' + GLOBAL_username, "collapse", {path: GLOBAL_contextPath});
                            self.hideRules();
                        }
                    }
                });
            }
        },
        showRules: function() {
            var self = this;
            $(self.target).find("#collapse").fadeOut("slow", function(foe) {
                $(self.target).find("#expand").slideDown("slow", function(sde) {
                    $(self.target).find("#switcherIcon").prop({
                        src: GLOBAL_contextPath + "/images/icon_expand.png"
                    });
                });
            });
        },
        hideRules: function() {
            var self = this;

            $(self.target).find("#expand").fadeOut("slow", function(foe) {
                $(self.target).find("#collapse").slideDown("slow", function(sde) {
                    $(self.target).find("#switcherIcon").prop({
                        src: GLOBAL_contextPath + "/images/icon_collapse.png"
                    });
                });
            });
        },
        getTemplate: function() {
            var output = '';

            output += '<div style="display:block;" class="fsize11 marT10 fDGray border">';
            output += '	<div id="expand" style="display:none">';
            output += '		<div id="activeRuleNoteHide" class="w655 marL20 info notification border fsize11 marB20 marT10">';
            output += ' 		Below are rules applied to your current search. You can toggle ON/OFF of each active rules to examine its effect on search results';
            output += ' 	</div>';
            output += '		<ul id="itemListing" class="mar16 marB10 marL20" >';
            output += '			<li id="itemPattern" class="items borderB padTB5 clearfix" style="display:none; width:690px">';
            output += '				<div class="floatL marT6">';
            output += '					<label class="select floatL w80 posRel topn3"><input type="checkbox" class="firerift-style-checkbox on-off ruleControl"></label>';
            output += '					<div class="clearB"></div>';
            output += '					<div class="alertStatus alert" style="display:none"></div>';
            output += '				</div>';
            output += '				<div class="floatR w300">';
            output += '					<div class="w300">';
            output += '						<label class="w30 preloader posRel floatR" style="display:none"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/ajax-loader-rect.gif") + '"></label>';
            output += '						<label class="ruleStatus w240 marT6 posRel floatL">';
            output += '							<span class="status fgray marL60"></span>';
            output += '							<span class="statusMode fsize11 forange padL5"></span>';
            output += '						</label>';
            output += '						<label class="lastPublished w240 fgray"></label>';
            output += '					</div>';
            output += '				</div>';
            output += '				<div class="floatR">';
            output += '					<div class="w230">';
            output += '						<label class="ruleType fbold w230 marT6"></label>';
            output += '					</div>';
            output += '					<div class="w230">';
            output += '						<label class="imageIcon w20 floatL posRel topn2"><img src="' + AjaxSolr.theme('getAbsoluteLoc', "images/icon_reviewContent2.png") + '" class="top2 posRel"></label>';
            output += '						<label class="name floatL w210"><span class="fbold"></span></label>';
            output += '					</div><div class="clearB"></div>';
            output += '				</div>';
            output += '			</li>';
            output += '		</ul>';
            output += '		<div id="hasReplacement" style="display:none">';
            output += '			<div class="alert w655 marL20 marB10">';
            output += '				Search results displayed are for';
            output += '				<span id="replacement" class="fbold fred"></span>';
            output += '			</div>';
            output += '		</div>';
            output += '		<div id="hasLoop" style="display:none">';
            output += '			<div class="alert w655 marL20 marB10">';
            output += '				Query Cleaning detected a circular redirection.';
            output += '			</div>';
            output += '		</div>';
            output += '		<div id="disabledRedirectToPage" style="display:none">';
            output += '			<div class="alert w655 marL20 marB10">';
            output += '				Query Cleaning will redirect to a page but page redirection option is disabled.';
            output += '			</div>';
            output += '		</div>';
            output += '	</div>';
            output += '	<div id="collapse" style="display:none">';
            output += '		<div id="activeRuleNoteShow" class="w655 marL20 info notification border fsize11 marB10 marT10">';
            output += ' 		Toggle this section to view all rules applied to current search';
            output += ' 	</div>';
            output += '	</div>';
            output += '</div>';

            output += '<a id="switcher" href="javascript:void(0);">';
            output += '	<div class="minW140 floatR borderB borderR borderL height23 posRel topn1 fbold fsize11 padT8 marL5" style="display:block; background: #fff; color:#329eea;">';
            output += '		<img id="switcherIcon" src="' + GLOBAL_contextPath + '/images/icon_expand.png" class="posRel marL20 marR3 marTn2 floatL">';
            output += '		<span id="switcherText" class="posRel marB6 floatL"></span>';
            output += '	</div>';
            output += '</a>';

            return $(output);
        }

    });

})(jQuery);
