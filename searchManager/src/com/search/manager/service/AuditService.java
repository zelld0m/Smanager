package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.User;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

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
			String store = UtilityService.getStoreId();
			
			logger.info(String.format("%s %s %d %d", keyword, productId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(entity.toString());
			auditTrail.setStoreId(store);
			auditTrail.setKeyword(keyword);
			auditTrail.setReferenceId(productId);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
		} catch (DaoException e) {
			return null;
		}
	}
	
	public RecordSet<AuditTrail> getActivityTrail(Entity entity, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreId();
			
			logger.info(String.format("%d %d", page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(entity.toString());
			auditTrail.setStoreId(store);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
		} catch (DaoException e) {
			return null;
		}
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getItemTrail(String productId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreId();
			
			logger.info(String.format("%s %d %d", productId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setStoreId(store);
			auditTrail.setReferenceId(productId);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
		} catch (DaoException e) {
			return null;
		}
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getRedirectTrail(String ruleId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreId();
			
			logger.info(String.format("%s %d %d", ruleId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(Entity.queryCleaning.toString());
			auditTrail.setReferenceId(ruleId);
			auditTrail.setStoreId(store);
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
		} catch (DaoException e) {
			return null;
		}
	}

	@RemoteMethod
	public RecordSet<AuditTrail> getRelevancyTrail(String ruleId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreId();
			
			logger.info(String.format("%s %d %d", ruleId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(Entity.relevancy.toString());
			auditTrail.setReferenceId(ruleId);
			auditTrail.setStoreId(store);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
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
	public RecordSet<AuditTrail> getDemoteItemTrail(String keyword, String productId, int page,int itemsPerPage) {
		return getProductTrail(Entity.demote, keyword, productId, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getFacetSortTrail(String ruleId, int page,int itemsPerPage) {
		try {
			String store = UtilityService.getStoreId();
			
			logger.info(String.format("%s %d %d", ruleId, page, itemsPerPage));
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(Entity.facetSort.toString());
			auditTrail.setReferenceId(ruleId);
			auditTrail.setStoreId(store);
			
			return daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, null, null, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
		} catch (DaoException e) {
			return null;
		}
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
	public RecordSet<AuditTrail> getDemoteActivity(int page,int itemsPerPage) {
		return getActivityTrail(Entity.demote, page, itemsPerPage);
	}
	
	@RemoteMethod
	public RecordSet<AuditTrail> getAuditTrail(String userName, String operation, String entity, String keyword, String referenceId, String startDate, String endDate, int page,int itemsPerPage) {
		String store = UtilityService.getStoreId();
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setUsername(StringUtils.isBlank(userName)?null:userName);
		auditTrail.setOperation(StringUtils.isBlank(operation)?null:operation);
		auditTrail.setEntity(StringUtils.isBlank(entity)?null:entity);
		auditTrail.setStoreId(store);
		auditTrail.setKeyword(StringUtils.isBlank(keyword)?null:keyword);
		auditTrail.setReferenceId(StringUtils.isBlank(referenceId)?null:referenceId);
		RecordSet<AuditTrail> rSet = null;
		
		DateTime startDt = !StringUtils.isBlank(startDate)? JodaDateTimeUtil.toDateTimeFromStorePattern(startDate, JodaPatternType.DATE): null;
		DateTime endDt = !StringUtils.isBlank(endDate)? JodaDateTimeUtil.toDateTimeFromStorePattern(endDate, JodaPatternType.DATE): null;
		
		try {
			rSet = daoService.getAuditTrail(new SearchCriteria<AuditTrail>(auditTrail, startDt, endDt, page, itemsPerPage), UtilityService.hasPermission("CREATE_RULE"));
		} catch (DaoException e) {
			logger.error("Error getting audit trail. " + e.getMessage());
		}
		return rSet;
	}
	@RemoteMethod
	public List<String> getDropdownValues(int type,String[] filter) throws DaoException {
		List<String> ddList = new ArrayList<String>();
		
		switch (type) {
			case 1 : {
				User user = new User();
				user.setStoreId(UtilityService.getStoreId());
				SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user,null,null,0,0);
				try {
					RecordSet<User> record = daoService.getUsers(searchCriteria, MatchType.MATCH_ID);
					 for(User tmp:record.getList()) 
						 ddList.add(tmp.getUsername());
				} catch (DaoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
			case 2 : {
				Entity ent = Entity.valueOf(filter[0]);
				switch (ent) {					
					case elevate:
						for(Object opt: Arrays.asList(AuditTrailConstants.elevateOperations)){
							ddList.add(opt.toString());
						}
						break;
					case exclude:
						for(Object opt: Arrays.asList(AuditTrailConstants.excludeOperations)){
							ddList.add(opt.toString());
						}
						break;
					case demote:
						for(Object opt: Arrays.asList(AuditTrailConstants.demoteOperations)){
							ddList.add(opt.toString());
						}
						break;
					case facetSort:
						for(Object opt: Arrays.asList(AuditTrailConstants.facetSortOperations)){
							ddList.add(opt.toString());
						}
						break;
					case keyword:
						for(Object opt: Arrays.asList(AuditTrailConstants.keywordOperations)){
							ddList.add(opt.toString());
						}
						break;
					case storeKeyword:
						for(Object opt: Arrays.asList(AuditTrailConstants.storeKeywordOperations)){
							ddList.add(opt.toString());
						}
						break;
					case campaign:
						for(Object opt: Arrays.asList(AuditTrailConstants.campaignOperations)){
							ddList.add(opt.toString());
						}
						break;
					case banner:
						for(Object opt: Arrays.asList(AuditTrailConstants.bannerOperations)){
							ddList.add(opt.toString());
						}
						break;
					case queryCleaning: 
						for(Object opt: Arrays.asList(AuditTrailConstants.queryCleaningOperations)){
							ddList.add(opt.toString());
						}
						for(Object opt: Arrays.asList(AuditTrailConstants.queryCleaningConditionOperations)){
							ddList.add(opt.toString());
						}
						for(Object opt: Arrays.asList(AuditTrailConstants.queryCleaningKeywordOperations)){
							ddList.add(opt.toString());
						}
						break;
					case relevancy: 
						for(Object opt: Arrays.asList(AuditTrailConstants.relevancyOperations)){
							ddList.add(opt.toString());
						}
						for(Object opt: Arrays.asList(AuditTrailConstants.relevancyKeywordOperations)){
							ddList.add(opt.toString());
						}
						for(Object opt: Arrays.asList(AuditTrailConstants.relevancyFieldOperations)){
							ddList.add(opt.toString());
						}
						break;
					case ruleStatus:
						for(Object opt: Arrays.asList(AuditTrailConstants.ruleStatusOperations)){
							ddList.add(opt.toString());
						}
						break;
					case security:
						if(UtilityService.hasPermission("CREATE_RULE")){
							for(Object opt: Arrays.asList(AuditTrailConstants.securityOperations)){
								ddList.add(opt.toString());
							}
						}
						break;
				}
			}
			break;
			case 3 : {
				for(Object opt: Arrays.asList(AuditTrailConstants.ENTITY_LIST_ASC)){
					if(opt.toString().equals(AuditTrailConstants.Entity.security) && UtilityService.hasPermission("CREATE_RULE") || !opt.toString().equals(AuditTrailConstants.Entity.security)){
						ddList.add(opt.toString());
					}
					
				} 
				break;
			}
			case 4 : {
				ddList = daoService.getRefIDs(filter[0], filter[1], UtilityService.getStoreId());
				break;
			}
			
		}	
		return ddList;
	}
	@RemoteMethod
	public List<String> getDropdownValues(int type) {
		try {
			return daoService.getDropdownValues(type, UtilityService.getStoreId(), UtilityService.hasPermission("CREATE_RULE"));
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