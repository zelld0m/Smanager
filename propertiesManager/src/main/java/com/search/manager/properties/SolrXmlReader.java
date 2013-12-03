package com.search.manager.properties;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

/**
 * Class for reading the solr.xml properties file
 *
 * @author Philip Mark Gutierrez
 * @since Sep 27, 2013
 * @version 1.0
 */
public class SolrXmlReader {

    private static final Logger logger = Logger.getLogger(SolrXmlReader.class.getName());
    private String storeSolrPropertiesFile;
    private XMLConfiguration xmlConfig;

    public SolrXmlReader(String storeSolrPropertiesFile) {
        this.storeSolrPropertiesFile = storeSolrPropertiesFile;
        
        // load the solr.xml file
        init();
    }

    /**
     * Helper method for initializing the solr.xml
     */
    private void init() {
        xmlConfig = new XMLConfiguration();
        xmlConfig.setDelimiterParsingDisabled(true);
        xmlConfig.setExpressionEngine(new XPathExpressionEngine());
        xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());

        try {
            xmlConfig.load(storeSolrPropertiesFile);
        } catch (ConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private List<String> getStoreAttributes(String attrName, boolean hasXmlTag) {
        List<String> storeAttrib = new ArrayList<String>();
        List<HierarchicalConfiguration> hcList = xmlConfig.configurationsAt("/store");
        
        for (HierarchicalConfiguration hc : hcList) {
            String attrib = "@" + attrName;
            
            if (hasXmlTag) {
                attrib = attrName;
            }
            
            String attrValue = hc.getString(attrib);
            
            if (!Strings.isNullOrEmpty(attrValue)) {
                storeAttrib.add(attrValue);
            }
        }
        
        return storeAttrib;
    }

    public List<String> getStoreIds() {
        return getStoreAttributes("id", false);
    }
}
