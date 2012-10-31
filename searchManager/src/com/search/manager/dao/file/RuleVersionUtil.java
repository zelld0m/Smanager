package com.search.manager.dao.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;

public class RuleVersionUtil {

	private static Logger logger = Logger.getLogger(RuleVersionUtil.class);
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);

	private static final String PATH = PropsUtils.getValue("backuppath");
	private static final String ROLLBACK_PREFIX = "rpnv";

	@SuppressWarnings("rawtypes")
	public static RuleVersionListXml getRuleVersionList(String store, RuleEntity ruleEntity, String ruleId){
		RuleVersionListXml ruleVersionListXml = new RuleVersionListXml();
		String dir = getRuleVersionFileDirectory(store, ruleEntity);
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
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setEventHandler(new RuleVersionValidationEventHandler());
			if (!new File(filename).exists()){
				m.marshal(new RuleVersionListXml(), new FileWriter(filename));
			}

			Unmarshaller um = context.createUnmarshaller(); 
			um.setEventHandler(new RuleVersionValidationEventHandler());
			ruleVersionListXml = (RuleVersionListXml) um.unmarshal(new FileReader(filename));

		} catch (JAXBException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		} catch (IOException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		}

		return ruleVersionListXml;
	}

	public static List<RuleVersionInfo> getRuleVersionInfo(String store, RuleEntity ruleEntity, String ruleId) throws Exception {
		return null;
	}

	public static boolean removeRollbackFile(String filename, long version){
		File rollbackFile = new File(filename + ROLLBACK_PREFIX + version);
		return FileUtils.deleteQuietly(rollbackFile);
	}

	public static boolean createRollbackFile(String filename, long version){
		try {
			File file = new File(filename);
			File rollbackFile = new File(filename + ROLLBACK_PREFIX + version);

			if (!rollbackFile.exists()){
				FileUtils.copyFile(file, rollbackFile, true);
				if (rollbackFile.exists()) return true;
			}else{
				return false;
			}
		} catch (IOException e) {
			logger.error("Unable to create rollback file", e);
			return false;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return false;
		} 
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean addRuleVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList){
		String dir = getRuleVersionFileDirectory(store, ruleEntity);
		String id = ruleId;
		long nextVersion = ruleVersionList.getNextVersion();

		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: id = StringUtil.escapeKeyword(ruleId); break;
		}
		
		String filename = getFileNameByDir(dir, id);

		if (!createRollbackFile(filename, nextVersion)){
			logger.error("Unable to create rollback file");
			return false;
		};

		try {
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ruleVersionList.setNextVersion(nextVersion + 1);
			m.marshal(ruleVersionList, new FileWriter(filename));
			if (!removeRollbackFile(filename, nextVersion)){
				logger.info(String.format("Failed to delete rollback file for next version %l", nextVersion));
			};
			return true;
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller", e);
			return false;
		} catch (Exception e) {
			logger.error("Unknown error", e);
			return false;
		}
	}

	public static void deleteFile(String filepath) throws IOException{
		File file = new File(filepath);

		if(file.exists() && !file.delete()){
			file.deleteOnExit();
		}
	}

	public static String getFileName(String store, RuleEntity ruleEntity ,String ruleId){
		StringBuilder filePath = new StringBuilder(getRuleVersionFileDirectory(store, ruleEntity)).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getFileNameByDir(String dir, String ruleId){
		StringBuilder filePath = new StringBuilder(dir).append(File.separator).append(ruleId).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getRuleVersionFileDirectory(String store, RuleEntity ruleEntity){
		StringBuilder sb = new StringBuilder();
		List<String> values = ruleEntity.getValues(); 
		String directory = CollectionUtils.isNotEmpty(values)? values.get(0): ruleEntity.name();
		sb.append(PATH).append(File.separator).append(store).append(File.separator).append(directory);
		return sb.toString();
	}
}