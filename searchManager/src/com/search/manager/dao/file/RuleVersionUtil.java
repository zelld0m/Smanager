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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleFileXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionValidationEventHandler;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.PropsUtils;
import com.search.manager.xml.file.RuleXmlUtil;

public class RuleVersionUtil {

	private static Logger logger = Logger.getLogger(RuleVersionUtil.class);
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);
	private static final String BACKUP_PATH = PropsUtils.getValue("backuppath");
	private static final String PUBLISH_PATH = PropsUtils.getValue("publishedfilepath");
	private static final String ROLLBACK_PREFIX = "rpnv";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static RuleXml getRuleVersion(String store, RuleEntity ruleEntity, String ruleId, int version){
		RuleVersionListXml ruleVersionListXml = RuleVersionUtil.getRuleVersionList(store, ruleEntity, ruleId);
		if (ruleVersionListXml != null) {
			List<RuleXml> ruleXmlList = (List<RuleXml>)ruleVersionListXml.getVersions();
			
			for(RuleXml xml: ruleXmlList){
				if(xml.getVersion()== version){
					return xml;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private static RuleVersionListXml getRuleList(String store, RuleEntity ruleEntity, String ruleId, String location){
		RuleVersionListXml ruleVersionListXml = new RuleVersionListXml();
		String dir = RuleXmlUtil.getRuleFileDirectory(location, store, ruleEntity);
		String filename = RuleXmlUtil.getFilenameByDir(
				dir, 
				RuleXmlUtil.getRuleId(ruleEntity, ruleId));

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			try {
				FileUtils.forceMkdir(dirFile);
			} catch (IOException e) {
				logger.error("Unable to create directory", e);
				return null;
			}
		}
		
		FileReader reader = null;
		try {
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Unmarshaller um = context.createUnmarshaller(); 
			um.setEventHandler(new RuleVersionValidationEventHandler());
			File file = new File(filename);
			if (file.exists()) {
				reader = new FileReader(filename);
				ruleVersionListXml = (RuleVersionListXml) um.unmarshal(reader);
			}
			else{
				logger.info("File not found: " + filename);
			}
		} catch (JAXBException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		} catch (IOException e) {
			logger.error("Unable to create marshaller/unmarshaller", e);
			return null;
		} finally {
			try { if (reader != null) reader.close(); } catch (Exception e) {}
		}

		return ruleVersionListXml;
	}

	@SuppressWarnings("rawtypes")
	public static RuleVersionListXml getRuleVersionList(String store, RuleEntity ruleEntity, String ruleId){
		return getRuleList(store, ruleEntity, ruleId, BACKUP_PATH);
	}
	
	@SuppressWarnings("rawtypes")
	public static RuleVersionListXml getPublishedList(String store, RuleEntity ruleEntity, String ruleId){
		return getRuleList(store, ruleEntity, ruleId, PUBLISH_PATH);
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
				if (file.exists()) {
					FileUtils.copyFile(file, rollbackFile, true);
				}
				else {
					FileUtils.write(rollbackFile, "");
				}
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
	private static boolean addRuleVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList, String location){
		long nextVersion = ruleVersionList.getNextVersion();
		FileWriter writer = null;
		String filename = RuleXmlUtil.getFilenameByDir(
				RuleXmlUtil.getRuleFileDirectory(location, store, ruleEntity), 
				RuleXmlUtil.getRuleId(ruleEntity, ruleId));

		if (!createRollbackFile(filename, nextVersion)){
			logger.error("Unable to create rollback file");
			return false;
		};

        List versions = ruleVersionList.getVersions();
        Object latest = versions.get(versions.size() - 1);

        if (latest instanceof RuleFileXml) {
            RuleFileXml latestVersion = (RuleFileXml) latest;
            String contentFileName =  RuleXmlUtil.getFilenameByDir(
                    RuleXmlUtil.getRuleFileDirectory(location, store, ruleEntity), latestVersion.getContentFileName());
            latestVersion.setPath(contentFileName);

            if (!RuleXmlUtil.saveRuleXml(latestVersion.getContent(), contentFileName, nextVersion)) {
                return false;
            }
        }

		try {
			JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ruleVersionList.setNextVersion(nextVersion + 1);
			writer = new FileWriter(filename);
			m.marshal(ruleVersionList, writer);
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
		} finally {
			try { if (writer != null) writer.close(); } catch (Exception e) {}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean addRuleVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList){
		return addRuleVersion(store, ruleEntity, ruleId, ruleVersionList, BACKUP_PATH);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean addPublishedVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList){
		return addRuleVersion(store, ruleEntity, ruleId, ruleVersionList, PUBLISH_PATH);
	}
	
	private static String getFilename(String store, RuleEntity ruleEntity, String ruleId, String location) {
		return RuleXmlUtil.getFilename(location, store, ruleEntity, ruleId);
	}
	
	public static String getRuleVersionFilename(String store, RuleEntity ruleEntity, String ruleId) {
		return getFilename(store, ruleEntity, ruleId, BACKUP_PATH);
	}
	
	public static String getPublishedFilename(String store, RuleEntity ruleEntity, String ruleId) {
		return getFilename(store, ruleEntity, ruleId, PUBLISH_PATH);
	}
	
}