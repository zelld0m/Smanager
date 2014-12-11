package com.search.service.solr;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.*;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.TypeaheadRuleService;

@Service("typeaheadRuleMigratorService")
@RemoteProxy(name = "TypeaheadRuleMigratorServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "typeaheadRuleMigratorService"))
public class TypeaheadRuleMigratorService implements SolrRuleService<TypeaheadRule> {

    @Autowired
    @Qualifier("typeaheadRuleServiceSolr")
    private TypeaheadRuleService typeaheadRuleServiceSolr;
    @Autowired
    @Qualifier("typeaheadRuleServiceSp")
    private TypeaheadRuleService typeaheadRuleServiceSp;

    public static final Integer MAX_ROWS = 1000;

    // create model
    private TypeaheadRule toTypeaheadRule(String storeId, String ruleId, String ruleName) throws CoreServiceException {
        TypeaheadRule typeaheadRule = new TypeaheadRule();

        typeaheadRule.setStoreId(StringUtils.defaultIfBlank(storeId, null));
        typeaheadRule.setRuleId(StringUtils.defaultIfBlank(ruleId, null));
        typeaheadRule.setRuleName(StringUtils.defaultIfBlank(ruleName, null));

        return typeaheadRule;
    }

    // search
    private SearchResult<TypeaheadRule> search(String storeId, String ruleId, String ruleName, String memberId)
            throws CoreServiceException {
        return typeaheadRuleServiceSolr.search(toTypeaheadRule(storeId, ruleId, ruleName));
    }

    @RemoteMethod
    @Override
    public SearchResult<TypeaheadRule> searchByStoreId(String storeId) throws CoreServiceException {
        return search(storeId, null, null, null);
    }

    @RemoteMethod
    @Override
    public TypeaheadRule searchByStoreKeyword(String storeId, String keyword) throws CoreServiceException {
        return searchByRuleName(storeId, keyword);
    }

    @RemoteMethod
    @Override
    public TypeaheadRule searchByRuleId(String storeId, String ruleId) throws CoreServiceException {
        SearchResult<TypeaheadRule> searchResult = search(storeId, ruleId, null, null);

        if (searchResult != null) {
            return searchResult.getResult().get(0);
        }

        return null;
    }

    @RemoteMethod
    @Override
    public TypeaheadRule searchByRuleName(String storeId, String ruleName) throws CoreServiceException {
        SearchResult<TypeaheadRule> searchResult = search(storeId, null, ruleName, null);

        if (searchResult != null) {
            return searchResult.getResult().get(0);
        }

        return null;
    }

    @RemoteMethod
    @Override
    public TypeaheadRule searchByMemberId(String storeId, String memberId) throws CoreServiceException {
        SearchResult<TypeaheadRule> searchResult = search(storeId, null, null, memberId);

        if (searchResult != null) {
            return searchResult.getResult().get(0);
        }

        return null;
    }

    // load
    private boolean load(String storeId, String ruleId, String ruleName) throws CoreServiceException {
        int page = 1;
        TypeaheadRule typeaheadRule = toTypeaheadRule(storeId, ruleId, ruleName);

        while (true) {
            SearchResult<TypeaheadRule> searchResult = typeaheadRuleServiceSp.search(typeaheadRule, page, MAX_ROWS);

            List<TypeaheadRule> results = searchResult.getResult();
            
            // Get Sections
            if(results != null) {
            	for(TypeaheadRule result : results) {
            		typeaheadRuleServiceSp.initializeTypeaheadSections(result);
            	}
            }
            
            if (searchResult.getResult().size() > 0) {
                typeaheadRuleServiceSolr.add(results);
                
                page++;
            } else {
            	if(page != 1) {
            		return true;
            	}
                return false;
            }
        }
    }

    @RemoteMethod
    @Override
    public boolean loadByStoreId(String storeId) throws CoreServiceException {
        return load(storeId, null, null);
    }

    @RemoteMethod
    @Override
    public boolean loadByStoreKeyword(String storeId, String keyword) throws CoreServiceException {
        return loadByRuleName(storeId, keyword);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> loadByStoreKeywords(String storeId, Collection<String> keywords)
            throws CoreServiceException {
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
    public boolean loadByRuleId(String storeId, String ruleId) throws CoreServiceException {
        return load(storeId, ruleId, null);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> loadByRuleIds(String storeId, Collection<String> ruleIds) throws CoreServiceException {
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
    public boolean loadByRuleName(String storeId, String ruleName) throws CoreServiceException {
        return load(storeId, null, ruleName);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> loadByRuleNames(String storeId, Collection<String> ruleNames)
            throws CoreServiceException {
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
    public boolean loadByMemberId(String storeId, String memberId) throws CoreServiceException {
        return load(storeId, null, null);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> loadByMemberIds(String storeId, Collection<String> memberIds)
            throws CoreServiceException {
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
    public boolean resetByStoreKeyword(String storeId, String keyword) throws CoreServiceException {
        if (deleteByStoreKeyword(storeId, keyword)) {
            return loadByStoreKeyword(storeId, keyword);
        }
        return false;
    }

    @Override
    public boolean resetByStoreKeywords(String storeId, Collection<String> keywords) throws CoreServiceException {
        if (keywords != null) {
            deleteByStoreKeywords(storeId, keywords);
            loadByStoreKeywords(storeId, keywords);
            return true;
        }
        return false;
    }

    @RemoteMethod
    @Override
    public boolean resetByRuleId(String storeId, String ruleId) throws CoreServiceException {
        if (deleteByRuleId(storeId, ruleId)) {
            return loadByRuleId(storeId, ruleId);
        }
        return false;
    }

    @RemoteMethod
    @Override
    public boolean resetByRuleIds(String storeId, Collection<String> ruleIds) throws CoreServiceException {
        if (ruleIds != null) {
            deleteByRuleIds(storeId, ruleIds);
            loadByRuleIds(storeId, ruleIds);
            return true;
        }
        return false;
    }

    @RemoteMethod
    @Override
    public boolean resetByRuleName(String storeId, String ruleName) throws CoreServiceException {
        if (deleteByRuleName(storeId, ruleName)) {
            return loadByRuleName(storeId, ruleName);
        }
        return false;
    }

    @Override
    public boolean resetByRuleNames(String storeId, Collection<String> ruleNames) throws CoreServiceException {
        if (ruleNames != null) {
            deleteByRuleNames(storeId, ruleNames);
            loadByRuleNames(storeId, ruleNames);
            return true;
        }
        return false;
    }

    @RemoteMethod
    @Override
    public boolean resetByMemberId(String storeId, String memberId) throws CoreServiceException {
        throw new CoreServiceException("Unsupported operation.");
    }

    @RemoteMethod
    @Override
    public boolean resetByMemberIds(String storeId, Collection<String> memberIds) throws CoreServiceException {
        throw new CoreServiceException("Unsupported operation.");
    }

    // delete
    private boolean delete(String storeId, String ruleId, String ruleName, String memberId) throws CoreServiceException {
        return typeaheadRuleServiceSolr.delete(toTypeaheadRule(storeId, ruleId, ruleName));
    }

    private Map<String, Boolean> delete(List<TypeaheadRule> models, String modelField) throws CoreServiceException {
        Map<TypeaheadRule, Boolean> status = typeaheadRuleServiceSolr.delete(models);

        if (status != null) {
            Map<String, Boolean> deleteStatus = new HashMap<String, Boolean>();
            Iterator<Entry<TypeaheadRule, Boolean>> it = status.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<TypeaheadRule, Boolean> entry = it.next();

                if (modelField.equals("ruleId") && entry.getKey().getRuleId() != null) {
                    deleteStatus.put(entry.getKey().getRuleId(), entry.getValue());
                } else if ((modelField.equals("keyword") || modelField.equals("ruleName"))
                        && entry.getKey().getRuleName() != null) {
                    deleteStatus.put(entry.getKey().getRuleName(), entry.getValue());
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
    public boolean deleteByStoreKeyword(String storeId, String keyword) throws CoreServiceException {
        return deleteByRuleName(storeId, keyword);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> deleteByStoreKeywords(String storeId, Collection<String> keywords)
            throws CoreServiceException {
        if (keywords != null) {
            List<TypeaheadRule> typeaheadRules = new ArrayList<TypeaheadRule>();
            for (String keyword : keywords) {
                typeaheadRules.add(toTypeaheadRule(storeId, null, keyword));
            }
            return delete(typeaheadRules, "keyword");
        }
        return null;
    }

    @RemoteMethod
    @Override
    public boolean deleteByRuleId(String storeId, String ruleId) throws CoreServiceException {
        return delete(storeId, ruleId, null, null);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> deleteByRuleIds(String storeId, Collection<String> ruleIds) throws CoreServiceException {
        if (ruleIds != null) {
            List<TypeaheadRule> typeaheadRules = new ArrayList<TypeaheadRule>();
            for (String ruleId : ruleIds) {
                typeaheadRules.add(toTypeaheadRule(storeId, ruleId, null));
            }
            return delete(typeaheadRules, "ruleId");
        }
        return null;
    }

    @RemoteMethod
    @Override
    public boolean deleteByRuleName(String storeId, String ruleName) throws CoreServiceException {
        return delete(storeId, null, ruleName, null);
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> deleteByRuleNames(String storeId, Collection<String> ruleNames)
            throws CoreServiceException {
        if (ruleNames != null) {
            List<TypeaheadRule> typeaheadRules = new ArrayList<TypeaheadRule>();
            for (String ruleName : ruleNames) {
                typeaheadRules.add(toTypeaheadRule(storeId, null, ruleName));
            }
            return delete(typeaheadRules, "ruleName");
        }
        return null;
    }

    @RemoteMethod
    @Override
    public boolean deleteByMemberId(String storeId, String memberId) throws CoreServiceException {
        throw new CoreServiceException("Unsupported operation.");
    }

    @RemoteMethod
    @Override
    public Map<String, Boolean> deleteByMemberIds(String storeId, Collection<String> memberIds)
            throws CoreServiceException {
        throw new CoreServiceException("Unsupported operation.");
    }

}
