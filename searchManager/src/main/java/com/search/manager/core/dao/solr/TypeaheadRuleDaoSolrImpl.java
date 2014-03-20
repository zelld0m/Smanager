package com.search.manager.core.dao.solr;

import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchBuilder;

@Repository("typeaheadRuleDaoSolr")
public class TypeaheadRuleDaoSolrImpl extends GenericDaoSolrImpl<TypeaheadRule> implements TypeaheadRuleDao {

    @Override
    protected Search generateQuery(TypeaheadRule model) {
        if (model != null) {
            return SearchBuilder.forClass(TypeaheadRule.class)
                    .addFilterIfPresent("ruleId", model.getRuleId())
                    .addFilterIfPresent("keyword", model.getRuleName())
                    .addFilterIfPresent("store", model.getStoreId())
                    .addFilterIfPresent("disabled", model.getDisabled())
                    .addFilterIfPresent("priority", model.getPriority())
                    .get();
        }

        return null;
    }
}
