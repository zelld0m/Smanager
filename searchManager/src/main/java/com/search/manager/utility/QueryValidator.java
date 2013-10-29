package com.search.manager.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.search.ExtendedDismaxQParser.ExtendedSolrQueryParser;
import org.apache.solr.search.QParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryValidator {

    private static final Logger logger = LoggerFactory.getLogger(QueryValidator.class);
    private static CoreContainer container;

    public static void init() throws FileNotFoundException {
        container = new CoreContainer("/home/solr/solrworks/solr4", new File("/home/solr/solrworks/solr4/solr.xml"));
    }

    public static boolean accept(String storeId, Map<String, String[]> params) {
        try {
            SolrCore core = container.getCore(storeId);
            LocalSolrQueryRequest request = new LocalSolrQueryRequest(core, params);
            QParser qparser = QParser.getParser(null, "edismax", request);
            ExtendedSolrQueryParser parser = new ExtendedSolrQueryParser(qparser, "EDP");

            parser.parse(request.getParamString());
        } catch (Exception e) {
            logger.warn("Invalid solr query.", e);
            return false;
        }

        return true;
    }
}
