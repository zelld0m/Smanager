package com.search.manager.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.mail.ReportNotificationMailService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.ZeroResult;
import com.search.manager.utility.CombinedInputStream;
import com.search.manager.utility.PropsUtils;

@Service(value = "zeroResultService")
@RemoteProxy(
		name = "ZeroResultServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "zeroResultService")
)
public class ZeroResultService {
	
	private static final Logger logger = Logger.getLogger(ZeroResultService.class);
	
	@Autowired ReportNotificationMailService reportNotificationMailService;
	
	@RemoteMethod
	public List<String> getFileList(){
		List<String> filenameList = new ArrayList<String>();
		File dir = new File(PropsUtils.getValue("zerorsdir") + File.separator + UtilityService.getStoreName());
		
		File[] files = dir.listFiles();
		
		if (files != null) {
			Arrays.sort(files, new Comparator<File>(){
				    public int compare(File f1, File f2) {
				        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
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
		}
		return filenameList;
	}

	@RemoteMethod
	public RecordSet<ZeroResult> getFileContents(String filename)  {
		List<ZeroResult> list = new ArrayList<ZeroResult>();
		BufferedReader reader = null;
		try {
			try {
				reader = new BufferedReader(new FileReader(PropsUtils.getValue("zerorsdir") + File.separator + UtilityService.getStoreName() + File.separator + filename));
				String readline = null;
				while ((readline = reader.readLine()) != null) {
					String[] valueArray = readline.split(",",2);
					list.add(new ZeroResult(valueArray[1], Integer.parseInt(valueArray[0])));
				}

			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new RecordSet<ZeroResult>(list, list.size());
	}

	private File getFile(String filename){
		return new File(PropsUtils.getValue("zerorsdir") + File.separator + UtilityService.getStoreName() + File.separator + filename);
	}
	
	@RemoteMethod
	public FileTransfer downloadFileAsCSV(String filename, String customFilename)  {
		FileTransfer fileTransfer = null;
		File file = getFile(filename);
		BufferedInputStream bis = null;
		try {
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				CombinedInputStream cis = new CombinedInputStream(new InputStream[]{new ByteArrayInputStream("Count,Keyword".getBytes()), bis});
				// FileTransfer auto-closes the stream
				fileTransfer = new FileTransfer(StringUtils.isBlank(customFilename)? filename : customFilename + ".csv", "application/csv", cis);
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return fileTransfer;
	}
	
	@RemoteMethod
	public boolean sendFileAsEmail(String filename, String customFilename, String[] recipients)  {
		return reportNotificationMailService.sendZeroResult(getFile(filename), StringUtils.isBlank(customFilename)? filename : customFilename + ".csv", recipients);
	}
}