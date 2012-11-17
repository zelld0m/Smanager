package com.search.manager.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.stereotype.Service;

import com.search.manager.model.KeywordStats;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.StatisticsUtil;

@Service(value = "keywordTrendsService")
@RemoteProxy(name = "KeywordTrendsServiceJS", 
		creator = SpringCreator.class, 
		creatorParams = @Param(name = "beanName", value = "keywordTrendsService"))
public class KeywordTrendsService {

	private static final Logger logger = Logger
			.getLogger(KeywordTrendsService.class);

	@RemoteMethod
	public List<KeywordStats> getStats(List<String> keywords, Date fromDate,
			Date toDate) {
		List<KeywordStats> list = StatisticsUtil.createEmptyStats(keywords);
		retrieveStats(list, fromDate, toDate);

		return list;
	}

	@RemoteMethod
	public KeywordStats getStats(String keyword, Date fromDate, Date toDate) {
		List<KeywordStats> list = StatisticsUtil.createEmptyStats(Arrays
				.asList(keyword));
		retrieveStats(list, fromDate, toDate);

		return list.get(0);
	}

	@RemoteMethod
	public List<KeywordStats> getTopTenKeywords(Date fromDate, Date toDate) {
		List<KeywordStats> list = StatisticsUtil.top(getFile(toDate), toDate,
				10, 0, 1);
		retrieveStats(list, fromDate, DateUtils.addDays(toDate, -1));
		return list;
	}

	@RemoteMethod
	public Date getMostRecentStatsDate() {
		File dir = new File(PropsUtils.getValue("splunkdir") + File.separator
				+ UtilityService.getStoreName());
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory()
						&& file.getName().matches("^[0-9]{6}$");
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
				String dateStr = new MessageFormat(getFilePattern())
						.parse(path)[1].toString();

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
	 * Get CSV file for the given date or null if non-existent.
	 * 
	 * @param date
	 *            Date
	 * @return CSV file for the given date
	 */
	private File getFile(Date date) {
		String str = DateAndTimeUtils.formatYYYYMMDD(date);
		return new File(MessageFormat.format(getFilePattern(),
				str.substring(0, 6), str));
	}

	private String getFilePattern() {
		return new StringBuilder().append(PropsUtils.getValue("splunkdir"))
				.append(File.separator).append(UtilityService.getStoreName())
				.append(File.separator).append("{0}").append(File.separator)
				.append("{1}.csv").toString();
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
	private void retrieveStats(List<KeywordStats> list, Date fromDate,
			Date toDate) {
		Date date = fromDate;

		while (DateAndTimeUtils.compare(date, toDate) <= 0) {
			StatisticsUtil.retrieveStats(list, getFile(date), date, 0, 1);
			date = DateUtils.addDays(date, 1);
		}
	}
}