package com.search.properties.manager.service;

import com.search.properties.manager.PropertiesManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since August 30, 2013
 * @version 1.0
 */
@RunWith(PowerMockRunner.class)
public class PropertiesManagerServiceCreateStoreSpecificPropertiesTest {
    @Mock
    private PropertiesManager propertiesManager;
    
    @Test
    public void testCreateStoreSpecificProperties() {
        propertiesManager.createStoreSpecificProperties();
    }
}
