$(function() {
    var tabContentTemplate = "<div id='#{id}'><p>#{content}</p></div>";

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

        for (var i = 0, len = modules.length; i < len; i++) {
            var module = modules[i];
            var toAppend = tabContentTemplate.replace(/#\{id\}/g, module.name).
                    replace(/#\{content\}/g, module.title);
            builder.append(toAppend);
        }

        $("#store_tabs").append(builder.toString());
    };

    var store_settings = {
        init: function() {
            PropertiesManagerServiceJS.getStoreProperties({
                callback: function(data) {
                    var modules = data.stores[1].modules;
                    generateTabContents(modules);
                    generateTabs(modules);
                }
            });
        }
    };

    store_settings.init();
});