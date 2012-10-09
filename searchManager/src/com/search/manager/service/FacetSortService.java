package com.search.manager.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

@Service(value = "facetSortService")
@RemoteProxy(
		name = "FacetSortServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "facetSortService")
)
public class FacetSortService {
	private static final Logger logger = Logger.getLogger(FacetSortService.class);

	@Autowired private DaoService daoService;

	@RemoteMethod
	public FacetSort addRule(String ruleName, String ruleType, String sortType) {
		int result = -1;
		String ruleId = "";
		String store = UtilityService.getStoreName();
		String username = UtilityService.getUsername();

		try {
			FacetSort rule = new FacetSort(ruleName, ruleType, sortType, store);
			rule.setCreatedBy(username);
			ruleId = daoService.addFacetSortAndGetId(rule);

			if (StringUtils.isNotBlank(ruleId)){
				if(RuleType.KEYWORD.getDisplayText().equalsIgnoreCase(ruleType))
					daoService.addKeyword(new StoreKeyword(store, ruleName));
				
				result = addAllFacetGroup(ruleId);
			}

			if (result>0){
				return getRuleById(ruleId);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRule()",e);
			try {
				daoService.deleteFacetSort(new FacetSort(ruleId, store));
			} catch (DaoException de) {
				logger.error("Unable to complete process, need to manually delete rule", de);
			}
		}

		return null;
	}

	@RemoteMethod
	public static Map<String, String> getSortOrderList(){
		Map<String, String> sortOrderList = new LinkedHashMap<String, String>();

		for (SortType st: SortType.values()) {
			sortOrderList.put(st.name(), st.getDisplayText());
		}

		return sortOrderList;
	}  

	@RemoteMethod
	public int deleteRule(String ruleId) {
		int result = -1;

		try {
			String store = UtilityService.getStoreName();
			String username = UtilityService.getUsername();
			FacetSort rule = new FacetSort(ruleId, store);
			rule.setLastModifiedBy(username);
			return daoService.deleteFacetSort(rule);
		} catch (DaoException e) {
			logger.error("Failed during deleteRule()",e);
		}

		return result;
	}

	@RemoteMethod
	public int updateRule(String ruleId, String name, String sortType, Map<String, String[]> facetGroupItems, Map<String, String>  sortOrders) {
		int result = -1;

		try {
			String store = UtilityService.getStoreName();
			String username = UtilityService.getUsername();
			FacetSort rule = new FacetSort(ruleId, name, "", sortType, store);
			rule.setLastModifiedBy(username);
			
			if(MapUtils.isNotEmpty(facetGroupItems)){
				FacetGroup facetGroup = new FacetGroup();
				
				for(Map.Entry<String, String[]> entry: facetGroupItems.entrySet()){
					String facetGroupId = entry.getKey();
					String[] arrFacetGroupItems = entry.getValue();
					
					facetGroup.setId(facetGroupId);
					facetGroup.setSortType(SortType.get(sortOrders.get(facetGroupId)));
					daoService.updateFacetGroup(facetGroup);
				
					clearFacetGroupItem(facetGroupId);
					for(int i=0; i < ArrayUtils.getLength(arrFacetGroupItems); i++){
						addSingleFacetGroupItem(facetGroupId, arrFacetGroupItems[i], i+1);
					}					
				}
			}
		
		} catch (DaoException e) {
			logger.error("Failed during updateRule()",e);
		}

		return result;
	}

	@RemoteMethod
	public RecordSet<FacetSort> getAllRule(String name, int page, int itemsPerPage){
		logger.info(String.format("%s %d %d", name, page, itemsPerPage));
		try {
			String store = UtilityService.getStoreName();
			FacetSort facetSort = new FacetSort("", name, store);
			SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(facetSort, page, itemsPerPage);
			return daoService.searchFacetSort(criteria, MatchType.LIKE_NAME);
		} catch (DaoException e) {
			logger.error("Failed during getAllRule()",e);
		}
		return null;
	}

	public FacetSort getRule(FacetSort facetSort)  {
		try {
			return daoService.getFacetSort(facetSort);
		} catch (DaoException e) {
			logger.error("Failed during getRule()",e);
		}
		return null;
	}

	@RemoteMethod
	public FacetSort getRuleById(String ruleId) {
		String store = UtilityService.getStoreName();
		return getRule(new FacetSort(ruleId, store));
	}
	
	@RemoteMethod
	public FacetSort getRuleByNameAndType(String ruleName, String ruleType) {
		String store = UtilityService.getStoreName();
		return getRule(new FacetSort(ruleName, RuleType.get(ruleType), null, new Store(store)));
	}

	@RemoteMethod
	public RecordSet<FacetGroup> getAllFacetGroup(String ruleId){
		try {
			FacetGroup facetGroup = new FacetGroup(ruleId, "");
			SearchCriteria<FacetGroup> criteria = new SearchCriteria<FacetGroup>(facetGroup);
			return daoService.searchFacetGroup(criteria, MatchType.MATCH_ID);
		} catch (DaoException e) {
			logger.error("Failed during getAllFacetGroup()",e);
		}
		return null;
	}
	
	public int addAllFacetGroup(String ruleId) {
		int facetGroupAdded = 0;

		for(FacetGroupType facetGroupType : FacetGroupType.values()){
			facetGroupAdded += addFacetGroup(ruleId, facetGroupType.getDisplayText(), facetGroupType.getDisplayText(), null, Integer.parseInt(facetGroupType.toString())) > 0 ? 1 : 0;
		}

		return facetGroupAdded;
	}

	@RemoteMethod
	public int addFacetGroup(String ruleId, String name, String facetGroupType, String sortType, Integer sequence) {
		int result = -1;

		try {
			String username = UtilityService.getUsername();
			FacetGroup facetGroup = new FacetGroup(ruleId, name, facetGroupType, sortType, sequence);
			facetGroup.setCreatedBy(username);
			return daoService.addFacetGroup(facetGroup);
		} catch (DaoException e) {
			logger.error("Failed during addFacetGroup()",e);
		}

		return result;
	}

	@RemoteMethod
	public RecordSet<FacetGroupItem> getAllFacetGroupItem(String ruleId, String facetGroupId){
		try {
			FacetGroupItem facetGroupItem = new FacetGroupItem(ruleId, facetGroupId);
			SearchCriteria<FacetGroupItem> criteria = new SearchCriteria<FacetGroupItem>(facetGroupItem);
			return daoService.searchFacetGroupItem(criteria, MatchType.MATCH_ID);
		} catch (DaoException e) {
			logger.error("Failed during getAllFacetGroup()",e);
		}
		return null;
	}
	
	public int addFacetGroupItem(FacetGroupItem facetGroupItem) {
		int result = -1;
		try {
			result = daoService.addFacetGroupItem(facetGroupItem);
		} catch (DaoException e) {
			logger.error("Failed during addFacetGroupItem()",e);
		}
		return result;
	}

	@RemoteMethod
	public int addAllFacetGroupItem(String facetGroupId, String facetGroupItems) {
		int result = -1;

		String[] arrFacetGroupItem = StringUtils.split(facetGroupItems, ',');
		int arrFacetGroupItemSize = ArrayUtils.getLength(arrFacetGroupItem);

		if (arrFacetGroupItemSize > 0){
			clearFacetGroupItem(facetGroupId);
			for (int i=0; i<arrFacetGroupItemSize ; i++){
				addFacetGroupItem(new FacetGroupItem(facetGroupId, "" , arrFacetGroupItem[i], i+1));
			}
		}
		return result;
	}

	@RemoteMethod
	public int addSingleFacetGroupItem(String facetGroupId, String name, Integer sequence) {
		FacetGroupItem facetGroupItem = new FacetGroupItem(facetGroupId, "" , name, sequence);
		facetGroupItem.setCreatedBy(UtilityService.getUsername());
		return addFacetGroupItem(facetGroupItem);
	}

	@RemoteMethod
	public int updateFacetGroupItem(String memberId, String name, Integer sequence) {
		int result = -1;

		try {
			String username = UtilityService.getUsername();
			FacetGroupItem facetGroupItem = new FacetGroupItem(memberId, name, sequence);
			facetGroupItem.setLastModifiedBy(username);
			return daoService.updateFacetGroupItem(facetGroupItem);
		} catch (DaoException e) {
			logger.error("Failed during deleteFacetGroupItem()",e);
		}

		return result;
	}

	@RemoteMethod
	public int deleteFacetGroupItem(String memberId) {
		int result = -1;

		try {
			String username = UtilityService.getUsername();
			FacetGroupItem facetGroupItem = new FacetGroupItem(memberId);
			facetGroupItem.setLastModifiedBy(username);
			return daoService.deleteFacetGroupItem(facetGroupItem);
		} catch (DaoException e) {
			logger.error("Failed during deleteFacetGroupItem()",e);
		}

		return result;
	}

	@RemoteMethod
	public int clearFacetGroupItem(String facetGroupId) {
		int result = -1;

		try {
			String username = UtilityService.getUsername();
			FacetGroup facetGroup = new FacetGroup(facetGroupId);
			facetGroup.setLastModifiedBy(username);
			return daoService.clearFacetGroupItem(facetGroup);
		} catch (DaoException e) {
			logger.error("Failed during clearFacetGroupItem()",e);
		}

		return result;
	}
}