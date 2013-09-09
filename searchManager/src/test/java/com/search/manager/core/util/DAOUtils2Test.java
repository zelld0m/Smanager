package com.search.manager.core.util;

import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since September 09, 2013
 * @version 1.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtils2.class)
@PowerMockIgnore("javax.management.*")
public class DAOUtils2Test {

    private final String expectedUniqueID = "819369d0-18f6-11e3-95bf-002655429c36";

    @Before
    public void setup() {
        PowerMock.mockStatic(DAOUtils2.class);

        EasyMock.expect(DAOUtils2.generateUniqueId()).andReturn(expectedUniqueID);
    }

    @Test
    public void testGenerateUniqueId() {
        PowerMock.replay(DAOUtils2.class);
        String generateUniqueId = DAOUtils2.generateUniqueId();
        PowerMock.verify(DAOUtils2.class);
        assertEquals(expectedUniqueID, generateUniqueId);
    }
}
