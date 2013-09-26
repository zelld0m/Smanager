package com.search.manager.utility;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
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
@PowerMockIgnore("javax.management.*")
@SuppressStaticInitializationFor("com.search.manager.utility.PropertiesUtils")
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

    /*
     * This tests shows the differences in behavior between old
     * PropsUtil#getValue implementation and the new PropertiesUtils#getValue
     * method. Old implementation changes periods (.) in keys to underscore
     * while the new implementation does not. Furthermore, values with comma are
     * treated as list of values in the current implementation and the method
     * getValue only returns the first value. For String values that contain
     * commas, these should be escaped with backslashes.
     */
    @Test
    public void testGetValueUsingKeysWithPeriod() {
        assertEquals("testValue1", PropertiesUtils.getValue("test.property.1"));
        assertNull(PropertiesUtils.getValue("test.property.2"));
    }

    @Test
    public void testGetValueUsingKeysWithUnderscore() {
        assertNull(PropertiesUtils.getValue("test_property_1"));
        assertEquals("testValue2", PropertiesUtils.getValue("test_property_2"));
    }

    @Test
    public void testGetPropertyWithUnescapedComma() {
        // Retrieves only the first value.
        // In file, test.property.with.comma1=value1,value2
        assertEquals("value1", PropertiesUtils.getValue("test.property.with.comma1"));
    }

    @Test
    public void testGetPropertyWithEscapedComma() {
        // Retrieves the complete string.
        // In file, test.property.with.comma2=value1\,value2
        assertEquals("value1,value2", PropertiesUtils.getValue("test.property.with.comma2"));
    }
}
