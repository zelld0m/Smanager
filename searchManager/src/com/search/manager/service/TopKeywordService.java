package com.search.manager.service;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.directwebremoting.annotations.*;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.search.manager.mail.ReportNotificationMailService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.TopKeyword;
import com.search.manager.utility.*;

@Service(value = "topKeywordService")
@RemoteProxy(
		name = "TopKeywordServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "topKeywordService")
)
public class TopKeywordService {
	
	private static final Logger logger = Logger.getLogger(TopKeywordService.class);
	
	@Autowired ReportNotificationMailService reportNotificationMailService;
	
	@RemoteMethod
	public List<String> getFileList(){
		List<String> filenameList = new ArrayList<String>();
		File dir = new File(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName());
		
		File[] files = dir.listFiles();
		
		if (files != null) {
			Arrays.sort(files, new Comparator<File>(){
				    public int compare(File f1, File f2) {
				        return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
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
	public RecordSet<TopKeyword> getFileContents(String filename)  {
		List<TopKeyword> list = new ArrayList<TopKeyword>();
		BufferedReader reader = null;
		try {
			try {
				String filePath = PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName() + File.separator + filename;
				
				if (filename.indexOf("-splunk") > 0) {
					readCsvFile(filePath, list);
				} else {
					reader = new BufferedReader(new FileReader(filePath));
					String readline = null;
					while ((readline = reader.readLine()) != null) {
						String[] valueArray = readline.split(",",2);
						list.add(new TopKeyword(valueArray[1], Integer.parseInt(valueArray[0])));
					}
				}
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new RecordSet<TopKeyword>(list, list.size());
	}

	private void readCsvFile(String filePath, List<TopKeyword> list)
			throws IOException {
		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(filePath), ',', '\"', '\0', 0, true);
			List<String[]> data = reader.readAll();

			for (String[] col : data) {
				list.add(new TopKeyword(col[1], Integer.parseInt(col[0]), Integer.parseInt(col[2]), col[3]));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	private String getFileHeader(String filename) {
		return filename.contains("-splunk") ? "Count,Keyword,Result,SKU" : "Count,Keyword";
	}

	private File getFile(String filename){
		return new File(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName() + File.separator + filename);
	}

	@RemoteMethod
    public FileTransfer downloadFileAsCSV(String filename, String customFilename) {
        try {
            return downloadCsv(new FileInputStream(getFile(filename)), filename, customFilename);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private FileTransfer downloadCsv(InputStream content, String filename, String customFilename) {
        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(content);
            CombinedInputStream cis = new CombinedInputStream(new InputStream[] {
                    new ByteArrayInputStream(getFileHeader(filename).getBytes()), bis });
            // FileTransfer auto-closes the stream
            return new FileTransfer(StringUtils.isBlank(customFilename) ? filename : customFilename + ".csv",
                    "application/csv", cis);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    @RemoteMethod
    public FileTransfer downloadCustomRangeAsCSV(Date from, Date to, String customFilename) {
        return downloadCsv(getCustomRangeReportStream(from, to), "customRangeTopKeywords", customFilename);
    }

	@RemoteMethod
	public boolean sendFileAsEmail(String filename, String customFilename, String[] recipients)  {
		return reportNotificationMailService.sendTopKeyword(getFile(filename), StringUtils.isBlank(customFilename)? filename : customFilename + ".csv", recipients,new ByteArrayInputStream(getFileHeader(filename).getBytes()),"text/csv");
	}

    @RemoteMethod
    public boolean sendCustomRangeAsEmail(Date from, Date to, String customFilename, String[] recipients)  {
        return reportNotificationMailService.sendTopKeyword(getCustomRangeReportStream(from, to), StringUtils.isBlank(customFilename)? "customRangeTopKeywords" : customFilename + ".csv", recipients,new ByteArrayInputStream(getFileHeader("customRangeTopKeywords").getBytes()),"text/csv");
    }

    private InputStream getCustomRangeReportStream(Date from, Date to) {
        List<TopKeyword> topKeywords = getTopKeywords(from, to);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(os));

        for (TopKeyword kw : topKeywords) {
            writer.writeNext(new String[] { String.valueOf(kw.getCount()), kw.getKeyword() });
        }

        // close writer before passing to downloader
        IOUtils.closeQuietly(writer);

        return new ByteArrayInputStream(os.toByteArray());
    }

	@RemoteMethod
    public List<TopKeyword> getTopKeywords(Date from, Date to) {
        Map<String, TopKeyword> stats = new HashMap<String, TopKeyword>();
        Date limit = DateUtils.truncate(to, Calendar.DATE);
        Date date = DateUtils.truncate(from, Calendar.DATE);

        while (!date.after(limit)) {
            StatisticsUtil.getAllStats(date, stats);
            date = DateUtils.addDays(date, 1);
        }

        List<TopKeyword> kcList = new ArrayList<TopKeyword>(stats.values());

        Collections.sort(kcList);
        return kcList;
    }
}