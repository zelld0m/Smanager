package com.search.manager.core.dao.solr;

import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.SolrCore;
import com.search.manager.core.dao.GenericDao;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchProcessor;
import com.search.manager.core.search.SearchResult;
import com.search.manager.solr.util.SolrServerFactory;

public abstract class GenericDaoSolrImpl<T> implements GenericDao<T> {

	@Autowired
	@Qualifier("solrSearchProcessor")
	private SearchProcessor searchProcessor;
	@Autowired
	private SolrServerFactory solrServerFactory;
	private Class<T> modelClass;

	protected abstract Search generateQuery(T model);

	@SuppressWarnings("unchecked")
	public GenericDaoSolrImpl() {
		this.modelClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public Class<T> getModelClass() throws Exception {
		return this.modelClass;
	}

	private SolrServer getSolrServer() throws Exception {
		String core;
		SolrCore solrCore = (SolrCore) modelClass.getAnnotation(SolrCore.class);

		if (solrCore != null) {
			core = solrCore.name();
			return solrServerFactory.getCoreInstance(core).getHttpSolrServer();
		}

		return null;
	}

	@Override
	public T add(T model) throws Exception {
		getSolrServer().addBean(model, 10000);
		return model;
	}

	@Override
	public T update(T model) throws Exception {
		return add(model);
	}

	@Override
	public boolean delete(T model) throws Exception {
		Search search = generateQuery(model);
		if (search != null) {
			String query = searchProcessor.generateStrQuery(search);
			// remove ../select?
			query = query.substring(query.indexOf('?') + 1, query.length());
			if (StringUtils.isNotBlank(query)) {
				// deleteByQuery features of solr don't support filter queries
				query = query.replace("q=*:*&fq=", "");
				UpdateResponse updateResponse = getSolrServer().deleteByQuery(
						query);
				getSolrServer().commit();
				return updateResponse.getStatus() == 0 ? true : false;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<T> search(Search search) throws Exception {
		return (SearchResult<T>) searchProcessor.processSearch(search);
	}

}
