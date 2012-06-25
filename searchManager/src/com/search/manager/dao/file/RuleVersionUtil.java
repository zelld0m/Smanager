package com.search.manager.dao.file;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.search.manager.enums.RuleEntity;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.RuleFileNameFilterImpl;

public class RuleVersionUtil {
	
	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);

	private static final String PATH = PropsUtils.getValue("backuppath");
	
	public static File[] getBackupInfo(String store, int ruleType, String ruleId) {
		String fileName = ruleId;
		if (RuleEntity.ELEVATE.getCode() == ruleType || RuleEntity.ELEVATE.getCode() == ruleType) {
			fileName = UtilityService.escapeKeyword(fileName);
		}
		File dir = new File(getFileDirectory(store, ruleType));
		File[] files = dir.listFiles(new RuleFileNameFilterImpl(fileName));

		return files;
	}
	
	public static synchronized int getNextVersion(String store, int ruleType, String ruleId) {
		int version = 0;
		File dir = new File(getFileDirectory(store, ruleType));
		File[] files = dir.listFiles(new RuleFileNameFilterImpl(ruleId));
		if (files !=null && files.length > 0) {
			Arrays.sort(files, new Comparator<File>(){
			    public int compare(File f1, File f2) {
			        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			    } 
		    });

	        Matcher matcher = PATTERN.matcher(files[0].getName());
	        if(matcher.find()){
	        	version = Integer.valueOf(matcher.group(1));
	        }

		}
		return ++version;
	}

	
	public static String getFileName(String store, int ruleType ,String ruleId, int version){
		StringBuilder filePath = new StringBuilder(getFileDirectory(store, ruleType)).append(File.separator).append(ruleId).append("__").append(version).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}
	
	public static String getFileNameByDir(String dir, String ruleId, int version){
		StringBuilder filePath = new StringBuilder(dir).append(File.separator).append(ruleId).append("__").append(version).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}
	
	public static String getFileDirectory(String store, int ruleType){
		StringBuilder dir = new StringBuilder();
		dir.append(PATH).append(File.separator).append(store).append(File.separator).append(ruleType);
		return dir.toString();
	}
}
