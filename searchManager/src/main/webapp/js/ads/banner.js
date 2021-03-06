(function($) {

    BannerPage = {
        moduleName: "Banner",
        rulePage: 1,
        rulePageSize: 10,
        ruleItemPageSize: 2,
        noPreviewImage: GLOBAL_contextPath + "/images/nopreview.png",
        selectedRule: null,
        selectedRulePage: 1,
        selectedRuleItemPage: 1,
        selectedRuleItemTotal: 0,
        selectedRuleStatus: null,
        ruleFilterText: "",
        bannerInfo: null,
        lookupMessages: {
            successAddNewKeyword: "Successfully added keyword {0}",
            successAddBannerToKeyword: "Successfully added banner {0} to {1} with priority {2}",
            successUpdateBannerItem: "Successfully updated details of {0}",
            successDeleteBannerItem: "Successfully deleted {0}",
            successCopyBannerItem: "Successfully copied {0} to {1}"
        },
        calendarOpts: {
            defaultDate: GLOBAL_currentDate,
            changeMonth: true,
            changeYear: true,
            showOn: "both",
            buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
            buttonImageOnly: true,
            buttonText: "Date to simulate",
        },
        init: function() {
            var self = this;
            $("#ruleItemHolder, #addBannerBtn").hide();
            $("#titleText").text(self.moduleName);

            $.each(GLOBAL_storeAllowedBannerSizes, function(index, value) {
                $('#filterBySize').append($('<option>', {
                    value: value,
                    text: value,
                    selected: GLOBAL_storeDefaultBannerSize === value
                }));
            });

            self.getRuleList(1);
        },
        setRule: function(rule) {
            var self = this;
            self.selectedRule = rule;
            self.showRuleStatus();
        },
        getRuleList: function(page) {
            var self = this;

            $("#rulePanel").sidepanel({
                moduleName: self.moduleName,
                headerText: "Keyword",
                fieldId: "ruleId",
                fieldName: "ruleName",
                page: page,
                pageSize: self.rulePageSize,
                showAddButton: true,
                filterText: self.ruleFilterText,
                itemDataCallback: function(base, ruleName, page) {
                    self.rulePage = page;
                    self.ruleFilterText = ruleName;
                    self.selectedRulePage = page;
                    BannerRuleService.getAllRules(GLOBAL_storeId, ruleName, page, base.options.pageSize, {
                        callback: function(sr) {
                            var data = sr["data"];
                            base.populateList(data, ruleName);
                            base.addPaging(ruleName, page, data.totalSize);
                        },
                        preHook: function() {
                            base.prepareList();
                        }
                    });
                },
                itemOptionCallback: function(base, item) {

                    BannerRuleItemService.getTotalRuleItems(GLOBAL_storeId, item.model["ruleId"], {
                        callback: function(sr) {
                            var count = sr["data"];
                            if (count > 0)
                                item.ui.find("#itemLinkValue").html("(" + count + ")");

                            item.ui.find("#itemLinkValue").off().on({
                                click: function(e) {
                                    self.selectedRuleItemTotal = count;
                                    self.setRule(e.data.item.model);
                                }
                            }, {item: item});
                        },
                        preHook: function() {
                            item.ui.find("#itemLinkValue").hide();
                            item.ui.find("#itemLinkPreloader").show();
                        },
                        postHook: function() {
                            item.ui.find("#itemLinkValue").show();
                            item.ui.find("#itemLinkPreloader").hide();
                        }
                    });
                },
                itemNameCallback: function(base, item) {
                    self.setRule(item.model);
                },
                itemAddCallback: function(base, ruleName) {
                    BannerRuleService.addRule(ruleName, {
                        callback: function(sr) {
                            switch (sr["status"]) {
                                case 0:
                                	// TODO added banner already in service response
                                    jAlert($.formatText(self.lookupMessages.successAddNewKeyword, ruleName), "Banner Rule", function() {
                                        BannerRuleService.getRuleByName(GLOBAL_storeId, ruleName, {
                                            callback: function(sr) {
                                                self.setRule(sr["data"]);
                                            }
                                        });
                                    });
                                    break;
                                default:
                                    jAlert($.formatText(sr["errorMessage"]["message"], ruleName), "Banner Rule");
                            }

                            base.getList(ruleName, 1);

                        },
                        preHook: function(e) {
                            base.prepareList();
                        }
                    });
                }
            });
        },
        showRuleStatus: function() {
            var self = this;

            $("#ruleStatus").rulestatusbar({
                moduleName: self.moduleName,
                rule: self.selectedRule,
                ruleType: "Banner",
                enableVersion: true,
                authorizeRuleBackup: allowModify,
                authorizeSubmitForApproval: allowModify,
                postRestoreCallback: function(base, rule) {
                    base.api.destroy();
                    BannerRuleService.getRuleById(GLOBAL_storeId, self.selectedRule["ruleId"], {
                        callback: function(response) {
                            if (response.status == 0) {
                                self.setRule(response.data);
                            } else {
                                jAlert(response.errorMessage.message, "Error");
                            }
                        },
                        preHook: function() {
                            self.beforeShowRuleStatus();
                        }
                    });
                },
                afterSubmitForApprovalRequest: function(ruleStatus) {
                    self.showRuleStatus();
                },
                beforeRuleStatusRequest: function() {
                    self.getRuleList();
                    self.beforeShowRuleStatus();
                },
                afterRuleStatusRequest: function(ruleStatus) {
                    self.afterShowRuleStatus();
                    self.selectedRuleStatus = ruleStatus;
                    self.setRuleItemFilter();
                    self.getRuleItemList(1);
                }
            });
        },
        addRuleItemToggleHandler: function(ui, item) {
            var self = this;
            var toggle = "hide"; // $.cookie('banner.toggle' + $.formatAsId(item["memberId"]));

            ui.find("#bannerInfo").hide();

            ui.find("#toggleText, #toggleIcon").off().on({
                click: function(e) {
                    e.data.status = ("hide" === e.data.status) ? "show" : "hide"; // $.cookie('banner.toggle' + $.formatAsId(e.data.item["memberId"]));
                    self.setToggleStatus(e.data.ui, e.data.item, "hide" === e.data.status);
                }
            }, {ui: ui, item: item, status: "show"});

            self.setToggleStatus(ui, item, "show".toLowerCase() === toggle);
        },
        addImageAliasRestriction: function(ui, item) {
            var self = this;

            ui.find(".imageAlias").prop({
                readonly: true,
                disabled: true,
            });
        },
        addImagePathRestriction: function(ui, item) {
            var self = this;

            ui.find("#imagePath").prop({
                readonly: true,
                disabled: true,
            });
        },
        setToggleStatus: function(ui, item, show) {
            var self = this;

            if (show) {
                ui.find("#toggleIcon").removeClass("ico_plus").addClass("ico_minus").end()
                        .find("#toggleText").text("Show Less");

                ui.find("#bannerInfo").slideDown("slow", function() {
                    //$.cookie('banner.toggle' + $.formatAsId(item["memberId"]), "show" ,{path:GLOBAL_contextPath});
                    self.addInputFieldListener(ui, item, item["linkPath"], ui.find("input#linkPath"), self.validateLinkPath);
                    self.addInputFieldListener(ui, item, item["priority"], ui.find("input#priority"));
                    self.addSetAliasHandler(ui, item);
                    self.addUpdateRuleItemHandler(ui, item);
                    self.addDeleteItemHandler(ui, item);

                    $(this).parents(".ruleItem").find("input, textarea").prop({
                        readonly: false,
                        disabled: false
                    }).end()
                            .find(".startDate, .endDate").datepicker("enable");

                    self.addImageAliasRestriction(ui, item);
                    self.addImagePathRestriction(ui, item);
                    self.addScheduleRestriction(ui, item);
                    self.addItemExpiredRestriction(ui, item);
                    self.addRuleStatusRestriction();
                });

            } else {
                ui.find("#toggleIcon").removeClass("ico_minus").addClass("ico_plus").end()
                        .find("#toggleText").text("Show More");

                ui.find("#bannerInfo").slideUp("slow", function() {
                    //$.cookie('banner.toggle' + $.formatAsId(item["memberId"]), "hide" ,{path:GLOBAL_contextPath});
                    // all element readonly and disabled regardless of schedule, rule status, and expiration
                    $(this).parents(".ruleItem").find("input, textarea").prop({
                        readonly: true,
                        disabled: true
                    }).end().find(".startDate, .endDate").datepicker("disable");
                });
            }
        },
        setRuleItemFilter: function(value) {
            var self = this;
            var filter = $.isNotBlank(value) ? value : $.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]));

            if ($.isNotBlank(filter)) {
                $("#itemFilter").val(filter);
            } else {
                $.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]), "all", {path: GLOBAL_contextPath});
                $("#itemFilter").val("all");
            }

            $("#filterByDate").datepicker("destroy").hide();

            if ($("#itemFilter").val() === "date") {
                $("#filterByDate").show().datepicker($.extend({}, self.calendarOpts, {
                    onClose: function(selectedText) {
                        self.getRuleItemList(1);
                    }
                })).val($.datepicker.formatDate("mm/dd/yy", GLOBAL_currentDate));
            }

            $("#filterBySize").off().on({
                change: function(e) {
                    self.getRuleItemList(1);
                }
            }).show();

            $("#itemFilter").off().on({
                change: function(e) {
                    $.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]), $(this).val(), {path: GLOBAL_contextPath});

                    $("#filterByDate").datepicker("destroy").hide();

                    if ($(this).val() === "date") {
                        $("#filterByDate").datepicker($.extend({}, self.calendarOpts, {
                            onClose: function(selectedText) {
                                self.getRuleItemList(1);
                            }
                        })).val($.datepicker.formatDate("mm/dd/yy", GLOBAL_currentDate)).show();
                    }
                    self.getRuleItemList(1);
                }
            });
        },
        getRuleItemFilter: function() {
            var self = this;
            return $.cookie('banner.filter' + $.formatAsId(self.selectedRule["ruleId"]));
        },
        getRuleItemList: function(page) {
            var self = this;
            var rule = self.selectedRule;
            self.selectedRuleItemPage = page;
            $(".ruleItem:not(#ruleItemPattern)").remove();
            $("#ruleItemHolder").hide();

            $("#keywordStatIcon").statbox({
                itemDataCallback: function(startDate, endDate, aggregate) {
                    var base = this;
                    aggregate = aggregate == null ? false : aggregate;
                    BannerRuleService.getStatsByKeyword(GLOBAL_storeId, rule["ruleName"], startDate, endDate, aggregate, {
                        callback: function(sr) {
                            base.populateList.call(base, sr["data"], "keyword");
                            base.reposition();
                        }
                    });
                },
                itemImagePathCallback: function(url) {
                    var u = this;
                   ImagePathService.getImagePath(GLOBAL_storeId, url, {
                        callback: function(sr) {
                            var imagePath = sr["data"];
                            if (imagePath) {
                                u.find(".itemName").text(imagePath["alias"]);
                            }
                            base.reposition();
                        }
                    });
                }
            });

            BannerRuleItemService.getRuleItemsByFilter(GLOBAL_storeId, rule["ruleId"], self.getRuleItemFilter(), $("#filterByDate").val(), $("#filterBySize").val(), page, self.ruleItemPageSize, {
                callback: function(sr) {
                    var recordSet = sr["data"];

                    if (recordSet && recordSet["totalSize"] > 0) {
                        $("#ruleItemHolder").show();
                        $("#ruleItemPagingTop").paginate({
                            type: 'short',
                            currentPage: page,
                            pageSize: self.ruleItemPageSize,
                            pageStyle: "style2",
                            totalItem: recordSet["totalSize"],
                            callbackText: function(itemStart, itemEnd, itemTotal) {
                                var selectedText = $.trim($("#itemFilter").val()) !== "all" ? " " + $("#itemFilter option:selected").text() : "";
                                selectedText = $.trim($("#itemFilter").val()) !== "date" ? selectedText : " " + $("#filterByDate").val();
                                selectedText = selectedText + " " + $("#filterBySize").val();
                                if ($("#itemFilter").val() === "all")
                                    self.selectedRuleItemTotal = itemTotal;

                                return itemStart + ' to ' + itemEnd + ' of ' + itemTotal + selectedText + " Items";
                            },
                            pageLinkCallback: function(e) {
                                self.getRuleItemList(e.data.page);
                            },
                            nextLinkCallback: function(e) {
                                self.getRuleItemList(e.data.page + 1);
                            },
                            prevLinkCallback: function(e) {
                                self.getRuleItemList(e.data.page - 1);
                            },
                            firstLinkCallback: function(e) {
                                self.getRuleItemList(1);
                            },
                            lastLinkCallback: function(e) {
                                self.getRuleItemList(e.data.totalPages);
                            }
                        });
                    }

                    self.populateRuleItem(recordSet);
                },
                preHook: function(e) {
                    $(".ruleItem:not(#ruleItemPattern)").remove();
                    $("#ruleItemPagingTop").empty();
                }
            });
        },
        addRuleStatusRestriction: function() {
            var self = this;

            self.addRuleItemHandler();

            if (self.selectedRuleStatus["locked"] || !allowModify) {
                $("#addBannerBtn, .setAliasBtn").hide();

                $(".ruleItem").find("input, textarea").prop({
                    readonly: true,
                    disabled: true
                }).end()
                        .find(".startDate, .endDate").datepicker('disable');
            }
        },
        populateRuleItem: function(rs) {
            var self = this;
            var $iHolder = $("#ruleItemHolder");
            var $iPattern = $iHolder.find("#ruleItemPattern").hide();

            if (rs && rs.list && rs.list.length) {
                for (var i = 0; i < rs.list.length; i++) {
                    var ui = $iPattern.clone();
                    var item = rs["list"][i];
                    ui.prop({
                        id: "ruleItem_" + item["memberId"]
                    }).addClass(i + 1 == rs.list.length ? "last" : "").appendTo($iHolder).show();

                    ui.find("input#linkPath").attr("data-valid", true);
                    self.populateRuleItemFields(ui, item);
                }
            } else {
                self.addRuleStatusRestriction();
            }
        },
        populateRuleItemFields: function(ui, item) {
            var self = this;

            self.previewImage(ui, item, item["imagePath"]["path"]);

            ui
                    .find("#imageTitle").text(item["imagePath"]["alias"]).end()
                    .find("#priority").val(item["priority"]).end()
                    .find("#startDate").val($.toStoreFormat(item["startDate"],GLOBAL_storeDateFormat)).end()
                    .find("#endDate").val($.toStoreFormat(item["endDate"],GLOBAL_storeDateFormat)).end()

                    .find("#imagePath").val(item["imagePath"]["path"]).prop({
                readonly: true,
                disabled: true,
            }).end()
                    .find("#imageAlias").val(item["imagePath"]["alias"]).prop({
                id: item["imagePath"]["id"],
                readonly: true,
                disabled: true,
            }).end()
                    .find("#imageAlt").val(item["imageAlt"]).end()
                    .find("#linkPath").val(item["linkPath"]).end()
                    .find("#description").val(item["description"]).end()
                    .find("#temporaryDisable").prop({
                checked: item["disabled"] == true
            }).end()
                    .find("#openNewWindow").prop({
                checked: item["openNewWindow"] == true
            }).end()

                    // Select a date range, datepicker issue on multiple id even with scoping
                    .find("#startDate").prop({id: "startDate_" + item["memberId"]}).datepicker({
                minDate: GLOBAL_currentDate,
                defaultDate: GLOBAL_currentDate,
                changeMonth: true,
                changeYear: true,
                showOn: "both",
                buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
                buttonImageOnly: true,
                buttonText: "Select start date",
                onClose: function(selectedDate) {
                    ui.find("#endDate_" + item["memberId"]).datepicker("option", "minDate", selectedDate);
                }
            }).end()

                    .find("#endDate").prop({id: "endDate_" + item["memberId"]}).datepicker({
                minDate: ui.find("#startDate_" + item["memberId"]).datepicker("getDate"),
                defaultDate: GLOBAL_currentDate,
                changeMonth: true,
                changeYear: true,
                showOn: "both",
                buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
                buttonImageOnly: true,
                buttonText: "Select end date",
                onClose: function(selectedDate) {
                    if (!ui.find("#startDate_" + item["memberId"]).datepicker("isDisabled")) {
                        ui.find("#startDate_" + item["memberId"]).datepicker("option", "maxDate", selectedDate);
                    }
                }
            });

            self.registerEventListener(ui, item);
        },
        registerEventListener: function(ui, item) {
            var self = this;

            self.addDurationHandler(ui, item);
            self.addCopyToHandler(ui, item);
            self.updateTotalLinkedKeyword(ui, item);
            self.addShowKeywordHandler(ui, item);
            self.addItemAuditHandler(ui, item);
            self.addItemStatisticHandler(ui, item);
            self.addLastUpdateHandler(ui, item);
            self.addItemCommentHandler(ui, item);
            self.addRuleItemToggleHandler(ui, item);
            self.addDeleteAllItemHandler();
            self.addDownloadRuleHandler(ui, item);
            self.addRuleStatusRestriction();
        },
        addItemStatisticHandler: function(ui, item) {
            var self = this;

            ui.find("#itemStatIcon").statbox({
                rule: self.selectedRule,
                ruleItem: item,
                itemDataCallback: function(startDate, endDate) {
                    var base = this;
                    BannerRuleService.getStatsByMemberId(GLOBAL_storeId, item["memberId"], startDate, endDate, {
                        callback: function(sr) {
                            base.populateList.call(base, sr["data"], "memberId");
                            base.reposition();
                        }
                    });
                },
                itemScheduleCallback: function(ruleId, memberId) {
                    var u = this;
                    BannerRuleItemService.getRuleItemByMemberId(GLOBAL_storeId, ruleId, memberId, {
                        callback: function(sr) {
                            var rItem = sr["data"];
                            if (rItem) {
                                u.find(".itemSchedule").text($.toStoreFormat(rItem["startDate"],GLOBAL_storeDateFormat) + '-' + $.toStoreFormat(rItem["endDate"],GLOBAL_storeDateFormat));
                            }
                            base.reposition();
                        }
                    });
                }
            });
        },
        addItemExpiredRestriction: function(ui, item) {
            var self = this;

            if (item["expired"]) {
                ui.find("input, textarea").prop({
                    readonly: true,
                    disabled: true
                }).end()
                        .find(".startDate, .endDate").datepicker("disable");
            }
        },
        addDurationHandler: function(ui, item) {
            var self = this;
            var color = "orange";
            var durationText = "Not Yet Started";

            if (!item["expired"] && item["started"]) {
                color = "green";
                durationText = item["daysLeft"];
            } else if (item["expired"]) {
                color = "red";
                durationText = "Has Expired";
            }

            ui.find("#daysLeft").text(durationText).css({
                color: color
            });
        },
        addDeleteAllItemHandler: function() {
            var self = this;

            $('#deleteAllItemIcon').off().on({
                click: function(e) {
                    if (e.data.locked)
                        return;
                    jConfirm("Delete all " + $("#filterBySize").val() + " in keyword " + self.selectedRule["ruleName"] + "?", self.moduleName, function(result) {
                        if (result) {
                            BannerRuleItemService.deleteRuleItemsByImageSize(GLOBAL_storeId, self.selectedRule["ruleId"], $("#filterBySize").val(), {
                                callback: function(e) {
                                    self.getRuleItemList(1);
                                },
                                postHook: function(e) {
                                    self.getRuleList(self.selectedRulePage);
                                }
                            });
                        }
                    });
                },
                mouseenter: showHoverInfo
            }, {locked: self.selectedRuleStatus["locked"] || !allowModify});
        },
        addLastUpdateHandler: function(ui, item) {
            var lastModifiedDate = $.isBlank(
            		$.toStoreFormat(item["lastModifiedDate"])) ? 
            				$.toStoreFormat(item["createdDate"]) : 
            					$.toStoreFormat(item["lastModifiedDate"]);
            var lastModifiedBy = $.isBlank(item["lastModifiedBy"]) ? item["createdBy"] : item["lastModifiedBy"];

            ui.find('#lastModifiedIcon').off().on({
                mouseenter: showLastModified
            }, {user: lastModifiedBy, date: lastModifiedDate});
        },
        addScheduleRestriction: function(ui, item) {
            var self = this;
            // Disable when rule item has started
            ui.find(".startDate").datepicker(item["started"] ? 'disable' : 'enable');
        },
        addInputFieldListener: function(ui, item, currValue, input, callback) {
            var self = this;

            input.off().on({
                mouseenter: showHoverInfo,
                focusin: function(e) {
                    if (e.data.locked)
                        return

                    if ($.trim(currValue) === $(e.currentTarget).val()) {
                        $(e.currentTarget).val("");
                    }
                },
                mouseleave: function(e) {
                    $(e.currentTarget).triggerHandler("focusout");
                },
                focusout: function(e) {
                    if (e.data.locked)
                        return;

                    if ($.isNotBlank($(e.currentTarget).val()) &&
                            currValue !== $(e.currentTarget).val() &&
                            e.data.valueWhenRequestSent !== $(e.currentTarget).val()
                            ) {
                        e.data.valueWhenRequestSent = $(e.currentTarget).val();
                        if (callback)
                            callback.call(self, e.data.ui, e.data.item, $(e.currentTarget).val());
                    }

                    if ($.isBlank($(e.currentTarget).val())) {
                        e.data.valueWhenRequestSent = "";
                        $(e.currentTarget).val(currValue);
                        if (callback)
                            callback.call(self, e.data.ui, e.data.item, $(e.currentTarget).val());
                    }
                }
            }, {ui: ui, item: item, locked: self.selectedRuleStatus["locked"] || !allowModify, valueWhenRequestSent: ""});

        },
        getImagePath: function(ui, item, imagePath) {
            var self = this;

            ui.find("#imageTitle").text(item["imagePath"]["alias"]).end()
                    .find(".imageAlias").val(item["imagePath"]["alias"]);

            self.addSetAliasHandler(ui, item);
        },
        validateLinkPath: function(ui, item, linkPath) {
            var validURL = false;
            ui.find("#linkPath").attr("data-valid", false);

            if ($.isNotBlank(linkPath)) {
                if (!$.startsWith(linkPath, "//")) {
                    jAlert("Link path value must start with //", "Banner");
                    return;
                }

                var url = GLOBAL_storeDefaultBannerLinkPathProtocol + ':' + linkPath;

                if (!$.isValidURL(url)) {
                    jAlert("Please specify a valid link path", "Banner");
                    return;
                }

                var $a = $('<a/>');
                $a.attr("href", url);
                var hostname = $a[0]["hostname"];

                for (var i = 0; i < GLOBAL_storeDomains.length; i++) {
                    if ($.isNotBlank(GLOBAL_storeDomains[i]) && $.endsWith(hostname, GLOBAL_storeDomains[i])) {
                        var hostnamePrefix = hostname.replace(GLOBAL_storeDomains[i], '');
                        validURL = validURL || $.isBlank(hostnamePrefix);

                        if ($.isNotBlank(hostnamePrefix) && hostnamePrefix !== hostname) {
                            validURL = validURL || /([A-Z0-9]*\.){0,1}/i.test(hostnamePrefix);
                        }
                    }
                }

                ui.find("#linkPath").attr("data-valid", validURL);

                if (!validURL) {
                    ui.find("#linkPath").attr("data-valid", "domain");
                    jAlert("Only the following domain are allowed value in link path: " + GLOBAL_storeDomains.join(','), "Banner");
                }

            } else {
                jAlert("Please specify a link path", "Banner");
            }
        },
        previewImage: function(ui, item, imagePath) {
            var self = this;
            var $previewHolder = ui.find("#preview");

            if ($.isBlank(imagePath)) {
                imagePath = self.noPreviewImage;
            }

            $previewHolder.find("img#imagePreview").attr("src", imagePath).off().on({
                error: function() {
                    $(this).unbind("error").attr("src", self.noPreviewImage);
                }
            });

            self.getImagePath(ui, item, imagePath);
        },
        addSetAliasHandler: function(ui, item) {
            var self = this;

            ui.find("#setAliasBtn").hide();
            if ($.iequals(item["imagePath"]["path"], ui.find("#imagePath").val())) {
                ui.find("#setAliasBtn").off().on({
                    click: function(e) {
                        if (e.data.locked)
                            return;

                        var btnSetText = "Set Alias";
                        var btnCancelText = "Cancel";
                        var setAlias = $(e.currentTarget).find("#setAliasText").text() === btnSetText;

                        e.data.ui.find(".imageAlias").prop({
                            readonly: !setAlias,
                            disabled: !setAlias
                        }).val(
                                !setAlias ? e.data.item["imagePath"]["alias"] : ""
                                );

                        $(e.currentTarget).find("#setAliasText").text(
                                setAlias ? btnCancelText : btnSetText
                                );
                    },
                    mouseenter: showHoverInfo
                }, {ui: ui, item: item, locked: self.selectedRuleStatus['locked'] || !allowModify || item["expired"]}).show();
            }
        },
        addCopyToHandler: function(ui, item) {
            var self = this;

            ui.find("#copyToBtn").addbanner({
                id: 'copybanner',
                rule: self.selectedRule,
                ruleItem: item,
                mode: 'copy',
                isPopup: true,
                addBannerCallback: function(base, e) {
                    var params = e.data;

                    var mapParams = {
                        "ruleId": params["ruleId"],
                        "ruleName": params["ruleName"],
                        "priority": params["priority"],
                        "startDate": params["startDate"],
                        "endDate": params["endDate"],
                        "imagePathId": params["imagePathId"],
                        "imagePath": params["imagePath"],
                        "imageAlias": params["imageAlias"],
                        "imageAlt": params["imageAlt"],
                        "linkPath": params["linkPath"],
                        "description": params["description"],
                        "keywords": params["keywords"],
                        "disable": params["disable"],
                        "imageSize": params["imageSize"],
                        "openNewWindow": params["openNewWindow"]
                    };

                    BannerRuleService.copyToRule(GLOBAL_storeId, params["keywords"], mapParams, {
                        callback: function(sr) {
                            var keyList = sr["data"];

                            if (keyList && keyList.length > 0) {
                                jAlert($.formatText(self.lookupMessages.successCopyBannerItem, base.options.ruleItem["imagePath"]["alias"], keyList.join(',')), "Banner Rule");
                                self.getRuleList(1);
                            } else {
                                jAlert($.formatText(sr["errorMessage"]["message"], base.options.ruleItem["imagePath"]["alias"]), "Banner Rule");
                            }
                        },
                        preHook: function() {
                            e.data.base.api.hide();
                        }
                    });
                }
            });
        },
        updateTotalLinkedKeyword: function(ui, item) {
            var self = this;
            var count = 1;

            BannerRuleService.getTotalRulesByImageId(GLOBAL_storeId, item["imagePath"]["id"], item["imagePath"]["alias"], {
                callback: function(sr) {
                    var total = sr["data"];
                    if ($.isNumeric(total) && total > 1) {
                        count = total;
                    }
                },
                preHook: function(e) {
                    ui.find("#keywordCount").text(count);
                },
                postHook: function(e) {
                    ui.find("#keywordCount").text(count);
                }
            });
        },
        addShowKeywordHandler: function(ui, item) {
            var self = this;

            ui.find("#keywordBtn").listbox({
                title: "Linked Keywords",
                emptyText: "No linked keywords",
                locked: self.selectedRuleStatus["locked"] || !allowModify,
                rule: self.selectedRule,
                ruleItem: item,
                page: 1,
                pageSize: 5,
                itemDataCallback: function() {
                    var base = this;
                    var baseItem = base.options.ruleItem;
                    var page = base.options.page;
                    var pageSize = base.options.pageSize;

                    BannerRuleItemService.getRuleItemsByImageId(GLOBAL_storeId, baseItem["imagePath"]["id"], page, pageSize, {
                        callback: function(sr) {
                            var recordSet = sr["data"];
                            base.populateList(recordSet);
                            base.addPaging(page, recordSet["totalSize"]);
                        },
                        preHook: function(e) {
                            base.prepareList();
                        },
                        postHook: function(e) {
                            base.reposition();
                        }
                    });
                },
                itemDeleteCallback: function(baseItem) {
                    var base = this;
                    //var baseItem = base.options.ruleItem;

                    BannerRuleItemService.deleteRuleItemByMemberId(GLOBAL_storeId, baseItem["rule"]["ruleId"], baseItem["memberId"], baseItem["imagePath"]["alias"], baseItem["imagePath"]["size"], {
                        callback: function(e) {
                            base.getList(1);
                        },
                        preHook: function(e) {
                            base.prepareList();
                        },
                        postHook: function(e) {
                            self.updateTotalLinkedKeyword(ui, item);
                            self.getRuleList(self.selectedRuleItemPage);
                        }
                    });
                },
                itemRuleStatusCallback: function(ui, item) {
                    var base = this;

                    DeploymentServiceJS.getRuleStatus(GLOBAL_storeId, base.options.ruleType, item["rule"]["ruleId"], {
                        callback: function(data) {
                            ui.find('.itemStatus').text(getRuleNameSubTextStatus(data));
                            if (!data["locked"] && allowModify) {

                                ui.find(".deleteIcon").find("img").prop({
                                    src: GLOBAL_contextPath + "/images/icon_delete2.png"
                                });

                                ui.find(".deleteIcon").off().on({
                                    click: function(e) {
                                        jConfirm("Delete " + e.data.item["imagePath"]["alias"] + " in " + e.data.item["rule"]["ruleName"] + "?", "Linked Keyword", function(result) {
                                            if (result)
                                                e.data.base.options.itemDeleteCallback.call(e.data.base, e.data.item);
                                        });
                                    }
                                }, {item: item, base: base});
                            }
                        }
                    });
                }
            });
        },
        addRuleItemHandler: function() {
            var self = this;

            $("#addBannerBtn").addbanner({
                id: 'addbanner',
                rule: self.selectedRule,
                ruleItem: null,
                mode: 'add',
                isPopup: true,
                addBannerCallback: function(base, e) {
                    var params = e.data;

                    var mapParams = {
                        "ruleId": params["ruleId"],
                        "ruleName": params["ruleName"],
                        "priority": params["priority"],
                        "startDate": params["startDate"],
                        "endDate": params["endDate"],
                        "imagePathId": params["imagePathId"],
                        "imagePath": params["imagePath"],
                        "imageAlias": params["imageAlias"],
                        "imageAlt": params["imageAlt"],
                        "linkPath": params["linkPath"],
                        "description": params["description"],
                        "disable": params["disable"],
                        "openNewWindow": params["openNewWindow"],
                        "imageSize": params["imageSize"]
                    };
                    ImagePathService.getImagePathByAlias(GLOBAL_storeId, params['imageAlias'], {
                    	callback: function(response) {
                    		var data = response.data;
                    		if(data) {
                    			if(data.path != params['imagePath']) {
                    				jAlert('This alias already exists in another image.', 'Banner Rule');
                    				return;
                    			}
                    		}
                    		
                    		BannerRuleItemService.addRuleItem(GLOBAL_storeId, mapParams, {
                                callback: function(sr) {
                                    switch (sr["status"]) {
                                        case 0:
                                            jAlert($.formatText(self.lookupMessages.successAddBannerToKeyword, params["imageAlias"], params["ruleName"], params["priority"]), "Banner Rule", function() {
                                                $("#itemFilter").val("all");
                                                self.getRuleItemList(1);
                                            });
                                            break;
                                        default:
                                            jAlert($.formatText(sr["errorMessage"]["message"], params["imageAlias"], params["ruleName"], params["priority"]), "Banner Rule");
                                    }
                                }
                            });
                    	}
                    }); 
                    
                    
                }
            }).show();
        },
        beforeShowRuleStatus: function() {
            var self = this;
            $("#preloader").show();
            $("#infographic, #ruleStatus, #ruleContent").hide();
            $("#titleText").text(self.moduleName);
            $("#titleHeader").empty();
        },
        afterShowRuleStatus: function() {
            var self = this;
            $("#preloader, #infographic").hide();
            $("#ruleStatus, #ruleContent").show();
            $("#titleText").text(self.moduleName + " for ");
            $("#titleHeader").text(self.selectedRule["ruleName"]);
        },
        addDeleteItemHandler: function(ui, item) {
            var self = this;

            ui.find("#deleteBtn").off().on({
                click: function(e) {
                    if (e.data.locked)
                        return;

                    jConfirm("Delete banner " + e.data.item["imagePath"]["alias"] + " from " + self.selectedRule["ruleName"] + "?", self.moduleName, function(result) {
                        if (result) {
                            BannerRuleItemService.deleteRuleItemByMemberId(GLOBAL_storeId, self.selectedRule["ruleId"], e.data.item["memberId"], e.data.item["imagePath"]["alias"], $("#filterBySize").val(), {
                                callback: function(sr) {
                                    switch (sr["status"]) {
                                        case 0:
                                            jAlert($.formatText(self.lookupMessages.successDeleteBannerItem, e.data.item["imagePath"]["alias"]), "Banner Rule", function() {
                                                self.getRuleItemList(1);
                                            });
                                            break;
                                        default:
                                            jAlert($.formatText(sr["errorMessage"]["message"], e.data.item["imagePath"]["alias"]), "Banner Rule");
                                    }
                                }
                            });
                        }
                    });
                },
                mouseenter: showHoverInfo
            }, {ui: ui, item: item, locked: self.selectedRuleStatus["locked"] || !allowModify || item["expired"]});
        },
        getUpdatedFields: function(ui, item) {
            var self = this;

            //Updatable fields
            var imagePathId = $.trim(ui.find(".imageAlias").prop("id"));
            var imagePath = $.trim(ui.find("#imagePath").val());
            var imageAlias = $.trim(ui.find(".imageAlias").val());
            var priority = $.trim(ui.find("#priority").val());
            var startDate = $.trim(ui.find(".startDate").val());
            var endDate = $.trim(ui.find(".endDate").val());
            var imageAlt = $.trim(ui.find("#imageAlt").val());
            var linkPath = $.trim(ui.find("#linkPath").val());
            var description = $.trim(ui.find("#description").val());
            var openNewWindow = ui.find("#openNewWindow").is(':checked');
            var disable = ui.find("#temporaryDisable").is(':checked');

            if ($.isNotBlank(imagePathId) && $.iequals(imagePathId, item["imagePath"]["id"]) &&
                    $.isNotBlank(imagePath) && $.iequals(imagePath, item["imagePath"]["path"]) &&
                    $.isNotBlank(imageAlias) && $.iequals(imageAlias, item["imagePath"]["alias"])) {
                //Do nothing
                imagePathId = imagePath = imageAlias = null;
            } else if ($.isNotBlank(imagePath) && $.iequals(imagePath, item["imagePath"]["path"]) &&
                    $.isNotBlank(imagePathId) && $.iequals(imagePathId, item["imagePath"]["id"])) {
                // update alias, provide image path id, new alias value
                imagePath = null;
            } else if ($.isNotBlank(imagePathId) && !$.iequals(imagePathId, item["imagePath"]["id"]) &&
                    $.isNotBlank(imagePath) && !$.iequals(imagePathId, item["imagePath"]["path"])) {
                // update to existing banner, hide set alias btn, pass only image path id
                imagePath = imageAlias = null;
            } else if ($.isBlank(imagePathId) &&
                    $.isNotBlank(imagePath) && !$.iequals(imagePathId, item["imagePath"]["path"])
                    ) {
                // update to new banner,  hide set alias btn, pass path and alias
                imagePathId = null;
            }

            var mapParams = {
                "imagePathId": imagePathId,
                "imagePath": imagePath,
                "imageAlias": imageAlias,
                "priority": !$.iequals(priority, $.trim(item["priority"])) ? priority : null,
                "startDate": !$.iequals(startDate, 
                		$.toStoreFormat(item["startDate"],GLOBAL_storeDateFormat)) ? startDate : null,
                "endDate": !$.iequals(endDate, 
                		$.toStoreFormat(item["endDate"],GLOBAL_storeDateFormat)) ? endDate : null,
                "imageAlt": !$.iequals(imageAlt, item["imageAlt"]) ? imageAlt : null,
                "linkPath": !$.iequals(linkPath, item["linkPath"]) ? linkPath : null,
                "description": !$.iequals(description, item["description"]) ? description : null,
                "disable": disable != item["disabled"] ? disable : null,
                "openNewWindow": openNewWindow != item["openNewWindow"] ? openNewWindow : null,
            };

            return mapParams;
        },
        addUpdateRuleItemHandler: function(ui, item) {
            var self = this;

            ui.find("#updateBtn").off().on({
                click: function(e) {
                    if (e.data.locked)
                        return;

                    var dirtyCount = 0;

                    //get all fields value
                    var imagePath = e.data.ui.find("#imagePath").val();
                    var imageAlias = e.data.ui.find(".imageAlias").val();
                    var priority = e.data.ui.find("#priority").val();
                    var startDate = e.data.ui.find(".startDate").val();
                    var endDate = e.data.ui.find(".endDate").val();
                    var imageAlt = e.data.ui.find("#imageAlt").val();
                    var linkPath = e.data.ui.find("#linkPath").val().replace(/.*?:\/\/(www\.)?/gi, "");
                    var description = e.data.ui.find("#description").val();
                    var isValidURL = e.data.ui.find("#linkPath").attr("data-valid") === "true";
                    var isRestrictDomain = e.data.ui.find("#linkPath").attr("data-valid") === "domain";
                    var params = self.getUpdatedFields(e.data.ui, e.data.item);

                    $.each(params, function(i) {
                        dirtyCount += params[i] != null ? 1 : 0;
                    });

                    if (dirtyCount == 0) {
                        jAlert("Nothing to update", "Banner");
                    } else if ($.isBlank(priority) || !$.isNumeric(priority) || priority.indexOf(".") >= 0) {
                        jAlert("Priority is required and must be a number", "Banner");
                    } else if (priority > self.selectedRuleItemTotal) {
                        jAlert("Maximum value for priority is " + self.selectedRuleItemTotal, "Banner");
                    } else if ($.isBlank(imagePath)) {
                        jAlert("Image path is required.", "Banner");
                    } else if (!$.startsWith(imagePath, "//") && !$.isValidURL(imagePath)) {
                        jAlert("Please specify a valid image path.", "Banner");
                    } else if ($.isBlank(imageAlias)) {
                        jAlert("Image alias is required.", "Banner");
                    } else if (!isXSSSafe(imageAlias)) {
                        jAlert("Invalid image alias. HTML/XSS is not allowed.", "Banner");
                    } else if ($.isBlank(imageAlt)) {
                        jAlert("Image alt is required.", "Banner");
                    } else if (!isXSSSafe(imageAlt)) {
                        jAlert("Image alt contains XSS.", "Banner");
                    } else if ($.isBlank(linkPath)) {
                        jAlert("Link path is required.", "Banner");
                    } else if (isRestrictDomain) {
                        jAlert("Only the following domain are allowed value in link path: " + GLOBAL_storeDomains.join(','), "Banner");
                    } else if (!isValidURL) {
                        jAlert("Please specify a valid link path.", "Banner");
                    } else if ($.isBlank(startDate) || !$.isDate(startDate)) {
                        jAlert("Please provide a valid start date", "Banner");
                    } else if ($.isBlank(endDate) || !$.isDate(endDate)) {
                        jAlert("Please provide a valid end date", "Banner");
                    } else if (!validateDescription("Banner", description, 1, 150)) {
                        // error alert in function	
                    } else {
                        jConfirm("Update " + e.data.item["imagePath"]["alias"] + "?", self.moduleName, function(result) {
                            if (result) {
                                // Add fixed params
                                params["ruleId"] = self.selectedRule["ruleId"];
                                params["ruleName"] = self.selectedRule["ruleName"];
                                params["memberId"] = e.data.item["memberId"];
                                params["imageSize"] = $("#filterBySize > option:selected").val();
                                
                                ImagePathService.getImagePathByAlias(GLOBAL_storeId, imageAlias, {
                                	callback: function(response) {
                                		var data = response.data;
                                		if(data) {
                                			if(data.path != imagePath) {
                                				jAlert('This alias already exists in another image.', 'Banner Rule');
                                				return;
                                			}
                                		}
                                
		                                BannerRuleItemService.updateRuleItem(GLOBAL_storeId, params, {
		                                    callback: function(sr) {
		                                        switch (sr["status"]) {
		                                            case 0:
		                                                jAlert($.formatText(self.lookupMessages.successUpdateBannerItem, e.data.item["imagePath"]["alias"]), "Banner Rule", function() {
		                                                    self.getRuleItemList(self.selectedRuleItemPage);
		                                                });
		                                                break;
		                                            default:
		                                                jAlert($.formatText(sr["errorMessage"]["message"], e.data.item["imagePath"]["alias"]), "Banner Rule");
		                                        }
		                                    }
		                                });
                                }});
                            }
                        });
                    }
                },
                mouseenter: showHoverInfo
            }, {ui: ui, item: item, locked: self.selectedRuleStatus["locked"] || !allowModify || item["expired"]});
        },
        addItemCommentHandler: function(ui, item) {
            var self = this;
            ui.find("#commentIcon").off().on({
                click: function(e) {
                    $(e.currentTarget).comment({
                        showAddComment: true,
                        locked: e.data.locked,
                        itemDataCallback: function(base, page) {
                            if (e.data) {
                                CommentServiceJS.getComment(self.moduleName, e.data.item["memberId"], base.options.page, base.options.pageSize, {
                                    callback: function(data) {
                                        var total = data.totalSize;
                                        base.populateList(data);
                                        base.addPaging(base.options.page, total);
                                    },
                                    preHook: function() {
                                        base.prepareList();
                                    }
                                });
                            }
                        },
                        itemAddComment: function(base, comment) {
                            CommentServiceJS.addRuleItemComment(self.moduleName, e.data.item["memberId"], comment, {
                                callback: function(data) {
                                    base.getList(base.options.page);
                                },
                                preHook: function() {
                                    base.prepareList();
                                }
                            });
                        }
                    });
                }
            }, {item: item, locked: self.selectedRuleStatus["locked"] || !allowModify || item["expired"]});
        },
        addItemAuditHandler: function(ui, item) {
            var self = this;
            ui.find('#auditIcon').off().on({
                click: function(e) {
                    $(e.currentTarget).viewaudit({
                        ruleItem: item,
                        itemDataCallback: function(base, page) {
                            AuditServiceJS.getBannerItemTrail(self.selectedRule["ruleId"], base.options.ruleItem["memberId"], base.options.page, base.options.pageSize, {
                                callback: function(data) {
                                    var total = data.totalSize;
                                    base.populateList(data);
                                    base.addPaging(base.options.page, total);
                                },
                                preHook: function() {
                                    base.prepareList();
                                }
                            });
                        }
                    });
                }
            }, {ui: ui, item: item});
        },
        addDownloadRuleHandler: function() {
            var self = this;

            $("a#downloadRuleIcon").download({
                headerText: "Download " + self.moduleName,
                requestCallback: function(e) {
                    var params = new Array();
                    var url = document.location.pathname + "/xls";
                    var urlParams = "";
                    var count = 0;

                    params["id"] = self.selectedRule["ruleId"];
                    params["filename"] = e.data.filename;
                    params["type"] = e.data.type;
                    params["keyword"] = self.selectedRule["ruleName"];
                    params["clientTimezone"] = +new Date();

                    for (var key in params) {
                        if (count > 0)
                            urlParams += '&';
                        urlParams += (key + '=' + encodeURIComponent(params[key]));
                        count++;
                    }
                    ;

                    document.location.href = url + '?' + urlParams;
                }
            });
        }
    };

    $(document).ready(function() {
        BannerPage.init();
    });

})(jQuery);
