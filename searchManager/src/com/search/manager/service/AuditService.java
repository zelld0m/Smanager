package com.search.manager.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.NameValue;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.utility.DateAndTimeUtils;

@Service(value = "auditService")
@RemoteProxy(
		name = "AuditServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "auditService")
	)
public class AuditService {
	private static final Logger logger = Logger.getLogger(AuditService.class);
	
	@Autowired private DaoService daoService;
	
	public RecordSet<AuditTrail> getProductTrail(Entity entity, String keyword, String productId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreName();
			
			logger.info(String.format("%s %s %d %d", keyword, productId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(entity.toString());
			auditTrail.setStoreId(store);
			auditTrail.setKeyword(keyword);
			auditTrail.setReferenceId(productId);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage));
		} catch (DaoException e) {
			return null;
		}
	}
	
	public RecordSet<AuditTrail> getActivityTrail(Entity entity, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreName();
			
			logger.info(String.format("%d %d", page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(entity.toString());
			auditTrail.setStoreId(store);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage));
		} catch (DaoException e) {
			return null;
		}
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getItemTrail(String productId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreName();
			
			logger.info(String.format("%s %d %d", productId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setStoreId(store);
			auditTrail.setReferenceId(productId);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage));
		} catch (DaoException e) {
			return null;
		}
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getRedirectTrail(String ruleId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreName();
			
			logger.info(String.format("%s %d %d", ruleId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(Entity.queryCleaning.toString());
			auditTrail.setReferenceId(ruleId);
			auditTrail.setStoreId(store);
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage));
		} catch (DaoException e) {
			return null;
		}
	}

	@RemoteMethod
	public RecordSet<AuditTrail> getRelevancyTrail(String ruleId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreName();
			
			logger.info(String.format("%s %d %d", ruleId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(Entity.relevancy.toString());
			auditTrail.setReferenceId(ruleId);
			auditTrail.setStoreId(store);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage));
		} catch (DaoException e) {
			return null;
		}
	}

	@RemoteMethod
	public RecordSet<AuditTrail> getElevateItemTrail(String keyword, String productId, int page,int itemsPerPage) {
		return getProductTrail(Entity.elevate, keyword, productId, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getExcludeItemTrail(String keyword, String productId, int page,int itemsPerPage) {
		return getProductTrail(Entity.exclude, keyword, productId, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getElevateActivity(int page,int itemsPerPage) {
		return getActivityTrail(Entity.elevate, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getExcludeActivity(int page,int itemsPerPage) {
		return getActivityTrail(Entity.exclude, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getAuditTrail(String userName, String operation, String entity, String keyword, String referenceId, String startDate, String endDate, int page,int itemsPerPage) {
		String store = UtilityService.getStoreName();
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setUsername(StringUtils.isBlank(userName)?null:userName);
		auditTrail.setOperation(StringUtils.isBlank(operation)?null:operation);
		auditTrail.setEntity(StringUtils.isBlank(entity)?null:entity);
		auditTrail.setStoreId(store);
		auditTrail.setKeyword(StringUtils.isBlank(keyword)?null:keyword);
		auditTrail.setReferenceId(StringUtils.isBlank(referenceId)?null:referenceId);
		RecordSet<AuditTrail> rSet = null;
		Date startDt = null;
		Date endDt = null;
		if (!StringUtils.isBlank(startDate)) {
			startDt = DateAndTimeUtils.toSQLDate(store, startDate);
		}
		if (!StringUtils.isBlank(endDate)) {
			endDt = DateAndTimeUtils.toSQLDate(store, endDate);
		}
		try {
			rSet = daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, startDt, endDt, page, itemsPerPage));
		} catch (DaoException e) {
			logger.error("Error getting audit trail. " + e.getMessage());
		}
		return rSet;
	}
	
	@RemoteMethod
	public List<NameValue> getDropdownValues() {
		try {
			return daoService.getDropdownValues(UtilityService.getStoreName());
		} catch (DaoException e) {
			logger.error("Error getting dropdown values" + e.getMessage());
		}
		return null;
	}
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}