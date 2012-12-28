package com.search.manager.service;

import static com.search.manager.utility.DateAndTimeUtils.asUTC;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.*;
import org.directwebremoting.io.FileTransfer;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.search.manager.mail.ReportNotificationMailService;
import com.search.manager.mail.TopKeywordMailCommand;
import com.search.manager.model.RecordSet;
import com.search.manager.model.TopKeyword;
import com.search.manager.utility.*;

@Service(value = "topKeywordService")
@RemoteProxy(name = "TopKeywordServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "topKeywordService"))
public class TopKeywordService {

    private static final Logger logger = Logger.getLogger(TopKeywordService.class);

    @Autowired
    ReportNotificationMailService reportNotificationMailService;

    @Autowired
    CommandExecutor commandExecutor;

    @RemoteMethod
    public List<String> getFileList() {
        List<String> filenameList = new ArrayList<String>();
        File dir = new File(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName());

        File[] files = dir.listFiles();

        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
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
    public RecordSet<TopKeyword> getFileContents(String filename) {
        List<TopKeyword> list = new ArrayList<TopKeyword>();
        BufferedReader reader = null;
        try {
            try {
                String filePath = PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName()
                        + File.separator + filename;

                if (filename.indexOf("-splunk") > 0) {
                    readCsvFile(filePath, list);
                } else {
                    reader = new BufferedReader(new FileReader(filePath));
                    String readline = null;
                    while ((readline = reader.readLine()) != null) {
                        String[] valueArray = readline.split(",", 2);
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

    private void readCsvFile(String filePath, List<TopKeyword> list) throws IOException {
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
        return filename.contains("-splunk") ? "\ufeffCount,Keyword,Result,SKU" : "\ufeffCount,Keyword";
    }

    private File getFile(String filename) {
        return new File(PropsUtils.getValue("topkwdir") + File.separator + UtilityService.getStoreName()
                + File.separator + filename);
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
        List<TopKeyword> topKeywords = StatisticsUtil.getTopKeywordsInRange(asUTC(from), asUTC(to), UtilityService.getStoreName());
        return downloadCsv(StatisticsUtil.getCustomRangeReportStream(topKeywords, new CsvTransformer<TopKeyword>() {

            @Override
            public String[] toStringArray(TopKeyword t) {
                return new String[] { String.valueOf(t.getCount()), t.getKeyword() };
            }

        }), "customRangeTopKeywords", customFilename);
    }

    @RemoteMethod
    public boolean sendFileAsEmail(String filename, String customFilename, String[] recipients) {
        return reportNotificationMailService.sendTopKeyword(getFile(filename),
                StringUtils.isBlank(customFilename) ? filename : customFilename + ".csv", recipients,
                new ByteArrayInputStream(getFileHeader(filename).getBytes()), "text/csv");
    }

    @RemoteMethod
    public boolean sendCustomRangeAsEmail(Date from, Date to, String customFilename, String[] recipients) {
        return commandExecutor.addCommand(new TopKeywordMailCommand(reportNotificationMailService, UtilityService
                .getStoreName(), asUTC(from), asUTC(to), recipients, StringUtils.isBlank(customFilename) ? "customRangeTopKeywords"
                : customFilename + ".csv", new ByteArrayInputStream(getFileHeader("customRangeTopKeywords-splunk")
                .getBytes()), "text/csv"));
    }

    @RemoteMethod
    public List<TopKeyword> getTopKeywords(Date from, Date to) {
        return StatisticsUtil.getTopKeywordsInRange(asUTC(from), asUTC(to), UtilityService.getStoreName());
    }
}