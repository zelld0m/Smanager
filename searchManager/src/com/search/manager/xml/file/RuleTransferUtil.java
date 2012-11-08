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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;

public class RuleTransferUtil {

	private static Logger logger = Logger.getLogger(RuleTransferUtil.class);
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);
	private static final String IMPORT_FILE_PATH = PropsUtils.getValue("importfilepath");

	
	public static List<RuleXml> getAllExportedRules(String store, String ruleType) {
		return (ArrayList<RuleXml>) getRules(store, RuleEntity.find(ruleType), IMPORT_FILE_PATH);
	}
	
	public static RuleXml getRule(String store, RuleEntity ruleEntity, String ruleId){
		return getRule(store, ruleEntity, new File(getFileName(store, ruleEntity, ruleId, IMPORT_FILE_PATH)), IMPORT_FILE_PATH);
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
	
	@SuppressWarnings("rawtypes")
	public static List<RuleXml> getRules(String store, RuleEntity ruleEntity, String path){
		List<RuleXml> ruleXmls = new ArrayList<RuleXml>();
		String dir = getFileDirectory(store, ruleEntity, path);

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
	
	@SuppressWarnings("rawtypes")
	public static RuleXml getImportRule(String store, RuleEntity ruleEntity, String ruleId){
		RuleXml ruleXml = new RuleXml();
		String dir = getFileDirectory(store, ruleEntity, IMPORT_FILE_PATH);
		String id = ruleId;

		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: id = StringUtil.escapeKeyword(ruleId); break;
		}
		
		String filename = getFileNameByDir(dir, id);

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

	@SuppressWarnings("rawtypes")
	public static boolean exportRuleAsXML(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule){
		String dir = getFileDirectory(store, ruleEntity, IMPORT_FILE_PATH);
		String id = ruleId;
		
		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: id = StringUtil.escapeKeyword(ruleId); break;
		}
		
		String filename = getFileNameByDir(dir, id);
		
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

	public static String getFileName(String store, RuleEntity ruleEntity ,String ruleId){
		StringBuilder filePath = new StringBuilder(getFileDirectory(store, ruleEntity, IMPORT_FILE_PATH)).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}
	
	public static String getFileName(String store, RuleEntity ruleEntity ,String ruleId, String path){
		StringBuilder filePath = new StringBuilder(getFileDirectory(store, ruleEntity, path)).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getFileNameByDir(String dir, String ruleId){
		StringBuilder filePath = new StringBuilder(dir).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getFileDirectory(String store, RuleEntity ruleEntity, String path){
		StringBuilder sb = new StringBuilder();
		List<String> values = ruleEntity.getValues(); 
		String directory = CollectionUtils.isNotEmpty(values)? values.get(0): ruleEntity.name();
		sb.append(path).append(File.separator).append(store).append(File.separator).append(directory);
		return sb.toString();
	}
}