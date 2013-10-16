package com.search.reports.manager;

import com.google.common.collect.Lists;
import com.search.reports.manager.model.RuleReport;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
@Component
public class ReportsManager {
    public List<RuleReport> uploadRuleReports(String... excelFilePath) {
        List<RuleReport> ruleReports = Lists.newArrayList();
        
        return ruleReports;
    }
}
