package com.search.reports.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.search.reports.manager.model.KeywordReport;
import com.search.reports.manager.model.Report;
import com.search.reports.manager.model.RuleReport;
import com.search.reports.manager.util.ReportsManagerUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
@Component
public class ReportsManager {

    private static final Logger logger = LoggerFactory.getLogger(ReportsManager.class);

    public List<RuleReport> uploadRuleReports(String... excelFilePaths) {
        List<RuleReport> ruleReports = Lists.newArrayList();

        for (String excelFilePath : excelFilePaths) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(excelFilePath);

                XSSFWorkbook workbook = new XSSFWorkbook(in);
                XSSFSheet elevateSheet = workbook.getSheet("Elevate");

                Iterator<Row> rowIterator = elevateSheet.iterator();

                RuleReport ruleReport = new RuleReport();
                ruleReport.setRuleName("Elevate");

                KeywordReport keywordReport = new KeywordReport();

                Report report = new Report();
                int cellCounter = 0;
                String currentKeyword = null;
                String nextKeyword = null;
                boolean isFirstRead = true;

                whileRow:
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    Iterator<Cell> cellIterator = row.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();

                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            String stringCellValue = cell.getStringCellValue();

                            if (ReportsManagerUtil.isAReportHeader(stringCellValue)) {

                                continue whileRow;
                            }
                        }

                        switch (cellCounter) {
                            case 0:
                                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                                    if (Strings.isNullOrEmpty(currentKeyword)) {
                                        currentKeyword = cell.getStringCellValue();
                                    } else {
                                        nextKeyword = cell.getStringCellValue();
                                    }

                                    if (isFirstRead) {
                                        isFirstRead = false;
                                        continue whileRow;
                                    }
                                    
                                    // update the rule report
                                    updateRuleReport(keywordReport, currentKeyword, 
                                            ruleReport);
                                    
                                    keywordReport = new KeywordReport();
                                    currentKeyword = nextKeyword;
                                    continue whileRow;
                                } else {
                                    report.setRank(cell.getNumericCellValue() + "");
                                }
                                break;
                            case 1:
                                report.setSku(cell.getNumericCellValue() + "");
                                break;
                            case 2:
                                report.setName(cell.getStringCellValue());
                                break;
                            case 3:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    report.setExpiration(cell.getDateCellValue());
                                }
                                break;
                        }

                        cellCounter++;
                    }

                    if (cellCounter >= 4) {
                        cellCounter = 0;
                        keywordReport.addReport(report);
                        report = new Report();
                    }
                }
                
                // update the rule report
                updateRuleReport(keywordReport, currentKeyword, ruleReport);
                
                // remove empty reports
                cleanupEmptyReport(ruleReport);
                
                for (KeywordReport keyRep : ruleReport.getKeywordReports()) {
                    logger.info(keyRep.getKeyword());
                    List<Report> reps = keyRep.getReports();

                    for (Report rep : reps) {
                        logger.info(rep.toString());
                    }

                    logger.info(System.getProperty("line.separator"));
                }
            } catch (IOException e) {
                logger.error(String.format("Unable to read excel file %s",
                        excelFilePath), e);
            } finally {
                try {
                    Closeables.close(in, false);
                } catch (IOException e) {
                    logger.error("Unable to close stream", e);
                }
            }
        }

        return ruleReports;
    }

    /**
     * Helper method for updating the rule report
     * @param keywordReport
     * @param currentKeyword
     * @param ruleReport
     */
    private void updateRuleReport(KeywordReport keywordReport, String currentKeyword, 
            RuleReport ruleReport) {
        keywordReport.setKeyword(currentKeyword);
        ruleReport.addKeywordReport(keywordReport);
    }
    
    /**
     * Removes empty Report objects
     * @param keywordReport 
     */
    private void cleanupEmptyReport(RuleReport ruleReport) {
        List<KeywordReport> keywordReports = ruleReport.getKeywordReports();
        for (KeywordReport keywordReport : keywordReports) {
            List<Report> reports = keywordReport.getReports();
            List<Report> cleanedReports = Lists.newArrayList();
            
            for (Report report : reports) {
                if (!ReportsManagerUtil.isReportObjectEmpty(report)) {
                    cleanedReports.add(report);
                }
            }
            
            keywordReport.setReports(cleanedReports);
        }
    }
}
