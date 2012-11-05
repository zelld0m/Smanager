package com.search.manager.service;

import java.io.File;
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
@RemoteProxy(name = "KeywordTrendsServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "keywordTrendsService"))
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
				10, 1, 0);
		retrieveStats(list, fromDate, DateUtils.addDays(toDate, -1));
		return list;
	}

	@RemoteMethod
	public Date getMostRecentStatsDate() {
		File dir = new File(PropsUtils.getValue("topkwdir") + File.separator
				+ UtilityService.getStoreName());
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("-splunk");
			}
		});

		if (files != null) {
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return -Long.valueOf(f1.lastModified()).compareTo(
							f2.lastModified());
				}
			});

			try {
				String path = files[0].getCanonicalPath();
				String dateStr = new MessageFormat(getFilePattern())
						.parse(path)[0].toString();

				return DateAndTimeUtils
						.getDateHyphenedDateStringMMDDYYYY(dateStr);
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			} catch (ParseException e) {
				logger.error(e.getMessage());
			}
		}

		return null;
	}

	private String getFilePattern() {
		return new StringBuilder().append(PropsUtils.getValue("topkwdir"))
				.append(File.separator).append(UtilityService.getStoreName())
				.append(File.separator).append(UtilityService.getStoreName())
				.append("_summary_{0}-splunk.csv").toString();
	}

	private File getFile(Date date) {
		return new File(MessageFormat.format(getFilePattern(),
				DateAndTimeUtils.getHyphenedDateStringMMDDYYYY(date)));
	}

	private void retrieveStats(List<KeywordStats> list, Date fromDate,
			Date toDate) {
		Date date = fromDate;

		while (DateAndTimeUtils.compare(date, toDate) <= 0) {
			StatisticsUtil.retrieveStats(list, getFile(date), date, 1, 0);
			date = DateUtils.addDays(date, 1);
		}
	}
}