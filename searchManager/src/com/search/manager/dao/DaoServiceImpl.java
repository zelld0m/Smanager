package com.search.manager.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.sp.AuditTrailDAO;
import com.search.manager.dao.sp.BannerDAO;
import com.search.manager.dao.sp.CampaignDAO;
import com.search.manager.dao.sp.CategoryDAO;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.ElevateDAO;
import com.search.manager.dao.sp.ExcludeDAO;
import com.search.manager.dao.sp.KeywordDAO;
import com.search.manager.dao.sp.RedirectRuleDAO;
import com.search.manager.dao.sp.RelevancyDAO;
import com.search.manager.dao.sp.RuleStatusDAO;
import com.search.manager.dao.sp.StoreKeywordDAO;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.CategoryList;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.NameValue;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Redirect;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.ws.SearchHelper;

@Service("daoService")
public class DaoServiceImpl implements DaoService {

	private Logger logger = Logger.getLogger(DaoServiceImpl.class);

	@Autowired private KeywordDAO 		keywordDAO;
	@Autowired private StoreKeywordDAO storeKeywordDAO;
	@Autowired private ElevateDAO 		elevateDAO;
	@Autowired private ExcludeDAO 		excludeDAO;
	@Autowired private AuditTrailDAO 	auditTrailDAO;
	@Autowired private BannerDAO 		bannerDAO;
	@Autowired private CampaignDAO 	campaignDAO;
	@Autowired private RelevancyDAO	relevancyDAO;
	@Autowired private CategoryDAO		categoryDAO;
	@Autowired private RedirectRuleDAO	redirectRuleDAO;
	@Autowired private RuleStatusDAO	ruleStatusDAO;

	DaoServiceImpl instance;
	public DaoServiceImpl() {
		instance = this;
	}
	
	public DaoServiceImpl getInstance() {
		return instance;
	}
	
	public void setElevateDAO(ElevateDAO elevateDAO) {
		this.elevateDAO = elevateDAO;
	}

	public void setKeywordDAO(KeywordDAO keywordDAO) {
		this.keywordDAO = keywordDAO;
	}

	public void setStoreKeywordDAO(StoreKeywordDAO storeKeywordDAO) {
		this.storeKeywordDAO = storeKeywordDAO;
	}

	public void setExcludeDAO(ExcludeDAO excludeDAO) {
		this.excludeDAO = excludeDAO;
	}

	public void setAuditTrailDAO(AuditTrailDAO auditTrailDAO) {
		this.auditTrailDAO = auditTrailDAO;
	}

	public void setBannerDAO(BannerDAO bannerDAO) {
		this.bannerDAO = bannerDAO;
	}

	public void setCampaignDAO(CampaignDAO campaignDAO) {
		this.campaignDAO = campaignDAO;
	}

	public void setRelevancyDAO(RelevancyDAO relevancyDAO) {
		this.relevancyDAO = relevancyDAO;
	}

	public void setCategoryDAO(CategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}

	public void setRedirectRuleDAO(RedirectRuleDAO redirectRuleDAO) {
		this.redirectRuleDAO = redirectRuleDAO;
	}

	public void setRuleStatusDAO(RuleStatusDAO ruleStatusDAO) {
		this.ruleStatusDAO = ruleStatusDAO;
	}

	/* Audit Trail */
    public RecordSet<AuditTrail> getAuditTrail(SearchCriteria<AuditTrail> auditDetail) {
    	return auditTrailDAO.getAuditTrail(auditDetail);
    }
    
    public int addAuditTrail(AuditTrail auditTrail) {
    	return auditTrailDAO.addAuditTrail(auditTrail);
    }
    
	@Override
	public List<NameValue> getDropdownValues() throws DaoException {
		return auditTrailDAO.getDropdownValues();
	}

	/* Big Bets */
	/* retrieve data from both Solr and DB */
	public RecordSet<ElevateProduct> getElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException{
		RecordSet<ElevateResult> set = getElevateResultList(criteria);
		LinkedHashMap<String, ElevateProduct> map = new LinkedHashMap<String, ElevateProduct>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (ElevateResult e: set.getList()) {
			ElevateProduct ep = new ElevateProduct();
			ep.setEdp(e.getEdp());
			ep.setLocation(e.getLocation());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			map.put(e.getEdp(), ep);
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<ElevateProduct>(new ArrayList<ElevateProduct>(map.values()),set.getTotalSize());
	}
	
	@Override
	public ElevateProduct getElevatedProduct(String serverName, ElevateResult elevate) throws DaoException {
		ElevateResult e = getElevateItem(elevate);
		StoreKeyword sk = elevate.getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		LinkedHashMap<String, ElevateProduct> map = new LinkedHashMap<String, ElevateProduct>();
		ElevateProduct ep = new ElevateProduct();
		ep.setEdp(e.getEdp());
		ep.setLocation(e.getLocation());
		ep.setExpiryDate(e.getExpiryDate());
		ep.setCreatedDate(e.getCreatedDate());
		ep.setLastModifiedDate(e.getLastModifiedDate());
		ep.setComment(e.getComment());
		ep.setLastModifiedBy(e.getLastModifiedBy());
		ep.setCreatedBy(e.getCreatedBy());
		ep.setStore(elevate.getStoreKeyword().getStoreId());
		map.put(e.getEdp(), ep);
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return map.get(e.getEdp());
	}

	@Override
	public RecordSet<ElevateProduct> getNoExpiryElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException {
		RecordSet<ElevateResult> set = getNoExpireElevateResultList(criteria);
		LinkedHashMap<String, ElevateProduct> map = new LinkedHashMap<String, ElevateProduct>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (ElevateResult e: set.getList()) {
			ElevateProduct ep = new ElevateProduct();
			ep.setEdp(e.getEdp());
			ep.setLocation(e.getLocation());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			map.put(e.getEdp(), ep);
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<ElevateProduct>(new ArrayList<ElevateProduct>(map.values()),set.getTotalSize());
	}
	
	@Override
	public RecordSet<Product> getExcludedProducts(String serverName, SearchCriteria<ExcludeResult> criteria) throws DaoException {
		RecordSet<ExcludeResult> set = getExcludeResultList(criteria);
		LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (ExcludeResult e: set.getList()) {
			Product ep = new Product();
			ep.setEdp(e.getEdp());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			map.put(e.getEdp(), ep);
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<Product>(new ArrayList<Product>(map.values()),set.getTotalSize());
	}
	
	@Override
	public String getEdpByPartNumber(String serverName, String storeId, String keyword, String partNumber) {
		return SearchHelper.getEdpByPartNumber(serverName, storeId, keyword, partNumber);
	}

	/* Keywords */
	@Override
	public int addKeyword(StoreKeyword storeKeyword) throws DaoException {
		if (StringUtils.isNotEmpty(DAOUtils.getKeywordId(storeKeyword)) && StringUtils.isNotEmpty(DAOUtils.getStoreId(storeKeyword))) {
			Keyword k = keywordDAO.getKeyword(storeKeyword.getKeyword());
			if (k == null) {
				keywordDAO.addKeyword(storeKeyword.getKeyword());
			}
		    return storeKeywordDAO.addStoreKeyword(storeKeyword);
		}
		return -1;
	}
	
	@Override
	public StoreKeyword updateKeyword(String storeId, String oldKeyword, String newKeyword) throws DaoException {
		throw new DaoException("Unsupported operation", new RuntimeException("Unsupported operation"));
	}
	
	@Override
	public int deleteKeyword(String storeId, String keyword) throws DaoException {
		throw new DaoException("Unsupported operation", new RuntimeException("Unsupported operation"));
	}
	
	@Override
	public StoreKeyword getKeyword(String storeId, String keyword) throws DaoException {
		return storeKeywordDAO.getStoreKeyword(storeId, keyword);
	}

	@Override
	public RecordSet<StoreKeyword> getAllKeywords(String storeId) throws DaoException {
		SearchCriteria<StoreKeyword> sc = new SearchCriteria<StoreKeyword>(new StoreKeyword(storeId, ""), null, null, 0, 0);
		return storeKeywordDAO.getStoreKeywords(sc);
	}

	@Override
	public RecordSet<StoreKeyword> getAllKeywords(String storeId, Integer page, Integer itemsPerPage) throws DaoException {
		SearchCriteria<StoreKeyword> sc = new SearchCriteria<StoreKeyword>(new StoreKeyword(storeId, ""), null, null, page, itemsPerPage);
		return storeKeywordDAO.getStoreKeywords(sc);
	}

	@Override
	public RecordSet<StoreKeyword> getAllKeywordsMatching(String storeId, String keyword) throws DaoException {
		SearchCriteria<StoreKeyword> sc = new SearchCriteria<StoreKeyword>(new StoreKeyword(storeId, keyword), null, null, 0, 0);
		return storeKeywordDAO.getStoreKeywords(sc);
	}

	@Override
	public RecordSet<StoreKeyword> getAllKeywordsMatching(String storeId, String keyword, Integer page, Integer itemsPerPage) throws DaoException {
		SearchCriteria<StoreKeyword> sc = new SearchCriteria<StoreKeyword>(new StoreKeyword(storeId, keyword), null, null, page, itemsPerPage);
		return storeKeywordDAO.getStoreKeywords(sc);
	}
	
	/* Elevate */
	@Override
	public int addElevateResult(ElevateResult elevate) throws DaoException {
		return elevateDAO.addElevate(elevate);
	}

	
	@Override
	public RecordSet<ElevateResult> getElevateResultList(SearchCriteria<ElevateResult> criteria) throws DaoException {
		RecordSet<ElevateResult> list = elevateDAO.getElevate(criteria);
		return list;
	}

	@Override
	public RecordSet<ElevateResult> getNoExpireElevateResultList(SearchCriteria<ElevateResult> criteria) throws DaoException {
		return elevateDAO.getElevateNoExpiry(criteria);
	}

	@Override
	public ElevateResult getElevateItem(ElevateResult elevate) throws DaoException {
		return elevateDAO.getElevateItem(elevate);
	}

	@Override
	public Map<String, ElevateResult> getElevateResultMap(SearchCriteria<ElevateResult> criteria) throws DaoException {
		Map<String, ElevateResult> map = new LinkedHashMap<String, ElevateResult>();
		RecordSet<ElevateResult> set = elevateDAO.getElevate(criteria);
		for (ElevateResult e: set.getList()) {
			map.put(e.getEdp(), e);
		}
		return map;
	}

	@Override
	public int deleteElevateResult(ElevateResult elevate) throws DaoException {
		return elevateDAO.removeElevate(elevate);
	}

	@Override
	public int clearElevateResult(StoreKeyword keyword) throws DaoException {
		return elevateDAO.clearElevate(keyword);
	}
	
	@Override
	public int updateElevateResult(ElevateResult elevate) throws DaoException {
		return elevateDAO.updateElevate(elevate);
	}
	
	@Override
	public int updateElevateResultExpiryDate(ElevateResult elevate) throws DaoException {
		return elevateDAO.updateElevateExpiryDate(elevate);
	}

	@Override
	public int updateElevateResultComment(ElevateResult elevate) throws DaoException {
		return elevateDAO.updateElevateComment(elevate);
	}

	@Override
	public int appendElevateResultComment(ElevateResult elevate) throws DaoException {
		return elevateDAO.appendElevateComment(elevate);
	}

	@Override
	public int getElevateResultCount(SearchCriteria<ElevateResult> criteria) throws DaoException {
		SearchCriteria<ElevateResult> sc = new SearchCriteria<ElevateResult>(criteria.getModel(), criteria.getStartDate(), criteria.getEndDate(), null, null);
		RecordSet<ElevateResult> set = getElevateResultList(sc);
		return set.getTotalSize();
	}

	/* Exclude */
	@Override
	public int addExcludeResult(ExcludeResult exclude) throws DaoException {
		return excludeDAO.addExclude(exclude);
	}
	
	@Override
	public RecordSet<ExcludeResult> getExcludeResultList(SearchCriteria<ExcludeResult> criteria) throws DaoException {
		return excludeDAO.getExclude(criteria);
	}
	
	@Override
	public int deleteExcludeResult(ExcludeResult exclude) throws DaoException {
		return excludeDAO.removeExclude(exclude);
	}

	@Override
	public int clearExcludeResult(StoreKeyword keyword) throws DaoException {
		return excludeDAO.clearExclude(keyword);
	}
	
	@Override
	public ExcludeResult getExcludeItem(ExcludeResult exclude) throws DaoException {
		return excludeDAO.getExcludeItem(exclude);
	}

	@Override
	public int getExcludeResultCount(SearchCriteria<ExcludeResult> criteria) throws DaoException {
		SearchCriteria<ExcludeResult> sc = new SearchCriteria<ExcludeResult>(criteria.getModel(), criteria.getStartDate(), criteria.getEndDate(), null, null);
		RecordSet<ExcludeResult> set = getExcludeResultList(sc);
		return set.getTotalSize();
	}

	@Override
	public Product getExcludedProduct(String serverName, ExcludeResult exclude) throws DaoException {
		ExcludeResult e = getExcludeItem(exclude);
		StoreKeyword sk = exclude.getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
		Product ep = new Product();
		ep.setEdp(e.getEdp());
		ep.setExpiryDate(e.getExpiryDate());
		ep.setCreatedDate(e.getCreatedDate());
		ep.setLastModifiedDate(e.getLastModifiedDate());
		ep.setComment(e.getComment());
		ep.setLastModifiedBy(e.getLastModifiedBy());
		ep.setCreatedBy(e.getCreatedBy());
		ep.setStore(exclude.getStoreKeyword().getStoreId());
		map.put(e.getEdp(), ep);
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return map.get(e.getEdp());	}

	@Override
	public int appendExcludeResultComment(ExcludeResult exclude) throws DaoException {
		return excludeDAO.appendExcludeComment(exclude);
	}
	
	@Override
	public int updateExcludeResultComment(ExcludeResult exclude) throws DaoException {
		return excludeDAO.updateExcludeComment(exclude);
	}
	
	@Override
	public int updateExcludeResultExpiryDate(ExcludeResult exclude) throws DaoException {
		return excludeDAO.updateExcludeExpiryDate(exclude);
	}
	
	@Override
	public int addBanner(Banner banner) throws DaoException {
		return bannerDAO.addBanner(banner);
	}
	
	@Override
	public int updateBanner(Banner banner) throws DaoException {
		return bannerDAO.updateBanner(banner);
	}
	
	@Override
	public int deleteBanner(Banner banner) throws DaoException {
		return bannerDAO.deleteBanner(banner);
	}
	
	@Override
	public int updateBannerComment(Banner banner) throws DaoException {
		return bannerDAO.updateBannerComment(banner);
	}
	
	@Override
	public int appendBannerComment(Banner banner) throws DaoException {
		return bannerDAO.appendBannerComment(banner);
	}
	
	@Override
	public Banner getBanner(Banner banner) throws DaoException {
		return bannerDAO.getBanner(banner);
	}
	
	@Override
	public RecordSet<Banner> getBannerList(SearchCriteria<Banner> criteria) throws DaoException {
		return bannerDAO.getBanners(criteria);
	}
	
	@Override
	public RecordSet<Banner> getBannerListWithNameLike(SearchCriteria<Banner> criteria) throws DaoException {
		return bannerDAO.searchBanner(criteria, MatchType.LIKE_NAME);
	}
	
	@Override
	public RecordSet<Banner> getBannerListWithNameMatching(SearchCriteria<Banner> criteria) throws DaoException {
		return bannerDAO.searchBanner(criteria, MatchType.MATCH_NAME);
	}

	@Override
	public int addCampaign(Campaign campaign) throws DaoException {
		return campaignDAO.addCampaign(campaign);
	}

	@Override
	public int updateCampaign(Campaign campaign) throws DaoException {
		return campaignDAO.updateCampaign(campaign);
	}

	@Override
	public int deleteCampaign(Campaign campaign) throws DaoException {
		return campaignDAO.deleteCampaign(campaign);
	}

	@Override
	public int updateCampaignComment(Campaign campaign) throws DaoException {
		return campaignDAO.updateCampaignComment(campaign);
	}

	@Override
	public int appendCampaignComment(Campaign campaign) throws DaoException {
		return campaignDAO.appendCampaignComment(campaign);
	}

	@Override
	public Campaign getCampaign(Campaign campaign) throws DaoException {
		return campaignDAO.getCampaign(campaign);
	}

	@Override
	public RecordSet<Campaign> getCampaigns(SearchCriteria<Campaign> criteria) throws DaoException {
		return campaignDAO.getCampaigns(criteria);
	}

	@Override
	public RecordSet<Campaign> getCampaignsContainingName(SearchCriteria<Campaign> criteria) throws DaoException {
		return campaignDAO.searchCampaign(criteria, MatchType.LIKE_NAME);
	}

	@Override
	public RecordSet<Campaign> getCampaignsWithName(SearchCriteria<Campaign> criteria) throws DaoException {
		return campaignDAO.searchCampaign(criteria, MatchType.MATCH_NAME);
	}
	
	
	@Override
	public Banner addCampaignBanner(String campaignId, String bannerId, Date startDate, Date endDate, List<String> keywordList) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public void deleteCampaignBanner(String campaignId, String bannerId) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public Banner updateCampaignBanner(String campaignId, String bannerId, Date startDate, Date endDate, List<String> keywordList) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public RecordSet<Banner> getCampaignBannerList(String campaignId) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}


	@Override
	public Redirect addRedirect(String store) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public void deleteRedirect(String redirectId) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public Redirect getRedirect(String redirectId) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public RecordSet<Redirect> getRedirects(String store) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public Redirect updateRedirect(String redirectId, String xml) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public int updateRedirectMapping(String redirectId, List<String> keywords) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	/* Relevancy */
	@Override
	public int addOrUpdateRelevancy(Relevancy relevancy) throws DaoException {
		return relevancyDAO.saveRelevancy(relevancy);
	}
	
	@Override
	public int addRelevancy(Relevancy relevancy) throws DaoException {
		return relevancyDAO.addRelevancy(relevancy);
	}

	@Override
	public String addRelevancyAndGetId(Relevancy relevancy) throws DaoException {
		return relevancyDAO.addRelevancyAndGetId(relevancy);
	}
	
	@Override
	public int updateRelevancy(Relevancy relevancy) throws DaoException {
		return relevancyDAO.updateRelevancy(relevancy);
	}

	@Override
	public int deleteRelevancy(Relevancy relevancy) throws DaoException {
		return relevancyDAO.deleteRelevancy(relevancy);
	}
	
	@Override
	public int appendRelevancyComment(Relevancy relevancy) throws DaoException {
		return relevancyDAO.appendRelevancyComment(relevancy);
	}
	
	@Override
	public int updateRelevancyComment(Relevancy relevancy) throws DaoException {
		return relevancyDAO.updateRelevancyComment(relevancy);
	}
	
	@Override
	public Relevancy getRelevancy(Relevancy relevancy) throws DaoException {
		return relevancyDAO.getRelevancy(relevancy);
	}

	@Override
	public boolean addOrUpdateRelevancyDetails(Relevancy relevancy) throws DaoException {
		return relevancyDAO.saveRelevancyDetails(relevancy);
	}
	
	@Override
	public Relevancy getRelevancyDetails(Relevancy relevancy) throws DaoException {
		return relevancyDAO.getRelevancyDetails(relevancy);
	}
	
	@Override
	public RecordSet<Relevancy> searchRelevancy(SearchCriteria<Relevancy> criteria, MatchType relevancyMatchType) throws DaoException {
		return relevancyDAO.searchRelevancy(criteria, relevancyMatchType);
	}
	
	/* Relevancy Field */
	@Override
	public int addOrUpdateRelevancyField(RelevancyField relevancyField) throws DaoException {
		return relevancyDAO.saveRelevancyField(relevancyField);
	}
	
	@Override
	public int addRelevancyField(RelevancyField relevancyField) throws DaoException {
		return relevancyDAO.addRelevancyField(relevancyField);
	}

	@Override
	public int updateRelevancyField(RelevancyField relevancyField) throws DaoException {
		return relevancyDAO.updateRelevancyField(relevancyField);
	}
	
	@Override
	public int deleteRelevancyField(RelevancyField relevancyField) throws DaoException {
		return relevancyDAO.deleteRelevancyField(relevancyField);
	}
	
	@Override
	public RelevancyField getRelevancyField(RelevancyField relevancyField) throws DaoException {
		return relevancyDAO.getRelevancyField(relevancyField);
	}
	
	/* Relevancy Keyword */
	@Override
	public int addOrUpdateRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
		return relevancyDAO.saveRelevancyKeyword(relevancyKeyword);
	}
	
	@Override
	public int addRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
		return relevancyDAO.addRelevancyKeyword(relevancyKeyword);

	}

	@Override
	public int updateRelevancyKeyword(RelevancyKeyword relevancyKeyword)throws DaoException {
		return relevancyDAO.updateRelevancyKeyword(relevancyKeyword);
	}
	
	@Override
	public int deleteRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
		return relevancyDAO.deleteRelevancyKeyword(relevancyKeyword);
	}

	@Override
	public RelevancyKeyword getRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException {
		return relevancyDAO.getRelevancyKeyword(relevancyKeyword);
	}

	@Override
	public RecordSet<RelevancyKeyword> getRelevancyKeywords(Relevancy relevancy) throws DaoException {
		return relevancyDAO.getRelevancyKeywords(relevancy);
	}
	
	@Override
	public int getRelevancyKeywordCount(Relevancy relevancy) throws DaoException {
		if (relevancy == null || relevancy.getRelevancyId() == null) {
			return 0;
		}
		return relevancyDAO.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
				new RelevancyKeyword(null, new Relevancy(relevancy.getRelevancyId())), null, null, null, null),
				MatchType.MATCH_ID, ExactMatch.MATCH).getTotalSize();
	}

	@Override
	public int getRelevancyKeywordCount(StoreKeyword storeKeyword) throws DaoException {
		if (storeKeyword == null || StringUtils.isEmpty(storeKeyword.getStoreId()) || StringUtils.isEmpty(storeKeyword.getKeywordId())) {
			return 0;
		}
		return relevancyDAO.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
				new RelevancyKeyword(storeKeyword.getKeyword(), new Relevancy("", "")), null, null, null, null),
				MatchType.LIKE_NAME, ExactMatch.MATCH).getTotalSize();
	}
	
	@Override
	public RecordSet<RelevancyKeyword> searchRelevancyKeywords(SearchCriteria<RelevancyKeyword> criteria,
			MatchType relevancyMatchType, ExactMatch keywordExactMatch) throws DaoException {
		return relevancyDAO.searchRelevancyKeywords(criteria, relevancyMatchType, keywordExactMatch);
	}

	/* Store */
	@Override
	public Store addStore(String storeId, String storeName) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public Store getStore(String storeId) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public Store updateStore(String storeId, String oldStoreName, String newStoreName) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}
	
	@Override
	public void deleteStore(String storeId) throws DaoException {
		// TODO Auto-generated method stub
		throw new DaoException("Unsupported operation");
	}

	@Override
	public CategoryList getCategories(String categoryCode) throws DaoException {
		logger.error("CategoryDAO is null " + (categoryDAO==null));
		return categoryDAO.getCategories(categoryCode);
	}

	@Override
	public RecordSet<RedirectRule> getRedirectRule(String searchTerm, String ruleId, String storeId, Integer startRow, Integer endRow) throws DaoException {
		return redirectRuleDAO.getRedirectrule(searchTerm, ruleId, storeId, startRow, endRow);
	}

	@Override
	public int addRedirectRule(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.addRedirectRule(rule);
	}

	@Override
	public int updateRedirectRule(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.updateRedirectRule(rule);
	}

	@Override
	public int removeRedirectRule(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.deleteRedirectRule(rule);
	}

	@Override
	public RecordSet<RedirectRule> getRedirectRule(
			SearchCriteria<RedirectRule> searchCriteria) throws DaoException {
		return redirectRuleDAO.getRedirectrule(searchCriteria.getModel().getSearchTerm(),searchCriteria.getModel().getRuleId(), searchCriteria.getModel().getStoreId(), searchCriteria.getStartRow(), searchCriteria.getEndRow());
	}

	public KeywordDAO getKeywordDAO() {
		return keywordDAO;
	}

	public StoreKeywordDAO getStoreKeywordDAO() {
		return storeKeywordDAO;
	}

	public ElevateDAO getElevateDAO() {
		return elevateDAO;
	}

	public ExcludeDAO getExcludeDAO() {
		return excludeDAO;
	}

	public AuditTrailDAO getAuditTrailDAO() {
		return auditTrailDAO;
	}

	public BannerDAO getBannerDAO() {
		return bannerDAO;
	}

	public CampaignDAO getCampaignDAO() {
		return campaignDAO;
	}

	public RelevancyDAO getRelevancyDAO() {
		return relevancyDAO;
	}

	public CategoryDAO getCategoryDAO() {
		return categoryDAO;
	}

	public RedirectRuleDAO getRedirectRuleDAO() {
		return redirectRuleDAO;
	}

	public RuleStatusDAO getRuleStatusDAO() {
		return ruleStatusDAO;
	}

	@Override
	public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<AuditTrail> auditDetail) throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int addRuleStatus(RuleStatus rule) throws DaoException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateRuleStatus(RuleStatus rule) throws DaoException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeRuleStatus(RuleStatus rule) throws DaoException {
		// TODO Auto-generated method stub
		return 0;
	}
}
