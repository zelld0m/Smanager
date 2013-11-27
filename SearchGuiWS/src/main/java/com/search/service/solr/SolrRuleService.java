package com.search.service.solr;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.search.SearchResult;

public interface SolrRuleService<T> {

	// search
	SearchResult<T> searchByStoreId(String storeId) throws CoreServiceException;

	T searchByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException;

	T searchByRuleId(String storeId, String ruleId) throws CoreServiceException;

	T searchByRuleName(String storeId, String ruleName)
			throws CoreServiceException;

	T searchByMemberId(String storeId, String memberId)
			throws CoreServiceException;

	// load
	boolean loadByStoreId(String storeId) throws CoreServiceException;

	boolean loadByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException;

	Map<String, Boolean> loadByStoreKeywords(String storeId,
			Collection<String> keywords) throws CoreServiceException;

	boolean loadByRuleId(String storeId, String ruleId)
			throws CoreServiceException;

	Map<String, Boolean> loadByRuleIds(String storeId,
			Collection<String> ruleIds) throws CoreServiceException;

	boolean loadByRuleName(String storeId, String ruleName)
			throws CoreServiceException;

	Map<String, Boolean> loadByRuleNames(String storeId,
			Collection<String> ruleNames) throws CoreServiceException;

	boolean loadByMemberId(String storeId, String memberId)
			throws CoreServiceException;

	Map<String, Boolean> loadByMemberIds(String storeId,
			Collection<String> memberIds) throws CoreServiceException;

	// reset
	boolean resetByStoreId(String storeId) throws CoreServiceException;

	boolean resetByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException;

	boolean resetByStoreKeywords(String storeId,
			Collection<String> keywords) throws CoreServiceException;

	boolean resetByRuleId(String storeId, String ruleId)
			throws CoreServiceException;

	boolean resetByRuleIds(String storeId,
			Collection<String> ruleIds) throws CoreServiceException;

	boolean resetByRuleName(String storeId, String ruleName)
			throws CoreServiceException;

	boolean resetByRuleNames(String storeId,
			Collection<String> ruleNames) throws CoreServiceException;

	boolean resetByMemberId(String storeId, String memberId)
			throws CoreServiceException;

	boolean resetByMemberIds(String storeId,
			Collection<String> memberIds) throws CoreServiceException;

	// delete
	boolean deleteByStoreId(String storeId) throws CoreServiceException;

	boolean deleteByStoreKeyword(String storeId, String keyword)
			throws CoreServiceException;

	Map<String, Boolean> deleteByStoreKeywords(String storeId,
			Collection<String> keywords) throws CoreServiceException;

	boolean deleteByRuleId(String storeId, String ruleId)
			throws CoreServiceException;

	Map<String, Boolean> deleteByRuleIds(String storeId,
			Collection<String> ruleIds) throws CoreServiceException;

	boolean deleteByRuleName(String storeId, String ruleName)
			throws CoreServiceException;

	Map<String, Boolean> deleteByRuleNames(String storeId,
			Collection<String> ruleNames) throws CoreServiceException;

	boolean deleteByMemberId(String storeId, String memberId)
			throws CoreServiceException;

	Map<String, Boolean> deleteByMemberIds(String storeId,
			Collection<String> memberIds) throws CoreServiceException;

}
