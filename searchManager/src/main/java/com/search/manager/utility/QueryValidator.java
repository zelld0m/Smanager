package com.search.manager.utility;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryValidator {

    private static final Logger logger = LoggerFactory.getLogger(QueryValidator.class);
    private static final String DEFAULT_HANDLER = "select";
    private static final String DEFAULT_SOLR_DIR = "/home/solr/solrworks/solr4";
    private static final String DEFAULT_SOLR_CONF = "solr.xml";

    private static CoreContainer container;

    public static void init() throws FileNotFoundException {
        if (BooleanUtils.toBoolean(PropertiesUtils.getValue("enableQueryValidator")) && container == null) {
            String solrDir = PropertiesUtils.getValue("queryValidatorSolrDir");
            String solrConf = PropertiesUtils.getValue("queryValidatorSolrConf");

            if (StringUtils.isBlank(solrDir)) {
                solrDir = DEFAULT_SOLR_DIR;
            }

            if (StringUtils.isBlank(solrConf)) {
                solrConf = DEFAULT_SOLR_CONF;
            }

            container = new CoreContainer(solrDir, new File(solrDir, solrConf));
        }
    }

    public static boolean accept(String storeId, HttpServletRequest originalRequest) {
        if (BooleanUtils.toBoolean(PropertiesUtils.getValue("enableQueryValidator"))) {
            if (container == null) {
                setEnabled(true);
            }

            if (container != null) {
                try {
                    SolrCore core = container.getCore(storeId);
                    LocalSolrQueryRequest request = new LocalSolrQueryRequest(core, originalRequest.getParameterMap());
                    SolrQueryResponse response = new SolrQueryResponse();
                    String handler = PropertiesUtils.getValue("queryValidatorHandler");

                    if (StringUtils.isBlank(handler)) {
                        handler = DEFAULT_HANDLER;
                    }

                    core.getRequestHandler(handler).handleRequest(request, response);

                    if (response.getException() != null) {
                        logger.warn("Invalid solr query: " + originalRequest.getRequestURL(), response.getException());
                        return false;
                    }
                } catch (Exception e) {
                    logger.error("Invalid solr query: " + originalRequest.getRequestURL(), e);
                    return false;
                }
            }
        } else if (container != null) {
            setEnabled(false);
        }

        return true;
    }

    public static void shutdown() {
        if (container != null) {
            container.shutdown();
            container = null;
        }
    }

    public static void setEnabled(boolean enabled) {
        if (!enabled && container != null) {
            shutdown();
        } else if (enabled && container == null) {
            try {
                init();
            } catch (FileNotFoundException e) {
                logger.warn("Unable to start container.", e);
            }
        }
    }
}
