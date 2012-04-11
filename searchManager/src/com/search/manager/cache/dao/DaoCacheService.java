package com.search.manager.cache.dao;

import java.util.List;

import com.search.manager.dao.DaoException;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;

public interface DaoCacheService {
	public List<String> getAllKeywords(String storeName) throws DaoException, DataException;
	public boolean resetAllkeywords(String storeName);
	public boolean resetElevateResult(String storeName, String kw);
	public boolean loadElevateResultList(String storeName) throws DaoException;
	public boolean loadExcludeResultList(String storeName) throws DaoException;

	public List<ElevateResult> getElevateResultList(SearchCriteria<ElevateResult> criteria, String storeName);
	public RecordSet<ElevateProduct> getElevatedProducts(String serverName, SearchCriteria<ElevateResult> criteria, String storeName);
	public int getElevateResultCount(SearchCriteria<ElevateResult> criteria, String storeName);
	public boolean updateElevateResultList(ElevateResult elevateResult);
	
	public List<ExcludeResult> getExcludeResultList(SearchCriteria<ExcludeResult> criteria, String storeName);
	public RecordSet<Product> getExcludedProducts(String serverName, SearchCriteria<ExcludeResult> criteria, String storeName);
	public int getExcludeResultCount(SearchCriteria<ExcludeResult> criteria, String storeName);
	public boolean updateExcludeResultList(ExcludeResult excludeResult);
	
	public boolean updateRedirectRule(String storeName);
	public String getRedirectRule(String storeName, String keyword);
}
