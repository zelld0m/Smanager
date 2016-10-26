
var callbackCount = 0;

$(function() {
    var tabContentTemplate = "<div id='#{id}'><p>#{content}</p><br/><br/></div>";
    var tabContentHeaderTemplate = "<tr class='groupHeader'><td colspan='2'><h2 class='padT5'>#{header}</h2></tr></tr>";
    var tabContentFieldTemplate = "<tr><td class='itemLabel'>#{label}&nbsp;</td><td>#{field}</td></tr>";
    var stringFieldTemplate = "<input type='text' class='w240' id='#{id}'/>";
    var booleanFieldTemplate = "<input id='#{id}' type='checkbox' class='firerift-style-checkbox on-off'/>";
    var accountRoleFieldTemplate = "<select class='w240p mar0' id='#{id}' style='cursor:pointer'></select>";
    var multiDropDownFieldTemplate = "<select class='w240p mar0' id='#{id}' style='cursor:pointer' multiple='multiple'></select>";
    var collapseLinkTemplate = "&nbsp;&nbsp;(<a class='hideBtn' href='javascript:void(0);'>#{label}</a>)";
        
    var dropDownListMap = new Object();
    var tabObjects = new Array();
    var storePropertiesFilesArray;
    var accountRoleArray = new Array();
    var ruleEntityArray = new Array();
    var contentTagsArray = new Array();
    var contentTypeArray = new Array();
    
    var DropDownOption = function(name, value) {
        this.name = name;
        this.value = value;
    };

    dropDownListMap['mail.workflow.approver.group'] = accountRoleArray;
    dropDownListMap['typeahead.excelUploadDefaultStatus'] = dropDownListMap['typeahead.weeklyDefaultStatus'] = dropDownListMap['typeahead.dailyDefaultStatus'] = dropDownListMap['status.elevate'] = dropDownListMap['status.exclude'] = dropDownListMap['status.demote'] = dropDownListMap['status.facetSort'] 
    = dropDownListMap['status.queryCleaning'] = dropDownListMap['status.didYouMean'] = dropDownListMap['status.banner'] = dropDownListMap['status.rankingRule'] = dropDownListMap['status.typeahead'] = ruleEntityArray;

    dropDownListMap['filters.tags'] = contentTagsArray;
    dropDownListMap['section.one.type'] = dropDownListMap['section.two.type'] = dropDownListMap['section.three.type'] = dropDownListMap['section.four.type'] = dropDownListMap['section.five.type'] = contentTypeArray;

    var Field = function(id, propertyId, type) {
        this.id = id;
        this.propertyId = propertyId;
        this.type = type;
    };

    var TabObject = function(name, fields) {
        this.name = name;
        this.fields = fields;
    };

    var StoreProperty = function(name, value) {
        this.name = name;
        this.value = value;
    };

    /**
     *  Generate tabs based from the Module object passed
     * @param {type} modules the Module object to generate from
     * @returns {@exp;tabStr@call;toString} the generated tabs
     */
    var generateTabs = function(modules) {
        var storeTabsTab = $("#store_tabs");

        for (var i = 0; i < modules.length; i++) {
            var module = modules[i];
            var moduleName = module.name;
            var moduleTitle = module.title;

            if (module.enableUI === true) {
            	storeTabsTab.tabs('add', '#' + moduleName, moduleTitle);
            }
        }
    };

    /**
     *  Generate tab contents based from the Module object passed
     * @param {store} modules the Module object to generate from
     */
    var generateTabContents = function(modules) {
        var builder = new StringBuilder();

        for (var i = 0; i < modules.length; i++) {
        	var module = modules[i];

        	if (module.enableUI === true) {
	            var moduleName = module.name;
	            var groups = module.groups;
	
	            if (groups !== null) {
	                var content = new StringBuilder();

	                for (var j = 0; j < groups.length; j++) {
	                    var group = groups[j];
	                    var groupName = group.name;
	                    var members = group.members;

	                    if (group.collapse === true) {
	                    	content.append("<table class='collapse fsize12 marT20 marL20 settingsTable'>");
	                    } else {
	                    	content.append("<table class='fsize12 marT20 marL20 settingsTable'>");
	                    }

	                    if (groupName !== null) {
	                        var groupNameHeader = tabContentHeaderTemplate.replace(
	                                /#\{header\}/g, groupName);
	                        content.append(groupNameHeader);
	                    }
	
	                    var fields = new Array();
	
	                    for (var k = 0; k < members.length; k++) {
	                        var member = members[k];
	                        var propertyId = member.propertyId;
	                        var property = getPropertyById(propertyId, module.properties);
	
	                        if (property !== null) {
	                            var label = property.label;
	                            var type = property.type;
	                            var fieldToAppend = tabContentFieldTemplate.replace(
	                                    /#\{label\}/g, label);
	                            var fieldId = moduleName + "_" + propertyId.replace(/\./g, "_");
	
	                            switch (type) {
	                                case "String":
	                                    var stringField = stringFieldTemplate.replace(
	                                            /#\{id\}/g, fieldId);
	                                    fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
	                                            stringField);
	                                    break;
	                                case "Boolean":
	                                    var booleanField = booleanFieldTemplate.replace(
	                                            /#\{id\}/g, fieldId);
	                                    fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
	                                            booleanField);
	                                    break;
	                                case "DropDown":
	                                	if (property.multiValued) {
	                                		var multiDropDownField = multiDropDownFieldTemplate.replace(
		                                            /#\{id\}/g, fieldId);
	                                		fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
	                                				multiDropDownField);
	                                	} else {
		                                    var accountRoleField = accountRoleFieldTemplate.replace(
		                                            /#\{id\}/g, fieldId);
		                                    fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
		                                            accountRoleField);
	                                	}
	                                    break;
	                            }
	
	                            content.append(fieldToAppend);
	
	                            fields.push(new Field(fieldId, propertyId, type));
	                        }
	                    }

	                    // add the module name and it's fields to tabObjects array
	                    addToTabObjectArray(moduleName, fields);
	                    content.append("</table>");
	                }
	
	                var toAppend = tabContentTemplate.replace(/#\{id\}/g, module.name).
	                        replace(/#\{content\}/g, content.toString());
	                builder.append(toAppend);
	            }
        	}
        }
        $("#store_tabs").append(builder.toString());
    };

    var addToTabObjectArray = function(moduleName, fields) {
        var tabObject = findTabObjectByModuleName(moduleName);

        if (tabObject === null) {
            tabObjects.push(new TabObject(moduleName, fields));
        } else {
            for (var i = 0; i < fields.length; i++) {
                var field = fields[i];
                tabObject.fields.push(field);
            }
        }
    };

    var findTabObjectByModuleName = function(moduleName) {
        for (var i = 0; i < tabObjects.length; i++) {
            var tabObject = tabObjects[i];
            if (tabObject.name === moduleName) {
                return tabObject;
            }
        }

        return null;
    };

    var populateFields = function(modules, storePropertiesFiles) {
        for (var i = 0; i < modules.length; i++) {
            var module = modules[i];
            var moduleName = module.name;
            var groups = module.groups;

            if (groups !== null) {
                for (var j = 0; j < groups.length; j++) {
                    var group = groups[j];
                    var members = group.members;

                    for (var k = 0; k < members.length; k++) {
                        var member = members[k];
                        var memberPropertyId = member.propertyId;
                        var property = getPropertyById(memberPropertyId,
                                module.properties);

                        if (property !== null) {
                            var type = property.type;

                            var propertyId = property.id;
                            var storeProperty = getStorePropertyByName(propertyId, module,
                                    storePropertiesFiles);
                            var fieldId = moduleName + "_" + propertyId.replace(
                                    /\./g, "_");

                            if (storeProperty !== null) {
                                switch (type) {
                                    case "String":
                                        $("#" + fieldId).val(storeProperty.value);
                                        break;
                                    case "Boolean":
                                        var booleanValue = changeStringToBoolean(
                                                storeProperty.value);
                                        $("#" + fieldId).slidecheckbox({
                                            initOn: booleanValue,
                                            locked: false
                                        });
                                        break;
                                    case "DropDown":
                                    	// populate the account role field
                                    	populateDropDownField(fieldId, dropDownListMap[propertyId]);
                                    	
                                        // set the selected option by store property value
                                        setSelectedOptionByValue(fieldId,
                                                storeProperty.value, property.multiValued);
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    var populateDropDownField = function(fieldId, dropDownList) {
        for (var i = 0; i < dropDownList.length; i++)
        {
            var option = dropDownList[i];
            $("select#" + fieldId).append(
                    $("<option>",
                    {value: option.value}).
                    text(option.name));
        }
    };

    var setSelectedOptionByValue = function(fieldId, fieldValue, isMultiple) {
    	if (!isMultiple) {
	        $("#" + fieldId + " option").each(function() {
	        	if ($(this).text() === fieldValue) {
	            	$(this).prop("selected", true);
	                return false; // break the foreach
	            }
	        });
    	} else {
    		var values = fieldValue.split(",");
    		$("#" + fieldId + " option").each(function() {
	        	if ($.inArray($(this).val(), values) > -1) {
	            	$(this).prop("selected", true);
	            }
	        });
    	}
    };

    var changeStringToBoolean = function(booleanStr) {
        switch (booleanStr) {
            case "true":
            case "1":
            case "on":
            case "yes":
                return true;
        }
        return false;
    };

    var getPropertyById = function(id, properties) {
        for (var i = 0; i < properties.length; i++) {
            var property = properties[i];

            if (property.id === id) {
                return property;
            }
        }

        return null;
    };

    var getStorePropertyByName = function(propertyName, module, storePropertiesFiles) {
        var moduleName = module.name;

        for (var i = 0; i < storePropertiesFiles.length; i++) {
            var storePropertiesFile = storePropertiesFiles[i];

            if (storePropertiesFile.moduleName === moduleName) {
                var storeProperties = storePropertiesFile.storeProperties;

                for (var j = 0; j < storeProperties.length; j++) {
                    var storeProperty = storeProperties[j];

                    if (propertyName === storeProperty.name) {
                        return storeProperty;
                    }
                }
            }
        }

        return null;
    };

    var generateStoreProperties = function(fields) {
        var storeProperties = new Array();

        for (var i = 0; i < fields.length; i++) {
            var field = fields[i];
            var fieldId = field.id;
            var fieldPropertyId = field.propertyId;
            var fieldType = field.type;
            var fieldValue = null;
            var fieldComponent = $("#" + fieldId);

            switch (fieldType) {
                case "String":
                case "DropDown":
                	if (fieldComponent.val()) {
	                	if (fieldType === "DropDown" && fieldComponent.attr("multiple") === "multiple") {
	                		fieldValue = fieldComponent.val().join();
	                	} else {
	                		fieldValue = fieldComponent.val();
	                	}
                	}
                    break;
                case "Boolean":
                    fieldValue = fieldComponent.prop("checked");
                    break;
            }

            console.log("GENERATE STORE PROPERTIES");
            console.log("Field: " + fieldPropertyId);
            console.log("Value: " + fieldValue);
            
            storeProperties.push(new StoreProperty(fieldPropertyId, fieldValue));
        }

        return storeProperties;
    };

    var findStorePropertiesFileByModuleName = function(moduleName) {
        for (var i = 0; i < storePropertiesFilesArray.length; i++) {
            var storePropertiesFile = storePropertiesFilesArray[i];

            if (storePropertiesFile.moduleName === moduleName) {
                return storePropertiesFile;
            }
        }

        return null;
    };

    /**
     * Helper method for hiding the error messages once the store-properties.xml file
     * has been successfully retrieved from the server
     * @returns {undefined}
     */
    var hideErrorMessages = function() {
        $("#no_store_message").hide();
        $("#settingsSaveBtnDiv").show();
    };
    
    function loadFieldsFromProperty (id, modules) {
    	if(callbackCount < 4) {
    		this.setTimeout(function() {loadFieldsFromProperty(id, modules);}, 100);
    	} else {
    		 PropertiesReaderServiceJS.
             readAllStorePropertiesFiles(id,
	             function(storePropertiesFiles) {
            	 	 // populate the fields
	                 populateFields(modules,
	                         storePropertiesFiles);
	                 storePropertiesFilesArray =
	                         storePropertiesFiles;
	                 // hides the error messages
	                 hideErrorMessages();
	                 // remove the loading icon

                     handleCollapse(modules);
                     store_settings.unbusy();
	             }
             );
    	}
    }

    var handleCollapse = function(modules) {
        var storeTabsTab = $("#store_tabs");
        for (var i = 0; i < modules.length; i++) {
            var module = modules[i];
            var $tabs = $("#"+module.name);
            var $collapse = $tabs.find(".collapse");

            $collapse.each(function() {
            	var collapseLabel = "";
            	if ($(this).find("input[id*='_enable']").is(':checked')) {
            		collapseLabel = collapseLinkTemplate.replace(/#\{label\}/g, "-")
            	} else {
            		collapseLabel = collapseLinkTemplate.replace(/#\{label\}/g, "+")
            		$(this).find("tr:gt(0)").hide();
            	}

            	$(this).find(".groupHeader").next().addClass("collapseHeader")
            	$(this).find(".groupHeader").find("td").find(":first-child").append(collapseLabel);
            });

            $collapse.find(".hideBtn").click(function(){
            	var text = "";
            	if ($(this).parents(".collapse").find("tr:eq(1)").is(":hidden")) {
            		text = "-";
            	} else {
            		text = "+";
            	}

            	$(this).text(text)
            	$(this).parents(".collapse").find("tr:gt(0)").toggle();
            	return false;
            });
        }
    };

    var store_settings = {
        prepare: function() {
            var storeConfig = $("#store_config");
            storeConfig.find('table.tblItems, div#actionBtn').hide();
            storeConfig.find("div#ruleCount").html("");
        },
        busy: function() {
        	if (!$("#loader-image").is(":visible")) {
                $.blockUI({"message": '<img id="loader-image" src="../images/ajax-loader-circ32x32.gif">',
                           "css": {border: "none", "background-color":"transparent", color:"transparent"}});
            }
        },
        unbusy: function() {
            $.unblockUI();
        },
        init: function() {
        	var base = this;
        	
            PropertiesManagerServiceJS.getStoreProperties(function(data) {
                var stores = data.stores;

                // show the loading icon
                store_settings.busy();
                store_settings.prepare();

                // for generating the labels and fields
                UtilityServiceJS.getStoreId({
                    callback: function(id) {
                        for (var i = 0; i < stores.length; i++) {
                            var store = stores[i];

                            if (store.id === id) {
                                var modules = store.modules;

                                // generate the tab contents
                                generateTabContents(modules);

                                // generate the tabs
                                generateTabs(modules);

                                UtilityServiceJS.getConfigProperty("getTagsListService", {
                                	callback: function(servletUrl) {
                                		jQuery.ajax({
                                			url: servletUrl,
                                			type: 'POST',
                                			dataType: 'json',
                                			success: function(data) {
                                				for(var i = 0; i < data.tags.length; i++) {
                                					var tagData = data.tags[i];
                                					contentTagsArray.push( new DropDownOption(tagData["name"], tagData["name"].replace(/ /g, "_")));
                                				}
                                				callbackCount++;
                                			}
                                		});
                                	}
                                });

                                UtilityServiceJS.getConfigProperty("getContentTypeService", {
                                	callback: function(servletUrl) {
                                		jQuery.ajax({
                                			url: servletUrl,
                                			data: { store : GLOBAL_storeId },
                                			type: 'POST',
                                			dataType: 'xml',
                                			success: function(data) {
                                				var $xml = $(data.activeElement);
                                				$xml.find("pageType").each(function() {
                                					var name = $(this).find("name").text();
                                					var id = $(this).find("code").text();
                                					contentTypeArray.push( new DropDownOption(name, id));
                                				});
                                				callbackCount++;
                                			}
                                		});
                                	}
                                });

                                SecurityServiceJS.getRoleList({
                                    callback: function(data) {
                                        roleList = data;
                                        var list = data.list;

                                        if (list.length > 0) {
                                        	
                                        	accountRoleArray.push(
                                                    new DropDownOption("All Roles", ""));
                                        	
                                            for (var i = 0; i < list.length; i++) {
                                                var listValue = list[i];
                                                var id = listValue["id"];
                                                var roleName = listValue["rolename"];

                                                accountRoleArray.push(
                                                        new DropDownOption(id, roleName));
                                            }
                                        }
                                        
                                        callbackCount++;
                                    },
                                    errorHandler: function(errorString, exception) {
                                    	callbackCount++;
                                    }
                                });
                                
        						UtilityServiceJS.getStoreListNameAndLogo(true, {
        							callback:function(data){
        								$.cookie(COOKIE_STORE_SELECTION, JSON.stringify(data) ,{path: GLOBAL_contextPath});
        								storeList = data;
        								$("select#refstore").append($("<option>", { value : "" }).text("All Stores"));
        								for (key in data){
        									var keyVal = data[key];
        									var storeName = keyVal['name'];
        									$("select#refstore").append($("<option>", { value : key }).text(storeName));
        								}
        							}
        						});
                                
                                SecurityServiceJS.getImportTypeList({
                                    callback: function(data) {
                                        roleList = data;
                                        var list = data.list;

                                        if (list.length > 0) {
                                            for (var i = 0; i < list.length; i++) {
                                                var listValue = list[i];
                                                var name = listValue["displayText"];

                                                ruleEntityArray.push( new DropDownOption(name, name));
                                            }
                                        }
                                        
                                        callbackCount++;
                                    },
                                    errorHandler: function(errorString, exception) {
                                    	callbackCount++;
                                    }
                                });

                                loadFieldsFromProperty(id, modules);  
                            	// for populating the fields 
                            }
                        }
                    }
                });
            });

            $("#settingsSaveBtn").click(function() {
            	base.busy();
                for (var i = 0; i < tabObjects.length; i++) {
                    var tabObject = tabObjects[i];
                    var tabName = tabObject.name;
                    var fields = tabObject.fields;

                    var storeProperties = generateStoreProperties(fields);

                    var storePropertiesFile = findStorePropertiesFileByModuleName(tabName);
                    storePropertiesFile.storeProperties = storeProperties;
                }

                PropertiesManagerServiceJS.saveStoreProperties(GLOBAL_storeId, storePropertiesFilesArray, GLOBAL_username,
                        function(result) {
                	        base.unbusy();
                            jAlert("Store settings saved", "Saved");
                        }
                );
            });
        }
    };

    store_settings.init();
});