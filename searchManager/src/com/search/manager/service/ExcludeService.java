package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.Comment;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;

@Service(value = "excludeService")
@RemoteProxy(
		name = "ExcludeServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "excludeService")
)
public class ExcludeService extends RuleService{
	private static final Logger logger = Logger.getLogger(ExcludeService.class);

	@Autowired private DaoService daoService;

	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.EXCLUDE;
	}

	private int addItem(String keyword, String edp, RedirectRuleCondition condition, String expiryDate, String comment, MemberTypeEntity entity) {
		int result = -1;
		try {
			logger.info(String.format("%s %s %s %s %s", keyword, edp, condition != null ? condition.getCondition() : "", expiryDate, comment));
			String store = UtilityService.getStoreName();
			String userName = UtilityService.getUsername();
			daoService.addKeyword(new StoreKeyword(store, keyword)); // TODO: What if keyword is not added?

			ExcludeResult e = new ExcludeResult(new StoreKeyword(store, keyword));
			e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(userName);
			e.setComment(UtilityService.formatComment(comment));
			e.setExcludeEntity(entity);
			switch (entity) {
				case PART_NUMBER:
					e.setEdp(edp);
					break;
				case FACET:
					e.setCondition(condition);
					break;
			}
			
			result  = daoService.addExcludeResult(e);
			if (result > 0) {
				if (!StringUtils.isBlank(comment)) {
					addComment(comment, e);
				}
				try {
					// TODO: add checking if existing rule status?
					daoService.addRuleStatus(new RuleStatus(RuleEntity.EXCLUDE, DAOUtils.getStoreId(e.getStoreKeyword()), 
							keyword, keyword, userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
				} catch (DaoException de) {
					logger.error("Failed to create rule status for exclude: " + keyword);
				}
			}
		} catch (DaoException e) {
			logger.error("Failed during excludeItem()",e);
		}
		return result;
	}
	
	
	@RemoteMethod
	public Map<String, List<String>> addItemToRuleUsingPartNumber(String keyword, String expiryDate, String comment, String[] partNumbers) {

		logger.info(String.format("%s %s", keyword, partNumbers));

		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
		
		ArrayList<String> passedList = new ArrayList<String>();
		ArrayList<String> failedList = new ArrayList<String>();

		resultMap.put("PASSED", passedList);
		resultMap.put("FAILED", failedList);
		
		String server = UtilityService.getServerName();
		String store = UtilityService.getStoreName();
		
		int count = 0;
		comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
		comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
		
		for(String partNumber: partNumbers){
			count = 0;
			try {
			
				String edp = daoService.getEdpByPartNumber(server, store, keyword, StringUtils.trim(partNumber));
				if (StringUtils.isNotBlank(edp)) {
					count = addItem(keyword, edp, null, expiryDate, comment, MemberTypeEntity.PART_NUMBER);
				}
			} catch (DaoException de) {
				logger.error("Failed during addExcludeByPartNumber()",de);
			}
			
			if (count > 0) {
				passedList.add(StringUtils.trim(partNumber));						
			}
			else {
				failedList.add(StringUtils.trim(partNumber));
			}
		}			
		return resultMap;
	}

	@RemoteMethod
	public int addFacetRule(String keyword, String expiryDate, String comment,  Map<String, List<String>> filter) {
		return addItem(keyword, null, new RedirectRuleCondition(filter), expiryDate, comment, MemberTypeEntity.FACET);
	}


	@RemoteMethod
	public int addExclude(String keyword, String memberTypeId, String value, String expiryDate, String comment) {
		MemberTypeEntity memberTypeEntity = MemberTypeEntity.valueOf(memberTypeId);
		return addItem(keyword, memberTypeEntity == MemberTypeEntity.PART_NUMBER ? value : null, 
				memberTypeEntity == MemberTypeEntity.FACET ? new RedirectRuleCondition(value) : null, 
				expiryDate, comment, memberTypeEntity);
	}

	@RemoteMethod
	public int deleteItemInRule(String keyword, String memberId) {
		int result = -1;
		try {

			String store = UtilityService.getStoreName();
			logger.info(String.format("%s %s %s", store, keyword,memberId));
			ExcludeResult exclude = new ExcludeResult();
			exclude.setStoreKeyword(new StoreKeyword(store, keyword));
			exclude.setMemberId(memberId);
			try {
				exclude = daoService.getExcludeItem(exclude);
			} catch (DaoException e) {
				exclude = null;
			}
			if (exclude != null) {
				result  = daoService.deleteExcludeResult(exclude);
			}
		} catch (DaoException e) {
			logger.error("Failed during removeExclude()",e);
		}
		return result;
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
	public Integer getTotalProductInRule(String ruleId) {
		try {
			logger.info(String.format("%s", ruleId));
			String store = UtilityService.getStoreName();
			ExcludeResult rule = new ExcludeResult();
			rule.setStoreKeyword(new StoreKeyword(store, ruleId));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(rule, null, null, null, null);
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
			return daoService.getExcludedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllExcludedProducts()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public RecordSet<Product> getAllExcludedProductsIgnoreKeyword(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			ExcludeResult e = new ExcludeResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(e, null, null,  page, itemsPerPage);
			return daoService.getExcludedProductsIgnoreKeyword(server, criteria);
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
			return daoService.getExcludedProductsIgnoreKeyword(server, criteria);
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
			return daoService.getExcludedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExpiredExcludedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public int updateExpiryDate(String keyword, String memberId, String expiryDate){
		int result = -1;
		try {
			logger.info(String.format("updateExpiryDate %s %s ", memberId, expiryDate));
			String store = UtilityService.getStoreName();
			ExcludeResult e = new ExcludeResult();
			e.setExpiryDate(DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setLastModifiedBy(UtilityService.getUsername());
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setMemberId(memberId);
			e = daoService.getExcludeItem(e);
			if (e != null) {
				e.setExpiryDate(DateAndTimeUtils.toSQLDate(store, expiryDate));
				e.setLastModifiedBy(UtilityService.getUsername());
				result = daoService.updateExcludeResultExpiryDate(e);
			}
		} catch (DaoException e) {
			logger.error("Failed during updateExpiryDate()",e);
		}
		return result;
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
			logger.error("Failed during getExcludedProduct()",e);
		}
		return null;
	}

	@RemoteMethod
	public String getComment(String keyword, String productId) {
		Product excludedProduct = getExcludedProduct(keyword, productId);
		if (excludedProduct == null)
			return StringUtils.EMPTY;

		return StringUtils.trimToEmpty(excludedProduct.getComment());
	}

	@RemoteMethod
	public int addComment(String keyword, String productId, String comment){
		try {
			logger.info(String.format("%s %s %s", keyword, productId, comment));
			String store = UtilityService.getStoreName();
			
			if(StringUtils.isNotBlank(comment)){
				comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
				comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			}
			
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
	
	@RemoteMethod
	public int updateExclude(String keyword, String memberId, String condition) {
		try {
			logger.info(String.format("%s %s %s", keyword, memberId, condition));
			ExcludeResult exclude = new ExcludeResult();
			exclude.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
			exclude.setMemberId(memberId);
			try {
				exclude = daoService.getExcludeItem(exclude);
			} catch (DaoException e) {
				exclude = null;
			}
			if (exclude!=null) {
				if (!StringUtils.isBlank(condition)) {
					exclude.setCondition(new RedirectRuleCondition((condition)));
				}
				
				exclude.setLastModifiedBy(UtilityService.getUsername());
				return daoService.updateExcludeResult(exclude);
			}
		} catch (DaoException e) {
			logger.error("Failed during updateExclude()",e);
		}
		return -1;
	}
	
	@RemoteMethod
	public int updateExcludeFacet(String keyword, String memberId, String comment, String expiryDate, Map<String, List<String>> filter){
		int changes = 0;
		
		ExcludeResult exclude = new ExcludeResult();
		exclude.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
		exclude.setMemberId(memberId);
		RedirectRuleCondition rrCondition = new RedirectRuleCondition();
		rrCondition.setFilter(filter);
		try {
			exclude = daoService.getExcludeItem(exclude);
		} catch (DaoException e) {
			exclude = null;
		}

		if(exclude==null){
			return changes;
		}
		
		if (StringUtils.isNotBlank(comment)){
			try {
				addComment(comment,exclude);
				changes++;
			} catch (DaoException e) {
				logger.error("Error adding comment in updateExcludeFacet()",e);
			}		
		}
		
		if (!rrCondition.getCondition().equals(exclude.getCondition().getCondition())){
			changes += ((updateExclude(keyword, memberId, rrCondition.getCondition()) > 0)? 1 : 0);
		}
		
		if (!StringUtils.isBlank(expiryDate) && !StringUtils.equalsIgnoreCase(expiryDate, DateAndTimeUtils.formatDateTimeUsingConfig(UtilityService.getStoreName(), exclude.getExpiryDate()))) {
			changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0)? 1 : 0);
		}
		
		return changes;
	}

	@RemoteMethod
	public int clearRule(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			return daoService.clearExcludeResult(new StoreKeyword(UtilityService.getStoreName(), keyword));
		} catch (DaoException e) {
			logger.error("Failed during clearRule()",e);
		}
		return 0;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	
	private int addComment(String comment, ExcludeResult e) throws DaoException {
		Comment com = new Comment();
		com.setComment(comment);
		com.setUsername(UtilityService.getUsername());
		com.setReferenceId(e.getMemberId());
		com.setRuleTypeId(RuleEntity.EXCLUDE.getCode());
		com.setStore(new Store(UtilityService.getStoreName()));
		return daoService.addComment(com);
	}

	@RemoteMethod
	public int addRuleComment(String keyword, String memberId, String pComment) {
		int result = -1;
		String store = UtilityService.getStoreName();
		try {
			ExcludeResult exclude = new ExcludeResult();
			exclude.setStoreKeyword(new StoreKeyword(store, keyword));
			exclude.setMemberId(memberId);
			try {
				exclude = daoService.getExcludeItem(exclude);
			} catch (DaoException e) {
				exclude = null;
			}
			if (exclude != null) {
				exclude.setComment(pComment);
				exclude.setLastModifiedBy(UtilityService.getUsername());
				daoService.updateExcludeResultComment(exclude);
				result = addComment(pComment, exclude);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRuleComment()",e);
		}
		return result;
	}
}
