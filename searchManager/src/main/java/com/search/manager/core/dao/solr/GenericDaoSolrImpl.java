package com.search.manager.core.dao.solr;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.annotation.SolrCore;
import com.search.manager.core.dao.GenericDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreSearchException;
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
	public List<T> update(Collection<T> models) throws CoreDaoException {
		return add(models);
	}

	@Override
	public boolean delete(T model) throws CoreDaoException {
		if (model != null) {
			Search search = generateQuery(model);
			if (search != null) {
				try {
					String query = formatQuery(searchProcessor
							.generateStrQuery(search));
					if (StringUtils.isNotBlank(query)) {
						try {
							getSolrServer().deleteByQuery(query);
							UpdateResponse updateResponse = getSolrServer()
									.commit();
							return updateResponse.getStatus() == 0 ? true
									: false;
						} catch (SolrServerException e) {
							throw new CoreDaoException(e);
						} catch (IOException e) {
							throw new CoreDaoException(e);
						}
					}
				} catch (CoreSearchException e) {
					throw new CoreDaoException(e);
				}
			}
		}
		return false;
	}

	@Override
	public Map<T, Boolean> delete(Collection<T> models) throws CoreDaoException {
		if (models != null) {
			Map<T, Boolean> deletedModelStatus = new HashMap<T, Boolean>();
			for (T model : models) {
				Search search = generateQuery(model);
				if (search != null) {
					try {
						String query = formatQuery(searchProcessor
								.generateStrQuery(search));
						if (StringUtils.isNotBlank(query)) {
							try {
								UpdateResponse updateResponse = getSolrServer()
										.deleteByQuery(query);
								deletedModelStatus.put(model, updateResponse
										.getStatus() == 0 ? true : false);
							} catch (SolrServerException e) {
								throw new CoreDaoException(e);
							} catch (IOException e) {
								throw new CoreDaoException(e);
							}
						}
					} catch (Exception e) {
						throw new CoreDaoException(e);
					}
				}
			}

			try {
				UpdateResponse updateResponse = getSolrServer().commit();
				return updateResponse.getStatus() == 0 ? deletedModelStatus
						: null;
			} catch (SolrServerException e) {
				throw new CoreDaoException(e);
			} catch (IOException e) {
				throw new CoreDaoException(e);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<T> search(Search search) throws CoreDaoException {
		try {
			return (SearchResult<T>) searchProcessor.processSearch(search);
		} catch (CoreSearchException e) {
			throw new CoreDaoException(e);
		}
	}

	@Override
	public SearchResult<T> search(T model) throws CoreDaoException {
		return (SearchResult<T>) search(model, -1, -1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<T> search(T model, int pageNumber, int maxRowCount)
			throws CoreDaoException {
		try {
			Search search = generateQuery(model);
			search.setPageNumber(pageNumber);
			search.setMaxRowCount(maxRowCount);
			return (SearchResult<T>) searchProcessor.processSearch(search);
		} catch (CoreSearchException e) {
			throw new CoreDaoException(e);
		}
	}

	private String formatQuery(String query) {
		if (StringUtils.isNotBlank(query)) {
			// remove ../select?
			query = query.substring(query.indexOf('?') + 1, query.length());
			// deleteByQuery features of solr don't support filter queries
			query = query.replace("q=*:*&fq=", "");
			return query;
		}
		return null;
	}
}
