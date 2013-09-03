$(function() {
    /**
     *  Generate tabs based from the Module object passed
     * @param {type} modules the Module object to generate from
     * @returns {@exp;tabStr@call;toString} the generated tabs
     */
    var generateTabs = function(modules) {
        var storeContentTab = $("#store_tabs");
        
        for (var i = 0, len = modules.length; i < len; i++) {
            var module = modules[i];
            
            storeContentTab.tabs('add', '#' + module.name, module.title);
        }
    };
    var store_settings = {
        init: function() {
            PropertiesManagerServiceJS.getStoreProperties({
                callback: function(data) {
                    var modules = data.stores[1].modules;
                    generateTabs(modules);
                }
            });
        }
    };

    store_settings.init();
});