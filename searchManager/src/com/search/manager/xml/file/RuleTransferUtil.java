package com.search.manager.xml.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.PropsUtils;

public class RuleTransferUtil {

	private static Logger logger = Logger.getLogger(RuleTransferUtil.class);
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);
	private static final String IMPORT_FILE_PATH = PropsUtils.getValue("importfilepath");
	private static final String EXPORT_FILE_PATH = PropsUtils.getValue("exportfilepath");

	
	public static List<RuleXml> getAllExportedRules(String store, String ruleType) {
		return (ArrayList<RuleXml>) getRules(store, RuleEntity.find(ruleType), IMPORT_FILE_PATH);
	}
	
	public static RuleXml getRule(String store, RuleEntity ruleEntity, String ruleId){
		return getRule(store, ruleEntity, new File(getFilename(store, ruleEntity, ruleId)), IMPORT_FILE_PATH);
	}
	
	public static RuleXml getRule(String store, RuleEntity ruleEntity, File file, String path){
		try {
			JAXBContext context = JAXBContext.newInstance(RuleXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setEventHandler(new RuleVersionValidationEventHandler());
			if (!file.exists()){
				m.marshal(new RuleXml(), new FileWriter(file));
			}

			Unmarshaller um = context.createUnmarshaller(); 
			um.setEventHandler(new RuleVersionValidationEventHandler());
			return (RuleXml) um.unmarshal(file);

		} catch (JAXBException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		} catch (IOException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		}
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
				ruleXmls.add(ruleXml);
			}
		}

		return ruleXmls;
	}
	
	public static RuleXml getImportRule(String store, RuleEntity ruleEntity, String ruleId){
		RuleXml ruleXml = new RuleXml();
		String dir = RuleXmlUtil.getRuleFileDirectory(IMPORT_FILE_PATH, store, ruleEntity);
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		String filename = RuleXmlUtil.getFilenameByDir(dir, id);

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			try {
				FileUtils.forceMkdir(dirFile);
			} catch (IOException e) {
				logger.error("Unable to create directory", e);
				return null;
			}
		}

		try {
			JAXBContext context = JAXBContext.newInstance(RuleXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setEventHandler(new RuleVersionValidationEventHandler());
			if (!new File(filename).exists()){
				m.marshal(new RuleXml(), new FileWriter(filename));
			}

			Unmarshaller um = context.createUnmarshaller(); 
			um.setEventHandler(new RuleVersionValidationEventHandler());
			ruleXml = (RuleXml) um.unmarshal(new FileReader(filename));

		} catch (JAXBException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		} catch (IOException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		}

		return ruleXml;
	}

	public static boolean exportRuleAsXML(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule){
		String dir = RuleXmlUtil.getRuleFileDirectory(EXPORT_FILE_PATH, store, ruleEntity);
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		String filename = RuleXmlUtil.getFilenameByDir(dir, id);

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			try {
				FileUtils.forceMkdir(dirFile);
			} catch (IOException e) {
				logger.error("Unable to create directory", e);
				return false;
			}
		}

		try {
			JAXBContext context = JAXBContext.newInstance(RuleXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(rule, new FileWriter(filename));
			
			return true;
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return false;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return false;
		}
	}

	public static String getFilename(String store, RuleEntity ruleEntity ,String ruleId){
		return RuleXmlUtil.getFilename(IMPORT_FILE_PATH, store, ruleEntity, ruleId);
	}
}