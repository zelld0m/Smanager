package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;

@Repository(value="demoteVersionDAO")
public class DemoteVersionDAO extends RuleVersionDAO<DemoteRuleXml>{
	
	@Autowired private DaoService daoService;
	
	@Override
	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFileName(store, RuleEntity.DEMOTE, ruleId);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<DemoteRuleXml> getRuleVersionList(String store, String ruleId) {
		return (RuleVersionListXml<DemoteRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.DEMOTE, ruleId);
	}
	
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<DemoteRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<DemoteRuleXml> demoteRuleXmlList = ruleVersionListXml.getVersions();
			List<DemoteItemXml> demoteItemXmlList = new ArrayList<DemoteItemXml>();

			// Get all items
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(new DemoteResult(new StoreKeyword(store, ruleId)));

			try {
				List<DemoteResult> demoteItemList = daoService.getDemoteResultList(criteria).getList();
				for (DemoteResult demoteResult : demoteItemList) {
					demoteItemXmlList.add(new DemoteItemXml(demoteResult));
				}
			} catch (DaoException e) {
				return false;
			}	

			demoteRuleXmlList.add(new DemoteRuleXml(store, version, name, notes, username, ruleId, demoteItemXmlList));

			ruleVersionListXml.setRuleId(ruleId);
			ruleVersionListXml.setRuleName(ruleId);
			ruleVersionListXml.setVersions(demoteRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.DEMOTE, ruleId, ruleVersionListXml);
		}

		return false;
	}
}