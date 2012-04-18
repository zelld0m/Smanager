package com.search.manager.utility;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;

@Repository(value="redirectUtility")
public class RedirectUtility {
	
	@Autowired private DaoService daoService;
	
	private static HashMap<String, String> fqMap; 
	private static HashMap<String, String> redirectMap; 
	public static final String DBL_ESC_PIPE_DELIM = "\\|\\|";
	public static final String DBL_PIPE_DELIM = "||";
	public static final String OR = ") OR (";

	private static Logger logger = Logger.getLogger(RedirectUtility.class);

	public RedirectUtility() {
	}

	public void updateRuleMap() {
		fqMap = new HashMap<String, String>();
		redirectMap = new HashMap<String, String>();
		RecordSet<RedirectRule> rRuleSet = null;
		try {
			rRuleSet = daoService.getRedirectRule(null, null, null, null, null);
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		List<RedirectRule> ruleList = rRuleSet.getList();
		if (ruleList != null) {
			for (RedirectRule rule : ruleList) {
				String[] searchTerms = rule.getSearchTerm().split(DBL_ESC_PIPE_DELIM);
				for (String searchTerm : searchTerms) {
					if (rule.getCondition().startsWith("http://")) {
						redirectMap.put((rule.getStoreId()+searchTerm).toLowerCase(), rule.getCondition());
					} else {
						fqMap.put((rule.getStoreId()+searchTerm).trim().toLowerCase(), rule.getCondition());
					}
				}
			}
		}
	}

	public String getRedirectURL(String keyword) {
		if (redirectMap==null) {
			updateRuleMap();
		}
		return redirectMap.get(keyword.toLowerCase());
	}

	public String getRedirectFQ(String keyword) {
		StringBuilder fq = new StringBuilder();
		String condition = fqMap.get(keyword.toLowerCase());
		if (condition!=null) {
			fq = fq.append(condition.replace(DBL_PIPE_DELIM, OR));
		}
		if (fq.length()>0) {
			fq.insert(0,"(").append(")");
		}
		return fq.toString();
	}
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}