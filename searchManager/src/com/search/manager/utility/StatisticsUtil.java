package com.search.manager.utility;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.search.manager.model.KeywordStats;

public class StatisticsUtil {

	private final static Logger logger = Logger.getLogger(StatisticsUtil.class);

	public static List<String[]> findInCSV(File file, String keyword, int keyCol) {
		return findInCSV(file, Arrays.asList(keyword), keyCol);
	}

	public static List<String[]> findInCSV(File file, List<String> keywords,
			int keyCol) {
		CSVReader reader = null;
		List<String[]> lines = null;

		try {
			if (file.exists()) {
				lines = new ArrayList<String[]>();
				reader = new CSVReader(new FileReader(file), ',', '\"', '\0');
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
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				} finally {
					reader = null;
				}
			}
		}

		return lines;

	}

	public static Integer getCount(File file, String keyword, int keyCol,
			int countCol) {
		List<String[]> line = findInCSV(file, keyword, keyCol);
		
		if (line == null) {
			return null;
		}

		return line.size() > 0 ? Integer.valueOf(line.get(0)[countCol]) : 0;
	}

	public static Map<String, Integer> getCount(File file,
			List<String> keywords, int keyCol, int countCol) {
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

	public static List<KeywordStats> top(File file, Date date, int count,
			int keywordCol, int countCol) {
		List<KeywordStats> top = new ArrayList<KeywordStats>(count);
		CSVReader reader = null;

		try {
			if (file.exists()) {
				reader = new CSVReader(new FileReader(file), ',', '\"', '\0');
				String[] data = reader.readNext();

				for (int i = 0; i < count && data != null; i++, data = reader
						.readNext()) {
					KeywordStats stats = new KeywordStats(data[keywordCol]);

					stats.addStats(date, Integer.parseInt(data[countCol]));
					top.add(stats);
				}
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				} finally {
					reader = null;
				}
			}
		}

		return top;
	}

	public static void retrieveStats(List<KeywordStats> list, File file,
			Date date, int keyCol, int countCol) {
		Map<String, Integer> counts = getCount(file, extractKeywords(list),
				keyCol, countCol);

		for (KeywordStats stats : list) {
			stats.addStats(date, counts.get(stats.getKeyword()));
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
}
