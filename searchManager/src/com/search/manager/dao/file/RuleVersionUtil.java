package com.search.manager.dao.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StringUtil;

public class RuleVersionUtil {

	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);

	private static final String PATH = PropsUtils.getValue("backuppath");
	
	@SuppressWarnings("rawtypes")
	public static RuleVersionListXml getRuleVersionFile(String store, RuleEntity ruleEntity, String ruleId) throws Exception {

		JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
		String dir = getRuleVersionFileDirectory(store, ruleEntity);
		String filename = ruleId;

		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: filename = getFileNameByDir(dir, StringUtil.escapeKeyword(ruleId)); break;
		}

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		if (!FileUtil.isDirectoryExist(dir)) {
			FileUtil.createDirectory(dir);
		}
		
		if (!FileUtil.isExist(filename)){
			m.marshal(new RuleVersionListXml(), new FileWriter(filename));
		}

		Unmarshaller um = context.createUnmarshaller(); 
		
		return (RuleVersionListXml) um.unmarshal(new FileReader(filename));
	}
	
	public static List<RuleVersionInfo> getRuleVersionInfo(String store, RuleEntity ruleEntity, String ruleId) throws Exception {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static boolean addRuleVersion(String store, RuleEntity ruleEntity, String ruleId, RuleVersionListXml ruleVersionList) throws Exception {
		JAXBContext context = JAXBContext.newInstance(RuleVersionListXml.class);
		String dir = getRuleVersionFileDirectory(store, ruleEntity);
		String filename = ruleId;

		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE: filename = getFileNameByDir(dir, StringUtil.escapeKeyword(ruleId)); break;
		}
		
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		ruleVersionList.setNextVersion(ruleVersionList.getNextVersion() + 1);
		m.marshal(ruleVersionList, new FileWriter(filename));

		return false;
	}

	public static void deleteRuleVersionFile(String store, RuleEntity ruleEntity, String ruleId) throws IOException{
		deleteFile(getFileName(store, ruleEntity, ruleId));
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