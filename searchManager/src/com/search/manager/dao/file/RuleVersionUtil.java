package com.search.manager.dao.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
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

	public static File[] getRuleVersionInfo(String store, RuleEntity ruleEntity, String ruleId) {
		String fileName = ruleId;
		
		switch(ruleEntity){
			case ELEVATE:
			case EXCLUDE:
			case DEMOTE: fileName = StringUtil.escapeKeyword(fileName); break;
		}
		
		File dir = new File(getRuleVersionFileDirectory(store, ruleEntity) + File.separator + StringUtil.escapeKeyword(ruleId));
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

	public static boolean hasVersionCounter(String store, RuleEntity ruleEntity, String ruleId){
		File dir = new File(getRuleVersionFileDirectory(store, ruleEntity));
		File[] verFiles = dir.listFiles(new VersionFileNameFilterImpl(ruleId + VERSION_COUNTER_PREFIX));
		
		if (ArrayUtils.isNotEmpty(verFiles)){
			return true;
		}
		
		return false;
	}
	
	public static void addVersionCounterFile(String store, RuleEntity ruleEntity, String ruleId, int count) throws Exception{
		File dir = new File(getRuleVersionFileDirectory(store, ruleEntity));
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
					if(oFile.exists()){
						if(!oFile.delete()){
							oFile.deleteOnExit();
						}
					}
				}
			}
		}
	}

	public static synchronized int getCurrentVersion(String store, RuleEntity ruleEntity, String ruleId) {
		File dir = new File(getRuleVersionFileDirectory(store, ruleEntity));
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

	public static synchronized int getNextVersion(String store, RuleEntity ruleEntity, String ruleId) {
		return getCurrentVersion(store, ruleEntity, ruleId) + 1;
	}

	public static void deleteFile(String storeName, String ruleId, RuleEntity ruleEntity, int version) throws Exception{
		
		if(!hasVersionCounter(storeName, ruleEntity, ruleId)){
			int currVer = getCurrentVersion(storeName, ruleEntity, ruleId);
			addVersionCounterFile(storeName, ruleEntity, ruleId, currVer);
		}

		deleteFile(getFileName(storeName, ruleEntity, ruleId, version));
	}
	
	public static void deleteFile(String filepath) throws IOException{
		File file = new File(filepath);

		if(file.exists() && !file.delete()){
				file.deleteOnExit();
		}
	}

	public static String getFileName(String store, RuleEntity ruleEntity ,String ruleId, int version){
		StringBuilder filePath = new StringBuilder(getRuleVersionFileDirectory(store, ruleEntity)).append(File.separator).append(ruleId).append(File.separator).append(ruleId).append("__").append(version).append(FileUtil.XML_FILE_TYPE);
		return filePath.toString();
	}

	public static String getFileNameByDir(String dir, String ruleId, int version){
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