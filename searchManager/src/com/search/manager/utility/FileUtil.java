package com.search.manager.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;


public class FileUtil {
	
	public static final String XML_FILE_TYPE = ".xml";
	private static Logger logger = Logger.getLogger(FileUtil.class);

	public static void createDirectory(String basePath,String directoryName) throws Exception{
		try{
			if(isBlank(basePath)) throw new Exception("basePath is null.");
			if("".equals(directoryName.trim()) || null == directoryName.trim()) throw new Exception("directoryName is null.");
			createDirectory(basePath+directoryName);
		}catch(Exception e){
			throw new Exception(e);
		}
	}

	public static void createDirectory(String directoryName) throws Exception{
		try{
			if(isBlank(directoryName)) throw new Exception("directoryName is null.");
			File newPath = new File(directoryName);
			if(!newPath.exists()) {
				boolean status = newPath.mkdirs();
			}
		}catch(Exception e){
			throw new Exception(e);
		}
	}

	// Put the file into server
	public static void fileStream(Object obj, String filePath, String fileName) throws Exception{
		ObjectOutputStream mediaFileOut=null;
        
		if(!isDirectoryExist(filePath)){
			createDirectory(filePath);
		}
		
        try {
        	mediaFileOut = new ObjectOutputStream(new FileOutputStream(filePath+File.separator+fileName)); 
        	mediaFileOut.writeObject(obj);
        } catch (java.io.FileNotFoundException e) {
        	throw new Exception(e);
        } catch (java.io.IOException e) {
        	throw new Exception(e);
        } finally {
             mediaFileOut.flush();
             mediaFileOut.close();
        }
	}
	
	// Get file from server
	public static Object fileStream(String filePath) throws Exception{
		ObjectInputStream mediaFileIn = null;
		try{
			mediaFileIn = new ObjectInputStream(new FileInputStream(filePath));
			return mediaFileIn.readObject();
		}catch(Exception e){
			mediaFileIn.close();
			throw new Exception(e);
		}finally {
            mediaFileIn.close();
       }
	}

	public static boolean isFileExists(String filePath) throws Exception{
		
		boolean retVal=false;
		FileInputStream fis = null;
		try {
			File fileHandler = new File(filePath);
			fis = new FileInputStream(fileHandler);
			retVal=fis!=null;
			}catch(Exception e){
				throw new Exception(e);
			} finally {
				if (fis != null) fis.close();
			}
		return retVal;
	}
	
	public static boolean isExist(String file) throws Exception{
		File fileHandler = new File(file);
		return fileHandler.exists();
	}
	
	public static boolean isDirectoryExist(String dir){
		File file = new File(dir);

		if(file.isDirectory())
			return true;
		else
			return false;
	}

	public static void deleteFile(String filepath) throws IOException{
		File file = new File(filepath);

		if(file.exists()){
			File dir = file.getParentFile();

			if(!file.delete()){
				file.deleteOnExit();
			}

			if(dir.isDirectory()){
				if(!dir.delete()){
					dir.deleteOnExit();
				}
			}
			
			if(!dir.exists())
				logger.info("File "+filepath+" has been deleted.");
			else{
				String newDir = dir.getPath()+".trash."+new Date().getTime();
				dir.renameTo(new File(newDir));
			}
		}
	}
	
	public static void deleteDir(String dir) throws IOException{
		new File(dir).deleteOnExit();
	}

	public static boolean isBlank(String str){
		try 
		{
			if (null == str || "".equals(str))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	// Check allowable file count
	public static boolean isValidFileCount(int validCnt, int curCnt){
		if(curCnt > validCnt)
			return false;
		else
			return true;
	}
	
	public static Date getLastModefied(String filePath){
		try {
			File fileHandler = new File(filePath);
			if(fileHandler.exists())
				return new Date(fileHandler.lastModified());

			}catch(Exception e){}
		return null;
	}
	
	public static Long getSizeKiloBytes(String filePath){
		try {
			File fileHandler = new File(filePath);
			
			if(fileHandler.exists())
				return (fileHandler.length() / 1024);		

		}catch(Exception e){}
		return 0L;
	}
	
	public static Long getSizeBytes(String filePath){
		try {
			File fileHandler = new File(filePath);
			
			if(fileHandler.exists())
				return fileHandler.length();		

		}catch(Exception e){}
		return 0L;
	}
	
	private static Object[] getFileExtentions(){
		List<String> extList = new ArrayList<String>();
		extList.add("doc");
		extList.add("docx");
		extList.add("docm");
		extList.add("dotx");
		extList.add("dotm");
		extList.add("cvs");
		extList.add("xls");
		extList.add("xlsx");
		extList.add("xlsm");
		extList.add("xltx");
		extList.add("xltm");
		extList.add("xlsb");
		extList.add("xlam");
		extList.add("ppt");
		extList.add("pptx");
		extList.add("pptm");
		extList.add("potx");
		extList.add("potm");
		extList.add("ppam");
		extList.add("ppsx");
		extList.add("ppsm");
		extList.add("sldx");
		extList.add("sldm");
		extList.add("thmx");
		extList.add("pdf");
		return extList.toArray();
	}
}