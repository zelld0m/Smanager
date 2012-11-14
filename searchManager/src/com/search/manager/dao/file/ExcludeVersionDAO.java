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
	
	static {
		ruleEntity = RuleEntity.EXCLUDE;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<ExcludeRuleXml> getRuleVersionList(String store, String ruleId) {
		return (RuleVersionListXml<ExcludeRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.EXCLUDE, ruleId);
	}
	
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<ExcludeRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<ExcludeRuleXml> excludeRuleXmlList = ruleVersionListXml.getVersions();
			List<ExcludeItemXml> excludeItemXmlList = new ArrayList<ExcludeItemXml>();

			// Get all items
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(new StoreKeyword(store, ruleId)));

			try {
				List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(criteria).getList();
				for (ExcludeResult excludeResult : excludeItemList) {
					excludeItemXmlList.add(new ExcludeItemXml(excludeResult));
				}
			} catch (DaoException e) {
				return false;
			}	

			excludeRuleXmlList.add(new ExcludeRuleXml(store, version, name, notes, username, ruleId, excludeItemXmlList));

			ruleVersionListXml.setRuleId(ruleId);
			ruleVersionListXml.setRuleName(ruleId);
			ruleVersionListXml.setVersions(excludeRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.EXCLUDE, ruleId, ruleVersionListXml);
		}

		return false;
	}
}