
var callbackCount = 0;

$(function() {
    var tabContentTemplate = "<div id='#{id}'><p>#{content}</p></div>";
    var tabContentHeaderTemplate = "<tr><td colspan='2'><h2 class='padT5'>#{header}</h2></tr></tr>";
    var tabContentFieldTemplate = "<tr><td>#{label}&nbsp;</td><td>#{field}</td></tr>";
    var stringFieldTemplate = "<input type='text' class='w240' id='#{id}'/>";
    var booleanFieldTemplate = "<input id='#{id}' type='checkbox' class='firerift-style-checkbox on-off'/>";
    var accountRoleFieldTemplate = "<select class=\"w240p mar0\" id=\"#{id}\" style=\"cursor:pointer\">" +
            "<option value=\"\">All Roles</option></select>";
    var ruleEntityTemplate = "<select class=\"w240p mar0\" id=\"#{id}\" style=\"cursor:pointer\">" +
    		"</select>";
    
    var dropDownListMap = new Object();
    var tabObjects = new Array();
    var storePropertiesFilesArray;
    var accountRoleArray = new Array();
    var ruleEntityArray = new Array();
    
    var DropDownOption = function(name, value) {
        this.name = name;
        this.value = value;
    };
    
    dropDownListMap['mail.workflow.approver.group'] = accountRoleArray;
    
    dropDownListMap['status_elevate'] = dropDownListMap['status_exclude'] = dropDownListMap['status_demote'] = dropDownListMap['status_facet_sort'] 
    = dropDownListMap['status_query_cleaning'] = dropDownListMap['status_did_you_mean'] = dropDownListMap['status_banner'] = ruleEntityArray;
    
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

            storeTabsTab.tabs('add', '#' + moduleName, moduleTitle);
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
            var moduleName = module.name;
            var groups = module.groups;

            if (groups !== null) {
                var content = new StringBuilder();

                for (var j = 0; j < groups.length; j++) {
                    var group = groups[j];
                    var groupName = group.name;
                    var members = group.members;

                    content.append("<table class='fsize12 marT20 marL20'>");

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
                                    var accountRoleField = accountRoleFieldTemplate.replace(
                                            /#\{id\}/g, fieldId);
                                    fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
                                            accountRoleField);
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
                                                storeProperty.value);
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

    var setSelectedOptionByValue = function(fieldId, fieldValue) {
        $("#" + fieldId + " option").each(function() {
        	if ($(this).text() === fieldValue) {
            	$(this).prop("selected", true);
                return false; // break the foreach
            }
        });
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
                    fieldValue = fieldComponent.val();
                    break;
                case "Boolean":
                    fieldValue = fieldComponent.prop("checked");
                    break;
            }

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
    	
    	if(callbackCount < 2) {
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
                     store_settings.cleanUpTabContent();
	             }
             );
    	}
    }

    var store_settings = {
        prepareTabContent: function() {
            var storeConfig = $("#store_config");

            if (!$("div.circlePreloader").is(":visible")) {
                $('<div class="circlePreloader"><img src="../images/ajax-loader-circ.gif"></div>').prependTo(storeConfig);
            }

            storeConfig.find('table.tblItems, div#actionBtn').hide();
            storeConfig.find("div#ruleCount").html("");
        },
        cleanUpTabContent: function() {
            $('div.circlePreloader').remove();
        },
        init: function() {
            PropertiesManagerServiceJS.getStoreProperties(function(data) {
                var stores = data.stores;

                // show the loading icon
                store_settings.prepareTabContent();

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

                                SecurityServiceJS.getRoleList({
                                    callback: function(data) {
                                        roleList = data;
                                        var list = data.list;

                                        if (list.length > 0) {
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
                                
                                SecurityServiceJS.getRuleEntityList({
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
                for (var i = 0; i < tabObjects.length; i++) {
                    var tabObject = tabObjects[i];
                    var tabName = tabObject.name;
                    var fields = tabObject.fields;

                    var storeProperties = generateStoreProperties(fields);

                    var storePropertiesFile = findStorePropertiesFileByModuleName(tabName);
                    storePropertiesFile.storeProperties = storeProperties;
                }
               
                PropertiesManagerServiceJS.saveStoreProperties(storePropertiesFilesArray,
                        function(result) {
                            jAlert("Store settings saved", "Saved");
                        }
                );
            });
        }
    };

    store_settings.init();
});