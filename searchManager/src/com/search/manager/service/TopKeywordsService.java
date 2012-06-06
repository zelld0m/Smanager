package com.search.manager.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

import com.search.manager.model.RecordSet;
import com.search.manager.model.TopKeyword;
import com.search.manager.utility.PropsUtils;

@Service(value = "topKeywordsService")
@RemoteProxy(
		name = "TopKeywordsServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "topKeywordsService")
)
public class TopKeywordsService {
	
	private static final Logger logger = Logger.getLogger(TopKeywordsService.class);
	
	@RemoteMethod
	public List<String> getFileList(){
		List<String> filenameList = new ArrayList<String>();
		File dir = new File(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName());
		
		File[] files = dir.listFiles();
		
		Arrays.sort(files, new Comparator<File>(){
			    public int compare(File f1, File f2) {
			        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			    } 
		    });
		int ctr = 1;
		for (File file : files) {
	        if (!file.isDirectory()) {
	        	filenameList.add(file.getName());
	        	if (ctr++ > 12) {
	        		break;
	        	}
	        }
	    }

		return filenameList;
	}

	@RemoteMethod
	public RecordSet<TopKeyword> getFileContents(String filename)  {
		List<TopKeyword> list = new ArrayList<TopKeyword>();
		BufferedReader reader = null;
		try {
			try {
				reader = new BufferedReader(new FileReader(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName() + File.separator + filename));
				String readline = null;
				while ((readline = reader.readLine()) != null) {
					String[] valueArray = readline.split(",",2);
					list.add(new TopKeyword(valueArray[1], Integer.parseInt(valueArray[0])));
				}

			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new RecordSet<TopKeyword>(list, list.size());
	}

	@RemoteMethod
	public FileTransfer downloadFile(String filename)  {
		FileTransfer fileTransfer = null;
		File file = new File(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName() + File.separator + filename);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		BufferedInputStream bis = null;
		try {
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				byte[] buf = new byte[1024];
				int n = 0;
				while ((n=bis.read(buf)) != -1) {
					buffer.write(buf, 0, n);
				}			
				fileTransfer = new FileTransfer(filename, "application/csv", buffer.toByteArray());
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				bis.close();
				buffer.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return fileTransfer;
	}

}
