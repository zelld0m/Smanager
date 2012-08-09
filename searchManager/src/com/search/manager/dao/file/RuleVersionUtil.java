package com.search.manager.dao.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.search.manager.enums.RuleEntity;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.RuleFileNameFilterImpl;
import com.search.manager.utility.StringUtil;
import com.search.manager.utility.VersionFileNameFilterImpl;

public class RuleVersionUtil {

	public static final Pattern PATTERN = Pattern.compile("__(.*).xml",Pattern.DOTALL);

	private static final String PATH = PropsUtils.getValue("backuppath");
	public static final String VERSION_COUNTER_PREFIX = "VERID";

	public static File[] getBackupInfo(String store, int ruleType, String ruleId) {
		String fileName = ruleId;
		if (RuleEntity.ELEVATE.getCode() == ruleType || RuleEntity.ELEVATE.getCode() == ruleType) {
			fileName = StringUtil.escapeKeyword(fileName);
		}
		File dir = new File(getFileDirectory(store, ruleType));
		File[] files = dir.listFiles(new RuleFileNameFilterImpl(fileName));

		if (ArrayUtils.isNotEmpty(files)){
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
				} 
			});
		}
		return files;
	}

	public static void addVersionCounterFile(String store, int ruleType, String ruleId, int count) throws Exception{
		File dir = new File(getFileDirectory(store, ruleType));
		File[] verFiles = dir.listFiles(new VersionFileNameFilterImpl(ruleId + VERSION_COUNTER_PREFIX));
		
		FileWriter file = null; 

		Arrays.sort(verFiles, new Comparator<File>(){
			public int compare(File f1, File f2) {
				return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			} 
		});
		
		try{
			file = new FileWriter(dir + File.separator + ruleId + VERSION_COUNTER_PREFIX + count);
		}catch(Exception e){
			throw new Exception(e);
		}finally{
			file.close();
			if(file!=null){
				for(File oFile: verFiles){
					oFile.delete();
					oFile.deleteOnExit();
				}
			}
		}
	}

	public static synchronized int getVersionCounter(String store, int ruleType, String ruleId) {
		File dir = new File(getFileDirectory(store, ruleType));
		File[] verFiles = dir.listFiles(new VersionFileNameFilterImpl(ruleId + VERSION_COUNTER_PREFIX));
		File[] ruleFiles = dir.listFiles(new RuleFileNameFilterImpl(ruleId));

		if (!ArrayUtils.isEmpty(verFiles)) {
			Arrays.sort(verFiles, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
				} 
			});
			
			String sCtr = StringUtils.substringAfter(verFiles[0].getName(), ruleId + VERSION_COUNTER_PREFIX);
			
			if (StringUtils.isNotBlank(sCtr) && StringUtils.isNumeric(sCtr)){
				return Integer.parseInt(sCtr);
			}
		}

		if (!ArrayUtils.isEmpty(ruleFiles)) {
			Arrays.sort(ruleFiles, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
				} 
			});

			Matcher matcher = PATTERN.matcher(ruleFiles[0].getName());

			if(matcher.find()){
				return Integer.valueOf(matcher.group(1));
			}			
		}
		
		return 0;
	}

	public static synchronized int getNextVersion(String store, int ruleType, String ruleId) {
		return getVersionCounter(store, ruleType, ruleId) + 1;
	}

	public static void deleteFile(String filepath) throws IOException{
		File file = new File(filepath);

		if(file.exists()){
			if(!file.delete()){
				file.deleteOnExit();
			}
		}
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
