package com.search.manager.utility;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.search.manager.model.KeywordStats;
import com.search.manager.model.TopKeyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(StatisticsUtil.class);

    public static List<String[]> findInCSV(File file, String keyword, int keyCol) {
        return findInCSV(file, Arrays.asList(keyword), keyCol);
    }

    public static List<String[]> findInCSV(File file, List<String> keywords, int keyCol) {
        CSVReader reader = null;
        List<String[]> lines = null;

        try {
            if (file.exists()) {
                lines = new ArrayList<String[]>();
                reader = new CSVReader(new FileReader(file), ',', '\"', '\0', 1, false);
                String[] data = reader.readNext();

                while (lines.size() < keywords.size() && data != null) {
                    if (keywords.indexOf(data[keyCol]) >= 0) {
                        lines.add(data);
                    }

                    data = reader.readNext();
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return lines;

    }

    public static Integer getCount(File file, String keyword, int keyCol, int countCol) {
        List<String[]> line = findInCSV(file, keyword, keyCol);

        if (line == null) {
            return null;
        }

        return line.size() > 0 ? Integer.valueOf(line.get(0)[countCol]) : 0;
    }

    public static Map<String, Integer> getCount(File file, List<String> keywords, int keyCol, int countCol) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        List<String[]> lines = findInCSV(file, keywords, keyCol);

        if (lines != null) {
            // initialise count to zero
            for (String keyword : keywords) {
                counts.put(keyword, 0);
            }

            // set proper value for those found
            for (String[] col : lines) {
                counts.put(col[keyCol], Integer.valueOf(col[countCol]));
            }
        }

        return counts;
    }

    public static List<KeywordStats> top(Date date, int count, int keywordCol, int countCol, String store) {
        List<KeywordStats> top = new ArrayList<KeywordStats>(count);
        File file = getSplunkFile(date, store);
        CSVReader reader = null;

        try {
            if (file.exists()) {
                reader = new CSVReader(new FileReader(file), ',', '\"', '\0', 1, false);
                String[] data = reader.readNext();

                for (int i = 0; i < count && data != null; i++, data = reader.readNext()) {
                    KeywordStats stats = new KeywordStats(data[keywordCol]);

                    stats.addStats(date, Integer.parseInt(data[countCol]));
                    top.add(stats);
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return top;
    }

    public static void retrieveStats(List<KeywordStats> list, Date date, int keyCol, int countCol, String collation, String storeName) {
        File file = getSplunkFile(date, storeName);
        Map<String, Integer> counts = getCount(file, extractKeywords(list), keyCol, countCol);
        Date key = date;

        if ("monthly".equalsIgnoreCase(collation)) {
            key = DateAndTimeUtils.getFirstDayOfMonth(date);
        } else if ("weekly".equalsIgnoreCase(collation)) {
            key = DateAndTimeUtils.getFirstDayOfWeek(date);
        }

        for (KeywordStats stats : list) {
            stats.addStats(key, counts.get(stats.getKeyword()));
        }
    }

    public static List<KeywordStats> createEmptyStats(List<String> keywords) {
        List<KeywordStats> list = new ArrayList<KeywordStats>();

        for (String keyword : keywords) {
            list.add(new KeywordStats(keyword));
        }

        return list;
    }

    private static List<String> extractKeywords(List<KeywordStats> list) {
        List<String> keywords = new ArrayList<String>();

        for (KeywordStats stats : list) {
            keywords.add(stats.getKeyword());
        }

        return keywords;
    }

    public static void getAllStats(Date date, Map<String, TopKeyword> stats, String storeName) {
        CSVReader reader = null;
        File file = getSplunkFile(date, storeName);

        try {
            if (file != null && file.exists()) {
                reader = new CSVReader(new FileReader(file), ',', '\"', '\0', 1, false);
                String[] data = reader.readNext();

                while (data != null) {
                    String keyword = data[0];
                    Integer count = Integer.parseInt(data[1]);
                    TopKeyword kc = stats.get(keyword);

                    if (kc == null) {
                        kc = new TopKeyword();
                        kc.setKeyword(keyword);
                        kc.setCount(count);
                        stats.put(keyword, kc);
                    } else {
                        kc.setCount(kc.getCount() + count);
                    }

                    data = reader.readNext();
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public static List<TopKeyword> getTopKeywordsInRange(Date from, Date to, String storeName) {
        Map<String, TopKeyword> stats = new HashMap<String, TopKeyword>();
        Date limit = to;
        Date date = from;

        while (!date.after(limit)) {
            StatisticsUtil.getAllStats(date, stats, storeName);
            date = DateUtils.addDays(date, 1);
        }

        List<TopKeyword> kcList = new ArrayList<TopKeyword>(stats.values());

        Collections.sort(kcList);
        return kcList;
    }

    /**
     * Get CSV file for the given date or null if non-existent.
     *
     * @param date Date
     * @return CSV file for the given date
     */
    public static File getSplunkFile(Date date, String storeName) {
        String str = DateAndTimeUtils.formatYYYYMMDD(date);
        return new File(MessageFormat.format(getSplunkFilePattern(storeName), str.substring(0, 6), str));
    }

    public static String getSplunkFilePattern(String storeName) {
        return new StringBuilder().append(PropertiesUtils.getValue("splunkdir")).append(File.separator)
                .append(storeName).append(File.separator).append("{0}").append(File.separator)
                .append("{1}.csv").toString();
    }
}
