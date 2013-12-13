(function($) {

    var Import = {
        moduleName: "Import Rule",
        tabSelected: "",
        entityName: "",
        ruleEntityList: null,
        importTypeList: null,
        ruleStatusMap: new Array(),
        ruleTransferMap: new Object(),
        ruleTargetList: new Array(),
        pageSize: 10,
        defaultText: "Search Rule Name",
        defaultPage: 1,
        defaultKeywordFilter: null,
        defaultSortOrder: "PUBLISHED_DATE_DESC",
        defaultRuleFilterBy: "nonrejected",
        currentPage: 1,
        searchText: "",
        pubDateSort: null,
        expDateSort: null,
        ruleNameSort: null,
        activeSortOrder: null,
        ruleFilterBy: null,
        postMsg: function(data, pub) {
            var self = this;
            var msg_ = pub;
            var okmsg = '';

            if (!$.isEmptyObject(data)) {
                var importFail = '<ul class="mar0 padL30">';
                var importSuccessSubmitForApprovalFail = '<ul class="mar0 padL30">';
                var importSuccessPublishFail = '<ul class="mar0 padL30">';
                var importSuccess = '<ul class="mar0 padL30">';
                var rejectFail = '<ul class="mar0 padL30">';
                var rejectSuccess = '<ul class="mar0 padL30">';

                var importFailCount = 0;
                var importSuccessSubmitForApprovalFailCount = 0;
                var importSuccessPublishFailCount = 0;
                var importSuccessCount = 0;
                var rejectFailCount = 0;
                var rejectSuccessCount = 0;

                for (key in data) {
                    switch (data[key]) {
                        case 'import_fail':
                            importFail += '<li>' + key + "</li>";
                            importFailCount++;
                            break;
                        case 'import_success_submit_for_approval_fail':
                            importSuccessSubmitForApprovalFail += '<li>' + key + '</li>';
                            importSuccessSubmitForApprovalFailCount++;
                            break;
                        case 'import_success_publish_fail':
                            importSuccessPublishFail += '<li>' + key + "</li>";
                            importSuccessPublishFailCount++;
                            break;
                        case 'import_success':
                            importSuccess += '<li>' + key + "</li>";
                            importSuccessCount++;
                            break;
                        case 'reject_fail':
                            rejectFail += '<li>' + key + "</li>";
                            rejectFailCount++;
                            break;
                        case 'reject_success':
                            rejectSuccess += '<li>' + key + "</li>";
                            rejectSuccessCount++;
                            break;
                    }

                }

                if (importFailCount) {
                    okmsg += 'Failed to import the following rules:';
                    okmsg += importFail + '</ul>';
                }
                if (importSuccessSubmitForApprovalFailCount) {
                    okmsg += 'Failed to submit for approval the following imported rules:';
                    okmsg += importSuccessSubmitForApprovalFail + '</ul>';
                }
                if (importSuccessPublishFailCount) {
                    okmsg += 'Failed to auto-publish the following imported rules:';
                    okmsg += importSuccessPublishFail + '</ul>';
                }
                if (importSuccessCount) {
                    okmsg += 'Following rules were successfully imported:';
                    okmsg += importSuccess + '</ul>';
                }
                if (rejectFailCount) {
                    okmsg += 'Following rules were fail to import:';
                    okmsg += rejectFail + '</ul>';
                }
                if (rejectSuccessCount) {
                    okmsg += 'Following rules were successfully rejected:';
                    okmsg += rejectSuccess + '</ul>';
                }
            } else {
                okmsg = 'No rules were successfully imported and rejected.';
            }

            jAlert(okmsg, self.moduleName);
        },
        populateTabContent: function() {
            var self = this;

            $("#import").tabs("destroy").tabs({
                cookie: {
                    expires: 0
                },
                show: function(event, ui) {
                    if (ui.panel) {
                        $(".qtip:visible").qtip('hide');
                        self.tabSelected = ui.panel.id;
                        self.entityName = self.tabSelected.substring(0, self.tabSelected.length - 3);
                        self.initVariables();
                    }
                }
            });
        },
        initVariables: function() {
            var ctr = 0, max = 2, self = this;
            DeploymentServiceJS.getAllRuleStatus(self.entityName, {
                callback: function(rs) {
                    self.ruleStatusMap[self.entityName] = rs.list;
                    ctr++;
                },
                postHook: function() {
                    if (ctr == max)
                        self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, self.defaultRuleFilterBy);
                }
            });

            EnumUtilityServiceJS.getImportTypeList(hasPublishRule, {
                callback: function(data) {
                    self.importTypeList = data;
                    ctr++;
                },
                postHook: function() {
                    if (ctr == max)
                        self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, self.defaultRuleFilterBy);
                }
            });
        },
        prepareTabContent: function() {
            var self = this;
            var $selectedTab = $("#" + self.tabSelected);
            if (!$("div.circlePreloader").is(":visible")) {
                $('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>').prependTo($selectedTab);
            }
            $selectedTab.find('table.tblItems, div#actionBtn').hide();
            $selectedTab.find("div#ruleCount").html("");

            $selectedTab.find("div.searchBoxHolder, a#searchBtn").hide();
            $selectedTab.find("div#resultsTopPaging, div#resultsBottomPaging").empty();
            $selectedTab.find("a#downloadIcon").hide();
            $selectedTab.find("div#ruleFilterDiv").hide();
        },
        cleanUpTabContent: function() {
            $('div.circlePreloader').remove();
        },
        getRuleEntityList: function() {
            var self = this;
            EnumUtilityServiceJS.getRuleEntityList({
                callback: function(data) {
                    self.ruleEntityList = data;
                }
            });
        },
        getRuleType: function(ruleTypeId) {
            var self = this;
            if (self.ruleEntityList)
                return self.ruleEntityList[ruleTypeId];
            return "";
        },
        getSelectedImportAsRefId: function(value) {
            var self = this;
            var selectedImportAsRefId = [];
            var $selectedTab = $("#" + self.tabSelected);
            var selectedItems = self.getSelectedItems(value);

            for (var id in selectedItems) {
                var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
                selectedImportAsRefId.push($selectedTr.find("td#importAs").find("select#importAsSelect > option:selected").val());
            }

            return selectedImportAsRefId;
        },
        getSelectedImportType: function(value) {
            var self = this;
            var importType = [];
            var $selectedTab = $("#" + self.tabSelected);
            var selectedItems = self.getSelectedItems(value);

            for (var id in selectedItems) {
                var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
                importType.push($selectedTr.find("td#type > select#importTypeList > option:selected").text());
            }

            return importType;
        },
        checkSelectedImportAsName: function(value) {
            var self = this;
            var selectedNames = self.getSelectedRuleName(value);

            if (selectedNames == null || selectedNames.length == 0)
                return false;

            for (var i = 0; i < selectedNames.length; i++) {
                if ($.isBlank(selectedNames[i]) || selectedNames[i].length == 0) {
                    return false;
                }
            }

            return true;
        },
        hasDuplicateImportAsId: function(value) {

            var self = this;
            var selectedRuleId = new Array();
            var $selectedTab = $("#" + self.tabSelected);
            var selectedItems = self.getSelectedItems(value);

            for (var id in selectedItems) {
                var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
                var $importAsSelect = $selectedTr.find("td#importAs").find("select#importAsSelect > option:selected");
                var ruleId = $importAsSelect.val();
                if ($.inArray(ruleId, selectedRuleId) == -1) {
                    if (ruleId !== "0") {
                        selectedRuleId.push(ruleId);
                    }
                    ;
                } else {
                    return true;
                }
            }

            return false;
        },
        hasDuplicateImportAsName: function(value) {
            var self = this;
            var selectedRuleName = new Array();
            var $selectedTab = $("#" + self.tabSelected);
            var selectedItems = self.getSelectedItems(value);

            for (var id in selectedItems) {
                var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
                var ruleName = $selectedTr.find("td#importAs #replacement input#newName").val();

                if ($.inArray(ruleName.toLowerCase(), selectedRuleName) == -1) {
                    selectedRuleName.push(ruleName.toLowerCase());
                } else {
                    return true;
                }
            }

            return false;
        },
        getSelectedRuleName: function(value) {

            var self = this;
            var selectedRuleNames = [];
            var $selectedTab = $("#" + self.tabSelected);
            var selectedItems = self.getSelectedItems(value);

            for (var id in selectedItems) {
                var $selectedTr = $selectedTab.find("tr#ruleItem" + id);
                var ruleName = $selectedTr.find("td#importAs").find("input#newName").val();
                selectedRuleNames.push(ruleName);
            }

            return selectedRuleNames;
        },
        getSelectedItems: function(flag) {
            var self = this;
            var selectedItems = [];
            var $selectedTab = $("#" + self.tabSelected);

            if (flag == 'all') {
                $selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:not([readonly]):checked").each(function(index, value) {
                    selectedItems[$(this).attr("id")] = $(this).attr("name");
                });
            } else {
                $selectedTab.find("tr:not(#ruleItemPattern) td#select > input." + flag + "[type='checkbox']:not([readonly]):checked").each(function(index, value) {
                    selectedItems[$(this).attr("id")] = $(this).attr("name");
                });
            }

            return selectedItems;
        },
        getSelectedRefId: function(flag) {
            var self = this;
            var selectedRefIds = [];
            var $selectedTab = $("#" + self.tabSelected);

            if (flag == 'all') {
                $selectedTab.find("tr:not(#ruleItemPattern) td#select > input[type='checkbox']:not([readonly]):checked").each(function(index, value) {
                    selectedRefIds.push($(this).attr("value"));
                });
            } else {
                $selectedTab.find("tr:not(#ruleItemPattern) td#select > input." + flag + "[type='checkbox']:not([readonly]):checked").each(function(index, value) {
                    selectedRefIds.push($(this).attr("value"));
                });
            }

            return selectedRefIds;
        },
        getSelectedStatusId: function(value) {
            var self = this;
            var selectedStatusId = [];
            var selectedItems = self.getSelectedItems(value);

            for (var i in selectedItems) {
                selectedStatusId.push(selectedItems[i]);
            }

            return selectedStatusId;
        },
        addFiltersHandler: function(selectedTab, curPage, totalItem, keywordFilter, sortOrderFilter, ruleFilter) {
            var self = this;
            var $selectedTab = selectedTab;
            if (totalItem == 0) {
                $selectedTab.find("div#resultsTopPaging, div#resultsBottomPaging").empty();
                $selectedTab.find("#downloadIcon").hide();
            } else {
                $selectedTab.find("div.searchBoxHolder, a#searchBtn").show();

                if (self.tabSelected != 'didYouMeanTab') {
                    $selectedTab.find("a#downloadIcon").show();
                }
                $selectedTab.find("div#ruleFilterDiv").show();
                $selectedTab.find("#resultsTopPaging, #resultsBottomPaging").paginate({
                    currentPage: curPage,
                    pageSize: self.pageSize,
                    totalItem: totalItem,
                    callbackText: function(itemStart, itemEnd, itemTotal) {
                        return "Displaying " + itemStart + "-" + itemEnd + " of " + itemTotal + " Items";
                    },
                    pageLinkCallback: function(e) {
                        self.getImportList(e.data.page, keywordFilter, sortOrderFilter, ruleFilter);
                    },
                    nextLinkCallback: function(e) {
                        self.getImportList(e.data.page + 1, keywordFilter, sortOrderFilter, ruleFilter);
                    },
                    prevLinkCallback: function(e) {
                        self.getImportList(e.data.page - 1, keywordFilter, sortOrderFilter, ruleFilter);
                    },
                    firstLinkCallback: function(e) {
                        self.getImportList(self.defaultPage, keywordFilter, sortOrderFilter, ruleFilter);
                    },
                    lastLinkCallback: function(e) {
                        self.getImportList(e.data.totalPages, keywordFilter, sortOrderFilter, ruleFilter);
                    }
                });

                $selectedTab.find("img#publishDateSort, img#ruleNameSort, img#exportDateSort").off().on({
                    click: function(e) {
                        var sortOrder = null;
                        var state = false;

                        switch ($(e.currentTarget).attr("id")) {
                            case "ruleNameSort":
                                state = !$selectedTab.find("input#ruleNameInp").is(":checked");
                                sortOrder = state ? "RULE_NAME_DESC" : "RULE_NAME_ASC";
                                break;
                            case "publishDateSort":
                                state = !$selectedTab.find("input#pubDateInp").is(":checked");
                                sortOrder = state ? "PUBLISHED_DATE_DESC" : "PUBLISHED_DATE_ASC";
                                $selectedTab.find("input#pubDateInp").prop("checked", state); //check/uncheck ruleNameInp
                                //$selectedTab.find('.sortGroup:not(#pubDateInp)').prop('checked', false); //uncheck the other options in group
                                break;
                            case "exportDateSort":
                                state = !$selectedTab.find("input#expDateInp").is(":checked");
                                sortOrder = state ? "EXPORT_DATE_DESC" : "EXPORT_DATE_ASC";
                                $selectedTab.find("input#expDateInp").prop("checked", state); //check/uncheck ruleNameInp
                                //$selectedTab.find('.sortGroup:not(#expDateInp)').prop('checked', false); //uncheck the other options in group
                                break;
                            default:
                                $selectedTab.find('.sortGroup').prop('checked', false);
                                break;
                        }

                        self.getImportList(curPage, keywordFilter, sortOrder, ruleFilter);
                    }
                });

                $selectedTab.find("a#downloadIcon").download({
                    headerText: "Download " + self.moduleName,
                    requestCallback: function(e) {
                        var params = new Array();
                        var url = document.location.pathname + "/xls";
                        var urlParams = "";
                        var count = 0;
                        params["filename"] = e.data.filename;
                        params["type"] = e.data.type;
                        params["clientTimezone"] = +new Date();
                        params["ruleType"] = self.entityName;

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

            $selectedTab.find("select#ruleFilter").val(ruleFilter).on({
                change: function(e) {
                    self.getImportList(self.defaultPage, keywordFilter, self.defaultSortOrder, $(this).val());
                }
            });

            $selectedTab.find('input#keyword').off().on({
                focusin: function(e) {
                    if ($.trim($(e.currentTarget).val()).toLowerCase() === $.trim(self.defaultText).toLowerCase())
                        $(e.currentTarget).val("");
                },
                focusout: function(e) {
                    if ($.isBlank($(e.currentTarget).val()))
                        $(e.currentTarget).val(self.defaultText);
                },
                keydown: function(e) {
                    var code = (e.keyCode ? e.keyCode : e.which);
                    var keyword = $.trim($(e.target).val());

                    if (code == 13) {
                        if (keyword.toLowerCase() !== $.trim(self.defaultText).toLowerCase())
                            self.getImportList(self.defaultPage, keyword, self.defaultSortOrder, ruleFilter);
                        else
                            self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, ruleFilter);
                    }
                }
            }).val(self.defaultText);

            $selectedTab.find("a#searchBtn").off().on({
                click: function(e) {
                    var keyword = $.trim($selectedTab.find('input#keyword').val());

                    if (keyword.toLowerCase() !== $.trim(self.defaultText).toLowerCase())
                        self.getImportList(self.defaultPage, keyword, self.defaultSortOrder, ruleFilter);
                    else
                        self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, ruleFilter);
                }
            });
        },
        // not in used.
        /*			
         importHandler : function(){
         var self = this;
         var $selectedTab = $("#"+self.tabSelected);
         
         $selectedTab.find("a#okBtn, a#rejectBtn").on({
         click: function(evt){
         var comment = $.trim($selectedTab.find("#comment").val());
         
         if(self.getSelectedRefId('all').length==0){
         jAlert("Please select Import/Reject on a rule.", self.moduleName);
         }else if ($.isBlank(comment)){
         jAlert("Please add comment.", self.moduleName);
         }else if(!isXSSSafe(comment)){
         jAlert("Invalid comment. HTML/XSS is not allowed.", self.moduleName);
         }else{
         switch($(evt.currentTarget).attr("id")){
         case "okBtn":
         setTimeout(function() {
         if(self.hasDuplicateImportAsId('all')){	//check if all selected rules have ruleName value
         jAlert("Duplicate selected import as value. Please check selected rules to import.", self.moduleName);
         }else if(self.hasDuplicateImportAsName('all')){	//check if all selected rules have ruleName value
         jAlert("Duplicate selected import as new name. Please check selected rules to import.", self.moduleName);
         }else if(!self.checkSelectedImportAsName('all')){	//check if all selected rules have ruleName value
         jAlert("Import As name is required. Please check selected rules to import.", self.moduleName);
         }else{
         RuleTransferServiceJS.importRules(self.entityName, self.getSelectedRefId(), comment, self.getSelectedImportType(), self.getSelectedImportAsRefId(), self.getSelectedRuleName(), {
         callback: function(data) {									
         self.postMsg(data, 'imported');	
         self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, self.defaultRuleFilterBy);		
         },
         preHook:function(){ 
         self.prepareTabContent(); 
         }	
         });
         }
         }, 500 );
         break;
         case "rejectBtn": 
         RuleTransferServiceJS.unimportRules(self.entityName, self.getSelectedRefId(), comment, self.getSelectedStatusId(), {
         callback: function(data){
         self.postMsg(data, 'rejected');	
         self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, self.defaultRuleFilterBy);
         },
         preHook:function(){
         self.prepareTabContent(); 
         }
         });
         break;
         }
         }
         }
         });
         },
         */

        submitHandler: function() {
            var self = this;
            var $selectedTab = $("#" + self.tabSelected);

            $selectedTab.find("a#sbmtBtn").on({
                click: function(evt) {
                    var comment = $.trim($selectedTab.find("#comment").val());

                    if (self.getSelectedRefId('all').length == 0) {
                        jAlert("Please select Import/Reject on a rule.", self.moduleName);
                    } else if (!validateComment(self.moduleName, comment, 1, 250)) {
                        //error alert in function validateComment
                    } else {
                        var importedItems = self.getSelectedRefId('import');
                        var validImport = true;

                        if (importedItems.length > 0) {
                            if (!self.checkSelectedImportAsName('import')) {	//check if all selected rules have ruleName value
                                jAlert("Import As name is required. Please check selected rules to import.", self.moduleName);
                                validImport = false;
                            } else if (self.hasDuplicateImportAsId('import')) {	//check if all selected rules have ruleName value
                                jAlert("Duplicate selected import as value. Please check selected rules to import.", self.moduleName);
                                validImport = false;
                            } else if (self.hasDuplicateImportAsName('import')) {	//check if all selected rules have ruleName value
                                jAlert("Duplicate selected import as new name. Please check selected rules to import.", self.moduleName);
                                validImport = false;
                            } else {
                                validImport = true;
                            }
                        }

                        if (validImport) {
                            if (self.entityName.toLowerCase() === 'querycleaning') {
                                RedirectServiceJS.checkForRuleNameDuplicates(self.getSelectedImportAsRefId('import'), self.getSelectedRuleName('import'), {
                                    preHook: function() {
                                        self.hideData();
                                    },
                                    callback: function(data) {
                                        if ($.isEmptyObject(data)) {
                                            self.importRejectRules();
                                        } else {
                                            self.showData();
                                            jAlert("The following Query Cleaning Rule names already exist:<ul class='mar0 padL30'><li>" + data.join("</li><li>") + "</li></ul>", self.moduleName);
                                        }
                                    }
                                });
                            }
                            else if (self.entityName.toLowerCase() === 'rankingrule') {
                                RelevancyServiceJS.checkForRuleNameDuplicates(self.getSelectedImportAsRefId('import'), self.getSelectedRuleName('import'), {
                                    preHook: function() {
                                        self.hideData();
                                    },
                                    callback: function(data) {
                                        if ($.isEmptyObject(data)) {
                                            self.importRejectRules();
                                        } else {
                                            self.showData();
                                            jAlert("The following Ranking Rule names already exist:<ul class='mar0 padL30'><li> " + data.join("</li><li>") + "</li></ul>", self.moduleName);
                                        }
                                    }
                                });
                            }
                            else {
                                self.importRejectRules();
                            }
                        }
                    }
                },
            });
        },
        hideData: function() {
            var self = this;
            var $selectedTab = $("#" + self.tabSelected);
            if (!$("div.circlePreloader").is(":visible")) {
                $('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>').prependTo($selectedTab);
            }
            $selectedTab.find('table.tblItems, div#actionBtn').hide();
            $selectedTab.find("div#ruleCount").hide();
            $selectedTab.find("div.searchBoxHolder, a#searchBtn").hide();
            $selectedTab.find("div#resultsTopPaging, div#resultsBottomPaging").hide();
            $selectedTab.find("a#downloadIcon").hide();
            $selectedTab.find("div#ruleFilterDiv").hide();
        },
        showData: function() {
            var self = this;
            var $selectedTab = $("#" + self.tabSelected);
            $("div.circlePreloader").hide();
            $selectedTab.find('table.tblItems, div#actionBtn').show();
            $selectedTab.find("div#ruleCount").show();
            $selectedTab.find("div.searchBoxHolder, a#searchBtn").show();
            $selectedTab.find("div#resultsTopPaging, div#resultsBottomPaging").show();

            if (self.tabSelected != 'didYouMeanTab') {
                $selectedTab.find("a#downloadIcon").show();
            }
            $selectedTab.find("div#ruleFilterDiv").show();
        },
        getConfirmationMessage: function() {
            var self = this;
            var arrImportIds = self.getSelectedRefId('import');
            var arrRejectIds = self.getSelectedRefId('reject');
            var confirmationMessage = "";
            var $row = null;
            var rName = "";
            var iType = "";
            var iAs = "";

            if (arrImportIds.length > 0) {
                confirmationMessage += "Rules to be imported:";

                if (arrImportIds.length > 1) {
                    confirmationMessage += "<ol type='1' class='mar0 padL30'>";
                } else {
                    confirmationMessage += "<ul type='none' class='mar0 padL30'>";
                }
            }

            var $selectedTab = $("#" + self.tabSelected);
            for (var i = 0; i < arrImportIds.length; i++) {
                $row = $selectedTab.find("tr#ruleItem" + $.formatAsId(arrImportIds[i]));
                rName = $row.find("#ruleName").text();
                iType = $row.find("#importTypeList option:selected:eq(0)").text();
                iAs = $row.find("#newName").val();
                confirmationMessage += "<li>" + iType + ": " + rName + " &rarr; " + iAs + "</li>";
            }

            if (arrImportIds.length > 1) {
                confirmationMessage += "</ol>";
            } else if (arrImportIds.length > 0) {
                confirmationMessage += "</ul>";
            }

            if (arrRejectIds.length > 0) {
                confirmationMessage += (arrImportIds.length > 0 ? "<br>" : "") + "Rules to be rejected:";

                if (arrRejectIds.length > 1) {
                    confirmationMessage += "<ol type='1' class='mar0 padL30'>";
                } else {
                    confirmationMessage += "<ul type='none' class='mar0 padL30'>";
                }
            }
            for (var i = 0; i < arrRejectIds.length; i++) {
                $row = $("tr#ruleItem" + $.formatAsId(arrRejectIds[i]));
                confirmationMessage += "<li>" + $row.find("#ruleName").text() + "</li>";
            }

            if (arrRejectIds.length > 1) {
                confirmationMessage += "</ol>";
            } else if (arrRejectIds.length > 0) {
                confirmationMessage += "</ul>";
            }

            return confirmationMessage;
        },
        importRejectRules: function() {
            var self = this;
            var exception = false;
            var $selectedTab = $("#" + self.tabSelected);
            var comment = $.trim($selectedTab.find("#comment").val());

            jConfirm(self.getConfirmationMessage(), "Confirm Import", function(status) {
                if (status) {
                    RuleTransferServiceJS.importRejectRules(GLOBAL_storeId, GLOBAL_storeName , self.entityName, self.getSelectedRefId('import'), comment, self.getSelectedImportType('import'), self.getSelectedImportAsRefId('import'), self.getSelectedRuleName('import'),
                            self.getSelectedRefId('reject'), self.getSelectedStatusId('reject'), {
                        callback: function(data) {
                            self.postMsg(data, 'all');
                        },
                        preHook: function() {
                            self.hideData();
                        },
                        postHook: function() {
                            if (!exception) {
                                self.prepareTabContent();
                                self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, self.defaultRuleFilterBy);
                            }
                            else {
                                self.showData();
                            }
                            ;
                        },
                        exceptionHandler: function(message, exc) {
                            exception = true;
                            jAlert(message, "Import Rule");
                        }
                    });
                }
                else {
                    self.showData();
                }
            });
        },
        getPreTemplate: function(entityName, selectedType) {
            var template = '';

            switch (entityName.toLowerCase()) {
                case "facetsort":
                    template = '<div class="rulePreview w590 marB20">';
                    template += '	<div class="alert marB10">The rule below is pending for import. Please examine carefully the details</div>';
                    template += '	<label class="w110 floatL fbold">Rule Name:</label>';
                    template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
                    template += '	<div class="clearB"></div>';
                    template += '	<label class="w110 floatL fbold">Rule Type:</label>';
                    template += '	<label class="wAuto floatL" id="ruleType"></label>';
                    template += '	<div class="clearB"></div>';
                    template += '	<label class="w110 floatL marL20 fbold">Import Type:</label>';
                    template += '	<label class="wAuto floatL" id="importType">';
                    template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
                    template += '	</label>';
                    template += '	<div class="clearB"></div>';
                    template += '</div>';
                    break;
                default:	//template for elevate, exclude, demote, redirect and relevancy rule
                    template = '<div class="rulePreview w590 marB20">';
                    template += '	<div class="alert marB10">The rule below is pending for import. Please examine carefully the details</div>';
                    template += '	<label class="w110 floatL fbold">Rule Name:</label>';
                    template += '	<label class="wAuto floatL" id="ruleInfo"></label>';
                    template += '	<div class="clearB"></div>';
                    template += '	<label class="w110 floatL marL20 fbold">Import Type:</label>';
                    template += '	<label class="wAuto floatL" id="importType">';
                    template += '		<img id="preloader" alt="Retrieving" src="' + GLOBAL_contextPath + '/images/ajax-loader-rect.gif">';
                    template += '	</label>';
                    template += '	<div class="clearB"></div>';
                    template += '</div>';
            }
            return template;
        },
        getPostTemplate: function() {
            var template = "";

            template = '<div id="actionBtn" class="marT10 fsize12 border pad10 w580 mar0 marB20" style="background: #f3f3f3;">';
            template += '	<h3 style="border:none">Import Rule Guidelines</h3>';
            template += '	<div class="fgray padR10 padB15 fsize11">';
            template += '		<p align="justify">';
            template += '			Before importing any rule, it is advisable to review rule details.<br/><br/>';
            template += '		<p>';
            template += '	</div>';
            template += '	<div id="btnHolder" align="right" class="padR15 marT10" style="display:none">';
//				template += '		<a id="setImportBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
//				template += '			<div class="buttons fontBold">Set For Import</div>';
//				template += '		</a>';
//				template += '		<a id="setRejectBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
//				template += '			<div class="buttons fontBold">Set For Reject</div>';
//				template += '		</a>';
            template += '       <div id="setImportBtn" class="approve_btn clearfix"><a href="javascript:void(0);" id="link_btn">Import</a></div>';
            template += '		<div id="setRejectBtn" class="reject_btn clearfix"><a href="javascript:void(0);" id="link_btn">Reject</a></div>';
            template += '	</div>';
            template += '</div>';

            return template;
        },
        getRightPanelTemplate: function() {
            var template = "";

            template += '	<div class="rulePreview w590 marB20">';
            template += '		<div class="alert marB10">';
            template += '			Selected rule below will be overwritten when import button is clicked.';
            template += '			It is advisable to review both rules as this action cannot be undone.';
            template += '		</div>';
            template += '		<label class="w110 floatL marL20 fbold">Rule Name:</label>';
            template += '		<label class="wAuto floatL" id="ruleInfo"></label>';
            template += '		<div class="clearB"></div>';
            template += '		<label class="w110 floatL marL20 fbold">Import As:</label>';
            template += '		<div id="importAs" class="wAuto floatL"></div>';
            template += '		<div class="clearB"></div>';
            template += '	</div>';

            return template;
        },
        getRuleTransferMap: function(curPage, keywordFilter, sortOrder, ruleFilter) {
            var self = this;
            RuleTransferServiceJS.getExportMapList("pcmall", $.makeArray(), self.entityName, {
                callback: function(exportMapList) {
                    if (exportMapList) {
                        for (var index in exportMapList) {
                            self.ruleTransferMap[exportMapList[index]["ruleIdOrigin"]] = exportMapList[index];
                            if (exportMapList[index]["ruleIdTarget"])
                                self.ruleTargetList[exportMapList[index]["ruleIdTarget"]] = exportMapList[index]["ruleIdTarget"];
                        }
                    }
                },
                postHook: function() {
                    self.getAllRulesToImport(curPage, keywordFilter, sortOrder, ruleFilter);
                }
            });
        },
        getAllRulesToImport: function(curPage, keywordFilter, sortOrder, ruleFilter) {
            var self = this;
            var $selectedTab = $("#" + self.tabSelected);

            RuleTransferServiceJS.getRulesToImport(self.entityName, keywordFilter, curPage, self.pageSize, ruleFilter, sortOrder, {
                callback: function(data) {
                    var list = data.list;
                    var listSize = list.length;
                    var totalSize = (data) ? data.totalSize : 0;

                    $selectedTab.html($("div#tabContentTemplate").html());
                    var ruleDiv = $selectedTab.find("#rule").parent()[0];

                    $selectedTab.find("img#ruleNameSort, img#publishDateSort, img#exportDateSort").hide();

                    if (totalSize > 0) {
                        $selectedTab.find("img#ruleNameSort, img#publishDateSort, img#exportDateSort").show();
                        // Populate table row
                        for (var i = 0; i < listSize; i++) {
                            var rule = list[i];
                            var ruleId = rule["ruleId"];
                            var ruleName = rule["ruleName"];
                            var storeOrigin = rule["store"];
                            var dbRuleId = "";
                            var isRejected = rule["rejected"];

                            switch (self.entityName.toLowerCase()) {
                                case "elevate":
                                case "exclude":
                                case "demote":
                                case "facetsort":
                                    dbRuleId = ruleName;
                                    break;
                                default:
                                    break;
                            }

                            var $table = $selectedTab.find("table#rule");
                            var $tr = $selectedTab.find("tr#ruleItemPattern").clone().attr("id", "ruleItem" + $.formatAsId(ruleId)).show();
                            var lastPublishedDate = (rule["ruleStatus"] && $.isNotBlank($.toStoreFormat(rule["ruleStatus"]["lastPublishedDate"])));

                            if (rule["deleted"]) {
                                var msg = "Data for rule <b>" + ruleName + "</b> ";
                                msg += $.isNotBlank(lastPublishedDate) ? " published on <b>" + lastPublishedDate + "</b> " : " ";
                                msg += "is not available. <br/>Please re-export rule from " + getStoreLabel(storeOrigin) + " or contact Search Manager Team.";
                                $tr.find("td#ruleRefId").html(msg)
                                        .prop("colspan", 6);
                                $tr.find("td#select,td#ruleOption,td#publishDate,td#type,td#importAs").remove();
                            }
                            else {
                                $tr.find("td#select > input[type='checkbox']").attr({"id": $.formatAsId(ruleId), "value": ruleId, "name": rule["ruleName"]});
                                $tr.find("td#select > div.approve_btn").attr({"id": $.formatAsId(ruleId)});
                                $tr.find("td#select > div.reject_btn").attr({"id": $.formatAsId(ruleId)});

                                $tr.find("td#ruleOption > img.previewIcon").attr("id", $.formatAsId(ruleId));

                                if (rule["updateStatus"] !== "DELETE") {
                                    if (self.entityName === "didYouMean") {
                                        $tr.find("img.previewIcon").hide();
                                        $tr.find("td#ruleRefId").append('<p class="breakWord">Click <a href="javascript:void(0);" id="downloadCurrent">here</a> to download current list.</p>');
                                        $tr.find("td#ruleRefId").append('<p class="breakWord">Click <a href="javascript:void(0);" id="downloadImport">here</a> to download for import list.</p>');

                                        $tr.find("td#ruleRefId p #downloadCurrent").download({
                                            headerText: "Download Current Did You Mean Rules",
                                            moduleName: self.entityName,
                                            ruleType: self.entityName,
                                            solo: $(".internal-tooltip"),
                                            classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped internal-tooltip',
                                            requestCallback: function(e2) {
                                                var params = new Array();
                                                var url = GLOBAL_contextPath + "/spell/" + GLOBAL_storeId + "/xls";
                                                var urlParams = "";
                                                var count = 0;

                                                params["filename"] = e2.data.filename;
                                                params["type"] = e2.data.type;
                                                params["id"] = "spell_rule";
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

                                        $tr.find("td#ruleRefId p #downloadImport").download({
                                            headerText: "Download Did You Mean Rules for Import",
                                            moduleName: self.entityName,
                                            ruleType: self.entityName,
                                            solo: $(".internal-tooltip"),
                                            classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-tipped internal-tooltip',
                                            requestCallback: function(e2) {
                                                var params = new Array();
                                                var url = GLOBAL_contextPath + "/spell/" + GLOBAL_storeId + "/import/xls";
                                                var urlParams = "";
                                                var count = 0;

                                                params["filename"] = e2.data.filename;
                                                params["type"] = e2.data.type;
                                                params["id"] = "spell_rule";
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
                                    else {
                                        $tr.find("img.previewIcon")
                                                .xmlpreview({
                                            transferType: "import",
                                            ruleType: self.entityName,
                                            ruleId: ruleId,
                                            ruleName: ruleName,
                                            ruleXml: rule,
                                            rule: rule,
                                            ruleStatusList: self.ruleStatusMap == null ? null : self.ruleStatusMap[self.entityName],
                                            ruleTransferMap: self.ruleTransferMap,
                                            enablePreTemplate: true,
                                            enablePostTemplate: true,
                                            leftPanelSourceData: "xml",
                                            enableRightPanel: true,
                                            rightPanelSourceData: "database",
                                            dbRuleId: dbRuleId,
                                            postTemplate: self.getPostTemplate(),
                                            preTemplate: self.getPreTemplate(self.entityName, rule["importType"]),
                                            rightPanelTemplate: self.getRightPanelTemplate(),
                                            postButtonClick: function() {
                                                self.getImportList(self.defaultPage, self.defaultKeywordFilter, self.defaultSortOrder, self.defaultRuleFilterBy);
                                            },
                                            itemImportAsListCallback: function(base, contentHolder, sourceData) {
                                                DeploymentServiceJS.getDeployedRules(self.entityName, "published", {
                                                    callback: function(data) {
                                                        base.populateImportAsList(data, contentHolder, sourceData);
                                                    }
                                                });
                                            },
                                            itemImportTypeListCallback: function(base, contentHolder) {
                                                base.populateImportTypeList(self.importTypeList, contentHolder);
                                            },
                                            itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap) {
                                                if (self.entityName === "elevate") {
                                                    ElevateServiceJS.isRequireForceAdd(ruleName, memberIds, {
                                                        callback: function(data) {
                                                            base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
                                                        },
                                                        preHook: function() {
                                                            base.prepareForceAddStatus(contentHolder);
                                                        }
                                                    });
                                                }
                                            },
                                            itemXmlForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberConditions, memberIdToItemMap) {
                                                if (self.entityName === "elevate") {
                                                    ElevateServiceJS.isItemInNaturalResult(ruleName, memberIds, memberConditions, {
                                                        callback: function(data) {
                                                            base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
                                                        },
                                                        preHook: function() {
                                                            base.prepareForceAddStatus(contentHolder);
                                                        }
                                                    });
                                                }
                                            },
                                            checkUncheckCheckboxCallback: function(base, ruleId, pub) {
                                                switch (pub) {
                                                    case 'import':
                                                        self.toggleImportCheckbox($.formatAsId(ruleId));
                                                        break;
                                                    case 'reject':
                                                        self.toggleRejectCheckbox($.formatAsId(ruleId));
                                                        break;
                                                }
                                            },
                                            changeImportTypeCallback: function(base, ruleId, opt) {
                                                $("#ruleItem" + $.formatAsId(ruleId) + " #type select").val(opt);
                                            },
                                            changeImportAsCallback: function(base, ruleId, importAs, ruleName, newName) {
                                                if (importAs != 0 || newName.length > 0) {
                                                    $("#ruleItem" + $.formatAsId(ruleId) + " #importAs select").val(importAs).change();
                                                    $("#ruleItem" + $.formatAsId(ruleId) + " #importAs #replacement #newName").val(newName);
                                                }
                                            },
                                            itemImportTypeListCallback: function(base, contentHolder) {
                                                base.populateImportTypeList(self.importTypeList, contentHolder);
                                            },
                                                    itemForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberIdToItemMap) {
                                                if (self.entityName === "elevate") {
                                                    ElevateServiceJS.isRequireForceAdd(ruleName, memberIds, {
                                                        callback: function(data) {
                                                            base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
                                                        },
                                                        preHook: function() {
                                                            base.prepareForceAddStatus(contentHolder);
                                                        }
                                                    });
                                                }
                                            },
                                                    itemXmlForceAddStatusCallback: function(base, contentHolder, ruleName, memberIds, memberConditions, memberIdToItemMap) {
                                                if (self.entityName === "elevate") {
                                                    ElevateServiceJS.isItemInNaturalResult(ruleName, memberIds, memberConditions, {
                                                        callback: function(data) {
                                                            base.updateForceAddStatus(contentHolder, data, memberIdToItemMap);
                                                        },
                                                        preHook: function() {
                                                            base.prepareForceAddStatus(contentHolder);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    $tr.find("td#ruleOption > img.previewIcon").hide();
                                }

                                //if(ruleId.toLowerCase() !== rule["ruleName"].toLowerCase())	
                                //	$tr.find("td#ruleRefId > p#ruleId").html(list[i]["ruleId"]);

                                $tr.find("td#ruleRefId > p#ruleName").html(" ").append(list[i]["ruleName"])
                                        .prepend($tr.find("img.previewIcon"));

                                $tr.find("td#publishDate > p#publishDate").html(lastPublishedDate);

                                //import type
                                var $importTypeSelect = $tr.find("td#type > select#importTypeList");

                                if (self.importTypeList) {
                                    for (var importType in self.importTypeList) {
                                        $importTypeSelect.append($("<option>", {value: importType}).text(self.importTypeList[importType]));
                                    }
                                }

                                self.toggleCheckbox($tr.find("td#select > div.approve_btn, td#select > div.reject_btn"));

                                //import as
                                $tr.find("td#importAs").importas({
                                    container: ruleDiv,
                                    rule: list[i],
                                    ruleStatusList: self.ruleStatusMap[self.entityName],
                                    ruleTransferMap: self.ruleTransferMap,
                                    ruleTargetList: self.ruleTargetList,
                                    setRuleStatusListCallback: function(base, list) {
                                        self.ruleStatusMap[self.entityName] = list;
                                    },
                                    targetRuleStatusCallback: function(item, r, rs) {
                                        var locked = !$.isEmptyObject(rs) && (rs["approvalStatus"] === "PENDING" || rs["approvalStatus"] === "APPROVED" || rs["updateStatus"] === "DELETE");
                                        var id = $.formatAsId(r["ruleId"]);

                                        var $importBtn = item.parents("tr.ruleItem").find("td#select > div.approve_btn").removeClass('import_locked').removeClass('approve_active').addClass('approve_gray');
                                        var $rejectBtn = item.parents("tr.ruleItem").find("td#select > div.reject_btn").removeClass('import_locked').removeClass('reject_active').addClass('reject_gray');
                                        item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].selectItem').prop({checked: false});

                                        self.toggleCheckbox(item.parents("tr.ruleItem").find("td#select > div.approve_btn, td#select > div.reject_btn"));

                                        if (r["rejected"]) {
                                            item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].reject#' + id).prop({disabled: true, readonly: true, checked: false});
                                            $rejectBtn
                                                    .addClass('import_locked').removeClass('reject_gray')
                                                    .off("click mouseenter")
                                                    .on({
                                                click: function(e) {

                                                },
                                                mouseenter: showHoverInfo
                                            }, {locked: true, message: "You are not allowed to perform this action because you do not have the required permission or rule has been previously rejected."});
                                        } else {
                                            item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].reject#' + id).prop({disabled: false, readonly: false});
                                        }

                                        if (locked) {
                                            item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].import#' + id).prop({disabled: true, readonly: true, checked: false});
                                            $importBtn
                                                    .addClass('import_locked').removeClass('approve_gray')
                                                    .off("click mouseenter")
                                                    .on({
                                                click: function(e) {

                                                },
                                                mouseenter: showHoverInfo
                                            }, {locked: true, message: "You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked."});
                                            item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].selectItem').prop({checked: false});
                                        } else {
                                            item.parents("tr.ruleItem").find('td#select > input[type="checkbox"].import#' + id).prop({disabled: false, readonly: false});
                                        }
                                    }
                                });
                            }
                            $tr.appendTo($table);
                        }

                        $selectedTab.find("div#ruleCount").html(totalSize + (totalSize == 1 ? " Rule" : " Rules"));
                        $(ruleDiv).scroll();

                        // Alternate row style
                        $selectedTab.find("tr:not(#ruleItemPattern):even").addClass("alt");
                        self.submitHandler();
                    } else {
                        $selectedTab.find("table#rule").append('<tr><td class="txtAC" colspan="5">No pending rules found</td></tr>');
                        $selectedTab.find('div#actionBtn').hide();
                    }
                    self.addFiltersHandler($selectedTab, curPage, totalSize, keywordFilter, sortOrder, ruleFilter);
                    self.populateFilters($selectedTab);
                },
                preHook: function() {
                    self.prepareTabContent();
                },
                postHook: function() {
                    self.cleanUpTabContent();
                    self.changeSelectedImportType();
                    self.setSelectAllImportCheckbox();
                }
            });
        },
        populateFilters: function($selectedTab) {
            var self = this;
            var defaultSortIcon = GLOBAL_contextPath + '/images/tablesorter/bg.gif';
            var ascSortIcon = GLOBAL_contextPath + '/images/tablesorter/asc.gif';
            var descSortIcon = GLOBAL_contextPath + '/images/tablesorter/desc.gif';

            //populate ruleFilter
            if ($.isBlank(self.ruleFilterBy)) { // set rejected as default
                $selectedTab.find("select#ruleFilter").val('rejected');
            } else {
                $selectedTab.find("select#ruleFilter").val(self.ruleFilterBy);
            }

            //populate search keyword input
            if ($.isBlank(self.searchText))
                $selectedTab.find('input#keyword').val(self.defaultText);
            else
                $selectedTab.find('input#keyword').val(self.searchText);

            //populate sort icon
            //1. initialize as no active sort Order
            $selectedTab.find("img#ruleNameSort, img#publishDateSort, img#exportDateSort").attr("src", defaultSortIcon);

            //2. change sort icon of active sort order
            //activeSortOrder - EXPORT_DATE_DESC, EXPORT_DATE_ASC, RULE_NAME_DESC, RULE_NAME_ASC, PUBLISHED_DATE_DESC, PUBLISHED_DATE_ASC
            switch (self.activeSortOrder) {
                case "RULE_NAME_ASC":
                    $selectedTab.find("img#ruleNameSort").attr("src", ascSortIcon);
                    $selectedTab.find("input#ruleNameInp").prop("checked", false);
                    break;
                case "PUBLISHED_DATE_ASC":
                    $selectedTab.find("img#publishDateSort").attr("src", ascSortIcon);
                    $selectedTab.find("input#pubDateInp").prop("checked", false);
                    break;
                case "EXPORT_DATE_ASC":
                    $selectedTab.find("img#exportDateSort").attr("src", ascSortIcon);
                    $selectedTab.find("input#expDateInp").prop("checked", false);
                    break;
                case "RULE_NAME_DESC":
                    $selectedTab.find("img#ruleNameSort").attr("src", descSortIcon);
                    $selectedTab.find("input#ruleNameInp").prop("checked", true);
                    break;
                case "PUBLISHED_DATE_DESC":
                    $selectedTab.find("img#publishDateSort").attr("src", descSortIcon);
                    $selectedTab.find("input#pubDateInp").prop("checked", true);
                    break;
                case "EXPORT_DATE_DESC":
                    $selectedTab.find("img#exportDateSort").attr("src", descSortIcon);
                    $selectedTab.find("input#expDateInp").prop("checked", true);
                    break;
                    break;
                default: //if no pubDate order is specified, default is ascending
                    break;
            }
        },
        getImportList: function(curPage, keywordFilter, sortOrder, ruleFilter) {
            var self = this;
            self.currentPage = curPage;
            self.searchText = keywordFilter;
            self.activeSortOrder = sortOrder;
            self.ruleFilterBy = ruleFilter;

            if (GLOBAL_BDGroup) {
                self.getRuleTransferMap(curPage, keywordFilter, sortOrder, ruleFilter);
            } else {
                self.getAllRulesToImport(curPage, keywordFilter, sortOrder, ruleFilter);
            }
        },
        toggleCheckbox: function(elem) {
            var self = this;

            elem.off("click mouseenter").on({
                click: function(evt) {
                    var id = $(this).attr('id');
                    switch ($(this).attr('class')) {
                        case 'approve_btn':
                        case 'approve_btn approve_gray':
                        case 'approve_btn approve_active':
                            if ($('input[type="checkbox"]#' + id + '.import').is(":not(:checked)")) {
                                self.toggleImportCheckbox(id);
                            } else {
                                self.untoggleImportCheckbox(id);
                            }
                            break;
                        case 'reject_btn':
                        case 'reject_btn reject_gray':
                        case 'reject_btn reject_active':
                            if ($('input[type="checkbox"]#' + id + '.reject').is(":not(:checked)")) {
                                self.toggleRejectCheckbox(id);
                            } else {
                                self.untoggleRejectCheckbox(id);
                            }
                            break;
                    }
                }
            });
        },
        toggleImportCheckbox: function(id) {
            $('input[type="checkbox"]#' + id + '.import').attr('checked', true);
            $('div#' + id + '.approve_btn').removeClass('import_locked').removeClass('approve_gray').addClass('approve_active');
            $('input[type="checkbox"]#' + id + '.reject').attr('checked', false);

            var filename = $('div#' + id + '.reject_btn').css('background-image');
            var fileNameIndex = filename.lastIndexOf("/") + 1;
            filename = filename.substr(fileNameIndex);

            if ($.startsWith(filename, 'import_gray_locked')) {
                $('div#' + id + '.reject_btn').addClass('import_locked');
            } else {
                $('div#' + id + '.reject_btn').removeClass('reject_active').addClass('reject_gray');
            }
        },
        untoggleImportCheckbox: function(id) {
            $('input[type="checkbox"]#' + id + '.import').attr('checked', false);
            $('div#' + id + '.approve_btn').removeClass('approve_active').addClass('approve_gray');
        },
        toggleRejectCheckbox: function(id) {
            $('input[type="checkbox"]#' + id + '.reject').attr('checked', true);
            $('div#' + id + '.reject_btn').removeClass('import_locked').removeClass('reject_gray').addClass('reject_active');
            $('input[type="checkbox"]#' + id + '.import').attr('checked', false);

            var filename = $('div#' + id + '.approve_btn').css('background-image');
            var fileNameIndex = filename.lastIndexOf("/") + 1;
            filename = filename.substr(fileNameIndex);

            if ($.startsWith(filename, 'import_gray_locked')) {
                $('div#' + id + '.approve_btn').addClass('import_locked');
            } else {
                $('div#' + id + '.approve_btn').removeClass('approve_active').addClass('approve_gray');
            }

            $("#selectAllCheckbox").attr("checked", false);
        },
        untoggleRejectCheckbox: function(id) {
            $('input[type="checkbox"]#' + id + '.reject').attr('checked', false);
            $('div#' + id + '.reject_btn').removeClass('reject_active').addClass('reject_gray');
        },
        filterContent: function() {
            if (GLOBAL_storeId == "pcmallgov") {
                $("#import ul li a[href='#rankingRuleTab']").closest("li").remove();
                $("#import div#rankingRuleTab").remove();
            }
        },
        changeSelectedImportType: function() {
            $("table#rule tbody tr td select#importTypeList option[value=\"3\"]").each(
                    function() {
                        $(this).attr("selected", "selected");
                    }
            );
        },
        selectAllToggleReject: function(rejectBtn, rejectCheckbox) {
            rejectCheckbox.each(function() {
                $(this).attr("checked", true);
            });

            rejectBtn.each(function() {
                $(this).removeClass("reject_gray");
                $(this).addClass("reject_active");
            });
        },
        selectAllUntoggleReject: function(rejectBtn, rejectCheckbox) {
            rejectCheckbox.each(function() {
                $(this).removeAttr("checked");
            });

            rejectBtn.each(function() {
                $(this).removeClass("reject_active");
                $(this).addClass("reject_gray");
            });
        },
        selectAllToggleImport: function(approveBtn, approveCheckbox) {
            approveCheckbox.each(function() {
                $(this).attr("checked", true);
            });

            approveBtn.each(function() {
                $(this).removeClass("approve_gray");
                $(this).addClass("approve_active");
            });
        },
        selectAllUntoggleImport: function(approveBtn, approveCheckbox) {
            approveCheckbox.each(function() {
                $(this).removeAttr("checked");
            });

            approveBtn.each(function() {
                $(this).removeClass("approve_active");
                $(this).addClass("approve_gray");
            });
        },
        setSelectAllImportCheckbox: function() {
            var self = this;

            $("input#selectAllCheckbox").off().on("change", function() {
                var approveBtn = $("td#select div.approve_btn");
                var approveCheckbox = $('input[type="checkbox"].import');

                var rejectBtn = $("td#select div.reject_btn");
                var rejectCheckbox = $('input[type="checkbox"].reject');

                if ($(this).is(":checked")) {
                    self.selectAllToggleImport(approveBtn, approveCheckbox);
                } else {
                    self.selectAllUntoggleImport(approveBtn, approveCheckbox);
                }

                self.selectAllUntoggleReject(rejectBtn, rejectCheckbox);
            });
        },
        init: function() {
            var self = this;
            $("#titleText").html(self.moduleName);
            self.getRuleEntityList();
            self.filterContent();
            self.populateTabContent();
        }
    };

    $(document).ready(function() {
        Import.init();
    });

})(jQuery);
