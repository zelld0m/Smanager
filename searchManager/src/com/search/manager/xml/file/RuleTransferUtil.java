package com.search.manager.xml.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleVersionXml;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;

public class RuleTransferUtil {

	private static Logger logger = Logger.getLogger(RuleTransferUtil.class);
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);
	private static final String IMPORT_FILE_PATH = PropsUtils.getValue("importfilepath");

	
	public static List<RuleVersionXml> getAllExportedRules(String store, String ruleType) {
		return (ArrayList<RuleVersionXml>) getRules(store, RuleEntity.find(ruleType), null, IMPORT_FILE_PATH);
	}
	
	@SuppressWarnings("rawtypes")
	public static List<RuleVersionXml> getRules(String store, RuleEntity ruleEntity, String ruleId, String path){
		List<RuleVersionXml> ruleXmls = new ArrayList<RuleVersionXml>();
		String dir = getFileDirectory(store, ruleEntity, path);
		String id = ruleId;

		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: id = StringUtil.escapeKeyword(ruleId); break;
		}
		
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
		//TODO File[] listOfFiles = StringUtils.isNotBlank(id) ? new File(getFileNameByDir(dir, id)): dirFile.listFiles();
		File[] listOfFiles = dirFile.listFiles();
		
		for(File file : listOfFiles){
			try {
				JAXBContext context = JAXBContext.newInstance(RuleVersionXml.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.setEventHandler(new RuleVersionValidationEventHandler());
				if (!file.exists()){
					m.marshal(new RuleVersionXml(), new FileWriter(file));
				}
	
				Unmarshaller um = context.createUnmarshaller(); 
				um.setEventHandler(new RuleVersionValidationEventHandler());
				ruleXmls.add((RuleVersionXml) um.unmarshal(file));
	
			} catch (JAXBException e) {
				logger.error("Unable to create marshaller/unmarshaller", e);
				return null;
			} catch (IOException e) {
				logger.error("Unable to create marshaller/unmarshaller", e);
				return null;
			}
		}

		return ruleXmls;
	}
	
	@SuppressWarnings("rawtypes")
	public static RuleVersionXml getImportRule(String store, RuleEntity ruleEntity, String ruleId){
		RuleVersionXml ruleXml = new RuleVersionXml();
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
			JAXBContext context = JAXBContext.newInstance(RuleVersionXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setEventHandler(new RuleVersionValidationEventHandler());
			if (!new File(filename).exists()){
				m.marshal(new RuleVersionXml(), new FileWriter(filename));
			}

			Unmarshaller um = context.createUnmarshaller(); 
			um.setEventHandler(new RuleVersionValidationEventHandler());
			ruleXml = (RuleVersionXml) um.unmarshal(new FileReader(filename));

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
	public static boolean exportRuleAsXML(String store, RuleEntity ruleEntity, String ruleId, RuleVersionXml rule){
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
			JAXBContext context = JAXBContext.newInstance(RuleVersionXml.class);
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