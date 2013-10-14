package com.search.reports.manager.service;

import com.search.reports.manager.ReportsManager;
import com.search.reports.manager.model.RuleReport;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
@Service
public class ReportsManagerService {
    @Autowired
    private ReportsManager reportsManager;
    
    public List<RuleReport> uploadRuleReports(String... excelFilePaths) {
        return reportsManager.uploadRuleReports(excelFilePaths);
    } 
}