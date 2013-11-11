package com.search.manager.core.dao.solr;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.SolrCore;
import com.search.manager.core.dao.GenericDao;
import com.search.manager.core.exception.CoreDaoException;
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
	public Class<T> getModelClass() throws CoreDaoException {
		return this.modelClass;
	}

	private SolrServer getSolrServer() throws CoreDaoException {
		String core;
		SolrCore solrCore = (SolrCore) modelClass.getAnnotation(SolrCore.class);

		if (solrCore != null) {
			core = solrCore.name();
			try {
				return solrServerFactory.getCoreInstance(core)
						.getHttpSolrServer();
			} catch (SolrServerException e) {
				throw new CoreDaoException(e);
			}
		}

		return null;
	}

	@Override
	public T add(T model) throws CoreDaoException {
		try {
			getSolrServer().addBean(model);
			getSolrServer().commit();
		} catch (IOException e) {
			throw new CoreDaoException(e);
		} catch (SolrServerException e) {
			throw new CoreDaoException(e);
		} catch (Exception e) {
			throw new CoreDaoException(e);
		}
		return model;
	}

	@Override
	public List<T> add(Collection<T> models) throws CoreDaoException {
		if (models != null) {
			for (T model : models) {
				try {
					getSolrServer().addBean(model);
				} catch (IOException e) {
					throw new CoreDaoException(e);
				} catch (SolrServerException e) {
					throw new CoreDaoException(e);
				}
			}

			try {
				getSolrServer().commit();
			} catch (SolrServerException e) {
				throw new CoreDaoException(e);
			} catch (IOException e) {
				throw new CoreDaoException(e);
			}
		}
		return (List<T>) models;
	}

	@Override
	public T update(T model) throws CoreDaoException {
		return add(model);
	}

	@Override
	public boolean delete(T model) throws CoreDaoException {
		Search search = generateQuery(model);
		if (search != null) {
			String query;
			try {
				query = searchProcessor.generateStrQuery(search);
			} catch (Exception e) {
				throw new CoreDaoException(e);
			}
			// remove ../select?
			query = query.substring(query.indexOf('?') + 1, query.length());
			if (StringUtils.isNotBlank(query)) {
				// deleteByQuery features of solr don't support filter queries
				query = query.replace("q=*:*&fq=", "");

				try {
					UpdateResponse updateResponse = getSolrServer()
							.deleteByQuery(query);
					getSolrServer().commit();
					return updateResponse.getStatus() == 0 ? true : false;
				} catch (SolrServerException e) {
					throw new CoreDaoException(e);
				} catch (IOException e) {
					throw new CoreDaoException(e);
				}

			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<T> search(Search search) throws CoreDaoException {
		try {
			return (SearchResult<T>) searchProcessor.processSearch(search);
		} catch (Exception e) {
			throw new CoreDaoException(e);
		}
	}

}
