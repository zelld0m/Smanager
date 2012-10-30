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
import com.search.manager.utility.StringUtil;

@Repository(value="elevateVersionDAO")
public class ElevateVersionDAO extends RuleVersionDAO<ElevateRuleXml>{

	@Autowired private DaoService daoService;
	
	@Override
	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFileName(store, RuleEntity.ELEVATE, StringUtil.escapeKeyword(ruleId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<ElevateRuleXml> getRuleVersionList(String store, String ruleId) {
		return (RuleVersionListXml<ElevateRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.ELEVATE, ruleId);
	}
	
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes){
		RuleVersionListXml<ElevateRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<ElevateRuleXml> elevateRuleXmlList = ruleVersionListXml.getVersions();
			List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();

			// Get all items
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(new ElevateResult(new StoreKeyword(store, ruleId)));

			try {
				List<ElevateResult> elevateItemList = daoService.getElevateResultList(criteria).getList();
				for (ElevateResult elevateResult : elevateItemList) {
					elevateItemXmlList.add(new ElevateItemXml(elevateResult));
				}
			} catch (DaoException e) {
				return false;
			}	

			elevateRuleXmlList.add(new ElevateRuleXml(store, version, name, notes, username, ruleId, elevateItemXmlList));

			ruleVersionListXml.setRuleId(ruleId);
			ruleVersionListXml.setRuleName(ruleId);
			ruleVersionListXml.setVersions(elevateRuleXmlList);

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.ELEVATE, ruleId, ruleVersionListXml);
		}

		return false;
	}

	public boolean restoreRuleVersion(String store, String ruleId, String username, long version) {
		// TODO Auto-generated method stub
		return false;
	}
}