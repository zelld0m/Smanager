package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

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
	public RecordSet<AuditTrail> getElevateItemTrail(String keyword, String productId, int page,int itemsPerPage) {
		return getProductTrail(Entity.elevate, keyword, productId, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getExcludeItemTrail(String keyword, String productId, int page,int itemsPerPage) {
		return getProductTrail(Entity.exclude, keyword, productId, page, itemsPerPage);
	}
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}