package com.search.properties.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Sep 27, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class SolrXmlReaderTest {
    @Autowired
    private SolrXmlReader solrXmlReader;
    
    @Test
    public void testGetStoreIds() {
        List<String> storeIds = solrXmlReader.getStoreIds();
        
        assertNotNull(storeIds);
        
        assertTrue(storeIds.contains("pcmall"));
        assertFalse(storeIds.contains("macmall"));
    }
}
