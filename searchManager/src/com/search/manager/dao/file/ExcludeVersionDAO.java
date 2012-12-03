package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ExcludeItemXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="excludeVersionDAO")
public class ExcludeVersionDAO extends RuleVersionDAO<ExcludeRuleXml>{
	
	@Autowired private DaoService daoService;
	
	@Override
	protected RuleEntity getRuleEntity() {
		return RuleEntity.EXCLUDE;
	}
	
	@Override
	protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes) {
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<ExcludeRuleXml> eRuleXmlList = ((RuleVersionListXml<ExcludeRuleXml>)ruleVersionListXml).getVersions();
			List<ExcludeItemXml> eItemXmlList = new ArrayList<ExcludeItemXml>();
			long version = ruleVersionListXml.getNextVersion();
			try {
				// Get all items
				SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(new StoreKeyword(store, ruleId)));
				List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(criteria).getList();
				for (ExcludeResult excludeResult : excludeItemList) {
					eItemXmlList.add(new ExcludeItemXml(excludeResult));
				}
				eRuleXmlList.add(new ExcludeRuleXml(store, version, name, notes, username, ruleId, eItemXmlList));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(ruleId);
				return true;
			} catch (DaoException e) {
			}	
		}
		return false;
	}
	
}