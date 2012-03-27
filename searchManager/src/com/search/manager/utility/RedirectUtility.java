package com.search.manager.utility;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;

public class RedirectUtility {

	
	private DaoService daoService;

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	private static HashMap<String, String> fqMap; 
	private static final String DBL_ESC_PIPE_DELIM = "\\|\\|";
	private static final String DBL_PIPE_DELIM = "||";
	private static final String OR = ") OR (";

	private static Logger logger = Logger.getLogger(RedirectUtility.class);

	public RedirectUtility() {
	}

	public void updateRuleMap() {
		fqMap = new HashMap<String, String>();
		RecordSet<RedirectRule> rRuleSet = null;
		try {
			rRuleSet = daoService.getRedirectRule(null, null, null, null, null);
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		List<RedirectRule> ruleList = rRuleSet.getList();
		for (RedirectRule rule : ruleList) {
			String[] searchTerms = rule.getSearchTerm().split(DBL_ESC_PIPE_DELIM);
			for (String searchTerm : searchTerms) {
				fqMap.put((rule.getStoreId()+searchTerm).toLowerCase(), rule.getCondition());
			}
		}
	}

//	private String addQuoteToManufacturer(String condition) {
//		StringBuilder sb = new StringBuilder(condition);
//		int pos = condition.indexOf("Manufacturer:"); 
//		if ( pos > -1) {
//			sb.insert(pos+13, "\"").append("\"");
//			
//		}
//		return sb.toString();
//	}

	public String getRedirectFQ(String keyword) {
		StringBuilder fq = new StringBuilder();
		if (fqMap==null) {
			fqMap = new HashMap<String, String>();
			updateRuleMap();
		}
		String condition = fqMap.get(keyword.toLowerCase());
		if (condition!=null) {
			fq = fq.append(condition.replace(DBL_PIPE_DELIM, OR));
		}
		if (fq.length()>0) {
			fq.insert(0,"(").append(")");
		}
		return fq.toString();
	}

}
