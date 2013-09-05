$(function() {
    var tabContentTemplate = "<div id='#{id}'><p>#{content}</p></div>";
    var tabContentHeaderTemplate = "<tr><td colspan='2'><h2 class='padT5'>#{header}</h2></tr></tr>";
    var tabContentFieldTemplate = "<tr><td>#{label}&nbsp;</td><td>#{field}</td></tr>";
    var stringFieldTemplate = "<input type='text' class='w240' id='#{id}'/>";
    var booleanFieldTemplate = "<input id='#{id}' type='checkbox' class='firerift-style-checkbox on-off'/>";

    /**
     *  Generate tabs based from the Module object passed
     * @param {type} modules the Module object to generate from
     * @returns {@exp;tabStr@call;toString} the generated tabs
     */
    var generateTabs = function(modules) {
        var storeTabsTab = $("#store_tabs");

        for (var i = 0, len = modules.length; i < len; i++) {
            var module = modules[i];

            storeTabsTab.tabs('add', '#' + module.name, module.title);
        }
    };

    /**
     *  Generate tab contents based from the Module object passed
     * @param {type} modules the Module object to generate from
     * @returns {@exp;tabStr@call;toString} the generated tab contents
     */
    var generateTabContents = function(modules) {
        var builder = new StringBuilder();

        for (var i = 0; i < modules.length; i++) {
            var module = modules[i];
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

                    for (var k = 0; k < members.length; k++) {
                        var member = members[k];
                        var propertyId = member.propertyId;
                        var property = getPropertyById(propertyId, module.properties);

                        if (property !== null) {
                            var label = property.label;
                            var type = property.type;
                            var fieldToAppend = tabContentFieldTemplate.replace(
                                    /#\{label\}/g, label);
                            var propertyId = property.id;

                            if (type === "String") {
                                var stringField = stringFieldTemplate.replace(/#\{id\}/g,
                                        propertyId);
                                fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
                                        stringField);
                            } else if (type === "Boolean") {
                                var booleanField = booleanFieldTemplate.replace(/#\{id\}/g,
                                        propertyId);
                                fieldToAppend = fieldToAppend.replace(/#\{field\}/g,
                                        booleanField);
                            }

                            content.append(fieldToAppend);
                        }
                    }

                    content.append("</table>");
                }

                var toAppend = tabContentTemplate.replace(/#\{id\}/g, module.name).
                        replace(/#\{content\}/g, content.toString());
                builder.append(toAppend);
            }
        }

        $("#store_tabs").append(builder.toString());
    };

    var populateFields = function(modules, storePropertiesFiles) {
        for (var i = 0; i < modules.length; i++) {
            var module = modules[i];
            var groups = module.groups;

            if (groups !== null) {
                for (var j = 0; j < groups.length; j++) {
                    var group = groups[j];
                    var members = group.members;

                    for (var k = 0; k < members.length; k++) {
                        var member = members[k];
                        var propertyId = member.propertyId;
                        var property = getPropertyById(propertyId, module.properties);

                        if (property !== null) {
                            var type = property.type;

                            var propertyId = property.id;
                            var storeProperty = getStorePropertyByName(propertyId, module,
                                    storePropertiesFiles);

                            if (storeProperty !== null) {
                                switch (type) {
                                    case "String":
                                        $("#" + propertyId).val(storeProperty.value);
                                        break;
                                    case "Boolean":
                                        var booleanValue = changeStringToBoolean(
                                                storeProperty.value);

                                        $("#" + propertyId).prop("checked", booleanValue);
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    var changeStringToBoolean = function(booleanStr) {
        switch (booleanStr) {
            case "true":
            case "1":
            case "yes":
                return true;
        }
        return false;
    };

    var getPropertyById = function(propertyId, properties) {
        for (var i = 0; i < properties.length; i++) {
            var property = properties[i];

            if (property.id === propertyId) {
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

    var store_settings = {
        init: function() {
            PropertiesManagerServiceJS.getStoreProperties(function(data) {
                var stores = data.stores;

                // for generating the labels and fields
                UtilityServiceJS.getStoreId(function(id) {
                    for (var i = 0; i < stores.length; i++) {
                        var store = stores[i];

                        if (store.id === id) {
                            var modules = store.modules;
                            generateTabContents(modules);
                            generateTabs(modules);

                            // for populating the fields 
                            PropertiesReaderServiceJS.readAllStorePropertiesFiles(id,
                                    function(storePropertiesFiles) {
//                                        getStorePropertyByName("", modules[0], storePropertiesFiles);
                                        populateFields(modules, storePropertiesFiles);
//                                        for (var i = 0; i < storePropertiesFiles.length; i++) {
//                                            var storePropertiesFile = storePropertiesFiles[i];
//                                            var storeProperties = storePropertiesFile.storeProperties;
//
//                                            for (var j = 0; j < storeProperties.length; j++) {
//                                                var storeProperty = storeProperties[j];
//                                                var name = storeProperty.name;
//                                                var value = storeProperty.value;
//
//                                                $("#" + name).val(value);
//                                            }
//                                        }
                                    }
                            );
                        }
                    }


                });
            });


        }
    };

    store_settings.init();
});