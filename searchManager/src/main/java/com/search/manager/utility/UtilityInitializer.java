package com.search.manager.utility;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.schema.MessagesConfig;
import com.search.manager.schema.RelevancyConfig;
//import com.search.ws.ConfigManager;
import com.search.ws.EnterpriseSearchConfigManager;

public class UtilityInitializer {

    private static final Logger logger = LoggerFactory.getLogger(UtilityInitializer.class);
    
    private String enterpriseSearchConfigFile;
    private String relevancyConfigFile;
    private String messagesConfigFile;

    public void setEnterpriseSearchConfigFile(String enterpriseSearchConfigFile) {
        this.enterpriseSearchConfigFile = enterpriseSearchConfigFile;
    }

    public void setRelevancyConfigFile(String relevancyConfigFile) {
        this.relevancyConfigFile = relevancyConfigFile;
    }

    public void setMessagesConfigFile(String messagesConfigFile) {
        this.messagesConfigFile = messagesConfigFile;
    }

    public void initialize() {
        // enterprise search config
        if (StringUtils.isNotBlank(enterpriseSearchConfigFile)) {
            logger.info("enterprise search config file: {}", enterpriseSearchConfigFile);
            EnterpriseSearchConfigManager.getInstance(enterpriseSearchConfigFile);
        }

        // relevancy config
        if (StringUtils.isNotBlank(relevancyConfigFile)) {
            logger.info("relevancy config file: {}", relevancyConfigFile);
            RelevancyConfig.getInstance(relevancyConfigFile);
        }

        // messages config
        if (StringUtils.isNotBlank(messagesConfigFile)) {
            logger.info("messages config file: {}", messagesConfigFile);
            MessagesConfig.getInstance(messagesConfigFile);
        }
    }
}