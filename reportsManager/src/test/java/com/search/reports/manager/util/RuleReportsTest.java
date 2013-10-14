package com.search.reports.manager.util;

import com.search.reports.manager.exception.ReportsException;
import com.search.reports.manager.model.KeywordReport;
import com.search.reports.manager.model.RuleReport;
import com.search.reports.manager.model.builder.ReportBuilder;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 14, 2013
 * @version 1.0
 */
public class RuleReportsTest {

    private RuleReport elevateRuleReport = new RuleReport("Elevate",
            Arrays.asList(new KeywordReport("Apple",
            Arrays.asList(ReportBuilder.create().rank("1.0").build()))));
    private RuleReport excludeRuleReport = new RuleReport("Exclude",
            Arrays.asList(new KeywordReport("Ball",
            Arrays.asList(ReportBuilder.create().rank("2.0").build()))));
    private RuleReport demoteRuleReport = new RuleReport("Demote",
            Arrays.asList(new KeywordReport("Cat",
            Arrays.asList(ReportBuilder.create().rank("3.0").build()))));
    private List<RuleReport> ruleReports = Arrays.asList(
            elevateRuleReport, excludeRuleReport, demoteRuleReport);

    @Test
    public void testGetRuleReportByRuleName() {
        assertEquals(elevateRuleReport, RuleReports.getRuleReportByRuleName("Elevate",
                ruleReports));
    }

    @Test(expected = ReportsException.class)
    public void testGetRuleReportByRuleName_RuleName_Not_Existing_Throw_ReportsException() {
        RuleReports.getRuleReportByRuleName("Banner", ruleReports);
    }
}
