package com.search.manager.report.statistics.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.search.manager.core.statistics.report.test.BannerStatisticsTest;
import com.search.manager.report.statistics.fluent.BannerStatisticsBuilder;
import com.search.manager.report.statistics.model.BannerStatistics;
import com.search.manager.utility.DateAndTimeUtils;

/**
 * Utility class for retrieving banner statistics
 * 
 * @author Philip Mark Gutierrez
 * @since July 08, 2013
 * @version 0.0.1
 */
public class BannerStatisticsUtil {
	private static final Logger logger = LoggerFactory
	        .getLogger(BannerStatisticsTest.class);
	private static String FILE_LOCATION;

	static {
		String filePath = "src/main/resources/statistics-messages.properties";

		try {
			PropertiesConfiguration config = new PropertiesConfiguration(
			        filePath);
			FILE_LOCATION = config.getString("fileLocation");
		} catch (ConfigurationException e) {
			logger.error(String.format("Unable to load file %s", filePath), e);
		}
	}

	/**
	 * Returns all banner statistics per keyword by memberId
	 * 
	 * @param storeId the storeId
	 * @param memberId the member id
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return all banner statistics per keyword by memberId
	 * @throws FileNotFoundException CSV file cannot be found
	 */
	public static List<BannerStatistics> getStatsPerKeywordByMemberId(
	        String storeId, String memberId, Date startDate, Date endDate)
	        throws FileNotFoundException {
		// return the CSV report file by member id
		return getCSVReportFile(storeId, memberId, 1, startDate, endDate);
	}

	/**
	 * Returns all banner statistics per banner by keyword
	 * 
	 * @param storeId the store id
	 * @param keyword the member id
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return all banner statistics per banner by keyword
	 * @throws FileNotFoundException CSV file cannot be found
	 */
	public static List<BannerStatistics> getStatsPerBannerByKeyword(
	        String storeId, String keyword, Date startDate, Date endDate)
	        throws FileNotFoundException {
		// return the CSV report file by keyword
		return getCSVReportFile(storeId, keyword, 0, startDate, endDate);
	}

	/**
	 * @param storeId
	 * @param keyword
	 * @param keyColumn
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws FileNotFoundException
	 */
	private static List<BannerStatistics> getCSVReportFile(String storeId,
	        String keyword, int keyColumn, Date startDate, Date endDate)
	        throws FileNotFoundException {
		List<BannerStatistics> bannerStatistics = new ArrayList<BannerStatistics>();

		DateTime currentDate = new DateTime(startDate);
		DateTime dateAfterEndDate = new DateTime(endDate).plusDays(1)
		        .toDateMidnight().toDateTime();

		while (currentDate.isBefore(dateAfterEndDate)) {
			File csvFile = new File(String.format(FILE_LOCATION, storeId,
			        DateAndTimeUtils.getHyphenedDateStringMMDDYYYY(currentDate
			                .toDate())));

			// read the CSV file only if it exists
			if (csvFile.exists()) {
				List<String[]> keywordReport = findInCSV(
				        csvFile, keyword, keyColumn);

				// populate the banner statistics
				populateBannerStatistics(bannerStatistics, keywordReport);
			}
			// else {
			// logger.info(String.format(FILE_CANNOT_BE_FOUND_MESSAGE,
			// csvFile != null ? csvFile.getPath() : ""));
			// logger.info("processing to the next file...");
			// }

			// go to the next day
			currentDate = currentDate.plusDays(1);
		}
		return bannerStatistics;
	}
	
	private static List<String[]> findInCSV(File file, String keyword, int keyCol) {
		CSVReader reader = null;
		List<String[]> lines = null;

		try {
			if (file.exists()) {
				lines = new ArrayList<String[]>();
				reader = new CSVReader(new FileReader(file), ',', '\"', '\0',
				        1, false);
				String[] data = reader.readNext();

				while (data != null) {
					if (keyword.equals(data[keyCol])) {
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

	/**
	 * Helper method for populating the banner statistics
	 * 
	 * @param bannerStatistics the banner statistics instance
	 * @param keywordReports an array that comes from the read CSV file
	 */
	private static void populateBannerStatistics(
	        List<BannerStatistics> bannerStatistics,
	        List<String[]> keywordReports) {

		for (String[] keywordReport : keywordReports) {
			String itemKeyword = keywordReport[0];
			String memberId = keywordReport[1];
			// String uniqueKey = itemKeyword + "_" + memberId;
			String imagePath = keywordReport[2];
			String linkPath = keywordReport[3];
			int itemClicks = Integer.parseInt(keywordReport[4]);
			int itemImpressions = Integer.parseInt(keywordReport[5]);

			BannerStatistics bannerStats = BannerStatisticsBuilder
			        .create(itemKeyword, memberId).imagePath(imagePath)
			        .linkPath(linkPath).clicks(itemClicks)
			        .impressions(itemImpressions).build();

			if (!bannerStatistics.contains(bannerStats)) {
				bannerStatistics.add(bannerStats);
			} else {
				BannerStatistics retrievedBannerStats = find(bannerStatistics,
				        bannerStats);
				retrievedBannerStats.incrementClicks(itemClicks);
				retrievedBannerStats.incrementImpressions(itemImpressions);
			}

			// bannerStatistics.add(bannerStats);

			// // find the banner stats
			// BannerStatistics retrievedBannerStats = bannerStatistics
			// .get(uniqueKey);
			//
			// // add it to the Map if not existing, else increment the clicks
			// // and impressions
			// if (retrievedBannerStats == null) {
			// bannerStatistics.put(uniqueKey, bannerStats);
			// } else {
			// retrievedBannerStats.incrementClicks(itemClicks);
			// retrievedBannerStats.incrementImpressions(itemImpressions);
			// }
		}
	}

	/**
	 * Searches for a specific object that matches the object to find
	 * 
	 * @param objectList the object list
	 * @param objectToFind the object to find
	 * @return the object that matches the object to find
	 */
	private static <T> T find(List<T> objectList, T objectToFind) {
		for (T currentObject : objectList) {
			if (currentObject.equals(objectToFind)) {
				return currentObject;
			}
		}

		return null;
	}
}
