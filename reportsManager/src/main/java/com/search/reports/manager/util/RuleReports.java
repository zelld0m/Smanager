package com.search.reports.manager.util;

import com.search.reports.manager.exception.ReportsException;
import com.search.reports.manager.model.RuleReport;
import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 14, 2013
 * @version 1.0
 */
public class RuleReports {

    /**
     * Looks for a {@link RuleReport} object by ruleName
     *
     * @param ruleName the rule name
     * @param ruleReports the list of {@link RuleReport} objects to look into
     * @return the matching {@link RuleReport} object having the same rule name as the
     * argument passed
     * @throws ReportsException the exception
     */
    public static RuleReport getRuleReportByRuleName(String ruleName,
            List<RuleReport> ruleReports) throws ReportsException {
        for (RuleReport ruleReport : ruleReports) {
            if (ruleReport.getRuleName().equals(ruleName)) {
                return ruleReport;
            }
        }

        throw new ReportsException(String.format(
                "Unable to find rule report with the rule name %s", ruleName));
    }
}
