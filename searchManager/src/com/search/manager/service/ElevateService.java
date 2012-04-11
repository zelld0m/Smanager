package com.search.manager.service;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.manager.utility.PropsUtils;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;

@RemoteProxy(
		name = "ElevateServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "elevateService")
)
public class ElevateService{

	private static final Logger logger = Logger.getLogger(ElevateService.class);

	private DaoService daoService;
	private DaoCacheService daoCacheService;

	@RemoteMethod
	public int addElevate(String keyword, String edp, int sequence, String expiryDate, String comment) {
		try {
			logger.info(String.format("%s %s %d %s %s", keyword, edp, sequence, expiryDate, comment));
			String store = UtilityService.getStoreName();

			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(edp);
			e.setLocation(sequence);
			e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			daoService.addKeyword(store, keyword); // TODO: What if keyword is not added?
			int ret = daoService.addElevateResult(e);
			daoCacheService.updateElevateResultList(e);
			return ret;
		} catch (DaoException e) {
			logger.error("Failed during addElevate()",e);
		}
		return -1;

	}

	@RemoteMethod
	public int addElevateByPartNumber(String keyword, String partNumber, int sequence, String expiryDate, String comment) {
		try {
			logger.info(String.format("%s %s %d", keyword, partNumber, sequence));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			String edp = daoService.getEdpByPartNumber(server, store, keyword, partNumber);
			comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
			comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(edp);
			e.setLocation(sequence);
			e.setExpiryDate(StringUtils.isBlank(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			if (StringUtils.isNotBlank(edp)){
				int ret = daoService.addElevateResult(e);
				daoCacheService.updateElevateResultList(e);
				return ret;
			}
			return 0;
		} catch (DaoException e) {
			logger.error("Failed during addElevateByPartNumber()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int updateExpiryDate(String keyword, String productId, String expiryDate){
		try {
			logger.info(String.format("%s %s %s", keyword, productId, expiryDate));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setExpiryDate(DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setLastModifiedBy(UtilityService.getUsername());
			int ret = daoService.updateElevateResultExpiryDate(e);		
			daoCacheService.updateElevateResultList(e);
			return ret;
		} catch (DaoException e) {
			logger.error("Failed during updateExpiryDate()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int addComment(String keyword, String productId, String comment){
		try {
			logger.info(String.format("%s %s %s", keyword, productId, comment));
			String store = UtilityService.getStoreName();
			comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
			comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			return daoService.appendElevateResultComment(e);
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int removeElevate(String keyword, String productId) {
		try {
			logger.info(String.format("%s %s", keyword, productId));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setLastModifiedBy(UtilityService.getUsername());	
			int ret = daoService.deleteElevateResult(e);
			daoCacheService.updateElevateResultList(e);
			return ret;
		} catch (DaoException e) {
			logger.error("Failed during removeElevate()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int updateElevate(String keyword, String productId, int sequence) {
		try {
			logger.info(String.format("%s %s %d", keyword, productId, sequence));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			e.setLocation(sequence);
			e.setLastModifiedBy(UtilityService.getUsername());
			int ret = daoService.updateElevateResult(e);
			daoCacheService.updateElevateResultList(e);
			return ret;
		} catch (DaoException e) {
			logger.error("Failed during updateElevate()",e);
		}
		return -1;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getProducts(String filter, String keyword, int page, int itemsPerPage) {
		
		if (StringUtils.isBlank(filter) || StringUtils.equalsIgnoreCase("all", filter))
			return getAllElevatedProducts(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("active", filter))
			return getActiveElevatedProducts(keyword, page, itemsPerPage);
		
		if (StringUtils.equalsIgnoreCase("expired", filter))
			return getExpiredElevatedProducts(keyword, page, itemsPerPage);
		
		return null;
	}
	
	@RemoteMethod
	public RecordSet<ElevateProduct> getAllElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null,  page, itemsPerPage);
			return daoCacheService.getElevatedProducts(server, criteria, store);
		} catch (Exception e) {
			logger.error("Failed during getAllElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getActiveElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, new Date(), null,  page, itemsPerPage);
			return daoCacheService.getElevatedProducts(server, criteria, store);
		} catch (Exception e) {
			logger.error("Failed during getActiveElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getExpiredElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, DateAndTimeUtils.getDateYesterday(),  page, itemsPerPage);
			return daoService.getElevatedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExpiredElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getNoExpiryElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null,  page, itemsPerPage);
			return daoService.getNoExpiryElevatedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getNoExpiryElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public Integer getElevatedProductCount(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null, null, null);
			return daoCacheService.getElevateResultCount(criteria,store);
		} catch (Exception e) {
			logger.error("Failed during getElevatedProductCount()",e);
		}
		return null;
	}

	@RemoteMethod
	public ElevateProduct getElevatedProduct(String keyword, String productId) {
		try {
			logger.info(String.format("%s %s", keyword, productId));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setEdp(productId);
			return daoService.getElevatedProduct(server, e);
		} catch (DaoException e) {
			logger.error("Failed during getElevatedProduct()",e);
		}
		return null;
	}

	@RemoteMethod
	public String getComment(String keyword, String productId) {
		ElevateProduct elevatedProduct = getElevatedProduct(keyword, productId);
		if (elevatedProduct == null)
			return StringUtils.EMPTY;
		
		return StringUtils.trimToEmpty(elevatedProduct.getComment());
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public void setDaoCacheService(DaoCacheService daoCacheService) {
		this.daoCacheService = daoCacheService;
	}
}
