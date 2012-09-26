package com.search.manager.service;

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
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;

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
	public int addRule(String ruleName, String ruleType, String sortType) {
		int result = 0;

		try {
			String store = UtilityService.getStoreName();
			String username = UtilityService.getUsername();
			FacetSort rule = new FacetSort(ruleName, ruleType, sortType, store);
			rule.setCreatedBy(username);
			String ruleId = daoService.addFacetSortAndGetId(rule);

			if (StringUtils.isNotBlank(ruleId)){
				result = addAllFacetGroup(ruleId);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRule()",e);
		}

		return result;
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
	public int updateRule(String ruleId) {
		int result = -1;

		try {
			String store = UtilityService.getStoreName();
			String username = UtilityService.getUsername();
			FacetSort rule = new FacetSort(ruleId, store);
			rule.setLastModifiedBy(username);
			return daoService.updateFacetSort(rule);
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
			SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(facetSort, null, null, page, itemsPerPage);
			return daoService.searchFacetSort(criteria, MatchType.LIKE_NAME);
		} catch (DaoException e) {
			logger.error("Failed during getAllRule()",e);
		}
		return null;
	}

	public int addAllFacetGroup(String ruleId) {
		int result = -1;

		for(FacetGroupType facetGroupType : FacetGroupType.values()){
			result = (result == -1) ? result = 0: result;
			result += addFacetGroup(ruleId, facetGroupType.getDisplayText(), facetGroupType.name(), null, facetGroupType.ordinal());
		}

		return result;
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
				addFacetGroupItem(new FacetGroupItem("", facetGroupId, arrFacetGroupItem[i], i+1));
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