package com.search.manager.solr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleType;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.dao.DemoteDao;
import com.search.manager.solr.dao.ElevateDao;
import com.search.manager.solr.dao.ExcludeDao;
import com.search.manager.solr.dao.FacetSortDao;
import com.search.manager.solr.dao.RedirectDao;
import com.search.manager.solr.dao.RelevancyDao;

@Service("solrService")
public class SolrServiceImpl implements SolrService {

	@Autowired
	@Qualifier("demoteDaoSolr")
	private DemoteDao demoteDao;

	@Autowired
	@Qualifier("elevateDaoSolr")
	private ElevateDao elevateDao;

	@Autowired
	@Qualifier("excludeDaoSolr")
	private ExcludeDao excludeDao;

	@Autowired
	@Qualifier("redirectDaoSolr")
	private RedirectDao redirectDao;

	@Autowired
	@Qualifier("relevancyDaoSolr")
	private RelevancyDao relevancyDao;

	@Autowired
	@Qualifier("facetSortDaoSolr")
	private FacetSortDao facetSortDao;

	@Override
	public List<ElevateResult> getElevateRules(Store store) throws DaoException {
		return (List<ElevateResult>) elevateDao.getElevateRules(store);
	}

	@Override
	public List<ElevateResult> getElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		return (List<ElevateResult>) elevateDao.getElevateRules(storeKeyword);
	}

	@Override
	public boolean loadElevateRules(Store store) throws DaoException {
		return elevateDao.loadElevateRules(store);
	}

	@Override
	public boolean loadElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		return elevateDao.loadElevateRules(storeKeyword);
	}

	@Override
	public boolean resetElevateRules(Store store) throws DaoException {
		return elevateDao.resetElevateRules(store);
	}

	@Override
	public boolean resetElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		return elevateDao.resetElevateRules(storeKeyword);
	}

	@Override
	public boolean deleteElevateRules(Store store) throws DaoException {
		return elevateDao.deleteElevateRules(store);
	}

	@Override
	public boolean deleteElevateRules(StoreKeyword storeKeyword)
			throws DaoException {
		return elevateDao.deleteElevateRules(storeKeyword);
	}

	@Override
	public boolean updateElevateRule(ElevateResult elevateResult)
			throws DaoException {
		return elevateDao.updateElevateRule(elevateResult);
	}

	@Override
	public List<ExcludeResult> getExcludeRules(Store store) throws DaoException {
		return (List<ExcludeResult>) excludeDao.getExcludeRules(store);
	}

	@Override
	public List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {
		return (List<ExcludeResult>) excludeDao.getExcludeRules(storeKeyword);
	}

	@Override
	public boolean loadExcludeRules(Store store) throws DaoException {
		return excludeDao.loadExcludeRules(store);
	}

	@Override
	public boolean loadExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {
		return excludeDao.loadExcludeRules(storeKeyword);
	}

	@Override
	public boolean resetExcludeRules(Store store) throws DaoException {
		return excludeDao.resetExcludeRules(store);
	}

	@Override
	public boolean resetExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {
		return excludeDao.resetExcludeRules(storeKeyword);
	}

	@Override
	public boolean deleteExcludeRules(Store store) throws DaoException {
		return excludeDao.deleteExcludeRules(store);
	}

	@Override
	public boolean deleteExcludeRules(StoreKeyword storeKeyword)
			throws DaoException {
		return excludeDao.deleteExcludeRules(storeKeyword);
	}

	@Override
	public boolean updateExcludeRule(ExcludeResult excludeResult)
			throws DaoException {
		return excludeDao.updateExcludeRule(excludeResult);
	}

	@Override
	public List<DemoteResult> getDemoteRules(Store store) throws DaoException {
		return (List<DemoteResult>) demoteDao.getDemoteRules(store);
	}

	@Override
	public List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {
		return (List<DemoteResult>) demoteDao.getDemoteRules(storeKeyword);
	}

	@Override
	public boolean loadDemoteRules(Store store) throws DaoException {
		return demoteDao.loadDemoteRules(store);
	}

	@Override
	public boolean loadDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {
		return demoteDao.loadDemoteRules(storeKeyword);
	}

	@Override
	public boolean resetDemoteRules(Store store) throws DaoException {
		return demoteDao.resetDemoteRules(store);
	}

	@Override
	public boolean resetDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {
		return demoteDao.resetDemoteRules(storeKeyword);
	}

	@Override
	public boolean deleteDemoteRules(Store store) throws DaoException {
		return demoteDao.deleteDemoteRules(store);
	}

	@Override
	public boolean deleteDemoteRules(StoreKeyword storeKeyword)
			throws DaoException {
		return demoteDao.deleteDemoteRules(storeKeyword);
	}

	@Override
	public boolean updateDemoteRule(DemoteResult demoteResult)
			throws DaoException {
		return demoteDao.updateDemoteRule(demoteResult);
	}

	@Override
	public List<FacetSort> getFacetSortRules(Store store) throws DaoException {
		return (List<FacetSort>) facetSortDao.getFacetSortRules(store);
	}

	@Override
	public FacetSort getFacetSortRule(Store store, String name,
			RuleType ruleType) throws DaoException {
		return facetSortDao.getFacetSortRule(store, name, ruleType);
	}

	@Override
	public FacetSort getFacetSortRuleById(Store store, String id)
			throws DaoException {
		return facetSortDao.getFacetSortRuleById(store, id);
	}

	@Override
	public boolean loadFacetSortRules(Store store) throws DaoException {
		return facetSortDao.loadFacetSortRules(store);
	}

	@Override
	public boolean loadFacetSortRuleByName(Store store, String name)
			throws DaoException {
		return facetSortDao.loadFacetSortRuleByName(store, name);
	}

	@Override
	public boolean loadFacetSortRuleById(Store store, String id)
			throws DaoException {
		return facetSortDao.loadFacetSortRuleById(store, id);
	}

	@Override
	public boolean resetFacetSortRules(Store store) throws DaoException {
		return facetSortDao.resetFacetSortRules(store);
	}

	@Override
	public boolean resetFacetSortRuleByName(Store store, String name)
			throws DaoException {
		return facetSortDao.resetFacetSortRulesByName(store, name);
	}

	@Override
	public boolean resetFacetSortRuleById(Store store, String id)
			throws DaoException {
		return facetSortDao.resetFacetSortRulesById(store, id);
	}

	@Override
	public boolean deleteFacetSortRules(Store store) throws DaoException {
		return facetSortDao.deleteFacetSortRules(store);
	}

	@Override
	public boolean deleteFacetSortRuleByName(Store store, String name)
			throws DaoException {
		return facetSortDao.deleteFacetSortRuleByName(store, name);
	}

	@Override
	public boolean deleteFacetSortRuleById(Store store, String id)
			throws DaoException {
		return facetSortDao.deleteFacetSortRuleById(store, id);
	}

	@Override
	public boolean updateFacetSortRule(FacetSort facetSort) throws DaoException {
		return facetSortDao.updateFacetSortRule(facetSort);
	}

	@Override
	public List<RedirectRule> getRedirectRules(Store store) throws DaoException {
		return (List<RedirectRule>) redirectDao.getRedirectRules(store);
	}

	@Override
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword)
			throws DaoException {
		return redirectDao.getRedirectRule(storeKeyword);
	}

	@Override
	public RedirectRule getRedirectRuleByName(Store store, String name)
			throws DaoException {
		return redirectDao.getRedirectRuleByName(store, name);
	}

	@Override
	public RedirectRule getRedirectRuleById(Store store, String id)
			throws DaoException {
		return redirectDao.getRedirectRuleById(store, id);
	}

	@Override
	public boolean loadRedirectRules(Store store) throws DaoException {
		return redirectDao.loadRedirectRules(store);
	}

	@Override
	public boolean loadRedirectRuleByName(Store store, String name)
			throws DaoException {
		return redirectDao.loadRedirectRuleByName(store, name);
	}

	@Override
	public boolean loadRedirectRuleById(Store store, String id)
			throws DaoException {
		return redirectDao.loadRedirectRuleById(store, id);
	}

	@Override
	public boolean resetRedirectRules(Store store) throws DaoException {
		return redirectDao.resetRedirectRules(store);
	}

	@Override
	public boolean resetRedirectRuleByName(Store store, String name)
			throws DaoException {
		return redirectDao.resetRedirectRuleByName(store, name);
	}

	@Override
	public boolean resetRedirectRuleById(Store store, String id)
			throws DaoException {
		return redirectDao.resetRedirectRuleById(store, id);
	}

	@Override
	public boolean deleteRedirectRules(Store store) throws DaoException {
		return redirectDao.deleteRedirectRules(store);
	}

	@Override
	public boolean deleteRedirectRuleByName(Store store, String name)
			throws DaoException {
		return redirectDao.deleteRedirectRuleByName(store, name);
	}

	@Override
	public boolean deleteRedirectRuleById(Store store, String id)
			throws DaoException {
		return redirectDao.deleteRedirectRuleById(store, id);
	}

	@Override
	public boolean updateRedirectRule(RedirectRule redirectRule)
			throws DaoException {
		return redirectDao.updateRedirectRule(redirectRule);
	}

	@Override
	public List<Relevancy> getRelevancyRules(Store store) throws DaoException {
		return (List<Relevancy>) relevancyDao.getRelevancyRules(store);
	}

	@Override
	public Relevancy getDefaultRelevancyRule(Store store) throws DaoException {
		return relevancyDao.getDefaultRelevancyRule(store);
	}

	@Override
	public Relevancy getRelevancyRule(StoreKeyword storeKeyword)
			throws DaoException {
		return relevancyDao.getRelevancyRule(storeKeyword);
	}

	@Override
	public Relevancy getRelevancyRuleByName(Store store, String name)
			throws DaoException {
		return relevancyDao.getRelevancyRuleByName(store, name);
	}

	@Override
	public Relevancy getRelevancyRuleById(Store store, String id)
			throws DaoException {
		return relevancyDao.getRelevancyRuleById(store, id);
	}

	@Override
	public boolean loadRelevancyRules(Store store) throws DaoException {
		return relevancyDao.loadRelevancyRules(store);
	}

	@Override
	public boolean loadRelevancyRuleByName(Store store, String name)
			throws DaoException {
		return relevancyDao.loadRelevancyRuleByName(store, name);
	}

	@Override
	public boolean loadRelevancyRuleById(Store store, String id)
			throws DaoException {
		return relevancyDao.loadRelevancyRuleById(store, id);
	}

	@Override
	public boolean resetRelevancyRules(Store store) throws DaoException {
		return relevancyDao.resetRelevancyRules(store);
	}

	@Override
	public boolean resetRelevancyRuleByName(Store store, String name)
			throws DaoException {
		return relevancyDao.resetRelevancyRuleByName(store, name);
	}

	@Override
	public boolean resetRelevancyRuleById(Store store, String id)
			throws DaoException {
		return relevancyDao.resetRelevancyRuleById(store, id);
	}

	@Override
	public boolean deleteRelevancyRules(Store store) throws DaoException {
		return relevancyDao.deleteRelevancyRules(store);
	}

	@Override
	public boolean deleteRelevancyRuleByName(Store store, String name)
			throws DaoException {
		return relevancyDao.deleteRelevancyRuleByName(store, name);
	}

	@Override
	public boolean deleteRelevancyRuleById(Store store, String id)
			throws DaoException {
		return relevancyDao.deleteRelevancyRuleById(store, id);
	}

	@Override
	public boolean updateRelevancyRule(Relevancy relevancy) throws DaoException {
		return relevancyDao.updateRelevancyRule(relevancy);
	}

}
