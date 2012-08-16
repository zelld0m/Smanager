package com.search.manager.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.search.manager.model.AuditTrail;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.CategoryList;
import com.search.manager.model.Comment;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Group;
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
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.User;

public interface DaoService {
	
	/* Store */
	public Store addStore(String storeId, String storeName) throws DaoException;
	public Store getStore(String storeId) throws DaoException;
	public Store updateStore(String storeId, String oldStoreName, String newStoreName) throws DaoException;
	public void deleteStore(String storeId) throws DaoException;
	
	/* Keywords */
	public int addKeyword(StoreKeyword storeKeyword) throws DaoException;
	public StoreKeyword getKeyword(String storeId, String keyword) throws DaoException;
	public StoreKeyword updateKeyword(String storeId, String oldKeyword, String newKeyword) throws DaoException;
	public int deleteKeyword(String storeId, String keyword) throws DaoException;
	public RecordSet<StoreKeyword> getAllKeywords(String storeId) throws DaoException;
	public RecordSet<StoreKeyword> getAllKeywords(String storeId, Integer page, Integer itemsPerPage) throws DaoException;
	public RecordSet<StoreKeyword> getAllKeywordsMatching(String storeId, String keyword) throws DaoException;
	public RecordSet<StoreKeyword> getAllKeywordsMatching(String storeId, String keyword, Integer page, Integer itemsPerPage) throws DaoException;

	/* Redirect */
	public RedirectRule getRedirectRule(RedirectRule redirectRule) throws DaoException;
	public RecordSet<RedirectRule> getRedirectRules(SearchCriteria<RedirectRule> searchCriteria) throws DaoException;
	public int addRedirectRule(RedirectRule rule) throws DaoException;
	public String addRedirectRuleAndGetId(RedirectRule rule) throws DaoException;
	public int updateRedirectRule(RedirectRule rule) throws DaoException;
	public int deleteRedirectRule(RedirectRule rule) throws DaoException;
			
	public int addRedirectKeyword(RedirectRule rule) throws DaoException;
	public int deleteRedirectKeyword(RedirectRule rule) throws DaoException;
	public int addRedirectCondition(RedirectRuleCondition rule) throws DaoException;
	public int updateRedirectCondition(RedirectRuleCondition rule) throws DaoException;
	public int deleteRedirectCondition(RedirectRuleCondition rule) throws DaoException;	
	public RecordSet<StoreKeyword> getRedirectKeywords(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType, ExactMatch keywordExactMatch) throws DaoException;
	public RecordSet<RedirectRuleCondition> getRedirectConditions(SearchCriteria<RedirectRule> criteria) throws DaoException;
	public RecordSet<RedirectRule> getRedirectForKeywords(SearchCriteria<StoreKeyword> criteria) throws DaoException;
	public RecordSet<RedirectRule> searchRedirectRule(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType) throws DaoException;
	public RecordSet<RedirectRule> searchRedirectRuleKeyword(SearchCriteria<RedirectRule> criteria, MatchType redirectMatchType,
			ExactMatch keywordExactMatch) throws DaoException;

	/* Campaigns */
	public int addCampaign(Campaign campaign) throws DaoException;
	public int updateCampaign(Campaign campaign) throws DaoException;
	public int deleteCampaign(Campaign campaign) throws DaoException;
	public int updateCampaignComment(Campaign campaign) throws DaoException;
	public int appendCampaignComment(Campaign campaign) throws DaoException;
	public Campaign getCampaign(Campaign campaign) throws DaoException;
	public RecordSet<Campaign> getCampaigns(SearchCriteria<Campaign> criteria) throws DaoException;
	public RecordSet<Campaign> getCampaignsContainingName(SearchCriteria<Campaign> criteria) throws DaoException;
	public RecordSet<Campaign> getCampaignsWithName(SearchCriteria<Campaign> criteria) throws DaoException;
	
	/* Banners */
	public int addBanner(Banner banner) throws DaoException;
	public int updateBanner(Banner banner) throws DaoException;
	public int deleteBanner(Banner banner) throws DaoException;
	public int updateBannerComment(Banner banner) throws DaoException;
	public int appendBannerComment(Banner banner) throws DaoException;
	public Banner getBanner(Banner banner) throws DaoException;
	public RecordSet<Banner> getBannerList(SearchCriteria<Banner> criteria) throws DaoException;
	public RecordSet<Banner> getBannerListWithNameLike(SearchCriteria<Banner> criteria) throws DaoException;
	public RecordSet<Banner> getBannerListWithNameMatching(SearchCriteria<Banner> criteria) throws DaoException;
	
	/* Banner Campaign Mapping */
	public Banner addCampaignBanner(String campaignId, String bannerId, Date startDate, Date endDate, List<String>keywordList) throws DaoException;
	public Banner updateCampaignBanner(String campaignId, String bannerId, Date startDate, Date endDate, List<String>keywordList) throws DaoException;
	public RecordSet<Banner> getCampaignBannerList(String campaignId) throws DaoException;
	public void deleteCampaignBanner(String campaignId, String bannerId) throws DaoException;
	
	/* Elevate */
	public int addElevateResult(ElevateResult elevate) throws DaoException;
	public int updateElevateResult(ElevateResult elevate) throws DaoException;
	public int updateElevateResultExpiryDate(ElevateResult elevate) throws DaoException;
	public int updateElevateResultComment(ElevateResult elevate) throws DaoException;
	public int appendElevateResultComment(ElevateResult elevate) throws DaoException;
	public int deleteElevateResult(ElevateResult elevate) throws DaoException;
	public int clearElevateResult(StoreKeyword keyword) throws DaoException;
	public int getElevateResultCount(SearchCriteria<ElevateResult> criteria) throws DaoException;
	public RecordSet<ElevateResult> getElevateResultList(SearchCriteria<ElevateResult> criteria) throws DaoException;
	public RecordSet<ElevateResult> getNoExpireElevateResultList(SearchCriteria<ElevateResult> criteria) throws DaoException;
	public Map<String, ElevateResult> getElevateResultMap(SearchCriteria<ElevateResult> criteria) throws DaoException;
	public ElevateResult getElevateItem(ElevateResult elevate) throws DaoException;
	
	/* Exclude */
	public int addExcludeResult(ExcludeResult exclude) throws DaoException;
	public int updateExcludeResultComment(ExcludeResult exclude) throws DaoException;
	public int appendExcludeResultComment(ExcludeResult exclude) throws DaoException;
	public int updateExcludeResultExpiryDate(ExcludeResult exclude) throws DaoException;
	public int deleteExcludeResult(ExcludeResult exclude) throws DaoException;
	public int clearExcludeResult(StoreKeyword keyword) throws DaoException;
	public int getExcludeResultCount(SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public RecordSet<ExcludeResult> getExcludeResultList(SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public ExcludeResult getExcludeItem(ExcludeResult exclude) throws DaoException;
	
	/* Big Bets */
	public RecordSet<Product> getExcludedProducts(String serverName, SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public RecordSet<ElevateProduct> getElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException;
	public RecordSet<ElevateProduct> getNoExpiryElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException;
	public ElevateProduct getElevatedProduct(String serverName, ElevateResult elevate) throws DaoException;
	public Product getExcludedProduct(String serverName, ExcludeResult exclude) throws DaoException;
	public String getEdpByPartNumber(String serverName, String storeId, String keyword, String partNumber) throws DaoException;
	
	/* Relevancy */
	/**
	 * Save the relevancy. Adds non-existent relevancy but updates existing one.
	 */
	public int addRelevancy(Relevancy relevancy) throws DaoException;
	public String addRelevancyAndGetId(Relevancy relevancy) throws DaoException;
	public int updateRelevancy(Relevancy relevancy) throws DaoException;
    public int deleteRelevancy(Relevancy relevancy) throws DaoException;
    /**
     * Returns relevancy info not including the relevancy fields
     */
	public Relevancy getRelevancy(Relevancy relevancy) throws DaoException;
    /**
     * Returns relevancy info including the relevancy fields
     */
	public Relevancy getRelevancyDetails(Relevancy relevancy) throws DaoException;
	public RecordSet<Relevancy> searchRelevancy(SearchCriteria<Relevancy> criteria, MatchType relevancyMatchType) throws DaoException;
    public int updateRelevancyComment(Relevancy relevancy) throws DaoException;
    public int appendRelevancyComment(Relevancy relevancy) throws DaoException;
    
    /* Relevancy Field */
	/**
	 * Save the relevancy field. Adds non-existent relevancy field but updates existing one.
	 */
    public int addOrUpdateRelevancyField(RelevancyField relevancyField) throws DaoException;
	public int addRelevancyField(RelevancyField relevancyField) throws DaoException;
	public int updateRelevancyField(RelevancyField relevancyField) throws DaoException;
    public int deleteRelevancyField(RelevancyField relevancyField) throws DaoException;
	public RelevancyField getRelevancyField(RelevancyField relevancyField) throws DaoException;
	
    /* Relevancy Keyword */
	public int addRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException;
	public int updateRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException;
    public int deleteRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException;
	public RelevancyKeyword getRelevancyKeyword(RelevancyKeyword relevancyKeyword) throws DaoException;
	public RecordSet<RelevancyKeyword> getRelevancyKeywords(Relevancy relevancy) throws DaoException;
	public int getRelevancyKeywordCount(Relevancy relevancy) throws DaoException;
	public int getRelevancyKeywordCount(StoreKeyword storeKeyword) throws DaoException;
	public RecordSet<RelevancyKeyword> searchRelevancyKeywords(SearchCriteria<RelevancyKeyword> criteria, MatchType relevancyMatchType,
				ExactMatch keywordExactMatch) throws DaoException;
	
    //Redirect Rule
	public CategoryList getCategories(String categoryCode) throws DaoException;

    /* Audit Trail */
    public RecordSet<AuditTrail> getAuditTrail(SearchCriteria<AuditTrail> auditDetail, boolean adminFlag) throws DaoException;
    public int addAuditTrail(AuditTrail auditTrail) throws DaoException;
    public List<String> getDropdownValues(int type, String storeId, boolean adminFlag) throws DaoException;
    public List<String> getRefIDs(String ent, String opt, String storeId) throws DaoException;
    /* Rule Status */
    public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria) throws DaoException;
	public int addRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public int updateRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public Map<String, Boolean> updateRuleStatus(List<RuleStatus> ruleStatusList) throws DaoException;
	public int removeRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public RuleStatus getRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public int processRuleStatus(RuleStatus ruleStatus, Boolean isDelete) throws DaoException;
	public List<String> getCleanList(List<String> ruleRefIds, Integer ruleTypeId, String pStatus, String aStatus) throws DaoException;
	
    /* Comment */
    public RecordSet<Comment> getComment(SearchCriteria<Comment> searchCriteria) throws DaoException;
	public int addComment(Comment comment) throws DaoException;
	public int updateComment(Comment comment) throws DaoException;
	public int removeComment(Integer commentId) throws DaoException;
	
    /* User */
    public RecordSet<User> getUsers(SearchCriteria<User> searchCriteria, MatchType matchTypeName) throws DaoException;
    public User getUser(String username) throws DaoException;
	public int addUser(User user) throws DaoException;
	public int updateUser(User user) throws DaoException;
	public int resetPassword(User user) throws DaoException;
	public int login(User user) throws DaoException;
	public int removeUser(User user) throws DaoException;

	public List<String> getGroups() throws DaoException;
	public List<String> getAllPermissions() throws DaoException;
	public RecordSet<Group> getGroupPermission(String groupId) throws DaoException;
	public int getFacetCount(String serverName, String storeId, String keyword, String fqCondition);
}
