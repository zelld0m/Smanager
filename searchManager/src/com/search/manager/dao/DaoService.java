package com.search.manager.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.search.manager.dao.sp.RuleStatusDAO.SortOrder;
import com.search.manager.enums.ExportRuleMapSortType;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
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
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;

public interface DaoService extends SearchDaoService {
	
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
	public List<Keyword> getAllKeywords(String storeId, RuleEntity ruleEntity) throws DaoException;
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
	public int updateExcludeResult(ExcludeResult exclude) throws DaoException;
	public int updateExcludeResultComment(ExcludeResult exclude) throws DaoException;
	public int appendExcludeResultComment(ExcludeResult exclude) throws DaoException;
	public int updateExcludeResultExpiryDate(ExcludeResult exclude) throws DaoException;
	public int deleteExcludeResult(ExcludeResult exclude) throws DaoException;
	public int clearExcludeResult(StoreKeyword keyword) throws DaoException;
	public int getExcludeResultCount(SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public RecordSet<ExcludeResult> getExcludeResultList(SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public ExcludeResult getExcludeItem(ExcludeResult exclude) throws DaoException;
	
	/* Demote */
	public int addDemoteResult(DemoteResult demote) throws DaoException;
	public int updateDemoteResult(DemoteResult demote) throws DaoException;
	public int updateDemoteResultExpiryDate(DemoteResult demote) throws DaoException;
	public int updateDemoteResultComment(DemoteResult demote) throws DaoException;
	public int appendDemoteResultComment(DemoteResult demote) throws DaoException;
	public int deleteDemoteResult(DemoteResult demote) throws DaoException;
	public int clearDemoteResult(StoreKeyword keyword) throws DaoException;
	public int getDemoteResultCount(SearchCriteria<DemoteResult> criteria) throws DaoException;
	public RecordSet<DemoteResult> getDemoteResultList(SearchCriteria<DemoteResult> criteria) throws DaoException;
	public RecordSet<DemoteResult> getNoExpireDemoteResultList(SearchCriteria<DemoteResult> criteria) throws DaoException;
	public Map<String, DemoteResult> getDemoteResultMap(SearchCriteria<DemoteResult> criteria) throws DaoException;
	public DemoteResult getDemoteItem(DemoteResult demote) throws DaoException;
	
	/* Facet Sort */
	public int addFacetSort(FacetSort facetSort) throws DaoException;
	public String addFacetSortAndGetId(FacetSort facetSort) throws DaoException;
	public int deleteFacetSort(FacetSort facetSort) throws DaoException;
	public int updateFacetSort(FacetSort facetSort) throws DaoException;
	public FacetSort getFacetSort(FacetSort facetSort) throws DaoException;
	public RecordSet<FacetSort> searchFacetSort(SearchCriteria<FacetSort> criteria, MatchType matchType) throws DaoException;
	public int addFacetGroup(FacetGroup facetGroup) throws DaoException;
	public int deleteFacetGroup(FacetGroup facetGroup) throws DaoException;
	public int updateFacetGroup(FacetGroup facetGroup) throws DaoException;
	public RecordSet<FacetGroup> searchFacetGroup(SearchCriteria<FacetGroup> criteria, MatchType matchType) throws DaoException;
	public int addFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException;
	public int addFacetGroupItems(List<FacetGroupItem> facetGroupItems) throws DaoException;
	public int deleteFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException;
	public int updateFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException;
	public int clearFacetGroupItem(FacetGroup facetGroup) throws DaoException;
	public RecordSet<FacetGroupItem> searchFacetGroupItem(SearchCriteria<FacetGroupItem> criteria, MatchType matchType) throws DaoException;
	
	/* Big Bets */
	public RecordSet<Product> getExcludedProducts(String serverName, SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public RecordSet<Product> getExcludedProductsIgnoreKeyword(String serverName, SearchCriteria<ExcludeResult> criteria) throws DaoException;
	public RecordSet<ElevateProduct> getElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException;
	public RecordSet<ElevateProduct> getElevatedProductsIgnoreKeyword(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException;
	public RecordSet<ElevateProduct> getNoExpiryElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria) throws DaoException;
	public ElevateProduct getElevatedProduct(String serverName, ElevateResult elevate) throws DaoException;
	public Product getExcludedProduct(String serverName, ExcludeResult exclude) throws DaoException;
	public String getEdpByPartNumber(String serverName, String storeId, String keyword, String partNumber) throws DaoException;
	public RecordSet<DemoteProduct> getDemotedProducts(String serverName, SearchCriteria<DemoteResult> criteria) throws DaoException;
	public RecordSet<DemoteProduct> getDemotedProductsIgnoreKeyword(String serverName, SearchCriteria<DemoteResult> criteria) throws DaoException;
	public RecordSet<DemoteProduct> getNoExpiryDemotedProducts(String serverName, SearchCriteria<DemoteResult> criteria) throws DaoException;
	public DemoteProduct getDemotedProduct(String serverName, DemoteResult elevate) throws DaoException;
	
	
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
	
    /* Audit Trail */
    public RecordSet<AuditTrail> getAuditTrail(SearchCriteria<AuditTrail> auditDetail, boolean adminFlag) throws DaoException;
    public int addAuditTrail(AuditTrail auditTrail) throws DaoException;
    public List<String> getDropdownValues(int type, String storeId, boolean adminFlag) throws DaoException;
    public List<String> getRefIDs(String ent, String opt, String storeId) throws DaoException;
    
    /* Rule Status */
    public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria) throws DaoException;
    public RecordSet<RuleStatus> getRuleStatus(SearchCriteria<RuleStatus> searchCriteria, SortOrder sortOrder) throws DaoException;
	public int addRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public int updateRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public Map<String, Boolean> updateRuleStatus(RuleStatusEntity status, List<RuleStatus> ruleStatusList, String requestBy, Date requestDate) throws DaoException;
	public int removeRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public RuleStatus getRuleStatus(RuleStatus ruleStatus) throws DaoException;
	public List<String> getCleanList(List<String> ruleRefIds, Integer ruleTypeId, String pStatus, String aStatus) throws DaoException;
	public int updateRuleStatusPublishInfo(RuleStatus ruleStatus, RuleStatusEntity requestedPublishStatus, String requestBy, Date requestDate) throws DaoException;
	public int updateRuleStatusApprovalInfo(RuleStatus ruleStatus, RuleStatusEntity requestedApprovalStatus,String requestBy, Date requestDate) throws DaoException;
	public int updateRuleStatusExportInfo(RuleStatus ruleStatus, String exportBy, ExportType exportType, Date exportDate) throws DaoException;
	public int updateRuleStatusDeletedInfo(RuleStatus ruleStatus, String deletedBy) throws DaoException;
	public Map<String, Integer> addRuleStatusComment(RuleStatusEntity ruleStatus, String store, String username, String pComment, String ...ruleStatusId);

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

	/* Version */
	public boolean createRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String notes);
	public boolean deleteRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, int version);
    public boolean deleteRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, int version, boolean physical);
	public boolean restoreRuleVersion(RuleXml xml);	
	public List<RuleXml> getRuleVersions(String store, String ruleType, String ruleId);
	public int getRuleVersionsCount(String store, String ruleType, String ruleId);
	public boolean createPublishedVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String notes);
	public List<RuleXml> getPublishedRuleVersions(String store, String ruleType, String ruleId);

	
	/* Export */
	public boolean exportRule(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, ExportType exportType, String username, String comment) throws DaoException;
	
	/* Export Rule Map */
	public RecordSet<ExportRuleMap> getExportRuleMap(SearchCriteria<ExportRuleMap> exportRuleMap, ExportRuleMapSortType sortType) throws DaoException;
	public int saveExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException;
	public int deleteExportRuleMap(ExportRuleMap exportRuleMap) throws DaoException;
	
	/* Did you mean */
    public SpellRules getSpellRules(String store);
    public SpellRule getSpellRuleById(String store, String ruleId);
    public boolean replaceSpellRules(SpellRules rules) throws DaoException;
    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> searchCriteria) throws DaoException;
    public RecordSet<SpellRule> getSpellRule(SearchCriteria<SpellRule> criteria, List<String> statusList) throws DaoException;
    public RecordSet<SpellRuleXml> getSpellRuleXml(SearchCriteria<SpellRule> criteria, List<String> statusList) throws DaoException; 
    public List<SpellRule> getActiveSpellRules(String storeId) throws DaoException;
    public void saveSpellRules(String store) throws DaoException;
    public void reloadSpellRules(String store) throws DaoException;
    public Integer getMaxSuggest(String storeId) throws DaoException;
    public int addSpellRule(SpellRule rule) throws DaoException;
    public int updateSpellRule(SpellRule rule) throws DaoException;
    public void deleteSpellRule(SpellRule rule) throws DaoException;
    public boolean isDuplicateSearchTerm(String store, String searchTerm, String ruleId) throws DaoException;
}
