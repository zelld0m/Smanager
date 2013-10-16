package com.search.reports.manager.service;

import com.search.reports.manager.model.RuleReport;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 14, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class ReportsManagerServiceTest {

    @Autowired
    private ReportsManagerService reportsManagerService;

    @Test
    public void testUploadRuleReports() throws URISyntaxException {
        String excelFile = FileUtils.getFile("src/test/resources/report1.xlsx").
                getAbsolutePath();
        List<RuleReport> ruleReports = reportsManagerService.uploadRuleReports(excelFile);

    }
}
