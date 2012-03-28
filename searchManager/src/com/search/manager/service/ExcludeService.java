package com.search.manager.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;

@RemoteProxy(
		name = "ExcludeServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "excludeService")
)
public class ExcludeService {
	private static final Logger logger = Logger.getLogger(ExcludeService.class);

	@Autowired private DaoService daoService;

	
	@RemoteMethod
	public int addExcludeByPartNumber(String keyword, String partNumber, int sequence, String expiryDate, String comment) {
		try {
			logger.info(String.format("%s %s %d", keyword, partNumber, sequence));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			String edp = daoService.getEdpByPartNumber(server, store, keyword, partNumber);
			comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
			comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(edp);
			e.setExpiryDate(StringUtils.isBlank(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			if (StringUtils.isNotBlank(edp)){
				return daoService.addExcludeResult(e);
			}
			return 0;
		} catch (DaoException e) {
			logger.error("Failed during addExcludeByPartNumber()",e);
		}
		return -1;
	}
	
	@RemoteMethod
	public int addExclude(String keyword, String edp) {
		try {
			logger.info(String.format("%s %s", keyword, edp));

			String store = UtilityService.getStoreName();
			daoService.addKeyword(store, keyword);
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(edp);
			e.setLastModifiedBy(UtilityService.getUsername());
			return daoService.addExcludeResult(e);
		} catch (DaoException e) {
			logger.error("Failed during addExclude()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int removeExclude(String keyword, String productId) {
		try {
			String store = UtilityService.getStoreName();

			logger.info(String.format("%s %s %s", store, keyword, productId));
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setLastModifiedBy(UtilityService.getUsername());
			return daoService.deleteExcludeResult(e);
		} catch (DaoException e) {
			logger.error("Failed during removeExclude()",e);
		}
		return -1;
	}

	@RemoteMethod
	public RecordSet<Product> getExcludedProducts(String keyword, int page,int itemsPerPage) {
		try {
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			logger.info(String.format("%s %s %s %d %d", server, store, keyword, page, itemsPerPage));
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(e, null, null,  page, itemsPerPage);
			return daoService.getExcludedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExcludedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public Integer getExcludedProductCount(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			String store = UtilityService.getStoreName();
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(e, null, null, null, null);
			return daoService.getExcludeResultCount(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExcludedProductCount",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<Product> getProducts(String filter, String keyword, int page, int itemsPerPage) {

		if (StringUtils.isBlank(filter) || StringUtils.equalsIgnoreCase("all", filter))
			return getAllExcludedProducts(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("active", filter))
			return getActiveExcludedProducts(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("expired", filter))
			return getExpiredExcludedProducts(keyword, page, itemsPerPage);

		return null;
	}

	@RemoteMethod
	public RecordSet<Product> getAllExcludedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(e, null, null,  page, itemsPerPage);
			return daoService.getExcludedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllExcludedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<Product> getActiveExcludedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(e, new Date(), null, page, itemsPerPage);
			return daoService.getExcludedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getActiveExcludedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<Product> getExpiredExcludedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(e, null, DateAndTimeUtils.getDateYesterday(),  page, itemsPerPage);
			return daoService.getExcludedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExpiredExcludedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public int updateExpiryDate(String keyword, String productId, String expiryDate){
		try {
			logger.info(String.format("%s %s %s", keyword, productId, expiryDate));
			String store = UtilityService.getStoreName();
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setExpiryDate(DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setLastModifiedBy(UtilityService.getUsername());
			return daoService.updateExcludeResultExpiryDate(e);
		} catch (DaoException e) {
			logger.error("Failed during updateExpiryDate()",e);
		}
		return -1;
	}
	
	@RemoteMethod
	public Product getExcludedProduct(String keyword, String productId) {
		try {
			logger.info(String.format("%s %s", keyword, productId));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			return daoService.getExcludedProduct(server, e);
		} catch (DaoException e) {
			logger.error("Failed during getElevatedProduct()",e);
		}
		return null;
	}

	@RemoteMethod
	public String getComment(String keyword, String productId) {
		Product elevatedProduct = getExcludedProduct(keyword, productId);
		if (elevatedProduct == null)
			return StringUtils.EMPTY;
		
		return StringUtils.trimToEmpty(elevatedProduct.getComment());
	}

	@RemoteMethod
	public int addComment(String keyword, String productId, String comment){
		try {
			logger.info(String.format("%s %s %s", keyword, productId, comment));
			String store = UtilityService.getStoreName();
			comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
			comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			return daoService.appendExcludeResultComment(e);
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return -1;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}