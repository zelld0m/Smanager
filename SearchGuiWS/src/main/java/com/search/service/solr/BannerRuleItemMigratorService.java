package com.search.service.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;

@Service("bannerRuleItemMigratorService")
@RemoteProxy(name = "BannerRuleItemMigratorServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "bannerRuleItemMigratorService"))
public class BannerRuleItemMigratorService implements
		SolrRuleService<BannerRuleItem> {

	@Autowired
	@Qualifier("bannerRuleItemServiceSolr")
	private BannerRuleItemService bannerRuleItemServiceSolr;
	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemServiceSp;

	public static final Integer MAX_ROWS = 1000;

	// create model
	private BannerRuleItem toBannerRuleItem(String storeId, String ruleId,
			String ruleName, String memberId) throws CoreServiceException {
		BannerRuleItem bannerRuleItem = new BannerRuleItem();
		BannerRule bannerRule = new BannerRule();
		if (StringUtils.isNotBlank(storeId)) {
			bannerRule.setStoreId(storeId);
		}
		if (StringUtils.isNotBlank(ruleId)) {
			bannerRule.setRuleId(ruleId);
		}
		if (StringUtils.isNotBlank(ruleName)) {
			bannerRule.setRuleName(ruleName);
		}
		if (StringUtils.isNotBlank(memberId)) {
			bannerRuleItem.setMemberId(memberId);
		}
		bannerRuleItem.setRule(bannerRule);

		return bannerRuleItem;
	}

	// search
	private SearchResult<BannerRuleItem> search(String storeId, String ruleId,
			String ruleName, String memberId) throws CoreServiceException {
		return bannerRuleItemServiceSolr.search(toBannerRuleItem(storeId,
				ruleId, ruleName, memberId));
	}

	@RemoteMethod
	@Override
	public SearchResult<BannerRuleItem> searchByStoreId(String storeId)
			throws CoreServiceException {
		return search(storeId, null, null, null);
	}

	@RemoteMethod
	@Override
	public BannerRuleItem searchByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException {
		return searchByRuleName(storeId, keyword);
	}

	@RemoteMethod
	@Override
	public BannerRuleItem searchByRuleId(String storeId, String ruleId)
			throws CoreServiceException {
		SearchResult<BannerRuleItem> searchResult = search(storeId, ruleId,
				null, null);

		if (searchResult != null) {
			return searchResult.getResult().get(0);
		}

		return null;
	}

	@RemoteMethod
	@Override
	public BannerRuleItem searchByRuleName(String storeId, String ruleName)
			throws CoreServiceException {
		SearchResult<BannerRuleItem> searchResult = search(storeId, null,
				ruleName, null);

		if (searchResult != null) {
			return searchResult.getResult().get(0);
		}

		return null;
	}

	@RemoteMethod
	@Override
	public BannerRuleItem searchByMemberId(String storeId, String memberId)
			throws CoreServiceException {
		SearchResult<BannerRuleItem> searchResult = search(storeId, null, null,
				memberId);

		if (searchResult != null) {
			return searchResult.getResult().get(0);
		}

		return null;
	}

	// load
	private boolean load(String storeId, String ruleId, String ruleName,
			String memberId) throws CoreServiceException {
		int page = 1;
		BannerRuleItem bannerRuleItem = toBannerRuleItem(storeId, ruleId,
				ruleName, memberId);
		while (true) {
			SearchResult<BannerRuleItem> searchResult = bannerRuleItemServiceSp
					.search(bannerRuleItem, page, MAX_ROWS);
			if (searchResult.getTotalCount() > 0) {
				bannerRuleItemServiceSolr.add(searchResult.getResult());
				if (searchResult.getTotalCount() < MAX_ROWS) {
					return true;
				}
				page++;
			} else {
				break;
			}
		}

		return false;
	}

	@RemoteMethod
	@Override
	public boolean loadByStoreId(String storeId) throws CoreServiceException {
		return load(storeId, null, null, null);
	}

	@RemoteMethod
	@Override
	public boolean loadByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException {
		return loadByRuleName(storeId, keyword);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> loadByStoreKeywords(String storeId,
			Collection<String> keywords) throws CoreServiceException {
		if (keywords != null) {
			Map<String, Boolean> status = new HashMap<String, Boolean>();
			for (String keyword : keywords) {
				status.put(keyword, loadByStoreKeyword(storeId, keyword));
			}
			return status;
		}
		return null;
	}

	@RemoteMethod
	@Override
	public boolean loadByRuleId(String storeId, String ruleId)
			throws CoreServiceException {
		return load(storeId, ruleId, null, null);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> loadByRuleIds(String storeId,
			Collection<String> ruleIds) throws CoreServiceException {
		if (ruleIds != null) {
			Map<String, Boolean> status = new HashMap<String, Boolean>();
			for (String ruleId : ruleIds) {
				status.put(ruleId, loadByRuleId(storeId, ruleId));
			}
			return status;
		}
		return null;
	}

	@RemoteMethod
	@Override
	public boolean loadByRuleName(String storeId, String ruleName)
			throws CoreServiceException {
		return load(storeId, null, ruleName, null);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> loadByRuleNames(String storeId,
			Collection<String> ruleNames) throws CoreServiceException {
		if (ruleNames != null) {
			Map<String, Boolean> status = new HashMap<String, Boolean>();
			for (String ruleName : ruleNames) {
				status.put(ruleName, loadByRuleName(storeId, ruleName));
			}
			return status;
		}
		return null;
	}

	@RemoteMethod
	@Override
	public boolean loadByMemberId(String storeId, String memberId)
			throws CoreServiceException {
		return load(storeId, null, null, memberId);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> loadByMemberIds(String storeId,
			Collection<String> memberIds) throws CoreServiceException {
		if (memberIds != null) {
			Map<String, Boolean> status = new HashMap<String, Boolean>();
			for (String memberId : memberIds) {
				status.put(memberId, loadByMemberId(storeId, memberId));
			}
			return status;
		}
		return null;
	}

	// reset
	@RemoteMethod
	@Override
	public boolean resetByStoreId(String storeId) throws CoreServiceException {
		if (deleteByStoreId(storeId)) {
			return loadByStoreId(storeId);
		}
		return false;
	}

	@RemoteMethod
	@Override
	public boolean resetByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException {
		if (deleteByStoreKeyword(storeId, keyword)) {
			return loadByStoreKeyword(storeId, keyword);
		}
		return false;
	}

	@Override
	public boolean resetByStoreKeywords(String storeId,
			Collection<String> keywords) throws CoreServiceException {
		if (keywords != null) {
			deleteByStoreKeywords(storeId, keywords);
			loadByStoreKeywords(storeId, keywords);
			return true;
		}
		return false;
	}

	@RemoteMethod
	@Override
	public boolean resetByRuleId(String storeId, String ruleId)
			throws CoreServiceException {
		if (deleteByRuleId(storeId, ruleId)) {
			return loadByRuleId(storeId, ruleId);
		}
		return false;
	}

	@RemoteMethod
	@Override
	public boolean resetByRuleIds(String storeId, Collection<String> ruleIds)
			throws CoreServiceException {
		if (ruleIds != null) {
			deleteByRuleIds(storeId, ruleIds);
			loadByRuleIds(storeId, ruleIds);
			return true;
		}
		return false;
	}

	@RemoteMethod
	@Override
	public boolean resetByRuleName(String storeId, String ruleName)
			throws CoreServiceException {
		if (deleteByRuleName(storeId, ruleName)) {
			return loadByRuleName(storeId, ruleName);
		}
		return false;
	}

	@Override
	public boolean resetByRuleNames(String storeId, Collection<String> ruleNames)
			throws CoreServiceException {
		if (ruleNames != null) {
			deleteByRuleNames(storeId, ruleNames);
			loadByRuleNames(storeId, ruleNames);
			return true;
		}
		return false;
	}

	@RemoteMethod
	@Override
	public boolean resetByMemberId(String storeId, String memberId)
			throws CoreServiceException {
		if (deleteByMemberId(storeId, memberId)) {
			return loadByMemberId(storeId, memberId);
		}
		return false;
	}

	@RemoteMethod
	@Override
	public boolean resetByMemberIds(String storeId, Collection<String> memberIds)
			throws CoreServiceException {
		if (memberIds != null) {
			deleteByMemberIds(storeId, memberIds);
			loadByMemberIds(storeId, memberIds);
			return true;
		}
		return false;
	}

	// delete
	private boolean delete(String storeId, String ruleId, String ruleName,
			String memberId) throws CoreServiceException {
		BannerRuleItem bannerRuleItem = toBannerRuleItem(storeId, ruleId,
				ruleName, memberId);
		return bannerRuleItemServiceSolr.delete(bannerRuleItem);
	}

	private Map<String, Boolean> delete(List<BannerRuleItem> models,
			String modelField) throws CoreServiceException {
		Map<BannerRuleItem, Boolean> status = bannerRuleItemServiceSolr
				.delete(models);

		if (status != null) {
			Map<String, Boolean> deleteStatus = new HashMap<String, Boolean>();
			Iterator<Entry<BannerRuleItem, Boolean>> it = status.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<BannerRuleItem, Boolean> entry = it.next();
				if (modelField.equals("ruleId")
						&& entry.getKey().getRule() != null) {
					deleteStatus.put(entry.getKey().getRule().getRuleId(),
							entry.getValue());
				} else if ((modelField.equals("keyword") || modelField
						.equals("ruleName"))
						&& entry.getKey().getRule() != null) {
					deleteStatus.put(entry.getKey().getRule().getRuleName(),
							entry.getValue());
				} else if (modelField.equals("memberId")) {
					deleteStatus.put(entry.getKey().getMemberId(),
							entry.getValue());
				}
			}
			return deleteStatus;
		}

		return null;
	}

	@RemoteMethod
	@Override
	public boolean deleteByStoreId(String storeId) throws CoreServiceException {
		return delete(storeId, null, null, null);
	}

	@RemoteMethod
	@Override
	public boolean deleteByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException {
		return deleteByRuleName(storeId, keyword);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> deleteByStoreKeywords(String storeId,
			Collection<String> keywords) throws CoreServiceException {
		if (keywords != null) {
			List<BannerRuleItem> bannerRuleItems = new ArrayList<BannerRuleItem>();
			for (String keyword : keywords) {
				bannerRuleItems.add(toBannerRuleItem(storeId, null, keyword,
						null));
			}
			return delete(bannerRuleItems, "keyword");
		}
		return null;
	}

	@RemoteMethod
	@Override
	public boolean deleteByRuleId(String storeId, String ruleId)
			throws CoreServiceException {
		return delete(storeId, ruleId, null, null);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> deleteByRuleIds(String storeId,
			Collection<String> ruleIds) throws CoreServiceException {
		if (ruleIds != null) {
			List<BannerRuleItem> bannerRuleItems = new ArrayList<BannerRuleItem>();
			for (String ruleId : ruleIds) {
				bannerRuleItems.add(toBannerRuleItem(storeId, ruleId, null,
						null));
			}
			return delete(bannerRuleItems, "ruleId");
		}
		return null;
	}

	@RemoteMethod
	@Override
	public boolean deleteByRuleName(String storeId, String ruleName)
			throws CoreServiceException {
		return delete(storeId, null, ruleName, null);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> deleteByRuleNames(String storeId,
			Collection<String> ruleNames) throws CoreServiceException {
		if (ruleNames != null) {
			List<BannerRuleItem> bannerRuleItems = new ArrayList<BannerRuleItem>();
			for (String ruleName : ruleNames) {
				bannerRuleItems.add(toBannerRuleItem(storeId, null, ruleName,
						null));
			}
			return delete(bannerRuleItems, "ruleName");
		}
		return null;
	}

	@RemoteMethod
	@Override
	public boolean deleteByMemberId(String storeId, String memberId)
			throws CoreServiceException {
		return delete(storeId, null, null, memberId);
	}

	@RemoteMethod
	@Override
	public Map<String, Boolean> deleteByMemberIds(String storeId,
			Collection<String> memberIds) throws CoreServiceException {
		if (memberIds != null) {
			List<BannerRuleItem> bannerRuleItems = new ArrayList<BannerRuleItem>();
			for (String memberId : memberIds) {
				bannerRuleItems.add(toBannerRuleItem(storeId, null, null,
						memberId));
			}
			return delete(bannerRuleItems, "memberId");
		}
		return null;
	}

}
