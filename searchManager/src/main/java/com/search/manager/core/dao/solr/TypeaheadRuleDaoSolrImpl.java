package com.search.manager.core.dao.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchBuilder;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

@Repository("typeaheadRuleDaoSolr")
public class TypeaheadRuleDaoSolrImpl extends GenericDaoSolrImpl<TypeaheadRule> implements TypeaheadRuleDao {

	@Autowired
	protected SolrServerFactory solrServers;
	
    @Override
    protected Search generateQuery(TypeaheadRule model) {
        if (model != null) {
        	
        	Search search = SearchBuilder.forClass(TypeaheadRule.class)
                    .addFilterIfPresent("ruleId", model.getRuleId())
                    .addFilterIfPresent("keyword", model.getRuleName())
                    .addFilterIfPresent("store", model.getStoreId())
                    .addFilterIfPresent("disabled", model.getDisabled())
                    .addFilterIfPresent("priority", model.getPriority())
                    .get();

        	return search;
            
            
        }

        return null;
    }
    
    @Override
    public TypeaheadRule add(TypeaheadRule model) throws CoreDaoException {
    	
    	try {
			List<TypeaheadRule> rules = new ArrayList<TypeaheadRule>();
			
			rules.add(model);
			
			add(rules);
			
		} catch (Exception e) {
			throw new CoreDaoException("Unable to commit typeahead rule: "+model.getRuleId()+"", e);
		}
		
    	
		return model;
    	
    }
    
    @Override
    public List<TypeaheadRule> add(Collection<TypeaheadRule> models) throws CoreDaoException {
    	
    	try {
    		
    		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    		
    		for(TypeaheadRule model : models) {
    		
    			SolrInputDocument doc = SolrDocUtil.composeSolrDoc(model);
    			docs.add(doc);
    		}
			
			solrServers.getCoreInstance(
					Constants.Core.TYPEAHEAD_RULE_PUB.getCoreName())
					.addDocs(docs);
			solrServers.getCoreInstance(
					Constants.Core.TYPEAHEAD_RULE_PUB.getCoreName())
					.softCommit();
			
		} catch (Exception e) {
			throw new CoreDaoException("Unable to commit typeahead rule. ", e);
		}
		
    	
		return new ArrayList<TypeaheadRule>(models);
    	
    }
}
