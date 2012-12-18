package com.search.manager.service;

import java.io.*;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.*;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

import com.search.manager.model.KeywordStats;
import com.search.manager.utility.*;

@Service(value = "keywordTrendsService")
@RemoteProxy(name = "KeywordTrendsServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "keywordTrendsService"))
public class KeywordTrendsService {

    private static final Logger logger = Logger.getLogger(KeywordTrendsService.class);

    @RemoteMethod
    public List<KeywordStats> getStats(List<String> keywords, Date fromDate, Date toDate, String collation) {
        List<KeywordStats> list = StatisticsUtil.createEmptyStats(keywords);
        retrieveStats(list, fromDate, toDate, collation);

        return list;
    }

    @RemoteMethod
    public KeywordStats getStats(String keyword, Date fromDate, Date toDate, String collation) {
        List<KeywordStats> list = StatisticsUtil.createEmptyStats(Arrays.asList(keyword));
        retrieveStats(list, fromDate, toDate, collation);

        return list.get(0);
    }

    @RemoteMethod
    public List<String> getTopTenKeywords() {
        Date recent = getMostRecentStatsDate();
        List<KeywordStats> list = StatisticsUtil.top(recent, 10, 0, 1);
        List<String> top = new ArrayList<String>();

        for (KeywordStats stats : list) {
            top.add(stats.getKeyword());
        }

        return top;
    }

    @RemoteMethod
    public Date getMostRecentStatsDate() {
        File dir = new File(PropsUtils.getValue("splunkdir") + File.separator + UtilityService.getStoreName());
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && file.getName().matches("^[0-9]{6}$");
            }
        });
        Comparator<File> comp = new Comparator<File>() {
            public int compare(File f1, File f2) {
                return -f1.getName().compareTo(f2.getName());
            }
        };
        File[] csvs = null;

        if (files != null) {
            Arrays.sort(files, comp);
            csvs = files[0].listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".csv");
                }
            });
        }

        if (csvs != null) {
            Arrays.sort(csvs, comp);

            try {
                String path = csvs[0].getCanonicalPath();
                String dateStr = new MessageFormat(StatisticsUtil.getSplunkFilePattern()).parse(path)[1].toString();

                return DateAndTimeUtils.parseDateYYYYMMDD(dateStr);
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }
        }

        return null;
    }

    /**
     * Retrieve stats of the given list of keywords for the specified date
     * range.
     * 
     * @param list
     *            List of keywords
     * @param fromDate
     *            start of date range
     * @param toDate
     *            end of date range
     */
    private void retrieveStats(List<KeywordStats> list, Date fromDate, Date toDate, String collation) {
        Date date = fromDate;

        while (DateAndTimeUtils.compare(date, toDate) <= 0) {
            StatisticsUtil.retrieveStats(list, date, 0, 1, collation);
            date = DateUtils.addDays(date, 1);
        }
    }
}
