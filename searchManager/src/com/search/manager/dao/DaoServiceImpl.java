package com.search.manager.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.file.DemoteVersionDAO;
import com.search.manager.dao.file.ElevateVersionDAO;
import com.search.manager.dao.file.ExcludeVersionDAO;
import com.search.manager.dao.file.FacetSortVersionDAO;
import com.search.manager.dao.file.RankingRuleVersionDAO;
import com.search.manager.dao.file.RedirectRuleVersionDAO;
import com.search.manager.dao.file.RuleVersionDAO;
import com.search.manager.dao.file.SpellRuleDAO;
import com.search.manager.dao.sp.AuditTrailDAO;
import com.search.manager.dao.sp.BannerDAO;
import com.search.manager.dao.sp.CampaignDAO;
import com.search.manager.dao.sp.CommentDAO;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.DemoteDAO;
import com.search.manager.dao.sp.ElevateDAO;
import com.search.manager.dao.sp.ExcludeDAO;
import com.search.manager.dao.sp.ExportRuleMapDAO;
import com.search.manager.dao.sp.FacetSortDAO;
import com.search.manager.dao.sp.GroupsDAO;
import com.search.manager.dao.sp.KeywordDAO;
import com.search.manager.dao.sp.RedirectRuleDAO;
import com.search.manager.dao.sp.RelevancyDAO;
import com.search.manager.dao.sp.RuleStatusDAO;
import com.search.manager.dao.sp.RuleStatusDAO.SortOrder;
import com.search.manager.dao.sp.StoreKeywordDAO;
import com.search.manager.dao.sp.UsersDAO;
import com.search.manager.enums.ExportRuleMapSortType;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.Comment;
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Group;
import com.search.manager.model.Keyword;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.User;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.manager.xml.file.RuleTransferUtil;
import com.search.ws.SearchHelper;

@Service("daoService")
public class DaoServiceImpl implements DaoService {

	@Autowired private KeywordDAO 		keywordDAO;
	@Autowired private StoreKeywordDAO 	storeKeywordDAO;
	@Autowired private ElevateDAO 		elevateDAO;
	@Autowired private ExcludeDAO 		excludeDAO;
	@Autowired private DemoteDAO 		demoteDAO;
	@Autowired private AuditTrailDAO 	auditTrailDAO;
	@Autowired private BannerDAO 		bannerDAO;
	@Autowired private CampaignDAO 		campaignDAO;
	@Autowired private RelevancyDAO		relevancyDAO;
	@Autowired private RedirectRuleDAO	redirectRuleDAO;
	@Autowired private RuleStatusDAO	ruleStatusDAO;
	@Autowired private CommentDAO		commentDAO;
	@Autowired private UsersDAO			usersDAO;
	@Autowired private GroupsDAO		groupsDAO;
	@Autowired private FacetSortDAO		facetSortDAO;
	@Autowired private ElevateVersionDAO elevateVersionDAO;
	@Autowired private ExcludeVersionDAO excludeVersionDAO;
	@Autowired private DemoteVersionDAO demoteVersionDAO;
	@Autowired private FacetSortVersionDAO facetSortVersionDAO;
	@Autowired private RedirectRuleVersionDAO queryCleaningVersionDAO;
	@Autowired private RankingRuleVersionDAO rankingRuleVersionDAO;
	@Autowired private ExportRuleMapDAO	exportRuleMapDAO;
    @Autowired private SpellRuleDAO spellRuleDAO;

	private DaoServiceImpl instance;
	private final static Logger logger = Logger.getLogger(DaoServiceImpl.class);
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
	
	public void setDemoteDAO(DemoteDAO demoteDAO) {
		this.demoteDAO = demoteDAO;
	}

	public void setFacetSortDAO(FacetSortDAO facetSortDAO) {
		this.facetSortDAO = facetSortDAO;
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

	public void setRedirectRuleDAO(RedirectRuleDAO redirectRuleDAO) {
		this.redirectRuleDAO = redirectRuleDAO;
	}

	public void setRuleStatusDAO(RuleStatusDAO ruleStatusDAO) {
		this.ruleStatusDAO = ruleStatusDAO;
	}

	public void setCommentDAO(CommentDAO commentDAO) {
		this.commentDAO = commentDAO;
	}

	public void setUsersDAO(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
	}

	public void setSpellRuleDAO(SpellRuleDAO spellRuleDAO) {
		this.spellRuleDAO = spellRuleDAO;
	}

	public void setGroupsDAO(GroupsDAO groupsDAO) {
		this.groupsDAO = groupsDAO;
	}

	/* Audit Trail */
    public RecordSet<AuditTrail> getAuditTrail(SearchCriteria<AuditTrail> auditDetail, boolean adminFlag) {
    	return auditTrailDAO.getAuditTrail(auditDetail, adminFlag);
    }
    
    public int addAuditTrail(AuditTrail auditTrail) {
    	return auditTrailDAO.addAuditTrail(auditTrail);
    }
    
	@Override
	public List<String> getDropdownValues(int type, String storeId, boolean adminFlag) throws DaoException {
		return auditTrailDAO.getDropdownValues(type, storeId, adminFlag);
	}
	
	public List<String> getRefIDs(String ent, String opt, String storeId) {
		return auditTrailDAO.getRefIDs(ent, opt, storeId);
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
			ElevateProduct ep = new ElevateProduct(e);
			ep.setStore(storeId);
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<ElevateProduct>(new ArrayList<ElevateProduct>(map.values()),set.getTotalSize());
	}
	
	/* retrieve data from both Solr and DB */
	public RecordSet<ElevateProduct> getElevatedProductsIgnoreKeyword(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException{
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
			ep.setMemberId(e.getMemberId());
			ep.setMemberTypeEntity(e.getElevateEntity());
			ep.setCondition(e.getCondition());
			ep.setForceAdd(e.isForceAdd());
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProductsIgnoreKeyword(map, storeId, serverName, keyword);
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
		ep.setCondition(e.getCondition());
		ep.setMemberTypeEntity(e.getElevateEntity());
		ep.setForceAdd(e.isForceAdd());
		if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
			map.put(UUID.randomUUID().toString(), ep);
		} else {
			map.put(e.getEdp(), ep);
		}
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
			ep.setCondition(e.getCondition());
			ep.setMemberTypeEntity(e.getElevateEntity());
			ep.setForceAdd(e.isForceAdd());
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<ElevateProduct>(new ArrayList<ElevateProduct>(map.values()),set.getTotalSize());
	}
	
	public RecordSet<DemoteProduct> getDemotedProducts(String serverName, SearchCriteria<DemoteResult> criteria) throws DaoException{
		RecordSet<DemoteResult> set = getDemoteResultList(criteria);
		LinkedHashMap<String, DemoteProduct> map = new LinkedHashMap<String, DemoteProduct>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (DemoteResult e: set.getList()) {
			DemoteProduct ep = new DemoteProduct();
			ep.setEdp(e.getEdp());
			ep.setLocation(e.getLocation());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			ep.setCondition(e.getCondition());
			ep.setMemberTypeEntity(e.getDemoteEntity());
			ep.setMemberId(e.getMemberId());
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<DemoteProduct>(new ArrayList<DemoteProduct>(map.values()),set.getTotalSize());
	}
	
	/* retrieve data from both Solr and DB */
	public RecordSet<DemoteProduct> getDemotedProductsIgnoreKeyword(String serverName, SearchCriteria<DemoteResult> criteria) throws DaoException{
		RecordSet<DemoteResult> set = getDemoteResultList(criteria);
		LinkedHashMap<String, DemoteProduct> map = new LinkedHashMap<String, DemoteProduct>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (DemoteResult e: set.getList()) {
			DemoteProduct ep = new DemoteProduct();
			ep.setEdp(e.getEdp());
			ep.setLocation(e.getLocation());
			ep.setExpiryDate(e.getExpiryDate());
			ep.setCreatedDate(e.getCreatedDate());
			ep.setLastModifiedDate(e.getLastModifiedDate());
			ep.setComment(e.getComment());
			ep.setLastModifiedBy(e.getLastModifiedBy());
			ep.setCreatedBy(e.getCreatedBy());
			ep.setStore(storeId);
			ep.setMemberId(e.getMemberId());
			ep.setMemberTypeEntity(e.getDemoteEntity());
			ep.setCondition(e.getCondition());
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProductsIgnoreKeyword(map, storeId, serverName, keyword);
		return new RecordSet<DemoteProduct>(new ArrayList<DemoteProduct>(map.values()),set.getTotalSize());
	}
	
	
	@Override
	public DemoteProduct getDemotedProduct(String serverName, DemoteResult demote) throws DaoException {
		DemoteResult e = getDemoteItem(demote);
		StoreKeyword sk = demote.getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		LinkedHashMap<String, DemoteProduct> map = new LinkedHashMap<String, DemoteProduct>();
		DemoteProduct dp = new DemoteProduct();
		dp.setEdp(e.getEdp());
		dp.setLocation(e.getLocation());
		dp.setExpiryDate(e.getExpiryDate());
		dp.setCreatedDate(e.getCreatedDate());
		dp.setLastModifiedDate(e.getLastModifiedDate());
		dp.setComment(e.getComment());
		dp.setLastModifiedBy(e.getLastModifiedBy());
		dp.setCreatedBy(e.getCreatedBy());
		dp.setStore(demote.getStoreKeyword().getStoreId());
		dp.setCondition(e.getCondition());
		dp.setMemberTypeEntity(e.getDemoteEntity());
		if (dp.getMemberTypeEntity() == MemberTypeEntity.FACET) {
			map.put(UUID.randomUUID().toString(), dp);
		} else {
			map.put(e.getEdp(), dp);
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return map.get(e.getEdp());
	}

	@Override
	public RecordSet<DemoteProduct> getNoExpiryDemotedProducts(String serverName, SearchCriteria<DemoteResult> criteria) throws DaoException {
		RecordSet<DemoteResult> set = getNoExpireDemoteResultList(criteria);
		LinkedHashMap<String, DemoteProduct> map = new LinkedHashMap<String, DemoteProduct>();
		StoreKeyword sk = criteria.getModel().getStoreKeyword();
		String storeId = DAOUtils.getStoreId(sk);
		String keyword = DAOUtils.getKeywordId(sk);
		for (DemoteResult e: set.getList()) {
			DemoteProduct dp = new DemoteProduct();
			dp.setEdp(e.getEdp());
			dp.setLocation(e.getLocation());
			dp.setExpiryDate(e.getExpiryDate());
			dp.setCreatedDate(e.getCreatedDate());
			dp.setLastModifiedDate(e.getLastModifiedDate());
			dp.setComment(e.getComment());
			dp.setLastModifiedBy(e.getLastModifiedBy());
			dp.setCreatedBy(e.getCreatedBy());
			dp.setStore(storeId);
			dp.setCondition(e.getCondition());
			dp.setMemberTypeEntity(e.getDemoteEntity());
			if (dp.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), dp);
			} else {
				map.put(e.getEdp(), dp);
			}
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<DemoteProduct>(new ArrayList<DemoteProduct>(map.values()),set.getTotalSize());
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
			ep.setCondition(e.getCondition());
			ep.setMemberTypeEntity(e.getExcludeEntity());
			ep.setMemberId(e.getMemberId());
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProducts(map, storeId, serverName, keyword);
		return new RecordSet<Product>(new ArrayList<Product>(map.values()),set.getTotalSize());
	}
	
	@Override
	public RecordSet<Product> getExcludedProductsIgnoreKeyword(String serverName, SearchCriteria<ExcludeResult> criteria) throws DaoException {
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
			ep.setMemberId(e.getMemberId());
			ep.setMemberTypeEntity(e.getExcludeEntity());
			ep.setCondition(e.getCondition());
			if (ep.getMemberTypeEntity() == MemberTypeEntity.FACET) {
				map.put(UUID.randomUUID().toString(), ep);
			} else {
				map.put(e.getEdp(), ep);
			}
		}
		SearchHelper.getProductsIgnoreKeyword(map, storeId, serverName, keyword);
		return new RecordSet<Product>(new ArrayList<Product>(map.values()),set.getTotalSize());
	}
	
	@Override
	public String getEdpByPartNumber(String serverName, String storeId, String keyword, String partNumber) {
		return SearchHelper.getEdpByPartNumber(serverName, storeId, partNumber);
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
	public List<Keyword> getAllKeywords(String storeId, RuleEntity ruleEntity) throws DaoException {
	    return storeKeywordDAO.getAllKeywords(storeId, ruleEntity);
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
	
	/* Demote */
	@Override
	public int addDemoteResult(DemoteResult demote) throws DaoException {
		return demoteDAO.add(demote);
	}

	
	@Override
	public RecordSet<DemoteResult> getDemoteResultList(SearchCriteria<DemoteResult> criteria) throws DaoException {
		RecordSet<DemoteResult> list = demoteDAO.getResultList(criteria);
		return list;
	}

	@Override
	public RecordSet<DemoteResult> getNoExpireDemoteResultList(SearchCriteria<DemoteResult> criteria) throws DaoException {
		return demoteDAO.getNoExpiry(criteria);
	}

	@Override
	public DemoteResult getDemoteItem(DemoteResult demote) throws DaoException {
		return demoteDAO.getItem(demote);
	}

	@Override
	public Map<String, DemoteResult> getDemoteResultMap(SearchCriteria<DemoteResult> criteria) throws DaoException {
		Map<String, DemoteResult> map = new LinkedHashMap<String, DemoteResult>();
		RecordSet<DemoteResult> set = demoteDAO.getResultList(criteria);
		for (DemoteResult e: set.getList()) {
			map.put(e.getEdp(), e);
		}
		return map;
	}

	@Override
	public int deleteDemoteResult(DemoteResult elevate) throws DaoException {
		return demoteDAO.remove(elevate);
	}

	@Override
	public int clearDemoteResult(StoreKeyword keyword) throws DaoException {
		return demoteDAO.clear(keyword);
	}
	
	@Override
	public int updateDemoteResult(DemoteResult demote) throws DaoException {
		return demoteDAO.update(demote);
	}
	
	@Override
	public int updateDemoteResultExpiryDate(DemoteResult demote) throws DaoException {
		return demoteDAO.updateExpiryDate(demote);
	}

	@Override
	public int updateDemoteResultComment(DemoteResult demote) throws DaoException {
		return demoteDAO.updateComment(demote);
	}

	@Override
	public int appendDemoteResultComment(DemoteResult demote) throws DaoException {
		return demoteDAO.appendComment(demote);
	}
	
	@Override
	public int getDemoteResultCount(SearchCriteria<DemoteResult> criteria) throws DaoException {
		SearchCriteria<DemoteResult> sc = new SearchCriteria<DemoteResult>(criteria.getModel(), criteria.getStartDate(), criteria.getEndDate(), null, null);
		RecordSet<DemoteResult> set = getDemoteResultList(sc);
		return set.getTotalSize();
	}

	/* Exclude */
	@Override
	public int addExcludeResult(ExcludeResult exclude) throws DaoException {
		return excludeDAO.addExclude(exclude);
	}
	
	@Override
	public int updateExcludeResult(ExcludeResult exclude) throws DaoException {
		return excludeDAO.updateExclude(exclude);
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

	/* Relevancy */
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
				new RelevancyKeyword(new Keyword(""), relevancy), null, null, null, null),
				MatchType.MATCH_ID, ExactMatch.SIMILAR).getTotalSize();
	}

	@Override
	public int getRelevancyKeywordCount(StoreKeyword storeKeyword) throws DaoException {
		if (storeKeyword == null || StringUtils.isEmpty(storeKeyword.getStoreId()) || StringUtils.isEmpty(storeKeyword.getKeywordId())) {
			return 0;
		}
		Relevancy relevancy = new Relevancy("", "");
		relevancy.setStore(storeKeyword.getStore());
		return relevancyDAO.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
				new RelevancyKeyword(storeKeyword.getKeyword(), relevancy), null, null, null, null),
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
	public int addRedirectRule(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.addRedirectRule(rule);
	}

	@Override
	public String addRedirectRuleAndGetId(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.addRedirectRuleAndGetId(rule);
	}
	
	@Override
	public int updateRedirectRule(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.updateRedirectRule(rule);
	}

	@Override
	public int deleteRedirectRule(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.deleteRedirectRule(rule);
	}

	@Override
	public RedirectRule getRedirectRule(RedirectRule redirectRule) throws DaoException {
		return redirectRuleDAO.getRedirectRule(redirectRule);
	}

	@Override
	public RecordSet<RedirectRule> getRedirectRules(SearchCriteria<RedirectRule> searchCriteria) throws DaoException {
		return redirectRuleDAO.getRedirectRules(searchCriteria);
	}

	@Override
	public int addRedirectKeyword(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.addRedirectKeyword(rule);
	}

	@Override
	public int deleteRedirectKeyword(RedirectRule rule) throws DaoException {
		return redirectRuleDAO.removeRedirectKeyword(rule);
	}

	@Override
	public int addRedirectCondition(RedirectRuleCondition rule) throws DaoException {
		return redirectRuleDAO.addRedirectCondition(rule);
	}

	@Override
	public int updateRedirectCondition(RedirectRuleCondition rule) throws DaoException {
		return redirectRuleDAO.updateRedirectCondition(rule);
	}
	
	@Override
	public int deleteRedirectCondition(RedirectRuleCondition rule) throws DaoException {
		return redirectRuleDAO.removeRedirectCondition(rule);
	}

	@Override
	public RecordSet<StoreKeyword> getRedirectKeywords(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType, ExactMatch keywordExactMatch) throws DaoException {
		return redirectRuleDAO.getRedirectKeywords(criteria, redirectMatchType, keywordExactMatch);
	}

	@Override
	public RecordSet<RedirectRuleCondition> getRedirectConditions(SearchCriteria<RedirectRule> criteria) throws DaoException {
		return redirectRuleDAO.getRedirectConditions(criteria);
	}
	
	@Override
	public RecordSet<RedirectRule> getRedirectForKeywords(SearchCriteria<StoreKeyword> criteria) throws DaoException {
		return redirectRuleDAO.getRedirectForKeywords(criteria);
	}
	
	@Override
	public RecordSet<RedirectRule> searchRedirectRule(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType) throws DaoException {
		return redirectRuleDAO.searchRedirectRules(criteria, redirectMatchType);
	}
	
	@Override
	public RecordSet<RedirectRule> searchRedirectRuleKeyword(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType,
			ExactMatch keywordExactMatch) throws DaoException {
		return redirectRuleDAO.searchRedirectRuleKeywords(criteria, redirectMatchType, keywordExactMatch);
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

	public DemoteDAO getDemoteDAO() {
		return demoteDAO;
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

	public RedirectRuleDAO getRedirectRuleDAO() {
		return redirectRuleDAO;
	}

	public RuleStatusDAO getRuleStatusDAO() {
		return ruleStatusDAO;
	}

	public CommentDAO getCommentDAO() {
		return commentDAO;
	}

	public UsersDAO getUsersDAO() {
		return usersDAO;
	}

	public GroupsDAO getGroupsDAO() {
		return groupsDAO;
	}

	@Override
	public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria) throws DaoException {
		return ruleStatusDAO.getRuleStatus(searchCriteria, null);
	}
	
	@Override
	public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria, SortOrder sortOrder) throws DaoException {
		return ruleStatusDAO.getRuleStatus(searchCriteria, sortOrder);
	}

	@Override
	public int addRuleStatus(RuleStatus ruleStatus) throws DaoException {
		return ruleStatusDAO.addRuleStatus(ruleStatus);
	}

	@Override
	public int updateRuleStatus(RuleStatus ruleStatus) throws DaoException {
		return ruleStatusDAO.updateRuleStatus(ruleStatus);
	}

	@Override
	public int removeRuleStatus(RuleStatus ruleStatus) throws DaoException {
		// TODO do we delete???
		return ruleStatusDAO.deleteRuleStatus(ruleStatus);
	}

	private Map<String,Boolean> approveRuleStatusList(RuleStatusEntity status, List<RuleStatus> ruleStatusList, String requestBy, Date requestDate) 
			throws DaoException {	
		Map<String,Boolean> statusMap = new HashMap<String,Boolean>();
		for (RuleStatus ruleStatus : ruleStatusList) {
			statusMap.put(ruleStatus.getRuleRefId(), updateRuleStatusApprovalInfo(ruleStatus, status, requestBy, requestDate) > 0 ? true : false);
		}
		return statusMap;
	}

	private Map<String,Boolean> publishRuleStatusList(RuleStatusEntity status, List<RuleStatus> ruleStatusList, String requestBy, Date requestDate) 
			throws DaoException {	
		Map<String,Boolean> statusMap = new HashMap<String,Boolean>();
		for (RuleStatus ruleStatus : ruleStatusList) {
			statusMap.put(ruleStatus.getRuleRefId(), updateRuleStatusPublishInfo(ruleStatus, status, requestBy, requestDate) > 0 ? true : false);
		}
		return statusMap;
	}

	@Override
	public Map<String,Boolean> updateRuleStatus(RuleStatusEntity status, List<RuleStatus> ruleStatusList, String requestBy, Date requestDate) 
			throws DaoException {	
		switch (status) {
			case PUBLISHED:
			case UNPUBLISHED:
				return publishRuleStatusList(status, ruleStatusList, requestBy, requestDate);
			case APPROVED:
			case REJECTED:
				return approveRuleStatusList(status, ruleStatusList, requestBy, requestDate);
		}
		return new HashMap<String,Boolean>();
	}

	@Override
	public RuleStatus getRuleStatus(RuleStatus ruleStatus) throws DaoException {
		return ruleStatusDAO.getRuleStatus(ruleStatus);
	}

	@Override
	public List<String> getCleanList(List<String> ruleRefIds, Integer ruleTypeId, String pStatus, String aStatus) throws DaoException {
		return ruleStatusDAO.getCleanList(ruleRefIds, ruleTypeId, pStatus, aStatus);
	}

	@Override
	public RecordSet<Comment> getComment(SearchCriteria<Comment> searchCriteria) throws DaoException {
		return commentDAO.getComment(searchCriteria);
	}

	@Override
	public int addComment(Comment comment) throws DaoException {
		return commentDAO.addComment(comment);
	}

	@Override
	public int updateComment(Comment comment) throws DaoException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeComment(Integer commentId) throws DaoException {
		return commentDAO.deleteComment(commentId);
	}

	@Override
	public RecordSet<User> getUsers(SearchCriteria<User> searchCriteria, MatchType matchTypeName) throws DaoException {
		return usersDAO.getUsers(searchCriteria, matchTypeName);
	}

	@Override
	public User getUser(String username) throws DaoException {
		User user = new User();
		user.setUsername(username);
		SearchCriteria<User> criteria = new SearchCriteria<User>(user,null,null,0,0);
		RecordSet<User> users = getUsers(criteria, MatchType.MATCH_ID);
		return users.getTotalSize()>0 ? users.getList().get(0) : null;
	}

	@Override
	public int addUser(User user) throws DaoException {
		return usersDAO.addUser(user);
	}

	@Override
	public int updateUser(User user) throws DaoException {
		return usersDAO.updateUser(user);
	}

	public int resetPassword(User user) throws DaoException {
		return usersDAO.resetPassword(user);
	}
	
	public int login(User user) throws DaoException {
		return usersDAO.login(user);
	}
	
	
	@Override
	public int removeUser(User user) throws DaoException {
		return usersDAO.deleteUser(user);
	}

	@Override
	public List<String> getAllPermissions() throws DaoException {
		return groupsDAO.getAllPermissions();
	}

	@Override
	public List<String> getGroups() throws DaoException {
		return groupsDAO.getGroups();
	}

	@Override
	public RecordSet<Group> getGroupPermission(String groupId) throws DaoException {
		return groupsDAO.getGroupPermission(groupId);
	}

	/* Facet Sort */
	@Override
	public int addFacetSort(FacetSort facetSort) throws DaoException {
		return facetSortDAO.addFacetSort(facetSort);
	}

	@Override
	public int deleteFacetSort(FacetSort facetSort) throws DaoException {
		return facetSortDAO.deleteFacetSort(facetSort);
	}

	@Override
	public int updateFacetSort(FacetSort facetSort) throws DaoException {
		return facetSortDAO.updateFacetSort(facetSort);
	}

	@Override
	public FacetSort getFacetSort(FacetSort facetSort) throws DaoException {
		return facetSortDAO.searchFacetSort(facetSort);
	}
	
	@Override
	public RecordSet<FacetSort> searchFacetSort(
			SearchCriteria<FacetSort> criteria, MatchType matchType)
			throws DaoException {
		return facetSortDAO.searchFacetSort(criteria, matchType);
	}

	@Override
	public int addFacetGroup(FacetGroup facetGroup) throws DaoException {
		return facetSortDAO.addFacetGroup(facetGroup);
	}

	@Override
	public int deleteFacetGroup(FacetGroup facetGroup) throws DaoException {
		return facetSortDAO.deleteFacetGroup(facetGroup);
	}

	@Override
	public int updateFacetGroup(FacetGroup facetGroup) throws DaoException {
		return facetSortDAO.updateFacetGroup(facetGroup);
	}

	@Override
	public RecordSet<FacetGroup> searchFacetGroup(
			SearchCriteria<FacetGroup> criteria, MatchType matchType)
			throws DaoException {
		return facetSortDAO.searchFacetGroup(criteria, matchType);
	}

	@Override
	public int addFacetGroupItem(FacetGroupItem facetGroupItem)
			throws DaoException {
		return facetSortDAO.addFacetGroupItem(facetGroupItem);
	}

	@Override
	public int deleteFacetGroupItem(FacetGroupItem facetGroupItem)
			throws DaoException {
		return facetSortDAO.deleteFacetGroupItem(facetGroupItem);
	}

	@Override
	public int updateFacetGroupItem(FacetGroupItem facetGroupItem)
			throws DaoException {
		return facetSortDAO.updateFacetGroupItem(facetGroupItem);
	}

	@Override
	public int clearFacetGroupItem(FacetGroup facetGroup)
			throws DaoException {
		return facetSortDAO.clearFacetGroupItem(facetGroup);
	}

	@Override
	public RecordSet<FacetGroupItem> searchFacetGroupItem(
			SearchCriteria<FacetGroupItem> criteria, MatchType matchType)
			throws DaoException {
		return facetSortDAO.searchFacetGroupItem(criteria, matchType);
	}

	@Override
	public String addFacetSortAndGetId(FacetSort facetSort) throws DaoException {
		return facetSortDAO.addFacetSortAndGetId(facetSort);
	}

	@Override
	public int addFacetGroupItems(List<FacetGroupItem> facetGroupItems) throws DaoException {
		return facetSortDAO.addFacetGroupItems(facetGroupItems);
	}

	private RuleVersionDAO<?> getRuleVersionDAO(RuleEntity ruleEntity) {
		switch (ruleEntity) {
		case ELEVATE:
			return elevateVersionDAO;
		case EXCLUDE:
			return excludeVersionDAO;
		case DEMOTE:
			return demoteVersionDAO;
		case FACET_SORT:
			return facetSortVersionDAO;
		case QUERY_CLEANING:
			return queryCleaningVersionDAO;
		case RANKING_RULE:
			return rankingRuleVersionDAO;
		}
		return null;
	}
	
	private RuleVersionDAO<?> getRuleVersionDAO(RuleXml xml) {
		// TODO: convert to map
		if (xml instanceof ElevateRuleXml) {
			return elevateVersionDAO;
		}
		else if (xml instanceof ExcludeRuleXml) {
			return excludeVersionDAO;
		}
		else if (xml instanceof DemoteRuleXml) {
			return demoteVersionDAO;
		}
		else if (xml instanceof FacetSortRuleXml) {
			return facetSortVersionDAO;
		}
		else if (xml instanceof RedirectRuleXml) {
			return queryCleaningVersionDAO;
		}
		else if (xml instanceof RankingRuleXml) {
			return rankingRuleVersionDAO;
		}
		return null;
	}
	
	@Override
	public boolean createPublishedVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String notes) {
		RuleVersionDAO<?> dao = getRuleVersionDAO(ruleEntity);
		if (dao != null) {
			return dao.createPublishedRuleVersion(store, ruleId, username, name, notes);
		}
		return false;
	}

	@Override
	public List<RuleXml> getPublishedRuleVersions(String store, String ruleType, String ruleId) {
		RuleVersionDAO<?> dao = getRuleVersionDAO(RuleEntity.find(ruleType));
		if (dao != null) {
			return dao.getPublishedRuleVersions(store, ruleId);
		}
		return new ArrayList<RuleXml>();
	}
	
	@Override
	public boolean createRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String reason){
		RuleVersionDAO<?> dao = getRuleVersionDAO(ruleEntity);
		if (dao != null) {
			return dao.createRuleVersion(store, ruleId, username, name, reason);
		}
		return false;
	}

	@Override
	public boolean deleteRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, int version){
		RuleVersionDAO<?> dao = getRuleVersionDAO(ruleEntity);
		if (dao != null) {
			return dao.deleteRuleVersion(store, ruleId, username, version);
		}
		return false;
	}

	@Override
	public List<RuleXml> getRuleVersions(String store, String ruleType, String ruleId) {
		RuleVersionDAO<?> dao = getRuleVersionDAO(RuleEntity.find(ruleType));
		if (dao != null) {
			return dao.getRuleVersions(store, ruleId);
		}
		return new ArrayList<RuleXml>();
	}

	@Override
	public int getRuleVersionsCount(String store, String ruleType, String ruleId) {
		RuleVersionDAO<?> dao = getRuleVersionDAO(RuleEntity.find(ruleType));
		if (dao != null) {
			return dao.getRuleVersionsCount(store, ruleId);
		}
		return 0;
	}
	
	@Override
	public boolean restoreRuleVersion(RuleXml xml) {
		RuleVersionDAO<?> dao = getRuleVersionDAO(xml);
		if (dao != null) {
			return dao.restoreRuleVersion(xml);
		}
		return false;
	}

	@Override
	public RecordSet<ExportRuleMap> getExportRuleMap(SearchCriteria<ExportRuleMap> exportRuleMap, ExportRuleMapSortType sortType) throws DaoException {
		return exportRuleMapDAO.getExportRuleMap(exportRuleMap, sortType);
	}

	@Override
	public int saveExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException {
		ExportRuleMap searchExportRuleMap = new ExportRuleMap(exportRuleMap.getStoreIdOrigin(), exportRuleMap.getRuleIdOrigin(), null,  
				exportRuleMap.getStoreIdTarget(), null, null, exportRuleMap.getRuleType());
		List<ExportRuleMap> rtList = getExportRuleMap(new SearchCriteria<ExportRuleMap>(searchExportRuleMap), null).getList();
		if (CollectionUtils.isEmpty(rtList)) {
			exportRuleMap.setRejected(false);
			exportRuleMapDAO.addExportRuleMap(searchExportRuleMap);
		}
		return exportRuleMapDAO.updateExportRuleMap(exportRuleMap);
	}
	
	@Override
	public int deleteExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException {
		return exportRuleMapDAO.deleteExportRuleMap(exportRuleMap);
	}

	private RuleStatus getRuleStatusPK(RuleStatus ruleStatus) {
		RuleStatus updateRuleStatus = new RuleStatus();
		if (ruleStatus != null) {
			updateRuleStatus.setStoreId(ruleStatus.getStoreId());
			updateRuleStatus.setRuleTypeId(ruleStatus.getRuleTypeId());
			updateRuleStatus.setRuleRefId(ruleStatus.getRuleRefId());
			updateRuleStatus.setRuleStatusId(ruleStatus.getRuleStatusId());
			updateRuleStatus.setDescription(ruleStatus.getDescription());
		}
		return updateRuleStatus;
	}
	
	@Override
	public int updateRuleStatusExportInfo(RuleStatus ruleStatus, String exportBy, ExportType exportType, Date exportDate) throws DaoException {
		if (ruleStatus != null) {
			RuleStatus updateRuleStatus  = getRuleStatusPK(ruleStatus);
			updateRuleStatus.setExportBy(exportBy);
			updateRuleStatus.setExportType(exportType);
			updateRuleStatus.setLastModifiedBy(exportBy);
			updateRuleStatus.setLastExportDate(exportDate);
			updateRuleStatus.setLastModifiedDate(exportDate);
			return updateRuleStatus(updateRuleStatus);
		}
		return -1;
	}

	@Override
	public int updateRuleStatusPublishInfo(RuleStatus ruleStatus, RuleStatusEntity requestedPublishStatus, 
			String requestBy, Date requestDate) throws DaoException {
		RuleStatus updateRuleStatus  = getRuleStatusPK(ruleStatus);
		updateRuleStatus.setApprovalStatus("");
		updateRuleStatus.setPublishedStatus(String.valueOf(requestedPublishStatus));
		updateRuleStatus.setPublishedBy(requestBy);
		updateRuleStatus.setLastModifiedBy(requestBy);
		updateRuleStatus.setLastPublishedDate(requestDate);
		updateRuleStatus.setLastModifiedDate(requestDate);
		return updateRuleStatus(updateRuleStatus);
	}

	@Override
	public int updateRuleStatusApprovalInfo(RuleStatus ruleStatus, RuleStatusEntity requestedApprovalStatus, 
			String requestBy, Date requestDate) throws DaoException {
		int result = -1;
		if (requestedApprovalStatus != null) {
			RuleStatus updateRuleStatus  = getRuleStatusPK(ruleStatus);
			updateRuleStatus.setApprovalStatus(String.valueOf(requestedApprovalStatus));
			updateRuleStatus.setLastModifiedDate(requestDate);
			switch(requestedApprovalStatus) {
				case APPROVED:
				case REJECTED:
					updateRuleStatus.setApprovalBy(requestBy);
					updateRuleStatus.setLastApprovalDate(requestDate);
					break;
				case PENDING:
					updateRuleStatus.setRequestBy(requestBy);
					updateRuleStatus.setLastRequestDate(requestDate);
					break;
				default:
					return result;
			}
			
			RecordSet<RuleStatus> rSet = getRuleStatus(new SearchCriteria<RuleStatus>(
					new RuleStatus(ruleStatus.getRuleTypeId(), ruleStatus.getStoreId(), ruleStatus.getRuleRefId()), null, null, 1, 1));
			if (rSet != null && CollectionUtils.isNotEmpty(rSet.getList())) {
				// existing rule
				RuleStatus existingRuleStatus = rSet.getList().get(0);
				// if rule is not for deletion do not change update status.
				if (!StringUtils.equalsIgnoreCase(existingRuleStatus.getUpdateStatus(), String.valueOf(RuleStatusEntity.DELETE)) && 
						(StringUtils.isBlank(existingRuleStatus.getUpdateStatus()) || 
						 StringUtils.equalsIgnoreCase(existingRuleStatus.getPublishedStatus(), String.valueOf(RuleStatusEntity.PUBLISHED)))) {
					updateRuleStatus.setUpdateStatus(String.valueOf(RuleStatusEntity.UPDATE));
				}
				updateRuleStatus.setLastModifiedBy(requestBy);
				result = updateRuleStatus(updateRuleStatus);
			} 
			else {
				// new rule
				updateRuleStatus.setUpdateStatus(RuleStatusEntity.ADD.toString());
				updateRuleStatus.setPublishedStatus(RuleStatusEntity.UNPUBLISHED.toString());
				updateRuleStatus.setCreatedBy(requestBy);
				updateRuleStatus.setCreatedDate(requestDate);
				result = addRuleStatus(updateRuleStatus);
			}
		}
		return result;
	}

	@Override
	public int updateRuleStatusDeletedInfo(RuleStatus ruleStatus, String deletedBy) throws DaoException {
		int result = -1;
		RecordSet<RuleStatus> rSet = getRuleStatus(new SearchCriteria<RuleStatus>(
				new RuleStatus(ruleStatus.getRuleTypeId(), ruleStatus.getStoreId(), ruleStatus.getRuleRefId()), null, null, 1, 1));
		if (rSet != null && CollectionUtils.isNotEmpty(rSet.getList())) {
			RuleStatus updateRuleStatus  = getRuleStatusPK(ruleStatus);
			updateRuleStatus.setApprovalStatus(StringUtils.equalsIgnoreCase(rSet.getList().get(0).getPublishedStatus(), 
					String.valueOf(RuleStatusEntity.UNPUBLISHED)) ? "" : String.valueOf(RuleStatusEntity.PENDING));
			updateRuleStatus.setUpdateStatus(RuleStatusEntity.DELETE.toString());
			updateRuleStatus.setLastModifiedBy(deletedBy);
			result = updateRuleStatus(updateRuleStatus);
		} 
		return result;
	}

	@Override
	public Map<String, Integer> addRuleStatusComment(RuleStatusEntity ruleStatus, String store, String username, String pComment, String ...ruleStatusId) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		String formatString = "%s";
		if (ruleStatus != null) {
			switch (ruleStatus) {
				case APPROVED:
					formatString = "[APPROVED] %s";
					break;
				case REJECTED:
					formatString = "[REJECTED] %s";
					break;
				case PUBLISHED:
					formatString = "[PUBLISHED] %s";
					break;
				case UNPUBLISHED:
					formatString = "[UNPUBLISHED] %s";
					break;
				case PENDING:
					formatString = "[REQUEST] %s";
					break;
				case IMPORTED:
					formatString = "[IMPORTED] %s";
					break;
				case EXPORTED:
					formatString = "[EXPORTED] %s";
					break;
				default:
					break;
			}
		}
		
		Comment comment = new Comment();
		comment.setRuleTypeId(RuleEntity.RULE_STATUS.getCode());
		comment.setUsername(username);
		comment.setComment(String.format(formatString, pComment));
		comment.setStore(new Store(store));
		for(String rsId: ruleStatusId){
			comment.setReferenceId(rsId);
			int result = -1;
			try {
				result = addComment(comment);
			} catch (DaoException e) {
				logger.error("Failed during addComment()", e);
			}
			resultMap.put(rsId, result);
		}
		return resultMap;
	}

	public boolean exportRule(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, ExportType exportType, String username, String comment) throws DaoException {
		// TODO: change return type to Map
		boolean exported = false;
		boolean exportedOnce = false;
		
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setEntity(String.valueOf(AuditTrailConstants.Entity.ruleStatus));
		auditTrail.setOperation(String.valueOf(AuditTrailConstants.Operation.exportRule));
		auditTrail.setUsername(username);
		auditTrail.setStoreId(store);
		Date exportDate = new Date();
		
		RuleStatus ruleStatus = null;
		try {
			SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(
					new RuleStatus(ruleEntity.getCode(), store, ruleId), null, null, null, null);
			RecordSet<RuleStatus> approvedRset = getRuleStatus(searchCriteria);
			if (approvedRset != null && CollectionUtils.isNotEmpty(approvedRset.getList())) {
				ruleStatus = approvedRset.getList().get(0);
			}
			else {
				logger.error("No rule status found for " + ruleEntity + " : "  + ruleId);
			}
		} catch (DaoException e) {
			logger.error("Failed to retrieve rule status for " + ruleEntity + " : "  + ruleId, e);
		}
		
		for(String targetStore: UtilityService.getStoresToExport(store)) {
			exported = RuleTransferUtil.exportRule(targetStore, ruleEntity, ruleId, rule);
			ExportRuleMap exportRuleMap = new ExportRuleMap(store, ruleId, rule.getRuleName(),  
					targetStore, null, null, ruleEntity);
			exportRuleMap.setExportDate(exportDate);
			exportRuleMap.setDeleted(false);
			if (ruleStatus != null) {
				exportRuleMap.setPublishedDate(ruleStatus.getLastPublishedDate());
			}
			saveExportRuleMap(exportRuleMap);
			exportedOnce |= exported;
			if(!exported) {
				logger.error("Failed to export " + ruleEntity + " : " + ruleId + " to store " + targetStore);
			}
		}
		
		if (exportedOnce) {
			try {
				if (ruleStatus != null) {
					// RULE STATUS
					updateRuleStatusExportInfo(ruleStatus, username, exportType, exportDate);
					// AUDIT TRAIL
					auditTrail.setDate(exportDate);
					auditTrail.setReferenceId(ruleStatus.getRuleRefId());
					if (ruleEntity == RuleEntity.ELEVATE || ruleEntity == RuleEntity.EXCLUDE || ruleEntity == RuleEntity.DEMOTE) {
						auditTrail.setKeyword(ruleStatus.getRuleRefId());
					}
					auditTrail.setDetails(String.format("Exported reference id = [%1$s], rule type = [%2$s], export type = [%3$s].", 
							auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId()), ExportType.AUTOMATIC));
					addAuditTrail(auditTrail);
					// COMMENT
					addRuleStatusComment(RuleStatusEntity.EXPORTED, store, username, comment, ruleStatus.getRuleStatusId());
				}
				else {
					logger.error("No rule status found for " + ruleEntity + " : "  + ruleId);
				}
			} catch (DaoException e) {
				logger.error("Failed to update rule status for " + ruleEntity + " : "  + ruleId, e);
			}
		}
		return exported;
	}

	/* Used by SearchServlet */
	
	@Override
	public RedirectRule getRedirectRule(StoreKeyword storeKeyword) throws DaoException {
		return getRedirectRule(new RedirectRule(storeKeyword.getStoreId(), storeKeyword.getKeywordId()));
	}

	@Override
	public Relevancy getRelevancyRule(StoreKeyword storeKeyword) throws DaoException {
		Relevancy relevancy = new Relevancy("", "");
		relevancy.setStore(storeKeyword.getStore());
		RecordSet<RelevancyKeyword>relevancyKeywords = searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
				new RelevancyKeyword(storeKeyword.getKeyword(), relevancy), new Date(), new Date(), 0, 0),
				MatchType.LIKE_NAME, ExactMatch.MATCH);
		return (relevancyKeywords.getTotalSize() > 0) ? getRelevancyRule(relevancy.getStore(), 
				relevancyKeywords.getList().get(0).getRelevancy().getRelevancyId()): null;
	}

	@Override
	public Relevancy getRelevancyRule(Store store, String relevancyId) throws DaoException {
		return getRelevancyDetails(new Relevancy(relevancyId));
	}

	@Override
	public FacetSort getFacetSortRule(StoreKeyword storeKeyword) throws DaoException {
		return getFacetSort(new FacetSort(storeKeyword.getKeywordTerm(), RuleType.KEYWORD, null, storeKeyword.getStore()));
	}

	@Override
	public FacetSort getFacetSortRule(Store store, String templateName) throws DaoException {
		return getFacetSort(new FacetSort(templateName, RuleType.TEMPLATE, null, store));
	}

	@Override
	public List<ElevateResult> getElevateRules(StoreKeyword storeKeyword) throws DaoException {
		return getElevateResultList(new SearchCriteria<ElevateResult>(
				new ElevateResult(storeKeyword), new Date(), null, 0, 0)).getList();
	}

	@Override
	public List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword) throws DaoException {
		return getElevateResultList(new SearchCriteria<ElevateResult>(
				new ElevateResult(storeKeyword), null, DateAndTimeUtils.getDateYesterday(), 0, 0)).getList();
	}

	@Override
	public List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword) throws DaoException {
		return getExcludeResultList(new SearchCriteria<ExcludeResult>(
				new ExcludeResult(storeKeyword), new Date(), null, 0, 0)).getList();
	}

	@Override
	public List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword) throws DaoException {
		return getExcludeResultList(new SearchCriteria<ExcludeResult>(
				new ExcludeResult(storeKeyword), null, DateAndTimeUtils.getDateYesterday(), 0, 0)).getList();
	}

	@Override
	public List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword) throws DaoException {
		return getDemoteResultList(new SearchCriteria<DemoteResult>(
				new DemoteResult(storeKeyword), new Date(), null, 0, 0)).getList();
	}

	@Override
	public List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword) throws DaoException {
		return getDemoteResultList(new SearchCriteria<DemoteResult>(
				new DemoteResult(storeKeyword), null, DateAndTimeUtils.getDateYesterday(), 0, 0)).getList();
	}

	@Override
    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria) throws DaoException {
	   return spellRuleDAO.getSpellRule(criteria);
    }
    
	@Override
	public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria, List<String> statusList) throws DaoException {
    	return spellRuleDAO.getSpellRule(criteria, statusList);
    }

	@Override
	public SpellRule getSpellRuleForSearchTerm(String store, String searchTerm) {
    	return spellRuleDAO.getSpellRuleForSearchTerm(store, searchTerm);
    }

	@Override    
	public int addSpellRule(SpellRule rule) throws DaoException {
    	return spellRuleDAO.addSpellRule(rule);
    }

	@Override
	public int updateSpellRule(SpellRule rule) throws DaoException {
    	return spellRuleDAO.updateSpellRule(rule);
    }

	@Override
	public int deleteSpellRule(SpellRule rule) throws DaoException {
    	return spellRuleDAO.deleteSpellRule(rule);
    }

	@Override
    public boolean isDuplicateSearchTerm(String storeId, String searchTerm, String ruleId) throws DaoException {
        return spellRuleDAO.isDuplicateSearchTerm(storeId, searchTerm, ruleId);
    }

	@Override
    public Integer getMaxSuggest(String storeId) throws DaoException {
    	return spellRuleDAO.getMaxSuggest(storeId);
    }

	@Override 
    public List<SpellRule> getActiveSpellRules(String storeId) {
		return spellRuleDAO.getActiveRules(storeId);
    }

	@Override
	public SpellRules getSpellRules(String storeId) throws DaoException {
		return spellRuleDAO.getSpellRules(storeId);
	}
	
}
