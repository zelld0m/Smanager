package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="elevateVersionDAO")
public class ElevateVersionDAO extends RuleVersionDAO<ElevateRuleXml>{

	@Autowired private DaoService daoService;
	
	protected RuleEntity getRuleEntity() {
		return RuleEntity.ELEVATE;
	}
	
	protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes){
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<ElevateRuleXml> eRuleXmlList = ((RuleVersionListXml<ElevateRuleXml>) ruleVersionListXml).getVersions();
			List<ElevateItemXml> eItemXmlList = new ArrayList<ElevateItemXml>();
			long version = ruleVersionListXml.getNextVersion();

			try {
				// Get all items
				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(new ElevateResult(new StoreKeyword(store, ruleId)));
				List<ElevateResult> elevateItemList = daoService.getElevateResultList(criteria).getList();
				for (ElevateResult er : elevateItemList) {
					eItemXmlList.add(new ElevateItemXml(er));
				}
				eRuleXmlList.add(new ElevateRuleXml(store, version, name, notes, username, ruleId, eItemXmlList));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(ruleId);
				return true;
			} catch (DaoException e) {
			}	
		}
		return false;
	}
	
}