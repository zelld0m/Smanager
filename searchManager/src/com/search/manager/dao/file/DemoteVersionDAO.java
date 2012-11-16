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

@Repository(value="demoteVersionDAO")
public class DemoteVersionDAO extends RuleVersionDAO<DemoteRuleXml>{
	
	@Autowired private DaoService daoService;
	
	@Override
	protected RuleEntity getRuleEntity() {
		return RuleEntity.DEMOTE;
	}

	@Override
	protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes) {
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<DemoteRuleXml> eRuleXmlList = ((RuleVersionListXml<DemoteRuleXml>)ruleVersionListXml).getVersions();
			List<DemoteItemXml> eItemXmlList = new ArrayList<DemoteItemXml>();
			long version = ruleVersionListXml.getNextVersion();
			try {
				// Get all items
				SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(new DemoteResult(new StoreKeyword(store, ruleId)));
				List<DemoteResult> demoteItemList = daoService.getDemoteResultList(criteria).getList();
				for (DemoteResult er : demoteItemList) {
					eItemXmlList.add(new DemoteItemXml(er));
				}
				eRuleXmlList.add(new DemoteRuleXml(store, version, name, notes, username, ruleId, eItemXmlList));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(ruleId);
			} catch (DaoException e) {
			}	
		}
		return false;
	}
}