package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.SortType;
import com.search.manager.model.FacetSort;
import com.search.manager.report.model.xml.FacetSortGroupXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="facetSortVersionDAO")
public class FacetSortVersionDAO extends RuleVersionDAO<FacetSortRuleXml>{
	
	@Autowired private DaoService daoService;
	
	protected RuleEntity getRuleEntity() {
		return RuleEntity.FACET_SORT;
	}

	@Override
	protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes) {
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<FacetSortRuleXml> eRuleXmlList = ((RuleVersionListXml<FacetSortRuleXml>)ruleVersionListXml).getVersions();
			List<FacetSortGroupXml> eItemXmlList = new ArrayList<FacetSortGroupXml>();
			long version = ruleVersionListXml.getNextVersion();
			try {
				FacetSort facetSort = daoService.getFacetSort(new FacetSort(ruleId, store));
				Map<String, List<String>> items = facetSort.getItems();
				Map<String, SortType> sortType = facetSort.getGroupSortType();
				for(String mapKey: items.keySet()){
					eItemXmlList.add(new FacetSortGroupXml(mapKey, items.get(mapKey), sortType.get(mapKey), facetSort.getSortType()));
				}
				eRuleXmlList.add(new FacetSortRuleXml(store, version, name, notes, username, facetSort.getRuleType(), facetSort.getSortType(), ruleId, facetSort.getRuleName(), eItemXmlList));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(facetSort.getRuleName());
				return true;
			} catch (DaoException e) {
			}	
		}
		return false;
	}

}