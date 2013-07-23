package com.search.manager.utility;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author PGutierr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PropertiesUtils.class)
@SuppressStaticInitializationFor(
        "com.search.manager.utility.PropertiesUtils")
public class PropertiesUtilsTest {

    @Before
    public void init() throws Exception {
        String filePath = "src/test/resources/config/globalvar.properties";
        PropertiesConfiguration config = new PropertiesConfiguration(filePath);
        config.setReloadingStrategy(new FileChangedReloadingStrategy());

        Whitebox.setInternalState(PropertiesUtils.class, "config", config);
    }

    @Test
    public void testGetValue() {
        String splunkdir = PropertiesUtils.getValue("splunkdir");
        assertEquals(splunkdir, "/home/solr/utilities/splunk");

        String topkwdir = PropertiesUtils.getValue("topkwdir");
        assertEquals(topkwdir, "/home/solr/utilities/topkeywords");
    }
}
