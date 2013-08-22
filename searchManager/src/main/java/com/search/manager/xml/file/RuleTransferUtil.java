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
import org.apache.log4j.Logger;

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
import com.search.manager.utility.PropsUtils;

public class RuleTransferUtil {

	private static Logger logger = Logger.getLogger(RuleTransferUtil.class);
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);
	private static final String IMPORT_FILE_PATH = PropsUtils.getValue("importfilepath");

	public static List<RuleXml> getAllExportedRules(String store, String ruleType) {
		return (ArrayList<RuleXml>) getRules(store, RuleEntity.find(ruleType), IMPORT_FILE_PATH);
	}

	public static RuleXml getRuleToImport(String store, RuleEntity ruleEntity, String ruleId){
		return getRule(store, ruleEntity, ruleId, IMPORT_FILE_PATH);
	}

	@SuppressWarnings("unchecked")
	public static RuleXml getRule(String store, RuleEntity ruleEntity, String ruleId, String path){
		RuleXml ruleXml = getRule(store, ruleEntity, new File(getFilename(store, ruleEntity, ruleId)), path);

		if(ruleXml instanceof ElevateRuleXml || ruleXml instanceof ExcludeRuleXml || ruleXml instanceof DemoteRuleXml){
			ProductDetailsAware productDetailsAware = (ProductDetailsAware) ruleXml;
			productDetailsAware.setProducts(RuleXmlUtil.getProductDetails(ruleXml, store));

			List<RuleItemXml> ruleItemXmlList = (List<RuleItemXml>) productDetailsAware.getItem();

			if(ruleItemXmlList!=null){
				for(RuleItemXml ruleItemXml: ruleItemXmlList){
					RedirectRuleCondition rrc = ruleItemXml.getRuleCondition();
					if(rrc!=null) rrc.setFacetValues(UtilityService.getStoreFacetPrefix(), UtilityService.getStoreFacetTemplate(), UtilityService.getStoreFacetTemplateName());
				}
			} 
		}

		return ruleXml;
	}

	public static RuleXml getRule(String store, RuleEntity ruleEntity, File file, String path){
		try {
			if (file != null  && file.exists()) {
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


	public static List<RuleXml> getRules(String store, RuleEntity ruleEntity, String path){
		List<RuleXml> ruleXmls = new ArrayList<RuleXml>();
		String dir = RuleXmlUtil.getRuleFileDirectory(IMPORT_FILE_PATH, store, ruleEntity);

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

		for(File file : listOfFiles){
			RuleXml ruleXml = getRule(store, ruleEntity, file, path);

			if(ruleXml != null){
				if(ruleXml instanceof ElevateRuleXml || ruleXml instanceof ExcludeRuleXml || ruleXml instanceof DemoteRuleXml){
					ProductDetailsAware productDetailsAware = (ProductDetailsAware) ruleXml;
					productDetailsAware.setProducts(RuleXmlUtil.getProductDetails(ruleXml, store));
					ruleXmls.add((RuleXml) productDetailsAware);
				}else{
					ruleXmls.add(ruleXml);
				}
			}
		}

		return ruleXmls;
	}

	public static boolean exportRule(String targetStore, RuleEntity ruleEntity, String ruleId, RuleXml rule){
		logger.info(String.format("Exporting rule xml... [store = %s, ruleId = %s, path = %s]", targetStore, ruleId, IMPORT_FILE_PATH));
		return RuleXmlUtil.ruleXmlToFile(targetStore, ruleEntity, ruleId, rule, IMPORT_FILE_PATH);
	}

	public static boolean importRule(String store, String ruleId, RuleXml ruleXml){
		logger.info(String.format("Importing rule xml... [store = %s, ruleId = %s]", store, ruleId));
		return RuleXmlUtil.importRule(ruleXml);
	}

	public static String getFilename(String store, RuleEntity ruleEntity ,String ruleId){
		return RuleXmlUtil.getFilename(IMPORT_FILE_PATH, store, ruleEntity, ruleId);
	}

	public static boolean deleteRuleFile(RuleEntity ruleEntity, String store, String ruleId, String comment){
		boolean success = false;
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		try{
			String filepath = RuleTransferUtil.getFilename(store, ruleEntity, id);
			File file = new File(filepath);

			logger.info(String.format("Trying to delete file [%s]", filepath));
			if(!file.exists()){
				logger.info("File to delete not found. Filename = " + filepath);
			}
			else{
				RuleXmlUtil.deleteFile(filepath);
				success = true;
				logger.info(String.format("File [%s] has been deleted.", filepath));
			}
		}
		catch (Exception e) {
			logger.error("Failed to delete rule file", e);
		}
		return success;
	}
}
