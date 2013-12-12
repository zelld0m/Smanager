package com.search.manager.xml.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.ProductDetailsAware;
import com.search.manager.report.model.xml.RuleItemXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.PropertiesUtils;

@Component
public class RuleTransferUtil {

    private static final Logger logger = LoggerFactory.getLogger(RuleTransferUtil.class);
    
    public static final Pattern PATTERN = Pattern.compile("__(.*).xml", Pattern.DOTALL);
    private static final String IMPORT_FILE_PATH = PropertiesUtils.getValue("importfilepath");
    
    @Autowired
    private RuleXmlUtil ruleXmlUtil;
    @Autowired
    private UtilityService utilityService;
    
    public List<RuleXml> getAllExportedRules(String storeId, String ruleType) {
        return (ArrayList<RuleXml>) getRules(storeId, RuleEntity.find(ruleType), IMPORT_FILE_PATH);
    }

    public RuleXml getRuleToImport(String storeId, RuleEntity ruleEntity, String ruleId) {
        return getRule(storeId, ruleEntity, ruleId, IMPORT_FILE_PATH);
    }

    @SuppressWarnings("unchecked")
    public RuleXml getRule(String storeId, RuleEntity ruleEntity, String ruleId, String path) {
        RuleXml ruleXml = getRule(storeId, ruleEntity, new File(getFilename(storeId, ruleEntity, ruleId)), path);

        if (ruleXml instanceof ElevateRuleXml || ruleXml instanceof ExcludeRuleXml || ruleXml instanceof DemoteRuleXml) {
            ProductDetailsAware productDetailsAware = (ProductDetailsAware) ruleXml;
            productDetailsAware.setProducts(ruleXmlUtil.getProductDetails(ruleXml, storeId));
        
            List<RuleItemXml> ruleItemXmlList = (List<RuleItemXml>) productDetailsAware.getItem();

			if(ruleItemXmlList!=null){
				for(RuleItemXml ruleItemXml: ruleItemXmlList){
					RedirectRuleCondition rrc = ruleItemXml.getRuleCondition();
					if(rrc!=null) rrc.setFacetValues(utilityService.getStoreFacetPrefix(storeId), utilityService.getStoreFacetTemplate(storeId), utilityService.getStoreFacetTemplateName(storeId));
				}
			} 
        }
        
        return ruleXml;
    }

    public RuleXml getRule(String storeId, RuleEntity ruleEntity, File file, String path) {
        try {
            if (file != null && file.exists()) {
                JAXBContext context = JAXBContext.newInstance(RuleXml.class);
                Unmarshaller um = context.createUnmarshaller();
                um.setEventHandler(new RuleVersionValidationEventHandler());
                return (RuleXml) um.unmarshal(file);
            }
            logger.warn(String.format("File %s does not exist", file));
        } catch (JAXBException e) {
            logger.error("Unable to create unmarshaller", e);
        }
        return null;
    }

    public List<RuleXml> getRules(String storeId, RuleEntity ruleEntity, String path) {
        List<RuleXml> ruleXmls = new ArrayList<RuleXml>();
        String dir = ruleXmlUtil.getRuleFileDirectory(IMPORT_FILE_PATH, storeId, ruleEntity);

        File dirFile = new File(dir);

        if (!dirFile.exists()) {
            try {
                FileUtils.forceMkdir(dirFile);
                return ruleXmls;
            } catch (IOException e) {
                logger.error("Unable to create directory", e);
                return null;
            }
        }

        //retrieve all files from directory
        File[] listOfFiles = dirFile.listFiles();

        for (File file : listOfFiles) {
            RuleXml ruleXml = getRule(storeId, ruleEntity, file, path);

            if (ruleXml != null) {
                if (ruleXml instanceof ElevateRuleXml || ruleXml instanceof ExcludeRuleXml || ruleXml instanceof DemoteRuleXml) {
                    ProductDetailsAware productDetailsAware = (ProductDetailsAware) ruleXml;
                    productDetailsAware.setProducts(ruleXmlUtil.getProductDetails(ruleXml, storeId));
                    ruleXmls.add((RuleXml) productDetailsAware);
                } else {
                    ruleXmls.add(ruleXml);
                }
            }
        }

        return ruleXmls;
    }

    public boolean exportRule(String targetStore, RuleEntity ruleEntity, String ruleId, RuleXml rule) {
        logger.info(String.format("Exporting rule xml... [store = %s, ruleId = %s, path = %s]", targetStore, ruleId, IMPORT_FILE_PATH));
        return ruleXmlUtil.ruleXmlToFile(targetStore, ruleEntity, ruleId, rule, IMPORT_FILE_PATH);
    }

    public boolean importRule(String storeId, String ruleId, RuleXml ruleXml) {
        logger.info(String.format("Importing rule xml... [store = %s, ruleId = %s]", storeId, ruleId));
        return ruleXmlUtil.importRule(ruleXml);
    }

    public String getFilename(String storeId, RuleEntity ruleEntity, String ruleId) {
        return ruleXmlUtil.getFilename(IMPORT_FILE_PATH, storeId, ruleEntity, ruleId);
    }

    public boolean deleteRuleFile(RuleEntity ruleEntity, String storeId, String ruleId, String comment) {
        boolean success = false;
        String id = ruleXmlUtil.getRuleId(ruleEntity, ruleId);
        try {
            String filepath = getFilename(storeId, ruleEntity, id);
            File file = new File(filepath);

            logger.info(String.format("Trying to delete file [%s]", filepath));
            if (!file.exists()) {
                logger.info("File to delete not found. Filename = " + filepath);
            } else {
                ruleXmlUtil.deleteFile(filepath);
                success = true;
                logger.info(String.format("File [%s] has been deleted.", filepath));
            }
        } catch (Exception e) {
            logger.error("Failed to delete rule file", e);
        }
        return success;
    }
}