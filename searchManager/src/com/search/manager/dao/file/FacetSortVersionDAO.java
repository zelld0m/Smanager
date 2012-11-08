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
import com.search.manager.report.model.xml.FacetSortItemXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="facetSortVersionDAO")
public class FacetSortVersionDAO extends RuleVersionDAO<FacetSortRuleXml>{
	
	@Autowired private DaoService daoService;
	
	@Override
	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFileName(store, RuleEntity.FACET_SORT, ruleId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<FacetSortRuleXml> getRuleVersionList(
			String store, String ruleId) {
		return (RuleVersionListXml<FacetSortRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.FACET_SORT, ruleId);
	}

	@Override
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes) {
		RuleVersionListXml<FacetSortRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<FacetSortRuleXml> facetSortRuleXmlList = ruleVersionListXml.getVersions();
			List<FacetSortItemXml> facetSortItemXml = new ArrayList<FacetSortItemXml>();

			// Get all items
			try {
				FacetSort facetSort = daoService.getFacetSort(new FacetSort(ruleId, store));
				
				Map<String, List<String>> items = facetSort.getItems();
				Map<String, SortType> sortType = facetSort.getGroupSortType();
	
				for(String mapKey: items.keySet()){
					facetSortItemXml.add(new FacetSortItemXml(mapKey, items.get(mapKey), sortType.get(mapKey), facetSort.getSortType()));
				}
				
				facetSortRuleXmlList.add(new FacetSortRuleXml(store, version, name, notes, username, facetSort.getRuleType(), facetSort.getSortType(), ruleId, facetSort.getRuleName(), facetSortItemXml));

				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(facetSort.getRuleName());
				ruleVersionListXml.setVersions(facetSortRuleXmlList);
			
			} catch (DaoException e) {
				return false;
			}	

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.FACET_SORT, ruleId, ruleVersionListXml);
		}

		return false;
	}
}